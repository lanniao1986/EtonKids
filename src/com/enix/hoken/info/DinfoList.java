package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class DinfoList extends MainInfoList<Dinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Dinfo mDinfo = get(i);
			if (mMainInfo.getSortId() == mDinfo.getId()) {
				set(i, (Dinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
