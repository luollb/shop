package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.DBUtils;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class UserCenterFragment extends Fragment implements ApiRequestListener {
	private ListView manageListView;
	private View view;
	private UserCenterAdapter manageAdapter;
	private FragmentActivity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		activity = getActivity();
		initManageData();
		MarketAPI.getUpgrade(activity, this);
		super.onCreate(savedInstanceState);
	}

	private void initView(View view) {
		manageListView = (ListView) view
				.findViewById(R.id.user_center_manage_listview);
		manageListView.setAdapter(manageAdapter);
	}

	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (view == null) {
			view = inflater.inflate(R.layout.user_center_view, null);
			initView(view);
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		return view;
	}

	public void initManageData() {
		ArrayList<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < Constants.userManageRes.length; i++) {
			HashMap<String, Object> item = new HashMap<String, Object>();
			item.put(Constants.KEY_APP_LOGO, Constants.userManageRes[i]);
			item.put(Constants.KEY_APP_ICON, R.drawable.see_more);
			item.put(Constants.KEY_APP_NAME, Constants.userManageTitle[i]);
			result.add(item);
		}
		manageAdapter = new UserCenterAdapter(activity, result);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	class UserCenterAdapter extends BaseAdapter {
		ArrayList<HashMap<String, Object>> data;
		Context context;

		public UserCenterAdapter(Context mContext,
				ArrayList<HashMap<String, Object>> datas) {
			this.context = mContext;
			this.data = datas;
		}

		@Override
		public int getCount() {
			return data == null ? 0 : data.size();
		}

		@Override
		public Object getItem(int position) {
			return data == null ? null : data.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			View view = convertView;
			ViewHold hold = null;
			if (convertView == null) {
				hold = new ViewHold();
				view = LayoutInflater.from(context).inflate(
						R.layout.user_center_item, null);
				hold.funtionImg = (ImageView) view
						.findViewById(R.id.setting_more);
				hold.headImg = (ImageView) view.findViewById(R.id.setting_logo);
				hold.name = (TextView) view.findViewById(R.id.setting_title);
				hold.item = (RelativeLayout) view
						.findViewById(R.id.setting_view);
				view.setTag(hold);
			} else {
				hold = (ViewHold) view.getTag();
			}

			final HashMap<String, Object> item = data.get(position);
			hold.funtionImg.setImageResource(Integer.valueOf(item.get(
					Constants.KEY_APP_ICON).toString()));
			hold.funtionImg.setVisibility(View.VISIBLE);
			hold.headImg.setVisibility(View.VISIBLE);
			hold.headImg.setPadding(20, 40, 20, 40);
			hold.headImg.setImageResource(Integer.valueOf(item.get(
					Constants.KEY_APP_LOGO).toString()));
			hold.name.setText(item.get(Constants.KEY_APP_NAME).toString());
			hold.item.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_DAILY_SIGN)) {
						// ToastUtil.showShortToast(context, "每日签到");
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_MY_FOCUS)) {
						// ToastUtil.showShortToast(context,
						// Constants.MANAGE_MY_FOCUS);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_MY_MESSAGE)) {
						// ToastUtil.showShortToast(context,
						// Constants.MANAGE_MY_MESSAGE);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_MY_COMMENT)) {
						// ToastUtil.showShortToast(context,
						// Constants.MANAGE_MY_COMMENT);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_DOWNLOAD)) {
						Utils.toOtherClass(activity, DownloadListActivity.class);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_UPDATE)) {
						Utils.toOtherClass(activity,
								ManageUpdateActivity.class, null);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_UNINSTALL)) {
						Utils.toOtherClass(activity,
								ManageUninstallActivity.class);
					} else if (item.get(Constants.KEY_APP_NAME).toString()
							.equals(Constants.MANAGE_SETTING)) {
						((MainActivity) activity).toggleMenu();
					}
				}
			});

			return view;
		}

	}

	class ViewHold {
		private ImageView headImg, funtionImg;
		private TextView name;
		private RelativeLayout item;
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_UPGRADE:
			if (obj != null && obj instanceof List<?>) {
				@SuppressWarnings("unchecked")
				ArrayList<UpgradeInfo> upgrades = (ArrayList<UpgradeInfo>) obj;
				DBUtils.addUpgradeProduct(activity, upgrades);
			} else {
				DBUtils.clearUpgradeProduct(activity);
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {

	}

}
