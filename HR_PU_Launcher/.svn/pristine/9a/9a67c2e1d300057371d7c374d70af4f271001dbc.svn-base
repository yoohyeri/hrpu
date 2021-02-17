package com.mobilus.hp.launcher;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class ScreenCapture extends Activity {
	private Activity mActivity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mActivity = this;

		String folderName = "/storage/sdcard1/screenshot";

		boolean ret = false;
		File folder = new File(folderName);
		if(folder.isDirectory() == false) {
			ret = folder.mkdir();
		}
		else {
			ret = true;
		}

		if(ret == false) {    
			Toast.makeText(this, "폴더 생성 실패!", Toast.LENGTH_SHORT).show();
		}
		else {
			if(screenshot()){
				AudioManager mgr = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
				mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
				ret = true;

				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(mActivity.getBaseContext(), "화면캡쳐가 완료되었습니다.", Toast.LENGTH_SHORT).show();
//						mActivity.finish();
					}
				}, 500);
			}
			else {
				Toast.makeText(this, "화면캡쳐가 실패되었습니다.", Toast.LENGTH_SHORT).show();
			}
		}

//		if(ret == false) {
//			finish();
//		}
	}

	public static boolean screenshot()
	{
		boolean ret = false;

		Process rmCache = null;
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat sdfNow = new SimpleDateFormat("MM_dd_HH_mm_ss");

		String filename = "/storage/sdcard1/screenshot/"+ sdfNow.format(date);
		filename += ".png";

		try {
			rmCache = Runtime.getRuntime().exec("screencap "+filename);
			ret = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}
}