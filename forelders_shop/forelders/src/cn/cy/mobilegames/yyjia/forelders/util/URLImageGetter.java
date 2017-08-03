package cn.cy.mobilegames.yyjia.forelders.util;

import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.view.Display;
import android.widget.TextView;

public class URLImageGetter implements ImageGetter {

	TextView textView;
	Context context;

	public URLImageGetter(Context contxt, TextView textView) {
		this.context = contxt;
		this.textView = textView;
	}

	@Override
	public Drawable getDrawable(String paramString) {
		URLDrawable urlDrawable = new URLDrawable(context);

		ImageGetterAsyncTask getterTask = new ImageGetterAsyncTask(urlDrawable);
		getterTask.execute(paramString);
		return urlDrawable;
	}

	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable drawable) {
			this.urlDrawable = drawable;
		}

		@Override
		protected void onPostExecute(Drawable result) {
			if (result != null) {
				urlDrawable.drawable = result;

				URLImageGetter.this.textView.requestLayout();
			}
		}

		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return fetchDrawable(source);
		}

		public Drawable fetchDrawable(String url) {
			Drawable drawable = null;
			URL Url;
			try {
				Url = new URL(url);
				drawable = Drawable.createFromStream(Url.openStream(), "");
			} catch (Exception e) {
				return null;
			}

			// 按比例缩放图片
			Rect bounds = getDefaultImageBounds(context);
			int newwidth = bounds.width();
			int newheight = bounds.height();
			double factor = 1;

			double fx = (double) drawable.getIntrinsicWidth()
					/ (double) newwidth;
			double fy = (double) drawable.getIntrinsicHeight()
					/ (double) newheight;
			factor = fx > fy ? fx : fy;
			if (factor < 1)
				factor = 1;
			newwidth = (int) (drawable.getIntrinsicWidth() / factor);
			newheight = (int) (drawable.getIntrinsicHeight() / factor);

			drawable.setBounds(0, 0, newwidth * 2, newheight * 2);
			return drawable;
		}
	}

	// public void setCompoundDrawablesWithIntrinsicBounds(Drawable left,
	// Drawable top, Drawable right, Drawable bottom) {
	//
	// if (left != null) {
	// left.setBounds(0, 0, left.getIntrinsicWidth(),
	// left.getIntrinsicHeight());
	// }
	// if (right != null) {
	// right.setBounds(0, 0, right.getIntrinsicWidth(),
	// right.getIntrinsicHeight());
	// }
	// if (top != null) {
	// top.setBounds(0, 0, top.getIntrinsicWidth(),
	// top.getIntrinsicHeight());
	// }
	// if (bottom != null) {
	// bottom.setBounds(0, 0, bottom.getIntrinsicWidth(),
	// bottom.getIntrinsicHeight());
	// }
	// textView.setCompoundDrawables(left, top, right, bottom);
	// }
	//
	// public void setCompoundDrawablesWithIntrinsicBounds(int left, int top,
	// int right, int bottom) {
	// final Resources resources = context.getResources();
	// setCompoundDrawablesWithIntrinsicBounds(
	// left != 0 ? resources.getDrawable(left) : null,
	// top != 0 ? resources.getDrawable(top) : null,
	// right != 0 ? resources.getDrawable(right) : null,
	// bottom != 0 ? resources.getDrawable(bottom) : null);
	// }

	// 预定图片宽高比例为 4:3
	@SuppressWarnings("deprecation")
	public Rect getDefaultImageBounds(Context context) {
		Display display = ((Activity) context).getWindowManager()
				.getDefaultDisplay();
		int width = display.getWidth();
		int height = width * 3 / 4;

		Rect bounds = new Rect(0, 0, width, height);
		return bounds;
	}

}
