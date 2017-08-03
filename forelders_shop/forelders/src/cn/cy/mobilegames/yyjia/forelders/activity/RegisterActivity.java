package cn.cy.mobilegames.yyjia.forelders.activity;

import android.graphics.Paint;
import android.os.Bundle;
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
 * 注册页面
 * 
 * @author Administrator
 * 
 */
public class RegisterActivity extends BaseActivity {
	private ImageView backBtn, searchBtn;
	private Button registerBtn;
	private TextView navTitle, loginNow, userAgreement;
	private RegisterActivity activity;
	private ClearEditText userName, email, passWord, pwdRepeat;
	private String username, pwd, mail, repwd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		setContentView(R.layout.activity_register);
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
		userAgreement = (TextView) findViewById(R.id.agree_register);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setText(Constants.REGISTER);
		loginNow = (TextView) findViewById(R.id.login_btn);
		backBtn = (ImageView) findViewById(R.id.back_btn);
		searchBtn = (ImageView) findViewById(R.id.menu_img);
		userName = (ClearEditText) findViewById(R.id.username);
		email = (ClearEditText) findViewById(R.id.email);
		passWord = (ClearEditText) findViewById(R.id.password);
		pwdRepeat = (ClearEditText) findViewById(R.id.pwd_repeat);
		registerBtn = (Button) findViewById(R.id.register_btn);

		backBtn.setOnClickListener(onClick);
		searchBtn.setOnClickListener(onClick);
		registerBtn.setOnClickListener(onClick);
		loginNow.setOnClickListener(onClick);
		userAgreement.setOnClickListener(onClick);
		userAgreement.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		userAgreement.setText("注册即同意《" + Constants.HOST_NAME + "》用户协议");

		// userName.setOnFocusChangeListener(onFocus);
		// email.setOnFocusChangeListener(onFocus);
		// passWord.setOnFocusChangeListener(onFocus);
		// pwdRepeat.setOnFocusChangeListener(onFocus);
	}

	private OnClickListener onClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.agree_register:
				Utils.toOtherClass(activity, UserAgreementActivity.class);
				break;
			case R.id.back_btn:
				Utils.finish(activity);
				break;
			case R.id.menu_img:
				Utils.toOtherClass(activity, SearchActivity.class);
				break;
			case R.id.register_btn:
				doRegister();
				break;
			case R.id.login_btn:
				Utils.toOtherClass(activity, LoginActivity.class);
				break;
			default:
				break;
			}
		}
	};

	// private OnFocusChangeListener onFocus = new OnFocusChangeListener() {
	//
	// @Override
	// public void onFocusChange(View v, boolean focusable) {
	// if (!focusable) {
	// switch (v.getId()) {
	// case R.id.username:
	// if (TextUtils.isEmpty(userName.getText())) {
	// userName.setShakeAnimation();
	// ToastUtil.showShortToast(activity,
	// Constants.PLEASE_ENTER_USERNAME);
	// return;
	// }
	// break;
	// case R.id.email:
	// if (TextUtils.isEmpty(email.getText())) {
	// ToastUtil.showShortToast(activity,
	// Constants.PLEASE_ENTER_EMAIL);
	// email.setShakeAnimation();
	// }
	// if (!TextUtils.isEmpty(email.getText())
	// && !Utils.isEmail(email.getText().toString())) {
	// ToastUtil.showShortToast(activity,
	// Constants.EMAIL_ERROR_FORMAT);
	// email.setShakeAnimation();
	// }
	// break;
	// case R.id.password:
	// if (TextUtils.isEmpty(passWord.getText())) {
	// ToastUtil.showShortToast(activity,
	// Constants.PLEASE_ENTER_PASSWORD);
	// passWord.setShakeAnimation();
	// }
	// break;
	// case R.id.pwd_repeat:
	// if (TextUtils.isEmpty(pwdRepeat.getText())) {
	// ToastUtil.showShortToast(activity,
	// Constants.PLEASE_REPEAT_PASSWORD);
	// pwdRepeat.setShakeAnimation();
	// }
	// if (!pwdRepeat.getText().toString()
	// .equals(pwdRepeat.getText().toString())) {
	// ToastUtil.showShortToast(activity,
	// Constants.PASSWORD_NOT_UNIFORM);
	// pwdRepeat.setShakeAnimation();
	// }
	// break;
	// default:
	// break;
	// }
	// }
	//
	// }
	// };

	private void doRegister() {
		username = userName.getText().toString();
		pwd = passWord.getText().toString();
		mail = email.getText().toString();
		repwd = pwdRepeat.getText().toString();
		userName.clearFocus();
		email.clearFocus();
		pwdRepeat.clearFocus();
		passWord.clearFocus();

		if (TextUtils.isEmpty(username)) {
			ToastUtil.showShortToast(activity, Constants.PLEASE_ENTER_USERNAME);
			userName.setShakeAnimation();
			return;
		}
		if (TextUtils.isEmpty(mail)) {
			ToastUtil.showShortToast(activity, Constants.PLEASE_ENTER_EMAIL);
			email.setShakeAnimation();
			return;
		}
		if (!TextUtils.isEmpty(mail) && !Utils.isEmail(mail)) {
			ToastUtil.showShortToast(activity, Constants.EMAIL_ERROR_FORMAT);
			email.setShakeAnimation();
			return;
		}
		if (TextUtils.isEmpty(pwd)) {
			ToastUtil.showShortToast(activity, Constants.PLEASE_ENTER_PASSWORD);
			passWord.setShakeAnimation();
			return;
		}
		if (TextUtils.isEmpty(repwd)) {
			ToastUtil
					.showShortToast(activity, Constants.PLEASE_REPEAT_PASSWORD);
			pwdRepeat.setShakeAnimation();
			return;
		}
		if (!pwd.equals(pwd)) {
			ToastUtil.showShortToast(activity, Constants.PASSWORD_NOT_UNIFORM);
			passWord.setShakeAnimation();
			pwdRepeat.setShakeAnimation();
			return;
		}

		MarketAPI.doRegister(activity, activity, username, pwd, repwd, mail);
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void saveLoginData(final String name, final String password,
			UserInfoEntity myinfo) {
		Session session = Session.get(activity);
		session.setUserId(myinfo.uid);
		session.setUserLogo(myinfo.logo);
		session.setUserName(name);
		session.setNickName(myinfo.username);
		session.setPassword(password);
		session.setLogin(true);

		if (!isFinishing()) {
			ToastUtil.showShortToast(activity, Constants.LOGIN_SUCCESS);
		}
		Utils.finish(activity);
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_DO_REGISTER:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent info = (InfoAndContent) obj;
				if (info.status == 0) {
					ToastUtil.showShortToast(activity, info.info);
					return;
				}
				if (info.status == 1) {
					Session session = Session.get(activity);
					session.setUserName(username);
					session.setPassword(pwd);
					session.setuserEmail(mail);

					ToastUtil.showShortToast(activity, Constants.REQUEST_LOGIN);
					MarketAPI.doLogin(activity, activity, username, pwd);
				}
			} else {
				ToastUtil.showShortToast(activity, Constants.REGISTER_FAILURE);
			}

			break;
		case MarketAPI.ACTION_DO_LOGIN:
			if (obj != null && obj instanceof InfoAndContent) {
				InfoAndContent info = (InfoAndContent) obj;
				if (info.status == 0) {
					ToastUtil.showShortToast(activity, info.content);
					return;
				}
				if (info.status == 1) {
					UserInfoEntity entity = JsonUtil.toObject(info.content,
							UserInfoEntity.class);
					saveLoginData(username, pwd, entity);
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

	}

}
