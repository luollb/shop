package cn.cy.mobilegames.yyjia.forelders.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 管理页面
 */
public class TabManageFragment extends BaseFragment {
	private RelativeLayout downMgLayout, updateMgLayout, uninstallLayout,
	// loginLayout, registerLayout,// isLoginLayout,
			favoriteMgLayout;
	private View view;

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_manage_view, null);
			initView(view);
			Utils.D("manage view == null initView(view)");
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		Utils.D("manage onCreateView");
		return view;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void initView(View view) {
		downMgLayout = (RelativeLayout) view
				.findViewById(R.id.manage_down_layout);
		updateMgLayout = (RelativeLayout) view
				.findViewById(R.id.manage_update_layout);
		favoriteMgLayout = (RelativeLayout) view
				.findViewById(R.id.manage_favorite_layout);
		uninstallLayout = (RelativeLayout) view
				.findViewById(R.id.manage_uninstall_layout);

		downMgLayout.setOnClickListener(onClick);
		updateMgLayout.setOnClickListener(onClick);
		favoriteMgLayout.setOnClickListener(onClick);
		uninstallLayout.setOnClickListener(onClick);

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.manage_down_layout:
				toMgDown();
				break;
			case R.id.manage_update_layout:
				toMgUpdate();
				break;
			case R.id.manage_favorite_layout:
				toMgFavorite();
				break;
			case R.id.manage_uninstall_layout:
				toMgUninstall();
				break;
			default:
				break;
			}
		}
	};

	private void toMgUninstall() {
		goActivity(ManageUninstallActivity.class);
	}

	private void toMgFavorite() {
		goActivity(ManageFavoriteActivity.class);
	}

	private void toMgUpdate() {
		goActivity(ManageUpdateActivity.class);
	}

	private void toMgDown() {
		goActivity(DownloadListActivity.class);
	}

	private void goActivity(Class<?> cls) {
		startActivity(new Intent(getActivity(), cls));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int method, Object obj) {

	}

	@Override
	public void onError(int method, int statusCode) {

	}

}
