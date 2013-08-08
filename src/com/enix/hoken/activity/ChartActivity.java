package com.enix.hoken.activity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Vector;
import com.enix.hoken.R;
import com.enix.hoken.common.BaseApplication;
import com.enix.hoken.custom.chart.*;
import com.enix.hoken.custom.chart.param.*;
import com.enix.hoken.info.*;
import com.enix.hoken.util.CommonUtil;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class ChartActivity extends Activity {

	private WebView web;
	private WebSettings webSettings;
	private Vector<Item> chartData = new Vector<Item>();
	private Chart chart;
	private Border border;
	private String data;
	private String data_labels;
	private BaseApplication mApplication;
	private SinfoList sinfoList;
	private RinfoList rinfoList;
	private String mainTitle = "伊顿慧智幼儿园 托班1班 ";
	private LinfoList linfoList;
	public static int CHART_MODE_3DCOLUMN = 1;
	public static int CHART_MODE_3DPIE = 2;
	public static int CHART_MODE_2DAREA = 3;
	private int currentLid;
	private int currentMode;
	public static String INTENT_PARAM_MODE = "MODE";
	public static String INTENT_PARAM_LID = "LID";
	private Pie3D pie3D;
	public String[] colors = new String[] { "#CAFF70", "#EE799F", "#9400D3",
			"#97FFFF", "#FFF68F", "#FFF5EE", "#FFEC8B", "#FFD700", "#FFC0CB",
			"#FF7F00", "#E8E8E8", "#CD8500", "#BFBFBF", "#8E8E8E" };
	private int colorIndex = 0;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.lay_chart);
		mApplication = (BaseApplication) getApplication();
		Intent intent = getIntent();
		currentLid = intent.getExtras().getInt(INTENT_PARAM_LID);
		currentMode = intent.getExtras().getInt(INTENT_PARAM_MODE);
		sinfoList = mApplication.getSelectedSinfoList();
		if (sinfoList == null || sinfoList.size() == 0) {
			sinfoList = mApplication.getSinfoList();
		}
		rinfoList = mApplication.getRinfoList();
		linfoList = mApplication.getLinfoList();

		initRinfoChart();
		web = (WebView) findViewById(R.id.webView);
		webSettings = web.getSettings();
		webSettings.setJavaScriptEnabled(true); // 设定该WebView可以执行JavaScript程序
		webSettings.setBuiltInZoomControls(true); // 设定该WebView可以缩放
		web.addJavascriptInterface(this, "ChartActivity");
		int width = sinfoList.size() * 60 < 400 ? 400 : sinfoList.size() * 60;

		for (Linfo mLinfo : linfoList) {
			if (mLinfo.getId() == currentLid) {
				mainTitle = mainTitle + mLinfo.getL_name() + " 成绩表";
			}
		}

		chart = new Chart(width, 400, mainTitle, "");
		border = new Border(true);
		border.setColor("#AAAAAA");
		border.setWidth(6);
		border.setEnable(true);
		chart.setOffsety(15);
		chart.setAnimation(true);
		chart.setShadow(true);
		chart.setSubtitle("2013年7月 任课老师: 张妍");
		chart.setFootnote("★本图表由安卓程序 我的伊顿 自动生成");

		Legend legend = new Legend(true, "left", "top"); // 实例化图表的图例
		legend.setRow(1); // 设定图例显示在一行中

		// web.loadUrl("file:///android_asset/Area2D.html");

		data = PackageChartData.PackageData(chartData);
		// data = PackageChartData.PackageDoubleData(chartData);
		// data_labels = PackageChartData.PackageDataLabels(new String[] { "一月",
		// "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月",
		// "十二月" });

		// column3D = new Column3D("TEXT", data);

		// web.addJavascriptInterface(column3D, "column3D");

		// //使的在Pie3D.html中内嵌的JS中能够通过标签'pie3D'调用Pie3D中的方法获取3D饼状图所需的数据
		web.addJavascriptInterface(chart, "chart");
		web.addJavascriptInterface(border, "border");
		web.addJavascriptInterface(legend, "legend");
		if (currentMode == CHART_MODE_3DPIE) {
			pie3D = new Pie3D(800, 600, mainTitle, data);
			// 实例化一个3D饼状图对象
			pie3D.setRadius(750); // 设定饼状图的半径
			pie3D.setyHeight(40); // 设定饼状图中饼的厚度
			web.addJavascriptInterface(pie3D, "pie3D");
			web.loadUrl("file:///android_asset/Pie3D.html");
		} else {
			web.loadUrl("file:///android_asset/Column3D.html");
		}

		Log.i("test", "data:" + data);
		// Log.i("test", "data_labels:"+data_labels);
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_chart, menu);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.export_chart: // 导出图表

			try {
				FileOutputStream out = new FileOutputStream(
						CommonUtil.getRootFolder() + mainTitle + "_图表.png");
				captureWebView(web)
						.compress(Bitmap.CompressFormat.PNG, 90, out);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			CommonUtil.showLongToast(this, "成功导出为" + CommonUtil.getRootFolder()
					+ mainTitle + "_图表.png");
			Intent intent = new Intent("android.intent.action.VIEW");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			Uri uri = Uri.fromFile(new File(CommonUtil.getRootFolder()
					+ mainTitle + "_图表.png"));
			intent.setDataAndType(uri, "image/*");
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);

	}

	public void initRinfoChart() {
		for (Sinfo sinfo : sinfoList) {
			for (Rinfo rinfo : rinfoList) {
				if (sinfo.getId() == rinfo.getS_id()
						&& rinfo.getL_id() == currentLid) {

					Item item = new Item();
					item.setName(sinfo.getS_name());
					item.setValue(rinfo.getR_score());
					item.setColor(getColor());
					chartData.add(item);
				}
			}
		}

	}

	/**
	 * 截取webView快照(webView加载的整个内容的大小)
	 * 
	 * @param webView
	 * @return
	 */
	private Bitmap captureWebView(WebView webView) {
		Picture snapShot = webView.capturePicture();
		Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(),
				snapShot.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bmp);
		snapShot.draw(canvas);
		return bmp;
	}

	/**
	 * 初始化2D面积图需要的内容，存放在chartData中,多值数据类型的
	 */
	public void initArea2DContacts() {
		Item item = new Item();
		item.setName("上海");
		item.setValues(new double[] { 4, 16, 18, 24, 32, 36, 38, 38, 36, 26,
				20, 14 });
		item.setColor("#dad81f");
		chartData.add(item);

		item = new Item();
		item.setName("北京");
		item.setValues(new double[] { 8, 12, 14, 20, 26, 28, 30, 26, 28, 20,
				16, 10 });
		item.setColor("#1f7e92");
		chartData.add(item);
	}

	/**
	 * 该方法将在js脚本中，通过window.mainActivity.getContact()进行调用,得到图表显示所需的JSON格式的data
	 * 
	 * @return
	 */
	@JavascriptInterface
	public String getContact() {
		Log.i("test", "get data:" + data);
		return data;
	}

	/**
	 * 该方法将在js脚本中，通过window.mainActivity.getContactLabels()进行调用,
	 * 得到图表显示所需的JSON格式的data_labels
	 * 
	 * @return
	 */
	@JavascriptInterface
	public String getContactLabels() {
		Log.i("test", "get data_labels:" + data_labels);
		return data_labels;
	}

	/**
	 * 用于调试的方法，该方法将在js脚本中，通过window.mainActivity.debugOut(“”)进行调用
	 * 
	 * @param out
	 */
	@JavascriptInterface
	public void debugOut(String out) {
		// TODO Auto-generated method stub
		Log.i("test", "debugOut:" + out);
	}

	@JavascriptInterface
	private String getColor() {
		String color;
		try {
			color = colors[colorIndex];
		} catch (Exception e) {
			colorIndex = 0;
			return colors[colorIndex];
		}
		colorIndex++;
		return color;
	}
}
