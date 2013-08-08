package com.enix.hoken.custom.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.common.*;
import com.enix.hoken.custom.adapter.DesktopAdapter;
import com.enix.hoken.info.Tinfo;
import com.enix.hoken.R;
import com.enix.hoken.util.CommonUtil;

public class Desktop {
	private BaseApplication mApplication;
	private MainActivity mAcitivty;
	private View mDesktop;
	private LinearLayout mInformation;
	private ImageView mAvatar;
	private TextView mName;
	private ExpandableListView mDisplay;
	private List<Map<String, Object>> mGroup = new ArrayList<Map<String, Object>>();
	private List<List<Map<String, Object>>> mChild = new ArrayList<List<Map<String, Object>>>();
	private String[] mGroupName;
	private String[] mChildFavorite;
	private int[] mChildFavoriteIcon;
	private String[] mChildAction;
	private int[] mChildActionIcon;
	public static int mChooesId = 0;
	private DesktopAdapter mAdapter;
	private Tinfo tinfo;
	private AppDataManager appdata;
	final ContentValues cv = new ContentValues();
	private onChangeViewListener mOnChangeViewListener;

	public Desktop(MainActivity mAcitivty) {
		mApplication = mAcitivty.mApplication;
		this.mAcitivty = mAcitivty;
		mDesktop = LayoutInflater.from(mAcitivty).inflate(R.layout.lay_desktop,
				null);
		findViewById();
		appdata = new AppDataManager(mAcitivty);
		init();
		setListener();

	}

	private void findViewById() {
		mInformation = (LinearLayout) mDesktop
				.findViewById(R.id.desktop_top_layout);
		mAvatar = (ImageView) mDesktop.findViewById(R.id.desktop_top_avatar);
		mName = (TextView) mDesktop.findViewById(R.id.desktop_top_name);
		mDisplay = (ExpandableListView) mDesktop
				.findViewById(R.id.desktop_list);
	}

	private void setListener() {
		mInformation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mChooesId = -1;
				mAdapter.notifyDataSetChanged();
				if (mOnChangeViewListener != null) {
					mOnChangeViewListener.onChangeView(MainActivity.MYHOMEPAGE);
				}
			}
		});
		mDisplay.setOnGroupClickListener(new OnGroupClickListener() {

			public boolean onGroupClick(ExpandableListView parent, View v,
					int groupPosition, long id) {
				return true;
			}
		});
		// 侧边菜单子项被点击时触发事件
		mDisplay.setOnChildClickListener(new OnChildClickListener() {

			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				if (groupPosition == 0) {
					mChooesId = childPosition;
					mAdapter.notifyDataSetChanged();
					if (mOnChangeViewListener != null) {
						switch (childPosition) {
						case 0:
							mOnChangeViewListener
									.onChangeView(MainActivity.MYHOMEPAGE);
							break;

						case 1:
							mOnChangeViewListener
									.onChangeView(MainActivity.CLASS_MAMBER);

							break;
						case 2:
							// mOnChangeViewListener.onChangeView(MainActivity.Chat);
							Toast.makeText(
									mAcitivty,
									mAcitivty
											.getResources()
											.getStringArray(
													R.array.desktop_list_item_favorite_strings)[2]
											+ "被点击了", Toast.LENGTH_SHORT)
									.show();
							break;
						case 3:
							// mOnChangeViewListener
							// .onChangeView(MainActivity.Friends);
							Toast.makeText(
									mAcitivty,
									mAcitivty
											.getResources()
											.getStringArray(
													R.array.desktop_list_item_favorite_strings)[3]
											+ "被点击了", Toast.LENGTH_SHORT)
									.show();
							break;
						case 4:
							mOnChangeViewListener
									.onChangeView(MainActivity.PUBLIC_RSS);
							break;
						case 5:
							// mOnChangeViewListener
							// .onChangeView(MainActivity.Location);
							Toast.makeText(
									mAcitivty,
									mAcitivty
											.getResources()
											.getStringArray(
													R.array.desktop_list_item_favorite_strings)[5]
											+ "被点击了", Toast.LENGTH_SHORT)
									.show();
							break;
						case 6:
							// mOnChangeViewListener
							// .onChangeView(MainActivity.Search);
							Toast.makeText(
									mAcitivty,
									mAcitivty
											.getResources()
											.getStringArray(
													R.array.desktop_list_item_favorite_strings)[6]
											+ "被点击了", Toast.LENGTH_SHORT)
									.show();
							break;
						case 7:
							mOnChangeViewListener
									.onChangeView(MainActivity.DOWNLOAD_CENTER);

							break;
						}
					}
				} else {
					switch (childPosition) {
					case 0:
						// mAcitivty.startActivity(new Intent(mAcitivty,
						// Settings.class));
						break;

					case 1:
						Toast.makeText(mAcitivty, "因无登录界面,所以暂时无操作",
								Toast.LENGTH_SHORT).show();
						break;
					}
				}
				return true;
			}
		});
	}

	public void init() {
		init_Data();
		tinfo = (Tinfo) appdata.getInfoFromApp(InfoSession.TINFO);
		mName.setText(tinfo.getT_name());
		BitmapDrawable bitmap = CommonUtil.getAvatar(mAcitivty,
				tinfo.getT_head());
		if (bitmap != null) {
			mAvatar.setImageDrawable(bitmap);
		} else {
			mAvatar.setImageDrawable(mAcitivty.getResources().getDrawable(
					R.drawable.default_avtar));
		}
		mAdapter = new DesktopAdapter(mAcitivty, mGroup, mChild);
		mDisplay.setAdapter(mAdapter);
		for (int i = 0; i < mGroup.size(); i++) {
			mDisplay.expandGroup(i);
		}
	}

	private void init_Data() {
		mGroupName = mAcitivty.getResources().getStringArray(
				R.array.desktop_list_head_strings);
		mChildFavorite = mAcitivty.getResources().getStringArray(
				R.array.desktop_list_item_favorite_strings);
		mChildAction = mAcitivty.getResources().getStringArray(
				R.array.desktop_list_item_action_strings);
		mChildFavoriteIcon = new int[8];
		mChildActionIcon = new int[2];
		mChildFavoriteIcon[0] = R.drawable.desktop_list_newsfeed;
		mChildFavoriteIcon[1] = R.drawable.desktop_list_friends;
		mChildFavoriteIcon[2] = R.drawable.desktop_list_chat;
		mChildFavoriteIcon[3] = R.drawable.desktop_list_photo;
		mChildFavoriteIcon[4] = R.drawable.desktop_list_page;
		mChildFavoriteIcon[5] = R.drawable.desktop_list_search;
		mChildFavoriteIcon[6] = R.drawable.desktop_list_search;
		mChildFavoriteIcon[7] = R.drawable.desktop_list_apps_center;
		mChildActionIcon[0] = R.drawable.desktop_list_settings;
		mChildActionIcon[1] = R.drawable.desktop_list_log_out;
		getGroupList();
		getChildList();
	}

	private void getGroupList() {
		for (int i = 0; i < mGroupName.length; i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("name", mGroupName[i]);
			mGroup.add(map);
		}
	}

	private void getChildList() {
		for (int i = 0; i < mGroupName.length; i++) {
			if (i == 0) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < mChildFavorite.length; j++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("icon", mChildFavoriteIcon[j]);
					map.put("name", mChildFavorite[j]);
					map.put("click", false);
					list.add(map);
				}
				mChild.add(list);
			} else if (i == 1) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				for (int j = 0; j < mChildAction.length; j++) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("icon", mChildActionIcon[j]);
					map.put("name", mChildAction[j]);
					map.put("click", false);
					list.add(map);
				}
				mChild.add(list);
			}
		}
		// 默认选择常用组第一项
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("icon", mChildFavoriteIcon[0]);
		map.put("name", mChildFavorite[0]);
		map.put("click", true);
		mChild.get(0).set(0, map);
	}

	public View getView() {
		return mDesktop;
	}

	public interface onChangeViewListener {
		public abstract void onChangeView(int arg0);
	}

	public void setOnChangeViewListener(
			onChangeViewListener onChangeViewListener) {
		mOnChangeViewListener = onChangeViewListener;
	}

}
