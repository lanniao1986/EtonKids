package com.enix.hoken.custom.adapter;



import android.view.LayoutInflater;
import android.view.*;
import android.widget.TextView;
import com.enix.hoken.basic.MainActivity;
import com.enix.hoken.basic.MainAdapter;
import com.enix.hoken.R;
import com.enix.hoken.info.*;

public class ParentListAdapter extends MainAdapter {

	private Pinfo pinfo;
	public ParentListAdapter(MainActivity mActivity, PinfoList mMainInfoList) {
		super(mActivity, mMainInfoList);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.parent_item, null);
			holder = new ViewHolder();

			holder.mName = (TextView) convertView
					.findViewById(R.id.parent_name);
			holder.mTel = (TextView) convertView.findViewById(R.id.parent_tel);
			holder.mAddress = (TextView) convertView
					.findViewById(R.id.parent_address);
			holder.mQQ = (TextView) convertView.findViewById(R.id.parent_qq);
			holder.mEmail = (TextView) convertView
					.findViewById(R.id.parent_email);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		pinfo = (Pinfo) mMainInfoList.get(position);
		holder.mName.setText(pinfo.getP_name());
		holder.mTel.setText("电话：" + pinfo.getP_tel());
		holder.mAddress.setText("地址：" + pinfo.getP_address());
		holder.mQQ.setText("QQ：" + pinfo.getP_qq());
		holder.mEmail.setText("Email：" + pinfo.getP_email());
		return convertView;
	}

	public class ViewHolder {
		public TextView mName;
		public TextView mTel;
		public TextView mAddress;
		public TextView mQQ;
		public TextView mEmail;
	}
}
