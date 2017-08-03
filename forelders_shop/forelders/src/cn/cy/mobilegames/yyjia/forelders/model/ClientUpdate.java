package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 客户端升级详情
 */
public class ClientUpdate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3096994119921519200L;
	/**
	 * 
	 */

	public String version;
	public long updatetime;
	public String content;
	public String downurl;
	public int upversion;

}
