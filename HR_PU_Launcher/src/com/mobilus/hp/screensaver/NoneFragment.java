package com.mobilus.hp.screensaver;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

public class NoneFragment extends Fragment implements OnTouchListener{
	private static final String CLASS_NAME = "[NoneFragment ]  ";
	
	private onNoneEventListener mCallback;
	
	public NoneFragment(Context context, onNoneEventListener mEventListener) {
		mCallback = mEventListener;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.frag_screen_saver_none, null);
		rootView.setOnTouchListener(this);
		return rootView;
	}

	@Override
	public void onPause() {
		try {
			LauncherMainActivity.M_MTX.setScreenOnOff(true);
//			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onPause - " +  LauncherMainActivity.M_MTX.getScreenOnOff());
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		super.onPause();
	}

	@Override
	public void onResume() {
		try {
			LauncherMainActivity.M_MTX.setScreenOnOff(false);
//			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume - " +  LauncherMainActivity.M_MTX.getScreenOnOff());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onResume();
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
//		HP_Manager.mLCDOnOff = HP_Index.LCD_ON;
		HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;	
		
		if(mCallback != null)
			mCallback.onExitScreenSaver();
		return false;
	}
	
	public interface onNoneEventListener{
   		public void onExitScreenSaver();
   	}
}