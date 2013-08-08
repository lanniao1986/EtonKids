package com.enix.hoken.custom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.enix.hoken.R;

public class LessonDetailPopAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private String lessonTitle;
	private String lessonDetail;

	public LessonDetailPopAdapter(Context context, String lessonTitle,String lesson_detail) {
		mInflater = LayoutInflater.from(context);
		this.lessonDetail = lesson_detail;
		this.lessonTitle= lessonTitle;
	}

	public int getCount() {
		return 1;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.lesson_popupwindow_item,
					null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView
					.findViewById(R.id.lesson_title);
			holder.detial = (TextView) convertView
					.findViewById(R.id.lesson_detail);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.title.setText(lessonTitle);
		holder.detial.setText(lessonDetail);
	
		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView detial;
	}
}
