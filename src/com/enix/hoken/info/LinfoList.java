package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class LinfoList extends MainInfoList<Linfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Linfo mLinfo = get(i);
			if (mMainInfo.getSortId() == mLinfo.getId()) {
				set(i, (Linfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
