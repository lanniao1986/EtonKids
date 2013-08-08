package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class PinfoList extends MainInfoList<Pinfo>{

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Pinfo mPinfo = get(i);
			if (mMainInfo.getSortId() == mPinfo.getId()) {
				set(i, (Pinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
	
}
