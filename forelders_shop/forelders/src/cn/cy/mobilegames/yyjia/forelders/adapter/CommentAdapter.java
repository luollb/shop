package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.model.CommentList;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class CommentAdapter extends BaseAdapter {
	private List<CommentList> lists;
	private Context context;
	private boolean all;
	private int size;

	public CommentAdapter(List<CommentList> commentlists, Context context,
			boolean showAll, int showNum) {
		super();
		this.lists = commentlists;
		this.context = context;
		this.size = showNum;
		this.all = showAll;
	}

	@Override
	public int getCount() {
		return lists == null ? 0 : (all ? lists.size()
				: (lists.size() > size ? size : lists.size()));
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
		ViewHolder holder = null;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(
					R.layout.comment_listview_item, null);
			holder = new ViewHolder();
			holder.message = (TextView) view.findViewById(R.id.message);
			holder.time = (TextView) view.findViewById(R.id.user_time);
			holder.userName = (TextView) view.findViewById(R.id.user_name);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}

		CommentList commentList = lists.get(position);
		holder.message.setText(Html.fromHtml(commentList.message, imgGetter,
				null));

		holder.time.setText(Utils.formatDates(Long.valueOf(Utils
				.checkValue(commentList.dateline)) * 1000));
		if (TextUtils.isEmpty(commentList.username.trim())) {
			holder.userName.setText(Constants.VISITOR_CN);
		} else {
			holder.userName.setText(Utils.checkEmpty(commentList.username
					.trim()));
		}

		return view;
	}

	class ViewHolder {
		TextView userName, time, message;
	}

	ImageGetter imgGetter = new Html.ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			Drawable drawable = null;
			URL url;
			try {
				url = new URL(source);
				drawable = Drawable.createFromStream(url.openStream(), "");
			} catch (Exception e) {
				return null;
			}
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			return drawable;
		}
	};
}
