package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Pinfo  extends MainInfo {
	private int id;
	private String p_name;
	private int s_id;
	private String p_title_code;
	private String p_tel;
	private String p_address;
	private String p_qq;
	private String p_email;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	public String getP_name() {
		return p_name;
	}

	public void setP_name(String p_name) {
		this.p_name = p_name;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public String getP_title_code() {
		return p_title_code;
	}

	public void setP_title_code(String p_title_code) {
		this.p_title_code = p_title_code;
	}

	public String getP_tel() {
		return p_tel;
	}

	public void setP_tel(String p_tel) {
		this.p_tel = p_tel;
	}

	public String getP_address() {
		return p_address;
	}

	public void setP_address(String p_address) {
		this.p_address = p_address;
	}

	public String getP_qq() {
		return p_qq;
	}

	public void setP_qq(String p_qq) {
		this.p_qq = p_qq;
	}

	public String getP_email() {
		return p_email;
	}

	public void setP_email(String p_email) {
		this.p_email = p_email;
	}

}
