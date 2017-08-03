package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.MarketAPI;
import cn.cy.mobilegames.yyjia.forelders.ApiAsyncTask.ApiRequestListener;
import cn.cy.mobilegames.yyjia.forelders.app.YYJiaApp;
import cn.cy.mobilegames.yyjia.forelders.model.OpenPicture;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 引导界面 广告引导界面
 */
public class GuideActivity extends BaseActivity implements ApiRequestListener {
	private static final String SHAREDPREFERENCES_NAME = "my_pref";
	private static final String KEY_IS_FIRST = "is_first";
	private GestureDetector gestureDetector;
	private ImageView image;
	// private Button btn;
	private List<String> imgUrlLists;
	private ImageLoaderUtil loaderUtil;
	private long serverTime;
	// private CustomProgressDialog progressDialog;
	/**
	 * 当前索引
	 */
	private int currentItem = 0;
	/**
	 * 滑动宽度
	 */
	private int flaggingWidth;
	/**
	 * 指示图片资源
	 */
	private int[] dotResId = { R.drawable.checkbox_checked,
			R.drawable.checkbox_unchecked_icon };
	/**
	 * 指示图片数组
	 */
	private ImageView[] dots;
	/**
	 * 指示图片
	 */
	private ImageView dot;
	private ImageView imageViewLeft, imageViewRight, splashImg;
	private boolean hasNew = true;
	private boolean isFirst = true;
	private List<View> mPageViews;
	private ViewPager viewPager;
	private ViewGroup viewDots;
	private ViewGroup viewPics;
	private SharedPreferences.Editor editor;
	private SharedPreferences sharPreferences;
	private LayoutInflater inflater;
	private GuideActivity activity;

	// private String cacheKey = "";
	// private ResponseCacheManager cacheManager;

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
//		activity = this;
		// cacheManager = ResponseCacheManager.getInstance();
//		System.out.println("--->GuideActivity");
//		startActivity(new Intent(this,MainActivity.class));
//		inflater = getLayoutInflater();
//		initStatus();
//		initView();
	}

	@Override
	public void onDestroy() {
		mPageViews = null;
		sharPreferences = null;
		editor = null;
		dots = null;
		dotResId = null;
		imgUrlLists = null;
		super.onDestroy();
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

	/**
	 * 初始化
	 */
	private void initStatus() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		flaggingWidth = (displayMetrics.widthPixels / 3);
		loaderUtil = ImageLoaderUtil.getInstance(this);
		sharPreferences = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
		editor = sharPreferences.edit();
	}

	/**
	 * 进入首页
	 */
	void toHomeActivity() {
		// 有新资源 保存到SharedPreferences 并修改已经没有更新
		if (hasNew) {
			editor.putBoolean(KEY_IS_FIRST, false);
			editor.putLong(Constants.GUIDE_FIX_TIME, serverTime);
			editor.commit();
			hasNew = false;
		}
		if (isFirst) {
			isFirst = false;
			editor.putBoolean(KEY_IS_FIRST, isFirst);
		}
		Utils.toOtherClass(this, MainActivity.class, null);
		Utils.finish(activity);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector != null) {
			if (gestureDetector.onTouchEvent(event)) {
				event.setAction(3);
			}
		}
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 当引导界面最后一页 并按返回键 进入首页
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mPageViews != null) {
			if ((keyCode == KeyEvent.KEYCODE_BACK)
					&& (currentItem == mPageViews.size() - 1)) {
				toHomeActivity();
				return false;
			}
		}
		// 双击返回键 提示并退出
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (System.currentTimeMillis() - currentTime < 1500) {
				YYJiaApp app = (YYJiaApp) getApplication();
				app.exit();
				Utils.finish(activity);
			} else {
				ToastUtil.showShortToast(this, R.string.warn_exit_hint);
			}
			currentTime = System.currentTimeMillis();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private long currentTime;

	/**
	 * 更新引导界面
	 * 
	 * @param openPicture
	 */
	@SuppressLint("InflateParams")
	private void updateBootInterface(OpenPicture openPicture) {
		List<String> pictures = JsonUtil.toJsonStrList(openPicture.list);
		imgUrlLists = new ArrayList<String>();
		if (pictures != null && pictures.size() > 0) {
			for (String pic : pictures) {
				imgUrlLists.add(pic);
			}
			editor.putBoolean(KEY_IS_FIRST, true);
			splashImg.setVisibility(View.GONE);
			imageViewLeft.setVisibility(View.GONE);
			imageViewRight.setVisibility(View.VISIBLE);
			gestureDetector = new GestureDetector(activity,
					new GuideViewTouch());

			if (imgUrlLists != null) {
				// if (cacheManager.getResponse(cacheKey) instanceof List<?>) {
				// }
				// cacheManager.putResponse(cacheKey, imgUrlLists);

				int size = imgUrlLists.size();
				mPageViews = new ArrayList<View>(size);
				dots = new ImageView[size];
				for (int i = 0; i < size; i++) {
					View view = inflater.inflate(R.layout.guide_item, null);
					RelativeLayout layout = (RelativeLayout) view
							.findViewById(R.id.guide_item);
					image = (ImageView) view.findViewById(R.id.guide_img);

					loaderUtil
							.setImageNetResource(image, Utils
									.checkUrlContainHttp(
											Constants.URL_BASE_HOST,
											imgUrlLists.get(i)),
									R.drawable.img_default);

					if (i == size - 1) {
						Button btn = (Button) view.findViewById(R.id.start);
						btn.setVisibility(View.VISIBLE);
						btn.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View paramView) {
								toHomeActivity();
							}
						});
					}
					mPageViews.add(layout);
				}

				for (int i = 0; i < size; i++) {
					dot = new ImageView(activity);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							30, 30);
					params.setMargins(8, 0, 8, 0);
					dot.setLayoutParams(params);
					dots[i] = dot;
					if (i == 0) {
						// 默认选中第一张图片
						dots[i].setBackgroundResource(R.drawable.checkbox_checked);
					} else {
						dots[i].setBackgroundResource(R.drawable.checkbox_unchecked_icon);
					}
					viewDots.addView(dots[i]);
				}

				setContentView(viewPics);

				viewPager.setAdapter(new MyAdapter(mPageViews));
				viewPager.setOnPageChangeListener(new MyPageChangeListener());
				imageViewLeft.setOnClickListener(new ButtonListener());
				imageViewRight.setOnClickListener(new ButtonListener());

			}
		} else {
			toHomeActivity();
		}
	}

	@SuppressLint("InflateParams")
	private void initView() {
		viewPics = ((ViewGroup) inflater.inflate(R.layout.activity_guide, null));
		viewDots = ((ViewGroup) viewPics.findViewById(R.id.guide_dots));
		viewPager = ((ViewPager) viewPics.findViewById(R.id.guide_viewpager));

		splashImg = (ImageView) viewPics.findViewById(R.id.splash);
		imageViewLeft = ((ImageView) viewPics.findViewById(R.id.leftBtn));
		imageViewRight = ((ImageView) viewPics.findViewById(R.id.rightBtn));
		setContentView(viewPics);

		AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1f);
		alphaAnimation.setDuration(1500);
		viewPics.setAnimation(alphaAnimation);
		alphaAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation anima) {
			}

			@Override
			public void onAnimationRepeat(Animation anima) {

			}

			@Override
			public void onAnimationEnd(Animation anima) {
				// if (Utils.isNetworkAvailable(activity)) {
				// MarketAPI.checkSplash(activity, activity);
				// } else {
				toHomeActivity();
				// }
			}
		});
	}

	private class ButtonListener implements View.OnClickListener {

		@Override
		public void onClick(View v) {
			int showNext = 0;
			if (v.getId() == R.id.leftBtn) {
				if (currentItem == 0) {
					showNext = currentItem;
				} else {
					showNext = currentItem - 1;
				}
				viewPager.setCurrentItem(showNext);
			}
			if (v.getId() == R.id.rightBtn) {
				if (mPageViews != null) {
					if (currentItem < mPageViews.size() - 1) {
						showNext = currentItem + 1;
						viewPager.setCurrentItem(showNext);
					} else if (currentItem == mPageViews.size() - 1) {
						showNext = currentItem;
						toHomeActivity();
					}
				}
			}
		}
	}

	private class GuideViewTouch extends
			GestureDetector.SimpleOnGestureListener {
		private GuideViewTouch() {
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (mPageViews != null) {
				if ((currentItem == mPageViews.size() - 1)
						&& (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1
								.getY() - e2.getY()))
						&& ((e1.getX() - e2.getX() <= -flaggingWidth) || (e1
								.getX() - e2.getX() >= flaggingWidth))
						&& (e1.getX() - e2.getX() >= flaggingWidth)) {
					toHomeActivity();
					return true;
				}
			}
			return false;
		}
	}

	private class MyAdapter extends PagerAdapter {
		List<View> views;

		public MyAdapter(List<View> lists) {
			super();
			views = lists;
		}

		@Override
		public void destroyItem(View paramView, int paramInt, Object paramObject) {
			((ViewPager) paramView).removeView((View) paramObject);
		}

		@Override
		public void finishUpdate(View paramView) {
		}

		@Override
		public int getCount() {
			return views == null ? 0 : views.size();
		}

		@Override
		public Object instantiateItem(View paramView, int paramInt) {
			((ViewPager) paramView).addView(views.get(paramInt));
			return views.get(paramInt);
		}

		@Override
		public boolean isViewFromObject(View paramView, Object paramObject) {
			return paramView == paramObject;
		}

		@Override
		public void restoreState(Parcelable paramParcelable,
				ClassLoader paramClassLoader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View paramView) {
		}
	}

	private class MyPageChangeListener implements
			ViewPager.OnPageChangeListener {
		private MyPageChangeListener() {
		}

		@Override
		public void onPageScrollStateChanged(int paramInt) {
		}

		@Override
		public void onPageScrolled(int paramInt1, float paramFloat,
				int paramInt2) {
		}

		@Override
		public void onPageSelected(int page) {
			currentItem = page;
			if (dots != null) {
				for (int i = 0; i < dots.length; i++) {
					if (page == i) {
						dots[page].setBackgroundResource(dotResId[0]);
					} else {
						dots[i].setBackgroundResource(dotResId[1]);
					}
					if (page == 0) {
						imageViewLeft.setVisibility(View.GONE);
					} else {
						imageViewLeft.setVisibility(View.VISIBLE);
					}
				}
			}
		}
	}

	@Override
	public void onSuccess(int method, Object obj) {
		switch (method) {
		case MarketAPI.ACTION_CHECK_SPLASH:
			if (obj != null && obj instanceof OpenPicture) {
				OpenPicture picture = (OpenPicture) obj;
				serverTime = picture.date;
				long oldTime = sharPreferences.getLong(
						Constants.GUIDE_FIX_TIME, 0);
				// 服务器有新的资源
				if (serverTime > oldTime) {
					hasNew = true;
					// cacheKey = MD5Util.stringToMD5(serverTime + "");
					updateBootInterface(picture);
				} else {
					// 没有更新的资源
					hasNew = false;
					isFirst = sharPreferences.getBoolean(KEY_IS_FIRST, true);
					toHomeActivity();
				}
			} else {
				toHomeActivity();
			}

			break;

		default:
			break;
		}
	}

	@Override
	public void onError(int method, int statusCode) {
		long oldTime = sharPreferences.getLong(Constants.GUIDE_FIX_TIME, 0);
		// 已经有加载过引导图片
		if (oldTime > 0) {
			toHomeActivity();
		} else {
			editor.putBoolean(KEY_IS_FIRST, true);
			toHomeActivity();
		}
	}

}
