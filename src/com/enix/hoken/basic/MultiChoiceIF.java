package com.enix.hoken.basic;

import java.util.HashMap;

public interface MultiChoiceIF {

	/**
	 * 开启关闭选择模式
	 * 
	 * @param checkable
	 *            //当前选择模式是否开启
	 * @param mSelectMap
	 *            //选中对象索引值对
	 */
	public void switchMultiChooice();

	public void multiChooiceOn();

	public void multiChooiceOff();
}
