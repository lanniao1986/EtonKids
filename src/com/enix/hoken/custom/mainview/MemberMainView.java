package com.enix.hoken.custom.mainview;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.Keyframe;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.Editable;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.activity.ChartActivity;
import com.enix.hoken.activity.DesktopActivity;
import com.enix.hoken.activity.SinfoSettingActivity;
import com.enix.hoken.activity.Welcome_Activity;
import com.enix.hoken.basic.MainView;
import com.enix.hoken.custom.adapter.MemberGridAdapter;
import com.enix.hoken.custom.adapter.MenuPopAdapter;
import com.enix.hoken.custom.adapter.ModePopAdapter;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.R;
import com.enix.hoken.custom.item.*;
import com.enix.hoken.custom.item.FlipperLayout.OnOpenListener;
import com.enix.hoken.info.CommonInfo;
import com.enix.hoken.info.Jinfo;
import com.enix.hoken.info.Pinfo;
import com.enix.hoken.info.Sinfo;
import com.enix.hoken.info.SinfoList;
import com.enix.hoken.info.Tinfo;
import com.enix.hoken.sqllite.Dbmanager;
import com.enix.hoken.util.CommonUtil;

public class MemberMainView extends MainView {

	private TextView mArrived;
	private TextView mDayoff;
	private TextView mTotal;
	private GridView mMainGrid;
	private LoadingDialog mDialog;
	private MemberGridAdapter mMemberGridAdapter;
	private CommonInfo commonInfo;
	public static final int MODE_MEMBERLIST = 1;// 点名册模式
	public static final int MODE_MEMBER_EDIT = 2;// 成员编辑模式
	public static final int MODE_MEMBER_RANK = 3;// 图表模式
	private ArrayList<Sinfo> sinfoList = null;
	private int currentMode;
	public static final int POPUP_MENU_TYPE_SUBMENU = 0;// 子菜单
	public static final int POPUP_MENU_TYPE_ARRIVED = 1;// 签到名单
	public static final int POPUP_MENU_TYPE_DAYOFF = 2;// 请假名单

	private int currentMenuType = 0;
	private ArrayList<Pinfo> pinfoList = null;
	private int selectedIndex = 0;
	private Uri uri = null;
	private Intent intent = null;
	private LinearLayout mCountLinear = null;
	private ProgressDialog mProgressDialog;
	private Runnable exportExcelThread;
	private String fullPathName;
	private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy年MM月");
	private SimpleDateFormat fullFormat = new SimpleDateFormat(
			"yyyy年MM月dd日 HH:mm");
	private LayoutTransition mTransitioner;
	// 用于多选保存选择状态
	private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private boolean checkable = false;
	private int currentChartMode = 1;

	public MemberMainView(DesktopActivity activity) {
		super(activity, R.layout.lay_member);
		sinfoList = mApplication.getSinfoList();
		commonInfo = mApplication.getCommonInfo();
		mMemberGridAdapter = new MemberGridAdapter(mActivity);
		findView();
		initView();
		setListener();		
	}

	public void findView() {
		super.findView();
		mArrived = (TextView) mView.findViewById(R.id.member_arrived);
		mDayoff = (TextView) mView.findViewById(R.id.member_dayoff);
		mTotal = (TextView) mView.findViewById(R.id.member_total);
		mMainGrid = (GridView) mView.findViewById(R.id.memberlist_gridview);
		mCountLinear = (LinearLayout) mView
				.findViewById(R.id.member_count_linear);

	}

	public void setListener() {
		super.setListener();
		// 滚动条到底后加载更多事件
		mMainGrid.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		// 表格项目点击事件
		mMainGrid.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (currentMode) {
				case MODE_MEMBERLIST:// 点名册时点击事件
					if (checkable) {// 多选状态时
						// 切换选择状态
						if (mSelectMap.get(arg2) == null ? false : mSelectMap
								.get(arg2)) {
							mSelectMap.put(arg2, false);
						} else {
							mSelectMap.put(arg2, true);
						}
						mMemberGridAdapter.setmSelectMap(mSelectMap);
						mMemberGridAdapter.notifyDataSetChanged();
					} else {// 非多选状态
						mCountLinear.setVisibility(View.VISIBLE);
						mMemberGridAdapter.toggleState(arg2, (ImageView) arg1
								.findViewById(R.id.member_state));
						setStateCount();
					}
					break;
				case MODE_MEMBER_EDIT:// 成员编辑时点击事件
					if (checkable) {// 多选状态时
						// 切换选择状态
						if (mSelectMap.get(arg2) == null ? false : mSelectMap
								.get(arg2)) {
							mSelectMap.put(arg2, false);
						} else {
							mSelectMap.put(arg2, true);
						}
						mMemberGridAdapter.setmSelectMap(mSelectMap);
						mMemberGridAdapter.notifyDataSetChanged();
					} else {// 非多选状态时
						Bundle mBundle = new Bundle();
						Sinfo mSinfo = (Sinfo) mMemberGridAdapter.getItem(arg2);
						mBundle.putInt(SinfoSettingActivity.INTENT_PARAM_SID,
								mSinfo.getId());
						mBundle.putInt(SinfoSettingActivity.INTENT_PARAM_MODE,
								SinfoSettingActivity.MODE_EDIT);
						mActionHandler.startIntentForResult(mActionHandler
								.createTranspotIntent(
										SinfoSettingActivity.class, mBundle),
								ActionHandler.REQUEST_FOR_MEMBER_EDIT);
					}
					break;
				case MODE_MEMBER_RANK:// 图表生成点击事件
					if (mSelectMap.get(arg2) == null ? false : mSelectMap
							.get(arg2)) {
						mSelectMap.put(arg2, false);
					} else {
						mSelectMap.put(arg2, true);
					}
					mMemberGridAdapter.setmSelectMap(mSelectMap);
					mMemberGridAdapter.notifyDataSetChanged();
					break;
				}
			}
		});
		mMainGrid.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// 点名模式下则无法多选
				if (currentMode != MODE_MEMBERLIST)
					switchMultiChooice(false);
				return true;
			}
		});
		// 成员模式选单点击事件
		mModeListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mModeChooseIdx = position;
				mModeText.setText(mModeName[position]);
				mModePopupWindow.dismiss();
				switch (position) {
				case 0:
					// 加载点名册
					handler.sendEmptyMessage(MODE_MEMBERLIST);
					break;
				case 1:
					// 加载成员管理
					handler.sendEmptyMessage(MODE_MEMBER_EDIT);
					break;
				case 2:
					// 加载排行榜
					handler.sendEmptyMessage(MODE_MEMBER_RANK);
					break;
				}
			}
		});
		// 弹出子菜单点击事件
		mMenuListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mMenuPopupWindow.dismiss();

				popMenuItemClick(currentMenuType, position);

			}
		});
		// 签到点击事件
		mArrived.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentMenuType = POPUP_MENU_TYPE_ARRIVED;
				showStateList(MemberGridAdapter.MEMBER_STATE_ARRIVED);
			}
		});
		// 请假点击事件
		mDayoff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				currentMenuType = POPUP_MENU_TYPE_DAYOFF;
				showStateList(MemberGridAdapter.MEMBER_STATE_DAYOFF);
			}
		});
		// 总计点击事件
		mTotal.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				View target = null;
				for (int i = 0; i < mMemberGridAdapter.getJinfoList().size(); i++) {
					if (mMemberGridAdapter.getJinfoList().get(i).getJ_state() == MemberGridAdapter.MEMBER_STATE_DEFALUT) {

						target = (View) mMainGrid.getChildAt(mMemberGridAdapter
								.getSinfoIndexBySid(mMemberGridAdapter
										.getJinfoList().get(i).getS_id()));
						target.startAnimation(AnimationUtils.loadAnimation(
								mActivity, R.anim.shake));
					}
				}
			}
		});
		// 全选按钮点击事件
		mExtend1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < mMainGrid.getCount(); i++) {
					mSelectMap.put(i, true);
				}

				mMemberGridAdapter.notifyDataSetChanged(mSelectMap);
			}
		});
		// 清除按钮点击事件
		mExtend2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				for (int i = 0; i < mMainGrid.getCount(); i++) {
					mSelectMap.put(i, false);
				}
				mMemberGridAdapter.notifyDataSetChanged(mSelectMap);
			}
		});
	}

	public void initModePopupWindow(int chooseId) {
		super.initModePopupWindow(chooseId);
		currentMenuType = POPUP_MENU_TYPE_SUBMENU;
	}

	/**
	 * 弹出菜单项目点击事件
	 * 
	 * @param menuType
	 *            当前打开的菜单类型
	 * @param position
	 *            当前项目位置
	 */
	private void popMenuItemClick(int menuType, int position) {
		switch (menuType) {
		// 点击了签到列中的项
		case POPUP_MENU_TYPE_ARRIVED:
			if (mMemberGridAdapter.nameList_Arrived != null
					&& mMemberGridAdapter.nameList_Arrived.size() > 0) {
				creatDialog(mMemberGridAdapter.nameList_Arrived.get(position));
			}
			break;
		// 点击了请假列中的项
		case POPUP_MENU_TYPE_DAYOFF:
			if (mMemberGridAdapter.nameList_Dayoff != null
					&& mMemberGridAdapter.nameList_Dayoff.size() > 0) {
				creatDialog(mMemberGridAdapter.nameList_Dayoff.get(position));
			}
			break;
		// 点击了子菜单列中的项
		case POPUP_MENU_TYPE_SUBMENU:
			switch (currentMode) {
			case MODE_MEMBERLIST:// 点名册
				switch (position) {
				case 0:
					// 重置状态
					mMemberGridAdapter.resetState();
					mCountLinear.setVisibility(View.GONE);
					setStateCount();
					return;
				case 1:
					// 导出EXCEL
					exportExcel();
					return;
				case 2:
					// 切换点选状态

					return;
				}
				break;
			case MODE_MEMBER_EDIT:// 成员编辑
				switch (position) {
				case 0:
					// 导出EXCEL
					exportExcel();

					return;
				case 1:
					// 新建成员
					Bundle mBundle = new Bundle();
					mBundle.putInt(SinfoSettingActivity.INTENT_PARAM_MODE,
							SinfoSettingActivity.MODE_CREATE);
					mActionHandler.startIntentForResult(mActionHandler
							.createTranspotIntent(SinfoSettingActivity.class,
									mBundle),
							ActionHandler.REQUEST_FOR_MEMBER_CREATE);
					return;
				}
			case MODE_MEMBER_RANK:// 图表生成模式
				switch (position) {
				case 0:
					// 导出图表
					exportRank();

					return;
				case 1:
					setChartMode();
					return;
				}
			}
		}
	}

	private SinfoList getSelectedSinfo() {
		SinfoList mSinfoList = new SinfoList();
		Iterator iter = mSelectMap.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			int key = (Integer) entry.getKey();
			boolean val = (Boolean) entry.getValue();
			if (val) {
				mSinfoList.add(mApplication.getSinfoList().get(key));
			}
		}
		return mSinfoList;
	}

	private void exportRank() {

		mApplication.setSelectedSinfoList(getSelectedSinfo());

		// 1 创建一个Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		// 2 给Builder设置属性
		builder.setTitle("请选择科目");// 设置Dialog的标题
		final String[] items = { "儿歌比赛成绩", "剪纸游戏成绩", "过家家游戏成绩" };

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Bundle mBundle = new Bundle();
				mBundle.putInt(ChartActivity.INTENT_PARAM_MODE,
						currentChartMode);
				mBundle.putInt(ChartActivity.INTENT_PARAM_LID, which + 1);
				mActionHandler.startIntent(mActionHandler.createTranspotIntent(
						ChartActivity.class, mBundle));
			}

		});
		builder.show();
	}

	private void setChartMode() {
		// 1 创建一个Builder
		AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
		// 2 给Builder设置属性
		builder.setTitle("请选择图表模式");// 设置Dialog的标题
		final String[] items = { "3D柱形图表", "3D饼图", "2D线图" };

		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentChartMode = which + 1;
			}

		});
		builder.show();
	}

	/**
	 * 创建快捷家长联系对话框
	 * 
	 * @param targetSinfo
	 */
	private void creatDialog(Sinfo targetSinfo) {
		pinfoList = mApplication.getPinfoListBySid(targetSinfo.getId());
		String[] pinfoName = null;

		if (pinfoList != null && pinfoList.size() > 0) {
			pinfoName = new String[pinfoList.size()];
			// 设置家长联系人姓名电话
			for (int i = 0; i < pinfoName.length; i++) {
				pinfoName[i] = pinfoList.get(i).getP_name() + "："
						+ pinfoList.get(i).getP_tel();
			}
			new AlertDialog.Builder(mActivity)

					.setTitle(targetSinfo.getS_name() + "的家长联系人")
					.setNeutralButton("短信通知",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									uri = Uri.parse("smsto://"
											+ pinfoList.get(selectedIndex)
													.getP_tel());
									// 指向用户短信的界面。
									mActivity.startActivity(new Intent(
											Intent.ACTION_SENDTO, uri));
								}
							})
					.setPositiveButton("电话通知",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									uri = Uri.parse("tel://"
											+ pinfoList.get(selectedIndex)
													.getP_tel());
									// 指向用户拨号的界面。
									mActivity.startActivity(new Intent(
											Intent.ACTION_DIAL, uri));
								}
							})
					.setSingleChoiceItems(pinfoName, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									selectedIndex = which;
								}
							}).show();
		}
	}

	/**
	 * 显示指定的状态成员列表
	 * 
	 * @param stateID
	 */
	private void showStateList(int stateID) {
		String[] namelist = null;
		switch (stateID) {
		case MemberGridAdapter.MEMBER_STATE_ARRIVED:
			namelist = new String[mMemberGridAdapter.nameList_Arrived.size()];
			for (int i = 0; i < namelist.length; i++) {
				namelist[i] = mMemberGridAdapter.nameList_Arrived.get(i)
						.getS_name();
			}
			initMorePopupWindow(namelist, mArrived);
			return;
		case MemberGridAdapter.MEMBER_STATE_DAYOFF:
			namelist = new String[mMemberGridAdapter.nameList_Dayoff.size()];
			for (int i = 0; i < namelist.length; i++) {
				namelist[i] = mMemberGridAdapter.nameList_Dayoff.get(i)
						.getS_name();
			}
			initMorePopupWindow(namelist, mDayoff);
			return;
		}

	}

	public void initView() {
		super.initView();
		mMainGrid.setCacheColorHint(Color.TRANSPARENT); // 设置背景透明度
		// resetTransition();

	}

	// 初始化默认显示列表
	public void init() {
		handler.sendEmptyMessage(MODE_MEMBERLIST);
	}

	public void init(int modeID) {
		handler.sendEmptyMessage(modeID);
	}

	/**
	 * 更新计数栏
	 * 
	 * @param state
	 */
	private void setStateCount() {
		mTotal.setText("总数:" + sinfoList.size() + "人/未选:"
				+ mMemberGridAdapter.disCount + "人");
		mArrived.setText("签到:" + mMemberGridAdapter.countArrived + "人");
		mDayoff.setText("请假:" + mMemberGridAdapter.countDayoff + "人");
		if (mMemberGridAdapter.disCount == sinfoList.size())
			mCountLinear.setVisibility(View.GONE);
	}

	private void initMorePopupWindow(String[] MenuItemName, View parentLocation) {
		if (MenuItemName == null || MenuItemName.length == 0) {
			MenuItemName = new String[] { "当前无项目" };
		}
		MenuPopAdapter adapter = new MenuPopAdapter(mActivity, MenuItemName);
		mMenuListView.setAdapter(adapter);
		if (mMenuPopupWindow == null) {
			int width = mApplication.getScreenWidth();
			mMenuPopupWindow = new PopupWindow(mMenuView, width / 2,
					LayoutParams.WRAP_CONTENT, true);
			mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
			mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		}
		if (mMenuPopupWindow.isShowing()) {
			mMenuPopupWindow.dismiss();
		} else {
			mMenuPopupWindow.showAsDropDown(parentLocation, 0, 0);
		}
	}

	// 指令控制侦听器
	Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			currentMode = msg.what;
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				mDialog.show();
				break;
			case MODE_MEMBERLIST:// 点名册模式
				mModeChooseIdx = 0;
				currentMode = MODE_MEMBERLIST;
				// 获取成员栏目的模式数组
				mModeName = mActivity.getResources().getStringArray(
						R.array.member_mode_strings);
				setModeMenuList(
						R.array.member_mode_strings,
						new int[] {
								R.drawable.profile_popupwindow_type_info_background,
								R.drawable.profile_popupwindow_type_minifeed_background,
								R.drawable.profile_popupwindow_type_blog_background });
				// 获取成员栏目的子菜单数组
				mMenuName = mActivity.getResources().getStringArray(
						R.array.member_menu_strings);
				mMainGrid.setAdapter(mMemberGridAdapter);
				mMemberGridAdapter.notifyDataSetChanged();
				break;
			case MODE_MEMBER_EDIT:// 成员编辑模式
				mMenuName = mActivity.getResources().getStringArray(
						R.array.member_config_menu_strings);

				mModeChooseIdx = 1;
				currentMode = MODE_MEMBER_EDIT;
				clearStateCount();

				break;
			case MODE_MEMBER_RANK:// 图表模式
				mMenuName = mActivity.getResources().getStringArray(
						R.array.member_config_rank_strings);
				mModeChooseIdx = 2;
				currentMode = MODE_MEMBER_RANK;
				clearStateCount();
				// 默认开启多选模式
				switchMultiChooice(true);
				break;
			}

		}
	};

	private void clearStateCount() {
		mMemberGridAdapter.resetState();
		mCountLinear.setVisibility(View.GONE);
		setStateCount();
		mMemberGridAdapter.notifyDataSetChanged();
	}

	/**
	 * 启动导出EXCEL线程
	 */
	private void exportExcel() {
		mProgressDialog = new ProgressDialog(mActivity);
		mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
		mProgressDialog.setTitle("正在为您导出EXCEL");
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "打开",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (mProgressDialog.getProgress() == 100) {
							openExcel();
						} else {
							CommonUtil.showLongToast(mActivity,
									"EXCEL正在导出请稍后...");
						}
					}
				});
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "隐藏",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						mProgressDialog.dismiss();
					}
				});
		mProgressDialog.show();
		exportExcelThread = new Runnable() {
			public void run() {
				if (insertToDb()) {
					mProgressDialog.setProgress(30);

					String sheetName = simpleFormat.format(new Date());
					String fileName = CommonUtil.getStringResource(mActivity,
							R.string.school_name)
							+ commonInfo.getClassName()
							+ sheetName + "学生签到表.xls";
					fullPathName = CommonUtil.getRootFolder()
							+ fileName;

					ExcelBuilder eb = new ExcelBuilder(mActivity, fullPathName);
					// 设置需要输出的考勤数据
					eb.setJinfoMapList(mApplication.getJinfoMapList());
					if (eb.WritingSpreadSheet()) {
						mProgressDialog.setProgress(100);
						CommonUtil.showLongToast(mActivity, "导出EXCEL成功!");
					} else {
						CommonUtil.showLongToast(mActivity, "导出EXCEL过程出错!");
					}
				} else {
					CommonUtil.showLongToast(mActivity, "数据保存过程出错!");
				}
			}
		};
		handler.postDelayed(exportExcelThread, 700);
	}

	/**
	 * 调用外部OFFICE软件 打开文档
	 */
	private void openExcel() {
		intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		uri = Uri.fromFile(new File(fullPathName));
		intent.setDataAndType(uri, "application/vnd.ms-excel");
		mActivity.startActivity(intent);
	}

	/**
	 * 插入当前签到数据到数据库
	 * 
	 * @return
	 */
	private boolean insertToDb() {
		// 将封装好的JINFOLIST设置到mApplication
		mAppDataManager.setInfoToApp(mMemberGridAdapter.getJinfoList(),
				InfoSession.JINFOLIST);
		mApplication.setJinfoToMapList(mMemberGridAdapter.getJinfoList());
		return mDbmanager.insertMemberCheck();
	}

	/**
	 * GridView重绑定数据
	 */
	public void dataRebind() {
		mMemberGridAdapter.notifyDataSetChanged();
	}

	/**
	 * 开启多选模式
	 * 
	 * @param isOpen
	 *            TRUE:默认强制进入多选 FALSE:切换多选
	 */
	private void switchMultiChooice(boolean isOpen) {
		if (isOpen) {
			checkable = true;
			mActivity.vibrator.vibrate(100);
			mMemberGridAdapter.setCheckMode(checkable);
			mExtend1.setVisibility(View.VISIBLE);
			mExtend1.setText("全选");
			mExtend2.setVisibility(View.VISIBLE);
			mExtend2.setText("清除");
			clearStateCount();
		} else {
			// 表格设为可选
			if (checkable) {
				checkable = false;
				mExtend1.setVisibility(View.INVISIBLE);
				mExtend2.setVisibility(View.INVISIBLE);
				mSelectMap.clear();
				mMemberGridAdapter.setCheckMode(checkable);
				mMemberGridAdapter.notifyDataSetChanged(mSelectMap);
			} else {
				checkable = true;
				mActivity.vibrator.vibrate(100);
				mMemberGridAdapter.setCheckMode(checkable);
				mExtend1.setVisibility(View.VISIBLE);
				mExtend1.setText("全选");
				mExtend2.setVisibility(View.VISIBLE);
				mExtend2.setText("清除");
				clearStateCount();
			}
		}
	}
}
