package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.model.MenuBean;
import cn.cy.mobilegames.yyjia.forelders.model.Subject;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.MenuPopupWindow;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 精品-->专题(详情页)
 */
public class QualitySubjectContentActivity extends BaseActivity {
	private ListView listview;
	private ImageView backBtn, advertImg;
	private TextView subTitle, subName, subFocus, subSummary;
	private RelativeLayout searchImgLayout;
	private QualitySubjectContentActivity activity;
	private Subject sub;
	private AppListAdapter mAdapter;
	private MenuAdapter menuAdapter;
	private MenuPopupWindow menuPopupWindow;
	private LinearLayout backView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		initData();
		setContentView(R.layout.subject_content);
		sub = (Subject) getIntent().getSerializableExtra(
				Constants.REQUEST_VALUE_KHD_SUBJECT);
		initView();
		doInitListAdapter();
		if (sub != null && !TextUtils.isEmpty(sub.id)) {
			MarketAPI.getSubjectDetail(activity, activity, sub.id);
		} else {
			ToastUtil.showShortToast(activity, R.string.no_data);
		}
	}

	@Override
	public void onDestroy() {
		Intent intent = new Intent();
		intent.putExtra(Constants.REQUEST_KEY_AC, true);
		setResult(Activity.RESULT_OK, intent);
		finish();
		super.onDestroy();
	};

	private void initView() {
		backView = (LinearLayout) findViewById(R.id.back_nav_view);
		searchImgLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchImgLayout.setVisibility(View.VISIBLE);
		searchImgLayout.setOnClickListener(onClick);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		backBtn.setOnClickListener(onClick);
		advertImg = (ImageView) findViewById(R.id.subject_advert);
		advertImg.setBackgroundColor(getResources().getColor(R.color.gray_6e));
		imageLoader.setImageNetResource(advertImg, sub.subjectpic,
				R.drawable.img_default);
		subTitle = (TextView) findViewById(R.id.title);
		subTitle.setVisibility(View.VISIBLE);
		subName = (TextView) findViewById(R.id.subject_title);
		subFocus = (TextView) findViewById(R.id.subject_focus);
		subSummary = (TextView) findViewById(R.id.sub_summary);
		subTitle.setText(sub.subjectname);
		subName.setText(sub.subjectname);
		try {
			subFocus.setText(Constants.FOCUS_CN
					+ (Integer.parseInt(sub.viewnum) + 1)
					+ Constants.RIGHT_BRACKET);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		subSummary.setText(sub.summary);
		listview = (ListView) findViewById(R.id.sub_listview);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				// 去产品详细页
				if (mAdapter.getItem(position) != null) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> item = (HashMap<String, Object>) mAdapter.getItem(listview
							.getHeaderViewsCount() > 0 ? position - 1
							: position);
					Bundle data = new Bundle();
					data.putString(Constants.KEY_APP_ID,
							item.get(Constants.KEY_PRODUCT_ID).toString());
					data.putString(Constants.KEY_APP_NAME,
							item.get(Constants.KEY_PRODUCT_NAME).toString());
					Utils.toOtherClass(activity, SoftDetailActivity.class, data);
				}
			}
		});
	}

	private void initData() {
		List<MenuBean> mList = new ArrayList<MenuBean>();
		for (int i = 0; i < Constants.menuLogos.length; i++) {
			mList.add(new MenuBean(Constants.menuNames[i],
					Constants.menuLogos[i]));
		}
		menuAdapter = new MenuAdapter(mList, activity);
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.menu_layout:
				menuPopupWindow = new MenuPopupWindow(activity, menuAdapter);
				menuPopupWindow
						.setWidth(BaseTools.getWindowsWidth(activity) * 2 / 5);
				int[] location = new int[2];
				backView.getLocationOnScreen(location);
				menuPopupWindow.showAtLocation(backView, Gravity.NO_GRAVITY,
						location[0] + backView.getWidth(), location[1]
								+ backView.getHeight());
				break;
			case R.id.back_btn:
				finish();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onStart() {
		super.onStart();
	}

	private void doSearch() {
		startActivity(new Intent(QualitySubjectContentActivity.this,
				SearchActivity.class));
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		if (menuPopupWindow != null) {
			menuPopupWindow.dismiss();
		}
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	public AppListAdapter doInitListAdapter() {
		mAdapter = new AppListAdapter(activity, listview, null,
				R.layout.soft_item_view, new String[] {
						Constants.KEY_PRODUCT_ICON_URL,
						Constants.KEY_PRODUCT_NAME,
						Constants.KEY_PRODUCT_DOWNLOAD_COUNT,
						Constants.KEY_PRODUCT_RATING_COUNT,
						Constants.KEY_PRODUCT_DOWNLOAD }, new int[] {
						R.id.soft_logo, R.id.soft_name, R.id.soft_count,
						R.id.soft_rating_bar, R.id.soft_downbtn });
		mAdapter.setProductList();
		// if (isRank) {
		// // 排行榜列表
		// mAdapter.setRankList();
		// }
		return mAdapter;
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_SUBJECT_DETAIL:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				ArrayList<HashMap<String, Object>> data = (ArrayList<HashMap<String, Object>>) ApiResponseFactory
						.parseAppList(activity, result);
				mAdapter.addData(data);
				listview.setAdapter(mAdapter);
				Utils.setListViewHeightBasedOnChildren(listview);
			} else {
				ToastUtil.showShortToast(activity, R.string.no_more_data);
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
