package cn.cy.mobilegames.yyjia.forelders.model;

import java.io.Serializable;

/**
 * 
 */
public class InfoAndContent implements Serializable {

	private static final long serialVersionUID = 2808669011219965381L;

	public String info;
	public Integer status;
	public String content;
	public String msg;

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
