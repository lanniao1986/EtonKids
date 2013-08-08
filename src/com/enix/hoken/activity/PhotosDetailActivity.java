package com.enix.hoken.activity;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.enix.hoken.common.*;
import com.enix.hoken.custom.item.*;
import com.enix.hoken.util.*;
import com.enix.hoken.R;

public class PhotosDetailActivity extends Activity {
	private BaseApplication mApplication;
	private LinearLayout mTop;
	private ImageView mBack;
	private TextView mTitle;
	private ImageView mShare;
	private ImageView mMenu;
	private LinearLayout mBottom;
	private PhotosGallery mGallery;
	private PhotosGalleryAdapter mAdapter;

	public static int mScreenWidth;
	public static int mScreenHeight;

	private String mAlbumName;
	private int mCurrentItem;
	private int mTotalCount;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.photosdetail);
		mApplication = (BaseApplication) getApplication();
		Display display = getWindowManager().getDefaultDisplay();
		mScreenWidth = display.getWidth();
		mScreenHeight = display.getHeight();
		findViewById();
		setListener();
		init();
	}

	private void findViewById() {
		mTop = (LinearLayout) findViewById(R.id.photosdetail_top);
		mBack = (ImageView) findViewById(R.id.photosdetail_back);
		mTitle = (TextView) findViewById(R.id.photosdetail_title);
		mShare = (ImageView) findViewById(R.id.photosdetail_share);
		mMenu = (ImageView) findViewById(R.id.photosdetail_menu);
		mBottom = (LinearLayout) findViewById(R.id.photosdetail_bottom);
		mGallery = (PhotosGallery) findViewById(R.id.photosdetail_gallery);
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
				Toast.makeText(PhotosDetailActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});
		mMenu.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Toast.makeText(PhotosDetailActivity.this, "暂时无法提供此功能",
						Toast.LENGTH_SHORT).show();
			}
		});

		mGallery.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (mTop.isShown() && mBottom.isShown()) {
					Animation anim = AnimationUtils.loadAnimation(
							PhotosDetailActivity.this, R.anim.fade_out);
					mTop.setAnimation(anim);
					mBottom.setAnimation(anim);
					mTop.setVisibility(View.GONE);
					mBottom.setVisibility(View.GONE);
				} else {
					Animation anim = AnimationUtils.loadAnimation(
							PhotosDetailActivity.this, R.anim.fade_in);
					mTop.setAnimation(anim);
					mBottom.setAnimation(anim);
					mTop.setVisibility(View.VISIBLE);
					mBottom.setVisibility(View.VISIBLE);
				}
			}
		});
		mGallery.setOnItemSelectedListener(new OnItemSelectedListener() {

			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				mCurrentItem = position;
				setValueToView();
			}

			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}

	private void init() {
		Intent intent = getIntent();
		mCurrentItem = intent.getIntExtra("position", 0);
		mAlbumName = intent.getStringExtra("AlbumPath");

		mAdapter = new PhotosGalleryAdapter(PhotosDetailActivity.this,
				mAlbumName);
		mGallery.setVerticalFadingEdgeEnabled(false);
		mGallery.setHorizontalFadingEdgeEnabled(false);
		mGallery.setAdapter(mAdapter);
		mGallery.setSelection(mCurrentItem);
	}

	private void setValueToView() {
		// mTitle.setText((mCurrentItem + 1) + "/" + mTotalCount);
		// mViewCount.setText("浏览"
		// + RenRenData.mPhotosResults.get(mCurrentItem).getView_count());
		// mCommentCount.setText(RenRenData.mPhotosResults.get(mCurrentItem)
		// .getComment_count() + "");
		// if (RenRenData.mPhotosResults.get(mCurrentItem).getCaption() == null
		// || RenRenData.mPhotosResults.get(mCurrentItem).getCaption()
		// .length() == 0) {
		// mCaption.setVisibility(View.GONE);
		// } else {
		// mCaption.setVisibility(View.VISIBLE);
		// mCaption.setText(RenRenData.mPhotosResults.get(mCurrentItem)
		// .getCaption());
		// }
	}

	public class PhotosGalleryAdapter extends BaseAdapter {
		private Context mContext;
		private AsyncImageLoader mLoader;
		private ArrayList mPhotoList;

		PhotosGalleryAdapter(Context context, String path) {
			mContext = context;
			mLoader = new AsyncImageLoader();
			mPhotoList = CommonUtil.getFilesByPath(path,1);
		}

		public int getCount() {
			return mPhotoList.size();
		}

		public Object getItem(int position) {
			return mPhotoList.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, final View convertView,
				ViewGroup parent) {
			PhotosImageView view = null;
			if (convertView == null) {
				view = new PhotosImageView(mContext);
				view.setLayoutParams(new Gallery.LayoutParams(mScreenWidth,
						mScreenHeight));
			} else {
				view = (PhotosImageView) convertView;
			}
			Bitmap bitmap = BitmapCache.getInstance().getBitmap(
					mPhotoList.get(position).toString(), true);

			if (bitmap == null) {
				CommonUtil.showShortToast(mContext, "加载图片失败: (");
			} else {
				float scale = getScale(bitmap);
				int bitmapWidth = (int) (bitmap.getWidth() * scale);
				int bitmapHeight = (int) (bitmap.getHeight() * scale);

				Bitmap zoomBitmap = Bitmap.createScaledBitmap(bitmap,
						bitmapWidth, bitmapHeight, true);
				view.setImageWidth(bitmapWidth);
				view.setImageHeight(bitmapHeight);
				view.setImageBitmap(zoomBitmap);
			}
			return view;
		}
	}

	private float getScale(Bitmap bitmap) {
		float scaleWidth = mScreenWidth / (float) bitmap.getWidth();
		float scaleHeight = mScreenHeight / (float) bitmap.getHeight();
		return Math.min(scaleWidth, scaleHeight);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(0, R.anim.roll_down);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
