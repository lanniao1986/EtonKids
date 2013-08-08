/**
 * 
 */
package com.enix.hoken.activity;

import com.enix.hoken.R;
import android.app.Activity;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;

import com.enix.hoken.basic.SubActivity;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.adapter.ParentListAdapter;
import com.enix.hoken.util.CommonUtil;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

/**
 * @author gumc
 * 
 */
public class ParentInfoActivity extends SubActivity implements
		OnItemClickListener, OnGestureListener {

	private ListView mParentList;
	private ParentListAdapter mParentAdapter;
	private int targetS_ID;
	private String targetS_NAME;
	private GestureDetector gd;
	// 事件状态
	private final char FLING_CLICK = 0;
	private final char FLING_LEFT = 1;
	private final char FLING_RIGHT = 2;
	private final char LONG_PRESS = 3;
	private char flingState = FLING_CLICK;
	public static final String INTENT_PARAM_SID = "SID";
	public static final String INTENT_PARAM_SNAME = "SNAME";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.parent_list);
		findView();
		initView();
		setListener();
	}

	public void initView() {
		super.initView();
		gd = new GestureDetector(this);
		mApplication = (BaseApplication) getApplication();
		targetS_ID = intent.getExtras().getInt(INTENT_PARAM_SID, -1);
		targetS_NAME = intent.getExtras().getString(INTENT_PARAM_SNAME);
		mParentAdapter = new ParentListAdapter(this,
				mApplication.getPinfoListBySid(targetS_ID));
		mParentList.setAdapter(mParentAdapter);
		mParentAdapter.notifyDataSetChanged();
		setTitle(targetS_NAME + "的家长联系人");
	}

	public void findView() {
		super.findView();
		mParentList = (ListView) findViewById(R.id.item_list);
	}

	/**
	 * 事件处理 其中flingState的值为事件 参数pos为ListView的每一项
	 */

	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		ParentListAdapter.ViewHolder holder = (ParentListAdapter.ViewHolder) arg1
				.getTag();
		Uri uri = null;
		Intent intent;

		switch (flingState) {
		// 处理左滑事件
		case FLING_LEFT:
			uri = Uri.parse("smsto://" + holder.mTel.getText().toString());
			intent = new Intent(Intent.ACTION_SENDTO, uri);// 指向用户短信的界面。
			startActivity(intent);
			break;
		// 处理右滑事件
		case FLING_RIGHT:
			uri = Uri.parse("tel://" + holder.mTel.getText().toString());
			intent = new Intent(Intent.ACTION_DIAL, uri);// 指向用户拨号的界面。
			startActivity(intent);
			flingState = FLING_CLICK;
			break;
		case LONG_PRESS:
			CommonUtil.showShortToast(this, "LONG_PRESSED");
			break;
		// 处理点击事件
		case FLING_CLICK:
			switch (pos) {
			case 0:
				break;
			case 1:
				break;
			}
			break;
		}
	}

	/**
	 * 覆写此方法，以解决ListView滑动被屏蔽问题
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		this.gd.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 覆写此方法，以使用手势识别
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gd.onTouchEvent(event);
	}

	/**
	 * 参数解释： e1：第1个ACTION_DOWN MotionEvent e2：最后一个ACTION_MOVE MotionEvent
	 * vX：X轴上的移动速度，像素/秒 vY：Y轴上的移动速度，像素/秒 触发条件 ：
	 * X坐标位移大于minX，Y坐标位移小于maxY，移动速度大于minV像素/秒
	 * 
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float vX, float vY) {
		final int minX = 120, maxY = 100, minV = 100;
		int x1 = (int) e1.getX(), x2 = (int) e2.getX();
		int y1 = (int) e1.getY(), y2 = (int) e2.getY();

		if (Math.abs(x1 - x2) > minX && Math.abs(y1 - y2) < maxY
				&& Math.abs(vX) > minV) {
			if (x1 > x2) {
				flingState = FLING_LEFT;
			} else {
				flingState = FLING_RIGHT;
			}
		}
		return false;
	}

	public void onLongPress(MotionEvent e) {
		flingState = LONG_PRESS;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float dX, float dY) {
		return false;
	}

	public void setListener() {
		super.setListener();
		mParentList.setOnItemClickListener(this);
	}

	public void onGesture(GestureOverlayView overlay, MotionEvent event) {

	}

	public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {

	}

	public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {

	}

	public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {

	}

}
