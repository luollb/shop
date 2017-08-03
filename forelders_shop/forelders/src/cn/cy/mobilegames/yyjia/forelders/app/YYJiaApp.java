package cn.cy.mobilegames.yyjia.forelders.app;

import android.content.Context;
import android.content.Intent;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadService;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ResponseCacheManager;

public class YYJiaApp extends App {
	public static YYJiaApp instance;
	public Context mContext;

	public static YYJiaApp getApplication() {
		return instance;
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		mContext = this;
	}

	@Override
	public void onLowMemory() {
		ImageLoaderUtil.getInstance(mContext).clearDiskCache();
		ImageLoaderUtil.getInstance(mContext).clearMemoryCache();
		ResponseCacheManager.getInstance().clear();
		super.onLowMemory();
	}

	@Override
	protected void afterCreate() {
		startDownloadService();
	}

	@Override
	protected void beforeExit() {
		// UserInfoContext.setUserInfoForm(getApplicationContext(),
		// UserInfoContext.ISLOGIN, 0);
	}

	public void startDownloadService() {
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), DownloadService.class);
		getApplicationContext().startService(intent);
	}

	@Override
	public void exit() {
		super.exit();
	}

	@Override
	public void finishActivities() {
		super.finishActivities();
	}

}
