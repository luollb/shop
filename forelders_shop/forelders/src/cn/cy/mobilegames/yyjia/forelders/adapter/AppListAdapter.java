/*
 * Copyright (C) 2010 mAPPn.Inc
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
package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Checkable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Request;
import cn.cy.mobilegames.yyjia.forelders.model.DownloadInfo;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * Client ListView associating adapter<br>
 * It has lazyload feature, which load data on-demand.
 * 
 * @author andrew.wang
 * 
 */
public class AppListAdapter extends BaseAdapter implements Observer,
		ApiRequestListener {

	private ArrayList<HashMap<String, Object>> mDataSource;
	private LazyloadListener mLazyloadListener;

	private int mResource;
	private int mDividerResource;
	private boolean mHasGroup;
	private boolean mIsLazyLoad;
	private String[] mFrom;
	private int[] mTo;
	private LayoutInflater mInflater;
	private ImageLoaderUtil loaderUtil;
	private Context mContext;
	private boolean mIsProductList;
	private HashMap<String, DownloadInfo> mDownloadingTask;
	// private HashMap<Integer, String> mList;
	private ArrayList<String> mInstalledList;
	private DownloadManager mDownloadManager;
	private HashMap<String, HashMap<String, Object>> mDownloadExtraInfo;
	private HashMap<String, String> mIconCache;
	private HashMap<String, HashMap<String, Object>> mCheckedList;
	private HashMap<String, UpgradeInfo> mUpdateList;
	private boolean mIsRankList;
	private ListView mListView;
	private String mPageType = Constants.GROUP_APPLIST;
	private String downUrl = "";

	/**
	 * Application list adapter<br>
	 * 如果不希望这个子View显示，设置Key对应的Value为Null即可
	 * 
	 * @param context
	 *            application context
	 * @param data
	 *            the datasource behind the listview
	 * @param resource
	 *            list item view layout resource
	 * @param from
	 *            the keys array of data source which you want to bind to the
	 *            view
	 * @param to
	 *            array of according view id
	 * @param hasGroup
	 *            whether has place holder
	 */
	public AppListAdapter(Context context, ListView listview,
			ArrayList<HashMap<String, Object>> data, int resource,
			String[] from, int[] to) {
		if (data == null) {
			mDataSource = new ArrayList<HashMap<String, Object>>();
		} else {
			mDataSource = data;
		}
		// mList = new HashMap<Integer, String>();
		mContext = context;
		mListView = listview;
		mResource = resource;
		loaderUtil = ImageLoaderUtil.getInstance(mContext);
		mFrom = from;
		mTo = to;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mCheckedList = new HashMap<String, HashMap<String, Object>>();
		mIconCache = new HashMap<String, String>();
	}

	/**
	 * @return the mCheckedList
	 */
	public HashMap<String, HashMap<String, Object>> getCheckedList() {
		return mCheckedList;
	}

	/**
	 * 设置是否包含分隔项
	 * 
	 * @param flag
	 *            默认是false, 不包含分隔符
	 */
	public void setContainsPlaceHolder(boolean flag) {
		mHasGroup = flag;
	}

	/**
	 * 设置分隔项的资源ID
	 */
	public void setPlaceHolderResource(int id) {
		mDividerResource = id;
	}

	/**
	 * 排行榜，需要识别排位
	 */
	public void setRankList() {
		mIsRankList = true;
	}

	/**
	 * 用于统计Lable
	 */
	public void setmPageType(String mPageType) {
		this.mPageType = mPageType;
	}

	/**
	 * 产品列表，需要刷新产品状态
	 */
	public void setProductList() {
		mIsProductList = true;
		Session session = Session.get(mContext);
		session.addObserver(this);
		mDownloadManager = session.getDownloadManager();
		mInstalledList = session.getInstalledApps();
		mDownloadingTask = session.getDownloadingList();
		mUpdateList = session.getUpdateList();
		mDownloadExtraInfo = new HashMap<String, HashMap<String, Object>>();
	}

	/*
	 * How many items are in the data set represented by this Adapter.
	 */
	@Override
	public int getCount() {
		if (mDataSource == null) {
			return 0;
		}
		return mDataSource.size();
	}

	@Override
	public Object getItem(int position) {
		if (mDataSource != null && position < getCount()) {
			return mDataSource.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		if (mDataSource == null || mDataSource.size() == 0) {
			return true;
		}
		return super.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	/*
	 * Clear all the data
	 */
	public void clearData() {
		if (mDataSource != null) {
			mDataSource.clear();
			notifyDataSetChanged();
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (mHasGroup && isPlaceHolder(position)) {
			// place holder for group
			return 1;
		}
		// normal item
		return 0;
	}

	/*
	 * Return the view types of the list adapter
	 */
	@Override
	public int getViewTypeCount() {
		if (mHasGroup) {
			return 2;
		} else {
			return 1;
		}
	}

	/**
	 * Lazyload web data
	 * 
	 * @param newData
	 */
	public void addData(ArrayList<HashMap<String, Object>> newData) {
		if (newData != null && newData.size() > 0
				&& !mDataSource.contains(newData)) {
			mDataSource.addAll(getCount(), newData);
			notifyDataSetChanged();
		}
	}

	public void addData(HashMap<String, Object> newData) {
		if (newData != null && newData.size() > 0
				&& !mDataSource.contains(newData)) {
			mDataSource.add(getCount(), newData);
			notifyDataSetChanged();
		}
	}

	public void removeData(HashMap<String, Object> oldData) {
		if (mDataSource != null && mDataSource.contains(oldData)) {
			mDataSource.remove(oldData);
			notifyDataSetChanged();
		}
	}

	public void removeData(int position) {
		if (mDataSource != null) {
			mDataSource.remove(position);
			notifyDataSetChanged();
		}
	}

	public void insertData(HashMap<String, Object> newData) {
		if (newData != null) {
			mDataSource.add(0, newData);
			notifyDataSetChanged();
		}
	}

	public void setLazyloadListener(LazyloadListener listener) {
		mIsLazyLoad = true;
		mLazyloadListener = listener;
	}

	@Override
	public boolean isEnabled(int position) {
		if (mHasGroup) {
			return !isPlaceHolder(position);
		} else {
			return true;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		// last 4 item trigger the lazyload event
		if (mIsLazyLoad && !mLazyloadListener.isEnd()
				&& (position == getCount() - 4)) {
			// fix the multi-load situation
			synchronized (this) {
				if (mLazyloadListener.isLoadOver()) {
					mLazyloadListener.lazyload();
					// Utils.trackEvent(mContext, mPageType,
					// Constants.PRODUCT_LAZY_LOAD);
				}
			}
		}

		// reach here when list is not at the end
		assert (position < getCount());
		View v;
		if (convertView == null) {
			v = newView(position, parent);
		} else {
			v = convertView;
		}

		if (mIsProductList) {
			HashMap<String, Object> item = mDataSource.get(position);
			String packageName = Utils.getString(item
					.get(Constants.KEY_PRODUCT_PACKAGE_NAME));
			// 列表中存在下载列表的任务，更新状态
			if (mDownloadingTask != null) {
				if (mDownloadingTask.containsKey(packageName)) {
					DownloadInfo info = mDownloadingTask.get(packageName);
					// 下载过程中，刷新进度
					item.put(Constants.KEY_PRODUCT_INFO, info.mProgress);
					item.put(Constants.KEY_PRODUCT_DOWNLOAD,
							info.mProgressLevel);
				} else if (mInstalledList.contains(packageName)) {
					// 已经安装的应用
					if (mUpdateList.containsKey(packageName)) {
						// 可以更新
						item.put(Constants.KEY_PRODUCT_DOWNLOAD,
								Constants.STATUS_UPDATE);
					} else {
						item.put(Constants.KEY_PRODUCT_DOWNLOAD,
								Constants.STATUS_INSTALLED);
					}
				} else {
					Object result = item.get(Constants.KEY_PRODUCT_DOWNLOAD);
					if (result != null) {
						int status = (Integer) result;
						if (status == Constants.STATUS_PENDING) {
							// 准备开始下载，无需处理
						} else if (status != Constants.STATUS_UNINSTALL) {
							// 默认的状态是未下载
							item.put(Constants.KEY_PRODUCT_DOWNLOAD,
									Constants.STATUS_NORMAL);
						}
					}
				}
			} else {
				// 默认的状态是未下载
				item.put(Constants.KEY_PRODUCT_DOWNLOAD,
						Constants.STATUS_NORMAL);
			}

		}

		bindView(position, v);
		return v;
	}

	/*
	 * Create new view object and cache some views associated with it
	 */
	private View newView(int position, ViewGroup parent) {
		View v;
		if (mHasGroup && isPlaceHolder(position)) {
			v = mInflater.inflate(mDividerResource, parent, false);
		} else {
			v = mInflater.inflate(mResource, parent, false);
		}

		final int[] to = mTo;
		final int count = to.length;
		final View[] holder = new View[count];

		for (int i = 0; i < count; i++) {
			holder[i] = v.findViewById(to[i]);

			// if (R.id.cb_install == to[i]) {
			// if (holder[i] != null) {
			// ((CheckBox) holder[i])
			// .setOnCheckedChangeListener(mCheckChangeListener);
			// }
			// }
		}

		v.setTag(holder);
		return v;
	}

	/*
	 * bind the background data to the view
	 */
	private void bindView(int position, View view) {

		final HashMap<String, Object> dataSet = mDataSource.get(position);
		if (dataSet == null) {
			return;
		}

		final View[] holder = (View[]) view.getTag();
		final String[] from = mFrom;
		final int[] to = mTo;
		final int count = to.length;

		setView(position, dataSet, holder, from, count);
	}

	private void setView(int position, final HashMap<String, Object> dataSet,
			final View[] holder, final String[] from, final int count) {
		for (int i = 0; i < count; i++) {

			final View v = holder[i];

			if (v != null) {

				final Object data = dataSet.get(from[i]);
				if (data == null) {
					// make this view invisible if value is empty
					v.setVisibility(View.GONE);
					continue;
				} else {
					v.setVisibility(View.VISIBLE);
				}

				if (v instanceof Checkable) {
					// 为装机必备记录列表位置
					v.setTag(position);
					if (data instanceof Boolean) {
						((Checkable) v).setChecked((Boolean) data);
					} else {
						throw new IllegalStateException(v.getClass().getName()
								+ " should be bound to a Boolean, not a "
								+ data.getClass());
					}
				} else if (v instanceof Button) {
					v.setTag(data);
				} else if (v instanceof ImageButton) {
					// Note: keep the instanceof ImageButton be Top of
					// ImageView
					// since ImageButton is ImageView
				} else if (v instanceof ImageView) {
					setViewImage(position, (ImageView) v, data);
				} else if (v instanceof RatingBar) {
					setViewRating((RatingBar) v, data);
				} else if (v instanceof TextView) {
					// Note: keep the instanceof TextView check at the
					// bottom of
					// these if since a lot of views are TextViews (e.g.
					// CheckBoxes).
					setViewText(position, (TextView) v, data);
				} else {
					throw new IllegalStateException(v.getClass().getName()
							+ " is not a "
							+ " view that can be bounds by this SimpleAdapter");
				}

			}
		}
	}

	protected void setViewResource(View v, int position, int[] bitmaps) {
		if (v instanceof ImageView) {
			ImageView view = (ImageView) v;
			HashMap<String, Object> map = mDataSource.get(position);

			int flag = (Integer) map.get(String.valueOf(position));
			view.setImageResource(bitmaps[flag]);
		}
	}

	/*
	 * Set drawable value for ImageView
	 */
	private void setViewImage(int position, ImageView v, Object obj) {

		Drawable oldDrawable = v.getDrawable();
		if (oldDrawable != null) {
			// clear the CALLBACK reference to prevent of OOM error
			oldDrawable.setCallback(null);
		}

		if (obj instanceof Drawable) {
			// here is one drawable object
			v.setImageDrawable((Drawable) obj);

		} else if (obj instanceof String) {
			// here is one remote object (URL)
			loaderUtil.setImageNetResourceCor(v, Utils.getString(obj),
					R.drawable.img_default);
			// ImageUtils.download(mContext, Utils.getString( obj, v);
		} else if (obj instanceof Boolean) {
			if ((Boolean) obj) {
				v.setVisibility(View.VISIBLE);
			} else {
				v.setVisibility(View.INVISIBLE);
			}
		}
	}

	/*
	 * Set the value for RatingBar
	 */

	private void setViewRating(RatingBar v, Object rating) {
		try {
			v.setMax(50);
			v.setProgress(Integer.valueOf(Utils.getString(rating)));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Set text value for TextView
	 */
	private void setViewText(int position, TextView v, Object text) {

		if (text instanceof byte[]) {
			v.setText(Utils.getUTF8String((byte[]) text));
		} else if (text instanceof CharSequence) {
			if (mIsRankList && v.getId() == R.id.soft_rank) {
				// 排行榜
				if (position == 0) {
					v.setBackgroundResource(R.drawable.rank_bg_red);
					v.setText((position + 1) + "");
					v.setVisibility(View.VISIBLE);
				} else if (position == 1) {
					v.setBackgroundResource(R.drawable.rank_bg_orange);
					v.setText((position + 1) + "");
					v.setVisibility(View.VISIBLE);
				} else if (position == 2) {
					v.setBackgroundResource(R.drawable.rank_bg_blue);
					v.setText((position + 1) + "");
					v.setVisibility(View.VISIBLE);
				} else {
					v.setVisibility(View.GONE);
				}
			} else if (v.getId() == R.id.soft_rank && !mIsRankList) {
				v.setVisibility(View.GONE);
			} else if (v.getId() == R.id.soft_name) {
				v.setText(Utils.getString(text));
			} else if (v.getId() == R.id.soft_size) {
				if (text != null && text.toString().length() > 0) {
					v.setText(" | " + Utils.getString(text));
				}
			} else if (v.getId() == R.id.soft_count) {
				v.setText(Utils.parseDownCount(Utils.getString(text))
						+ Constants.COUNT_DOWNLOAD);
			} else {
				v.setText((CharSequence) text);
			}
		} else if (text instanceof Integer) {
			// 应用状态指示器
			v.setTag(position);
			final int level = (Integer) text;
			// Drawable indicatorDrawble = v.getCompoundDrawables()[1];
			// indicatorDrawble.setLevel(level);
			if (Constants.STATUS_NORMAL == level) {
				// 未下载
				Utils.setTvColorAndBg(mContext, v, R.string.download,
						R.color.status_run, R.drawable.status_run_seletor);
			} else if (Constants.STATUS_PENDING == level) {
				// 准备开始下载
				Utils.setTvColorAndBg(mContext, v,
						R.string.download_status_downloading,
						R.color.status_run, R.drawable.status_run_seletor);
			} else if (Constants.STATUS_RUNNING == level) {
				// 下载中
				Utils.setTvColorAndBg(
						mContext,
						v,
						Utils.getString(mDataSource.get(position).get(
								Constants.KEY_PRODUCT_INFO)),
						R.color.status_run, R.drawable.status_run_seletor);
			} else if (Constants.STATUS_PAUSE == level) {
				// 下载暂停中
				Utils.setTvColorAndBg(
						mContext,
						v,
						Utils.getString(mDataSource.get(position).get(
								Constants.KEY_PRODUCT_INFO)),
						R.color.status_wait, R.drawable.status_waited);
			} else if (Constants.STATUS_QUEUE == level) {
				// 等待队列中
				Utils.setTvColorAndBg(mContext, v, R.string.download_paused,
						R.color.status_wait, R.drawable.status_waited);
			} else if (Constants.STATUS_FAILURE == level) {
				// 下载失败
				Utils.setTvColorAndBg(mContext, v,
						R.string.download_status_failure,
						R.color.status_failure, R.drawable.status_failured);
			} else if (Constants.STATUS_UNINSTALL == level) {
				// 已经下载，未安装
				Utils.setTvColorAndBg(mContext, v, R.string.download_install,
						R.color.status_install, R.drawable.status_installed);
			} else if (Constants.STATUS_UPDATE == level) {
				// 有更新
				Utils.setTvColorAndBg(mContext, v, R.string.operation_update,
						R.color.status_update, R.drawable.status_updated);
			} else if (Constants.STATUS_INSTALLED == level) {
				// 已经安装
				Utils.setTvColorAndBg(mContext, v, R.string.download_open,
						R.color.status_open, R.drawable.status_opened);
			} else {
				// 未下载
				Utils.setTvColorAndBg(mContext, v, R.string.download,
						R.color.status_run, R.drawable.status_run_seletor);
			}
			// 为下载按钮绑定事件
			v.setOnClickListener(mDownloadListener);
		}
	}

	private OnClickListener mDownloadListener = new OnClickListener() {

		@Override
		public void onClick(View v) {

			int position = (Integer) v.getTag();
			final HashMap<String, Object> item = mDataSource.get(position);
			int status = (Integer) item.get(Constants.KEY_PRODUCT_DOWNLOAD);
			// int payType = (Integer) item.get(Constants.KEY_PRODUCT_PAY_TYPE);
			Object downloadId = item.get(Constants.KEY_PRODUCT_DOWNLOAD_ID);
			final String appid = Utils.getString(item
					.get(Constants.KEY_PRODUCT_ID));
			final String pkgName = Utils.getString(item
					.get(Constants.KEY_PRODUCT_PACKAGE_NAME));
			if (Constants.STATUS_NORMAL == status
					|| Constants.STATUS_UPDATE == status) {
				final String iconUrl = Utils.getString(item
						.get(Constants.KEY_PRODUCT_ICON_URL));
				// 开始下载，避免用户多次点击
				item.put(Constants.KEY_PRODUCT_DOWNLOAD,
						Constants.STATUS_PENDING);
				mIconCache.put(pkgName, iconUrl);
				Utils.deleteFile(mDownloadManager.queryLocalUriByAppid(appid));
				downUrl = Utils.checkUrlContainHttp(Constants.URL_BASE_HOST,
						Utils.getString(item
								.get(Constants.KEY_PRODUCT_DOWNLOAD_URL)));
				Utils.E("Downurl  " + downUrl);
				Request request = new Request(Uri.parse(downUrl));
				request.setTitle(Utils.getString(item
						.get(Constants.KEY_PRODUCT_NAME)));
				request.setPackageName(Utils.getString(pkgName));
				request.setLogo(Utils.checkUrlContainHttp(
						Constants.URL_BASE_HOST, iconUrl));
				request.setAppId(appid);
				request.setDestinationInExternalPublicDir(Constants.ROOT_DIR,
						Utils.getString(item.get(Constants.KEY_PRODUCT_NAME)));
				request.setDescription(Utils.getString(item
						.get(Constants.KEY_PRODUCT_DESCRIPTION)));
				request.setSourceType(Constants.DOWNLOAD_FROM_MARKET);
				// request.setMD5(info.fileMD5);
				long downId = mDownloadManager.enqueue(request);
				item.put(Constants.KEY_PRODUCT_DOWNLOAD_ID, downId);
				mDownloadExtraInfo.put(appid, item);
				ToastUtil.showShortToast(mContext, R.string.download_start);
				notifyDataSetChanged();
			} else if (Constants.STATUS_FAILURE == status) {
				Utils.deleteFile(mDownloadManager.queryLocalUriByAppid(appid));
				mDownloadManager.restartDownload(getDownloadId(downloadId,
						appid));
				notifyDataSetChanged();
			} else if (Constants.STATUS_RUNNING == status) {
				mDownloadManager
						.pauseDownload(getDownloadId(downloadId, appid));
				notifyDataSetChanged();
			} else if (Constants.STATUS_PAUSE == status) {
				mDownloadManager
						.resumeDownload(getDownloadId(downloadId, appid));
				notifyDataSetChanged();
			} else if (Constants.STATUS_UNINSTALL == status) {
				// 安装应用
				String packageName = Utils.getString(item
						.get(Constants.KEY_PRODUCT_PACKAGE_NAME));
				String filePath = mDownloadManager.queryLocalUriByAppid(appid);
				DownloadInfo info = mDownloadingTask.get(packageName);
				if (info != null) {
					Utils.installApk(mContext, info.mFilePath, appid,
							getDownloadId(downloadId, appid));
				} else if (!TextUtils.isEmpty(filePath)) {
					Utils.installApk(mContext, filePath, appid,
							getDownloadId(downloadId, appid));
				} else {
					ToastUtil.showShortToast(mContext, "下载文件出错或者丢失,无法安装.");
				}
			} else if (Constants.STATUS_INSTALLED == status) {
				// 已经安装,直接打开应用
				Utils.startAPP(mContext, pkgName);
			}
		}
	};

	/**
	 * 是否分隔符
	 */
	private boolean isPlaceHolder(int position) {
		HashMap<String, Object> item = mDataSource.get(position);
		return (Boolean) item.get(Constants.KEY_PLACEHOLDER);
	}

	// /*
	// * 装机必备安装选项
	// */
	// private OnCheckedChangeListener mCheckChangeListener = new
	// OnCheckedChangeListener() {
	//
	// @Override
	// public void onCheckedChanged(CompoundButton buttonView,
	// boolean isChecked) {
	// int position = (Integer) buttonView.getTag();
	//
	// HashMap<String, Object> item = mDataSource.get(position);
	// item.put(Constants.INSTALL_APP_IS_CHECKED, isChecked);
	//
	// if (isChecked) {
	// mCheckedList.put(
	// Utils.getString(item.get(Constants.KEY_PRODUCT_ID)),
	// item);
	// } else {
	// mCheckedList.remove(item.get(Constants.KEY_PRODUCT_ID));
	// }
	// buttonView.setChecked(isChecked);
	// }
	// };
	//
	// @SuppressWarnings("unchecked")
	// private void updateSingleRow(ListView listView, String packageName) {
	//
	// if (listView != null) {
	// // Parcelable listState = listView.onSaveInstanceState();
	// // listView.onRestoreInstanceState(listState);
	// int start = listView.getFirstVisiblePosition();
	// for (int i = start, last = listView.getLastVisiblePosition(); i <= last;
	// i++)
	// if (listView.getItemAtPosition(i) != null
	// && listView.getItemAtPosition(i) instanceof HashMap<?, ?>) {
	//
	// if (packageName.equals(((HashMap<String, Object>) listView
	// .getItemAtPosition(i))
	// .get(Constants.KEY_PRODUCT_PACKAGE_NAME))) {
	// View view = listView.getChildAt(i - start);
	// getView(i, view, listView);
	// break;
	// }
	// }
	// }
	// }

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {

		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {

	}

	/**
	 * 下载状态更新，刷新列表状态
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg1 instanceof HashMap) {
			mDownloadingTask = (HashMap<String, DownloadInfo>) arg1;
			notifyDataSetChanged();
		} else if (arg1 instanceof Integer) {
			notifyDataSetChanged();
		}
	}

	private long getDownloadId(Object downId, String pid) {
		return downId == null ? mDownloadManager.queryIdByAppId(pid) : Long
				.valueOf(Utils.getString(downId));
	}

	/**
	 * Lazyload linstener If you want use the lazyload function, must implements
	 * this interface
	 */
	public interface LazyloadListener {

		/**
		 * You should implements this method to justify whether should do
		 * lazyload
		 * 
		 * @return
		 */
		boolean isEnd();

		/**
		 * Do something that process lazyload
		 */
		void lazyload();

		/**
		 * Indicate whether the loading process is over
		 * 
		 * @return
		 */
		boolean isLoadOver();
	}

}