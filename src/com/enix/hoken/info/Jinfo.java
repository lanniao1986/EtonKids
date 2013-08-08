package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

/**
 * 签到表结构对象
 * 
 * @author enix
 * 
 */
public class Jinfo  extends MainInfo {
	private int id;
	private int s_id;
	private String s_name;
	private String j_checktime;
	private int j_state;
	private String j_dis;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getJ_checktime() {
		return j_checktime;
	}

	public void setJ_checktime(String j_checktime) {
		this.j_checktime = j_checktime;
	}

	public int getJ_state() {
		return j_state;
	}

	public void setJ_state(int j_state) {
		this.j_state = j_state;
	}

	public String getJ_dis() {
		return j_dis;
	}

	public void setJ_dis(String j_dis) {
		this.j_dis = j_dis;
	}

	public String getS_name() {
		return s_name;
	}

	public void setS_name(String s_name) {
		this.s_name = s_name;
	}

}
