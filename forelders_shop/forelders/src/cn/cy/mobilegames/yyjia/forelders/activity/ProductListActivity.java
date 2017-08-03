package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.LazyloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.util.view.LoadingDrawable;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.R;

public class ProductListActivity extends LazyloadListActivity implements
		ApiRequestListener, OnItemClickListener, OnClickListener {
	private ImageView backBtn, menuBtn;
	private TextView titleTv;
	private MenuAdapter adapter;
	private MenuPopupWindow menuPopupWindow;

	// Loading
	private FrameLayout mLoading;
	private ProgressBar mProgress;
	private TextView mNoData;
	private String cid, name;
	private int mTotalSize;
	private int requestNum = 1;
	private ProductListActivity activity;
	private AppListAdapter mAdapter;
	public int requestCode;
	public int totalPage;

	@Override
	public boolean doInitView(Bundle savedInstanceState) {
		setContentView(R.layout.activity_listview);
		initView();
		lazyload();
		return true;
	}

	private void initView() {
		mList = (ListView) findViewById(android.R.id.list);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		menuBtn = (ImageView) findViewById(R.id.menu_img);
		titleTv = (TextView) findViewById(R.id.title);
		titleTv.setText(TextUtils.isEmpty(name) ? "" : name);
		mLoading = (FrameLayout) findViewById(R.id.loading);
		mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
		mProgress.setIndeterminateDrawable(new LoadingDrawable(
				getApplicationContext()));
		mProgress.setVisibility(View.VISIBLE);
		mNoData = (TextView) mLoading.findViewById(R.id.no_data);
		mNoData.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		menuBtn.setOnClickListener(this);
		mList.setEmptyView(mLoading);
		mList.setOnItemClickListener(this);

	}

	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i],
					Constants.menuLogos[i]));
		}
		adapter = new MenuAdapter(mList, activity);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		activity = ProductListActivity.this;
		initData();
		super.onCreate(savedInstanceState);
	}

	public void setRequestCode(int code) {
		requestCode = code;
	};

	@Override
	public void doLazyload() {
		switch (requestCode) {
		case MarketAPI.ACTION_GET_CATE_MORE:
			MarketAPI.getCateMore(getApplicationContext(), this, cid,
					requestNum);
			break;
		case MarketAPI.ACTION_GUEST_RECOM:
			MarketAPI.guestRecom(getApplicationContext(),
					ProductListActivity.this);
			break;
		default:
			break;
		}
	}

	@Override
	public AppListAdapter doInitListAdapter() {
		mAdapter = new AppListAdapter(getApplicationContext(), mList, null,
				R.layout.soft_item_view, new String[] {
						Constants.KEY_PRODUCT_ICON_URL,
						Constants.KEY_PRODUCT_NAME,
						Constants.KEY_PRODUCT_DOWNLOAD_COUNT,
						Constants.KEY_PRODUCT_SIZE,
						Constants.KEY_PRODUCT_RATING_COUNT,
						Constants.KEY_PRODUCT_DOWNLOAD, Constants.KEY_RANK },
				new int[] { R.id.soft_logo, R.id.soft_name, R.id.soft_count,
						R.id.soft_size, R.id.soft_rating_bar,
						R.id.soft_downbtn, R.id.soft_rank });
		mAdapter.setProductList();
		// if (!TextUtils.isEmpty(mCategory)) {
		// // 排行榜列表
		// mAdapter.setRankList();
		// }
		return mAdapter;
	}

	public void setTotalSize(int size) {
		if (size > 0) {
			mTotalSize = size;
		}
	}

	public void setTitle(String title) {
		if (title != null) {
			name = title;
		}
	}

	public void setCid(String id) {
		if (id != null) {
			cid = id;
		}
	}

	@Override
	protected int getItemCount() {
		return mTotalSize;
	}

	@Override
	protected void onPause() {
		if (menuPopupWindow != null) {
			menuPopupWindow.dismiss();
		}
		super.onPause();
	}

	@Override
	public void onSuccess(int method, Object obj) {
		if (obj != null && obj instanceof ListResult) {
			ListResult result = (ListResult) obj;

			ArrayList<HashMap<String, Object>> data = ApiResponseFactory
					.parseAppList(activity, obj);
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
	}

	@Override
	public void onError(int method, int statusCode) {
		if (statusCode == ApiAsyncTask.BUSSINESS_ERROR) {
			// 没有数据
			if (mList != null && mFooterView != null) {
				mList.removeFooterView(mFooterView);
			}
		} else {
			// 超时
			mNoData.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
		setLoadResult(false);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Activity parent = getParent();
		if (parent != null) {
			return parent.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 去产品详细页
		int headerViewCounts = mList.getHeaderViewsCount();
		HashMap<String, Object> item = (HashMap<String, Object>) mAdapter
				.getItem(headerViewCounts > 0 ? position + 1 : position);
		Bundle data = new Bundle();
		data.putString(Constants.KEY_APP_ID, item.get(Constants.KEY_PRODUCT_ID)
				.toString());
		data.putString(Constants.KEY_APP_NAME,
				item.get(Constants.KEY_PRODUCT_NAME).toString());
		Utils.toOtherClass(ProductListActivity.this, SoftDetailActivity.class,
				data);
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
		case R.id.back_btn:
			finish();
			break;
		case R.id.menu_img:
			menuPopupWindow = new MenuPopupWindow(activity, adapter);
			menuPopupWindow
					.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
			int[] location = new int[2];
			menuBtn.getLocationOnScreen(location);
			menuPopupWindow.showAtLocation(menuBtn, Gravity.NO_GRAVITY,
					location[0] + menuBtn.getWidth(),
					location[1] + menuBtn.getHeight());
			break;
		default:
			break;
		}
	}

}