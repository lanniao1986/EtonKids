package com.enix.hoken.custom.chart;

import android.webkit.JavascriptInterface;

import com.enix.hoken.custom.chart.param.Chart;


/**
 * 2D饼状图
 * @author SQ
 *
 */
public class Pie2D extends Chart{
	private int offsetAngle = 0; //第一个扇形的起始角度，默认为0，即三点钟方向。例-90为12点钟方向
	private double radius; //饼图的半径，单位为px
	
	/**
	 * 2D饼状图
	 * @param title 2D饼状图标题
	 * @param data 2D饼状图所需的单一数据源,是打包好的JSON格式数据
	 */
	public Pie2D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}
	@JavascriptInterface
	public int getOffsetAngle() {
		return offsetAngle;
	}
	@JavascriptInterface
	public void setOffsetAngle(int offsetAngle) {
		this.offsetAngle = offsetAngle;
	}
	@JavascriptInterface
	public double getRadius() {
		return radius;
	}
	@JavascriptInterface
	public void setRadius(double radius) {
		this.radius = radius;
	}
	
	
}
