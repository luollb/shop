package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import cn.cy.mobilegames.yyjia.forelders.ApiResponseFactory;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter;
import cn.cy.mobilegames.yyjia.forelders.model.ListResult;
import cn.cy.mobilegames.yyjia.forelders.util.BaseTools;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.AutoScrollViewPager;
import cn.cy.mobilegames.yyjia.forelders.R;

/**
 * 精品--热门
 * 
 * @author Administrator
 * 
 */
public class QualityHotFragment extends ProductListFragment {
	private View view;
	private AutoScrollViewPager pager;

	/**
	 * 存储图片的容器
	 */
	private List<View> guides = new ArrayList<View>();
	/**
	 * 移动的小点
	 */
	private ImageView curDot;
	/**
	 * 存储点的容器
	 */
	private LinearLayout dotContain;
	/**
	 * 位移量
	 */
	private int offset;
	private int paddingRight = 15;
	/**
	 * 记录当前的位置
	 */
	private int curPos = 0;
	private int position = 0;

	private int size;

	@Override
	@SuppressLint("InflateParams")
	public boolean doInitView(Bundle savedInstanceState) {
		return super.doInitView(savedInstanceState);
	}

	@Override
	public void setTotalSize(int size) {
		super.setTotalSize(size);
	}

	@Override
	public void setRequestCode(int code) {
		super.setRequestCode(code);
	}

	@Override
	public void doLazyload() {
		setRequestCode(MarketAPI.ACTION_GET_QUALITY_HOT);
		MarketAPI.getHomeBanner(getActivity(), this);
		super.doLazyload();
	}

	@Override
	public AppListAdapter doInitListAdapter() {

		return super.doInitListAdapter();
	}

	@Override
	protected int getItemCount() {

		return super.getItemCount();
	}

	@Override
	public void onPause() {
		pager.stopAutoScroll();
		super.onPause();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		pager.startAutoScroll(1000);
		super.onResume();
	}

	@Override
	public void onStart() {
		// 当curDot的所在的树形层次将要被绘出时此方法被调用
		curDot.getViewTreeObserver().addOnPreDrawListener(
				new OnPreDrawListener() {
					@Override
					public boolean onPreDraw() {
						// 获取ImageView的宽度也就是点图片的宽度
						offset = curDot.getDrawable().getMinimumWidth();
						return true;
					}
				});
		super.onStart();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_GET_HOME_BANNER:
			if (obj != null && obj instanceof ListResult) {
				ListResult result = (ListResult) obj;

				ArrayList<HashMap<String, Object>> data = ApiResponseFactory
						.parseAppList(getActivity(), result);
				if (data != null && data.size() > 0) {
					if (mList.getHeaderViewsCount() == 0) {
						mList.addHeaderView(view);
					}
					view.setVisibility(View.VISIBLE);
					size = data.size();
					initDot(size);
					ImageView iv = null;
					guides.clear();
					if (size > 0) {
						for (int i = 0; i < size; i++) {
							iv = buildImageView(
									Utils.checkEmpty(data.get(i).get(
											Constants.KEY_PRODUCT_HOME_BANNER)),
									Constants.URL_BASE_HOST);
							guides.add(iv);
						}
					}

					final GuidePagerAdapter adapter = new GuidePagerAdapter(
							guides, data);
					// ViewPager设置数据适配器，这个类似于使用ListView时用的adapter
					pager.setAdapter(adapter);
					pager.clearAnimation();
					pager.startAutoScroll(1000);
					// 为Viewpager添加事件监听器 OnPageChangeListener
					pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

						@Override
						public void onPageScrolled(int position,
								float positionOffset, int positionOffsetPixels) {
							super.onPageScrolled(position, positionOffset,
									positionOffsetPixels);
						}

						@Override
						public void onPageSelected(int position) {
							int pos = position % size;
							moveCursorTo(pos);
							curPos = pos;
							super.onPageSelected(position);
						}
					});
					if (position > 0) {
						pager.setCurrentItem(position);
						curPos = position;
						moveCursorTo(position);
					}
				} else {
					view.setVisibility(View.GONE);
				}
			} else {
				view.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
		super.onSuccess(method, obj);
	}

	@Override
	public void onError(int method, int statusCode) {
		switch (method) {
		case MarketAPI.ACTION_GET_HOME_BANNER:
			mList.removeHeaderView(view);
			break;
		default:
			break;
		}
		super.onError(method, statusCode);
	}

	// ViewPager 适配器
	private class GuidePagerAdapter extends PagerAdapter {

		private List<View> views;
		ArrayList<HashMap<String, Object>> list;

		private GuidePagerAdapter(List<View> views,
				ArrayList<HashMap<String, Object>> data) {
			this.views = views;
			this.list = data;
		}

		@Override
		public void destroyItem(View view, int position, Object obj) {
			View child = views.get(position % views.size());
			if (child.getParent() == null) {
				((ViewPager) view).removeView(child);
			}
		}

		@Override
		public void finishUpdate(View view) {
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View view, final int position) {
			View child = views.get(position % views.size());
			if (child.getParent() == null) {
				((ViewPager) view).addView(child, 0);
			}
			child.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					HashMap<String, Object> item = list.get(position);
					if (item != null) {
						Bundle data = new Bundle();
						data.putString(Constants.KEY_APP_ID,
								item.get(Constants.KEY_PRODUCT_ID).toString());
						data.putString(Constants.KEY_APP_NAME,
								item.get(Constants.KEY_PRODUCT_NAME).toString());
						Utils.toOtherClass(getActivity(),
								SoftDetailActivity.class, data);
					}
				}
			});
			return views.get(position % views.size());
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {

		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		super.onItemClick(parent, view, position, id);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);
	}

	@SuppressLint("InflateParams")
	@Override
	protected void doInitHeaderViewOrFooterView() {
		view = inflater.inflate(R.layout.viewpager_indicate, null);
		// 获取屏幕像素相关信息
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);

		initView(view);
		mList.addHeaderView(view);
		super.doInitHeaderViewOrFooterView();
	}

	private void initView(View view) {
		dotContain = (LinearLayout) view.findViewById(R.id.dot_contain);
		pager = (AutoScrollViewPager) view.findViewById(R.id.contentPager);
		pager.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, BaseTools
						.getWindowsHeight(getActivity()) * 1 / 5));
		curDot = (ImageView) view.findViewById(R.id.cur_dot);
	}

	/**
	 * 初始化 下方指示点 dot
	 * 
	 * @return 返回true说明初始化点成功，否则实例化失败
	 */
	private boolean initDot(int size) {
		if (size > 0) {
			ImageView dotView;
			dotContain.removeAllViews();
			for (int i = 0; i < size; i++) {
				dotView = new ImageView(getActivity());
				dotView.setImageResource(R.drawable.dot_selector);
				dotView.setPadding(0, 0, paddingRight, 0);
				dotView.setLayoutParams(new LinearLayout.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT, 1.0f));
				dotContain.addView(dotView);
			}
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 移动指针到相邻的位置 动画
	 * 
	 * @param position
	 *            指针的索引值
	 * */
	private void moveCursorTo(int position) {
		if (offset == 0) {
			offset = curDot.getDrawable().getMinimumWidth();
		}
		AnimationSet animationSet = new AnimationSet(true);
		TranslateAnimation tAnim = new TranslateAnimation(
				(offset + paddingRight) * curPos, (offset + paddingRight)
						* position, 0, 0);
		animationSet.addAnimation(tAnim);
		animationSet.setDuration(300);
		animationSet.setFillAfter(true);
		curDot.startAnimation(animationSet);
	}

	private ImageView buildImageView(String url, String prefix) {
		ImageView iv = new ImageView(getActivity());
		imageLoader.setImageNetResource(iv,
				Utils.checkUrlContainHttp(prefix, url), R.drawable.default_img);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_XY);
		return iv;
	}

}
