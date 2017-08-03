package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.LazyloadListFragment;
import cn.cy.mobilegames.yyjia.forelders.util.view.LoadingDrawable;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * @author Administrator
 * 
 */
public class ProductListFragment extends LazyloadListFragment
		implements ApiRequestListener, OnItemClickListener, OnClickListener {
	// Loading
	private FrameLayout mLoading;
	private ProgressBar mProgress;
	private TextView mNoData;
	private AppListAdapter mAdapter;
	private int mTotalSize = 500;
	public int requestNum = 1;
	public int requestCode;
	public boolean isRank;
	public int totalPage;
	// private MenuAdapter menuAdapter;
	// private MenuPopupWindow menuPopupWindow;
	// private LinearLayout backView;
	private FragmentActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = getActivity();
		// initData();
		super.onCreate(savedInstanceState);
	}

	// private void initData() {
	// List<MenuBean> mList = new ArrayList<MenuBean>();
	// for (int i = 0; i < Constants.menuLogos.length; i++) {
	// mList.add(new MenuBean(Constants.menuNames[i],
	// Constants.menuLogos[i]));
	// }
	// menuAdapter = new MenuAdapter(mList, activity);
	// }

	private void initView(LayoutInflater inflater, View view) {
		mList = (ListView) view.findViewById(android.R.id.list);
		mLoading = (FrameLayout) view.findViewById(R.id.loading);
		mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
		mProgress.setIndeterminateDrawable(new LoadingDrawable(getActivity()));
		mProgress.setVisibility(View.VISIBLE);
		mNoData = (TextView) mLoading.findViewById(R.id.no_data);
		mNoData.setOnClickListener(this);
		mList.setEmptyView(mLoading);
		mList.setOnItemClickListener(this);

	}

	@SuppressLint("InflateParams")
	@Override
	public boolean doInitView(Bundle savedInstanceState) {

		if (view == null) {
			view = getActivity().getLayoutInflater().inflate(R.layout.common_list_view, null);
			initView(inflater, view);
		}
		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}

		lazyload();
		return true;
	}

	public void setTotalSize(int size) {
		if (size > 0) {
			mTotalSize = size;
		}
	}

	public void setRank(boolean isRank) {
		this.isRank = isRank;
	}

	public void setRequestCode(int code) {
		requestCode = code;
	};

	@Override
	public void doLazyload() {
		switch (requestCode) {
		case MarketAPI.ACTION_GET_QUALITY_HOT:
			MarketAPI.getQualityHot(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_QUALITY_NECESSARY:
			MarketAPI.getQualityNecessary(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_SOFT_NEW:
			MarketAPI.getSoftNew(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_SOFT_RANK:
			MarketAPI.getSoftRank(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_SOFT_RECOM:
			MarketAPI.getSoftRecom(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_GAME_NEW:
			MarketAPI.getGameNew(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_GAME_RANK:
			MarketAPI.getGameRank(getActivity(), this, requestNum);
			break;
		case MarketAPI.ACTION_GET_GAME_RECOM:
			MarketAPI.getGameRecom(getActivity(), this, requestNum);
			break;
		default:
			break;
		}
	}

	@Override
	public AppListAdapter doInitListAdapter() {
		mAdapter = new AppListAdapter(getActivity(), mList, null, R.layout.soft_item_view,
				new String[] { Constants.KEY_PRODUCT_ICON_URL, Constants.KEY_PRODUCT_NAME,
						Constants.KEY_PRODUCT_DOWNLOAD_COUNT, Constants.KEY_PRODUCT_SIZE,
						Constants.KEY_PRODUCT_RATING_COUNT, Constants.KEY_PRODUCT_DOWNLOAD, Constants.KEY_RANK },
				new int[] { R.id.soft_logo, R.id.soft_name, R.id.soft_count, R.id.soft_size, R.id.soft_rating_bar,
						R.id.soft_downbtn, R.id.soft_rank });
		mAdapter.setProductList();
		if (isRank) {
			// 排行榜列表
			mAdapter.setRankList();
		}
		return mAdapter;
	}

	@Override
	protected int getItemCount() {
		return mTotalSize;
	}

	@Override
	public void onSuccess(int method, Object obj) {
		mProgress.setVisibility(View.GONE);
		switch (method) {
		case MarketAPI.ACTION_GET_QUALITY_HOT:
		case MarketAPI.ACTION_GET_QUALITY_NECESSARY:
		case MarketAPI.ACTION_GET_SOFT_NEW:
		case MarketAPI.ACTION_GET_SOFT_RANK:
		case MarketAPI.ACTION_GET_SOFT_RECOM:
		case MarketAPI.ACTION_GET_GAME_NEW:
		case MarketAPI.ACTION_GET_GAME_RANK:
		case MarketAPI.ACTION_GET_GAME_RECOM:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				ArrayList<HashMap<String, Object>> data = ApiResponseFactory.parseAppList(getActivity(), obj);
				mAdapter.addData(data);

				if (totalPage <= 0) {
					if (result.totalpage > 0) {
						totalPage = result.totalpage;
						setTotalSize(data.size() * totalPage);
					}
				}
				if (requestNum <= totalPage) {
					requestNum++;
					setLoadResult(true);
				}
			} else {
				setLoadResult(false);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		mProgress.setVisibility(View.GONE);
		if (statusCode == ApiAsyncTask.BUSSINESS_ERROR) {
			// 没有数据
			if (mList != null && mFooterView != null) {
				mList.removeFooterView(mFooterView);
				mNoData.setVisibility(View.VISIBLE);
			}
		} else {
			// 超时
			mNoData.setVisibility(View.VISIBLE);
		}
		setLoadResult(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// 去产品详细页
		if (mAdapter.getItem(position) != null) {
			HashMap<String, Object> item = (HashMap<String, Object>) mAdapter
					.getItem(mList.getHeaderViewsCount() > 0 ? position - 1 : position);
			Bundle data = new Bundle();
			data.putString(Constants.KEY_APP_ID, item.get(Constants.KEY_PRODUCT_ID).toString());
			data.putString(Constants.KEY_APP_NAME, item.get(Constants.KEY_PRODUCT_NAME).toString());
			Utils.toOtherClass(getActivity(), SoftDetailActivity.class, data);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.no_data:
			// 重试
			mProgress.setVisibility(View.VISIBLE);
			mNoData.setVisibility(View.GONE);
			lazyload();
			break;

		// case R.id.about_version_code:
		// menuPopupWindow = new MenuPopupWindow(activity, menuAdapter);
		// menuPopupWindow
		// .setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
		// int[] location = new int[2];
		// backView.getLocationOnScreen(location);
		// menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY,
		// location[0] + backView.getWidth(),
		// location[1] + backView.getHeight());
		// break;
		default:
			break;
		}
	}

}