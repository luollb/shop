package cn.cy.mobilegames.yyjia.forelders.app;

import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;

public abstract class App extends Application {

	private static App sInstance;
	private List<WeakReference<Activity>> activities = new LinkedList<WeakReference<Activity>>();

	public static Application getApplication() {
		return sInstance;
	}

	protected final App getApp() {
		return sInstance;
	}

	@Override
	public void onCreate() {
		PushAgent mPushAgent = PushAgent.getInstance(getApplicationContext());
		mPushAgent.setDebugMode(false);
		mPushAgent.register(new IUmengRegisterCallback() {
			@Override
			public void onSuccess(String token) {
				Utils.E("deviceToken " + token);
			}

			@Override
			public void onFailure(String s, String s1) {
				Utils.E("onFailure " + s + "  s1 " + s1);
			}
		});
		mPushAgent.setNoDisturbMode(23, 0, 7, 0);
		super.onCreate();
		sInstance = this;
		afterCreate();

	}

	protected abstract void afterCreate();

	/**
	 * 注册当前界面
	 * 
	 * @param activity
	 */
	public void registerActivity(Activity activity) {
		activities.add(new WeakReference<Activity>(activity));
	}

	/**
	 * 关闭应用所有的界面
	 */
	public void finishActivities() {
		for (WeakReference<Activity> aRef : activities) {
			Activity a = aRef.get();
			if (null != a && !a.isFinishing())
				a.finish();
		}
	}

	/**
	 * 退出当前应用
	 */
	public void exit() {
		beforeExit();
		sInstance.finishActivities();
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
	}

	/**
	 * 退出应用前需要处理的内容
	 */
	protected abstract void beforeExit();

}
