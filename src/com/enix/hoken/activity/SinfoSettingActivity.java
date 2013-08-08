/**
 * 
 */
package com.enix.hoken.activity;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import com.enix.hoken.R;
import com.enix.hoken.info.Cinfo;
import com.enix.hoken.info.CinfoList;
import com.enix.hoken.info.CommonInfo;
import com.enix.hoken.info.Sinfo;
import com.enix.hoken.info.SinfoList;

import android.R.layout;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.basic.SubActivity;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.custom.adapter.FragmentListAdapter;
import com.enix.hoken.custom.adapter.ListItem;
import com.enix.hoken.custom.item.BadgeView;
import com.enix.hoken.util.*;

import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.EditText;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author gumc
 * 
 */
public class SinfoSettingActivity extends SubActivity {

	private Sinfo sinfo;
	private RadioGroup mGenderGroup;
	private RadioGroup mWayGroup;
	private TextView mInDate;
	private TextView mBirthday;
	private TextView mDis;
	private Button mConfirm;
	private Button mCancel;
	private RadioButton mGenderBoy;
	private RadioButton mGenderGirl;
	private RadioButton mWayBus;
	private RadioButton mWayParent;
	private Date now = new Date();
	private Date setIndate;
	private Date setBirthday;
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat format2 = new SimpleDateFormat("yyyy年MM月dd日");
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
	public static final int MODE_CREATE = 1;// 新建档案模式
	public static final int MODE_EDIT = 2;// 编辑档案模式
	public static final int MODE_VIEW = 3;// 只读预览模式
	public static final String INTENT_PARAM_SID = "SID";
	public static final String INTENT_PARAM_MODE = "MODE";
	private int currentMode;
	private ArrayList<ListItem> mClassList;
	private EditText mName;
	private ImageView mAvatar;
	private TextView mClass;
	private CommonInfo mCommonInfo;
	private static final String TAG_INDATE = "TAG_INDATE";
	private static final String TAG_BIRTHDAY = "TAG_BIRTHDAY";
	private boolean mHasError = false;// 是否有错误项
	private Sinfo sinfo_bk = new Sinfo();// 还原用数据
	private HorizontalScrollView mTabsView;
	private LinearLayout mTabsLinear;
	private SinfoList mSinfoList;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 从前画面传来的SID获取 SINFO对象
		currentMode = intent.getExtras().getInt(INTENT_PARAM_MODE, -1);
		if (currentMode != MODE_CREATE) {
			sinfo = mApplication.getSinfoBySid(intent.getExtras().getInt(
					INTENT_PARAM_SID, -1));
		} else {
			sinfo = new Sinfo();
		}
		copySinfo(sinfo_bk, sinfo);
		mSinfoList = mApplication.getSinfoList();
		mCommonInfo = mApplication.getCommonInfo();
		setContentView(R.layout.sinfo_setting);
		findView();
		initView();
		setListener();

		// new Thread(this).start();
	}

	/**
	 * 备份Sinfo对象
	 * 
	 * @param target
	 *            备份为副本对象
	 * @param base
	 *            需要备份的对象
	 */
	public void copySinfo(Sinfo target, Sinfo base) {
		target.setC_id(base.getC_id());
		target.setId(base.getId());
		target.setS_age(base.getS_age());
		target.setS_birthday(base.getS_birthday());
		target.setS_dis(base.getS_dis());
		target.setS_gender(base.getS_gender());
		target.setS_head(base.getS_head());
		target.setS_indate(base.getS_indate());
		target.setS_outdate(base.getS_outdate());
		target.setS_name(base.getS_name());
		target.setS_way(base.getS_way());
		target.setSortId(base.getSortId());
	}

	public void setListener() {
		super.setListener();
		mCancel.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				CommonUtil.showShortToast(SinfoSettingActivity.this, "取消成员设置");
				finish();

			}
		});
		if (currentMode != MODE_VIEW) {
			mInDate.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					DatePickFragment newFragment = new DatePickFragment(
							SinfoSettingActivity.this, sinfo.getS_indate());
					newFragment.show(
							SinfoSettingActivity.this.getFragmentManager(),
							TAG_INDATE);
				}
			});
			mConfirm.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					if (validate()) {
						// 新建模式
						if (currentMode == MODE_CREATE) {
							if (mDbmanager.insertSinfo(sinfo)) {
								Log.i("DEBUG", "MEMBER_CREATE_SUCESSED");
								setResult(RESULT_OK, null);
								finish();
							} else {
								Log.i("DEBUG", "MEMBER_CREATE_FAILED");
								CommonUtil.showShortToast(
										SinfoSettingActivity.this, "新建成员失败!");
								setResult(RESULT_FAILED, null);
								finish();
							}
						} else {
							// 更新APPDATA 是否成功
							if (mDbmanager.updateSinfo(sinfo)) {
								Log.i("DEBUG", "MEMBER_UPDATE_SUCESSED");
								setResult(RESULT_OK, null);
								finish();
							} else {
								// 失败
								Log.i("DEBUG", "MEMBER_UPDATE_FAILED");
								CommonUtil.showShortToast(
										SinfoSettingActivity.this, "更新成员失败!");
								setResult(RESULT_FAILED, null);
								finish();
							}
						}
					}
				}
			});

			mMenuListView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					mMenuPopupWindow.dismiss();
					switch (position) {
					case 0:
						CommonUtil.showDialog("是否还原修改前状态", new String[] { "是",
								"否" }, SinfoSettingActivity.this,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											copySinfo(sinfo, sinfo_bk);
											initView();// 设置默认
										}
									}
								});

						break;
					case 1:
						CommonUtil.showDialog("是否删除当前档案", new String[] { "是",
								"否" }, SinfoSettingActivity.this,
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										if (which == 0) {
											if (delSinfoById(sinfo.getId()))
												finish();
										}
									}
								});
						break;

					}
				}
			});
			mClass.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					FragmentListAdapter mAdapter = new FragmentListAdapter(
							SinfoSettingActivity.this, getClassList());

					// ArrayListFragment mArrayListFragment = new
					// ArrayListFragment(
					// SinfoSettingActivity.this, mAdapter);
					// getFragmentManager().beginTransaction().add(android.R.id.content,
					// mArrayListFragment).commit();
					new AlertDialog.Builder(SinfoSettingActivity.this)
							.setTitle("请选择班级")
							.setAdapter(mAdapter,
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											mClass.setText(mClassList
													.get(which).getValue());
											sinfo.setC_id(mClassList.get(which)
													.getId());
										}
									}).create().show();
				}
			});
			mBirthday.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View view) {
					DatePickFragment newFragment = new DatePickFragment(
							SinfoSettingActivity.this, sinfo.getS_birthday());
					newFragment.show(
							SinfoSettingActivity.this.getFragmentManager(),
							TAG_BIRTHDAY);

				}
			});

			// 点击头像事件
			mAvatar.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Bundle mBundle = new Bundle();
					if (sinfo.getS_head() != null
							&& !sinfo.getS_head().isEmpty()) {
						mBundle.putString(
								PicturePickerActivity.INTENT_PARAM_FILENAME,
								CommonUtil.getAvatarPath(
										SinfoSettingActivity.this,
										sinfo.getS_head()));
					} else {
						mBundle.putString(
								PicturePickerActivity.INTENT_PARAM_FILENAME,
								CommonUtil.getAvatarPath(
										SinfoSettingActivity.this, "nothing"));
					}

					mBundle.putBoolean(
							PicturePickerActivity.INTENT_PARAM_FREEMODE, false);

					mActionHandler.startIntentForResult(mActionHandler
							.createTranspotIntent(PicturePickerActivity.class,
									mBundle),
							ActionHandler.REQUEST_FOR_AVATAR_UPDATE);

				}
			});
			mGenderGroup
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						@Override
						public void onCheckedChanged(RadioGroup group,
								int checkedId) {

							if (checkedId == R.id.sinfo_setting_gender_boy) {
								sinfo.setS_gender(1);
							} else {
								sinfo.setS_gender(0);
							}
						}
					});
			mWayGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					if (checkedId == R.id.sinfo_setting_way_parent) {
						sinfo.setS_way(1);
					} else {
						sinfo.setS_way(0);
					}

				}
			});
		}
	}

	/**
	 * 获取年龄
	 * 
	 * @param birthday
	 * @return
	 */
	private int caculateAge(String birthday) {
		if (birthday != null && !birthday.isEmpty()) {
			Date now = new Date();
			try {
				Date mBirthday = format.parse(birthday);
				// 出生日大于等于当前日期 提示错误
				if (mBirthday.getTime() > now.getTime()) {
					return -1;
				} else {
					long day = ((now.getTime() - mBirthday.getTime()))
							/ (24 * 60 * 60 * 1000) + 1;
					return (int) (day / 365);
				}

			} catch (ParseException e) {
				return -1;
			}
		}
		return -1;
	}

	/**
	 * 设定班级列表数据
	 * 
	 * @return
	 */
	private ArrayList<ListItem> getClassList() {
		mClassList = new ArrayList<ListItem>();
		ListItem item = null;
		for (Cinfo mCinfo : mApplication.getCinfoList()) {
			item = new ListItem();
			item.setId(mCinfo.getId());
			item.setValue(mCinfo.getC_name());
			mClassList.add(item);
		}
		return mClassList;
	}

	public void onDataPickerResult(String fragmentTag, Calendar mCalendar,
			Date mDate) {
		if (fragmentTag.equals(TAG_BIRTHDAY)) {
			int mAge = caculateAge(format.format(mDate));
			if (mAge > 0) {
				mBirthday.setTextColor(Color.BLACK);
				mBirthday.setText(format2.format(mDate) + "[" + mAge + "岁]");
				sinfo.setS_birthday(format.format(mDate));
				sinfo.setS_age(mAge);
				mHasError = false;
			} else {
				mBirthday.setTextColor(Color.parseColor("#FFB6C1"));
				mBirthday.setText("日期错误!请重新选择...");
				mHasError = true;
			}

		} else if (fragmentTag.equals(TAG_INDATE)) {
			int mIndateYears = caculateAge(format.format(mDate));
			if (mIndateYears >= 0) {
				mInDate.setTextColor(Color.BLACK);
				mInDate.setText(format2.format(mDate));
				sinfo.setS_indate(format.format(mDate));
				mHasError = false;
			} else {
				mInDate.setTextColor(Color.parseColor("#FFB6C1"));
				mInDate.setText("日期错误!请重新选择...");
				mHasError = true;
			}
		}
	}

	/**
	 * 设置默认
	 */
	private void setDefault() {
		setTitle("新建成员");
		mGenderGroup.check(R.id.sinfo_setting_gender_boy);
		mWayGroup.check(R.id.sinfo_setting_way_parent);
		mInDate.setText(format2.format(now));
		mBirthday.setText("");
		mDis.setText("");
		mName.setText("");
		mAvatar.setImageDrawable(this.getResources().getDrawable(
				R.drawable.default_avtar));

		mClass.setText(mCommonInfo.getClassName());
		sinfo.setC_id(mCommonInfo.getCid());

		sinfo.setS_birthday("");
		sinfo.setS_dis("");
		sinfo.setS_gender(1);
		sinfo.setS_way(1);
		sinfo.setS_indate(format.format(now));
		sinfo.setS_age(0);
		sinfo.setS_name("");
	}

	/**
	 * 表单值验证
	 * 
	 * @return
	 */
	private boolean validate() {
		if (mName.getText() == null || mName.getText().length() == 0) {
			mName.setHintTextColor(Color.parseColor("#FFB6C1"));
			mName.setHint("必须输入学生姓名!");
			mHasError = true;
		} else {
			mName.setHintTextColor(Color.GRAY);
			mName.setHint("输入学生姓名");
			mHasError = false;
			sinfo.setS_name(mName.getText().toString().trim());
		}
		if (mInDate.getText() != null && mInDate.getText().length() > 0
				&& !mHasError) {
			mHasError = false;
		} else {
			mHasError = true;
		}
		if (mBirthday.getText() != null && mBirthday.getText().length() > 0
				&& !mHasError) {
			mHasError = false;
		} else {
			mBirthday.setTextColor(Color.parseColor("#FFB6C1"));
			mBirthday.setText("请选择日期...");
			mHasError = true;
		}
		sinfo.setS_dis(mDis.getText().toString());

		if (!mHasError) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 回传删除命令
	 * 
	 * @param id
	 * @return
	 */
	private boolean delSinfoById(int id) {
		if (currentMode == MODE_EDIT && mDbmanager.deleteSinfo(sinfo)) {// 编辑状态下才能删除
			setResult(ActionHandler.RESULT_FOR_MEMBER_DELETE, null);
			return true;
		} else {
			CommonUtil.showShortToast(this, "删除成员出错!");
			return false;
		}

	}

	@Override
	public void findView() {
		super.findView();
		mGenderGroup = (RadioGroup) findViewById(R.id.sinfo_setting_gender_group);
		mGenderBoy = (RadioButton) findViewById(R.id.sinfo_setting_gender_boy);
		mGenderGirl = (RadioButton) findViewById(R.id.sinfo_setting_gender_girl);
		mWayBus = (RadioButton) findViewById(R.id.sinfo_setting_way_bus);
		mWayParent = (RadioButton) findViewById(R.id.sinfo_setting_way_parent);
		mWayGroup = (RadioGroup) findViewById(R.id.sinfo_setting_way_group);
		mInDate = (TextView) findViewById(R.id.sinfo_setting_indate);
		mBirthday = (TextView) findViewById(R.id.sinfo_setting_birthday);
		mName = (EditText) findViewById(R.id.sinfo_setting_name);
		mDis = (TextView) findViewById(R.id.sinfo_setting_dis);
		mClass = (TextView) findViewById(R.id.sinfo_setting_class);
		mAvatar = (ImageView) findViewById(R.id.sinfo_setting_avatar);
		mConfirm = (Button) findViewById(R.id.sinfo_setting_confirm);
		mCancel = (Button) findViewById(R.id.sinfo_setting_cancel);
		mTabsView = (HorizontalScrollView) findViewById(R.id.sinfo_setting_tabs_view);
		mTabsLinear = (LinearLayout) findViewById(R.id.sinfo_setting_tabs_linear);

	}

	private void initEditView() {
		setTitle(sinfo.getS_name() + "档案");
		try {
			setIndate = format.parse(sinfo.getS_indate());
			setBirthday = format.parse(sinfo.getS_birthday());
		} catch (ParseException e) {
			setIndate = now;
			setBirthday = now;
		}
		mInDate.setText(format2.format(setIndate));
		mBirthday.setText(format2.format(setBirthday) + "[" + sinfo.getS_age()
				+ "岁]");
		if (sinfo.getS_name() != null && !sinfo.getS_name().isEmpty()) {
			mName.setText(sinfo.getS_name());
		}

		Drawable bitmap = ImageTools.bitmapToDrawable(BitmapCache.getInstance()
				.getBitmap(CommonUtil.getAvatarPath(this, sinfo.getS_head()),
						false));
		if (bitmap != null) {
			mAvatar.setImageDrawable(bitmap);
		} else {
			mAvatar.setImageDrawable(this.getResources().getDrawable(
					R.drawable.default_avtar));
		}

		if (sinfo.getS_dis() != null && !sinfo.getS_dis().isEmpty()) {
			mDis.setText(sinfo.getS_dis());
		}

		mClass.setText(mApplication.getCinfoByCid(sinfo.getC_id()).getC_name());

		if (sinfo.getS_gender() == 0) {
			mGenderGroup.check(R.id.sinfo_setting_gender_girl);
		} else {
			mGenderGroup.check(R.id.sinfo_setting_gender_boy);
		}
		if (sinfo.getS_way() == 0) {

			mWayGroup.check(R.id.sinfo_setting_way_bus);
		} else {
			mWayGroup.check(R.id.sinfo_setting_way_parent);
		}
	}

	@Override
	public void initView() {
		super.initView();
		mTabsLinear.removeAllViews();
		for (final Sinfo mSinfo : mSinfoList) {
			TextView tv = new TextView(this);

			tv.setTextSize(16);
			tv.setText(" " + mSinfo.getS_name() + " ");
			tv.setBackgroundResource(R.drawable.flipper_head_popup_menu_white_shadow);
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					sinfo = mSinfo;
					copySinfo(sinfo_bk, sinfo);
					initView();
				}
			});
			mTabsLinear.addView(tv);
			if (mSinfo.equals(sinfo)) {
				tv.setTextColor(Color.parseColor("#ff005092"));

			} else {
				tv.setTextColor(Color.GRAY);
			}
		}
		setSubMenuList(R.array.sinfo_setting_menu_strings);
		switch (currentMode) {
		case MODE_VIEW:// 只读模式
			initEditView();
			mName.setEnabled(false);
			mGenderGroup.setEnabled(false);
			mBirthday.setEnabled(false);
			mInDate.setEnabled(false);
			mWayParent.setEnabled(false);
			mWayBus.setEnabled(false);
			mClass.setEnabled(false);
			mAvatar.setEnabled(false);
			mGenderBoy.setEnabled(false);
			mGenderGirl.setEnabled(false);
			mWayParent.setEnabled(false);
			mDis.setEnabled(false);
			mGenderGroup.setEnabled(false);
			mWayGroup.setEnabled(false);
			setMenuEnable(false);
			mCancel.setText("返回");
			mConfirm.setVisibility(View.GONE);
			break;

		case MODE_CREATE:// 新建模式
			setDefault();
			break;

		case MODE_EDIT:// 编辑模式
			initEditView();
			break;
		}
	}

	@Override
	public void onBackKeyDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResultSucess(int requestCode, Intent data) {
		switch (requestCode) {
		case ActionHandler.REQUEST_FOR_AVATAR_UPDATE:
			initView();
			break;
		}
	}

	@Override
	public void onResultNotOK(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ActionHandler.REQUEST_FOR_AVATAR_UPDATE:
			switch (resultCode) {
			case ActionHandler.RESULT_FOR_AVATER_CREATE:
				String filePath = data
						.getStringExtra(PicturePickerActivity.INTENT_PARAM_CREATEFILE);
				sinfo.setS_head(CommonUtil.getFilenameFromFullPath(filePath));
				Drawable bitmap = ImageTools.bitmapToDrawable(BitmapCache
						.getInstance().getBitmap(
								CommonUtil.getAvatarPath(this,
										sinfo.getS_head()), false));
				if (bitmap != null) {
					mAvatar.setImageDrawable(bitmap);
				} else {
					mAvatar.setImageDrawable(this.getResources().getDrawable(
							R.drawable.default_avtar));
				}
				break;
			}
			break;
		}
	}
}
