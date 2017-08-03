package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.ArrayList;
import java.util.List;

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
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;

public class AdvertPagerAdapter extends PagerAdapter {
	private ArrayList<ImageView> views;
	private ViewPager viewPager;
	private List<Softinfo> softwareLists;
	private Context mContext;

	public AdvertPagerAdapter(ArrayList<ImageView> lists, ViewPager pager,
			List<Softinfo> softList, Context context) {
		super();
		this.views = lists;
		this.viewPager = pager;
		this.softwareLists = softList;
		this.mContext = context;
	}

	@Override
	public int getCount() {
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
						data.putString(Constants.KEY_APP_ID,
								softwareLists.get(position).getId());
						data.putString(Constants.KEY_APP_NAME, softwareLists
								.get(position).getName());
						Utils.toOtherClass(mContext, SoftDetailActivity.class,
								data);
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			});
		}
		return views.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
