package com.mobilus.hp.setting;

import java.util.Calendar;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;
import com.mobilus.hp.setting.screen.Screen_Adjust;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.mobilus.hp.setting.screen.Screen_Ratio;
import com.mobilus.hp.setting.sound.Sound_LR_Balance;
import com.mobilus.hp.setting.sound.Sound_Tone;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbScan;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class Setting_Initialize_View extends Fragment {

	private static final String CLASS_NAME = "[Setting_Initialize_View ]  ";
	public final static int DEFAULT_BRIGHTNESS = 209;
	
	private TextView mMsgInit, btnInit;
	public static int mMenuType;

	public Setting_Initialize_View(int type) {
		mMenuType = type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_setting_initialize_view, container, false);
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
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_INIT || HP_Manager.mSubMenu == HP_Index.SOUND_MENU_INIT ||
				HP_Manager.mSubMenu == HP_Index.ROOT_MENU_SYSTEM)
			setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setLoadView() {
		mMsgInit = (TextView) HP_Manager.mContext.findViewById(R.id.msgInitialize);
		btnInit = (TextView) HP_Manager.mContext.findViewById(R.id.btnInit);

		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			btnInit.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_2));
		else
			btnInit.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_2));

		if (mMenuType == HP_Index.ROOT_MENU_SCREEN)
			mMsgInit.setText(HP_Manager.mContext.getResources().getString(R.string.msg_screen_initialize));
		else if (mMenuType == HP_Index.ROOT_MENU_SOUND)
			mMsgInit.setText(HP_Manager.mContext.getResources().getString(R.string.msg_sound_initialize));
		else if (mMenuType == HP_Index.ROOT_MENU_SYSTEM)
			mMsgInit.setText(HP_Manager.mContext.getResources().getString(R.string.msg_factory_initialize));

		btnInit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_INITAILIZE;
				LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, 
						HP_Manager.mContext.getResources().getString(R.string.initialize_message));
			}
		});
	}
	
	public static boolean screenSettingReset() {
		Log.i(HP_Manager.TAG_SETTING, CLASS_NAME + "================ screenSettingReset ====================");
		
		// Adjust
		Screen_Adjust.mBrightnessStep = Screen_Adjust.PROGRESS_MAX / 2;
		Screen_Adjust.mContrastStep = Screen_Adjust.PROGRESS_MAX / 2;
		Screen_Adjust.mSaturationStep = Screen_Adjust.PROGRESS_MAX / 2;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_ADJUST);
		
		// 자동 밝기 여부
		Screen_LCD.isAutoIllum = true;
		Screen_LCD.isManualDayLightMode = true;
		Screen_LCD.mBrightnessValue = HP_Index.DEFAULT_BRIGHTNESS_STEP;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SCREEN_LCD);
		
		// Rear Camera
		HP_Manager.mParkingGuideLine = true;
		try {
			LauncherMainActivity.M_MTX.setParkingLine(0);
			LauncherMainActivity.M_MTX.saveParkingLine(0);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		// Ratio
		Screen_Ratio.mRatioMode = HP_Index.DISPLAY_RATIO_16_9;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_RATIO);
		
		Screen_LCD _ScreenLCD = new Screen_LCD();
		_ScreenLCD.mSetIllumination();
		
		Screen_Adjust _ScreenAdjust = new Screen_Adjust();
		_ScreenAdjust.mBootComplete();
		
		Screen_Ratio _ScreenRatio = new Screen_Ratio();
		_ScreenRatio.mBootComplete();
		
		return true;
	}
	
	public static boolean soundSettingReset() {
		Log.i(HP_Manager.TAG_SETTING, CLASS_NAME + "================ soundSettingReset ====================");
		// 내비게이션 안내 우선 설정/ 비프음
		HP_Manager.mIsNaviGuidance = true;
		Setting_CheckBox_View.isBeepSound = true;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SOUND_CHECK_BOX);
		Settings.System.putInt(HP_Manager.mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		
		// 좌우 밸런스
		Sound_LR_Balance.mBalanceStep =  Sound_LR_Balance.PROGRESS_MAX / 2;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LR_BALANCE);
		Sound_LR_Balance _SoundLR = new Sound_LR_Balance();
		_SoundLR.mBootComplete();
		
		// tone 설정
		Sound_Tone.mHightToneStep = Sound_Tone.PROGRESS_MAX / 2;
		Sound_Tone.mMediumToneStep = Sound_Tone.PROGRESS_MAX / 2;
		Sound_Tone.mLowToneStep = Sound_Tone.PROGRESS_MAX / 2;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_TONE);
		Sound_Tone _SoundTone = new Sound_Tone();
		_SoundTone.mBootComplete();
		return true;
	}

	public static boolean systemSettingReset() {
		Log.i(HP_Manager.TAG_SETTING, CLASS_NAME + "================ systemSettingReset ====================");
		soundSettingReset();
		screenSettingReset();
		DxbScan.mDMBFactoryReset();
		DxbPlayer.mTPEGFactoryReset();
		
		// 대기화면 초기화
		HP_Manager.mScreenSaver = HP_Index.SCREEN_SAVER_DIGITAL;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);
		
		// 날짜/시간 설정 초기화
		HP_Manager.bIsAutoTime = true;
		HP_Manager.mSetHour = HP_Index.TIME_SET_12_HOUR;
		HP_Manager.setDateAndTime();
		HP_Manager.mSystemDate = Calendar.getInstance();
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
		
		// 191104 yhr
		SystemProperties.set(LauncherMainActivity.PROPERTIES_HOUR_OF_DAY, "false");
		
		// DMB Volume
		HP_Manager.mCurrentDMBVol = 15;
		HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
		HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
		
		HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
		LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HP_Manager.mCurrentDMBVol, 0);
		

		// Fuel
		try {
			HP_Manager.mFuel = LauncherMainActivity.M_MTX.loadFuelType();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
		return true;
	}
	
	// 190909 yhr (생산공정에서 초기화 이벤트가 발생될 경우)
	public static boolean mInitSystem() {
		soundSettingReset();
		screenSettingReset();
		DxbScan.mDMBFactoryReset();
		DxbPlayer.mTPEGFactoryReset();
		
		// 대기화면 초기화
		HP_Manager.mScreenSaver = HP_Index.SCREEN_SAVER_DIGITAL;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);
		
		// 날짜/시간 설정 초기화
		HP_Manager.bIsAutoTime = true;
		HP_Manager.mSetHour = HP_Index.TIME_SET_12_HOUR;
		HP_Manager.setDateAndTime();
		HP_Manager.mSystemDate = Calendar.getInstance();
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
		
		// DMB Volume
		HP_Manager.mCurrentDMBVol = 15;
		HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
		HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
		LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HP_Manager.mCurrentDMBVol, 0);
		
		try {
			// Fuel
			HP_Manager.mFuel = HP_Index.FUEL_DIESEL;
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "0");
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_DIESEL, 0);
			
			LauncherMainActivity.M_MTX.saveFuelType(HP_Manager.mFuel);
			
			// Vehicle
			HP_Manager.mVehicleType = HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W;
			LauncherMainActivity.M_MTX.saveCarType(HP_Manager.mVehicleType);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
		
		return true;
	}
	
	public static boolean mSystemInitialize() {
		if (screenSettingReset() == false)
			return false;

		if (soundSettingReset() == false)
			return false;

		if (systemSettingReset() == false)
			return false;

		Log.i(HP_Manager.TAG_SETTING, CLASS_NAME + "================ mSystemInitialize ====================");
		DxbScan.mDMBFactoryReset();
		
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);

		HP_Manager.mCallback.onChangeDMBMuteState(HP_Index.DMB_SOUND_UNMUTE, true);
		LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 15, 0);
		
		HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
		
		if(LauncherMainActivity.getInstance() != null)
			LauncherMainActivity.getInstance().setNaviMuteOnOff(HP_Index.NAVI_SOUND_UNMUTE);
		return true;
	}
}