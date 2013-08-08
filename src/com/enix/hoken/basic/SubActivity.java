package com.enix.hoken.basic;

import com.enix.hoken.custom.adapter.MenuPopAdapter;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.enix.hoken.R;

public abstract class SubActivity extends MainActivity {
	private ImageView mMenu;
	private ImageView mBack;
	// 子菜单项数组
	private String[] mMenuName;
	private View mMenuView;
	public ListView mMenuListView;
	public PopupWindow mMenuPopupWindow;
	public Intent intent;
	public TextView mTitle;
	public TextView mExtend1;
	public TextView mExtend2;
	private boolean menuEnable = true;// 子菜单可否使用

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		intent = getIntent();
		// 设定子菜单视图
		mMenuView = LayoutInflater.from(this).inflate(
				R.layout.menu_popupwindow, null);
		mMenuPopupWindow = new PopupWindow(mMenuView,
				mApplication.getScreenWidth() / 2, LayoutParams.WRAP_CONTENT,
				true);
		mMenuPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mMenuPopupWindow.setAnimationStyle(R.style.ModePopupAnimation);

	}

	public void initMorePopupWindow() {
		if (mMenuName != null && mMenuName.length > 0) {
			MenuPopAdapter adapter = new MenuPopAdapter(this, mMenuName);
			mMenuListView.setAdapter(adapter);
			if (mMenuPopupWindow == null) {

				mMenuPopupWindow = new PopupWindow(mMenuView,
						mApplication.getScreenWidth() / 2,
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

	/**
	 * 控件加载FindViewById
	 */
	public void findView() {
		// Layout 中必须定义head_bar_linear的LinearLayout
		LinearLayout headBar = (LinearLayout) findViewById(R.id.head_bar_sub_linear);
		// 动态统一加载模式导航条
		headBar.addView(LayoutInflater.from(this).inflate(
				R.layout.head_bar_sub, null));
		mMenuListView = (ListView) mMenuView.findViewById(R.id.menu_pop_list);
		mMenu = (ImageView) headBar.findViewById(R.id.head_bar_sub_menu);
		if (!menuEnable) {
			mMenu.setVisibility(View.INVISIBLE);
		}
		mBack = (ImageView) headBar.findViewById(R.id.head_bar_sub_back);
		mTitle = (TextView) headBar.findViewById(R.id.head_bar_sub_title);
		mExtend1 = (TextView) headBar.findViewById(R.id.head_bar_sub_extend1);
		mExtend2 = (TextView) headBar.findViewById(R.id.head_bar_sub_extend2);
	}

	/**
	 * 控件加载后初始化内容属性
	 */
	public void initView() {

	}

	/**
	 * 控件初始化后设置事件侦听器
	 */
	public void setListener() {
		// 子版块菜单按钮点击事件
		if (menuEnable) {
			mMenu.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					initMorePopupWindow();
				}
			});
		}
		mBack.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				onBackKeyDown();
				finish();
			}
		});
	}

	public void setSubMenuList(int resArrayTextId) {
		// 获取子菜单数组
		mMenuName = getResources().getStringArray(resArrayTextId);
	}

	/**
	 * 按下返回键时事件处理
	 */
	public void onBackKeyDown() {

	}

	/**
	 * 按下菜单键时事件处理
	 */
	public void onMenuKeyDown() {

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 将返回结果移交给ActionHandler做进一步处理
		mActionHandler.activityResultHandler(requestCode, resultCode, data);
	}

	public void startActivityForResult(Intent intent, int requestCode) {
		super.startActivityForResult(intent, requestCode);
	}

	/**
	 * onActivityResult ResultCode = OK时调用此方法
	 * 
	 * @param requestCode
	 * @param data
	 */
	public void onResultSucess(int requestCode, Intent data) {

	}

	/**
	 * onActivityResult ResultCode != OK时调用此方法
	 * 
	 * @param requestCode
	 * @param data
	 */
	public void onResultNotOK(int requestCode, int resultCode, Intent data) {

	}

	/**
	 * 虚拟按键按下时
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackKeyDown();
			finish();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			if (menuEnable) {
				onMenuKeyDown();
				initMorePopupWindow();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	public void setTitle(String title) {
		mTitle.setText(title);
	}

	public boolean isMenuEnable() {
		return menuEnable;
	}

	public void setMenuEnable(boolean menuEnable) {
		this.menuEnable = menuEnable;
		if (!menuEnable) {
			mMenu.setVisibility(View.INVISIBLE);
		} else {
			mMenu.setVisibility(View.VISIBLE);
		}
	}

}
