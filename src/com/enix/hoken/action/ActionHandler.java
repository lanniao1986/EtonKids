package com.enix.hoken.action;

import com.enix.hoken.R;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.SinfoSettingActivity;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.custom.receiver.AlarmReceiver;
import com.enix.hoken.util.CommonUtil;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;

public class ActionHandler {
	private MainActivity mActvity;
	private boolean isDeleted = false;// 删除文件时的状态标示
	public static final int INTENT_ACTIVITY_TRANSPOT = 0;
	public static final int INTENT_PICTURE_PICKER = 1;
	public static final int INTENT_IMAGE_CAPTURE = 2;
	public static final int INTENT_IMAGE_CROP = 3;
	public static final int INTENT_MESSAGE_SEND = 4;
	public static final int INTENT_DIAL_CALL = 5;

	public static final int REQUEST_FOR_AVATAR_UPDATE = 1;
	public static final int REQUEST_FOR_SCHEDULE_UPDATE = 2;
	public static final int REQUEST_FOR_MEMBER_EDIT = 3;// 请求返回编辑模式结果
	public static final int REQUEST_FOR_MEMBER_CREATE = 4;// 请求返回新建模式结果
	public static final int RESULT_FOR_MEMBER_DELETE = 5;// 请求返回删除成员结果
	public static final int RESULT_FOR_SCHEDULE_DELETE = 6;
	public static final int RESULT_FOR_AVATER_CREATE = 7;

	public ActionHandler(MainActivity mActvity) {
		this.mActvity = mActvity;
	}

	/**
	 * 从MainActivity中获取的onActivityResult
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void activityResultHandler(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == MainActivity.RESULT_OK) {
			mActvity.onResultSucess(requestCode, data);
		} else {
			mActvity.onResultNotOK(requestCode, resultCode, data);
		}
	}

	/**
	 * 创建指定时间后闹铃Intent
	 * 
	 * @param alarmTime
	 * @return
	 */
	public Intent createAlarmIntent(long alarmTime) {
		Intent intent = new Intent(mActvity, AlarmReceiver.class);
		intent.setAction("short");
		PendingIntent sender = PendingIntent.getBroadcast(mActvity, 0, intent,
				0);
		AlarmManager alarm = (AlarmManager) mActvity
				.getSystemService(MainActivity.ALARM_SERVICE);
		alarm.set(AlarmManager.RTC_WAKEUP, alarmTime, sender);
		return intent;
	}

	/**
	 * 创建打开外部浏览器的INTENT
	 * 
	 * @param url
	 *            URL网址
	 * @return
	 */
	public Intent createBrowserIntent(String url) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.VIEW");
		if (url != null) {
			Uri content_url = Uri.parse(url);
			intent.setData(content_url);
			return intent;
		} else {
			return null;
		}

	}

	/**
	 * 创建Activity跳转用 Intent
	 * 
	 * @param packageContext
	 * @param cls
	 * @param mBundle
	 * @return
	 */
	public Intent createTranspotIntent(Class<?> cls, Bundle mBundle) {
		if (mActvity != null) {
			Intent intent = new Intent(mActvity, cls);
			if (mBundle != null)
				intent.putExtras(mBundle);
			return intent;
		} else {
			return null;
		}
	}

	/**
	 * 跳转ACTIVITY
	 * 
	 * @param intent
	 */
	public void startIntent(Intent intent) {
		if (intent != null) {
			mActvity.startActivity(intent);
			mActvity.overridePendingTransition(R.anim.push_up_in,
					R.anim.push_up_out);
		} else {
			Log.e("DEBUG", "startIntent : NO_INTENT_CREATED");
		}
	}

	public void startNetWorkSettingIntent() {
		mActvity.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));// 进入无线网络设置界面
		mActvity.overridePendingTransition(R.anim.push_up_in,
				R.anim.push_up_out);
	}

	/**
	 * 跳转ACTIVITY 返回结果
	 * 
	 * @param intent
	 * @param requestCode
	 */
	public void startIntentForResult(Intent intent, int requestCode) {
		if (intent != null) {
			mActvity.startActivityForResult(intent, requestCode);
			mActvity.overridePendingTransition(R.anim.push_up_in,
					R.anim.push_up_out);
		}
	}

	/**
	 * 创建查看图片用Intent
	 * 
	 * @param imageUri
	 *            图片路径URI
	 * @return
	 */
	public Intent createPicturePickerIntent(Uri imageUri) {
		if (imageUri != null) {
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action
			intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
			return intent;
		} else {
			return null;
		}
	}

	/**
	 * 创建图片裁剪用Intent
	 * 
	 * @param uri
	 *            图片路径
	 * @param isFreeMode
	 *            是否自定义图片大小
	 * @return
	 */
	public Intent createImageCropIntent(Uri uri, boolean isFreeMode) {
		if (uri != null) {
			Intent intent = new Intent("com.android.camera.action.CROP");
			intent.setDataAndType(uri, "image/*");
			intent.putExtra("crop", "true");
			if (!isFreeMode) {
				intent.putExtra("aspectX", 1);
				intent.putExtra("aspectY", 1);
				intent.putExtra("scale", true);
				intent.putExtra("outputX", 320);
				intent.putExtra("outputY", 320);
			}
			intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			intent.putExtra("return-data", false);
			intent.putExtra("outputFormat",
					Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("noFaceDetection", true); // no face detection
			return intent;
		} else {
			return null;
		}
	}

	/**
	 * 删除指定文件或文件夹
	 * 
	 * @param filePath
	 *            文件或文件夹路径
	 * @param confirm
	 *            是否弹出确认框
	 * @return 是否成功删除
	 */
	public boolean fileDelete(final String filePath, boolean confirm) {
		isDeleted = false;
		if (confirm) {
			CommonUtil.showDialog("确定删除" + CommonUtil.getFileNameNoEx(filePath)
					+ "吗?", new String[] { "确定", "取消" }, mActvity,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == 0) {
								if (CommonUtil.del(filePath, mActvity))
									isDeleted = true;
							}
						}
					});
			return isDeleted;
		} else {
			return CommonUtil.del(filePath, mActvity);
		}
	}

}
