package com.enix.hoken.custom.item;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;

import com.enix.hoken.util.BitmapCache;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class LocalImageLoaderTask extends AsyncTask<String, Void, Bitmap> {
private int itemWidth;
	private TaskParam param;
	private final WeakReference<ImageView> imageViewReference; // 防止内存溢出

	public LocalImageLoaderTask(ImageView imageView,int itemWidth) {
		imageViewReference = new WeakReference<ImageView>(imageView);
		this.itemWidth = itemWidth;
	}	
	/**
	 * 获取内存卡中图片文件
	 * 
	 * @param filename
	 * @return
	 */
	private Bitmap loadImageFile(String filename) {
		try {
			Bitmap bmp = BitmapCache.getInstance().getBitmap(filename,false);
			return bmp;
		} catch (Exception e) {
			return null;
		}
	}

	protected void onPostExecute(Bitmap bitmap) {
		if (isCancelled()) {
			bitmap = null;
		}
		if (imageViewReference != null) {
			ImageView imageView = imageViewReference.get();
			if (imageView != null) {
				if (bitmap != null) {
					int width = bitmap.getWidth();// 获取真实宽高
					int height = bitmap.getHeight();
					LayoutParams lp = imageView.getLayoutParams();
					lp.height = (height * itemWidth) / width;// 调整高度
					imageView.setLayoutParams(lp);
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}

	/**
	 * 后台处理加载内存卡本地图片
	 * 
	 * @param fileName
	 * @return
	 */
	protected Bitmap doInBackground(String... params) {
		
		return loadImageFile(params[0]);
	
	}
}