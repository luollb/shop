package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.SearchListAdapter;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.SearchList;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 搜索页面
 */
public class SearchActivity extends BaseActivity {
	private TextView titleText, navTitle;
	private RelativeLayout menuLayout, searchEtLayout;
	private EditText searchEt;
	private Button searchBtn, rechangeBtn;
	private ImageView searchClearBtn, backBtn;
	private GridView listGridView;
	private ListView searchWordListview;
	private int reqPageNum = 1;
	private AppListAdapter listAdapter;
	private int totalPage;
	private SearchActivity activity;
	private FrameLayout loadingView;
	private ProgressBar progressBar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_search_view);
		initView();
		MarketAPI.getRecomSearch(activity, activity, reqPageNum);
	}

	private void initView() {
		backBtn = (ImageView) findViewById(R.id.back_btn);
		titleText = (TextView) findViewById(R.id.search_title);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setVisibility(View.GONE);
		loadingView = (FrameLayout) findViewById(R.id.loading);
		progressBar = (ProgressBar) loadingView.findViewById(R.id.progressbar);
		menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		menuLayout.setVisibility(View.GONE);
		searchEtLayout = (RelativeLayout) findViewById(R.id.search_et_layout);
		// searchImgLayout.setVisibility(View.GONE);
		searchEtLayout.setVisibility(View.VISIBLE);
		titleText.setText(getResources().getString(R.string.hot_search_text));
		searchClearBtn = (ImageView) findViewById(R.id.search_clear);
		searchEt = (EditText) findViewById(R.id.search_et);
		searchEt.addTextChangedListener(watcher);
		searchBtn = (Button) findViewById(R.id.search_btn);
		searchWordListview = (ListView) findViewById(R.id.search_keyword_listview);
		searchWordListview.setEmptyView(loadingView);
		searchWordListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int position, long id) {
				Object result = searchWordListview.getItemAtPosition(position);
				if (result != null && result instanceof HashMap<?, ?>) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> item = (HashMap<String, Object>) result;
					Bundle data = new Bundle();
					data.putString(Constants.KEY_APP_ID,
							item.get(Constants.KEY_PRODUCT_ID).toString());
					data.putString(Constants.KEY_APP_NAME,
							item.get(Constants.KEY_PRODUCT_NAME).toString());
					Utils.toOtherClass(activity, SoftDetailActivity.class, data);
				}
			}
		});
		rechangeBtn = (Button) findViewById(R.id.rechange_btn);

		listGridView = (GridView) findViewById(R.id.search_detail_gridview);
		listGridView.setEmptyView(loadingView);
		backBtn.setOnClickListener(onClick);
		searchBtn.setOnClickListener(onClick);
		searchClearBtn.setOnClickListener(onClick);
		searchEt.setOnClickListener(onClick);
		rechangeBtn.setOnClickListener(onClick);
		menuLayout.setOnClickListener(onClick);
		progressBar.setOnClickListener(onClick);

	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	/**
	 * 搜索框输入为空,隐藏清除按钮
	 * 
	 * @param s
	 */
	private void textHide(CharSequence s) {
		if (s.length() != 0) {
			searchClearBtn.setVisibility(View.VISIBLE);
		} else {
			searchClearBtn.setVisibility(View.GONE);
		}
	}

	/**
	 * 推荐搜索词
	 */
	protected void doSearchList() {
		setListVisible();
		if (reqPageNum < totalPage) {
			++reqPageNum;
			MarketAPI.getRecomSearch(activity, activity, reqPageNum);
		} else {
			ToastUtil.showShortToast(context, R.string.no_more_data);
		}
	}

	/**
	 * 关键字搜索
	 */
	protected void searchKeyWord() {
		searchWordListview.setAdapter(null);
		Utils.HideSoftInput(activity);
		String keyword = searchEt.getText().toString().trim();
		if (!Utils.isEmptyString(keyword)) {
			progressBar.setVisibility(View.VISIBLE);
			MarketAPI.getSearchKeyword(activity, activity, keyword);
			setKeyWordVisible();
		} else {
			ToastUtil.showShortToast(context, "搜索关键词不能为空！");
		}
	}

	protected void clearSearchWd() {
		searchEt.setText("");
		searchClearBtn.setVisibility(View.GONE);
		setTitleText();
		setListVisible();
	}

	TextWatcher watcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.toString().trim().equals("") && s.length() == 0) {
				setTitleText();
				setListVisible();
			}
			textHide(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			textHide(s);
		}

		@Override
		public void afterTextChanged(Editable s) {
			if (s.toString() != null) {
				searchClearBtn.setVisibility(View.VISIBLE);
			} else {
				searchClearBtn.setVisibility(View.GONE);
			}
		}
	};

	private void setListVisible() {
		rechangeBtn.setVisibility(View.VISIBLE);
		listGridView.setVisibility(View.VISIBLE);
		searchWordListview.setVisibility(View.GONE);
	}

	private void setKeyWordVisible() {
		rechangeBtn.setVisibility(View.GONE);
		listGridView.setVisibility(View.GONE);
		searchWordListview.setVisibility(View.VISIBLE);
	}

	private void setTitleText() {
		titleText.setText(getResources().getString(R.string.hot_search_text));
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.menu_layout:
				Utils.toOtherClass(activity, DownloadListActivity.class);
				break;
			case R.id.search_clear:
				clearSearchWd();
				break;
			case R.id.search_btn:
				searchKeyWord();
				break;
			case R.id.search_et:
				break;
			case R.id.rechange_btn:
				doSearchList();
				break;
			case R.id.progressbar:
				searchKeyWord();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_SEARCH_KEYWORD:
			progressBar.setVisibility(View.GONE);

			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				ArrayList<HashMap<String, Object>> data = ApiResponseFactory
						.parseAppList(activity, result);
				titleText.setText("搜索结果(" + data.size() + ")");
				listAdapter = new AppListAdapter(activity, searchWordListview,
						data, R.layout.soft_item_view, new String[] {
								Constants.KEY_PRODUCT_ICON_URL,
								Constants.KEY_PRODUCT_NAME,
								Constants.KEY_PRODUCT_DOWNLOAD_COUNT,
								Constants.KEY_PRODUCT_SIZE,
								Constants.KEY_PRODUCT_RATING_COUNT,
								Constants.KEY_PRODUCT_DOWNLOAD,
								Constants.KEY_RANK }, new int[] {
								R.id.soft_logo, R.id.soft_name,
								R.id.soft_count, R.id.soft_size,
								R.id.soft_rating_bar, R.id.soft_downbtn,
								R.id.soft_rank });
				listAdapter.setProductList();
				searchWordListview.setAdapter(listAdapter);
			} else {
				ToastUtil.showShortToast(context, "没有搜索到数据!");
				titleText.setText("搜索结果(0)");
				searchWordListview.setAdapter(null);
			}

			break;
		case MarketAPI.ACTION_GET_RECOM_SEARCH:
			titleText.setText(getResources()
					.getString(R.string.hot_search_text));
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;
				if (result.totalpage > 0) {
					totalPage = result.totalpage;
				}
				List<SearchList> lists = JsonUtil.toObjectList(result.list,
						SearchList.class);
				listGridView.setAdapter(new SearchListAdapter(lists,
						SearchActivity.this));
				listGridView.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						SearchList searchList = null;
						try {
							searchList = (SearchList) listGridView
									.getItemAtPosition(position);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (searchList != null) {
							searchEt.setText(searchList.word.trim());
							searchKeyWord();
						}
					}
				});

			} else {
				ToastUtil.showShortToast(activity, "没有推荐关键字");
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
		case MarketAPI.ACTION_GET_SEARCH_KEYWORD:
			progressBar.setVisibility(View.GONE);
			searchWordListview.setAdapter(null);
			ToastUtil.showShortToast(context, "没有搜索到数据!");
			titleText.setText("搜索结果(0)");
			break;
		case MarketAPI.ACTION_GET_RECOM_SEARCH:
			ToastUtil.showShortToast(activity, "没有推荐关键字");
			break;
		default:
			break;
		}
	}
}
