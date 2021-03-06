package com.enix.hoken.custom.chart;

import android.webkit.JavascriptInterface;

import com.enix.hoken.custom.chart.param.Chart;


/**
 * 3D柱形图
 * @author SQ
 *
 */
public class Column3D extends Chart{
	private int xAngle = 60; //3D效果中x轴角度,默认为60,有效范围0-90
	private int yAngle = 20; //3D效果中y轴角度,默认为20,有效范围0-90
	private int zScale = 1; //3D效果中z轴深度因子(与宽度比)，默认为1
	
	/**
	 * 3D柱形图
	 * @param title 3D柱形图标题
	 * @param data 3D柱形图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Column3D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}
	@JavascriptInterface
	public int getxAngle() {
		return xAngle;
	}
	@JavascriptInterface
	public void setxAngle(int xAngle) {
		this.xAngle = xAngle;
	}
	@JavascriptInterface
	public int getyAngle() {
		return yAngle;
	}
	@JavascriptInterface
	public void setyAngle(int yAngle) {
		this.yAngle = yAngle;
	}
	@JavascriptInterface
	public int getzScale() {
		return zScale;
	}
	@JavascriptInterface
	public void setzScale(int zScale) {
		this.zScale = zScale;
	}
	
	
}
