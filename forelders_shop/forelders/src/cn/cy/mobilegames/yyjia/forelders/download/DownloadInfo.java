/*
 * Copyright (C) 2008 The Android Open Source Project
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.CharArrayBuffer;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import android.util.Pair;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

/**
 * Stores information about an individual download.
 */
public class DownloadInfo {

	public static class Reader {
		private ContentResolver mResolver;
		private Cursor mCursor;
		private CharArrayBuffer mOldChars;
		private CharArrayBuffer mNewChars;

		public Reader(ContentResolver resolver, Cursor cursor) {
			mResolver = resolver;
			mCursor = cursor;
		}

		public DownloadInfo newDownloadInfo(Context context,
				SystemFacade systemFacade) {
			DownloadInfo info = new DownloadInfo(context, systemFacade);
			updateFromDatabase(info);
			readRequestHeaders(info);
			return info;
		}

		public void updateFromDatabase(DownloadInfo info) {
			info.mId = getLong(BaseColumns._ID);
			info.mUri = getString(info.mUri, Impl.COLUMN_URI);
			info.mNoIntegrity = getInt(Impl.COLUMN_NO_INTEGRITY) == 1;
			info.mHint = getString(info.mHint, Impl.COLUMN_FILE_NAME_HINT);
			info.mFileName = getString(info.mFileName, Impl._DATA);
			info.mMimeType = getString(info.mMimeType, Impl.COLUMN_MIME_TYPE);
			info.mDestination = getInt(Impl.COLUMN_DESTINATION);
			info.mVisibility = getInt(Impl.COLUMN_VISIBILITY);
			info.mStatus = getInt(Impl.COLUMN_STATUS);
			info.mNumFailed = getInt(Constants.FAILED_CONNECTIONS);
			int retryRedirect = getInt(Constants.RETRY_AFTER_X_REDIRECT_COUNT);
			info.mRetryAfter = retryRedirect & 0xfffffff;
			info.mLastMod = getLong(Impl.COLUMN_LAST_MODIFICATION);
			info.mPackage = getString(info.mPackage,
					Impl.COLUMN_NOTIFICATION_PACKAGE);
			info.mClass = getString(info.mClass, Impl.COLUMN_NOTIFICATION_CLASS);
			info.mExtras = getString(info.mExtras,
					Impl.COLUMN_NOTIFICATION_EXTRAS);
			info.mCookies = getString(info.mCookies, Impl.COLUMN_COOKIE_DATA);
			info.mUserAgent = getString(info.mUserAgent, Impl.COLUMN_USER_AGENT);
			info.mReferer = getString(info.mReferer, Impl.COLUMN_REFERER);
			info.mTotalBytes = getLong(Impl.COLUMN_TOTAL_BYTES);
			info.mCurrentBytes = getLong(Impl.COLUMN_CURRENT_BYTES);
			info.mETag = getString(info.mETag, Constants.ETAG);
			info.mDeleted = getInt(Impl.COLUMN_DELETED) == DownloadManager.STATUS_DELETED;
			info.mIsPublicApi = getInt(Impl.COLUMN_IS_PUBLIC_API) != 0;
			info.mAllowedNetworkTypes = getInt(Impl.COLUMN_ALLOWED_NETWORK_TYPES);
			info.mAllowRoaming = getInt(Impl.COLUMN_ALLOW_ROAMING) != 0;
			info.mTitle = getString(info.mTitle, Impl.COLUMN_TITLE);
			info.mDescription = getString(info.mDescription,
					Impl.COLUMN_DESCRIPTION);
			info.mBypassRecommendedSizeLimit = getInt(Impl.COLUMN_BYPASS_RECOMMENDED_SIZE_LIMIT);
			info.mRedirectCount = retryRedirect >> 28;
			// info.mSource = getInt(Impl.COLUMN_SOURCE);
			// try {
			// info.mPackageName = getString(info.mPackageName,
			// Impl.COLUMN_PACKAGE_NAME);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }
			// info.mMD5 = getString(info.mPackageName, Impl.COLUMN_MD5);
			synchronized (this) {
				info.mControl = getInt(Impl.COLUMN_CONTROL);
			}

		}

		private void readRequestHeaders(DownloadInfo info) {
			info.mRequestHeaders.clear();
			Uri headerUri = Uri.withAppendedPath(info.getAllDownloadsUri(),
					Impl.RequestHeaders.URI_SEGMENT);
			Cursor cursor = mResolver.query(headerUri, null, null, null, null);
			try {
				int headerIndex = cursor
						.getColumnIndexOrThrow(Impl.RequestHeaders.COLUMN_HEADER);
				int valueIndex = cursor
						.getColumnIndexOrThrow(Impl.RequestHeaders.COLUMN_VALUE);
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
						.moveToNext()) {
					addHeader(info, cursor.getString(headerIndex),
							cursor.getString(valueIndex));
				}
			} finally {
				cursor.close();
			}

			if (info.mCookies != null) {
				addHeader(info, "Cookie", info.mCookies);
			}
			if (info.mReferer != null) {
				addHeader(info, "Referer", info.mReferer);
			}
		}

		private void addHeader(DownloadInfo info, String header, String value) {
			info.mRequestHeaders.add(Pair.create(header, value));
		}

		/**
		 * Returns a String that holds the current value of the column,
		 * optimizing for the case where the value hasn't changed.
		 */
		private String getString(String old, String column) {
			int index = mCursor.getColumnIndexOrThrow(column);
			if (old == null) {
				return mCursor.getString(index);
			}
			if (mNewChars == null) {
				mNewChars = new CharArrayBuffer(128);
			}
			mCursor.copyStringToBuffer(index, mNewChars);
			int length = mNewChars.sizeCopied;
			if (length != old.length()) {
				return new String(mNewChars.data, 0, length);
			}
			if (mOldChars == null || mOldChars.sizeCopied < length) {
				mOldChars = new CharArrayBuffer(length);
			}
			char[] oldArray = mOldChars.data;
			char[] newArray = mNewChars.data;
			old.getChars(0, length, oldArray, 0);
			for (int i = length - 1; i >= 0; --i) {
				if (oldArray[i] != newArray[i]) {
					return new String(newArray, 0, length);
				}
			}
			return old;
		}

		private Integer getInt(String column) {
			return mCursor.getInt(mCursor.getColumnIndexOrThrow(column));
		}

		private Long getLong(String column) {
			return mCursor.getLong(mCursor.getColumnIndexOrThrow(column));
		}

		// TODO

		public Reader(Cursor cursor) {
			mCursor = cursor;
		}

		// public DownloadInfo newDownloadInfo(Context context) {
		// DownloadInfo info = new DownloadInfo(context);
		// updateFromDatabase(info);
		// return info;
		// }

	}

	// the following NETWORK_* constants are used to indicates specfic reasons
	// for disallowing a
	// download from using a network, since specific causes can require special
	// handling

	/**
	 * The network is usable for the given download.
	 */
	public static final int NETWORK_OK = 1;

	/**
	 * There is no network connectivity.
	 */
	public static final int NETWORK_NO_CONNECTION = 2;

	/**
	 * The download exceeds the maximum size for this network.
	 */
	public static final int NETWORK_UNUSABLE_DUE_TO_SIZE = 3;

	/**
	 * The download exceeds the recommended maximum size for this network, the
	 * user must confirm for this download to proceed without WiFi.
	 */
	public static final int NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE = 4;

	/**
	 * The current connection is roaming, and the download can't proceed over a
	 * roaming connection.
	 */
	public static final int NETWORK_CANNOT_USE_ROAMING = 5;

	/**
	 * The app requesting the download specific that it can't use the current
	 * network connection.
	 */
	public static final int NETWORK_TYPE_DISALLOWED_BY_REQUESTOR = 6;

	/**
	 * For intents used to notify the user that a download exceeds a size
	 * threshold, if this extra is true, WiFi is required for this download
	 * size; otherwise, it is only recommended.
	 */
	public static final String EXTRA_IS_WIFI_REQUIRED = "isWifiRequired";

	// ID
	public long mId;
	// 下载链接
	public String mUri;
	// 提示
	public String mHint;
	// 文件名
	public String mFileName;
	// MIME TYPE
	public String mMimeType;
	// 存储路径
	public int mDestination;
	// 可见性
	public int mVisibility;
	// 下载控制（暂停、取消）
	public int mControl;
	// 下载状态
	public int mStatus;
	// 下载失败次数
	public int mNumFailed;
	// 重试时间
	public int mRetryAfter;
	// 重定向次数
	public int mRedirectCount;
	// 最后修改时间
	public long mLastMod;
	// 提醒包名
	public String mPackage;
	// 提醒类名
	public String mClass;
	// 用于提醒的额外信息
	public String mExtras;
	// 文件大小
	public long mTotalBytes;
	// 已经下载的大小
	public long mCurrentBytes;
	// 文件完整性ETAG
	public String mETag;
	// 是否删除此记录
	public boolean mDeleted;
	// 标题
	public String mTitle;
	// 描述信息
	public String mDescription;
	// // 请求来源
	// public int mSource;
	// MD5校验码
	public String mMD5;
	// 应用包名
	public String mPackageName;

	public String mCookies;
	public String mUserAgent;
	public String mReferer;
	public boolean mNoIntegrity;
	public boolean mIsPublicApi;
	public int mAllowedNetworkTypes;
	public boolean mAllowRoaming;
	public int mBypassRecommendedSizeLimit;
	public int mFuzz;
	public volatile boolean mHasActiveThread;

	private List<Pair<String, String>> mRequestHeaders = new ArrayList<Pair<String, String>>();
	private SystemFacade mSystemFacade;
	private Context mContext;

	private DownloadInfo(Context context, SystemFacade systemFacade) {
		mContext = context;
		mSystemFacade = systemFacade;
		mFuzz = Helpers.sRandom.nextInt(1001);
	}

	// private DownloadInfo(Context context) {
	// mContext = context;
	// mFuzz = Helpers.sRandom.nextInt(1001);
	// }

	public Collection<Pair<String, String>> getHeaders() {
		return Collections.unmodifiableList(mRequestHeaders);
	}

	public void sendIntentIfRequested() {
		if (mPackage == null) {
			return;
		}

		Intent intent;
		if (mIsPublicApi) {
			intent = new Intent(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
			intent.setPackage(mPackage);
			intent.putExtra(DownloadManager.EXTRA_DOWNLOAD_ID, mId);
			intent.putExtra(cn.cy.mobilegames.yyjia.forelders.Constants.KEY_FILE_PATH, mFileName);
		} else { // legacy behavior
			if (mClass == null) {
				return;
			}
			intent = new Intent(Impl.ACTION_DOWNLOAD_COMPLETED);
			intent.setClassName(mPackage, mClass);
			if (mExtras != null) {
				intent.putExtra(Impl.COLUMN_NOTIFICATION_EXTRAS, mExtras);
			}
			// We only send the content: URI, for security reasons. Otherwise,
			// malicious
			// applications would have an easier time spoofing download results
			// by
			// sending spoofed intents.
			intent.setData(getMyDownloadsUri());
		}
		mSystemFacade.sendBroadcast(intent);
		Utils.E(" send action " + intent.getAction() + "  mid " + mId
				+ "  pkgName " + intent.getPackage());
	}

	/**
	 * Returns the time when a download should be restarted.
	 */
	public long restartTime(long now) {
		if (mNumFailed == 0) {
			return now;
		}
		if (mRetryAfter > 0) {
			return mLastMod + mRetryAfter;
		}
		return mLastMod + Constants.RETRY_FIRST_DELAY * (1000 + mFuzz)
				* (1 << (mNumFailed - 1));
	}

	/**
	 * Returns whether this download (which the download manager hasn't seen
	 * yet) should be started.
	 */
	private boolean isReadyToStart(long now) {
		if (mHasActiveThread) {
			// already running
			return false;
		}

		if (mControl == Impl.CONTROL_PAUSED) {
			// the download is paused, so it's not going to start
			return false;
		}

		// if (0 <= num && num <= settings.getDownloadConcurrentNum()) {
		// if (Impl.isStatusCompleted(mStatus)) {
		// return false;
		// }

		switch (mStatus) {
		case 0: // status hasn't been initialized yet, this is a new
				// download
		case Impl.STATUS_PAUSED_BY_APP: // download is paused by app
		case Impl.STATUS_PENDING: // download is explicit marked as ready to
									// start
		case Impl.STATUS_RUNNING: // download interrupted (process killed
									// etc)
									// while
									// running, without a chance to update
									// the
									// database
			return true;
		case Impl.STATUS_WAITING_FOR_NETWORK:
		case Impl.STATUS_QUEUED_FOR_WIFI:
			return checkCanUseNetwork() == NETWORK_OK;

		case Impl.STATUS_WAITING_TO_RETRY:
			// download was waiting for a delayed restart
			return restartTime(now) <= now;
		case Impl.STATUS_QUEUED_FOR_LIMIT:
			return true;
		}
		return false;
		// }

		// if (num > settings.getDownloadConcurrentNum()) {
		// Utils.D(" info status " + info.mStatus + " num " + num);
		// if (Impl.isStatusRunning(mStatus)) {
		// info.mStatus = Impl.STATUS_QUEUED_FOR_LIMIT;
		// ContentValues values = new ContentValues();
		// values.put(Impl.COLUMN_STATUS, mStatus);
		// mContext.getContentResolver().update(getAllDownloadsUri(),
		// values, null, null);
		// } else {
		// return false;
		// }
		// return false;
		// }

	}

	/**
	 * Returns whether this download has a visible notification after
	 * completion.
	 */
	public boolean hasCompletionNotification() {
		if (!Impl.isStatusCompleted(mStatus)) {
			return false;
		}
		if (mVisibility == Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
			return true;
		}
		return false;
	}

	/**
	 * Returns whether this download is allowed to use the network.
	 * 
	 * @return one of the NETWORK_* constants
	 */
	public int checkCanUseNetwork() {
		Integer networkType = mSystemFacade.getActiveNetworkType();
		if (networkType == null) {
			return NETWORK_NO_CONNECTION;
		}
		if (!isRoamingAllowed() && mSystemFacade.isNetworkRoaming()) {
			return NETWORK_CANNOT_USE_ROAMING;
		}
		return checkIsNetworkTypeAllowed(networkType);
	}

	private boolean isRoamingAllowed() {
		if (mIsPublicApi) {
			return mAllowRoaming;
		} else { // legacy behavior
			return true;
		}
	}

	/**
	 * @return a non-localized string appropriate for logging corresponding to
	 *         one of the NETWORK_* constants.
	 */
	public String getLogMessageForNetworkError(int networkError) {
		switch (networkError) {
		case NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE:
			return "下载文件大小字节超过网络推荐限制.";

		case NETWORK_UNUSABLE_DUE_TO_SIZE:
			return "下载文件大小超过网络限制";

		case NETWORK_NO_CONNECTION:
			return "没有网络连接,请检查网络.";

		case NETWORK_CANNOT_USE_ROAMING:
			return "漫游状态下不能下载.";

		case NETWORK_TYPE_DISALLOWED_BY_REQUESTOR:
			return "下载不能使用当前网络.";

		default:
			return "网络连接未知错误.";
		}
	}

	/**
	 * Check if this download can proceed over the given network type.
	 * 
	 * @param networkType
	 *            a constant from ConnectivityManager.TYPE_*.
	 * @return one of the NETWORK_* constants
	 */
	private int checkIsNetworkTypeAllowed(int networkType) {
		if (mIsPublicApi) {
			int flag = translateNetworkTypeToApiFlag(networkType);
			if ((flag & mAllowedNetworkTypes) == 0) {
				return NETWORK_TYPE_DISALLOWED_BY_REQUESTOR;
			}
			// trinea BEGIN TODO
			if (mStatus == Impl.STATUS_WAITING_FOR_NETWORK
					&& flag != DownloadManager.Request.NETWORK_WIFI) {
				return NETWORK_TYPE_DISALLOWED_BY_REQUESTOR;
			}
			// trinea END TODO
		}
		return checkSizeAllowedForNetwork(networkType);
	}

	/**
	 * Translate a ConnectivityManager.TYPE_* constant to the corresponding
	 * DownloadManager.Request.NETWORK_* bit flag.
	 */
	private int translateNetworkTypeToApiFlag(int networkType) {
		switch (networkType) {
		case ConnectivityManager.TYPE_MOBILE:
			return DownloadManager.Request.NETWORK_MOBILE;

		case ConnectivityManager.TYPE_WIFI:
			return DownloadManager.Request.NETWORK_WIFI;

		default:
			return 0;
		}
	}

	/**
	 * Check if the download's size prohibits it from running over the current
	 * network.
	 * 
	 * @return one of the NETWORK_* constants
	 */
	private int checkSizeAllowedForNetwork(int networkType) {
		if (mTotalBytes <= 0) {
			return NETWORK_OK; // we don't know the size yet
		}
		if (networkType == ConnectivityManager.TYPE_WIFI) {
			return NETWORK_OK; // anything goes over wifi
		}
		Long maxBytesOverMobile = mSystemFacade.getMaxBytesOverMobile();
		if (maxBytesOverMobile != null && mTotalBytes > maxBytesOverMobile) {
			return NETWORK_UNUSABLE_DUE_TO_SIZE;
		}
		if (mBypassRecommendedSizeLimit == 0) {
			Long recommendedMaxBytesOverMobile = mSystemFacade
					.getRecommendedMaxBytesOverMobile();
			if (recommendedMaxBytesOverMobile != null
					&& mTotalBytes > recommendedMaxBytesOverMobile) {
				return NETWORK_RECOMMENDED_UNUSABLE_DUE_TO_SIZE;
			}
		}
		return NETWORK_OK;
	}

	boolean startIfReady(long now) {
		synchronized (this) {
			if (!isReadyToStart(now)) { // 设置了暂停就返回
				return false;
			}

			if (Constants.LOGV) {
				Log.v(Constants.TAG,
						"Service spawning thread to handle download " + mId
								+ " mHasActiveThread=" + mHasActiveThread);
			}

			if (mHasActiveThread) {
				// return;
				throw new IllegalStateException(
						"Multiple threads on same download");

			}

			if (mStatus != Impl.STATUS_RUNNING) {
				mStatus = Impl.STATUS_RUNNING;
				ContentValues values = new ContentValues();
				values.put(Impl.COLUMN_STATUS, mStatus);
				mContext.getContentResolver().update(getAllDownloadsUri(),
						values, null, null);

				if (Constants.LOGV) {
					Log.v(Constants.TAG, "Service startIfReady return " + mId);
				}
				// 比如在这里调用pauseDownload(),刚好暂停在这按下
				// return;
				// 在这里返回后，到下一次进入isReadyToStart()之前设置暂停下载，则不会再执行后边DownloadThread，下载状态会变更为正在下载，不会再开启下载线程
			}

			DownloadThread downloader = new DownloadThread(mContext,
					mSystemFacade, this);
			mHasActiveThread = true;
			mSystemFacade.startThread(downloader);
			return true;
		}
		// if (!isReadyToStart(now)) {
		// return false;
		// }
		//
		// if (Constants.LOGV) {
		// Log.v(Constants.TAG, "Service spawning thread to handle download "
		// + mId);
		// }
		// if (mHasActiveThread) {
		// throw new IllegalStateException("Multiple threads on same download");
		// }
		//
		// if (mStatus != Impl.STATUS_RUNNING) {
		// mStatus = Impl.STATUS_RUNNING;
		// ContentValues values = new ContentValues();
		// values.put(Impl.COLUMN_STATUS, mStatus);
		// mContext.getContentResolver().update(getAllDownloadsUri(), values,
		// null, null);
		// return true;
		// }
		//
		// DownloadThread downloader = new DownloadThread(mContext,
		// mSystemFacade,
		// this);
		// mHasActiveThread = true;
		// mSystemFacade.startThread(downloader);
		// return true;
	}

	public Uri getMyDownloadsUri() {
		return ContentUris.withAppendedId(Impl.CONTENT_URI, mId);
	}

	public Uri getAllDownloadsUri() {
		return ContentUris.withAppendedId(Impl.ALL_DOWNLOADS_CONTENT_URI, mId);
	}

	public Uri getAllDownloadsUri(long myId) {
		return ContentUris.withAppendedId(Impl.ALL_DOWNLOADS_CONTENT_URI, myId);
	}

	/**
	 * Returns the amount of time (as measured from the "now" parameter) at
	 * which a download will be active. 0 = immediately - service should stick
	 * around to handle this download. -1 = never - service can go away without
	 * ever waking up. positive value - service must wake up in the future, as
	 * specified in ms from "now"
	 */
	long nextAction(long now) {
		if (Impl.isStatusCompleted(mStatus)) {
			return -1;
		}
		if (mStatus != Impl.STATUS_WAITING_TO_RETRY) {
			return 0;
		}
		long when = restartTime(now);
		if (when <= now) {
			return 0;
		}
		return when - now;
	}

	// TODO
	void notifyPauseDueToSize(boolean isWifiRequired) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(getAllDownloadsUri());
		intent.setClass(this.mContext, SizeLimitActivity.class);
		// intent.setClassName(SizeLimitActivity.class.getPackage().getName(),
		// SizeLimitActivity.class.getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(EXTRA_IS_WIFI_REQUIRED, isWifiRequired);
		mContext.startActivity(intent);
	}

	// TODO

	/**
	 * 判断存储位置处于内部
	 */
	public boolean isOnCache() {
		return (mDestination == Impl.DESTINATION_CACHE_PARTITION || mDestination == Impl.DESTINATION_CACHE_PARTITION_PURGEABLE);
	}

	/*
	 * 打印下载项目详细信息
	 */
	public void logVerboseInfo() {
		Log.d("", getVerboseInfo());
	}

	/*
	 * 获取下载项目详细信息
	 */
	public String getVerboseInfo() {
		StringBuilder info = new StringBuilder();
		info.append("ID      : " + mId + "\n");
		info.append("URI     : " + ((mUri != null) ? "yes" : "no") + "\n");
		info.append("HINT    : " + mHint + "\n");
		info.append("FILENAME: " + mFileName + "\n");
		info.append("MIMETYPE: " + mMimeType + "\n");
		info.append("DESTINAT: " + mDestination + "\n");
		info.append("VISIBILI: " + mVisibility + "\n");
		info.append("CONTROL : " + mControl + "\n");
		info.append("STATUS  : " + mStatus + "\n");
		info.append("FAILED_C: " + mNumFailed + "\n");
		info.append("RETRY_AF: " + mRetryAfter + "\n");
		info.append("REDIRECT: " + mRedirectCount + "\n");
		info.append("LAST_MOD: " + mLastMod + "\n");
		info.append("PACKAGE : " + mPackage + "\n");
		info.append("CLASS   : " + mClass + "\n");
		info.append("TOTAL   : " + mTotalBytes + "\n");
		info.append("CURRENT : " + mCurrentBytes + "\n");
		info.append("ETAG    : " + mETag + "\n");
		info.append("DELETED : " + mDeleted + "\n");
		return info.toString();
	}

	/**
	 * 当Wi-Fi网络转换到手机网络时，并且还有批量下载任务，提醒用户可以暂停
	 */
	/* package */void notifyNetworkChanged() {

		// TODO 去应用管理页面，提供一键暂停功能
	}
}
