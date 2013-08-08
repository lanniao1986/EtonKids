package com.enix.hoken.activity;

import com.enix.hoken.basic.SubActivity;
import com.enix.hoken.custom.download.services.TrafficCounterService;
import com.enix.hoken.custom.download.utils.DownLoadIntents;

import com.enix.hoken.custom.download.widgets.DownloadListAdapter;
import com.enix.hoken.custom.download.widgets.ViewHolder;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.*;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

public class DownloadListActivity extends SubActivity {

	private ListView downloadList;
	private Button mBtnAdd;
	private Button mBtnPause;
	private Button mBtnDelete;
	private Button mBtnTraffic;
	private DownloadListAdapter mDownloadListAdapter;
	private DownLoadReceiver mDownLoadReceiver;
	private int urlIndex = 0;
	private static final String APK_OFFICE = "http://211.167.105.80/1Q2W3E4R5T6Y7U8I9O0P1Z2X3C4V5B/wdl.cache.ijinshan.com/wps/download/android/kingsoftoffice_2052/moffice_2052_wpscn.apk";
	private static final String APK_MAXTHON = "http://211.167.105.112:81/1Q2W3E4R5T6Y7U8I9O0P1Z2X3C4V5B/dl.maxthon.cn/mobile/download/mx/100/MaxthonCloudBrowser_Android_v4.0.6.2000.apk";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.download_list_activity);
		if (!CommonUtil.isSDCardPresent()) {
			Toast.makeText(this, "未发现SD卡", Toast.LENGTH_LONG).show();
			return;
		}
		if (!CommonUtil.isSdCardWrittenable()) {
			Toast.makeText(this, "SD卡不能读写", Toast.LENGTH_LONG).show();
			return;
		}
		try {
			CommonUtil.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
		findView();
		initView();
		setListener();
	}

	public void findView() {
		super.findView();
		downloadList = (ListView) findViewById(R.id.download_list);
		mBtnAdd = (Button) findViewById(R.id.btn_add);
		mBtnPause = (Button) findViewById(R.id.btn_pause_all);
		mBtnDelete = (Button) findViewById(R.id.btn_delete_all);
		mBtnTraffic = (Button) findViewById(R.id.btn_traffic);
	}

	public void initView() {
		super.initView();
		setTitle("下载管理列表");
		mExtend1.setVisibility(View.GONE);
		mExtend2.setVisibility(View.GONE);
		mDownloadListAdapter = new DownloadListAdapter(this);
		downloadList.setAdapter(mDownloadListAdapter);
		// downloadManager.startManage();
		// 启动流量监控服务
		Intent trafficIntent = new Intent(this, TrafficCounterService.class);
		startService(trafficIntent);
		// 启动下载服务
		Intent downloadIntent = new Intent(
				"com.enix.hoken.custom.download.services.IDownloadService");
		downloadIntent.putExtra(DownLoadIntents.TYPE,
				DownLoadIntents.Types.START);
		startService(downloadIntent);

		// // handle intent
		// Intent intent = getIntent();
		// handleIntent(intent);
		mDownLoadReceiver = new DownLoadReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.enix.hoken.activity.DownloadListActivity");
		registerReceiver(mDownLoadReceiver, filter);

	}

	public void setListener() {
		super.setListener();
		mBtnAdd.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// downloadManager.addTask(Utils.url[urlIndex]);
				Intent downloadIntent = new Intent(
						"com.enix.hoken.custom.download.services.IDownloadService");
				downloadIntent.putExtra(DownLoadIntents.TYPE,
						DownLoadIntents.Types.ADD);
				downloadIntent.putExtra(DownLoadIntents.URL, APK_OFFICE);
				startService(downloadIntent);

				urlIndex++;
				if (urlIndex >= APK_OFFICE.length()) {
					urlIndex = 0;
				}
			}
		});

		mBtnPause.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		mBtnDelete.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// Intent downloadIntent = new
				// Intent("com.enix.hoken.custom.download.services.IDownloadService");
				// downloadIntent.putExtra(MyIntents.TYPE,
				// MyIntents.Types.STOP);
				// startService(downloadIntent);
				//
				// Intent trafficIntent = new Intent(DownloadListActivity.this,
				// TrafficCounterService.class);
				// stopService(trafficIntent);
			}
		});

		mBtnTraffic.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(DownloadListActivity.this,
						TrafficStatActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onDestroy() {

		unregisterReceiver(mDownLoadReceiver);

		super.onDestroy();
	}

	/**
	 * 接收下载服务的广播
	 * 
	 * @author gumc
	 * 
	 */
	public class DownLoadReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			handleIntent(intent);

		}

		private void handleIntent(Intent intent) {

			if (intent != null
					&& intent.getAction().equals(
							"com.enix.hoken.activity.DownloadListActivity")) {
				int type = intent.getIntExtra(DownLoadIntents.TYPE, -1);
				String url;

				switch (type) {// 判断操作类型
				case DownLoadIntents.Types.ADD:
					url = intent.getStringExtra(DownLoadIntents.URL);
					boolean isPaused = intent.getBooleanExtra(
							DownLoadIntents.IS_PAUSED, false);
					if (!TextUtils.isEmpty(url)) {
						mDownloadListAdapter.addItem(url, isPaused);
					}
					break;
				case DownLoadIntents.Types.COMPLETE:
					url = intent.getStringExtra(DownLoadIntents.URL);
					if (!TextUtils.isEmpty(url)) {
						mDownloadListAdapter.removeItem(url);
					}
					break;
				case DownLoadIntents.Types.PROCESS:
					url = intent.getStringExtra(DownLoadIntents.URL);
					View taskListItem = downloadList.findViewWithTag(url);
					ViewHolder viewHolder = new ViewHolder(taskListItem);
					viewHolder
							.setData(
									url,
									intent.getStringExtra(DownLoadIntents.PROCESS_SPEED),
									intent.getStringExtra(DownLoadIntents.PROCESS_SIZE),
									intent.getStringExtra(DownLoadIntents.PROCESS_PROGRESS));
					break;
				case DownLoadIntents.Types.ERROR:
					url = intent.getStringExtra(DownLoadIntents.URL);
					// int errorCode =
					// intent.getIntExtra(MyIntents.ERROR_CODE,
					// DownloadTask.ERROR_UNKONW);
					// handleError(url, errorCode);
					break;
				default:
					break;
				}
			}
		}

		// private void handleError(String url, int code) {
		//
		// switch (code) {
		// case DownloadTask.ERROR_BLOCK_INTERNET:
		// case DownloadTask.ERROR_UNKOWN_HOST:
		// showAlert("错误", "无法连接网络");
		// View taskListItem = downloadList.findViewWithTag(url);
		// ViewHolder viewHolder = new ViewHolder(taskListItem);
		// viewHolder.onPause();
		// break;
		// case DownloadTask.ERROR_FILE_EXIST:
		// showAlert("", "文件已经存在，取消下载");
		// break;
		// case DownloadTask.ERROR_SD_NO_MEMORY:
		// showAlert("错误", "存储卡空间不足");
		// break;
		// case DownloadTask.ERROR_UNKONW:
		//
		// break;
		// case DownloadTask.ERROR_TIME_OUT:
		// showAlert("错误", "连接超时，请检查网络后重试");
		// View timeoutItem = downloadList.findViewWithTag(url);
		// ViewHolder timeoutHolder = new ViewHolder(timeoutItem);
		// timeoutHolder.onPause();
		// break;
		//
		// default:
		// break;
		// }
		// }

		@SuppressWarnings("unused")
		private void showAlert(String title, String msg) {

			new AlertDialog.Builder(DownloadListActivity.this)
					.setTitle(title)
					.setMessage(msg)
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									dialog.dismiss();
								}
							}).create().show();
		}
	}

}
