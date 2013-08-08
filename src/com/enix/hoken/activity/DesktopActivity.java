package com.enix.hoken.activity;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.basic.*;
import com.enix.hoken.*;
import com.enix.hoken.common.*;
import com.enix.hoken.custom.item.Desktop.onChangeViewListener;
import com.enix.hoken.custom.item.*;
import com.enix.hoken.custom.item.FlipperLayout.OnOpenListener;

import com.enix.hoken.custom.mainview.*;
import com.enix.hoken.util.*;

public class DesktopActivity extends MainActivity implements OnOpenListener {

	private Desktop mDesktop;
	private UserMainView mUser;
	private MemberMainView mMember;
	private RssMainView mRss;
	private DownLoadMainView mDownLoad;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommonUtil.setActivity(this);
		setContentView(mRoot);
		findView();
		initView();
		setListener();
	}

	public void sendHandler(int actionID, Object arg) {
		switch (actionID) {
		case ActionHandler.REQUEST_FOR_AVATAR_UPDATE:
			Intent intent = new Intent();
			intent.putExtra("filePath",
					CommonUtil.getAvatarPath(this, arg.toString()));// 传递完整文件路径
			intent.putExtra("freeMode", false);
			intent.setClass(this, PicturePickerActivity.class);
			// startActivityForResult(intent, SEND_AVATAR_UPDATE);
			DesktopActivity.this.startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 确认退出对话框
	 */
	private void confirmDialog() {
		AlertDialog.Builder builder = new Builder(DesktopActivity.this);
		builder.setMessage("您确定要退出吗?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				mAppDataManager.serializerInfo(InfoSession.JINFOMAPLIST);
				dialog.dismiss();
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		builder.create().show();
	}

	@Override
	protected void onDestroy() {
		Log.d("DEBUG", "onDestroy()");
		super.onDestroy();
		mAppDataManager.serializerInfo(InfoSession.JINFOLIST);

	}

	@Override
	public void setListener() {
		// 注册各MainView的按键事件
		// 所有基于MainView必须在此setOnOpenListener才能是按钮Filp有效
		mUser.setOnOpenListener(this);
		mMember.setOnOpenListener(this);
		mRss.setOnOpenListener(this);
		mDownLoad.setOnOpenListener(this);
		mDesktop.setOnChangeViewListener(new onChangeViewListener() {

			public void onChangeView(int arg0) {
				switch (arg0) {
				case MYHOMEPAGE:
					mRoot.close(mUser);
					break;

				case CLASS_MAMBER:
					mRoot.close(mMember);
					break;

				case CLASS_ACTIVITY:
					// mRoot.close(mMessage.getView());
					break;

				case CLASS_ALBUM:
					// mRoot.close(mChat.getView());
					break;
				case PUBLIC_RSS:
					mRoot.close(mRss);
					break;
				case PARENT_CONTACTS:
					// if (RenRenData.mPageResults.size() == 0) {
					// mPage.init();
					// }
					// mRoot.close(mPage.getView());
					break;
				case TEACHER_CONTACTS:
					// mLocation.init();
					// mRoot.close(mLocation.getView());
					break;
				case DOWNLOAD_CENTER:
					mRoot.close(mDownLoad);
					break;
				}
			}
		});

	}

	@Override
	public void initView() {

		mDesktop = new Desktop(this);
		mUser = new UserMainView(this);
		mMember = new MemberMainView(this);
		mRss = new RssMainView(this);
		mDownLoad = new DownLoadMainView(this);
		mRoot.addView(mDesktop.getView(), paramsFillParent);
		// 初始化第一个界面为教师资料
		mRoot.addView(mUser.getView(), paramsFillParent);

	}

	@Override
	public void onBackKeyDown() {
		confirmDialog();

	}

	@Override
	public void onMenuKeyDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResultSucess(int requestCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case ActionHandler.REQUEST_FOR_AVATAR_UPDATE:
			mUser.init();
			mDesktop.init();
			CommonUtil.showShortToast(this, "头像更新成功!O(∩_∩)O");
			break;
		case ActionHandler.REQUEST_FOR_SCHEDULE_UPDATE:
			CommonUtil.showShortToast(this, "日程更新成功!O(∩_∩)O");
			break;
		case ActionHandler.REQUEST_FOR_MEMBER_EDIT:
			mMember.init(MemberMainView.MODE_MEMBER_EDIT);
			CommonUtil.showShortToast(this, "成员更新成功!O(∩_∩)O");
			break;
		case ActionHandler.REQUEST_FOR_MEMBER_CREATE:
			mMember.init(MemberMainView.MODE_MEMBER_EDIT);
			CommonUtil.showShortToast(this, "新建成员成功!O(∩_∩)O");
			break;
		}

	}

	@Override
	public void onResultNotOK(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case ActionHandler.REQUEST_FOR_AVATAR_UPDATE:
			switch (resultCode) {
			case ActionHandler.RESULT_FOR_AVATER_CREATE:

				String fileName = data
						.getStringExtra(PicturePickerActivity.INTENT_PARAM_CREATEFILE);

				break;
			case RESULT_CANCELED:
				CommonUtil.showShortToast(this, "头像未更新!╮(╯_╰)╭");
				break;
			}
			break;
		case ActionHandler.REQUEST_FOR_SCHEDULE_UPDATE:
			if (resultCode == ActionHandler.RESULT_FOR_SCHEDULE_DELETE) {
				if (mDbmanager.deleteDataById(InfoSession.SCHEDULELIST,
						data.getIntExtra("id", -1))) {
					mUser.init(UserMainView.MODE_SCHEDULE);// 重新绑定数据
					CommonUtil.showShortToast(this, "日程删除成功!O(∩_∩)O");
				}
			} else {
				CommonUtil.showShortToast(this, "日程未更新!╮(╯_╰)╭");
			}
			break;
		case ActionHandler.REQUEST_FOR_MEMBER_EDIT:
			switch (resultCode) {
			case ActionHandler.RESULT_FOR_MEMBER_DELETE:
				mMember.init(MemberMainView.MODE_MEMBER_EDIT);
				CommonUtil.showShortToast(this, "成员删除成功!O(∩_∩)O");
				break;
			}
		}
	}

	@Override
	public void findView() {
		// TODO Auto-generated method stub

	}
}
