package cn.cy.mobilegames.yyjia.forelders.util;

import java.io.File;

import android.content.Context;
import android.os.Environment;
import cn.cy.mobilegames.yyjia.forelders.Constants;

/**
 * @describe:TODO 全局应用程序类：用于保存和调用全局应用配置及访问网络数据 <br/>
 *                原理是在主工程使用相同名称的string资源文件替换当前子项目的string的资源文件 <br/>
 *                所以，使用本类时，请确保主工程已经配置了与本子工程（library）具有相同名称的string资源文件 <br/>
 *                使用时，在需要使用到这些全局配置常量的地方，导入本library，然后调用方法获取即可
 * @author: mayday
 * @date:2013-4-13
 */
public class AppContext {
	private static String getCacheRootDir(Context context) {
		return Environment.getExternalStorageDirectory() + File.separator
				+ getProjectName(context);
	}

	/**
	 * @describe:TODO 创建数据库文件的路径
	 * @param dbpath
	 * @param dbName
	 * @return
	 */
	public static File getCachePath(Context context) {
		File path = new File(Environment.getExternalStorageDirectory(),
				getCacheRootDir(context));// 创建目录
		if (!path.exists()) {// 目录存在返回false
			path.mkdirs();// 创建一个目录
		}
		return path;
	}

	/**
	 * @title: getCacheRootDir
	 * @description: TODO获取本系统在SDCard中的缓存的根路径
	 * @return File
	 * @throws
	 */
	public static File getCacheRootDirFile(Context context) {
		File dir = new File(getCacheRootDir(context));
		if (!dir.exists() || !dir.isDirectory())
			dir.mkdirs();
		return dir;
	}

	/**
	 * @description: TODO 根据与之聊天的人的uid创建语音文件夹
	 * @param uid
	 *            根据与之聊天的人的uid
	 * @return
	 */
	public static File getChattingSoundCacheDir(Context context, String uid) {
		// File dir = new File(Environment.getExternalStorageDirectory(),
		// getCacheRootDir(context)+ "/ChattingSound");
		// if (!dir.exists() || !dir.isDirectory()) {
		// dir.mkdirs();
		// }
		File dir = getCacheFile(getCacheRootDir(context), "ChattingSound");
		File soundDir = new File(dir, uid);
		if (!soundDir.exists() || !soundDir.isDirectory()) {
			soundDir.mkdir();
		}
		return soundDir;
	}

	/**
	 * @describe:TODO 获取图片缓存目录
	 * @param context
	 * @return
	 */
	public static File getImageCachePath(Context context) {
		// File path = new File(Environment.getExternalStorageDirectory(),
		// getCacheRootDir(context)+"/ImageFile");// 创建目录
		// if (!path.exists()) {// 目录存在返回false
		// path.mkdirs();// 创建一个目录
		// }
		return getCacheFile(getCacheRootDir(context), "ImageFile");
	}

	/**
	 * 获取或者生成缓存文件夹
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	private static File getCacheFile(String pathName, String cacheName) {
		if (pathName == null) {
			pathName = "";
		}
		File path = new File(pathName + File.separator + cacheName);// 创建目录
		if (!path.exists() || !path.isDirectory()) {// 目录存在返回false
			path.mkdirs();// 创建一个目录
		}
		return path;
	}

	/**
	 * @describe:TODO 获取主工程的项目名称
	 * @param context
	 * @return
	 */
	public static String getProjectName(Context context) {
		return Constants.PROJECT_NAME;

	}

	/**
	 * @describe:TODO 获取主工程的数据库名称
	 * @param context
	 * @return
	 */
	public static String getDatabaseName(Context context) {
		return Constants.DATABASE_NAME;

	}

	/**
	 * @describe:TODO 获取主工程的数据库版本
	 * @param context
	 * @return
	 */
	public static int getDatabaseVersion(Context context) {
		return Constants.DATABASE_VERSION;
	}

	public static String getAppName(Context context) {
		return Constants.APP_NAME;
	}
}
