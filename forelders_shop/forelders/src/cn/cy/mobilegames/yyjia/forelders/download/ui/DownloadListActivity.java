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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.activity.BaseActivity;
import cn.cy.mobilegames.yyjia.forelders.activity.SearchActivity;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.util.DialogUtil;
import cn.cy.mobilegames.yyjia.forelders.util.DownloadSelectListener;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.pedant.sweetalert.SweetAlertDialog;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * View showing a list of all downloads the Download Manager knows about.
 */
public class DownloadListActivity extends BaseActivity implements
		OnChildClickListener, OnItemClickListener, DownloadSelectListener,
		OnClickListener, OnCancelListener {
	private static final String LOG_TAG = "DownloadList";
	private ExpandableListView mDateOrderedListView;
	private ListView mSizeOrderedListView;
	private ImageView backBtn, searchImg;
	private TextView navTitle, selectAllBtn;
	private RelativeLayout searchLayout;
	private View mEmptyView;
	private ViewGroup mSelectionMenuView;
	private Button mSelectionDeleteButton;
	private Cursor mDateSortedCursor;
	private DateSortedDownloadAdapter mDateSortedAdapter;
	private Cursor mSizeSortedCursor;
	private DownloadAdapter mSizeSortedAdapter;
	private MyContentObserver mContentObserver = new MyContentObserver();
	private MyDataSetObserver mDataSetObserver = new MyDataSetObserver();
	private int mStatusColumnId;
	private int mIdColumnId;
	private int mLocalUriColumnId;
	private int mMediaTypeColumnId;
	private int mReasonColumndId;
	private boolean mIsSortedBySize = false;
	private final static String ISSORTEDBYSIZE = "isSortedBySize";
	private Set<Long> mSelectedIds = new HashSet<Long>();
	private final static String SELECTION = "selection";
	private Session mSession;
	/**
	 * We keep track of when a dialog is being displayed for a pending download,
	 * because if that download starts running, we want to immediately hide the
	 * dialog.
	 */
	private Long mQueuedDownloadId = null;

	// private AlertDialog mQueuedDialog;
	// private SweetAlertDialog mAlertDialog;

	private class MyContentObserver extends ContentObserver {
		public MyContentObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			handleDownloadsChanged();
		}
	}

	private class MyDataSetObserver extends DataSetObserver {
		@Override
		public void onChanged() {
			// may need to switch to or from the empty view
			chooseListToShow();
			ensureSomeGroupIsExpanded();
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setupViews();
		// mDownloadManager = new DownloadManager(getContentResolver(),
		// getPackageName());
		mSession = Session.get(DownloadListActivity.this);
		mDownloadManager.setAccessAllDownloads(true);
		DownloadManager.Query baseQuery = new DownloadManager.Query()
				.setOnlyIncludeVisibleInDownloadsUi(true);
		mDateSortedCursor = mDownloadManager.query(baseQuery);
		mSizeSortedCursor = mDownloadManager.query(baseQuery.orderBy(
				DownloadManager.COLUMN_TOTAL_SIZE_BYTES,
				DownloadManager.Query.ORDER_DESCENDING));

		// only attach everything to the listbox if we can access the download
		// database. Otherwise,
		// just show it empty
		if (haveCursors()) {
			startManagingCursor(mDateSortedCursor);
			startManagingCursor(mSizeSortedCursor);

			mStatusColumnId = mDateSortedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS);
			mIdColumnId = mDateSortedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_ID);
			mLocalUriColumnId = mDateSortedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_LOCAL_URI);
			mMediaTypeColumnId = mDateSortedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_MEDIA_TYPE);
			mReasonColumndId = mDateSortedCursor
					.getColumnIndexOrThrow(DownloadManager.COLUMN_REASON);
			mDateSortedAdapter = new DateSortedDownloadAdapter(this,
					mDateSortedCursor, this);
			mDateOrderedListView.setAdapter(mDateSortedAdapter);
			mSizeSortedAdapter = new DownloadAdapter(this, mSizeSortedCursor,
					this);
			mSizeOrderedListView.setAdapter(mSizeSortedAdapter);

			ensureSomeGroupIsExpanded();
		}

		chooseListToShow();
	}

	/**
	 * If no group is expanded in the date-sorted list, expand the first one.
	 */
	private void ensureSomeGroupIsExpanded() {
		mDateOrderedListView.post(new Runnable() {
			@Override
			public void run() {
				if (mDateSortedAdapter.getGroupCount() == 0) {
					return;
				}
				for (int group = 0; group < mDateSortedAdapter.getGroupCount(); group++) {
					if (mDateOrderedListView.isGroupExpanded(group)) {
						return;
					}
				}
				mDateOrderedListView.expandGroup(0);
			}
		});
	}

	private void setupViews() {
		setContentView(R.layout.activity_manage_download);
		setTitle(getText(R.string.download_title));

		mDateOrderedListView = (ExpandableListView) findViewById(R.id.date_ordered_list);
		mDateOrderedListView.setOnChildClickListener(this);
		mSizeOrderedListView = (ListView) findViewById(R.id.size_ordered_list);
		mSizeOrderedListView.setOnItemClickListener(this);
		mEmptyView = findViewById(R.id.empty);

		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setText(getResources().getString(R.string.download_manage));
		navTitle.setVisibility(View.VISIBLE);
		selectAllBtn = (TextView) findViewById(R.id.select_all);

		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setOnClickListener(onClick);
		searchLayout.setVisibility(View.VISIBLE);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		searchImg = (ImageView) findViewById(R.id.menu_img);
		backBtn.setOnClickListener(onClick);
		searchImg.setOnClickListener(onClick);
		selectAllBtn.setOnClickListener(onClick);

		mSelectionMenuView = (ViewGroup) findViewById(R.id.selection_menu);
		mSelectionDeleteButton = (Button) findViewById(R.id.selection_delete);
		mSelectionDeleteButton.setOnClickListener(this);

		((Button) findViewById(R.id.deselect_all)).setOnClickListener(this);
	}

	private boolean haveCursors() {
		return mDateSortedCursor != null && mSizeSortedCursor != null;
	}

	OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_img:
				Utils.toOtherClass(DownloadListActivity.this,
						SearchActivity.class);
				break;
			case R.id.back_btn:
				finish();
				break;
			case R.id.select_all:
				selectAll();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		if (haveCursors()) {
			mDateSortedCursor.registerContentObserver(mContentObserver);
			mDateSortedCursor.registerDataSetObserver(mDataSetObserver);
			refresh();
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		if (haveCursors()) {
			mDateSortedCursor.unregisterContentObserver(mContentObserver);
			mDateSortedCursor.unregisterDataSetObserver(mDataSetObserver);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(ISSORTEDBYSIZE, mIsSortedBySize);
		outState.putLongArray(SELECTION, getSelectionAsArray());
	}

	private long[] getSelectionAsArray() {
		long[] selectedIds = new long[mSelectedIds.size()];
		Iterator<Long> iterator = mSelectedIds.iterator();
		for (int i = 0; i < selectedIds.length; i++) {
			selectedIds[i] = iterator.next();
		}
		return selectedIds;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mIsSortedBySize = savedInstanceState.getBoolean(ISSORTEDBYSIZE);
		mSelectedIds.clear();
		for (long selectedId : savedInstanceState.getLongArray(SELECTION)) {
			mSelectedIds.add(selectedId);
		}
		chooseListToShow();
		showOrHideSelectionMenu();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// if (haveCursors()) {
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.menu.download_ui_menu, menu);
	// }
	// return true;
	// }
	//
	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// menu.findItem(R.id.download_menu_sort_by_size).setVisible(
	// !mIsSortedBySize);
	// menu.findItem(R.id.download_menu_sort_by_date).setVisible(
	// mIsSortedBySize);
	// return super.onPrepareOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.download_menu_sort_by_size:
	// mIsSortedBySize = true;
	// chooseListToShow();
	// return true;
	// case R.id.download_menu_sort_by_date:
	// mIsSortedBySize = false;
	// chooseListToShow();
	// return true;
	// }
	// return false;
	// }

	/**
	 * Show the correct ListView and hide the other, or hide both and show the
	 * empty view.
	 */
	private void chooseListToShow() {
		mDateOrderedListView.setVisibility(View.GONE);
		mSizeOrderedListView.setVisibility(View.GONE);

		if (mDateSortedCursor == null || mDateSortedCursor.getCount() == 0) {
			mEmptyView.setVisibility(View.VISIBLE);
		} else {
			mEmptyView.setVisibility(View.GONE);
			activeListView().setVisibility(View.VISIBLE);
			activeListView().invalidateViews(); // ensure checkboxes get updated
		}
	}

	/**
	 * @return the ListView that should currently be visible.
	 */
	private ListView activeListView() {
		if (mIsSortedBySize) {
			return mSizeOrderedListView;
		}
		return mDateOrderedListView;
	}

	// /**
	// * @return an OnClickListener to delete the given downloadId from the
	// * Download Manager
	// */
	// private DialogInterface.OnClickListener getDeleteClickHandler(
	// final long downloadId) {
	// return new DialogInterface.OnClickListener() {
	// @Override
	// public void onClick(DialogInterface dialog, int which) {
	// deleteDownload(downloadId, true);
	// }
	// };
	// }

	/**
	 * @return an OnClickListener to delete the given downloadId from the
	 *         Download Manager
	 */
	private SweetAlertDialog.OnSweetClickListener getDeleteOnClickHandler(
			final long downloadId) {
		return new SweetAlertDialog.OnSweetClickListener() {

			@Override
			public void onClick(SweetAlertDialog tDialog) {
				deleteDownload(downloadId, true);
				tDialog.dismissWithAnimation();
			}
		};
	}

	/**
	 * @return an OnClickListener to pause the given downloadId from the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getPauseClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (mDownloadManager.isStatusRunning(downloadId)) {
					mDownloadManager.pauseDownload(downloadId);
				}
			}
		};
	}

	/**
	 * @return an OnClickListener to resume the given downloadId from the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getResumeClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDownloadManager.resumeDownload(downloadId);
			}
		};
	}

	/**
	 * @return an OnClickListener to restart the given downloadId in the
	 *         Download Manager
	 */
	private DialogInterface.OnClickListener getRestartClickHandler(
			final long downloadId) {
		return new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDownloadManager.restartDownload(downloadId);
			}
		};
	}

	/**
	 * @return an OnClickListener to restart the given downloadId in the
	 *         Download Manager
	 */
	private SweetAlertDialog.OnSweetClickListener getRestartOnClickHandler(
			final long downloadId) {
		return new SweetAlertDialog.OnSweetClickListener() {
			@Override
			public void onClick(SweetAlertDialog tDialog) {
				mDownloadManager.restartDownload(downloadId);
				tDialog.dismissWithAnimation();
			}
		};
	}

	/**
	 * Send an Intent to open the download currently pointed to by the given
	 * cursor.
	 */
	private void openCurrentDownload(Cursor cursor, long id) {
		Uri localUri = null;
		try {
			localUri = Uri.parse(cursor.getString(mLocalUriColumnId));
			getContentResolver().openFileDescriptor(localUri, "r").close();
		} catch (Exception exc) {
			Log.d(LOG_TAG,
					"Failed to open download " + cursor.getLong(mIdColumnId),
					exc);
			// showFailedDialog(cursor.getLong(mIdColumnId),
			// getString(R.string.dialog_file_missing_body));
			return;
		}

		String pkg_name = "";
		try {
			pkg_name = mDownloadManager.queryPackageName(id);
			if (Utils.isAppInstalled(context, pkg_name)) {
				Utils.startAPP(context, pkg_name);
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		if (localUri != null) {
			intent.setDataAndType(localUri,
					cursor.getString(mMediaTypeColumnId));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					| Intent.FLAG_GRANT_READ_URI_PERMISSION);
		} else {
			return;
		}

		try {
			startActivity(intent);
			String packageName = Utils
					.getApkInfo(localUri.getSchemeSpecificPart(), context)
					.get(Constants.KEY_PACKAGE_NAME).toString();
			mDownloadManager.updatePkgName(packageName, id + "");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void handleItemClick(Cursor cursor) {
		long id = cursor.getInt(mIdColumnId);
		switch (cursor.getInt(mStatusColumnId)) {
		case DownloadManager.STATUS_PENDING:
		case DownloadManager.STATUS_RUNNING:
			showRunningDialog(id);
			break;

		case DownloadManager.STATUS_PAUSED:
			if (isPausedForWifi(cursor)) {
				mQueuedDownloadId = id;
				DialogUtil.showUserDefineDialog(context,
						R.string.dialog_title_queued_body,
						R.string.dialog_queued_body, 0,
						R.string.download_keep_queued,
						R.string.download_remove, getDeleteOnClickHandler(id));
				// mQueuedDialog = new AlertDialog.Builder(this)
				// .setTitle(R.string.dialog_title_queued_body)
				// .setMessage(R.string.dialog_queued_body)
				// .setIcon(R.drawable.logo)
				// .setPositiveButton(R.string.keep_queued_download, null)
				// .setNegativeButton(R.string.remove_download,
				// getDeleteClickHandler(id))
				// .setOnCancelListener(this).show();
			} else {
				mDownloadManager.resumeDownload(id);
				// showPausedDialog(id);
			}
			break;

		case DownloadManager.STATUS_SUCCESS:
		case DownloadManager.STATUS_INSTALLED:
			openCurrentDownload(cursor, id);
			break;

		case DownloadManager.STATUS_FAILED:
			String path = mDownloadManager.queryLocalUri(id);
			if (path != null) {
				Utils.deleteFile(path);
				mDownloadManager.restartDownload(id);
			}
			// showFailedDialog(id, getErrorMessage(cursor));
			break;
		}
	}

	/**
	 * @return the appropriate error message for the failed download pointed to
	 *         by cursor
	 */
	private String getErrorMessage(Cursor cursor) {
		switch (cursor.getInt(mReasonColumndId)) {
		case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
			if (isOnExternalStorage(cursor)) {
				return getString(R.string.dialog_file_already_exists);
			} else {
				// the download manager should always find a free filename for
				// cache downloads,
				// so this indicates a strange internal error
				return getUnknownErrorMessage();
			}

		case DownloadManager.ERROR_INSUFFICIENT_SPACE:
			if (isOnExternalStorage(cursor)) {
				return getString(R.string.dialog_insufficient_space_on_external);
			} else {
				return getString(R.string.dialog_insufficient_space_on_cache);
			}

		case DownloadManager.ERROR_DEVICE_NOT_FOUND:
			return getString(R.string.dialog_media_not_found);

		case DownloadManager.ERROR_CANNOT_RESUME:
			return getString(R.string.dialog_cannot_resume);

		default:
			return getUnknownErrorMessage();
		}
	}

	// TODO
	/**
	 * 是否存在SD卡
	 * 
	 * @param cursor
	 * @return
	 */
	private boolean isOnExternalStorage(Cursor cursor) {
		String localUriString = cursor.getString(mLocalUriColumnId);
		if (localUriString == null) {
			return false;
		}
		Uri localUri = Uri.parse(localUriString);
		if (!localUri.getScheme().equals("file")) {
			return false;
		}
		String path = localUri.getPath();
		String externalRoot = Environment.getExternalStorageDirectory()
				.getPath();
		return path.startsWith(externalRoot);
	}

	private String getUnknownErrorMessage() {
		return getString(R.string.dialog_failed_body);
	}

	private void showRunningDialog(long downloadId) {
		if (mDownloadManager.isStatusRunning(downloadId)) {
			mDownloadManager.pauseDownload(downloadId);
		}
	}

	private void showFailedDialog(long downloadId, String dialogBody) {
		DialogUtil.showUserDefineDialog(context,
				R.string.dialog_title_not_available, dialogBody, 0,
				R.string.download_delete, R.string.download_retry,
				getDeleteOnClickHandler(downloadId),
				getRestartOnClickHandler(downloadId));
	}

	// handle a click from the date-sorted list
	@Override
	public boolean onChildClick(ExpandableListView parent, View v,
			int groupPosition, int childPosition, long id) {
		mDateSortedAdapter.moveCursorToChildPosition(groupPosition,
				childPosition);
		handleItemClick(mDateSortedCursor);
		return true;
	}

	// handle a click from the size-sorted list
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		mSizeSortedCursor.moveToPosition(position);
		handleItemClick(mSizeSortedCursor);
	}

	// handle a click on one of the download item checkboxes
	@Override
	public void onDownloadSelectionChanged(long downloadId, boolean isSelected) {
		if (isSelected) {
			mSelectedIds.add(downloadId);
		} else {
			mSelectedIds.remove(downloadId);
		}
		showOrHideSelectionMenu();
	}

	@Override
	public boolean isDownloadSelected(long id) {
		return mSelectedIds.contains(id);
	}

	// public void allSelect

	private void showOrHideSelectionMenu() {
		boolean shouldBeVisible = !mSelectedIds.isEmpty();
		boolean isVisible = mSelectionMenuView.getVisibility() == View.VISIBLE;
		if (shouldBeVisible) {
			updateSelectionMenu();
			if (!isVisible) {
				// show menu
				mSelectionMenuView.setVisibility(View.VISIBLE);
				mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(
						this, R.anim.footer_appear));
				searchImg.setVisibility(View.GONE);
				selectAllBtn.setVisibility(View.VISIBLE);
			}
		} else if (!shouldBeVisible && isVisible) {
			// hide menu
			mSelectionMenuView.setVisibility(View.GONE);
			mSelectionMenuView.startAnimation(AnimationUtils.loadAnimation(
					this, R.anim.footer_disappear));
			searchImg.setVisibility(View.VISIBLE);
			selectAllBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * Set up the contents of the selection menu based on the current selection.
	 */
	private void updateSelectionMenu() {
		int deleteButtonStringId = R.string.download_delete;
		if (mSelectedIds.size() == 1) {
			Cursor cursor = mDownloadManager.query(new DownloadManager.Query()
					.setFilterById(mSelectedIds.iterator().next()));
			try {
				cursor.moveToFirst();
				switch (cursor.getInt(mStatusColumnId)) {
				case DownloadManager.STATUS_FAILED:
					deleteButtonStringId = R.string.download_delete;
					break;

				case DownloadManager.STATUS_PENDING:
					// deleteButtonStringId = R.string.remove_download;
					deleteButtonStringId = R.string.download_delete;
					break;

				case DownloadManager.STATUS_PAUSED:
				case DownloadManager.STATUS_RUNNING:
					// deleteButtonStringId = R.string.cancel_running_download;
					deleteButtonStringId = R.string.download_delete;
					break;
				}
			} finally {
				cursor.close();
			}
		}
		mSelectionDeleteButton.setText(deleteButtonStringId);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.selection_delete:
			DialogUtil.showDefaultDialog(context, Constants.KINDER_NOTICE,
					"您确定要删除记录及安装包", R.drawable.app_icon,
					new SweetAlertDialog.OnSweetClickListener() {

						@Override
						public void onClick(SweetAlertDialog tDialog) {
							for (Long downloadId : mSelectedIds) {
								deleteDownload(downloadId, true);
								mSession.updateDownloading(Session.CURSOR_CHANGED);
							}
							clearSelection();
							tDialog.dismissWithAnimation();
						}
					});
			break;
		case R.id.deselect_all:
			clearSelection();
			break;

		}
	}

	/**
	 * Requery the database and update the UI.
	 */
	@SuppressWarnings("deprecation")
	private void refresh() {
		mDateSortedCursor.requery();
		mSizeSortedCursor.requery();
		// Adapters get notification of changes and update automatically
	}

	private void clearSelection() {
		mSelectedIds.clear();
		showOrHideSelectionMenu();
	}

	/**
	 * Delete a download from the Download Manager.
	 */
	private void deleteDownload(long downloadId, boolean deleteFile) {
		if (moveToDownload(downloadId)) {
			// int status = mDateSortedCursor.getInt(mStatusColumnId);
			// boolean isComplete = status == DownloadManager.STATUS_SUCCESS
			// || status == DownloadManager.STATUS_FAILED;
			String localUri = mDateSortedCursor.getString(mLocalUriColumnId);
			mDownloadManager.remove(downloadId);
			if (localUri != null) {
				String path = Uri.parse(localUri).getPath();
				if (deleteFile) {
					Utils.deleteFile(path);
				}
			}
		}
	}

	/**
	 * Called when there's a change to the downloads database.
	 */
	void handleDownloadsChanged() {
		checkSelectionForDeletedEntries();

		if (mQueuedDownloadId != null && moveToDownload(mQueuedDownloadId)) {
			if (mDateSortedCursor.getInt(mStatusColumnId) != DownloadManager.STATUS_PAUSED
					|| !isPausedForWifi(mDateSortedCursor)) {
				// TODO
				// mQueuedDialog.cancel();
			}
		}
	}

	private boolean isPausedForWifi(Cursor cursor) {
		return cursor.getInt(mReasonColumndId) == DownloadManager.PAUSED_QUEUED_FOR_WIFI;
	}

	/**
	 * Check if any of the selected downloads have been deleted from the
	 * downloads database, and remove such downloads from the selection.
	 */
	private void checkSelectionForDeletedEntries() {
		// gather all existing IDs...
		Set<Long> allIds = new HashSet<Long>();
		for (mDateSortedCursor.moveToFirst(); !mDateSortedCursor.isAfterLast(); mDateSortedCursor
				.moveToNext()) {
			allIds.add(mDateSortedCursor.getLong(mIdColumnId));
		}

		// ...and check if any selected IDs are now missing
		for (Iterator<Long> iterator = mSelectedIds.iterator(); iterator
				.hasNext();) {
			if (!allIds.contains(iterator.next())) {
				iterator.remove();
			}
		}
	}

	private void selectAll() {
		// gather all existing IDs...
		Set<Long> allIds = new HashSet<Long>();
		for (mDateSortedCursor.moveToFirst(); !mDateSortedCursor.isAfterLast(); mDateSortedCursor
				.moveToNext()) {
			allIds.add(mDateSortedCursor.getLong(mIdColumnId));
		}
		for (Long ids : allIds) {
			if (!mSelectedIds.contains(ids)) {
				mSelectedIds.add(ids);
				isDownloadSelected(ids);
			}
		}
		refresh();
		showOrHideSelectionMenu();
	}

	/**
	 * Move {@link #mDateSortedCursor} to the download with the given ID.
	 * 
	 * @return true if the specified download ID was found; false otherwise
	 */
	private boolean moveToDownload(long downloadId) {
		for (mDateSortedCursor.moveToFirst(); !mDateSortedCursor.isAfterLast(); mDateSortedCursor
				.moveToNext()) {
			if (mDateSortedCursor.getLong(mIdColumnId) == downloadId) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when a dialog for a pending download is canceled.
	 */
	@Override
	public void onCancel(DialogInterface dialog) {
		mQueuedDownloadId = null;
		// mQueuedDialog = null;
	}

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int method, int statusCode) {

	}

}
