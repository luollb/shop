package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.activity.QualityNewsDetailActivity;
import cn.cy.mobilegames.yyjia.forelders.model.NewsList;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class InfoItemAdapter extends BaseAdapter {
	private Activity activity;
	private List<NewsList> newsLists;
	private ImageLoaderUtil loader;
	private ListView listView;
	private boolean isTitle;
	private boolean all;

	public InfoItemAdapter(Activity activity, List<NewsList> news,
			ImageLoaderUtil loader, ListView myListView, boolean title,
			boolean showAll) {
		this.activity = activity;
		this.newsLists = news;
		this.loader = loader;
		this.listView = myListView;
		this.isTitle = title;
		this.all = showAll;
	}

	@Override
	public int getCount() {
		return newsLists == null ? 0
				: (newsLists.size() >= 4 ? (all == true ? newsLists.size() : 4)
						: newsLists.size());
	}

	@Override
	public Object getItem(int position) {
		return newsLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if (convertView == null) {
			view = LayoutInflater.from(activity).inflate(R.layout.info_item,
					null);
			holder = new ViewHolder();
			holder.infoTime = (TextView) view.findViewById(R.id.info_time);
			holder.infoTitle = (TextView) view.findViewById(R.id.info_title);
			holder.infoContent = (TextView) view
					.findViewById(R.id.info_content);
			holder.dot = (ImageView) view.findViewById(R.id.new_dot);
			holder.infoItem = (LinearLayout) view
					.findViewById(R.id.info_item_layout);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final NewsList news = newsLists.get(position);
		if (isTitle) {
			holder.infoTitle.setVisibility(View.VISIBLE);
			holder.dot.setVisibility(View.GONE);
		} else {
			holder.infoTitle.setVisibility(View.GONE);
			holder.dot.setVisibility(View.VISIBLE);
		}
		if (news.categoryid.equals("1")) {
			holder.infoTitle
					.setBackgroundResource(R.drawable.cornors_yellow_orange);
		} else if (news.categoryid.equals("2")) {
			holder.infoTitle.setBackgroundResource(R.drawable.cornors_red_rose);
		} else if (news.categoryid.equals("3")) {
			holder.infoTitle
					.setBackgroundResource(R.drawable.cornors_purple_dark);
		} else {
			holder.infoTitle
					.setBackgroundResource(R.drawable.cornors_purple_dark);
		}
		holder.infoItem.setBackgroundResource(R.drawable.bg_press_gray);
		holder.infoItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				NewsList news = (NewsList) listView.getItemAtPosition(position);
				Bundle data = new Bundle();
				data.putString(Constants.KEY_APP_ID, news.newid);
				data.putString(Constants.KEY_APP_NAME, news.title);
				Utils.toOtherClass(activity, QualityNewsDetailActivity.class,
						data);
			}
		});
		holder.infoTitle.setText(Utils.checkEmpty(news.catename));
		holder.infoContent.setText(Utils.checkEmpty(news.title));
		holder.infoTime
				.setText(Utils.formatMonth(Long.valueOf(news.dateline) * 1000));
		return view;
	}

	private class ViewHolder {
		private TextView infoTitle, infoContent, infoTime;
		private LinearLayout infoItem;
		private ImageView dot;
	}
}
