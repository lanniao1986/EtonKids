package com.enix.hoken.custom.download.services;

/**
 * 下载任务侦听器接口
 * 
 * @author gumc
 * 
 */
public interface DownloadTaskListener {
	/**
	 * 更新下载进度
	 * 
	 * @param task
	 */
	public void updateProcess(DownloadTask task);

	/**
	 * 完成下载
	 * 
	 * @param task
	 */
	public void finishDownload(DownloadTask task);

	/**
	 * 断点续传指定任务
	 * 
	 * @param task
	 */
	public void preDownload(DownloadTask task);

	/**
	 * 下载错误
	 * 
	 * @param task
	 * @param error
	 */
	public void errorDownload(DownloadTask task, Throwable error);
}
