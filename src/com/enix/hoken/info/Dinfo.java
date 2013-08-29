package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Dinfo extends MainInfo {
	private int id;
	private String fileName;// 文件显示用名称
	private String url;// 下载URL
	private String filePathName;// 文件下载名称
	private int state;// 下载状态标识
	private String datetime;// 状态变更时间
	private String imgurl;// 图标路径
	private long presize;// 断点续传已下载量
	private long totalsize;// 文件大小

	public Dinfo() {

	}

	public Dinfo(String url, String fileName, String imgurl) {
		this.url = url;
		this.fileName = fileName;
		this.imgurl = imgurl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getFilePathName() {
		return filePathName;
	}

	public void setFilePathName(String filePathName) {
		this.filePathName = filePathName;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getImgurl() {
		return imgurl;
	}

	public void setImgurl(String imgurl) {
		this.imgurl = imgurl;
	}

	public long getPresize() {
		return presize;
	}

	public void setPresize(long presize) {
		this.presize = presize;
	}

	public long getTotalsize() {
		return totalsize;
	}

	public void setTotalsize(long totalsize) {
		this.totalsize = totalsize;
	}

}
