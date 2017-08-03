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

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;

/**
 * Receives system broadcasts (boot, network connectivity)
 */
public class DownloadReceiver extends BroadcastReceiver {
	SystemFacade mSystemFacade = null;

	@Override
	public void onReceive(Context context, Intent intent) {
		if (mSystemFacade == null) {
			mSystemFacade = new RealSystemFacade(context);
		}

		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			startService(context);
		} else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			NetworkInfo info = (NetworkInfo) intent
					.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
			// trinea BEGIN TODO
			/**
			 * modified by trinea@trinea.cn @2013/04/01, resume download only
			 * when network type is wifi
			 */
			if (info != null && info.isConnected()
					&& info.getType() == ConnectivityManager.TYPE_WIFI) {
				// trinea END TODO
				startService(context);
			}
		} else if (action.equals(Constants.ACTION_RETRY)) {
			startService(context);
		} else if (action.equals(Constants.ACTION_OPEN)
				|| action.equals(Constants.ACTION_LIST)
				|| action.equals(Constants.ACTION_HIDE)) {
			handleNotificationBroadcast(context, intent);
		}
	}

	/**
	 * Handle any broadcast related to a system notification.
	 */
	private void handleNotificationBroadcast(Context context, Intent intent) {
		Uri uri = intent.getData();
		String action = intent.getAction();
		if (Constants.LOGVV) {
			if (action.equals(Constants.ACTION_OPEN)) {
				Log.v(Constants.TAG, "Receiver open for " + uri);
			} else if (action.equals(Constants.ACTION_LIST)) {
				Log.v(Constants.TAG, "Receiver list for " + uri);
			} else { // ACTION_HIDE
				Log.v(Constants.TAG, "Receiver hide for " + uri);
			}
		}

		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor == null) {
			return;
		}
		try {
			if (!cursor.moveToFirst()) {
				return;
			}

			if (action.equals(Constants.ACTION_OPEN)) {
				int status = intent.getIntExtra(
						DownloadManager.Impl.COLUMN_STATUS,
						DownloadManager.Impl.STATUS_UNKNOWN_ERROR);
				if (status == DownloadManager.Impl.STATUS_SUCCESS) {
					// download success
					openDownload(context, cursor);
				} else {
					// download failed
					hideNotification(context, uri, cursor);
				}
			} else if (action.equals(Constants.ACTION_LIST)) {
				sendNotificationClickedIntent(intent, cursor);
			} else { // ACTION_HIDE
				hideNotification(context, uri, cursor);
			}
		} finally {
			cursor.close();
		}
	}

	/**
	 * Hide a system notification for a download.
	 * 
	 * @param uri
	 *            URI to update the download
	 * @param cursor
	 *            Cursor for reading the download's fields
	 */
	private void hideNotification(Context context, Uri uri, Cursor cursor) {
		mSystemFacade.cancelNotification(ContentUris.parseId(uri));

		int statusColumn = cursor.getColumnIndexOrThrow(Impl.COLUMN_STATUS);
		int status = cursor.getInt(statusColumn);
		int visibilityColumn = cursor
				.getColumnIndexOrThrow(Impl.COLUMN_VISIBILITY);
		int visibility = cursor.getInt(visibilityColumn);
		if (Impl.isStatusCompleted(status)
				&& visibility == Impl.VISIBILITY_VISIBLE_NOTIFY_COMPLETED) {
			ContentValues values = new ContentValues();
			values.put(Impl.COLUMN_VISIBILITY, Impl.VISIBILITY_VISIBLE);
			context.getContentResolver().update(uri, values, null, null);
		}
	}

	/**
	 * Open the download that cursor is currently pointing to, since it's
	 * completed notification has been clicked.
	 */
	private void openDownload(Context context, Cursor cursor) {
		String filename = cursor.getString(cursor
				.getColumnIndexOrThrow(Impl._DATA));
		String mimetype = cursor.getString(cursor
				.getColumnIndexOrThrow(Impl.COLUMN_MIME_TYPE));
		int destination = cursor
				.getInt(cursor
						.getColumnIndexOrThrow(DownloadManager.Impl.COLUMN_DESTINATION));
		// TODO
		// if (destination == Constants.DESTINATION_EXTERNAL
		// && !Utils.isSdcardWritable()) {
		// // SDCard 没有挂载，无法进行安装操作
		// Utils.makeEventToast(context,
		// context.getString(R.string.warning_sdcard_unmounted), false);
		// return;
		// }

		Uri path = Uri.parse(filename);
		// If there is no scheme, then it must be a file
		if (path.getScheme() == null) {
			path = Uri.fromFile(new File(filename));
		}

		Intent activityIntent = new Intent(Intent.ACTION_VIEW);
		activityIntent.setDataAndType(path, mimetype);
		activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		try {
			context.startActivity(activityIntent);
		} catch (ActivityNotFoundException ex) {
			Log.d(Constants.TAG, "no activity for " + mimetype, ex);
		}
	}

	/**
	 * Notify the owner of a running download that its notification was clicked.
	 * 
	 * @param intent
	 *            the broadcast intent sent by the notification manager
	 * @param cursor
	 *            Cursor for reading the download's fields
	 */
	private void sendNotificationClickedIntent(Intent intent, Cursor cursor) {
		String pckg = cursor.getString(cursor
				.getColumnIndexOrThrow(Impl.COLUMN_NOTIFICATION_PACKAGE));
		if (pckg == null) {
			return;
		}

		String clazz = cursor.getString(cursor
				.getColumnIndexOrThrow(Impl.COLUMN_NOTIFICATION_CLASS));
		boolean isPublicApi = cursor.getInt(cursor
				.getColumnIndex(Impl.COLUMN_IS_PUBLIC_API)) != 0;

		Intent appIntent = null;
		if (isPublicApi) {
			appIntent = new Intent(DownloadManager.ACTION_NOTIFICATION_CLICKED);
			appIntent.setPackage(pckg);
		} else { // legacy behavior
			if (clazz == null) {
				return;
			}
			appIntent = new Intent(Impl.ACTION_NOTIFICATION_CLICKED);
			appIntent.setClassName(pckg, clazz);
			if (intent.getBooleanExtra("multiple", true)) {
				appIntent.setData(Impl.CONTENT_URI);
			} else {
				long downloadId = cursor.getLong(cursor
						.getColumnIndexOrThrow(BaseColumns._ID));
				appIntent.setData(ContentUris.withAppendedId(Impl.CONTENT_URI,
						downloadId));
			}
		}

		mSystemFacade.sendBroadcast(appIntent);
	}

	private void startService(Context context) {
		context.startService(new Intent(context, DownloadService.class));
	}

}
