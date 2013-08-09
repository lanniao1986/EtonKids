package com.enix.hoken.custom.mainview;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.activity.PicturePickerActivity;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.SinfoSettingActivity;
import com.enix.hoken.basic.MainView;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.custom.adapter.LessonAdapter;
import com.enix.hoken.custom.adapter.ScheduleListAdapter;
import com.enix.hoken.custom.adapter.StudentListAdapter;
import com.enix.hoken.R;
import com.enix.hoken.custom.item.*;
import com.enix.hoken.custom.media.MyPlayer;
import com.enix.hoken.info.CommonInfo;
import com.enix.hoken.info.Tinfo;
import com.enix.hoken.util.BitmapCache;
import com.enix.hoken.util.ImageTools;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.util.LetterParser;

public class UserMainView extends MainView {

	private ListView mMainList;
	private ImageView mAvatar;
	private TextView mName;
	private ImageView mStar;
	private TextView mVip;
	private TextView mNoteTips;
	private TextView mMemberCount;
	private LoadingDialog mDialog;

	private StudentListAdapter mStudentAdapter;
	private LessonAdapter mLessonAdapter;
	private ScheduleListAdapter mScheduleAdapter;
	private Tinfo tinfo;
	private CommonInfo commonInfo;
	public static final int MODE_SINFOLIST = 1;
	public static final int MODE_SCHEDULE = 2;
	public static final int MODE_LESSIONLIST = 3;
	private int currentAdapter;

	// head_flipper 组件
	private ViewFlipper flipper;
	private ImageView[] dotView;
	private ImageView imageView;
	private ViewGroup pointGroup;
	private View main_list_head_flipper;
	private View mPlayer;
	private int currentFlipperViewId;

	private Button mHeadFillperNext;
	private Button mHeadFillperPre;
	private MyPlayer mMyPlayer;

	public UserMainView(DesktopActivity activity) {
		super(activity, R.layout.lay_user);

		main_list_head_flipper = LayoutInflater.from(activity).inflate(
				R.layout.main_list_head_flipper, null);
		tinfo = (Tinfo) mAppDataManager.getInfoFromApp(InfoSession.TINFO);
		commonInfo = mApplication.getCommonInfo();

		findView();
		initView();
		setListener();
		init();
	}

	public void findView() {
		super.findView();
		mMainList = (ListView) mView.findViewById(R.id.user_display);
		// -----head_flipper_items_start
		flipper = (ViewFlipper) main_list_head_flipper
				.findViewById(R.id.head_flipper_viewflipper);
		mNoteTips = (TextView) flipper.findViewById(R.id.user_head_notetips);
		mMemberCount = (TextView) flipper.findViewById(R.id.student_sum);
		mAvatar = (ImageView) flipper.findViewById(R.id.user_head_avatar);
		mName = (TextView) flipper.findViewById(R.id.user_head_name);
		mStar = (ImageView) flipper.findViewById(R.id.user_head_star);
		mVip = (TextView) flipper.findViewById(R.id.user_head_vip);
		mPlayer = (LinearLayout) flipper
				.findViewById(R.id.head_flipper_linearlayout_player);
		mHeadFillperNext = (Button) main_list_head_flipper
				.findViewById(R.id.head_flipper_btn_next);
		mHeadFillperPre = (Button) main_list_head_flipper
				.findViewById(R.id.head_flipper_btn_pre);
		// -----head_flipper_items_end

	}

	public void initView() {
		super.initView();
		mMainList.addHeaderView(main_list_head_flipper);
		mMainList.setCacheColorHint(Color.TRANSPARENT); // 设置背景透明度
		// mMainList.setLayoutAnimation(getListAnim());// 每次加载时都显示动画
		mStudentAdapter = new StudentListAdapter(mActivity);
		mStudentAdapter.setmResultCount(badgeResultCount);
		mNoteTips.setText(commonInfo.getCurrentNote());
		// head_view_player_start
		dotView = new ImageView[flipper.getChildCount()];
		pointGroup = (ViewGroup) main_list_head_flipper
				.findViewById(R.id.head_flipper_linearlayout);
		for (int i = 0; i < flipper.getChildCount(); i++) {
			imageView = new ImageView(mActivity);
			imageView.setLayoutParams(new LayoutParams(20, 20));
			imageView.setPadding(20, 0, 20, 0);
			dotView[i] = imageView;
			if (i == 0) {
				dotView[i].setBackgroundResource(R.drawable.fliper_dot_focuse);
			} else {
				dotView[i]
						.setBackgroundResource(R.drawable.fliper_dot_unfocuse);
			}
			pointGroup.addView(dotView[i]);
		}
		mMyPlayer = new MyPlayer(mActivity, mPlayer);
		// head_view_player_end

		// 筛选文本框编辑事件侦听器
		mTextWatcher = new TextWatcher() {

			public void afterTextChanged(Editable s) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				onFilterTextChanged(s, start, before, count);
			}
		};
		mEditFilter.addTextChangedListener(mTextWatcher);// 添加过滤器

	}

	public void setListener() {
		super.setListener();
		// 头部VIEW往前切换
		mHeadFillperPre.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flipper.setInAnimation(mActivity, R.anim.push_right_in);
				flipper.setOutAnimation(mActivity, R.anim.push_right_out);
				currentFlipperViewId = flipper.getDisplayedChild();
				onPointSelected(currentFlipperViewId);
				flipper.showPrevious();
			}
		});
		// 头部VIEW往后切换
		mHeadFillperNext.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				flipper.setInAnimation(mActivity, R.anim.push_left_in);
				flipper.setOutAnimation(mActivity, R.anim.push_left_out);
				flipper.showNext();
				currentFlipperViewId = flipper.getDisplayedChild();
				onPointSelected(currentFlipperViewId);
			}
		});
		// 待办事项点击事件
		mNoteTips.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(mActivity);
				final EditText input = new EditText(mActivity);
				input.setText(mNoteTips.getText());
				builder.setTitle("编辑待办事项");
				builder.setView(input);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								mNoteTips.setText(input.getText().toString());
								mDbmanager.updateNoteTips(mNoteTips.getText()
										.toString());
							}
						}).setNegativeButton("取消", null).create();
				builder.create().show();
			}
		});

		// 点击头像事件
		mAvatar.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Bundle mBundle = new Bundle();
				mBundle.putString(PicturePickerActivity.INTENT_PARAM_FILENAME,
						CommonUtil.getAvatarPath(mActivity, tinfo.getT_head()));
				mBundle.putBoolean(PicturePickerActivity.INTENT_PARAM_FREEMODE,
						false);
				mActionHandler.startIntentForResult(mActionHandler
						.createTranspotIntent(PicturePickerActivity.class,
								mBundle),
						ActionHandler.REQUEST_FOR_AVATAR_UPDATE);

			}
		});

		// 滚动条到底后加载更多事件
		mMainList.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		// 我的首页子选单点击事件
		mModeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mModeChooseIdx = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				switch (position) {
				case 0:
					// 加载我的首页
					handler.sendEmptyMessage(MODE_SINFOLIST);
					break;
				case 1:
					// 加载课程列表
					handler.sendEmptyMessage(MODE_LESSIONLIST);
					break;
				case 2:
					// 加载日程提醒列表
					handler.sendEmptyMessage(MODE_SCHEDULE);
					break;
				}
			}
		});
		// 弹出子菜单点击事件
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuPopupWindow.dismiss();
				switch (currentAdapter) {
				case MODE_SINFOLIST: // 学生列表状态下的子菜单项点击事件
					switch (position) {
					case 0:
						// 设置过滤输入框可见
						setEditFillterVisiable(true);
						return;
					case 1:
						// 设置过滤输入框不可见
						setEditFillterVisiable(false);
						return;
					}
					return;
				case MODE_LESSIONLIST: // 课程列表状态下的子菜单项点击事件
					CommonUtil.showShortToast(mActivity, "课程列表状态下的子菜单项点击事件");
					return;
				case MODE_SCHEDULE: // 日程列表状态下的子菜单项点击事件
					CommonUtil.showShortToast(mActivity, "日程列表状态下的子菜单项点击事件");
					return;
				}
			}
		});
	}

	// 导航圆点激活
	private void onPointSelected(int arg0) {
		pointGroup.removeAllViews();
		for (int i = 0; i < dotView.length; i++) {
			if (arg0 == i) {
				dotView[i].setBackgroundResource(R.drawable.fliper_dot_focuse);
			} else {
				dotView[i]
						.setBackgroundResource(R.drawable.fliper_dot_unfocuse);
			}
			pointGroup.addView(dotView[i]);
		}
	}

	// 初始化默认显示列表
	public void init() {
		handler.sendEmptyMessage(MODE_SINFOLIST);
	}

	public void init(int modeID) {
		handler.sendEmptyMessage(modeID);
	}

	/**
	 * 初次加载LISTVIEW时的动画
	 * 
	 * @return
	 */
	private LayoutAnimationController getListAnim() {
		AnimationSet set = new AnimationSet(true);
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(300);
		set.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(700);
		set.addAnimation(animation);
		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);
		return controller;
	}

	// 指令控制侦听器
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			currentAdapter = msg.what;
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mDialog.show();
				break;
			case MODE_SINFOLIST:
				// 获取教师信息栏目的模式数组
				mModeChooseIdx = 0;
				setModeMenuList(
						R.array.myhomepage_mode_strings,
						new int[] {
								R.drawable.profile_popupwindow_type_info_background,
								R.drawable.profile_popupwindow_type_minifeed_background,
								R.drawable.profile_popupwindow_type_blog_background });
				// 获取教师信息栏目的子菜单数组
				setSubMenuList(R.array.myhomepage_menu_strings);
				mMainList.setAdapter(mStudentAdapter);
				mStar.setVisibility(View.VISIBLE);
				mVip.setText(commonInfo.getCurrentTitle());
				mName.setText(tinfo.getT_name());
				Drawable bitmap = ImageTools.bitmapToDrawable(BitmapCache
						.getInstance().getBitmap(
								CommonUtil.getAvatarPath(mActivity,
										tinfo.getT_head()), true));

				if (bitmap != null) {
					mAvatar.setImageDrawable(bitmap);
				} else {
					mAvatar.setImageDrawable(mActivity.getResources()
							.getDrawable(R.drawable.default_avtar));
				}

				mMemberCount.setText("班级人数：" + commonInfo.getClassMemberCount()
						+ "人");
				mStudentAdapter.notifyDataSetChanged();

				break;
			case MODE_LESSIONLIST:
				mLessonAdapter = new LessonAdapter(mActivity, mApplication,
						mModeLayout);
				mMainList.setAdapter(mLessonAdapter);
				break;
			case MODE_SCHEDULE:
				// 获取教师信息栏目的子菜单数组
				mMenuName = mActivity.getResources().getStringArray(
						R.array.schedulelist_menu_strings);
				// 加载日程提醒列表
				mScheduleAdapter = new ScheduleListAdapter(mActivity);
				mScheduleAdapter.notifyDataSetChanged();
				mMainList.setAdapter(mScheduleAdapter);
				break;
			}
			// 主列表开启筛选过滤属性
			mMainList.setTextFilterEnabled(true);
		}
	};

	public void onFilterTextChanged(CharSequence s, int start, int before,
			int count) {
		if (s.length() > 0 && !LetterParser.isNumeric(s.toString().trim())) {
			String s2 = CommonUtil.getNameNum(s.toString().trim());
			s = (CharSequence) s2;

			flipper.setVisibility(View.GONE);
			pointGroup.setVisibility(View.GONE);
			mStudentAdapter.getFilter().filter(s);
		} else {
			flipper.setVisibility(View.VISIBLE);
			pointGroup.setVisibility(View.VISIBLE);
			badgeResultCount.setVisibility(View.GONE);
			badgeResultCount.clearFocus();
			mStudentAdapter.resetData();
		}
	}

	public void dataRebind() {
		mStudentAdapter.notifyDataSetChanged();

	}
}
