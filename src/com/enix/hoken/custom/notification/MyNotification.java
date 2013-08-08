package com.enix.hoken.custom.notification;

import com.enix.hoken.R;
import com.enix.hoken.util.CommonUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

public class MyNotification {
	private NotificationManager mNotificationManager;
	private Context mContext;
	private Intent mIntent;
	private CharSequence message = "我的伊顿";
	private PendingIntent mPendingIntent;
	private Notification.Builder mBuilder;
	private Notification notification;
	public static final int NOTIFICATION_MUSIC_PLAY = 1;
	public static final int NOTIFICATION_MUSIC_PAUSE = 2;
	public static final int NOTIFICATION_MUSIC_STOP = 3;
	private String mTicker;
	private String mTitle;
	private String mContent;

	public NotificationManager getmNotificationManager() {
		return mNotificationManager;
	}

	public void setmNotificationManager(NotificationManager mNotificationManager) {
		this.mNotificationManager = mNotificationManager;
	}

	public Context getmContext() {
		return mContext;
	}

	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}

	public Intent getmIntent() {
		return mIntent;
	}

	public void setmIntent(Intent mIntent) {
		this.mIntent = mIntent;
	}

	public PendingIntent getmPendingIntent() {
		return mPendingIntent;
	}

	public void setmPendingIntent(PendingIntent mPendingIntent) {
		this.mPendingIntent = mPendingIntent;
	}

	public Notification.Builder getmBuilder() {
		return mBuilder;
	}

	public void setmBuilder(Notification.Builder mBuilder) {
		this.mBuilder = mBuilder;
	}

	public Notification getNotification() {
		return notification;
	}

	public void setNotification(Notification notification) {
		this.notification = notification;
	}

	public String getmTicker() {
		return mTicker;
	}

	public void setmTicker(String mTicker) {
		this.mTicker = mTicker;
	}

	public String getmTitle() {
		return mTitle;
	}

	public void setmTitle(String mTitle) {
		this.mTitle = mTitle;
	}

	public String getmContent() {
		return mContent;
	}

	public void setmContent(String mContent) {
		this.mContent = mContent;
	}

	public MyNotification(Context mContext, Class<?> cls) {
		this.mContext = mContext;
		mNotificationManager = (NotificationManager) mContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mIntent = new Intent(mContext, cls);
		mTitle = "TITLE";
		mContent = "Content";
		mTicker = "TICKER";
	}

	public void startNotification() {

		mPendingIntent = PendingIntent.getActivity(mContext, 0, mIntent, 0);
		// Builder方式创建 仅在API 11版本后可用
		mBuilder = new Notification.Builder(mContext);
		mBuilder.setLargeIcon(
				CommonUtil.getBitmapByResId(mContext, R.drawable.eton_icon))
				.setSmallIcon(R.drawable.eton_icon_s)
				.setTicker(mTicker)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(mTitle)
				.setContentText(mContent)
				.setContentIntent(
						PendingIntent.getActivity(mContext, 0, mIntent, 0))
				.setAutoCancel(true);// 可否点击后自动关闭该通知

		Notification notification = mBuilder.getNotification();
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(NOTIFICATION_MUSIC_PLAY, notification);
	}
}
