/*
 * Copyright (C) 2010 mAPPn.Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.cy.mobilegames.yyjia.forelders;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Pair;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;
import cn.cy.mobilegames.yyjia.forelders.model.DownloadInfo;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.DBUtils;
import cn.cy.mobilegames.yyjia.forelders.util.MarketProvider;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * 
 * The Client Seesion Object for , contains some necessary information.
 * 
 * @author andrew
 * @date 2010-12-22
 * @since Version 0.5.1
 * 
 */
public class Session extends Observable {

	// /** Log tag */
	// private final static String TAG = "Session";

	/** Application Context */
	private Context mContext;

	/** The application debug mode */
	public boolean isDebug;

	/** The application uid */
	private String uid;

	/** The mobile device screen size */
	private String screenSize;

	/** The version of OS */
	private int osVersion;

	/** The Http User-Agent */
	private String userAgent;

	/** The user login status */
	private boolean isLogin;

	/** show image */
	private boolean isShowImage;

	/** first Recommnd */
	private boolean isFirstRecom;

	/** Indicate whether auto clear cache when user exit */
	private boolean isAutoClearCache;

	/** The channel id */
	private String cid;

	private final static String CHANNEL_ID = "45app_cid";
	private final static String SDK_ID = "45app_cpid";
	private final static String DEBUG_TYPE = "45app_debug";
	private final static String DEBUG_TRUE = "1";
	private final static String DEBUG_FALSE = "0";

	/** The SDK id */
	private String cappid;

	/** The Application Debug flag */
	private String debugType;

	/** The Application Version Code */
	private int versionCode;

	/** The Application package name */
	private String packageName;

	/** The Application version name */
	private String version;

	/** The Application app name */
	private String appName;

	/** The mobile IMEI code */
	private String imei;

	/** The mobile sim code */
	private String sim;

	/** The mobile mac address */
	private String macAddress;

	/**
	 * The mobile model such as "Nexus One" Attention: some model type may have
	 * illegal characters
	 */
	private String model;

	/** The user-visible OSversion string. E.g., "1.0" */
	private String buildVersion;

	/** User login name */
	private String userName;

	/** User login NickName */
	private String nickName;

	/** User login userid */
	private String userId;

	/** User login logo */
	private String userLogo;

	/** User login email */
	private String userEmail;

	/** User login password */
	private String password;

	/** Indicate whether new version is available */
	private boolean isUpdateAvailable;

	/** The new version name */
	private String updateversion;

	/** The new version code */
	private int updateVersionCode;

	/** The new version description */
	private String updateVersionDesc;

	/** The new version update uri */
	private String updateUri;

	/** The new version update level(Force Update/Option Update) */
	private int updateLevel;

	/** The new version APK download task id */
	private long updateId;

	/** The cloud service device bind flag */
	private boolean isDeviceBinded;

	/** The mobile device id */
	private String deviceId;

	/** The apps upgrade number */
	private int upgradeNumber;

	/** The apps update check time */
	private long updataCheckTime;

	/** The local card version */
	private int creditCardVersion;

	/** The current version */
	private int lastVersion;

	/** 上次更新splash的时间戳 */
	private long splashTime;

	/** 上次更新splash的id */
	private long splashId;

	/** The application list which user has installed */
	private ArrayList<String> mInstalledApps;

	// /** 下载的应用ICON缓存 */
	// private HashMap<String, String> mIconUrlCache;

	/** Session Manager */
	private SessionManager mSessionManager;

	/** Download Manager */
	private DownloadManager mDownloadManager;

	/** The singleton instance */
	private static Session mInstance;

	/** 默认的支付方式 */
	private String mDefaultChargeType;

	// /** Google Analytics */
	// private GoogleAnalyticsTracker tracker;

	private DbStatusRefreshTask mDbStatusRefreshTask;

	/**
	 * default constructor
	 * 
	 * @param context
	 */
	private Session(Context context) {

		synchronized (this) {
			mContext = context;

			mHandler.sendEmptyMessage(CURSOR_CREATED);

			osVersion = Build.VERSION.SDK_INT;
			buildVersion = Build.VERSION.RELEASE;
			try {
				model = URLEncoder.encode(Build.MODEL, "UTF-8");
			} catch (UnsupportedEncodingException e) {

			}
			mDownloadManager = DownloadManager.getInstance(context);
			// new DownloadManager(
			// context.getContentResolver(), getPackageName());

			// tracker = GoogleAnalyticsTracker.getInstance();
			// // Start the tracker in manual dispatch mode...
			// tracker.start(Constants.GOOGLE_UID, context);

			readSettings();
		}
	}

	/*
	 * 读取用户所有的设置
	 */
	private void readSettings() {
		new Thread() {
			@Override
			public void run() {
				mSessionManager = SessionManager.get(mContext);
				addObserver(mSessionManager);
				HashMap<String, Object> preference = mSessionManager
						.readPreference();
				uid = (String) preference.get(SessionManager.P_UID);
				screenSize = (String) preference
						.get(SessionManager.P_SCREEN_SIZE);
				isLogin = (Boolean) preference.get(SessionManager.P_IS_LOGIN);
				isShowImage = (Boolean) preference
						.get(SessionManager.P_IS_SHOW_IMAGE);
				isFirstRecom = (Boolean) preference
						.get(SessionManager.P_IS_FIRST_RECOM);
				isAutoClearCache = (Boolean) preference
						.get(SessionManager.P_CLEAR_CACHE);
				userName = (String) preference
						.get(SessionManager.P_MARKET_USERNAME);
				userId = (String) preference
						.get(SessionManager.P_MARKET_USERID);
				nickName = (String) preference
						.get(SessionManager.P_MARKET_NICKNAME);
				userLogo = (String) preference
						.get(SessionManager.P_MARKET_USERLOGO);
				userEmail = (String) preference
						.get(SessionManager.P_MARKET_USEREMAIL);
				password = (String) preference
						.get(SessionManager.P_MARKET_PASSWORD);
				upgradeNumber = (Integer) preference
						.get(SessionManager.P_UPGRADE_NUM);
				updataCheckTime = (Long) preference
						.get(SessionManager.P_PRODUCT_UPDATE_CHECK_TIMESTAMP);
				updateId = (Long) preference.get(SessionManager.P_UPDATE_ID);

				// cloud preferences
				deviceId = (String) preference
						.get(SessionManager.P_LPNS_BINDED_DEVID);
				isDeviceBinded = (Boolean) preference
						.get(SessionManager.P_LPNS_IS_BINDED);

				creditCardVersion = (Integer) preference
						.get(SessionManager.P_CARD_VERSION);
				lastVersion = (Integer) preference
						.get(SessionManager.P_CURRENT_VERSION);

				isUpdateAvailable = (Boolean) preference
						.get(SessionManager.P_UPDATE_AVAILABIE);
				updateversion = (String) preference
						.get(SessionManager.P_UPDATE_VERSION_NAME);
				updateVersionCode = (Integer) preference
						.get(SessionManager.P_UPDATE_VERSION_CODE);
				updateVersionDesc = (String) preference
						.get(SessionManager.P_UPDATE_DESC);
				updateUri = (String) preference
						.get(SessionManager.P_UPDATE_URI);
				updateLevel = (Integer) preference
						.get(SessionManager.P_UPDATE_LEVEL);

				splashId = (Long) preference.get(SessionManager.P_SPLASH_ID);
				splashTime = (Long) preference
						.get(SessionManager.P_SPLASH_TIME);

				mDefaultChargeType = (String) preference
						.get(SessionManager.P_DEFAULT_CHARGE_TYPE);

				getApplicationInfo();
			};
		}.start();
	}

	public static Session get(Context context) {
		if (mInstance == null) {
			mInstance = new Session(context);
		}
		return mInstance;
	}

	public int isFilterApps() {
		return SessionManager.get(mContext).isFilterApps();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(SessionManager.P_UID,
				uid));
	}

	public String getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		this.screenSize = dm.widthPixels < dm.heightPixels ? dm.widthPixels
				+ "#" + dm.heightPixels : dm.heightPixels + "#"
				+ dm.widthPixels;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_SCREEN_SIZE, screenSize));
	}

	public int getOsVersion() {
		return osVersion;
	}

	public String getJavaApiUserAgent() {
		if (TextUtils.isEmpty(userAgent)) {
			StringBuilder ua = new StringBuilder();
			final String splash = "/";
			ua.append(getModel()).append(splash).append(getBuildVersion())
					.append(splash)
					.append(mContext.getString(R.string.app_name))
					.append(splash).append(getversion()).append(splash)
					.append(getCid()).append(splash)
					// 2011/3/7 add mac address for Analytics
					.append(getIMEI()).append(splash).append(getSim())
					.append(splash).append(getMac());
			return ua.toString();
		}
		return userAgent;
	}

	public String getUCenterApiUserAgent() {
		return "packagename=" + getPackageName() + ",appName=" + getAppName()
				+ ",channelID=" + getCid();
	}

	/**
	 * get Application Info
	 */
	private void getApplicationInfo() {

		final PackageManager pm = mContext.getPackageManager();
		try {
			final PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(),
					0);
			version = pi.versionName;
			versionCode = pi.versionCode;

			final ApplicationInfo ai = pm.getApplicationInfo(
					mContext.getPackageName(), PackageManager.GET_META_DATA);
			cid = Utils.checkEmpty(ai.metaData.get(CHANNEL_ID));
			cappid = Utils.checkEmpty(ai.metaData.get(SDK_ID));
			debugType = Utils.checkEmpty(ai.metaData.get(DEBUG_TYPE));

			// if (DEBUG_TRUE.equals(debugType)) {
			// // developer mode
			// isDebug = true;
			// } else if (DEBUG_FALSE.equals(debugType)) {
			// // release mode
			// isDebug = false;
			// }
			// Utils.sDebug = isDebug;

			appName = String.valueOf(ai.loadLabel(pm));
			Utils.sLogTag = appName;
			packageName = mContext.getPackageName();

			TelephonyManager telMgr = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telMgr.getDeviceId();
			sim = telMgr.getSimSerialNumber();
		} catch (NameNotFoundException e) {
			Utils.D("met some error when get application info");
		}
	}

	public String getCid() {
		if (TextUtils.isEmpty(cid)) {
			getApplicationInfo();
		}
		return cid;
	}

	public String getCappid() {
		if (TextUtils.isEmpty(cappid)) {
			getApplicationInfo();
		}
		return cappid;
	}

	public String getversion() {
		if (TextUtils.isEmpty(version)) {
			getApplicationInfo();
		}
		return version;
	}

	public int getVersionCode() {
		if (versionCode <= 0) {
			getApplicationInfo();
		}
		return versionCode;
	}

	public String getIMEI() {
		if (TextUtils.isEmpty(imei)) {
			getApplicationInfo();
		}
		return imei;
	}

	public String getPackageName() {
		if (TextUtils.isEmpty(packageName)) {
			getApplicationInfo();
		}
		return packageName;
	}

	public String getSim() {
		if (TextUtils.isEmpty(sim)) {
			getApplicationInfo();
		}
		return sim;
	}

	public String getMac() {
		if (TextUtils.isEmpty(macAddress)) {
			WifiManager wifi = (WifiManager) mContext
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			macAddress = info.getMacAddress();
		}
		return macAddress;
	}

	public boolean isShowImage() {
		return isShowImage;
	}

	public void setShowImage(boolean show) {

		// there is no need to update for [same] value
		if (this.isShowImage == show) {
			return;
		}

		this.isShowImage = show;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_IS_SHOW_IMAGE, isShowImage));
	}

	public boolean isFirstRecom() {
		return isFirstRecom;
	}

	public void setFirstRecom(boolean recom) {

		// there is no need to update for [same] value
		if (this.isFirstRecom == recom) {
			return;
		}

		this.isFirstRecom = recom;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_IS_FIRST_RECOM, isFirstRecom));
	}

	public boolean isLogin() {
		return isLogin;
	}

	public void setLogin(boolean isLogin) {

		// there is no need to update for [same] value
		if (this.isLogin == isLogin) {
			return;
		}

		this.isLogin = isLogin;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_IS_LOGIN, isLogin));
	}

	public String getUserLogo() {
		return userLogo;
	}

	public void setUserLogo(String logo) {

		this.userLogo = logo;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_USERLOGO, userLogo));
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setuserEmail(String email) {

		this.userEmail = email;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_USEREMAIL, userEmail));
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String name) {
		this.nickName = name;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_NICKNAME, nickName));
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_USERNAME, userName));
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userid) {
		this.userId = userid;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_USERID, userId));
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {

		this.password = password;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_MARKET_PASSWORD, password));
	}

	public String getAppName() {
		return appName;
	}

	public boolean isUpdateAvailable() {
		return isUpdateAvailable;
	}

	public void setUpdateAvailable(boolean flag) {
		this.isUpdateAvailable = flag;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_AVAILABIE, flag));
	}

	public String getUpdateversion() {
		return updateversion;
	}

	public int getUpdateVersionCode() {
		return updateVersionCode;
	}

	public String getUpdateVersionDesc() {
		return updateVersionDesc;
	}

	public String getUpdateUri() {
		return updateUri;
	}

	public int getUpdateLevel() {
		return updateLevel;
	}

	public void setUpdateInfo(String version, int versionCode,
			String description, String url, int level) {

		this.isUpdateAvailable = true;
		this.updateversion = version;
		this.updateVersionCode = versionCode;
		this.updateVersionDesc = description;
		this.updateUri = url;
		this.updateLevel = level;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_AVAILABIE, true));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_VERSION_CODE, versionCode));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_DESC, description));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_URI, url));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_VERSION_NAME, version));
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_LEVEL, level));
	}

	public long getUpdateId() {
		return updateId;
	}

	public void setUpdateID(long updateId) {

		if (this.updateId == updateId) {
			return;
		}

		this.updateId = updateId;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPDATE_ID, updateId));
	}

	public boolean isAutoClearCache() {
		return isAutoClearCache;
	}

	public String getModel() {
		return model;
	}

	public String getBuildVersion() {
		return buildVersion;
	}

	public DownloadManager getDownloadManager() {
		return DownloadManager.getInstance(mContext);
		// if (mDownloadManager == null) {
		// mDownloadManager = new DownloadManager(
		// mContext.getContentResolver(), getPackageName());
		// }
		// return mDownloadManager;
	}

	public boolean isDeviceBinded() {
		return isDeviceBinded;
	}

	public void setDeviceBinded(boolean isDeviceBinded) {

		// there is no need to update for [same] value
		if (this.isDeviceBinded == isDeviceBinded) {
			return;
		}

		this.isDeviceBinded = isDeviceBinded;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_LPNS_IS_BINDED, isDeviceBinded));
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {

		this.deviceId = deviceId;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_LPNS_BINDED_DEVID, deviceId));
	}

	public int getUpgradeNumber() {
		return upgradeNumber;
	}

	/**
	 * 设置可升级数
	 * 
	 * @param upgradeNumber
	 */
	public void setUpgradeNumber(int upgradeNumber) {

		// there is no need to update for [same] value
		if (this.upgradeNumber == upgradeNumber) {
			return;
		}
		this.upgradeNumber = upgradeNumber;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_UPGRADE_NUM, upgradeNumber));
		mHandler.sendEmptyMessage(CURSOR_UPGRADE);
	}

	public long getUpdataCheckTime() {
		return updataCheckTime;
	}

	public void setUpdataCheckTime(long updataCheckTime) {

		// there is no need to update for [same] value
		if (this.updataCheckTime == updataCheckTime) {
			return;
		}
		this.updataCheckTime = updataCheckTime;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_PRODUCT_UPDATE_CHECK_TIMESTAMP,
				updataCheckTime));
	}

	public int getCreditCardVersion() {
		return creditCardVersion;
	}

	public void setCreditCardVersion(int creditCardVersion) {

		// there is no need to update for [same] value
		if (this.creditCardVersion == creditCardVersion) {
			return;
		}

		this.creditCardVersion = creditCardVersion;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_CARD_VERSION, creditCardVersion));
	}

	public String getDebugType() {
		return debugType;
	}

	public void close() {
		SessionManager.get(mContext).writePreferenceQuickly();
		mDownloadingCursor.unregisterContentObserver(mCursorObserver);
		mDownloadingCursor.close();
		// tracker.dispatch();
		mHandler = null;
		mDbStatusRefreshTask = null;
		mInstance = null;
	}

	/**
	 * 获取已装应用包名
	 * 
	 * @return
	 */
	public ArrayList<String> getInstalledApps() {
		if (mInstalledApps == null) {
			mInstalledApps = Utils.getAllInstalledPkg(Utils
					.getAllInstalledApps(mContext));
		}
		return mInstalledApps;
	}

	/**
	 * 增加已装应用记录
	 * 
	 * @param packageName
	 */
	public void addInstalledApp(String packageName) {

		if (mInstalledApps == null) {
			mInstalledApps = Utils.getAllInstalledPkg(Utils
					.getAllInstalledApps(mContext));
		}
		// 2011/2/21 fix bug
		mInstalledApps.add(packageName);
		mHandler.sendEmptyMessage(CURSOR_UPGRADE);
	}

	/**
	 * 卸载应用
	 * 
	 * @param packageName
	 *            包名
	 */
	public void removeInstalledApp(String packageName) {
		if (mInstalledApps == null) {
			mInstalledApps = Utils.getAllInstalledPkg(Utils
					.getAllInstalledApps(mContext));
		}
		// 2011/2/21 fix bug
		mInstalledApps.remove(packageName);
		mHandler.sendEmptyMessage(CURSOR_UPGRADE);
	}

	/**
	 * 设置已装应用
	 * 
	 * @param mInstalledApps
	 */
	public void setInstalledApps(ArrayList<String> mInstalledApps) {
		this.mInstalledApps = mInstalledApps;
	}

	public long getSplashTime() {
		return splashTime;
	}

	public void setSplashTime(long splashTime) {
		this.splashTime = splashTime;

		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_SPLASH_TIME, splashTime));
	}

	public int getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(int currentVersion) {
		if (currentVersion == this.lastVersion) {
			return;
		}
		clearData();
		this.lastVersion = currentVersion;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_CURRENT_VERSION, currentVersion));
	}

	/**
	 * 清除上一个版本数据
	 */
	public void clearData() {
		setDeviceId("");
		setDeviceBinded(false);
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(mContext);
		sp.edit().clear().commit();
	}

	public long getSplashId() {
		return splashId;
	}

	public void setSplashId(long splashId) {
		this.splashId = splashId;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_SPLASH_ID, splashId));
	}

	public String getDefaultChargeType() {
		return mDefaultChargeType;
	}

	public void setDefaultChargeType(String type) {
		mDefaultChargeType = type;
		super.setChanged();
		super.notifyObservers(new Pair<String, Object>(
				SessionManager.P_DEFAULT_CHARGE_TYPE, type));
	}

	// /**
	// * @return the tracker
	// */
	// public GoogleAnalyticsTracker getTracker() {
	// return tracker;
	// }

	/** 创建下载数据结果集 */
	public static final int CURSOR_CREATED = 0;
	/** 下载数据结果集有更新 */
	public static final int CURSOR_CHANGED = 1;
	/** 应用有更新 */
	public static final int CURSOR_UPGRADE = 2;
	/** 升级列表有更新 */
	public static final int UPGRADE_LIST = 3;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("deprecation")
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case CURSOR_CREATED:
				mDownloadingList = new HashMap<String, DownloadInfo>();
				startQuery();
				break;
			case CURSOR_CHANGED:
				if (mDownloadingCursor == null) {
					mDownloadingList = new HashMap<String, DownloadInfo>();
					setChanged();
					notifyObservers(mDownloadingList);
					return;
				}
				// TODO
				mDownloadingCursor.requery();
				synchronized (this) {
					refreshDownloadApp(mDownloadingCursor);
				}
				break;
			case CURSOR_UPGRADE:
				setChanged();
				notifyObservers(Constants.INFO_UPDATE);
				break;
			case UPGRADE_LIST:
				setChanged();
				notifyObservers(mDownloadingList);
				break;
			default:
				break;
			}
		}
	};

	private HashMap<String, DownloadInfo> mDownloadingList;
	/** The application list which user can update */
	private HashMap<String, UpgradeInfo> mUpdateApps = new HashMap<String, UpgradeInfo>();
	private Cursor mDownloadingCursor;

	public HashMap<String, DownloadInfo> getDownloadingList() {
		return mDownloadingList;
	}

	public HashMap<String, UpgradeInfo> getUpdateList() {
		return mUpdateApps;
	}

	public void queryUpgradeList() {
		mUpdateApps = DBUtils.queryUpgradeProduct(mContext);
		mHandler.sendEmptyMessage(CURSOR_UPGRADE);
	}

	public void setUpgradeList(HashMap<String, UpgradeInfo> list) {
		mUpdateApps = list;
		mHandler.sendEmptyMessage(CURSOR_UPGRADE);
	}

	private ContentObserver mCursorObserver = new ContentObserver(mHandler) {
		@Override
		public void onChange(boolean selfChange) {
			mHandler.sendEmptyMessage(CURSOR_CHANGED);
		}
	};

	public void updateDownloading(int code) {
		switch (code) {
		case CURSOR_CREATED:
			mHandler.sendEmptyMessage(CURSOR_CREATED);
			break;
		case CURSOR_CHANGED:
			mHandler.sendEmptyMessage(CURSOR_CHANGED);
			break;
		case CURSOR_UPGRADE:
			mHandler.sendEmptyMessage(CURSOR_UPGRADE);
			break;
		case UPGRADE_LIST:
			mHandler.sendEmptyMessage(UPGRADE_LIST);
			break;
		default:
			break;
		}
	}

	private void startQuery() {
		mDbStatusRefreshTask = new DbStatusRefreshTask(
				mContext.getContentResolver());
		mDbStatusRefreshTask.startQuery(DbStatusRefreshTask.DOWNLOAD, null,
				DownloadManager.Impl.CONTENT_URI, null, "(("
						+ DownloadManager.Impl.COLUMN_STATUS
						+ " >= '100' ) AND "
						// TODO
						// + DownloadManager.Impl.COLUMN_STATUS +
						// " >= '190' AND "
						// + DownloadManager.Impl.COLUMN_STATUS +
						// " <= '200') OR "
						// + DownloadManager.Impl.COLUMN_STATUS
						// + " = '"
						// + DownloadManager.Impl.STATUS_CANCELED
						// + "') AND "
						// TODO
						+ DownloadManager.Impl.COLUMN_DESTINATION + " = '"
						+ DownloadManager.Impl.DESTINATION_FILE_URI
						// + "' AND "
						// + Impl.COLUMN_MIME_TYPE + " = '"
						// + Constants.MIMETYPE_APK
						+ "')", null, null);
		String selection = MarketProvider.COLUMN_P_IGNORE + "=?";
		String[] selectionArgs = new String[] { "0" };
		mDbStatusRefreshTask.startQuery(DbStatusRefreshTask.UPDATE, null,
				MarketProvider.UPGRADE_CONTENT_URI, null, selection,
				selectionArgs, null);
	}

	/**
	 * 刷新正在下载中的应用
	 */
	void refreshDownloadApp(Cursor cursor) {

		// 绑定观察者
		if (mDownloadingCursor == null) {
			if (cursor == null) {
				mDownloadingList = new HashMap<String, DownloadInfo>();
				setChanged();
				notifyObservers(mDownloadingList);
				return;
			}
			mDownloadingCursor = cursor;
			cursor.registerContentObserver(mCursorObserver);
		}

		if (cursor.getCount() > 0) {
			// 检索有结果
			mDownloadingList = new HashMap<String, DownloadInfo>();
		} else {
			mDownloadingList = new HashMap<String, DownloadInfo>();
			setChanged();
			notifyObservers(mDownloadingList);
			return;
		}

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			String packageName = cursor.getString(cursor
					.getColumnIndex(DownloadManager.Impl.COLUMN_PACKAGE_NAME));

			DownloadInfo infoItem = new DownloadInfo();
			infoItem.id = cursor.getInt(cursor
					.getColumnIndex(DownloadManager.Impl.COLUMN_ID));
			infoItem.mPackageName = packageName;
			infoItem.mAppName = cursor.getString(cursor
					.getColumnIndex(DownloadManager.Impl.COLUMN_TITLE));

			// int source = cursor.getInt(cursor
			// .getColumnIndex(DownloadManager.Impl.COLUMN_SOURCE));
			// if (source == Constants.DOWNLOAD_FROM_BBS) {
			// infoItem.mIconUrl = mContext.getResources().getDrawable(
			// R.drawable.manager_installed_bbs_icon);
			// } else if (source == Constants.DOWNLOAD_FROM_CLOUD) {
			// infoItem.mIconUrl = mContext.getResources().getDrawable(
			// R.drawable.manager_installed_soft_icon);
			// }
			// else {
			infoItem.mIconUrl = cursor.getString(cursor
					.getColumnIndex(DownloadManager.Impl.COLUMN_LOGO));
			// }
			infoItem.mFilePath = Uri.parse(
					cursor.getString(cursor
							.getColumnIndex(Impl.COLUMN_FILE_NAME_HINT)))
					.getPath();
			infoItem.mStatus = Utils.translateStatus(cursor.getInt(cursor
					.getColumnIndex(DownloadManager.Impl.COLUMN_STATUS)));
			mDownloadingList.put(packageName, infoItem);
			Utils.D("  infoItem.mStatus   " + infoItem.mStatus);
			switch (infoItem.mStatus) {
			case DownloadManager.STATUS_PENDING:
				// 准备开始下载
				infoItem.mProgressLevel = Constants.STATUS_PENDING;
				break;
			case DownloadManager.STATUS_RUNNING:
				// downloading progress
				long currentBytes = cursor
						.getInt(cursor
								.getColumnIndex(DownloadManager.Impl.COLUMN_CURRENT_BYTES));
				long totalBytes = cursor
						.getInt(cursor
								.getColumnIndex(DownloadManager.Impl.COLUMN_TOTAL_BYTES));
				infoItem.mTotalSize = totalBytes;
				infoItem.mCurrentSize = currentBytes;
				if (totalBytes > 0) {
					int progress = (int) ((float) currentBytes
							/ (float) totalBytes * 100);
					infoItem.mProgress = progress + "%";
					infoItem.mProgressNumber = progress;
					infoItem.mProgressLevel = Constants.STATUS_RUNNING;
					// // 下载分成8个级别，用于显示下载进度动画
					// int progressLevel = progress / 14 + 2;
					// infoItem.mProgressLevel = progressLevel > 8 ? 8
					// : progressLevel;
				}
				break;
			case DownloadManager.STATUS_PAUSED:
				// downloading progress
				long current = cursor
						.getInt(cursor
								.getColumnIndex(DownloadManager.Impl.COLUMN_CURRENT_BYTES));
				long total = cursor
						.getInt(cursor
								.getColumnIndex(DownloadManager.Impl.COLUMN_TOTAL_BYTES));
				infoItem.mTotalSize = total;
				infoItem.mCurrentSize = current;
				if (total > 0) {
					int progress = (int) ((float) current / (float) total * 100);
					infoItem.mProgress = progress + "%";
					infoItem.mProgressNumber = progress;
					// // 下载分成8个级别，用于显示下载进度动画
					// int progressLevel = progress / 14 + 2;
					// infoItem.mProgressLevel = progressLevel > 8 ? 8
					// : progressLevel;
				}
				// 下载暂停中
				infoItem.mProgressLevel = Constants.STATUS_PAUSE;
				break;
			case DownloadManager.STATUS_SUCCESS:
				// 下载成功
				infoItem.mProgressLevel = Constants.STATUS_UNINSTALL;
				break;
			case DownloadManager.STATUS_FAILED:
				// 下载失败
				infoItem.mProgressLevel = Constants.STATUS_FAILURE;
				break;
			case DownloadManager.STATUS_WAITED:
				// 等待队列中
				infoItem.mProgressLevel = Constants.STATUS_QUEUE;
				break;
			case DownloadManager.STATUS_INSTALLED:
				// 已经安装
				infoItem.mProgressLevel = Constants.STATUS_INSTALLED;
				break;
			case DownloadManager.STATUS_CANCELED:
				// 用户取消下载，恢复原始状态
				infoItem.mProgressLevel = Constants.STATUS_NORMAL;
				mDownloadManager.remove(infoItem.id);
				break;
			default:
				// 用户取消下载，恢复原始状态
				infoItem.mProgressLevel = Constants.STATUS_NORMAL;
				mDownloadManager.remove(infoItem.id);
				break;
			}

		}
		setChanged();
		notifyObservers(mDownloadingList);
	}

	/**
	 * 刷新可更新的应用
	 */
	void refreshUpdateApp(Cursor cursor) {
		mUpdateApps = new HashMap<String, UpgradeInfo>();
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				UpgradeInfo info = new UpgradeInfo();
				info.appid = cursor.getString(cursor
						.getColumnIndex(MarketProvider.COLUMN_P_ID));
				info.sourceurl = cursor.getString(cursor
						.getColumnIndex(MarketProvider.COLUMN_P_PACKAGE_NAME));
				info.version = cursor
						.getString(cursor
								.getColumnIndex(MarketProvider.COLUMN_P_NEW_VERSION_NAME));
				info.versionCode = cursor
						.getInt(cursor
								.getColumnIndex(MarketProvider.COLUMN_P_NEW_VERSION_CODE));
				mUpdateApps.put(info.sourceurl, info);
			}
			cursor.close();
		}
	}

	/**
	 * 本地数据库刷新检查
	 * 
	 */
	@SuppressLint("HandlerLeak")
	private class DbStatusRefreshTask extends AsyncQueryHandler {

		private final static int DOWNLOAD = 0;
		private final static int UPDATE = 1;

		public DbStatusRefreshTask(ContentResolver cr) {
			super(cr);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor) {

			switch (token) {
			case DOWNLOAD:
				refreshDownloadApp(cursor);
				break;

			case UPDATE:
				refreshUpdateApp(cursor);
				break;

			default:
				break;
			}
		}
	}
}