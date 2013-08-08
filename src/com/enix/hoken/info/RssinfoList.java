package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;

public class RssinfoList extends MainInfoList<Rssinfo> {
	public static final String CHANNEL = "channel";// 频道
	public static final String TITLE = "title";// 描述该RSS的名称
	public static final String LINK = "link";// 频道对应的URL链接
	public static final String DESCRIPTION = "description";// 频道的描述

	private String rss_channel;// 目录类别
	private String rss_channel_url;// RSS URL
	private String rss_channel_image;// 频道图片

	public String getRss_channel_image() {
		return rss_channel_image;
	}

	public void setRss_channel_image(String rss_channel_image) {
		this.rss_channel_image = rss_channel_image;
	}

	public String getRss_channel_url() {
		return rss_channel_url;
	}

	public void setRss_channel_url(String rss_channel_url) {
		this.rss_channel_url = rss_channel_url;
	}

	public String getRss_channel() {
		return rss_channel;
	}

	public void setRss_channel(String rss_channel) {
		this.rss_channel = rss_channel;
	}

	public boolean set(MainInfo mMainInfo) {
		for (int i = 0; i < size(); i++) {
			Rssinfo mRssinfo = get(i);
			if (mMainInfo.getSortId() == mRssinfo.getId()) {
				set(i, (Rssinfo) mMainInfo);
				return true;
			}
		}
		return false;
	}
}
