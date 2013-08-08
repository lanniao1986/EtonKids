package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class BinfoList extends MainInfoList<Binfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Binfo mBinfo = get(i);
			if (mMainInfo.getSortId() == mBinfo.getId()) {
				set(i, (Binfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
