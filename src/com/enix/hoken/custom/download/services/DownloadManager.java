package com.enix.hoken.custom.download.services;

import com.enix.hoken.custom.download.utils.ConfigUtils;
import com.enix.hoken.custom.download.utils.DownLoadIntents;

import com.enix.hoken.util.CommonUtil;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 下载管理器线程
 * 
 * @author gumc
 * 
 */
public class DownloadManager extends Thread {

	private static final int MAX_TASK_COUNT = 100;// 最大任务总数
	private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;// 最大下载线程总数

	private Context mContext;

	private TaskQueue mTaskQueue;// 下载序列
	private List<DownloadTask> mDownloadingTasks;// 下载中任务列表
	private List<DownloadTask> mPausingTasks;// 暂停中任务列表

	private Boolean isRunning = false;

	public DownloadManager(Context context) {

		mContext = context;
		mTaskQueue = new TaskQueue();
		mDownloadingTasks = new ArrayList<DownloadTask>();
		mPausingTasks = new ArrayList<DownloadTask>();
	}

	/**
	 * 启动下载线程
	 */
	public void startManage() {

		isRunning = true;
		this.start();
		checkUncompleteTasks();
	}

	public void close() {

		isRunning = false;
		pauseAllTask();
		this.stop();
	}

	public boolean isRunning() {

		return isRunning;
	}

	@Override
	public void run() {

		super.run();
		while (isRunning) {
			DownloadTask task = mTaskQueue.poll();
			mDownloadingTasks.add(task);
			task.execute();
		}
	}

	public void addTask(String url) {

		if (!CommonUtil.isSDCardPresent()) {
			Toast.makeText(mContext, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}

		if (!CommonUtil.isSdCardWrittenable()) {
			Toast.makeText(mContext, "SD卡不能读写", Toast.LENGTH_LONG).show();
			return;
		}

		if (getTotalTaskCount() >= MAX_TASK_COUNT) {
			Toast.makeText(mContext, "任务列表已满", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			addTask(newDownloadTask(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

	}

	private void addTask(DownloadTask task) {

		broadcastAddTask(task.getUrl());

		mTaskQueue.offer(task);

		if (!this.isAlive()) {
			this.startManage();
		}
	}

	private void broadcastAddTask(String url) {

		broadcastAddTask(url, false);
	}

	/**
	 * 发送添加下载任务广播
	 * 
	 * @param url
	 *            下载任务URL
	 * @param isInterrupt
	 *            是否暂停状态
	 */
	private void broadcastAddTask(String url, boolean isInterrupt) {

		Intent nofityIntent = new Intent(
				"com.enix.hoken.activity.DownloadListActivity");
		nofityIntent.putExtra(DownLoadIntents.TYPE, DownLoadIntents.Types.ADD);
		nofityIntent.putExtra(DownLoadIntents.URL, url);
		nofityIntent.putExtra(DownLoadIntents.IS_PAUSED, isInterrupt);
		mContext.sendBroadcast(nofityIntent);
	}

	/**
	 * 批量发布广播
	 */
	public void reBroadcastAddAllTask() {

		DownloadTask task;
		// 发布下载中任务广播
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			broadcastAddTask(task.getUrl(), task.isInterrupt());
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			broadcastAddTask(task.getUrl());
		}
		// 发布暂停中任务广播
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			broadcastAddTask(task.getUrl());
		}
	}

	public boolean hasTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task.getUrl().equals(url)) {
				return true;
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
		}
		return false;
	}

	public DownloadTask getTask(int position) {

		if (position >= mDownloadingTasks.size()) {
			return mTaskQueue.get(position - mDownloadingTasks.size());
		} else {
			return mDownloadingTasks.get(position);
		}
	}

	public int getQueueTaskCount() {

		return mTaskQueue.size();
	}

	public int getDownloadingTaskCount() {

		return mDownloadingTasks.size();
	}

	public int getPausingTaskCount() {

		return mPausingTasks.size();
	}

	public int getTotalTaskCount() {

		return getQueueTaskCount() + getDownloadingTaskCount()
				+ getPausingTaskCount();
	}

	/**
	 * 获取所有未下载完成的任务URL
	 */
	public void checkUncompleteTasks() {

		List<String> urlList = ConfigUtils.getURLArray(mContext);
		if (urlList.size() >= 0) {
			for (int i = 0; i < urlList.size(); i++) {
				// 逐一添加到任务列表
				addTask(urlList.get(i));
			}
		}
	}

	/**
	 * 暂停指定URL的下载任务
	 * 
	 * @param url
	 */
	public synchronized void pauseTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				pauseTask(task);
			}
		}
	}

	/**
	 * 暂停所有下载任务
	 */
	public synchronized void pauseAllTask() {

		DownloadTask task;

		// 暂停下载序列中所有任务
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			// 从下载序列中移除
			mTaskQueue.remove(task);
			mPausingTasks.add(task);
		}

		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null) {
				pauseTask(task);
			}
		}
	}

	/**
	 * 删除指定URL的下载任务
	 * 
	 * @param url
	 */
	public synchronized void deleteTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				// 删除下载文件
				File file = new File(CommonUtil.getDownLoadPath()
						+ CommonUtil.getFileNameFromUrl(task.getUrl()));
				if (file.exists())
					file.delete();

				task.onCancelled();
				completeTask(task);
				return;
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mTaskQueue.remove(task);
			}
		}
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mPausingTasks.remove(task);
			}
		}
	}

	public synchronized void continueTask(String url) {

		DownloadTask task;
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				continueTask(task);
			}

		}
	}

	/**
	 * 暂停指定的下载任务
	 * 
	 * @param task
	 */
	public synchronized void pauseTask(DownloadTask task) {

		if (task != null) {
			// 取消任务
			task.onCancelled();

			// move to pausing list
			String url = task.getUrl();
			try {
				mDownloadingTasks.remove(task);
				task = newDownloadTask(url);
				mPausingTasks.add(task);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

		}
	}

	public synchronized void continueTask(DownloadTask task) {

		if (task != null) {
			mPausingTasks.remove(task);
			mTaskQueue.offer(task);
		}
	}

	/**
	 * 发送广播将指定任务设为完成状态
	 * 
	 * @param task
	 */
	public synchronized void completeTask(DownloadTask task) {

		if (mDownloadingTasks.contains(task)) {
			ConfigUtils.clearURL(mContext, mDownloadingTasks.indexOf(task));
			mDownloadingTasks.remove(task);

			// notify list changed
			Intent nofityIntent = new Intent(
					"com.enix.hoken.activity.DownloadListActivity");
			nofityIntent.putExtra(DownLoadIntents.TYPE,
					DownLoadIntents.Types.COMPLETE);
			nofityIntent.putExtra(DownLoadIntents.URL, task.getUrl());
			mContext.sendBroadcast(nofityIntent);
		}
	}

	/**
	 * 以默认设置下新建下载任务
	 * 
	 * @param url
	 *            下载对象URL
	 * @return
	 * @throws MalformedURLException
	 */
	private DownloadTask newDownloadTask(String url)
			throws MalformedURLException {

		DownloadTaskListener taskListener = new DownloadTaskListener() {

			@Override
			public void updateProcess(DownloadTask task) {

				Intent updateIntent = new Intent(
						"com.enix.hoken.activity.DownloadListActivity");
				updateIntent.putExtra(DownLoadIntents.TYPE,
						DownLoadIntents.Types.PROCESS);
				updateIntent.putExtra(
						DownLoadIntents.PROCESS_SIZE,
						CommonUtil.parseSizeString(task.getDownloadSize())
								+ " / "
								+ CommonUtil.parseSizeString(task
										.getTotalSize()));
				updateIntent
						.putExtra(
								DownLoadIntents.PROCESS_SPEED,
								CommonUtil.parseSizeString(task
										.getDownloadSpeed() * 1000) + "/秒");
				updateIntent.putExtra(DownLoadIntents.PROCESS_PROGRESS,
						task.getDownloadPercent() + "");
				updateIntent.putExtra(DownLoadIntents.URL, task.getUrl());
				mContext.sendBroadcast(updateIntent);
			}

			@Override
			public void preDownload(DownloadTask task) {

				ConfigUtils.storeURL(mContext, mDownloadingTasks.indexOf(task),
						task.getUrl());
			}

			@Override
			public void finishDownload(DownloadTask task) {

				completeTask(task);
			}

			@Override
			public void errorDownload(DownloadTask task, Throwable error) {

				if (error != null) {
					Toast.makeText(mContext, "Error: " + error.getMessage(),
							Toast.LENGTH_LONG).show();
				}

				// Intent errorIntent = new
				// Intent("com.enix.hoken.activity.DownloadListActivity");
				// errorIntent.putExtra(MyIntents.TYPE, MyIntents.Types.ERROR);
				// errorIntent.putExtra(MyIntents.ERROR_CODE, error);
				// errorIntent.putExtra(MyIntents.ERROR_INFO,
				// DownloadTask.getErrorInfo(error));
				// errorIntent.putExtra(MyIntents.URL, task.getUrl());
				// mContext.sendBroadcast(errorIntent);
				//
				// if (error != DownloadTask.ERROR_UNKOWN_HOST
				// && error != DownloadTask.ERROR_BLOCK_INTERNET
				// && error != DownloadTask.ERROR_TIME_OUT) {
				// completeTask(task);
				// }
			}
		};
		return new DownloadTask(mContext, url, CommonUtil.getDownLoadPath(),
				taskListener);
	}

	/**
	 * 下载序列
	 * 
	 * @author Gumc
	 */
	private class TaskQueue {
		private Queue<DownloadTask> taskQueue;

		public TaskQueue() {

			taskQueue = new LinkedList<DownloadTask>();
		}

		/**
		 * 添加任务到下载序列
		 * 
		 * @param task
		 */
		public void offer(DownloadTask task) {

			taskQueue.offer(task);
		}

		public DownloadTask poll() {

			DownloadTask task = null;
			// 下载任务数大于等于最大下载线程个数 或 序列为空
			while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
					|| (task = taskQueue.poll()) == null) {
				try {
					// 线程休眠1秒
					Thread.sleep(1000); // sleep
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return task;
		}

		/**
		 * 根据位置索引获取下载任务对象
		 * 
		 * @param position
		 * @return
		 */
		public DownloadTask get(int position) {

			if (position >= size()) {
				return null;
			}
			return ((LinkedList<DownloadTask>) taskQueue).get(position);
		}

		/**
		 * 获取当前下载序列中的任务个数
		 * 
		 * @return
		 */
		public int size() {

			return taskQueue.size();
		}

		/**
		 * 移除指定位置的下载任务
		 * 
		 * @param position
		 * @return
		 */
		@SuppressWarnings("unused")
		public boolean remove(int position) {

			return taskQueue.remove(get(position));
		}

		/**
		 * 移除指定下载任务
		 * 
		 * @param task
		 * @return
		 */
		public boolean remove(DownloadTask task) {

			return taskQueue.remove(task);
		}
	}

}
