package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Group  extends MainInfo {
	private String g_code;
	private String g_key;
	private String g_value;
	private String g_detail;

	private String g_extend_1;
	private String g_extend_2;
	private String g_extend_3;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	private int id;

	public String getG_code() {
		return g_code;
	}

	public void setG_code(String g_code) {
		this.g_code = g_code;
	}

	public String getG_key() {
		return g_key;
	}

	public void setG_key(String g_key) {
		this.g_key = g_key;
	}

	public String getG_value() {
		return g_value;
	}

	public void setG_value(String g_value) {
		this.g_value = g_value;
	}

	public String getG_detail() {
		return g_detail;
	}

	public void setG_detail(String g_detail) {
		this.g_detail = g_detail;
	}

	public String getG_extend_1() {
		return g_extend_1;
	}

	public void setG_extend_1(String g_extend_1) {
		this.g_extend_1 = g_extend_1;
	}

	public String getG_extend_2() {
		return g_extend_2;
	}

	public void setG_extend_2(String g_extend_2) {
		this.g_extend_2 = g_extend_2;
	}

	public String getG_extend_3() {
		return g_extend_3;
	}

	public void setG_extend_3(String g_extend_3) {
		this.g_extend_3 = g_extend_3;
	}

}
