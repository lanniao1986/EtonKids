package com.enix.hoken.custom.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import com.enix.hoken.info.*;
import com.enix.hoken.R;
import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.ScheduleSettingActivity;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.common.BaseApplication;

public class ScheduleListAdapter extends MainAdapter {

	private LayoutInflater mInflater;
	private Group group;

	private Date fullDate;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat format1 = new SimpleDateFormat("HH时mm分");
	private SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");


	public ScheduleListAdapter(MainActivity mActivity) {
		super(mActivity,mActivity.mApplication.getScheduleGroupList());
		mInflater = LayoutInflater.from(mActivity);	
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.schedule_item, null);
			holder = new ViewHolder();
			holder.mName = (TextView) convertView
					.findViewById(R.id.schedule_name);
			holder.mDis = (TextView) convertView
					.findViewById(R.id.schedule_dis);
			holder.mDateTime = (TextView) convertView
					.findViewById(R.id.schedule_datetime);
			holder.mCheck = (CheckBox) convertView
					.findViewById(R.id.schedule_check);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		group = (Group) mMainInfoList.get(position);
		holder.mName.setText(group.getG_value());
		holder.mDis.setText(group.getG_detail());
		holder.mDateTime.setText(parseDateTimeByMode(group.getG_extend_2(),
				group.getG_extend_1()));
		// 是否开启该日程提醒
		if ("1".equals(group.getG_extend_3())) {
			holder.mCheck.setChecked(true);
		} else {
			holder.mCheck.setChecked(false);
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent();
				intent.putExtra("name", group.getG_value());// 传递日程标题名称
				intent.putExtra("dis", group.getG_detail());// 传递日程内容
				intent.putExtra("id", group.getId());// 传递groupid
				intent.putExtra("key", group.getG_key());// 传递groupgkey
				intent.putExtra("isNew", false);// 非新建日程
				intent.putExtra("datetime", group.getG_extend_1());// 传递设置的日程时间日期
				intent.putExtra("mode", group.getG_extend_2());// 传递提醒模式
				intent.putExtra("enable", group.getG_extend_3());// 传递是否启用状态

				intent.setClass(mActivity, ScheduleSettingActivity.class);
				mActivity.startActivityForResult(intent,
						ActionHandler.REQUEST_FOR_SCHEDULE_UPDATE);
				mActivity.overridePendingTransition(R.anim.push_up_in,
						R.anim.push_up_out);
			}
		});
		return convertView;
	}

	/**
	 * 根据提醒模式来转型日期字符串
	 * 
	 * @return
	 */
	private String parseDateTimeByMode(String mode, String datetime) {

		if (mode != null) {
			try {
				fullDate = format.parse(datetime);
				if (mode.equals("1")) {
					return format2.format(fullDate);
				} else if (mode.equals("2")) {
					return "工作日:" + format1.format(fullDate);
				} else if (mode.equals("3")) {
					return "每天:" + format1.format(fullDate);
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return datetime;
	}

	private class ViewHolder {
		TextView mName;
		TextView mDis;
		TextView mDateTime;
		CheckBox mCheck;
	}
}
