package com.enix.hoken.info;



import com.enix.hoken.basic.MainInfo;

public class Cinfo  extends MainInfo {
	private int id;
	private int c_grade;
	private String c_name;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	public int getC_grade() {
		return c_grade;
	}

	public void setC_grade(int c_grade) {
		this.c_grade = c_grade;
	}

	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

}
