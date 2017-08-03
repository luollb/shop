package cn.cy.mobilegames.yyjia.forelders.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.Session;
import cn.cy.mobilegames.yyjia.forelders.model.InfoAndContent;
import cn.cy.mobilegames.yyjia.forelders.model.UserInfoEntity;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.ClearEditText;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 登录页面
 */
public class LoginActivity extends BaseActivity {
	private ImageView backBtn, searchBtn, qqBtn, wbBtn;
	// private EditText accountEt, pwdEt;
	private Button loginBtn;
	private TextView navTitle, forgetTv, registerTv;
	private LoginActivity activity;
	private ClearEditText userName, passWord;
	private String name, password;
	private boolean isPause;
	private ProgressDialog loginDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = LoginActivity.this;
		setContentView(R.layout.activity_login);
		initView();
	}

	@Override
	public void onResume() {
		isPause = false;
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		isPause = true;
		super.onPause();
		MobclickAgent.onPause(this);
	}

	private void initView() {
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setText(Constants.SIGN_IN);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		searchBtn = (ImageView) findViewById(R.id.menu_img);
		qqBtn = (ImageView) findViewById(R.id.login_qq);
		wbBtn = (ImageView) findViewById(R.id.login_wb);
		loginBtn = (Button) findViewById(R.id.login_btn);
		userName = (ClearEditText) findViewById(R.id.username);
		passWord = (ClearEditText) findViewById(R.id.password);

		forgetTv = (TextView) findViewById(R.id.forget_pwd_btn);
		registerTv = (TextView) findViewById(R.id.register_tv);
		backBtn.setOnClickListener(onClick);
		searchBtn.setOnClickListener(onClick);
		qqBtn.setOnClickListener(onClick);
		wbBtn.setOnClickListener(onClick);
		forgetTv.setOnClickListener(onClick);
		registerTv.setOnClickListener(onClick);
		loginBtn.setOnClickListener(onClick);

	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			case R.id.forget_pwd_btn:
				if (!isPause) {
					ToastUtil.showShortToast(activity,
							Constants.FORGET_PASSWORD);
				}
				break;
			case R.id.register_tv:
				Utils.toOtherClass(activity, RegisterActivity.class);
				Utils.finish(activity);
				break;
			case R.id.menu_img:
				Utils.toOtherClass(activity, SearchActivity.class);
				break;
			case R.id.login_qq:
				if (!isPause) {
					ToastUtil.showShortToast(activity, Constants.QQ_LOGIN);
				}
				break;
			case R.id.login_wb:
				if (!isPause) {
					ToastUtil.showShortToast(activity, Constants.WB_LOGIN);
				}
				break;
			case R.id.login_btn:
				doLogin();
				break;
			default:
				break;
			}
		}
	};

	private void doLogin() {
		name = userName.getText().toString();
		password = passWord.getText().toString();
		if (TextUtils.isEmpty(name)) {
			userName.setShakeAnimation();
			if (!isPause) {
				ToastUtil.showShortToast(activity,
						Constants.PLEASE_ENTER_USERNAME);
			}
			return;
		}
		if (TextUtils.isEmpty(password)) {
			passWord.setShakeAnimation();
			if (!isPause) {
				ToastUtil.showShortToast(activity,
						Constants.PLEASE_ENTER_PASSWORD);
			}
			return;
		}

		initLoginDialog();
		loginDialog.show();
		MarketAPI.doLogin(activity, activity, name, password);
	}

	public void initLoginDialog() {
		loginDialog = new ProgressDialog(activity);
		loginDialog.setMessage("请求登录中...");
		loginDialog.setCanceledOnTouchOutside(false);
		loginDialog.setCancelable(false);
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
		case MarketAPI.ACTION_DO_LOGIN:
			loginDialog.dismiss();
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent info = (InfoAndContent) obj;
				if (info.status == 0) {
					ToastUtil.showShortToast(activity, info.info);
					return;
				}
				if (info.status == 1) {
					UserInfoEntity entity = JsonUtil.toObject(info.content,
							UserInfoEntity.class);
					saveLoginData(name, password, entity);
				}
			} else {
				ToastUtil.showShortToast(activity,
						Constants.LOGIN_FAILURE_AND_RETRY);
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
		case MarketAPI.ACTION_DO_LOGIN:
			loginDialog.dismiss();
			ToastUtil.showShortToast(activity,
					Constants.LOGIN_FAILURE_AND_RETRY);
			break;
		default:
			break;
		}
	}

	private void saveLoginData(final String name, final String password,
			UserInfoEntity myinfo) {
		Session session = Session.get(activity);
		session.setLogin(true);
		session.setUserName(name);
		session.setUserId(myinfo.uid);
		session.setPassword(password);
		session.setUserLogo(myinfo.logo);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				Bundle data = new Bundle();
				data.putBoolean(Constants.REQUEST_KEY_LOGIN_SUCCESS, true);
				if (!isPause) {
					ToastUtil.showShortToast(activity, Constants.LOGIN_SUCCESS);
				}
				Utils.finish(activity);
			}
		}, 1000);
	}

}
