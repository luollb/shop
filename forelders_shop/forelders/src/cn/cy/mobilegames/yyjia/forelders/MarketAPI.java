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

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.util.MD5Util;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

/**
 * Market API utility class
 * 
 * @author andrew.wang
 * @date 2010-10-29
 * @since Version 0.4.0
 */
public class MarketAPI {

	/**
	 * 4545应用商店 API URLS
	 */
	static final String[] API_URLS = {
			// ACTION_GET_QUALITY_HOT 0
			Constants.URL_BASE_URL,
			// ACTION_GET_QUALITY_NECESSARY 1
			Constants.URL_BASE_URL,
			// ACTION_GET_SOFT_RECOM 2
			Constants.URL_BASE_URL,
			// ACTION_GET_SOFT_NEW 3
			Constants.URL_BASE_URL,
			// ACTION_GET_SOFT_RANK 4
			Constants.URL_BASE_URL,
			// ACTION_GET_GAME_RECOM 5
			Constants.URL_BASE_URL,
			// ACTION_GET_GAME_NEW 6
			Constants.URL_BASE_URL,
			// ACTION_GET_GAME_RANK 7
			Constants.URL_BASE_URL,
			// ACTION_GET_HOME_BANNER 8
			Constants.URL_BASE_URL,
			// ACTION_GET_CATE_MORE 9
			Constants.URL_BASE_URL,
			// ACTION_GET_APP_INFO 10
			Constants.URL_BASE_URL,
			// ACTION_DO_COMMENT 11
			Constants.URL_BASE_URL,
			// ACTION_GET_COMMENT_LIST 12
			Constants.URL_BASE_URL,
			// ACTION_GET_APP_CATEGORY 13
			Constants.URL_BASE_URL,
			// ACTION_DO_LOGIN 14
			Constants.URL_BASE_URL,
			// ACTION_DO_REGISTER 15
			Constants.URL_BASE_URL,
			// ACTION_GET_NEWS_DETAIL 16
			Constants.URL_BASE_URL,
			// ACTION_GET_NEWS_CATE 17
			Constants.URL_BASE_URL,
			// ACTION_GET_NEWS_LIST 18
			Constants.URL_BASE_URL,
			// ACTION_CLIENT_UPDATE 19
			Constants.URL_BASE_URL,
			// ACTION_CHECK_SPLASH 20
			Constants.URL_BASE_URL,
			// ACTION_FIRST_RECOM 21
			Constants.URL_BASE_URL,
			// ACTION_GUEST_RECOM 22
			Constants.URL_BASE_URL,
			// ACTION_GET_SUBJECT_LIST 23
			Constants.URL_BASE_URL,
			// ACTION_GET_SUBJECT_DETAIL 24
			Constants.URL_BASE_URL,
			// ACTION_CHANGE_PASSWORD 25
			Constants.URL_BASE_URL,
			// ACTION_GET_SEARCH_KEYWORD 26
			Constants.URL_BASE_URL,
			// ACTION_GET_RECOM_SEARCH 27
			Constants.URL_BASE_URL,
			// ACTION_GET_UPGRADE 28
			Constants.URL_BASE_URL,
			// 29
			Constants.URL_BASE_URL,
			// 30
			Constants.URL_BASE_URL,
			// 31
			Constants.URL_BASE_URL,
			// 32
			Constants.URL_BASE_URL,
			// 33
			Constants.URL_BASE_URL,
			// 34
			Constants.URL_BASE_URL,
			// 35
			Constants.URL_BASE_URL,
			// 36
			Constants.URL_BASE_URL,

	};

	/** 获取精品_最火 */
	public static final int ACTION_GET_QUALITY_HOT = 0;
	/** 获取精品_必备 */
	public static final int ACTION_GET_QUALITY_NECESSARY = 1;
	/** 获取软件-推荐 */
	public static final int ACTION_GET_SOFT_RECOM = 2;
	/** 获取软件-新锐 */
	public static final int ACTION_GET_SOFT_NEW = 3;
	/** 获取软件-排行 */
	public static final int ACTION_GET_SOFT_RANK = 4;
	/** 获取游戏-推荐 */
	public static final int ACTION_GET_GAME_RECOM = 5;
	/** 获取游戏-新锐 */
	public static final int ACTION_GET_GAME_NEW = 6;
	/** 获取游戏-排行 */
	public static final int ACTION_GET_GAME_RANK = 7;
	/** 获取首页-banner广告 */
	public static final int ACTION_GET_HOME_BANNER = 8;
	/** 获取分类-更多 */
	public static final int ACTION_GET_CATE_MORE = 9;
	/** 获取应用-详情 */
	public static final int ACTION_GET_APP_INFO = 10;
	/** 获取评论列表 */
	public static final int ACTION_GET_COMMENT_LIST = 12;
	/** 获取应用(软件/游戏)-分类 */
	public static final int ACTION_GET_APP_CATEGORY = 13;
	/** 资讯--详情 */
	public static final int ACTION_GET_NEWS_DETAIL = 16;
	/** 资讯--分类 */
	public static final int ACTION_GET_NEWS_CATE = 17;
	/** 资讯--列表 */
	public static final int ACTION_GET_NEWS_LIST = 18;
	/** 客户端升级 */
	public static final int ACTION_CLIENT_UPDATE = 19;
	/** 检查开机图更新 */
	public static final int ACTION_CHECK_SPLASH = 20;
	/** 首次推荐应用 */
	public static final int ACTION_FIRST_RECOM = 21;
	/** 推荐软件 */
	public static final int ACTION_GUEST_RECOM = 22;
	/** 专题列表 */
	public static final int ACTION_GET_SUBJECT_LIST = 23;
	/** 专题列表 */
	public static final int ACTION_GET_SUBJECT_DETAIL = 24;
	/** 搜索推荐 */
	public static final int ACTION_GET_RECOM_SEARCH = 27;
	public static final int ACTION_GET_USER_AGREEMENT = 30;
	// POST=====
	/** 提交评论 */
	public static final int ACTION_DO_COMMENT = 11;
	/** 登录 */
	public static final int ACTION_DO_LOGIN = 14;
	/** 注册 */
	public static final int ACTION_DO_REGISTER = 15;
	/** 修改密码 */
	public static final int ACTION_CHANGE_PASSWORD = 25;
	/** 关键字搜索 */
	public static final int ACTION_GET_SEARCH_KEYWORD = 26;
	/** 已装软件更新 */
	public static final int ACTION_GET_UPGRADE = 28;

	public static final int ACTION_POST_UPLOAD_LOGO = 29;

	static final int[] API_GET_ACTION = { ACTION_GET_QUALITY_HOT,
			ACTION_GET_QUALITY_NECESSARY, ACTION_GET_SOFT_RECOM,
			ACTION_GET_SOFT_NEW, ACTION_GET_SOFT_RANK, ACTION_GET_GAME_RECOM,
			ACTION_GET_GAME_NEW, ACTION_GET_GAME_RANK, ACTION_GET_HOME_BANNER,
			ACTION_GET_CATE_MORE, ACTION_GET_APP_INFO, ACTION_GET_COMMENT_LIST,
			ACTION_GET_APP_CATEGORY, ACTION_GET_NEWS_DETAIL,
			ACTION_GET_NEWS_CATE, ACTION_GET_NEWS_LIST, ACTION_CLIENT_UPDATE,
			ACTION_CHECK_SPLASH, ACTION_FIRST_RECOM, ACTION_GUEST_RECOM,
			ACTION_GET_SUBJECT_LIST, ACTION_GET_SUBJECT_DETAIL,
			ACTION_GET_RECOM_SEARCH, ACTION_GET_USER_AGREEMENT, };

	static final int[] API_POST_ACTION = { ACTION_DO_COMMENT, ACTION_DO_LOGIN,
			ACTION_DO_REGISTER, ACTION_CHANGE_PASSWORD,
			ACTION_GET_SEARCH_KEYWORD, ACTION_GET_UPGRADE,
			ACTION_POST_UPLOAD_LOGO, };

	static final int[] API_CACHE = { ACTION_GET_QUALITY_HOT,
			ACTION_GET_HOME_BANNER, ACTION_GET_QUALITY_NECESSARY,
			ACTION_GET_USER_AGREEMENT, };

	// TODO
	/**
	 * 修改密码
	 */
	public static void changePassword(Context context,
			ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_KEY_LOSTPASSWD);
		params.put(Constants.REQUEST_KEY_STEP, Constants.REQUEST_VALUE_STEP_ONE);
		params.put(Constants.REQUEST_KEY_USERNAME, Session.get(context)
				.getUserName());
		params.put(Constants.REQUEST_KEY_EMAIL, Session.get(context)
				.getUserEmail());
		new ApiAsyncTask(context, ACTION_CHANGE_PASSWORD, handler, params)
				.execute();
	}

	/**
	 * 专题详情
	 */
	public static void getSubjectDetail(Context context,
			ApiRequestListener handler, String id) {
		HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_SUBJECTDETAIL);
		params.put(Constants.REQUEST_KEY_ID, id);
		new ApiAsyncTask(context, ACTION_GET_SUBJECT_DETAIL, handler, params)
				.execute();
	}

	/**
	 * 关键字搜索
	 */
	public static void getSearchKeyword(Context context,
			ApiRequestListener handler, String keyword) {
		HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		params.put(Constants.REQUEST_KEY_KEYWORD, keyword);
		new ApiAsyncTask(context, ACTION_GET_SEARCH_KEYWORD, handler, params)
				.execute();
	}

	/**
	 * 搜索推荐
	 */
	public static void getRecomSearch(Context context,
			ApiRequestListener handler, int page) {
		HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_KEY_KHD_SEARCHKEY);
		params.put(Constants.REQUEST_KEY_REQPAGENUM, page + "");
		new ApiAsyncTask(context, ACTION_GET_RECOM_SEARCH, handler, params)
				.execute();
	}

	/**
	 * 专题列表
	 */
	public static void getSubjectList(Context context,
			ApiRequestListener handler, int page) {
		HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_SUBJECT);
		params.put(Constants.REQUEST_KEY_PAGE, page + "");
		new ApiAsyncTask(context, ACTION_GET_SUBJECT_LIST, handler, params)
				.execute();
	}

	/**
	 * 推荐应用
	 */
	public static void guestRecom(Context context, ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_DEFAULT);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		new ApiAsyncTask(context, ACTION_GUEST_RECOM, handler, params)
				.execute();
	}

	/**
	 * 装机必备
	 */
	public static void firstRecom(Context context, ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_DEFAULT);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		new ApiAsyncTask(context, ACTION_FIRST_RECOM, handler, params)
				.execute();
	}

	/**
	 * 开机图检查
	 */
	public static void checkSplash(Context context, ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_OPENPIC);
		new ApiAsyncTask(context, ACTION_CHECK_SPLASH, handler, params)
				.execute();
	}

	/**
	 * 客户端升级
	 */
	public static void getClientUpdate(Context context,
			ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_VERSION);
		new ApiAsyncTask(context, ACTION_CLIENT_UPDATE, handler, params)
				.execute();
	}

	/**
	 * 资讯--分类
	 */
	public static void getNewsCate(Context context, ApiRequestListener handler) {
		HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_NEWS);
		new ApiAsyncTask(context, ACTION_GET_NEWS_CATE, handler, params)
				.execute();
	}

	/**
	 * 子资讯--列表
	 */
	public static void getNewsList(Context context, ApiRequestListener handler,
			String cid, int page) {
		HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_NEWS);
		params.put(Constants.REQUEST_KEY_CID, cid);
		params.put(Constants.REQUEST_KEY_REQPAGENUM, page + "");
		new ApiAsyncTask(context, ACTION_GET_NEWS_LIST, handler, params)
				.execute();
	}

	/**
	 * 资讯--详情
	 */
	public static void getNewsDetail(Context context,
			ApiRequestListener handler, String newId) {
		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_NEWSDETAIL);
		params.put(Constants.REQUEST_KEY_NEWID, newId);
		new ApiAsyncTask(context, ACTION_GET_NEWS_DETAIL, handler, params)
				.execute();
	}

	/**
	 * 注册
	 */
	public static void doRegister(Context context, ApiRequestListener handler,
			String username, String password, String repwd, String mail) {
		final HashMap<String, Object> params = new HashMap<String, Object>(6);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_REGIST);
		params.put(Constants.REQUEST_KEY_USERNAME, username);
		params.put(Constants.REQUEST_KEY_PASSWORD, password);
		params.put(Constants.REQUEST_KEY_REPASSWORD, repwd);
		params.put(Constants.REQUEST_KEY_EMAIL, mail);
		params.put(Constants.REQUEST_KEY_SIGN,
				MD5Util.stringToMD5(password + Constants.REQUEST_KEY_SIGN_NUM));
		new ApiAsyncTask(context, ACTION_DO_REGISTER, handler, params)
				.execute();
	}

	/**
	 * 登录
	 */
	public static void doLogin(Context context, ApiRequestListener handler,
			String name, String password) {
		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_LOGIN);
		params.put(Constants.REQUEST_KEY_USERNAME, name);
		params.put(Constants.REQUEST_KEY_PASSWORD, password);
		params.put(Constants.REQUEST_KEY_SIGN,
				MD5Util.stringToMD5(password + Constants.REQUEST_KEY_SIGN_NUM));
		new ApiAsyncTask(context, ACTION_DO_LOGIN, handler, params).execute();
	}

	/**
	 * 获取应用(软件/游戏)-分类
	 */
	public static void getAppCategory(Context context,
			ApiRequestListener handler, String type) {
		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_CATE);
		params.put(Constants.REQUEST_KEY_OPITION, type);
		new ApiAsyncTask(context, ACTION_GET_APP_CATEGORY, handler, params)
				.execute();
	}

	/**
	 * 获取评论列表 page更多页数
	 */
	public static void getCommentList(Context context,
			ApiRequestListener handler, String appid) {
		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_COMMENT);
		params.put(Constants.REQUEST_KEY_APPID, appid);
		new ApiAsyncTask(context, ACTION_GET_COMMENT_LIST, handler, params)
				.execute();
	}

	/**
	 * 提交评论
	 */
	public static void doComment(Context context, ApiRequestListener handler,
			boolean isLogin, String appid, String message, String imei,
			int score) {
		final HashMap<String, Object> params = new HashMap<String, Object>(7);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPINFO);
		params.put(Constants.REQUEST_KEY_MESSAGE, message);
		params.put(Constants.REQUEST_KEY_IP, imei);
		params.put(Constants.REQUEST_KEY_SCORE, score);
		params.put(Constants.REQUEST_KEY_APPID, appid);
		if (isLogin) {
			params.put(Constants.REQUEST_KEY_UID, Session.get(context)
					.getUserId());
			params.put(Constants.REQUEST_KEY_USERNAME, Session.get(context)
					.getUserName());
		}
		new ApiAsyncTask(context, ACTION_DO_COMMENT, handler, params).execute();
	}

	/**
	 * 获取应用-详情
	 */
	public static void getAppInfo(Context context, ApiRequestListener handler,
			String appid) {
		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPINFO);
		params.put(Constants.REQUEST_KEY_APPID, appid);
		new ApiAsyncTask(context, ACTION_GET_APP_INFO, handler, params)
				.execute();
	}

	/**
	 * 获取分类-更多
	 */
	public static void getCateMore(Context context, ApiRequestListener handler,
			String cid, int page) {
		final HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_CID, cid);
		params.put(Constants.REQUEST_KEY_PAGE, page);
		new ApiAsyncTask(context, ACTION_GET_CATE_MORE, handler, params)
				.execute();
	}

	/**
	 * 获取首页-banner广告
	 */
	public static void getHomeBanner(Context context, ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(1);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_AD);
		new ApiAsyncTask(context, ACTION_GET_HOME_BANNER, handler, params)
				.execute();
	}

	/**
	 * 获取游戏-排行
	 */
	public static void getGameRank(Context context, ApiRequestListener handler,
			int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_GAME);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_GAME_RANK, handler, params)
				.execute();
	}

	/**
	 * 获取游戏-新锐
	 */
	public static void getGameNew(Context context, ApiRequestListener handler,
			int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_GAME);
		params.put(Constants.REQUEST_KEY_ORDER,
				Constants.REQUEST_VALUE_DATELINE);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_GAME_NEW, handler, params)
				.execute();
	}

	/**
	 * 获取游戏-推荐
	 */
	public static void getGameRecom(Context context,
			ApiRequestListener handler, int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_GAME);
		params.put(Constants.REQUEST_KEY_ORDER,
				Constants.REQUEST_VALUE_ISRECOMMEND);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_GAME_RECOM, handler, params)
				.execute();
	}

	/**
	 * 获取软件-排行
	 */
	public static void getSoftRank(Context context, ApiRequestListener handler,
			int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_SOFT);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_SOFT_RANK, handler, params)
				.execute();
	}

	/**
	 * 获取软件-新锐
	 */
	public static void getSoftNew(Context context, ApiRequestListener handler,
			int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_SOFT);
		params.put(Constants.REQUEST_KEY_ORDER,
				Constants.REQUEST_VALUE_DATELINE);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_SOFT_NEW, handler, params)
				.execute();
	}

	/**
	 * 获取软件-推荐
	 */
	public static void getSoftRecom(Context context,
			ApiRequestListener handler, int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_SOFT);
		params.put(Constants.REQUEST_KEY_ORDER,
				Constants.REQUEST_VALUE_ISRECOMMEND);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");
		new ApiAsyncTask(context, ACTION_GET_SOFT_RECOM, handler, params)
				.execute();
	}

	/**
	 * 获取精品_最火
	 */
	public static void getQualityHot(Context context,
			ApiRequestListener handler, int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);

		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_DEFAULT);
		params.put(Constants.REQUEST_KEY_ORDER, Constants.KEY_DOWNLOAD_COUNT);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");

		new ApiAsyncTask(context, ACTION_GET_QUALITY_HOT, handler, params)
				.execute();
	}

	/**
	 * 获取精品_必备
	 */
	public static void getQualityNecessary(Context context,
			ApiRequestListener handler, int requestNum) {

		final HashMap<String, Object> params = new HashMap<String, Object>(4);

		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_APPLIST);
		params.put(Constants.REQUEST_KEY_APPTYPE,
				Constants.REQUEST_VALUE_APPTYPE_DEFAULT);
		params.put(Constants.REQUEST_KEY_ORDER,
				Constants.REQUEST_VALUE_ISRECOMMEND);
		params.put(Constants.REQUEST_KEY_PAGE, requestNum + "");

		new ApiAsyncTask(context, ACTION_GET_QUALITY_NECESSARY, handler, params)
				.execute();
	}

	public static void getUpgrade(Context context, ApiRequestListener handler) {
		final HashMap<String, Object> params = new HashMap<String, Object>(2);
		ArrayList<HashMap<String, Object>> installPkgs = Utils
				.getNoSystemApkInfo(context);
		StringBuffer sb = new StringBuffer();
		for (HashMap<String, Object> item : installPkgs) {
			sb.append(Utils.checkEmpty(item.get(Constants.KEY_PACKAGE_NAME))
					+ "@@"
					+ Utils.checkEmpty(item.get(Constants.KEY_VERSION_CODE))
					+ Constants.REQUEST_KEY_MARK);
		}
		params.put(Constants.REQUEST_KEY_AC,
				Constants.REQUEST_VALUE_KHD_INSTALLATION);
		params.put(Constants.REQUEST_KEY_PACKS,
				sb.subSequence(0, sb.length() - 3).toString());
		new ApiAsyncTask(context, ACTION_GET_UPGRADE, handler, params)
				.execute();
	}

	/**
	 * 上传头像(29) http://apk.45app.com/api.php?
	 */
	public static void uploadLogo(Context context, ApiRequestListener handler,
			String uid, String sign, String data) {
		HashMap<String, Object> params = new HashMap<String, Object>(5);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_UPLOADLOGO);
		params.put(Constants.REQUEST_KEY_UID, uid);
		params.put(Constants.REQUEST_KEY_SIGN, sign);
		params.put(Constants.REQUEST_VALUE_UPLOADEDFILE, data);
		new ApiAsyncTask(context, ACTION_POST_UPLOAD_LOGO, handler, params)
				.execute();
	}

	/**
	 * 用户协议(30) http://apk.45app.com/api.php?ac=khd_config&op=agreement
	 */
	public static void getUserAgree(Context context, ApiRequestListener handler) {
		HashMap<String, Object> params = new HashMap<String, Object>(3);
		params.put(Constants.REQUEST_KEY_AC, Constants.REQUEST_VALUE_KHD_CONFIG);
		params.put(Constants.REQUEST_KEY_OPITION,
				Constants.REQUEST_VALUE_AGREEMENT);
		new ApiAsyncTask(context, ACTION_GET_USER_AGREEMENT, handler, params)
				.execute();
	}

}