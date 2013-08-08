package com.enix.hoken.custom.mainview;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.*;
import android.view.View.*;
import android.view.*;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.DownloadListActivity;
import com.enix.hoken.basic.MainView;
import com.enix.hoken.R;
import com.enix.hoken.custom.adapter.RssListAdapter;
import com.enix.hoken.custom.item.PullToRefreshView;
import com.enix.hoken.custom.item.PullToRefreshView.*;
import com.enix.hoken.custom.rss.RssFeed;
import com.enix.hoken.custom.rss.RssItem;
import com.enix.hoken.custom.rss.RssReader;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;

public class DownLoadMainView extends MainView {

	public static final int MODE_DOWNLOAD_COMPLETE = 1;// 下载完成列表
	private ListView mDownLoadList;
	private Button mBtnRecommand;
	private Button mBtnPlugin;
	private Button mBtnManager;
	private TextView mListEmpty;

	private SimpleDateFormat format = new SimpleDateFormat("上次更新于  M月d日 HH:mm");
	HttpHandler<File> mHandler;
	FinalHttp mFinalHttp;

	public DownLoadMainView(DesktopActivity activity) {
		super(activity, R.layout.lay_download);

		findView();
		initView();
		setListener();
	}

	public void findView() {
		super.findView();
		mDownLoadList = (ListView) mView.findViewById(R.id.download_listview);
		mListEmpty = (TextView) mView
				.findViewById(R.id.download_listview_empty);
		mBtnRecommand = (Button) mView
				.findViewById(R.id.download_btn_recommend);
		mBtnPlugin = (Button) mView.findViewById(R.id.download_btn_plugin);
		mBtnManager = (Button) mView.findViewById(R.id.download_btn_manager);

	}

	public void setListener() {
		super.setListener();
		mBtnManager.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mActionHandler.startIntent(mActionHandler.createTranspotIntent(
						DownloadListActivity.class, null));

			}

		});
		mBtnRecommand.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

	}

	public void initView() {
		super.initView();
		mModeText.setText("下载中心");
	}

	// 初始化默认显示列表
	public void init() {
		handler.sendEmptyMessage(MODE_DOWNLOAD_COMPLETE);
	}

	public void init(int modeID) {
		handler.sendEmptyMessage(modeID);
	}

	// 指令控制侦听器
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mDialog.show();
				break;
			case MODE_DOWNLOAD_COMPLETE:// 已完成列表

				initCompleteList();

				break;

			}

		}

		private void initCompleteList() {
			// TODO Auto-generated method stub

		}
	};

}
