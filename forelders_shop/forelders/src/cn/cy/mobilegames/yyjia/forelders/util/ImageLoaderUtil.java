package cn.cy.mobilegames.yyjia.forelders.util;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.Session;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 用于图片加载缓存处理
 * 
 * @author 鳥不死
 * 
 */
public class ImageLoaderUtil {
	private static Context mContext;
	private ImageLoader imageLoader;
	private Session session;

	private volatile static ImageLoaderUtil instance;

	private ImageLoaderUtil(Context context) {
		mContext = context;
		session = Session.get(mContext);
		imageLoader = ImageLoader.getInstance();
		File cacheDir = StorageUtils.getCacheDirectory(mContext);
		// HttpParams params = new BasicHttpParams();
		// // Turn off stale checking. Our connections break all the time
		// anyway,
		// // and it's not worth it to pay the penalty of checking every time.
		// HttpConnectionParams.setStaleCheckingEnabled(params, false);
		// // Default connection and socket timeout of 10 seconds. Tweak to
		// taste.
		// HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
		// HttpConnectionParams.setSoTimeout(params, 10 * 1000);
		// HttpConnectionParams.setSocketBufferSize(params, 8192);
		//
		// // Don't handle redirects -- return them to the caller. Our code
		// // often wants to re-POST after a redirect, which we must do
		// ourselves.
		// HttpClientParams.setRedirecting(params, false);
		// // Set the specified user agent and register standard protocols.
		// HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
		// SchemeRegistry schemeRegistry = new SchemeRegistry();
		// schemeRegistry.register(new Scheme("http", PlainSocketFactory
		// .getSocketFactory(), 80));
		// schemeRegistry.register(new Scheme("https", SSLSocketFactory
		// .getSocketFactory(), 443));
		//
		// ClientConnectionManager manager = new ThreadSafeClientConnManager(
		// params, schemeRegistry);

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				mContext)
				// .defaultDisplayImageOptions(defaultOptions)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.threadPoolSize(1)
				.memoryCache(new WeakMemoryCache())
				.imageDownloader(
						new HttpClientImageDownloader(mContext, 10 * 1000,
								10 * 1000)).build();

		// // default = device screen dimensions
		// .diskCacheExtraOptions(480, 800, null)
		// // .taskExecutor().taskExecutorForCachedImages()
		// .threadPoolSize(3)
		// // default
		// .threadPriority(Thread.NORM_PRIORITY - 1)
		// // default
		// .tasksProcessingOrder(QueueProcessingType.FIFO)
		// // default
		// .denyCacheImageMultipleSizesInMemory()
		// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		// .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13)
		// // default
		// .diskCache(new UnlimitedDiscCache(cacheDir))
		// // default
		// .diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
		// .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) //
		// default
		// .imageDecoder(new BaseImageDecoder(false)) // default
		// .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) //
		// default
		// .build();
		imageLoader.init(config);
	}

	public static ImageLoaderUtil getInstance(Context context) {
		if (instance == null) {
			synchronized (ImageLoaderUtil.class) {
				if (instance == null) {
					instance = new ImageLoaderUtil(context);
				}
			}
		}
		return instance;
	}

	public Bitmap loadImageSync(String uri) {
		return imageLoader.loadImageSync(uri);
	}

	public void clearUrlCache(String url) {
		DiskCacheUtils.removeFromCache(url, imageLoader.getDiskCache());
		MemoryCacheUtils.removeFromCache(url, imageLoader.getMemoryCache());
	}

	public void clearMemoryCache() {
		imageLoader.clearMemoryCache();
	}

	public void clearDiskCache() {
		imageLoader.clearDiscCache();
	}

	public void setImageNetResourceCor(final ImageView view, final String url,
			int imageRes) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageRes).showImageForEmptyUri(imageRes)
				.showImageOnFail(imageRes).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(20)).build();

		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		try {
			if (!session.isShowImage()) {
				view.setImageResource(imageRes);
			} else {
				imageLoader
						.displayImage(Utils.checkUrlContainHttp(
								Constants.URL_BASE_HOST, url), view, options,
								animateFirstListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setImageNetResource(final ImageView view, final String url,
			int imageRes) {

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageRes).showImageForEmptyUri(imageRes)
				.showImageOnFail(imageRes).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(0)).build();

		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		try {
			if (!session.isShowImage()) {
				view.setImageResource(imageRes);
			} else {
				imageLoader
						.displayImage(Utils.checkUrlContainHttp(
								Constants.URL_BASE_HOST, url), view, options,
								animateFirstListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setImageNetResource(final ImageView view, final String url,
			int imageRes, boolean cacheInMemory, boolean cacheOnDisk) {

		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(imageRes).showImageForEmptyUri(imageRes)
				.showImageOnFail(imageRes).cacheInMemory(cacheInMemory)
				.cacheOnDisc(cacheOnDisk).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(0)).build();

		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		if (!session.isShowImage()) {
			view.setImageResource(imageRes);
		} else {
			imageLoader.displayImage(
					Utils.checkUrlContainHttp(Constants.URL_BASE_HOST, url),
					view, options, animateFirstListener);
		}
	}

	public void setImageNetResource(final ImageView view, final String url,
			int imageRes, DisplayImageOptions options) {

		ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
		try {
			if (!session.isShowImage()) {
				view.setImageResource(imageRes);
			} else {
				imageLoader
						.displayImage(Utils.checkUrlContainHttp(
								Constants.URL_BASE_HOST, url), view, options,
								animateFirstListener);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static class AnimateFirstDisplayListener extends
			SimpleImageLoadingListener {

		static final List<String> displayedImages = Collections
				.synchronizedList(new LinkedList<String>());

		@Override
		public void onLoadingComplete(String imageUri, View view,
				Bitmap loadedImage) {
			if (loadedImage != null) {
				ImageView imageView = (ImageView) view;
				boolean firstDisplay = !displayedImages.contains(imageUri);
				if (firstDisplay) {
					FadeInBitmapDisplayer.animate(imageView, 500);
					displayedImages.add(imageUri);
				}
			}
		}
	}

}
