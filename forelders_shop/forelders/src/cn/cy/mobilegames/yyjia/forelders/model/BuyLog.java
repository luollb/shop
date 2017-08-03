package cn.cy.mobilegames.yyjia.forelders.model;

import android.content.ContentValues;
import cn.cy.mobilegames.yyjia.forelders.util.MarketProvider;

public class BuyLog {
	public String pId;
	public String packageName;

	@Override
	public String toString() {
		String temp = pId + " " + packageName;
		return temp;
	}

	public void onAddToDatabase(ContentValues values) {
		values.put(MarketProvider.COLUMN_P_ID, pId);
		values.put(MarketProvider.COLUMN_P_PACKAGE_NAME, packageName);
	}
}
