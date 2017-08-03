/**
 * Program  : T1Activity.java
 * Author   : qianj
 * Create   : 2012-5-31 下午4:24:32
 *
 * Copyright 2012 by newyulong Technologies Ltd.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of newyulong Technologies Ltd.("Confidential Information").  
 * You shall not disclose such Confidential Information and shall 
 * use it only in accordance with the terms of the license agreement 
 * you entered into with newyulong Technologies Ltd.
 *
 */

package cn.cy.mobilegames.yyjia.forelders.activity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;

/**
 * 猜你喜欢
 */
public class ManageFavoriteActivity extends ProductListActivity {

	@Override
	public boolean doInitView(Bundle savedInstanceState) {

		return super.doInitView(savedInstanceState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
	}

	@Override
	public void setRequestCode(int code) {

		super.setRequestCode(code);
	}

	@Override
	public void doLazyload() {

		super.doLazyload();
	}

	@Override
	public AppListAdapter doInitListAdapter() {

		return super.doInitListAdapter();
	}

	@Override
	public void setTotalSize(int size) {

		super.setTotalSize(size);
	}

	@Override
	public void setTitle(String title) {

		super.setTitle(title);
	}

	@Override
	public void setCid(String id) {

		super.setCid(id);
	}

	@Override
	protected int getItemCount() {

		return super.getItemCount();
	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	public void onSuccess(int method, Object obj) {

		super.onSuccess(method, obj);
	}

	@Override
	public void onError(int method, int statusCode) {

		super.onError(method, statusCode);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		super.onItemClick(parent, view, position, id);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
	}

}
