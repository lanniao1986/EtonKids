package com.enix.hoken.basic;

import android.app.ProgressDialog;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enix.hoken.R;
import com.enix.hoken.action.ActionHandler;

import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.adapter.MenuPopAdapter;
import com.enix.hoken.custom.adapter.ModePopAdapter;
import com.enix.hoken.custom.item.AnimationController;
import com.enix.hoken.custom.item.BadgeView;
import com.enix.hoken.custom.item.LoadingDialog;
import com.enix.hoken.custom.item.FlipperLayout.OnOpenListener;
import com.enix.hoken.sqllite.Dbmanager;

public abstract class MainView {
	// 主要部件
	public MainActivity mActivity;
	public BaseApplication mApplication;
	public AppDataManager mAppDataManager;
	public Dbmanager mDbmanager;
	public ActionHandler mActionHandler;
	// 控制
	public OnOpenListener mOnOpenListener;
	public AnimationController animationController;// 动画组件
	public ProgressDialog mDialog;
	// 通用导航条部件
	public TextView mModeText;
	public View mModeView;
	public RelativeLayout mModeLayout;
	public ListView mModeListView;

	public ImageView mFlip;
	public ImageView mMenu;

	public TextView mExtend1;
	public TextView mExtend2;

	public EditText mEditFilter;
	public LinearLayout mFilterLinear;
	public BadgeView badgeResultCount;
	// 模式图标ID数组
	public int[] mModeIcon;
	// 模式菜单项数组
	public String[] mModeName;
	public PopupWindow mModePopupWindow;
	// 子菜单项数组
	public String[] mMenuName;
	public View mMenuView;
	public ListView mMenuListView;
	public PopupWindow mMenuPopupWindow;
	// 模式菜单选项INDEX
	public int mModeChooseIdx = 0;
	// 界面VIEW
	public View mView;
	public TextWatcher mTextWatcher;
	public boolean isFirstLoad = true;// 是否初次加载界面 (耗时工作将再侧栏close()时调用)

	// 筛选文本框编辑事件侦听器

	public MainView(MainActivity mActivity, int resLayId) {
		this.mActivity = mActivity;

		mApplication = mActivity.mApplication;
		mAppDataManager = mActivity.mAppDataManager;
		mDbmanager = mActivity.mDbmanager;
		mActionHandler = mActivity.mActionHandler;
		animationController = new AnimationController();

		mView = LayoutInflater.from(mActivity).inflate(resLayId, null);
	}

	public void findView() {
		// MainView 中必须定义head_bar_linear的LinearLayout
		LinearLayout headBar = (LinearLayout) mView
				.findViewById(R.id.head_bar_linear);
		// 动态统一加载模式导航条
		headBar.addView(LayoutInflater.from(mActivity).inflate(
				R.layout.head_bar, null));
		mFlip = (ImageView) headBar.findViewById(R.id.head_bar_flip);
		mModeLayout = (RelativeLayout) headBar
				.findViewById(R.id.head_bar_mode_layout);
		mModeText = (TextView) headBar.findViewById(R.id.head_bar_mode_text);
		mExtend1 = (TextView) headBar.findViewById(R.id.head_bar_extend1);
		mExtend2 = (TextView) headBar.findViewById(R.id.head_bar_extend2);
		mMenu = (ImageView) headBar.findViewById(R.id.head_bar_menu);
		mEditFilter = (EditText) headBar
				.findViewById(R.id.head_bar_edit_filler);
		mFilterLinear = (LinearLayout) headBar
				.findViewById(R.id.head_bar_linear_filter);
		// 设定模式视图
		mModeView = LayoutInflater.from(mActivity).inflate(
				R.layout.mode_popupwindow, null);
		// 设定子菜单视图
		mMenuView = LayoutInflater.from(mActivity).inflate(
				R.layout.menu_popupwindow, null);
		mModeListView = (ListView) mModeView.findViewById(R.id.mode_pop_list);
		mMenuListView = (ListView) mMenuView.findViewById(R.id.menu_pop_list);

	}

	public void setListener() {
		mModeLayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initModePopupWindow(mModeChooseIdx);
			}
		});
		// 侧边栏界面展开按钮
		mFlip.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mOnOpenListener != null) {
					mOnOpenListener.open();
				}
			}
		});
		// 子版块菜单按钮点击事件
		mMenu.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initMorePopupWindow();
			}
		});
	}

	/**
	 * 菜单键按下时事件
	 */
	public void onMenuKeyDown() {
		initMorePopupWindow();
	}

	public void initModePopupWindow(int chooseId) {
		if (mModeIcon != null && mModeIcon.length > 0 && mModeName != null
				&& mModeName.length > 0) {
			ModePopAdapter adapter = new ModePopAdapter(mActivity, mModeIcon,
					mModeName, chooseId);
			mModeListView.setAdapter(adapter);
			if (mModePopupWindow == null) {
				mModePopupWindow = new PopupWindow(mModeView,
						LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT,
						true);
				mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
				mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
			}
			if (mModePopupWindow.isShowing()) {
				mModePopupWindow.dismiss();
			} else {
				mModePopupWindow.showAsDropDown(mModeLayout, 0, 0);
			}
		}
	}

	private void initMorePopupWindow() {
		if (mMenuName != null && mMenuName.length > 0) {
			MenuPopAdapter adapter = new MenuPopAdapter(mActivity, mMenuName);
			mMenuListView.setAdapter(adapter);
			if (mMenuPopupWindow == null) {
				DisplayMetrics metric = new DisplayMetrics();
				mActivity.getWindowManager().getDefaultDisplay()
						.getMetrics(metric);
				int width = mApplication.getScreenWidth();
				mMenuPopupWindow = new PopupWindow(mMenuView, width / 2,
						LayoutParams.WRAP_CONTENT, true);
				mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
				mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
			}
			if (mMenuPopupWindow.isShowing()) {
				mMenuPopupWindow.dismiss();
			} else {
				mMenuPopupWindow.showAsDropDown(mMenu, 0, 0);
			}
		}
	}

	public void setModeMenuList(int resArrayTextId, int[] resArrayIconId) {
		// 获取模式数组
		mModeName = mActivity.getResources().getStringArray(resArrayTextId);
		mModeIcon = resArrayIconId;
		mModeText.setText(mModeName[mModeChooseIdx]);
	}

	public void setSubMenuList(int resArrayTextId) {
		// 获取子菜单数组
		mMenuName = mActivity.getResources().getStringArray(resArrayTextId);
	}

	public void initView() {

		badgeResultCount = new BadgeView(mActivity, mEditFilter);

		mModePopupWindow = new PopupWindow(mModeView, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mModePopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mModePopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		mMenuPopupWindow = new PopupWindow(mMenuView,
				mApplication.getScreenWidth() / 2, LayoutParams.WRAP_CONTENT,
				true);
		mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);
		createLoadingDialog();
	}

	/**
	 * 设置过滤编辑框可见
	 * 
	 * @param isVisiable
	 */
	public void setEditFillterVisiable(boolean isVisiable) {
		if (isVisiable) {
			animationController.slideFadeIn(mFilterLinear, 800, 0);
			mEditFilter.requestFocus();
		} else {
			animationController.slideFadeOut(mFilterLinear, 800, 0);
			mEditFilter.clearFocus();
		}
	}

	/**
	 * 重新绑定数据
	 */
	public void dataRebind() {

	}

	/**
	 * 获取VIEW之前重新绑定数据 用于更新
	 * 
	 * @return
	 */
	public View getView() {
		dataRebind();
		return mView;
	}

	public void setOnOpenListener(OnOpenListener onOpenListener) {
		mOnOpenListener = onOpenListener;
	}

	private void createLoadingDialog() {
		mDialog = new ProgressDialog(mActivity);
		mDialog.setMessage("正在加载中，请稍候...");
		mDialog.setIndeterminate(true);
		mDialog.setCancelable(true);
	}

	public abstract void init();
}
