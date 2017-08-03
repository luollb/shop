package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.model.SearchList;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.R;

public class SearchListAdapter extends BaseAdapter {
	public List<SearchList> searchObject;
	public SearchList searchList;
	Context mContext;

	public SearchListAdapter(List<SearchList> lists, Context context) {
		super();
		this.searchObject = lists;
		this.mContext = context;
	}

	@Override
	public int getCount() {

		return (searchObject == null) ? 0 : searchObject.size();
	}

	@Override
	public Object getItem(int position) {

		return searchObject.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		SearchListViewHolder viewHolder = null;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.search_list_gridview_detail, null);
			viewHolder = new SearchListViewHolder();
			viewHolder.softName = (TextView) view
					.findViewById(R.id.search_list_title_text);
			// viewHolder.softPic = (ImageView)
			// view.findViewById(R.id.search_list_img);

			view.setTag(viewHolder);
		} else {
			viewHolder = (SearchListViewHolder) view.getTag();
		}

		try {
			if (searchObject != null) {
				searchList = searchObject.get(position);
				viewHolder.softName.setText(searchList.word.trim());
				// viewHolder.softName.setTextSize(Float
				// .valueOf(searchList.fontsize));
				// viewHolder.softName.setTextColor(Color
				// .parseColor(searchList.color));
			} else {
				ToastUtil.showShortToast(mContext, "没有更多内容了！");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	public class SearchListViewHolder {
		TextView softName;
	}

}
