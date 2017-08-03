package cn.cy.mobilegames.yyjia.forelders.util.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CheckBox;
import cn.cy.mobilegames.yyjia.forelders.util.DownloadSelectListener;

public class MyCheckBox extends CheckBox {
	private long mDownloadId;
	public DownloadSelectListener mListener;
	private boolean mIsInDownEvent = false;

	public MyCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public MyCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public MyCheckBox(Context context) {
		super(context);
	}

	public void setDownloadId(long downloadId) {
		mDownloadId = downloadId;
	}

	public void setSelectListener(DownloadSelectListener listener) {
		mListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean handled = false;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// if (event.getX() < CHECKMARK_AREA) {
			mIsInDownEvent = true;
			handled = true;
			// }
			break;

		case MotionEvent.ACTION_CANCEL:
			mIsInDownEvent = false;
			break;

		case MotionEvent.ACTION_UP:
			if (mIsInDownEvent
			// && event.getX() < CHECKMARK_AREA
			) {
				toggleCheckMark();
				handled = true;
			}
			mIsInDownEvent = false;
			break;
		}

		if (handled) {
			postInvalidate();
		} else {
			handled = super.onTouchEvent(event);
		}

		return handled;
	}

	@Override
	public boolean isChecked() {
		return super.isChecked();
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
	}

	@Override
	public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
		super.setOnCheckedChangeListener(listener);
	}

	@Override
	public void toggle() {
		super.toggle();
	}

	private void toggleCheckMark() {
		toggle();
		mListener.onDownloadSelectionChanged(mDownloadId, isChecked());
	}

}
