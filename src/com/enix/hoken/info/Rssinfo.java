package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Rssinfo extends MainInfo {
	public static final String ITEM = "item";// 单项目节点
	public static final String TITLE = "title";// 该条的名称
	public static final String LINK = "link";// 该条对应的URL链接
	public static final String AUTHOR = "author";// 作者
	public static final String DESCRIPTION = "description";// 该条目的描述

	private int id;
	private String rss_channel;// 目录类别
	private String rss_author;// 作者
	private String rss_link;// 链接地址
	private String rss_title;// 标题
	private String rss_description;// 简要正文
	private String rss_datetime;// 更新时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getRss_channel() {
		return rss_channel;
	}

	public void setRss_channel(String rss_channel) {
		this.rss_channel = rss_channel;
	}

	public String getRss_author() {
		return rss_author;
	}

	public void setRss_author(String rss_author) {
		this.rss_author = rss_author;
	}

	public String getRss_link() {
		return rss_link;
	}

	public void setRss_link(String rss_link) {
		this.rss_link = rss_link;
	}

	public String getRss_title() {
		return rss_title;
	}

	public void setRss_title(String rss_title) {
		this.rss_title = rss_title;
	}

	public String getRss_description() {
		return rss_description;
	}

	public void setRss_description(String rss_description) {
		this.rss_description = rss_description;
	}

	public String getRss_datetime() {
		return rss_datetime;
	}

	public void setRss_datetime(String rss_datetime) {
		this.rss_datetime = rss_datetime;
	}

}
