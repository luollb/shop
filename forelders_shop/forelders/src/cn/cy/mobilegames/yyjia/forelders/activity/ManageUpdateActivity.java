package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager;
import cn.cy.mobilegames.yyjia.forelders.download.DownloadManager.Request;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadListActivity;
import cn.cy.mobilegames.yyjia.forelders.model.UpgradeInfo;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 管理--升级
 * 
 * @author Administrator
 * 
 */
public class ManageUpdateActivity extends BaseActivity {
	private ImageView backBtn;
	private TextView title;
	private RelativeLayout searchLayout;
	private TextView noNeedUpdate;
	private ListView listview;
	private ManageUpdateActivity activity;
	private HashMap<String, UpgradeInfo> data = new HashMap<String, UpgradeInfo>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		data = mSession.getUpdateList();
		setContentView(R.layout.activity_manage_update);
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
		backBtn = (ImageView) findViewById(R.id.back_btn);
		backBtn.setOnClickListener(onClick);
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getString(R.string.download_update));
		title.setVisibility(View.VISIBLE);
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		searchLayout.setOnClickListener(onClick);
		noNeedUpdate = (TextView) findViewById(R.id.no_need_update);
		listview = (ListView) findViewById(R.id.listview_soft_show);
		if (data != null && data.size() > 0) {
			listview.setVisibility(View.VISIBLE);
			noNeedUpdate.setVisibility(View.GONE);
			listview.setAdapter(new UpgradeAdapter(data, activity, imageLoader));
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent,
						View convertView, int position, long id) {

				}
			});
		} else {
			listview.setVisibility(View.GONE);
			noNeedUpdate.setVisibility(View.VISIBLE);
		}

	}

	class UpgradeAdapter extends BaseAdapter {
		private HashMap<String, UpgradeInfo> maps;
		private Context context;
		private ImageLoaderUtil loader;

		public UpgradeAdapter(HashMap<String, UpgradeInfo> datas,
				Context context, ImageLoaderUtil loaderUtil) {
			super();
			this.maps = datas;
			this.context = context;
			this.loader = loaderUtil;
		}

		@Override
		public int getCount() {
			return maps == null ? 0 : maps.size();
		}

		@Override
		public Object getItem(int position) {
			return maps.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHold hold = null;
			View view = convertView;
			if (convertView == null) {
				view = LayoutInflater.from(context).inflate(
						R.layout.soft_item_view, null);
				hold = new ViewHold();
				hold.logo = (ImageView) view.findViewById(R.id.soft_logo);
				hold.name = (TextView) view.findViewById(R.id.soft_name);
				hold.size = (TextView) view.findViewById(R.id.soft_summary);
				hold.updateBtn = (TextView) view
						.findViewById(R.id.soft_downbtn);
				hold.upgradeInfo = (TextView) view
						.findViewById(R.id.soft_brief);
				hold.upgradeInfo.setVisibility(View.VISIBLE);
				hold.itemView = (LinearLayout) view
						.findViewById(R.id.soft_item_layout);
				hold.version = (TextView) view.findViewById(R.id.soft_count);
				view.setTag(hold);
			} else {
				hold = (ViewHold) view.getTag();
			}

			if (maps != null && maps.size() > 0) {
				final UpgradeInfo info = maps.values().iterator().next();
				loader.setImageNetResource(hold.logo, info.logo,
						R.drawable.img_default);
				hold.name.setText(info.name);
				hold.size.setText(Constants.DOWNLENGTH_TITLE_CN + info.size);
				hold.updateBtn.setText("更新");
				hold.upgradeInfo.setText(info.updateinfo);
				hold.version.setText(Constants.VERSION_CODE_CN + info.version);
				hold.itemView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						doDownload(info);
					}
				});
			}

			return view;
		}
	}

	class ViewHold {
		private TextView name, upgradeInfo, size, version, updateBtn;
		private ImageView logo;
		private LinearLayout itemView;

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			case R.id.menu_layout:
				Utils.toOtherClass(activity, SearchActivity.class);
				break;
			case R.id.no_need_update:
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	protected void doDownload(UpgradeInfo info) {

		if (info != null && info.appdownurl != null) {
			Uri srcUri = Uri.parse(Utils.checkUrlContainHttp(
					Constants.URL_BASE_HOST, info.appdownurl));
			if (mDownloadManager.queryExists(srcUri.toString())) {
				Utils.toOtherClass(ManageUpdateActivity.this,
						DownloadListActivity.class);
				return;
			} else {
				Utils.deleteFile(mDownloadManager
						.queryLocalUriByAppid(info.appid));
				DownloadManager.Request request = new Request(srcUri);
				request.setDestinationInExternalPublicDir(Constants.ROOT_DIR,
						info.sourceurl);
				request.setTitle(info.name);
				request.setShowRunningNotification(true);
				request.setVisibleInDownloadsUi(true);
				request.setAppId(info.appid);
				request.setLogo(info.logo);
				request.setPackageName(info.sourceurl);
				downloadId = mDownloadManager.enqueue(request);
				ToastUtil.showShortToast(activity, R.string.download_start);
			}
		}
		Utils.toOtherClass(ManageUpdateActivity.this,
				DownloadListActivity.class);
	}

	@Override
	public void onSuccess(int method, Object obj) {

	}

	@Override
	public void onError(int method, int statusCode) {

	}
}
