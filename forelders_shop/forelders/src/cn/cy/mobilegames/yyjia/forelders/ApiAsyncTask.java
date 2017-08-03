package cn.cy.mobilegames.yyjia.forelders;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import android.content.Context;
import android.os.AsyncTask;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

/**
 * API请求任务
 */
public class ApiAsyncTask extends AsyncTask<Void, Void, Object> {

	/**
	 * 超时（网络）异常
	 */
	public static final int TIMEOUT_ERROR = 600;
	/**
	 * 业务异常
	 */
	public static final int BUSSINESS_ERROR = 610;

	private int mReuqestAction;
	private ApiRequestListener mHandler;
	private Object mParameter;
	private Context mContext;
	private ResponseCacheManager mResponseCache;
	private RequestQueue mQueue;
	private String cacheKey;
	private Object mResult;

	ApiAsyncTask(Context context, int action, ApiRequestListener handler,
			Object param) {
		this.mContext = context;
		this.mReuqestAction = action;
		this.mHandler = handler;
		this.mParameter = param;
		this.mResponseCache = ResponseCacheManager.getInstance();
		this.mQueue = Volley.newRequestQueue(context);
	}

	@Override
	protected Object doInBackground(final Void... params) {
		String requestUrl = MarketAPI.API_URLS[mReuqestAction];

		String requestEntity = null;
		try {
			requestEntity = ApiRequestFactory.getRequestEntity(mReuqestAction,
					mParameter);
		} catch (UnsupportedEncodingException e) {
			Utils.E("OPPS...This device not support UTF8 encoding.[should not happend]");
			return BUSSINESS_ERROR;
		}

		Object result = null;
		// 缓存唯一标识
		cacheKey = "";
		if (!ApiRequestFactory.API_NO_CACHE_MAP.contains(mReuqestAction + "")) {
			final boolean isBodyEmpty = (requestEntity == null);
			if (isBodyEmpty) {
				// if no http entity then directly use the request url as cache
				// key
				cacheKey = Utils.getMD5(requestUrl);
			} else {
				// we only cache string request
				cacheKey = Utils.getMD5(requestUrl + requestEntity);
			}
			// fetch result from the cache
			result = mResponseCache.getResponse(cacheKey);
			if (result != null) {
				Utils.E("retrieve response from the cache");
				handResult(result);
				return result;
			}
		} else {
			if (!Utils.isNetworkAvailable(mContext)) {
				return TIMEOUT_ERROR;
			}
		}
		Utils.E("requestUrl   " + requestUrl + "?"
				+ Utils.generateGetRequestBody(mParameter));
		boolean isGet = ApiRequestFactory.S_GET_REQUESTS
				.contains(mReuqestAction) ? true : false;

		StringRequest stringRequest = new StringRequest(
				isGet ? Request.Method.GET : Request.Method.POST,
				isGet ? requestUrl + "?"
						+ Utils.generateGetRequestBody(mParameter) : requestUrl,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String result) {
						// Utils.E(" onResponse  action  "+mReuqestAction+"  result  "+result);
						if (result != null
								&& !ApiRequestFactory.API_NO_CACHE_MAP
										.contains(mReuqestAction + "")) {
							mResponseCache.putResponse(cacheKey, result);
						}
						mResult = result;
						handResult(mResult);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError volleyError) {
						Utils.E("onResponse  " + volleyError);
						mResult = volleyError;
						handResult(mResult);
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				if (mParameter == null) {
					return null;
				} else if (mParameter instanceof Map<?, ?>) {
					return (Map<String, String>) mParameter;
				} else {
					return null;
				}
			}
		};
		mQueue.add(stringRequest);
		return mResult;
	}

	@Override
	protected void onPostExecute(Object response) {

	}

	private void handResult(Object response) {
		if (mHandler == null) {
			Utils.E("  mHandler == null");
			return;
		}
		// if (mContext instanceof Activity && ((Activity)
		// mContext).isFinishing()) {
		// // 页面已经被关闭，无须进行后续处理
		// Utils.E("  页面已经被关闭，无须进行后续处理");
		// return;
		// }
		if (response == null) {
			mHandler.onError(this.mReuqestAction, BUSSINESS_ERROR);
			Utils.E("  response == null");
			return;
		}

		if (response instanceof VolleyError) {
			mHandler.onError(this.mReuqestAction, BUSSINESS_ERROR);
			Utils.E("  response == VolleyError");
			return;
		}

		mHandler.onSuccess(this.mReuqestAction, ApiResponseFactory.getResponse(
				mContext, mReuqestAction, response.toString()));
	}

	// // 225 请求的数据不存在
	// public static final int SC_DATA_NOT_EXIST = 225;
	// // 232 非法回复内容
	// public static final int SC_ILLEGAL_COMMENT = 232;
	// // 421 请求头参数为空或参数不完整（User-Agent等）
	// public static final int SC_ILLEGAL_USER_AGENT = 421;
	// // 422 请求xml解析错误
	// public static final int SC_XML_ERROR = 422;
	// // 423 请求xml中参数缺失或参数类型错误
	// public static final int SC_XML_PARAMS_ERROR = 423;
	// // 427 请求解密或解码错误
	// public static final int SC_ENCODE_ERROR = 427;
	// // 520 DB访问或SQL执行出错
	// public static final int SC_SERVER_DB_ERROR = 520;
	//
	// /**
	// * 处理公用Http Status Code
	// *
	// * @param statusCode Http Status Code
	// * @return 此Code是否被处理（True：已经被处理）
	// */
	// private boolean handleCommonError(int statusCode) {
	//
	// if (statusCode == 200) {
	// return true;
	// }
	// // if(statusCode >= TIMEOUT_ERROR) {
	// // Utils.makeEventToast(mContext,
	// // mContext.getString(R.string.notification_server_error),
	// // false);
	// // } else if(statusCode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
	// // Utils.makeEventToast(mContext,
	// // mContext.getString(R.string.notification_server_error),
	// // false);
	// // } else if(statusCode >= HttpStatus.SC_BAD_REQUEST) {
	// // Utils.makeEventToast(mContext,
	// // mContext.getString(R.string.notification_client_error),
	// // false);
	// // }
	// return false;
	// }

	/**
	 * 市场API请求监听器
	 * 
	 * @author andrew.wang
	 * @date 2010-10-28
	 * @since Version 0.4.0
	 */
	public interface ApiRequestListener {

		/**
		 * The CALLBACK for success aMarket API HTTP response
		 * 
		 * @param obj
		 *            the HTTP response
		 */
		void onSuccess(int method, Object obj);

		/**
		 * The CALLBACK for failure aMarket API HTTP response
		 * 
		 * @param statusCode
		 *            the HTTP response status code
		 */
		void onError(int method, int statusCode);
	}

}