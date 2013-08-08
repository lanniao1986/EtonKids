package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class CommonInfo extends MainInfo {
	private int classMemberCount;// 班级人数
	private int cid;

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public int getClassMemberCount() {
		return classMemberCount;
	}

	public void setClassMemberCount(int classMemberCount) {
		this.classMemberCount = classMemberCount;
	}

	public int getClassAlbumCount() {
		return classAlbumCount;
	}

	public void setClassAlbumCount(int classAlbumCount) {
		this.classAlbumCount = classAlbumCount;
	}

	public String getCurrentNote() {
		return currentNote;
	}

	private void setCurrentNote() {
		this.currentNote = noteTips.getG_detail();
	}

	public String getCurrentActivity() {
		return currentActivity;
	}

	public void setCurrentActivity(String currentActivity) {
		this.currentActivity = currentActivity;
	}

	private String className;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	private int classAlbumCount;// 相册本数
	private String currentNote;// 当前备忘信息
	private String currentActivity;// 当前班级活动
	private String currentTitle;// 当前教师职称

	public String getCurrentTitle() {
		return currentTitle;
	}

	public void setCurrentTitle(String currentTitle) {
		this.currentTitle = currentTitle;
	}

	private Group noteTips;

	public Group getNoteTips() {
		return noteTips;
	}

	public void setNoteTips(Group noteTips) {
		this.noteTips = noteTips;
		setCurrentNote();
	}
}
