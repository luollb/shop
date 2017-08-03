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
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import cn.cy.mobilegames.yyjia.forelders.model.AppInfo;
import cn.cy.mobilegames.yyjia.forelders.model.Category;
import cn.cy.mobilegames.yyjia.forelders.model.CommentList;
import cn.cy.mobilegames.yyjia.forelders.model.InfoAndContent;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.LostPassWd;
import cn.cy.mobilegames.yyjia.forelders.model.NewsDetail;
import cn.cy.mobilegames.yyjia.forelders.model.OpenPicture;
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.model.Subject;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

/**
 * API 响应结果解析工厂类，所有的API响应结果解析需要在此完成。
 * 
 * @author andrew
 * @date 2011-4-22
 * 
 */
public class ApiResponseFactory {

	// private static final String TAG = "ApiResponseFactory";

	/**
	 * 解析市场API响应结果
	 * 
	 * @param action
	 *            请求API方法
	 * @param response
	 *            HTTP Response
	 * @return 解析后的结果（如果解析错误会返回Null）
	 */
	public static Object getResponse(Context context, int action,
			String response) {
		String inputBody = null;
		if (ApiRequestFactory.S_GET_REQUESTS.contains(action)
				|| ApiRequestFactory.UCENTER_API.contains(action)) {
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				inputBody = response;
			}
		} else {
			if (TextUtils.isEmpty(response)) {
				return null;
			} else {
				inputBody = response;
			}
		}

		// Utils.E(" getResponse  action  " + action + "  inputBody   "
		// + inputBody);
		String requestMethod = "";
		Object result = null;
		try {
			switch (action) {
			case MarketAPI.ACTION_GET_QUALITY_HOT:
				// 解析精品_最火
				requestMethod = "ACTION_GET_QUALITY_HOT";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_QUALITY_NECESSARY:
				// 解析精品_必备
				requestMethod = "ACTION_GET_QUALITY_NECESSARY";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_SOFT_RECOM:
				// 解析软件-推荐
				requestMethod = "ACTION_GET_SOFT_RECOM";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_SOFT_NEW:
				// 解析软件-新锐
				requestMethod = "ACTION_GET_SOFT_NEW";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_SOFT_RANK:
				// 解析软件-排行
				requestMethod = "ACTION_GET_SOFT_RANK";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_GAME_RECOM:
				// 解析游戏-推荐
				requestMethod = "ACTION_GET_GAME_RECOM";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_GAME_NEW:
				// 解析游戏-新锐
				requestMethod = "ACTION_GET_GAME_NEW";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_GAME_RANK:
				// 解析游戏-排行
				requestMethod = "ACTION_GET_GAME_RANK";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_HOME_BANNER:
				// 解析首页广告
				requestMethod = "ACTION_GET_HOME_BANNER";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_CATE_MORE:
				// 解析分类-更多
				requestMethod = "ACTION_GET_CATE_MORE";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_APP_INFO:
				// 解析应用-详情
				requestMethod = "ACTION_GET_APP_INFO";
				result = parseAppinfo(inputBody);
				break;
			case MarketAPI.ACTION_DO_COMMENT:
				// 解析提交评论
				requestMethod = "ACTION_DO_COMMENT";
				result = parseAppinfo(inputBody);
				break;
			case MarketAPI.ACTION_GET_COMMENT_LIST:
				// 解析获取评论列表
				requestMethod = "ACTION_GET_COMMENT_LIST";
				result = parseCommentList(inputBody);
				break;
			case MarketAPI.ACTION_GET_APP_CATEGORY:
				// 解析应用分类
				requestMethod = "ACTION_GET_APP_CATEGORY";
				result = parseCate(inputBody);
				break;
			case MarketAPI.ACTION_DO_LOGIN:
				// 解析登录
				requestMethod = "ACTION_DO_LOGIN";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_DO_REGISTER:
				// 解析注册
				requestMethod = "ACTION_DO_REGISTER";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_GET_NEWS_DETAIL:
				// 解析资讯详情
				requestMethod = "ACTION_GET_NEWS_DETAIL";
				result = parseNewsDetail(inputBody);
				break;
			case MarketAPI.ACTION_GET_NEWS_CATE:
				// 解析资讯分类
				requestMethod = "ACTION_GET_NEWS_CATE";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_NEWS_LIST:
				// 解析资讯列表
				requestMethod = "ACTION_GET_NEWS_LIST";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_CLIENT_UPDATE:
				// 解析客户端升级
				requestMethod = "ACTION_CLIENT_UPDATE";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_CHECK_SPLASH:
				// 解析开机图更新
				requestMethod = "ACTION_CHECK_SPLASH";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_FIRST_RECOM:
				// 解析首次推荐必备应用
				requestMethod = "ACTION_FIRST_RECOM";
				result = parseSoftList(inputBody);
				break;
			case MarketAPI.ACTION_GUEST_RECOM:
				// 解析推荐应用
				requestMethod = "ACTION_GUEST_RECOM";
				result = parseSoftList(inputBody);
				break;
			case MarketAPI.ACTION_GET_SUBJECT_LIST:
				// 解析专题列表
				requestMethod = "ACTION_GET_SUBJECT_LIST";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_SUBJECT_DETAIL:
				// 解析专题详情
				requestMethod = "ACTION_GET_SUBJECT_DETAIL";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_CHANGE_PASSWORD:
				// 解析修改密码
				requestMethod = "ACTION_CHANGE_PASSWORD";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_GET_SEARCH_KEYWORD:
				// 解析关键字搜索
				requestMethod = "ACTION_GET_SEARCH_KEYWORD";
				result = parseListResult(context, inputBody);
				break;

			case MarketAPI.ACTION_GET_RECOM_SEARCH:
				// 解析搜索结果
				requestMethod = "ACTION_GET_RECOM_SEARCH";
				result = parseListResult(context, inputBody);
				break;
			case MarketAPI.ACTION_GET_UPGRADE:
				// 解析搜索结果
				requestMethod = "ACTION_GET_UPGRADE";
				result = parseUpgrade(context, inputBody);
			case MarketAPI.ACTION_POST_UPLOAD_LOGO:
				// 解析搜索结果
				requestMethod = "ACTION_POST_UPLOAD_LOGO";
				result = parseInfoAndContent(inputBody);
				break;
			case MarketAPI.ACTION_GET_USER_AGREEMENT:
				Utils.E("ACTION_GET_USER_AGREEMENT  " + inputBody);
				requestMethod = "ACTION_GET_USER_AGREEMENT";
				result = parseInfoAndContent(inputBody);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 解析应用升级详情
	 */
	public static ArrayList<UpgradeInfo> parseUpgrade(Context context,
			String body) {
		if (body == null || TextUtils.isEmpty(body)) {
			return null;
		}

		ListResult result = JsonUtil.toObject(body, ListResult.class);
		if (result != null && !TextUtils.isEmpty(result.list)
				&& result.status != 0) {
			ArrayList<UpgradeInfo> upgrades = new ArrayList<UpgradeInfo>();
			List<UpgradeInfo> list = JsonUtil.toObjectList(result.list,
					UpgradeInfo.class);
			if (list != null && list.size() > 0) {
				upgrades.addAll(list);
				return upgrades;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 统一解析list数据
	 */
	public static Object parseListResult(Context context, String body) {
		if (body == null || TextUtils.isEmpty(body)) {
			return null;
		}

		ListResult result = JsonUtil.toObject(body, ListResult.class);
		if (result != null && !TextUtils.isEmpty(result.list)
				&& result.status != 0) {
			return result;
		} else {
			return null;
		}
	}

	public static ArrayList<HashMap<String, Object>> parseAppList(
			Context context, Object body) {
		if (body == null) {
			return null;
		}

		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		if (body instanceof ListResult) {
			ListResult data = (ListResult) body;
			List<Softinfo> softLists = JsonUtil.toObjectList(data.list,
					Softinfo.class);
			if (softLists != null && softLists.size() > 0) {
				// 获取已经安装的应用列表
				Session session = Session.get(context);
				ArrayList<String> installedApps = session.getInstalledApps();
				for (Softinfo soft : softLists) {
					HashMap<String, Object> productItem = new HashMap<String, Object>();
					productItem.put(Constants.INSTALL_PLACE_HOLDER, false);
					productItem.put(Constants.KEY_PRODUCT_ID, soft.getId());
					productItem.put(Constants.KEY_PRODUCT_DOWNLOAD,
							Constants.STATUS_NORMAL);
					// TODO 获取包名
					productItem.put(
							Constants.KEY_PRODUCT_PACKAGE_NAME,
							TextUtils.isEmpty(soft.getSourceurl()) ? soft
									.getId() : soft.getSourceurl());
					// TODO 获取包名
					productItem.put(Constants.KEY_PRODUCT_ICON_URL,
							soft.getLogo());
					productItem.put(Constants.KEY_PRODUCT_HOME_BANNER,
							soft.getAdpictureurl());
					productItem.put(Constants.KEY_PRODUCT_NAME, soft.getName());
					productItem.put(Constants.KEY_PRODUCT_SIZE, soft.getSize());
					productItem.put(Constants.KEY_PRODUCT_DOWNLOAD_COUNT,
							soft.getDownloadcount());
					productItem.put(
							Constants.KEY_PRODUCT_DOWNLOAD_URL,
							soft.getDownurl() != null
									&& soft.getDownurl().length() > 0 ? soft
									.getDownurl() : soft.getAppdownurl());
					productItem.put(Constants.KEY_PRODUCT_ENGNAME,
							soft.getEngname());
					productItem.put(Constants.KEY_PRODUCT_RATING_COUNT,
							soft.getStar());
					productItem.put(Constants.KEY_PRODUCT_SCORE,
							soft.getScore());
					productItem.put(Constants.KEY_PRODUCT_DESCRIPTION,
							soft.getBriefsummary());
					// TODO============
					productItem.put(Constants.KEY_PRODUCT_PRICE,
							Constants.DOWNLOAD_CN);
					productItem.put(Constants.KEY_RANK, Constants.KEY_RANK);
					// TODO============
					String packageName = soft.getSourceurl();
					if (installedApps.contains(packageName)) {
						productItem.put(Constants.KEY_PRODUCT_IS_INSTALLED,
								true);
					} else {
						productItem.put(Constants.KEY_PRODUCT_IS_INSTALLED,
								false);
					}
					result.add(productItem);
				}
				return result;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 解析软件列表
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Object> parseSoftList(Object obj) {
		try {
			ListResult result = JsonUtil.toObject(obj.toString(),
					ListResult.class);
			if (result != null) {
				List<Softinfo> softLists = JsonUtil.toObjectList(result.list,
						Softinfo.class);
				ArrayList<Object> lists = new ArrayList<Object>();
				lists.addAll(softLists);
				return lists;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析专辑列表
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Object> parseSubjectList(Object obj) {
		try {
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;
				List<Subject> softLists = JsonUtil.toObjectList(result.list,
						Subject.class);
				ArrayList<Object> lists = new ArrayList<Object>();
				lists.addAll(softLists);
				return lists;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析软件列表
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Softinfo> parseSoftLists(Object obj) {
		try {
			ListResult result = JsonUtil.toObject(obj.toString(),
					ListResult.class);
			if (TextUtils.isEmpty(result.list) || result.list == null) {
				return null;
			} else {
				List<Softinfo> softLists = JsonUtil.toObjectList(
						result.list.toString(), Softinfo.class);
				ArrayList<Softinfo> lists = new ArrayList<Softinfo>();
				lists.addAll(softLists);
				return lists;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析修改密码
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseChangePassword(Object obj) {
		try {
			LostPassWd result = JsonUtil.toObject(obj.toString(),
					LostPassWd.class);
			if (result == null) {
				return null;
			} else {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析分类
	 * 
	 * @param obj
	 * @return
	 */
	public static List<Object> parseCate(String data) {
		try {
			if (data != null && !TextUtils.isEmpty(data)) {
				ListResult result = JsonUtil.toObject(data, ListResult.class);
				if (result != null && result.list != null
						&& !TextUtils.isEmpty(result.list)
						&& result.status != 0) {
					List<Category> cates = JsonUtil.toObjectList(result.list,
							Category.class);
					ArrayList<Object> lists = new ArrayList<Object>();
					lists.addAll(cates);
					return lists;
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析资讯-详情
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseNewsDetail(String response) {
		try {
			NewsDetail result = JsonUtil.toObject(response, NewsDetail.class);
			if (result != null) {
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析应用详情
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseAppinfo(Object obj) {
		try {
			AppInfo appinfo = JsonUtil.toObject(obj.toString(), AppInfo.class);
			if (appinfo != null && !TextUtils.isEmpty(appinfo.toString())) {
				return appinfo;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析评论列表
	 * 
	 * @param data
	 * @return
	 */
	public static List<Object> parseCommentList(String data) {
		try {
			ListResult result = JsonUtil.toObject(data, ListResult.class);
			if (result != null && result.list != null
					&& !TextUtils.isEmpty(result.list) && result.status != 0) {
				List<CommentList> softLists = JsonUtil.toObjectList(
						result.list, CommentList.class);
				ArrayList<Object> lists = new ArrayList<Object>();
				lists.addAll(softLists);
				return lists;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析注册/登录
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseInfoAndContent(String data) {
		try {
			InfoAndContent result = JsonUtil.toObject(data,
					InfoAndContent.class);
			if (result != null && !TextUtils.isEmpty(result.toString())) {
				return result;
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析开机图检查
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseSplash(String response) {
		try {
			OpenPicture result = JsonUtil.toObject(response, OpenPicture.class);
			if (result == null) {
				return null;
			} else {
				return result;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 解析搜索列表
	 * 
	 * @param obj
	 * @return
	 */
	public static Object parseKeywordList(Object obj) {
		try {
			ListResult result = JsonUtil.toObject(obj.toString(),
					ListResult.class);
			if (result != null) {
				if (result.status != 0) {
					List<Softinfo> softLists = JsonUtil.toObjectList(
							result.list, Softinfo.class);
					ArrayList<Object> lists = new ArrayList<Object>();
					lists.addAll(softLists);
					return lists;
				} else {
					return Constants.STATUS_NO_DATA;
				}
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 解析应用列表
	 */
	public static HashMap<String, Object> parseProductList(Context context,
			List<Softinfo> softLists, boolean isIgnoreStar) {
		HashMap<String, Object> result = null;
		ArrayList<HashMap<String, Object>> productArray = null;
		if (softLists != null && softLists.size() > 0) {
			// 获取已经安装的应用列表
			Session session = Session.get(context);
			ArrayList<String> installedApps = session.getInstalledApps();
			result = new HashMap<String, Object>();
			productArray = new ArrayList<HashMap<String, Object>>();
			for (Softinfo element : softLists) {
				HashMap<String, Object> item = new HashMap<String, Object>();
				item.put(Constants.KEY_PRODUCT_ID, element.getId());
				String packageName = TextUtils.isEmpty(element.getSourceurl()) ? element
						.getId() : element.getSourceurl();
				item.put(Constants.KEY_PRODUCT_PACKAGE_NAME, packageName);
				if (installedApps.contains(packageName)) {
					// 应用已经安装，显示已经安装的信息提示
					item.put(Constants.KEY_PRODUCT_DOWNLOAD,
							Constants.STATUS_INSTALLED);
				} else {
					// 应用未安装，显示正常信息提示
					item.put(Constants.KEY_PRODUCT_DOWNLOAD,
							Constants.STATUS_NORMAL);
				}
				item.put(Constants.KEY_PRODUCT_NAME, element.getName());
				item.put(Constants.KEY_PRODUCT_DESCRIPTION,
						element.getBriefsummary());
				item.put(Constants.KEY_PRODUCT_RATING_COUNT, element.getStar());
				item.put(Constants.KEY_PRODUCT_ICON_URL, element.getLogo());
				item.put(Constants.KEY_PRODUCT_DOWNLOAD_URL,
						element.getDownurl());
				item.put(Constants.KEY_PRODUCT_TAG, element.getEngname());
				item.put(Constants.KEY_PRODUCT_DOWNLOAD_COUNT,
						element.getDownloadcount());
				productArray.add(item);
			}
			result.put(Constants.KEY_PRODUCT_LIST, productArray);
		} else {
			return null;
		}
		return result;
	}

}