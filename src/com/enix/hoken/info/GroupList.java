package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class GroupList extends MainInfoList<Group> {
	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Group mGroup = get(i);
			if (mMainInfo.getSortId() == mGroup.getId()) {
				set(i, (Group) mMainInfo);
				return true;
			}
		}
		return false;
	}

}
