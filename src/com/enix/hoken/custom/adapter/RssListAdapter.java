package com.enix.hoken.custom.adapter;

import java.util.HashMap;

import android.view.*;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.R;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;

public class RssListAdapter extends MainAdapter {

	private Rssinfo mRssinfo;
	private HashMap<Integer, Boolean> mExplandMap;

	public RssListAdapter(MainActivity mActivity, RssinfoList mMainInfoList) {
		super(mActivity, mMainInfoList);
		mExplandMap = new HashMap<Integer, Boolean>();
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.rss_item, null);
			holder = new ViewHolder();

			holder.mRelativeLayout = (RelativeLayout) convertView
					.findViewById(R.id.rss_relativelayout);
			holder.mTitle = (TextView) convertView
					.findViewById(R.id.rss_item_title);
			holder.mDiscription = (TextView) convertView
					.findViewById(R.id.rss_item_description);
			holder.mDateTime = (TextView) convertView
					.findViewById(R.id.rss_item_datetime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		mRssinfo = (Rssinfo) mMainInfoList.get(position);
		holder.mTitle.setText((position + 1) + "." + mRssinfo.getRss_title());
		holder.mDiscription.setText(mRssinfo.getRss_description()
				+ ".……(点击查看原文)");
		holder.mDateTime.setText(mRssinfo.getRss_datetime());
		holder.mTitle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 点击展开关闭详细内容
				mExplandMap.put(position, mExplandMap.get(position) == null
						|| mExplandMap.get(position) == false ? true : false);
				notifyDataSetChanged();
			}
		});
		holder.mDiscription.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// 点击调用外部浏览器打开网页
				Rssinfo targetRssinfo = (Rssinfo) mMainInfoList.get(position);
				mActionHandler.startIntent(mActionHandler
						.createBrowserIntent(targetRssinfo.getRss_link()));

			}
		});
		if (mExplandMap.get(position) != null && mExplandMap.get(position)) {
			holder.mDiscription.setVisibility(View.VISIBLE);
			holder.mDateTime.setVisibility(View.VISIBLE);
		} else {
			holder.mDiscription.setVisibility(View.GONE);
			holder.mDateTime.setVisibility(View.GONE);
		}
		return convertView;
	}

	public class ViewHolder {
		public RelativeLayout mRelativeLayout;
		public TextView mTitle;
		public TextView mDiscription;
		public TextView mDateTime;
	}
}
