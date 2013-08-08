package com.enix.hoken.common;

import java.text.SimpleDateFormat;
import java.util.*;

import net.tsz.afinal.FinalBitmap;
import android.app.Application;
import android.util.DisplayMetrics;

import com.enix.hoken.R;
import com.enix.hoken.basic.MainInfoList;
import com.enix.hoken.info.*;

public class BaseApplication extends Application {
	private SinfoList sinfoList;
	private PinfoList pinfoList;
	private JinfoList jinfoList;
	private CinfoList cinfoList;
	private TinfoList tinfoList;
	private BinfoList binfoList;
	private GroupList groupList;
	private GroupList scheduleGroupList;
	private GroupList lessonGroupList;
	private Rinfo rinfo;
	private RinfoList rinfoList;
	private LinfoList linfoList;
	private Linfo linfo;
	private SinfoList selectedSinfoList;
	private DisplayMetrics metric;
	private int screenWidth;
	private int screenHeight;
	private Tinfo tinfo;
	private Binfo binfo;
	private Sinfo sinfo;
	private Pinfo pinfo;
	private Group group;
	private Cinfo cinfo;
	private Jinfo jinfo;

	private HashMap<String, JinfoList> jinfomaplist;

	public Rinfo getRinfo() {
		return rinfo;
	}

	public void setRinfo(Rinfo rinfo) {
		this.rinfo = rinfo;
	}

	public RinfoList getRinfoList() {
		return rinfoList;
	}

	public void setRinfoList(RinfoList rinfoList) {
		this.rinfoList = rinfoList;
	}

	public SinfoList getSelectedSinfoList() {
		return selectedSinfoList;
	}

	public void setSelectedSinfoList(SinfoList selectedSinfoList) {
		this.selectedSinfoList = selectedSinfoList;
	}

	public LinfoList getLinfoList() {
		return linfoList;
	}

	public void setLinfoList(LinfoList linfoList) {
		this.linfoList = linfoList;
	}

	public Linfo getLinfo() {
		return linfo;
	}

	public void setLinfo(Linfo linfo) {
		this.linfo = linfo;
	}

	public GroupList getGroupList() {
		return groupList;
	}

	public void setGroupList(GroupList groupList) {
		this.groupList = groupList;
	}

	public BinfoList getBinfoList() {
		return binfoList;
	}

	public void setBinfoList(BinfoList binfoList) {
		this.binfoList = binfoList;
	}

	public TinfoList getTinfoList() {
		return tinfoList;
	}

	public void setTinfoList(TinfoList tinfoList) {
		this.tinfoList = tinfoList;
	}

	public HashMap<String, JinfoList> getJinfoMapList() {
		if (jinfomaplist == null) {
			jinfomaplist = new HashMap<String, JinfoList>();
		}
		return jinfomaplist;
	}

	public Sinfo getSinfoBySid(int sid) {
		for (Sinfo mSinfo : sinfoList) {
			if (mSinfo.getId() == sid) {
				return mSinfo;
			}
		}
		return null;
	}

	public CinfoList getCinfoList() {
		return cinfoList;
	}

	public void setCinfoList(CinfoList cinfoList) {
		this.cinfoList = cinfoList;
	}

	public Cinfo getCinfoByCid(int cid) {
		for (Cinfo mCinfo : cinfoList) {
			if (mCinfo.getId() == cid) {
				return mCinfo;
			}
		}
		return null;
	}

	public void setJinfoMapList(HashMap<String, JinfoList> jinfomaplist) {
		this.jinfomaplist = jinfomaplist;
	}

	public void setJinfoToMapList(JinfoList jinfoList) {
		getJinfoMapList().put(
				new SimpleDateFormat("yyyyMMdd").format(new Date()), jinfoList);
	}

	public DisplayMetrics getMetric() {
		return metric;
	}

	public void setMetric(DisplayMetrics metric) {
		this.metric = metric;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public int getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(int screenHeight) {
		this.screenHeight = screenHeight;
	}

	public Binfo getBinfo() {
		return binfo;
	}

	public void setBinfo(Binfo binfo) {
		this.binfo = binfo;
	}

	public Sinfo getSinfo() {
		return sinfo;
	}

	public void setSinfo(Sinfo sinfo) {
		this.sinfo = sinfo;
	}

	public Pinfo getPinfo() {
		return pinfo;
	}

	public void setPinfo(Pinfo pinfo) {
		this.pinfo = pinfo;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public Cinfo getCinfo() {
		return cinfo;
	}

	public void setCinfo(Cinfo cinfo) {
		this.cinfo = cinfo;
	}

	public Jinfo getJinfo() {
		return jinfo;
	}

	public void setJinfo(Jinfo jinfo) {
		this.jinfo = jinfo;
	}

	private CommonInfo commonInfo;

	public CommonInfo getCommonInfo() {
		return commonInfo;
	}

	public void setCommonInfo(CommonInfo commonInfo) {
		this.commonInfo = commonInfo;
	}

	public SinfoList getSinfoList() {
		return sinfoList;
	}

	public void setSinfoList(SinfoList sinfoList) {
		this.sinfoList = sinfoList;
	}

	public PinfoList getPinfoList() {
		return pinfoList;
	}

	public PinfoList getPinfoListBySid(int sid) {
		PinfoList mPinfoList = null;
		if (pinfoList != null && pinfoList.size() > 0) {
			mPinfoList = new PinfoList();
			for (Pinfo mPinfo : pinfoList) {
				if (mPinfo.getS_id() == sid)
					mPinfoList.add(mPinfo);
			}
		}
		return mPinfoList;
	}

	public void setPinfoList(PinfoList pinfoList) {
		this.pinfoList = pinfoList;
	}

	public JinfoList getJinfoList() {
		if (jinfoList == null)
			jinfoList = new JinfoList();
		return jinfoList;
	}

	public void setJinfoList(JinfoList jinfoList) {
		this.jinfoList = jinfoList;
	}

	public GroupList getScheduleGroupList() {
		return scheduleGroupList;
	}

	public void setScheduleGroupList(GroupList scheduleGroupList) {
		this.scheduleGroupList = scheduleGroupList;
	}

	public GroupList getLesson_group_list() {
		return lessonGroupList;
	}

	public void setLesson_group_list(GroupList lesson_group_list) {
		lessonGroupList = lesson_group_list;
	}

	public void onCreate() {
		super.onCreate();

	}

	public Tinfo getTinfo() {
		return tinfo;
	}

	public void setTinfo(Tinfo tinfo) {
		this.tinfo = tinfo;
	}

}
