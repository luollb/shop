package cn.cy.mobilegames.yyjia.forelders.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Pair;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.SessionManager;
import cn.cy.mobilegames.yyjia.forelders.model.ClientUpdate;
import cn.cy.mobilegames.yyjia.forelders.model.InfoAndContent;
import cn.cy.mobilegames.yyjia.forelders.model.Logo;
import cn.cy.mobilegames.yyjia.forelders.model.LostPassWd;
import cn.cy.mobilegames.yyjia.forelders.util.Base64Coder;
import cn.cy.mobilegames.yyjia.forelders.util.DialogUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonMananger;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.MD5Util;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.SelectPicPopupWindow;
import cn.pedant.sweetalert.SweetAlertDialog;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;
import com.zcw.togglebutton.ToggleButton;
import com.zcw.togglebutton.ToggleButton.OnToggleChanged;

/**
 * 用户设置界面
 * 
 * @author Administrator
 * 
 */
public class UserSettingFragment extends BaseFragment implements Observer {
	private ImageView userLogo, showPicMore, clientMore;
	private Button logoutBtn;
	private ToggleButton showPicBtn, clientUpdateBtn, necessaryRecomBtn;
	private Activity activity;
	private TextView userName, showPic, clearImgCache, clearCache, setClient,
			clientUpdate, clientUpdateCon, necessaryRecom;
	private RelativeLayout showPicLayout, clearImgCacheLayout,
			clearCacheLayout, clientUpdateMode, checkClientUpdate,
			necessaryRecomLayout;
	private SelectPicPopupWindow menuWindow;
	/* 用来标识请求照相功能的activity */
	private static final int PHOTO_SELECTED_FROM_CAMERA = 2023;
	/* 用来标识请求系统头像的activity */
	private static final int PHOTO_SELECTED_FROM_DEFAULT = 2025;
	/**
	 * 4.4及以上从相册选择照片
	 */
	private static final int SELECT_A_PICTURE_AFTER_KIKAT = 0x30;
	/**
	 * 4.4以下从相册选照片并剪切
	 */
	private static final int SELECT_A_PICTURE_BEFORE_KIKAT = 0x31;
	/**
	 * 4.4及以上选取照片后剪切方法
	 */
	private static final int SET_ALBUM_PICTURE_KITKAT = 0x32;
	/**
	 * 对拍照的图片剪切
	 */
	private static final int SET_PICTURE_FORM_CAMERA = 0x33;

	// /* 用来标识请求照相功能的activity */
	// private static final int PHOTO_SELECTED_FROM_CAMERA = 2023;
	// /* 用来标识请求gallery的activity */
	// private static final int PHOTO_SELECTED_FROM_GALLERY = 2021;
	// /* 用来标识请求系统头像的activity */
	// private static final int PHOTO_SELECTED_FROM_DEFAULT = 2025;
	/* 拍照的照片存储位置 */
	private static final File PHOTO_DIR = new File(
			Environment.getExternalStorageDirectory() + "/"
					+ Constants.ROOT_DIR + "/Camera");
	private File mCurrentPhotoFile;// 照相机拍照得到的图片
	private MyHandler mHandler;
	private View view;
	private Session mSession;
	private boolean viewFinish;
	private final static int LOAD_SUCCESS = 0x19, LOAD_FAILURE = 0x20;
	private boolean mIsKitKat;
	private UserSettingFragment fragment;
	private String mAlbumPicturePath;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_user_setting, null);
			initView(view);
		}

		// 缓存的rootView需要判断是否已经被加过parent，如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIsKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
		fragment = UserSettingFragment.this;
		activity = getActivity();
		mSession = Session.get(activity);
		mSession.addObserver(this);
		mHandler = new MyHandler(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageEnd(getClass().getName());
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageStart(getClass().getName());
	}

	private void initView(View view) {
		viewFinish = false;
		logoutBtn = (Button) view.findViewById(R.id.log_out_btn);
		userLogo = (ImageView) view.findViewById(R.id.user_logo);
		userName = (TextView) view.findViewById(R.id.user_name);
		checkClientUpdate = (RelativeLayout) view
				.findViewById(R.id.user_check_client_update);
		necessaryRecomLayout = (RelativeLayout) view
				.findViewById(R.id.necessary_recom);
		showPicLayout = (RelativeLayout) view
				.findViewById(R.id.user_set_showpic);
		clearImgCacheLayout = (RelativeLayout) view
				.findViewById(R.id.user_clear_img);

		necessaryRecom = (TextView) necessaryRecomLayout
				.findViewById(R.id.setting_title);
		necessaryRecomBtn = (ToggleButton) necessaryRecomLayout
				.findViewById(R.id.toggleButton);
		necessaryRecomBtn.setVisibility(View.VISIBLE);
		clientUpdate = (TextView) checkClientUpdate
				.findViewById(R.id.setting_title);
		clientUpdateCon = (TextView) checkClientUpdate
				.findViewById(R.id.nav_content);

		clearCacheLayout = (RelativeLayout) view
				.findViewById(R.id.user_clear_cache);
		clientUpdateMode = (RelativeLayout) view
				.findViewById(R.id.user_set_client_update);

		showPic = (TextView) showPicLayout.findViewById(R.id.setting_title);
		setClient = (TextView) clientUpdateMode
				.findViewById(R.id.setting_title);
		clearImgCache = (TextView) clearImgCacheLayout
				.findViewById(R.id.setting_title);
		clearCache = (TextView) clearCacheLayout
				.findViewById(R.id.setting_title);
		showPicBtn = (ToggleButton) showPicLayout
				.findViewById(R.id.toggleButton);
		clientUpdateBtn = (ToggleButton) clientUpdateMode
				.findViewById(R.id.toggleButton);
		showPicMore = (ImageView) showPicLayout.findViewById(R.id.setting_more);
		clientMore = (ImageView) clientUpdateMode
				.findViewById(R.id.setting_more);

		showPic.setText("显示图片");
		setClient.setText("客户端升级模式");
		clearImgCache.setText("清除图片缓存");
		clearCache.setText("清除内容缓存");
		clientUpdate.setText("检查更新");
		necessaryRecom.setText("必备软件推荐");

		try {
			clientUpdateCon.setText("当前版本:"
					+ getActivity().getPackageManager().getPackageInfo(
							getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			clientUpdateCon.setText("当前版本:");
			e.printStackTrace();
		}

		logoutBtn.setOnClickListener(onClick);
		userLogo.setOnClickListener(onClick);
		clearImgCacheLayout.setOnClickListener(onClick);
		clearCacheLayout.setOnClickListener(onClick);
		checkClientUpdate.setOnClickListener(onClick);

		clientMore.setVisibility(View.GONE);
		showPicMore.setVisibility(View.GONE);
		showPicBtn.setVisibility(View.VISIBLE);
		clientUpdateBtn.setVisibility(View.VISIBLE);

		showPicBtn.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				mSession.setShowImage(on);
			}
		});

		necessaryRecomBtn.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				mSession.setFirstRecom(on);
			}
		});

		clientUpdateBtn.setOnToggleChanged(new OnToggleChanged() {

			@Override
			public void onToggle(boolean on) {
				mSession.setUpdateAvailable(on);
			}
		});
		viewFinish = true;
		doLoginView();
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.log_out_btn:
				doLogout();
				break;
			case R.id.user_clear_img:
				doClearImgCache();
				break;
			case R.id.user_clear_cache:
				doClearCache();
				break;
			case R.id.user_check_client_update:
				queryUpdate();
				break;
			case R.id.user_logo:
				if (mSession.isLogin()) {
					// 实例化SelectPicPopupWindow
					menuWindow = new SelectPicPopupWindow(activity,
							itemsOnClick);
					// 显示窗口 参数 第一个为menuWindow的父类Activity
					menuWindow.showAtLocation(
							activity.findViewById(R.id.user_setting),
							Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				} else {
					Utils.toOtherClass(activity, LoginActivity.class);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * menuWindow的按键监听事件
	 */
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_cancel_dialog:
				menuWindow.dismiss();
				break;
			case R.id.btn_pick_photo_from_album:
				// pick photo
				doPickPhotoFromGallery();
				menuWindow.dismiss();
				break;
			case R.id.btn_pick_photo_from_default:
				// pick photo
				startActivityForResult(new Intent(activity,
						ChangePictureActivity.class),
						PHOTO_SELECTED_FROM_DEFAULT);
				Utils.overridePendingTransition(activity);
				menuWindow.dismiss();
				break;
			case R.id.btn_take_photo:
				// do phote and save/cancel the phote
				String status = Environment.getExternalStorageState();
				if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
					doTakePhoto();// 用户点击了从照相机获取
				} else {
					ToastUtil.showLongToast(activity,
							Constants.NO_SDCARD_AND_CHECK);
				}
				menuWindow.dismiss();
				break;
			default:
				menuWindow.dismiss();
				break;
			}

		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case SELECT_A_PICTURE_BEFORE_KIKAT:
			uploadPhoto(data);
			break;
		case SELECT_A_PICTURE_AFTER_KIKAT:
			if (null != data) {
				mAlbumPicturePath = Utils.getPath(context, data.getData());
				Utils.cropImageUriAfterKikat(
						Uri.fromFile(new File(mAlbumPicturePath)),
						SET_ALBUM_PICTURE_KITKAT, fragment);
			} else {
				Utils.cropImageUriAfterKikat(Uri.fromFile(mCurrentPhotoFile),
						SET_ALBUM_PICTURE_KITKAT, fragment);
			}
			break;
		case SET_ALBUM_PICTURE_KITKAT: // 调用相册返回的
			uploadPhoto(data);
			break;
		case SET_PICTURE_FORM_CAMERA: // 调用拍照返回的
			uploadPhoto(data);
			break;
		case PHOTO_SELECTED_FROM_CAMERA: // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
			if (null != data) {
				mAlbumPicturePath = Utils.getPath(context, data.getData());
				Utils.cropImageUriAfterKikat(
						Uri.fromFile(new File(mAlbumPicturePath)),
						SET_ALBUM_PICTURE_KITKAT, fragment);
			} else {
				Utils.cropImageUriAfterKikat(Uri.fromFile(mCurrentPhotoFile),
						SET_ALBUM_PICTURE_KITKAT, fragment);
			}
			// doCropPhoto(mCurrentPhotoFile, SET_PICTURE_FORM_CAMERA);
			break;
		case PHOTO_SELECTED_FROM_DEFAULT:
			// 从系统默认选中头像
			try {
				int resId = data.getExtras().getInt(Constants.SYSTEM_PICTURE,
						-1);
				if (resId >= 0) {
					Bitmap picture = Utils.readBitMap(activity, resId);
					uploadlogo(picture);
				} else {
					ToastUtil.showLongToast(activity,
							Constants.ERROR_RETURN_PHOTE);
				}
			} catch (Exception e) {
				e.printStackTrace();
				ToastUtil.showLongToast(activity, Constants.ERROR_RETURN_PHOTE);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void uploadPhoto(Intent data) {
		Bitmap photo = null;
		try {
			photo = data.getParcelableExtra("data");
		} catch (Exception e1) {
			e1.printStackTrace();
			ToastUtil.showLongToast(activity, Constants.ERROR_RETURN_DATA);
		}

		if (photo != null) {
			// 下面就是显示照片了
			// 缓存用户选择的图片 保存照片uri到服务器
			uploadlogo(photo);
		} else {
			ToastUtil.showLongToast(activity, Constants.ERROR_RETURN_PHOTE);
		}
	}

	protected void doClearImgCache() {
		imageLoader.clearDiskCache();
		imageLoader.clearMemoryCache();
		ToastUtil.showShortToast(activity, "清除图片缓存成功");
	}

	protected void doClearCache() {
		cacheManager.clear();
		ToastUtil.showShortToast(activity, "清除内容缓存成功");
	}

	/**
	 * 客户端升级
	 */
	private void queryUpdate() {
		if (!mSession.isUpdateAvailable()) {
			ToastUtil.showShortToast(activity, "当前模式为忽略模式,请更改为升级模式.");
		} else {
			MarketAPI.getClientUpdate(activity, UserSettingFragment.this);
		}
	}

	/**
	 * take photo(拍照)
	 */
	private void doTakePhoto() {
		try {
			// Launch camera to take photo for selected contact
			if (!PHOTO_DIR.exists()) {
				PHOTO_DIR.mkdirs();// 创建照片的存储目录
			}
			mCurrentPhotoFile = new File(PHOTO_DIR, Utils.getPhotoFileName());//
			// 给新照的照片文件命名
			final Intent intent = Utils.getTakePickIntent(mCurrentPhotoFile);
			startActivityForResult(intent, PHOTO_SELECTED_FROM_CAMERA);
			Utils.overridePendingTransition(activity);
		} catch (ActivityNotFoundException e) {
			notFindPicture();
		}
	}

	// 请求Gallery程序
	private void doPickPhotoFromGallery() {
		try {
			// Launch picker to choose photo for selected contact
			if (mIsKitKat) {
				Utils.selectImageUriAfterKikat(SELECT_A_PICTURE_AFTER_KIKAT,
						fragment);
			} else {
				// Launch picker to choose photo for selected contact
				final Intent intent = Utils.getPhotoPickIntent(true);
				this.startActivityForResult(intent,
						SELECT_A_PICTURE_BEFORE_KIKAT);
				Utils.overridePendingTransition(activity);
			}
		} catch (ActivityNotFoundException e) {
			notFindPicture();
		}
	}

	private void notFindPicture() {
		ToastUtil.showShortToast(activity, Constants.NOT_FIND_PICTURE);
	}

	/**
	 * do logout
	 */
	private void doLogout() {
		DialogUtil.showDefaultDialog(getActivity(), Constants.LOGOUT_CN,
				R.string.logout_attention, R.drawable.app_icon,
				new SweetAlertDialog.OnSweetClickListener() {

					@Override
					public void onClick(final SweetAlertDialog tDialog) {
						tDialog.dismiss();
						mSession.setLogin(false);
						doLoginView();
						ToastUtil.showShortToast(activity,
								R.string.login_out_success);
					}
				});
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
	 * 剪辑照片
	 * 
	 * @param f
	 */
	private void doCropPhoto(File f, int requestCode) {
		try {
			// 启动gallery去剪辑这个照片
			Utils.cropImageUri(Uri.fromFile(f), 80, 80, requestCode, this);
		} catch (Exception e) {
			notFindPicture();
		}
	}

	/**
	 * 上传用户头像
	 */
	private void uploadlogo(final Bitmap bitmap) {
		MarketAPI.uploadLogo(activity, fragment, mSession.getUserId(), MD5Util
				.stringToMD5(mSession.getUserId()
						+ Constants.REQUEST_KEY_SIGN_NUM), new String(
				Base64Coder.encodeLines(Utils.Bitmap2Bytes(bitmap))));
		ToastUtil.showLongToast(activity, Constants.UPLOAD_LOADING);
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
	}

	@Override
	public boolean getUserVisibleHint() {
		return super.getUserVisibleHint();
	}

	private void doLoginView() {
		if (viewFinish) {
			if (mSession.isLogin()) {
				logoutBtn.setVisibility(View.VISIBLE);
			} else {
				logoutBtn.setVisibility(View.GONE);
			}

			if (mSession.isShowImage()) {
				showPicBtn.toggleOn();
			} else {
				showPicBtn.toggleOff();
			}

			if (mSession.isUpdateAvailable()) {
				clientUpdateBtn.toggleOn();
			} else {
				clientUpdateBtn.toggleOff();
			}

			if (mSession.isFirstRecom()) {
				necessaryRecomBtn.toggleOn();
			} else {
				necessaryRecomBtn.toggleOff();
			}

			if (mSession.isUpdateAvailable()) {
				clientUpdateBtn.toggleOn();
			} else {
				clientUpdateBtn.toggleOff();
			}

			if (mSession.isLogin()) {
				imageLoader.setImageNetResourceCor(userLogo,
						mSession.getUserLogo(),
						R.drawable.admin_page_default_head);
			} else {
				userLogo.setImageResource(R.drawable.admin_page_default_head);
			}

			userName.setText(mSession.isLogin() ? "您好,"
					+ mSession.getUserName() : "点击头像登录/注册");
		}
	}

	private void toast(String toast) {
		ToastUtil.showShortToast(activity, toast);
	}

	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {

		private WeakReference<Activity> mActivity;

		public MyHandler(Activity activity) {
			mActivity = new WeakReference<Activity>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case LOAD_SUCCESS:
				if (mActivity.get() == null) {
					return;
				} else {
					String logoUrl = msg.obj.toString();
					imageLoader.clearUrlCache(logoUrl);
					mSession.setUserLogo(logoUrl);
					imageLoader.setImageNetResourceCor(userLogo,
							mSession.getUserLogo(),
							R.drawable.admin_page_default_head);
					ToastUtil
							.showShortToast(activity, Constants.UPLOAD_SUCCESS);
					imageLoader.setImageNetResourceCor(userLogo, Utils
							.checkUrlContainHttp(Constants.URL_BASE_HOST,
									logoUrl),
							R.drawable.admin_page_default_head);
				}
				break;
			case LOAD_FAILURE:
				if (mActivity.get() == null) {
					return;
				} else {
					toast(Constants.UPLOAD_FAILURE);
				}
				break;
			case Constants.P_IS_LOGIN:
				if (mActivity.get() == null) {
					return;
				} else {
					doLoginView();
				}
				break;
			case Constants.P_MARKET_USERLOGO:

				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_CHANGE_PASSWORD:
			if (obj != null && obj instanceof LostPassWd) {
				LostPassWd lostPassWd = (LostPassWd) obj;
				if (lostPassWd != null
						&& lostPassWd.status.equals(Constants.STATUS_OK)) {
					ToastUtil.showShortToast(activity, lostPassWd.info);
				} else {
					ToastUtil.showShortToast(activity, lostPassWd.info);
				}
			}
			break;
		case MarketAPI.ACTION_CLIENT_UPDATE:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent result = (InfoAndContent) obj;
				ClientUpdate update = JsonUtil.toObject(result.content,
						ClientUpdate.class);
				if (update.upversion > 0
						&& update.upversion > mSession.getVersionCode()) {
					Utils.showUpdateDialog(getActivity(), update,
							mDownloadManager);
				} else {
					ToastUtil.showShortToast(activity, R.string.lastet_version);
				}
			} else {
				ToastUtil.showShortToast(activity, R.string.lastet_version);
			}
			break;
		case MarketAPI.ACTION_POST_UPLOAD_LOGO:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent item = (InfoAndContent) obj;
				if (item != null && item.status == 1) {
					Logo logo = JsonMananger.jsonToBean(item.content,
							Logo.class);
					if (logo != null) {
						mHandler.sendMessage(mHandler.obtainMessage(
								LOAD_SUCCESS, logo.logo));
					} else {
						ToastUtil.showLongToast(context, item.info);
					}
				} else {
					ToastUtil.showLongToast(context, item.info);
				}
			} else {
				ToastUtil.showLongToast(context, Constants.UPLOAD_FAILURE);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
		case MarketAPI.ACTION_POST_UPLOAD_LOGO:
			ToastUtil.showLongToast(context, Constants.UPLOAD_FAILURE);
			break;
		default:
			break;
		}
	}

	@Override
	public void update(Observable arg0, Object data) {
		if (data != null && data instanceof Pair) {
			synchronized (MainActivity.class) {
				@SuppressWarnings("unchecked")
				Pair<String, Object> result = (Pair<String, Object>) data;
				if (result != null) {
					if (result.first.equals(SessionManager.P_MARKET_USERLOGO)) {
						mHandler.sendEmptyMessage(Constants.P_MARKET_USERLOGO);
					} else if (result.first.equals(SessionManager.P_IS_LOGIN)) {
						mHandler.sendEmptyMessage(Constants.P_IS_LOGIN);
					}
				}
			}
		}
	}

}
