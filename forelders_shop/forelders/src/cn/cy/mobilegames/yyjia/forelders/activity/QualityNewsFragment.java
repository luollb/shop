package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
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
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.InfoListAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.NewsCate;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.view.LoadingDrawable;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 精品--资讯列表
 */
public class QualityNewsFragment extends BaseFragment implements
		OnClickListener, OnItemClickListener {
	private ListView listView;
	private FrameLayout mLoading;
	private ProgressBar mProgress;
	private TextView mNoData;
	private View view;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		MarketAPI.getNewsCate(getActivity(), this);
		super.onCreate(savedInstanceState);
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

		listView = (ListView) view.findViewById(android.R.id.list);
		listView.setBackgroundColor(getResources().getColor(R.color.gray_6e));
		listView.setDividerHeight(15);
		mLoading = (FrameLayout) view.findViewById(R.id.loading);
		mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
		mProgress.setIndeterminateDrawable(new LoadingDrawable(getActivity()));
		mProgress.setVisibility(View.VISIBLE);
		mNoData = (TextView) mLoading.findViewById(R.id.no_data);
		mNoData.setOnClickListener(this);
		listView.setEmptyView(mLoading);
		listView.setOnItemClickListener(this);

	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_NEWS_CATE:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				if (result.list != null) {
					List<NewsCate> newsCateLists = JsonUtil.toObjectList(
							result.list, NewsCate.class);
					if (newsCateLists != null) {
						listView.setAdapter(new InfoListAdapter(newsCateLists,
								getActivity(), imageLoader));
					}
				}
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> paramAdapterView, View paramView,
			int paramInt, long paramLong) {

	}

}
