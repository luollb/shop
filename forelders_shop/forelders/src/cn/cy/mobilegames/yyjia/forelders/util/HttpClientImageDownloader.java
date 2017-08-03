package cn.cy.mobilegames.yyjia.forelders.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ContentLengthInputStream;
import com.nostra13.universalimageloader.core.download.ImageDownloader;

public class HttpClientImageDownloader implements ImageDownloader {

	/** {@value} */
	public static final int DEFAULT_HTTP_CONNECT_TIMEOUT = 5 * 1000; // milliseconds
	/** {@value} */
	public static final int DEFAULT_HTTP_READ_TIMEOUT = 20 * 1000; // milliseconds

	/** {@value} */
	protected static final int BUFFER_SIZE = 32 * 1024; // 32 Kb
	/** {@value} */
	protected static final String ALLOWED_URI_CHARS = "@#&=*+-_.,:!?()/~'%";

	protected static final int MAX_REDIRECT_COUNT = 5;

	protected static final String CONTENT_CONTACTS_URI_PREFIX = "content://com.android.contacts/";

	private static final String ERROR_UNSUPPORTED_SCHEME = "UIL doesn't support scheme(protocol) by default [%s]. "
			+ "You should implement this support yourself (BaseImageDownloader.getStreamFromOtherSource(...))";

	protected final Context context;
	protected final int connectTimeout;
	protected final int readTimeout;

	public HttpClientImageDownloader(Context context) {
		this.context = context.getApplicationContext();
		this.connectTimeout = DEFAULT_HTTP_CONNECT_TIMEOUT;
		this.readTimeout = DEFAULT_HTTP_READ_TIMEOUT;
	}

	public HttpClientImageDownloader(Context context, int connectTimeout,
			int readTimeout) {
		this.context = context.getApplicationContext();
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	@Override
	public InputStream getStream(String imageUri, Object extra)
			throws IOException {
		switch (Scheme.ofUri(imageUri)) {
		case HTTP:
		case HTTPS:
			return getStreamFromNetwork(imageUri, extra);
		case FILE:
			return getStreamFromFile(imageUri, extra);
		case CONTENT:
			return getStreamFromContent(imageUri, extra);
		case ASSETS:
			return getStreamFromAssets(imageUri, extra);
		case DRAWABLE:
			return getStreamFromDrawable(imageUri, extra);
		case UNKNOWN:
		default:
			return getStreamFromOtherSource(imageUri, extra);
		}
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in the
	 * network).
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException
	 *             if some I/O error occurs during network request or if no
	 *             InputStream could be created for URL.
	 */
	protected InputStream getStreamFromNetwork(String imageUri, Object extra)
			throws IOException {
		String encodedUrl = Uri.encode(imageUri, ALLOWED_URI_CHARS);
		DefaultHttpClient client = createHttpClient();
		HttpGet httpRequest = new HttpGet(encodedUrl.toString());
		HttpResponse response = client.execute(httpRequest);

		int redirectCount = 0;
		while (response.getStatusLine().getStatusCode() / 100 == 3
				&& redirectCount < MAX_REDIRECT_COUNT) {
			client.execute(httpRequest);
			redirectCount++;
		}

		HttpEntity entity = response.getEntity();
		BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
		return bufHttpEntity.getContent();
	}

	/**
	 * Create {@linkplain DefaultHttpClient HTTPClient}
	 * 
	 */
	protected DefaultHttpClient createHttpClient() throws IOException {

		HttpParams params = new BasicHttpParams();
		// Turn off stale checking. Our connections break all the time anyway,
		// and it's not worth it to pay the penalty of checking every time.
		HttpConnectionParams.setStaleCheckingEnabled(params, false);
		// Default connection and socket timeout of 10 seconds. Tweak to taste.
		HttpConnectionParams.setConnectionTimeout(params, connectTimeout);
		HttpConnectionParams.setSoTimeout(params, readTimeout);
		HttpConnectionParams.setSocketBufferSize(params, 8192);

		// Don't handle redirects -- return them to the caller. Our code
		// often wants to re-POST after a redirect, which we must do ourselves.
		HttpClientParams.setRedirecting(params, false);
		// Set the specified user agent and register standard protocols.
		HttpProtocolParams.setUserAgent(params, "some_randome_user_agent");
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(new org.apache.http.conn.scheme.Scheme("http",
				PlainSocketFactory.getSocketFactory(), 80));
		schemeRegistry.register(new org.apache.http.conn.scheme.Scheme("https",
				SSLSocketFactory.getSocketFactory(), 443));

		ClientConnectionManager manager = new ThreadSafeClientConnManager(
				params, schemeRegistry);

		DefaultHttpClient client = new DefaultHttpClient(manager, params);

		return client;
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located on the
	 * local file system or SD card).
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException
	 *             if some I/O error occurs reading from file system
	 */
	protected InputStream getStreamFromFile(String imageUri, Object extra)
			throws IOException {
		String filePath = Scheme.FILE.crop(imageUri);
		return new ContentLengthInputStream(new BufferedInputStream(
				new FileInputStream(filePath), BUFFER_SIZE), (int) new File(
				filePath).length());
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is accessed using
	 * {@link ContentResolver}).
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws FileNotFoundException
	 *             if the provided URI could not be opened
	 */
	protected InputStream getStreamFromContent(String imageUri, Object extra)
			throws FileNotFoundException {
		ContentResolver res = context.getContentResolver();
		Uri uri = Uri.parse(imageUri);
		if (imageUri.startsWith(CONTENT_CONTACTS_URI_PREFIX)) {
			return ContactsContract.Contacts.openContactPhotoInputStream(res,
					uri);
		} else {
			return res.openInputStream(uri);
		}
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in assets
	 * of application).
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException
	 *             if some I/O error occurs file reading
	 */
	protected InputStream getStreamFromAssets(String imageUri, Object extra)
			throws IOException {
		String filePath = Scheme.ASSETS.crop(imageUri);
		return context.getAssets().open(filePath);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI (image is located in
	 * drawable resources of application).
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 */
	protected InputStream getStreamFromDrawable(String imageUri, Object extra) {
		String drawableIdString = Scheme.DRAWABLE.crop(imageUri);
		int drawableId = Integer.parseInt(drawableIdString);
		return context.getResources().openRawResource(drawableId);
	}

	/**
	 * Retrieves {@link InputStream} of image by URI from other source with
	 * unsupported scheme. Should be overriden by successors to implement image
	 * downloading from special sources.<br />
	 * This method is called only if image URI has unsupported scheme. Throws
	 * {@link UnsupportedOperationException} by default.
	 * 
	 * @param imageUri
	 *            Image URI
	 * @param extra
	 *            Auxiliary object which was passed to
	 *            {@link DisplayImageOptions.Builder#extraForDownloader(Object)
	 *            DisplayImageOptions.extraForDownloader(Object)}; can be null
	 * @return {@link InputStream} of image
	 * @throws IOException
	 *             if some I/O error occurs
	 * @throws UnsupportedOperationException
	 *             if image URI has unsupported scheme(protocol)
	 */
	protected InputStream getStreamFromOtherSource(String imageUri, Object extra)
			throws IOException {
		throw new UnsupportedOperationException(String.format(
				ERROR_UNSUPPORTED_SCHEME, imageUri));
	}

}
