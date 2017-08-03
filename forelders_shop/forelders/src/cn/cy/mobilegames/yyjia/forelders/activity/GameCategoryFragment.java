package cn.cy.mobilegames.yyjia.forelders.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.cy.mobilegames.yyjia.forelders.Constants;

import com.umeng.analytics.MobclickAgent;

/**
 * 游戏分类
 */
public class GameCategoryFragment extends CategoryFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		setType(Constants.REQUEST_VALUE_GAME);
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {

		super.onStart();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
	}

}
