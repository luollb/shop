package cn.cy.mobilegames.yyjia.forelders.model;

import android.content.ContentValues;
import cn.cy.mobilegames.yyjia.forelders.util.MarketProvider;

public class UpgradeInfo {
	public String sourceurl;
	public String appid;
	public String version;
	public String size;
	public Integer versionCode;
	public String updateinfo;
	public String name;
	public String logo;
	public String appdownurl;
	/**
	 * 1 为忽略 0为升级
	 */
	public int update;

	public ContentValues getContentValues() {
		final ContentValues values = new ContentValues();
		values.put(MarketProvider.COLUMN_P_PACKAGE_NAME, sourceurl);
		values.put(MarketProvider.COLUMN_P_ID, appid);
		values.put(MarketProvider.COLUMN_P_NEW_VERSION_NAME, version);
		values.put(MarketProvider.COLUMN_P_SIZE, size);
		values.put(MarketProvider.COLUMN_P_NEW_VERSION_CODE, versionCode);
		values.put(MarketProvider.COLUMN_P_UPGRADE_INFO, updateinfo);
		values.put(MarketProvider.COLUMN_P_NAME, name);
		values.put(MarketProvider.COLUMN_P_ICON_URL, logo);
		values.put(MarketProvider.COLUMN_P_DOWN_URL, appdownurl);
		values.put(MarketProvider.COLUMN_P_IGNORE, update);
		return values;
	}
}
