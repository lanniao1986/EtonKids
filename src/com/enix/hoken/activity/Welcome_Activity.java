/**
 * 
 */
package com.enix.hoken.activity;

import com.enix.hoken.R;
import com.enix.hoken.info.Binfo;
import com.enix.hoken.info.BinfoList;
import com.enix.hoken.info.Cinfo;
import com.enix.hoken.info.CommonInfo;
import com.enix.hoken.info.Group;
import com.enix.hoken.info.GroupList;
import com.enix.hoken.info.Pinfo;
import com.enix.hoken.info.PinfoList;
import com.enix.hoken.info.Sinfo;
import com.enix.hoken.info.SinfoList;
import com.enix.hoken.info.Tinfo;
import com.enix.hoken.sqllite.DbHelper;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import net.tsz.afinal.FinalDb;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author gumc
 * 
 */
public class Welcome_Activity extends Activity implements Runnable {
	static final String Activity_ID = "Welcome_Activity";

	SQLiteDatabase sqldb;
	DbHelper helper;
	final ContentValues cv = new ContentValues();

	private Tinfo tinfo;
	private Binfo binfo;
	private Cinfo cinfo;
	private Sinfo sinfo;
	private FinalDb db;
	private Pinfo pinfo;
	private Group group;
	private AppDataManager appdata;
	private CommonInfo comminfo;
	private BaseApplication mApplication;
	private SinfoList mSinfoList;
	public SinfoList getmSinfoList() {
		return mSinfoList;
	}

	public void setmSinfoList(SinfoList mSinfoList) {
		this.mSinfoList = mSinfoList;
	}

	private RinfoList mRinfoList;
	private LinfoList mLinfoList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// /测试用快捷代码

		DisplayMetrics metric = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(metric);
		mApplication = (BaseApplication) getApplication();
		mApplication.setMetric(metric);
		mApplication.setScreenHeight(metric.heightPixels);
		mApplication.setScreenWidth(metric.widthPixels);
		appdata = new AppDataManager(this);
		helper = new DbHelper(Welcome_Activity.this, DbHelper.DB_NAME, null,
				DbHelper.DB_VERSION);
		sqldb = helper.getDataBase();
		helper.close();
		helper = null;

		// CommonUtil.copyFile("/sdcard/EtonKids/Eton.db","/data/data/com.enix.hoken/databases/Eton.db");
		db = db.create(this, DbHelper.DB_NAME, true);
		initData();
		// 默认总是复制一份数据库到内存卡
		CommonUtil.copyFile("/data/data/com.enix.hoken/databases/Eton.db",
				"/sdcard/EtonKids/Eton.db");
		if (setDBDataToApp()) {
			startActivity(new Intent(Welcome_Activity.this,
					DesktopActivity.class));
			// startActivity(new Intent(Welcome_Activity.this,
			// ChartActivity.class));
			finish();
		} else {
			CommonUtil.showShortToast(this, "初始化数据错误!请检查默认数据库");
		}

		// setContentView(R.layout.lay_welcome);
		// new Thread(this).start();
	}

	public void run() {

		try {
			// 欢迎界面停留时间
			Thread.sleep(1000);
			startActivity(new Intent(Welcome_Activity.this,
					WelcomeGuide_Activity.class));
			finish();
		} catch (InterruptedException e) {
			Log.e(Activity_ID, "Welcome_crash");
			finish();
		}
	}

	private boolean setDBDataToApp() {
		BinfoList binfoList = new BinfoList();
		CinfoList cinfoList = new CinfoList();
		SinfoList sinfoList = new SinfoList();
		PinfoList pinfoList = new PinfoList();
		GroupList groupList = new GroupList();
		TinfoList tinfoList = new TinfoList();
		LinfoList linfoList = new LinfoList();
		RinfoList rinfoList = new RinfoList();
		binfoList.addAll(db.findAll(Binfo.class));
		try {
			if (binfoList != null && binfoList.size() > 0) {
				comminfo = new CommonInfo();
				// 封装基本信息表对象
				binfo = binfoList.get(0);
				appdata.setInfoToApp(binfo, InfoSession.BINFO);

				// 封装教师信息表对象 where _ID = ?
				tinfoList.addAll(db.findAllByWhere(Tinfo.class,
						"id=" + binfo.getT_id()));
				if (tinfoList != null && tinfoList.size() > 0) {
					tinfo = (Tinfo) tinfoList.get(0);
					appdata.setInfoToApp(tinfo, InfoSession.TINFO);
				}
				linfoList.addAll(db.findAll(Linfo.class));
				appdata.setInfoToApp(linfoList, InfoSession.LINFOLIST);
				rinfoList.addAll(db.findAll(Rinfo.class));
				appdata.setInfoToApp(rinfoList, InfoSession.RINFOLIST);
				// 封装班级信息表对象
				cinfoList.addAll(db.findAll(Cinfo.class));
				if (cinfoList != null && cinfoList.size() > 0) {
					appdata.setInfoToApp(cinfoList, InfoSession.CINFOLIST);
					cinfo = mApplication.getCinfoByCid(binfo.getC_id());
					appdata.setInfoToApp(cinfo, InfoSession.CINFO);
					sinfoList.addAll(db.findAllByWhere(Sinfo.class, "c_id='"
							+ binfo.getC_id() + "'"));
					appdata.setInfoToApp(sinfoList, InfoSession.SINFOLIST);
					comminfo.setClassMemberCount(sinfoList.size());
					comminfo.setClassName(cinfo.getC_name());
					comminfo.setCid(cinfo.getId());
				}
				// 封装课程组信息
				groupList = new GroupList();
				groupList.addAll(db.findAllByWhere(Group.class, "g_code='"
						+ tinfo.getL_gid() + "'"));
				if (groupList != null && groupList.size() > 0) {
					appdata.setInfoToApp(groupList, InfoSession.LESSONLIST);
					groupList = new GroupList();
				}
				// 封装所有家长信息表对象
				pinfoList.addAll(db.findAll(Pinfo.class));
				if (pinfoList != null && pinfoList.size() > 0) {
					appdata.setInfoToApp(pinfoList, InfoSession.PINFOLIST);
				}
				// 获取快捷备忘事项
				groupList = new GroupList();
				groupList.addAll(db
						.findAllByWhere(Group.class, "g_code='N_01'"));

				if (groupList != null) {
					group = groupList.get(0);
					comminfo.setNoteTips(group);
					groupList = new GroupList();
				}
				groupList.addAll(db.findAllByWhere(Group.class, "g_code='"
						+ binfo.getH_code() + "'"));
				group = (Group) groupList.get(0);
				comminfo.setCurrentTitle(cinfo.getC_name() + group.getG_value());
				appdata.setInfoToApp(comminfo, InfoSession.COMMONINFO);
				// 获取日程列表
				groupList = new GroupList();
				groupList.addAll(db
						.findAllByWhere(Group.class, "g_code='S_01'"));
				appdata.setInfoToApp(groupList, InfoSession.SCHEDULELIST);
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	private void initData() {

	}
}
