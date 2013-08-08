package com.enix.hoken.util;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.enix.hoken.R;
import com.enix.hoken.basic.MainActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources.NotFoundException;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.*;
import android.provider.MediaStore;
import android.util.Log;
import android.view.*;
import android.widget.*;

/**
 * 通用工具类
 * 
 * 
 */
public class CommonUtil {
	public static final int FILETYPE_MUSIC = 2;
	public static final int FILETYPE_PICTURE = 1;
	public static final int FILETYPE_DOC = 4;
	public static final int FILETYPE_VIDEO = 3;
	private static MainActivity mActivity;

	public static MainActivity getActivity() {
		return mActivity;
	}

	public static void setActivity(MainActivity mActivity) {
		CommonUtil.mActivity = mActivity;
	}

	private static final String SDCARD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";
	private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;

	/**
	 * 如果输入的是中文或者是英文字母则返回首字母
	 * 
	 * @param name
	 * @return
	 */
	public static String getNameNum(String name) {
		// TODO Auto-generated method stub
		if (name != null && name.length() != 0) {
			int len = name.length();
			char[] nums = new char[len];
			for (int i = 0; i < len; i++) {
				String tmp = name.substring(i);
				// nums[i] =
				// getOneNumFromAlpha(letterParser.getFirstAlpha(tmp));
				nums[i] = new LetterParser().getFirstAlpha(tmp);
			}
			return new String(nums);
		}
		return null;
	}

	/**
	 * 返回首字母对应的键盘数字
	 * 
	 * @param firstAlpha
	 * @return
	 */
	public static char getOneNumFromAlpha(char firstAlpha) {
		// TODO Auto-generated method stub
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
			return '2';
		case 'd':
		case 'e':
		case 'f':
			return '3';
		case 'g':
		case 'h':
		case 'i':
			return '4';
		case 'j':
		case 'k':
		case 'l':
			return '5';
		case 'm':
		case 'n':
		case 'o':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
			return '7';
		case 't':
		case 'u':
		case 'v':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return '9';
		default:
			return '0';
		}
	}

	/**
	 * 从SD卡中根据表情符号获取表情图片
	 * 
	 * @param imageName
	 *            表情的名称
	 * @return 表情的Bitmap
	 */
	public static Bitmap getLoaclEmoticons(String imageName) {
		File dir = new File("/sdcard/EtonKids/Emoticons/");
		if (!dir.exists() || dir == null) {
			dir.mkdirs();
		}
		File[] cacheFiles = dir.listFiles();
		int i = 0;
		if (cacheFiles != null) {
			for (; i < cacheFiles.length; i++) {
				if (imageName.equals(cacheFiles[i].getName())) {
					break;
				}
			}
		}
		if (i < cacheFiles.length) {
			/**
			 * 因表情图片较小,则这里返回了一个60*60的Bitmap,该数值可根据情况调整
			 */
			return Bitmap.createScaledBitmap(
					BitmapFactory.decodeFile("/sdcard/EtonKids/Emoticons/"
							+ imageName), 60, 60, true);
		}
		return null;
	}

	/**
	 * 是否加载了内存卡
	 * 
	 * @return
	 */
	public static boolean sdcardMounted() {
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存文本到SD卡中
	 * 
	 * @param context
	 *            上下文
	 * @param fileName
	 *            文件的名称
	 * @param filePath
	 *            文件的路径
	 * @param stringToWrite
	 *            写入的字符串
	 */
	public void savedToText(String fileName, String filePath,
			String stringToWrite) {
		if (sdcardMounted()) {
			String foldername = getRootFolder() + filePath;

			File folder = new File(foldername);
			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}
			File targetFile = new File(foldername + "/" + fileName);
			OutputStreamWriter osw;

			try {

				if (!targetFile.exists()) {
					targetFile.createNewFile();
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile), "UTF-8");
					osw.write(stringToWrite);
					osw.close();

				} else {
					/**
					 * 再次写入时不采用拼接的方法,而是重新写
					 */
					osw = new OutputStreamWriter(new FileOutputStream(
							targetFile, false), "UTF-8");
					osw.write(stringToWrite);
					osw.flush();
					osw.close();
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * 读取SD卡中文件
	 * 
	 * @param fileName
	 *            文件的名称
	 * @param filePath
	 *            文件的路径
	 * @return 文件中的字符
	 */
	public String readFromFile(String fileName, String filePath) {

		if (sdcardMounted()) {

			String foldername = "/sdcard/EtonKids/" + filePath + "/";
			File folder = new File(foldername);

			if (folder == null || !folder.exists()) {
				folder.mkdirs();
			}

			File targetFile = new File(foldername + "/" + fileName);
			String readedStr = "";

			try {
				if (!targetFile.exists()) {
					targetFile.createNewFile();
				} else {
					InputStream in = new BufferedInputStream(
							new FileInputStream(targetFile));
					BufferedReader br = new BufferedReader(
							new InputStreamReader(in, "UTF-8"));
					String tmp;
					while ((tmp = br.readLine()) != null) {
						readedStr += tmp;
					}
					br.close();
					in.close();
					return readedStr;
				}
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	public static String getStringResource(Context context, int id) {
		return context.getResources().getString(id);
	}

	/**
	 * 获取程序主目录
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getRootFolder() {
		return SDCARD_ROOT
				+ getStringResource(mActivity, R.string.sd_folder_root) + "/";
	}

	/**
	 * 获取程序缓存目录
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getCacheFolder(Context mContext) {
		return getRootFolder()
				+ getStringResource(mContext, R.string.sd_folder_cache) + "/";
	}

	/**
	 * 通过头像名从内存卡的头像文件夹获取图片
	 * 
	 * @param mContext
	 * @param avater_name
	 * @return
	 */
	public static BitmapDrawable getAvatar(Context mContext, String avater_name) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(getRootFolder()
					+ getStringResource(mContext, R.string.sd_folder_avater)
					+ "/" + avater_name);
		} catch (NotFoundException e) {
			return null;
		}
		if (bitmap != null) {
			return new BitmapDrawable(bitmap);
		} else {
			return null;
		}
	}

	/**
	 * 弹出确认窗口
	 * 
	 * @param title
	 * @param message
	 * @param buttonText
	 * @param context
	 * @param onPositiveClickListener
	 */
	public static void showConfirmDialog(String title, String message,
			String[] buttonText, Context context,
			DialogInterface.OnClickListener onPositiveClickListener) {
		AlertDialog.Builder builder = new Builder(context);
		if (buttonText == null || buttonText.length < 2) {
			buttonText = new String[] { "确定", "取消" };
		}
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(buttonText[0], onPositiveClickListener);
		builder.setNegativeButton(buttonText[1],
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		builder.create().show();
	}

	/**
	 * 根据R.id 返回Bitmap对象
	 * 
	 * @param mContext
	 * @param resId
	 * @return
	 */
	public static Bitmap getBitmapByResId(Context mContext, int resId) {
		Bitmap bitmap = null;
		if (mContext != null) {
			bitmap = BitmapFactory.decodeResource(mContext.getResources(),
					resId);
		}
		return bitmap;
	}

	/**
	 * 获取指定头像名称的完整图片路径
	 * 
	 * @param mContext
	 * @param avater_name
	 * @return
	 */
	public static String getAvatarPath(Context mContext, String avater_name) {
		return getRootFolder()
				+ getStringResource(mContext, R.string.sd_folder_avater) + "/"
				+ avater_name;

	}

	/**
	 * 获取配置文件中的下载文件夹路径
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getDownLoadPath() {
		return getRootFolder()
				+ getStringResource(mActivity, R.string.sd_folder_download)
				+ "/";
	}

	/**
	 * 获取配置文件中的多媒体文件夹路径
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getMediaPath(Context mContext) {
		return getRootFolder()
				+ getStringResource(mContext, R.string.sd_folder_media);
	}

	public static BitmapDrawable getClassAlbum(Context mContext, String filePath) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(getRootFolder()
					+ getStringResource(mContext,
							R.string.sd_folder_class_album) + "/" + filePath);
		} catch (NotFoundException e) {
			return null;
		}
		if (bitmap != null) {
			return new BitmapDrawable(bitmap);
		} else {
			return null;
		}
	}

	public static String getStudentAlbumPath(Context mContext) {
		return getRootFolder()
				+ getStringResource(mContext, R.string.sd_folder_student_album)
				+ "/";
	}

	public static BitmapDrawable getBitmapDrawableByPath(Context mContext,
			String filePath) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(filePath);
		} catch (NotFoundException e) {
			return null;
		}
		if (bitmap != null) {
			return new BitmapDrawable(bitmap);
		} else {
			return null;
		}
	}

	public static Bitmap getBitByPath(Context mContext, String filePath) {
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeFile(filePath);
		} catch (NotFoundException e) {
			return null;
		}
		if (bitmap != null) {
			return bitmap;
		} else {
			return null;
		}
	}

	/**
	 * 快速显示短Toast
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showShortToast(Context mContext, String msg) {
		Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * 快速显示长Toast
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showLongToast(Context mContex, String msg) {
		Toast.makeText(mContex, msg, Toast.LENGTH_LONG).show();
	}

	/**
	 * 快速显示长Toast(定制显示位置) position : Gravity.CENTER
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showLongToast(Context mContex, String msg, int position,
			int offsetX, int offsetY) {

		Toast toast = Toast.makeText(mContex, msg, Toast.LENGTH_LONG);
		toast.setGravity(position, offsetX, offsetY);
		toast.show();
	}

	/**
	 * 快速显示长Toast(定制显示位置,扩展图片) position : Gravity.CENTER
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showLongToast(Context mContex, int drawableID,
			String msg, int position, int offsetX, int offsetY) {
		Toast toast = Toast.makeText(mContex, msg, Toast.LENGTH_LONG);
		toast.setGravity(position, offsetX, offsetY);
		LinearLayout layout = (LinearLayout) toast.getView();
		ImageView image = new ImageView(mContex);
		image.setImageResource(drawableID);
		layout.addView(image, 0);
		toast.show();
	}

	/**
	 * 快速显示长Toast(定制显示位置,扩展图片,自定LAYOUT) position : Gravity.CENTER
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showLongToast(Context mContex, Activity mActivity,
			int drawableID, String title, String msg, int position,
			int offsetX, int offsetY) {

		LayoutInflater inflater = mActivity.getLayoutInflater();
		View view = inflater.inflate(R.layout.userdefinedtoast,
				(ViewGroup) mActivity.findViewById(R.id.toast_layout));
		TextView txtView_Title = (TextView) view.findViewById(R.id.txt_Title);
		txtView_Title.setText(title);
		TextView txtView_Context = (TextView) view
				.findViewById(R.id.txt_context);
		txtView_Context.setText(msg);
		ImageView imageView = (ImageView) view.findViewById(R.id.image_toast);
		imageView.setImageResource(drawableID);
		Toast toast = new Toast(mContex);
		toast.setGravity(position, offsetX, offsetY);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(view);
		toast.show();
	}

	/**
	 * 快速显示对话框式Toast(定制显示位置,扩展图片,自定LAYOUT) position : Gravity.CENTER
	 * 
	 * @param mContext
	 * @param msg
	 */
	public static void showDialogToast(Context mContex, Activity mActivity,
			int drawableID, String title, String msg, int position,
			int offsetX, int offsetY) {
		LayoutInflater inflater1 = mActivity.getLayoutInflater();
		View view1 = inflater1.inflate(R.layout.userdefinedtoast,
				(ViewGroup) mActivity.findViewById(R.id.toast_layout));
		TextView txtView_Title = (TextView) view1.findViewById(R.id.txt_Title);
		txtView_Title.setText(title);
		TextView txtView_Context = (TextView) view1
				.findViewById(R.id.txt_context);
		txtView_Context.setText(msg);
		ImageView imageView = (ImageView) view1.findViewById(R.id.image_toast);
		imageView.setImageResource(drawableID);
		Builder builder = new AlertDialog.Builder(mContex);
		builder.setView(view1);
		AlertDialog dialog = builder.create();
		dialog.show();
	}

	/**
	 * 获取指定目录下的所有指定文件 filetype 0:所有 1:图片 2:音乐 3:视频 4:文档
	 * 
	 * @param path
	 * @return
	 */
	public static ArrayList getFilesByPath(String path, final int filetype) {
		ArrayList fileList = new ArrayList();
		try {
			File[] allFiles = new File(path).listFiles();
			for (int i = 0; i < allFiles.length; i++) {
				File file = allFiles[i];
				if (file.isFile()) {
					switch (filetype) {
					case FILETYPE_PICTURE:
						if (isPictureFile(file.getName())) {
							fileList.add(file.getAbsolutePath());
						}
						break;
					case FILETYPE_MUSIC:
						if (isMusicFile(file.getName())) {
							fileList.add(file.getAbsolutePath());
						}
						break;
					case FILETYPE_VIDEO:
						if (isVideoFile(file.getName())) {
							fileList.add(file.getAbsolutePath());
						}
						break;
					case FILETYPE_DOC:
						if (isDocFile(file.getName())) {
							fileList.add(file.getAbsolutePath());
						}

						break;
					default:
						fileList.add(file.getAbsolutePath());
						break;
					}

				} else if (!file.getAbsolutePath().contains(".thumnail")) {
					getFilesByPath(file.getAbsolutePath(), filetype);
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return fileList;
	}

	/*
	 * Java文件操作 获取不带扩展名的文件名
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * 获取文件扩展名
	 * 
	 * @param filename
	 * @return
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/**
	 * 判断文件是否为图片格式
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isPictureFile(String filename) {
		String extensionName = getExtensionName(filename).toLowerCase();
		if (("jpg".equals(extensionName)) || ("jpeg".equals(extensionName))
				|| ("png".equals(extensionName))
				|| ("gif".equals(extensionName))
				|| ("bmp".equals(extensionName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否为音乐格式
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isMusicFile(String filename) {
		String extensionName = getExtensionName(filename).toLowerCase();
		if (("mp3".equals(extensionName)) || ("wma".equals(extensionName))
				|| ("arm".equals(extensionName))
				|| ("wav".equals(extensionName))
				|| ("aac".equals(extensionName))
				|| ("ogg".equals(extensionName))
				|| ("mid".equals(extensionName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否为视频格式
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isVideoFile(String filename) {
		String extensionName = getExtensionName(filename).toLowerCase();
		if (("mp4".equals(extensionName)) || ("wmv".equals(extensionName))
				|| ("mkv".equals(extensionName))
				|| ("rmvb".equals(extensionName))
				|| ("avi".equals(extensionName))
				|| ("flash".equals(extensionName))
				|| ("3gp".equals(extensionName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断文件是否为视频格式
	 * 
	 * @param filename
	 * @return
	 */
	public static boolean isDocFile(String filename) {
		String extensionName = getExtensionName(filename).toLowerCase();
		if (("txt".equals(extensionName)) || ("doc".equals(extensionName))
				|| ("pdf".equals(extensionName))
				|| ("xsl".equals(extensionName))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除指定文件夹内最近时间内保存的最后一张图片
	 * 
	 * @param path
	 * @return
	 */
	public static boolean deleteLastFromFloder(String path) {
		boolean success = false;
		try {
			ArrayList<File> images = getFilesByPath(path, 1);
			File latestSavedImage = images.get(0);
			if (latestSavedImage.exists()) {
				for (int i = 1; i < images.size(); i++) {
					File nextFile = images.get(i);
					if (nextFile.lastModified() > latestSavedImage
							.lastModified()) {
						latestSavedImage = nextFile;
					}
				}
				success = latestSavedImage.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	/**
	 * 设置选项弹出窗口
	 * 
	 * @param title
	 * @param items
	 * @param mContext
	 * @param onClickListener
	 */
	public static void showDialog(String title, String[] items,
			Context mContext, DialogInterface.OnClickListener onClickListener) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle(title);
		builder.setItems(items, onClickListener);
		builder.create().show();
	}

	public static void showEditDialog(String title, String[] items,
			final Context mContext, final TextView targetView) {
		AlertDialog.Builder builder = new Builder(mContext);
		final EditText input = new EditText(mContext);
		input.setText(targetView.getText());
		builder.setTitle(title);
		builder.setView(input);
		builder.setPositiveButton(items[0],
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						targetView.setText(input.getText().toString());
					}
				}).setNegativeButton(items[1], null).create();
		builder.create().show();
	}

	/**
	 * 从完整文件名中获取文件名
	 * 
	 * @param fullPath
	 * @return
	 */
	public static String getFilenameFromFullPath(String fullPath) {
		return fullPath.substring(fullPath.lastIndexOf("/") + 1,
				fullPath.length());
	}

	public static String getPathFromFullPath(String fullPath) {
		return fullPath.substring(0, fullPath.lastIndexOf("/") + 1);
	}

	/**
	 * 复制文件
	 * 
	 * @param sourceFile
	 * @param targetFiles
	 */
	public static boolean copyFile(String sourceFile, String targetFile) {
		if (!sdcardMounted()) {
			return false;
		}
		BufferedInputStream inBuff = null;
		BufferedOutputStream outBuff = null;
		try {
			File dir = new File(getPathFromFullPath(targetFile));
			if (!dir.exists()) {
				dir.mkdir();
			}
			File target = new File(targetFile);
			if (target.exists()) {
				target.delete();
			}
			// 新建文件输入流并对它进行缓冲
			inBuff = new BufferedInputStream(new FileInputStream(sourceFile));
			// 新建文件输出流并对它进行缓冲
			outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} catch (IOException e) {
			return false;
		} finally {
			// 关闭流
			try {
				if (inBuff != null)
					inBuff.close();
				if (outBuff != null)
					outBuff.close();
			} catch (IOException e) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 复制文件夹
	 * 
	 * @param sourceDir
	 * @param targetDir
	 * @throws IOException
	 */
	public static void copyDirectiory(String sourceDir, String targetDir)
			throws IOException {
		// 新建目标目录
		(new File(targetDir)).mkdirs();
		// 获取源文件夹当前下的文件或目录
		File[] file = (new File(sourceDir)).listFiles();
		for (int i = 0; i < file.length; i++) {
			if (file[i].isFile()) {
				// 源文件
				File sourceFile = file[i];
				// 目标文件
				File targetFile = new File(
						new File(targetDir).getAbsolutePath() + File.separator
								+ file[i].getName());
				copyFile(sourceFile.getAbsolutePath(),
						targetFile.getAbsolutePath());
			}
			if (file[i].isDirectory()) {
				// 准备复制的源文件夹
				String dir1 = sourceDir + "/" + file[i].getName();
				// 准备复制的目标文件夹
				String dir2 = targetDir + "/" + file[i].getName();
				copyDirectiory(dir1, dir2);
			}
		}
	}

	/**
	 * 
	 * @param srcFileName
	 * @param destFileName
	 * @param srcCoding
	 * @param destCoding
	 * @throws IOException
	 */
	public static void copyFile(File srcFileName, File destFileName,
			String srcCoding, String destCoding) throws IOException {// 把文件转换为GBK文件
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					srcFileName), srcCoding));
			bw = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(destFileName), destCoding));
			char[] cbuf = new char[1024 * 5];
			int len = cbuf.length;
			int off = 0;
			int ret = 0;
			while ((ret = br.read(cbuf, off, len)) > 0) {
				off += ret;
				len -= ret;
			}
			bw.write(cbuf, 0, off);
			bw.flush();
		} finally {
			if (br != null)
				br.close();
			if (bw != null)
				bw.close();
		}
	}

	/**
	 * 删除指定路径的文件或文件夹
	 * 
	 * @param filepath
	 * @throws IOException
	 */
	public static boolean del(String filepath, Context mContext) {
		if (!checkSdMounted(mContext)) { // 检测sd是否可用
			return false;
		}
		File f = new File(filepath);// 定义文件路径
		try {
			if (f.exists()) {
				if (f.isDirectory()) {// 判断是文件还是目录
					if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
						f.delete();
					} else {// 若有则把文件放进数组，并判断是否有下级目录
						File delFile[] = f.listFiles();
						int i = f.listFiles().length;
						for (int j = 0; j < i; j++) {
							if (delFile[j].isDirectory()) {
								del(delFile[j].getAbsolutePath(), mContext);// 递归调用del方法并取得子目录路径
							}
							delFile[j].delete();// 删除文件
						}
					}
				} else {
					f.delete();
				}
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;
	}

	public static boolean checkSdMounted(Context mContext) {

		if (!sdcardMounted()) { // 检测sd是否可用
			Toast.makeText(mContext, "内存卡不可用,请检查 : (  ", Toast.LENGTH_LONG)
					.show();
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 获取调用系统相机后得到的照片,并保存到sdcard卡中(onActivityResult)
	 * 
	 * @param data
	 * 
	 * @return
	 */
	public static boolean saveBitmapToSd(Bitmap bitmap, String fullPathName,
			Context mContext) {
		if (!checkSdMounted(mContext)) { // 检测sd是否可用
			return false;
		}
		FileOutputStream b = null;
		File file = new File(fullPathName);
		file.mkdirs();// 创建文件夹
		try {
			b = new FileOutputStream(fullPathName);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 调用系统相册的操作 获取选中的Bitmap(onActivityResult)
	 * 
	 * @param data
	 * 
	 */
	public static Bitmap getBitmapFromGarlly(Intent data, Context mContext) {
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(selectedImage,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return BitmapFactory.decodeFile(picturePath);
	}

	/**
	 * 调用系统相册的操作,将选中的图片复制到指定路径(onActivityResult)
	 * 
	 * @param data
	 * @param targetPath
	 * @param mContext
	 * @return
	 */
	public static boolean copyPhotoFromGally(Intent data, String targetPath,
			Context mContext) {
		if (!checkSdMounted(mContext)) { // 检测sd是否可用
			return false;
		}
		Uri uri = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = mContext.getContentResolver().query(uri,
				filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		String fileName = getFilenameFromFullPath(picturePath);
		cursor.close();
		if (copyFile(picturePath, targetPath + fileName)) {
			return true;
		}
		return false;
	}

	public static long dateDiff(Date d1, Date d2) {
		long diff;
		try {
			long n1 = d1.getTime();
			long n2 = d2.getTime();
			diff = Math.abs(n1 - n2);
			diff /= 3600 * 1000 * 24;
		} catch (Exception e) {
			return -1;
		}
		return diff;
	}

	public static long dateDiffWithoutYear(Date d1, Date d2) {
		long diff;
		try {
			d1.setYear(d2.getYear());
			long n1 = d1.getTime();
			long n2 = d2.getTime();
			diff = Math.abs(n1 - n2);
			diff /= 3600 * 1000 * 24;
		} catch (Exception e) {
			return -1;
		}
		return diff;
	}

	/**
	 * 获取全角化后的字符串
	 * 
	 * @param input
	 * @return
	 */
	public static String fullSpaceText(String input) {
		return stringFilter(ToDBC(input));
	}

	/***
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}

	/**
	 * * 去除特殊字符或将所有中文标号替换为英文标号
	 * 
	 * @param str
	 * @return
	 */
	public static String stringFilter(String str) {
		str = str.replaceAll("【", "[").replaceAll("】", "]")
				.replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
		String regEx = "[『』]"; // 清除掉特殊字符
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}

	public static String html2Text(String HTMLStr) {
		String htmlStr = HTMLStr;
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
			String regEx_html = "<[^>]+>";
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll("");
			textStr = htmlStr.replaceAll(" ", "");
			textStr = htmlStr.replaceAll("<", "<");
			textStr = htmlStr.replaceAll(">", ">");
			textStr = htmlStr.replaceAll("®", "®");
			textStr = htmlStr.replaceAll("&", "&");
			textStr = htmlStr.replaceAll("&nbsp;", " ");
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("DEBUG", "CommonUtil : html2Text_error");
		}
		return textStr;
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @param context
	 * @return 0: 未连接 1:移动网络已连接 2:WIFI网络已连接
	 */
	public static int CheckNetworkState(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		State mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
				.getState();
		// 如果3G、wifi、2G等网络状态是连接的，则退出，否则显示提示信息进入网络设置界面
		if (mobile == State.CONNECTED || mobile == State.CONNECTING)
			return 1;
		if (wifi == State.CONNECTED || wifi == State.CONNECTING)
			return 2;
		return 0;
	}

	public static boolean isSdCardWrittenable() {

		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static long getAvailableStorage() {

		String storageDirectory = null;
		storageDirectory = Environment.getExternalStorageDirectory().toString();

		try {
			StatFs stat = new StatFs(storageDirectory);
			long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat
					.getBlockSize());
			return avaliableSize;
		} catch (RuntimeException ex) {
			return 0;
		}
	}

	public static boolean checkAvailableStorage() {

		if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
			return false;
		}

		return true;
	}

	public static boolean isSDCardPresent() {

		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	/**
	 * 创建文件夹
	 * 
	 * @throws IOException
	 */
	public static void mkdir() throws IOException {

		File file = new File(getRootFolder());
		if (!file.exists() || !file.isDirectory())
			file.mkdir();
	}

	public static Bitmap getLoacalBitmap(String url) {

		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param size
	 * @return
	 */
	public static String size(long size) {

		if (size / (1024 * 1024) > 0) {
			float tmpSize = (float) (size) / (float) (1024 * 1024);
			DecimalFormat df = new DecimalFormat("#.##");
			return "" + df.format(tmpSize) + "MB";
		} else if (size / 1024 > 0) {
			return "" + (size / (1024)) + "KB";
		} else
			return "" + size + "B";
	}

	/**
	 * 安装指定路径下的APK
	 * 
	 * @param context
	 * @param url
	 */
	public static void installAPK(Context mContext, final String url) {

		Intent intent = new Intent(Intent.ACTION_VIEW);
		String fileName = getDownLoadPath() + getFileNameFromUrl(url);
		intent.setDataAndType(Uri.fromFile(new File(fileName)),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setClassName("com.android.packageinstaller",
				"com.android.packageinstaller.PackageInstallerActivity");
		mContext.startActivity(intent);
	}

	/**
	 * 是否删除文件
	 * 
	 * @param path
	 *            文件或文件夹路径
	 * @return 是否成功删除
	 */
	public static boolean delete(File path) {

		boolean result = true;
		if (path.exists()) {
			if (path.isDirectory()) {
				for (File child : path.listFiles()) {
					result &= delete(child);
				}
				result &= path.delete(); // Delete empty directory.
			}
			if (path.isFile()) {
				result &= path.delete();
			}
			if (!result) {
				Log.e(null, "Delete failed;");
			}
			return result;
		} else {
			Log.e(null, "File does not exist.");
			return false;
		}
	}

	/**
	 * 当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * 从远程URL中获取下载文件名称
	 * 
	 * @param url
	 * @return
	 */
	public static String getFileNameFromUrl(String url) {
		// 通过 ‘？’ 和 ‘/’ 判断文件名
		int index = url.lastIndexOf('?');
		String filename;
		if (index > 1) {
			filename = url.substring(url.lastIndexOf('/') + 1, index);
		} else {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			filename = UUID.randomUUID() + ".apk";// 默认取一个文件名
		}
		return filename;
	}

	/**
	 * 输出调试信息
	 * 
	 * @param msg
	 */
	public static void printDebugMsg(String msg) {
		Log.i("DEBUG", msg);
	}

	public static void printDebugMsg(int msg) {
		printDebugMsg(String.valueOf(msg));
	}

	// public static String getRandomColor() {
	//
	// Color.argb((new Double(Math.random() * 128)).intValue() + 128,(new
	// Double(Math.random() * 128)).intValue() + 128, (new Double(Math.random()
	// * 128)).intValue() + 128, (new Double(Math.random() * 128)).intValue() +
	// 128);
	//
	// }
	// public static String Color2String(Color color) {
	// String R = Integer.toHexString(color.getRed());
	// R = R.length()<2?('0'+R):R;
	// String B = Integer.toHexString(color.getBlue());
	// B = B.length()<2?('0'+B):B;
	// String G = Integer.toHexString(color.getGreen());
	// G = G.length()<2?('0'+G):G;
	// return '#'+R+B+G;
	// }
}
