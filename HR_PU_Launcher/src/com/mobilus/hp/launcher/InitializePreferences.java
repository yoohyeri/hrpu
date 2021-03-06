package com.mobilus.hp.launcher;

import com.mobilus.hp.setting.PreferenceConst;
import com.mobilus.hp.setting.Setting_CheckBox_View;
import com.mobilus.hp.setting.Setting_Main;
import com.mobilus.hp.setting.screen.Screen_Adjust;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.mobilus.hp.setting.screen.Screen_Ratio;
import com.mobilus.hp.setting.sound.Sound_LR_Balance;
import com.mobilus.hp.setting.sound.Sound_Tone;
import com.mobilus.hp.setting.system.System_Time;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.LauncherMainActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.IMTX;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;

public class InitializePreferences {
	private String CLASS_NAME = "[InitializePreferences ]  ";
	private static Context mContext;

	/* 200203 yhr 
	 * LastMode 저장 */
	public SharedPreferences mLastModePref = null;
	public SharedPreferences.Editor last_mode_prefs_edt = null;
	
	/* Setting에서 설정한 대기화면을 저장 */
	public SharedPreferences screen_saver_prefs = null;
	public SharedPreferences.Editor screen_saver_prefs_edt = null;

	/* DMB, Navi Volume */
	public SharedPreferences mDMBSoundPref = null;
	public SharedPreferences mNAVISoundPref = null;

	/* Adjust */
	public SharedPreferences mAdjustPref = null;

	/* Ratio */
	public SharedPreferences mRatioPref = null;

	/* L/R Balance */
	public SharedPreferences mBalancePref = null;

	/* Tone */
	public SharedPreferences mTonePref = null;

	/* 자동 밝기 여부 */
	public SharedPreferences mIllumPref = null;

	/* 주간 / 야간  LCD 선택값*/
	public SharedPreferences mDisplayModePref = null;

	/* 내비게이션 안내 우선 */
	private SharedPreferences mNaviGuidePref = null;

	/* 효과음 */
	private SharedPreferences mBeepSoundPref = null;

	/* 날짜/시간 */
	private SharedPreferences mSystemTimePref = null;
	
	public InitializePreferences() {
	}

	public InitializePreferences(Context context) {
		mContext = context;
	}

	public void mInitializePreferences()
	{
		mLastModePref = mContext.getSharedPreferences(PreferenceConst.LAST_MODE_SHARED_PREF, Context.MODE_PRIVATE);
		HP_Manager.mLastMode = mLastModePref.getInt(PreferenceConst.SET_LAST_MODE, HP_Index.LAST_MODE_HOME);
		HP_Manager.mDMBVideoOnOff = mLastModePref.getInt(PreferenceConst.SET_LAST_DMB_ON_OFF, HP_Index.DMB_VIDEO_ON);
		
		mScreenInitializePreferences();
		mSoundInitializePreferences();
		mSystemInitializePreferences();
		
		if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL)
		{
			HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
		}
		
		if(LauncherMainActivity.getInstance() != null)
		{
			LauncherMainActivity.getInstance().createSharedPreference();
			LauncherMainActivity.getInstance().loadSharedPreference();
		}
//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mInitializePreferences // LAST MODE : " + HP_Manager.mLastMode + ", LAST DMB VIDEO : " + HP_Manager.mDMBVideoOnOff);
	}
	
	//200221 yhr
	public int getDMBVideoOnOff()
	{
		return mLastModePref.getInt(PreferenceConst.SET_LAST_DMB_ON_OFF, HP_Index.DMB_VIDEO_ON);
	}
	
	public void mScreenInitializePreferences()
	{
		// Adjust
		mAdjustPref = mContext.getSharedPreferences(PreferenceConst.DISPLAY_ADJUST_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Screen_Adjust.mBrightnessStep = mAdjustPref.getInt(PreferenceConst.ADJUST_Brightness, Screen_Adjust.PROGRESS_MAX / 2);
		Screen_Adjust.mContrastStep = mAdjustPref.getInt(PreferenceConst.ADJUST_Contrast, Screen_Adjust.PROGRESS_MAX / 2);
		Screen_Adjust.mSaturationStep = mAdjustPref.getInt(PreferenceConst.ADJUST_Saturation, Screen_Adjust.PROGRESS_MAX / 2);
		
		//LCD Settings
		mIllumPref = mContext.getSharedPreferences(PreferenceConst.DISPLAY_ILLUM_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Screen_LCD.isAutoIllum = mIllumPref.getBoolean(PreferenceConst.MODE_IsAutoIllum, true);
		if(Screen_LCD.isAutoIllum)
			SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "auto");
		else
			SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "manual");
		
		// 주/야간 모드
		mDisplayModePref = mContext.getSharedPreferences(PreferenceConst.DISPLAY_MODE_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Screen_LCD.isManualDayLightMode = mDisplayModePref.getBoolean(PreferenceConst.MODE_IsDayLightMode, false); // true : 야간, false : 주간
		
		try {
			if(LauncherMainActivity.M_MTX == null)
				LauncherMainActivity.M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));
			
			if(LauncherMainActivity.M_MTX.loadParkingLine() == 0 )
				HP_Manager.mParkingGuideLine = true;
			else
				HP_Manager.mParkingGuideLine = false;
			
			HP_Manager.mVehicleType = LauncherMainActivity.M_MTX.loadCarType();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
		// Ratio
		mRatioPref = mContext.getSharedPreferences(PreferenceConst.DISPLAY_RATIO_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Screen_Ratio.mRatioMode = mRatioPref.getInt(PreferenceConst.RATIO, HP_Index.DISPLAY_RATIO_16_9);
	}

	public void mSoundInitializePreferences()
	{
		// 동시 음량 설정
		mNaviGuidePref = mContext.getSharedPreferences(PreferenceConst.SOUND_NAVI_GUIDANCE_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		HP_Manager.mIsNaviGuidance = mNaviGuidePref.getBoolean(PreferenceConst.NAVI_GUIDE, true);

		// 버튼 효과음 
		mBeepSoundPref = mContext.getSharedPreferences(PreferenceConst.SOUND_BEEP_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Setting_CheckBox_View.isBeepSound = mBeepSoundPref.getBoolean(PreferenceConst.BEEP_SOUND, true);
		
		// L/R Balance
		mBalancePref = mContext.getSharedPreferences(PreferenceConst.SOUND_LR_BALANCE_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Sound_LR_Balance.mBalanceStep = mBalancePref.getInt(PreferenceConst.LR_BALANCE, Sound_LR_Balance.PROGRESS_MAX / 2);
		Sound_LR_Balance _SoundLR = new Sound_LR_Balance();
		_SoundLR.mBootComplete();

		// Tone
		mTonePref = mContext.getSharedPreferences(PreferenceConst.SOUND_TONE_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		Sound_Tone.mHightToneStep = mTonePref.getInt(PreferenceConst.TONE_High, Sound_Tone.PROGRESS_MAX / 2);
		Sound_Tone.mMediumToneStep = mTonePref.getInt(PreferenceConst.TONE_Medium, Sound_Tone.PROGRESS_MAX / 2);
		Sound_Tone.mLowToneStep = mTonePref.getInt(PreferenceConst.TONE_Low, Sound_Tone.PROGRESS_MAX / 2);
		Sound_Tone _SoundTone = new Sound_Tone();
		_SoundTone.mBootComplete();
	}

	public void mSystemInitializePreferences()
	{
		// Screen Saver
		screen_saver_prefs = mContext.getSharedPreferences(PreferenceConst.SYSTEM_SCREEN_SAVER_SHARED_PREF,	Context.MODE_PRIVATE);
		screen_saver_prefs_edt = screen_saver_prefs.edit();
		HP_Manager.mScreenSaver = screen_saver_prefs.getInt(PreferenceConst.SET_SCREEN_SAVER_MODE, HP_Index.SCREEN_SAVER_DIGITAL);
		
		// 날짜/시간 설정
		mSystemTimePref = mContext.getSharedPreferences(PreferenceConst.SYSTEM_TIME_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		HP_Manager.bIsAutoTime = mSystemTimePref.getBoolean(PreferenceConst.SET_AUTO_TIME, true);
		HP_Manager.mSetHour = mSystemTimePref.getInt(PreferenceConst.SET_24_HOUR, HP_Index.TIME_SET_12_HOUR);
		
		// 191104 yhr
		if(HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			SystemProperties.set(LauncherMainActivity.PROPERTIES_HOUR_OF_DAY, "false");
		else
			SystemProperties.set(LauncherMainActivity.PROPERTIES_HOUR_OF_DAY, "true");
		
		System_Time _SystemTime = new System_Time();
		_SystemTime.mBootComplete();

		//--------- etc
		// DMB, Navi Volume
		mDMBSoundPref = mContext.getSharedPreferences(PreferenceConst.SOUND_CURRENT_VOLUME_SHARED_PREF, Context.MODE_MULTI_PROCESS);
		HP_Manager.mCurrentDMBVol = mDMBSoundPref.getInt(PreferenceConst.CURRENT_VOL, 15);
		HP_Manager.mDMBMuteStatus = mDMBSoundPref.getInt(PreferenceConst.DMB_MUTE_STATE, HP_Index.DMB_SOUND_UNMUTE);
		if(HP_Manager.mCurrentDMBVol == 0)
			HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_MUTE;
		
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "1 mSystemInitializePreferences // mCurrentDMBVol >> " + HP_Manager.mCurrentDMBVol 
				+ ", mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus 
				+ ", mNAVIMuteStatus : " + HP_Manager.mNAVIMuteStatus);

		// 20200616 yhr
		if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
		{
			if (HP_Manager.mCurrentDMBVol > DxbView_Normal.VOL_MAX)
				HP_Manager.mCurrentDMBVol = DxbView_Normal.VOL_MAX;
		}
				
		mNAVISoundPref = mContext.getSharedPreferences(PreferenceConst.NAVI_SOUND_CURRENT_VOLUME_SHARED_PREF, Context.MODE_MULTI_PROCESS); 
		HP_Manager.mNAVIMuteStatus = mNAVISoundPref.getInt(PreferenceConst.NAVI_MUTE_STATE, HP_Index.NAVI_SOUND_UNMUTE);
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "2 mSystemInitializePreferences // mCurrentDMBVol >> " + HP_Manager.mCurrentDMBVol 
																										+ ", mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus 
																										+ ", mNAVIMuteStatus : " + HP_Manager.mNAVIMuteStatus);
		
		HP_Manager.mDMBVideoOnOff = getDMBVideoOnOff();
		LauncherMainActivity.onChangeMuteIcon();
		
		try {
			if(LauncherMainActivity.M_MTX == null)
				LauncherMainActivity.M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));
			
			HP_Manager.mFuel = LauncherMainActivity.M_MTX.loadFuelType();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		Setting_Main _SettingMain = new Setting_Main();
		_SettingMain.mBootComplete();
		
	}

	public final int MODE_SCREEN_LCD = 0;
	public final int MODE_FUEL_PREF = 1;
	public final int MODE_SOUND_CHECK_BOX = 2; //
	public final int MODE_SYSTEM_TIME = 3;
	public final int MODE_SYSTEM_SCREEN = 4;
	public final int MODE_TONE = 5;
	public final int MODE_LR_BALANCE = 6;
	public final int MODE_RATIO = 7;
	public final int MODE_ADJUST = 8;
	public final int MODE_DMB_VOLUME = 9;
	public final int MODE_NAVI_MUTE_STATE = 10;
	public final int MODE_LAST_VIEW = 11;
	public final int MODE_DMB_ON_OFF = 12;
	
	public void mSavePreferenceSystem()
	{
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_TIME);
	}
	
	public void mSavePreferenceScreen()
	{
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_ADJUST);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SCREEN_LCD);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_RATIO);
	}
	
	public void mSavePreferenceSound()
	{
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_TONE);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LR_BALANCE);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SOUND_CHECK_BOX);
	}
	
	
	public void mSavePreferences(int _mode) {
		switch(_mode) {
		case MODE_SCREEN_LCD:
			if(mIllumPref != null )	 mIllumPref.edit().putBoolean(PreferenceConst.MODE_IsAutoIllum, Screen_LCD.isAutoIllum).commit();
			if(mDisplayModePref != null)  mDisplayModePref.edit().putBoolean(PreferenceConst.MODE_IsDayLightMode, Screen_LCD.isManualDayLightMode).commit();
			break;
		case MODE_FUEL_PREF:
			try {
				LauncherMainActivity.M_MTX.saveFuelType(HP_Manager.mFuel);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case MODE_SOUND_CHECK_BOX:
			if(mNaviGuidePref != null)	mNaviGuidePref.edit().putBoolean(PreferenceConst.NAVI_GUIDE, HP_Manager.mIsNaviGuidance).commit();
			if(mBeepSoundPref != null)	mBeepSoundPref.edit().putBoolean(PreferenceConst.BEEP_SOUND, Setting_CheckBox_View.isBeepSound).commit();
			break;
		case MODE_SYSTEM_TIME:
			if(mSystemTimePref != null) {
				mSystemTimePref.edit().putBoolean(PreferenceConst.SET_AUTO_TIME, HP_Manager.bIsAutoTime).commit();
				mSystemTimePref.edit().putInt(PreferenceConst.SET_24_HOUR, HP_Manager.mSetHour).commit();
			}
			break;
		case MODE_SYSTEM_SCREEN:
			if(screen_saver_prefs != null) screen_saver_prefs.edit().putInt(PreferenceConst.SET_SCREEN_SAVER_MODE, HP_Manager.mScreenSaver).commit();
			break;
		case MODE_TONE:
			if(mTonePref != null) {
				mTonePref.edit().putInt(PreferenceConst.TONE_High, Sound_Tone.mHightToneStep).commit();
				mTonePref.edit().putInt(PreferenceConst.TONE_Medium, Sound_Tone.mMediumToneStep).commit();
				mTonePref.edit().putInt(PreferenceConst.TONE_Low, Sound_Tone.mLowToneStep).commit();
			}
			break;
		case MODE_LR_BALANCE:
			if(mBalancePref != null)	mBalancePref.edit().putInt(PreferenceConst.LR_BALANCE, Sound_LR_Balance.mBalanceStep).commit();
			break;
		case MODE_RATIO:
			if(mRatioPref != null)	mRatioPref.edit().putInt(PreferenceConst.RATIO, Screen_Ratio.mRatioMode).commit();
			break;
		case MODE_ADJUST:
			if(mAdjustPref != null) {
				mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Brightness, Screen_Adjust.mBrightnessStep).commit();
				mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Contrast, Screen_Adjust.mContrastStep).commit();
				mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Saturation, Screen_Adjust.mSaturationStep).commit();
			}
			break;
		case MODE_DMB_VOLUME:
			if(mDMBSoundPref != null) {
				mDMBSoundPref.edit().putInt(PreferenceConst.DMB_MUTE_STATE, HP_Manager.mDMBMuteStatus).commit();
				mDMBSoundPref.edit().putInt(PreferenceConst.CURRENT_VOL, HP_Manager.mCurrentDMBVol).commit();
			}
			break;
		case MODE_NAVI_MUTE_STATE:
			if(mNAVISoundPref != null)
				mNAVISoundPref.edit().putInt(PreferenceConst.NAVI_MUTE_STATE, HP_Manager.mNAVIMuteStatus).commit();
			break;
		case MODE_LAST_VIEW:
			if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL )
			{ 
				if(HP_Manager.mProductionProcess || LauncherMainActivity.engineer_cas)
					HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
			}
			if(mLastModePref != null)  mLastModePref.edit().putInt(PreferenceConst.SET_LAST_MODE, HP_Manager.mLastMode).commit();
			break;
		case MODE_DMB_ON_OFF:
			if(mLastModePref != null)  mLastModePref.edit().putInt(PreferenceConst.SET_LAST_DMB_ON_OFF, HP_Manager.mDMBVideoOnOff).commit();
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mSavePreferences // getDMBVideoOnOff >> " + getDMBVideoOnOff()
					+ ",mDMBVideoOnOff : " + HP_Manager.mDMBVideoOnOff );
			break;
		}
	}
}
