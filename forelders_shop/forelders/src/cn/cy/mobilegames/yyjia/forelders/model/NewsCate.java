package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 资讯分类
 */
public class NewsCate implements Serializable {

	private static final long serialVersionUID = 7160183636323618822L;
	public String categoryid;
	public String categoryname;
	public String newslist;
	public String newsadlist;

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getNewslist() {
		return newslist;
	}

	public void setNewslist(String newslist) {
		this.newslist = newslist;
	}

	public String getNewsadlist() {
		return newsadlist;
	}

	public void setNewsadlist(String newsadlist) {
		this.newsadlist = newsadlist;
	}

}
