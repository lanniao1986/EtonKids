package com.enix.hoken.activity;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import com.enix.hoken.R;
import com.enix.hoken.action.ActionHandler;
import com.enix.hoken.util.CommonUtil;

public class PicturePickerActivity extends Activity {

	// data
	Intent intent = null;
	private static final int TAKE_BIG_PICTURE = 1;
	private static final int CROP_BIG_PICTURE = 2;
	private static final int CHOOSE_BIG_PICTURE = 3;
	private String filePath = "file://";
	private String fileName;
	private boolean isFreeMode;
	private boolean isCreateMode = false;
	private Uri imageUri;// to store the big bitmap
	Bitmap bitmap;
	public static final String INTENT_PARAM_FILENAME = "FILENAME ";
	public static final String INTENT_PARAM_FREEMODE = "FREEMODE";
	public static final String INTENT_PARAM_CREATEFILE = "CREATE_FILE";
	// views
	private ImageView imageView;

	private void cropImageUri(Uri uri, int requestCode) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		if (!isFreeMode) {
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("outputX", 320);
			intent.putExtra("outputY", 320);
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", false);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	private Bitmap decodeUriAsBitmap(Uri uri) {
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeStream(getContentResolver()
					.openInputStream(uri));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return bitmap;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_capture);

		imageView = (ImageView) findViewById(R.id.imgpreview);
		intent = getIntent();
		Bundle mBundle = intent.getExtras();
		// 可否自定义图片大小,否则返回固定头像尺寸
		isFreeMode = mBundle.getBoolean(INTENT_PARAM_FREEMODE, true);
		fileName = mBundle.getString(INTENT_PARAM_FILENAME);

		if (fileName.endsWith("nothing")) {
			java.util.Date dt = new java.util.Date(System.currentTimeMillis());
			SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
			fileName = fileName.replace("nothing", fmt.format(dt) + ".jpg");
			isCreateMode = true;
			filePath = filePath + fileName;// 新建文件不加.temp
		} else {
			isCreateMode = false;
			filePath = filePath + fileName + ".temp";
		}

		imageUri = Uri.parse(filePath);
		imageView.setImageDrawable(CommonUtil.getBitmapDrawableByPath(this,
				fileName));
		this.findViewById(R.id.btnFromCamera).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (imageUri == null)

							intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// action
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						startActivityForResult(intent, TAKE_BIG_PICTURE);
					}
				});
		this.findViewById(R.id.btnFromGallary).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						// 截图后显示
						intent = new Intent(Intent.ACTION_GET_CONTENT, null);
						intent.setType("image/*");
						intent.putExtra("crop", "true");
						if (!isFreeMode) {
							intent.putExtra("aspectX", 1);
							intent.putExtra("aspectY", 1);
							intent.putExtra("scale", true);
							intent.putExtra("outputX", 320);
							intent.putExtra("outputY", 320);
						}
						intent.putExtra("return-data", false);
						intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
						intent.putExtra("outputFormat",
								Bitmap.CompressFormat.JPEG.toString());
						intent.putExtra("noFaceDetection", false); // no face
																	// detection
						startActivityForResult(intent, CHOOSE_BIG_PICTURE);

					}
				});
		this.findViewById(R.id.confirm).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						intent = new Intent();

						if (isCreateMode) {// 新建图片模式下 需返回创建的文件完整路径名
							intent.putExtra(INTENT_PARAM_CREATEFILE, fileName);
							setResult(ActionHandler.RESULT_FOR_AVATER_CREATE,
									intent);
						} else {
							if (CommonUtil.copyFile(fileName + ".temp",
									fileName)) {
								CommonUtil.del(fileName + ".temp",
										PicturePickerActivity.this);
								setResult(Activity.RESULT_OK, intent);
							} else {
								setResult(Activity.RESULT_CANCELED, intent);
								CommonUtil.showShortToast(
										PicturePickerActivity.this, "图像设置失败!");
							}
						}
						finish();// 结束之后会将结果传回From
						overridePendingTransition(R.anim.push_up_in,
								R.anim.push_up_out);
					}
				});
		this.findViewById(R.id.cancel).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						intent = new Intent();
						setResult(Activity.RESULT_CANCELED, intent);
						finish();// 结束之后会将结果传回From
						overridePendingTransition(R.anim.push_up_in,
								R.anim.push_up_out);
					}
				});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode != Activity.RESULT_OK) {// result is not correct

			return;
		} else {
			switch (requestCode) {
			case TAKE_BIG_PICTURE:

				cropImageUri(imageUri, CROP_BIG_PICTURE);

				break;
			case CROP_BIG_PICTURE:// from crop_big_picture

				if (imageUri != null) {
					bitmap = decodeUriAsBitmap(imageUri);
					imageView.setImageBitmap(bitmap);
					CommonUtil.saveBitmapToSd(bitmap, fileName + ".temp",
							PicturePickerActivity.this);
				}
				break;
			case CHOOSE_BIG_PICTURE:

				if (imageUri != null) {
					bitmap = decodeUriAsBitmap(imageUri);
					imageView.setImageBitmap(bitmap);
				}
				break;
			default:
				break;
			}
		}
	}

}
