package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.InfoItemAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.model.NewsCate;
import cn.cy.mobilegames.yyjia.forelders.model.NewsList;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.DropDownListView;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 资讯-(活动公告/分类)
 * 
 * @author Administrator
 * 
 */
public class QualityNewsCateActivity extends BaseActivity {
	private ImageView backBtn, menuBtn;
	private TextView navTitle, noData;
	private DropDownListView listView;
	private ImageView topBtn, bottomBtn;
	private List<NewsList> newsLists = new ArrayList<NewsList>();
	private String title = "";
	private String cid = "";
	private FrameLayout noDataView;
	private QualityNewsCateActivity activity;
	private InfoItemAdapter adapter;
	private boolean moreData;
	private int num;
	private LinearLayout backView;
	private MenuAdapter menuAdapter;
	private MenuPopupWindow menuPopupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		num = 1;
		initData();
		setContentView(R.layout.activity_soft_listview);
		try {
			Bundle bun = getIntent().getExtras();
			if (bun != null) {
				cid = bun.getString(Constants.KEY_APP_ID);
				title = bun.getString(Constants.KEY_APP_NAME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
		MarketAPI.getNewsList(activity, activity, cid, num);
	}

	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i],
					Constants.menuLogos[i]));
		}
		menuAdapter = new MenuAdapter(mList, activity);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (menuPopupWindow != null) {
			menuPopupWindow.dismiss();
		}
		MobclickAgent.onPause(this);
	}

	private void initView() {
		backView = (LinearLayout) findViewById(R.id.back_nav_view);
		backView.setVisibility(View.VISIBLE);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		menuBtn = (ImageView) findViewById(R.id.menu_img);
		topBtn = (ImageView) findViewById(R.id.to_top_btn);
		bottomBtn = (ImageView) findViewById(R.id.to_bottom_btn);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setText(title);

		listView = (DropDownListView) findViewById(R.id.listview_soft_show);

		noDataView = (FrameLayout) findViewById(R.id.loading);
		noData = (TextView) findViewById(R.id.no_data);
		listView.setEmptyView(noDataView);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int position, long id) {
				Object item = listView.getItemAtPosition(position);
				if (item != null && item instanceof NewsList) {
					NewsList newsList = (NewsList) item;
					Bundle data = new Bundle();
					data.putString(Constants.KEY_APP_ID, newsList.newid);
					data.putString(Constants.KEY_APP_NAME, newsList.title);
					Utils.toOtherClass(activity,
							QualityNewsDetailActivity.class, data);
				}

			}
		});

		listView.setOnBottomListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (moreData) {
					++num;
					MarketAPI.getNewsList(activity, activity, cid, num);
				}
			}
		});

		backBtn.setOnClickListener(onClick);
		menuBtn.setOnClickListener(onClick);
		noData.setOnClickListener(onClick);
		topBtn.setOnClickListener(onClick);
		bottomBtn.setOnClickListener(onClick);

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			case R.id.no_data:
				MarketAPI.getNewsList(activity, activity, cid, num);
				break;
			case R.id.menu_img:
				menuPopupWindow = new MenuPopupWindow(activity, menuAdapter);
				menuPopupWindow
						.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
				int[] location = new int[2];
				backView.getLocationOnScreen(location);
				menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY,
						location[0] + backView.getWidth(), location[1]
								+ backView.getHeight());
				break;
			case R.id.to_top_btn:
				listView.setSelection(0);
				break;
			case R.id.to_bottom_btn:
				listView.setSelection(listView.getAdapter().getCount() - 1);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		num = 1;
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_NEWS_LIST:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;
				if (result.list != null) {
					List<NewsCate> cates = JsonUtil.toObjectList(result.list,
							NewsCate.class);
					if (cates != null && cates.size() > 0) {
						List<NewsList> news = JsonUtil.toObjectList(
								cates.get(0).newslist, NewsList.class);
						if (news != null && news.size() > 0) {
							moreData = true;

							if (num == 1) {
								newsLists = news;
							} else {
								if (!newsLists.containsAll(news)) {
									newsLists.addAll(newsLists.size(), news);
								}
							}

							if (adapter == null) {
								adapter = new InfoItemAdapter(activity,
										newsLists, imageLoader, listView,
										false, true);
								listView.setAdapter(adapter);
							} else {
								adapter.notifyDataSetChanged();
							}
						} else {
							moreData = false;
						}
						listView.setHasMore(moreData);
					}
				} else {
					ToastUtil.showShortToast(activity, R.string.no_more_data);
				}
				listView.onBottomComplete();
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {

	}

}
