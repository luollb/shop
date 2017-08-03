package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 下拉菜单
 */
public class MenuBean implements Serializable {
	private static final long serialVersionUID = 6655760981006293184L;
	public String name;
	public String imgUrl;
	public int imgRes;

	public int getImgRes() {
		return imgRes;
	}

	public void setImgRes(int imgRes) {
		this.imgRes = imgRes;
	}

	public MenuBean(String name, int imgRes) {
		super();
		this.name = name;
		this.imgRes = imgRes;
	}

	public MenuBean(String name, String imgUrl) {
		super();
		this.name = name;
		this.imgUrl = imgUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

}
