package cn.cy.mobilegames.yyjia.forelders.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 评论
 */
public class CommentActivity extends BaseActivity {
	private ImageView backBtn;
	private TextView title
	// , commentStatus
	;
	private RelativeLayout searchLayout;
	private EditText commentEt;
	private RatingBar ratingBar;
	private Button submitBtn;
	private String appid = "";
	private String imei = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		initView();
		try {
			appid = getIntent().getStringExtra(Constants.REQUEST_KEY_APPID);
		} catch (Exception e) {
			e.printStackTrace();
		}

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
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	private void initView() {
		backBtn = (ImageView) findViewById(R.id.back_btn);
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getString(R.string.do_comment));
		title.setVisibility(View.VISIBLE);
		searchLayout = (RelativeLayout) findViewById(R.id.menu_layout);
		searchLayout.setVisibility(View.VISIBLE);
		commentEt = (EditText) findViewById(R.id.comment_et);
		submitBtn = (Button) findViewById(R.id.submit_comment);

		ratingBar = (RatingBar) findViewById(R.id.comment_rating_bar);
		// commentStatus = (TextView) findViewById(R.id.comment_status);

		searchLayout.setOnClickListener(onClick);
		backBtn.setOnClickListener(onClick);
		commentEt.setOnClickListener(onClick);
		submitBtn.setOnClickListener(onClick);
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				finish();
				break;
			case R.id.menu_layout:
				Utils.toOtherClass(CommentActivity.this, SearchActivity.class);
				break;
			case R.id.comment_et:
				break;
			case R.id.submit_comment:
				doComment();
				break;

			default:
				break;
			}
		}
	};

	private void doComment() {
		String commentTv = commentEt.getText().toString();
		if (!TextUtils.isEmpty(commentTv.trim())) {
			submitComment(commentTv);
			commentEt.clearFocus();
			commentEt.setText("");
			Utils.HideOrToggleSoftInput(context);
		} else {
			ToastUtil
					.showShortToast(context, Constants.COMMENT_EMPTY_PLEASE_COMMENT);
		}
	}

	private void submitComment(String msg) {
		if (!TextUtils.isEmpty(msg)) {
			MarketAPI.doComment(CommentActivity.this, this, Session
					.get(context).isLogin(), appid, msg, (TextUtils
					.isEmpty(imei.trim()) ? "" : imei),
					ratingBar.getProgress() * 10);
		} else {
			ToastUtil
					.showShortToast(context, Constants.COMMENT_EMPTY_PLEASE_COMMENT);
			return;
		}
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
		switch (method) {
		case MarketAPI.ACTION_DO_COMMENT:
			ToastUtil.showShortToast(context, "您的评论已提交,等待审核通过.");
			Intent intent = new Intent();
			intent.putExtra(Constants.REQUEST_KEY_AC, true);
			setResult(Activity.RESULT_OK, intent);
			finish();// 结束之后会将结果传回From
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		Intent intent = new Intent();
		intent.putExtra(Constants.REQUEST_KEY_AC, false);
		setResult(Activity.RESULT_OK, intent);
		ToastUtil.showShortToast(context, Constants.COMMENT_FAILURE);
		finish();// 结束之后会将结果传回From
	}

}
