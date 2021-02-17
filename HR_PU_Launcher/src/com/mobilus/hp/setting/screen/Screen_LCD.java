package com.mobilus.hp.setting.screen;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Screen_LCD extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[Screen_LCD ]  ";
	
	public static final int LCD_NIGHT_MODE_BRIGHTNESS = 170;
	public static final int LCD_DAY_MODE_BRIGHTNESS = 215;

	private TextView mCheckBoxBrightness, mRadioBtnDayMode, mRadioBtnNightMode;
	private TextView mSelectAutoBrightness, mSelectDayMode, mSelectNightMode;
	private TextView mTxtDayMode, mTxtNightMode;
	private TextView mMsgDayMode, mMsgNightMode;
	private LinearLayout mIlluminationLayout;

	public static boolean isAutoIllum;
	public static boolean isManualDayLightMode;
	public static int mBrightnessValue = HP_Index.DEFAULT_BRIGHTNESS_STEP;
	
	public static boolean mFirstBoot = true;

	public Screen_LCD() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_screen_lcd, container, false);

		return inflater.inflate(R.layout.fragment_screen_lcd, container, false);
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
		super.onResume();
	}

	@Override
	public void onStart() {
		super.onStart();

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SCREEN)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SCREEN_MENU_LCD_SET)
			return;

		DxbView.eState = DxbView.STATE.NORMAL_VIEW;
		setLoadView();
	}

	private void setLoadView() {
		mIlluminationLayout = (LinearLayout) HP_Manager.mContext.findViewById(R.id.IlluminationLayout);
		mCheckBoxBrightness = (TextView) HP_Manager.mContext.findViewById(R.id.checkBoxAutoBrightness);
		mSelectAutoBrightness = (TextView) HP_Manager.mContext.findViewById(R.id.selectAutoBrightness);

		mSelectDayMode = (TextView) HP_Manager.mContext.findViewById(R.id.selectDayMode);
		mSelectNightMode = (TextView) HP_Manager.mContext.findViewById(R.id.selectNightMode);
		mRadioBtnDayMode = (TextView) HP_Manager.mContext.findViewById(R.id.radioBtnDayMode);
		mRadioBtnNightMode = (TextView) HP_Manager.mContext.findViewById(R.id.radioBtnNightMode);

		mTxtDayMode = (TextView) HP_Manager.mContext.findViewById(R.id.txtDayMode);
		mTxtNightMode = (TextView) HP_Manager.mContext.findViewById(R.id.txtNightMode);
		mMsgDayMode = (TextView) HP_Manager.mContext.findViewById(R.id.msgDayMode);
		mMsgNightMode = (TextView) HP_Manager.mContext.findViewById(R.id.msgNightMode);

		mSelectAutoBrightness.setOnClickListener(this);
		mSelectDayMode.setOnClickListener(this);
		mSelectNightMode.setOnClickListener(this);
		setView();
	}

	public void mSetIllumination() {
		if(isAutoIllum)
		{
			try {
				if(LauncherMainActivity.M_MTX.getStateIllumination())	//ILL On --> 야간모드로
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
				else	
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);  //ILL Off
				
				setBrightNess(LauncherMainActivity.M_MTX.getStateIllumination());
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
			}
		}
	}
	
	public static void setBrightNess(boolean isDayLightMode) {
		if (!isDayLightMode) { // 주간
			mDataLCDBrightness = LCD_DAY_MODE_BRIGHTNESS;
		} else { // 야간
			mDataLCDBrightness = LCD_NIGHT_MODE_BRIGHTNESS;
		}

		try {
			LauncherMainActivity.M_MTX.setBrightness(mDataLCDBrightness);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		mSendToCPU(mDataLCDBrightness);
	}

	private void setView() {
		if (isAutoIllum) {
			SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "auto");
			
			// illumination on/off인지 판단하는 로직 추가 --> on일 경우 야간모드로 바꾸기(illumination start 이벤트 처리와 동일하게)
			try {
				if(LauncherMainActivity.M_MTX.getStateIllumination())	//ILL On  --> 야간
				{
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
					setBrightNess(true);
				} 
				else 
				{	
					//ILL Off
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
					setBrightNess(false);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
					
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxBrightness.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxBrightness.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
			mIlluminationLayout.setVisibility(View.GONE);
		} else {
			SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "manual");

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxBrightness.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxBrightness.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			mIlluminationLayout.setVisibility(View.VISIBLE);
			setBrightnessMode(isManualDayLightMode);
		}
	}

	@Override
	public void onClick(View view) {

		int id = view.getId();
		switch (id) {
		case R.id.selectAutoBrightness:
			isAutoIllum = !isAutoIllum;
			setView();
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SCREEN_LCD);
			break;
		case R.id.selectDayMode:
			if(isManualDayLightMode == HP_Index.BRIGHTNESS_DAY_MODE) return;
			
			isManualDayLightMode = HP_Index.BRIGHTNESS_DAY_MODE;
			setBrightnessMode(HP_Index.BRIGHTNESS_DAY_MODE);
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SCREEN_LCD);
			break;
		case R.id.selectNightMode:
			if(isManualDayLightMode == HP_Index.BRIGHTNESS_NIGHT_MODE) return;
			isManualDayLightMode = HP_Index.BRIGHTNESS_NIGHT_MODE;
			setBrightnessMode(HP_Index.BRIGHTNESS_NIGHT_MODE);
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SCREEN_LCD);
			break;
		default:
			break;
		}
	}

	/**
	 * 주/야간모드에 대한 Hovered 처리
	 * 
	 * @param mode
	 */
	private void setBrightnessMode(boolean _mode) {
		if (isManualDayLightMode == HP_Index.BRIGHTNESS_DAY_MODE) {	//주간 (false)
			mSelectDayMode.setHovered(true);
			mSelectNightMode.setHovered(false);
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI)) {
				mRadioBtnDayMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));
				mRadioBtnNightMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));
			}
			else {
				mRadioBtnDayMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));
				mRadioBtnNightMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			}
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);

			/* 글씨색 변경 */
			mTxtDayMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
			mMsgDayMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
			mTxtNightMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
			mMsgNightMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
		} else {	// 야간 (true)
			mSelectDayMode.setHovered(false);
			mSelectNightMode.setHovered(true);
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI)) {
				mRadioBtnDayMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));
				mRadioBtnNightMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));
			}
			else {
				mRadioBtnDayMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
				mRadioBtnNightMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));
			}

			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
			
			/* 글씨색 변경 */
			mTxtDayMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
			mMsgDayMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
			mTxtNightMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
			mMsgNightMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
		}
		setProgressBar(HP_Index.PROGRESS_SET);
	}

	public static int mDataLCDBrightness;

	/**
	 * 프로그레스바 조정
	 * 
	 * @param seekbar
	 * @param state
	 */
	private void setProgressBar(int state) {
		int value = 0;
		boolean isDayLightMode = true;

		try {
			if(isAutoIllum)
				isDayLightMode = LauncherMainActivity.M_MTX.getStateIllumination();
			else
				isDayLightMode = isManualDayLightMode;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
		}

		if (value < 0)
			value = 0;
		else if (value > HP_Index.PROGRESS_MAX)
			value = HP_Index.PROGRESS_MAX;


		// 190426 yhr
		if (isDayLightMode == HP_Index.BRIGHTNESS_DAY_MODE) {
			mDataLCDBrightness = LCD_DAY_MODE_BRIGHTNESS;
		} else {
			mDataLCDBrightness = LCD_NIGHT_MODE_BRIGHTNESS;
		}

		try {
			LauncherMainActivity.M_MTX.setBrightness(mDataLCDBrightness);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}

		try {
			if(state != HP_Index.PROGRESS_MOVE)
				mSendToCPU(mDataLCDBrightness);
		} catch (NullPointerException e) {
		}
	}
	
	private static void mSendToCPU(int _value)
	{
		int day=_value, night = _value;
		boolean isDayMode = isManualDayLightMode;
		if(isAutoIllum) {
			day = LCD_DAY_MODE_BRIGHTNESS;
			night = LCD_NIGHT_MODE_BRIGHTNESS;
		} else if(isDayMode == HP_Index.BRIGHTNESS_NIGHT_MODE) {
			day = LCD_NIGHT_MODE_BRIGHTNESS;
			night = LCD_NIGHT_MODE_BRIGHTNESS;
		} else {
			day = LCD_DAY_MODE_BRIGHTNESS;
			night = LCD_DAY_MODE_BRIGHTNESS;
		}
		
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mSendToCPU / day : " + day + ", night : " + night);
		
		if(mFirstBoot) {
			int _cpu = _value;
			try {
				_cpu = LauncherMainActivity.M_MTX.loadBrightness(isDayMode);
			} catch (RemoteException e) {
			} catch (NullPointerException e) {
			}
			if(_value == _cpu) {
				try {
					LauncherMainActivity.M_MTX.saveBrightness(day, night);
				} catch (RemoteException e) {
				} catch (NullPointerException e) {
				}
			}
		} else {
			try {
				LauncherMainActivity.M_MTX.saveBrightness(day, night);
			} catch (RemoteException e) {
			} catch (NullPointerException e) {
			}
		}
		mFirstBoot = false;
	}
	
	public void mBootComplete() {
		if(isAutoIllum)
		{
			try {
				if(LauncherMainActivity.M_MTX.getStateIllumination())	//ILL On --> 야간모드로
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
				 else
					 HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
				
				setBrightNess(LauncherMainActivity.M_MTX.getStateIllumination());
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (NullPointerException e) {
			}
		}
		else
		{
			if(isManualDayLightMode == HP_Index.BRIGHTNESS_DAY_MODE) // 주간모드
				HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
			else
				HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
			setBrightNess(isManualDayLightMode);
		}
	}
}
