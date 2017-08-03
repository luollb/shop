package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.activity.SoftDetailActivity;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

public class BannerAdapter extends PagerAdapter {
	private ArrayList<ImageView> views;
	private ViewPager viewPager;
	private ArrayList<HashMap<String, Object>> result;
	private Context mContext;

	public BannerAdapter(ArrayList<ImageView> lists, ViewPager pager,
			ArrayList<HashMap<String, Object>> dataList, Context context) {
		super();
		this.views = lists;
		this.viewPager = pager;
		this.result = dataList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
		Utils.D("getcount " + ((views == null) ? 0 : views.size()));
		return (views == null) ? 0 : views.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object obj) {
		return view == obj;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		viewPager = (ViewPager) container;
		if (views.get(position).getParent() == null) {
			viewPager.removeView(views.get(position));
		}
	}

	@Override
	public Object instantiateItem(View container, final int position) {
		viewPager = (ViewPager) container;
		if (views.get(position).getParent() == null) {
			viewPager.addView(views.get(position));
			views.get(position).setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					try {
						Bundle data = new Bundle();
						data.putString(
								Constants.KEY_APP_ID,
								result.get(position)
										.get(Constants.KEY_PRODUCT_ID)
										.toString());
						data.putString(
								Constants.KEY_APP_NAME,
								result.get(position)
										.get(Constants.KEY_PRODUCT_NAME)
										.toString());
						Utils.toOtherClass(mContext, SoftDetailActivity.class,
								data);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		Utils.D(" instantiateItem  " + views.get(position));
		return views.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
