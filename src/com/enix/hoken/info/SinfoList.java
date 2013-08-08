package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class SinfoList extends MainInfoList<Sinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Sinfo mSinfo = get(i);
			if (mMainInfo.getSortId() == mSinfo.getId()) {
				set(i, (Sinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}

}
