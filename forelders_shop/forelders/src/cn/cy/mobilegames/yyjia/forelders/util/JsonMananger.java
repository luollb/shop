/*
    ShengDao Android Client, JsonMananger
    Copyright (c) 2014 ShengDao Tech Company Limited
 */

package cn.cy.mobilegames.yyjia.forelders.util;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;

/**
 * [JSON解析管理类]
 * 
 * @author huxinwu
 * @version 1.0
 * @date 2014-3-5
 **/
public class JsonMananger {

	static {
		TypeUtils.compatibleWithJavaBean = true;
	}

	@SuppressWarnings("unused")
	private static final String tag = JsonMananger.class.getSimpleName();

	/**
	 * 将json字符串转换成java对象
	 * 
	 * @param json
	 * @param cls
	 * @return
	 * @throws HttpException
	 */
	public static <T> T jsonToBean(String json, Class<T> cls) {
		try {
			return JSON.parseObject(json, cls);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将json字符串转换成java List对象
	 * 
	 * @param json
	 * @param cls
	 * @return
	 * @throws HttpException
	 */
	public static <T> List<T> jsonToList(String json, Class<T> cls) {
		try {
			return JSON.parseArray(json, cls);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 将bean对象转化成json字符串
	 * 
	 * @param obj
	 * @return
	 * @throws HttpException
	 */
	public static String beanToJson(Object obj) {
		String result;
		try {
			result = JSON.toJSONString(obj);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
