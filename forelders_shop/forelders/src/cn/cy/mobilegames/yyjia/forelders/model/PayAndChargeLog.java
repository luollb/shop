package cn.cy.mobilegames.yyjia.forelders.model;

public class PayAndChargeLog {
	public static final int TYPE_CONSUME = 1;
	public static final int TYPE_MARKET = 2;
	public static final int TYPE_CHARGE = 3;

	public String name;
	public String desc;
	public String time;
	public Integer payment;
	public Integer type;
	public Integer id;
	public String iconUrl;
	public Integer sourceType;

	public PayAndChargeLog() {
		type = TYPE_MARKET;
	}
}