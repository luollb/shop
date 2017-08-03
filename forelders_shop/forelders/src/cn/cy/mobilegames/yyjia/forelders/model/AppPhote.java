package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 应用详情截图
 */
public class AppPhote implements Serializable {
	private static final long serialVersionUID = 6655760981006293184L;
	public String[] photeUrls;
	public int position;

	public String[] getPhoteUrls() {
		return photeUrls;
	}

	public void setPhoteUrls(String[] photeUrls) {
		this.photeUrls = photeUrls;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

}
