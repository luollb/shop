package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.ArrayList;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class DetailImgPagerAdapter extends PagerAdapter {
	ArrayList<ImageView> views;
	ViewPager viewPager;

	public DetailImgPagerAdapter(ArrayList<ImageView> lists, ViewPager pager) {
		super();
		this.views = lists;
		this.viewPager = pager;
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
	public Object instantiateItem(View container, int position) {
		viewPager = (ViewPager) container;
		if (views.get(position).getParent() == null) {
			viewPager.addView(views.get(position));
		}
		viewPager.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		return views.get(position);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}
}
