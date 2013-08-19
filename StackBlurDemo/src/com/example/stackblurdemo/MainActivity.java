package com.example.stackblurdemo;

import java.io.IOException;
import java.io.InputStream;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

import com.enrique.stackblur.StackBlurManager;

public class MainActivity extends RoboActivity {

	@InjectView(R.id.imageView)    ImageView    _imageView;
	@InjectView(R.id.seekBar)      SeekBar      _seekBar  ;
	@InjectView(R.id.toggleButton) ToggleButton _toggleButton;
	
	private StackBlurManager _stackBlurManager;
	
	private String IMAGE_TO_ANALYZE = "android_platform_256.png";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		_stackBlurManager = new StackBlurManager(getBitmapFromAsset(this, "android_platform_256.png"));
		
		_seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				_stackBlurManager.process(progress*5);
				_imageView.setImageBitmap(_stackBlurManager.returnBlurredImage() );
			}
		});
		
		_toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
		    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		        if (isChecked) {
		        	IMAGE_TO_ANALYZE = "image_transparency.png";
		        	_stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
		        	_imageView.setImageDrawable(getResources().getDrawable(R.drawable.image_transparency));
		        } else {
		        	IMAGE_TO_ANALYZE = "android_platform_256.png";
		        	_stackBlurManager = new StackBlurManager(getBitmapFromAsset(getApplicationContext(), IMAGE_TO_ANALYZE));
		        	_imageView.setImageDrawable(getResources().getDrawable(R.drawable.android_platform_256));
		        }
		    }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
    private Bitmap getBitmapFromAsset(Context context, String strName) {
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(strName);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            return null;
        }
        return bitmap;
    }

}
