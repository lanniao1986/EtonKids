package com.enix.hoken.activity;

import java.util.ArrayList;
import com.enix.hoken.R;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.info.*;
import com.enix.hoken.sqllite.*;
import com.enix.hoken.util.CommonUtil;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 引导界面
 * 
 * @author gumc
 * 
 */
public class WelcomeGuide_Activity extends MainActivity {
	private ImageView iv_display;
	private TextView tv_display;
	private Button btn_register;
	private Button btn_other;
	private Button btn_login;
	private String[] strArr_display; // 展示文言数组
	private int strArr_index;// 当前显示的文言索引

	SQLiteDatabase sqldb;
	DbHelper helper;
	final ContentValues cv = new ContentValues();
	private BaseApplication mApplication;
	private Tinfo tinfo;
	private Binfo binfo;
	private Cinfo cinfo;
	private Pinfo pinfo;
	private Sinfo sinfo;
	private Group group;

	/**
	 * 三个切换的动画
	 */
	private Animation anim_FadeIn;
	private Animation anim_FadeInScale;
	private Animation anim_FadeOut;

	private ArrayList<Drawable> list_Drawable;
	private AppDataManager appdata;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lay_welcome_guide);
		findViewById();
		mApplication = (BaseApplication) getApplication();
		initDb(this);
		init();
		setListener();
		appdata = new AppDataManager(this);
	}

	// 初始化数据库对象
	private void initDb(Context mContext) {
		helper = new DbHelper(mContext, DbHelper.DB_NAME, null,
				DbHelper.DB_VERSION);
		sqldb = helper.getDataBase();
	}

	/**
	 * 绑定UI
	 */
	private void findViewById() {
		iv_display = (ImageView) findViewById(R.id.iv_guide);
		tv_display = (TextView) findViewById(R.id.tv_guide_content);
		btn_register = (Button) findViewById(R.id.btn_register);
		btn_other = (Button) findViewById(R.id.btn_preview);
		btn_login = (Button) findViewById(R.id.btn_login);
		strArr_display = this.getResources().getStringArray(
				R.array.welcome_guide_strings);
	}

	/**
	 * 监听事件
	 */
	public void setListener() {
		/**
		 * 动画切换原理:开始时是用第一个渐现动画,当第一个动画结束时开始第二个放大动画,当第二个动画结束时调用第三个渐隐动画,
		 * 第三个动画结束时修改显示的内容并且重新调用第一个动画,从而达到循环效果
		 */
		anim_FadeIn.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				iv_display.startAnimation(anim_FadeInScale);
			}
		});
		anim_FadeInScale.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				iv_display.startAnimation(anim_FadeOut);
			}
		});
		anim_FadeOut.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				set_TextImage(strArr_index);
				iv_display.startAnimation(anim_FadeIn);
			}
		});
		btn_register.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}

		});
		btn_other.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				CommonUtil.showShortToast(WelcomeGuide_Activity.this,
						"暂时无法提供此功能");
			}
		});
		btn_login.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// 数据库中教师信息表中数据存入APP对象
				setDBDataToApp();
				startActivity(new Intent(WelcomeGuide_Activity.this,
						DesktopActivity.class));
				finish();
			}
		});
	}

	private void setDBDataToApp() {

	}

	/**
	 * 初始化
	 */
	private void init() {
		initAnim();
		initPicture();
		/**
		 * 界面刚开始显示的内容
		 */
		set_TextImage(strArr_index);
		iv_display.startAnimation(anim_FadeIn);
	}

	/**
	 * 初始化动画
	 */
	private void initAnim() {
		anim_FadeIn = AnimationUtils.loadAnimation(WelcomeGuide_Activity.this,
				R.anim.guide_welcome_fade_in);
		anim_FadeIn.setDuration(1000);
		anim_FadeInScale = AnimationUtils.loadAnimation(
				WelcomeGuide_Activity.this, R.anim.guide_welcome_fade_in_scale);
		anim_FadeInScale.setDuration(3000);
		anim_FadeOut = AnimationUtils.loadAnimation(WelcomeGuide_Activity.this,
				R.anim.guide_welcome_fade_out);
		anim_FadeOut.setDuration(1000);
	}

	/**
	 * 初始化图片
	 */
	private void initPicture() {
		list_Drawable = new ArrayList<Drawable>();
		list_Drawable.add(getResources().getDrawable(R.drawable.guide1));
		list_Drawable.add(getResources().getDrawable(R.drawable.guide2));
		list_Drawable.add(getResources().getDrawable(R.drawable.guide3));
		list_Drawable.add(getResources().getDrawable(R.drawable.guide1));
		list_Drawable.add(getResources().getDrawable(R.drawable.guide2));
		list_Drawable.add(getResources().getDrawable(R.drawable.guide3));
	}

	/**
	 * 
	 * @param strArrIndex
	 *            当前显示的文言索引
	 * 
	 * @return int 下一个显示的文言索引
	 */
	private int reSetIndex(int currentIndex) {

		if (currentIndex == strArr_display.length - 1) {
			strArr_index = 0;

		} else {
			strArr_index = currentIndex + 1;
		}
		return strArr_index;
	}

	/**
	 * 设置TextView显示文言内容
	 * 
	 * @param strArrIndex
	 *            当前显示的文言索引
	 */
	private void set_TextImage(int strArrIndex) {

		tv_display.setText(strArr_display[strArrIndex]);
		iv_display.setImageDrawable(list_Drawable.get(strArrIndex));
		reSetIndex(strArrIndex);
	}

	@Override
	public void findView() {
		// TODO Auto-generated method stub

	}

	@Override
	public void initView() {
		// TODO Auto-generated method stub

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
