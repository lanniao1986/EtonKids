package com.enix.hoken.custom.item;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import com.enix.hoken.info.Dinfo;
import com.enix.hoken.util.CommonUtil;

public class DownloadManager {
	private FinalHttp finalHttp;
	private HttpHandler<File> handler;
	public DownloadStateChangedListener listener;
	public static final int START = 0;
	public static final int DOWNLOADING = 1;
	public static final int PAUSE = 2;
	public static final int ERROR = 3;
	public static final int COMPLETE = 4;
	public DownloadAjaxCallBack mAjaxCallBack;
	private Dinfo mDinfo;

	public DownloadManager(Dinfo mDinfo, DownloadStateChangedListener listener) {
		this.mDinfo = mDinfo;
		this.listener = listener;
		finalHttp = new FinalHttp();
	}

	/**
	 * 开始/继续下载任务
	 */
	public void startDownloadTask() {
		startDownloadTask(true);
	}

	/**
	 * 删除文件并重新下载
	 */
	public void restartDownloadTask() {
		startDownloadTask(false);
	}

	/**
	 * 开始下载任务
	 * 
	 * @param isResume
	 *            是否断点续传
	 */
	private void startDownloadTask(boolean isResume) {
		listener.downloadStateChanged(START, mDinfo);

		mAjaxCallBack = new DownloadAjaxCallBack(mDinfo, listener);
		handler = finalHttp.download(mDinfo.getUrl(),
				CommonUtil.getDownLoadPath(mDinfo.getUrl()), true,
				mAjaxCallBack);
	}

	/**
	 * 暂停下载任务
	 */
	public void pauseDownloadTask() {
		handler.stop();
		listener.downloadStateChanged(PAUSE, mDinfo);
	}

	/**
	 * 继续下载任务
	 */
	public void resumeDownloadTask() {
		finalHttp.download(mDinfo.getUrl(),
				CommonUtil.getDownLoadPath(mDinfo.getUrl()), true,
				mAjaxCallBack);
		listener.downloadStateChanged(DOWNLOADING, mDinfo);
	}

	public interface DownloadStateChangedListener {
		/**
		 * 返回下载状态变更后的事件
		 * 
		 * @param stateId
		 *            状态ID
		 * @param mDinfo
		 *            下载记录对象
		 */
		public void downloadStateChanged(int stateId, Dinfo mDinfo);
	}

	private static class DownloadAjaxCallBack extends AjaxCallBack<File> {
		Dinfo mDinfo;
		DownloadStateChangedListener listener;
		private SimpleDateFormat format = new SimpleDateFormat("M-d HH:mm");

		public DownloadAjaxCallBack(Dinfo mDinfo,
				DownloadStateChangedListener listener) {
			this.mDinfo = mDinfo;
			this.listener = listener;
		}

		@Override
		public void onSuccess(File t) {
			mDinfo.setState(COMPLETE);
			mDinfo.setDatetime(format.format(new Date()));
			listener.downloadStateChanged(COMPLETE, mDinfo);
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			mDinfo.setState(PAUSE);
			mDinfo.setDatetime(format.format(new Date()));
			listener.downloadStateChanged(PAUSE, mDinfo);
		}

		public void onLoading(long count, long current) {
			mDinfo.setPresize(current);
			mDinfo.setTotalsize(count);
			mDinfo.setState(DOWNLOADING);
			listener.downloadStateChanged(DOWNLOADING, mDinfo);
		}

	}

}
