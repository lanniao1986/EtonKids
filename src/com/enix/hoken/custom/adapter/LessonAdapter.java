package com.enix.hoken.custom.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.R;
import com.enix.hoken.common.BaseApplication;

public class LessonAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private String monday;
	private String tuesday;
	private String wednesday;
	private String thursday;
	private String friday;
	private String saturday;
	private String sunday;
	private String monday_detail;
	private String tuesday_detail;
	private String wednesday_detail;
	private String thursday_detail;
	private String friday_detail;
	private String saturday_detail;
	private String sunday_detail;
	private GroupList lessonList;
	private Group group;
	private Context mContext;
	private ListView mPoplist;
	private View mPopWindow;
	private PopupWindow mModePopupWindow;
	private View mPopPositonView;
	public LessonAdapter(Context context, BaseApplication mApplication,View mModeLayout) {
		mInflater = LayoutInflater.from(context);
		this.mContext = context;
		this.mPopWindow = mInflater.inflate(
				R.layout.mode_popupwindow, null);
		mPoplist = (ListView) mPopWindow.findViewById(R.id.mode_pop_list);
		mPopPositonView = mModeLayout;
		// 存在课程列表
		lessonList = mApplication.getLesson_group_list();
		if (lessonList != null && lessonList.size() > 0) {
			for (int i = 0; i < lessonList.size(); i++) {
				group = (Group) lessonList.get(i);
				// 根据KEY判断是星期几的课程
				switch (Integer.parseInt(group.getG_key())) {
				case 1:
					monday = group.getG_value();
					monday_detail = group.getG_detail();
					break;
				case 2:
					tuesday = group.getG_value();
					tuesday_detail = group.getG_detail();
					break;
				case 3:
					wednesday = group.getG_value();
					wednesday_detail = group.getG_detail();
					break;
				case 4:
					thursday = group.getG_value();
					thursday_detail = group.getG_detail();
					break;
				case 5:
					friday = group.getG_value();
					friday_detail = group.getG_detail();
					break;
				case 6:
					saturday = group.getG_value();
					saturday_detail = group.getG_detail();
					break;
				case 7:
					sunday = group.getG_value();
					sunday_detail = group.getG_detail();
					break;
				default:
					break;
				}
			}
		}
	}

	public int getCount() {
		return 1;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
	private void initPopupWindow(int weekNum) {
		String title =null;
		String detail = null;
		switch (weekNum) {
		case 1:
			title = monday;
			detail = monday_detail;
			break;
		case 2:
			title = tuesday;
			detail = tuesday_detail;
			break;
		case 3:
			title = wednesday;
			detail = wednesday_detail;
			break;
		case 4:
			title = thursday;
			detail = thursday_detail;
			break;
		case 5:
			title = friday;
			detail = friday_detail;
			break;
		case 6:
			title = saturday;
			detail = saturday_detail;
			break;
		case 7:
			title = sunday;
			detail = sunday_detail;
			break;
		}
		//弹窗数据源设置
		LessonDetailPopAdapter adapter = new LessonDetailPopAdapter(mContext, title,detail);
		mPoplist.setAdapter(adapter);
		//弹窗触发
		if (mModePopupWindow == null) {
			mModePopupWindow = new PopupWindow(mPopWindow,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mModePopupWindow.isShowing()) {
			mModePopupWindow.dismiss();
		} else {
			mModePopupWindow.showAsDropDown(mPopPositonView,0,0);
		}
	
	}
	public void setListener(ViewHolder holder) {
		holder.mMonday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopupWindow(1);
			}
		});
		holder.mTuesday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopupWindow(2);
			}
		});
		holder.mWednesday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
			
				initPopupWindow(3);		
			}

		});
		holder.mThursday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopupWindow(4);		

			}

		});
		holder.mFriday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopupWindow(5);		

			}

		});
		holder.mSaturday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initPopupWindow(6);		

			}

		});
		holder.mSunday.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
		
				initPopupWindow(7);		
			}

		});
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lesson_list, null);
			holder = new ViewHolder();
			holder.mLessonList = (Button) convertView
					.findViewById(R.id.lesson_list);
			holder.mMonday = (TextView) convertView
					.findViewById(R.id.lesson_monday);
			holder.mTuesday = (TextView) convertView
					.findViewById(R.id.lesson_tuesday);
			holder.mWednesday = (TextView) convertView
					.findViewById(R.id.lesson_wednesday);
			holder.mThursday = (TextView) convertView
					.findViewById(R.id.lesson_thursday);
			holder.mFriday = (TextView) convertView
					.findViewById(R.id.lesson_friday);
			holder.mSaturday = (TextView) convertView
					.findViewById(R.id.lesson_saturday);
			holder.mSunday = (TextView) convertView
					.findViewById(R.id.lesson_sunday);
			holder.mMonday_Line = (View) convertView
					.findViewById(R.id.lesson_monday_line);
			holder.mTuesday_Line = (View) convertView
					.findViewById(R.id.lesson_tuesday_line);
			holder.mWednesday_Line = (View) convertView
					.findViewById(R.id.lesson_wednesday_line);
			holder.mThursday_Line = (View) convertView
					.findViewById(R.id.lesson_thursday_line);
			holder.mFriday_Line = (View) convertView
					.findViewById(R.id.lesson_friday_line);
			holder.mSaturday_Line = (View) convertView
					.findViewById(R.id.lesson_saturday_line);
			holder.mSunday_Line = (View) convertView
					.findViewById(R.id.lesson_sunday_line);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (monday != "") {
			holder.mMonday.setText("周一：" + monday);
		} else {
			holder.mMonday.setText("周一：" + "当日未设置课程");
		}
		if (tuesday != "") {
			holder.mTuesday.setText("周二：" + tuesday);
		} else {
			holder.mTuesday.setText("周二：" + "当日未设置课程");
		}
		if (wednesday != "") {
			holder.mWednesday.setText("周三：" + wednesday);
		} else {
			holder.mWednesday.setText("周三：" + "当日未设置课程");
		}
		if (thursday != "") {
			holder.mThursday.setText("周四：" + thursday);
		} else {
			holder.mThursday.setText("周四：" + "当日未设置课程");
		}
		if (friday != "") {
			holder.mFriday.setText("周五：" + friday);
		} else {
			holder.mFriday.setText("周五：" + "当日未设置课程");
		}
		if (saturday != "") {
			holder.mSaturday.setText("周六：" + saturday);
		} else {
			holder.mSaturday.setText("周六：" + "当日未设置课程");
		}
		if (sunday != "") {
			holder.mSunday.setText("周日：" + sunday);
		} else {
			holder.mSunday.setText("周日：" + "当日未设置课程");
		}
		setListener(holder);
		return convertView;
	}

	class ViewHolder {
		Button mLessonList;
		TextView mMonday;
		TextView mTuesday;
		TextView mWednesday;
		TextView mThursday;
		TextView mFriday;
		TextView mSaturday;
		TextView mSunday;
		View mMonday_Line;
		View mTuesday_Line;
		View mWednesday_Line;
		View mThursday_Line;
		View mFriday_Line;
		View mSaturday_Line;
		View mSunday_Line;
	}
}
