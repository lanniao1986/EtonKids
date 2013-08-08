package com.enix.hoken.info;

import com.enix.hoken.basic.MainInfo;

/**
 * 分数数据类
 * 
 * @author gumc
 * 
 */
public class Rinfo extends MainInfo {
	private int id; // 分数ID
	private int s_id;// 成员ID
	private int l_id;// 科目ID
	private int r_score;// 分值
	private int r_rank;// 排行值

	private String r_date;// 日期时间

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getS_id() {
		return s_id;
	}

	public void setS_id(int s_id) {
		this.s_id = s_id;
	}

	public int getL_id() {
		return l_id;
	}

	public void setL_id(int l_id) {
		this.l_id = l_id;
	}

	public int getR_score() {
		return r_score;
	}

	public void setR_score(int r_score) {
		this.r_score = r_score;
	}

	public int getR_rank() {
		return r_rank;
	}

	public void setR_rank(int r_rank) {
		this.r_rank = r_rank;
	}

	public String getR_date() {
		return r_date;
	}

	public void setR_date(String r_date) {
		this.r_date = r_date;
	}

}
