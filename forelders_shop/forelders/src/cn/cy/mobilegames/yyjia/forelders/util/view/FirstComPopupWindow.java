package cn.cy.mobilegames.yyjia.forelders.util.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * PopWindow
 * 
 * @author Administrator
 * 
 */
public class FirstComPopupWindow extends PopupWindow {
	private ImageView iv_close;
	private ListView lv_first_com;
	private Button btn_one_key, btn_no_alert;
	private View mMenuView;
	private BaseAdapter adapter;

	@SuppressLint("InflateParams")
	public FirstComPopupWindow(FragmentActivity context,
			OnClickListener onClick, BaseAdapter adapters) {
		super(context);
		this.adapter = adapters;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.activity_first_recom, null);
		btn_one_key = (Button) mMenuView
				.findViewById(R.id.first_com_one_btn_install_btn);
		iv_close = (ImageView) mMenuView.findViewById(R.id.first_com_close_iv);
		btn_no_alert = (Button) mMenuView
				.findViewById(R.id.first_com_no_alert_btn);
		lv_first_com = (ListView) mMenuView
				.findViewById(R.id.first_com_listview);

		// // 取消按钮
		// iv_close.setOnClickListener(new OnClickListener() {
		//
		// public void onClick(View v) {
		// // 销毁弹出框
		// dismiss();
		// }
		// });
		// 设置按钮监听

		iv_close.setOnClickListener(onClick);
		btn_no_alert.setOnClickListener(onClick);
		btn_one_key.setOnClickListener(onClick);
		lv_first_com.setAdapter(adapter);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		// this.setWidth(BaseTools.getWindowsWidth(context) * 4 / 5);
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(BaseTools.getWindowsHeight(context) * 3 / 5);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.);
		// 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(0xb0000000);
		// // 设置SelectPicPopupWindow弹出窗体的背景
		// this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.activity_firset_recom)
						.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});

	}

}
