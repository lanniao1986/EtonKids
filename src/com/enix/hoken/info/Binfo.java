package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Binfo extends MainInfo {
	private int id;
	private int t_id;
	private int c_id;
	private String h_code;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
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

	public String getH_code() {
		return h_code;
	}

	public void setH_code(String h_code) {
		this.h_code = h_code;
	}

}
