package com.enix.hoken.basic;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.enix.hoken.R;
import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.common.AppDataManager;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.adapter.FragmentListAdapter;
import com.enix.hoken.custom.adapter.ListItem;
import com.enix.hoken.custom.item.AnimationController;
import com.enix.hoken.custom.item.FlipperLayout;
import com.enix.hoken.sqllite.Dbmanager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class MainActivity extends FragmentActivity {

	public BaseApplication mApplication;
	public AppDataManager mAppDataManager;
	public Dbmanager mDbmanager;
	public ActionHandler mActionHandler;
	public FlipperLayout mRoot;
	public View mMainView[];
	public LayoutParams paramsFillParent;
	public AnimationController animationController;
	public static final int RESULT_FAILED = -1;
	public Vibrator vibrator;
	public static final int MYHOMEPAGE = 0;
	public static final int CLASS_MAMBER = 1;
	public static final int CLASS_ACTIVITY = 2;
	public static final int CLASS_ALBUM = 3;
	public static final int PUBLIC_RSS = 4;
	public static final int PARENT_CONTACTS = 5;
	public static final int TEACHER_CONTACTS = 6;
	public static final int DOWNLOAD_CENTER = 7;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApplication = (BaseApplication) getApplication();
		mDbmanager = new Dbmanager(this);
		mAppDataManager = new AppDataManager(this);
		mActionHandler = new ActionHandler(this);
		animationController = new AnimationController();
		mAppDataManager.setmDbmanager(mDbmanager);
		mDbmanager.setmAppDataManager(mAppDataManager);

		mRoot = new FlipperLayout(this);
		paramsFillParent = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT);
		mRoot.setLayoutParams(paramsFillParent);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

	}

	/**
	 * 控件加载FindViewById
	 */
	public abstract void findView();

	/**
	 * 控件加载后初始化内容属性
	 */
	public abstract void initView();

	/**
	 * 控件初始化后设置事件侦听器
	 */
	public abstract void setListener();

	/**
	 * 按下返回键时事件处理
	 */
	public abstract void onBackKeyDown();

	/**
	 * 按下菜单键时事件处理
	 */
	public abstract void onMenuKeyDown();

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
	public abstract void onResultSucess(int requestCode, Intent data);

	/**
	 * onActivityResult ResultCode != OK时调用此方法
	 * 
	 * @param requestCode
	 * @param data
	 */
	public abstract void onResultNotOK(int requestCode, int resultCode,
			Intent data);

	/**
	 * 虚拟按键按下时
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBackKeyDown();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU) {
			onMenuKeyDown();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 侧边栏打开
	public void open() {
		if (mRoot.getScreenState() == FlipperLayout.SCREEN_STATE_CLOSE) {
			mRoot.open();
		}
	}

	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
	}

	/**
	 * 日期选择对话框返回结果
	 * 
	 * @param fragmentTag
	 *            对话框的TAG
	 * @param mCalendar
	 *            设定日期的CALENDAR
	 * @param mDate
	 *            设定日期的DATE
	 */
	public void onDataPickerResult(String fragmentTag, Calendar mCalendar,
			Date mDate) {

	}

	/**
	 * 列表对话框点击返回结果
	 * 
	 * @param fragmentTag
	 *            对话框的TAG
	 * @param l
	 *            ListView
	 * @param v
	 *            View
	 * @param position
	 * @param id
	 */
	public void onListFragmentItemClick(String fragmentTag, ListView l, View v,
			int position, long id) {

	}

	public static class ArrayListFragment extends ListFragment {
		private MainActivity mActivity;
		private FragmentListAdapter adapter;

		public ArrayListFragment(MainActivity mActivity,
				FragmentListAdapter adapter) {
			this.mActivity = mActivity;
			this.adapter = adapter;
		}

		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(adapter);

		}

		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			mActivity
					.onListFragmentItemClick(this.getTag(), l, v, position, id);
			Log.i("ArrayListFragment", "Item clicked: " + id);
		}
	}

	/**
	 * 日期选择对话框
	 * 
	 * @author gumc
	 * 
	 */
	public static class DatePickFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		private Date date;
		private MainActivity mActivity;

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}

		private Calendar calendar;

		public Calendar getCalendar() {
			return calendar;
		}

		public void setCalendar(Calendar calendar) {
			this.calendar = calendar;
		}

		public DatePickFragment(MainActivity mActivity, String initDate) {
			this.mActivity = mActivity;
			calendar = Calendar.getInstance();
			try {
				date = format.parse(initDate);
			} catch (ParseException e) {
				date = new Date();
				e.printStackTrace();
			}
			calendar.setTime(date);
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {

			return new DatePickerDialog(getActivity(), this,
					calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
					calendar.get(Calendar.DATE));
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			calendar.set(year, month, day);
			date = calendar.getTime();
			mActivity.onDataPickerResult(this.getTag(), calendar, date);
		}

	}
}
