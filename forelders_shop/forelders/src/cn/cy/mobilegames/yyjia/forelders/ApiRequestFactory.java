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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.pm.PackageInfo;
import android.text.TextUtils;

/**
 * 这个类是获取API请求内容的工厂方法
 */
public class ApiRequestFactory {

	public static ArrayList<Integer> S_XML_REQUESTS = new ArrayList<Integer>();
	public static ArrayList<Integer> S_JSON_REQUESTS = new ArrayList<Integer>();
	public static ArrayList<Integer> S_GET_REQUESTS = new ArrayList<Integer>();

	static {
		for (int i = 0; i < MarketAPI.API_GET_ACTION.length; i++) {
			S_GET_REQUESTS.add(MarketAPI.API_GET_ACTION[i]);
		}
	}

	// justify the G-Header
	static ArrayList<Integer> UCENTER_API = new ArrayList<Integer>();

	static {
		for (int i = 0; i < MarketAPI.API_POST_ACTION.length; i++) {
			UCENTER_API.add(MarketAPI.API_POST_ACTION[i]);
		}
	}

	// 不需要进行缓存的API
	public static ArrayList<String> API_NO_CACHE_MAP = new ArrayList<String>();

	static {
		ArrayList<Integer> API_LIST = new ArrayList<Integer>();
		for (int i = 0; i < MarketAPI.API_GET_ACTION.length; i++) {
			API_LIST.add(MarketAPI.API_GET_ACTION[i]);
		}

		for (int i = 0; i < MarketAPI.API_POST_ACTION.length; i++) {
			API_LIST.add(MarketAPI.API_POST_ACTION[i]);
		}

		Object[] sortList = API_LIST.toArray();
		Arrays.asList(sortList);
		for (Object item : sortList) {
			API_NO_CACHE_MAP.add(item.toString());
		}

		for (int i : MarketAPI.API_CACHE) {
			API_NO_CACHE_MAP.remove(i + "");
		}
	}

	// /**
	// * 获取Market API HttpReqeust
	// */
	// public static HttpUriRequest getRequest(String url, int action,
	// HttpEntity entity) throws IOException {
	//
	// // if (MarketAPI.ACTION_UNBIND == action) {
	// // HttpGet request = new HttpGet(url + session.getUid());
	// // return request;
	// // } else
	// if (S_GET_REQUESTS.contains(action)) {
	// HttpGet request = null;
	// try {
	// request = new HttpGet(url + "?" + EntityUtils.toString(entity));
	// } catch (ParseException e) {
	// e.printStackTrace();
	// }
	// return request;
	// } else if (UCENTER_API.contains(action)) {
	// HttpPost request = new HttpPost(url);
	// // update the User-Agent
	// // request.setHeader("User-Agent",
	// // session.getUCenterApiUserAgent());
	// request.setEntity(entity);
	// return request;
	// } else if (S_XML_REQUESTS.contains(action)) {
	// HttpPost request = new HttpPost(url);
	// // update the g-header
	// // request.setHeader("G-Header", session.getJavaApiUserAgent());
	// request.addHeader("Accept-Encoding", "gzip");
	// request.setEntity(AndroidHttpClient.getCompressedEntity(entity
	// .getContent()));
	// return request;
	// } else {
	// // for BBS search API
	// HttpPost request = new HttpPost(url);
	// request.setEntity(entity);
	// return request;
	// }
	// }

	/**
	 * 获取Market API HTTP 请求内容
	 * 
	 * @param action
	 *            请求的API Code
	 * @param params
	 *            请求参数
	 * @return 处理完成的请求内容
	 * @throws UnsupportedEncodingException
	 *             假如不支持UTF8编码方式会抛出此异常
	 */
	public static String getRequestEntity(int action, Object params)
			throws UnsupportedEncodingException {

		if (S_XML_REQUESTS.contains(action)) {
			// 普通的XML请求内容
			return getXmlRequest(params);
		}
		// else if (S_ENCODE_FORM_REQUESTS.contains(action)) {
		// // URL encode form 请求内容
		// return getFormRequest(action, params);
		// }
		else if (S_GET_REQUESTS.contains(action)) {
			// 普通的GET请求内容
			return getGetRequest(params);
		} else if (UCENTER_API.contains(action)) {
			// 普通的POST请求内容
			return getGetRequest(params);
			// return getGetRequest(action, params);
		} else if (S_JSON_REQUESTS.contains(action)) {
			// 普通的JSON请求内容
			return getJsonRequest(action, params);
		}
		// else if (S_ENCRYPT_REQUESTS.contains(action)) {
		// // 加密的请求内容
		// return getEncryptRequest(action, params);
		// }
		else {
			// 不需要请求内容
			return null;
		}
	}

	/**
	 * 获取标准的XML请求内容，采用utf8编码方式
	 * 
	 * @return XML请求内容
	 * @throws UnsupportedEncodingException
	 *             假如不支持UTF8编码方式会抛出此异常
	 */
	private static String getXmlRequest(Object params)
			throws UnsupportedEncodingException {
		String body = generateXmlRequestBody(params);
		return body;
	}

	/**
	 * 获取标准的JSON请求内容，采用utf8编码方式
	 * 
	 * @return JSON请求内容
	 * @throws UnsupportedEncodingException
	 *             假如不支持UTF8编码方式会抛出此异常
	 */
	private static String getJsonRequest(int action, Object params)
			throws UnsupportedEncodingException {
		String body = generateJsonRequestBody(params);
		return body;
	}

	/**
	 * 获取标准的JSON请求内容，采用utf8编码方式
	 * 
	 * @return JSON请求内容
	 * @throws UnsupportedEncodingException
	 *             假如不支持UTF8编码方式会抛出此异常
	 */
	private static String getGetRequest(Object params)
			throws UnsupportedEncodingException {
		String body = generateGetRequestBody(params);
		return body;
	}

	// /**
	// * 获取标准的POST方式请求内容，采用utf8编码方式
	// *
	// * @return JSON请求内容
	// * @throws UnsupportedEncodingException 假如不支持UTF8编码方式会抛出此异常
	// */
	// @SuppressWarnings("unchecked")
	// private static HttpEntity getPostRequest(Object params)
	// throws UnsupportedEncodingException {
	// List<NameValuePair> nvps = new ArrayList<NameValuePair>();
	// if (params == null) {
	// return new UrlEncodedFormEntity(new ArrayList<NameValuePair>());
	// }
	//
	// HashMap<String, Object> requestParams;
	// if (params instanceof HashMap) {
	// requestParams = (HashMap<String, Object>) params;
	// } else {
	// return new UrlEncodedFormEntity(new ArrayList<NameValuePair>());
	// }
	//
	// // add parameter node
	// final Iterator<String> keySet = requestParams.keySet().iterator();
	// try {
	// while (keySet.hasNext()) {
	// final String key = keySet.next();
	// Object value = requestParams.get(key);
	// nvps.add(new BasicNameValuePair(key, value.toString()));
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// return new UrlEncodedFormEntity(new ArrayList<NameValuePair>());
	//
	// }
	// return new UrlEncodedFormEntity(nvps, HTTP.UTF_8);
	// }

	// /**
	// * 获取加密后的请求内容
	// *
	// * @return ByteArrayEntity请求内容
	// */
	// private static ByteArrayEntity getEncryptRequest(int action, Object
	// params) {
	// String body = generateXmlRequestBody(params);
	//
	// // 加密处理
	// if (action == MarketAPI.ACTION_CHARGE) {
	// final byte[] encyptedBody = SecurityUtil
	// .encryptHttpChargeBody(body);
	// return new ByteArrayEntity(encyptedBody);
	// } else {
	// final byte[] encyptedBody = SecurityUtil.encryptHttpBody(body);
	// return new ByteArrayEntity(encyptedBody);
	// }
	// }

	// /**
	// * 获取标准的表单请求内容，采用utf8编码方式
	// *
	// * @return UrlEncodedFormEntity请求内容
	// * @throws UnsupportedEncodingException
	// * 假如不支持UTF8编码方式会抛出此异常
	// */
	// @SuppressWarnings("unchecked")
	// private static UrlEncodedFormEntity getFormRequest(int action, Object
	// params)
	// throws UnsupportedEncodingException {
	//
	// if (action == MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO
	// || action == MarketAPI.ACTION_QUERY_ALIPAY_RESULT) {
	// String body = generateJsonRequestBody(params);
	// final byte[] encyptedBody = SecurityUtil
	// .encryptHttpChargePalipayBody(body);
	// String dataenc = new String(encyptedBody, HTTP.UTF_8);
	// String cno = "03";
	// String actionMethod = null;
	// if (MarketAPI.ACTION_GET_ALIPAY_ORDER_INFO == action) {
	// // 获取支付宝订单信息
	// actionMethod = "addAlipayOrder";
	// } else {
	// // 查询支付宝充值结果
	// actionMethod = "queryAlipayOrderIsSuccess";
	// }
	// final ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>(
	// 4);
	// postParams.add(new BasicNameValuePair("action", actionMethod));
	// postParams.add(new BasicNameValuePair("data", dataenc));
	// postParams.add(new BasicNameValuePair("cno", cno));
	// postParams.add(new BasicNameValuePair(Constants.KEY_SIGN, DigestUtils
	// .md5Hex("action=" + actionMethod + "&data=" + dataenc
	// + "&cno=" + cno
	// + SecurityUtil.KEY_HTTP_CHARGE_ALIPAY)));
	// return new UrlEncodedFormEntity(postParams, HTTP.UTF_8);
	// } else if (params instanceof ArrayList) {
	// return new UrlEncodedFormEntity((ArrayList<NameValuePair>) params,
	// HTTP.UTF_8);
	// }
	// return null;
	// }

	/**
	 * Generate the API XML request body
	 */
	@SuppressWarnings("unchecked")
	private static String generateXmlRequestBody(Object params) {

		if (params == null) {
			return "<request version=\"2\"></request>";
		}

		HashMap<String, Object> requestParams;
		if (params instanceof HashMap) {
			requestParams = (HashMap<String, Object>) params;
		} else {
			return "<request version=\"2\"></request>";
		}

		final StringBuilder buf = new StringBuilder();

		// 2010/12/29 update version to 2 to get comments from bbs
		buf.append("<request version=\"2\"");
		if (requestParams.containsKey("local_version")) {
			buf.append(" local_version=\"" + requestParams.get("local_version")
					+ "\" ");
			requestParams.remove("local_version");
		}
		buf.append(">");

		// add parameter node
		final Iterator<String> keySet = requestParams.keySet().iterator();
		while (keySet.hasNext()) {
			final String key = keySet.next();

			if ("upgradeList".equals(key)) {
				buf.append("<products>");
				List<PackageInfo> productsList = (List<PackageInfo>) requestParams
						.get(key);
				for (PackageInfo info : productsList) {
					buf.append("<product package_name=\"").append(
							info.packageName);
					buf.append("\" version_code=\"").append(info.versionCode)
							.append("\"/>");
				}
				buf.append("</products>");
				continue;
			} else if ("appList".equals(key)) {
				buf.append("<apps>");
				// List<UpgradeInfo> productsList = (List<UpgradeInfo>)
				// requestParams
				// .get(key);
				// for (UpgradeInfo info : productsList) {
				// buf.append("<app package_name=\"").append(info.sourceurl);
				// buf.append("\" version_code=\"").append(info.versionCode);
				// buf.append("\" version_name=\"").append(info.version);
				// buf.append("\" app_name=\"").append(wrapText(info.name));
				// // buf.append("\" md5=\"").append(info.md5);
				// buf.append("\"/>");
				// }
				buf.append("</apps>");
				continue;
			}

			buf.append("<").append(key).append(">");
			buf.append(requestParams.get(key));
			buf.append("</").append(key).append(">");
		}

		// add the enclosing quote
		buf.append("</request>");
		return buf.toString();
	}

	/**
	 * Generate the API get request body
	 */
	@SuppressWarnings("unchecked")
	private static String generateGetRequestBody(Object params) {
		if (params == null) {
			return Constants.KEY_EMPTY;
		}

		HashMap<String, Object> requestParams;
		if (params instanceof HashMap) {
			requestParams = (HashMap<String, Object>) params;
		} else {
			return Constants.KEY_EMPTY;
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
			return Constants.KEY_EMPTY;
		}
		return sb.substring(0, sb.length() - 1);
	}

	/**
	 * Generate the API JSON request body
	 */
	@SuppressWarnings("unchecked")
	private static String generateJsonRequestBody(Object params) {

		if (params == null) {
			return Constants.KEY_EMPTY;
		}

		HashMap<String, Object> requestParams;
		if (params instanceof HashMap) {
			requestParams = (HashMap<String, Object>) params;
		} else {
			return Constants.KEY_EMPTY;
		}

		// add parameter node
		final Iterator<String> keySet = requestParams.keySet().iterator();
		JSONObject jsonObject = new JSONObject();
		try {
			while (keySet.hasNext()) {
				final String key = keySet.next();
				jsonObject.put(key, requestParams.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
			return Constants.KEY_EMPTY;
		}
		return jsonObject.toString();
	}

	private static final String[] REPLACE = { "&", "&amp;", "\"", "&quot;",
			"'", "&apos;", "<", "&lt;", ">", "&gt;" };

	@SuppressWarnings("unused")
	private static String wrapText(String input) {

		if (!TextUtils.isEmpty(input)) {
			for (int i = 0, length = REPLACE.length; i < length; i += 2) {
				input = input.replace(REPLACE[i], REPLACE[i + 1]);
			}
			return input;
		} else {
			return Constants.KEY_EMPTY;
		}
	}

}
