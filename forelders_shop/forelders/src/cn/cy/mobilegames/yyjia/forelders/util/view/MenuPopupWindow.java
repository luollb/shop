package cn.cy.mobilegames.yyjia.forelders.util.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.cy.mobilegames.yyjia.forelders.adapter.MenuAdapter;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * 详情菜单
 * 
 * @author Administrator
 * 
 */
public class MenuPopupWindow extends PopupWindow {
	private ListView lv_menu;
	private View mMenuView;
	private MenuAdapter spinnerAdapter;

	@SuppressLint("InflateParams")
	public MenuPopupWindow(Activity context, MenuAdapter adapter) {
		super(context);
		this.spinnerAdapter = adapter;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.menu_listview, null);
		lv_menu = (ListView) mMenuView.findViewById(R.id.menu_listview);

		lv_menu.setAdapter(spinnerAdapter);
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体外面不可点击
		// this.setOutsideTouchable(false);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		// this.setAnimationStyle(R.style.);
		// // 实例化一个ColorDrawable颜色为半透明
		// ColorDrawable dw = new ColorDrawable(R.color.white);
		// // 设置SelectPicPopupWindow弹出窗体的背景
		// this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.menu_listview)
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
