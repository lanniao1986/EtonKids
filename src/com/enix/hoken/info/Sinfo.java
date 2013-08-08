package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Sinfo  extends MainInfo {

	private int id;
	private String s_name;
	private int c_id;
	private int s_age;
	private int s_gender;
	private String s_indate;
	private String s_outdate;
	private String s_birthday;
	private int s_way;
	private String s_dis;
	private String s_head;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	public String getS_name() {
		return s_name;
	}

	public void setS_name(String s_name) {
		this.s_name = s_name;
	}

	public int getC_id() {
		return c_id;
	}

	public void setC_id(int c_id) {
		this.c_id = c_id;
	}

	public int getS_age() {
		return s_age;
	}

	public void setS_age(int s_age) {
		this.s_age = s_age;
	}

	public int getS_gender() {
		return s_gender;
	}

	public void setS_gender(int s_gender) {
		this.s_gender = s_gender;
	}

	public String getS_indate() {
		return s_indate;
	}

	public void setS_indate(String s_indate) {
		this.s_indate = s_indate;
	}

	public String getS_outdate() {
		return s_outdate;
	}

	public void setS_outdate(String s_outdate) {
		this.s_outdate = s_outdate;
	}

	public String getS_birthday() {
		return s_birthday;
	}

	public void setS_birthday(String s_birthday) {
		this.s_birthday = s_birthday;
	}

	public int getS_way() {
		return s_way;
	}

	public void setS_way(int s_way) {
		this.s_way = s_way;
	}

	public String getS_dis() {
		return s_dis;
	}

	public void setS_dis(String s_dis) {
		this.s_dis = s_dis;
	}

	public String getS_head() {
		return s_head;
	}

	public void setS_head(String s_head) {
		this.s_head = s_head;
	}

}
