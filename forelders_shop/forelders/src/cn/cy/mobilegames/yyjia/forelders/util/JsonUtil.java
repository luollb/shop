package cn.cy.mobilegames.yyjia.forelders.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON鏁版嵁瑙ｆ瀽宸ュ叿绫伙紝鐢ㄤ簬灏咼SON瀛楃涓茶浆鎹㈡垚鎸囧畾鐨勫璞℃垨瀵硅薄List锛屼互鍙奙ap鎴栧寘鍚玀ap鐨凩ist
 * 
 * @author wu weihua
 */
public class JsonUtil<T> {

	private static final String BYTE = "java.lang.Byte";
	private static final String INTEGER = "java.lang.Integer";
	private static final String SHORT = "java.lang.Short";
	private static final String LONG = "java.lang.Long";
	private static final String BOOLEAN = "java.lang.Boolean";
	private static final String CHAR = "java.lang.Character";
	private static final String FLOAT = "java.lang.Float";
	private static final String DOUBLE = "java.lang.Double";

	private static final String VALUE_BYTE = "byte";
	private static final String VALUE_INTEGER = "int";
	private static final String VALUE_SHORT = "short";
	private static final String VALUE_LONG = "long";
	private static final String VALUE_BOOLEAN = "boolean";
	private static final String VALUE_CHAR = "char";
	private static final String VALUE_FLOAT = "float";
	private static final String VALUE_DOUBLE = "double";

	/**
	 * 鏍规嵁key鑾峰彇缁欏畾鐨刯son鏁版嵁鐨勶拷??
	 * 
	 * @param json
	 *            缁欏畾鐨凧SON瀛楃锟�?
	 * @param key
	 *            鎸囧畾鐨勮鑾峰彇鍊兼墍瀵瑰簲鐨刱ey
	 * @return 
	 *         杩斿洖锟�?涓瓧绗︿覆锛岃〃绀烘牴鎹寚瀹氱殑key锟�?寰楀埌鐨勶拷?锟斤紝鑾峰彇澶辫触鎴栧彂鐢烰SON瑙ｆ瀽閿欒鍒欒繑鍥炵┖
	 *         瀛楃锟� ?
	 */
	public static String getJsonValueByKey(String json, String key) {
		String value = "";
		try {
			JSONObject jo = new JSONObject(json);
			value = jo.getString(key);
		} catch (JSONException e) {
			Utils.E("  getJsonValueByKey  " + e.getLocalizedMessage());
		}
		return value;
	}

	/**
	 * 灏嗘寚瀹氱殑JSON瀛楃涓茶浆鎹㈡垚cls鎸囧畾鐨勭被鐨勫疄渚嬪锟�?
	 * 
	 * @param json
	 *            缁欏畾鐨凧SON瀛楃锟�?
	 * @param cls
	 *            鎸囧畾瑕佽浆鎹㈡垚鐨勫璞℃墍灞炵殑绫诲瀷Class瀹炰緥
	 * @return 杩斿洖cls鎸囧畾绫诲瀷鐨勫璞″疄锟�?,鍏朵腑鐨勫瓧娈典笌json鏁版嵁閿拷?锟藉锟�?锟�?瀵瑰簲
	 */
	public static <T> T toObject(String json, Class<T> cls) {
		T obj = null;
		try {
			JSONObject jsonObject = new JSONObject(json);
			obj = cls.newInstance();
			Field[] fields = cls.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isFinal(field.getModifiers())
						|| Modifier.isPrivate(field.getModifiers())) {
					continue;
				}
				try {
					String key = field.getName();
					if (jsonObject.get(key) == JSONObject.NULL) {
						field.set(obj, null);
					} else {
						Object value = getValue4Field(jsonObject.get(key),
								jsonObject.get(key).getClass().getName());
						field.set(obj, value);
					}
				} catch (JSONException e) {
					field.set(obj, null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.E("  toObject  " + e.getLocalizedMessage());
		}
		return obj;
	}

	/**
	 * 鎶婃寚瀹氱殑瀵硅薄orginalValue杞崲鎴恡ypeName鎸囧畾鐨勭被鍨嬬殑瀵硅薄
	 * 
	 * @param orginalValue
	 *            瀵硅薄鍦ㄨ浆鎹箣鍓嶇殑锟�?
	 * @param fieldType
	 *            瑕佽浆鎹㈢殑绫诲瀷鍚嶇О
	 * @return
	 */
	private static Object getValue4Field(Object orginalValue, String typeName) {
		Object value = orginalValue.toString();
		if (typeName.equals(BYTE) || typeName.equals(VALUE_BYTE)) {
			value = Byte.class.cast(orginalValue);
		}
		if (typeName.equals(INTEGER) || typeName.equals(VALUE_INTEGER)) {
			value = Integer.class.cast(orginalValue);
		}
		if (typeName.equals(SHORT) || typeName.equals(VALUE_SHORT)) {
			value = Short.class.cast(orginalValue);
		}
		if (typeName.equals(LONG) || typeName.equals(VALUE_LONG)) {
			value = Long.class.cast(orginalValue);
		}
		if (typeName.equals(BOOLEAN) || typeName.equals(VALUE_BOOLEAN)) {
			value = Boolean.class.cast(orginalValue);
		}
		if (typeName.equals(CHAR) || typeName.equals(VALUE_CHAR)) {
			value = Character.class.cast(orginalValue);
		}
		if (typeName.equals(FLOAT) || typeName.equals(VALUE_FLOAT)) {
			value = Float.class.cast(orginalValue);
		}
		if (typeName.equals(DOUBLE) || typeName.equals(VALUE_DOUBLE)) {
			value = Double.class.cast(orginalValue);
		}
		return value;
	}

	/**
	 * 灏嗘寚瀹氱殑JSON瀛楃涓茶浆鎹㈡垚鍖呭惈cls鎸囧畾鐨勭被鍨嬬殑瀹炰綋瀵硅薄List闆嗗悎
	 * 
	 * @param json
	 *            缁欏畾鐨凧SON瀛楃锟�?
	 * @param cls
	 *            鎸囧畾瑕佽浆鎹㈡垚鐨勫璞℃墍灞炵殑绫诲瀷Class瀹炰緥
	 * @return 杩斿洖锟�?涓狶ist闆嗗悎锛屽叾涓寘鍚玧son涓殑鏁版嵁鍏冪礌锟�?瀵瑰簲鐨勫疄浣撳璞″疄锟�?
	 */
	public static <Object> List<Object> toObjectList(String json,
			Class<Object> cls) {
		List<Object> list = new ArrayList<Object>();
		try {
			List<String> jsonStrList = toJsonStrList(json);
			for (String jsonStr : jsonStrList) {
				Object obj = toObject(jsonStr, cls);
				list.add(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Utils.E("  toObjectList  " + e.getMessage());
		}
		return list;
	}

	/**
	 * 灏嗕竴涓暟缁勫瀷json瀛楃涓茶浆鎹㈡垚鍖呭惈瀛恓son瀛楃涓茬殑List闆嗗悎
	 * 
	 * @param json
	 *            缁欏畾鐨凧SON瀛楃锟�?
	 * @return 杩斿洖锟�?涓狶ist闆嗗悎锛屽寘鍚竴缁勫瓧绗︿覆锛屽搴斾簬缁欏畾鍘熷JSON鏁版嵁鍐呭厓绱犵殑瀛楃涓插舰锟�?
	 */
	public static List<String> toJsonStrList(String json) {
		List<String> strList = new ArrayList<String>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				String jsonStr = jsonArray.getString(i);
				strList.add(jsonStr);
			}
		} catch (JSONException e) {
			Utils.E("  toJsonStrList  " + e.getMessage());
		}
		return strList;
	}

	/**
	 * 灏唈son瀛楃涓茶浆鎹负Map
	 * 
	 * @param json
	 * @return
	 */
	public static Map<String, Object> toMap(String json) {
		Map<String, Object> map = null;
		try {
			JSONObject jo = new JSONObject(json);
			map = convertJSONObjectToMap(jo);
		} catch (Exception e) {
			Utils.E("  toMap  " + e.getMessage());
		}
		return map;
	}

	/**
	 * 灏唈son瀛楃涓茶浆鎹负鍖呭惈Map鐨凩ist闆嗗悎
	 * 
	 * @param json
	 * @return
	 */
	public static List<Map<String, Object>> toMapList(String json) {
		List<Map<String, Object>> mapList = new ArrayList<Map<String, Object>>();
		try {
			JSONArray jsonArray = new JSONArray(json);
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jo = jsonArray.getJSONObject(i);
				Map<String, Object> map = convertJSONObjectToMap(jo);
				mapList.add(map);
			}
		} catch (JSONException e) {
			Utils.E("  toMapList  " + e.getMessage());
		}
		return mapList;
	}

	/**
	 * 灏嗕竴涓狫SONObject瀵硅薄杞崲涓篗ap
	 * 
	 * @param jo
	 * @return
	 * @throws JSONException
	 */
	private static Map<String, Object> convertJSONObjectToMap(JSONObject jo)
			throws JSONException {
		Map<String, Object> map = new HashMap<String, Object>();
		JSONObject newJo = mergeJsonNodes(jo);

		JSONArray names = newJo.names();
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			Object value = newJo.get(key);
			if ((value != null) && (!value.toString().equals(""))
					&& (!value.toString().equals("null"))) {
				map.put(key, value);
			}
		}
		return map;
	}

	/**
	 * 灏咼SON瀵硅薄鐨勯潪锟�?闃跺瓙缁撶偣涓庝竴闃剁粨鐐瑰悎锟�?
	 * 
	 * @param oldJo
	 *            鍖呭惈闈炰竴闃剁粨鐐圭殑Json瀵硅薄
	 * @return 杩斿洖鍚堝苟涔嬪悗鐨勶紝鍙寘鍚竴闃剁粨鐐圭殑Json瀵硅薄
	 */
	private static JSONObject mergeJsonNodes(JSONObject oldJo)
			throws JSONException {
		JSONObject newJo = oldJo;
		JSONArray names = newJo.names();
		List<String> delKeys = new ArrayList<String>(); // 寰呭垹闄ょ殑闈炰竴闃剁粨鐐圭殑Json瀵硅薄鐨刱ey

		// 鎵惧嚭锟�?瑕佸悎骞剁殑瀛愮粨鐐圭殑key
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			if (newJo.optJSONObject(key) != null) {
				delKeys.add(key);
			}
		}
		// 鍚堝苟鎵惧埌鐨勫瓙缁撶偣涓庝竴闃剁粨鐐癸紝骞跺垹闄ゅ師鍏堢殑瀛愮粨锟�?
		for (String key : delKeys) {
			JSONObject subJo = newJo.getJSONObject(key);
			subJo = mergeJsonNodes(subJo); // 閫掑綊鏁寸悊瀛愮粨鐐圭殑锟�?鏈夊瓙缁撶偣
			newJo = merge(newJo, subJo);
			newJo.remove(key);
		}
		return newJo;
	}

	/**
	 * 鍚堝苟涓や釜JSON瀵硅薄
	 * 
	 * @param jo1
	 * @param jo2
	 * @return 杩斿洖鍚堝苟涔嬪悗鐨凧SON瀵硅薄
	 */
	private static JSONObject merge(JSONObject jo1, JSONObject jo2)
			throws JSONException {
		JSONObject newJo = jo1;
		JSONArray names = jo2.names();
		for (int i = 0; i < names.length(); i++) {
			String key = names.getString(i);
			newJo.put(key, jo2.get(key));
		}
		return newJo;
	}

	/**
	 * 鍒ゆ柇锟�?涓狫SON瀛楃涓叉槸鍚︽槸绌烘暟锟�?
	 * 
	 * @param json
	 * @return
	 */
	public static boolean isJsonNull(String json) {
		if (json == null || json.equals("") || json.equals("null")
				|| json.equals("{}") || json.equals("[]")) {
			return true;
		} else {
			return false;
		}
	}

}