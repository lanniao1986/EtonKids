package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Linfo extends MainInfo {
	private int id;
	private int t_id;// 教师ID
	private int c_id;// 班级ID
	private String l_datetime;// 日期
	private String l_name;// 名称

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getT_id() {
		return t_id;
	}

	public void setT_id(int t_id) {
		this.t_id = t_id;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public String getL_datetime() {
		return l_datetime;
	}

	public void setL_datetime(String l_datetime) {
		this.l_datetime = l_datetime;
	}

	public String getL_name() {
		return l_name;
	}

	public void setL_name(String l_name) {
		this.l_name = l_name;
	}

}
