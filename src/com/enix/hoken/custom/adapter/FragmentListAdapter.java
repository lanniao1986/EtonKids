package com.enix.hoken.custom.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


public class FragmentListAdapter extends BaseAdapter {
	private ArrayList<ListItem> itemList;
	private Context mContext;
	private LayoutInflater mInflater;

	public FragmentListAdapter(Context context, ArrayList<ListItem> itemList) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		this.itemList = itemList;
	}

	public int getCount() {
		return itemList.size();
	}

	public Object getItem(int position) {
		return itemList.get(position);
	}

	public long getItemId(int position) {
		return itemList.get(position).getId();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(android.R.layout.simple_list_item_1,
					null);
			holder = new ViewHolder();
			holder.name = (TextView) convertView
					.findViewById(android.R.id.text1);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.name.setText(itemList.get(position).getValue());
	
		return convertView;
	}
	class ViewHolder {
		TextView name;
	}
}
