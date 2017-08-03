package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.activity.QualityNewsCateActivity;
import cn.cy.mobilegames.yyjia.forelders.activity.QualityNewsDetailActivity;
import cn.cy.mobilegames.yyjia.forelders.model.NewsAdList;
import cn.cy.mobilegames.yyjia.forelders.model.NewsCate;
import cn.cy.mobilegames.yyjia.forelders.model.NewsList;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.util.view.MyListView;
import cn.cy.mobilegames.yyjia.forelders.R;

public class InfoListAdapter extends BaseAdapter {
	private List<NewsCate> newsCateLists;
	private Activity activity;
	private ImageLoaderUtil loader;

	public InfoListAdapter(List<NewsCate> newsCateLists, Activity activity,
			ImageLoaderUtil loader) {
		this.newsCateLists = newsCateLists;
		this.activity = activity;
		this.loader = loader;
	}

	@Override
	public int getCount() {
		return newsCateLists == null ? 0 : newsCateLists.size();
	}

	@Override
	public Object getItem(int position) {
		return newsCateLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHolder holder = null;
		if (convertView == null) {
			view = LayoutInflater.from(activity).inflate(R.layout.info_sub,
					null);
			holder = new ViewHolder();
			holder.infoTitle = (TextView) view.findViewById(R.id.setting_title);
			holder.leftAd = (ImageView) view.findViewById(R.id.first_ad);
			holder.leftLayout = (RelativeLayout) view
					.findViewById(R.id.left_layout);
			holder.adLayout = (LinearLayout) view.findViewById(R.id.ad_layout);
			holder.leftTv = (TextView) view.findViewById(R.id.first_text);
			holder.listview = (MyListView) view
					.findViewById(R.id.item_listview);
			holder.moreImg = (ImageView) view.findViewById(R.id.setting_more);
			holder.rightAd = (ImageView) view.findViewById(R.id.second_ad);
			holder.moreLayout = (LinearLayout) view
					.findViewById(R.id.see_more_layout);
			holder.rightLayout = (RelativeLayout) view
					.findViewById(R.id.right_layout);
			holder.rightTv = (TextView) view.findViewById(R.id.second_text);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final NewsCate newsCateList = newsCateLists.get(position);
		if (newsCateList.newsadlist != null) {
			final List<NewsAdList> newsAds = JsonUtil.toObjectList(
					newsCateList.newsadlist, NewsAdList.class);
			holder.infoTitle.setText(Utils
					.checkEmpty(newsCateList.categoryname));
			if (newsCateList.newsadlist == null
					|| newsCateList.newsadlist == "") {
				holder.adLayout.setVisibility(View.GONE);
			} else {
				holder.leftTv.setText(Utils.checkEmpty(newsAds.get(0).title));
				loader.setImageNetResource(holder.leftAd, newsAds.get(0).pic,
						R.drawable.img_default);
				holder.leftAd.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Bundle data = new Bundle();
						data.putString(Constants.KEY_APP_ID,
								newsAds.get(0).newid);
						data.putString(Constants.KEY_APP_NAME,
								newsAds.get(0).title);
						Utils.toOtherClass(activity,
								QualityNewsDetailActivity.class, data);
					}
				});
				// if (newsAds.size() == 1) {
				// // holder.rightLayout.setVisibility(View.GONE);
				// } else
				if (newsAds.size() > 1) {
					holder.rightTv
							.setText(Utils.checkEmpty(newsAds.get(1).title));
					loader.setImageNetResource(holder.rightAd,
							newsAds.get(1).pic, R.drawable.img_default);
					holder.rightAd.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							Bundle data = new Bundle();
							data.putString(Constants.KEY_APP_ID,
									newsAds.get(1).newid);
							data.putString(Constants.KEY_APP_NAME,
									newsAds.get(1).title);
							Utils.toOtherClass(activity,
									QualityNewsDetailActivity.class, data);
						}
					});
				}
			}
		} else {
			holder.adLayout.setVisibility(View.GONE);
		}

		holder.moreLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				data.putString(Constants.KEY_APP_ID, newsCateList.categoryid);
				data.putString(Constants.KEY_APP_NAME,
						newsCateList.categoryname);
				Utils.toOtherClass(activity, QualityNewsCateActivity.class, data);
			}
		});

		holder.infoTitle.setText(Utils.checkEmpty(newsCateList.categoryname));
		final List<NewsList> newsLists = JsonUtil.toObjectList(
				newsCateList.newslist, NewsList.class);
		if (newsLists != null) {
			holder.listview.setAdapter(new InfoItemAdapter(activity, newsLists,
					loader, holder.listview, false, false));

			holder.listview.setOnItemClickListener(new OnItemClick(holder) {

				@Override
				void onItemClick(ViewHolder holder, AdapterView<?> parent,
						View convertView, int position, long id) {
					NewsList news = (NewsList) holder.listview
							.getItemAtPosition(position);
					Bundle data = new Bundle();
					data.putString(Constants.KEY_APP_ID, news.newid);
					data.putString(Constants.KEY_APP_NAME, news.title);
					Utils.toOtherClass(activity,
							QualityNewsDetailActivity.class, data);
				}
			});
		}
		return view;
	}

	private abstract class OnItemClick implements OnItemClickListener {
		private ViewHolder holder;

		public OnItemClick(ViewHolder holder) {
			this.holder = holder;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View convertView,
				int position, long id) {
			onItemClick(holder, parent, convertView, position, id);
		}

		abstract void onItemClick(ViewHolder holder, AdapterView<?> parent,
				View convertView, int position, long id);

	}

	private class ViewHolder {
		private TextView infoTitle, leftTv, rightTv;
		private ImageView leftAd, rightAd, moreImg;
		private MyListView listview;
		private RelativeLayout leftLayout, rightLayout;
		private LinearLayout adLayout, moreLayout;
	}

}
