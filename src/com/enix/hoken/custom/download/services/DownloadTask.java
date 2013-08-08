package com.enix.hoken.custom.download.services;

import com.enix.hoken.custom.download.error.FileAlreadyExistException;
import com.enix.hoken.custom.download.error.NoMemoryException;
import com.enix.hoken.custom.download.http.AndroidHttpClient;
import com.enix.hoken.util.CommonUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

public class DownloadTask extends AsyncTask<Void, Integer, Long> {

	public final static int TIME_OUT = 30000;
	private final static int BUFFER_SIZE = 1024 * 8;

	private static final String TAG = "DownloadTask";
	private static final boolean DEBUG = true;
	private static final String TEMP_SUFFIX = ".download";

	private URL URL;
	private File file;
	private File tempFile;
	private String url;
	private RandomAccessFile outputStream;
	private DownloadTaskListener listener;
	private Context context;

	private long downloadSize;
	private long previousFileSize;
	private long totalSize;
	private long downloadPercent;
	private long networkSpeed;
	private long previousTime;
	private long totalTime;
	private Throwable error = null;
	private boolean interrupt = false;// 是否暂停

	private final class ProgressReportingRandomAccessFile extends
			RandomAccessFile {
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {

			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count)
				throws IOException {

			super.write(buffer, offset, count);
			progress += count;
			publishProgress(progress);
		}
	}

	public DownloadTask(Context context, String url, String path)
			throws MalformedURLException {

		this(context, url, path, null);
	}

	public DownloadTask(Context context, String url, String path,
			DownloadTaskListener listener) throws MalformedURLException {

		super();
		this.url = url;
		this.URL = new URL(url);
		this.listener = listener;
		String fileName = new File(URL.getFile()).getName();
		this.file = new File(path, fileName);
		this.tempFile = new File(path, fileName + TEMP_SUFFIX);
		this.context = context;
	}

	/**
	 * 获取下载对象URL字符串
	 * 
	 * @return
	 */
	public String getUrl() {

		return url;
	}

	/**
	 * 是否中断下载状态
	 * 
	 * @return
	 */
	public boolean isInterrupt() {

		return interrupt;
	}

	/**
	 * 获取下载百分比
	 * 
	 * @return
	 */
	public long getDownloadPercent() {

		return downloadPercent;
	}

	/**
	 * 获取当前已下载的文件大小
	 * 
	 * @return
	 */
	public long getDownloadSize() {

		return downloadSize + previousFileSize;
	}

	/**
	 * 获取下载对象总文件大小
	 * 
	 * @return
	 */
	public long getTotalSize() {

		return totalSize;
	}

	/**
	 * 获取当前下载速度
	 * 
	 * @return
	 */
	public long getDownloadSpeed() {

		return this.networkSpeed;
	}

	/**
	 * 获取下载总计时间
	 * 
	 * @return
	 */
	public long getTotalTime() {

		return this.totalTime;
	}

	public DownloadTaskListener getListener() {

		return this.listener;
	}

	@Override
	protected void onPreExecute() {

		previousTime = System.currentTimeMillis();
		if (listener != null)
			listener.preDownload(this);
	}

	@Override
	protected Long doInBackground(Void... params) {

		long result = -1;
		try {
			result = download();
		} catch (NetworkErrorException e) {
			error = e;
		} catch (FileAlreadyExistException e) {
			this.onCancelled();
			listener.restartDownload(this);
		} catch (NoMemoryException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		} finally {
			if (client != null) {
				client.close();
			}
		}

		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {

		if (progress.length > 1) {
			totalSize = progress[1];
			if (totalSize == -1) {
				if (listener != null)
					listener.errorDownload(this, error);
			} else {

			}
		} else {
			totalTime = System.currentTimeMillis() - previousTime;
			downloadSize = progress[0];
			downloadPercent = (downloadSize + previousFileSize) * 100
					/ totalSize;
			networkSpeed = downloadSize / totalTime;
			if (listener != null)
				listener.updateProcess(this);
		}
	}

	@Override
	protected void onPostExecute(Long result) {

		if (result == -1 || interrupt || error != null) {
			if (DEBUG && error != null) {
				Log.v(TAG, "Download failed." + error.getMessage());
			}
			if (listener != null) {
				listener.errorDownload(this, error);
			}
			return;
		}
		// finish download
		tempFile.renameTo(file);
		if (listener != null)
			listener.finishDownload(this);
	}

	@Override
	public void onCancelled() {

		super.onCancelled();
		interrupt = true;
	}

	private AndroidHttpClient client;
	private HttpGet httpGet;
	private HttpResponse response;

	private long download() throws NetworkErrorException, IOException,
			FileAlreadyExistException, NoMemoryException {

		if (DEBUG) {
			Log.v(TAG, "totalSize: " + totalSize);
		}

		/*
		 * check net work
		 */
		if (!CommonUtil.isNetworkAvailable(context)) {
			throw new NetworkErrorException("Network blocked.");
		}

		/*
		 * check file length
		 */
		client = AndroidHttpClient.newInstance("DownloadTask");
		httpGet = new HttpGet(url);
		response = client.execute(httpGet);
		totalSize = response.getEntity().getContentLength();

		if (file.exists() && totalSize == file.length()) {
			if (DEBUG) {
				Log.v(null, "Output file already exists. Skipping download.");
			}
			throw new FileAlreadyExistException("需要下载的文件已经存在!");
		} else if (tempFile.exists()) {
			httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
			previousFileSize = tempFile.length();

			client.close();
			client = AndroidHttpClient.newInstance("DownloadTask");
			response = client.execute(httpGet);

			if (DEBUG) {
				Log.v(TAG, "File is not complete, download now.");
				Log.v(TAG, "File length:" + tempFile.length() + " totalSize:"
						+ totalSize);
			}
		}

		/*
		 * check memory
		 */
		long storage = CommonUtil.getAvailableStorage();
		if (DEBUG) {
			Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
		}

		if (totalSize - tempFile.length() > storage) {
			throw new NoMemoryException("SD card no memory.");
		}

		/*
		 * start download
		 */
		outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");

		publishProgress(0, (int) totalSize);

		InputStream input = response.getEntity().getContent();
		int bytesCopied = copy(input, outputStream);

		if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1
				&& !interrupt) {
			throw new IOException("下载未能正常完成,当前现在量为: " + bytesCopied + " != "
					+ totalSize);
		}

		if (DEBUG) {
			Log.v(TAG, "Download completed successfully.");
		}

		return bytesCopied;

	}

	public int copy(InputStream input, RandomAccessFile out)
			throws IOException, NetworkErrorException {

		if (input == null || out == null) {
			return -1;
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		if (DEBUG) {
			Log.v(TAG, "length" + out.length());
		}

		int count = 0, n = 0;
		long errorBlockTimePreviousTime = -1, expireTime = 0;

		try {

			out.seek(out.length());

			while (!interrupt) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;

				/*
				 * check network
				 */
				if (!CommonUtil.isNetworkAvailable(context)) {
					throw new NetworkErrorException("网络不可用,请检查网络设置!.");
				}

				if (networkSpeed == 0) {
					if (errorBlockTimePreviousTime > 0) {
						expireTime = System.currentTimeMillis()
								- errorBlockTimePreviousTime;
						if (expireTime > TIME_OUT) {
							throw new ConnectTimeoutException(
									"connection time out.");
						}
					} else {
						errorBlockTimePreviousTime = System.currentTimeMillis();
					}
				} else {
					expireTime = 0;
					errorBlockTimePreviousTime = -1;
				}
			}
		} finally {
			client.close(); // must close client first
			client = null;
			out.close();
			in.close();
			input.close();
		}
		return count;

	}

}
