package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.activity.MainActivity;
import cn.cy.mobilegames.yyjia.forelders.activity.ManageUninstallActivity;
import cn.cy.mobilegames.yyjia.forelders.activity.SearchActivity;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class MenuAdapter extends BaseAdapter {
	private List<MenuBean> mList;
	private Activity mContext;

	public MenuAdapter(List<MenuBean> lists, Activity context) {
		super();
		this.mList = lists;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Object getItem(int postion) {
		return mList.get(postion);
	}

	@Override
	public long getItemId(int postion) {
		return postion;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int postion, View convertView, ViewGroup parent) {
		Viewhold holder = null;
		View view = convertView;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.menu_item,
					null);
			holder = new Viewhold();
			holder.name = (TextView) view.findViewById(R.id.spinner_tv);
			holder.logo = (ImageView) view.findViewById(R.id.spinner_iv);
			holder.item = (RelativeLayout) view.findViewById(R.id.menu_item);
			view.setTag(holder);
		} else {
			holder = (Viewhold) view.getTag();
		}

		final MenuBean bean = mList.get(postion);
		holder.name.setText(bean.name);
		holder.logo.setImageResource(bean.imgRes);
		holder.item.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (bean.name.equals(Constants.MANAGE_SEARCH)) {
					Utils.toOtherClass(mContext, SearchActivity.class);
				}
				if (bean.name.equals(Constants.MANAGE_DOWNLOAD)) {
					Utils.toOtherClass(mContext, DownloadListActivity.class);
				}
				if (bean.name.equals(Constants.MANAGE_UNINSTALL)) {
					Utils.toOtherClass(mContext, ManageUninstallActivity.class);
				}
				if (bean.name.equals(Constants.MANAGE_HOME)) {
					Utils.toOtherClass(mContext, MainActivity.class);
				}
			}
		});
		return view;
	}

	private class Viewhold {
		private TextView name;
		private ImageView logo;
		private RelativeLayout item;
	}

}
