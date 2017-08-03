package cn.cy.mobilegames.yyjia.forelders.util;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

final public class TaskUtil {
	private static Notification mNotification;
	private static TaskUtil mTaskUtil;
	private int taskNum = -111;
	private Context mContext;
	private Cursor mCursor;
	public final static String SP_NAME = "downloads";
	public final static String NUMBER_NAME = "number";
	public static SharedPreferences SharedPreferences;

	private TaskUtil() {

	}

	private TaskUtil(Context context) {
		this.mContext = context;
		if (SharedPreferences == null) {
			SharedPreferences = mContext.getSharedPreferences(SP_NAME,
					Context.MODE_PRIVATE);
		}
	}

	public static TaskUtil getTaskUtil(Context context) {
		if (mTaskUtil == null) {
			mTaskUtil = new TaskUtil(context);
		}
		return mTaskUtil;
	}

	public static Notification getNotification() {
		synchronized (TaskUtil.class) {
			if (mNotification == null) {
				mNotification = new Notification();
			}
		}
		return mNotification;
	}

	public static void e(String s, String log) {
		Log.e(s, log);
	}

	public static void i(String s, String log) {
		Log.i(s, log);
	}

	public static void d(String s, String log) {
		Log.d(s, log);
	}

	public static void v(String s, String log) {
		Log.v(s, log);
	}

	public static void w(String s, String log) {
		Log.w(s, log);
	}

	public int getTaskNum() {
		return
		// taskNum == -111 ?
		SharedPreferences.getInt(NUMBER_NAME, 0)
		// : taskNum
		;
	}

	public void setTaskNum(int number) {
		this.taskNum = number;
	}

	// public int taskAdd() {
	// if (taskNum == -111) {
	// taskNum = SharedPreferences.getInt(NUMBER_NAME, 0);
	// }
	// return ++this.taskNum;
	// }
	//
	// public int taskReduce() {
	// if (taskNum == -111) {
	// taskNum = SharedPreferences.getInt(NUMBER_NAME, -1);
	// }
	// return --this.taskNum;
	// }

	public Cursor getCursor() {
		return mCursor;
	}

	public void setCursor(Cursor mCursor) {
		this.mCursor = mCursor;
	}

}
