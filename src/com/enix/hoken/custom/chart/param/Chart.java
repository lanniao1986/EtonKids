package com.enix.hoken.custom.chart.param;

import java.util.Vector;

/**
 * 图表
 * 
 * @author SQ
 * 
 */
public class Chart {
	private String data;

	// 主图位置布局
	private int width; // 图表宽度
	private int height; // 图表高度
	private float offsetx;// 横坐标偏移量
	private float offsety;// 纵坐标偏移量

	// 动画过渡效果
	private boolean animation;// 是否开启动画效果
	private int duration_animation_duration; // 过渡动画过程所用的时间，单位毫秒

	// 标题
	private String titleText; // 图表主标题配置项
	private int titleSize = 18; // 图表主标题字体大小
	private String titleWeight = "bold";// 主标题字体粗细值，默认为'bold'
	private String titleColor = "#000000";// 图表主标题字体颜色
	private String title_algin; // 标题的对齐方式,可选项有：'left'、'center'、'right'
	private String subtitle; // 副标题配置项

	// 字体样式
	private String font = "Verdana"; // 定义文本的字体系列，默认为'Verdana'
	private int fontsize = 12; // 字体大小,默认为12px
	private String fontweight = "normal"; // 字体粗细值，默认为'normal'
	private String fontcolor = "#FFFFFF";// 字体颜色

	// 阴影效果
	private boolean shadow = false; // 是否启用阴影效果,默认为false
	private int shadow_blur = 4; // 阴影效果的模糊值，默认为4px
	private String shadow_color = "#111111"; // 阴影颜色,默认为'#111111'
	private int shadow_offsetx = 2; // 阴影x轴偏移量，正数向右偏移，负数向左偏移,默认为0px
	private int shadow_offsety = 2; // 阴影y轴偏移量，正数向下偏移，负数向上偏移,默认为0px
	// 脚注
	private String footnote; // 脚注配置项
	private String footnote_align = "right"; // 脚注的对齐方式,可选项有：'left'、'center'、'right'
	private int footnoteSize = 12; // 图表主标题字体大小
	private String footnoteWeight = "normal";// 主标题字体粗细值，默认为'normal'
	private String footnoteColor = "#C5C1AA";// 图表主标题字体颜色
	/**
	 * 图表
	 */
	public Chart(String title, String data) {
		super();
		this.titleText = title;
		this.data = data;
	}

	/**
	 * 图表
	 * 
	 * @param width
	 *            宽度
	 * @param height
	 *            高度
	 * @param title
	 *            标题
	 */
	public Chart(int width, int height, String titleText, String data) {
		super();
		this.width = width;
		this.height = height;
		this.titleText = titleText;
		this.data = data;
	}

	public boolean isAnimation() {
		return animation;
	}

	public int getDuration_animation_duration() {
		return duration_animation_duration;
	}

	public void setDuration_animation_duration(int duration_animation_duration) {
		this.duration_animation_duration = duration_animation_duration;
	}

	public void setAnimation(boolean animation) {
		this.animation = animation;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public String getTitleText() {
		return titleText;
	}

	public void setTitleText(String titleText) {
		this.titleText = titleText;
	}

	public int getTitleSize() {
		return titleSize;
	}

	public void setTitleSize(int titleSize) {
		this.titleSize = titleSize;
	}

	public String getTitleWeight() {
		return titleWeight;
	}

	public void setTitleWeight(String titleWeight) {
		this.titleWeight = titleWeight;
	}

	public String getTitleColor() {
		return titleColor;
	}

	public void setTitleColor(String titleColor) {
		this.titleColor = titleColor;
	}

	public String getTitle_algin() {
		return title_algin;
	}

	public void setTitle_algin(String title_algin) {
		this.title_algin = title_algin;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}

	public int getFontsize() {
		return fontsize;
	}

	public void setFontsize(int fontsize) {
		this.fontsize = fontsize;
	}

	public String getFontweight() {
		return fontweight;
	}

	public void setFontweight(String fontweight) {
		this.fontweight = fontweight;
	}

	public float getOffsetx() {
		return offsetx;
	}

	public void setOffsetx(float offsetx) {
		this.offsetx = offsetx;
	}

	public float getOffsety() {
		return offsety;
	}

	public void setOffsety(float offsety) {
		this.offsety = offsety;
	}

	public boolean isShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public int getShadow_blur() {
		return shadow_blur;
	}

	public void setShadow_blur(int shadow_blur) {
		this.shadow_blur = shadow_blur;
	}

	public String getShadow_color() {
		return shadow_color;
	}

	public void setShadow_color(String shadow_color) {
		this.shadow_color = shadow_color;
	}

	public int getShadow_offsetx() {
		return shadow_offsetx;
	}

	public void setShadow_offsetx(int shadow_offsetx) {
		this.shadow_offsetx = shadow_offsetx;
	}

	public int getShadow_offsety() {
		return shadow_offsety;
	}

	public void setShadow_offsety(int shadow_offsety) {
		this.shadow_offsety = shadow_offsety;
	}

	public String getFontcolor() {
		return fontcolor;
	}

	public void setFontcolor(String fontcolor) {
		this.fontcolor = fontcolor;
	}

	public String getFootnote() {
		return footnote;
	}

	public void setFootnote(String footnote) {
		this.footnote = footnote;
	}

	public String getFootnote_align() {
		return footnote_align;
	}

	public void setFootnote_align(String footnote_align) {
		this.footnote_align = footnote_align;
	}
	public int getFootnoteSize() {
		return footnoteSize;
	}

	public void setFootnoteSize(int footnoteSize) {
		this.footnoteSize = footnoteSize;
	}

	public String getFootnoteWeight() {
		return footnoteWeight;
	}

	public void setFootnoteWeight(String footnoteWeight) {
		this.footnoteWeight = footnoteWeight;
	}

	public String getFootnoteColor() {
		return footnoteColor;
	}

	public void setFootnoteColor(String footnoteColor) {
		this.footnoteColor = footnoteColor;
	}
}
