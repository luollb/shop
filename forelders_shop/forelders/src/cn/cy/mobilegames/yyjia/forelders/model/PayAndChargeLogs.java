package cn.cy.mobilegames.yyjia.forelders.model;

import java.util.ArrayList;

public class PayAndChargeLogs {
	public Integer endPosition;
	public Integer totalSize;
	public ArrayList<PayAndChargeLog> payAndChargeLogList;

	public PayAndChargeLogs() {
		payAndChargeLogList = new ArrayList<PayAndChargeLog>();
	}

}
