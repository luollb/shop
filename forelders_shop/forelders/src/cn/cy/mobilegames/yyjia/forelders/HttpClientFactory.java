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

import java.util.WeakHashMap;

/**
 * This is http client factory class.<br>
 * 
 * You can fetch http client by using get***HttpClient method easily.
 * 
 * @author andrew.wang
 * @date 2010-9-26
 * @since Version 0.4.0
 */
public class HttpClientFactory {

	private static HttpClientFactory mInstance;

	private static String YYJIA_CLIENT = "45app";
	// private boolean isCMWAP;

	private WeakHashMap<String, AndroidHttpClient> mHttpClientMap;

	private HttpClientFactory() {
		synchronized (this) {
			mHttpClientMap = new WeakHashMap<String, AndroidHttpClient>(1);
		}
	}

	public static HttpClientFactory get() {
		if (mInstance == null) {
			mInstance = new HttpClientFactory();
		}
		return mInstance;
	}

	/**
	 * Get the http client for 45app module
	 * 
	 * @param userAgent
	 *            customize user agent
	 * @return android http client contains some default settings for android
	 *         device
	 */
	public AndroidHttpClient getHttpClient() {

		AndroidHttpClient client = mHttpClientMap.get(YYJIA_CLIENT);
		if (client != null) {
			// if (isCMWAP) {
			// client.useCmwapConnection();
			// } else {
			// client.useDefaultConnection();
			// }
			return client;
		}

		client = AndroidHttpClient.newInstance("");
		// if (isCMWAP) {
		// client.useCmwapConnection();
		// } else {
		// client.useDefaultConnection();
		// }
		mHttpClientMap.put(YYJIA_CLIENT, client);
		return client;
	}

	/**
	 * update the G-Header
	 * 
	 * @param gHeader
	 */
	public void updateMaxHelperHeader(String gHeader) {
		AndroidHttpClient client = mHttpClientMap.get(YYJIA_CLIENT);
		if (client != null) {
			client.getParams().setParameter("G-Header", gHeader);
		}
	}

	// /**
	// * 使用CMWAP网络
	// */
	// public void setCmwapConnection() {
	// isCMWAP = true;
	// }

	/**
	 * Must close all http clients when application is closed
	 */
	public synchronized void close() {
		AndroidHttpClient client;
		if (mHttpClientMap.containsKey(YYJIA_CLIENT)) {
			client = mHttpClientMap.get(YYJIA_CLIENT);
			if (client != null) {
				client.close();
				client = null;
			}
		}
		mHttpClientMap.clear();
		mInstance = null;
	}
}