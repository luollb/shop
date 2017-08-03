package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 资讯广告
 */
public class NewsAdList implements Serializable {

	private static final long serialVersionUID = 7160183636323618822L;
	public String newid;
	public String pic;
	public String title;
	public String dateline;
	public String newsurl;

	public String getNewid() {
		return newid;
	}

	public void setNewid(String newid) {
		this.newid = newid;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getNewsurl() {
		return newsurl;
	}

	public void setNewsurl(String newsurl) {
		this.newsurl = newsurl;
	}

}
