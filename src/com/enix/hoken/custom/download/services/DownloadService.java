package com.enix.hoken.custom.download.services;

import com.enix.hoken.custom.download.utils.DownLoadIntents;
import com.enix.hoken.util.CommonUtil;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

/**
 * 下载服务
 * 
 * @author gumc
 * 
 */
public class DownloadService extends Service {

	private DownloadManager mDownloadManager;

	@Override
	public IBinder onBind(Intent intent) {
		// 绑定服务
		CommonUtil.printDebugMsg("DownloadService:OnBind");
		return new DownloadServiceImpl();
	}

	@Override
	public void onCreate() {

		super.onCreate();
		mDownloadManager = new DownloadManager(this);
	}

	@Override
	public void onStart(Intent intent, int startId) {

		super.onStart(intent, startId);

		// if (mDownloadManager == null) {
		// mDownloadManager = new DownloadManager(this);
		// }

		if (intent != null
				&& intent
						.getAction()
						.equals("com.enix.hoken.custom.download.services.IDownloadService")) {
			int type = intent.getIntExtra(DownLoadIntents.TYPE, -1);
			String url;
			if (mDownloadManager != null) {
				switch (type) {
				// 启动下载服务
				case DownLoadIntents.Types.START:
					if (!mDownloadManager.isRunning()) {// 线程停止状态
						// 启动下载管理器线程
						mDownloadManager.startManage();
					} else {
						// 批量发送任务广播
						mDownloadManager.reBroadcastAddAllTask();
					}
					break;
				case DownLoadIntents.Types.ADD:
					url = intent.getStringExtra(DownLoadIntents.URL);
					// 任务线程中添加指定URL的下载任务
					if (!TextUtils.isEmpty(url)
							&& !mDownloadManager.hasTask(url)) {
						mDownloadManager.addTask(url);
					}
					break;
				case DownLoadIntents.Types.CONTINUE:
					url = intent.getStringExtra(DownLoadIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						// 任务线程中继续执行指定URL的下载任务
						mDownloadManager.continueTask(url);
					}
					break;
				case DownLoadIntents.Types.DELETE:
					url = intent.getStringExtra(DownLoadIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						// 任务线程中删除指定URL的下载任务
						mDownloadManager.deleteTask(url);
					}
					break;
				case DownLoadIntents.Types.PAUSE:
					url = intent.getStringExtra(DownLoadIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						// 任务线程中暂停指定URL的下载任务
						mDownloadManager.pauseTask(url);
					}
					break;
				case DownLoadIntents.Types.STOP:
					mDownloadManager.close();
					// mDownloadManager = null;
					break;

				default:
					break;
				}
			}
		}

	}

	/**
	 * 下载服务接口 (继承自IDownloadService.aidl)
	 * 
	 * @author gumc
	 * 
	 */
	private class DownloadServiceImpl extends IDownloadService.Stub {

		@Override
		public void startManage() throws RemoteException {

			mDownloadManager.startManage();
		}

		@Override
		public void addTask(String url) throws RemoteException {

			mDownloadManager.addTask(url);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {

		}

		@Override
		public void deleteTask(String url) throws RemoteException {

		}

		@Override
		public void continueTask(String url) throws RemoteException {

		}

	}

}
