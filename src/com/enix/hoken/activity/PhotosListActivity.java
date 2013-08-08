package com.enix.hoken.activity;

import java.util.Iterator;

import android.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.BitmapDrawable;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import android.widget.TextView;
import android.widget.Toast;

import com.enix.hoken.common.*;
import com.enix.hoken.custom.adapter.MenuPopBlackAdapter;
import com.enix.hoken.custom.adapter.PhotosListAdapter;
import com.enix.hoken.info.Sinfo;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.R;

public class PhotosListActivity extends Activity {
	private BaseApplication mApplication;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mShare;
	private ImageView mMenu;
	private TextView mAlbumName;
	private GridView mGridView;
	private PhotosListAdapter mAdapter;

	private Sinfo sinfo;
	private PopupWindow mMenuPopupWindow;
	private View mMenuView;
	private ListView mMenuListView;
	private String[] mMenuName = { "编辑", "全选", "删除" };
	private final int RESULT_CAMERA = 2;// 从相机设备导入
	private final int RESULT_GARLLY = 1;// 从本地相册导入
	public static final String INTENT_PARAM_SID = "SID";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photoslist);
		mApplication = (BaseApplication) getApplication();
		mMenuView = LayoutInflater.from(this).inflate(
				R.layout.menu_popupwindow_black, null);
		mMenuPopupWindow = new PopupWindow(mMenuView,
				mApplication.getScreenWidth() / 2, LayoutParams.WRAP_CONTENT,
				true);
		mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		findViewById();
		setListener();
		init();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {

		}
	}

	private void findViewById() {
		mBack = (ImageView) findViewById(R.id.photoslist_back);
		mTitle = (TextView) findViewById(R.id.photoslist_title);
		mShare = (ImageView) findViewById(R.id.photoslist_share);
		mMenu = (ImageView) findViewById(R.id.photoslist_menu);
		mAlbumName = (TextView) findViewById(R.id.photoslist_albumname);
		mGridView = (GridView) findViewById(R.id.photoslist_gridview);
		mMenuListView = (ListView) mMenuView
				.findViewById(R.id.menu_pop_black_list);
	}

	private void setListener() {
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				finish();
				overridePendingTransition(0, R.anim.roll_down);
			}
		});
		mShare.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CommonUtil.showDialog("导入照片",
						new String[] { "本地相册导入", "拍照导入" },
						PhotosListActivity.this,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Intent intent;
								if (which == 0) {
									intent = new Intent(
											Intent.ACTION_PICK,
											android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
									startActivityForResult(intent,
											RESULT_GARLLY);
								} else {

									intent = new Intent(
											MediaStore.ACTION_IMAGE_CAPTURE);
									startActivityForResult(intent,
											RESULT_CAMERA);

								}
							}
						});
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				initMorePopupWindow();
			}
		});
		mGridView.setOnScrollListener(new OnScrollListener() {

			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// if (view.getLastVisiblePosition() == view.getCount() - 1
				// && mRefresh == false && mIsOver == false) {
				// mRefresh = true;
				// mGridView.setSelection(mGridView.getCount());
				// mPage++;
				// mIsAdd = true;
				// mRefreshBar.setVisibility(View.VISIBLE);
				// getPhotos();
				// }
				// if (scrollState == SCROLL_STATE_FLING
				// || scrollState == SCROLL_STATE_TOUCH_SCROLL) {
				// mAlbumName.setVisibility(View.GONE);
				// }
				// if (scrollState == SCROLL_STATE_IDLE
				// && view.getFirstVisiblePosition() == 0) {
				// mAlbumName.setVisibility(View.VISIBLE);
				// }

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		mGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent();
				intent.setClass(PhotosListActivity.this,
						PhotosDetailActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("AlbumPath",
						CommonUtil.getStudentAlbumPath(PhotosListActivity.this)
								+ sinfo.getId() + "/");
				startActivity(intent);
				overridePendingTransition(R.anim.roll_up, R.anim.roll);
			}
		});
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuListView.setOnItemClickListener(new OnItemClickListener() {

					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						mMenuPopupWindow.dismiss();
						switch (position) {
						case 0:
							Toast.makeText(PhotosListActivity.this,
									"position = " + position,
									Toast.LENGTH_SHORT).show();
							break;

						case 1:
							Toast.makeText(PhotosListActivity.this,
									"position = " + position,
									Toast.LENGTH_SHORT).show();
							break;

						case 2:
							Toast.makeText(PhotosListActivity.this,
									"position = " + position,
									Toast.LENGTH_SHORT).show();
							break;

						case 3:
							Toast.makeText(PhotosListActivity.this,
									"position = " + position,
									Toast.LENGTH_SHORT).show();
							break;
						}
					}
				});
			}
		});
	}

	private void init() {
		sinfo = getSinfoById(getIntent().getExtras().getInt(INTENT_PARAM_SID, -1));
		mAlbumName.setVisibility(View.GONE);
		// mAlbumName.setText(mName + "(" + mCount + ")");
		// 以学生ID为标识获取文件夹内图片
		mAdapter = new PhotosListAdapter(mApplication, this,
				CommonUtil.getStudentAlbumPath(this) + sinfo.getId() + "/");
		if (sinfo != null) {
			mTitle.setText(sinfo.getS_name() + "的照片(" + mAdapter.getCount()
					+ "张)");
		}
		mGridView.setAdapter(mAdapter);
		getPhotos();
	}

	private Sinfo getSinfoById(int sid) {
		Sinfo target = null;
		for (Iterator<Sinfo> iter = mApplication.getSinfoList().iterator(); iter
				.hasNext();) {
			target = iter.next();
			if (target.getId() == sid) {
				return target;
			}
		}
		return target;
	}

	private void getPhotos() {
		handler.sendEmptyMessage(1);
	}

	Handler handler = new Handler() {

		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;

			case 1:
				if (mAdapter.getCount() == 0) {
					CommonUtil.showShortToast(PhotosListActivity.this,
							"该相册下没有任何照片");
				}
				mAdapter.notifyDataSetChanged();
				break;
			}
		}
	};

	private void initMorePopupWindow() {
		MenuPopBlackAdapter adapter = new MenuPopBlackAdapter(this, mMenuName);
		mMenuListView.setAdapter(adapter);
		if (mMenuPopupWindow == null) {
			mMenuPopupWindow = new PopupWindow(mMenuView,
					mApplication.getScreenWidth() / 2,
					LayoutParams.WRAP_CONTENT, true);
			mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mMenuPopupWindow.isShowing()) {
			mMenuPopupWindow.dismiss();
		} else {
			mMenuPopupWindow.showAsDropDown(mMenu, 0, 0);
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
