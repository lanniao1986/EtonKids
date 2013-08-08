package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class RinfoList extends MainInfoList<Rinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Rinfo mRinfo = get(i);
			if (mMainInfo.getSortId() == mRinfo.getId()) {
				set(i, (Rinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
