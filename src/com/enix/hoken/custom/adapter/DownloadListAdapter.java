package com.enix.hoken.custom.adapter;

import java.util.*;

import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.R;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;

public class DownloadListAdapter extends MainAdapter implements
		Dinfo.DownloadStateChangedListener {

	private Dinfo mDinfo;

	public DownloadListAdapter(MainActivity mActivity, DinfoList mMainInfoList) {
		super(mActivity, mMainInfoList);
		initTaskList();
	}

	/**
	 * 从首选项中获取历史任务
	 */
	private void initTaskList() {
		mMainInfoList = mAppDataManager.getDownloadInfoList(this);
		notifyDataSetChanged();
	}

	private void setInfoToAppData(int index, Dinfo mInfo) {

		mAppDataManager.storeDownloadInfo(index, mInfo.getUrl(),
				mInfo.getFileName(), mInfo.getState());
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
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mDinfo = (Dinfo) mMainInfoList.get(position);
		int state = mDinfo.getState();
		holder.mFileName.setText(mDinfo.getFileName());
		switch (state) {
		case Dinfo.ABORT:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText("");
			holder.mSpector.setVisibility(View.INVISIBLE);
			holder.mState.setText("任务终止");
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			break;
		case Dinfo.COMPLETE:
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
		case Dinfo.PAUSE:
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
		case Dinfo.ERROR:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText("");
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			holder.mSpector.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mState.setText("出错终止");
			break;
		case Dinfo.READY:
			holder.mCurrentSize.setText("");
			holder.mTotalSize.setText("正在计算文件大小");
			holder.mContinue.setVisibility(View.VISIBLE);
			holder.mPause.setVisibility(View.GONE);
			holder.mLaunch.setVisibility(View.GONE);
			holder.mProgressBar.setVisibility(View.VISIBLE);
			holder.mSpector.setVisibility(View.INVISIBLE);
			holder.mState.setText("准备下载");
			break;
		case Dinfo.DOWNLOADING:
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
		holder.mProgressBar.setMax((int) mDinfo.getTotalsize());
		holder.mProgressBar.setProgress((int) mDinfo.getPresize());
		holder.mPause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mDinfo.pauseDownloadTask();
			}
		});
		holder.mContinue.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDinfo.getState() == Dinfo.ABORT) {
					mDinfo.restartDownloadTask();
				} else {
					mDinfo.startDownloadTask();
				}
			}
		});

		return convertView;
	}

	public void addDownloadItem(String url, String fileName) {
		Dinfo mDinfo = new Dinfo(url, fileName, this);
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
			mDinfo.startDownloadTask();
			setInfoToAppData(mMainInfoList.size(), mDinfo);
		} else {
			CommonUtil.showShortToast(mActivity, "该下载任务已经存在");
		}

	}

	public void removeDownloadItem(Dinfo mDinfo) {
		mMainInfoList.remove(mDinfo);
	}

	public class ViewHolder {
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
	}

	@Override
	public void downloadStateChanged(int stateId, Dinfo mDinfo) {
		switch (stateId) {
		case Dinfo.ABORT:

			break;
		case Dinfo.COMPLETE:

			break;
		case Dinfo.READY:

			break;
		default:
			break;
		}
		notifyDataSetChanged();
	}
}
