package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 资讯列表(简略)
 */
public class NewsList implements Serializable {

	private static final long serialVersionUID = -7820937233968514480L;
	public String newid;
	public String title;
	public String categoryid;
	public String dateline;
	public String catename;

	public String getNewid() {
		return newid;
	}

	public void setNewid(String newid) {
		this.newid = newid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCategoryid() {
		return categoryid;
	}

	public void setCategoryid(String categoryid) {
		this.categoryid = categoryid;
	}

	public String getDateline() {
		return dateline;
	}

	public void setDateline(String dateline) {
		this.dateline = dateline;
	}

	public String getCatename() {
		return catename;
	}

	public void setCatename(String catename) {
		this.catename = catename;
	}

}
