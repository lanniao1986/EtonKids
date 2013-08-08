package com.enix.hoken.custom.adapter;


import java.util.ArrayList;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.enix.hoken.common.*;
import com.enix.hoken.custom.item.LocalImageLoaderTask;
import com.enix.hoken.util.CommonUtil;

public class PhotosListAdapter extends BaseAdapter {
	private BaseApplication mApplication;
	private Context mContext;
	private ArrayList mPhotoList;
	public PhotosListAdapter(BaseApplication application, Context context,
			String path) {
		mApplication = application;
		mContext = context;
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

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView mImageView = null;
		if (convertView == null) {
			mImageView = new ImageView(mContext);	
			mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);			
			LocalImageLoaderTask task = new LocalImageLoaderTask(mImageView,mApplication.getScreenWidth() / 3);
			task.execute(mPhotoList.get(position).toString());
		} else {
			mImageView = (ImageView) convertView;
		}
		return mImageView;
	}

	
}
