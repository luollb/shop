package cn.cy.mobilegames.yyjia.forelders.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.model.Subject;
import cn.cy.mobilegames.yyjia.forelders.util.ImageLoaderUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

public class SubjectAdapter extends BaseAdapter {
	Context mContext;
	List<Object> subList;
	ImageLoaderUtil imageUtil;
	Viewholder holder = null;
	Subject sub;

	public SubjectAdapter(Context context, List<Object> lists,
			ImageLoaderUtil loader) {
		super();
		this.mContext = context;
		this.subList = lists;
		this.imageUtil = loader;
	}

	@Override
	public int getCount() {
		return subList.size() == 0 ? 0 : subList.size();
	}

	@Override
	public Object getItem(int position) {
		return subList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	public void reSetTv(int position) {
		if (holder != null && sub != null) {
			try {
				holder.focusTv
						.setText(Constants.FOCUS_CN
								+ (Integer.valueOf(((Subject) subList
										.get(position)).viewnum) + 1)
								+ Constants.RIGHT_BRACKET);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	};

	@SuppressLint("InflateParams")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.subject_show,
					null);
			holder = new Viewholder();
			holder.breifSumTv = (TextView) view
					.findViewById(R.id.sub_breif_summary);
			holder.focusTv = (TextView) view.findViewById(R.id.subject_focus);
			holder.subTitle = (TextView) view.findViewById(R.id.subject_title);
			holder.subAdImg = (ImageView) view
					.findViewById(R.id.subject_advert);
			holder.subSeeMoreImg = (ImageView) view
					.findViewById(R.id.sub_see_more);
			view.setTag(holder);
		} else {
			holder = (Viewholder) view.getTag();
		}
		sub = (Subject) subList.get(position);
		// holder.subSeeMoreImg.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// mContext.startActivity(new Intent(mContext,
		// QualitySubjectContentActivity.class).putExtra(
		// Constants.SUBJECT, sub));
		// }
		// });
		holder.breifSumTv.setText(Utils.checkEmpty(sub.summary));

		holder.focusTv.setText(Constants.FOCUS_CN + sub.viewnum
				+ Constants.RIGHT_BRACKET);
		holder.subAdImg.setBackgroundColor(mContext.getResources().getColor(
				R.color.gray_6e));
		imageUtil.setImageNetResource(holder.subAdImg, sub.subjectpic,
				R.drawable.img_default);
		holder.subTitle.setText(Utils.checkEmpty(sub.subjectname));
		return view;
	}

	static class Viewholder {
		TextView subTitle, focusTv, breifSumTv;
		ImageView subAdImg, subSeeMoreImg;
	}

}
