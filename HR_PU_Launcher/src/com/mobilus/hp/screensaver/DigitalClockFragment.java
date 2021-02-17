package com.mobilus.hp.screensaver;

import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.HP_Manager.CurrentTime;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class DigitalClockFragment extends Fragment implements OnTouchListener {

	private static final String CLASS_NAME = "[DigitalClockFragment ]  ";
	private TextView txtFullModeAmPm;
	private TextView txtFullModeTime;
	private TextView txtFullModeDate;

	private onDigitalEventListener mCallback;
	private Context mContext;

	public DigitalClockFragment(Context context, onDigitalEventListener mEventListener) {
		mCallback = mEventListener;
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
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			rootView = inflater.inflate(R.layout.frag_screen_saver_digital, null);
		else
			rootView = inflater.inflate(R.layout._kia_frag_screen_saver_digital, null);

		txtFullModeAmPm = (TextView) rootView.findViewById(R.id.full_digital_ampm);
		txtFullModeTime = (TextView) rootView.findViewById(R.id.full_digital_time);
		txtFullModeDate = (TextView) rootView.findViewById(R.id.full_digital_date);

		rootView.setOnTouchListener(this);

		String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
		String date = HP_Manager.getCurrentYear() + HP_Manager.mContext.getResources().getString(R.string.year) + " " 
				+ HP_Manager.getCurrentMonth() + HP_Manager.mContext.getResources().getString(R.string.month) + " " 
				+ HP_Manager.getCurrentDay() + HP_Manager.mContext.getResources().getString(R.string.day);
		
		if (!txtFullModeAmPm.getText().toString().equals(HP_Manager.getCurrentAmPm()))
			txtFullModeAmPm.setText(HP_Manager.getCurrentAmPm());

		if (!txtFullModeDate.getText().toString().equals(date))
			txtFullModeDate.setText(date);

		if (HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			txtFullModeAmPm.setVisibility(View.VISIBLE);
		else
			txtFullModeAmPm.setVisibility(View.GONE);

		txtFullModeTime.setText(time);
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
				String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
				String date = HP_Manager.getCurrentYear() + HP_Manager.mContext.getResources().getString(R.string.year) + " " 
							+ HP_Manager.getCurrentMonth() + HP_Manager.mContext.getResources().getString(R.string.month) + " " 
							+ HP_Manager.getCurrentDay() + HP_Manager.mContext.getResources().getString(R.string.day);
				
				txtFullModeTime.setText(time);
				txtFullModeDate.setText(date);
				HP_Manager.getCurrentAmPm();
				if (CurrentTime.mAmPm == HP_Index.TIME_AM)
					txtFullModeAmPm.setText(HP_Manager.mContext.getResources().getString(R.string.am));
				else
					txtFullModeAmPm.setText(HP_Manager.mContext.getResources().getString(R.string.pm));
			}
		}
	};

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {

//		HP_Manager.mLCDOnOff = HP_Index.LCD_ON;
		HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;

		mContext.unregisterReceiver(mIntentReceiver);
		
		if (mCallback != null)
			mCallback.onExitScreenSaver();
		return false;
	}

	public interface onDigitalEventListener {
		public void onExitScreenSaver();
	}
}