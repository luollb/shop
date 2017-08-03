package cn.cy.mobilegames.yyjia.forelders.download;

import java.util.HashMap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.DBUtils;

public class PackageReceiver extends BroadcastReceiver {
	private Session session;
	private static Context mContext;
	private static DownloadManager mDownloadManager;
	private String action;
	// private String scheme;
	private String packageName;
	private HashMap<String, UpgradeInfo> updateLists = new HashMap<String, UpgradeInfo>();

	@Override
	public void onReceive(Context context, Intent intent) {
		mContext = context;
		session = Session.get(context);
		updateLists = session.getUpdateList();
		mDownloadManager = DownloadManager.getInstance(mContext);
		action = intent.getAction();

		try {
			// scheme = intent.getScheme();
			packageName = intent.getDataString().split(":")[1];

			if (action == Intent.ACTION_PACKAGE_ADDED) {
				if (updateLists != null && updateLists.containsKey(packageName)) {
					session.removeInstalledApp(packageName);
					DBUtils.removeUpgrade(mContext, packageName);
				}
				session.addInstalledApp(packageName);
				mDownloadManager.updateStatusByPkgName(packageName,
						Impl.STATUS_INSTALLED);

			} else if (action == Intent.ACTION_PACKAGE_CHANGED) {

			} else if (action == Intent.ACTION_PACKAGE_DATA_CLEARED) {

			} else if (action == Intent.ACTION_PACKAGE_FIRST_LAUNCH) {

			} else if (action == Intent.ACTION_PACKAGE_FULLY_REMOVED) {
				session.removeInstalledApp(packageName);
			} else if (action == Intent.ACTION_PACKAGE_NEEDS_VERIFICATION) {

			} else if (action == Intent.ACTION_PACKAGE_REMOVED) {
				mDownloadManager.updateStatusByPkgName(intent.getDataString()
						.split(":")[1], Impl.STATUS_UNINSTALLED);
				session.removeInstalledApp(packageName);
			} else if (action == Intent.ACTION_PACKAGE_REPLACED) {
				if (updateLists != null && updateLists.containsKey(packageName)) {
					session.removeInstalledApp(packageName);
					DBUtils.removeUpgrade(mContext, packageName);
				}
				session.addInstalledApp(packageName);
				mDownloadManager.updateStatusByPkgName(packageName,
						Impl.STATUS_INSTALLED);
			} else if (action == Intent.ACTION_PACKAGE_RESTARTED) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
