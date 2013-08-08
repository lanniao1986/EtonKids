package com.enix.hoken.custom.mainview;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.util.*;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.enix.hoken.activity.DesktopActivity;
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

public class RssMainView extends MainView implements OnHeaderRefreshListener,
		OnFooterRefreshListener {

	public static final int MODE_RSSLIST = 1;// 订阅列表

	private static final int REFLASH_INIT = 0;
	private static final int REFLASH_RELOADE = 1;
	private static final int REFLASH_LOADE_MORE = 2;
	private int currentReflashMode = 0;

	private RssListAdapter mRssListAdapter;
	private RssinfoList mAllRssinfoList;// 全部RSSITEM列表
	private RssinfoList mPartRssinfoList;// 局部RSSITEM列表
	private RssinfoList moreList;// 下拉获取更多时装载的列表
	private ListView mRssListView;
	private TextView mListEmpty;
	private String[] mRssUrlArray;
	private int currentRssIndex = 0;
	private PullToRefreshView mPullToRefreshView;
	public static final int MAX_ITEM_COUNT = 20;// 默认每次加载条数
	private int lastEndIndex = 0;// 上一次最末尾索引数
	private SimpleDateFormat format = new SimpleDateFormat("上次更新于  M月d日 HH:mm");

	public RssMainView(DesktopActivity activity) {
		super(activity, R.layout.lay_rss);

		findView();
		initView();
		setListener();
	}

	public void findView() {
		super.findView();
		mRssListView = (ListView) mView.findViewById(R.id.rss_listview);
		mListEmpty = (TextView) mView.findViewById(R.id.rss_listview_empty);
		mPullToRefreshView = (PullToRefreshView) mView
				.findViewById(R.id.main_pull_refresh_view);
	}

	public void setListener() {
		super.setListener();
		mPullToRefreshView.setOnHeaderRefreshListener(this);
		mPullToRefreshView.setOnFooterRefreshListener(this);
		mModeListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterview, View view,
					int position, long l) {
				mModeChooseIdx = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				currentReflashMode = REFLASH_INIT;
				initRssList(mRssUrlArray[position]);
			}
		});
	}

	public void initView() {
		super.initView();
		mRssUrlArray = mActivity.getResources().getStringArray(
				R.array.rss_url_strings);
		mModeName = mActivity.getResources().getStringArray(
				R.array.rss_url_names);
		int[] iconName = new int[mRssUrlArray.length];
		for (int i = 0; i < iconName.length; i++) {
			iconName[i] = R.drawable.profile_popupwindow_type_collection_background;
		}
		setModeMenuList(R.array.rss_url_names, iconName);
		mExtend1.setVisibility(View.GONE);
		mExtend2.setVisibility(View.GONE);
	}

	// 初始化默认显示列表
	public void init() {
		handler.sendEmptyMessage(MODE_RSSLIST);
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
			case MODE_RSSLIST:// 订阅列表

				initRssList(mRssUrlArray[currentRssIndex]);
				break;

			}

		}
	};

	/**
	 * 初始化加载RSS列表
	 */
	private void initRssList(final String rssurl) {
		// 判断联网状态为已连接
		if (CommonUtil.CheckNetworkState(mActivity) > 0) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					URL url;
					try {
						if (currentReflashMode == REFLASH_INIT) {
							handler.sendEmptyMessage(0);
						}

						url = new URL(rssurl);
						Log.i("DEBUG", rssurl);
						RssFeed feed = RssReader.read(url);
						ArrayList<RssItem> rssItems = feed.getRssItems();
						mAllRssinfoList = new RssinfoList();
						for (RssItem rssItem : rssItems) {
							Rssinfo mRssinfo = new Rssinfo();
							mRssinfo.setRss_title(rssItem.getTitle());
							mRssinfo.setRss_datetime(rssItem.getPubDate());
							mRssinfo.setRss_description(CommonUtil
									.html2Text(rssItem.getDescription()));
							mRssinfo.setRss_link(rssItem.getLink());
							mAllRssinfoList.add(mRssinfo);
						}
					} catch (Exception e) {
						e.printStackTrace();
						CommonUtil.showLongToast(mActivity, "获取数据失败!");
					} finally {
						handler.post(runnableUpdateUi);
					}
				}
			}).start();
		} else {// 网络未连接
			CommonUtil.showConfirmDialog("是否开启网络设置", "当前未连接任何网络,是否进入移动网络设置界面?",
					new String[] { "开启设置", "返回" }, mActivity,
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

							mActionHandler.startNetWorkSettingIntent();
						}
					});
			CommonUtil.showShortToast(mActivity, "网络未连接,数据无法获取!");
			handler.post(runnableUpdateUi);
		}
	}

	// 构建Runnable对象，在runnable中更新界面
	private Runnable runnableUpdateUi = new Runnable() {
		@Override
		public void run() {
			// 更新界面
			mDialog.dismiss();
			mPullToRefreshView.onHeaderRefreshComplete(format
					.format(new Date()));
			mPullToRefreshView.onFooterRefreshComplete();
			if (mAllRssinfoList == null) {
				mAllRssinfoList = new RssinfoList();
			}
			getMoreList();
			mRssListAdapter = new RssListAdapter(mActivity, mPartRssinfoList);
			mRssListView.setAdapter(mRssListAdapter);
			// 列表内无数据时显示提示
			if (mRssListView.getCount() == 0) {
				mListEmpty.setVisibility(View.VISIBLE);
			} else {
				mListEmpty.setVisibility(View.GONE);
			}
		}
	};

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		currentReflashMode = REFLASH_RELOADE;
		initRssList(mRssUrlArray[currentRssIndex]);

	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		currentReflashMode = REFLASH_LOADE_MORE;
		getMoreList();
		mRssListAdapter.notifyDataSetChanged();
		mPullToRefreshView.onFooterRefreshComplete();
	}

	/**
	 * 获取更多列表项目
	 */
	private void getMoreList() {

		if (currentReflashMode == REFLASH_INIT) { // 初始化加载列表
			mPartRssinfoList = new RssinfoList();
			lastEndIndex = 0;
		} else if (currentReflashMode == REFLASH_LOADE_MORE) { // 加载更多

		} else if (currentReflashMode == REFLASH_RELOADE) {// 刷新重新加载
			return;
		}
		moreList = new RssinfoList();
		int end = MAX_ITEM_COUNT + lastEndIndex;
		if (end > mAllRssinfoList.size()) {
			end = mAllRssinfoList.size();
		}
		if (lastEndIndex < mAllRssinfoList.size() - 1) {
			moreList.addAll(mAllRssinfoList.subList(lastEndIndex, end));
			mPartRssinfoList.addAll(moreList);
		} else {
			CommonUtil.showShortToast(mActivity, "没有更多数据了");
		}
		lastEndIndex = end;
	}

}
