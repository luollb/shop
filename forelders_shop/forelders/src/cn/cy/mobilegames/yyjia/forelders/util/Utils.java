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
package cn.cy.mobilegames.yyjia.forelders.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.cy.mobilegames.yyjia.forelders.AndroidHttpClient;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.activity.MainActivity;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Impl;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Request;
import cn.cy.mobilegames.yyjia.forelders.model.ClientUpdate;
import cn.pedant.sweetalert.SweetAlertDialog;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * Common Utils for the application
 * 
 * @author andrew.wang
 * @date 2010-9-19
 * @since Version 0.4.0
 */
public class Utils {

	public static boolean sDebug = true;
	public static String sLogTag = "45应用商店";
	public static final String PREFS_NAME = "JPUSH_EXAMPLE";
	public static final String PREFS_DAYS = "JPUSH_EXAMPLE_DAYS";
	public static final String PREFS_START_TIME = "PREFS_START_TIME";
	public static final String PREFS_END_TIME = "PREFS_END_TIME";
	public static final String KEY_APP_KEY = "JPUSH_APPKEY";
	// UTF-8 encoding
	private static final String ENCODING_UTF8 = "UTF-8";

	private static WeakReference<Calendar> calendar;
	/** url and para separator **/
	public static final String URL_AND_PARA_SEPARATOR = "?";
	/** parameters separator **/
	public static final String PARAMETERS_SEPARATOR = "&";
	/** paths separator **/
	public static final String PATHS_SEPARATOR = "/";
	/** equal sign **/
	public static final String SIGN_EQUAL = "=";

	static final DecimalFormat DOUBLE_DECIMAL_FORMAT = new DecimalFormat("0.##");
	public static final int MB_2_BYTE = 1024 * 1024;
	public static final int KB_2_BYTE = 1024;

	/**
	 * join url and paras
	 * 
	 * <pre>
	 * getUrlWithParas(null, {(a, b)})                        =   "?a=b";
	 * getUrlWithParas("baidu.com", {})                       =   "baidu.com";
	 * getUrlWithParas("baidu.com", {(a, b), (i, j)})         =   "baidu.com?a=b&i=j";
	 * getUrlWithParas("baidu.com", {(a, b), (i, j), (c, d)}) =   "baidu.com?a=b&i=j&c=d";
	 * </pre>
	 * 
	 * @param url
	 *            url
	 * @param parasMap
	 *            paras map, key is para name, value is para value
	 * @return if url is null, process it as empty string
	 */
	public static String getUrlWithParas(String url,
			Map<String, String> parasMap) {
		StringBuilder urlWithParas = new StringBuilder(isEmpty(url) ? "" : url);
		String paras = joinParas(parasMap);
		if (!isEmpty(paras)) {
			urlWithParas.append(URL_AND_PARA_SEPARATOR).append(paras);
		}
		return urlWithParas.toString();
	}

	public static boolean isEmpty(String str) {
		return (str == null || str.length() == 0);
	}

	/**
	 * join paras
	 * 
	 * @param parasMap
	 * @return
	 */
	public static String joinParas(Map<String, String> parasMap) {
		if (parasMap == null || parasMap.size() == 0) {
			return null;
		}
		StringBuilder paras = new StringBuilder();
		Iterator<Map.Entry<String, String>> ite = parasMap.entrySet()
				.iterator();
		while (ite.hasNext()) {
			Map.Entry<String, String> entry = ite.next();
			paras.append(entry.getKey()).append(SIGN_EQUAL)
					.append(entry.getValue());
			if (ite.hasNext()) {
				paras.append(PARAMETERS_SEPARATOR);
			}
		}
		return paras.toString();
	}

	/**
	 * inputStream to String
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String inputStream2String(InputStream is) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		int i = -1;
		while ((i = is.read()) != -1) {
			baos.write(i);
		}
		return baos.toString();
	}

	/**
	 * <p>
	 * Get string
	 * </p>
	 */
	public static String getString(Object obj) {
		if (obj == null) {
			return "";
		} else {
			return obj.toString();
		}
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 * 
	 * @param b
	 *            byte array
	 * @return string in utf-8 encoding, or empty if the byte array is not
	 *         encoded with UTF-8
	 */
	public static String getUTF8String(byte[] b) {
		if (b == null)
			return "";
		return getUTF8String(b, 0, b.length);
	}

	/**
	 * <p>
	 * Get string in UTF-8 encoding
	 * </p>
	 */
	public static String getUTF8String(byte[] b, int start, int length) {
		if (b == null) {
			return "";
		} else {
			try {
				return new String(b, start, length, ENCODING_UTF8);
			} catch (UnsupportedEncodingException e) {
				return "";
			}
		}
	}

	/**
	 * <p>
	 * Get UTF8 bytes from a string
	 * </p>
	 * 
	 * @param string
	 *            String
	 * @return UTF8 byte array, or null if failed to get UTF8 byte array
	 */
	public static byte[] getUTF8Bytes(String string) {
		if (string == null)
			return new byte[0];

		try {
			return string.getBytes(ENCODING_UTF8);
		} catch (UnsupportedEncodingException e) {
			/*
			 * If system doesn't support UTF-8, use another way
			 */
			try {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				DataOutputStream dos = new DataOutputStream(bos);
				dos.writeUTF(string);
				byte[] jdata = bos.toByteArray();
				bos.close();
				dos.close();
				byte[] buff = new byte[jdata.length - 2];
				System.arraycopy(jdata, 2, buff, 0, buff.length);
				return buff;
			} catch (IOException ex) {
				return new byte[0];
			}
		}
	}

	/**
	 * <p>
	 * Parse int value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return int value
	 */
	public static int getInt(String value) {
		if (TextUtils.isEmpty(value)) {
			return 0;
		}

		try {
			return Integer.parseInt(value.trim(), 10);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	/**
	 * <p>
	 * Parse float value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return float value
	 */
	public static float getFloat(String value) {
		if (value == null)
			return 0f;

		try {
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	/**
	 * <p>
	 * Parse long value from string
	 * </p>
	 * 
	 * @param value
	 *            string
	 * @return long value
	 */
	public static long getLong(String value) {
		if (value == null)
			return 0L;

		try {
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/**
	 * 格式化非法字符
	 * 
	 * @param source
	 * @return
	 */
	public static String transform(String source) {
		if (source != null && !TextUtils.isEmpty(source)) {
			source.replace("+", "%2B");
			source.replace(" ", "%20");
			source.replace("/", "%2F");
			source.replace("?", "%3F");
			source.replace("%", "%25");
			source.replace("#", "%23");
			source.replace("&", "%26");
			source.replace("=", "%3D");
		} else {
			return "";
		}
		return source;
	}

	public static void V(String msg) {
		if (sDebug) {
			Log.v(sLogTag, msg);
		}
	}

	public static void V(String msg, Throwable e) {
		if (sDebug) {
			Log.v(sLogTag, msg, e);
		}
	}

	public static void D(String msg) {
		if (sDebug) {
			Log.d(sLogTag, msg);
		}
	}

	public static void D(String msg, Throwable e) {
		if (sDebug) {
			Log.d(sLogTag, msg, e);
		}
	}

	public static void I(String msg) {
		if (sDebug) {
			Log.i(sLogTag, msg);
		}
	}

	public static void I(String msg, Throwable e) {
		if (sDebug) {
			Log.i(sLogTag, msg, e);
		}
	}

	public static void W(String msg) {
		if (sDebug) {
			Log.w(sLogTag, msg);
		}
	}

	public static void W(String msg, Throwable e) {
		if (sDebug) {
			Log.w(sLogTag, msg, e);
		}
	}

	public static void E(String msg) {
		if (sDebug) {
			Log.e(sLogTag, msg);
		}
	}

	public static void E(String msg, Throwable e) {
		if (sDebug) {
			Log.e(sLogTag, msg, e);
		}
	}

	/**
	 * 校验Tag Alias 只能是数字,英文字母和中文
	 */
	public static boolean isValidTagAndAlias(String s) {
		Pattern p = Pattern.compile("^[\u4E00-\u9FA50-9a-zA-Z_-]{0,}$");
		Matcher m = p.matcher(s);
		return m.matches();
	}

	public static void showToast(final String toast, final Context context) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Looper.prepare();
				Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
				Looper.loop();
			}
		}).start();
	}

	public static boolean isConnected(Context context) {
		ConnectivityManager conn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = conn.getActiveNetworkInfo();
		return (info != null && info.isConnected());
	}

	/**
	 * 取得AppKey
	 * 
	 * @param context
	 * @return
	 */
	public static String getAppKey(Context context) {
		Bundle metaData = null;
		String appKey = null;
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai)
				metaData = ai.metaData;
			if (null != metaData) {
				appKey = metaData.getString(KEY_APP_KEY);
				if ((null == appKey) || appKey.length() != 24) {
					appKey = null;
				}
			}
		} catch (NameNotFoundException e) {

		}
		return appKey;
	}

	/**
	 * 取得版本号
	 * 
	 * @param context
	 * @return
	 */
	public static String getVersion(Context context) {
		try {
			PackageInfo manager = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return manager.versionName;
		} catch (NameNotFoundException e) {
			return "Unknown";
		}
	}

	/**
	 * 格式化日期（Format：yyyy-MM-dd）
	 * 
	 * @param time
	 * @return
	 */
	public static String formatDate(long time) {
		if (calendar == null || calendar.get() == null) {
			calendar = new WeakReference<Calendar>(Calendar.getInstance());
		}
		Calendar target = calendar.get();
		target.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(target.getTime());
	}

	/**
	 * 格式化时间（Format：yyyy-MM-dd HH:mm）
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String formatTime(long timeInMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return sdf.format(new Date(timeInMillis));
	}

	/**
	 * 格式化时间（Format：yyyy-MM-dd）
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String formatDates(long timeInMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(timeInMillis));
	}

	/**
	 * 获取当前日期
	 * 
	 * @return
	 */
	public static String getTodayDate() {
		if (calendar == null || calendar.get() == null) {
			calendar = new WeakReference<Calendar>(Calendar.getInstance());
		}
		Calendar today = calendar.get();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(today.getTime());
	}

	public static String getImei(Context context, String imei) {
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			imei = telephonyManager.getDeviceId();
		} catch (Exception e) {
			Log.e(Utils.class.getSimpleName(), e.getMessage());
		}
		return imei;
	}

	/**
	 * Returns whether the network is available
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			D("couldn't get connectivity manager");
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0, length = info.length; i < length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns whether the network is roaming
	 */
	public static boolean isNetworkRoaming(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			// Log.w(Constants.TAG, "couldn't get connectivity manager");
		} else {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null
					&& info.getType() == ConnectivityManager.TYPE_MOBILE) {
			} else {
			}
		}
		return false;
	}

	/**
	 * 文件拷贝工具类
	 * 
	 * @param src
	 *            源文件
	 * @param dst
	 *            目标文件
	 * @throws IOException
	 */
	public static void copyFile(InputStream in, FileOutputStream dst)
			throws IOException {
		byte[] buffer = new byte[8192];
		int len = 0;
		while ((len = in.read(buffer)) > 0) {
			dst.write(buffer, 0, len);
		}
		in.close();
		dst.close();
	}

	/**
	 * 解析HTTP String Entity
	 * 
	 * @param response
	 *            HTTP Response
	 * @return API返回的消息(String)
	 */
	public static String getStringResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			return entity == null ? null : EntityUtils.toString(response
					.getEntity());
		} catch (ParseException e) {
			E("getStringResponse meet ParseException", e);
		} catch (IOException e) {
			E("getStringResponse meet IOException", e);
		}
		return null;
	}

	/**
	 * 解析HTTP InputStream Entity
	 * 
	 * @param response
	 *            HTTP Response
	 * @return API返回的消息(InputStream)
	 */
	public static InputStream getInputStreamResponse(HttpResponse response) {
		HttpEntity entity = response.getEntity();
		try {
			if (entity == null) {
				return null;
			}
			return AndroidHttpClient.getUngzippedContent(entity);
		} catch (IllegalStateException e) {
			D("getInputStreamResponse meet IllegalStateException", e);
		} catch (IOException e) {
			D("getInputStreamResponse meet IOException", e);
		}
		return null;
	}

	/**
	 * 界面切换动画
	 * 
	 * @return
	 */
	public static LayoutAnimationController getLayoutAnimation() {
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(100);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	// /**
	// * 创建Tab中的只包含TextView的View
	// */
	// public static View createTabView(Context context, String text) {
	// TextView view = (TextView) LayoutInflater.from(context).inflate(
	// R.layout.common_tab_view, null);
	// view.setText(text);
	// return view;
	// }
	//
	// /**
	// * 创建Search Tab中的只包含TextView的View
	// */
	// public static View createSearchTabView(Context context, String text) {
	// TextView view = (TextView) LayoutInflater.from(context).inflate(
	// R.layout.common_tab_view, null);
	// view.setBackgroundResource(R.drawable.search_tab_selector);
	// view.setTextAppearance(context, R.style.search_tab_text);
	// view.setText(text);
	// return view;
	// }

	/**
	 * Get MD5 Code
	 */
	public static String getMD5(String text) {
		try {
			byte[] byteArray = text.getBytes("utf8");
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(byteArray, 0, byteArray.length);
			return convertToHex(md.digest());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * Convert byte array to Hex string
	 */
	private static String convertToHex(byte[] data) {
		StringBuilder buf = new StringBuilder();
		for (int i = 0; i < data.length; i++) {
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do {
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Check whether the SD card is readable
	 */
	public static boolean isSdcardReadable() {
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)
				|| Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	/**
	 * Check whether the SD card is writable
	 */
	public static boolean isSdcardWritable() {
		final String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	// /**
	// * Show toast information
	// *
	// * @param context
	// * application context
	// * @param text
	// * the information which you want to show
	// * @return show toast dialog
	// */
	// public static void makeEventToast(Context context, String text,
	// boolean isLongToast) {
	//
	// Toast toast = null;
	// if (isLongToast) {
	// toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
	// } else {
	// toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	// }
	// View v = LayoutInflater.from(context)
	// .inflate(R.layout.toast_view, null);
	// TextView textView = (TextView) v.findViewById(R.id.tab_title);
	// textView.setText(text);
	// toast.setView(v);
	// toast.show();
	// }

	/**
	 * get app size
	 * 
	 * @param size
	 * @return
	 */
	public static CharSequence getAppSize(long size) {
		if (size <= 0) {
			return "0M";
		}
		if (size >= MB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		} else if (size >= KB_2_BYTE) {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / KB_2_BYTE))
					.append("K");
		} else {
			return size + "B";
		}
	}

	/**
	 * get app size
	 * 
	 * @param size
	 * @return
	 */
	public static CharSequence getAppSizeByMb(long size) {
		if (size <= 0) {
			return "0M";
		} else {
			return new StringBuilder(16).append(
					DOUBLE_DECIMAL_FORMAT.format((double) size / MB_2_BYTE))
					.append("M");
		}
	}

	/**
	 * 获取所有应用程序
	 */
	public static List<PackageInfo> getAllInstalledApps(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		// ArrayList<String> installed = new ArrayList<String>();
		for (PackageInfo info : packages) {
			apps.add(info);
			// installed.add(info.packageName);
		}
		return apps;
	}

	/**
	 * 获取用户安装的应用列表
	 */
	public static List<PackageInfo> getInstalledApps(Context context) {
		PackageManager pm = context.getPackageManager();
		final String ourPackageName = Session.get(context).getPackageName();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		for (PackageInfo info : packages) {
			// 只返回非系统级应用
			if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
					&& !ourPackageName.equals(info.packageName)) {
				apps.add(info);
			}
		}
		return apps;
	}

	/**
	 * 获取非系统应用
	 */
	public static List<PackageInfo> getUserInstalledApps(Context context) {
		PackageManager pm = context.getPackageManager();
		final String ourPackageName = context.getPackageName();
		List<PackageInfo> packages = pm.getInstalledPackages(0);
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		for (PackageInfo info : packages) {
			// 只返回非系统级应用
			if (((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
					&& !ourPackageName.equals(info.packageName)) {
				apps.add(info);
			}
		}

		return apps;
	}

	/**
	 * 获取非系统APK包名/icon/应用名
	 * 
	 * @param context
	 * @return
	 */
	public static ArrayList<HashMap<String, Object>> getNoSystemApkInfo(
			Context context) {
		List<PackageInfo> appList = getUserInstalledApps(context);
		PackageManager pManager = context.getPackageManager();
		ArrayList<HashMap<String, Object>> appInfos = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < appList.size(); i++) {
			PackageInfo pinfo = appList.get(i);
			String appName = "";
			try {
				appName = pManager.getApplicationLabel(
						pManager.getApplicationInfo(pinfo.packageName,
								PackageManager.GET_META_DATA)).toString();
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			HashMap<String, Object> appInfo = new HashMap<String, Object>();
			appInfo.put(Constants.KEY_PACKAGE_NAME,
					checkEmpty(pinfo.packageName));
			appInfo.put(Constants.KEY_APP_NAME, checkEmpty(appName.trim()));
			appInfo.put(Constants.KEY_VERSION_CODE, (pinfo.versionCode));
			appInfo.put(Constants.KEY_VERSION_NAME,
					checkEmpty(pinfo.versionName));
			String dir = pinfo.applicationInfo.publicSourceDir.toString();
			int appSize = Integer.valueOf((int) new File(dir).length());
			appInfo.put(Constants.KEY_APP_SIZE, appSize);
			// appInfo.put(Constants.APPNAME, checkEmpty(pinfo.applicationInfo
			// .loadLabel(pManager).toString()));
			appInfo.put(Constants.KEY_APP_LOGO,
					pinfo.applicationInfo.loadIcon(pManager));
			appInfos.add(appInfo);
		}
		return appInfos;
	}

	public static String checkEmpty(Object data) {
		if (data == null) {
			return "";
		}
		if (TextUtils.isEmpty(data.toString())) {
			return "";
		} else {
			return data.toString();
		}
	}

	public static String checkEmpty(String data) {
		if (TextUtils.isEmpty(data)) {
			return "";
		} else {
			return data.trim();
		}
	}

	public static String checkValue(String data) {
		if (TextUtils.isEmpty(data)) {
			return "0";
		} else {
			return data.trim();
		}
	}

	public static int checkInt(Object data) {
		int value = 0;
		if (data == null) {
			return 0;
		}

		try {
			value = Integer.valueOf(data.toString());
		} catch (NumberFormatException e) {
			e.printStackTrace();
			return 0;
		}
		return value;
	}

	/*
	 * 获取APK信息(通过file)
	 */
	public static HashMap<String, Object> getApkInfo(Context context, File file) {
		PackageManager pm = context.getPackageManager();
		String filePath = file.getAbsolutePath();
		PackageInfo info = pm.getPackageArchiveInfo(filePath,
				PackageManager.GET_ACTIVITIES);
		if (info == null) {
			return null;
		}
		try {
			ApplicationInfo appInfo = info.applicationInfo;
			// 得到应用名
			// String appName = pm.getApplicationLabel(appInfo).toString();
			String appName = file.getName().split("\\.")[0].toString();
			String packageName = appInfo.packageName; // 得到包名
			String version = info.versionName; // 得到版本信息
			info.applicationInfo.sourceDir = filePath;
			info.applicationInfo.publicSourceDir = filePath;
			Drawable icon = pm.getApplicationIcon(appInfo);
			String dir = appInfo.publicSourceDir.toString();
			int appSize = Integer.valueOf((int) new File(dir).length());
			// Long lastUpdateTime = info.lastUpdateTime;
			// Long firstInstallTime = info.firstInstallTime;
			HashMap<String, Object> apk = new HashMap<String, Object>();
			// apk.put(Constants.LASTUPDATETIME, lastUpdateTime);
			// apk.put(Constants.FIRSTINSTALLTIME, firstInstallTime);
			apk.put(Constants.KEY_APP_LOGO, icon);
			apk.put(Constants.KEY_PACKAGE_NAME, packageName);
			apk.put(Constants.KEY_APP_NAME, appName);
			apk.put(Constants.KEY_APP_SIZE, appSize);
			apk.put(Constants.KEY_PRODUCT_INFO, filePath);
			apk.put(Constants.KEY_PRODUCT_DESCRIPTION, file.getAbsolutePath());
			apk.put(Constants.KEY_VERSION_NAME, version);
			I(appName + " is installed " + packageName);
			return apk;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取apk信息：版本号，名称，图标等(通过文件路径)
	 * 
	 * @param archiveFilePath
	 *            apk包的绝对路径
	 * @param context
	 */
	public static HashMap<String, Object> getApkInfo(String archiveFilePath,
			Context context) {

		PackageManager pm = context.getPackageManager();
		if (TextUtils.isEmpty(archiveFilePath)) {
			return null;
		}
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null) {
			ApplicationInfo appInfo = pkgInfo.applicationInfo;
			/* 必须加这两句，不然下面icon获取是default icon而不是应用包的icon */
			appInfo.sourceDir = archiveFilePath;
			appInfo.publicSourceDir = archiveFilePath;
			String dir = appInfo.publicSourceDir;
			int size = Integer.valueOf((int) new File(dir).length());
			String appName = pm.getApplicationLabel(appInfo).toString();// 得到应用名
			String packageName = pkgInfo.packageName; // 得到包名
			String versionName = pkgInfo.versionName; // 得到版本信息
			int versionCode = pkgInfo.versionCode; // 得到版本信息
			/* icon1和icon2其实是一样的 */
			Drawable icon1 = pm.getApplicationIcon(appInfo);// 得到图标信息
			// Drawable icon2 = appInfo.loadIcon(pm);

			HashMap<String, Object> pkginfo = new HashMap<String, Object>();
			pkginfo.put(Constants.KEY_PACKAGE_NAME, packageName);
			pkginfo.put(Constants.KEY_VERSION_NAME, versionName);
			pkginfo.put(Constants.KEY_VERSION_CODE, versionCode);
			pkginfo.put(Constants.KEY_APP_NAME, appName);
			pkginfo.put(Constants.KEY_APP_SIZE, size);
			pkginfo.put(Constants.KEY_APP_LOGO, icon1);
			return pkginfo;
		}
		return null;
	}

	/**
	 * 获取apk包名
	 * 
	 * @param archiveFilePath
	 *            apk包的绝对路径
	 * @param context
	 */
	public static String getPkgName(String archiveFilePath, Context context) {

		PackageManager pm = context.getPackageManager();
		PackageInfo pkgInfo = pm.getPackageArchiveInfo(archiveFilePath,
				PackageManager.GET_ACTIVITIES);
		if (pkgInfo != null) {
			String packageName = pkgInfo.packageName; // 得到包名
			return packageName;
		}
		return "";
	}

	/**
	 * 删除安装包文件
	 */
	public static boolean deleteFile(String path) {
		File file = null;
		try {
			if (path != null && !TextUtils.isEmpty(path)) {
				file = new File(path);
				if (file != null && file.exists() && file.isFile()) {
					return file.delete();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
	private static int UNINSTALLED = 1; // 表示未安装
	private static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新

	// /**
	// * 判断该应用在手机中的安装情况 0: 表示已经安装，且跟现在这个apk文件是一个版本 1:表示未安装
	// * 2:表示已经安装，版本比现在这个版本要低，可以点击按钮更新
	// *
	// * @param pm
	// * PackageManager
	// * @param packageName
	// * 要判断应用的包名
	// * @param versionCode
	// * 要判断应用的版本号
	// *
	// */
	// public static int checkType(Context context, String packageName) {
	// PackageManager pm = context.getPackageManager();
	// int versionCode = -1;
	// D(" checkType  " + packageName);
	// try {
	// versionCode = pm.getPackageInfo(packageName, 0).versionCode;
	// } catch (NameNotFoundException e) {
	// e.printStackTrace();
	// }
	//
	// List<PackageInfo> pakageinfos = pm
	// .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
	// for (PackageInfo pi : pakageinfos) {
	// String pi_packageName = pi.packageName;
	// int pi_versionCode = pi.versionCode;
	// D(" versionCode  " + versionCode + "  pi_packageName  "
	// + pi_packageName + "  pi_versionCode  " + pi_versionCode);
	// // 如果这个包名在系统已经安装过的应用中存在
	// if (packageName.endsWith(pi_packageName)) {
	// if (versionCode == pi_versionCode) {
	// I("已经安装，不用更新，可以卸载该应用");
	// return INSTALLED;
	// } else if (versionCode > pi_versionCode) {
	// I("已经安装，有更新");
	// return INSTALLED_UPDATE;
	// }
	// }
	// }
	// I("未安装该应用，可以安装");
	// return UNINSTALLED;
	// }

	/**
	 * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
	 * 
	 * @param time
	 *            需要格式化的时间 如"2014-07-14 19:01:45"
	 * @param pattern
	 *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
	 *            <p/>
	 *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
	 * @return time为null，或者时间格式不匹配，输出空字符""
	 */
	public static String formatDisplayTime(String time, String pattern) {
		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;

		if (time != null) {
			try {
				Date tDate = new SimpleDateFormat(pattern).parse(time);
				Date today = new Date();
				SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
				Date thisYear = new Date(thisYearDf.parse(
						thisYearDf.format(today)).getTime());
				Date yesterday = new Date(todayDf.parse(todayDf.format(today))
						.getTime());
				Date beforeYes = new Date(yesterday.getTime() - tDay);
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
					long dTime = today.getTime() - tDate.getTime();
					if (tDate.before(thisYear)) {
						display = new SimpleDateFormat("yyyy年MM月dd日")
								.format(tDate);
					} else {
						if (dTime < tMin) {
							display = "刚刚";
						} else if (dTime < tHour) {
							display = (int) Math.ceil(dTime / tMin) + "分钟前";
						} else if (dTime < tDay && tDate.after(yesterday)) {
							display = (int) Math.ceil(dTime / tHour) + "小时前";
						} else if (tDate.after(beforeYes)
								&& tDate.before(yesterday)) {
							display = "昨天"
									+ new SimpleDateFormat("HH:mm")
											.format(tDate);
						} else {
							display = halfDf.format(tDate);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return display;
	}

	/**
	 * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
	 * 
	 * @param time
	 *            需要格式化的时间 如"2014-07-14 19:01:45"
	 * @return time为null，或者时间格式不匹配，输出空字符""
	 */
	public static String formatDisplayTime(long time) {
		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;

		if (time != 0) {
			try {
				Date tDate = new Date(time);
				Date today = new Date();
				SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
				Date thisYear = new Date(thisYearDf.parse(
						thisYearDf.format(today)).getTime());
				Date yesterday = new Date(todayDf.parse(todayDf.format(today))
						.getTime());
				Date beforeYes = new Date(yesterday.getTime() - tDay);
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
					long dTime = today.getTime() - tDate.getTime();
					if (tDate.before(thisYear)) {
						display = new SimpleDateFormat("yyyy年MM月dd日")
								.format(tDate);
					} else {
						if (dTime < tMin) {
							display = "刚刚";
						} else if (dTime < tHour) {
							display = (int) Math.ceil(dTime / tMin) + "分钟前";
						} else if (dTime < tDay && tDate.after(yesterday)) {
							display = (int) Math.ceil(dTime / tHour) + "小时前";
						} else if (tDate.after(beforeYes)
								&& tDate.before(yesterday)) {
							display = "昨天"
									+ new SimpleDateFormat("HH:mm")
											.format(tDate);
						} else {
							display = halfDf.format(tDate);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return display;
	}

	/**
	 * 格式化时间（Format：MM-dd HH:mm）
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String formatTimeMonth(long timeInMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
		return sdf.format(new Date(timeInMillis));
	}

	/**
	 * 格式化时间（Format：MM-dd ）
	 * 
	 * @param timeInMillis
	 * @return
	 */
	public static String formatMonth(long timeInMillis) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM-dd");
		return sdf.format(new Date(timeInMillis));
	}

	/**
	 * 比较两个文件的签名是否一致
	 */
	public static boolean compareFileWithSignature(String path1, String path2) {

		long start = System.currentTimeMillis();
		if (TextUtils.isEmpty(path1) || TextUtils.isEmpty(path2)) {
			return false;
		}

		String signature1 = getFileSignatureMd5(path1);
		String signature2 = getFileSignatureMd5(path2);

		V("compareFileWithSignature total time is "
				+ (System.currentTimeMillis() - start));
		if (!TextUtils.isEmpty(signature1) && signature1.equals(signature2)) {
			return true;
		}
		return false;
	}

	/**
	 * 获取应用签名MD5
	 */
	public static String getFileSignatureMd5(String targetFile) {
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(targetFile);
			// 取RSA公钥
			JarEntry jarEntry = jarFile.getJarEntry("AndroidManifest.xml");

			if (jarEntry != null) {
				InputStream is = jarFile.getInputStream(jarEntry);
				byte[] buffer = new byte[8192];
				while (is.read(buffer) > 0) {
					// do nothing
				}
				is.close();
				Certificate[] certs = jarEntry == null ? null : jarEntry
						.getCertificates();
				if (certs != null && certs.length > 0) {
					String rsaPublicKey = String.valueOf(certs[0]
							.getPublicKey());
					return getMD5(rsaPublicKey);
				}
			}
		} catch (IOException e) {
			W("occur IOException when get file signature", e);
		} finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
		}
		return "";
	}

	// /**
	// * Show toast information
	// *
	// * @param context
	// * application context
	// * @param text
	// * the information which you want to show
	// * @return show toast dialog
	// */
	// public static void makeEventToast(Context context, String text,
	// boolean isLongToast) {
	//
	// Toast toast = null;
	// if (isLongToast) {
	// toast = Toast.makeText(context, "", Toast.LENGTH_LONG);
	// } else {
	// toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
	// }
	// View v = LayoutInflater.from(context)
	// .inflate(R.layout.toast_view, null);
	// TextView textView = (TextView) v.findViewById(R.id.tab_title);
	// textView.setText(text);
	// toast.setView(v);
	// toast.show();
	// }

	/**
	 * 解析二维码地址
	 */
	public static HashMap<String, String> parserUri(Uri uri) {
		HashMap<String, String> parameters = new HashMap<String, String>();
		String paras[] = uri.getQuery().split("&");
		for (String s : paras) {
			if (s.indexOf("=") != -1) {
				String[] item = s.split("=");
				parameters.put(item[0], item[1]);
			} else {
				return null;
			}
		}
		return parameters;
	}

	/**
	 * 检查默认Proxy
	 */
	public static HttpHost detectProxy(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable()
				&& ni.getType() == ConnectivityManager.TYPE_MOBILE) {
			String proxyHost = android.net.Proxy.getDefaultHost();
			int port = android.net.Proxy.getDefaultPort();
			if (proxyHost != null) {
				return new HttpHost(proxyHost, port, "http");
			}
		}
		return null;
	}

	/**
	 * 获取已装应用包名
	 * 
	 * @param apps
	 * @return
	 */
	public static ArrayList<String> getAllInstalledPkg(List<PackageInfo> apps) {
		ArrayList<String> pkgList = new ArrayList<String>();
		if (apps != null && apps.size() > 0) {
			for (PackageInfo info : apps) {
				pkgList.add(info.packageName);
			}
			return pkgList;
		}
		return null;
	}

	/**
	 * 安装应用(通过path)
	 * 
	 * @param context
	 *            Application Context
	 * @param filePath
	 *            APK文件路径
	 */
	public static void installApk(Context context, String filePath,
			String appid, long downId) {
		try {
			DownloadManager manager = DownloadManager.getInstance(context);
			File file = new File(filePath);
			if (file != null && file.length() > 0 && file.exists()
					&& file.isFile()) {
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				Uri uri = Uri.fromFile(file);
				i.setDataAndType(uri, "application/vnd.android.package-archive");
				((ContextWrapper) context).startActivity(i);
				HashMap<String, Object> apkInfo = Utils.getApkInfo(filePath,
						context);
				if (apkInfo != null) {
					String pkgName = Utils.checkEmpty(apkInfo
							.get(Constants.KEY_PACKAGE_NAME));
					int versionCode = Utils.checkInt(apkInfo
							.get(Constants.KEY_VERSION_CODE));
					String versionName = Utils.checkEmpty(apkInfo
							.get(Constants.KEY_VERSION_NAME));
					if (!manager.queryPackageName(downId).equals(pkgName)) {
						manager.updatePkgName(pkgName, downId + "");
					}
					if (manager.queryVersionCode(downId) != versionCode) {
						manager.updateVersionCode(versionCode, downId + "");
					}
					if (!manager.queryVersionName(downId).equals(versionName)) {
						manager.updateVersionName(versionName, downId + "");
					}
				}
			} else {
				ToastUtil
						.showShortToast(
								context,
								context.getString(R.string.install_fail_file_not_exist));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// /**
	// * 安装应用(通过file)
	// *
	// * @param context
	// * Application Context
	// * @param filePath
	// * APK文件路径
	// */
	// public static void installApk(Context context, File file) {
	// try {
	// if (file != null && file.length() > 0 && file.exists()
	// && file.isFile()) {
	// Intent i = new Intent(Intent.ACTION_VIEW);
	// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// i.setDataAndType(Uri.fromFile(file),
	// "application/vnd.android.package-archive");
	// D(" installApk " + i.getPackage());
	// ((ContextWrapper) context).startActivity(i);
	//
	// } else {
	// ToastUtil
	// .showShortToast(
	// context,
	// context.getString(R.string.install_fail_file_not_exist));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * 查询手机内所有支持分享的应用 通过 PackageInfo 获取具体信息方法： 包名获取方法：packageInfo.packageName
	 * icon获取获取方法：packageManager.getApplicationIcon(applicationInfo)
	 * 应用名称获取方法：packageManager.getApplicationLabel(applicationInfo)
	 * 使用权限获取方法：packageManager.getPackageInfo(packageName,PackageManager.
	 * GET_PERMISSIONS) .requestedPermissions
	 * 
	 * 通过 ResolveInfo 获取具体信息方法： 包名获取方法：resolve.activityInfo.packageName
	 * icon获取获取方法：resolve.loadIcon(packageManager)
	 * 应用名称获取方法：resolve.loadLabel(packageManager).toString()
	 * 
	 * @param context
	 * @return
	 */
	public static List<ResolveInfo> getShareApps(Context context) {
		List<ResolveInfo> mApps = new ArrayList<ResolveInfo>();
		Intent intent = new Intent(Intent.ACTION_SEND, null);
		intent.addCategory(Intent.CATEGORY_DEFAULT);
		intent.setType("text/plain");
		PackageManager pManager = context.getPackageManager();
		mApps = pManager.queryIntentActivities(intent,
				PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);

		return mApps;
	}

	/**
	 * 获取Max助手下载的应用文件
	 */
	public static ArrayList<HashMap<String, Object>> getLocalApks(
			Context context) {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			File root = new File(Environment.getExternalStorageDirectory()
					.getAbsolutePath(), Constants.ROOT_DIR);
			ArrayList<HashMap<String, Object>> apks = new ArrayList<HashMap<String, Object>>();
			getApkList(context, root, apks);
			return apks;
		}
		return null;
	}

	/**
	 * 遍历maxhelper APK文件
	 */
	private static void getApkList(Context context, File root,
			ArrayList<HashMap<String, Object>> apkList) {

		// int index = 0;
		// File marketRoot = new File(root, Constants.ROOT_DIR);
		// boolean hasMarket = false;
		if (root.exists()) {
			File[] children = root.listFiles();
			if (children.length > 0) {
				for (File child : children) {
					if (!child.isDirectory()) {
						if (child.getName().endsWith(".apk")) {
							HashMap<String, Object> item = getApkInfo(context,
									child);
							if (item != null) {
								// index++;
								apkList.add(item);
							}
						}
					}
				}
				// if (index > 0) {
				// hasMarket = true;
				// HashMap<String, Object> group = new HashMap<String,
				// Object>();
				// // TODO === TODO
				// // group.put(Constants.KEY_PRODUCT_NAME,
				// // context.getString(R.string.apk_title_market) + "("
				// // + marketRoot.getAbsolutePath() + ")");
				// // group.put(Constants.KEY_PLACEHOLDER, true);
				// // TODO === TODO
				// apkList.add(0, group);
				// }
			}
		}
	}

	/*
	 * 获取APK信息(通过file)
	 */

	/**
	 * 卸载应用
	 * 
	 * @param context
	 *            应用上下文
	 * @param pkgName
	 *            包名
	 */
	public static void uninstallApk(Context context, String pkgName) {
		Uri packageURI = Uri.parse("package:" + pkgName);
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI);
		uninstallIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(uninstallIntent);
	}

	/**
	 * Whether the APK was installed
	 * 
	 * @param context
	 *            :the Context
	 * @param packageName
	 *            :the apk packageName(包名)
	 * @return
	 */
	public static boolean isAppInstalled(Context context, String packageName) {
		PackageManager pm = context.getPackageManager();
		boolean installed = false;
		try {
			pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
			// pm.getPackageInfo(packageName, 0);
			installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			// 捕捉到异常,说明未安装
			installed = false;
		}
		return installed;
	}

	public final static String getMeta(Context context, String appKey) {
		ApplicationInfo info = null;
		try {
			info = context.getPackageManager().getApplicationInfo(
					context.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String meta = info.metaData.getString(appKey);
		return meta;
	}

	// private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
	// private static int UNINSTALLED = 1; // 表示未安装
	// private static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新
	//
	// /**
	// * 判断该应用在手机中的安装情况 0: 表示已经安装，且跟现在这个apk文件是一个版本 1:表示未安装
	// * 2:表示已经安装，版本比现在这个版本要低，可以点击按钮更新
	// *
	// * @param pm
	// * PackageManager
	// * @param packageName
	// * 要判断应用的包名
	// * @param versionCode
	// * 要判断应用的版本号
	// *
	// */
	// public static int checkType(Context context, String packageName) {
	// PackageManager pm = context.getPackageManager();
	// int versionCode = -1;
	// try {
	// versionCode = pm.getPackageInfo(packageName, 0).versionCode;
	// } catch (NameNotFoundException e) {
	// e.printStackTrace();
	// }
	// List<PackageInfo> pakageinfos = pm
	// .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
	// for (PackageInfo pi : pakageinfos) {
	// String pi_packageName = pi.packageName;
	// int pi_versionCode = pi.versionCode;
	// D(" versionCode  " + versionCode + "  pi_packageName  "
	// + pi_packageName + "  pi_versionCode  " + pi_versionCode);
	// // 如果这个包名在系统已经安装过的应用中存在
	// if (packageName.endsWith(pi_packageName)) {
	// if (versionCode == pi_versionCode) {
	// I("已经安装，不用更新，可以卸载该应用");
	// return INSTALLED;
	// } else if (versionCode > pi_versionCode) {
	// I("已经安装，有更新");
	// return INSTALLED_UPDATE;
	// }
	// }
	// }
	// I("未安装该应用，可以安装");
	// return UNINSTALLED;
	// }

	public static HashMap<String, Integer> checkUpdate(Context context) {
		List<PackageInfo> packageInfos = getUserInstalledApps(context);
		for (PackageInfo packageInfo : packageInfos) {

		}

		return null;
	}

	/**
	 * check Apk exist
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean checkApkExist(Context context, Intent intent) {
		List<ResolveInfo> list = context.getPackageManager()
				.queryIntentActivities(intent, 0);
		if (list.size() > 0) {
			return true;
		}
		return false;
	}

	/**
	 * 打开apk
	 */
	public static void startAPP(Context context, String packageName) {
		try {
			Intent intent = context.getPackageManager()
					.getLaunchIntentForPackage(packageName)
					.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			if (intent != null) {
				context.startActivity(intent);
			}
		} catch (Exception e) {
			Toast.makeText(context, "安装包有问题,请检查.", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 打开apk
	 */
	public static void openApk(Context context, String filePath) {
		PackageManager manager = context.getPackageManager();
		PackageInfo info = null;
		try {
			info = manager.getPackageArchiveInfo(filePath,
					PackageManager.GET_ACTIVITIES);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (info != null) {
			Intent intent = manager
					.getLaunchIntentForPackage(info.applicationInfo.packageName);
			context.startActivity(intent);
		}
	}

	/**
	 * 获取通知栏 下载进度百分比
	 * 
	 * @param progress
	 * @param max
	 * @return
	 */
	public static String getNotiPercent(long progress, long max) {
		double rate = 0;
		if (progress <= 0 || max <= 0) {
			rate = 0D;
		} else if (progress > max) {
			rate = 100D;
		} else {
			DecimalFormat df = new DecimalFormat("#.0");
			rate = Double.valueOf(df.format((double) progress / max * 100));
		}
		return new StringBuilder(16).append(rate).append("%").toString();
	}

	/**
	 * 计算下载剩余进度
	 */
	public static String calculateRemainBytes(Context ctx, float current,
			float total) {

		float remain = total - current;
		remain = remain > 0 ? remain : 0;
		String text = "";
		final String megaBytes = "M";
		final String kiloBytes = "K";
		final String bytes = "B";
		if (remain > 1000000) {
			text = ctx.getString(R.string.download_remain_bytes,
					String.format("%.02f", (remain / 1000000)), megaBytes);
		} else if (remain > 1000) {
			text = ctx.getString(R.string.download_remain_bytes,
					String.format("%.02f", (remain / 1000)), kiloBytes);
		} else {
			text = ctx.getString(R.string.download_remain_bytes, (int) remain,
					bytes);
		}
		return text;
	}

	public static final void toHomeActivity(Activity activity) {
		toOtherClass(activity, MainActivity.class, Constants.RIGHT_TO_LEFT);
	}

	/**
	 * 
	 * @param activity
	 *            当前activity
	 * @param cls
	 *            目标activity
	 * @param code
	 *            动画编号
	 */
	public static final void toOtherClass(Activity activity, Class<?> cls,
			int code) {
		toOtherClass(activity, cls, null, code);
	}

	/**
	 * 默认动画从右到左
	 * 
	 * @param activity
	 *            当前activity
	 * @param cls
	 *            目标activity
	 * @param code
	 *            动画编号
	 */
	public static final void toOtherClass(Activity activity, Class<?> cls) {
		toOtherClass(activity, cls, null, Constants.RIGHT_TO_LEFT);
	}

	/**
	 * 带动画切换的finish 从左到右
	 * 
	 * @param activity
	 */
	public static final void finish(Activity activity) {
		activity.finish();
		activity.overridePendingTransition(R.anim.slide_left_in,
				R.anim.slide_right_out);
	};

	public static final void toOtherClass(Activity activity, Class<?> cls,
			Bundle data) {
		Intent intent = new Intent(activity, cls);
		if (data != null) {
			intent.putExtras(data);
		}
		activity.startActivity(intent);
		int version = android.os.Build.VERSION.SDK_INT;
		if (version > 5) {
			activity.overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
		}
	}

	/**
	 * 带动画切换的activity跳转
	 * 
	 * @param activity
	 *            当前activity
	 * @param cls
	 *            目标activity
	 * @param data
	 *            传输的数据
	 * @param code
	 *            动画编号
	 */
	public static final void toOtherClass(Activity activity, Class<?> cls,
			Bundle data, int code) {
		Intent intent = new Intent(activity, cls);
		if (data != null) {
			intent.putExtras(data);
		}
		activity.startActivity(intent);
		int version = android.os.Build.VERSION.SDK_INT;
		switch (code) {
		case Constants.ZOOM_OUT_IN:
			// 放大缩小跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.zoom_in,
						R.anim.zoom_out);
			}
			break;
		case Constants.ZOOM_IN_OUT:
			// 放大缩小跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.zoom_out,
						R.anim.zoom_in);
			}
			break;

		case Constants.LEFT_TO_RIGHT:
			// 左向右跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.slide_left_in,
						R.anim.slide_right_out);
			}
			break;
		case Constants.RIGHT_TO_LEFT:
			// 右向左跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_left_out);
			}
			break;
		case Constants.BOTTOM_TO_TOP:
			// 底部到顶部
			if (version > 5) {
				activity.overridePendingTransition(R.anim.abc_slide_in_bottom,
						R.anim.abc_slide_in_top);
			}
			break;
		case Constants.TOP_TO_BOTTOM:
			// 顶部到底部
			if (version > 5) {
				activity.overridePendingTransition(R.anim.abc_slide_in_top,
						R.anim.abc_slide_out_bottom);
			}
			break;
		default:
			break;
		}
	}

	public static final void overridePendingTransition(int enterAnim,
			int exitAnim, Activity activity) {
		activity.overridePendingTransition(enterAnim, exitAnim);
	}

	public static final void overridePendingTransition(Activity activity) {
		int version = android.os.Build.VERSION.SDK_INT;
		if (version > 5) {
			activity.overridePendingTransition(R.anim.slide_right_in,
					R.anim.slide_left_out);
		}
	}

	public static final void overridePendingTransition(Activity activity,
			int code) {
		int version = android.os.Build.VERSION.SDK_INT;
		switch (code) {
		case Constants.ZOOM_OUT_IN:
			// 放大缩小跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.zoom_in,
						R.anim.zoom_out);
			}
			break;
		case Constants.ZOOM_IN_OUT:
			// 放大缩小跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.zoom_out,
						R.anim.zoom_in);
			}
			break;

		case Constants.LEFT_TO_RIGHT:
			// 左向右跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.slide_left_in,
						R.anim.slide_right_out);
			}
			break;
		case Constants.RIGHT_TO_LEFT:
			// 右向左跳转
			if (version > 5) {
				activity.overridePendingTransition(R.anim.slide_right_in,
						R.anim.slide_left_out);
			}
			break;

		case Constants.BOTTOM_TO_TOP:
			// 底部到顶部
			if (version > 5) {
				activity.overridePendingTransition(R.anim.abc_slide_in_bottom,
						R.anim.abc_slide_in_top);
			}
			break;
		case Constants.TOP_TO_BOTTOM:
			// 顶部到底部
			if (version > 5) {
				activity.overridePendingTransition(R.anim.abc_slide_in_top,
						R.anim.abc_slide_out_bottom);
			}
			break;
		default:
			break;
		}
	}

	public static String submitLogs() {
		Process mLogcatProc = null;
		BufferedReader reader = null;
		try {
			mLogcatProc = Runtime.getRuntime().exec(
					new String[] { "logcat", "-d", "Max助手:v" });

			reader = new BufferedReader(new InputStreamReader(
					mLogcatProc.getInputStream()));

			String line;
			final StringBuilder log = new StringBuilder();
			String separator = System.getProperty("line.separator");

			while ((line = reader.readLine()) != null) {
				log.append(line);
				log.append(separator);
			}
			return log.toString();

			// do whatever you want with the log. I'd recommend using Intents to
			// create an email
		} catch (IOException e) {
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
				}
		}
		return "";
	}

	/**
	 * 清除缓存
	 * 
	 * @param context
	 */
	public static void clearCache(Context context) {
		File file = Environment.getDownloadCacheDirectory();
		File[] files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
		file = context.getCacheDir();
		files = file.listFiles();
		if (files != null) {
			for (File f : files) {
				f.delete();
			}
		}
	}

	/**
	 * 获取手机信息大全(IMEI 型号 版本等)
	 * 
	 * @param context
	 * @return
	 */
	public static HashMap<String, Object> getTelInfo(Context context) {
		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = telephonyManager.getDeviceId();
		String model = android.os.Build.MODEL;
		String version = android.os.Build.VERSION.RELEASE;
		String sdkVersion = android.os.Build.VERSION.SDK;
		String imsi = telephonyManager.getSubscriberId();
		String number = telephonyManager.getLine1Number(); // 手机号码，有的可得，有的不可得
		HashMap<String, Object> telInfo = new HashMap<String, Object>();
		telInfo.put(Constants.KEY_IMEI, imei);
		telInfo.put(Constants.KEY_MODEL, model);
		telInfo.put(Constants.KEY_SDK_VERSION, sdkVersion);
		telInfo.put(Constants.KEY_VERSION_NAME, version);
		telInfo.put(Constants.KEY_IMSI, imsi);
		telInfo.put(Constants.KEY_NUMBER, number);
		return telInfo;
	}

	// 判断手机格式是否正确
	public final static boolean isMobileNO(String mobiles) {
		Pattern p = Pattern
				.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

	// 判断email格式是否正确
	public final static boolean isEmail(String email) {
		String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
		Pattern p = Pattern.compile(str);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	// 判断是否全是数字
	public final static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	/**
	 * 直接通过HTTP协议提交数据到服务器,实现表单提交功能
	 * 
	 * @time 2012/8/3
	 * @YPF 添加了以流的方式带参数post提交相册图片
	 */
	public static String postPhoto(String actionUrl, String formName,
			HashMap<String, String> params, byte[] buffer, Context context) {
		try {
			String BOUNDARY = "--------------et567z"; // 数据分隔线
			String MULTIPART_FORM_DATA = "Multipart/form-data";
			URL url = new URL(actionUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);// 允许输入
			conn.setDoOutput(true);// 允许输出
			conn.setUseCaches(false);// 不使用Cache
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type", MULTIPART_FORM_DATA
					+ ";boundary=" + BOUNDARY);
			StringBuilder sb = new StringBuilder();
			// 上传的表单参数部分，格式请参考文章
			for (Map.Entry<String, String> entry : params.entrySet()) {// 构建表单字段内容
				sb.append("--");
				sb.append(BOUNDARY);
				sb.append("\r\n");
				sb.append("Content-Disposition: form-data; name=\""
						+ entry.getKey() + "\"\r\n\r\n");
				sb.append(entry.getValue());
				sb.append("\r\n");
			}
			DataOutputStream outStream = new DataOutputStream(
					conn.getOutputStream());
			outStream.write(sb.toString().getBytes());// 发送表单字段数据

			// 上传的文件部分，格式请参考文章
			StringBuilder split = new StringBuilder();
			split.append("--");
			split.append(BOUNDARY);
			split.append("\r\n");
			split.append("Content-Disposition: form-data;name=\"" + formName
					+ "\";filename=\"temp.jpg\"\r\n");
			split.append("Content-Type: image/jpeg\r\n\r\n");
			outStream.write(split.toString().getBytes());
			outStream.write(buffer, 0, buffer.length);
			outStream.write("\r\n".getBytes());
			byte[] end_data = ("--" + BOUNDARY + "--\r\n").getBytes();// 数据结束标志
			outStream.write(end_data);
			outStream.flush();
			/* 取得Response内容 */
			InputStream is = conn.getInputStream();
			// E("  is  " + (char) is.read());
			int ch;
			StringBuffer returnStr = new StringBuffer();
			while ((ch = is.read()) != -1) {
				returnStr.append((char) ch);
			}

			/* 将Response显示于Dialog */
			outStream.close();
			conn.disconnect();
			E("upload-=-   " + returnStr);
			return returnStr.toString().trim();
		} catch (Exception e) {
			ToastUtil.showShortToast(context, "上传图片错误,请检查网络.");
			e.printStackTrace();
			return null;
		}
	}

	// /**
	// * 0:获取已下载 1:总下载大小 2:下载状态
	// *
	// * @param downloadId
	// * @return
	// */
	// public final static int[] getBytesAndStatus(long downloadId,
	// DownloadManager mDownloadManager) {
	// int[] bytesAndStatus = new int[] { -1, -1, 0 };
	// DownloadManager.Query query = new DownloadManager.Query()
	// .setFilterById(downloadId);
	// Cursor c = null;
	// try {
	// c = mDownloadManager.query(query);
	// if (c != null && c.moveToFirst()) {
	// bytesAndStatus[0] = c
	// .getInt(c
	// .getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
	// bytesAndStatus[1] = c
	// .getInt(c
	// .getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
	// bytesAndStatus[2] = c.getInt(c
	// .getColumnIndex(DownloadManager.COLUMN_STATUS));
	// }
	// } finally {
	// if (c != null) {
	// c.close();
	// }
	// }
	// return bytesAndStatus;
	// }

	/**
	 * 设置TabHost的tab项
	 * 
	 * @param index
	 * @return
	 */
	public static View getIndicatorView(Activity activity, int res, int index,
			String[] titles, int[] drawableRes) {
		View view = activity.getLayoutInflater().inflate(res, null);
		ImageView ivLogo = (ImageView) view.findViewById(R.id.tab_icon);
		TextView tvTitle = (TextView) view.findViewById(R.id.tab_title);
		ivLogo.setImageResource(drawableRes[index]);
		tvTitle.setText(titles[index]);
		return view;
	}

	/**
	 * 设置TabHost的tab项
	 * 
	 * @param index
	 * @return
	 */
	public static View getIndicatorTitle(Activity activity, int res, int index,
			String[] titles) {
		View view = activity.getLayoutInflater().inflate(res, null);
		ImageView ivLogo = (ImageView) view.findViewById(R.id.tab_icon);
		TextView tvTitle = (TextView) view.findViewById(R.id.tab_title);
		tvTitle.setText(titles[index]);
		ivLogo.setVisibility(View.GONE);
		return view;
	}

	public static void HideOrToggleSoftInput(Context context) {
		// 隐藏输入法
		InputMethodManager imm = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		// 显示或者隐藏输入法
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * 关闭输入法
	 */
	public static void HideSoftInput(Activity context) {
		// 输入法
		InputMethodManager imm = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(context.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static void ToggleSoftInput(Context context, View view) {
		// 输入法
		InputMethodManager imm = (InputMethodManager) context
				.getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
	}

	public static ApplicationInfo showUninstallAPKIcon(Context context,
			String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";
		String PATH_AssetManager = "android.content.res.AssetManager";
		try {
			// apk包的文件路径
			// 这是一个Package 解释器, 是隐藏的
			// 构造函数的参数只有一个, apk文件的路径
			// PackageParser packageParser = new PackageParser(apkPath);
			Class pkgParserCls = Class.forName(PATH_PackageParser);
			Class[] typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Constructor pkgParserCt = pkgParserCls.getConstructor(typeArgs);
			Object[] valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			Object pkgParser = pkgParserCt.newInstance(valueArgs);
			D("ANDROID_LAB pkgParser:" + pkgParser.toString());
			// 这个是与显示有关的, 里面涉及到一些像素显示等等, 我们使用默认的情况
			DisplayMetrics metrics = new DisplayMetrics();
			metrics.setToDefaults();
			// PackageParser.Package mPkgInfo = packageParser.parsePackage(new
			// File(apkPath), apkPath,
			// metrics, 0);
			typeArgs = new Class[4];
			typeArgs[0] = File.class;
			typeArgs[1] = String.class;
			typeArgs[2] = DisplayMetrics.class;
			typeArgs[3] = Integer.TYPE;
			Method pkgParser_parsePackageMtd = pkgParserCls.getDeclaredMethod(
					"parsePackage", typeArgs);
			valueArgs = new Object[4];
			valueArgs[0] = new File(apkPath);
			valueArgs[1] = apkPath;
			valueArgs[2] = metrics;
			valueArgs[3] = 0;
			Object pkgParserPkg = pkgParser_parsePackageMtd.invoke(pkgParser,
					valueArgs);
			// 应用程序信息包, 这个公开的, 不过有些函数, 变量没公开
			// ApplicationInfo info = mPkgInfo.applicationInfo;
			Field appInfoFld = pkgParserPkg.getClass().getDeclaredField(
					"applicationInfo");
			ApplicationInfo info = (ApplicationInfo) appInfoFld
					.get(pkgParserPkg);
			// uid 输出为"-1"，原因是未安装，系统未分配其Uid。
			D("pkg:" + info.packageName + " uid=" + info.uid);
			// Resources pRes = getResources();
			// AssetManager assmgr = new AssetManager();
			// assmgr.addAssetPath(apkPath);
			// Resources res = new Resources(assmgr, pRes.getDisplayMetrics(),
			// pRes.getConfiguration());
			Class assetMagCls = Class.forName(PATH_AssetManager);
			Constructor assetMagCt = assetMagCls.getConstructor((Class[]) null);
			Object assetMag = assetMagCt.newInstance((Object[]) null);
			typeArgs = new Class[1];
			typeArgs[0] = String.class;
			Method assetMag_addAssetPathMtd = assetMagCls.getDeclaredMethod(
					"addAssetPath", typeArgs);
			valueArgs = new Object[1];
			valueArgs[0] = apkPath;
			assetMag_addAssetPathMtd.invoke(assetMag, valueArgs);
			Resources res = context.getResources();
			typeArgs = new Class[3];
			typeArgs[0] = assetMag.getClass();
			typeArgs[1] = res.getDisplayMetrics().getClass();
			typeArgs[2] = res.getConfiguration().getClass();
			Constructor resCt = Resources.class.getConstructor(typeArgs);
			valueArgs = new Object[3];
			valueArgs[0] = assetMag;
			valueArgs[1] = res.getDisplayMetrics();
			valueArgs[2] = res.getConfiguration();
			res = (Resources) resCt.newInstance(valueArgs);
			CharSequence label = null;
			if (info.labelRes != 0) {
				label = res.getText(info.labelRes);
			}
			return info;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 裁减图片
	 * 
	 * @param uri
	 *            图片uri
	 * @param outputX
	 *            图片宽度
	 * @param outputY
	 *            图片高度
	 * @param requestCode
	 *            请求码
	 */
	public final static void cropImageUri(Uri uri, int outputX, int outputY,
			int requestCode, Fragment context) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 纵横比
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		context.startActivityForResult(intent, requestCode);
	}

	/**
	 * 用当前时间给取得的图片命名
	 * 
	 */
	public final static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		String timeStamp = new SimpleDateFormat("'IMG'_yyyy-MM-dd HH:mm:ss")
				.format(date);
		return timeStamp + ".jpg";
	}

	public static final byte[] Bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	/**
	 * 判断给定字符串是否为空字符串
	 * 
	 * @param source
	 * @return 给定的字符串是空字符串返回true，否则返回false
	 */
	public static boolean isEmptyString(String source) {
		if (source == null) {
			return true;
		} else if (source.length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 判断指定的字符串是否是合法的电话号码
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isPhoneNumber(String numberString) {
		boolean isNumber = false;
		if (!numberString.equals("")) {
			if (numberString.length() == 11
					&& (isNumber(numberString))
					&& (numberString.startsWith("13")
							|| numberString.startsWith("18")
							|| numberString.startsWith("15") || numberString
								.startsWith("14"))) {
				isNumber = true;
			}
		}

		return isNumber;
	}

	/**
	 * 判断给定的文本是否是数字
	 * 
	 * @param numberString
	 * @return
	 */
	public static boolean isNumber(String numberString) {
		return numberString.matches("^[0-9]*$");
	}

	/**
	 * 将双精度浮点数字形式的字符串转换成float类型数据
	 * 
	 * @param doubleString
	 * @return
	 */
	public static Double getDouble(String doubleString) {
		Double number = 0d;
		try {
			number = Double.parseDouble(doubleString);
		} catch (Exception e) {
		}
		return number;
	}

	/**
	 * 
	 * @param count
	 * @return
	 */
	public static String parseDownCount(String count) {
		int downcount = 0;
		try {
			downcount = Integer.parseInt(count);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (downcount >= 0) {
			if (downcount / 10000 < 1) {
				return count;
			} else {
				return downcount / 10000 + "万";
			}
		}
		return count;
	}

	/**
	 * sql 选择语句拼接 "select condition from table where variable = field "
	 * 
	 * @param condition
	 * @param table
	 * @param variable
	 * @param field
	 * @return
	 */
	public static final String combinaSql(String condition, String table,
			String variable, String field) {
		return Constants.SQL_SELECT + condition + Constants.SQL_FROM + table
				+ Constants.SQL_WHERE + variable + Constants.SQL_EQUAL + field
				+ Constants.SQL_SINGLE_QUOTATION_MARKS;
	}

	/**
	 * sql 选择语句拼接 "select condition from table "
	 * 
	 * @param condition
	 * @param table
	 * @param variable
	 * @param field
	 * @return
	 */
	public static final String combinaSql(String condition, String table) {
		return Constants.SQL_SELECT + condition + Constants.SQL_FROM + table;
	}

	/**
	 * sql语句拼接 " variable = ' field ' "
	 * 
	 * @param variable
	 * @param field
	 * @return
	 */
	public static final String combinaEqual(String variable, String field) {
		return variable + Constants.SQL_EQUAL + field
				+ Constants.SQL_SINGLE_QUOTATION_MARKS;
	}

	/**
	 * 检查url是否Http开头,否则添加
	 * 
	 * @param url
	 * @return
	 */
	public static final String checkUrlContainHttp(String prefix, String url) {
		if (checkEmpty(url).startsWith(Constants.KEY_HTTP)) {
			return url;
		} else {
			return prefix + url;
		}
	}

	/**
	 * ScrollView 嵌套Listview listview无法正常显示解决方法
	 * (子ListView的每个Item必须是LinearLayout)
	 * 只要在设置ListView的Adapter后调用此静态方法即可让ListView正确的显示在其父ListView的ListItem中
	 * 
	 * @param listView
	 */
	public static void setListViewHeightBasedOnChildren(ListView listView) {
		// 获取ListView对应的Adapter
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0); // 计算子项View 的宽高
			totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1));
		// listView.getDividerHeight()获取子项间分隔符占用的高度
		// params.height最后得到整个ListView完整显示需要的高度
		listView.setLayoutParams(params);
	}

	/**
	 * 重置输入
	 * 
	 * @param et
	 */
	public static final void clearEtText(EditText et) {
		et.setText(Constants.KEY_NULL_STRING);
	}

	private static ArrayList<Activity> actList = new ArrayList<Activity>();

	public static final void addAct(Activity act) {
		actList.add(act);
	}

	public static final void finishAll() {
		for (int i = actList.size() - 1; i >= 0; i--) {
			actList.get(i).finish();
		}
		actList.clear();
	}

	public static final void clearAll() {
		actList.clear();
	}

	public static final void toHomeActivity(Context context) {
		to(context, MainActivity.class);
	}

	public static final void to(Context context, Class<?> cls) {
		toOtherClass(context, cls, null);
	}

	public static final void toOtherClass(Context context, Class<?> cls,
			Bundle data) {
		Intent intent = new Intent(context, cls);
		if (data != null) {
			intent.putExtras(data);
		}
		context.startActivity(intent);
	}

	public static final void gotoHome(Context context) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		context.startActivity(intent);
	}

	/**
	 * 添加桌面快捷图标 需要权限:<uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
	 * 
	 * @param context
	 * @param cls
	 */
	public static void createShorcut(Context context, int logo, String name,
			Class<?> cls) {
		Intent thisIntent = new Intent();
		thisIntent.setClass(context, cls);
		String ACTION_ADD_SHORTCUT = "com.android.launcher.action.INSTALL_SHORTCUT";
		Intent addShortcut = new Intent(ACTION_ADD_SHORTCUT);

		Parcelable icon = Intent.ShortcutIconResource
				.fromContext(context, logo);

		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
		addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, thisIntent);
		context.sendBroadcast(addShortcut);
	}

	// public static final DecimalFormat format = new DecimalFormat("0.00");
	//
	// /**
	// * 保留两位小数
	// *
	// * @param decimal
	// * @return
	// */
	// public static final String formatToTwoDecimalPlaces(String decimal) {
	// try {
	// return format.format(format.parse(decimal));
	// } catch (java.text.ParseException e) {
	// e.printStackTrace();
	// }
	// }

	public static final String formatToDate(String date) {
		if (date == null) {
			return "";
		} else {
			int index = date.indexOf(" ");
			return (index > 0 ? date.substring(0, index) : date).replaceAll(
					"/", "-");
		}
	}

	/**
	 * 设置背景颜色
	 * 
	 * @param context
	 * @param view
	 * @param id
	 */
	public static void setBackgroundColor(Context context, View view, int id) {
		view.setBackgroundColor(context.getResources().getColor(id));
	}

	/**
	 * 设置TextView
	 * 
	 * @param context
	 * @param tv
	 * @param id
	 */
	public static void setBtnText(Context context, Button btn, int id) {
		btn.setText(context.getResources().getString(id));
	}

	/**
	 * 设置TextView 文字
	 * 
	 * @param context
	 * @param tv
	 * @param id
	 */
	public static void setText(Context context, TextView tv, int id) {
		tv.setText(context.getResources().getString(id));
	}

	/**
	 * 设置TextView 文字 文字颜色
	 * 
	 * @param tv
	 * @param resId
	 * @param textColor
	 * @param backgroundRes
	 */
	public static void setTextAndColor(Context context, TextView tv, int resId,
			int textColor) {
		tv.setText(context.getString(resId));
		tv.setTextColor(context.getResources().getColor(textColor));
	}

	/**
	 * 设置TextView 文字 文字颜色
	 * 
	 * @param tv
	 * @param resId
	 * @param textColor
	 * @param backgroundRes
	 */
	public static void setTextAndColor(Context context, TextView tv,
			String text, int textColor) {
		tv.setText(text);
		tv.setTextColor(context.getResources().getColor(textColor));
	}

	/**
	 * 设置TextView 文字 文字颜色 背景
	 * 
	 * @param tv
	 * @param resId
	 * @param textColor
	 * @param backgroundRes
	 */
	public static void setTvColorAndBg(Context context, TextView tv, int resId,
			int textColor, int backgroundRes) {
		tv.setText(context.getString(resId));
		tv.setTextColor(context.getResources().getColor(textColor));
		tv.setBackgroundResource(backgroundRes);
	}

	/**
	 * 设置TextView 文字 文字颜色 背景
	 * 
	 * @param tv
	 * @param text
	 * @param textColor
	 * @param backgroundRes
	 */
	public static void setTvColorAndBg(Context context, TextView tv,
			String text, int textColor, int backgroundRes) {
		tv.setText(text);
		tv.setTextColor(context.getResources().getColor(textColor));
		tv.setBackgroundResource(backgroundRes);
	}

	/**
	 * 解析下载状态 STATUS_PENDING/STATUS_RUNNING/STATUS_PAUSED/STATUS_SUCCESS/
	 * STATUS_INSTALLED/STATUS_FAILED
	 * 
	 * @param status
	 * @return
	 */
	public static int translateStatus(int status) {
		switch (status) {
		case Impl.STATUS_PENDING:
			return DownloadManager.STATUS_PENDING;
		case Impl.STATUS_RUNNING:
			return DownloadManager.STATUS_RUNNING;
			// trinea BEGIN TODO
		case Impl.STATUS_INSUFFICIENT_SPACE_ERROR:
			// trinea END TODO
		case Impl.STATUS_PAUSED_BY_APP:
		case Impl.STATUS_WAITING_TO_RETRY:
		case Impl.STATUS_WAITING_FOR_NETWORK:
		case Impl.STATUS_QUEUED_FOR_WIFI:
			return DownloadManager.STATUS_PAUSED;
		case Impl.STATUS_SUCCESS:
			return DownloadManager.STATUS_SUCCESS;
		case Impl.STATUS_QUEUED_FOR_LIMIT:
			return DownloadManager.STATUS_WAITED;
		case Impl.STATUS_INSTALLED:
			return DownloadManager.STATUS_INSTALLED;
		default:
			assert Impl.isStatusError(status);
			return DownloadManager.STATUS_FAILED;
		}
	}

	// public static void ReceiveGift(final Context context,
	// final PacketResult packetResult) {
	// DialogUtil.showUserDefineDialog(context, Constants.GIFT_CODE,
	// Utils.checkEmpty(packetResult.ucard), R.drawable.app_logo,
	// Constants.COPY_TEXT, Constants.CANCEL_TEXT,
	// new SweetAlertDialog.OnSweetClickListener() {
	//
	// @Override
	// public void onClick(SweetAlertDialog tDialog) {
	// ClipboardManager cm = (ClipboardManager) context
	// .getSystemService(Context.CLIPBOARD_SERVICE);
	// // 将文本数据复制到剪贴板
	// // cm.setPrimaryClip(ClipData.newPlainText(null,
	// // Utils.checkEmpty(packetResult.ucard)));
	// cm.setText(Utils.checkEmpty(packetResult.ucard));
	// tDialog.dismissWithAnimation();
	// }
	// });
	// }

	public static String getApkPackageName(Context context, String apkFilePath) {
		return getApkInfo(apkFilePath, context).get(Constants.KEY_PACKAGE_NAME)
				.toString();
	}

	/**
	 * 通过包名获取应用程序的名称。
	 * 
	 * @param context
	 *            Context对象。
	 * @param packageName
	 *            包名。
	 * @return 返回包名所对应的应用程序的名称。
	 */
	public static String getProgramNameByPackageName(Context context,
			String packageName) {
		PackageManager pm = context.getPackageManager();
		String name = "";
		try {
			name = pm.getApplicationLabel(
					pm.getApplicationInfo(packageName,
							PackageManager.GET_META_DATA)).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return name;
	}

	public final static void setViewVisible(int num, FrameLayout noDataView,
			ListView listview) {
		switch (num) {
		case 1:
			noDataView.setVisibility(View.GONE);
			listview.setVisibility(View.VISIBLE);
			break;
		case 2:
			noDataView.setVisibility(View.VISIBLE);
			listview.setVisibility(View.GONE);
			break;
		case 3:
			noDataView.setVisibility(View.GONE);
			listview.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public static void showUpdateDialog(final Activity context,
			final ClientUpdate data, final DownloadManager manager) {
		final String packageName = context.getPackageName();
		final String path = manager.queryLocalUriByPkgName(packageName);
		DialogUtil.showDefaultDialog(context, "版本更新", "版本更新: " + data.version
				+ "\r\n" + " 更新时间: " + Utils.formatDate(data.updatetime * 1000)
				+ "\r\n" + " 更新内容: " + data.content, R.drawable.app_icon,
				new SweetAlertDialog.OnSweetClickListener() {

					@Override
					public void onClick(SweetAlertDialog tDialog) {
						// 已经有下载记录
						if (manager.queryPackageExists(packageName)) {
							// 查询状态 成功就安装应用
							if (manager.queryActualStatus(packageName) == 200) {
								Utils.installApk(context, path, null, 0);
							} else {
								// 否则删除下载文件 重新开始下载
								Utils.deleteFile(path);
								manager.restartDownload(manager
										.queryIdByPackageName(packageName));
							}
						} else {
							// 没有下载记录 开始下载
							String url = data.downurl;
							if (TextUtils.isEmpty(url)) {
								ToastUtil
										.showShortToast(context, "下载地址无效,请检查.");
								return;
							}
							Uri srcUri = Uri.parse(Utils.checkUrlContainHttp(
									Constants.URL_BASE_HOST, url));
							DownloadManager.Request request = new Request(
									srcUri);
							request.setDestinationInExternalPublicDir(
									Constants.ROOT_DIR,
									url.split("/")[url.split("/").length - 1]);
							request.setTitle(AppContext.getAppName(context));
							request.setDescription(data.content);
							request.setShowRunningNotification(false);
							request.setVisibleInDownloadsUi(false);
							request.setLogo(context.getResources()
									.getResourceName(R.drawable.app_icon));
							request.setPackageName(packageName);
							manager.enqueue(request);
						}
						tDialog.dismissWithAnimation();
					}
				});
	}

	public static String generateGetRequestBody(Object params) {
		if (params == null) {
			return "";
		}

		HashMap<String, Object> requestParams;
		if (params instanceof HashMap) {
			requestParams = (HashMap<String, Object>) params;
		} else {
			return "";
		}

		// add parameter node
		final Iterator<String> keySet = requestParams.keySet().iterator();
		StringBuffer sb = new StringBuffer();
		try {
			while (keySet.hasNext()) {
				final String key = keySet.next();
				sb.append(key + "=" + requestParams.get(key) + "&");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * <br>
	 * 功能简述:4.4及以上获取图片的方法 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String getPath(final Context context, final Uri uri) {

		final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		// DocumentProvider
		if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
			// ExternalStorageProvider
			if (isExternalStorageDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			}
			// DownloadsProvider
			else if (isDownloadsDocument(uri)) {

				final String id = DocumentsContract.getDocumentId(uri);
				final Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));

				return getDataColumn(context, contentUri, null, null);
			}
			// MediaProvider
			else if (isMediaDocument(uri)) {
				final String docId = DocumentsContract.getDocumentId(uri);
				final String[] split = docId.split(":");
				final String type = split[0];

				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}

				final String selection = "_id=?";
				final String[] selectionArgs = new String[] { split[1] };

				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		}
		// MediaStore (and general)
		else if ("content".equalsIgnoreCase(uri.getScheme())) {

			// Return the remote address
			if (isGooglePhotosUri(uri))
				return uri.getLastPathSegment();

			return getDataColumn(context, uri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(uri.getScheme())) {
			return uri.getPath();
		}

		return null;
	}

	public static String getDataColumn(Context context, Uri uri,
			String selection, String[] selectionArgs) {

		Cursor cursor = null;
		final String column = "_data";
		final String[] projection = { column };

		try {
			cursor = context.getContentResolver().query(uri, projection,
					selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				final int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri
				.getAuthority());
	}

	/**
	 * @param uri
	 *            The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri
				.getAuthority());
	}

	/**
	 * 对拍照的图片剪切
	 * 
	 * @param uri
	 *            图片uri
	 * @param outputX
	 *            图片宽度
	 * @param outputY
	 *            图片高度
	 * @param requestCode
	 *            请求码
	 */
	public final static void cameraCropImageUri(Uri uri, int outputX,
			int outputY, int requestCode, boolean mIsKitKat, Fragment child) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 纵横比
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);// 黑边
		intent.putExtra("scaleUpIfNeeded", true);// 黑边
		if (mIsKitKat) {
			intent.putExtra("return-data", true);
		} else {
			intent.putExtra("return-data", false);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		}
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		child.startActivityForResult(intent, requestCode);
	}

	/**
	 * <br>
	 * 功能简述: 4.4及以上选取照片后剪切方法 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 * 
	 * @param uri
	 */
	public final static void cropImageUriAfterKikat(Uri uri, int requestCode,
			Fragment child) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", true); // 返回数据bitmap
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		child.startActivityForResult(intent, requestCode);
	}

	/**
	 * <br>
	 * 功能简述:4.4以下从相册选照片并剪切 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	public final static void cropImageUriBeforeKikat(Uri uri, int requestCode,
			Fragment child) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		child.startActivityForResult(intent, requestCode);
	}

	/**
	 * <br>
	 * 功能简述:4.4及以上从相册选择照片 <br>
	 * 功能详细描述: <br>
	 * 注意:
	 */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static void selectImageUriAfterKikat(int requestCode, Fragment child) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		// intent.addCategory(Intent.CATEGORY_APP_GALLERY);
		// intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
		// "image/*");
		intent.setType("image/*");
		child.startActivityForResult(intent, requestCode);
	}

	// 封装请求Gallery的intent
	public static Intent getPhotoPickIntent(boolean small) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 80);
		intent.putExtra("outputY", 80);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		// ==========small picture return bitmap
		if (small) {
			intent.putExtra("return-data", true);
		} else {
			// ==========
			// bigPicture not return
			intent.putExtra("return-data", false);
			// intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		}
		return intent;
	}

	/**
	 * 获取拍照intent
	 */
	public final static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
}