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

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.provider.BaseColumns;
import android.util.Log;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;

/**
 * Performs the background downloads requested by applications that use the
 * Downloads provider.
 */
public class DownloadService extends Service {
	/** Observer to get notified when the content observer's data changes */
	private DownloadManagerContentObserver mObserver;

	/** Class to handle Notification Manager updates */
	private DownloadNotification mNotifier;

	private int mThreadNum;
	/**
	 * The Service's view of the list of downloads, mapping download IDs to the
	 * corresponding info object. This is kept independently from the content
	 * provider, and the Service only initiates downloads based on this data, so
	 * that it can deal with situation where the data in the content provider
	 * changes or disappears.
	 */
	private Map<Long, DownloadInfo> mDownloads = new HashMap<Long, DownloadInfo>();

	/**
	 * The thread that updates the internal download list from the content
	 * provider.
	 */
	private UpdateThread mUpdateThread;

	/**
	 * Whether the internal download list should be updated from the content
	 * provider.
	 */
	private boolean mPendingUpdate;

	private SystemFacade mSystemFacade;

	/**
	 * Receives notifications when the data in the content provider changes
	 */
	private class DownloadManagerContentObserver extends ContentObserver {

		public DownloadManagerContentObserver() {
			super(new Handler());
		}

		/**
		 * Receives notification when the data in the observed content provider
		 * changes.
		 */
		@Override
		public void onChange(final boolean selfChange) {
			if (Constants.LOGVV) {
				Log.v(Constants.TAG,
						"Service ContentObserver received notification");
			}
			updateFromProvider();
		}

	}

	/**
	 * Returns an IBinder instance when someone wants to connect to this
	 * service. Binding to this service is not allowed.
	 * 
	 * @throws UnsupportedOperationException
	 */
	@Override
	public IBinder onBind(Intent i) {
		throw new UnsupportedOperationException(
				"Cannot bind to Download Manager Service");
	}

	/**
	 * Initializes the service when it is first created
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		if (Constants.LOGVV) {
			Log.v(Constants.TAG, "Service onCreate");
		}

		if (mSystemFacade == null) {
			mSystemFacade = new RealSystemFacade(this);
		}

		mObserver = new DownloadManagerContentObserver();
		getContentResolver().registerContentObserver(
				Impl.ALL_DOWNLOADS_CONTENT_URI, true, mObserver);

		mNotifier = new DownloadNotification(this, mSystemFacade);
		mSystemFacade.cancelAllNotifications();
		updateFromProvider();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		int returnValue = super.onStartCommand(intent, flags, startId);
		if (Constants.LOGVV) {
			Log.v(Constants.TAG, "Service onStart");
		}
		updateFromProvider();
		return returnValue;
	}

	/**
	 * Cleans up when the service is destroyed
	 */
	@Override
	public void onDestroy() {
		getContentResolver().unregisterContentObserver(mObserver);
		if (Constants.LOGVV) {
			Log.v(Constants.TAG, "Service onDestroy");
		}
		super.onDestroy();
	}

	/**
	 * Parses data from the content provider into private array
	 */
	private void updateFromProvider() {
		synchronized (this) {
			mPendingUpdate = true;
			if (mUpdateThread == null) {
				mUpdateThread = new UpdateThread();
				mSystemFacade.startThread(mUpdateThread);
			}
		}
	}

	private class UpdateThread extends Thread {
		// private int mThreadNum = 0;

		public UpdateThread() {
			super("Download Service");
		}

		@Override
		public void run() {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

			trimDatabase();
			removeSpuriousFiles();

			boolean keepService = false;
			// for each update from the database, remember which download is
			// supposed to get restarted soonest in the future
			long wakeUp = Long.MAX_VALUE;
			// int num = queryDownloadingTask();
			for (;;) {
				synchronized (DownloadService.this) {
					if (mUpdateThread != this) {
						throw new IllegalStateException(
								"multiple UpdateThreads in DownloadService");
					}
					if (!mPendingUpdate) {
						mUpdateThread = null;
						if (!keepService) {
							stopSelf();
						}
						if (wakeUp != Long.MAX_VALUE) {
							scheduleAlarm(wakeUp);
						}
						return;
					}
					mPendingUpdate = false;
				}

				long now = mSystemFacade.currentTimeMillis();
				keepService = false;
				wakeUp = Long.MAX_VALUE;
				Set<Long> idsNoLongerInDatabase = new HashSet<Long>(
						mDownloads.keySet());
				Cursor cursor = getContentResolver().query(
						Impl.ALL_DOWNLOADS_CONTENT_URI, null, null, null, null);
				if (cursor == null) {
					continue;
				}

				try {
					DownloadInfo.Reader reader = new DownloadInfo.Reader(
							getContentResolver(), cursor);
					int idColumn = cursor.getColumnIndexOrThrow(BaseColumns._ID);

					for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
							.moveToNext()) {

						long id = cursor.getLong(idColumn);
						idsNoLongerInDatabase.remove(id);
						DownloadInfo info = mDownloads.get(id);

						if (info != null) {
							// if (Impl.isStatusInformational(info.mStatus)) {
							updateDownload(reader, info, now);
							// }
						} else {
							info = insertDownload(reader, now);
						}

						if (info.hasCompletionNotification()) {
							keepService = true;
						}
						long next = info.nextAction(now);
						if (next == 0) {
							keepService = true;
						} else if (next > 0 && next < wakeUp) {
							wakeUp = next;
						}
					}

				} finally {
					cursor.close();
				}

				for (Long id : idsNoLongerInDatabase) {
					deleteDownload(id);
				}

				for (DownloadInfo info : mDownloads.values()) {
					if (info.mDeleted) {
						keepService = true;
						break;
					}
				}

				mNotifier.updateNotification(mDownloads.values());

				// look for all rows with deleted flag set and delete the rows
				// from the database
				// permanently
				for (DownloadInfo info : mDownloads.values()) {
					if (info.mDeleted) {
						Helpers.deleteFile(getContentResolver(), info.mId,
								info.mFileName, info.mMimeType);
					}
				}

			}
		}

		private void scheduleAlarm(long wakeUp) {
			AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			if (alarms == null) {
				Log.e(Constants.TAG, "couldn't get alarm manager");
				return;
			}

			if (Constants.LOGV) {
				Log.v(Constants.TAG, "scheduling retry in " + wakeUp + "ms");
			}

			Intent intent = new Intent(Constants.ACTION_RETRY);
			intent.setClassName(getPackageName(),
					DownloadReceiver.class.getName());
			alarms.set(AlarmManager.RTC_WAKEUP,
					mSystemFacade.currentTimeMillis() + wakeUp, PendingIntent
							.getBroadcast(DownloadService.this, 0, intent,
									PendingIntent.FLAG_ONE_SHOT));
		}
	}

	/**
	 * Removes files that may have been left behind in the cache directory
	 */
	private void removeSpuriousFiles() {
		File[] files = Environment.getDownloadCacheDirectory().listFiles();
		if (files == null) {
			// The cache folder doesn't appear to exist (this is likely the case
			// when running the simulator).
			return;
		}
		HashSet<String> fileSet = new HashSet<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].getName().equals(Constants.KNOWN_SPURIOUS_FILENAME)) {
				continue;
			}
			if (files[i].getName().equalsIgnoreCase(
					Constants.RECOVERY_DIRECTORY)) {
				continue;
			}
			fileSet.add(files[i].getPath());
		}

		Cursor cursor = getContentResolver().query(
				Impl.ALL_DOWNLOADS_CONTENT_URI, new String[] { Impl._DATA },
				null, null, null);
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					fileSet.remove(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		Iterator<String> iterator = fileSet.iterator();
		while (iterator.hasNext()) {
			String filename = iterator.next();
			if (Constants.LOGV) {
				Log.v(Constants.TAG, "deleting spurious file " + filename);
			}
			new File(filename).delete();
		}
	}

	/**
	 * Drops old rows from the database to prevent it from growing too large
	 */
	private void trimDatabase() {
		Cursor cursor = getContentResolver().query(
				Impl.ALL_DOWNLOADS_CONTENT_URI, new String[] { BaseColumns._ID },
				Impl.COLUMN_STATUS + " >= '200'", null,
				Impl.COLUMN_LAST_MODIFICATION);
		if (cursor == null) {
			// This isn't good - if we can't do basic queries in our database,
			// nothing's gonna work
			Log.e(Constants.TAG, "null cursor in trimDatabase");
			return;
		}
		if (cursor.moveToFirst()) {
			int numDelete = cursor.getCount() - Constants.MAX_DOWNLOADS;
			int columnId = cursor.getColumnIndexOrThrow(BaseColumns._ID);
			while (numDelete > 0) {
				Uri downloadUri = ContentUris.withAppendedId(
						Impl.ALL_DOWNLOADS_CONTENT_URI,
						cursor.getLong(columnId));
				getContentResolver().delete(downloadUri, null, null);
				if (!cursor.moveToNext()) {
					break;
				}
				numDelete--;
			}
		}
		cursor.close();
	}

	/**
	 * Keeps a local copy of the info about a download, and initiates the
	 * download if appropriate.
	 */
	private DownloadInfo insertDownload(DownloadInfo.Reader reader, long now) {
		DownloadInfo info = reader.newDownloadInfo(this, mSystemFacade);
		mDownloads.put(info.mId, info);

		// if (Constants.LOGVV) {
		// info.logVerboseInfo();
		// }
		if (info.startIfReady(now)) {
			mThreadNum = mThreadNum + 1;
		}
		// if ((mThreadNum < 3) && (info.startIfReady(now))) {
		// mThreadNum = (1 + mThreadNum);
		// }
		return info;
	}

	/**
	 * Updates the local copy of the info about a download.
	 */
	private void updateDownload(DownloadInfo.Reader reader, DownloadInfo info,
			long now) {
		int oldVisibility = info.mVisibility;
		int oldStatus = info.mStatus;

		reader.updateFromDatabase(info);

		boolean lostVisibility = oldVisibility == Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
				&& info.mVisibility != Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
				&& Impl.isStatusCompleted(info.mStatus);
		boolean justCompleted = !Impl.isStatusCompleted(oldStatus)
				&& Impl.isStatusCompleted(info.mStatus);
		boolean unComplete =
		// Impl.isStatusCompleted(oldStatus)&&
		!Impl.isStatusCompleted(info.mStatus);
		boolean isRunning =
		// !Impl.isStatusRunning(oldStatus) &&
		Impl.isStatusRunning(info.mStatus);

		if (lostVisibility || justCompleted) {
			mSystemFacade.cancelNotification(info.mId);
		}

		info.startIfReady(now);

		// if ((lostVisibility || justCompleted) && (mThreadNum > 0)) {
		// mThreadNum = mThreadNum - 1;
		// }
		//
		// if ((mThreadNum >= 3) || (!info.startIfReady(now))) {
		// mThreadNum = mThreadNum + 1;
		// }

	}

	// private void updateDownload(DownloadInfo.Reader reader, DownloadInfo
	// info,
	// long now) {
	// int i = 1;
	// int oldVisible = info.mVisibility;
	// int oldStatus = info.mStatus;
	// reader.updateFromDatabase(info);
	// int lostVisibility;
	// int unCompleted;
	// if ((oldVisible == Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
	// && (info.mVisibility != Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
	// && (DownloadManager.Impl.isStatusCompleted(info.mStatus))) {
	// lostVisibility = i;
	// if ((DownloadManager.Impl.isStatusCompleted(oldStatus))
	// || (!DownloadManager.Impl.isStatusCompleted(info.mStatus)))
	// break label134;
	// unCompleted = i;
	// label72: if ((!DownloadManager.Impl.isStatusRunning(oldStatus))
	// || (DownloadManager.Impl.isStatusRunning(info.mStatus)))
	// break label140;
	// label90: if (((lostVisibility != 0) || (unCompleted == 0))
	// || ((i != 0) && (this.mThreadNum > 0)))
	// this.mThreadNum = (-1 + this.mThreadNum);
	// if (unCompleted == 0)
	// break label146;
	// }
	// label134: label140: label146: do {
	// return;
	// lostVisibility = 0;
	// break;
	// unCompleted = 0;
	// break label72;
	// i = 0;
	// break label90;
	// } while ((this.mThreadNum >= 3) ||
	// (!info.startIfReady(now))){this.mThreadNum = (1 + this.mThreadNum);}
	// }

	/**
	 * Removes the local copy of the info about a download.
	 */
	private void deleteDownload(long id) {
		DownloadInfo info = mDownloads.get(id);
		if (info.mStatus == Impl.STATUS_RUNNING) {
			info.mStatus = Impl.STATUS_CANCELED;
			if (mThreadNum > 0) {
				mThreadNum = (-1 + mThreadNum);
			}
		}
		if (info.mDestination != Impl.DESTINATION_EXTERNAL
				&& info.mFileName != null) {
			new File(info.mFileName).delete();
		}
		mSystemFacade.cancelNotification(info.mId);
		mDownloads.remove(info.mId);
	}
}
