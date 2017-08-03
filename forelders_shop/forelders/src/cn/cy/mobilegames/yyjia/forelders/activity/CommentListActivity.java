package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.CommentAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.CommentList;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.LoadingDrawable;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 评论列表
 */
public class CommentListActivity extends BaseActivity {
	private ImageView backBtn;
	private TextView titleTv, noComment;
	private RelativeLayout searchLayout;
	private ListView mList;
	private Button doCommentBtn;
	// Loading
	private FrameLayout mLoading;
	private ProgressBar mProgress;
	private TextView mNoData;

	private CommentAdapter commentAdapter;
	private CommentListActivity activity;

	private String appid = "";
	private String title = "";
	private boolean isSuccess;
	private final static int REQUEST_CODE = 0x10;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_commentlist);
		Bundle data = getIntent().getExtras();
		if (data != null) {
			appid = data.getString(Constants.KEY_APP_ID);
			title = data.getString(Constants.KEY_APP_NAME);
		}
		initView();
		MarketAPI.getCommentList(this, this, appid);
	}

	private void initView() {
		mList = (ListView) findViewById(R.id.comment_listview);
		mLoading = (FrameLayout) findViewById(R.id.loading);
		mProgress = (ProgressBar) mLoading.findViewById(R.id.progressbar);
		mProgress.setIndeterminateDrawable(new LoadingDrawable(this));
		mProgress.setVisibility(View.VISIBLE);
		mNoData = (TextView) mLoading.findViewById(R.id.no_data);
		mNoData.setOnClickListener(onClick);
		mList.setEmptyView(mLoading);

		backBtn = (ImageView) findViewById(R.id.back_btn);
		titleTv = (TextView) findViewById(R.id.title);
		titleTv.setText(Utils.checkEmpty(title));
		titleTv.setVisibility(View.VISIBLE);
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		doCommentBtn = (Button) findViewById(R.id.do_comment_btn);

		searchLayout.setOnClickListener(onClick);
		backBtn.setOnClickListener(onClick);
		doCommentBtn.setOnClickListener(onClick);

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Intent intent = new Intent();
				intent.putExtra(Constants.REQUEST_KEY_AC, isSuccess);
				setResult(Activity.RESULT_OK, intent);
				finish();// 结束之后会将结果传回From
				break;
			case R.id.menu_layout:
				Utils.toOtherClass(activity, SearchActivity.class);
				break;
			case R.id.do_comment_btn:
				if (appid != null) {
					startActivityForResult(new Intent(activity,
							CommentActivity.class).putExtra(
							Constants.REQUEST_KEY_APPID, appid), REQUEST_CODE);
				}
				break;
			case R.id.no_data:
				// 重试
				mNoData.setVisibility(View.GONE);
				mProgress.setVisibility(View.VISIBLE);
				MarketAPI.getCommentList(activity, activity, appid);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK)
				isSuccess = data.getBooleanExtra(Constants.REQUEST_KEY_AC,
						false);
			if (isSuccess) {
				MarketAPI.getCommentList(activity, activity, appid);
			}
			break;
		}
	};

	@Override
	public void onDestroy() {
		Intent intent = new Intent();
		intent.putExtra(Constants.REQUEST_KEY_AC, isSuccess);
		setResult(Activity.RESULT_OK, intent);
		finish();
		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_COMMENT_LIST:
			if (obj != null & obj instanceof List<?>) {
				@SuppressWarnings("unchecked")
				List<CommentList> comLists = (List<CommentList>) obj;
				if (comLists != null && comLists.size() > 0) {
					commentAdapter = new CommentAdapter(comLists, context,
							true, 0);
					mList.setAdapter(commentAdapter);
				} else {
					ToastUtil.showShortToast(activity, R.string.no_comment_data);
				}
			}
			break;

		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {
		if (statusCode == ApiAsyncTask.BUSSINESS_ERROR) {
			// 没有数据
			mNoData.setVisibility(View.GONE);
			mProgress.setVisibility(View.GONE);
			ToastUtil.showShortToast(activity, R.string.no_comment_data);
		} else {
			// 超时
			mNoData.setVisibility(View.VISIBLE);
			mProgress.setVisibility(View.GONE);
		}
	}
}
