package cn.cy.mobilegames.yyjia.forelders.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;

/**
 * 软件--排行
 */
public class SoftRankFragment extends ProductListFragment {

	@Override
	@SuppressLint("InflateParams")
	public boolean doInitView(Bundle savedInstanceState) {
		setRank(true);
		return super.doInitView(savedInstanceState);
	}

	@Override
	public void setTotalSize(int size) {

		super.setTotalSize(size);
	}

	@Override
	public void setRequestCode(int code) {

		super.setRequestCode(code);
	}

	@Override
	public void doLazyload() {
		setRequestCode(MarketAPI.ACTION_GET_SOFT_RANK);
		super.doLazyload();
	}

	@Override
	public AppListAdapter doInitListAdapter() {

		return super.doInitListAdapter();
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
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		super.onItemClick(parent, view, position, id);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
	}

}
