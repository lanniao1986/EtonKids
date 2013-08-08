package com.enix.hoken.custom.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.TextView;
import com.enix.hoken.R;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.common.*;
import com.enix.hoken.custom.item.AnimationController;
import com.enix.hoken.custom.item.BadgeView;
import com.enix.hoken.custom.item.LocalImageLoaderTask;
import com.enix.hoken.info.Jinfo;
import com.enix.hoken.info.JinfoList;
import com.enix.hoken.info.Sinfo;
import com.enix.hoken.info.SinfoList;
import com.enix.hoken.util.BitmapCache;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.util.ImageTools;

public class MemberGridAdapter extends MainAdapter {

	private SinfoList sinfoList;
	private Sinfo sinfo;
	private Jinfo jinfo;
	public ArrayList<Sinfo> nameList_Arrived;
	public ArrayList<Sinfo> nameList_Dayoff;
	public int countArrived = 0;
	public int countDayoff = 0;
	public int disCount;
	private BadgeView mBadgeView;
	private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	public boolean checkMode = false;

	public static final int MEMBER_STATE_ARRIVED = 2;
	public static final int MEMBER_STATE_DAYOFF = 1;
	public static final int MEMBER_STATE_DEFALUT = 0;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");

	public MemberGridAdapter(MainActivity mActivity) {
		super(mActivity, mActivity.mApplication.getJinfoList());

		sinfoList = (SinfoList) mAppDataManager
				.getInfoFromApp(InfoSession.SINFOLIST);
		// 初始化从缓存恢复数据
		if (!recoveryFormCache()) {
			mMainInfoList = mApplication.getJinfoList();
		}
		jinfo = null;
		boolean has = false;
		// 学生列表与考勤表对比 两表有差异的则考勤表追加
		if (mMainInfoList.size() > 0) {
			for (int i = 0; i < sinfoList.size(); i++) {
				has = false;
				sinfo = sinfoList.get(i);
				for (Jinfo mjinfo : (JinfoList) mMainInfoList) {
					if (sinfo.getId() == mjinfo.getS_id())
						has = true;
				}
				if (!has) {
					jinfo = new Jinfo();
					jinfo.setS_id(sinfo.getId());
					jinfo.setS_name(sinfo.getS_name());
					mMainInfoList.add(jinfo);
				}
			}
		} else {
			// 初始考勤表 则逐条赋值
			for (Sinfo mSinfo : sinfoList) {
				jinfo = new Jinfo();
				jinfo.setS_id(mSinfo.getId());
				jinfo.setS_name(mSinfo.getS_name());
				mMainInfoList.add(jinfo);
			}
		}
		nameList_Arrived = new ArrayList<Sinfo>();
		nameList_Dayoff = new ArrayList<Sinfo>();
	}

	public int getCountArrived() {
		return countArrived;
	}

	public void setCountArrived(int countArrived) {
		this.countArrived = countArrived;
	}

	public int getCountDayoff() {
		return countDayoff;
	}

	public boolean isCheckMode() {
		return checkMode;
	}

	public void setCheckMode(boolean checkMode) {
		this.checkMode = checkMode;
	}

	public void setCountDayoff(int countDayoff) {
		this.countDayoff = countDayoff;
	}

	public Map<Integer, Boolean> getmSelectMap() {
		return mSelectMap;
	}

	public void setmSelectMap(Map<Integer, Boolean> mSelectMap) {
		this.mSelectMap = mSelectMap;
	}

	public int getDisCount() {
		return disCount;
	}

	public void setDisCount(int disCount) {
		this.disCount = disCount;
	}

	/**
	 * 从缓存中恢复设置状态
	 * 
	 * @return
	 */
	public boolean recoveryFormCache() {
		HashMap<String, JinfoList> mapList = (HashMap<String, JinfoList>) mAppDataManager
				.parseSerializedInfo(InfoSession.JINFOMAPLIST);
		// 以当天日期为KEY 从缓存MAP中获取对应日期的考勤记录表数据对象
		if (mapList != null && mapList.size() > 0) {
			// 缓存中存在MAP对象则加入到SESSION
			mAppDataManager.setInfoToApp(mapList, InfoSession.JINFOMAPLIST);
			mMainInfoList = mapList.get(new SimpleDateFormat("yyyyMMdd")
					.format(new Date()));
		}
		if (mMainInfoList != null && mMainInfoList.size() > 0) {
			return true;
		}
		return false;
	}

	/*
	 * 根据指定SID获取该对象所在的LIST索引位置
	 */
	public int getSinfoIndexBySid(int sid) {
		for (int i = 0; i < sinfoList.size(); i++) {
			if (sinfoList.get(i).getId() == sid)
				return i;
		}
		return -1;
	}

	public JinfoList getJinfoList() {
		return (JinfoList) mMainInfoList;
	}

	public void setJinfoList(JinfoList jinfoList) {
		this.mMainInfoList = jinfoList;
	}

	public int getCount() {
		return sinfoList.size();
	}

	public Object getItem(int position) {
		return sinfoList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	/**
	 * 切换设置同一SID成员的签到状态图标
	 * 
	 * @param position
	 * @param mState
	 */
	public void toggleState(int position, ImageView mState) {
		for (int i = 0; i < mMainInfoList.size(); i++) {
			if (sinfoList.get(position).getId() == ((JinfoList) mMainInfoList)
					.get(i).getS_id()) {
				switch (((JinfoList) mMainInfoList).get(i).getJ_state()) {
				case MEMBER_STATE_DEFALUT:
					((JinfoList) mMainInfoList).get(i).setJ_state(
							MEMBER_STATE_ARRIVED);
					mState.setImageResource(R.drawable.qiandao);
					mState.setVisibility(View.VISIBLE);
					break;
				case MEMBER_STATE_ARRIVED:
					((JinfoList) mMainInfoList).get(i).setJ_state(
							MEMBER_STATE_DAYOFF);
					mState.setImageResource(R.drawable.qingjia);
					mState.setVisibility(View.VISIBLE);
					break;
				case MEMBER_STATE_DAYOFF:
					((JinfoList) mMainInfoList).get(i).setJ_state(
							MEMBER_STATE_DEFALUT);
					mState.setVisibility(View.GONE);
					break;
				}
				// 点击后记录更新的时间
				((JinfoList) mMainInfoList).get(i).setJ_checktime(
						format.format(new Date()));
				break;
			}
		}

		if (mState.getVisibility() == View.VISIBLE) {
			ScaleAnimation animation = new ScaleAnimation(2, 1, 2, 1,
					Animation.RELATIVE_TO_SELF, 0.5f,
					Animation.RELATIVE_TO_SELF, 0.5f);
			animation.setInterpolator(new BounceInterpolator());
			animation.setDuration(1000);
			mState.setAnimation(animation);
			animation.start();
		}
		setStateCount();
	}

	/**
	 * 显示签到状态图标
	 * 
	 * @param position
	 * @param mState
	 */
	private void showState(int sid, ImageView mState) {
		for (int i = 0; i < mMainInfoList.size(); i++) {
			if (sid == ((JinfoList) mMainInfoList).get(i).getS_id()) {
				switch (((JinfoList) mMainInfoList).get(i).getJ_state()) {
				case MEMBER_STATE_DEFALUT:
					mState.setVisibility(View.GONE);
					break;
				case MEMBER_STATE_ARRIVED:
					mState.setImageResource(R.drawable.qiandao);
					mState.setVisibility(View.VISIBLE);
					break;
				case MEMBER_STATE_DAYOFF:
					mState.setImageResource(R.drawable.qingjia);
					mState.setVisibility(View.VISIBLE);
					break;
				}
			}
		}
	}

	/**
	 * 重置所有签到状态
	 */
	public void resetState() {
		for (int i = 0; i < mMainInfoList.size(); i++) {
			((JinfoList) mMainInfoList).get(i).setJ_state(MEMBER_STATE_DEFALUT);
		}
		setStateCount();
		notifyDataSetChanged();
	}

	/**
	 * 根据SID获取SINFO对象
	 * 
	 * @param sid
	 * @return
	 */
	private Sinfo getSinfoBySid(int sid) {
		for (Sinfo mSinfo : sinfoList) {
			if (mSinfo.getId() == sid)
				return mSinfo;
		}
		return null;
	}

	/**
	 * 重新计数
	 */
	public void setStateCount() {
		nameList_Arrived.clear();
		nameList_Dayoff.clear();
		countArrived = 0;
		countDayoff = 0;
		disCount = 0;
		for (Jinfo mJinfo : ((JinfoList) mMainInfoList)) {
			switch (mJinfo.getJ_state()) {
			case MEMBER_STATE_DEFALUT:
				disCount++;
				break;
			case MEMBER_STATE_ARRIVED:
				countArrived++;
				nameList_Arrived.add(getSinfoBySid(mJinfo.getS_id()));
				break;
			case MEMBER_STATE_DAYOFF:
				countDayoff++;
				nameList_Dayoff.add(getSinfoBySid(mJinfo.getS_id()));
				break;
			}
		}
	}

	public void notifyDataSetChanged(Map<Integer, Boolean> mSelectMap) {
		this.mSelectMap = mSelectMap;
		notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {

		sinfoList = (SinfoList) mAppDataManager
				.getInfoFromApp(InfoSession.SINFOLIST);

		super.notifyDataSetChanged();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.member_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.member_avatar);
			holder.mName = (TextView) convertView
					.findViewById(R.id.member_name);
			holder.mState = (ImageView) convertView
					.findViewById(R.id.member_state);
			holder.mCheck = (CheckBox) convertView
					.findViewById(R.id.member_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		sinfo = sinfoList.get(position);
		// 获取成员头像
		Drawable bitmap = ImageTools.bitmapToDrawable(BitmapCache.getInstance()
				.getBitmap(
						CommonUtil.getAvatarPath(mActivity, sinfo.getS_head()),
						false));
		// 不存在则设置默认头像图片
		if (bitmap != null) {
			holder.mAvatar.setImageDrawable(bitmap);
		} else {
			holder.mAvatar.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.default_avtar));
		}
		holder.mName.setText(sinfo.getS_name());
		showState(sinfo.getId(), holder.mState);
		if (isCheckMode()) {// 选择模式下 选择框可见
			holder.mCheck.setVisibility(View.VISIBLE);
			holder.setChecked(mSelectMap.get(position) == null ? false
					: mSelectMap.get(position));
		} else {
			holder.mCheck.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder implements Checkable {
		ImageView mAvatar;
		TextView mName;
		ImageView mState;
		CheckBox mCheck;
		boolean isCheck;

		@Override
		public boolean isChecked() {
			// TODO Auto-generated method stub
			return isCheck;
		}

		@Override
		public void setChecked(boolean checked) {
			isCheck = checked;
			mCheck.setChecked(checked);
		}

		@Override
		public void toggle() {
			setChecked(!isCheck);
		}

	}

}
