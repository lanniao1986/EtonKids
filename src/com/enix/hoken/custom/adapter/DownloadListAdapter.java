package com.enix.hoken.custom.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.tsz.afinal.FinalBitmap;

import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.custom.item.DownloadManager.DownloadStateChangedListener;
import com.enix.hoken.custom.item.DownloadTask;
import com.enix.hoken.R;
import com.enix.hoken.custom.item.DownloadManager;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;

public class DownloadListAdapter extends MainAdapter implements
		DownloadStateChangedListener {

	private Dinfo mDinfo;
	private DownloadTask mDonwloadTask;
	private ArrayList<DownloadTask> mTaskList;
	private FinalBitmap fb;
	public boolean checkMode = false;
	private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

	public DownloadListAdapter(MainActivity mActivity, DinfoList mMainInfoList) {
		super(mActivity, mMainInfoList);
		mTaskList = new ArrayList<DownloadTask>();
		if (mMainInfoList.size() > 0) {
			for (int i = 0; i < mMainInfoList.size(); i++) {
				mDonwloadTask = new DownloadTask(mMainInfoList.get(i), this);
				mDonwloadTask.resetDinfoState();
				mTaskList.add(mDonwloadTask);
			}
		}
		fb = mActivity.fb;
	}

	public boolean isCheckMode() {
		return checkMode;
	}

	public void setCheckMode(boolean checkMode) {
		this.checkMode = checkMode;
	}

	public Map<Integer, Boolean> getmSelectMap() {
		return mSelectMap;
	}

	public void setmSelectMap(Map<Integer, Boolean> mSelectMap) {
		this.mSelectMap = mSelectMap;
	}

	private void setInfoToAppData() {
		mAppDataManager.setInfoToApp(mMainInfoList, InfoSession.DINFOLIST);
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.download_list_item, null);
			holder = new ViewHolder();

			holder.mCheckBox = (CheckBox) convertView
					.findViewById(R.id.download_setting_checkbox);
			holder.mFileName = (TextView) convertView
					.findViewById(R.id.download_setting_filename);
			holder.mSpector = (TextView) convertView
					.findViewById(R.id.download_setting_spector);
			holder.mProgressBar = (ProgressBar) convertView
					.findViewById(R.id.download_setting_progress_bar);
			holder.mState = (TextView) convertView
					.findViewById(R.id.download_setting_state);
			holder.mCurrentSize = (TextView) convertView
					.findViewById(R.id.download_setting_currentsize);
			holder.mTotalSize = (TextView) convertView
					.findViewById(R.id.download_setting_totalsize);
			holder.mContinue = (Button) convertView
					.findViewById(R.id.download_setting_continue);
			holder.mPause = (Button) convertView
					.findViewById(R.id.download_setting_pause);
			holder.mLaunch = (Button) convertView
					.findViewById(R.id.download_setting_launch);
			holder.mIcon = (ImageView) convertView
					.findViewById(R.id.download_setting_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mDonwloadTask = mTaskList.get(position);
		mDinfo = mDonwloadTask.getmDinfo();
		int state = mDinfo.getState();
		holder.mFileName.setText(mDinfo.getFileName());
		fb.display(holder.mIcon, mDinfo.getImgurl());
		switch (state) {
		case DownloadManager.COMPLETE:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText(CommonUtil.parseSizeString(mDinfo
					.getTotalsize()));
			holder.mSpector.setVisibility(View.INVISIBLE);
			holder.mState.setText("下载完成");
			holder.mProgressBar.setVisibility(View.INVISIBLE);
			holder.mContinue.setVisibility(View.GONE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.VISIBLE);
			break;
		case DownloadManager.PAUSE:
			holder.mCurrentSize.setText(CommonUtil.parseSizeString(mDinfo
					.getPresize()));
			holder.mTotalSize.setText(CommonUtil.parseSizeString(mDinfo
					.getTotalsize()));
			holder.mSpector.setVisibility(View.VISIBLE);
			holder.mState.setText("下载暂停");
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			break;
		case DownloadManager.ERROR:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText("");
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			holder.mSpector.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mState.setText("出错终止");
			break;
		case DownloadManager.START:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText("正在计算文件大小");
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mSpector.setVisibility(View.INVISIBLE);
			holder.mState.setText("准备下载");
			break;
		case DownloadManager.DOWNLOADING:
			holder.mCurrentSize.setText(CommonUtil.parseSizeString(mDinfo
					.getPresize()));
			holder.mTotalSize.setText(CommonUtil.parseSizeString(mDinfo
					.getTotalsize()));
			holder.mSpector.setVisibility(View.VISIBLE);
			holder.mState.setText("正在下载");
			holder.mContinue.setVisibility(View.GONE);
			holder.mPause.setVisibility(View.VISIBLE);
			holder.mLaunch.setVisibility(View.GONE);
			break;
		}
		if (isCheckMode()) {// 选择模式下 选择框可见
			holder.mCheckBox.setVisibility(View.VISIBLE);
			holder.setChecked(mSelectMap.get(position) == null ? false
					: mSelectMap.get(position));
			holder.mContinue.setVisibility(View.GONE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
		} else {
			holder.mCheckBox.setVisibility(View.GONE);
		}

		holder.mProgressBar.setMax((int) mDinfo.getTotalsize());
		holder.mProgressBar.setProgress((int) mDinfo.getPresize());
		// 暂停下载按钮事件
		holder.mPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTaskList.get(position).pause();
			}
		});
		// 继续下载按钮事件
		holder.mContinue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mTaskList.get(position).resume();
			}
		});
		// 下载完成打开APK文件
		holder.mLaunch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				CommonUtil.installAPK(mActivity, mTaskList.get(position)
						.getmDinfo().getUrl());
			}
		});
		return convertView;
	}

	/**
	 * 添加下载任务
	 * 
	 * @param url
	 *            下载地址
	 * @param fileName
	 *            显示用文件名
	 */
	public void addDownloadItem(String url, String fileName, String imgUrl) {
		Dinfo mDinfo = new Dinfo(url, fileName, imgUrl);
		boolean hasSameTask = false;
		// 存在同一文件则不新建任务
		for (int i = 0; i < mMainInfoList.size(); i++) {
			Dinfo mInfo = (Dinfo) mMainInfoList.get(i);
			if (mInfo.getUrl().equals(url)
					&& mInfo.getFileName().equals(fileName)) {
				hasSameTask = true;
				mDinfo = mInfo;
				break;
			}
		}
		if (!mMainInfoList.contains(mDinfo) && !hasSameTask) {
			mMainInfoList.add(mDinfo);
			mDonwloadTask = new DownloadTask(mDinfo, this);
			mTaskList.add(mDonwloadTask);
			mDonwloadTask.start();
		} else {
			CommonUtil.showShortToast(mActivity, "该下载任务已经存在");
		}
	}

	public void removeDownloadItem(Dinfo mDinfo) {
		mMainInfoList.remove(mDinfo);
	}

	public class ViewHolder implements Checkable {
		public TextView mFileName;
		public TextView mState;
		public TextView mCurrentSize;
		public TextView mTotalSize;
		public TextView mSpector;
		public CheckBox mCheckBox;
		public ProgressBar mProgressBar;
		public Button mContinue;
		public Button mPause;
		public Button mLaunch;
		public ImageView mIcon;
		boolean isCheck;

		@Override
		public boolean isChecked() {
			// TODO Auto-generated method stub
			return isCheck;
		}

		@Override
		public void setChecked(boolean flag) {
			isCheck = flag;
			mCheckBox.setChecked(flag);
		}

		@Override
		public void toggle() {
			setChecked(!isCheck);
		}
	}

	@Override
	public void downloadStateChanged(int stateId, Dinfo mDinfo) {

		for (int i = 0; i < mTaskList.size(); i++) {
			mDonwloadTask = mTaskList.get(i);
			Dinfo mInfo = mDonwloadTask.getmDinfo();
			if (mInfo.getUrl().equals(mDinfo.getUrl())
					&& mInfo.getFileName().equals(mDinfo.getFileName())) {
				mMainInfoList.set(i, mDinfo);
				mTaskList.get(i).setmDinfo(mDinfo);
				setInfoToAppData();
				// 初次下载 插入下载记录
				if (stateId == DownloadManager.START
						&& !mDbmanager.IsDinfoExist(mDinfo)) {
					mDbmanager.insertInfo(mDinfo);
				} else {
					// 否则为更新数据库
					if (mDbmanager.updateInfoByWhere(mDinfo,
							"fileName='" + mDinfo.getFileName() + "' and url='"
									+ mDinfo.getUrl() + "'")) {
						CommonUtil
								.printDebugMsg("downloadStateChanged : state ="
										+ stateId);
					} else {
						CommonUtil
								.printDebugMsg("downloadStateChanged : update_db_failed");
					}
				}
			}
		}
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged(Map<Integer, Boolean> mSelectMap) {
		this.mSelectMap = mSelectMap;
		notifyDataSetChanged();
	}
}
