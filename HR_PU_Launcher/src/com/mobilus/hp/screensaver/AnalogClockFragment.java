package com.mobilus.hp.screensaver;

import android.os.Bundle;
import java.util.Calendar;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.View.OnTouchListener;

public class AnalogClockFragment extends Fragment implements OnTouchListener{
	
	private static final String CLASS_NAME = "[AnalogClockFragment ]  ";

	private onAnalogEventListener mCallback;
	
	/* Analog Clock Date */
	private static TextView mDate;
	private Context mContext;
	
	public AnalogClockFragment(Context context, onAnalogEventListener eventlistener) {
		mCallback = eventlistener;
		mContext = context;
		
		IntentFilter timeFilter = new IntentFilter();
		timeFilter.addAction(Intent.ACTION_TIME_TICK);
		timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
		timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

		context.registerReceiver(mIntentReceiver, timeFilter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		View rootView;
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			rootView = inflater.inflate(R.layout.frag_screen_saver_analog, null);
		else
			rootView = inflater.inflate(R.layout._kia_frag_screen_saver_analog, null);
		
		rootView.setOnTouchListener(this);
		
		mDate = (TextView)rootView.findViewById(R.id.full_analog_date);
		String date = HP_Manager.getCurrentYear() + HP_Manager.mContext.getResources().getString(R.string.year) + " " 
				+ HP_Manager.getStatusbarCurrentMonth() + HP_Manager.mContext.getResources().getString(R.string.month) + " " 
				+ HP_Manager.getStatusbarCurrentDay() + HP_Manager.mContext.getResources().getString(R.string.day)+ " " 
				+ HP_Manager.getCurrentDayOfWeek();
		
//		String date = HP_Manager.getCurrentYear() + "년 " 
//					+ HP_Manager.getStatusbarCurrentMonth() + "월 "
//					+ HP_Manager.getStatusbarCurrentDay() + "일 "
//					+ HP_Manager.getCurrentDayOfWeek();
		mDate.setText(date);
		return rootView;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				HP_Manager.mSystemDate = Calendar.getInstance();
				
				String date = HP_Manager.getCurrentYear() + HP_Manager.mContext.getResources().getString(R.string.year) + " " 
						+ HP_Manager.getStatusbarCurrentMonth() + HP_Manager.mContext.getResources().getString(R.string.month) + " " 
						+ HP_Manager.getStatusbarCurrentDay() + HP_Manager.mContext.getResources().getString(R.string.day)+ " " 
						+ HP_Manager.getCurrentDayOfWeek();
				
//				String date = HP_Manager.getCurrentYear() + "년 " 
//						+ HP_Manager.getStatusbarCurrentMonth() + "월 "
//						+ HP_Manager.getStatusbarCurrentDay() + "일 "
//						+ HP_Manager.getCurrentDayOfWeek();
				
				mDate.setText(date);
			}
		}
	};
	
	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
//		HP_Manager.mLCDOnOff = HP_Index.LCD_ON;
		HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;	
		
		mContext.unregisterReceiver(mIntentReceiver);
		
		if(mCallback != null)
			mCallback.onExitScreenSaver();
		return false;
	}
	
   	public interface onAnalogEventListener{
   		public void onExitScreenSaver();
   	}
}