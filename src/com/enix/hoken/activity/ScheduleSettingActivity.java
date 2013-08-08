/**
 * 
 */
package com.enix.hoken.activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.enix.hoken.R;
import com.enix.hoken.info.Group;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import com.enix.hoken.R;
import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.SubActivity;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.adapter.MenuPopAdapter;
import com.enix.hoken.custom.receiver.AlarmReceiver;
import com.enix.hoken.custom.wheeldatepicker.DatePickWheelDialog;
import com.enix.hoken.util.*;
import android.content.DialogInterface;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author gumc
 * 
 */
public class ScheduleSettingActivity extends SubActivity {

	private DatePickWheelDialog datePickWheelDialog;
	private Group group;
	private boolean isNew = true;// 是否是新建模式
	private CheckBox mCheckOnce;
	private CheckBox mCheckWeek;
	private CheckBox mCheckAll;
	private TextView mDateTime;
	private TextView mName;
	private TextView mDis;
	private Button mConfirm;
	private Button mCancel;
	private Date now = new Date();
	private Date setdate;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
	private SimpleDateFormat format2 = new SimpleDateFormat(
			"yyyy年MM月dd日     HH:mm");
	private static final int CHECK_ID_FAILE = 0;
	private static final int CHECK_NAME_FAILE = 1;
	private static final int CHECK_DATETIME_FAILE = 2;
	private static final int CHECK_MODE_FAILE = 3;
	private static final int CHECK_SUCESS = -1;
	private static final int CHECK_DATETIME_PASSED = 4;
	private static final String MESSAGE_ID_FAILE = "日程未设置ID";
	private static final String MESSAGE_NAME_FAILE = "日程名不能为空,请输入日程名";
	private static final String MESSAGE_DATETIME_FAILE = "日程日期时间不能为空,请选择提醒日期时间";
	private static final String MESSAGE_DATETIME_PASSED = "单次提醒模式,选择日期不能早于当前时间";
	private static final String MESSAGE_MODE_FAILE = "提醒模式未设置";
	private static final int MODE_ONCE = 1;
	private static final int MODE_WEEK = 2;
	private static final int MODE_ALL = 3;
	private int currentMode;
	private Calendar pickedCalendar;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		group = new Group();
		isNew = intent.getBooleanExtra("isNew", true);
		group.setG_value(intent.getStringExtra("name"));
		group.setG_detail(intent.getStringExtra("dis"));
		group.setG_code("S_01");// 日程CODE
		group.setId(intent.getIntExtra("id", -1));// 新建日程把Id设为NULL
		group.setG_key(intent.getStringExtra("key"));// 目前无作用
		group.setG_extend_1(intent.getStringExtra("datetime"));// 提醒日期时间
		group.setG_extend_2(intent.getStringExtra("mode"));// 提醒模式
		group.setG_extend_3(intent.getStringExtra("enable"));// 是否开启提醒1开启
		setContentView(R.layout.schedule_setting);

		findView();
		initView();
		setListener();
		// new Thread(this).start();
	}

	private void setCheckBox(int selectedid) {
		switch (selectedid) {
		case R.id.schedule_setting_checkonce:
			mCheckOnce.setChecked(true);
			mCheckWeek.setChecked(false);
			mCheckAll.setChecked(false);
			break;
		case R.id.schedule_setting_checkweek:
			mCheckOnce.setChecked(false);
			mCheckWeek.setChecked(true);
			mCheckAll.setChecked(false);
			break;
		case R.id.schedule_setting_checkall:
			mCheckOnce.setChecked(false);
			mCheckWeek.setChecked(false);
			mCheckAll.setChecked(true);
			break;
		default:
			mCheckOnce.setChecked(false);
			mCheckWeek.setChecked(false);
			mCheckAll.setChecked(false);
			break;
		}
	}

	public void setListener() {
		super.setListener();
		mDateTime.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showPicker();
			}
		});
		mConfirm.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (validate()) {
					if (setAlarm(pickedCalendar, currentMode)) {
						setResult(RESULT_OK, null);
						finish();
				
					}
				}
			}
		});
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CommonUtil.showShortToast(ScheduleSettingActivity.this,
						"取消日程设置");
				finish();			
			}
		});
		mCheckOnce.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mCheckWeek.setChecked(false);
					mCheckAll.setChecked(false);
					currentMode = MODE_ONCE;
				}
			}
		});
		mCheckWeek.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mCheckOnce.setChecked(false);
					mCheckAll.setChecked(false);
					currentMode = MODE_WEEK;
				}
			}
		});
		mCheckAll.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mCheckWeek.setChecked(false);
					mCheckOnce.setChecked(false);
					currentMode = MODE_ALL;
				}
			}
		});

		mMenuListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuPopupWindow.dismiss();
				switch (position) {
				case 0:
					CommonUtil.showDialog("是否清除当前设置",
							new String[] { "是", "否" },
							ScheduleSettingActivity.this,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										setDefault();// 设置默认
									}
								}
							});

					break;
				case 1:
					CommonUtil.showDialog("是否删除当前日程",
							new String[] { "是", "否" },
							ScheduleSettingActivity.this,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									if (which == 0) {
										delScheduleById(group.getId());
									}
								}
							});
					break;

				}
			}
		});

	}

	/**
	 * 设置默认
	 */
	private void setDefault() {
		mCheckOnce.setChecked(true);
		mCheckWeek.setChecked(false);
		mCheckAll.setChecked(false);
		mDateTime.setText(format2.format(now));
		mName.setText(null);
		mDis.setText(null);
	}

	/**
	 * 回传删除命令
	 * 
	 * @param id
	 * @return
	 */
	private boolean delScheduleById(int id) {
		if (!isNew) {// 更新状态下才能删除
			intent = new Intent();
			intent.putExtra("id", id);
			setResult(ActionHandler.RESULT_FOR_SCHEDULE_DELETE, intent);
			finish();
			return true;
		}
		return false;
	}

	private void showPicker() {
		datePickWheelDialog = new DatePickWheelDialog.Builder(this)
				.setPositiveButton("确定", new View.OnClickListener() {
					public void onClick(View v) {
						pickedCalendar = datePickWheelDialog.getSetCalendar();
						group.setG_extend_1(getFormatTime(pickedCalendar));
						try {
							setdate = format
									.parse(getFormatTime(pickedCalendar));
						} catch (ParseException e) {
							setdate = now;
						}
						mDateTime.setText(format2.format(setdate));
						datePickWheelDialog.dismiss();
					}
				}).setTitle("请选择日期与时间").setNegativeButton("取消", null).create();
		datePickWheelDialog.show();
	}

	/**
	 * 验证当前设置日程
	 * 
	 * @return
	 */
	private int checkSetting() {
		setScheduleInfo();
		if (!isNew) {
			if (group.getId() > 0) {// 日程对象ID是否可用
				if (group.getG_extend_1() != null
						&& !group.getG_extend_1().isEmpty()) {// 日程时间是否被设置
					if (mCheckOnce.isChecked()) {// 单次提醒模式下 日期不能小于当前时间
						if (pickedCalendar != null) {// 未设置过日期控件的则通过原有时间日期判断
							if (pickedCalendar.getTime().before(new Date())) {
								return CHECK_DATETIME_PASSED;
							}
						} else {
							if (setdate.before(new Date())) {
								return CHECK_DATETIME_PASSED;
							}
						}
					}
					if (group.getG_value() != null
							&& !group.getG_value().isEmpty()) {
						if (mCheckAll.isChecked() || mCheckOnce.isChecked()
								|| mCheckWeek.isChecked()) {
							return CHECK_SUCESS;
						} else {
							return CHECK_MODE_FAILE;
						}

					} else {
						return CHECK_NAME_FAILE;
					}
				} else {
					return CHECK_DATETIME_FAILE;
				}
			} else {
				return CHECK_ID_FAILE;
			}
		}
		return CHECK_SUCESS;
	}

	/**
	 * 验证输入值是否正确,并显示错误提示
	 * 
	 * @return
	 */
	private boolean validate() {
		switch (checkSetting()) {
		case CHECK_DATETIME_PASSED:
			CommonUtil.showLongToast(this, MESSAGE_DATETIME_PASSED);
			return false;
		case CHECK_ID_FAILE:
			CommonUtil.showLongToast(this, MESSAGE_ID_FAILE);
			return false;
		case CHECK_DATETIME_FAILE:
			CommonUtil.showLongToast(this, MESSAGE_DATETIME_FAILE);
			return false;
		case CHECK_NAME_FAILE:
			CommonUtil.showLongToast(this, MESSAGE_NAME_FAILE);
			return false;
		case CHECK_MODE_FAILE:
			CommonUtil.showLongToast(this, MESSAGE_MODE_FAILE);
			return false;
		case CHECK_SUCESS:
			return true;
		}
		return true;
	}

	private String getFormatTime(Calendar c) {
		String parten = "00";
		DecimalFormat decimal = new DecimalFormat(parten);
		// 设置日期的显示
		Calendar calendar = c;
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		return year + decimal.format(month + 1) + decimal.format(day)
				+ decimal.format(hour) + decimal.format(minute);

	}

	private void setScheduleInfo() {
		group.setG_value(mName.getText().toString());
		group.setG_detail(mDis.getText().toString());
		if (mCheckOnce.isChecked()) {
			group.setG_extend_2("1");
		} else if (mCheckWeek.isChecked()) {
			group.setG_extend_2("2");
		} else if (mCheckAll.isChecked()) {
			group.setG_extend_2("3");
		}
		group.setG_extend_3("1");// 设置启用该闹钟
	}

	/**
	 * 向系统发送闹铃注册申请
	 * 
	 * @param targetCalendar
	 * @param mode
	 * @return
	 */
	private boolean setAlarm(Calendar targetCalendar, int mode) {
		Intent intent = new Intent(ScheduleSettingActivity.this,
				AlarmReceiver.class);
		switch (mode) {
		case MODE_ONCE:// 单次提醒的闹铃设置
			intent.setAction("short");
			PendingIntent sender = PendingIntent.getBroadcast(
					ScheduleSettingActivity.this, 0, intent, 0);
			AlarmManager alarm = (AlarmManager) getSystemService(ALARM_SERVICE);
			if (targetCalendar != null) {
				alarm.set(AlarmManager.RTC_WAKEUP,
						targetCalendar.getTimeInMillis(), sender);
			} else {// 未设置过日期的场合
				alarm.set(AlarmManager.RTC_WAKEUP, setdate.getTime(), sender);
			}
			return true;

		case MODE_WEEK:// 工作日提醒的闹铃设置
			return true;
		case MODE_ALL:// 每天提醒的闹铃设置
			return true;
		}
		return false;
	}

	@Override
	public void findView() {
		super.findView();
		mCheckOnce = (CheckBox) findViewById(R.id.schedule_setting_checkonce);
		mCheckWeek = (CheckBox) findViewById(R.id.schedule_setting_checkweek);
		mCheckAll = (CheckBox) findViewById(R.id.schedule_setting_checkall);
		mDateTime = (TextView) findViewById(R.id.schedule_setting_datetime);
		mName = (TextView) findViewById(R.id.schedule_setting_name);
		mDis = (TextView) findViewById(R.id.schedule_setting_dis);
		mConfirm = (Button) findViewById(R.id.schedule_setting_confirm);
		mCancel = (Button) findViewById(R.id.schedule_setting_cancel);

	}

	@Override
	public void initView() {
		super.initView();
		setSubMenuList(R.array.schedule_setting_menu_strings);
		setTitle("日程设定");
		String checkNum;
		if (!isNew) {// 更新模式
			try {
				setdate = format.parse(group.getG_extend_1());
			} catch (ParseException e) {
				setdate = now;
			}
			mDateTime.setText(format2.format(setdate));
			if (group.getG_value() != null && !group.getG_value().isEmpty()) {
				mName.setText(group.getG_value());
			}
			if (group.getG_detail() != null && !group.getG_detail().isEmpty()) {
				mDis.setText(group.getG_detail());
			}
			// 当前提醒模式
			if (group.getG_extend_2() != null
					&& !group.getG_extend_2().isEmpty()) {
				checkNum = group.getG_extend_2();
				if (checkNum.equals("1")) {
					setCheckBox(R.id.schedule_setting_checkonce);
					currentMode = MODE_ONCE;
				} else if (checkNum.equals("2")) {
					setCheckBox(R.id.schedule_setting_checkweek);
					currentMode = MODE_WEEK;
				} else if (checkNum.equals("3")) {
					setCheckBox(R.id.schedule_setting_checkall);
					currentMode = MODE_ALL;
				} else {
					setCheckBox(-1);
				}
			}
		} else {// 新建模式
			// mDateTime.setText(df.format(now));
		}
	}

	@Override
	public void onBackKeyDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onMenuKeyDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResultSucess(int requestCode, Intent data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResultNotOK(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

	}
}
