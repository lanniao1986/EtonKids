package com.enix.hoken.sqllite;

import java.util.HashMap;
import java.util.Set;

import net.tsz.afinal.FinalDb;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.custom.adapter.DesktopAdapter;
import com.enix.hoken.info.*;
import com.enix.hoken.util.*;

import android.content.Context;

public class Dbmanager {

	private BaseApplication mApplication;
	private Tinfo tinfo;
	private Binfo binfo;
	private Cinfo cinfo;
	private Sinfo sinfo;
	private FinalDb db;
	private Pinfo pinfo;
	private Group group;
	private Jinfo jinfo;
	private CommonInfo commonInfo;
	private BinfoList binfoList;
	private CinfoList cinfoList;
	private SinfoList sinfoList;
	private PinfoList pinfoList;
	private GroupList groupList;
	private TinfoList tinfoList;
	private JinfoList jinfoList;
	private MainInfoList<MainInfo> mMainInfoList;
	private AppDataManager mAppDataManager;

	public Dbmanager(MainActivity mActivity) {
		db = FinalDb.create(mActivity, DbHelper.DB_NAME, true);
		this.mApplication = mActivity.mApplication;
		commonInfo = mApplication.getCommonInfo();
	}

	public AppDataManager getmAppDataManager() {
		return mAppDataManager;
	}

	public void setmAppDataManager(AppDataManager mAppDataManager) {
		this.mAppDataManager = mAppDataManager;
	}

	private boolean copyDataBaseToLocal() {
		return CommonUtil.copyFile(
				"/data/data/com.enix.hoken/databases/Eton.db",
				"/sdcard/EtonKids/Eton.db");
	}

	/**
	 * 更新待办事项 复制数据库到本地成功返回true
	 * 
	 * @param noteTips
	 * @return
	 */
	public boolean updateNoteTips(String noteTips) {
		group = commonInfo.getNoteTips();
		group.setG_detail(noteTips);
		db.update(group);
		if (copyDataBaseToLocal()) {
			return true;
		}
		return false;
	}

	/**
	 * 插入签到考勤表记录
	 * 
	 * @return
	 */
	public boolean insertMemberCheck() {
		try {
			jinfoList = (JinfoList) mAppDataManager
					.getInfoFromApp(InfoSession.JINFOLIST);
			for (Jinfo mJinfo : jinfoList) {
				db.save(mJinfo);
			}

			copyDataBaseToLocal();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据info判断要删除该表对应Id的数据,成功删除返回true
	 * 
	 * @param info
	 * @param id
	 * @return
	 */
	public boolean deleteDataById(int info, int id) {
		switch (info) {
		case InfoSession.SCHEDULELIST:
			db.deleteById(Group.class, id);
			break;
		default:
			break;
		}

		copyDataBaseToLocal();
		return resetInfo(info);// 删除后重新更新本地Info
	}

	public boolean resetInfo(int info) {
		switch (info) {
		case InfoSession.SCHEDULELIST:
			// 获取日程列表
			groupList.addAll(db.findAllByWhere(Group.class, "g_code='S_01'"));
			mAppDataManager.setInfoToApp(groupList, InfoSession.SCHEDULELIST);
			copyDataBaseToLocal();
			return true;
		default:
			return false;
		}
	}

	/**
	 * 删除指定单个对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean deleteInfo(MainInfo mMainInfo) {
		if (mMainInfo == null) {
			return false;
		}
		try {
			db.delete(mMainInfo);

			copyDataBaseToLocal();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 插入单个对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean insertInfo(MainInfo mMainInfo) {
		if (mMainInfo == null) {
			return false;
		}
		try {
			db.save(mMainInfo);

			copyDataBaseToLocal();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 删除SINFO 并同时更新SINFOLIST(CID)
	 * 
	 * @param sinfo
	 * @return
	 */
	public boolean deleteSinfo(Sinfo sinfo) {
		if (deleteInfo(sinfo)) {
			mAppDataManager.reBindSinfoList(commonInfo.getCid());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新SINFO 并同时更新SINFOLIST(CID)
	 * 
	 * @param sinfo
	 * @return
	 */
	public boolean updateSinfo(Sinfo sinfo) {
		if (updateInfo(sinfo)) {
			mAppDataManager.reBindSinfoList(commonInfo.getCid());
			return true;
		} else {
			return false;
		}
	}

	public boolean insertSinfo(Sinfo sinfo) {
		if (insertInfo(sinfo)) {
			mAppDataManager.reBindSinfoList(commonInfo.getCid());
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新单个对象
	 * 
	 * @param mMainInfo
	 * @return
	 */
	public boolean updateInfo(MainInfo mMainInfo) {
		if (mMainInfo == null) {
			return false;
		}
		try {
			db.update(mMainInfo);
			copyDataBaseToLocal();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根据条件语句更新单个对象数据
	 * 
	 * @param mMainInfo
	 * @param whereStr
	 * @return
	 */
	public boolean updateInfoByWhere(MainInfo mMainInfo, String whereStr) {
		if (mMainInfo == null) {
			return false;
		}
		try {
			db.update(mMainInfo, whereStr);
			copyDataBaseToLocal();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 逐条删除列表中指定对象
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean deleteInfoList(MainInfoList<MainInfo> mInfoList) {

		for (MainInfo mMainInfo : mInfoList) {
			if (deleteInfo(mMainInfo)) {
				continue;
			} else {
				return false;
			}
		}

		copyDataBaseToLocal();
		return true;
	}

	/**
	 * 逐条更新列表中指定对象
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean updateInfoList(MainInfoList<MainInfo> mInfoList) {

		for (MainInfo mMainInfo : mInfoList) {
			if (updateInfo(mMainInfo)) {
				continue;
			} else {
				return false;
			}
		}

		copyDataBaseToLocal();
		return true;
	}

	/**
	 * 逐条插入或更新列表中指定对象
	 * 
	 * @param mInfoList
	 * @return
	 */
	public boolean insertInfoList(MainInfoList<MainInfo> mInfoList) {
		for (MainInfo mMainInfo : mInfoList) {
			if (insertInfo(mMainInfo)) {
				continue;
			} else {
				return false;
			}
		}

		copyDataBaseToLocal();
		return true;
	}

	/**
	 * 无条件查找全部指定对象的表数据
	 * 
	 * @param clazz
	 * @return
	 */
	public MainInfoList<MainInfo> findAll(Class<MainInfo> clazz) {
		mMainInfoList = new MainInfoList<MainInfo>();
		mMainInfoList.addAll(db.findAll(clazz));
		return mMainInfoList;
	}

	/**
	 * 无条件查找全部指定对象的表数据,并排序指定字段
	 * 
	 * @param clazz
	 *            MainInfo 对象orderBy 指定字段
	 * @return
	 */
	public MainInfoList<MainInfo> findAll(Class<MainInfo> clazz,
			java.lang.String orderBy) {
		mMainInfoList = new MainInfoList<MainInfo>();
		mMainInfoList.addAll(db.findAll(clazz, orderBy));
		return mMainInfoList;
	}

	/**
	 * 指定条件查找全部对象的表数据
	 * 
	 * @param clazz
	 *            MainInfo 对象 strWhere 指定条件
	 * @return
	 */
	public MainInfoList<MainInfo> findAllByWhere(Class<MainInfo> clazz,
			java.lang.String strWhere) {
		mMainInfoList = new MainInfoList<MainInfo>();
		mMainInfoList.addAll(db.findAllByWhere(clazz, strWhere));
		return mMainInfoList;
	}

	/**
	 * 获取同名下载任务是否在数据库中已存在
	 * 
	 * @param mDinfo
	 * @return
	 */
	public boolean IsDinfoExist(Dinfo mDinfo) {
		try {
			if (db.findAllByWhere(
					Dinfo.class,
					"fileName='" + mDinfo.getFileName() + "' and url='"
							+ mDinfo.getUrl() + "'").size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 指定条件查找全部对象的表数据
	 * 
	 * @param clazz
	 *            MainInfo 对象 strWhere 指定条件 orderBy 指定字段
	 * @return
	 */
	public MainInfoList<MainInfo> findAllByWhere(Class<MainInfo> clazz,
			java.lang.String strWhere, String orderBy) {
		mMainInfoList = new MainInfoList<MainInfo>();
		mMainInfoList.addAll(db.findAllByWhere(clazz, strWhere, orderBy));
		return mMainInfoList;
	}

	/**
	 * 根据CID查询获取SINFO
	 * 
	 * @param cid
	 * @return
	 */
	public SinfoList findSinfoListByCid(int cid) {
		SinfoList mSinfoList = new SinfoList();
		mSinfoList.addAll(db.findAllByWhere(Sinfo.class, "c_id=" + cid));
		return mSinfoList;
	}

	/**
	 * 根据SID查询获取PINFO
	 * 
	 * @param cid
	 * @return
	 */
	public PinfoList findPinfoListBySid(int sid) {
		PinfoList mPinfoList = new PinfoList();
		mPinfoList.addAll(db.findAllByWhere(Pinfo.class, "s_id=" + sid));
		return mPinfoList;
	}

}
