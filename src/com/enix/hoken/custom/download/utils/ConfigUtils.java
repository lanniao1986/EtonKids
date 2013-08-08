package com.enix.hoken.custom.download.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class ConfigUtils {

	public static final String PREFERENCE_NAME = "com.enix.hoken.custom.download";
	public static final int URL_COUNT = 3;// URL集合最大条目数
	public static final String KEY_URL = "url";

	public static SharedPreferences getPreferences(Context context) {
		return context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_WORLD_WRITEABLE);
	}

	/**
	 * 从首选项存储器中获取指定KEY的VALUE字符串值
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static String getString(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}

	/**
	 * 保存指定键值对象到首选项存储器
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setString(Context context, String key, String value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	/**
	 * 存储指定URL到首选项存储器
	 * 
	 * @param context
	 * @param index
	 * @param url
	 */
	public static void storeURL(Context context, int index, String url) {
		setString(context, KEY_URL + index, url);
	}

	/**
	 * 从首选项存储器中清空指定键值
	 * 
	 * @param context
	 * @param index
	 */
	public static void clearURL(Context context, int index) {
		setString(context, KEY_URL + index, "");
	}

	/**
	 * 从首选项存储器中获取下载对象URL
	 * 
	 * @param context
	 * @param index
	 * @return
	 */
	public static String getURL(Context context, int index) {
		return getString(context, KEY_URL + index);
	}

	/**
	 * 从首选项存储器中获取下载对象URL集合(最大条目数受限)
	 * 
	 * @param context
	 * @return
	 */
	public static List<String> getURLArray(Context context) {
		List<String> urlList = new ArrayList<String>();
		for (int i = 0; i < URL_COUNT; i++) {
			if (!TextUtils.isEmpty(getURL(context, i))) {
				urlList.add(getString(context, KEY_URL + i));
			}
		}
		return urlList;
	}

	public static final String KEY_RX_WIFI = "rx_wifi";
	public static final String KEY_TX_WIFI = "tx_wifi";
	public static final String KEY_RX_MOBILE = "tx_mobile";
	public static final String KEY_TX_MOBILE = "tx_mobile";
	public static final String KEY_Network_Operator_Name = "operator_name";

	/**
	 * 获取指定INT型键值VALUE
	 * 
	 * @param context
	 * @param key
	 * @return
	 */
	public static int getInt(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getInt(key, 0);
		else
			return 0;
	}

	/**
	 * 存储INT型对象到首选项存储器
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void setInt(Context context, String key, int value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public static long getLong(Context context, String key) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getLong(key, 0L);
		else
			return 0L;
	}

	public static void setLong(Context context, String key, long value) {
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public static void addLong(Context context, String key, long value) {
		setLong(context, key, getLong(context, key) + value);
	}
}
