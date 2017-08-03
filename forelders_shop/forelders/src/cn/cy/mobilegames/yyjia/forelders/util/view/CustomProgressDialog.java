package cn.cy.mobilegames.yyjia.forelders.util.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.R;

public class CustomProgressDialog extends Dialog {
	private Context context = null;
	private static CustomProgressDialog customProgressDialog = null;
	private ProgressBar progressBar;

	private CustomProgressDialog(Context context) {
		super(context);
		this.context = context;
	}

	private CustomProgressDialog(Context context, int theme) {
		super(context, theme);
	}

	public static CustomProgressDialog getInstance(Context context) {
		if (customProgressDialog == null) {
			customProgressDialog = new CustomProgressDialog(context,
					R.style.CustomProgressDialog);

		}
		return customProgressDialog;
	}

	public static CustomProgressDialog createDialog(Context context) {
		customProgressDialog = getInstance(context);
		
		customProgressDialog.setContentView(R.layout.progressdialog);
		Window window = customProgressDialog.getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.CENTER;
		params.dimAmount = 0f;
		window.setAttributes(params);
		return customProgressDialog;
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (customProgressDialog == null) {
			return;
		}
		if (!hasFocus) {
			dismiss();
		}
		progressBar = (ProgressBar) customProgressDialog
				.findViewById(R.id.loadingProgreeBar);

	}

	public void setProgressBarIndeterminateDrawable(int resId) {
		progressBar.setIndeterminateDrawable(context.getResources()
				.getDrawable(resId));
	}

	/**
	 * 
	 * [Summary] setTitile 设置标题
	 * 
	 * @param strTitle
	 * @return
	 * 
	 */
	public CustomProgressDialog setTitile(String strTitle) {
		return customProgressDialog;
	}

	/**
	 * 
	 * [Summary] setMessage 设置消息
	 * 
	 * @param strMessage
	 * @return
	 * 
	 */
	public CustomProgressDialog setMessage(String strMessage) {
		TextView tvMsg = (TextView) customProgressDialog
				.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
		return customProgressDialog;
	}
}
