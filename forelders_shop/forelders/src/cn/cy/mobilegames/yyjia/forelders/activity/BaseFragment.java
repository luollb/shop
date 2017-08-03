package cn.cy.mobilegames.yyjia.forelders.activity;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.app.App;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadService;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ResponseCacheManager;

import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

public abstract class BaseFragment extends Fragment implements
		ApiRequestListener {

	public Context context;
	public ImageLoaderUtil imageLoader;
	public ResponseCacheManager cacheManager;
	public BroadcastReceiver mReceiver;
	public DownloadManager mDownloadManager;

	void showDownloadList() {
		Intent localIntent = new Intent();
		localIntent.setClass(getActivity(), DownloadListActivity.class);
		startActivity(localIntent);
	}

	private void startDownloadService() {
		Intent localIntent = new Intent();
		localIntent.setClass(getActivity(), DownloadService.class);
		getActivity().startService(localIntent);
	}

	@Override
	public void onCreate(Bundle paramBundle) {
		this.context = getActivity();
		((App) getActivity().getApplication()).registerActivity(getActivity());
		this.cacheManager = ResponseCacheManager.getInstance();
		this.imageLoader = ImageLoaderUtil.getInstance(this.context);
		this.mDownloadManager = DownloadManager.getInstance(context);
		startDownloadService();
		this.mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context paramContext, Intent paramIntent) {
				BaseFragment.this.showDownloadList();
			}
		};
		getActivity().registerReceiver(
				this.mReceiver,
				new IntentFilter(
						"android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED"));
		File localFile = Environment
				.getExternalStoragePublicDirectory(Constants.ROOT_DIR);
		if ((!localFile.exists()) || (!localFile.isDirectory()))
			localFile.mkdirs();
		super.onCreate(paramBundle);
	}

	@Override
	public void onDestroy() {
		getActivity().unregisterReceiver(this.mReceiver);
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

}
