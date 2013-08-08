package com.enix.hoken.custom.item;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;

import com.enix.hoken.R;
import com.enix.hoken.custom.adapter.MemberGridAdapter;
import com.enix.hoken.info.Jinfo;
import com.enix.hoken.info.JinfoList;
import com.enix.hoken.util.CommonUtil;
import com.enix.hoken.util.DateUtil;
import jxl.*;
import jxl.biff.DisplayFormat;
import jxl.format.BoldStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.read.biff.*;
import jxl.read.*;
import jxl.write.*;
import jxl.write.Boolean;
import jxl.write.Number;
import jxl.write.WritableFont.FontName;
import jxl.write.biff.RowsExceededException;

public class ExcelBuilder {
	private Context mContext;
	private String fileName = "CheckTable.xls";
	private String sheetName = "2013年";
	private String title = "伊顿慧智双语幼儿园";
	private String className = "托班1班";
	private JinfoList jinfoList;
	private JinfoList nameList;
	private String[] weeks = { "星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日" };
	private String[] noon = new String[] { "上午", "下午" };
	private String tname = "张妍";

	private int startXSheetTitle = 9;// 标题起始X坐标
	private int startYSheetTitle = 1;// 标题起始Y坐标
	private int startXWeekTitle = 9;// 第几周标题起始X坐标
	private int startYWeekTitle = 3;// 第几周标题起始Y坐标
	private int startXColumnTitle = 1;// 列名起始X坐标
	private int startYColumnTitle = 4;// 列名起始Y坐标
	private int startXNo = 1;// 序号列起始X坐标
	private int startYNo = 6;// 序号列起始Y坐标
	private int startXSname = 2;// 姓名列起始X坐标
	private int startYSname = 6;// 姓名列起始Y坐标
	private int startXMergeTitle = 1;// 姓名序号列头合并起始X坐标
	private int startYMergeTitle = 4;// 姓名序号列头合并起始Y坐标
	private int startXMergeWeeks = 3;// 星期列头合并起始X坐标
	private int startYMergeWeeks = 4;// 星期列头合并起始Y坐标
	private int startXState = 3;// 签到状态起始坐标
	private int startYState = 6;// 签到状态起始坐标
	private int maxRowCount = 0;// 当前绘制到的最大行数;
	private int maxColumCount = 0;// 当前绘制到的最大列数;
	private Calendar mCalendar;
	private Date now;
	private Date firstMonday = DateUtil.getFirstMondayDate();
	private Calendar firstMondayCalendar = DateUtil.getFirstMondayCalendar();
	private SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
	private SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
	private SimpleDateFormat simpleFormat = new SimpleDateFormat("yyyy年MM月");
	private SimpleDateFormat fullFormat = new SimpleDateFormat(
			"yyyy年MM月dd日 HH:mm");
	private String fullPathName;
	private HashMap<String, JinfoList> jinfoMapList;// 所有集合
	private HashMap subJinfoMapList;// 一周内的所有集合 及坐标 KEY: jinfolist
									// (获取数据)KEY:(startxy)(获取坐标)

	private ArrayList<HashMap> sublist;// 一周集合暂存

	public HashMap<String, JinfoList> getJinfoMapList() {
		return jinfoMapList;
	}

	public void setJinfoMapList(HashMap<String, JinfoList> jinfoMapList) {
		this.jinfoMapList = jinfoMapList;
	}

	public ExcelBuilder(Context mContext) {
		this.mContext = mContext;
		mCalendar = Calendar.getInstance();
		now = new Date();
		sheetName = simpleFormat.format(now);
		fileName = title + className + sheetName + "学生签到表.xls";
		fullPathName = CommonUtil.getRootFolder() + fileName;
		initData();
	}

	public ExcelBuilder(Context mContext, String fullPathName) {
		this.mContext = mContext;
		mCalendar = Calendar.getInstance();
		now = new Date();
		sheetName = simpleFormat.format(now);
		this.fullPathName = fullPathName;
		initData();
	}

	public boolean WritingSpreadSheet() {
		try {

			if (!CommonUtil.sdcardMounted()) {
				CommonUtil.showLongToast(mContext, "未检测出内存卡,请检查存储设备!");
				return false;
			}
			Log.i("DEBUG", "WritingSpreadSheet_START");
			// 创建一个WorkBook，即一个Excel文件，文件目录为param0
			WritableWorkbook workbook = Workbook.createWorkbook(new File(
					fullPathName));
			// 创建一个Sheet，即表单，名字为param0;位置为param1
			WritableSheet sheet = workbook.createSheet(sheetName, 0);

			// SHEET TITLE START
			sheet.mergeCells(startXSheetTitle - 4, startYSheetTitle,
					startXSheetTitle + 4, startYSheetTitle);
			CellParam cpSheetTitle = new CellParam(sheet);
			cpSheetTitle.setAligValig(CellParam.CELL_ALIGNMENT_CENTRE,
					CellParam.CELL_VERTICAL_ALIGNMENT_CENTRE);
			cpSheetTitle.setFontSize(16);
			cpSheetTitle.setValue(startXSheetTitle - 4, startYSheetTitle, title
					+ "  " + (mCalendar.get(Calendar.MONTH) + 1) + "月  "
					+ className + "学生签到表");
			cpSheetTitle.setHeight(500);
			cpSheetTitle.setBold(true);
			cpSheetTitle.creatCell();// 设置SHEET 标题行
			// SHEET TITLE END

			// 初始化 JINFOLIST为当天的考勤记录数据
			jinfoList = new JinfoList();

			// 为了不影响姓名列的绘制 复制一个LIST
			nameList = getJinfoListByDate(now);

			int nextWeekYSpan = 0;
			// 循环4次作为一个月4周的数据表格式
			for (int weekCount = 0; weekCount < 4; weekCount++) {
				nextWeekYSpan = (nameList.size() + 6) * weekCount;
				// WEEKNUM TITLE START
				cpSheetTitle.setBold(true);
				cpSheetTitle.setValue(startXWeekTitle, startYWeekTitle
						+ nextWeekYSpan, "第" + (weekCount + 1) + "周");
				cpSheetTitle.creatCell();
				// WEEKNUM TITLE END

				sublist = new ArrayList<HashMap>();
				// 设置常规默认单元格FORMAT
				CellParam cpNomalBlack = new CellParam(sheet);
				cpNomalBlack.setAligValig(CellParam.CELL_ALIGNMENT_CENTRE,
						CellParam.CELL_VERTICAL_ALIGNMENT_CENTRE);
				cpNomalBlack.setFontSize(11);

				// --绘制 序号 姓名 签到行列 start
				sheet.mergeCells(startXMergeTitle, startYMergeTitle
						+ nextWeekYSpan, startXMergeTitle + 1, startYMergeTitle
						+ 1 + nextWeekYSpan);
				cpNomalBlack.setBorder(CellParam.CELL_BORDER_ALL_THIN_BLACK);
				// --绘制序号姓名固定列头
				cpNomalBlack.setValue(startXMergeTitle, startYMergeTitle
						+ nextWeekYSpan, "序号/姓名");
				cpNomalBlack.setBackGround(Colour.GRAY_25);
				cpNomalBlack.setBold(true);
				cpNomalBlack.creatCell();

				// --绘制星期标题 上午下午 start
				for (int i = 0; i < weeks.length; i++) {
					// 星期标题列头合并1格
					sheet.mergeCells(startXMergeWeeks + i * 2, startYMergeWeeks
							+ nextWeekYSpan, startXMergeWeeks + 1 + i * 2,
							startYMergeWeeks + nextWeekYSpan);
					cpNomalBlack.setBackGround(Colour.GRAY_25);
					cpNomalBlack.setBold(true);
					// 绘制星期具体日期
					cpNomalBlack.setValue(
							startXMergeWeeks + i * 2,
							startYMergeWeeks + nextWeekYSpan,
							weeks[i]
									+ " "
									+ DateUtil.getWeekSpan(firstMonday, i + 1,
											weekCount) + "日");
					cpNomalBlack.creatCell();

					// 更新考勤记录表数据为当前绘制的日期下的数据.
					jinfoList = new JinfoList();
					jinfoList = getJinfoListByDate(firstMondayCalendar
							.getTime());
					// 更新绘制签到状态的起始坐标
					if (jinfoList != null && jinfoList.size() > 0) {
						subJinfoMapList = new HashMap<String, JinfoList>();
						subJinfoMapList.put("jinfolist", jinfoList);
						subJinfoMapList.put("startx", startXMergeWeeks + i * 2);
						subJinfoMapList.put("starty", startYMergeWeeks
								+ nextWeekYSpan + 2);
						sublist.add(subJinfoMapList);
					}
					firstMondayCalendar.add(Calendar.DATE, 1);// 日期递增
					for (int j = 0; j < noon.length; j++) {
						cpNomalBlack.setBackGround(Colour.GRAY_25);
						// 绘制上午下午
						cpNomalBlack.setValue(startXMergeWeeks + i * 2 + j,
								startYMergeWeeks + nextWeekYSpan + 1, noon[j]);
						cpNomalBlack.setBold(true);
						cpNomalBlack.creatCell();
					}
				}
				// --绘制星期标题 end
				int aftenoonSpan = 0;
				int weekSpan = 0;
				if (mCalendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
					weekSpan = 12;
				} else {
					weekSpan = (mCalendar.get(Calendar.DAY_OF_WEEK) - 2) * 2;
				}

				for (int m = 0; m < nameList.size(); m++) {
					// 绘制所有空格边框
					for (int j = 0; j < weeks.length * 2; j++) {
						cpNomalBlack.setFontSize(11);
						cpNomalBlack.setValue(startXState + j, startYState
								+ nextWeekYSpan + m, "");
						cpNomalBlack.creatCell();
					}
					cpNomalBlack.setValue(startXNo, startYNo + nextWeekYSpan
							+ m, m + 1);
					cpNomalBlack.creatCell();
					// 绘制姓名
					cpNomalBlack.setValue(startXSname, startYSname
							+ nextWeekYSpan + m, nameList.get(m).getS_name());
					cpNomalBlack.creatCell();
				}

				if (sublist != null && sublist.size() > 0) {

					for (int i = 0; i < sublist.size(); i++) {
						JinfoList subJinfoList = (JinfoList) sublist.get(i)
								.get("jinfolist");
						for (int k = 0; k < subJinfoList.size(); k++) {

							int subStartX = (Integer) sublist.get(i).get(
									"startx");
							int subStartY = (Integer) sublist.get(i).get(
									"starty");
							// 如果是下午时间 则写入到下午一栏
							if (DateUtil.isMorning(subJinfoList.get(k)
									.getJ_checktime())) {
								aftenoonSpan = 1;
							} else {
								aftenoonSpan = 0;
							}
							for (int j = 0; j < noon.length; j++) {
								cpNomalBlack.setValue(subStartX, subStartY + k,
										"--");
								cpNomalBlack.creatCell();
							}

							// 签到状态颜色
							String mState = null;
							switch (subJinfoList.get(k).getJ_state()) {
							case MemberGridAdapter.MEMBER_STATE_DEFALUT:
								cpNomalBlack
										.setFontColor(jxl.format.Colour.RED);
								mState = "缺勤";
								break;
							case MemberGridAdapter.MEMBER_STATE_DAYOFF:
								cpNomalBlack
										.setFontColor(jxl.format.Colour.GREEN);
								mState = "请假";
								break;
							case MemberGridAdapter.MEMBER_STATE_ARRIVED:
								mState = "签到";
								cpNomalBlack
										.setFontColor(jxl.format.Colour.BLACK);
								break;
							}
							cpNomalBlack.setValue(subStartX + aftenoonSpan,
									subStartY + k, mState);
							cpNomalBlack.creatCell();
							Log.i("DEBUG", "CELL X="
									+ (subStartX + aftenoonSpan) + " Y= "
									+ (subStartY + k));
						}
					}

				}
				// --绘制 序号 姓名 签到 行列 end

			}

			// -- FOOTER start
			maxRowCount = sheet.getRows();
			CellParam cpFooter = new CellParam(sheet);
			cpFooter.setFontSize(12);
			cpFooter.setValue(startXMergeTitle, maxRowCount + 2, "更新时间:"
					+ fullFormat.format(now));
			cpFooter.creatCell();
			cpFooter.setValue(startXMergeTitle, maxRowCount + 4, "担当老师:"
					+ tname);
			cpFooter.creatCell();
			cpFooter.setFontColor(Colour.RED);
			cpFooter.setValue(startXMergeTitle, maxRowCount + 6,
					"★ 本文档由安卓程序 我的伊顿 自动生成");
			cpFooter.creatCell();
			// -- FOOTER end
			workbook.write();
			workbook.close();
			Log.i("DEBUG", "WritingSpreadSheet_SUCESS");
			return true;
		} catch (IOException e) {
			Log.e("DEBUG", "WritingSpreadSheet_FAILED");
			e.printStackTrace();
			return false;
		} catch (RowsExceededException e) {
			Log.e("DEBUG", "WritingSpreadSheet_FAILED");
			e.printStackTrace();
			return false;
		} catch (WriteException e) {
			Log.e("DEBUG", "WritingSpreadSheet_FAILED");
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 根据日期获取对应的考勤表
	 * 
	 * @param date
	 * @return
	 */
	private JinfoList getJinfoListByDate(Date date) {
		String yyyymmdd = format2.format(date);
		return jinfoMapList.get(yyyymmdd);
	}

	/**
	 * 初始化制表数据
	 */
	private void initData() {

	}

	public class CellParam {
		HashMap<Integer, Integer> mCellParamInt;
		HashMap<Integer, String> mCellParamString;
		WritableFont wf;
		WritableCellFormat wcf;
		WritableSheet sheet;
		FontName fontName = WritableFont.ARIAL;
		int fontSize = 10;
		Colour fontColor = Colour.BLACK;
		Colour backGround;
		Colour borderLineColour = Colour.BLACK;
		int startX = 0;
		int startY = 0;
		int width = 0;
		int height = 0;
		int border = 0;
		int alig = 0;
		int valig = 0;
		boolean Italic = false;
		boolean isBlank = false;
		boolean isBold = false;
		int valueType = 0;

		public static final int CELL_ALIGNMENT_CENTRE = 1;
		public static final int CELL_ALIGNMENT_FILL = 2;
		public static final int CELL_ALIGNMENT_GENERAL = 3;
		public static final int CELL_ALIGNMENT_JUSTIFY = 4;
		public static final int CELL_ALIGNMENT_LEFT = 5;
		public static final int CELL_ALIGNMENT_RIGHT = 6;
		public static final int CELL_VERTICAL_ALIGNMENT_CENTRE = 7;
		public static final int CELL_VERTICAL_ALIGNMENT_BOTTOM = 8;
		public static final int CELL_VERTICAL_ALIGNMENT_TOP = 9;
		public static final int CELL_VERTICAL_ALIGNMENT_JUSTIFY = 10;
		public static final int CELL_BORDER_ALL_THIN_BLACK = 11;
		public static final int CELL_BORDER_ALL_MEDIUM_BLACK = 12;
		public static final int CELL_VALUE = 13;
		public static final int CELL_VALUE_TYPE_INT = 14;
		public static final int CELL_VALUE_TYPE_STRING = 15;

		public boolean isBlank() {
			return isBlank;
		}

		public void setBlank(boolean isBlank) {
			this.isBlank = isBlank;
		}

		public FontName getFontName() {
			return fontName;
		}

		public void setFontName(FontName fontName) {
			this.fontName = fontName;
		}

		public int getFontSize() {
			return fontSize;
		}

		public void setFontSize(int fontSize) {
			this.fontSize = fontSize;
		}

		public Colour getFontColor() {
			return fontColor;
		}

		public void setFontColor(Colour fontColor) {
			this.fontColor = fontColor;
		}

		public Colour getBackGround() {
			return backGround;
		}

		public void setBackGround(Colour backGround) {
			this.backGround = backGround;
		}

		public int getStartX() {
			return startX;
		}

		public void setStartX(int startX) {
			this.startX = startX;
		}

		public void setStartXY(int startX, int startY) {
			this.startX = startX;
			this.startY = startY;
		}

		public int getStartY() {
			return startY;
		}

		public void setStartY(int startY) {
			this.startY = startY;
		}

		public int getWidth() {
			return width;
		}

		public void setWidth(int width) {
			this.width = width;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public boolean isItalic() {
			return Italic;
		}

		public void setItalic(boolean italic) {
			Italic = italic;
		}

		public int getBorder() {
			return border;
		}

		public void setBorder(int border) {
			this.border = border;
		}

		public int getAlig() {
			return alig;
		}

		public void setAlig(int alig) {
			this.alig = alig;
		}

		public void setAligValig(int alig, int valig) {
			this.alig = alig;
			this.valig = valig;
		}

		public int getValig() {
			return valig;
		}

		public void setValig(int valig) {
			this.valig = valig;
		}

		public CellParam(WritableSheet sheet) {
			mCellParamString = new HashMap<Integer, String>();
			mCellParamInt = new HashMap<Integer, Integer>();
			this.sheet = sheet;
		}

		public void setParam(int paramType, String param) {
			mCellParamString.put(paramType, param);
		}

		public void setValue(int startX, int startY, String value) {
			setStartXY(startX, startY);
			mCellParamString.put(CELL_VALUE, value);
			valueType = CELL_VALUE_TYPE_STRING;
		}

		public void setValue(int startX, int startY, int value) {
			setStartXY(startX, startY);
			mCellParamInt.put(CELL_VALUE, value);
			valueType = CELL_VALUE_TYPE_INT;
		}

		public void setValue(String value) {
			mCellParamString.put(CELL_VALUE, value);
			valueType = CELL_VALUE_TYPE_STRING;
		}

		public void setValue(int value) {
			mCellParamInt.put(CELL_VALUE, value);
			valueType = CELL_VALUE_TYPE_INT;
		}

		public void setParam(int paramType, int param) {
			mCellParamInt.put(paramType, param);
		}

		public boolean isBold() {
			return isBold;
		}

		public void setBold(boolean isBold) {
			this.isBold = isBold;
		}

		/**
		 * 按指定的FORMAT创建CELL对象
		 * 
		 * @return
		 */
		public boolean creatCell(WritableCellFormat wcf) {
			if (wcf != null) {
				WritableCell cell = null;
				if (!isBlank) {
					switch (valueType) {
					case CELL_VALUE_TYPE_STRING:
						cell = new Label(startX, startY,
								mCellParamString.get(CELL_VALUE), wcf);
						break;
					case CELL_VALUE_TYPE_INT:
						cell = new Number(startX, startY,
								mCellParamInt.get(CELL_VALUE), wcf);
						break;
					}

				} else {
					cell = new Blank(startX, startY);
				}
				if (sheet != null && cell != null) {
					try {
						if (width > 0) {
							sheet.setColumnView(startX, width); // 设置列的宽度
						}
						if (height > 0) {
							sheet.setRowView(startY, height); // 设置行的高度
						}
						sheet.addCell(cell);
						resetDefault();
					} catch (WriteException e) {
						e.printStackTrace();
						return false;
					}
					return true;
				}
			} else {
				return creatCell();
			}
			return false;
		}

		/**
		 * 直接按设定的FORMAT创建CELL对象
		 * 
		 * @return
		 */
		public boolean creatCell() {
			wf = new WritableFont(fontName, fontSize);
			try {
				wf.setItalic(Italic);
				if (isBold)
					wf.setBoldStyle(WritableFont.BOLD);
				wf.setColour(fontColor);
				wcf = new WritableCellFormat(wf);
				if (alig != 0)
					switch (alig) {
					case CELL_ALIGNMENT_CENTRE:
						wcf.setAlignment(jxl.format.Alignment.CENTRE);
						break;
					case CELL_ALIGNMENT_FILL:
						wcf.setAlignment(jxl.format.Alignment.FILL);
						break;
					case CELL_ALIGNMENT_GENERAL:
						wcf.setAlignment(jxl.format.Alignment.GENERAL);
						break;
					case CELL_ALIGNMENT_JUSTIFY:
						wcf.setAlignment(jxl.format.Alignment.JUSTIFY);
						break;
					case CELL_ALIGNMENT_LEFT:
						wcf.setAlignment(jxl.format.Alignment.LEFT);
						break;
					case CELL_ALIGNMENT_RIGHT:
						wcf.setAlignment(jxl.format.Alignment.RIGHT);
						break;
					}
				if (valig != 0) {
					switch (valig) {
					case CELL_VERTICAL_ALIGNMENT_JUSTIFY:
						wcf.setVerticalAlignment(jxl.format.VerticalAlignment.JUSTIFY);
						break;
					case CELL_VERTICAL_ALIGNMENT_BOTTOM:
						wcf.setVerticalAlignment(jxl.format.VerticalAlignment.BOTTOM);
						break;
					case CELL_VERTICAL_ALIGNMENT_CENTRE:
						wcf.setVerticalAlignment(jxl.format.VerticalAlignment.CENTRE);
						break;
					case CELL_VERTICAL_ALIGNMENT_TOP:
						wcf.setVerticalAlignment(jxl.format.VerticalAlignment.TOP);
						break;
					}
				}
				if (backGround != null)
					wcf.setBackground(backGround);
				if (border != 0) {
					switch (border) {
					case CELL_BORDER_ALL_THIN_BLACK:
						wcf.setBorder(jxl.format.Border.ALL,
								jxl.format.BorderLineStyle.THIN, Colour.BLACK);
						break;

					case CELL_BORDER_ALL_MEDIUM_BLACK:
						wcf.setBorder(jxl.format.Border.ALL,
								jxl.format.BorderLineStyle.MEDIUM, Colour.BLACK);
						break;
					}
				}
				WritableCell cell = null;
				if (!isBlank && valueType != 0) {
					switch (valueType) {
					case CELL_VALUE_TYPE_STRING:
						cell = new Label(startX, startY,
								mCellParamString.get(CELL_VALUE), wcf);
						break;
					case CELL_VALUE_TYPE_INT:
						cell = new Number(startX, startY,
								mCellParamInt.get(CELL_VALUE), wcf);
						break;
					}
				} else {
					cell = new Blank(startX, startY);
				}
				if (sheet != null && cell != null) {
					if (width > 0) {
						sheet.setColumnView(startX, width); // 设置列的宽度
					}
					if (height > 0) {
						sheet.setRowView(startY, height); // 设置行的高度
					}
					sheet.addCell(cell);
					resetDefault();

					return true;
				}
			} catch (WriteException e) {
				e.printStackTrace();
				return false;
			}
			return false;
		}

		/**
		 * 重置部分属性值
		 */
		private void resetDefault() {
			fontColor = Colour.BLACK;
			backGround = null;
			borderLineColour = Colour.BLACK;
			Italic = false;
			isBlank = false;
			isBold = false;
			valueType = 0;
		}

		/**
		 * 重置所有属性值
		 */
		public void resetDefaultAll() {
			fontName = WritableFont.ARIAL;
			fontSize = 10;
			fontColor = Colour.BLACK;
			backGround = null;
			borderLineColour = Colour.BLACK;
			startX = 0;
			startY = 0;
			width = 0;
			height = 0;
			border = 0;
			alig = 0;
			valig = 0;
			Italic = false;
			isBlank = false;
			valueType = 0;
			isBold = false;
		}
	}
}
