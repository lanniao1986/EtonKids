package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

public class Tinfo  extends MainInfo {
	private int id;
	private String t_name;
	private String l_gid;
	private String t_head;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
		sortId = id;
	}

	public String getT_name() {
		return t_name;
	}

	public void setT_name(String t_name) {
		this.t_name = t_name;
	}

	public String getL_gid() {
		return l_gid;
	}

	public void setL_gid(String l_gid) {
		this.l_gid = l_gid;
	}

	public String getT_head() {
		return t_head;
	}

	public void setT_head(String t_head) {
		this.t_head = t_head;
	}

}
