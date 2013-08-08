package com.enix.hoken.custom.adapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.activity.*;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.basic.MainInfo;
import com.enix.hoken.basic.MainInfoList;
import com.enix.hoken.common.InfoSession;
import com.enix.hoken.custom.item.*;
import com.enix.hoken.R;
import com.enix.hoken.info.*;
import com.enix.hoken.util.BitmapCache;
import com.enix.hoken.util.DateUtil;
import com.enix.hoken.util.ImageTools;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.util.LetterParser;

public class StudentListAdapter extends MainAdapter {

	private Sinfo sinfo;
	private Date today = new Date();
	private Date inDate;
	private Date birthday;
	private int currentPosition = -1;

	private MainInfoList<MainInfo> mMainInfoList_bk;
	public static int resultCount = 0;// 传筛选结果列表个数
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private DateFormat df = DateFormat.getDateInstance(DateFormat.LONG,
			Locale.CHINA);
	SimpleDateFormat df2 = new SimpleDateFormat("M月d日");
	// 将名字转换为首字母再到数字，比如“王佳佳”--->"wjj"--->955
	private List<String> nameToNumList;
	// 名字列表
	private List<String> nameList;
	// 过滤结果条目数
	private BadgeView mResultCount;

	public BadgeView getmResultCount() {
		return mResultCount;
	}

	public void setmResultCount(BadgeView mResultCount) {
		this.mResultCount = mResultCount;
	}

	public StudentListAdapter(MainActivity activity) {
		super(activity, activity.mApplication.getSinfoList());

		mMainInfoList_bk = mMainInfoList;
		initFilterList();
	}

	// 删除成员时播放动画
	private void removeListItem(View rowView, final int position) {
		final Animation animation = (Animation) AnimationUtils.loadAnimation(
				rowView.getContext(), R.anim.item_anim);
		animation.setAnimationListener(new AnimationListener() {
			public void onAnimationStart(Animation animation) {
				mApplication.getSinfoList().remove(position);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {

				notifyDataSetChanged();
				animation.cancel();
			}
		});

		rowView.startAnimation(animation);
	}

	public void notifyDataSetChanged() {
		if (resultCount == 0) {
			mMainInfoList = (SinfoList) mAppDataManager
					.getInfoFromApp(InfoSession.SINFOLIST);
		}
		super.notifyDataSetChanged();
	}

	// 显示扩展按钮时播放动画
	private void showExtendLinear(View rowView, boolean isVisable) {
		if (isVisable) {
			animationController.slideIn(
					rowView.findViewById(R.id.linear_student_item_extend), 500,
					0);
		} else {
			animationController.slideOut(
					rowView.findViewById(R.id.linear_student_item_extend), 500,
					0);
		}
	}

	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.student_item, null);
			holder = new ViewHolder();
			holder.mAvatar = (ImageView) convertView
					.findViewById(R.id.student_avatar);
			holder.mName = (TextView) convertView
					.findViewById(R.id.student_name);
			holder.mButton = (Button) convertView
					.findViewById(R.id.student_button);
			holder.mTime = (TextView) convertView
					.findViewById(R.id.student_time);
			holder.mDis = (TextView) convertView.findViewById(R.id.student_dis);
			holder.mGender = (ImageView) convertView
					.findViewById(R.id.student_gender);
			holder.mBirthday = (TextView) convertView
					.findViewById(R.id.student_birthday);
			holder.mIsNew = (TextView) convertView
					.findViewById(R.id.student_new);
			holder.mPhotoList = (Button) convertView
					.findViewById(R.id.btn_student_item_photo_list);
			holder.mInfo = (Button) convertView
					.findViewById(R.id.btn_student_item_info);
			holder.mExtendLinear = (LinearLayout) convertView
					.findViewById(R.id.linear_student_item_extend);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		sinfo = (Sinfo) mMainInfoList.get(position);
		Drawable bitmap = ImageTools.bitmapToDrawable(BitmapCache.getInstance()
				.getBitmap(
						CommonUtil.getAvatarPath(mActivity, sinfo.getS_head()),
						false));

		if (bitmap != null) {
			holder.mAvatar.setImageDrawable(bitmap);
		} else {
			holder.mAvatar.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.default_avtar));
		}
		try {
			birthday = format.parse(sinfo.getS_birthday());
			inDate = format.parse(sinfo.getS_indate());
		} catch (ParseException e) {
		}
		holder.mName.setText(sinfo.getS_name() + "(" + sinfo.getS_age() + "岁)");
		holder.mDis.setText(sinfo.getS_dis());
		holder.mTime.setText("入园日：" + df.format(inDate));
		holder.mBirthday.setText("生日：" + df2.format(birthday));
		// 生日在三天内则字体为红色
		if (DateUtil.getDays(today, birthday) >= 3) {
			holder.mBirthday.setTextColor(Color.RED);
		} else {
			holder.mBirthday.setTextColor(Color.parseColor("#ff888888"));
		}
		// 入园日在当前7天内,显示新生报到
		if (DateUtil.getDays(today, inDate) >= 7) {
			holder.mIsNew.setVisibility(View.VISIBLE);
		} else {
			holder.mIsNew.setVisibility(View.GONE);
		}

		if ("0".equals(String.valueOf(sinfo.getS_gender()))) {
			holder.mGender.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.gender_girl));
		} else {
			holder.mGender.setImageDrawable(mActivity.getResources()
					.getDrawable(R.drawable.gender_boy));
		}
		final int sid = sinfo.getId();
		final String sname = sinfo.getS_name();
		if (position == currentPosition) {
			// 扩展按钮可视状态
			holder.mExtendLinear.setVisibility(View.VISIBLE);
			holder.mPhotoList.setClickable(true);
			holder.mInfo.setClickable(true);
			holder.mPhotoList.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Bundle mBundle = new Bundle();
					mBundle.putInt(PhotosListActivity.INTENT_PARAM_SID, sid);
					mActionHandler.startIntent(mActionHandler
							.createTranspotIntent(PhotosListActivity.class,
									mBundle));

				}
			});
			holder.mInfo.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

					Bundle mBundle = new Bundle();
					mBundle.putInt(SinfoSettingActivity.INTENT_PARAM_SID, sid);
					mBundle.putInt(SinfoSettingActivity.INTENT_PARAM_MODE,
							SinfoSettingActivity.MODE_VIEW);// 只读模式
					mActionHandler.startIntent(mActionHandler
							.createTranspotIntent(SinfoSettingActivity.class,
									mBundle));

				}
			});
		} else {
			holder.mExtendLinear.setVisibility(View.GONE);
		}
		holder.mButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Bundle mBundle = new Bundle();
				mBundle.putInt(ParentInfoActivity.INTENT_PARAM_SID, sid);
				mBundle.putString(ParentInfoActivity.INTENT_PARAM_SNAME, sname);//
				mActionHandler.startIntent(mActionHandler.createTranspotIntent(
						ParentInfoActivity.class, mBundle));
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {
			public boolean onLongClick(View v) {
				currentPosition = position;
				mActivity.vibrator.vibrate(80);
				notifyDataSetChanged();
				showExtendLinear(v, true);
				return false;
			}
		});
		// 单项点击事件 关闭扩展按钮
		convertView.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				showExtendLinear(v, false);
				currentPosition = -1;
				// removeListItem(v, position);
			}
		});
		return convertView;
	}

	private void initFilterList() {
		nameList = new ArrayList<String>();
		nameToNumList = new ArrayList<String>();
		Sinfo sinfo = null;
		for (int i = 0; i < mMainInfoList.size(); i++) {
			sinfo = (Sinfo) mMainInfoList.get(i);
			nameList.add(sinfo.getS_name());
		}

		initNameToNumList();
	}

	private void initNameToNumList() {
		if (nameToNumList != null) {
			nameToNumList.clear();
		}
		for (String name : nameList) {
			String num = CommonUtil.getNameNum(name);
			if (null != num) {
				nameToNumList.add(num);
			} else {
				nameToNumList.add(name);
			}
		}
	}

	/***
	 * 下面是给ListView添加过滤方法，首字母检索名字。
	 */
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				mMainInfoList = (MainInfoList<MainInfo>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
					showResultBadge();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s) {
				FilterResults results = new FilterResults();
				MainInfoList result = new MainInfoList();
				if (mMainInfoList_bk != null && mMainInfoList_bk.size() != 0) {
					if (!LetterParser.isNumeric(s.toString())
							&& nameList != null) {
						for (int i = 0; i < nameList.size(); i++) {
							if (nameToNumList.get(i).contains(s)) {
								result.add(mMainInfoList_bk.get(i));
							}
						}
					}
				}
				results.values = result;
				results.count = result.size();
				resultCount = results.count;
				return results;
			}
		};
		return filter;
	}

	// 还原原有数据
	public void resetData() {
		mMainInfoList = mMainInfoList_bk;
		notifyDataSetChanged();
	}

	public void showResultBadge() {
		mResultCount.setText(resultCount + "名");
		mResultCount.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
		mResultCount.setBadgeMargin(15, 10);
		mResultCount.setBadgeBackgroundColor(Color.parseColor("#A4C639"));
		TranslateAnimation anim = new TranslateAnimation(-100, 0, 0, 0);
		anim.setInterpolator(new BounceInterpolator());
		anim.setDuration(1000);
		mResultCount.setVisibility(View.VISIBLE);
		mResultCount.show(anim);
		mResultCount.clearFocus();
	}

	private class ViewHolder {
		ImageView mAvatar;
		TextView mName;
		TextView mDis;
		Button mButton;
		TextView mTime;
		ImageView mGender;
		TextView mBirthday;
		TextView mIsNew;
		Button mPhotoList;
		Button mInfo;
		LinearLayout mExtendLinear;
	}

}
