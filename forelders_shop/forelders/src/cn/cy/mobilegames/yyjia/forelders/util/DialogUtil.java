package cn.cy.mobilegames.yyjia.forelders.util;

import android.app.Activity;
import android.content.Context;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.util.view.CustomProgressDialog;
import cn.pedant.sweetalert.SweetAlertDialog;
import cn.pedant.sweetalert.SweetAlertDialog.OnSweetClickListener;

public class DialogUtil {
	private static CustomProgressDialog progressDialog;

	public static void startProgressDialog(Activity context) {
		try {
			if (progressDialog == null) {
				progressDialog = CustomProgressDialog.createDialog(context);
				progressDialog.setMessage("数据加载中...");
				if (!context.isFinishing()) {
					progressDialog.show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void stopProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	/**
	 * 自定义Dialog 自定义标题 消息 图标 左右按钮文字 确定监听事件
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param positiveButtonText
	 * @param negativeButtonText
	 */
	public static void showUserDefineDialog(Context context, String title,
			String message, int icon, String positiveButtonText,
			String negativeButtonText,
			OnSweetClickListener cancelClickListener,
			OnSweetClickListener confirmClickListener) {
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText(positiveButtonText)
				.setConfirmText(negativeButtonText).showCancelButton(true)
				.setCancelClickListener(cancelClickListener)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 自定义Dialog 自定义标题 消息 图标 左右按钮文字 确定监听事件
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param positiveButtonText
	 * @param negativeButtonText
	 */
	public static void showUserDefineDialog(Context context, int title,
			String message, int icon, int positiveButtonText,
			int negativeButtonText, OnSweetClickListener cancelClickListener,
			OnSweetClickListener confirmClickListener) {
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.WARNING_TYPE).setTitleText(title)
				.setContentText(message).setCancelText(positiveButtonText)
				.setConfirmText(negativeButtonText).showCancelButton(true)
				.setCancelClickListener(cancelClickListener)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 自定义Dialog 自定义标题 消息 图标 左右按钮文字 确定监听事件
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param positiveButtonText
	 * @param negativeButtonText
	 */
	public static void showUserDefineDialog(Context context, String title,
			String message, int icon, String positiveButtonText,
			String negativeButtonText, OnSweetClickListener confirmClickListener) {
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText(positiveButtonText)
				.setConfirmText(negativeButtonText).showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 自定义Dialog 自定义标题 消息 图标 左右按钮文字 确定监听事件
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param positiveButtonText
	 * @param negativeButtonText
	 */
	public static void showUserDefineDialog(Context context, String title,
			int resId, int icon, String positiveButtonText,
			String negativeButtonText, OnSweetClickListener confirmClickListener) {
		String message = context.getResources().getString(resId);
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText(positiveButtonText)
				.setConfirmText(negativeButtonText).showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 自定义Dialog 自定义标题 消息 图标 左右按钮文字 确定监听事件
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 * @param positiveButtonText
	 * @param negativeButtonText
	 */
	public static void showUserDefineDialog(Context context, int title,
			int resId, int icon, int rightResId, int leftResId,
			OnSweetClickListener confirmClickListener) {
		String message = context.getResources().getString(resId);
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText(leftResId)
				.setConfirmText(rightResId).showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 默认含有确定/取消按钮的Dialog 自定义标题 消息 图标
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 */
	public static void showDefaultDialog(Context context, String title,
			String message, int icon, OnSweetClickListener confirmClickListener) {
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText("    取消    ")
				.setConfirmText("    确定    ").showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	/**
	 * 默认含有确定/取消按钮的Dialog 自定义标题 消息
	 * 
	 * @param context
	 * @param title
	 * @param message
	 * @param icon
	 */
	public static void showDefaultDialog(Context context, String title,
			int resId, int icon, OnSweetClickListener confirmClickListener) {
		String message = context.getResources().getString(resId);
		if (message == null || message.trim().equals(Constants.KEY_NULL_STRING)) {
			return;
		}
		SweetAlertDialog alertDialog = new SweetAlertDialog(context,
				SweetAlertDialog.SUCCESS_TYPE).setTitleText(title)
				.setContentText(message).setCancelText("    取消    ")
				.setConfirmText("    确定    ").showCancelButton(true)
				.setCancelClickListener(null)
				.setConfirmClickListener(confirmClickListener);
		alertDialog.setCanceledOnTouchOutside(true);
		alertDialog.setCancelable(true);
		alertDialog.show();
	}
}
