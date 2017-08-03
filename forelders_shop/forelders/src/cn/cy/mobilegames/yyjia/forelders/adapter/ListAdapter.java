package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import cn.cy.mobilegames.yyjia.forelders.adapter.AppListAdapter.LazyloadListener;

public class ListAdapter extends BaseAdapter implements LazyloadListener {
	private LazyloadListener mLazyloadListener;
	private boolean mIsLazyLoad;
	private ArrayList<HashMap<String, Object>> mDataSource;

	@Override
	public int getCount() {
		return 0;
	}

	@Override
	public Object getItem(int arg0) {

		return null;
	}

	@Override
	public long getItemId(int arg0) {

		return 0;
	}

	public void setLazyloadListener(LazyloadListener listener) {
		mIsLazyLoad = true;
		mLazyloadListener = listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// last 4 item trigger the lazyload event
		if (mIsLazyLoad && !mLazyloadListener.isEnd()
				&& (position == getCount() - 4)) {
			// fix the multi-load situation
			synchronized (this) {
				if (mLazyloadListener.isLoadOver()) {
					mLazyloadListener.lazyload();
				}
			}
		}

		return null;
	}

	/**
	 * Lazyload web data
	 * 
	 * @param newData
	 */
	public void addData(ArrayList<HashMap<String, Object>> newData) {
		if (newData != null && newData.size() > 0
				&& !mDataSource.contains(newData)) {
			mDataSource.addAll(getCount(), newData);
			notifyDataSetChanged();
		}
	}

	public void addData(HashMap<String, Object> newData) {
		if (newData != null && newData.size() > 0
				&& !mDataSource.contains(newData)) {
			mDataSource.add(getCount(), newData);
			notifyDataSetChanged();
		}
	}

	public void removeData(HashMap<String, Object> oldData) {
		if (mDataSource != null && mDataSource.contains(oldData)) {
			mDataSource.remove(oldData);
			notifyDataSetChanged();
		}
	}

	public void removeData(int position) {
		if (mDataSource != null) {
			mDataSource.remove(position);
			notifyDataSetChanged();
		}
	}

	public void insertData(HashMap<String, Object> newData) {
		if (newData != null) {
			mDataSource.add(0, newData);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public boolean isEnd() {

		return false;
	}

	@Override
	public void lazyload() {

	}

	@Override
	public boolean isLoadOver() {

		return false;
	}

}
