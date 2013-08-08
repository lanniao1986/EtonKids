package com.enix.hoken.basic;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.item.AnimationController;
import com.enix.hoken.sqllite.Dbmanager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class MainAdapter extends BaseAdapter {
	public BaseApplication mApplication;
	public MainActivity mActivity;
	public AppDataManager mAppDataManager;
	public Dbmanager mDbmanager;
	public ActionHandler mActionHandler;
	public MainInfoList mMainInfoList;
	public AnimationController animationController;

	public MainAdapter(MainActivity mActivity, MainInfoList mMainInfoList) {
		this.mApplication = mActivity.mApplication;
		this.mAppDataManager = mActivity.mAppDataManager;
		this.mActivity = mActivity;
		this.mDbmanager = mActivity.mDbmanager;
		this.mActionHandler = mActivity.mActionHandler;
		this.mMainInfoList = mMainInfoList;
		this.animationController = mActivity.animationController;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMainInfoList.size();
	}

	@Override
	public Object getItem(int i) {
		// TODO Auto-generated method stub
		return mMainInfoList.get(i);
	}

	@Override
	public long getItemId(int i) {
		// TODO Auto-generated method stub
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 同步操作单一对象到数据库
	 * 
	 * @param position
	 * @param mMainInfo
	 * @return
	 */
	public boolean bindDbSingle(int position, MainInfo mMainInfo) {

		return false;
	}

	public boolean bindDbList(MainInfoList mInfoList) {

		return false;
	}

	/**
	 * 删除单一指定对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean deleteItem(MainInfo mMainInfo) {
		return mDbmanager.deleteInfo(mMainInfo);
	}

	/**
	 * 插入单一指定对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean insertItem(MainInfo mMainInfo) {
		return mDbmanager.insertInfo(mMainInfo);
	}

	/**
	 * 更新单一指定对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean updateItem(MainInfo mMainInfo) {
		return mDbmanager.updateInfo(mMainInfo);
	}

	/**
	 * 删除指定集合内的数据
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean deleteList(MainInfoList<MainInfo> mInfoList) {
		return mDbmanager.updateInfoList(mInfoList);
	}

	/**
	 * 插入指定集合内的数据
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean insertList(MainInfoList<MainInfo> mInfoList) {
		return mDbmanager.insertInfoList(mInfoList);
	}

	/**
	 * 更新指定集合内的数据
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean updateList(MainInfoList<MainInfo> mInfoList) {
		return mDbmanager.updateInfoList(mInfoList);
	}

	/**
	 * 移动集合中的对象位置
	 * 
	 * @param fromPosition
	 * @param toPosition
	 */
	public void moveItem(int fromPosition, int toPosition) {
		if (fromPosition >= 0 && fromPosition <= mMainInfoList.size()
				&& toPosition >= 0 && toPosition <= mMainInfoList.size()
				&& fromPosition != toPosition) {
			MainInfo mInfo = (MainInfo) mMainInfoList.get(fromPosition);
			mMainInfoList.add(toPosition, mInfo);
			int index = fromPosition < toPosition ? fromPosition
					: fromPosition + 1;
			mMainInfoList.remove(index);
		}

	}

	public abstract class ViewHoder {

	}
}
