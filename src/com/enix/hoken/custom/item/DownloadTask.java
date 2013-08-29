package com.enix.hoken.custom.item;

import com.enix.hoken.custom.item.DownloadManager.DownloadStateChangedListener;
import com.enix.hoken.info.Dinfo;
import com.enix.hoken.util.CommonUtil;

public class DownloadTask {

	private Dinfo mDinfo;

	private DownloadManager mDownloadManager;
	private DownloadStateChangedListener listener;

	/**
	 * 新建的下载记录对象构造方法
	 * 
	 * @param url
	 * @param fileName
	 * @param listener
	 */
	public DownloadTask(String url, String fileName, String imgurl,
			DownloadStateChangedListener listener) {
		this.listener = listener;
		mDinfo = new Dinfo(url, fileName, imgurl);
		mDownloadManager = new DownloadManager(mDinfo, listener);
	}

	/**
	 * 从数据库获取的下载记录对象构造方法
	 * 
	 * @param mDinfo
	 * @param listener
	 */
	public DownloadTask(Dinfo mDinfo, DownloadStateChangedListener listener) {
		this.mDinfo = mDinfo;
		this.listener = listener;
		mDownloadManager = new DownloadManager(mDinfo, listener);
	}

	public void resetDinfoState() {
		if (mDinfo.getState() != DownloadManager.COMPLETE)
			mDinfo.setState(DownloadManager.PAUSE);
	}

	public void start() {
		if (mDinfo != null) {
			mDownloadManager.startDownloadTask();
		}
	}

	public void pause() {
		mDownloadManager.pauseDownloadTask();
	}

	public void resume() {
		mDownloadManager.resumeDownloadTask();
	}

	public void restart() {
		mDownloadManager.restartDownloadTask();
	}

	public Dinfo getmDinfo() {
		return mDinfo;
	}

	public void setmDinfo(Dinfo mDinfo) {
		this.mDinfo = mDinfo;
	}
}
