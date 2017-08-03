package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnPreDrawListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.model.AppPhote;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.AutoScrollViewPager;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 应用全屏截图
 */
public class AppScreenShotActivity extends BaseActivity implements
		OnGestureListener, OnTouchListener {
	private AppPhote appPhote;
	private ArrayList<String> photeUrls;
	private GestureDetector gestureScanner;
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

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		photeUrls = new ArrayList<String>();
		appPhote = (AppPhote) getIntent().getSerializableExtra(
				Constants.KEY_PHOTE);
		if (appPhote != null) {
			for (int i = 0; i < appPhote.photeUrls.length; i++) {
				photeUrls.add(appPhote.photeUrls[i]);
			}
			position = appPhote.position;
		}

		gestureScanner = new GestureDetector(this, new GuideViewTouch());
		gestureScanner
				.setOnDoubleTapListener(new GestureDetector.OnDoubleTapListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						// 双击时产生一次
						return false;
					}

					@Override
					public boolean onDoubleTapEvent(MotionEvent e) {
						// 双击时产生两次
						return false;
					}

					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						// 短快的点击算一次单击
						finish();
						return false;
					}
				});
		setContentView(R.layout.viewpager_indicate);
		init();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
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

		final GuidePagerAdapter adapter = new GuidePagerAdapter(guides);
		// ViewPager设置数据适配器，这个类似于使用ListView时用的adapter
		pager.setAdapter(adapter);
		pager.clearAnimation();
		// 为Viewpager添加事件监听器 OnPageChangeListener
		pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				super.onPageScrolled(position, positionOffset,
						positionOffsetPixels);
			}

			@Override
			public void onPageSelected(int position) {
				int pos = position % photeUrls.size();
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
		super.onStart();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureScanner != null) {
			if (gestureScanner.onTouchEvent(event)) {
				event.setAction(MotionEvent.ACTION_CANCEL);
			}
		}
		return super.dispatchTouchEvent(event);
	}

	@Override
	public void onDestroy() {
		guides = null;
		photeUrls = null;
		gestureScanner = null;
		super.onDestroy();
	}

	private class GuideViewTouch extends
			GestureDetector.SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return super.onDoubleTapEvent(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {

			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public void onShowPress(MotionEvent e) {
			super.onShowPress(e);
		}

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

	}

	private ImageView buildImageView(String url, String prefix) {
		ImageView iv = new ImageView(this);
		imageLoader.setImageNetResource(iv,
				Utils.checkUrlContainHttp(prefix, url), R.drawable.default_img);
		ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		iv.setLayoutParams(params);
		iv.setScaleType(ScaleType.FIT_XY);
		return iv;
	}

	// 功能介绍界面的初始化
	private void init() {
		getView();
		initDot();
		ImageView iv = null;
		guides.clear();
		if (photeUrls.size() > 0) {
			for (int i = 0; i < photeUrls.size(); i++) {
				iv = buildImageView(photeUrls.get(i), Constants.URL_BASE_HOST);
				guides.add(iv);
			}
		}

	}

	/**
	 * 在layout中实例化一些View
	 */
	private void getView() {
		dotContain = (LinearLayout) this.findViewById(R.id.dot_contain);
		pager = (AutoScrollViewPager) findViewById(R.id.contentPager);
		curDot = (ImageView) findViewById(R.id.cur_dot);
	}

	/**
	 * 初始化 下方指示点 dot
	 * 
	 * @return 返回true说明初始化点成功，否则实例化失败
	 */
	private boolean initDot() {

		if (photeUrls.size() > 0) {
			ImageView dotView;
			for (int i = 0; i < photeUrls.size(); i++) {
				dotView = new ImageView(this);
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

	// ViewPager 适配器
	private class GuidePagerAdapter extends PagerAdapter {

		private List<View> views;

		private GuidePagerAdapter(List<View> views) {
			this.views = views;
		}

		@Override
		public void destroyItem(View view, int position, Object obj) {
			((ViewPager) view).removeView(views.get(position % views.size()));
		}

		@Override
		public void finishUpdate(View view) {
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public Object instantiateItem(View view, int position) {
			((ViewPager) view).addView(views.get(position % views.size()), 0);
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
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public boolean onFling(MotionEvent e, MotionEvent arg1, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return gestureScanner.onTouchEvent(event);
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
