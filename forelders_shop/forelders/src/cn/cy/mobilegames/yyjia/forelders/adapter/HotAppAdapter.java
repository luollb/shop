package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.model.Softinfo;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class HotAppAdapter extends BaseAdapter {
	private Context mContext;
	private List<Softinfo> hotApps;
	private ImageLoaderUtil loader;
	GridView gridview;

	public HotAppAdapter(Context context, List<Softinfo> hotList,
			ImageLoaderUtil imageLoaderUtil, GridView gridView) {
		super();
		this.mContext = context;
		this.hotApps = hotList;
		this.loader = imageLoaderUtil;
		this.gridview = gridView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		View view = convertView;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(
					R.layout.logo_title_view, null);
			holder = new ViewHolder();
			holder.appName = (TextView) view.findViewById(R.id.soft_name);
			holder.hotAppImg = (ImageView) view.findViewById(R.id.soft_logo);
			view.setTag(holder);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		Softinfo sof = hotApps.get(position);
		holder.appName.setText(Utils.checkEmpty(sof.getName()));
		loader.setImageNetResourceCor(holder.hotAppImg, sof.getLogo(),
				R.drawable.img_default);
		return view;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return hotApps.get(position);
	}

	@Override
	public int getCount() {
		return hotApps == null ? 0 : (hotApps.size() >= 4 ? 4 : hotApps.size());
	}

	private class ViewHolder {
		private TextView appName;
		private ImageView hotAppImg;
	}

}
