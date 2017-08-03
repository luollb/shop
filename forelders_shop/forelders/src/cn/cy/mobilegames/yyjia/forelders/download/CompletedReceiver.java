package cn.cy.mobilegames.yyjia.forelders.download;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

public class CompletedReceiver extends BroadcastReceiver {
	private String action;
	private static Context mContext;
	private final static int WHAT = 0x11;
	private MyHandler handler = new MyHandler();
	private static DownloadManager mDownloadManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		mDownloadManager = DownloadManager.getInstance(mContext);
		action = intent.getAction();
		Utils.E("complete action " + action);
		try {
			if (action == DownloadManager.ACTION_DOWNLOAD_COMPLETE) {
				handler.sendMessage(handler.obtainMessage(WHAT, intent));
			} else if (action == DownloadManager.ACTION_NOTIFICATION_CLICKED) {
				Utils.to(mContext, DownloadListActivity.class);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case WHAT:
				Intent intent = (Intent) msg.obj;
				long downloadId = intent.getLongExtra(
						DownloadManager.EXTRA_DOWNLOAD_ID, -1);
				String filePath = intent
						.getStringExtra(Constants.KEY_FILE_PATH);
				int status = mDownloadManager.queryActualStatus(downloadId);
				if (status == 200) {
					Utils.E("complete installApk " + status);
					Utils.installApk(mContext, filePath,
							mDownloadManager.queryAppidById(downloadId),
							downloadId);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	}
}
