package com.enix.hoken.sqllite;

import java.io.*;
import java.util.*;

import com.enix.hoken.info.*;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {

	public static final String DB_NAME = "Eton.db"; // 数据库名称
	public static final String ASSETS_NAME = "Eton.db";
	public static final int DB_VERSION = 1; // 数据库版本
	public static final String DB_PATH = "/data/data/com.enix.hoken/databases/";// 数据库路径

	private static final int ASSETS_SUFFIX_BEGIN = 101;// 第一个文件名后缀
	private static final int ASSETS_SUFFIX_END = 103;// 最后一个文件名后缀

	private Context context;
	private InputStream is;
	private OutputStream os;
	private SQLiteDatabase db;
	private boolean isChecked = false;


	public DbHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}
	
	

	/**
	 * 打开数据库
	 * 
	 * @return
	 */
	public SQLiteDatabase getDataBase() {
		try {
			// 初次校验数据库时调用创建
			if (!isChecked) {
				createDataBase();
			}
			db = SQLiteDatabase.openOrCreateDatabase(DB_PATH + DB_NAME, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return db;
	}

	public void createDataBase() throws IOException {
		boolean isDbExist = checkDataBase();

		if (isDbExist) {

		} else {
			// 不存在的情况下，创建数据库
			try {
				File dir = new File(DB_PATH);
				if (!dir.exists()) {
					dir.mkdir();
				}
				File dbf = new File(DB_PATH + DB_NAME);

				if (dbf.exists()) {
					dbf.delete();
				}
				SQLiteDatabase.openOrCreateDatabase(dbf, null);
				// 复制assets目录下数据库到设备目录
				copyDataBase();
				isChecked = true;
			} catch (IOException e) {
				// 数据库创建失败
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将assets目录下数据库文件拷贝到设备
	 * 
	 * @throws IOException
	 */
	private void copyDataBase() throws IOException  {

		try {
		 	is = this.context.getAssets().open(ASSETS_NAME);
			os = new FileOutputStream(DB_PATH + DB_NAME);

			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} catch (IOException e) {
		
			e.printStackTrace();
		}finally{
			os.flush();
			os.close();
			is.close();
		}
	}

	/**
	 * 复制大于1MB文件时需要用此方法复制
	 * 
	 * @throws IOException
	 */
	private void copyBigDataBase() throws IOException {
		os = new FileOutputStream(DB_PATH + DB_NAME);
		for (int i = ASSETS_SUFFIX_BEGIN; i < ASSETS_SUFFIX_END + 1; i++) {
			is = this.context.getAssets().open(DB_PATH + DB_NAME + "." + i);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
			os.flush();
			is.close();
		}
		os.close();
	}

	public synchronized void close() {
		if (db != null) {
			db.close();
		}
		super.close();
	}

	/**
	 * 检查数据库文件是否正常打开
	 * 
	 * @return boolean
	 */
	private boolean checkDataBase() {
		SQLiteDatabase checkDb = null;
		try {
			checkDb = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (SQLiteException e) {
			// 无法打开本地数据库文件
			e.printStackTrace();
		}finally{
			if (checkDb != null) {
				checkDb.close();
			}
		}
		
		return checkDb != null ? true : false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
