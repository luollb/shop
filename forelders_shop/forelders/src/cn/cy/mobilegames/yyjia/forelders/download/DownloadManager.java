/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.cy.mobilegames.yyjia.forelders.download;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Pair;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.util.TaskUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

import com.umeng.analytics.MobclickAgent;

/**
 * The download manager is a system service that handles long-running HTTP
 * downloads. Clients may request that a URI be downloaded to a particular
 * destination file. The download manager will conduct the download in the
 * background, taking care of HTTP interactions and retrying downloads after
 * failures or across connectivity changes and system reboots.
 * 
 * Instances of this class should be obtained through
 * {@link android.content.Context#getSystemService(String)} by passing
 * {@link android.content.Context#DOWNLOAD_SERVICE}.
 * 
 * Apps that request downloads through this API should register a broadcast
 * receiver for {@link #ACTION_NOTIFICATION_CLICKED} to appropriately handle
 * when the user clicks on a running download in a notification or from the
 * downloads UI.
 */
public class DownloadManager {
	private static DownloadManager instance;
	private static Context mContext;
	private static SharedPreferences sp;
	@SuppressWarnings("unused")
	private static final String TAG = "DownloadManager";

	/**
	 * An identifier for a particular download, unique across the system.
	 * Clients use this ID to make subsequent calls related to the download.
	 */
	public final static String COLUMN_ID = BaseColumns._ID;

	/**
	 * The client-supplied title for this download. This will be displayed in
	 * system notifications. Defaults to the empty string.
	 */
	public final static String COLUMN_TITLE = "title";

	/**
	 * The client-supplied description of this download. This will be displayed
	 * in system notifications. Defaults to the empty string.
	 */
	public final static String COLUMN_DESCRIPTION = "description";

	/**
	 * URI to be downloaded.
	 */
	public final static String COLUMN_URI = "uri";

	/**
	 * Internet Media Type of the downloaded file. If no value is provided upon
	 * creation, this will initially be null and will be filled in based on the
	 * server's response once the download has started.
	 * 
	 * @see <a href="http://www.ietf.org/rfc/rfc1590.txt">RFC 1590, defining
	 *      Media Types</a>
	 */
	public final static String COLUMN_MEDIA_TYPE = "media_type";

	/**
	 * Total size of the download in bytes. This will initially be -1 and will
	 * be filled in once the download starts.
	 */
	public final static String COLUMN_TOTAL_SIZE_BYTES = "total_size";

	/**
	 * Uri where downloaded file will be stored. If a destination is supplied by
	 * client, that URI will be used here. Otherwise, the value will initially
	 * be null and will be filled in with a generated URI once the download has
	 * started.
	 */
	public final static String COLUMN_LOCAL_URI = "local_uri";

	/**
	 * Current status of the download, as one of the STATUS_* constants.
	 */
	public final static String COLUMN_STATUS = "status";

	/**
	 * Provides more detail on the status of the download. Its meaning depends
	 * on the value of {@link #COLUMN_STATUS}.
	 * 
	 * When {@link #COLUMN_STATUS} is {@link #STATUS_FAILED}, this indicates the
	 * type of error that occurred. If an HTTP error occurred, this will hold
	 * the HTTP status code as defined in RFC 2616. Otherwise, it will hold one
	 * of the ERROR_* constants.
	 * 
	 * When {@link #COLUMN_STATUS} is {@link #STATUS_PAUSED}, this indicates why
	 * the download is paused. It will hold one of the PAUSED_* constants.
	 * 
	 * If {@link #COLUMN_STATUS} is neither {@link #STATUS_FAILED} nor
	 * {@link #STATUS_PAUSED}, this column's value is undefined.
	 * 
	 * @see <a
	 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec6.html#sec6.1.1">RFC
	 *      2616 status codes</a>
	 */
	public final static String COLUMN_REASON = "reason";

	/**
	 * Number of bytes download so far.
	 */
	public final static String COLUMN_BYTES_DOWNLOADED_SO_FAR = "bytes_so_far";

	/**
	 * Timestamp when the download was last modified, in
	 * {@link System#currentTimeMillis System.currentTimeMillis()} (wall clock
	 * time in UTC).
	 */
	public final static String COLUMN_LAST_MODIFIED_TIMESTAMP = "last_modified_timestamp";

	public final static int STATUS_DELETED = 1 << 0;
	/**
	 * Value of {@link #COLUMN_STATUS} when the download is waiting to start.
	 */
	public final static int STATUS_PENDING = 1 << 0;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download is currently running.
	 */
	public final static int STATUS_RUNNING = 1 << 1;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download is waiting to retry or
	 * resume.
	 */
	public final static int STATUS_PAUSED = 1 << 2;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download has successfully
	 * completed.
	 */
	public final static int STATUS_SUCCESS = 1 << 3;

	/**
	 * Value of {@link #COLUMN_STATUS} when the download has failed (and will
	 * not be retried).
	 */
	public final static int STATUS_FAILED = 1 << 4;
	/**
	 * Value of {@link #COLUMN_STATUS} when the download is waiting to other
	 * task completed
	 * 
	 */
	public final static int STATUS_WAITED = 1 << 5;
	/**
	 * 已安装--打开
	 */
	public final static int STATUS_INSTALLED = 1 << 6;
	/**
	 * 用户取消
	 */
	public final static int STATUS_CANCELED = 1 << 7;

	/**
	 * Value of COLUMN_ERROR_CODE when the download has completed with an error
	 * that doesn't fit under any other error code.
	 */
	public final static int ERROR_UNKNOWN = 1000;

	/**
	 * Value of {@link #COLUMN_REASON} when a storage issue arises which doesn't
	 * fit under any other error code. Use the more specific
	 * {@link #ERROR_INSUFFICIENT_SPACE} and {@link #ERROR_DEVICE_NOT_FOUND}
	 * when appropriate.
	 */
	public final static int ERROR_FILE_ERROR = 1001;

	/**
	 * Value of {@link #COLUMN_REASON} when an HTTP code was received that
	 * download manager can't handle.
	 */
	public final static int ERROR_UNHANDLED_HTTP_CODE = 1002;

	/**
	 * Value of {@link #COLUMN_REASON} when an error receiving or processing
	 * data occurred at the HTTP level.
	 */
	public final static int ERROR_HTTP_DATA_ERROR = 1004;

	/**
	 * Value of {@link #COLUMN_REASON} when there were too many redirects.
	 */
	public final static int ERROR_TOO_MANY_REDIRECTS = 1005;

	/**
	 * Value of {@link #COLUMN_REASON} when there was insufficient storage
	 * space. Typically, this is because the SD card is full.
	 */
	public final static int ERROR_INSUFFICIENT_SPACE = 1006;

	/**
	 * Value of {@link #COLUMN_REASON} when no external storage device was
	 * found. Typically, this is because the SD card is not mounted.
	 */
	public final static int ERROR_DEVICE_NOT_FOUND = 1007;

	/**
	 * Value of {@link #COLUMN_REASON} when some possibly transient error
	 * occurred but we can't resume the download.
	 */
	public final static int ERROR_CANNOT_RESUME = 1008;

	/**
	 * Value of {@link #COLUMN_REASON} when the requested destination file
	 * already exists (the download manager will not overwrite an existing
	 * file).
	 */
	public final static int ERROR_FILE_ALREADY_EXISTS = 1009;

	/**
	 * Value of {@link #COLUMN_REASON} when the download is paused because some
	 * network error occurred and the download manager is waiting before
	 * retrying the request.
	 */
	public final static int PAUSED_WAITING_TO_RETRY = 1;

	/**
	 * Value of {@link #COLUMN_REASON} when the download is waiting for network
	 * connectivity to proceed.
	 */
	public final static int PAUSED_WAITING_FOR_NETWORK = 2;

	/**
	 * Value of {@link #COLUMN_REASON} when the download exceeds a size limit
	 * for downloads over the mobile network and the download manager is waiting
	 * for a Wi-Fi connection to proceed.
	 */
	public final static int PAUSED_QUEUED_FOR_WIFI = 3;

	/**
	 * Value of {@link #COLUMN_REASON} when the download task number exceeds
	 * limit for downloads and the download manager is waiting for other
	 * completed
	 */
	public final static int PAUSED_QUEUED_FOR_LIMIT = 4;

	/**
	 * Value of {@link #COLUMN_REASON} when the download is paused for some
	 * other reason.
	 */
	public final static int PAUSED_UNKNOWN = 5;

	/**
	 * Broadcast intent action sent by the download manager when a download
	 * completes.
	 */
	public final static String ACTION_DOWNLOAD_COMPLETE = "android.intent.action.DOWNLOAD_COMPLETE";

	/**
	 * Broadcast intent action sent by the download manager when the user clicks
	 * on a running download, either from a system notification or from the
	 * downloads UI.
	 */
	public final static String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED";

	/**
	 * Intent action to launch an activity to display all downloads.
	 */
	public final static String ACTION_VIEW_DOWNLOADS = "android.intent.action.VIEW_DOWNLOADS";

	/**
	 * Intent extra included with {@link #ACTION_DOWNLOAD_COMPLETE} intents,
	 * indicating the ID (as a long) of the download that just completed.
	 */
	public static final String EXTRA_DOWNLOAD_ID = "extra_download_id";

	// this array must contain all public columns
	private static final String[] COLUMNS = new String[] { COLUMN_ID,
			COLUMN_TITLE, COLUMN_DESCRIPTION, COLUMN_URI, COLUMN_MEDIA_TYPE,
			COLUMN_TOTAL_SIZE_BYTES, COLUMN_LOCAL_URI, COLUMN_STATUS,
			COLUMN_REASON, COLUMN_BYTES_DOWNLOADED_SO_FAR,
			COLUMN_LAST_MODIFIED_TIMESTAMP };

	// columns to request from DownloadProvider
	private static final String[] UNDERLYING_COLUMNS = new String[] { Impl._ID,
			Impl.COLUMN_TITLE, Impl.COLUMN_DESCRIPTION, Impl.COLUMN_URI,
			Impl.COLUMN_MIME_TYPE, Impl.COLUMN_TOTAL_BYTES, Impl.COLUMN_STATUS,
			Impl.COLUMN_CURRENT_BYTES, Impl.COLUMN_LAST_MODIFICATION,
			Impl.COLUMN_DESTINATION, Impl.COLUMN_FILE_NAME_HINT, Impl._DATA, };

	private static final Set<String> LONG_COLUMNS = new HashSet<String>(
			Arrays.asList(COLUMN_ID, COLUMN_TOTAL_SIZE_BYTES, COLUMN_STATUS,
					COLUMN_REASON, COLUMN_BYTES_DOWNLOADED_SO_FAR,
					COLUMN_LAST_MODIFIED_TIMESTAMP));

	/**
	 * This class contains all the information necessary to request a new
	 * download. The URI is the only required parameter.
	 * 
	 * Note that the default download destination is a shared volume where the
	 * system might delete your file if it needs to reclaim space for system
	 * use. If this is a problem, use a location on external storage (see
	 * {@link #setDestinationUri(Uri)}.
	 */
	public static class Request {
		/**
		 * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
		 * {@link ConnectivityManager#TYPE_MOBILE}.
		 */
		public static final int NETWORK_MOBILE = 1 << 0;

		/**
		 * Bit flag for {@link #setAllowedNetworkTypes} corresponding to
		 * {@link ConnectivityManager#TYPE_WIFI}.
		 */
		public static final int NETWORK_WIFI = 1 << 1;

		private Uri mUri;
		private Uri mDestinationUri;
		private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
		private CharSequence mTitle;
		private CharSequence mDescription;
		private boolean mShowNotification = true;
		private String mMimeType;
		private String mLogo;
		private int mSourceType;
		private String mMd5;
		private String mAppId;
		private String mPackageName;
		private String mVersionName;
		private int mVersionCode;
		private boolean mRoamingAllowed = false;
		private int mAllowedNetworkTypes = ~0; // default to all network types
		// allowed
		private boolean mIsVisibleInDownloadsUi = true;
		public PauseListener mListener;

		// BEGIN, added by trinea@trinea.cn 2013/03/01 TODO
		private CharSequence mNotiClass;
		private CharSequence mNotiExtras;

		public Request setPackageName(String packageName) {
			mPackageName = packageName;
			return this;
		}

		public Request setVesionName(String versionName) {
			mVersionName = versionName;
			return this;
		}

		public Request setVersionCode(int versionCode) {
			mVersionCode = versionCode;
			return this;
		}

		public Request setPauseListener(PauseListener listener) {
			mListener = listener;
			return this;
		}

		public Request setAppId(String appid) {
			mAppId = appid;
			return this;
		}

		public Request setSourceType(int type) {
			mSourceType = type;
			return this;
		}

		public Request setMD5(String md5) {
			mMd5 = md5;
			return this;
		}

		/**
		 * Set notiClass, to be used as destination when click downloading in
		 * download manager ui
		 * 
		 * @return this object
		 */
		public Request setNotiClass(CharSequence notiClass) {
			mNotiClass = notiClass;
			return this;
		}

		public Request setLogo(String uri) {
			mLogo = uri;
			return this;
		}

		/**
		 * Set notiExtras, to be sended to notiClass when click downloading in
		 * download manager ui
		 * 
		 * @return this object
		 */
		public Request setNotiExtras(CharSequence notiExtras) {
			mNotiExtras = notiExtras;
			return this;
		}

		// trinea END TODO
		/**
		 * @param uri
		 *            the HTTP URI to download.
		 */
		public Request(Uri uri) {
			if (uri == null) {
				throw new NullPointerException();
			}
			String scheme = uri.getScheme();
			if (scheme == null || !scheme.equals("http")) {
				return;
				// throw new IllegalArgumentException(
				// "Can only download HTTP URIs: " + uri);
			}
			mUri = uri;
		}

		/**
		 * Set the local destination for the downloaded file. Must be a file URI
		 * to a path on external storage, and the calling application must have
		 * the WRITE_EXTERNAL_STORAGE permission.
		 * 
		 * If the URI is a directory(ending with "/"), destination filename will
		 * be generated.
		 * 
		 * @return this object
		 */
		public Request setDestinationUri(Uri uri) {
			mDestinationUri = uri;
			return this;
		}

		/**
		 * Set the local destination for the downloaded file to a path within
		 * the application's external files directory (as returned by
		 * {@link Context#getExternalFilesDir(String)}.
		 * 
		 * @param context
		 *            the {@link Context} to use in determining the external
		 *            files directory
		 * @param dirType
		 *            the directory type to pass to
		 *            {@link Context#getExternalFilesDir(String)}
		 * @param subPath
		 *            the path within the external directory. If subPath is a
		 *            directory(ending with "/"), destination filename will be
		 *            generated.
		 * @return this object
		 */
		public Request setDestinationInExternalFilesDir(Context context,
				String dirType, String subPath) {
			setDestinationFromBase(context.getExternalFilesDir(dirType),
					subPath);
			return this;
		}

		/**
		 * Set the local destination for the downloaded file to a path within
		 * the public external storage directory (as returned by
		 * {@link Environment#getExternalStoragePublicDirectory(String)}.
		 * 
		 * @param dirType
		 *            the directory type to pass to
		 *            {@link Environment#getExternalStoragePublicDirectory(String)}
		 * @param subPath
		 *            the path within the external directory. If subPath is a
		 *            directory(ending with "/"), destination filename will be
		 *            generated.
		 * @return this object
		 */
		public Request setDestinationInExternalPublicDir(String dirType,
				String subPath) {
			setDestinationFromBase(
					Environment.getExternalStoragePublicDirectory(dirType),
					subPath);
			return this;
		}

		private void setDestinationFromBase(File base, String subPath) {
			if (subPath == null) {
				throw new NullPointerException("subPath cannot be null");
			}
			mDestinationUri = Uri.withAppendedPath(Uri.fromFile(base), subPath);
		}

		/**
		 * Add an HTTP header to be included with the download request. The
		 * header will be added to the end of the list.
		 * 
		 * @param header
		 *            HTTP header name
		 * @param value
		 *            header value
		 * @return this object
		 * @see <a
		 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2">HTTP/1.1
		 *      Message Headers</a>
		 */
		public Request addRequestHeader(String header, String value) {
			if (header == null) {
				throw new NullPointerException("header cannot be null");
			}
			if (header.contains(":")) {
				throw new IllegalArgumentException("header may not contain ':'");
			}
			if (value == null) {
				value = "";
			}
			mRequestHeaders.add(Pair.create(header, value));
			return this;
		}

		/**
		 * Set the title of this download, to be displayed in notifications (if
		 * enabled). If no title is given, a default one will be assigned based
		 * on the download filename, once the download starts.
		 * 
		 * @return this object
		 */
		public Request setTitle(CharSequence title) {
			mTitle = title;
			return this;
		}

		/**
		 * Set a description of this download, to be displayed in notifications
		 * (if enabled)
		 * 
		 * @return this object
		 */
		public Request setDescription(CharSequence description) {
			mDescription = description;
			return this;
		}

		/**
		 * Set the MIME content type of this download. This will override the
		 * content type declared in the server's response.
		 * 
		 * @see <a
		 *      href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.7">HTTP/1.1
		 *      Media Types</a>
		 * @return this object
		 */
		public Request setMimeType(String mimeType) {
			mMimeType = mimeType;
			return this;
		}

		/**
		 * Control whether a system notification is posted by the download
		 * manager while this download is running. If enabled, the download
		 * manager posts notifications about downloads through the system
		 * {@link com.mozillaonline.providers.NotificationManager}. By default,
		 * a notification is shown.
		 * 
		 * If set to false, this requires the permission
		 * android.permission.DOWNLOAD_WITHOUT_NOTIFICATION.
		 * 
		 * @param show
		 *            whether the download manager should show a notification
		 *            for this download.
		 * @return this object
		 */
		public Request setShowRunningNotification(boolean show) {
			mShowNotification = show;
			return this;
		}

		/**
		 * Restrict the types of networks over which this download may proceed.
		 * By default, all network types are allowed.
		 * 
		 * @param flags
		 *            any combination of the NETWORK_* bit flags.
		 * @return this object
		 */
		public Request setAllowedNetworkTypes(int flags) {
			mAllowedNetworkTypes = flags;
			return this;
		}

		/**
		 * Set whether this download may proceed over a roaming connection. By
		 * default, roaming is allowed.
		 * 
		 * @param allowed
		 *            whether to allow a roaming connection to be used
		 * @return this object
		 */
		public Request setAllowedOverRoaming(boolean allowed) {
			mRoamingAllowed = allowed;
			return this;
		}

		/**
		 * Set whether this download should be displayed in the system's
		 * Downloads UI. True by default.
		 * 
		 * @param isVisible
		 *            whether to display this download in the Downloads UI
		 * @return this object
		 */
		public Request setVisibleInDownloadsUi(boolean isVisible) {
			mIsVisibleInDownloadsUi = isVisible;
			return this;
		}

		/**
		 * @return ContentValues to be passed to DownloadProvider.insert()
		 */
		ContentValues toContentValues(String packageName) {
			ContentValues values = new ContentValues();
			assert mUri != null;
			values.put(Impl.COLUMN_URI, mUri.toString());
			values.put(Impl.COLUMN_IS_PUBLIC_API, true);
			values.put(Impl.COLUMN_NOTIFICATION_PACKAGE, packageName);

			if (mDestinationUri != null) {
				values.put(Impl.COLUMN_DESTINATION, Impl.DESTINATION_FILE_URI);
				values.put(Impl.COLUMN_FILE_NAME_HINT,
						mDestinationUri.toString());
			} else {
				values.put(Impl.COLUMN_DESTINATION, Impl.DESTINATION_EXTERNAL);
			}

			if (!mRequestHeaders.isEmpty()) {
				encodeHttpHeaders(values);
			}

			putIfNonNull(values, Impl.COLUMN_MD5, mMd5);
			putIfNonNull(values, Impl.COLUMN_SOURCE, mSourceType);
			putIfNonNull(values, Impl.COLUMN_LOGO, mLogo);
			putIfNonNull(values, Impl.COLUMN_TITLE, mTitle);
			putIfNonNull(values, Impl.COLUMN_APP_ID, mAppId);
			putIfNonNull(values, Impl.COLUMN_MIME_TYPE, mMimeType);
			putIfNonNull(values, Impl.COLUMN_DESCRIPTION, mDescription);
			putIfNonNull(values, Impl.COLUMN_PACKAGE_NAME, mPackageName);
			putIfNonNull(values, Impl.COLUMN_VERSION_NAME, mVersionName);
			putIfNonNull(values, Impl.COLUMN_VERSION_CODE, mVersionCode);
			// trinea BEGIN TODO
			putIfNonNull(values, Impl.COLUMN_NOTIFICATION_CLASS, mNotiClass);
			putIfNonNull(values, Impl.COLUMN_NOTIFICATION_EXTRAS, mNotiExtras);
			// trinea END TODO

			values.put(Impl.COLUMN_VISIBILITY,
					mShowNotification ? Impl.VISIBILITY_VISIBLE
							: Impl.VISIBILITY_HIDDEN);

			values.put(Impl.COLUMN_ALLOWED_NETWORK_TYPES, mAllowedNetworkTypes);
			values.put(Impl.COLUMN_ALLOW_ROAMING, mRoamingAllowed);
			values.put(Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI,
					mIsVisibleInDownloadsUi);

			// values.put(Impl.COLUMN_CONTROL, Impl.CONTROL_PENDING);
			values.put(Impl.COLUMN_NO_INTEGRITY, 1);

			return values;
		}

		private void encodeHttpHeaders(ContentValues values) {
			int index = 0;
			for (Pair<String, String> header : mRequestHeaders) {
				String headerString = header.first + ": " + header.second;
				values.put(Impl.RequestHeaders.INSERT_KEY_PREFIX + index,
						headerString);
				index++;
			}
		}

		private void putIfNonNull(ContentValues contentValues, String key,
				Object value) {
			if (value != null) {
				contentValues.put(key, value.toString());
			}
		}
	}

	/**
	 * This class may be used to filter download manager queries.
	 */
	public static class Query {
		public static Query query;

		/**
		 * Constant for use with {@link #orderBy}
		 * 
		 * @hide
		 */
		public static final int ORDER_ASCENDING = 1;

		/**
		 * Constant for use with {@link #orderBy}
		 * 
		 * @hide
		 */
		public static final int ORDER_DESCENDING = 2;

		private long[] mIds = null;
		private Integer mStatusFlags = null;
		private String mOrderByColumn = Impl.COLUMN_LAST_MODIFICATION;
		private int mOrderDirection = ORDER_DESCENDING;
		private boolean mOnlyIncludeVisibleInDownloadsUi = false;

		public static Query getInstance() {
			synchronized (Query.class) {
				if (query == null) {
					query = new Query();
				}
			}
			return query;
		}

		/**
		 * Include only the downloads with the given IDs.
		 * 
		 * @return this object
		 */
		public Query setFilterById(long... ids) {
			mIds = ids;
			return this;
		}

		/**
		 * Include only downloads with status matching any the given status
		 * flags.
		 * 
		 * @param flags
		 *            any combination of the STATUS_* bit flags
		 * @return this object
		 */
		public Query setFilterByStatus(int flags) {
			mStatusFlags = flags;
			return this;
		}

		/**
		 * Controls whether this query includes downloads not visible in the
		 * system's Downloads UI.
		 * 
		 * @param value
		 *            if true, this query will only include downloads that
		 *            should be displayed in the system's Downloads UI; if false
		 *            (the default), this query will include both visible and
		 *            invisible downloads.
		 * @return this object
		 * @hide
		 */
		public Query setOnlyIncludeVisibleInDownloadsUi(boolean value) {
			mOnlyIncludeVisibleInDownloadsUi = value;
			return this;
		}

		/**
		 * Change the sort order of the returned Cursor.
		 * 
		 * @param column
		 *            one of the COLUMN_* constants; currently, only
		 *            {@link #COLUMN_LAST_MODIFIED_TIMESTAMP} and
		 *            {@link #COLUMN_TOTAL_SIZE_BYTES} are supported.
		 * @param direction
		 *            either {@link #ORDER_ASCENDING} or
		 *            {@link #ORDER_DESCENDING}
		 * @return this object
		 * @hide
		 */
		public Query orderBy(String column, int direction) {
			if (direction != ORDER_ASCENDING && direction != ORDER_DESCENDING) {
				throw new IllegalArgumentException("Invalid direction: "
						+ direction);
			}

			if (column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP)) {
				mOrderByColumn = Impl.COLUMN_LAST_MODIFICATION;
			} else if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
				mOrderByColumn = Impl.COLUMN_TOTAL_BYTES;
			} else {
				throw new IllegalArgumentException("Cannot order by " + column);
			}
			mOrderDirection = direction;
			return this;
		}

		/**
		 * Run this query using the given ContentResolver.
		 * 
		 * @param projection
		 *            the projection to pass to ContentResolver.query()
		 * @return the Cursor returned by ContentResolver.query()
		 */
		Cursor runQuery(ContentResolver resolver, String[] projection,
				Uri baseUri) {
			Uri uri = baseUri;
			List<String> selectionParts = new ArrayList<String>();
			String[] selectionArgs = null;

			if (mIds != null) {
				selectionParts.add(getWhereClauseForIds(mIds));
				selectionArgs = getWhereArgsForIds(mIds);
			}

			if (mStatusFlags != null) {
				List<String> parts = new ArrayList<String>();
				if ((mStatusFlags & STATUS_PENDING) != 0) {
					parts.add(statusClause("=", Impl.STATUS_PENDING));
				}
				if ((mStatusFlags & STATUS_RUNNING) != 0) {
					parts.add(statusClause("=", Impl.STATUS_RUNNING));
					// trinea BEGIN TODO
					parts.add(statusClause("=",
							Impl.STATUS_INSUFFICIENT_SPACE_ERROR));
					// trinea END TODO
				}
				if ((mStatusFlags & STATUS_PAUSED) != 0) {
					parts.add(statusClause("=", Impl.STATUS_PAUSED_BY_APP));
					parts.add(statusClause("=", Impl.STATUS_WAITING_TO_RETRY));
					parts.add(statusClause("=", Impl.STATUS_WAITING_FOR_NETWORK));
					parts.add(statusClause("=", Impl.STATUS_QUEUED_FOR_WIFI));
				}
				if ((mStatusFlags & STATUS_WAITED) != 0) {
					parts.add(statusClause("=", Impl.STATUS_QUEUED_FOR_LIMIT));
				}
				if ((mStatusFlags & STATUS_SUCCESS) != 0) {
					parts.add(statusClause("=", Impl.STATUS_SUCCESS));
				}
				if ((mStatusFlags & STATUS_INSTALLED) != 0) {
					parts.add(statusClause("=", Impl.STATUS_INSTALLED));
				}
				if ((mStatusFlags & STATUS_FAILED) != 0) {
					parts.add("(" + statusClause(">=", 400) + " AND "
							+ statusClause("<", 600) + ")");
				}
				selectionParts.add(joinStrings(" OR ", parts));
			}

			if (mOnlyIncludeVisibleInDownloadsUi) {
				selectionParts.add(Impl.COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI
						+ " != '0'");
			}

			// only return rows which are not marked 'deleted = 1'
			selectionParts.add(Impl.COLUMN_DELETED + " != '1'");

			String selection = joinStrings(" AND ", selectionParts);
			String orderDirection = (mOrderDirection == ORDER_ASCENDING ? "ASC"
					: "DESC");
			String orderBy = mOrderByColumn + " " + orderDirection;

			return resolver.query(uri, projection, selection, selectionArgs,
					orderBy);
		}

		private String joinStrings(String joiner, Iterable<String> parts) {
			StringBuilder builder = new StringBuilder();
			boolean first = true;
			for (String part : parts) {
				if (!first) {
					builder.append(joiner);
				}
				builder.append(part);
				first = false;
			}
			return builder.toString();
		}

		private String statusClause(String operator, int value) {
			return Impl.COLUMN_STATUS + operator + "'" + value + "'";
		}
	}

	private ContentResolver mResolver;
	private String mPackageName;
	private Uri mBaseUri = Impl.CONTENT_URI;

	public static DownloadManager getInstance(Context context) {
		if (instance == null) {
			synchronized (DownloadManager.class) {
				if (instance == null) {
					instance = new DownloadManager(
							context.getContentResolver(),
							context.getPackageName());
				}
			}
		}
		mContext = context;
		sp = context.getApplicationContext().getSharedPreferences(
				TaskUtil.SP_NAME, Context.MODE_PRIVATE);
		return instance;
	}

	/**
	 * @hide
	 */
	private DownloadManager(ContentResolver resolver, String packageName) {
		mResolver = resolver;
		mPackageName = packageName;
	}

	/**
	 * Makes this object access the download provider through /all_downloads
	 * URIs rather than /my_downloads URIs, for clients that have permission to
	 * do so.
	 * 
	 * @hide
	 */
	public void setAccessAllDownloads(boolean accessAllDownloads) {
		if (accessAllDownloads) {
			mBaseUri = Impl.ALL_DOWNLOADS_CONTENT_URI;
		} else {
			mBaseUri = Impl.CONTENT_URI;
		}
	}

	/**
	 * Enqueue a new download. The download will start automatically once the
	 * download manager is ready to execute it and connectivity is available.
	 * 
	 * @param request
	 *            the parameters specifying this download
	 * @return an ID for the download, unique across the system. This ID is used
	 *         to make future calls related to this download.
	 */
	public long enqueue(Request request) {
		ContentValues values = request.toContentValues(mPackageName);
		Uri downloadUri = mResolver.insert(Impl.CONTENT_URI, values);
		long id = Long.parseLong(downloadUri.getLastPathSegment());
		MobclickAgent.onEvent(mContext, Constants.EVENT_DOWNLOAD_START);
		return id;
	}

	public void updatePkgName(String package_name, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_PACKAGE_NAME, package_name);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public void updateVersionCode(int version_code, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_VERSION_CODE, version_code);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public void updateVersionName(String version_name, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_VERSION_NAME, version_name);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public void updateAppId(String appid, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_APP_ID, appid);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public void updateLogo(String logo, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_LOGO, logo);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public void updateStatusByPkgName(String package_name, int status) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_STATUS, status);
		mResolver.update(Impl.CONTENT_URI, values, Impl.COLUMN_PACKAGE_NAME
				+ " =? ", new String[] { package_name });
	}

	public void updateStatus(String status, String download_id) {
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_STATUS, status);
		mResolver.update(Impl.CONTENT_URI, values, COLUMN_ID + " =? ",
				new String[] { download_id });
	}

	public boolean queryPackageExists(String packageName) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI, new String[] { COLUMN_URI },
				Impl.COLUMN_PACKAGE_NAME + " =? ",
				new String[] { packageName }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	public boolean queryExists(String uri) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI, new String[] { COLUMN_URI },
				COLUMN_URI + " =? ", new String[] { uri }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	public String queryLocalUriByPkgName(String packageName) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_FILE_NAME_HINT },
				Impl.COLUMN_PACKAGE_NAME + " =? ", new String[] { packageName
						+ "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return Uri.parse(cursor.getString(0)).getPath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryLocalUri(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_FILE_NAME_HINT },
				COLUMN_ID + " =? ", new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return Uri.parse(cursor.getString(0)).getPath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryLocalUriByUrl(String url) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_FILE_NAME_HINT }, COLUMN_URI
						+ " =? ", new String[] { url + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return Uri.parse(cursor.getString(0)).getPath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryLocalUriByAppid(String appid) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_FILE_NAME_HINT }, Impl.COLUMN_APP_ID
						+ " =? ", new String[] { appid + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return Uri.parse(cursor.getString(0)).getPath();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public int queryVersionCode(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_VERSION_CODE }, COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getInt(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public String queryVersionName(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_VERSION_NAME }, COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryPackageName(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_PACKAGE_NAME }, COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryPackageName(int appid) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_PACKAGE_NAME }, Impl.COLUMN_APP_ID
						+ " =? ", new String[] { appid + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryPackageName(String url) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_PACKAGE_NAME }, COLUMN_URI + " =? ",
				new String[] { url + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return null;
	}

	public String queryLogo(String url) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_LOGO }, COLUMN_URI + " =? ",
				new String[] { url + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	public String queryLogo(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_LOGO }, COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return null;
	}

	/**
	 * 查询实际status状态值 197 200等
	 * 
	 * @param id
	 * @return
	 */
	public int queryActualStatus(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { COLUMN_STATUS }, COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	/**
	 * 查询实际status状态值 197 200等
	 * 
	 * @param id
	 * @return
	 */
	public int queryActualStatus(String packageName) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { COLUMN_STATUS }, Impl.COLUMN_PACKAGE_NAME
						+ " =? ", new String[] { packageName + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getInt(cursor.getColumnIndex(COLUMN_STATUS));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	/**
	 * 查询运行status代表值 比如STATUS_RUNNING
	 * 
	 * @param id
	 * @return
	 */
	public int queryStatus(long id) {
		Cursor cursor = query(new Query().setFilterById(id));
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast();) {
				int status = cursor
						.getInt(cursor.getColumnIndex(COLUMN_STATUS));
				return status;
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public long queryIdByUri(String uri) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI, new String[] { COLUMN_ID },
				COLUMN_URI + " =? ", new String[] { uri }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getLong(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public long queryIdByPackageName(String packageName) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI, new String[] { COLUMN_ID },
				Impl.COLUMN_PACKAGE_NAME + " =? ",
				new String[] { packageName }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getLong(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public long queryIdByAppId(String appid) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI, new String[] { COLUMN_ID },
				Impl.COLUMN_APP_ID + " =? ", new String[] { appid }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getLong(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return 0;
	}

	public String queryAppidById(long id) {
		Cursor cursor = null;
		cursor = mResolver.query(Impl.CONTENT_URI,
				new String[] { Impl.COLUMN_APP_ID }, Impl.COLUMN_ID + " =? ",
				new String[] { id + "" }, "");
		try {
			if (cursor != null && cursor.moveToFirst()) {
				return cursor.getString(0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return "";
	}

	/**
	 * Query the download manager about downloads that have been requested.
	 * 
	 * @param query
	 *            parameters specifying filters for this query
	 * @return a Cursor over the result set of downloads, with columns
	 *         consisting of all the COLUMN_* constants.
	 */
	public Cursor query(Query query) {
		Cursor underlyingCursor = null;
		// try {
		underlyingCursor = query.runQuery(mResolver, UNDERLYING_COLUMNS,
				mBaseUri);
		if (underlyingCursor == null) {
			return null;
		}
		return new CursorTranslator(underlyingCursor, mBaseUri);
		// } catch (Exception e) {
		// e.printStackTrace();
		// } finally {
		// if (underlyingCursor != null) {
		// underlyingCursor.close();
		// }
		// }
		// return null;
	}

	/**
	 * Open a downloaded file for reading. The download must have completed.
	 * 
	 * @param id
	 *            the ID of the download
	 * @return a read-only {@link ParcelFileDescriptor}
	 * @throws FileNotFoundException
	 *             if the destination file does not already exist
	 */
	public ParcelFileDescriptor openDownloadedFile(long id)
			throws FileNotFoundException {
		return mResolver.openFileDescriptor(getDownloadUri(id), "r");
	}

	/**
	 * Marks the specified download as 'to be deleted'. This is done when a
	 * completed download is to be removed but the row was stored without enough
	 * info to delete the corresponding metadata from Mediaprovider database.
	 * Actual cleanup of this row is done in DownloadService.
	 * 
	 * @param ids
	 *            the IDs of the downloads to be marked 'deleted'
	 * @return the number of downloads actually updated
	 * @hide
	 */
	public int markRowDeleted(long... ids) {
		if (ids == null || ids.length == 0) {
			// called with nothing to remove!
			throw new IllegalArgumentException(
					"input param 'ids' can't be null");
		}
		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_DELETED, STATUS_DELETED);
		return mResolver.update(mBaseUri, values, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
	}

	/**
	 * Cancel downloads and remove them from the download manager. Each download
	 * will be stopped if it was running, and it will no longer be accessible
	 * through the download manager. If a file was already downloaded to
	 * external storage, it will not be deleted.
	 * 
	 * @param ids
	 *            the IDs of the downloads to remove
	 * @return the number of downloads actually removed
	 */
	public int remove(long... ids) {
		if (ids == null || ids.length == 0) {
			// called with nothing to remove!
			throw new IllegalArgumentException(
					"input param 'ids' can't be null");
		}
		MobclickAgent.onEvent(mContext, Constants.EVENT_DOWNLOAD_REMOVE);
		return mResolver.delete(mBaseUri, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
	}

	/**
	 * Pause the given downloads, which must be running. This method will only
	 * work when called from within the download manager's process.
	 * 
	 * @param ids
	 *            the IDs of the downloads
	 * @hide
	 */
	public void pauseDownload(long... ids) {
		Cursor cursor = query(new Query().setFilterById(ids));
		// boolean pauseing;
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				int status = cursor
						.getInt(cursor.getColumnIndex(COLUMN_STATUS));
				if (status != STATUS_RUNNING && status != STATUS_PENDING) {
					// throw new IllegalArgumentException(
					// "Can only pause a running download: "
					// + cursor.getLong(cursor
					// .getColumnIndex(COLUMN_ID)));
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_CONTROL, Impl.CONTROL_PAUSED);
		// TODO
		values.put(Impl.COLUMN_NO_INTEGRITY, 1);
		MobclickAgent.onEvent(mContext, Constants.EVENT_DOWNLOAD_PAUSE);
		// int result =
		mResolver.update(mBaseUri, values, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
		// TODO 暂停下载 其他等待中任务开始下载(下载任务增加) 如果其他任务处于暂停/完成/失败则不处理
	}

	/**
	 * Resume the given downloads, which must be paused. This method will only
	 * work when called from within the download manager's process.
	 * 
	 * @param ids
	 *            the IDs of the downloads
	 * @hide
	 */
	public void resumeDownload(long... ids) {
		Cursor cursor = query(new Query().setFilterById(ids));
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				int status = cursor
						.getInt(cursor.getColumnIndex(COLUMN_STATUS));
				if (status != STATUS_PAUSED) {
					return;
					// throw new IllegalArgumentException(
					// "Cann only resume a paused download: "
					// + cursor.getLong(cursor
					// .getColumnIndex(COLUMN_ID)));
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_STATUS, Impl.STATUS_PENDING);
		values.put(Impl.COLUMN_CONTROL, Impl.CONTROL_RUN);
		MobclickAgent.onEvent(mContext, Constants.EVENT_DOWNLOAD_RESUME);
		mResolver.update(mBaseUri, values, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
		// TODO 恢复下载 如果最大下载任务数未达到 就开始下载(同时任务数加1) 否则下载等待中
		// queryTaskStatus();
	}

	/**
	 * 0:获取已下载 1:总下载大小 2:下载具体值 197
	 * 
	 * @param downloadId
	 * @return
	 */
	public int[] getBytesAndStatus(long downloadId) {
		int[] bytesAndStatus = new int[] { -1, -1, 0 };
		// DownloadManager.Query query = new DownloadManager.Query()
		// .setFilterById(downloadId);
		Cursor c = null;
		try {
			c = mResolver.query(Impl.CONTENT_URI, null, null, null, null);
			// query(query);
			if (c != null && c.moveToFirst()) {
				bytesAndStatus[0] = c.getInt(c
						.getColumnIndexOrThrow(Impl.COLUMN_CURRENT_BYTES));
				bytesAndStatus[1] = c.getInt(c
						.getColumnIndexOrThrow(Impl.COLUMN_TOTAL_BYTES));
				bytesAndStatus[2] = c.getInt(c
						.getColumnIndex(Impl.COLUMN_STATUS));
			}
		} finally {
			if (c != null) {
				c.close();
			}
		}
		return bytesAndStatus;
	}

	public boolean isStatusRunning(long id) {
		return Impl.isStatusRunning(queryActualStatus(id));
	}

	public boolean isStatusClientError(long id) {
		return Impl.isStatusClientError(queryActualStatus(id));
	}

	public boolean isStatusCompleted(long id) {
		return Impl.isStatusCompleted(queryActualStatus(id));
	}

	public boolean isStatusPaused(long id) {
		return Impl.isStatusPaused(queryActualStatus(id));
	}

	public boolean isStatusError(long id) {
		return Impl.isStatusError(queryActualStatus(id));
	}

	public boolean isStatusPending(long id) {
		return Impl.isStatusPending(queryActualStatus(id));
	}

	public boolean isStatusInformational(long id) {
		return Impl.isStatusInformational(queryActualStatus(id));
	}

	public boolean isStatusServerError(long id) {
		return Impl.isStatusServerError(queryActualStatus(id));
	}

	public boolean isStatusSuccessed(long id) {
		return Impl.isStatusSuccessed(queryActualStatus(id));
	}

	public boolean isStatusSuccess(long id) {
		return Impl.isStatusSuccess(queryActualStatus(id));
	}

	public boolean isStatusRunning(int status) {
		return status == STATUS_RUNNING || status == STATUS_PENDING;
	}

	public static boolean isStatusInformational(int status) {
		return status == STATUS_RUNNING || status == STATUS_PENDING
				|| status == STATUS_PAUSED;
	}

	public boolean isStatusCompleted(int status) {
		return status == STATUS_SUCCESS || status == STATUS_INSTALLED
				|| status == STATUS_FAILED;
	}

	public boolean isStatusPaused(int status) {
		return status == STATUS_PAUSED;
	}

	public boolean isStatusError(int status) {
		return status == STATUS_FAILED;
	}

	/**
	 * Restart the given downloads, which must have already completed
	 * (successfully or not). This method will only work when called from within
	 * the download manager's process.
	 * 
	 * @param ids
	 *            the IDs of the downloads
	 * @hide
	 */
	public void restartDownload(long... ids) {
		Cursor cursor = query(new Query().setFilterById(ids));
		try {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				int status = cursor
						.getInt(cursor.getColumnIndex(COLUMN_STATUS));
				if (status != STATUS_SUCCESS && status != STATUS_FAILED) {
					// throw new IllegalArgumentException(
					// "Cannot restart incomplete download: "
					// + cursor.getLong(cursor
					// .getColumnIndex(COLUMN_ID)));
				}
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_CURRENT_BYTES, 0);
		values.put(Impl.COLUMN_TOTAL_BYTES, -1);
		values.putNull(Impl._DATA);
		values.put(Impl.COLUMN_STATUS, Impl.STATUS_PENDING);
		// queryDownloadingTask(mContext);
		MobclickAgent.onEvent(mContext, Constants.EVENT_DOWNLOAD_RESTART);
		mResolver.update(mBaseUri, values, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
		// TODO 重新下载 如果最大下载任务数未达到 就开始下载(同时任务数加1) 否则下载等待中

	}

	// public static int queryDownloadingTask(Context context) {
	// Cursor c = context.getContentResolver().query(
	// Impl.ALL_DOWNLOADS_CONTENT_URI,
	// new String[] { "count(*)" },
	// Impl.COLUMN_STATUS + " = '192' " + " OR " + Impl.COLUMN_STATUS
	// + " = '190' ", null, null);
	//
	// try {
	// if (c != null) {
	// for (c.moveToFirst(); !c.isAfterLast();) {
	// TaskUtil.getTaskUtil(context.getApplicationContext())
	// .setTaskNum(c.getInt(0));
	// return c.getInt(0);
	// }
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return 0;
	// } finally {
	// if (c != null) {
	// c.close();
	// }
	// }
	// return 0;
	// }

	// private void whichThreadToStart(long myID) {
	// ContentValues changeValue = new ContentValues();
	// changeValue.put(Impl.COLUMN_STATUS, Impl.STATUS_PENDING);
	// changeValue.put(Impl.COLUMN_CONTROL, Impl.CONTROL_RUN);
	// mContext.getContentResolver()
	// .update(ContentUris.withAppendedId(
	// Impl.ALL_DOWNLOADS_CONTENT_URI, myID), changeValue,
	// null, null);
	// mContext.startService(new Intent(mContext, DownloadService.class));
	// }

	/**
	 * Get the DownloadProvider URI for the download with the given ID.
	 */
	Uri getDownloadUri(long id) {
		return ContentUris.withAppendedId(mBaseUri, id);
	}

	/**
	 * Get a parameterized SQL WHERE clause to select a bunch of IDs.
	 */
	static String getWhereClauseForIds(long[] ids) {
		StringBuilder whereClause = new StringBuilder();
		whereClause.append("(");
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) {
				whereClause.append("OR ");
			}
			whereClause.append(BaseColumns._ID);
			whereClause.append(" = ? ");
		}
		whereClause.append(")");
		return whereClause.toString();
	}

	/**
	 * Get the selection args for a clause returned by
	 * {@link #getWhereClauseForIds(long[])}.
	 */
	static String[] getWhereArgsForIds(long[] ids) {
		String[] whereArgs = new String[ids.length];
		for (int i = 0; i < ids.length; i++) {
			whereArgs[i] = Long.toString(ids[i]);
		}
		return whereArgs;
	}

	/**
	 * This class wraps a cursor returned by DownloadProvider -- the
	 * "underlying cursor" -- and presents a different set of columns, those
	 * defined in the DownloadManager.COLUMN_* constants. Some columns
	 * correspond directly to underlying values while others are computed from
	 * underlying data.
	 */
	private static class CursorTranslator extends CursorWrapper {
		private static CursorTranslator instance;

		public static CursorTranslator getInstance(Cursor cursor, Uri uri) {
			synchronized (CursorTranslator.class) {
				if (instance == null) {
					instance = new CursorTranslator(cursor, uri);
				}
			}
			return instance;
		}

		private CursorTranslator(Cursor cursor, Uri baseUri) {
			super(cursor);
		}

		@Override
		public int getColumnIndex(String columnName) {
			return Arrays.asList(COLUMNS).indexOf(columnName);
		}

		@Override
		public int getColumnIndexOrThrow(String columnName)
				throws IllegalArgumentException {
			int index = getColumnIndex(columnName);
			if (index == -1) {
				throw new IllegalArgumentException("No such column: "
						+ columnName);
			}
			return index;
		}

		@Override
		public String getColumnName(int columnIndex) {
			int numColumns = COLUMNS.length;
			if (columnIndex < 0 || columnIndex >= numColumns) {
				throw new IllegalArgumentException("Invalid column index "
						+ columnIndex + ", " + numColumns + " columns exist");
			}
			return COLUMNS[columnIndex];
		}

		@Override
		public String[] getColumnNames() {
			String[] returnColumns = new String[COLUMNS.length];
			System.arraycopy(COLUMNS, 0, returnColumns, 0, COLUMNS.length);
			return returnColumns;
		}

		@Override
		public int getColumnCount() {
			return COLUMNS.length;
		}

		@Override
		public byte[] getBlob(int columnIndex) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double getDouble(int columnIndex) {
			return getLong(columnIndex);
		}

		private boolean isLongColumn(String column) {
			return LONG_COLUMNS.contains(column);
		}

		@Override
		public float getFloat(int columnIndex) {
			return (float) getDouble(columnIndex);
		}

		@Override
		public int getInt(int columnIndex) {
			return (int) getLong(columnIndex);
		}

		@Override
		public long getLong(int columnIndex) {
			return translateLong(getColumnName(columnIndex));
		}

		@Override
		public short getShort(int columnIndex) {
			return (short) getLong(columnIndex);
		}

		@Override
		public String getString(int columnIndex) {
			return translateString(getColumnName(columnIndex));
		}

		private String translateString(String column) {
			if (isLongColumn(column)) {
				return Long.toString(translateLong(column));
			}
			if (column.equals(COLUMN_TITLE)) {
				return getUnderlyingString(Impl.COLUMN_TITLE);
			}
			if (column.equals(COLUMN_DESCRIPTION)) {
				return getUnderlyingString(Impl.COLUMN_DESCRIPTION);
			}
			if (column.equals(COLUMN_URI)) {
				return getUnderlyingString(Impl.COLUMN_URI);
			}
			if (column.equals(COLUMN_MEDIA_TYPE)) {
				return getUnderlyingString(Impl.COLUMN_MIME_TYPE);
			}

			assert column.equals(COLUMN_LOCAL_URI);
			return getLocalUri();
		}

		private String getLocalUri() {
			String localPath = getUnderlyingString(Impl._DATA);
			if (localPath == null) {
				return null;
			}
			return Uri.fromFile(new File(localPath)).toString();
		}

		private long translateLong(String column) {
			if (!isLongColumn(column)) {
				// mimic behavior of underlying cursor -- most likely, throw
				// NumberFormatException
				return Long.valueOf(translateString(column));
			}

			if (column.equals(COLUMN_ID)) {
				return getUnderlyingLong(BaseColumns._ID);
			}
			if (column.equals(COLUMN_TOTAL_SIZE_BYTES)) {
				return getUnderlyingLong(Impl.COLUMN_TOTAL_BYTES);
			}
			if (column.equals(COLUMN_STATUS)) {
				return translateStatus((int) getUnderlyingLong(Impl.COLUMN_STATUS));
			}
			if (column.equals(COLUMN_REASON)) {
				return getReason((int) getUnderlyingLong(Impl.COLUMN_STATUS));
			}
			if (column.equals(COLUMN_BYTES_DOWNLOADED_SO_FAR)) {
				return getUnderlyingLong(Impl.COLUMN_CURRENT_BYTES);
			}
			assert column.equals(COLUMN_LAST_MODIFIED_TIMESTAMP);
			return getUnderlyingLong(Impl.COLUMN_LAST_MODIFICATION);
		}

		private long getReason(int status) {
			switch (translateStatus(status)) {
			case STATUS_FAILED:
				return getErrorCode(status);

			case STATUS_PAUSED:
				return getPausedReason(status);
			case STATUS_WAITED:
				return getPausedReason(status);
			default:
				return 0; // arbitrary value when status is not an error
			}
		}

		private long getPausedReason(int status) {
			switch (status) {
			case Impl.STATUS_WAITING_TO_RETRY:
				return PAUSED_WAITING_TO_RETRY;

			case Impl.STATUS_WAITING_FOR_NETWORK:
				return PAUSED_WAITING_FOR_NETWORK;

			case Impl.STATUS_QUEUED_FOR_WIFI:
				return PAUSED_QUEUED_FOR_WIFI;

			case Impl.STATUS_QUEUED_FOR_LIMIT:
				return PAUSED_QUEUED_FOR_LIMIT;

			default:
				return PAUSED_UNKNOWN;
			}
		}

		private long getErrorCode(int status) {
			if ((400 <= status && status < Impl.MIN_ARTIFICIAL_ERROR_STATUS)
					|| (500 <= status && status < 600)) {
				// HTTP status code
				return status;
			}

			switch (status) {
			case Impl.STATUS_FILE_ERROR:
				return ERROR_FILE_ERROR;

			case Impl.STATUS_UNHANDLED_HTTP_CODE:
			case Impl.STATUS_UNHANDLED_REDIRECT:
				return ERROR_UNHANDLED_HTTP_CODE;

			case Impl.STATUS_HTTP_DATA_ERROR:
				return ERROR_HTTP_DATA_ERROR;

			case Impl.STATUS_TOO_MANY_REDIRECTS:
				return ERROR_TOO_MANY_REDIRECTS;

			case Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
				return ERROR_INSUFFICIENT_SPACE;

			case Impl.STATUS_DEVICE_NOT_FOUND_ERROR:
				return ERROR_DEVICE_NOT_FOUND;

			case Impl.STATUS_CANNOT_RESUME:
				return ERROR_CANNOT_RESUME;

			case Impl.STATUS_FILE_ALREADY_EXISTS_ERROR:
				return ERROR_FILE_ALREADY_EXISTS;

			default:
				return ERROR_UNKNOWN;
			}
		}

		private long getUnderlyingLong(String column) {
			return super.getLong(super.getColumnIndex(column));
		}

		private String getUnderlyingString(String column) {
			return super.getString(super.getColumnIndex(column));
		}

		public static int translateStatus(int status) {
			switch (status) {
			case Impl.STATUS_PENDING:
				return STATUS_PENDING;
			case Impl.STATUS_RUNNING:
				return STATUS_RUNNING;
				// trinea BEGIN TODO
			case Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
				// trinea END TODO
			case Impl.STATUS_PAUSED_BY_APP:
			case Impl.STATUS_WAITING_TO_RETRY:
			case Impl.STATUS_WAITING_FOR_NETWORK:
			case Impl.STATUS_QUEUED_FOR_WIFI:
				return STATUS_PAUSED;
			case Impl.STATUS_SUCCESS:
				return STATUS_SUCCESS;
			case Impl.STATUS_CANCELED:
				return STATUS_CANCELED;
			case Impl.STATUS_QUEUED_FOR_LIMIT:
				return STATUS_WAITED;
			case Impl.STATUS_INSTALLED:
				return STATUS_INSTALLED;
			default:
				assert Impl.isStatusError(status);
				return STATUS_FAILED;
			}
		}
	}

	/**
	 * 隐藏已经下载完成的OTA任务
	 */
	public int hideDownload(long... ids) {
		if (ids == null || ids.length == 0) {
			// called with nothing to remove!
			throw new IllegalArgumentException(
					"input param 'ids' can't be null");
		}

		ContentValues values = new ContentValues();
		values.put(Impl.COLUMN_VISIBILITY, Impl.VISIBILITY_HIDDEN);
		return mResolver.update(mBaseUri, values, getWhereClauseForIds(ids),
				getWhereArgsForIds(ids));
	}

	// TODO====TODO
	/**
	 * Implementation details
	 * 
	 * Exposes constants used to interact with the download manager's content
	 * provider. The constants URI ... STATUS are the names of columns in the
	 * downloads table.
	 * 
	 */
	public static final class Impl implements BaseColumns {
		private Impl() {
		}

		/**
		 * DownloadProvider authority
		 */
		public static final String AUTHORITY = "cn.cy.mobilegames.yyjia.forelders.download";
		/**
		 * An identifier for a particular download, unique across the system.
		 * Clients use this ID to make subsequent calls related to the download.
		 */
		public final static String COLUMN_ID = BaseColumns._ID;

		/**
		 * The name of the column containing application-specific data.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read/Write
		 * </P>
		 */
		public static final String COLUMN_APP_DATA = "entity";

		/**
		 * The name of the column containing the filename where the downloaded
		 * data was actually stored.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 */
		public static final String COLUMN_DATA = "_data";

		/**
		 * The times of trying to get contact with server but failed
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 */
		public static final String COLUMN_FAILED_CONNECTIONS = "numfailed";

		/**
		 * The times of request meet the HTTP redirect directives
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 */
		public static final String COLUMN_RETRY_AFTER_REDIRECT_COUNT = "redirectcount";

		/**
		 * The column that is used for the downloads's ETag(Guarantee the file
		 * integrity)
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_ETAG = "etag";

		/**
		 * The column that is used to remember where download request comes from
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 */
		public static final String COLUMN_SOURCE = "source";

		/**
		 * The column that is used for the initiating app's MD5
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_MD5 = "md5";

		/**
		 * The package name of the APK file. This not necessary for other files.
		 */
		public static final String COLUMN_PACKAGE_NAME = "package_name";
		public static final String COLUMN_VERSION_NAME = "version_name";
		public static final String COLUMN_VERSION_CODE = "version_code";

		/*
		 * Lists the destinations that an application can specify for a
		 * download.
		 */
		/**
		 * This download will be saved to the external storage. This is the
		 * default behavior, and should be used for any file that the user can
		 * freely access, copy, delete. Even with that destination, unencrypted
		 * DRM files are saved in secure internal storage. Downloads to the
		 * external destination only write files for which there is a registered
		 * handler. The resulting files are accessible by filename to all
		 * applications.
		 */
		public static final int DESTINATION_EXTERNAL = 0;

		/**
		 * This download will be saved to the download manager's private
		 * partition. This is the behavior used by applications that want to
		 * download private files that are used and deleted soon after they get
		 * downloaded. All file types are allowed, and only the initiating
		 * application can access the file (indirectly through a content
		 * provider). This requires the
		 * android.permission.ACCESS_DOWNLOAD_MANAGER_ADVANCED permission.
		 */
		public static final int DESTINATION_CACHE_PARTITION = 1;

		/**
		 * This download will be saved to the download manager's private
		 * partition and will be purged as necessary to make space. This is for
		 * private files (similar to CACHE_PARTITION) that aren't deleted
		 * immediately after they are used, and are kept around by the download
		 * manager as long as space is available.
		 */
		public static final int DESTINATION_CACHE_PARTITION_PURGEABLE = 2;

		/**
		 * This download will be saved to the location given by the file URI in
		 * {@link #COLUMN_FILE_NAME_HINT}.
		 */
		public static final int DESTINATION_FILE_URI = 3;

		/**
		 * This download is allowed to run.
		 */
		public static final int CONTROL_RUN = 0;

		/**
		 * This download must pause at the first opportunity.
		 */
		public static final int CONTROL_PAUSED = 1;

		/**
		 * This download is in the task waiting queue
		 */
		public static final int CONTROL_PENDING = 2;

		/*
		 * Lists the states that the download manager can set on a download to
		 * notify applications of the download progress. The codes follow the
		 * HTTP families:<br> 1xx: informational<br> 2xx: success<br> 3xx:
		 * redirects (not used by the download manager)<br> 4xx: client
		 * errors<br> 5xx: server errors
		 */

		/**
		 * Returns whether the status is informational (i.e. [100-199]).
		 */
		public static boolean isStatusInformational(int status) {
			return (status >= 100 && status < 200);
		}

		/**
		 * 下载进行中
		 */
		public static boolean isStatusRunning(int status) {
			return status == STATUS_RUNNING || status == STATUS_PENDING;
		}

		/**
		 * 下载等待中
		 */
		public static boolean isStatusPending(int status) {
			return status == STATUS_PENDING || status == STATUS_PAUSED_BY_APP
					|| status == STATUS_WAITING_TO_RETRY
					|| status == STATUS_WAITING_FOR_NETWORK
					|| status == STATUS_QUEUED_FOR_LIMIT
					|| status == STATUS_QUEUED_FOR_WIFI;
		}

		/**
		 * 准备下载
		 */
		public static boolean isStatusPend(int status) {
			return status == STATUS_PENDING;
		}

		/**
		 * Returns whether the status is a success (i.e. 2xx).
		 */
		public static boolean isStatusSuccess(int status) {
			return (status >= 200 && status < 300);
		}

		/**
		 * Returns whether the status has installed (260).
		 */
		public static boolean isStatusInstalled(int status) {
			return status == STATUS_INSTALLED;
		}

		/**
		 * Returns whether the status is a success (200).
		 */
		public static boolean isStatusSuccessed(int status) {
			return status == STATUS_SUCCESS;
		}

		/**
		 * Returns whether the status is a success (197).
		 */
		public static boolean isStatusWaited(int status) {
			return status == Impl.STATUS_QUEUED_FOR_LIMIT;
		}

		/**
		 * Returns whether the status is an error (i.e. [400-599]).
		 */
		public static boolean isStatusError(int status) {
			return (status >= 400 && status < 600);
		}

		/**
		 * Returns whether the status is a client error (i.e. 4xx).
		 */
		public static boolean isStatusClientError(int status) {
			return (status >= 400 && status < 500);
		}

		/**
		 * Returns whether the status is a server error (i.e. 5xx).
		 */
		public static boolean isStatusServerError(int status) {
			return (status >= 500 && status < 600);
		}

		/**
		 * Returns whether the download has completed (either with success or
		 * error).
		 */
		public static boolean isStatusCompleted(int status) {
			return (status >= 200 && status < 300)
					|| (status >= 400 && status < 600);
		}

		public static boolean isStatusPaused(int status) {
			return status == Impl.STATUS_PAUSED_BY_APP
					|| status == STATUS_WAITING_TO_RETRY
					|| status == STATUS_WAITING_FOR_NETWORK
					|| status == STATUS_QUEUED_FOR_WIFI;
		}

		public static boolean isStatusQueue(int status) {
			return status == Impl.STATUS_QUEUED_FOR_LIMIT;
		}

		/**
		 * This download hasn't stated yet
		 */
		public static final int STATUS_PENDING = 190;

		/**
		 * This download has started
		 */
		public static final int STATUS_RUNNING = 192;

		/**
		 * This download has been paused by the owning app.
		 */
		public static final int STATUS_PAUSED_BY_APP = 193;

		/**
		 * This download encountered some network error and is waiting before
		 * retrying the request.
		 */
		public static final int STATUS_WAITING_TO_RETRY = 194;

		/**
		 * This download is waiting for network connectivity to proceed.
		 */
		public static final int STATUS_WAITING_FOR_NETWORK = 195;

		/**
		 * This download exceeded a size limit for mobile networks and is
		 * waiting for a Wi-Fi connection to proceed.
		 */
		public static final int STATUS_QUEUED_FOR_WIFI = 196;

		/**
		 * This download exceeded a download Task limit and is waiting for
		 * proceed.
		 */
		public static final int STATUS_QUEUED_FOR_LIMIT = 197;

		/**
		 * This download has successfully completed. Warning: there might be
		 * other status values that indicate success in the future. Use
		 * isSucccess() to capture the entire category.
		 */
		public static final int STATUS_SUCCESS = 200;

		/**
		 * This downloaded apk has successfully installed.
		 */
		public static final int STATUS_INSTALLED = 260;

		/**
		 * This downloaded apk has't ever installed.
		 */
		public static final int STATUS_UNINSTALLED = 261;

		/**
		 * This request couldn't be parsed. This is also used when processing
		 * requests with unknown/unsupported URI schemes.
		 */
		public static final int STATUS_BAD_REQUEST = 400;

		/**
		 * This download can't be performed because the content type cannot be
		 * handled.
		 */
		public static final int STATUS_NOT_ACCEPTABLE = 406;

		/**
		 * This download cannot be performed because the length cannot be
		 * determined accurately. This is the code for the HTTP error "Length
		 * Required", which is typically used when making requests that require
		 * a content length but don't have one, and it is also used in the
		 * client when a response is received whose length cannot be determined
		 * accurately (therefore making it impossible to know when a download
		 * completes).
		 */
		public static final int STATUS_LENGTH_REQUIRED = 411;

		/**
		 * This download was interrupted and cannot be resumed. This is the code
		 * for the HTTP error "Precondition Failed", and it is also used in
		 * situations where the client doesn't have an ETag at all.
		 */
		public static final int STATUS_PRECONDITION_FAILED = 412;

		/**
		 * the MD5 check error
		 */
		public static final int STATUS_FILE_MD5_ERROR = 486;

		/**
		 * The lowest-valued error status that is not an actual HTTP status
		 * code.
		 */
		public static final int STATUS_MIN_ARTIFICIAL_ERROR_STATUS = 487;

		/**
		 * The requested destination file already exists.
		 */
		public static final int STATUS_FILE_ALREADY_EXISTS_ERROR = 488;

		/**
		 * Some possibly transient error occurred, but we can't resume the
		 * download.
		 */
		public static final int STATUS_CANNOT_RESUME = 489;

		/**
		 * This download was canceled
		 */
		public static final int STATUS_CANCELED = 490;

		/**
		 * This download has completed with an error. Warning: there will be
		 * other status values that indicate errors in the future. Use
		 * isStatusError() to capture the entire category.
		 */
		public static final int STATUS_UNKNOWN_ERROR = 491;

		/**
		 * This download couldn't be completed because of a storage issue.
		 * Typically, that's because the filesystem is missing or full. Use the
		 * more specific {@link #STATUS_INSUFFICIENT_SPACE_ERROR} and
		 * {@link #STATUS_DEVICE_NOT_FOUND_ERROR} when appropriate.
		 */
		public static final int STATUS_FILE_ERROR = 492;

		/**
		 * This download couldn't be completed because of an HTTP redirect
		 * response that the download manager couldn't handle.
		 */
		public static final int STATUS_UNHANDLED_REDIRECT = 493;

		/**
		 * This download couldn't be completed because of an unspecified
		 * unhandled HTTP code.
		 */
		public static final int STATUS_UNHANDLED_HTTP_CODE = 494;

		/**
		 * This download couldn't be completed because of an error receiving or
		 * processing data at the HTTP level.
		 */
		public static final int STATUS_HTTP_DATA_ERROR = 495;

		/**
		 * This download couldn't be completed because of an HttpException while
		 * setting up the request.
		 */
		public static final int STATUS_HTTP_EXCEPTION = 496;

		/**
		 * This download couldn't be completed because there were too many
		 * redirects.
		 */
		public static final int STATUS_TOO_MANY_REDIRECTS = 497;

		/**
		 * This download couldn't be completed due to insufficient storage
		 * space. Typically, this is because the SD card is full.
		 */
		public static final int STATUS_INSUFFICIENT_SPACE_ERROR = 498;

		/**
		 * This download couldn't be completed because no external storage
		 * device was found. Typically, this is because the SD card is not
		 * mounted.
		 */
		public static final int STATUS_DEVICE_NOT_FOUND_ERROR = 499;

		/**
		 * This download is visible but only shows in the notifications while
		 * it's in progress.
		 */
		public static final int VISIBILITY_VISIBLE = 0;

		/**
		 * This download is visible and shows in the notifications while in
		 * progress and after completion.
		 */
		public static final int VISIBILITY_VISIBLE_NOTIFY_COMPLETED = 1;

		/**
		 * This download doesn't show in the UI or in the notifications.
		 */
		public static final int VISIBILITY_HIDDEN = 2;

		/**
		 * The permission to access the download manager
		 * 
		 * @hide
		 */
		public static final String PERMISSION_ACCESS = "cn.cy.mobilegames.yyjia.forelders.ACCESS_DOWNLOAD_MANAGER";

		/**
		 * The permission to access the download manager's advanced functions
		 * 
		 * @hide
		 */
		public static final String PERMISSION_ACCESS_ADVANCED = "cn.cy.mobilegames.yyjia.forelders.ACCESS_DOWNLOAD_MANAGER_ADVANCED";

		/**
		 * The permission to access the all the downloads in the manager.
		 */
		public static final String PERMISSION_ACCESS_ALL = "cn.cy.mobilegames.yyjia.forelders.ACCESS_ALL_DOWNLOADS";

		/**
		 * The permission to send broadcasts on download completion
		 * 
		 * @hide
		 */
		public static final String PERMISSION_SEND_INTENTS = "cn.cy.mobilegames.yyjia.forelders.SEND_DOWNLOAD_COMPLETED_INTENTS";

		/**
		 * The permission to download files without any system notification
		 * being shown.
		 */
		public static final String PERMISSION_NO_NOTIFICATION = "cn.cy.mobilegames.yyjia.forelders.DOWNLOAD_WITHOUT_NOTIFICATION";

		/**
		 * The content:// URI for the data table in the provider
		 * 
		 * @hide
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/my_downloads");

		/**
		 * The content URI for accessing all downloads across all UIDs (requires
		 * the ACCESS_ALL_DOWNLOADS permission).
		 */
		public static final Uri ALL_DOWNLOADS_CONTENT_URI = Uri
				.parse("content://" + AUTHORITY + "/all_downloads");

		/**
		 * Broadcast Action: this is sent by the download manager to the app
		 * that had initiated a download when that download completes. The
		 * download's content: uri is specified in the intent's data.
		 * 
		 * @hide
		 */
		public static final String ACTION_DOWNLOAD_COMPLETED = "android.intent.action.DOWNLOAD_COMPLETED";

		/**
		 * Broadcast Action: this is sent by the download manager to the app
		 * that had initiated a download when the user selects the notification
		 * associated with that download. The download's content: uri is
		 * specified in the intent's data if the click is associated with a
		 * single download, or Downloads.CONTENT_URI if the notification is
		 * associated with multiple downloads. Note: this is not currently sent
		 * for downloads that have completed successfully.
		 * 
		 * @hide
		 */
		public static final String ACTION_NOTIFICATION_CLICKED = "android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED";

		/**
		 * The name of the column containing the URI of the data being
		 * downloaded.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_URI = "uri";

		/**
		 * The name of the column containing the flags that indicates whether
		 * the initiating application is capable of verifying the integrity of
		 * the downloaded file. When this flag is set, the download manager
		 * performs downloads and reports success even in some situations where
		 * it can't guarantee that the download has completed (e.g. when doing a
		 * byte-range request without an ETag, or when it can't determine
		 * whether a download fully completed).
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_NO_INTEGRITY = "no_integrity";

		/**
		 * The name of the column containing the filename that the initiating
		 * application recommends. When possible, the download manager will
		 * attempt to use this filename, or a variation, as the actual name for
		 * the file.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_FILE_NAME_HINT = "hint";

		/**
		 * The name of the column containing the filename where the downloaded
		 * data was actually stored.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String _DATA = "_data";

		/**
		 * the uri of the column containing the logo uri of the downloaded data
		 */
		public static final String COLUMN_LOGO = "logo";

		public static final String COLUMN_APP_ID = "appid";

		/**
		 * The name of the column containing the MIME type of the downloaded
		 * data.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_MIME_TYPE = "mimetype";

		/**
		 * The name of the column containing the flag that controls the
		 * destination of the download. See the DESTINATION_* constants for a
		 * list of legal values.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_DESTINATION = "destination";

		/**
		 * The name of the column containing the flags that controls whether the
		 * download is displayed by the UI. See the VISIBILITY_* constants for a
		 * list of legal values.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Init/Read/Write
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_VISIBILITY = "visibility";

		/**
		 * The name of the column containing the current control state of the
		 * download. Applications can write to this to control (pause/resume)
		 * the download. the CONTROL_* constants for a list of legal values.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_CONTROL = "control";

		/**
		 * The name of the column containing the current status of the download.
		 * Applications can read this to follow the progress of each download.
		 * See the STATUS_* constants for a list of legal values.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_STATUS = "status";

		/**
		 * The name of the column containing the date at which some interesting
		 * status changed in the download. Stored as a
		 * System.currentTimeMillis() value.
		 * <P>
		 * Type: BIGINT
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_LAST_MODIFICATION = "lastmod";

		/**
		 * The name of the column containing the package name of the application
		 * that initiating the download. The download manager will send
		 * notifications to a component in this package when the download
		 * completes.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";

		/**
		 * The name of the column containing the component name of the class
		 * that will receive notifications associated with the download. The
		 * package/class combination is passed to
		 * Intent.setClassName(String,String).
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";

		/**
		 * If extras are specified when requesting a download they will be
		 * provided in the intent that is sent to the specified class and
		 * package when a download has finished.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_NOTIFICATION_EXTRAS = "notificationextras";

		/**
		 * The name of the column contain the values of the cookie to be used
		 * for the download. This is used directly as the value for the Cookie:
		 * HTTP header that gets sent with the request.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_COOKIE_DATA = "cookiedata";

		/**
		 * The name of the column containing the user agent that the initiating
		 * application wants the download manager to use for this download.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_USER_AGENT = "useragent";

		/**
		 * The name of the column containing the referer (sic) that the
		 * initiating application wants the download manager to use for this
		 * download.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_REFERER = "referer";

		/**
		 * The name of the column containing the total size of the file being
		 * downloaded.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_TOTAL_BYTES = "total_bytes";

		/**
		 * The name of the column containing the size of the part of the file
		 * that has been downloaded so far.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_CURRENT_BYTES = "current_bytes";

		/**
		 * The name of the column where the initiating application can provide
		 * the UID of another application that is allowed to access this
		 * download. If multiple applications share the same UID, all those
		 * applications will be allowed to access this download. This column can
		 * be updated after the download is initiated. This requires the
		 * permission cn.cy.mobilegames.yyjia.forelders.ACCESS_DOWNLOAD_MANAGER_ADVANCED.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Init
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_OTHER_UID = "otheruid";

		/**
		 * The name of the column where the initiating application can provided
		 * the title of this download. The title will be displayed ito the user
		 * in the list of downloads.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read/Write
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_TITLE = "title";

		/**
		 * The name of the column where the initiating application can provide
		 * the description of this download. The description will be displayed
		 * to the user in the list of downloads.
		 * <P>
		 * Type: TEXT
		 * </P>
		 * <P>
		 * Owner can Init/Read/Write
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_DESCRIPTION = "description";

		/**
		 * The name of the column indicating whether the download was requesting
		 * through the public API. This controls some differences in behavior.
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_IS_PUBLIC_API = "is_public_api";

		/**
		 * The name of the column indicating whether roaming connections can be
		 * used. This is only used for public API downloads.
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_ALLOW_ROAMING = "allow_roaming";

		/**
		 * The name of the column holding a bitmask of allowed network types.
		 * This is only used for public API downloads.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_ALLOWED_NETWORK_TYPES = "allowed_network_types";

		/**
		 * Whether or not this download should be displayed in the system's
		 * Downloads UI. Defaults to true.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 * <P>
		 * Owner can Init/Read
		 * </P>
		 */
		public static final String COLUMN_IS_VISIBLE_IN_DOWNLOADS_UI = "is_visible_in_downloads_ui";

		/**
		 * If true, the user has confirmed that this download can proceed over
		 * the mobile network even though it exceeds the recommended maximum
		 * size.
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 */
		public static final String COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT = "bypass_recommended_size_limit";

		/**
		 * Set to true if this download is deleted. mdelete == 1 return true;
		 * <P>
		 * Type: BOOLEAN
		 * </P>
		 * <P>
		 * Owner can Read
		 * </P>
		 * 
		 * @hide
		 */
		public static final String COLUMN_DELETED = "deleted";

		/*
		 * Lists the destinations that an application can specify for a
		 * download.
		 */

		/**
		 * The lowest-valued error status that is not an actual HTTP status
		 * code.
		 */
		public static final int MIN_ARTIFICIAL_ERROR_STATUS = 488;

		/**
		 * Constants related to HTTP request headers associated with each
		 * download.
		 */
		public static class RequestHeaders {
			public static final String HEADERS_DB_TABLE = "request_headers";
			public static final String COLUMN_DOWNLOAD_ID = "download_id";
			public static final String COLUMN_HEADER = "header";
			public static final String COLUMN_VALUE = "value";

			/**
			 * Path segment to add to a download URI to retrieve request headers
			 */
			public static final String URI_SEGMENT = "headers";

			/**
			 * Prefix for ContentValues keys that contain HTTP header lines, to
			 * be passed to DownloadProvider.insert().
			 */
			public static final String INSERT_KEY_PREFIX = "http_header_";
		}

	}

	/**
	 * 暂停监听器
	 * 
	 */
	public interface PauseListener {
		/**
		 * 暂停处理中
		 */
		void isPausing();

		/**
		 * 暂停成功
		 */
		void isPauseOver();

		/**
		 * The CALLBACK for success aMarket API HTTP response
		 * 
		 * @param response
		 *            the HTTP response
		 */
		void onSuccess(int method, Object obj);

		/**
		 * The CALLBACK for failure aMarket API HTTP response
		 * 
		 * @param statusCode
		 *            the HTTP response status code
		 */
		void onError(int method, int statusCode);
	}
}
