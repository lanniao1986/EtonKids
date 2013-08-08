package com.enix.hoken.activity;

import com.enix.hoken.R;
import com.enix.hoken.custom.item.ColorPickerView;
import com.enix.hoken.custom.item.ColorPickerView.OnColorChangedListener;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class ColorPickerActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.color_picker);
		ColorPickerView cpv = (ColorPickerView) findViewById(R.id.color_picker);
		final TextView tv = (TextView) findViewById(R.id.textView);
		cpv.setOnColorChangedListenner(new OnColorChangedListener() {

			@Override
			public void onColorChanged(int color, int originalColor,
					float saturation) {
				// TODO Auto-generated method stub

				tv.setTextSize(24);
				tv.setBackgroundColor(color);
				Log.i("COLOR", "COLOR=" + color);
			}
		});

	}
}