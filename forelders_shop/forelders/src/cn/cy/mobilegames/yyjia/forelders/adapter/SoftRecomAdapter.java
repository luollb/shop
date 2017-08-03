package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.download.ui.DownloadItem;
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.util.DownloadSelectListener;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.view.MyCheckBox;
import cn.cy.mobilegames.yyjia.forelders.R;

public class SoftRecomAdapter extends BaseAdapter {
	private Context mContext;
	// private MyCheckBox checkBox;
	private int showNum;
	private DownloadSelectListener mDownloadSelectionListener;
	private List<Object> sofLists;
	private ImageLoaderUtil loaderUtil;
	private ViewHolder holder = null;

	public SoftRecomAdapter(Context context, List<Object> soList,
			DownloadSelectListener mListener, int num) {
		super();
		this.mContext = context;
		loaderUtil = ImageLoaderUtil.getInstance(mContext);
		this.sofLists = soList;
		this.showNum = num;
		this.mDownloadSelectionListener = mListener;
	}

	@Override
	public int getCount() {
		return sofLists == null ? 0 : (sofLists.size() >= showNum ? showNum
				: sofLists.size());
	}

	@Override
	public Object getItem(int position) {
		return sofLists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("InflateParams")
	@Override
	public View getView(final int position, View convertView,
			ViewGroup viewgroup) {
		DownloadItem view = (DownloadItem) convertView;
		if (convertView == null) {
			holder = new ViewHolder();
			view = (DownloadItem) LayoutInflater.from(mContext).inflate(
					R.layout.manage_download_item, null);
			view.setBackgroundResource(R.drawable.bg_press_gray);
			view.setSelectListener(mDownloadSelectionListener);
			holder.downBtnLayout = (RelativeLayout) view
					.findViewById(R.id.down_layout);
			holder.downBtnLayout.setVisibility(View.GONE);
			holder.myCheckBox = (MyCheckBox) view
					.findViewById(R.id.download_checkbox);
			holder.myCheckBox.setSelectListener(mDownloadSelectionListener);
			holder.title = (TextView) view.findViewById(R.id.download_title);
			holder.summary = (TextView) view.findViewById(R.id.status_text);
			holder.logo = (ImageView) view.findViewById(R.id.download_icon);
			// holder.ratingBar = (RatingBar)
			// view.findViewById(R.id.download_icon);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		final Softinfo sofList = (Softinfo) sofLists.get(position);
		holder.myCheckBox.setDownloadId(position);
		holder.myCheckBox.setChecked(mDownloadSelectionListener
				.isDownloadSelected(position));
		view.setDownloadId(position);
		loaderUtil.setImageNetResource(holder.logo, sofList.getLogo(),
				R.drawable.img_default);
		holder.title.setText(sofList.getName());
		holder.summary.setText(sofList.getBriefsummary());
		return view;
	}

	private class ViewHolder {
		private RelativeLayout downBtnLayout;
		private MyCheckBox myCheckBox;
		private TextView title, summary;
		private ImageView logo;
		// private RatingBar ratingBar;

	}

	public void setChecked(int id, boolean checked) {
		mDownloadSelectionListener.onDownloadSelectionChanged(id, checked);
		notifyDataSetChanged();
	}
}
