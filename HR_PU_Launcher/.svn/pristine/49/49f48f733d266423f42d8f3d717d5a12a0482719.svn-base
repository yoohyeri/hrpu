package com.mobilus.hp.launcher;

import com.telechips.android.tdmb.R;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Clock_Widget extends Fragment implements View.OnClickListener{

	private static final String CLASS_NAME = "[Clock_Widget ]  ";

	private RelativeLayout rlWidgetDigital, rlWidgetAnalog, rlWidgetNone;
	private TextView isTouch;

	/* Clock - Digital */
	private static TextView txtAmPm;
	private static TextView txtTime;
	private static TextView txtDate;
	
	public Clock_Widget()
	{
		
	}
	
	public Clock_Widget(int mode)
	{
		HP_Manager.mViewMode = mode;
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
    		return inflater.inflate(R.layout._kia_fragment_clock, container, false);
    	
        return inflater.inflate(R.layout.fragment_clock, container, false);
    }
    
    @Override
   	public void onCreate(Bundle savedInstanceState) {
       	super.onCreate(savedInstanceState);
   	}

   	@Override
   	public void onDestroy() {
   		super.onDestroy();
   	}

   	@Override
   	public void onPause() {
   		super.onPause();
   	}

   	@Override
   	public void onResume() {
   		setLoadView();
   		super.onResume();
   		setClockView();
   	}
   	
   	@Override
   	public void onStart() {
   		super.onStart();
   	}

	private void setLoadView()
	{
		// widget touch
		isTouch = (TextView) HP_Manager.mContext.findViewById(R.id.widget_clock_touch_view);
		
		// widget mode
		rlWidgetDigital = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_digital);
		rlWidgetAnalog = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_analog);
		rlWidgetNone = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_none);
		
		////////////////////////////////////////////////////////////////////////////////////////
		// digital - widget
		txtAmPm = (TextView) HP_Manager.mContext.findViewById(R.id.digital_ampm);
		txtTime = (TextView) HP_Manager.mContext.findViewById(R.id.digital_time);
		txtDate = (TextView) HP_Manager.mContext.findViewById(R.id.digital_date);
		
		if(txtAmPm != null)
		{
			if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
				txtAmPm.setVisibility(View.VISIBLE);
			else
				txtAmPm.setVisibility(View.GONE);
		}
		
		if(isTouch != null)
			isTouch.setOnClickListener(this);
	}
	
	/**
	 * screen saver에 따라 위젯의 GUI변경
	 */
	private void setClockView()
	{
		if(HP_Manager.mContext == null ||rlWidgetDigital == null || rlWidgetAnalog == null || rlWidgetNone == null )
			return;
	
//		if(HP_Manager.mLCDOnOff == HP_Index.LCD_OFF)
//		{
//			rlWidgetNone.setVisibility(View.GONE);
//			rlWidgetAnalog.setVisibility(View.GONE);
//			rlWidgetDigital.setVisibility(View.GONE);
//			return;
//		}
		
		if(HP_Manager.mScreenSaver == HP_Index.SCREEN_SAVER_DIGITAL)
		{
			if(HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
			{
				rlWidgetDigital.setVisibility(View.VISIBLE);
				rlWidgetAnalog.setVisibility(View.GONE);
				rlWidgetNone.setVisibility(View.GONE);
			}
			else
			{
				rlWidgetDigital.setVisibility(View.GONE);
				rlWidgetAnalog.setVisibility(View.GONE);
				rlWidgetNone.setVisibility(View.GONE);
			}
		}
		else if(HP_Manager.mScreenSaver == HP_Index.SCREEN_SAVER_ANALOG)
		{
			if(HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
			{	
				rlWidgetAnalog.setVisibility(View.VISIBLE);
				rlWidgetDigital.setVisibility(View.GONE);
				rlWidgetNone.setVisibility(View.GONE);
			}
			else
			{
				rlWidgetAnalog.setVisibility(View.GONE);
				rlWidgetDigital.setVisibility(View.GONE);
				rlWidgetNone.setVisibility(View.GONE);
			}
		}
		else
		{
			// SCREEN_SAVER_NONE
			if(HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
			{
				rlWidgetNone.setVisibility(View.VISIBLE);
				rlWidgetAnalog.setVisibility(View.GONE);
				rlWidgetDigital.setVisibility(View.GONE);
			}
			else
			{
				rlWidgetAnalog.setVisibility(View.GONE);
				rlWidgetDigital.setVisibility(View.GONE);
				rlWidgetNone.setVisibility(View.GONE);
			}
		}
		updateTime();
	}
	
	public static void updateTime()
	{
		String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
//		String date = HP_Manager.getCurrentYear() + "년 " + HP_Manager.getCurrentMonth() + "월 " + HP_Manager.getCurrentDay() + "일";
		
		String date = HP_Manager.getCurrentYear() + HP_Manager.mContext.getResources().getString(R.string.year) + " " 
				+ HP_Manager.getCurrentMonth() + HP_Manager.mContext.getResources().getString(R.string.month) + " " 
				+ HP_Manager.getCurrentDay() + HP_Manager.mContext.getResources().getString(R.string.day);
		
		if(HP_Manager.mContext == null || txtAmPm == null || txtDate == null || txtTime == null)
			return;
		
		if(HP_Manager.mScreenSaver == HP_Index.SCREEN_SAVER_DIGITAL)
		{
			if(HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
			{
				if(!txtAmPm.getText().toString().equals(HP_Manager.getCurrentAmPm()))
					txtAmPm.setText(HP_Manager.getCurrentAmPm());

				if(!txtDate.getText().toString().equals(date))
					txtDate.setText(date);

				txtTime.setText(time);
			}
		}
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		
//		if(LauncherMainActivity.getInstance() != null)
//		{
//			if(LauncherMainActivity.getInstance().mVolumeDialog != null)
//			{
//				if(LauncherMainActivity.getInstance().mVolumeDialog.isShowing())
//					LauncherMainActivity.getInstance().mVolumeDialog.dismissVolumeDialog();
//			}
//		}
		
		switch (id) {
		// CloCK Widget 클릭
		case R.id.widget_clock_touch_view:
			if(HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
			{
				if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
					HP_Manager.mChangeHome = true;
				else if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
					HP_Manager.mChangeDMB = true;
				
//				Log.d(Dlog.TAG_LAUNCHER, CLASS_NAME + "HP_Manager.mBackView : " + HP_Manager.mBackView + ", mChangeHome : " + HP_Manager.mChangeHome + ", mChangeDMB : " + HP_Manager.mChangeDMB);
				HP_Manager.mCallback.goSettingMenu(HP_Index.FRAGMENT_SET_SYSTEM_SCREEN_SAVER, HP_Index.SUB_MENU_LIST_0);
			}
			break;
		default:
			break;
		}
	}
}