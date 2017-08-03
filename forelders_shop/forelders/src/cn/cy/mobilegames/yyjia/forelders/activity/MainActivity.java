package cn.cy.mobilegames.yyjia.forelders.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.SessionManager;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.SoftRecomAdapter;
import cn.cy.mobilegames.yyjia.forelders.app.YYJiaApp;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Request;
import cn.cy.mobilegames.yyjia.forelders.model.ClientUpdate;
import cn.cy.mobilegames.yyjia.forelders.model.InfoAndContent;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.DownloadSelectListener;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.FirstComPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.indicator.Indicator;
import com.jeremyfeinstein.slidingmenu.lib.indicator.IndicatorViewPager;
import com.umeng.analytics.MobclickAgent;

/**
 * 主界面--分页
 */
public class MainActivity extends FragmentActivity implements Observer, ApiRequestListener, DownloadSelectListener {

	private ImageView downBtn;
	private TextView searchEt;
	private MyAdapter adapter;
	private Indicator indicator;
	private ViewPager viewPager;
	private MainActivity activity;
	private ImageButton searchCode;
	private static ImageView menuBtn;
	private SoftRecomAdapter comAdapter;
	private IndicatorViewPager indicatorViewPager;

	private int num = 4;
	private Fragment[] instance;
	private static final int recomMsgWhat = 0x11;
	private Set<Long> mSelectedIds = new HashSet<Long>();
	private final static String SELECTION = "selection";
	private List<Object> softwareLists;

	private Session mSession;
	private LinearLayout backView;
	private static Context context;
	private MenuAdapter menuAdapter;
	private static SlidingMenu menu;
	private MenuPopupWindow menuPopupWindow;
	private static FirstComPopupWindow firstComWindow;
	private static DownloadManager mDownloadManager;
	private static ImageLoaderUtil mImageLoaderUtil;
	private Handler mHandler = new MyHandler(this);

	public Fragment[] getFragments() {
		synchronized (MainActivity.this) {
			if (instance == null) {
				Fragment[] fragment = new Fragment[4];
				fragment[0] = new TabQualityFragment();
				fragment[1] = new TabSoftFragment();
				fragment[2] = new TabGameFragment();
				fragment[3] = new UserCenterFragment();
				instance = fragment;
			}
		}
		return instance;
	}

	private OnClickListener popOnClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.first_com_close_iv:
				firstComWindow.dismiss();
				break;
			case R.id.first_com_no_alert_btn:
				// mSession.setFirstRecom(false);
				firstComWindow.dismiss();
				break;
			case R.id.first_com_one_btn_install_btn:
				new GetRecomTask().execute();
				// mSession.setFirstRecom(false);
				firstComWindow.dismiss();
				break;
			default:
				firstComWindow.dismiss();
				break;
			}
		}
	};

	private OnClickListener onClick = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.icon_delete:
				// doCode();
				break;
			case R.id.search_edit:
				doSearch();
				break;
			case R.id.logo_button:
				menu.toggle();
				break;
			case R.id.down_manage_btn:
				menuPopupWindow = new MenuPopupWindow(activity, menuAdapter);
				menuPopupWindow.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
				int[] location = new int[2];
				backView.getLocationOnScreen(location);
				menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY, location[0] + backView.getWidth(),
						location[1] + backView.getHeight());
				break;
			default:
				break;
			}
		}
	};

	private long[] getSelectionAsArray() {
		long[] selectedIds = new long[mSelectedIds.size()];
		Iterator<Long> iterator = mSelectedIds.iterator();
		for (int i = 0; i < selectedIds.length; i++) {
			selectedIds[i] = iterator.next();
		}
		return selectedIds;
	}

	private long[] getSelectionAsArray(int num) {
		long[] selectedIds = new long[num];
		for (int i = 0; i < num; i++) {
			selectedIds[i] = i;
		}
		return selectedIds;
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mSelectedIds.clear();
		for (long selectedId : savedInstanceState.getLongArray(SELECTION)) {
			mSelectedIds.add(selectedId);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLongArray(SELECTION, getSelectionAsArray());
	}

	private class MyHandler extends Handler {
		private final WeakReference<Activity> mActivity;

		public MyHandler(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case recomMsgWhat:
				if (mActivity.get() == null) {
					return;
				} else {
					showFirstRecomDialog(msg);
				}
				break;
			case Constants.P_MARKET_USERLOGO:
				if (mActivity.get() == null) {
					return;
				} else {
					mImageLoaderUtil.setImageNetResourceCor(menuBtn, mSession.getUserLogo(),
							R.drawable.admin_page_default_head);
				}
				break;
			case Constants.P_IS_LOGIN:
				if (mActivity.get() == null) {
					return;
				} else {
					if (msg.obj instanceof Boolean) {
						Boolean islogin = (Boolean) msg.obj;
						if (islogin) {
							mImageLoaderUtil.setImageNetResourceCor(menuBtn, mSession.getUserLogo(),
									R.drawable.admin_page_default_head);
						} else {
							menuBtn.setImageResource(R.drawable.admin_page_default_head);
						}
					}
				}
				break;

			default:
				break;
			}

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

	// private void doCode() {
	// Utils.toOtherClass(this, CaptureActivity.class);
	// }

	private void doSearch() {
		Utils.toOtherClass(this, SearchActivity.class);
	}

	private void initView() {
		final ImageView splashImg = (ImageView) findViewById(R.id.splash);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1f);
		alphaAnimation.setDuration(1500);
		splashImg.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation anima) {
			}

			@Override
			public void onAnimationRepeat(Animation anima) {

			}

			@Override
			public void onAnimationEnd(Animation anima) {
				// if (Utils.isNetworkAvailable(activity)) {
				// MarketAPI.checkSplash(activity, activity);
				// } else {
				splashImg.setVisibility(View.GONE);
				indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
				if (adapter == null) {
					adapter = new MyAdapter(getSupportFragmentManager());
				}
				indicatorViewPager.setAdapter(adapter);
				viewPager.setOffscreenPageLimit(4);
				// }
			}
		});

		backView = (LinearLayout) findViewById(R.id.search_view_title);
		searchCode = ((ImageButton) findViewById(R.id.icon_delete));
		searchEt = ((TextView) findViewById(R.id.search_edit));
		menuBtn = ((ImageView) findViewById(R.id.logo_button));
		downBtn = ((ImageView) findViewById(R.id.down_manage_btn));
		searchCode.setOnClickListener(onClick);
		searchEt.setOnClickListener(onClick);
		menuBtn.setOnClickListener(onClick);
		downBtn.setOnClickListener(onClick);

		refreshLogo();
		viewPager = ((ViewPager) findViewById(R.id.tabmain_viewPager));
		indicator = ((Indicator) findViewById(R.id.tabchild_indicator));
	}

	public void refreshLogo() {
		if (menuBtn != null) {
			if (mSession.isLogin()) {
				mImageLoaderUtil.setImageNetResourceCor(menuBtn, mSession.getUserLogo(),
						R.drawable.admin_page_default_head);
			} else {
				menuBtn.setImageResource(R.drawable.admin_page_default_head);
			}
		}
	}

	public static void toggleMenu() {
		if (menu != null) {
			menu.toggle();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tabmain);
		context = this;
		activity = this;
		mSession = Session.get(context);
		mSession.addObserver(this);
		mImageLoaderUtil = ImageLoaderUtil.getInstance(context);
		mDownloadManager = DownloadManager.getInstance(context);
		MarketAPI.getClientUpdate(activity, this);
		if (mSession.isFirstRecom()) {
			MarketAPI.firstRecom(activity, activity);
		}

		initData();
		initView();
		initMenu();
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new UserSettingFragment()).commit();
	}

	private void initMenu() {
		menu = new SlidingMenu(this);
		menu.setMode(SlidingMenu.LEFT);

		menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		menu.setBehindScrollScale(1f);
		menu.setBehindOffsetRes(R.dimen.kaka_60_dip);
		menu.setShadowWidth(R.dimen.space_small);
		menu.setFadeDegree(0.35f);
		menu.setFadeEnabled(true);
		menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
		menu.setMenu(R.layout.slide_menu_frame);
	}

	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i], Constants.menuLogos[i]));
		}
		menuAdapter = new MenuAdapter(mList, activity);
	}

	private class GetRecomTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			long[] ids = getSelectionAsArray();
			if (ids.length < 0) {
				ids = getSelectionAsArray(num);
			}

			if (softwareLists != null && softwareLists.size() > 0 && ids.length > 0) {
				for (int i = 0; i < ids.length; i++) {
					Softinfo sofList = (Softinfo) softwareLists.get(i);
					if (mDownloadManager.queryExists(sofList.getDownurl())) {
						continue;
					}

					Utils.deleteFile(mDownloadManager.queryLocalUriByAppid(sofList.getId()));
					DownloadManager.Request request = new Request(Uri.parse(sofList.getDownurl()));
					request.setDestinationInExternalPublicDir(Constants.ROOT_DIR, sofList.getName());
					request.setAllowedOverRoaming(false);

					request.setAppId(sofList.getId());
					request.setLogo(sofList.getLogo());
					request.setDescription(sofList.getBriefsummary());
					request.setShowRunningNotification(true);
					request.setVisibleInDownloadsUi(true);
					mDownloadManager.enqueue(request);
				}
			}
			return null;
		}
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setCurrentTab(int pos) {
		indicator.setCurrentItem(pos);
		viewPager.setCurrentItem(pos);
	}

	private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {
		private LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
		private int[] tabIcons = Constants.tabIcons;
		private String[] tabNames = Constants.tabNames;

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return tabNames.length;
		}

		@Override
		public Fragment getFragmentForPage(int page) {
			return getFragments()[page];
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.tab_main, container, false);

			}
			TextView textView = (TextView) convertView.findViewById(R.id.tab_name);
			textView.setText(tabNames[position]);
			textView.setCompoundDrawablesWithIntrinsicBounds(0, tabIcons[position], 0, 0);
			return convertView;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 双击返回键 进入首页
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (menu != null && menu.isMenuShowing()) {
			menu.toggle();
			return true;
		}

		// 双击返回键 提示并退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - currentTime < 1500) {
				YYJiaApp app = (YYJiaApp) getApplication();
				app.exit();
				Utils.finish(this);
			} else {
				ToastUtil.showShortToast(this, R.string.warn_exit_hint);
			}
			currentTime = System.currentTimeMillis();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private long currentTime;

	// handle a click on one of the download item checkboxes
	@Override
	public void onDownloadSelectionChanged(long downloadId, boolean isSelected) {
		if (isSelected) {
			mSelectedIds.add(downloadId);
		} else {
			mSelectedIds.remove(downloadId);
		}
	}

	@Override
	public boolean isDownloadSelected(long id) {
		return mSelectedIds.contains(id);
	}

	private void showFirstRecomDialog(Message msg) {
		firstComWindow = new FirstComPopupWindow(MainActivity.this, popOnClick, (SoftRecomAdapter) msg.obj);
		// 显示窗口 参数 第一个为menuWindow的父类Activity
		firstComWindow.showAtLocation(activity.findViewById(R.id.activity_tabmain),
				Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
		// mSession.setFirstRecom(false);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_FIRST_RECOM:
			if (obj != null && obj instanceof List<?>) {
				softwareLists = (List<Object>) obj;
				if (softwareLists != null && softwareLists.size() > 0) {
					comAdapter = new SoftRecomAdapter(context, softwareLists, MainActivity.this, num);
					int count = softwareLists.size() >= 4 ? 4 : softwareLists.size();
					for (int i = 0; i < count; i++) {
						comAdapter.setChecked(i, true);
					}
					mHandler.sendMessage(mHandler.obtainMessage(recomMsgWhat, comAdapter));
				}
			}

		case MarketAPI.ACTION_CLIENT_UPDATE:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent result = (InfoAndContent) obj;
				if (result.status == 1 && !TextUtils.isEmpty(result.content)) {
					ClientUpdate data = JsonUtil.toObject(result.content, ClientUpdate.class);
					if (data != null) {
						if (data.upversion > 0 && data.upversion > mSession.getVersionCode()) {
							Utils.showUpdateDialog(activity, data, mDownloadManager);
						}
					}
				}
			} else {
				// ToastUtil.showShortToast(context, R.string.lastet_version);
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
	public void update(Observable observable, Object data) {
		if (data != null && data instanceof Pair) {
			synchronized (MainActivity.class) {
				@SuppressWarnings("unchecked")
				Pair<String, Object> result = (Pair<String, Object>) data;
				if (result != null) {
					if (result.first.equals(SessionManager.P_MARKET_USERLOGO)) {
						mHandler.sendEmptyMessage(Constants.P_MARKET_USERLOGO);
					} else if (result.first.equals(SessionManager.P_IS_LOGIN)) {
						mHandler.sendMessage(mHandler.obtainMessage(Constants.P_IS_LOGIN, result.second));
					}
				}
			}
		}
	}

}
