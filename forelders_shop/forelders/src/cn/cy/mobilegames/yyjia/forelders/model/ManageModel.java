package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

public class ManageModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5131983966968063506L;
	/**
	 * 
	 */

	public int picRes;
	public String title;
	public int functionRes;

	public int getPicRes() {
		return picRes;
	}

	public void setPicRes(int picRes) {
		this.picRes = picRes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getFunctionRes() {
		return functionRes;
	}

	public void setFunctionRes(int functionRes) {
		this.functionRes = functionRes;
	}

}
