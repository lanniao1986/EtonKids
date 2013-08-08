package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class JinfoList extends MainInfoList<Jinfo> {

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Jinfo mJinfo = get(i);
			if (mMainInfo.getSortId() == mJinfo.getId()) {
				set(i, (Jinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
