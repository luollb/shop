package cn.cy.mobilegames.yyjia.forelders.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.jeremyfeinstein.slidingmenu.lib.indicator.Indicator;
import com.jeremyfeinstein.slidingmenu.lib.indicator.IndicatorViewPager;
import com.umeng.analytics.MobclickAgent;

/**
 * 软件--主界面
 * 
 * @author Administrator
 * 
 */
public class TabSoftFragment extends BaseFragment {
	private Fragment[] fragments;
	private Indicator indicator;
	private IndicatorViewPager indicatorViewPager;
	private ViewPager viewPager;
	private View view;

	public TabSoftFragment() {
		Fragment[] arrayOfFragment = new Fragment[4];
		arrayOfFragment[0] = new SoftRecomFragment();
		arrayOfFragment[1] = new SoftCategoryFragment();
		arrayOfFragment[2] = new SoftRankFragment();
		arrayOfFragment[3] = new SoftNewFragment();
		fragments = arrayOfFragment;
	}

	private void initView(View paramView) {
		viewPager = ((ViewPager) paramView.findViewById(R.id.tab_viewPager));
		indicator = ((Indicator) paramView.findViewById(R.id.tab_indicator));
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
		// viewPager.setCanScroll(true);
		viewPager.setOffscreenPageLimit(4);
		// viewPager.setPrepareNumber(1);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
	}

	public void getDetailInfo() {
	}

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
	}

	@Override
	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedBudleState) {
		if (view == null) {
			view = inflater.inflate(R.layout.activity_tabchild, null);
			initView(view);
			Utils.D("soft view == null");
		}

		ViewGroup parent = (ViewGroup) view.getParent();
		if (parent != null) {
			parent.removeView(view);
		}
		Utils.D("soft onCreateView");
		return view;
	}

	public void setCurrentTab(int pos) {
		indicator.setCurrentItem(pos);
		viewPager.setCurrentItem(pos);
	}

	private class MyAdapter extends
			IndicatorViewPager.IndicatorFragmentPagerAdapter {
		private LayoutInflater inflater = LayoutInflater.from(getActivity());
		private String[] tabNames = Constants.softNames;

		public MyAdapter(FragmentManager arg2) {
			super(getChildFragmentManager());
		}

		@Override
		public int getCount() {
			return tabNames.length;
		}

		@Override
		public Fragment getFragmentForPage(int paramInt) {
			return fragments[paramInt];
		}

		@Override
		public View getViewForTab(int paramInt, View convertView,
				ViewGroup paramViewGroup) {
			if (convertView == null)
				convertView = inflater.inflate(R.layout.tab_child,
						paramViewGroup, false);

			TextView tv = (TextView) convertView.findViewById(R.id.tab_tv);
			tv.setText(tabNames[paramInt]);
			return convertView;
		}
	}

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub

	}
}
