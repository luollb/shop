package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.activity.CateMoreActivity;
import cn.cy.mobilegames.yyjia.forelders.activity.SoftDetailActivity;
import cn.cy.mobilegames.yyjia.forelders.model.CateItem;
import cn.cy.mobilegames.yyjia.forelders.model.Category;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.JsonUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class CateAdapter extends BaseAdapter {
	private Context mContext;
	private List<Object> lists;
	private ImageLoaderUtil loaderUtil;

	public CateAdapter(Context context, List<Object> lists,
			ImageLoaderUtil imageLoaderUtil) {
		super();
		this.mContext = context;
		this.lists = lists;
		this.loaderUtil = imageLoaderUtil;
	}

	@Override
	public int getCount() {
		return lists == null ? 0 : lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		ViewHold hold = null;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.category_item, null);
			hold = new ViewHold();
			hold.cateItemGridView = (GridView) view
					.findViewById(R.id.cate_item_grid);
			// hold.cateMore = (ImageView)
			// view.findViewById(R.id.cate_see_more);
			hold.moreCateLayout = (RelativeLayout) view
					.findViewById(R.id.cate_more);
			hold.cateTitle = (TextView) view.findViewById(R.id.cate_item_title);

			view.setTag(hold);

		} else {
			hold = (ViewHold) view.getTag();
		}
		final Category category = (Category) lists
				.get(position);
		hold.moreCateLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle data = new Bundle();
				data.putString(Constants.KEY_APP_ID, category.categoryid);
				data.putString(Constants.KEY_APP_NAME, category.categoryname);
				Utils.toOtherClass(mContext, CateMoreActivity.class, data);
			}
		});
		hold.cateTitle.setText(Utils.checkEmpty(category.categoryname.trim()));
		final List<CateItem> cateItemLists = JsonUtil.toObjectList(
				category.list, CateItem.class);
		hold.cateItemGridView.setAdapter(new Adapter(cateItemLists));
		hold.cateItemGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View convertView,
					int position, long id) {
				Bundle data = new Bundle();
				data.putString(Constants.KEY_APP_ID,
						cateItemLists.get(position).id);
				data.putString(Constants.KEY_APP_NAME,
						cateItemLists.get(position).name);
				Utils.toOtherClass(mContext, SoftDetailActivity.class, data);
			}
		});

		return view;
	}

	private class ViewHold {
		private RelativeLayout moreCateLayout;
		private TextView cateTitle, cateItemName;
		private ImageView cateItemImg;
		private GridView cateItemGridView;
	}

	private class Adapter extends BaseAdapter {
		private List<CateItem> cateGridList;

		private Adapter(List<CateItem> list) {
			this.cateGridList = list;
		}

		@Override
		public int getCount() {
			return cateGridList.size() == 0 ? 0 : cateGridList.size();
		}

		@Override
		public Object getItem(int position) {
			return cateGridList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@SuppressLint("InflateParams")
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			ViewHold hold = null;
			if (convertView == null) {
				view = LayoutInflater.from(mContext).inflate(
						R.layout.logo_title_view, null);
				hold = new ViewHold();
				hold.cateItemImg = (ImageView) view
						.findViewById(R.id.soft_logo);
				hold.cateItemName = (TextView) view
						.findViewById(R.id.soft_name);

				view.setTag(hold);
			} else {
				hold = (ViewHold) view.getTag();
			}
			CateItem itemList = cateGridList.get(position);
			hold.cateItemName.setText(Utils.checkEmpty(itemList.name.trim()));
			loaderUtil.setImageNetResource(hold.cateItemImg,
					itemList.logo.trim(), R.drawable.img_default);
			return view;
		}

	}

}
