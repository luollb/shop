/*
 * Copyright (C) 2008 The Android Open Source Project
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

import cn.cy.mobilegames.yyjia.forelders.R;
public class Constants {

	// ====请求域名 ====//
	public static final String URL_BASE_URL = "http://apps.forelders.com/api.php";
	public static final String URL_BASE_HOST = "http://apps.forelders.com/";

	public static final String HOST_NAME = "45应用商店";
	public static final String TAB_QUALITY = "精品";
	public static final String TAB_SOFT = "软件";
	public static final String TAB_GAME = "游戏";
	public static final String TAB_MANAGE = "管理";

	public static final String TAB_RECOM = "推荐";
	public static final String TAB_CATEGORY = "分类";
	public static final String TAB_RANK = "排行";
	public static final String TAB_NEW = "新锐";

	public static final String MANAGE_SEARCH = "热门搜索";

	public static final String MANAGE_DOWNLOAD = "下载管理";
	public static final String MANAGE_UPDATE = "应用更新";
	public static final String MANAGE_UNINSTALL = "卸载管理";
	public static final String MANAGE_SETTING = "设置";

	public static final String MANAGE_UCENTER = "用户中心";
	public static final String MANAGE_HOME = "返回首页";

	public static final String MANAGE_DAILY_SIGN = "每日签到";
	public static final String MANAGE_MY_FOCUS = "我的关注";
	public static final String MANAGE_MY_MESSAGE = "我的消息";
	public static final String MANAGE_MY_COMMENT = "我的评论";

	public static final int[] userMineRes = new int[] { R.drawable.daily_sign,
			R.drawable.my_focus, R.drawable.my_message, R.drawable.my_comment };
	public static final int[] userManageRes = new int[] {
			R.drawable.manage_download, R.drawable.game_update,
			R.drawable.manage_uninstall, R.drawable.setting };

	public static final String[] userMineTitle = new String[] {
			MANAGE_DAILY_SIGN, MANAGE_MY_FOCUS, MANAGE_MY_MESSAGE,
			MANAGE_MY_COMMENT };
	public static final String[] userManageTitle = new String[] {
			MANAGE_DOWNLOAD, MANAGE_UPDATE, MANAGE_UNINSTALL, MANAGE_SETTING };

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>> Intent Extra Message Field
	/** 分类信息 */
	public static final String EXTRA_CATEGORY = "extra.category";
	/** 包名信息 */
	public static final String EXTRA_PACKAGE_NAME = "extra.key.package.name";
	/** 产品ID信息 */
	public static final String EXTRA_PRODUCT_ID = "extra.key.pid";
	/** 产品来源信息（4545应用商店/谷歌市场） */
	public static final String EXTRA_SOURCE_TYPE = "extra.key.source.type";
	/** 产品详细信息 */
	public static final String EXTRA_PRDUCT_DETAIL = "extra.product.detail";
	/** 产品截图ID信息 */
	public static final String EXTRA_SCREENSHOT_ID = "extra.screenshot.id";
	/** 产品排序信息 （按时间或者下载量） */
	public static final String EXTRA_SORT_TYPE = "extra.order";
	/** 产品分类ID */
	public static final String EXTRA_CATEGORY_ID = "extra.category.id";
	/** 搜索结果页分类 */
	public static final String EXTRA_SEARCH_TYPE = "extra.search.type";
	/** 首页预加载内容 */
	public static final String EXTRA_HOME_DATA = "extra.home.data";
	/** 首页预加载内容（顶部） */
	public static final String EXTRA_HOME_DATA_TOP = "extra.home.data.top";
	/** 首页预加载内容（底部） */
	public static final String EXTRA_HOME_DATA_BOTTOM = "extra.home.data.bottom";
	/** 加载最大值 */
	public static final String EXTRA_MAX_ITEMS = "extra.max.items";
	/** 应用分类 */
	public static final String CATEGORY_APP = "app";
	/** 游戏分类 */
	public static final String CATEGORY_GAME = "game";
	/** 主题分类 */
	public static final String CATEGORY_THEME = "theme";
	/** 电子书分类 */
	public static final String CATEGORY_EBOOK = "ebook";
	/** 增长最快分类 */
	public static final String CATEGORY_GROW = "grow";
	/** 排序的类型：下载次数 */
	public static final int ORDER_TYPE_DOWNLOAD = 1;
	/** 排序的类型：发布时间 */
	public static final int ORDER_TYPE_TIME = 2;
	/** 排序的类型：装机量 */
	public static final int ORDER_TYPE_INSTALLED_NUM = 3;

	/** The MIME type of APKs */
	public static final String MIMETYPE_APK = "application/vnd.android.package-archive";

	/** The MIME type of image */
	public static final String MIMETYPE_IMAGE = "image/*";

	/** 从客户端发起的下载任务 */
	public static final int DOWNLOAD_FROM_MARKET = 0;

	/** 从社区发起的下载任务 */
	public static final int DOWNLOAD_FROM_BBS = 1;

	/** 推送的应用 */
	public static final int DOWNLOAD_FROM_CLOUD = 2;

	/** OTA任务 */
	public static final int DOWNLOAD_FROM_OTA = 3;

	public static final int P_MARKET_USERLOGO = 0x12, P_IS_LOGIN = 0x13,
			P_DOWN_TASK = 0x14;
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public static final String KEY_EMPTY = "";

	public static final String KEY_CATEGORY_ID = "category_id";

	public static final String KEY_CATEGORY_NAME = "category_name";

	public static final String KEY_ID = "id";

	public static final String KEY_SUBJECT = "subject";

	public static final String KEY_TOTAL_SIZE = "total_size";

	public static final String KEY_END_POSITION = "end_position";

	public static final String KEY_JK_LIST = "bbsAttJkVOList";

	public static final String KEY_FILE_LIST = "bbsAttJkFileVOList";

	public static final String KEY_FILE_NAME = "fileName";

	public static final String KEY_DOWN_URL = "downloadUrl";

	public static final String KEY_SUB_LIST = "sub";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

	public static final String KEY_KEYLIST = "keys";

	public static final String KEY_TEXT = "text";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// 支付页
	public static final String REMOTE_VERSION = "remote_version";
	public static final String RESULT = "result";
	public static final String PAY_CARD = "card";
	public static final String PAY_TYPE = "pay_type";
	public static final String ACCOUNT_LEN = "account_len";
	public static final String PASSWORD_LEN = "password_len";
	public static final String PAY_CREDIT = "credit";

	public static final String PAY_RESULT = "pay_result";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// product attribute
	public static final String PRODUCT_PACKAGENAME = "package_name";

	public static final String INSTALL_APP_LOGO = "logo";
	public static final String INSTALL_APP_TITLE = "app_title";
	public static final String INSTALL_APP_SIZE = "app_size";
	public static final String INSTALL_APP_SCORE = "app_score";
	public static final String INSTALL_APP_DOWNLOADCOUNT = "app_downloadcount";
	public static final String INSTALL_APP_APPDOWNURL = "app_appdownurl";
	public static final String INSTALL_APP_ENGNAME = "app_engname";
	public static final String INSTALL_APP_DESCRIPTION = "app_detail";
	public static final String INSTALL_APP_CHECKED = "app_checked";
	public static final String INSTALL_PLACE_HOLDER = "place_holder";
	public static final String INSTALL_APP_IS_CHECKED = "is_checked";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	// BBS搜索结果Title
	public static final String SEARCH_RESULT_TITLE = "search_result_title";

	public static final String GROUP_NECESSARY = "装机必备";
	public static final String GROUP_SEARCH = "搜索";
	public static final String GROUP_RECOMMEND = "推荐位";
	public static final String GROUP_HOME = "首页";
	public static final String GROUP_CATEGORY = "分类页";
	public static final String GROUP_RANK = "排行页";
	public static final String GROUP_MANAGE = "管理页";
	public static final String GROUP_MENU = "菜单";
	public static final String GROUP_LOGIN = "登录页";
	public static final String GROUP_REGISTER = "注册页";
	public static final String GROUP_PERSONCENTER = "个人中心页";
	public static final String GROUP_APPDETAIL = "应用详情页";
	public static final String GROUP_REBACK = "反馈页";
	public static final String GROUP_APPLIST = "产品列表页";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_PLACEHOLDER = "place_holder";

	public static final String KEY_TOPICS = "topics";
	public static final String KEY_TOPIC = "topic";
	public static final String KEY_TOPIC_ID = "topic_id";
	public static final String KEY_TOPIC_ICON = "app_icon_url";
	public static final String KEY_TOPIC_ICON_LDPI = "ldpi_app_icon_url";
	public static final String KEY_TOPIC_NAME = "name";

	public static final String KEY_REQUIRED_CATEGORY = "req_category";
	public static final String KEY_REQUIRED_CATEGORY_NAME = "name";

	public static final String KEY_PAY_CONSUME = "consume";
	public static final String KEY_PAY_CHARGE = "charge";
	public static final String KEY_PAY_BUY_APP = "buy_app";
	public static final String KEY_PAY_LOGS = "logs";
	public static final String KEY_PAY_FLAG = "flag";
	public static final String KEY_PAY_ORDER_ID = "order_id";
	public static final String KEY_PAY_DESCRIPTION = "description";
	public static final String KEY_PAY_TIME = "create_time";
	public static final String KEY_PAY_MONEY = "money";
	public static final String KEY_PAY_STATUS = "status";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_USER_NAME = "name";

	public static final String KEY_USER_UID = "uid";

	public static final String KEY_USER_EMAIL = "email";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_COMMENTS = "comments";
	public static final String KEY_COMMENT = "comment";
	public static final String KEY_COMMENT_BODY = "comment";
	public static final String KEY_COMMENT_ID = "comment_id";
	public static final String KEY_COMMENT_AUTHOR = "author";
	public static final String KEY_COMMENT_DATE = "date";
	public static final String KEY_COMMENT_LIST = "comment_list";

	public static final String KEY_VALUE = "value";

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final int NO_UPDATE = 0;
	public static final String EXTRA_UPDATE_LEVEL = "update_level";
	public static final String EXTRA_VERSION_CODE = "version_code";
	public static final String EXTRA_VERSION_NAME = "version_name";
	public static final String EXTRA_DESCRIPTION = "description";
	public static final String EXTRA_URL = "apk_url";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_PRODUCT_SCREENSHOT_1 = "screenshot_1";
	public static final String KEY_PRODUCT_SCREENSHOT_2 = "screenshot_2";
	public static final String KEY_PRODUCT_SCREENSHOT_3 = "screenshot_3";
	public static final String KEY_PRODUCT_SCREENSHOT_4 = "screenshot_4";
	public static final String KEY_PRODUCT_SCREENSHOT_5 = "screenshot_5";

	public static final String KEY_PRODUCT_SCREENSHOT_LDPI_1 = "screenshot_1";
	public static final String KEY_PRODUCT_SCREENSHOT_LDPI_2 = "screenshot_2";
	public static final String KEY_PRODUCT_SCREENSHOT_LDPI_3 = "screenshot_3";
	public static final String KEY_PRODUCT_SCREENSHOT_LDPI_4 = "screenshot_4";
	public static final String KEY_PRODUCT_SCREENSHOT_LDPI_5 = "screenshot_5";

	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_DOWNLOAD_INFO = "download_info";
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/** 谷歌市场产品 */
	public static final String SOURCE_TYPE_GOOGLE = "1";
	/** 客户端 */
	public static final String SOURCE_TYPE_6816MG = "0";
	/** 产品收费的类型：免费 */
	public static final int PAY_TYPE_FREE = 1;
	/** 产品收费的类型：收费 */
	public static final int PAY_TYPE_PAID = 2;
	// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	public static final String KEY_CATEGORY = "category";

	public static final String KEY_CATEGORYS = "categorys";

	public static final String KEY_SUB_CATEGORY = "sub_category";

	public static final String KEY_APP_COUNT = "app_count";

	public static final String KEY_CATEGORY_ICON_URL = "icon_url";

	public static final String KEY_TOP_APP = "top_app";

	public static final String KEY_APP_1 = "app_1";

	public static final String KEY_APP_2 = "app_2";

	public static final String KEY_APP_3 = "app_3";

	public static final String KEY_RECOMMEND_TYPE = "top_recommend_type";

	public static final String KEY_RECOMMEND_ICON = "pic";

	public static final String KEY_RANK = "rank";

	public static final String KEY_RECOMMEND_ID = "id";

	public static final String KEY_RECOMMEND_TITLE = "reason";

	public static final int INFO_UPDATE = 0;

	public static final int INFO_REFRESH = 1;

	/** 状态 -- 未下载 */
	public static final int STATUS_NORMAL = 0;
	/** 状态 -- 准备开始下载 */
	public static final int STATUS_PENDING = 1;
	/** 状态 -- 下载运行中 */
	public static final int STATUS_RUNNING = 2;
	/** 状态 -- 下载暂停中 */
	public static final int STATUS_PAUSE = 3;
	/** 状态 -- 等待下载中 */
	public static final int STATUS_QUEUE = 4;
	/** 状态 -- 下载失败 */
	public static final int STATUS_FAILURE = 5;
	/** 状态 -- 已下载未安装 */
	public static final int STATUS_UNINSTALL = 9;
	/** 状态 -- 可更新 */
	public static final int STATUS_UPDATE = 10;
	/** 状态 -- 已安装 */
	public static final int STATUS_INSTALLED = 11;

	public static final String KEY_PRODUCT_LIST = "product_list";

	public static final String KEY_PRODUCT = "product";

	public static final String KEY_PRODUCTS = "products";

	public static final int[] menuLogos = new int[] {
			R.drawable.manage_to_search, R.drawable.manage_to_download,
			R.drawable.manage_to_uninstall
	// , R.drawable.manage_to_home
	};
	public static final String[] menuNames = new String[] { MANAGE_SEARCH,
			MANAGE_DOWNLOAD, MANAGE_UNINSTALL
	// , MANAGE_HOME
	};

	public static final String[] softNames = new String[] {
			Constants.TAB_RECOM, Constants.TAB_CATEGORY, Constants.TAB_RANK,
			Constants.TAB_NEW };

	public static final int[] tabIcons = { R.drawable.tab_choice,
			R.drawable.tab_soft, R.drawable.tab_game, R.drawable.tab_manage };
	public static final String[] tabNames = { TAB_QUALITY, TAB_SOFT, TAB_GAME,
			TAB_MANAGE };

	// ====个人信息管理====//
	public static final String SIGN_IN = "登录";
	public static final String LOGOUT_CN = "退出";
	public static final String VISITOR_CN = "游客";
	public static final String REGISTER = "注册";
	public static final String QQ_LOGIN = "QQ登录";
	public static final String WB_LOGIN = "微博登录";
	public static final String CHANGE_PASSWORD = "修改密码";
	public static final String CHANGE_PICTURE = "更换头像";
	public static final String PLEASE_ENTER_USERNAME = "请输入用户名";
	public static final String PLEASE_ENTER_EMAIL = "请输入邮箱号码";
	public static final String EMAIL_ERROR_FORMAT = "邮箱格式不正确";
	public static final String PLEASE_ENTER_PASSWORD = "请输入密码";
	public static final String PLEASE_REPEAT_PASSWORD = "请输入确认密码";
	public static final String PASSWORD_NOT_UNIFORM = "两次密码输入不一致";
	public static final String LOGIN_SUCCESS = "登录成功";
	public static final String REGISTER_FAILURE = "注册失败";
	public static final String LOGIN_FAILURE = "登录失败,";
	public static final String LOGIN_FAILURE_AND_RETRY = "登录失败,请重试.";
	public static final String LOGIN_HINT = "还未登录,马上登录.";
	public static final String NO_LOGIN = "还未登录";
	public static final String FORGET_PASSWORD = "忘记密码";
	public static final String UPLOAD_FAILURE = "上传头像失败";
	public static final String UPLOAD_SUCCESS = "上传头像成功";
	public static final String NO_SDCARD_AND_CHECK = "没有SD卡,请检查.";
	public static final String REQUEST_LOGIN = "注册成功,正在请求登录!";
	public static final String NOT_FIND_PICTURE = "没有发现照片.";
	// ====礼包管理====//
	public static final String GIFT_CHECK = "查看";
	public static final String GIFT_RECEIVE = "领取";
	public static final String COPY_TEXT = "复制";
	public static final String GIFT_CODE = "礼包号";
	public static final String ACTIVE_CODE = "激活码";
	public static final String LIMIT_TEXT = "有效期限: ";
	public static final String GIFT_CONTENT_TEXT = "礼包内容: ";
	// public static final String NOVICE_CARD = "新手卡";
	// ====数据加载====//
	public static final String NO_DATA = "暂时没有数据,请稍候.";
	public static final String LOAD_ERROR = "加载失败,请检查.";
	public static final String NO_MORE_DATA = "没有更多的数据,请稍候.";
	public static final String NET_CONNECT_NO_WORK = "网络连接不可用,请检查.";
	public static final String RETURN_DATA_ERROR_AND_CHECK = "返回数据有误,请检查.";
	// ====应用说明====//
	public static final String TO = " 至 ";
	public static final String LEFT_BRACKET = "(";
	public static final int THOUSAND = 1000;
	public static final String COUNT_HOT = "热度";
	// ====下载管理====//
	public static final String SURE_TEXT = "确定";
	public static final String CANCEL_TEXT = "取消";
	public static final String QUOTATION_MARK = "\"";
	public static final String DOWNLOAD_UPDATE = "更新";
	public static final String COUNT_DOWNLOAD = "次下载";
	public static final String VERSION_CODE_CN = "版本:";
	public static final String DOWNLOAD_START = "开始下载";
	public static final String DOWNLOAD_ERROR = "下载失败";
	public static final String CANCEL_SURE = "确定取消下载 \"";
	public static final String CANCEL_TASK = "下载任务将被取消";
	public static final String DOWNLENGTH_TITLE_CN = "大小:";
	public static final String UNINSTALL_SUCCESS = "卸载成功";
	public static final String DOWNLOAD_URL_INVALID_AND_CHECK = "下载地址无效,请检查.";
	// ====软件说明====//
	// public final static String MY_CN = "我";
	// public static final String ALL_CN = "全部";
	// public final static String INFO_CN = "资讯";
	// public final static String GIFT_CN = "礼包";
	public final static String DOWNLOAD_CN = "下载";
	// public static final String TOP_NEWS = "头条资讯";
	// public static final String HOT_RANK = "热门排行";
	// public static final String HOT_GIFT = "疯抢礼包";
	// public static final String GAME_OPEN = "游戏开服";
	// public static final String GAME_TEST = "新游开测";
	// public static final String BEST_RECOM = "精品推荐";
	// public static final String HOT_RECOM = "热门推荐";
	// public final static String MY_GIFT_CN = "我的礼包";
	// public final static String MY_FAVORITE = "我的收藏";
	// public static final String NEW_PUBLISH = "最新上线";
	// public final static String GAME_MANAGE = "游戏管理";
	public static final String SEPARATE_MARK = " | ";
	public static final String KINDER_NOTICE = "温馨提示";
	public static final String GUIDE_FIX_TIME = "引导图片修改时间";
	public static final String PAUSE_CN = "暂停";
	public static final String FOCUS_CN = "关注(";
	public static final String RIGHT_BRACKET = ")";
	public final static String COMMENT_FAILURE = "评论失败";
	public final static String COMMENT_EMPTY_PLEASE_COMMENT = "评论不能为空,请输入您的评论！";
	// ====搜索====//
	public static final String SEARCH_KEY_WORD_NO_NULL = "搜索关键词不能为空!";
	// ====二维码====//
	public static final String SCAN_CODE = "二维码扫描";

	// ====存储根目录====//
	public static final String ROOT_DIR = "45应用商店";
	/** 缓存目录 */
	public static final String IMAGE_CACHE_DIR = ROOT_DIR + "/.cache";
	public static final String APP_NAME = "45app";
	public static final String APP_NAME_EN = "4545应用商店";
	public static final String PROJECT_NAME = "ZhiNei";
	public static final String DATABASE_NAME = "soft_manage";
	public static final int DATABASE_VERSION = 1;
	// ====友盟事件统计 说明====//
	public static final String EVENT_TO_SEARCH = "to_search";
	public static final String EVENT_TO_COMMENT = "to_comment";
	public static final String EVENT_DOWNLOAD_OPEN = "download_open";
	public static final String EVENT_DOWNLOAD_PAUSE = "download_pause";
	public static final String EVENT_DOWNLOAD_START = "download_start";
	public static final String EVENT_DOWNLOAD_RESUME = "download_resume";
	public static final String EVENT_DOWNLOAD_REMOVE = "download_remove";
	public static final String EVENT_DOWNLOAD_CANCEL = "download_cancel";
	public static final String EVENT_DOWNLOAD_RESTART = "download_restart";
	public static final String EVENT_DOWNLOAD_INSTALL = "download_install";
	public static final String EVENT_NO_DATA_AND_RETRY = "no_data_and_retry";
	/**
	 * 动画
	 */
	public final static int ZOOM_OUT_IN = 0x01;
	public final static int ZOOM_IN_OUT = 0x02;
	public final static int LEFT_TO_RIGHT = 0x03;
	public final static int RIGHT_TO_LEFT = 0x04;
	public final static int BOTTOM_TO_TOP = 0x05;
	public final static int TOP_TO_BOTTOM = 0x06;
	// ====SQL参数====//
	public static final String SQL_STAR = "*";
	public static final String SQL_EQUAL = " = '";
	public static final String SQL_FROM = " from ";
	public static final String SQL_WHERE = " where ";
	public static final String SQL_QUESTION_MARK = "?";
	public static final String SQL_EQUAL_SIGN = " = ";
	public static final String SQL_SELECT = " select ";
	public static final String SQL_STAR_COUNT = "count(*)";
	public static final String SQL_DOUBLE_SQUATION_MARK = "\"";
	public static final String SQL_SINGLE_QUOTATION_MARKS = "'";
	// ====数据库 列名====//
	public static final String KEY_OPEN = "open";
	public static final String KEY_APP_ID = "appid";
	public static final String KEY_AD_APPS = "AdApps";
	public static final String KEY_NEWS_ADS = "newsAds";
	public static final String KEY_PHOTE = "phote";
	public static final String KEY_PIN_YIN = "pin_yin";
	public final static String KEY_DOWNLOAD = "download";
	public static final String KEY_FILE_URL = "fileurl";
	public static final String KEY_FILE_PATH = "file_path";
	public static final String KEY_DOWN_STATUS = "down_status";
	public static final String KEY_NEWS_DETAIL = "news_detail";
	public static final String KEY_DOWNLOAD_ID = "download_id";
	public static final String KEY_BRIEF_SUMMARY = "briefsummary";
	public static final String KEY_TABLE_DOWN_LIST = "down_list";
	public static final String KEY_SOFTWARE_LIST = "softwarelist";
	public static final String KEY_VERSION_CODE = "version_code";
	public static final String KEY_VERSION_NAME = "version_name";
	public static final String KEY_DOWNLOAD_COUNT = "downloadcount";
	public static final String KEY_ZHUHUOBI_VALUE = "zhuhuobi_value";
	public static final String KEY_OLD_VERSION_DETAIL = "old_version_detail";
	public static final String KEY_NEW_VERSION_DETAIL = "new_version_detail";

	public static final String MIME_APK = "application/vnd.android.package-archive";
	// ==========请求key=========================//
	public static final String REQUEST_KEY_IP = "ip";
	public static final String REQUEST_KEY_CID = "cid";
	public static final String REQUEST_KEY_PAGE = "page";
	public static final String REQUEST_KEY_STEP = "step";
	public static final String REQUEST_KEY_AC = "ac";
	public static final String REQUEST_KEY_UID = "uid";
	public static final String REQUEST_KEY_MARK = "@@@";
	public static final String REQUEST_KEY_SIGN = "sign";
	public static final String REQUEST_KEY_EMAIL = "email";
	public static final String REQUEST_KEY_OPITION = "op";
	public static final String REQUEST_KEY_SCORE = "score";
	public static final String REQUEST_KEY_APPID = "appid";
	public static final String REQUEST_KEY_KEYWORD = "keyword";
	public static final String REQUEST_KEY_REQPAGENUM = "reqPageNum";
	public static final String REQUEST_KEY_NEWID = "newid";
	public static final String REQUEST_KEY_ORDER = "order";
	public static final String REQUEST_KEY_CNAME = "cname";
	public static final String REQUEST_KEY_LBTYPE = "lbtype";
	public static final String REQUEST_KEY_PACKID = "packid";
	public static final String REQUEST_KEY_MESSAGE = "message";
	public static final String REQUEST_KEY_LOGIN_SUCCESS = "login_success";
	public static final String REQUEST_KEY_USERNAME = "username";
	public static final String REQUEST_KEY_PASSWORD = "password";
	public static final String REQUEST_KEY_REPASSWORD = "repassword";
	public static final String REQUEST_KEY_PACKDETAIL = "packdetail";
	// ==========请求value=========================//
	public static final String REQUEST_VALUE_STEP_ONE = "1";
	public static final String REQUEST_VALUE_TEST = "test";
	public static final String REQUEST_VALUE_INDEX = "index";
	public static final String REQUEST_VALUE_DAYNUM = "daynum";
	public static final String REQUEST_VALUE_OTHERS = "others";
	public static final String REQUEST_VALUE_VIEWNUM = "viewnum";
	public static final String REQUEST_VALUE_WEEKNUM = "weeknum";
	public static final String REQUEST_VALUE_GETCARD = "getcard";
	public static final String REQUEST_VALUE_MYPACKS = "mypacks";
	public static final String REQUEST_VALUE_SERVICE = "service";
	public static final String REQUEST_VALUE_APPNEWS = "appnews";
	public static final String REQUEST_VALUE_CATEINFO = "cateinfo";
	public static final String REQUEST_VALUE_DATELINE = "dateline";
	public static final String REQUEST_VALUE_PACKLIST = "packlist";
	public static final String REQUEST_VALUE_APPDETAIL = "appdetail";
	public static final String REQUEST_VALUE_UPDATEAPP = "updateapp";
	public static final String REQUEST_VALUE_SEARCHPACK = "searchpack";
	public static final String REQUEST_VALUE_ISRECOMMEND = "isrecommend";
	public static final String REQUEST_VALUE_APPPACKLIST = "apppacklist";
	public static final String REQUEST_VALUE_UPLOADEDFILE = "uploadedfile";
	// ====请求参数====//
	public static final String KEY_ZERO = "0";
	public static final String KEY_SIZE = "size";
	public static final String KEY_NAME = "name";
	public static final String KEY_IMSI = "imsi";
	public static final String KEY_LOGO = "logo";
	public static final String KEY_HTTP = "http";
	public final static String KEY_GIFT = "gift";
	public static final String KEY_IMEI = "imei";
	public static final String KEY_MODEL = "model";
	public static final String KEY_NULL_STRING = "";
	public static final String KEY_CLIENT = "client";
	public static final String KEY_SOFTWARE = "software";
	public final static String KEY_DOWNLOG = "downlog";
	public static final String KEY_APP_LOGO = "applogo";
	public static final String KEY_STATUS_OK = "200";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_NEWSID = "newsid";
	public static final String KEY_APP_ICON = "icon";
	public static final String KEY_PACKAGE = "package";
	public static final String KEY_APP_NAME = "appname";
	public static final String KEY_APP_SIZE = "appsize";
	public static final String KEY_POSITION = "position";

	public final static String KEY_PACKINFOS = "packInfos";
	public static final String KEY_NEWSTITLE = "newstitle";
	public static final String KEY_FROM_OTHER = "from_other";
	public static final String REQUEST_KEY_ID = "id";
	public static final String REQUEST_KEY_PACKS = "packs";
	public static final String REQUEST_VALUE_GAME = "game";
	public static final String REQUEST_KEY_SIGN_NUM = "123321";
	public static final String REQUEST_KEY_APPTYPE = "apptype";
	public final static String KEY_MYPACKLIST = "myPackList";
	public static final String KEY_SDK_VERSION = "sdkVersion";
	public static final String KEY_PACKAGE_NAME = "packageName";
	public static final String REQUEST_VALUE_APPTYPE_GAME = "1";
	public static final String REQUEST_VALUE_APPTYPE_SOFT = "2";
	public static final String REQUEST_VALUE_APPTYPE_DEFAULT = "0";
	public static final String KEY_PRODUCT_INFO = "key_product_info";
	public static final String KEY_IS_FROM_SETTING = "isfromsetting";
	public static final String KEY_SYSTEM_PICTURE = "system_picture";

	// 客户端请求参数
	public static final String REQUEST_KEY_LOSTPASSWD = "khd_lostpasswd";
	public static final String REQUEST_KEY_KHD_SEARCHKEY = "khd_searchkey";
	// ================================//
	public static final String REQUEST_VALUE_KHD_AD = "khd_ad";
	public static final String REQUEST_VALUE_KHD_NEWS = "khd_news";
	public static final String REQUEST_VALUE_KHD_CATE = "khd_cate";
	public static final String REQUEST_VALUE_KHD_LOGIN = "khd_login";
	public static final String REQUEST_VALUE_KHD_REGIST = "khd_register";
	public static final String REQUEST_VALUE_KHD_SUBJECT = "khd_subject";
	public static final String REQUEST_VALUE_KHD_APPLIST = "khd_applist";
	public static final String REQUEST_VALUE_KHD_VERSION = "khd_version";
	public static final String REQUEST_VALUE_KHD_APPINFO = "khd_appinfo";
	public static final String REQUEST_VALUE_KHD_COMMENT = "khd_comment";
	public static final String REQUEST_VALUE_KHD_OPENPIC = "khd_openpic";
	public static final String REQUEST_VALUE_KHD_UPLOADLOGO = "khd_uploadlogo";
	public static final String REQUEST_VALUE_KHD_NEWSDETAIL = "khd_newsdetail";
	public static final String REQUEST_VALUE_KHD_INSTALLATION = "khd_installation";
	public static final String REQUEST_VALUE_KHD_SUBJECTDETAIL = "khd_subjectdetail";
	// ====请求参数====//
	public static final String STATUS_OK = "1";
	public static final String STATUS_NO_DATA = "0";
	public static final String ENDWITHAPK = ".apk";
	public static final String YYJIA_TAG = "45app";
	public static final String GET_GIFT_SUCCESS = "get_gift_success";
	public final static String NO_SYSTEM_APK_INFO = "No_System_Apk_Info";

	/*
	 * Product Item Information
	 */
	public static final String KEY_PRODUCT_ID = "p_id";

	public static final String KEY_PRODUCT_NAME = "name";

	public static final String KEY_PRODUCT_TYPE = "product_type";

	public static final String KEY_PRODUCT_SUB_CATEGORY = "sub_category";

	public static final String KEY_PRODUCT_IS_STAR = "is_star";

	public static final String KEY_PRODUCT_CATEGORY = "product_category";

	public static final String KEY_PRODUCT_DESCRIPTION = "product_description";

	public static final String KEY_PRODUCT_PRICE = "price";

	public static final String KEY_PRODUCT_SIZE = "app_size";

	public static final String KEY_PRODUCT_RATING = "rating";

	public static final String KEY_PRODUCT_DOWNLOAD_URL = "url";

	public static final String KEY_PRODUCT_TAG = "tag";

	public static final String KEY_PRODUCT_PAY_TYPE = "pay_category";

	public static final String KEY_PRODUCT_SHORT_DESCRIPTION = "short_description";

	public static final String KEY_PRODUCT_SOURCE_TYPE = "source_type";

	public static final String KEY_PRODUCT_AUTHOR = "author_name";

	public static final String KEY_PRODUCT_ENGNAME = "engname";

	public static final String KEY_PRODUCT_DOWNLOAD = "product_download";

	public static final String KEY_PRODUCT_DOWNLOAD_ID = "product_download_id";

	public static final String KEY_PRODUCT_ICON_URL = "icon_url";

	public static final String KEY_PRODUCT_HOME_BANNER = "home_banner";

	public static final String KEY_PRODUCT_ICON_URL_LDPI = "ldpi_icon_url";

	public static final String KEY_PRODUCT_PACKAGE_NAME = "packagename";

	public static final String KEY_PRODUCT_VERSION_NAME = "version_name";

	public static final String KEY_PRODUCT_VERSION_CODE = "version_code";

	public static final String KEY_PRODUCT_COMMENTS_COUNT = "comments_count";

	public static final String KEY_PRODUCT_RATING_COUNT = "ratings_count";

	public static final String KEY_PRODUCT_SCORE = "score";

	public static final String KEY_PRODUCT_DOWNLOAD_COUNT = "download_count";

	public static final String KEY_PRODUCT_LONG_DESCRIPTION = "long_description";

	public static final String KEY_PRODUCT_PUBLISH_TIME = "publish_time";

	public static final String KEY_PRODUCT_MD5 = "filemd5";

	public static final String KEY_PRODUCT_UP_REASON = "up_reason";

	public static final String KEY_PRODUCT_UP_TIME = "up_time";

	public static final String KEY_PRODUCT_PERMISSIONS = "uses_permission";

	public static final String KEY_PRODUCT_IS_INSTALLED = "is_installed";

	public static final String KEY_PRODUCT_STATUS = "status";

	public static final String KEY_PRODUCT_IS_RECOMMEND = "product_is_recommend";

	public static final String KEY_PRODUCT_ICON = "icon_url";

	public static final String KEY_PRODUCT_PUBLIC_TIME = "public_time";

	public static final String ERROR_RETURN_PHOTE = "无法读取图片数据,请检查";
	public static final String SYSTEM_PICTURE = "system_picture";
	public static final String ERROR_RETURN_DATA = "无法读取数据,请检查";
	public static final String UPLOAD_LOADING = "正在上传...";
	public static final String REQUEST_VALUE_UPLOADLOGO = "khd_uploadlogo";
	public static final String REQUEST_VALUE_KHD_CONFIG = "khd_config";
	public static final String REQUEST_VALUE_AGREEMENT = "agreement";
	public static final String KEY_VERSION = "version";
}
