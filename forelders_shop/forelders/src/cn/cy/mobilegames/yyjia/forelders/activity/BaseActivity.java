package cn.cy.mobilegames.yyjia.forelders.activity;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.view.Window;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.app.App;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadService;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;

public abstract class BaseActivity extends FragmentActivity implements
		ApiRequestListener {
	public Context context;
	Session mSession;
	ImageLoaderUtil imageLoader;
	public DownloadManager mDownloadManager;
	private BroadcastReceiver mReceiver;
	String apkFilePath;
	long downloadId = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 设置无标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏
		// getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);
		context = this;
		mSession = Session.get(context);
		App app = (App) getApplication();
		app.registerActivity(this);// 注册当前的activity到app，便于app管理
		imageLoader = ImageLoaderUtil.getInstance(context);
		startDownloadService();
		mDownloadManager = DownloadManager.getInstance(context);
		mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				showDownloadList();
			}
		};
		registerReceiver(mReceiver, new IntentFilter(
				DownloadManager.ACTION_NOTIFICATION_CLICKED));
		File folder = Environment
				.getExternalStoragePublicDirectory(Constants.ROOT_DIR);
		if (!folder.exists() || !folder.isDirectory()) {
			folder.mkdirs();
		}
		super.onCreate(savedInstanceState);
	}

	private void startDownloadService() {
		Intent intent = new Intent();
		intent.setClass(this, DownloadService.class);
		startService(intent);
	}

	private void showDownloadList() {
		Intent intent = new Intent();
		intent.setClass(this, DownloadListActivity.class);
		startActivity(intent);
	}

	@Override
	public void onResume() {
		mSession = Session.get(context);
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
