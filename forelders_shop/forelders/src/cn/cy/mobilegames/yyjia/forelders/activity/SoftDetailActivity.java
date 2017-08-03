package cn.cy.mobilegames.yyjia.forelders.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.CommentAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.HotAppAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Request;
import cn.cy.mobilegames.yyjia.forelders.model.AppInfo;
import cn.cy.mobilegames.yyjia.forelders.model.AppPhote;
import cn.cy.mobilegames.yyjia.forelders.model.CommentList;
import cn.cy.mobilegames.yyjia.forelders.model.DownloadInfo;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.DialogUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.URLImageGetter;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.HorizontalListView;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.util.view.MyListView;
import cn.cy.mobilegames.yyjia.forelders.util.view.VerticalScrollView;
import cn.pedant.sweetalert.SweetAlertDialog;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 应用详情
 */
public class SoftDetailActivity extends BaseActivity implements Observer {
	private ImageView backBtn, softLogo,
	// shareBtn, favoriteBtn,
	// seeMoreComment,noCommentImg
			pauseBtn, continueBtn, deleteBtn;
	private TextView downBtn, softTitle, softName, softCount, softSize,
			versionTv, openCloseTv, shortSummary, summaryTv,
			// commentTitle,
			currentTv;
	private RelativeLayout menuLayout, moreCommentLayout, progressLayout;
	private FrameLayout noDataView;
	private Button doCommentBtn, downBottomBtn;
	private GridView favoriteGridView;
	private MyListView commentListView;
	private RatingBar ratingBar;
	private HorizontalListView horizontalListView;
	private VerticalScrollView scrollView;
	private ProgressBar downProgreebar;
	private LinearLayout bottomLayout;
	private MenuAdapter adapter;
	private MenuPopupWindow menuPopupWindow;
	private LinearLayout backView;

	private CommentAdapter commentAdapter;
	private MyHandler mHandler;

	private int mScreen;
	private String[] imgurls;
	private boolean isSuccess;
	private String appid = "";
	private String title = "";
	private String packageName;
	private boolean open = false;
	private final static int REQUEST_CODE = 2;
	private final static int REQUEST_OTHER_TO_COMMENT = 0x05;

	private Softinfo softInfo;
	private AppPhote appPhote;
	private SoftDetailActivity activity;
	private RelativeLayout.LayoutParams layoutParams;
	private boolean isViewFinish;
	private String downUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		mHandler = new MyHandler(activity);
		setContentView(R.layout.activity_soft_detail_view);
		try {
			Bundle data = getIntent().getExtras();
			if (data != null) {
				appid = data.getString(Constants.KEY_APP_ID);
				title = data.getString(Constants.KEY_APP_NAME);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		initView();
		initData();
		mSession.addObserver(this);
		MarketAPI.getAppInfo(activity, this, appid);
	}

	private void initView() {
		isViewFinish = false;
		backView = (LinearLayout) findViewById(R.id.back_title);
		noDataView = (FrameLayout) findViewById(R.id.refresh_layout);
		bottomLayout = (LinearLayout) findViewById(R.id.down_bottom_layout);
		scrollView = (VerticalScrollView) findViewById(R.id.vertical_scrollview);
		setView(1);

		ratingBar = (RatingBar) findViewById(R.id.soft_rating_bar);
		commentListView = (MyListView) findViewById(R.id.comment_listview);
		favoriteGridView = (GridView) findViewById(R.id.soft_detail_favorite);
		downBottomBtn = (Button) findViewById(R.id.down_bottom_btn);
		downBtn = (TextView) findViewById(R.id.soft_downbtn);
		doCommentBtn = (Button) findViewById(R.id.comment_btn);
		menuLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		moreCommentLayout = (RelativeLayout) findViewById(R.id.soft_comment_layout);
		progressLayout = (RelativeLayout) findViewById(R.id.down_progress_layout);
		softTitle = (TextView) findViewById(R.id.title);
		// commentTitle = (TextView) findViewById(R.id.soft_comment_title);
		currentTv = (TextView) findViewById(R.id.current_tv);
		downProgreebar = (ProgressBar) findViewById(R.id.down_progress);
		horizontalListView = (HorizontalListView) findViewById(R.id.horizontal_layout);

		menuLayout.setVisibility(View.VISIBLE);
		softTitle.setVisibility(View.VISIBLE);
		softTitle.setText(title);
		softName = (TextView) findViewById(R.id.soft_name);
		softCount = (TextView) findViewById(R.id.soft_count);
		softSize = (TextView) findViewById(R.id.soft_size);
		versionTv = (TextView) findViewById(R.id.version_text);
		openCloseTv = (TextView) findViewById(R.id.open_close_tv);
		shortSummary = (TextView) findViewById(R.id.soft_summary_short);
		summaryTv = (TextView) findViewById(R.id.summary_soft);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		softLogo = (ImageView) findViewById(R.id.soft_logo);
		// shareBtn = (ImageView) findViewById(R.id.down_share);
		// favoriteBtn = (ImageView) findViewById(R.id.down_favorite);
		// noCommentImg = (ImageView) findViewById(R.id.no_comment);
		pauseBtn = (ImageView) findViewById(R.id.down_pause);
		continueBtn = (ImageView) findViewById(R.id.down_continue);
		deleteBtn = (ImageView) findViewById(R.id.down_delete);
		// favoriteBtn = (ImageView) findViewById(R.id.down_favorite);
		// shareBtn = (ImageView) findViewById(R.id.down_share);
		//
		// shareBtn.setOnClickListener(onClick);
		// favoriteBtn.setOnClickListener(onClick);
		deleteBtn.setOnClickListener(onClick);
		continueBtn.setOnClickListener(onClick);
		pauseBtn.setOnClickListener(onClick);
		backBtn.setOnClickListener(onClick);
		openCloseTv.setOnClickListener(onClick);
		menuLayout.setOnClickListener(onClick);
		moreCommentLayout.setOnClickListener(onClick);
		doCommentBtn.setOnClickListener(onClick);
		downBtn.setOnClickListener(onClick);
		downBottomBtn.setOnClickListener(onClick);
		noDataView.setOnClickListener(onClick);
		bottomLayout.setOnClickListener(onClick);
		// shareBtn.setOnClickListener(onClick);
		// favoriteBtn.setOnClickListener(onClick);

		isViewFinish = true;
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.down_share:
				ToastUtil.showShortToast(context, "分享");
				break;
			case R.id.down_favorite:
				ToastUtil.showShortToast(context, "收藏");
				break;
			case R.id.refresh_layout:
				MarketAPI.getAppInfo(activity, activity, appid);
				break;
			case R.id.down_delete:
				cancelDown(Utils.checkEmpty(softInfo.getName()));
				break;
			case R.id.soft_downbtn:
				download();
				break;
			case R.id.down_bottom_btn:
				download();
				break;
			case R.id.down_bottom_layout:

				break;
			case R.id.down_continue:
				download();
				break;
			case R.id.down_pause:
				download();
				break;
			case R.id.soft_comment_layout:
				Bundle data = new Bundle();
				data.putString(Constants.KEY_APP_ID, softInfo.getId());
				data.putString(Constants.KEY_APP_NAME, softInfo.getName());
				startActivityForResult(new Intent(activity,
						CommentListActivity.class).putExtras(data),
						REQUEST_OTHER_TO_COMMENT);
				break;
			case R.id.back_btn:
				finish();
				break;
			case R.id.open_close_tv:
				if (open) {
					openCloseTv.setText(getResources().getString(
							R.string.open_text));
					summaryTv.setVisibility(View.GONE);
					shortSummary.setVisibility(View.VISIBLE);
					open = false;
				} else {
					openCloseTv.setText(getResources().getString(
							R.string.close_text));
					summaryTv.setVisibility(View.VISIBLE);
					shortSummary.setVisibility(View.GONE);
					open = true;
				}
				break;
			case R.id.menu_layout:
				menuPopupWindow = new MenuPopupWindow(activity, adapter);
				menuPopupWindow
						.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
				// menuPopupWindow.setBackgroundDrawable(new ColorDrawable(
				// Color.WHITE));
				int[] location = new int[2];
				backView.getLocationOnScreen(location);
				menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY,
						location[0] + backView.getWidth(), location[1]
								+ backView.getHeight());
				break;
			case R.id.comment_btn:
				if (mSession.isLogin()) {
					startActivityForResult(new Intent(activity,
							CommentActivity.class).putExtra(
							Constants.REQUEST_KEY_APPID, appid), REQUEST_CODE);
				} else {
					Utils.toOtherClass(activity, LoginActivity.class);
				}
				break;
			default:
				break;
			}
		}
	};

	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i],
					Constants.menuLogos[i]));
		}
		adapter = new MenuAdapter(mList, activity);
	}

	private class MyHandler extends Handler {
		private final WeakReference<Activity> mActivity;

		public MyHandler(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case Constants.P_DOWN_TASK:
				if (mActivity.get() == null) {
					return;
				} else {
					if (msg.obj instanceof HashMap) {
						@SuppressWarnings("unchecked")
						HashMap<String, DownloadInfo> mDownloadingTask = (HashMap<String, DownloadInfo>) msg.obj;
						setDownloadView(mDownloadingTask);
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

	// /**
	// * 初始化TopBar
	// */
	// private void initTopBar(SoftwareInfo product) {
	// HashMap<String, DownloadInfo> list = mSession.getDownloadingList();
	//
	// if (list.containsKey(product.sourceurl)) {
	// DownloadInfo info = list.get(product.sourceurl);
	// if (DownloadManager.isStatusInformational(info.mStatus)) {
	// // 正在下载
	// initAppInfo(info);
	// } else if (info.mStatus == DownloadManager.STATUS_SUCCESS) {
	// // 下载完成
	// Utils.setTvColorAndBg(activity, downBottomBtn,
	// R.string.download_install, R.color.white,
	// R.color.status_install);
	// Utils.setTvColorAndBg(activity, downBtn,
	// R.string.download_install, R.color.white,
	// R.color.status_install);
	// } else {
	// initAppInfo(product);
	// }
	// } else {
	// initAppInfo(product);
	// }
	// }

	/**
	 * 初始化应用的状态信息(更新/打开/安装)
	 */
	private void initAppInfo(Softinfo product, String packageName) {
		final TextView downBtn = (TextView) findViewById(R.id.soft_downbtn);
		final TextView downBottomBtn = (TextView) findViewById(R.id.down_bottom_btn);

		HashMap<String, DownloadInfo> mDownTask = mSession.getDownloadingList();
		if (mDownTask != null && !TextUtils.isEmpty(packageName)) {
			if (!mDownTask.containsKey(packageName)) {
				// 正常模式
				PackageManager pm = getPackageManager();
				PackageInfo info = null;
				try {
					info = pm.getPackageInfo(packageName, 0);
				} catch (NameNotFoundException e) {
					// do nothing
				}
				/**
				 * 手机上已安装同样的安装包(同样包名)
				 */
				if (info != null) {
					int versionCode = info.versionCode;
					Utils.D(" produce Versioncode  " + product.getVersionCode()
							+ " system code " + versionCode);
					if (Utils.checkInt(product.getVersionCode()) > versionCode) {
						// 有更新
						Utils.setTvColorAndBg(activity, downBtn,
								R.string.operation_update,
								R.color.status_update,
								R.drawable.status_updated);
						Utils.setTvColorAndBg(activity, downBottomBtn,
								R.string.operation_update,
								R.color.status_update,
								R.drawable.status_updated);
					} else {
						// 服务器版本 小于等于 手机上的版本 显示打开
						Utils.setTvColorAndBg(activity, downBtn,
								R.string.download_open, R.color.status_open,
								R.drawable.status_opened);
						Utils.setTvColorAndBg(activity, downBottomBtn,
								R.string.download_open, R.color.status_open,
								R.drawable.status_opened);
					}
				} else {
					/**
					 * 有包名字段,但手机上未下载
					 */
					// 未下载
					Utils.setTvColorAndBg(activity, downBtn,
							R.string.download_down, R.color.status_run,
							R.drawable.status_runed);
					Utils.setTvColorAndBg(activity, downBottomBtn,
							R.string.download_down, R.color.status_run,
							R.drawable.status_runed);
				}
			} else {
				setDownloadView(mDownTask);
			}
		} else {
			/**
			 * 没有下载记录,且没有包名信息
			 */
			// 未下载
			Utils.setTvColorAndBg(activity, downBtn, R.string.download_down,
					R.color.status_run, R.drawable.status_runed);
			Utils.setTvColorAndBg(activity, downBottomBtn,
					R.string.download_down, R.color.status_run,
					R.drawable.status_runed);
		}

	}

	public void download() {
		HashMap<String, DownloadInfo> list = mSession.getDownloadingList();
		ArrayList<String> installApps = mSession.getInstalledApps();
		HashMap<String, UpgradeInfo> updateApps = mSession.getUpdateList();
		downloadId = mDownloadManager.queryIdByAppId(appid);
		String path = mDownloadManager.queryLocalUriByAppid(appid);

		if (packageName != null && packageName != "") {
			// 下载任务中
			if (list.containsKey(packageName)) {
				DownloadInfo info = list.get(packageName);
				if (info != null) {
					switch (info.mStatus) {
					// STATUS_SUCCESS
					case DownloadManager.STATUS_SUCCESS:
						Utils.installApk(activity, info.mFilePath, appid,
								downloadId);
						break;
					// STATUS_PAUSED
					case DownloadManager.STATUS_PAUSED:
						if (downloadId > 0) {
							mDownloadManager.resumeDownload(downloadId);
						}
						break;
					// STATUS_RUNNING
					case DownloadManager.STATUS_RUNNING:
					case DownloadManager.STATUS_PENDING:
						if (downloadId > 0) {
							mDownloadManager.pauseDownload(downloadId);
						}
						break;
					case DownloadManager.STATUS_FAILED:
						Utils.deleteFile(path);
						mDownloadManager.restartDownload(downloadId);
						break;
					case DownloadManager.STATUS_WAITED:

						break;
					case DownloadManager.STATUS_INSTALLED:
						Utils.startAPP(activity, packageName);
						break;
					case DownloadManager.STATUS_CANCELED:
						initDownTask(softInfo, packageName);
						break;
					default:
						break;
					}
				}
				return;
			}

			if (installApps.contains(packageName)) {
				if (updateApps.containsKey(packageName)) {
					initDownTask(softInfo, packageName);
					return;
				}

				if (installApps.contains(packageName)) {
					Utils.startAPP(activity, packageName);
					return;
				}
			} else {
				initDownTask(softInfo, packageName);
			}
		} else {
			initDownTask(softInfo, null);
		}
	};

	private void initDownTask(final Softinfo softInfo, final String packageName) {
		if (softInfo != null) {
			String downUrlServer = softInfo.getDownurl();
			if (downUrlServer != null && downUrlServer.length() > 0
					&& downUrlServer.startsWith("http")) {
				Uri srcUri = Uri.parse(downUrlServer);
				String package_name = packageName == null ? softInfo
						.getSourceurl() : packageName;

				DownloadManager.Request request = new Request(srcUri);
				request.setDestinationInExternalPublicDir(Constants.ROOT_DIR,
						softInfo.getName());
				request.setTitle(softInfo.getName());
				request.setDescription(softInfo.getBriefsummary());
				request.setShowRunningNotification(true);
				request.setVisibleInDownloadsUi(true);
				request.setAppId(softInfo.getId());
				request.setLogo(softInfo.getLogo());
				request.setPackageName(package_name);
				request.setVersionCode(Utils.checkInt(softInfo.getVersionCode()));
				request.setVesionName(softInfo.getVersion());
				downloadId = mDownloadManager.enqueue(request);
				apkFilePath = mDownloadManager.queryLocalUri(downloadId);
				ToastUtil.showShortToast(context, R.string.download_start);
			} else {
				ToastUtil.showShortToast(context, "下载地址无效,请检查.");
				return;
			}
		}
	}

	private void cancelDown(String appname) {
		if (mDownloadManager.isStatusRunning(downloadId)) {
			mDownloadManager.pauseDownload(downloadId);
		}
		DialogUtil.showUserDefineDialog(context, Constants.CANCEL_TASK,
				Constants.CANCEL_SURE + appname + Constants.QUOTATION_MARK,
				R.drawable.app_icon, Constants.CANCEL_TEXT,
				Constants.SURE_TEXT,
				new SweetAlertDialog.OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog tDialog) {
						if (mDownloadManager.isStatusPaused(downloadId)) {
							mDownloadManager.resumeDownload(downloadId);
						}
						tDialog.dismissWithAnimation();
					}
				}, new SweetAlertDialog.OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog tDialog) {
						mDownloadManager.remove(downloadId);
						mDownloadManager.markRowDeleted(downloadId);
						if (!TextUtils.isEmpty(apkFilePath)) {
							File file = new File(apkFilePath);
							if (file.exists()) {
								file.delete();
							}
						}
						setInitialView();
						tDialog.dismissWithAnimation();
					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CODE:
		case REQUEST_OTHER_TO_COMMENT:
			if (resultCode == Activity.RESULT_OK)
				isSuccess = data.getBooleanExtra(Constants.REQUEST_KEY_AC,
						false);
			if (isSuccess) {
				MarketAPI.getCommentList(activity, activity, appid);
			}
			break;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(Constants.KEY_OPEN, open);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		open = savedInstanceState.getBoolean(Constants.KEY_OPEN);
	}

	private class ViewHold {
		private ImageView image;
	}

	/**
	 * code 1:都不显示 2:显示scrollView 3:显示noData
	 * 
	 * @param code
	 */
	private void setView(int code) {
		switch (code) {
		case 1:
			noDataView.setVisibility(View.GONE);
			scrollView.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
			break;
		case 2:
			noDataView.setVisibility(View.GONE);
			scrollView.setVisibility(View.VISIBLE);
			bottomLayout.setVisibility(View.VISIBLE);
			break;
		case 3:
			noDataView.setVisibility(View.VISIBLE);
			scrollView.setVisibility(View.GONE);
			bottomLayout.setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_APP_INFO:
			if (obj != null && obj instanceof AppInfo) {
				AppInfo appInfo = (AppInfo) obj;
				setAppInfoView(appInfo);
			}
			break;
		case MarketAPI.ACTION_GET_COMMENT_LIST:
			if (obj != null && obj instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<CommentList> comList = (List<CommentList>) obj;
				setCommentList(comList);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {

	}

	private void setAppInfoView(AppInfo appInfo) {
		if (appInfo != null) {
			setView(2);
			List<CommentList> commentList = JsonUtil.toObjectList(
					appInfo.commentlist, CommentList.class);
			List<Softinfo> favorites = JsonUtil.toObjectList(appInfo.lovelist,
					Softinfo.class);
			softInfo = JsonUtil.toObject(appInfo.list, Softinfo.class);
			if (softInfo != null) {
				String name = mDownloadManager.queryPackageName(appid);
				packageName = TextUtils.isEmpty(name) ? softInfo.getSourceurl()
						: name;
				initAppInfo(softInfo, packageName);
				// initTopBar(softInfo);
				softName.setText(Utils.checkEmpty(softInfo.getName()));
				softCount.setText(Utils.parseDownCount(Utils
						.checkValue(softInfo.getDownloadcount()))
						+ Constants.COUNT_DOWNLOAD);
				softSize.setText(Constants.SEPARATE_MARK
						+ Utils.checkEmpty(softInfo.getSize()));
				ratingBar.setMax(50);
				ratingBar.setProgress(Utils.checkInt(softInfo.getStar()));
				ratingBar.setVisibility(View.VISIBLE);

				shortSummary
						.setMovementMethod(LinkMovementMethod.getInstance());// 加这句才能让里面的超链接生效
				summaryTv.setMovementMethod(LinkMovementMethod.getInstance());// 加这句才能让里面的超链接生效
				URLImageGetter urlImageGetter = new URLImageGetter(context,
						summaryTv);
				summaryTv.setText(Html.fromHtml(
						Utils.checkEmpty(softInfo.getSummary()),
						urlImageGetter, null));
				shortSummary.setText(Html.fromHtml(
						Utils.checkEmpty(softInfo.getBriefsummary()),
						urlImageGetter, null));

				versionTv.setText(Constants.VERSION_CODE_CN
						+ Utils.checkEmpty(softInfo.getVersion()));
				imageLoader.setImageNetResourceCor(softLogo,
						softInfo.getLogo(), R.drawable.img_default);
				mScreen = BaseTools.getWindowsWidth(activity);

				if (!TextUtils.isEmpty(softInfo.getImgurl())) {
					imgurls = softInfo.getImgurl().split("@@@");

					appPhote = new AppPhote();
					appPhote.setPhoteUrls(imgurls);
					switch (imgurls.length) {
					case 1:
						layoutParams = new RelativeLayout.LayoutParams(mScreen,
								LayoutParams.MATCH_PARENT);
						break;
					case 2:
						layoutParams = new RelativeLayout.LayoutParams(
								mScreen / 2, LayoutParams.MATCH_PARENT);
						break;
					default:
						layoutParams = new RelativeLayout.LayoutParams(
								mScreen / 3, LayoutParams.MATCH_PARENT);
						break;
					}
					horizontalListView.setAdapter(new BaseAdapter() {

						@SuppressLint("InflateParams")
						@Override
						public View getView(final int position,
								View convertView, ViewGroup parent) {
							View view = convertView;
							ViewHold hold = null;
							if (convertView == null) {
								hold = new ViewHold();
								view = LayoutInflater.from(activity).inflate(
										R.layout.screen_shot_view, null);
								hold.image = (ImageView) view
										.findViewById(R.id.screen_img);
								view.setTag(hold);
							} else {
								hold = (ViewHold) view.getTag();
							}

							hold.image.setLayoutParams(layoutParams);
							imageLoader.setImageNetResource(hold.image, Utils
									.checkUrlContainHttp(
											Constants.URL_BASE_HOST,
											imgurls[position]),
									R.drawable.default_img);

							hold.image
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											appPhote.setPosition(position);
											startActivity(new Intent(activity,
													AppScreenShotActivity.class)
													.putExtra(
															Constants.KEY_PHOTE,
															appPhote));
										}
									});

							return view;
						}

						@Override
						public long getItemId(int position) {

							return position;
						}

						@Override
						public Object getItem(int position) {

							return imgurls[position];
						}

						@Override
						public int getCount() {

							return imgurls == null ? 0 : imgurls.length;
						}
					});
					horizontalListView.setVisibility(View.VISIBLE);
				} else {
					horizontalListView.setVisibility(View.GONE);
				}

				setCommentList(commentList);

				favoriteGridView.setAdapter(new HotAppAdapter(context,
						favorites, imageLoader, favoriteGridView));
				favoriteGridView
						.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> parent,
									View convertView, int position, long id) {
								Softinfo sof = (Softinfo) favoriteGridView
										.getItemAtPosition(position);
								Bundle data = new Bundle();
								data.putString(Constants.KEY_APP_ID,
										sof.getId());
								data.putString(Constants.KEY_APP_NAME,
										sof.getName());
								Utils.toOtherClass(activity,
										SoftDetailOtherActivity.class, data);
							}
						});

			}

		}
	}

	private void setCommentList(List<CommentList> comList) {
		if (comList.size() == 0) {
			commentListView.setVisibility(View.GONE);
		} else {
			commentListView.setVisibility(View.VISIBLE);
			commentAdapter = new CommentAdapter(comList, context, false, 4);
			commentListView.setAdapter(commentAdapter);
		}
	}

	@Override
	public void update(Observable observable, Object obj) {
		mHandler.sendMessage(mHandler.obtainMessage(Constants.P_DOWN_TASK, obj));
	}

	public void setDownloadView(HashMap<String, DownloadInfo> mDownloadingTask) {
		if (!mDownloadingTask.containsKey(packageName)) {
			if (isViewFinish) {
				setInitialView();
			}
			return;
		}

		DownloadInfo info = mDownloadingTask.get(packageName);
		if (info != null) {
			initAppInfo(info);
		}
	}

	@SuppressLint("ResourceAsColor")
	private void initAppInfo(DownloadInfo info) {
		if (isViewFinish) {
			switch (info.mStatus) {
			case DownloadManager.STATUS_SUCCESS:
				Utils.setTvColorAndBg(activity, downBtn, R.string.install,
						R.color.status_install, R.drawable.status_installed);
				Utils.setTvColorAndBg(activity, downBottomBtn,
						R.string.install, R.color.status_install,
						R.drawable.status_installed);
				downBottomBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.GONE);
				continueBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
				progressLayout.setVisibility(View.GONE);
				break;
			case DownloadManager.STATUS_PAUSED:
				Utils.setTvColorAndBg(activity, downBtn,
						R.string.download_resume, R.color.status_wait,
						R.drawable.status_waited);
				Utils.setTvColorAndBg(activity, downBottomBtn,
						R.string.download_resume, R.color.status_wait,
						R.drawable.status_waited);
				currentTv.setText(info.mProgress);
				downProgreebar.setProgress(info.mProgressNumber);
				continueBtn.setVisibility(View.VISIBLE);
				deleteBtn.setVisibility(View.VISIBLE);
				progressLayout.setVisibility(View.VISIBLE);
				downBottomBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.GONE);
				break;
			case DownloadManager.STATUS_WAITED:
				Utils.setTvColorAndBg(activity, downBtn,
						R.string.download_queue, R.color.status_wait,
						R.drawable.status_waited);
				Utils.setTvColorAndBg(activity, downBottomBtn,
						R.string.download_queue, R.color.status_wait,
						R.drawable.status_waited);
				currentTv.setText(R.string.download_queue);
				downProgreebar.setProgress(info.mProgressNumber);
				continueBtn.setVisibility(View.VISIBLE);
				deleteBtn.setVisibility(View.VISIBLE);
				progressLayout.setVisibility(View.VISIBLE);
				downBottomBtn.setVisibility(View.GONE);
				pauseBtn.setVisibility(View.GONE);
				break;
			case DownloadManager.STATUS_PENDING:
			case DownloadManager.STATUS_RUNNING:
				Utils.setTvColorAndBg(activity, downBtn, info.mProgress,
						R.color.status_run, R.drawable.status_runed);
				Utils.setTvColorAndBg(activity, downBottomBtn, info.mProgress,
						R.color.status_run, R.drawable.status_runed);
				currentTv.setText(info.mProgress);
				downProgreebar.setProgress(info.mProgressNumber);
				pauseBtn.setVisibility(View.VISIBLE);
				deleteBtn.setVisibility(View.VISIBLE);
				progressLayout.setVisibility(View.VISIBLE);
				downBottomBtn.setVisibility(View.GONE);
				continueBtn.setVisibility(View.GONE);
				break;
			case DownloadManager.STATUS_FAILED:
				Utils.setTvColorAndBg(activity, downBtn,
						R.string.download_status_failure,
						R.color.status_failure, R.drawable.status_failured);
				Utils.setTvColorAndBg(activity, downBottomBtn,
						R.string.download_status_failure,
						R.color.status_failure, R.drawable.status_failured);
				downBottomBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.GONE);
				continueBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
				progressLayout.setVisibility(View.GONE);
				break;
			case DownloadManager.STATUS_INSTALLED:
				Utils.setTvColorAndBg(activity, downBtn,
						R.string.download_status_installed,
						R.color.status_install, R.drawable.status_installed);
				Utils.setTvColorAndBg(activity, downBottomBtn,
						R.string.download_status_installed,
						R.color.status_install, R.drawable.status_installed);
				downBottomBtn.setVisibility(View.VISIBLE);
				pauseBtn.setVisibility(View.GONE);
				continueBtn.setVisibility(View.GONE);
				deleteBtn.setVisibility(View.GONE);
				progressLayout.setVisibility(View.GONE);
				break;
			default:
				setInitialView();
				break;
			}
		}
	}

	private void setInitialView() {
		initAppInfo(softInfo, packageName);
		downBottomBtn.setVisibility(View.VISIBLE);
		progressLayout.setVisibility(View.GONE);
		pauseBtn.setVisibility(View.GONE);
		continueBtn.setVisibility(View.GONE);
		deleteBtn.setVisibility(View.GONE);
	}
}
