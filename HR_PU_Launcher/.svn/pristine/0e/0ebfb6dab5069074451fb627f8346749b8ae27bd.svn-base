package com.mobilus.hp.screensaver;

import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.R;
import com.telechips.android.tdmb.DxbView.STATE;
import com.telechips.android.tdmb.LauncherMainActivity;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.screensaver.AnalogClockFragment;
import com.mobilus.hp.screensaver.AnalogClockFragment.onAnalogEventListener;
import com.mobilus.hp.screensaver.DigitalClockFragment;
import com.mobilus.hp.screensaver.DigitalClockFragment.onDigitalEventListener;
import com.mobilus.hp.screensaver.NoneFragment;
import com.mobilus.hp.screensaver.NoneFragment.onNoneEventListener;

public class ScreenSaverActivity extends Activity {
	private static final String CLASS_NAME = "[ScreenSaverActivity ]  ";

	private Fragment mFragment;
	public static Context mContext = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		mContext = ScreenSaverActivity.this;
		overridePendingTransition(0, 0);
		setContentView(R.layout.screen_saver_main);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		overridePendingTransition(0, 0);

		HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SCREEN_SAVER;
		FragmentTransaction transaction = getFragmentManager().beginTransaction();
		switch (HP_Manager.mScreenSaver) {
		case HP_Index.SCREEN_SAVER_DIGITAL:
			DigitalClockFragment digital = new DigitalClockFragment(this, mEventDigitalListener);
			transaction.replace(R.id.containerBody, digital);
			transaction.commit();
			break;
		case HP_Index.SCREEN_SAVER_ANALOG:
			AnalogClockFragment analog = new AnalogClockFragment(this, mEventAnalogListener);
			transaction.replace(R.id.containerBody, analog);
			transaction.commit();
			break;
		case HP_Index.SCREEN_SAVER_NONE:
			
			NoneFragment none = new NoneFragment(this, mEventNoneListener);
			transaction.replace(R.id.containerBody, none);
			transaction.commit();
			break;
		default:
			break;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	public static Context getInstance() {
		return mContext;
	}

	private onAnalogEventListener mEventAnalogListener = new onAnalogEventListener() {

		@Override
		public void onExitScreenSaver() {
			mExit();
		}
	};

	private onDigitalEventListener mEventDigitalListener = new onDigitalEventListener() {

		@Override
		public void onExitScreenSaver() {
			mExit();
		}
	};

	private onNoneEventListener mEventNoneListener = new onNoneEventListener() {

		@Override
		public void onExitScreenSaver() {
			mExit();
		}
	};

	public void mExit() {
//		if (HP_Manager.mLCDOnOff == HP_Index.LCD_OFF)
//			return;
		
		if(DxbView.eState == STATE.WIDGET)
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
		else
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_DMB;
		
//		Log.i(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "mExit / eState : "+ DxbView.eState + ", HP_Manager.mCurrentView : " + HP_Manager.mCurrentView
//				+ ", HP_Manager.mViewMode : " + HP_Manager.mViewMode);
		
		HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;
		finish();
	}
}