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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.util.CharacterParser;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.ClearEditText;
import cn.cy.mobilegames.yyjia.forelders.util.view.SideBar;
import cn.cy.mobilegames.yyjia.forelders.util.view.SideBar.OnTouchingLetterChangedListener;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 管理--卸载
 * 
 * @author Administrator
 * 
 */
public class ManageUninstallActivity extends BaseActivity {
	private ImageView backBtn;
	private TextView title;
	private RelativeLayout searchLayout;
	private TextView noData;
	private ListView listview;
	private List<HashMap<String, Object>> localApks;
	private HashMap<String, Object> apk;
	private UnInstallReceiver unInstallReceiver;
	private String pkgName = "";
	private MyHandler handler;
	private UninstallAdapter adapter;
	private ManageUninstallActivity activity;
	private FrameLayout loadingView;
	private ProgressBar progressBar;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private SideBar sideBar;
	private TextView dialog;
	private ClearEditText mClearEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_manage_uninstall);
		characterParser = CharacterParser.getInstance();
		unInstallReceiver = new UnInstallReceiver();
		IntentFilter filter = new IntentFilter(
				"android.intent.action.PACKAGE_REMOVED");
		filter.addDataScheme("package");
		registerReceiver(unInstallReceiver, filter);
		localApks = new ArrayList<HashMap<String, Object>>();
		apk = new HashMap<String, Object>();
		handler = new MyHandler();
		initData();
		initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		loadingView = (FrameLayout) findViewById(R.id.loading);
		progressBar = (ProgressBar) loadingView.findViewById(R.id.progressbar);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		noData = (TextView) findViewById(R.id.no_data);
		backBtn.setOnClickListener(onClick);
		noData.setOnClickListener(onClick);
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getString(R.string.download_uninstall));
		title.setVisibility(View.VISIBLE);
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		searchLayout.setOnClickListener(onClick);
		listview = (ListView) findViewById(R.id.listview_soft_show);
		listview.setEmptyView(loadingView);
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listview.setSelection(position);
				}

			}
		});

		mClearEditText = (ClearEditText) findViewById(R.id.filter_edit);
		mClearEditText.clearFocus();
		// 根据输入框输入值的改变来过滤搜索
		mClearEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// 当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
				filterData(s.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			case R.id.no_data:
				initData();
				break;
			case R.id.menu_layout:
				Utils.toOtherClass(activity, SearchActivity.class);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<HashMap<String, Object>> mSortList = new ArrayList<HashMap<String, Object>>();

		if (TextUtils.isEmpty(filterStr)) {
			mSortList = localApks;
		} else {
			mSortList.clear();
			for (HashMap<String, Object> map : localApks) {
				String name = map.get(Constants.KEY_APP_NAME).toString();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					mSortList.add(map);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(mSortList, new PinyinComparator());
		adapter.updateListView(mSortList);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void initData() {
		new GetDataTask().execute();
	}

	private class UnInstallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
				String packageName = intent.getData().getSchemeSpecificPart();
				ToastUtil.showShortToast(context, Constants.UNINSTALL_SUCCESS);
				handler.sendMessage(handler.obtainMessage(0, 0, 0, packageName));
			}
		}
	}

	private @SuppressLint("HandlerLeak")
	class MyHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				if (pkgName.equals(msg.obj.toString())) {
					localApks.remove(apk);
					adapter.notifyDataSetChanged();
				}
				break;
			}
		}
	}

	class PinyinComparator implements Comparator<HashMap<String, Object>> {

		@Override
		public int compare(HashMap<String, Object> o1,
				HashMap<String, Object> o2) {
			if (o1.get(Constants.KEY_PIN_YIN).equals("@")
					|| o2.get(Constants.KEY_PIN_YIN).equals("#")) {
				return -1;
			} else if (o1.get(Constants.KEY_PIN_YIN).equals("#")
					|| o2.get(Constants.KEY_PIN_YIN).equals("@")) {
				return 1;
			} else {
				return o1.get(Constants.KEY_PIN_YIN).toString()
						.compareTo(o2.get(Constants.KEY_PIN_YIN).toString());
			}
		}
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, List<HashMap<String, Object>>> {
		@Override
		protected List<HashMap<String, Object>> doInBackground(Void... position) {
			
			localApks = Utils.getNoSystemApkInfo(activity);
			filledData(localApks);

			Collections.sort(localApks, new PinyinComparator());
			if (localApks.size() > 0) {
				handler.sendMessage(handler.obtainMessage(1, 0,
						localApks.size()));
				return localApks;
			} else {
				handler.sendMessage(handler.obtainMessage(1, 1, 0));
				return null;
			}
		}

		@Override
		protected void onPostExecute(final List<HashMap<String, Object>> result) {
			super.onPostExecute(result);
			adapter = new UninstallAdapter(result);
			listview.setAdapter(adapter);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(unInstallReceiver);
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	private List<HashMap<String, Object>> filledData(
			List<HashMap<String, Object>> date) {
		List<HashMap<String, Object>> mSortList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < date.size(); i++) {
			HashMap<String, Object> map = date.get(i);
			String appName = map.get(Constants.KEY_APP_NAME).toString();
			String pinyin = characterParser.getSelling(appName);
			String sortString = pinyin.substring(0, 1).toUpperCase();
			if (sortString.matches("[A-Z]")) {
				map.put(Constants.KEY_PIN_YIN, sortString);
			} else {
				map.put(Constants.KEY_PIN_YIN, "#");
			}
			mSortList.add(map);
		}
		return mSortList;
	}

	private class UninstallAdapter extends BaseAdapter {
		private List<HashMap<String, Object>> appInfos;

		private UninstallAdapter(List<HashMap<String, Object>> lists) {
			super();
			this.appInfos = lists;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder = null;
			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(activity).inflate(
						R.layout.manage_uninstall_item, null);
				holder = new ViewHolder();
				holder.appLength = (TextView) view
						.findViewById(R.id.status_text);
				holder.appLogo = (ImageView) view
						.findViewById(R.id.app_detail_img);
				holder.tvLetter = (TextView) view.findViewById(R.id.catalog);
				holder.appName = (TextView) view.findViewById(R.id.app_name);
				// holder.softType = (TextView) view
				// .findViewById(R.id.mobile_type_text);
				holder.versionTv = (TextView) view
						.findViewById(R.id.app_version);
				holder.deleteBtn = (Button) view.findViewById(R.id.delete_btn);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			holder.deleteBtn.setText(R.string.uninstall);
			if (appInfos != null) {
				HashMap<String, Object> appInfo = appInfos.get(position);
				try {
					holder.appName.setText(appInfo.get(Constants.KEY_APP_NAME)
							.toString());
					holder.versionTv.setText(Constants.VERSION_CODE_CN
							+ appInfo.get(Constants.KEY_VERSION_NAME)
									.toString());
					holder.appLength.setText(Constants.DOWNLENGTH_TITLE_CN
							+ Utils.getAppSizeByMb(Long.valueOf(appInfo.get(
									Constants.KEY_APP_SIZE).toString())));
					holder.appLogo.setImageDrawable((Drawable) (appInfo
							.get(Constants.KEY_APP_LOGO)));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				// 根据position获取分类的首字母的Char ascii值
				int section = getSectionForPosition(position);
				// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
				if (position == getPositionForSection(section)) {
					holder.tvLetter.setVisibility(View.VISIBLE);
					holder.tvLetter.setText(appInfo.get(Constants.KEY_PIN_YIN)
							.toString());
				} else {
					holder.tvLetter.setVisibility(View.GONE);
				}

				holder.deleteBtn.setOnClickListener(new MyOnClick(appInfo) {

					@Override
					void click(View v, HashMap<String, Object> appInfo) {
						switch (v.getId()) {
						case R.id.delete_btn:
							pkgName = appInfo.get(Constants.KEY_PACKAGE_NAME)
									.toString().trim();
							apk = appInfo;
							deleteApk(appInfo);
							break;
						default:
							break;
						}
					}
				});
			}
			return view;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public Object getItem(int position) {
			return appInfos.get(position);
		}

		@Override
		public int getCount() {
			return appInfos == null ? 0 : appInfos.size();
		}

		@Override
		public void notifyDataSetChanged() {
			super.notifyDataSetChanged();
		}

		private class ViewHolder {
			private ImageView appLogo;
			// ProgressBar loadProgressBar;
			private Button deleteBtn;
			private TextView appName, versionTv, appLength, tvLetter;
			// currentTv, percentTv, downStatus,softType,
			// RelativeLayout loadingLayout;
		}

		private abstract class MyOnClick implements OnClickListener {
			private HashMap<String, Object> appInfo;

			private MyOnClick(HashMap<String, Object> appInfo) {
				super();
				this.appInfo = appInfo;
			}

			@Override
			public void onClick(View v) {
				click(v, appInfo);
			}

			abstract void click(View v, HashMap<String, Object> appInfo);
		}

		private void deleteApk(HashMap<String, Object> appInfo) {
			Utils.uninstallApk(activity, appInfo
					.get(Constants.KEY_PACKAGE_NAME).toString());
		}

		/**
		 * 当ListView数据发生变化时,调用此方法来更新ListView
		 * 
		 * @param list
		 */
		public void updateListView(List<HashMap<String, Object>> list) {
			this.appInfos = list;
			notifyDataSetChanged();
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			return appInfos.get(position).get(Constants.KEY_PIN_YIN).toString()
					.charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		@SuppressLint("DefaultLocale")
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = appInfos.get(i).get(Constants.KEY_PIN_YIN)
						.toString();
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}
			return -1;
		}

		/**
		 * 提取英文的首字母，非英文字母用#代替。
		 * 
		 * @param str
		 * @return
		 */
		@SuppressLint("DefaultLocale")
		private String getAlpha(String str) {
			String sortStr = str.trim().substring(0, 1).toUpperCase();
			// 正则表达式，判断首字母是否是英文字母
			if (sortStr.matches("[A-Z]")) {
				return sortStr;
			} else {
				return "#";
			}
		}

	}

	@Override
	public void onSuccess(int method, Object obj) {

	}

	@Override
	public void onError(int method, int statusCode) {

	}
}
