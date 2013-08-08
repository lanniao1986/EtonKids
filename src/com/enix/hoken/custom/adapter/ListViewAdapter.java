package com.enix.hoken.custom.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.enix.hoken.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {
	// 填充数据LIST
	private ArrayList<String> arrayData;
	// 控制checkbox是否勾选
	private static HashMap<Integer, Boolean> isSelected;

	// 用来导入布局
	private LayoutInflater inflater = null;

	// 构造器
	public ListViewAdapter(ArrayList<String> arrayData, Context context) {
		this.arrayData = arrayData;
		inflater = LayoutInflater.from(context);
		isSelected = new HashMap<Integer, Boolean>();
		// 初始化数据
		initData();
	}

	private void initData() {
		for (int i = 0; i < arrayData.size(); i++) {
			getIsSelected().put(i, false);
		}
	}

	public static HashMap<Integer, Boolean> getIsSelected() {
		return isSelected;
	}

	@Override
	public int getCount() {

		return arrayData.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			// 获得ViewHolder对象
			holder = new ViewHolder();
			// 导入布局并赋值给convertView对象
			convertView = inflater
					.inflate(R.layout.lay_line_for_listview, null);
			holder.tv = (TextView) convertView.findViewById(R.id.item_tv);
			holder.cb = (CheckBox) convertView.findViewById(R.id.item_cb);
			// 为VIEW设置标签
			convertView.setTag(holder);
		} else {
			// 取出ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		// 设置ListView中TextView的显示
		holder.tv.setText(arrayData.get(position));
		// 设置CheckBox的勾选状态
		holder.cb.setChecked(getIsSelected().get(position));
		// 奇偶数行间隔颜色
		if (position % 2 == 0) {
			convertView.setBackgroundResource(R.color.tv_color_gray);
		} else {
			convertView.setBackgroundResource(R.color.tv_color_white);
		}
		return convertView;
	}

	public static void setIsSelected(HashMap<Integer, Boolean> isSelected) {

		ListViewAdapter.isSelected = isSelected;
	}

	public class ViewHolder {
		public TextView tv = null;
		public CheckBox cb = null;
		
	}

}
