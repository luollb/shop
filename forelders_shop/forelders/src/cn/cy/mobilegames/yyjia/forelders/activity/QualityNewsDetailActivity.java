package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.model.NewsDetail;
import cn.cy.mobilegames.yyjia.forelders.model.NewsDetailRecom;
import cn.cy.mobilegames.yyjia.forelders.model.NewsInfoDetail;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.util.view.MyListView;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 精品--资讯详情
 * 
 * @author Administrator
 * 
 */
public class QualityNewsDetailActivity extends BaseActivity {
	private RelativeLayout searchLayout;
	private TextView navTitle;
	// , newsTitle, newsTime, newsType, newsFocus,newsSummary;
	private MyListView relateListview;
	private LinearLayout relateLayout;
	private ImageView backBtn;
	private WebView webView;
	private GestureDetector detector;
	private FrameLayout noDataView;
	private RelativeLayout newsView;
	private ProgressBar progressBar;
	private String newId = "";
	private String title = "";
	private QualityNewsDetailActivity activity;
	private LinearLayout backView;
	private MenuAdapter menuAdapter;
	private MenuPopupWindow menuPopupWindow;
	
	

	// private int progress;
	// private boolean pageFinish;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		initData();
		setContentView(R.layout.activity_news_content);
		try {
			Bundle bun = getIntent().getExtras();
			if (bun != null) {
				newId = bun.getString(Constants.KEY_APP_ID);
				title = bun.getString(Constants.KEY_APP_NAME);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		initView();
		if (newId != null && !TextUtils.isEmpty(newId)) {
			MarketAPI.getNewsDetail(context, activity, newId);
		}
	}

	
	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i],
					Constants.menuLogos[i]));
		}
		menuAdapter = new MenuAdapter(mList, activity);
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		backView = (LinearLayout) findViewById(R.id.back_nav_view);
		noDataView = (FrameLayout) findViewById(R.id.loading);
		newsView = (RelativeLayout) findViewById(R.id.news_view);
		// setView(3);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setVisibility(View.VISIBLE);
		if (title != null) {
			navTitle.setText(Utils.checkEmpty(title));
		}
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		relateLayout = (LinearLayout) findViewById(R.id.relate_news_layout);
		progressBar = (ProgressBar) findViewById(R.id.news_progress);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();
		// webSettings.setLoadWithOverviewMode(true);// 设置webview加载的页面的模式
		webSettings.setUseWideViewPort(true);// 设置webview推荐使用的窗口
		webSettings.setJavaScriptEnabled(true);// 设置支持JS
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// webSettings.setSupportZoom(true);// 实现缩放

		DisplayMetrics metric = new DisplayMetrics(); // 获取屏幕宽度高度
		getWindowManager().getDefaultDisplay().getMetrics(metric); //
		// 获取屏幕宽度高度

		detector = new GestureDetector(this, new MyGestureListener(this));

		// newsFocus = (TextView) findViewById(R.id.news_focus);
		// newsSummary = (TextView) findViewById(R.id.news_summary);
		// newsTime = (TextView) findViewById(R.id.news_time);
		// newsTitle = (TextView) findViewById(R.id.news_title);
		// newsType = (TextView) findViewById(R.id.news_type);

		relateListview = (MyListView) findViewById(R.id.news_relate);
		relateListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int position, long id) {

			}
		});
		searchLayout.setOnClickListener(onClick);
		backBtn.setOnClickListener(onClick);
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.menu_layout:
				menuPopupWindow = new MenuPopupWindow(activity, menuAdapter);
				menuPopupWindow
						.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
				int[] location = new int[2];
				backView.getLocationOnScreen(location);
				menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY,
						location[0] + backView.getWidth(), location[1]
								+ backView.getHeight());
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 把触摸事件交给手势识别对象
	 */

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) { // 注意这里不能用ONTOUCHEVENT方法，不然无效的
		detector.onTouchEvent(ev);
		webView.onTouchEvent(ev);// 这几行代码也要执行，将webview载入MotionEvent对象一下，况且用载入把，不知道用什么表述合适
		return super.dispatchTouchEvent(ev);
	}

	private class MyGestureListener implements OnGestureListener {
		Context context;

		public MyGestureListener(Context context) {
			super();
			this.context = context;
		}

		@Override
		public boolean onDown(MotionEvent paramMotionEvent) {

			return false;
		}

		@Override
		public void onShowPress(MotionEvent paramMotionEvent) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent paramMotionEvent) {

			return false;
		}

		@Override
		public boolean onScroll(MotionEvent paramMotionEvent1,
				MotionEvent paramMotionEvent2, float paramFloat1,
				float paramFloat2) {

			return false;
		}

		@Override
		public void onLongPress(MotionEvent paramMotionEvent) {

		}

		@Override
		public boolean onFling(MotionEvent paramMotionEvent1,
				MotionEvent paramMotionEvent2, float paramFloat1,
				float paramFloat2) {

			return false;
		}

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
		if (menuPopupWindow != null) {
			menuPopupWindow.dismiss();
		}
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private class NewsAdapter extends BaseAdapter {
		List<NewsDetailRecom> recoms;

		private NewsAdapter(List<NewsDetailRecom> recoms) {
			super();
			this.recoms = recoms;
		}

		@Override
		public int getCount() {
			return recoms == null ? 0 : recoms.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHolder holder = null;
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.relate_news_item, null);
				holder = new ViewHolder();
				// holder.relateNewsTime = (TextView) view

				// .findViewById(R.id.news_time);
				holder.relateNewsTitle = (TextView) view
						.findViewById(R.id.news_title);
				holder.itemLayout = (RelativeLayout) view
						.findViewById(R.id.relate_news_item);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			final NewsDetailRecom newsDetailRecom = recoms.get(position);
			holder.itemLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Bundle data = new Bundle();
					data.putString(Constants.KEY_APP_ID, newsDetailRecom.newid);
					data.putString(Constants.KEY_APP_NAME,
							newsDetailRecom.title);
					Utils.toOtherClass(QualityNewsDetailActivity.this,
							QualityNewsDetailOtherActivity.class, data);
				}
			});
			holder.relateNewsTitle.setText(Utils
					.checkEmpty(newsDetailRecom.title));
			return view;
		}
	}

	private class ViewHolder {
		private TextView relateNewsTitle
		// , relateNewsTime
		;
		private RelativeLayout itemLayout;
	}

	// /**
	// * 3 都不显示 2 noDataView显示 1 vScrollView显示
	// *
	// * @param code
	// */
	// private void setView(int code) {
	// switch (code) {
	// case 1:
	// vScrollView.setVisibility(View.VISIBLE);
	// noDataView.setVisibility(View.GONE);
	// break;
	// case 2:
	// vScrollView.setVisibility(View.GONE);
	// noDataView.setVisibility(View.VISIBLE);
	// break;
	// case 3:
	// vScrollView.setVisibility(View.GONE);
	// noDataView.setVisibility(View.GONE);
	// break;
	// default:
	// break;
	// }
	// }

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_NEWS_DETAIL:
			if (obj != null && obj instanceof NewsDetail) {
				NewsDetail detail = (NewsDetail) obj;
				setNewsDetail(detail);
			} else {
				ToastUtil.showShortToast(activity, R.string.no_data);
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {

	}

	private void setNewsDetail(final NewsDetail detail) {
		if (detail.info != null) {
			final NewsInfoDetail infoDetail = JsonUtil.toObject(detail.info,
					NewsInfoDetail.class);
			if (infoDetail != null) {
				if (infoDetail.content != null) {
					webView.loadDataWithBaseURL("about:blank",
							infoDetail.content
							// .replace("<img ",
							// "<img style=\"max-width:100%;height:auto\"")
							, "text/html", "utf-8", null);

					webView.setWebChromeClient(new WebChromeClient() {
						@Override
						public void onProgressChanged(WebView view,
								int newProgress) {
							progressBar.setVisibility(View.VISIBLE);
							progressBar.setProgress(newProgress);
							if (newProgress >= 100) {
								progressBar.setVisibility(View.GONE);
								if (detail.recomlist != null) {
									List<NewsDetailRecom> newsDetailRecom = JsonUtil
											.toObjectList(detail.recomlist,
													NewsDetailRecom.class);
									relateListview.setAdapter(new NewsAdapter(
											newsDetailRecom));
									relateLayout.setVisibility(View.VISIBLE);
								}
							}
						};
					});
				} else {
					newsView.setVisibility(View.GONE);
				}
			} else {
				newsView.setVisibility(View.GONE);
			}
		}
	}

}
