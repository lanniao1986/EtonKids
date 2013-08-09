package com.enix.hoken.info;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.util.CommonUtil;

public class Dinfo extends MainInfo {
	private int id;
	private String fileName;// 文件显示用名称
	private String url;// 下载URL
	private String filePathName;// 文件下载名称
	private int state;// 下载状态标识
	private String datetime;// 状态变更时间
	private String imgurl;// 图标路径
	private boolean check;// 是否选择
	private long presize;// 断点续传已下载量
	private long totalsize;// 文件大小
	private FinalHttp finalHttp;
	private HttpHandler<File> handler;
	private DownloadStateChangedListener listener;
	public static final int READY = 0;
	public static final int DOWNLOADING = 1;
	public static final int PAUSE = 2;
	public static final int ABORT = 3;
	public static final int ERROR = 4;
	public static final int COMPLETE = 5;
	public DownloadAjaxCallBack mAjaxCallBack;

	public Dinfo(String url, String fileName,
			DownloadStateChangedListener listener) {
		this.url = url;
		this.fileName = fileName;
		this.listener = listener;
	}

	public Dinfo(String url, String fileName, int state,
			DownloadStateChangedListener listener) {
		this.url = url;
		this.fileName = fileName;
		this.state = state;
		this.listener = listener;
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
		listener.downloadStateChanged(READY, this);
		finalHttp = new FinalHttp();
		mAjaxCallBack = new DownloadAjaxCallBack(this);
		handler = finalHttp.download(url, CommonUtil.getDownLoadPath(url),
				true, mAjaxCallBack);
	}

	/**
	 * 暂停下载任务
	 */
	public void pauseDownloadTask() {
		handler.stop();
		listener.downloadStateChanged(PAUSE, this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DownloadStateChangedListener getListener() {
		return listener;
	}

	public void setListener(DownloadStateChangedListener listener) {
		this.listener = listener;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePathName() {
		return filePathName;
	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public long getPresize() {
		return presize;
	}

	public void setPresize(long presize) {
		this.presize = presize;
	}

	public long getTotalsize() {
		return totalsize;
	}

	public void setTotalsize(long totalsize) {
		this.totalsize = totalsize;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public FinalHttp getFinalHttp() {
		return finalHttp;
	}

	public void setFinalHttp(FinalHttp finalHttp) {
		this.finalHttp = finalHttp;
	}

	public HttpHandler<File> getHandler() {
		return handler;
	}

	public void setHandler(HttpHandler<File> handler) {
		this.handler = handler;
	}

	public interface DownloadStateChangedListener {
		public void downloadStateChanged(int stateId, Dinfo mDinfo);
	}

	private static class DownloadAjaxCallBack extends AjaxCallBack<File> {
		Dinfo mDinfo;
		private SimpleDateFormat format = new SimpleDateFormat("M-d HH:mm");

		public DownloadAjaxCallBack(Dinfo mDinfo) {
			this.mDinfo = mDinfo;
		}

		@Override
		public void onSuccess(File t) {
			mDinfo.setState(COMPLETE);
			mDinfo.setDatetime(format.format(new Date()));
			mDinfo.listener.downloadStateChanged(COMPLETE, mDinfo);
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			mDinfo.setState(ABORT);
			mDinfo.setDatetime(format.format(new Date()));
			mDinfo.listener.downloadStateChanged(ABORT, mDinfo);
		}

		public void onLoading(long count, long current) {
			mDinfo.setPresize(current);
			mDinfo.setTotalsize(count);
			mDinfo.setState(DOWNLOADING);
			mDinfo.listener.downloadStateChanged(DOWNLOADING, mDinfo);
		}

	}
}
