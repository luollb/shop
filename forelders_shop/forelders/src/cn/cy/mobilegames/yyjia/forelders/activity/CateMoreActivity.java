package cn.cy.mobilegames.yyjia.forelders.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;

public class CateMoreActivity extends ProductListActivity {
	private String id, name;


	@Override
	public boolean doInitView(Bundle savedInstanceState) {
		setTitle(name);
		return super.doInitView(savedInstanceState);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		if (intent != null) {
			id = intent.getStringExtra(Constants.KEY_APP_ID);
			name = intent.getStringExtra(Constants.KEY_APP_NAME);
		}
		super.onCreate(savedInstanceState);
	}

	@Override
	public void doLazyload() {
		setCid(id);
		setTotalSize(1000);
		setRequestCode(MarketAPI.ACTION_GET_CATE_MORE);
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
	protected int getItemCount() {

		return super.getItemCount();
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
