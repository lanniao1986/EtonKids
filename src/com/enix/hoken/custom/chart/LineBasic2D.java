package com.enix.hoken.custom.chart;

import android.webkit.JavascriptInterface;

import com.enix.hoken.custom.chart.param.Chart;


/**
 * 2D折线图
 * @author SQ
 *
 */
public class LineBasic2D extends Chart{
	private boolean intersection = true; //线段是否在的结合点处突出显示,默认为true
	private int point_size = 3; //数据项点大小,默认为3,仅当配置项intersection为true生效。
	private boolean point_hollow = true; //指定两个线段结合处的圆点是否为空心,默认为true,即空心
	
	/**
	 * 2D折线图
	 * @param title 2D折线图标题
	 * @param data 2D折线图所需的单一数据源,是打包好的JSON格式数据
	 */
	public LineBasic2D(String title, String data) {
		super(title, data);
		// TODO Auto-generated constructor stub
	}
	@JavascriptInterface
	public boolean isIntersection() {
		return intersection;
	}
	@JavascriptInterface
	public void setIntersection(boolean intersection) {
		this.intersection = intersection;
	}
	@JavascriptInterface
	public int getPoint_size() {
		return point_size;
	}
	@JavascriptInterface
	public void setPoint_size(int point_size) {
		this.point_size = point_size;
	}
	@JavascriptInterface
	public boolean isPoint_hollow() {
		return point_hollow;
	}
	@JavascriptInterface
	public void setPoint_hollow(boolean point_hollow) {
		this.point_hollow = point_hollow;
	}
	
	
}
