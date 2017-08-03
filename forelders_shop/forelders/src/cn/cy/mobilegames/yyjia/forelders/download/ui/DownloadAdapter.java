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

package cn.cy.mobilegames.yyjia.forelders.download.ui;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.util.DownloadSelectListener;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ResponseCacheManager;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.MyCheckBox;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * List adapter for Cursors returned by {@link DownloadManager}.
 */
public class DownloadAdapter extends CursorAdapter {
	private DownloadManager manager;
	private Context mContext;
	private Cursor mCursor;
	MyCheckBox checkBox;
	private DownloadSelectListener mDownloadSelectionListener;
	private Resources mResources;
	private DateFormat mDateFormat;
	private DateFormat mTimeFormat;

	final private int mTitleColumnId;
	final private int mStatusColumnId;
	final private int mReasonColumnId;
	final private int mTotalBytesColumnId;
	final private int mCurrentBytesColumnId;
	final private int mMediaTypeColumnId;
	final private int mDateColumnId;
	final private int mIdColumnId;
	final private int mLocalUrl;
	final private int mUrl;
	private ResponseCacheManager cacheManager;
	private String cacheKey = "";
	private ImageLoaderUtil imageLoaderUtil;

	@SuppressWarnings("deprecation")
	public DownloadAdapter(Context context, Cursor cursor,
			DownloadSelectListener selectionListener) {
		super(context, cursor);
		mContext = context;
		mCursor = cursor;
		imageLoaderUtil = ImageLoaderUtil.getInstance(mContext);
		manager = DownloadManager.getInstance(mContext);
		cacheManager = ResponseCacheManager.getInstance();
		mResources = mContext.getResources();
		mDownloadSelectionListener = selectionListener;
		mDateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
		mTimeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

		mIdColumnId = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
		mTitleColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_TITLE);
		mStatusColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
		mReasonColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
		mTotalBytesColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
		mCurrentBytesColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
		mMediaTypeColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
		mLocalUrl = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
		mUrl = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_URI);
		mDateColumnId = cursor
				.getColumnIndexOrThrow(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP);
	}

	public View newView() {
		DownloadItem view = (DownloadItem) LayoutInflater.from(mContext)
				.inflate(R.layout.manage_download_item, null);
		view.setSelectListener(mDownloadSelectionListener);
		checkBox = (MyCheckBox) view.findViewById(R.id.download_checkbox);
		checkBox.setSelectListener(mDownloadSelectionListener);
		return view;
	}

	public void bindView(View convertView) {
		if (!(convertView instanceof DownloadItem)) {
			return;
		}

		long downloadId = mCursor.getLong(mIdColumnId);

		((DownloadItem) convertView).setDownloadId(downloadId);

		checkBox = (MyCheckBox) convertView
				.findViewById(R.id.download_checkbox);
		checkBox.setDownloadId(downloadId);

		// Retrieve the icon for this download
		try {
			retrieveAndSetIcon(convertView, downloadId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		checkBox.setChecked(mDownloadSelectionListener
				.isDownloadSelected(downloadId));

		String title = mCursor.getString(mTitleColumnId);
		long totalBytes = mCursor.getLong(mTotalBytesColumnId);
		long currentBytes = mCursor.getLong(mCurrentBytesColumnId);
		int status = mCursor.getInt(mStatusColumnId);

		if (title.length() == 0) {
			title = mResources.getString(R.string.download_missing_title);
		}
		setTextForView(convertView, R.id.download_title, title);

		int progress = getProgressValue(totalBytes, currentBytes);

		boolean indeterminate = status == DownloadManager.STATUS_PENDING;
		ProgressBar progressBar = (ProgressBar) convertView
				.findViewById(R.id.download_progress);
		progressBar.setIndeterminate(false);
		if (!indeterminate) {
			progressBar.setProgress(progress);
		}
		if (manager.isStatusCompleted(status)) {
			progressBar.setVisibility(View.GONE);
		} else {
			progressBar.setVisibility(View.VISIBLE);
		}

		setTextForView(convertView, R.id.size_text,
				getSizeText(status, currentBytes, totalBytes));
		setTextForView(convertView, R.id.download_percent,
				getPercent(status, currentBytes, totalBytes));
		setTextForView(convertView, R.id.status_text,
				mResources.getString(getStatusStringId(status)));
		setTextForView(convertView, R.id.soft_downbtn,
				mResources.getString(getStatusId(status, downloadId)), status,
				downloadId);
		// setTextForView(convertView, R.id.last_modified_date,
		// getPercent(status, currentBytes, totalBytes));

	}

	private String getDateString() {
		Date date = new Date(mCursor.getLong(mDateColumnId));
		if (date.before(getStartOfToday())) {
			return mDateFormat.format(date);
		} else {
			return mTimeFormat.format(date);
		}
	}

	private Date getStartOfToday() {
		Calendar today = new GregorianCalendar();
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return today.getTime();
	}

	public int getProgressValue(long totalBytes, long currentBytes) {
		if (totalBytes == -1) {
			return 0;
		}
		if (currentBytes == 0) {
			return 0;
		}
		return (int) (currentBytes * 100 / totalBytes);
	}

	private String getSizeText(int status, long current, long totalBytes) {
		String sizeText = "";
		switch (status) {
		case DownloadManager.STATUS_FAILED:
		case DownloadManager.STATUS_INSTALLED:
		case DownloadManager.STATUS_SUCCESS:
			if (totalBytes >= 0) {
				sizeText = Formatter.formatFileSize(mContext, totalBytes);
			}
			return sizeText;
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
		case DownloadManager.STATUS_WAITED:
		case DownloadManager.STATUS_PAUSED:
			return Utils.getAppSize(current) + "/"
					+ Utils.getAppSize(totalBytes);
		}
		throw new IllegalStateException("Unknown status: "
				+ mCursor.getInt(mStatusColumnId));
	}

	private int getStatusStringId(int status) {
		switch (status) {
		case DownloadManager.STATUS_FAILED:
			return R.string.download_error;

		case DownloadManager.STATUS_SUCCESS:
			return R.string.download_install;
		case DownloadManager.STATUS_INSTALLED:
			return R.string.download_open;

		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			return R.string.download_running;
		case DownloadManager.STATUS_WAITED:
			return R.string.download_queued;

		case DownloadManager.STATUS_PAUSED:
			if (mCursor.getInt(mReasonColumnId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI) {
				return R.string.download_queued;
			} else {
				return R.string.download_paused;
			}
		}
		throw new IllegalStateException("Unknown status: "
				+ mCursor.getInt(mStatusColumnId));
	}

	private int getStatusId(int status, long downId) {
		switch (status) {
		case DownloadManager.STATUS_FAILED:
			return R.string.download_error;
		case DownloadManager.STATUS_SUCCESS:
			return R.string.download_install;
		case DownloadManager.STATUS_INSTALLED:
			return R.string.download_open;
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			return R.string.download_pause;
		case DownloadManager.STATUS_PAUSED:
			return R.string.download_resume;
		case DownloadManager.STATUS_WAITED:
			return R.string.download_queue;
		}
		throw new IllegalStateException("Unknown status: "
				+ mCursor.getInt(mStatusColumnId));
	}

	private String getPercent(int status, long current, long total) {
		switch (status) {
		case DownloadManager.STATUS_FAILED:
		case DownloadManager.STATUS_SUCCESS:
		case DownloadManager.STATUS_INSTALLED:
		case DownloadManager.STATUS_PENDING:
			return "";
		case DownloadManager.STATUS_RUNNING:
		case DownloadManager.STATUS_PAUSED:
		case DownloadManager.STATUS_WAITED:
			return Utils.getNotiPercent(current, total);
		}
		throw new IllegalStateException("Unknown status: "
				+ mCursor.getInt(mStatusColumnId));
	}

	private void retrieveAndSetIcon(View convertView, long downId) {
		ImageView iconView = (ImageView) convertView
				.findViewById(R.id.download_icon);
		String logoUri = manager.queryLogo(downId);
		if (TextUtils.isEmpty(logoUri)) {
			iconView.setImageResource(R.drawable.img_default);
		} else {
			imageLoaderUtil.setImageNetResource(iconView, logoUri,
					R.drawable.img_default);
		}
		iconView.setVisibility(View.VISIBLE);
		return;
	}

	private void setTextForView(View parent, int textViewId, String text) {
		TextView view = (TextView) parent.findViewById(textViewId);
		view.setText(text);
	}

	private void setTextForView(View parent, int textViewId, String text,
			int status, long downloadId) {
		TextView view = (TextView) parent.findViewById(textViewId);
		// view.setText(text);
		switch (status) {
		case DownloadManager.STATUS_FAILED:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_failure,
					R.drawable.status_failured);
			break;
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_run,
					R.drawable.status_runed);
			break;
		case DownloadManager.STATUS_WAITED:
		case DownloadManager.STATUS_PAUSED:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_wait,
					R.drawable.status_waited);
			break;
		case DownloadManager.STATUS_SUCCESS:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_install,
					R.drawable.status_installed);
			break;
		case DownloadManager.STATUS_INSTALLED:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_open,
					R.drawable.status_opened);
			break;
		default:
			Utils.setTvColorAndBg(mContext, view, text, R.color.status_run,
					R.drawable.status_runed);
			break;
		}

	}

	// CursorAdapter overrides

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		return newView();
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		bindView(view);
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		super.registerDataSetObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		super.unregisterDataSetObserver(observer);
	}

}
