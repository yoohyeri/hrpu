package com.mobilus.hp.setting.system;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.HP_Manager.CurrentTime;
import com.mobilus.hp.launcher.LongClickEvent.LongClickCallBack;
import com.mobilus.hp.launcher.LongClickEvent;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author yhr
 *
 */
public class System_Time extends Fragment implements View.OnClickListener{
	
	private static final String CLASS_NAME = "[System_Time ]  ";
	
	private TextView mSelectAutoTime, mSelect24Hour;
	private TextView mCheckBoxAutoTime, mCheckBox24Hour;
	
	/* time setting */
	private LongClickEvent mBtnYearUp, mBtnYearDown;
	private LongClickEvent mBtnMonthUp, mBtnMonthDown;
	private LongClickEvent mBtnDayUp, mBtnDayDown;

	public static TextView txtCurrentDay, txtCurrentYear, txtCurrentMonth, txtCurrentHour, txtCurrentMinute, txtCurrentAmPm;
	public static TextView txtDay, txtYear, txtMonth, txtHour, txtMinute, txtAmPm;
	
	private LongClickEvent mBtnHourUp, mBtnHourDown;
	private LongClickEvent mBtnMinuteUp, mBtnMinuteDown;
	private LongClickEvent mBtnAmPmUP, mBtnAmPmDown;
	
	private LinearLayout mAmPmSpin;
	
	private enum MONTH {
		JAN, FEB, MAR, APR, MAY, JUN, JUL, AUG, SEP, OCT, NOV, DEC
	};
	
	private View mRootView;
	public static String AM = "오전";
	public static String PM = "오후";
	
	public static int minHour;
	public static int maxHour;
	
	public static int dayArray[] = null;
	private boolean updateTime = false;
	
	public System_Time()
	{
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
    	if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
    		mRootView = inflater.inflate(R.layout._kia_fragment_system_time, null);
    	else
    		mRootView = inflater.inflate(R.layout.fragment_system_time, null);
    	return mRootView;
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
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onPause() {
		super.onPause();
		
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
		
		if(!HP_Manager.bIsAutoTime && updateTime) 
		{
			updateTime = false;
			HP_Manager.setDateAndTime();
			dateChanged();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
   			return;
		
		if(HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SYSTEM)
			return;
		
		if(HP_Manager.mSubMenu != HP_Index.SYSTEM_MENU_TIME_SET)
			return;
		
		updateTime = false;
		
		setLoadView();
		setInitTime();
		setEnableBtn(HP_Manager.bIsAutoTime);
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	/**
	 * GUI 초기화 
	 */
	private void setLoadView()
	{	
		mAmPmSpin = (LinearLayout) HP_Manager.mContext.findViewById(R.id.ampmLayout);
		mSelectAutoTime = (TextView) HP_Manager.mContext.findViewById(R.id.SelectAutoTime);
		mSelect24Hour = (TextView) HP_Manager.mContext.findViewById(R.id.Select24Hour);
		
		mCheckBoxAutoTime = (TextView) HP_Manager.mContext.findViewById(R.id.checkBoxAutoTime);
		mCheckBox24Hour = (TextView) HP_Manager.mContext.findViewById(R.id.checkBox24Hour);
		
		txtYear = (TextView) HP_Manager.mContext.findViewById(R.id.txtYear);
		txtMonth = (TextView) HP_Manager.mContext.findViewById(R.id.txtMonth);
		txtDay = (TextView) HP_Manager.mContext.findViewById(R.id.txtDay);
		txtHour = (TextView) HP_Manager.mContext.findViewById(R.id.txtHour);
		txtMinute = (TextView) HP_Manager.mContext.findViewById(R.id.txtMinute);
		txtAmPm = (TextView) HP_Manager.mContext.findViewById(R.id.txtAMPM);
		
		// time
		mBtnYearUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnYearUp);
		mBtnYearDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnYearDown);
		txtCurrentYear = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentYear);
		
		mBtnMonthUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMonthUp);
		mBtnMonthDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMonthDown);
		txtCurrentMonth = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentMonth);
		
		mBtnDayUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnDayUp);
		mBtnDayDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnDayDown);
		txtCurrentDay = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentDay);
		
		mBtnHourUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHourUp);
		mBtnHourDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHourDown);
		txtCurrentHour = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentHour);
		
		mBtnMinuteUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMinuteUp);
		mBtnMinuteDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMinutehDown);
		txtCurrentMinute = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentMinute);
		
		mBtnAmPmUP = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnAMPMUp);
		mBtnAmPmDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnAMPMDown);
		txtCurrentAmPm = (TextView) HP_Manager.mContext.findViewById(R.id.txtCurrentAMPM);
		
		mSelectAutoTime.setOnClickListener(this);
		mSelect24Hour.setOnClickListener(this);
		
		mBtnYearUp.setLongClickCallback(mLongClickEvent);
		mBtnYearDown.setLongClickCallback(mLongClickEvent);
		mBtnMonthUp.setLongClickCallback(mLongClickEvent);
		mBtnMonthDown.setLongClickCallback(mLongClickEvent);
		mBtnDayUp.setLongClickCallback(mLongClickEvent);
		mBtnDayDown.setLongClickCallback(mLongClickEvent);
		
		mBtnHourUp.setLongClickCallback(mLongClickEvent);
		mBtnHourDown.setLongClickCallback(mLongClickEvent);
		mBtnMinuteUp.setLongClickCallback(mLongClickEvent);
		mBtnMinuteDown.setLongClickCallback(mLongClickEvent);
		mBtnAmPmUP.setLongClickCallback(mLongClickEvent);
		mBtnAmPmDown.setLongClickCallback(mLongClickEvent);
		
		AM = HP_Manager.mContext.getResources().getString(R.string.am);
		PM = HP_Manager.mContext.getResources().getString(R.string.pm);
		
		if(HP_Manager.bIsAutoTime)
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		}
		else
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
		}
		
		if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
		}
		else
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		}
		// 매달 말일 저장
		dayArray = new int[12];
		for(int i = 0; i < 12; i++)
		{
			HP_Manager.mEditDate.set(HP_Manager.mEditDate.get(Calendar.YEAR), i, HP_Manager.mEditDate.get(Calendar.DAY_OF_WEEK));
			dayArray[i] = HP_Manager.mEditDate.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
//		maxYear = HP_Manager.mEditDate.get(Calendar.YEAR);
	}
	
	private LongClickEvent.LongClickCallBack mLongClickEvent = new LongClickCallBack() {

   		@Override
   		public void onLongPress(View view) {
   			mBtnClick(view);
   		}
   	};
   	
   	private void mBtnClick(View view)
	{
		int id = view.getId();
		
		switch (id) {
		case R.id.btnYearUp:
			updateTime = true;
			setYear(HP_Index.TIME_SET_BTN_UP);
			break;
		case R.id.btnYearDown:
			updateTime = true;
			setYear(HP_Index.TIME_SET_BTN_DOWN);
			break;
		case R.id.btnMonthUp:
			updateTime = true;
			setMonth(HP_Index.TIME_SET_BTN_UP);
			break;
		case R.id.btnMonthDown:
			updateTime = true;
			setMonth(HP_Index.TIME_SET_BTN_DOWN);
			break;
		case R.id.btnDayUp:
			updateTime = true;
			setDay(HP_Index.TIME_SET_BTN_UP);
			break;
		case R.id.btnDayDown:
			updateTime = true;
			setDay(HP_Index.TIME_SET_BTN_DOWN);
			break;
		case R.id.btnHourUp:
			updateTime = true;
			setHour(HP_Index.TIME_SET_BTN_UP);
			break;
		case R.id.btnHourDown:
			updateTime = true;
			setHour(HP_Index.TIME_SET_BTN_DOWN);
			break;
		case R.id.btnMinuteUp:
			updateTime = true;
			setMin(HP_Index.TIME_SET_BTN_UP);
			break;
		case R.id.btnMinutehDown:
			updateTime = true;
			setMin(HP_Index.TIME_SET_BTN_DOWN);
			break;
		case R.id.btnAMPMUp:
		case R.id.btnAMPMDown:
			updateTime = true;
			String ampm = txtCurrentAmPm.getText().toString();
			toggleAmPm(ampm.equals(AM));
			break;
		default:
			break;
		}
	}
   	
	private void setEnableBtn(boolean isEnable) 
	{
		isEnable = !isEnable;
		if(!isEnable)
		{
			txtYear.setAlpha((float) 0.5);
			txtMonth.setAlpha((float) 0.5);
			txtDay.setAlpha((float) 0.5);
			txtHour.setAlpha((float) 0.5);
			txtMinute.setAlpha((float) 0.5);
			txtAmPm.setAlpha((float) 0.5);
			
			txtCurrentYear.setAlpha((float) 0.5);
			txtCurrentMonth.setAlpha((float) 0.5);
			txtCurrentDay.setAlpha((float) 0.5);
			
			txtCurrentHour.setAlpha((float) 0.5);
			txtCurrentMinute.setAlpha((float) 0.5);
			txtCurrentAmPm.setAlpha((float) 0.5);
		}
		else
		{
			txtYear.setAlpha(1);
			txtMonth.setAlpha(1);
			txtDay.setAlpha(1);
			txtHour.setAlpha(1);
			txtMinute.setAlpha(1);
			txtAmPm.setAlpha(1);
			
			txtCurrentYear.setAlpha(1);
			txtCurrentMonth.setAlpha(1);
			txtCurrentDay.setAlpha(1);
			
			txtCurrentHour.setAlpha(1);
			txtCurrentMinute.setAlpha(1);
			txtCurrentAmPm.setAlpha(1);
		}
		mBtnYearUp.setEnabled(isEnable);
		mBtnYearDown.setEnabled(isEnable);
		mBtnMonthUp.setEnabled(isEnable);
		mBtnMonthDown.setEnabled(isEnable);
		mBtnDayUp.setEnabled(isEnable);
		mBtnDayDown.setEnabled(isEnable);
		
		mBtnHourUp.setEnabled(isEnable);
		mBtnHourDown.setEnabled(isEnable);
		mBtnMinuteUp.setEnabled(isEnable);
		mBtnMinuteDown.setEnabled(isEnable);
		mBtnAmPmUP.setEnabled(isEnable);
		mBtnAmPmDown.setEnabled(isEnable);
	}
	
	private void setInitTime()
	{
		boolean is24Hour = false;
		setAutoTime();
		
		if(is24Hour())
		{
			HP_Manager.mSetHour = HP_Index.TIME_SET_24_HOUR;
			is24Hour = true;
		}
		else
		{
			HP_Manager.mSetHour = HP_Index.TIME_SET_12_HOUR;
			is24Hour = false;
		}
		set24Hour(is24Hour);
	}

	private void updateDateAndTimeDisplay(Calendar currentDate) {
		int hour;
		String ampm;
		
		Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "updateDateAndTimeDisplay / " + currentDate.getTime().toString());
		
		if (is24Hour()) {
			hour = currentDate.get(Calendar.HOUR_OF_DAY);
			ampm = "";
			mAmPmSpin.setVisibility(View.INVISIBLE);
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
			
			minHour = 0;
			maxHour = 23;
			HP_Manager.mSetHour = HP_Index.TIME_SET_24_HOUR;
		} else {
			hour = currentDate.get(Calendar.HOUR);
			if (hour == 0)
				hour = 12;
			
			if(HP_Manager.bIsAutoTime)
				ampm = (currentDate.get(Calendar.AM_PM) == 0) ? AM : PM;
			else
				ampm = (CurrentTime.mAmPm == 0) ? AM : PM;
			
			mAmPmSpin.setVisibility(View.VISIBLE);
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			minHour = 1;
			maxHour = 12;
			HP_Manager.mSetHour = HP_Index.TIME_SET_12_HOUR;
		}
		int month = currentDate.get(Calendar.MONTH);
		month++;
		HP_Manager.setDateAndTime();
		setLayoutContents(currentDate.get(Calendar.YEAR), month,
					currentDate.get(Calendar.DAY_OF_MONTH), hour, currentDate.get(Calendar.MINUTE), currentDate.get(Calendar.SECOND), ampm);
		
	}

	private void setLayoutContents(int year, int month, int day, int hour, int min, int sec, String ampm) {
		HP_Manager.mEditDate.set(Calendar.YEAR, year);
		HP_Manager.mEditDate.set(Calendar.MONTH, month-1);
		HP_Manager.mEditDate.set(Calendar.DAY_OF_MONTH, day);
		HP_Manager.mEditDate.set(Calendar.MINUTE, min);
//		HP_Manager.mEditDate.set(Calendar.SECOND, sec);
		HP_Manager.mEditDate.set(Calendar.AM_PM, ampm.equals(AM) ? 0 : 1);
		
		// 191018
		long when = HP_Manager.mEditDate.getTimeInMillis();
		if (when / 1000 < Integer.MAX_VALUE)
		{
			txtCurrentYear.setText(String.valueOf(year));
		}
		else
		{
			HP_Manager.mEditDate.set(Calendar.YEAR, year-1);
		}
		
		txtCurrentDay.setText(String.valueOf(day));
		txtCurrentMonth.setText(String.valueOf(month));
		txtCurrentHour.setText(String.valueOf(hour));
		txtCurrentMinute.setText(String.valueOf(min));
		txtCurrentAmPm.setText(ampm);
		
		if (is24Hour())
		{
			HP_Manager.mEditDate.set(Calendar.HOUR_OF_DAY, hour);
		}
		else 
		{
			if (hour == 12)
				hour = 0;
			HP_Manager.mEditDate.set(Calendar.HOUR, hour);
		}
//		Log.d(HP_Manager.TAG_SETTING, "3 setLayoutContents / YEAR : " + HP_Manager.mEditDate.get(Calendar.YEAR) + ", year : " + year );
	}

	private void checkLeapYear(int year) {
		if (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
			dayArray[MONTH.FEB.ordinal()] = 29;
		else
			dayArray[MONTH.FEB.ordinal()] = 28;
	}
	
	/**
	 * 24시간으로 설정되면 true, 12시간으로 설정되면 false반환
	 * @return
	 */
	private boolean is24Hour() {
		if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			return false;
		else
			return true;
	}

	private void set24Hour(boolean is24Hour) {
		if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
		}
		else
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox24Hour.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		}
//		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
	}

	@Override
	public void onClick(View view) {
		boolean is24Hour = false;
		int id = view.getId();
		switch (id) {
		case R.id.SelectAutoTime:
			if(HP_Manager.bIsAutoTime)
				HP_Manager.bIsAutoTime = false;
			else
				HP_Manager.bIsAutoTime = true;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
			setAutoTime();
			break;
		case R.id.Select24Hour:
			updateTime = true;
			if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			{
				HP_Manager.mSetHour = HP_Index.TIME_SET_24_HOUR;
				is24Hour = true;
				
				// 191104 yhr
				SystemProperties.set(LauncherMainActivity.PROPERTIES_HOUR_OF_DAY, "true");
			}
			else
			{
				HP_Manager.mSetHour = HP_Index.TIME_SET_12_HOUR;
				is24Hour = false;
				
				// 191104 yhr
				SystemProperties.set(LauncherMainActivity.PROPERTIES_HOUR_OF_DAY, "false");
			}
			
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
			set24Hour(is24Hour);
			onChange24to12();
			
			if(HP_Manager.bIsAutoTime)
				updateDateAndTimeDisplay(HP_Manager.mSystemDate);
			else
				updateDateAndTimeDisplay(HP_Manager.mEditDate);
			break;
		
		default:
			break;
		}
	}
	
	
	private void onChange24to12()
	{
		String strHour = "";
		int hour;
		
		strHour = txtCurrentHour.getText().toString();
		hour = Integer.valueOf(strHour);
		
		if (is24Hour()) {
			
		} else {
			minHour = 1;
			maxHour = 12;
			
			if(hour >= maxHour)
			{
				// PM
				toggleAmPm(true);
			}
			else
			{
				// AM
				if (hour == 0)
					hour = 12;
				
				toggleAmPm(false);
			}
		}
	}
	
	/**
	 * @param isAm
	 * @return
	 */
	public void toggleAmPm(boolean isPm) {
		if (isPm)
		{
			txtCurrentAmPm.setText(HP_Manager.mContext.getResources().getString(R.string.pm));
			CurrentTime.mAmPm = HP_Index.TIME_PM;
		}
		else
		{
			txtCurrentAmPm.setText(HP_Manager.mContext.getResources().getString(R.string.am));
			CurrentTime.mAmPm = HP_Index.TIME_AM;
		}
		HP_Manager.mEditDate.set(Calendar.AM_PM, CurrentTime.mAmPm);
	}

	
	/**
	 * 자동 시간 설정
	 */
	public void setAutoTime()
	{
		if(HP_Manager.bIsAutoTime)
		{
			LauncherMainActivity.mUpdateTime = true;
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
			
			HP_Manager.setDateAndTime();
			
			HP_Manager.mSystemDate = Calendar.getInstance();
			updateDateAndTimeDisplay(HP_Manager.mSystemDate);
		}
		else
		{
			LauncherMainActivity.mUpdateTime = false;
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxAutoTime.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			
			HP_Manager.mEditDate = Calendar.getInstance();
			updateDateAndTimeDisplay(HP_Manager.mEditDate);
		}
		setEnableBtn(HP_Manager.bIsAutoTime);
		getCurrentTime();
	}
	
	private void getCurrentTime()
	{
		TimeZone tz;
		Date date = new Date();
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		tz = TimeZone.getTimeZone("Asia/Seoul");
		sf.setTimeZone(tz);
		
	}
	
	/**
	 * 년도 설정
	 * @param updown
	 */
	private void setYear(int updown)
	{
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
		
		String ampm = txtCurrentAmPm.getText().toString();
		
		
		
		if(updown == HP_Index.TIME_SET_BTN_UP)
		{
			// 2019.10.17 yhr
			long when = HP_Manager.mEditDate.getTimeInMillis();
			if (when / 1000 < Integer.MAX_VALUE)
			{
				year++;	
				
				checkLeapYear(year);

				if (day > dayArray[month - 1])
					day = dayArray[month - 1];
				
				// 210114 yhr
				setLayoutContents(year, month, day, hour, min, sec, ampm);
			}
		}
		else
		{
			year--;
			if (year < 1970)
				year = 1970;
			
			checkLeapYear(year);

			int	_month = month -1;
			if(_month < 0)
				_month = 0;
			
			if (day > dayArray[_month])
				day = dayArray[_month];
			
			// 210114 yhr
			setLayoutContents(year, month, day, hour, min, sec, ampm);
		}
	}
	
	/**
	 * 달 설정
	 * @param updown
	 */
	private void setMonth(int updown)
	{
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
				
		String ampm = txtCurrentAmPm.getText().toString();

		if(updown == HP_Index.TIME_SET_BTN_UP)
		{
			month++;
			if (month > 12)
				month = 1;

			if (day > dayArray[month - 1])
				day = dayArray[month - 1];
		}
		else
		{
			month--;
			if (month < 1)
				month = 12;

			if (day > dayArray[month - 1])
				day = dayArray[month - 1];
		}
		
		// 210114 yhr
		setLayoutContents(year, month, day, hour, min, sec, ampm);
	}
	
	/**
	 * 일 설정
	 * @param updown
	 */
	private void setDay(int updown)
	{
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
		
		String ampm = txtCurrentAmPm.getText().toString();
				
		if(updown == HP_Index.TIME_SET_BTN_UP)
		{
			day++;
			if (day > dayArray[month - 1])
				day = 1;
		}
		else
		{
			day--;
			if (day < 1)
				day = dayArray[month - 1];
		}
		
		// 210114 yhr
		setLayoutContents(year, month, day, hour, min, sec, ampm);
	}

	/**
	 * 시 설정
	 * @param updown
	 */
	private void setHour(int updown)
	{
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
		
		String ampm = txtCurrentAmPm.getText().toString();
		if(updown == HP_Index.TIME_SET_BTN_UP)
		{
			hour++;
			if (hour > maxHour)
				hour = minHour;
		}
		else
		{
			hour--;
			if (hour < minHour)
				hour = maxHour;
		}
		
		// 210114 yhr
		setLayoutContents(year, month, day, hour, min, sec, ampm);
	}
	
	/**
	 * 분 설정
	 * @param updown
	 */
	private void setMin(int updown)
	{
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
		String ampm = txtCurrentAmPm.getText().toString();
		if(updown == HP_Index.TIME_SET_BTN_UP)
		{
			min++;
			if (min > 59)
				min = 0;
		}
		else
		{
			min--;
			if (min < 0)
				min = 59;
		}
		
		//210114 yhr
		setLayoutContents(year, month, day, hour, min, sec, ampm);
	}
	
	private void dateChanged() {
		
		if(txtCurrentDay == null || txtCurrentMonth == null || txtCurrentYear == null || txtCurrentHour == null || txtCurrentMinute == null)
			return;
		
		int day = Integer.valueOf(txtCurrentDay.getText().toString());
		int month = Integer.valueOf(txtCurrentMonth.getText().toString());
		int year = Integer.valueOf(txtCurrentYear.getText().toString());
		int hour = Integer.valueOf(txtCurrentHour.getText().toString());
		int min = Integer.valueOf(txtCurrentMinute.getText().toString());
		// 210104 yhr
		int sec = 0;
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			sec = HP_Manager.mSystemDate.get(Calendar.SECOND);
		}
		else
		{
			HP_Manager.mEditDate = Calendar.getInstance();
			sec = HP_Manager.mEditDate.get(Calendar.SECOND);
		}
		/////////////////////////////////////////////
		
		checkLeapYear(year);

		year = Integer.valueOf(txtCurrentYear.getText().toString());

		if (month < 1 || month > 12)
			month = Integer.valueOf(txtCurrentMonth.getText().toString());

		if (day < 1 || day > dayArray[month - 1]) {
			day = Integer.valueOf(txtCurrentDay.getText().toString());

			if (day > dayArray[month - 1])
				day = dayArray[month - 1];
		}

		if (hour < minHour || hour > maxHour)
			hour = Integer.valueOf(txtCurrentHour.getText().toString());

		if (min < 0 || min > 59)
			min = Integer.valueOf(txtCurrentMinute.getText().toString());
		
		// 210114 yhr
		setLayoutContents(year, month, day, hour, min, sec, txtCurrentAmPm.getText().toString());
	}
	
	public void mBootComplete() {
		if(HP_Manager.bIsAutoTime)
		{
			if(LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.mUpdateTime = true;
		}
	}
	
	public void setChangeView()
	{
		if(HP_Manager.bIsAutoTime)
		{
			HP_Manager.mSystemDate = Calendar.getInstance();
			int hour;
		
			if (is24Hour()) {
				hour = HP_Manager.mSystemDate.get(Calendar.HOUR_OF_DAY);
			} else {
								
				hour = HP_Manager.mSystemDate.get(Calendar.HOUR);
				if (hour == 0)
					hour = 12;
			}
						
			int month = HP_Manager.mSystemDate.get(Calendar.MONTH);
			month++;
			
			txtCurrentDay.setText(String.valueOf(HP_Manager.mSystemDate.get(Calendar.DAY_OF_MONTH)));
			txtCurrentMonth.setText(String.valueOf(month));
			txtCurrentYear.setText(String.valueOf(HP_Manager.mSystemDate.get(Calendar.YEAR)));
			txtCurrentHour.setText(String.valueOf(hour));
			txtCurrentMinute.setText(String.valueOf(HP_Manager.mSystemDate.get(Calendar.MINUTE)));
		}
	}
}