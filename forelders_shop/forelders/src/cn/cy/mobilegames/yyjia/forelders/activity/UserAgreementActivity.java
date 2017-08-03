package cn.cy.mobilegames.yyjia.forelders.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.model.InfoAndContent;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 */
public class UserAgreementActivity extends BaseActivity implements
		ApiRequestListener {
	private ImageView ivBackBtn, ivMenu;
	// private ProgressBar mBar;
	private TextView tvTitle, tvNoData;
	private UserAgreementActivity activity;

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		activity = this;
		setContentView(R.layout.activity_user_agreement);
		initView();
		// mBar.setVisibility(View.VISIBLE);
		MarketAPI.getUserAgree(activity, activity);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 当引导界面最后一页 并按返回键 进入首页
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// 双击返回键 提示并退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Utils.finish(activity);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@SuppressLint("InflateParams")
	private void initView() {
		tvTitle = (TextView) findViewById(R.id.title);
		ivBackBtn = (ImageView) findViewById(R.id.back_btn);
		ivMenu = (ImageView) findViewById(R.id.menu_img);
		// mBar = (ProgressBar) findViewById(R.id.progressbar);
		tvNoData = (TextView) findViewById(R.id.no_data);
		tvNoData.setPadding(10, 10, 10, 10);
		tvNoData.setTextColor(getResources().getColor(R.color.gray_64));
		tvNoData.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		tvTitle.setText("用户协议");

		ivBackBtn.setOnClickListener(onClick);
		ivMenu.setVisibility(View.GONE);
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			default:
				break;
			}
		}
	};

	@Override
	public void onSuccess(int method, Object obj) {
		Utils.E(" onSuccess ACTION_GET_USER_AGREEMENT " + obj.toString());
		// mBar.setVisibility(View.GONE);
		tvNoData.setVisibility(View.VISIBLE);
		switch (method) {
		case MarketAPI.ACTION_GET_USER_AGREEMENT:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent infoAndContent = (InfoAndContent) obj;
				String content = infoAndContent.getContent();
				if (content != null && content.length() > 0) {
					JSONObject jsonObject;
					try {
						jsonObject = new JSONObject(content);
						String agreement = jsonObject.getString("agreement");
						if (agreement != null && agreement.length() > 0) {
							Spanned spanned = Html.fromHtml(agreement);
							tvNoData.setText(spanned);
						} else {
							tvNoData.setText("没有相关数据");
						}
					} catch (JSONException e) {
						e.printStackTrace();
						tvNoData.setText("没有相关数据");
					}
				}
			} else {
				tvNoData.setText("没有相关数据");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		Utils.E(" onError ACTION_GET_USER_AGREEMENT ");
		// mBar.setVisibility(View.GONE);
		tvNoData.setVisibility(View.VISIBLE);
		switch (method) {
		case MarketAPI.ACTION_GET_USER_AGREEMENT:
			tvNoData.setText("没有相关数据");
			break;
		default:
			break;
		}
	}

}
