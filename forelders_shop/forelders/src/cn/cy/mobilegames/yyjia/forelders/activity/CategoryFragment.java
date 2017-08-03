package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.CateAdapter;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.view.LoadingDrawable;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 分类
 */
public class CategoryFragment extends BaseFragment {
	private List<Object> cateList;
	private ListView mList;
	private CateAdapter adapter;
	private View view;
	private String type;
	// Loading
	private FrameLayout mLoading;
	private ProgressBar mProgress;
	private TextView mNoData;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setType(type);
		MarketAPI.getAppCategory(getActivity(), CategoryFragment.this, type);
		super.onCreate(savedInstanceState);
	}

	public void setType(String type) {
		this.type = type;
	};

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.common_list_view, null);
			initView(view);
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void initView(View view) {
		mList = (ListView) view.findViewById(android.R.id.list);
		mList.setBackgroundColor(getResources().getColor(R.color.gray_6e));
		mList.setDividerHeight(15);
		mLoading = (FrameLayout) view.findViewById(R.id.loading);
		mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
		mProgress.setIndeterminateDrawable(new LoadingDrawable(getActivity()));
		mProgress.setVisibility(View.VISIBLE);
		mNoData = (TextView) mLoading.findViewById(R.id.no_data);
		mNoData.setOnClickListener(onClick);
		mList.setEmptyView(mLoading);

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.no_data:
				// 重试
				mNoData.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
				setType(type);
				MarketAPI.getAppCategory(getActivity(), CategoryFragment.this,
						type);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_APP_CATEGORY:
			if (obj != null && obj instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<Object> lists = (List<Object>) obj;
				setCateView(lists);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		if (statusCode == ApiAsyncTask.BUSSINESS_ERROR) {
			// 没有数据
			ToastUtil.showShortToast(getActivity(), R.string.no_data);
		} else {
			// 超时
			mNoData.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
	}

	private void setCateView(List<Object> lists) {
		if (lists != null) {
			cateList = lists;
			if (adapter == null) {
				adapter = new CateAdapter(getActivity(), cateList, imageLoader);
				mList.setAdapter(adapter);
			} else {
				adapter.notifyDataSetChanged();
			}
		}
	}

}
