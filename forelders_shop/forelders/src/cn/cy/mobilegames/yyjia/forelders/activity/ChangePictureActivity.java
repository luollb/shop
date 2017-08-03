package cn.cy.mobilegames.yyjia.forelders.activity;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.cy.mobilegames.yyjia.forelders.Constants;
import cn.cy.mobilegames.yyjia.forelders.util.ToastUtil;
import cn.cy.mobilegames.yyjia.forelders.util.Utils;
import cn.cy.mobilegames.yyjia.forelders.R;

import com.umeng.analytics.MobclickAgent;

/**
 * 更换头像
 */
@SuppressLint({ "InflateParams" })
public class ChangePictureActivity extends BaseActivity {
	private ImageView pic, backBtn, searchBtn;
	private Button saveNowBtn;
	private GridView picGridView;
	private TextView navTitle;
	private int[] picResource = new int[] { R.drawable.default_img0,
			R.drawable.default_img1, R.drawable.default_img2,
			R.drawable.default_img3, R.drawable.default_img4,
			R.drawable.default_img5, R.drawable.default_img7,
			R.drawable.default_img8, R.drawable.default_img9 };
	private ArrayList<Integer> imageLists;
	private int resId = -1;

	private class ViewHolder {
		private ImageView img;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(getClass().getName()); // 统计页面
		MobclickAgent.onResume(this); // 统计时长
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(getClass().getName()); // 保证 onPageEnd 在onPause
														// 之前调用,因为 onPause
														// 中会保存信息
		MobclickAgent.onPause(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_user_pic);
		imageLists = new ArrayList<Integer>();
		for (int i = 0; i < picResource.length; i++) {
			imageLists.add(picResource[i]);
		}
		initView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void initView() {
		backBtn = (ImageView) findViewById(R.id.back_btn);
		searchBtn = (ImageView) findViewById(R.id.menu_img);
		navTitle = (TextView) findViewById(R.id.title);
		navTitle.setText(Constants.CHANGE_PICTURE);
		pic = (ImageView) findViewById(R.id.pic_bg);
		saveNowBtn = (Button) findViewById(R.id.save_now_btn);
		picGridView = (GridView) findViewById(R.id.pic_gridview_layout);
		picGridView.setAdapter(new picAdapter(imageLists));

		picGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

			}
		});
		saveNowBtn.setOnClickListener(onclick);
		backBtn.setOnClickListener(onclick);
		searchBtn.setOnClickListener(onclick);
	}

	private class picAdapter extends BaseAdapter {
		private ArrayList<Integer> lists;

		private picAdapter(ArrayList<Integer> lists) {
			super();
			this.lists = lists;
		}

		@Override
		public int getCount() {

			return lists == null ? 0 : lists.size();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(final int position, View convertview,
				ViewGroup parent) {
			View view = convertview;
			ViewHolder viewHolder = null;
			if (convertview == null) {
				view = LayoutInflater.from(ChangePictureActivity.this).inflate(
						R.layout.pic_preview, null);
				viewHolder = new ViewHolder();
				viewHolder.img = (ImageView) view
						.findViewById(R.id.pic_gridview_detail_img);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			// resId = lists.get(0);
			viewHolder.img.setImageResource(lists.get(position));
			viewHolder.img.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					pic.setBackgroundResource(lists.get(position));
					resId = lists.get(position);
				}
			});
			return view;
		}

	}

	/**
	 * onclick's event
	 */
	private OnClickListener onclick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.save_now_btn:
				// to save now
				Intent intent = new Intent(ChangePictureActivity.this,
						MainActivity.class);
				if (resId >= 0) {
					intent.putExtra(Constants.KEY_SYSTEM_PICTURE, resId);
					setResult(Activity.RESULT_OK, intent);
				} else {
					ToastUtil.showShortToast(context, "您还没选择头像");
				}
				finish();
				break;
			case R.id.back_btn:
				finish();
				break;
			case R.id.menu_img:
				Utils.toOtherClass(ChangePictureActivity.this,
						SearchActivity.class);
				break;
			default:
				break;
			}
		}

	};

	@Override
	public void onSuccess(int method, Object obj) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onError(int method, int statusCode) {
		// TODO Auto-generated method stub

	}
}
