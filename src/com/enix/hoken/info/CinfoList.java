package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class CinfoList extends MainInfoList<Cinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Cinfo mCinfo = get(i);
			if (mMainInfo.getSortId() == mCinfo.getId()) {
				set(i, (Cinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}

}
