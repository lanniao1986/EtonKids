package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class TinfoList extends MainInfoList<Tinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Tinfo mTinfo = get(i);
			if (mMainInfo.getSortId() == mTinfo.getId()) {
				set(i, (Tinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}

}
