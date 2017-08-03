package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.SubjectAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.Subject;
import cn.cy.mobilegames.yyjia.forelders.util.view.DropDownListView;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 精品-->专题
 */
public class QualitySubjectFragment extends BaseFragment {
	private View view;
	private SubjectAdapter adapter;
	private FrameLayout noDataView;
	private DropDownListView listview;
	private ImageView topBtn, bottomBtn;
	private List<Object> lists;
	private int page = 1;
	private int totalPage;
	private boolean hasMore;
	private final static int REQUESTCODE = 0x02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MarketAPI.getSubjectList(getActivity(), QualitySubjectFragment.this,
				page);
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
			view = inflater.inflate(R.layout.activity_soft_listview, null);
			initView(view);
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	private void initView(View view) {
		noDataView = (FrameLayout) view.findViewById(R.id.loading);
		topBtn = (ImageView) view.findViewById(R.id.to_top_btn);
		bottomBtn = (ImageView) view.findViewById(R.id.to_bottom_btn);
		listview = (DropDownListView) view
				.findViewById(R.id.listview_soft_show);
		listview.setBackgroundColor(getResources().getColor(R.color.gray_6e));
		listview.setDividerHeight(15);
		listview.setEmptyView(noDataView);
		// topBtn.setVisibility(View.VISIBLE);
		// bottomBtn.setVisibility(View.VISIBLE);
		topBtn.setOnClickListener(onClick);
		bottomBtn.setOnClickListener(onClick);
		noDataView.setOnClickListener(onClick);
		listview.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (hasMore) {
					MarketAPI.getSubjectList(getActivity(),
							QualitySubjectFragment.this, page);
				}
			}
		});

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Object item = listview.getItemAtPosition(position);
				if (item != null && item instanceof Subject) {
					startActivityForResult((new Intent(getActivity(),
							QualitySubjectContentActivity.class)
							.putExtra(Constants.REQUEST_VALUE_KHD_SUBJECT,
									(Subject) item)), REQUESTCODE);
				}
				if (adapter != null) {
					adapter.reSetTv(position);
				}
			}
		});
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.to_top_btn:
				listview.setSelection(0);
				break;
			case R.id.to_bottom_btn:
				listview.setSelection(listview.getAdapter().getCount() - 1);
				break;
			case R.id.refresh_layout:
				MarketAPI.getSubjectList(getActivity(),
						QualitySubjectFragment.this, page);
				break;
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_SUBJECT_LIST:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				List<Object> subjects = ApiResponseFactory
						.parseSubjectList(result);
				if (page == 1) {
					lists = subjects;
				}

				if (!lists.containsAll(subjects)) {
					lists.addAll(lists.size(), subjects);
				}

				if (adapter == null) {
					adapter = new SubjectAdapter(getActivity(), lists,
							imageLoader);
					listview.setAdapter(adapter);

				} else {
					adapter.notifyDataSetChanged();
				}

				totalPage = result.totalpage;
				if (page < totalPage) {
					hasMore = true;
					page++;
				} else {
					hasMore = false;
				}
			}
			listview.setHasMore(hasMore);
			listview.onBottomComplete();
			break;

		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {
		listview.setHasMore(false);
		listview.onBottomComplete();
	}

}
