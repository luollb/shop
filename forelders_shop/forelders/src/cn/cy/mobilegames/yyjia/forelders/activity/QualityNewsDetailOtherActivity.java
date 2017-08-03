package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
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
import cn.cy.mobilegames.yyjia.forelders.model.NewsInfoDetail;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 资讯详情
 * 
 * @author Administrator
 * 
 */
public class QualityNewsDetailOtherActivity extends BaseActivity {
	private RelativeLayout searchLayout;
	private TextView navTitle
	// , newsTitle, newsTime, newsType, newsFocus, newsSummary
	;
	private LinearLayout relateLayout;
	private ImageView backBtn;
	private WebView webView;
	private GestureDetector detector;
	// private VerticalScrollView vScrollView;
	// private FrameLayout noDataView;
	private String newId;
	private String newtitle;
	private ProgressBar progressBar;
	private QualityNewsDetailOtherActivity activity;
	private LinearLayout backView;
	private MenuAdapter menuAdapter;
	private MenuPopupWindow menuPopupWindow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		initData();
		setContentView(R.layout.activity_news_content);
		Bundle data = getIntent().getExtras();
		if (data != null) {
			newId = data.getString(Constants.KEY_APP_ID);
			newtitle = data.getString(Constants.KEY_APP_NAME);
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
		// vScrollView = (VerticalScrollView)
		// findViewById(R.id.vertical_scrollview);
		// noDataView = (FrameLayout) findViewById(R.id.refresh_layout);
		// setView(3);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setVisibility(View.VISIBLE);
		navTitle.setText(Utils.checkEmpty(newtitle));
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		relateLayout = (LinearLayout) findViewById(R.id.relate_news_layout);
		relateLayout.setVisibility(View.GONE);
		progressBar = (ProgressBar) findViewById(R.id.news_progress);
		webView = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webView.getSettings();

		DisplayMetrics metric = new DisplayMetrics(); // 获取屏幕宽度高度
		getWindowManager().getDefaultDisplay().getMetrics(metric); // 获取屏幕宽度高度
		// webSettings.setLoadWithOverviewMode(true);// 设置webview加载的页面的模式
		// webSettings.setSupportZoom(true);// 实现缩放
		webSettings.setUseWideViewPort(true);// 设置webview推荐使用的窗口
		webSettings.setJavaScriptEnabled(true);// 设置支持JS
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		detector = new GestureDetector(this, new MyGestureListener());

		// newsFocus = (TextView) findViewById(R.id.news_focus);
		// newsSummary = (TextView) findViewById(R.id.news_summary);
		// newsTime = (TextView) findViewById(R.id.news_time);
		// newsTitle = (TextView) findViewById(R.id.news_title);
		// newsType = (TextView) findViewById(R.id.news_type);

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

	private void setNewsDetail(final NewsDetail detail) {
		if (detail != null) {
			if (detail.info != null) {
				final NewsInfoDetail infoDetail = JsonUtil.toObject(
						detail.info, NewsInfoDetail.class);
				if (infoDetail.content != null) {
					webView.loadDataWithBaseURL(
							"about:blank",
							infoDetail.content
									.replace("<img ",
											"<img style=\"max-width:100%;height:auto\""),
							"text/html", "utf-8", null);

					webView.setWebChromeClient(new WebChromeClient() {
						@Override
						public void onProgressChanged(WebView view,
								int newProgress) {
							progressBar.setProgress(newProgress);
							if (newProgress >= 100) {
								progressBar.setVisibility(View.GONE);
							}
						};
					});
				}
			}
		}
	}

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
}
