package com.enix.hoken.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.*;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.custom.download.utils.TextUtils;
import com.enix.hoken.encode.Encryp;
import com.enix.hoken.info.*;
import com.enix.hoken.sqllite.Dbmanager;
import com.enix.hoken.util.CommonUtil;

public class AppDataManager {

	private BaseApplication mApplication;
	private Encryp mEncryp;
	private String mCacheFileName;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private Dbmanager mDbmanager;
	private Activity mActvity;
	public static final String PREFERENCE_NAME = "download";
	public static final int URL_COUNT = 10;// URL集合最大条目数
	public static final String KEY_URL = "url";
	public static final String KEY_STATE = "state";
	public static final String KEY_FILENAME = "filename";

	public SharedPreferences getPreferences() {
		return mActvity.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_WORLD_WRITEABLE);
	}

	/**
	 * 从首选项存储器中获取指定KEY的VALUE字符串值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public String getStringFromPreferences(String key) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}

	/**
	 * 保存指定键值对象到首选项存储器
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void setStringToPreferences(String key, String value) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * 存储指定下载信息存储到首选项存储器
	 * 
	 * @param context
	 * @param index
	 * @param url
	 */
	public void storeDownloadInfo(int index, String url, String fileName,
			int state) {
		setStringToPreferences(KEY_URL + index, url);
		setStringToPreferences(KEY_FILENAME + index, fileName);
		setIntToPreferences(KEY_FILENAME + index, state);
	}

	/**
	 * 获取首选项存储器中的任务对象列表
	 * 
	 * @return
	 */
	public DinfoList getDownloadInfoList(
			Dinfo.DownloadStateChangedListener listener) {
		DinfoList mDinfoList = new DinfoList();
		Dinfo mDinfo;
		for (int i = 0; i < URL_COUNT; i++) {
			String url = getStringFromPreferences(KEY_URL + i);
			String filename = getStringFromPreferences(KEY_FILENAME + i);
			int state = getIntFromPreferences(KEY_STATE + i);
			if (!TextUtils.isEmpty(url) && !TextUtils.isEmpty(filename)) {
				state = state == Dinfo.COMPLETE ? Dinfo.COMPLETE : Dinfo.ABORT;
				mDinfo = new Dinfo(getStringFromPreferences(KEY_URL + i),
						getStringFromPreferences(KEY_FILENAME + i), state,
						listener);
				mDinfoList.add(mDinfo);

				CommonUtil
						.printDebugMsg("AppDataManager:getDownloadInfoList url = "
								+ url);
				CommonUtil
						.printDebugMsg("AppDataManager:getDownloadInfoList filename = "
								+ filename);
				CommonUtil
						.printDebugMsg("AppDataManager:getDownloadInfoList state = "
								+ state);

			}
		}
		return mDinfoList;
	}

	/**
	 * 从首选项存储器中清空指定键值
	 * 
	 * @param context
	 * @param index
	 */
	public void clearURLFromPreferences(int index) {
		setStringToPreferences(KEY_URL + index, "");
	}

	/**
	 * 获取指定INT型键值VALUE
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public int getIntFromPreferences(String key) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null)
			return preferences.getInt(key, 0);
		else
			return 0;
	}

	/**
	 * 存储INT型对象到首选项存储器
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void setIntToPreferences(String key, int value) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public long getLongFromPreferences(String key) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null)
			return preferences.getLong(key, 0L);
		else
			return 0L;
	}

	public void setLongToPreferences(String key, long value) {
		SharedPreferences preferences = getPreferences();
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public void addLongToPreferences(String key, long value) {
		setLongToPreferences(key, getLongFromPreferences(key) + value);
	}

	public AppDataManager(MainActivity mActivity) {
		this.mActvity = mActivity;
		this.mApplication = mActivity.mApplication;
		try {
			mEncryp = new Encryp();
		} catch (Exception e) {
			e.printStackTrace();
			mEncryp = null;
		}
	}

	public Dbmanager getmDbmanager() {
		return mDbmanager;
	}

	public void setmDbmanager(Dbmanager mDbmanager) {
		this.mDbmanager = mDbmanager;
	}

	public AppDataManager(Activity mActivity) {
		this.mActvity = mActivity;
		this.mApplication = (BaseApplication) mActivity.getApplication();
		try {
			mEncryp = new Encryp();
		} catch (Exception e) {
			e.printStackTrace();
			mEncryp = null;
		}
	}

	/**
	 * 返回指定的INFO缓存文件名
	 * 
	 * @param infoID
	 * @return
	 */
	private String getCacheFilePath(int infoID) {
		String strTbl = null;
		switch (infoID) {
		case InfoSession.TINFO:
			strTbl = InfoSession.TBL_NAME_TINFO;
			break;
		case InfoSession.BINFO:
			strTbl = InfoSession.TBL_NAME_BINFO;
			break;
		case InfoSession.CINFO:
			strTbl = InfoSession.TBL_NAME_CINFO;
			break;
		case InfoSession.SINFO:
			strTbl = InfoSession.TBL_NAME_SINFO;
			break;
		case InfoSession.GROUP:
			strTbl = InfoSession.TBL_NAME_GROUP;
			break;
		case InfoSession.PINFO:
			strTbl = InfoSession.TBL_NAME_PINFO;
			break;
		case InfoSession.RINFO:
			strTbl = InfoSession.TBL_NAME_RINFO;
			break;
		case InfoSession.LINFO:
			strTbl = InfoSession.TBL_NAME_LINFO;
			break;
		case InfoSession.PINFOLIST:
			strTbl = InfoSession.TBL_NAME_PINFOLIST;
			break;
		case InfoSession.RINFOLIST:
			strTbl = InfoSession.TBL_NAME_RINFOLIST;
			break;
		case InfoSession.LINFOLIST:
			strTbl = InfoSession.TBL_NAME_LINFOLIST;
			break;
		case InfoSession.SINFOLIST:
			strTbl = InfoSession.TBL_NAME_SINFOLIST;
			break;
		case InfoSession.JINFOLIST:
			strTbl = InfoSession.TBL_NAME_JINFOLIST;
			break;
		case InfoSession.JINFO:
			strTbl = InfoSession.TBL_NAME_JINFO;
			break;
		case InfoSession.CINFOLIST:
			strTbl = InfoSession.TBL_NAME_CINFOLIST;
			break;
		case InfoSession.JINFOMAPLIST:
			strTbl = InfoSession.TBL_NAME_JINFOMAPLIST;
			break;
		default:
			return null;
		}
		// 如果存在加密类实例
		if (mEncryp != null) {
			mCacheFileName = mEncryp.getSha(strTbl);
		} else {
			mCacheFileName = strTbl;
		}
		mCacheFileName = strTbl;
		return CommonUtil.getCacheFolder(mActvity) + mCacheFileName;
	}

	/**
	 * 根据指定的表对象进行序列化保存到程序CACHE目录
	 * 
	 * @param infoID
	 * @return 返回保存的完整文件路径名
	 */
	public String serializerInfo(int infoID) {
		Log.i("DEBUG", "serializerInfo_START");
		String cacheFilePath = getCacheFilePath(infoID);
		if (cacheFilePath != null && CommonUtil.sdcardMounted()) {
			try {
				out = new ObjectOutputStream(
						new FileOutputStream(cacheFilePath));
				// 将程序SESSION中指定表数据仅需序列化保存
				out.writeObject(getInfoFromApp(infoID));
				Log.i("DEBUG", "serializerInfo_SUCESS");
				return cacheFilePath;
			} catch (Exception e) {
				Log.e("DEBUG", "serializerInfo_FAILED");
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (IOException e) {
					out = null;
					e.printStackTrace();
				}

			}
		}
		return null;
	}

	public Object parseSerializedInfo(int infoID) {
		Log.i("DEBUG", "parseSerializedInfo_START");
		String cacheFilePath = getCacheFilePath(infoID);
		if (cacheFilePath != null && CommonUtil.sdcardMounted()) {
			try {
				in = new ObjectInputStream(new FileInputStream(cacheFilePath));

				Object infoResult = null;
				infoResult = in.readObject();

				return infoResult;
			} catch (Exception e) {
				Log.e("DEBUG", "parseSerializedInfo_FAILED");
				e.printStackTrace();
				return null;
			} finally {
				try {
					if (in != null)
						in.close();
				} catch (IOException e) {
					in = null;
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	/**
	 * 添加INFO对象到APP 返回TRUE :添加成功
	 * 
	 * @param infoId
	 * @param mMainInfo
	 * @return
	 */
	public boolean addInfo(int infoId, MainInfo mMainInfo) {
		if (mMainInfo != null) {
			try {
				switch (infoId) {
				case InfoSession.TINFO:
					return mApplication.getTinfoList().add((Tinfo) mMainInfo);
				case InfoSession.BINFO:
					return mApplication.getBinfoList().add((Binfo) mMainInfo);
				case InfoSession.CINFO:
					return mApplication.getCinfoList().add((Cinfo) mMainInfo);
				case InfoSession.SINFO:
					return mApplication.getSinfoList().add((Sinfo) mMainInfo);
				case InfoSession.GROUP:
					return mApplication.getGroupList().add((Group) mMainInfo);
				case InfoSession.PINFO:
					return mApplication.getPinfoList().add((Pinfo) mMainInfo);
				case InfoSession.JINFO:
					return mApplication.getJinfoList().add((Jinfo) mMainInfo);
				case InfoSession.RINFO:
					return mApplication.getJinfoList().add((Jinfo) mMainInfo);
				case InfoSession.LINFO:
					return mApplication.getJinfoList().add((Jinfo) mMainInfo);
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 删除INFO对象 返回True :删除成功
	 * 
	 * @param infoId
	 * @param mMainInfo
	 * @return
	 */
	public boolean deleteInfo(int infoId, MainInfo mMainInfo) {
		if (mMainInfo != null) {
			try {
				switch (infoId) {
				case InfoSession.TINFO:
					return mApplication.getTinfoList()
							.remove((Tinfo) mMainInfo);
				case InfoSession.BINFO:
					return mApplication.getBinfoList()
							.remove((Binfo) mMainInfo);
				case InfoSession.CINFO:
					return mApplication.getCinfoList()
							.remove((Cinfo) mMainInfo);
				case InfoSession.SINFO:
					return mApplication.getSinfoList()
							.remove((Sinfo) mMainInfo);
				case InfoSession.GROUP:
					return mApplication.getGroupList()
							.remove((Group) mMainInfo);
				case InfoSession.PINFO:
					return mApplication.getPinfoList()
							.remove((Pinfo) mMainInfo);
				case InfoSession.JINFO:
					return mApplication.getJinfoList()
							.remove((Jinfo) mMainInfo);
				case InfoSession.RINFO:
					return mApplication.getRinfoList()
							.remove((Jinfo) mMainInfo);
				case InfoSession.LINFO:
					return mApplication.getLinfoList()
							.remove((Jinfo) mMainInfo);
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 根据CID从数据库获取SINFOLIST
	 * 
	 * @param cid
	 */
	public void reBindSinfoList(int cid) {
		setInfoToApp(mDbmanager.findSinfoListByCid(cid), InfoSession.SINFOLIST);
	}

	/**
	 * 更新INFO对象 返回TRUE:更新成功
	 * 
	 * @param infoId
	 * @param mMainInfo
	 * @return
	 */
	public boolean updateInfo(int infoId, MainInfo mMainInfo) {
		if (mMainInfo != null) {
			try {
				switch (infoId) {
				case InfoSession.TINFO:
					return mApplication.getTinfoList().set(mMainInfo);
				case InfoSession.BINFO:
					return mApplication.getBinfoList().set(mMainInfo);
				case InfoSession.CINFO:
					return mApplication.getCinfoList().set(mMainInfo);
				case InfoSession.SINFO:
					return mApplication.getSinfoList().set(mMainInfo);
				case InfoSession.GROUP:
					return mApplication.getGroupList().set(mMainInfo);
				case InfoSession.PINFO:
					return mApplication.getPinfoList().set(mMainInfo);
				case InfoSession.JINFO:
					return mApplication.getJinfoList().set(mMainInfo);
				case InfoSession.RINFO:
					return mApplication.getRinfoList().set(mMainInfo);
				case InfoSession.LINFO:
					return mApplication.getLinfoList().set(mMainInfo);
				}
				return false;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		} else {
			return false;
		}
	}

	public Object getInfoFromApp(int infoID) {
		switch (infoID) {
		case InfoSession.TINFO:
			return mApplication.getTinfo();
		case InfoSession.BINFO:
			return mApplication.getBinfo();
		case InfoSession.CINFO:
			return mApplication.getCinfo();
		case InfoSession.SINFO:
			return mApplication.getSinfo();
		case InfoSession.GROUP:
			return mApplication.getGroup();
		case InfoSession.PINFO:
			return mApplication.getPinfo();
		case InfoSession.JINFO:
			return mApplication.getJinfo();
		case InfoSession.PINFOLIST:
			return mApplication.getPinfoList();
		case InfoSession.RINFO:
			return mApplication.getRinfo();
		case InfoSession.RINFOLIST:
			return mApplication.getRinfoList();
		case InfoSession.LINFO:
			return mApplication.getLinfo();
		case InfoSession.LINFOLIST:
			return mApplication.getLinfoList();
		case InfoSession.SINFOLIST:
			return mApplication.getSinfoList();
		case InfoSession.COMMONINFO:
			return mApplication.getCommonInfo();
		case InfoSession.SCHEDULELIST:
			return mApplication.getScheduleGroupList();
		case InfoSession.JINFOLIST:
			return mApplication.getJinfoList();
		case InfoSession.CINFOLIST:
			return mApplication.getCinfoList();
		case InfoSession.JINFOMAPLIST:
			return mApplication.getJinfoMapList();
		}
		return null;
	}

	public void setInfoToApp(Object obj_info, int infoID) {
		switch (infoID) {
		case InfoSession.TINFO:
			mApplication.setTinfo((Tinfo) obj_info);
			break;
		case InfoSession.BINFO:
			mApplication.setBinfo((Binfo) obj_info);
			break;
		case InfoSession.CINFO:
			mApplication.setCinfo((Cinfo) obj_info);
			break;
		case InfoSession.SINFO:
			mApplication.setSinfo((Sinfo) obj_info);
			break;
		case InfoSession.PINFO:
			mApplication.setPinfo((Pinfo) obj_info);
			break;
		case InfoSession.JINFO:
			mApplication.setJinfo((Jinfo) obj_info);
			break;
		case InfoSession.RINFO:
			mApplication.setRinfo((Rinfo) obj_info);
			break;
		case InfoSession.LINFO:
			mApplication.setLinfo((Linfo) obj_info);
			break;
		case InfoSession.GROUP:
			mApplication.setGroup((Group) obj_info);
			break;
		case InfoSession.PINFOLIST:
			mApplication.setPinfoList((PinfoList) obj_info);
			break;
		case InfoSession.SINFOLIST:
			mApplication.setSinfoList((SinfoList) obj_info);
			break;
		case InfoSession.LESSONLIST:
			mApplication.setLesson_group_list((GroupList) obj_info);
			break;
		case InfoSession.COMMONINFO:
			mApplication.setCommonInfo((CommonInfo) obj_info);
			break;
		case InfoSession.SCHEDULELIST:
			mApplication.setScheduleGroupList((GroupList) obj_info);
			break;
		case InfoSession.JINFOLIST:
			mApplication.setJinfoList((JinfoList) obj_info);
			break;
		case InfoSession.RINFOLIST:
			mApplication.setRinfoList((RinfoList) obj_info);
			break;
		case InfoSession.LINFOLIST:
			mApplication.setLinfoList((LinfoList) obj_info);
			break;
		case InfoSession.CINFOLIST:
			mApplication.setCinfoList((CinfoList) obj_info);
			break;
		case InfoSession.JINFOMAPLIST:
			mApplication.setJinfoMapList((HashMap<String, JinfoList>) obj_info);
			break;
		}
	}

}
