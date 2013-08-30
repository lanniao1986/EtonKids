package com.enix.hoken.custom.mainview;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import android.os.Handler;
import android.os.Message;
import android.view.View.*;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.DownloadListActivity;
import com.enix.hoken.basic.MainView;
import com.enix.hoken.basic.MultiChoiceIF;
import com.enix.hoken.R;
import com.enix.hoken.custom.adapter.DownloadListAdapter;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;

public class DownLoadMainView extends MainView implements MultiChoiceIF {

	public static final int MODE_DOWNLOAD_COMPLETE = 1;// 下载完成列表
	private ListView mDownLoadList;
	public DownloadListAdapter mDownloadListAdapter;
	private Button mBtnRecommand;
	private Button mBtnPlugin;
	private Button mBtnManager;
	private TextView mListEmpty;
	private SimpleDateFormat format = new SimpleDateFormat("上次更新于  M月d日 HH:mm");

	public static final String APK_BUDEJIE = "http://gdown.baidu.com/data/wisegame/ef4db096fe5741dc/baisibudejie_58.apk";
	public static final String APK_MAXTHON = "http://211.167.105.112:81/1Q2W3E4R5T6Y7U8I9O0P1Z2X3C4V5B/dl.maxthon.cn/mobile/download/mx/100/MaxthonCloudBrowser_Android_v4.0.6.2000.apk";
	public static final String IMG_MAXTHON = "http://soft.hao123.com/uploads/2013/0711/2013071114083051de4bde21f22.jpg";
	public static final String IMG_BUDEJIE = "http://d.hiphotos.bdimg.com/wisegame/pic/item/c3ec08fa513d2697ea86218254fbb2fb4216d89c.jpg";
	private DinfoList mDinfoList;

	private int downindex = 0;

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
				downindex += 1;
				if (downindex % 2 == 1) {
					startNewDownload(APK_BUDEJIE, "百思不得姐", IMG_BUDEJIE);
				} else {
					startNewDownload(APK_MAXTHON, "傲游浏览器移动版", IMG_MAXTHON);
				}
			}
		});
		// 长按下载列表 进入多选模式
		mDownLoadList.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				switchMultiChooice();
				return false;
			}
		});
		mDownLoadList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int i, long l) {
				if (checkable) {// 多选状态时
					// 切换选择状态
					if (mSelectMap.get(i) == null ? false : mSelectMap.get(i)) {
						mSelectMap.put(i, false);
					} else {
						mSelectMap.put(i, true);
					}
					mDownloadListAdapter.setmSelectMap(mSelectMap);
					mDownloadListAdapter.notifyDataSetChanged();
				}
			}
		});
		// 全选按钮点击事件
		mExtend1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < mDownLoadList.getCount(); i++) {
					mSelectMap.put(i, true);
				}
				mDownloadListAdapter.notifyDataSetChanged(mSelectMap);
			}
		});
		// 清除按钮点击事件
		mExtend2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < mDownLoadList.getCount(); i++) {
					mSelectMap.put(i, false);
				}
				mDownloadListAdapter.notifyDataSetChanged(mSelectMap);
			}
		});
		// 弹出子菜单点击事件
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuPopupWindow.dismiss();
				if (checkable) {// 多选模式下

				}
			}
		});

	}

	public void initView() {
		super.initView();
		mModeText.setText("下载中心");
		mDinfoList = initTaskList();
		mDownloadListAdapter = new DownloadListAdapter(mActivity, mDinfoList);
		mDownLoadList.setAdapter(mDownloadListAdapter);
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
				mMenuName = mActivity.getResources().getStringArray(
						R.array.download_setting_menu_strings);
				break;
			}
		}

		private void initCompleteList() {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 从首选项中获取历史任务
	 */
	public DinfoList initTaskList() {
		return mApplication.getDinfoList();
	}

	private void startNewDownload(String url, String name, String imgurl) {
		mDownloadListAdapter.addDownloadItem(url, name, imgurl);
	}

	@Override
	public void multiChooiceOff() {
		// TODO Auto-generated method stub
		super.multiChooiceOff();
		mDownloadListAdapter.setCheckMode(checkable);
		mDownloadListAdapter.notifyDataSetChanged(mSelectMap);
	}

	@Override
	public void multiChooiceOn() {
		// TODO Auto-generated method stub
		super.multiChooiceOn();
		mDownloadListAdapter.setCheckMode(checkable);
		mDownloadListAdapter.notifyDataSetChanged(mSelectMap);
	}
}
