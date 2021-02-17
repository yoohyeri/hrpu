package com.mobilus.hp.launcher;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.mnsoft.navi.ISurfaceInterface;
import com.mobilus.hp.setting.Setting_CheckBox_View;
import com.mobilus.hp.setting.Setting_Initialize_View;
import com.mobilus.hp.setting.Setting_Main;
import com.mobilus.hp.setting.Setting_Main_List;
import com.mobilus.hp.setting.Setting_TitleBar;
import com.mobilus.hp.setting.screen.Screen_Adjust;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.mobilus.hp.setting.screen.Screen_Ratio;
import com.mobilus.hp.setting.sound.Sound_LR_Balance;
import com.mobilus.hp.setting.sound.Sound_Tone;
import com.mobilus.hp.setting.system.System_Info;
import com.mobilus.hp.setting.system.System_Screen_Saver;
import com.mobilus.hp.setting.system.System_Time;
import com.mobilus.hp.setting.system.System_Update;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.IMTX;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.util.Log;

public class HP_Manager {
	public static final String TAG_LAUNCHER = "HRPU_Launcher";
	public static final String TAG_DMB = "HRPU_TDMB";
	public static final String TAG_SETTING = "HRPU_Setting";
	public static final String TAG_POPUP = "HRPU_Popup";
	public static final String TAG_CAS = "HRPU_CAS";
	public static final String TAG_BROADCAST = "HRPU_BroadCast";
	public static final String TAG_TPEG = "HRPU_Tpeg";
	
	private static final String CLASS_NAME = "[HP_Manager ]  ";
	public static final String HR_PU_PACKAGE_NAME = "com.telechips.android.tdmb";
	
	public static LauncherMainActivity mContext;

	/* Activity와 통신 */
	public static onEventListener mCallback;

	/* Fragment index */
	public static int mFragmentSetIndex = HP_Index.FRAGMENT_LAUNCHER_MAIN;

	/* 현재 보여지는 화면 */
	public static int mCurrentView;

	/* LCD ON/OFF */
//	public static int mLCDOnOff;
	public static boolean mPowerLong = false;

	/* 대기화면 */
	public static int mScreenSaver;
	
	/* 주차가이드라인 */
	public static boolean mParkingGuideLine;

	/* Setting에서 '자동시간조정' 체크 여부 */
	public static boolean bIsAutoTime;

	/* Setting에서 '24시간' 체크 여부 */
	public static int mSetHour = HP_Index.TIME_SET_12_HOUR;

	/* Widget의 모드 - Full(Clock/Map), Widget(Clock, Map) */
	public static int mViewMode = HP_Index.CLOCK_WIDGET_MODE;

	/* 현재 날짜와 시간 */
	public static SimpleDateFormat sdf;
	public static long mCurrentTime;
	public static Calendar mSystemDate;

	/* Back버튼 누를 시 이동해야 할 화면 */
	public static int mBackView = -1;
	public static boolean mChangeHome = false;
	public static boolean mChangeDMB = false;
	public static boolean mChangeSettings = false;

	/* 차량 연료  */
	public static int mFuel;

	/* 검색된 채널 개수 */
	public static int mCntSearchChannel = 0;

	/* 볼륨 팝업 크기 */
//	public static int mVolumeDialogMode = HP_Index.DIALOG_SHOW_MODE_DMB_NORMAL;

	/* 시스템 시간 변경할 때 쓰이는 Calendar */
	public static Calendar mEditDate;

	/* 현재 OS 버전정보 저장 */
	public static String strNewOSVersion = "없음";

	/* 현재 MCU 버전정보 저장 */
	public static String strNewFWVersion = "없음";

	/* OS 업데이트 파일 */
	public static File mUpdateFile;

	/* FW 업데이트 파일 */
	public static File mUpdateFileFW;

	/* Navi Menu Popup 표출 여부 */
	public static boolean mShowNaviMenuPopup = false;

	/* Map or Clock widget */
	public static boolean mWidgetMap;

	/* 모델정보 */
	public static final String SYSTEM_VENDOR_KIA = "kia";
	public static final String SYSTEM_VENDOR_HYUNDAI = "hyundai";
	public static String Vendor = "";

	/* navi service */
	public static ISurfaceInterface iNaviService;

	/* SD 마운트 상태 */
	public static boolean bIsSDCardMounted = false;
	public static String strSDPath = "";

	/* setting menu 정보 */
	public static int mRootMenu = -1;
	public static int mSubMenu = -1;
	
	/* 재부팅 유무 */
	public static boolean mIsRebooting = false;

	/* NaviGuidance 정보 */
	public static boolean mIsNaviGuidance;

	/* CAS SystemProperty */
	public final static String PROPERTIES_CAS_ID = "persist.mtx.cas.id";
	public final static String PROPERTIES_CAS_BS = "persist.mtx.cas.bs";
	public final static String PROPERTIES_CAS_NO = "persist.mtx.tdmb.cas";
	public final static String PROPERTIES_CAS_USER = "persist.mtx.cas.user";
	public final static String PROPERTIES_VER_OS = "mtx.system.version.cpu";
	public final static String PROPERTIES_VER_MCU = "mtx.system.version.mcu";

	/* Screen Saver 표출 여부 */
	public static boolean mShowScreenSaver = false;

	/* 생산 공정 프로그램 실행 여부 */
	public static boolean mProductionProcess;
	public static boolean mExistProductionChannel;

	/* Navi 음량 출력 상태 */
	public static boolean mNaviSoundStart = false;
	public static boolean mDMBSoundStart = false;

	/* Mute 상태 */
	public static int mSystemMuteStatus; // Power키에 의해 dmb, navi 모두 mute on/off
	public static int mDMBMuteStatus; // DMB만 Mute On/Off
	public static int mNAVIMuteStatus; // Navi만 Mute On/Off

	/* Preference */
	public static InitializePreferences mPreferences = null;
	
	/* 현재 DMB 볼륨 값 */	
	public static int mCurrentDMBVol = 0;
	
	/* 화면 캡처 */	
	public static boolean mCaptureScreen = false;
	
	/* 채널 정보 표출 유무 (히든메뉴 설정) */	
	public static boolean mShowDMBSignal = false;
	
	/* 차종 정보 */	
	public static int mVehicleType = -1;
	
	// 191127 yhr
	public static int mLastMode = -1;
	public static int mDMBVideoOnOff = -1;
	
	/* 현재 시간을 저장 */
	public static class CurrentTime {
		public static int mCurrentYear;
		public static int mCurrentMonthLastDay;
		public static int mYear;
		public static int mMonth;
		public static int mDay;
		public static int mDayOfWeek; // 요일

		public static int mHour;
		public static int mMin;
		public static int mSec;
		public static int mAmPm;
	}

	public static void init(LauncherMainActivity context, onEventListener mListener, boolean ProductionProcess) {
		
		Log.d(TAG_LAUNCHER, CLASS_NAME + "init()");
		mContext = context;
		mCurrentView = HP_Index.CURRENT_VIEW_HOME;
		mProductionProcess = ProductionProcess;
		mCallback = mListener;
		
		// 200123
		if(LauncherMainActivity.M_MTX == null)
			LauncherMainActivity.M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));
		
		mPreferences = new InitializePreferences(context);
		mPreferences.mInitializePreferences();

		mSystemDate = Calendar.getInstance(); // 시스템 시간
		mEditDate = Calendar.getInstance(); // 설정에서 지정한 시간

		// 현재 년도와 이번달의 말일 가져오기
		CurrentTime.mCurrentYear = mSystemDate.get(Calendar.YEAR);
		CurrentTime.mCurrentMonthLastDay = mSystemDate.getActualMaximum(Calendar.DATE);
	}

	/**
	 * Acitivty와의 통신을 위한 Interface
	 * 
	 * @author yhr
	 *
	 */
	public interface onEventListener {
		public void onChangeDMBView();
		public void onChangeMAPView();
		public void onChangeChannel(int ch);
		public void onChangeSettingView();
		public void onChangeDMBMuteState(int state, boolean visible);
		public void onChangeSDCardState(Intent state);
		public void goSettingMenu(int rootMenu, int subMenu);
		public void onChangeScreenSaverFullMode();
		public void onChangeHome();
		public void onPlayDMB();
		public void onNaviGuidance(int state);
		public void oncheckSDCardState();
		public void onRebooting();
	}

	public static void sendAppToNavi(int dwData, int lpData1, int lpData2) {
		if (mContext == null)
			return;

		Log.d(TAG_LAUNCHER, CLASS_NAME + "sendAppToNavi : " + dwData + ", data : " + lpData1);
		Intent _intent = new Intent();
		_intent.setAction(MAP_INFO.DEV_TO_MAP);
		_intent.putExtra(MAP_INFO.CMD_MAIN_1, dwData);
		_intent.putExtra(MAP_INFO.CMD_DATA_1, lpData1);
		_intent.putExtra(MAP_INFO.CMD_DATA_2, lpData2);
		mContext.sendBroadcastAsUser(_intent, new UserHandle(UserHandle.USER_CURRENT));
	}

	public static void _startLauncherActivity() {
		Log.d(TAG_LAUNCHER, CLASS_NAME + "_startLauncherActivity");
		Intent intent = new Intent(mContext, LauncherMainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		mContext.startActivity(intent);
	}

	/**
	 * 시계위젯 출력
	 */
	public static void outputWidget1(int mode) {
		if (LauncherMainActivity.getInstance() == null) {
			Log.e(TAG_LAUNCHER, CLASS_NAME + "outputWidget1 - LauncherMainActivity.getInstance() is null...");
			return;
		}

		if (LauncherMainActivity.getTopActivity().equals(HP_Manager.HR_PU_PACKAGE_NAME) == false) {
			Log.e(TAG_LAUNCHER, CLASS_NAME + "outputWidget1 - getTopActivity : " + LauncherMainActivity.getTopActivity());
			return;
		}

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_HOME) {
			if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER) {
				FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
				Clock_Widget widget1 = new Clock_Widget(mode);
				transaction.replace(R.id.fragment_clock, widget1);
				transaction.commit();
				return;
			}
		}

		FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
		Clock_Widget widget1 = new Clock_Widget(mode);
		transaction.replace(R.id.fragment_clock, widget1);
		transaction.commitAllowingStateLoss();
	}

	/**
	 * DMB 위젯 출력
	 * 
	 * @param mode
	 */
	public static void outputWidget2() {
		if (LauncherMainActivity.getInstance() == null)
			return;

		if (LauncherMainActivity.getTopActivity().equals(HP_Manager.HR_PU_PACKAGE_NAME) == false)
			return;

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_HOME)
			return;

		FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
		DMB_Widget widget2 = new DMB_Widget();
		transaction.replace(R.id.fragment_dmb, widget2);
		transaction.commit();
	}

	/**
	 * Fragment 호출 - Launcher/MAP or Clock/DMB/Setting Main 화면 호출
	 * 
	 * @param frament_no
	 */
	public static void callFragment(int fragIndex, int subMenu) {
		if(HP_Manager.mProductionProcess)
			return;
		
		FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
		mFragmentSetIndex = fragIndex;
		
		switch (fragIndex) {
		case HP_Index.FRAGMENT_LAUNCHER_MAIN:
			mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			Launcher_Main fragment0 = new Launcher_Main();
			transaction.replace(R.id.frag_container, fragment0);
			// transaction.commit();
			transaction.commitAllowingStateLoss();

			break;
		case HP_Index.FRAGMENT_DMB_MAIN:
			mCurrentView = HP_Index.CURRENT_VIEW_DMB;
			LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);
			break;
		case HP_Index.FRAGMENT_SETTING_MAIN:
			mRootMenu = -1;
			mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			// title bar
			Setting_TitleBar fragment2 = new Setting_TitleBar();
			transaction.replace(R.id.frag_container, fragment2);
			transaction.commitAllowingStateLoss();
			
			break;
		case HP_Index.FRAGMENT_SET_SYSTEM_SCREEN_SAVER:
			mRootMenu = HP_Index.ROOT_MENU_SYSTEM;
			mCurrentView = HP_Index.CURRENT_VIEW_SETTING;

			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			Setting_TitleBar fragment4 = new Setting_TitleBar(HP_Index.FRAGMENT_SET_SYSTEM, subMenu);
			transaction.replace(R.id.frag_container, fragment4);
			transaction.commit();
			break;
		case HP_Index.FRAGMENT_SET_SCREEN:
			mRootMenu = HP_Index.ROOT_MENU_SCREEN;
			mCurrentView = HP_Index.CURRENT_VIEW_SETTING;

			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			Setting_TitleBar fragment3 = new Setting_TitleBar(fragIndex, subMenu);
			transaction.replace(R.id.frag_container, fragment3);
			transaction.commit();

			break;
		case HP_Index.FRAGMENT_SET_SOUND:
			mRootMenu = HP_Index.ROOT_MENU_SOUND;
			mCurrentView = HP_Index.CURRENT_VIEW_SETTING;

			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			Setting_TitleBar fragment5 = new Setting_TitleBar(fragIndex, subMenu);
			transaction.replace(R.id.frag_container, fragment5);
			transaction.commit();

			break;
		case HP_Index.FRAGMENT_SET_SYSTEM:
			mRootMenu = HP_Index.ROOT_MENU_SYSTEM;
			mCurrentView = HP_Index.CURRENT_VIEW_SETTING;

			if (LauncherMainActivity.getInstance() != null)
				LauncherMainActivity.getInstance().changeStatusBar(HP_Manager.mCurrentView);

			Setting_TitleBar fragment6 = new Setting_TitleBar(fragIndex, subMenu);
			transaction.replace(R.id.frag_container, fragment6);
			transaction.commit();
			break;
		default:
			break;
		}
	}

	/**
	 * Setting Fragment 호출 (Setting화면의 메인/리스트만 호출) - RootMenu
	 * 
	 * @param frament_no
	 */
	public static void callSettingMenuFragment(int rootMenu, int subMenu) {

		if(HP_Manager.mProductionProcess)
			return;
		
		// 프래그먼트 사용을 위해
		FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();
		mRootMenu = rootMenu;
		mSubMenu = subMenu;
		mFragmentSetIndex = rootMenu;
		
		switch (rootMenu) {
		case HP_Index.FRAGMENT_SETTING_MAIN:
			Setting_TitleBar.txtIcon.setBackground(mContext.getResources().getDrawable(R.drawable.icon_setting));
			Setting_TitleBar.txtTitle.setText(mContext.getResources().getString(R.string.settings));

			Setting_Main fragment0 = new Setting_Main();
			transaction.replace(R.id.setting_fragment_container, fragment0);
			transaction.commit();
			break;
		case HP_Index.FRAGMENT_SET_SCREEN: // Screen의 첫번째 리스트가 첫화면 --> 화면조정
			Setting_TitleBar.txtIcon.setBackground(mContext.getResources().getDrawable(R.drawable.icon_screen));
			Setting_TitleBar.txtTitle.setText(mContext.getResources().getString(R.string.set_screen));

			Setting_Main_List fragment1 = new Setting_Main_List(HP_Index.ROOT_MENU_SCREEN, subMenu);
			transaction.replace(R.id.setting_fragment_container, fragment1);
			transaction.commit();
			break;
		case HP_Index.FRAGMENT_SET_SOUND: // Sound의 첫번째 리스트가 첫화면 --> 동시음량설정
			Setting_TitleBar.txtIcon.setBackground(mContext.getResources().getDrawable(R.drawable.icon_sound));
			Setting_TitleBar.txtTitle.setText(mContext.getResources().getString(R.string.set_sound));

			Setting_Main_List fragment2 = new Setting_Main_List(HP_Index.ROOT_MENU_SOUND, subMenu);
			transaction.replace(R.id.setting_fragment_container, fragment2);
			transaction.commit();
			break;
		case HP_Index.FRAGMENT_SET_SYSTEM: // System의 첫번째 리스트가 첫화면 --> 화면보호기
			Setting_TitleBar.txtIcon.setBackground(mContext.getResources().getDrawable(R.drawable.icon_system));
			Setting_TitleBar.txtTitle.setText(mContext.getResources().getString(R.string.set_system));

			Setting_Main_List fragment3 = new Setting_Main_List(HP_Index.ROOT_MENU_SYSTEM, subMenu);
			transaction.replace(R.id.setting_fragment_container, fragment3);
			transaction.commit();
			break;
		}
	}

	/**
	 * 메뉴 리스트 항목에 대한 설정화면으로 전환
	 * 
	 * @param frament_no
	 */
	public static void callListItemFragment(int type, int menu_list_no) {

		if(Setting_Main.mHiddenMode == false)
		{
			mRootMenu = type;
			mSubMenu = menu_list_no;
		}

		// 프래그먼트 사용을 위해
		FragmentTransaction transaction = mContext.getFragmentManager().beginTransaction();

		if (type == HP_Index.ROOT_MENU_SCREEN) {
			switch (menu_list_no) {
			case HP_Index.SCREEN_MENU_ADJUST:
				Screen_Adjust fragment0 = new Screen_Adjust();
				transaction.replace(R.id.sub_fragment_container, fragment0);
				transaction.commit();
				break;
			case HP_Index.SCREEN_MENU_LCD_SET:
				Screen_LCD fragment1 = new Screen_LCD();
				transaction.replace(R.id.sub_fragment_container, fragment1);
				transaction.commit();
				break;
			case HP_Index.SCREEN_MENU_REAR_CAM:
				Setting_CheckBox_View fragment2 = new Setting_CheckBox_View(HP_Index.SCREEN_MENU_REAR_CAM);
				transaction.replace(R.id.sub_fragment_container, fragment2);
				transaction.commit();
				break;
			case HP_Index.SCREEN_MENU_RATIO:
				Screen_Ratio fragment3 = new Screen_Ratio();
				transaction.replace(R.id.sub_fragment_container, fragment3);
				transaction.commit();
				break;
			case HP_Index.SCREEN_MENU_INIT:
				Setting_Initialize_View fragment4 = new Setting_Initialize_View(HP_Index.ROOT_MENU_SCREEN);
				transaction.replace(R.id.sub_fragment_container, fragment4);
				transaction.commit();
				break;
			}
		} else if (type == HP_Index.ROOT_MENU_SOUND) {
			switch (menu_list_no) {
			case HP_Index.SOUND_MENU_VOL_SET:
				Setting_CheckBox_View fragment0 = new Setting_CheckBox_View(HP_Index.SOUND_MENU_VOL_SET);
				transaction.replace(R.id.sub_fragment_container, fragment0);
				transaction.commit();

				break;
			case HP_Index.SOUND_MENU_BEEP_SET:
				Setting_CheckBox_View fragment1 = new Setting_CheckBox_View(HP_Index.SOUND_MENU_BEEP_SET);
				transaction.replace(R.id.sub_fragment_container, fragment1);
				transaction.commit();

				break;
			case HP_Index.SOUND_MENU_L_R_BALANCE:
				Sound_LR_Balance fragment2 = new Sound_LR_Balance();
				transaction.replace(R.id.sub_fragment_container, fragment2);
				transaction.commit();

				break;
			case HP_Index.SOUND_MENU_TONE_SET:
				Sound_Tone fragment3 = new Sound_Tone();
				transaction.replace(R.id.sub_fragment_container, fragment3);
				transaction.commit();

				break;
			case HP_Index.SOUND_MENU_INIT:
				Setting_Initialize_View fragment4 = new Setting_Initialize_View(HP_Index.ROOT_MENU_SOUND);
				transaction.replace(R.id.sub_fragment_container, fragment4);
				transaction.commit();
				break;
			}
		} else {
			switch (menu_list_no) {
			case HP_Index.SYSTEM_MENU_SCREEN_SAVER:
				System_Screen_Saver fragment0 = new System_Screen_Saver();
				transaction.replace(R.id.sub_fragment_container, fragment0);
				transaction.commit();
				break;
			case HP_Index.SYSTEM_MENU_TIME_SET:
				System_Time fragment1 = new System_Time();
				transaction.replace(R.id.sub_fragment_container, fragment1);
				transaction.commit();

				break;
			case HP_Index.SYSTEM_MENU_UPDATE:
				System_Update fragment2 = new System_Update();
				transaction.replace(R.id.sub_fragment_container, fragment2);
				transaction.commit();

				break;
			case HP_Index.SYSTEM_MENU_SYSTEM_INFO:
				System_Info fragment3 = new System_Info();
				transaction.replace(R.id.sub_fragment_container, fragment3);
				transaction.commit();
				break;
			case HP_Index.SYSTEM_MENU_SYSTEM_FACTORY:
				Setting_Initialize_View fragment4 = new Setting_Initialize_View(HP_Index.ROOT_MENU_SYSTEM);
				transaction.replace(R.id.sub_fragment_container, fragment4);
				transaction.commit();
				break;
			}
		}
	}

	/**
	 * year 반환
	 * 
	 * @return
	 */
	public static String getCurrentYear() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mYear = mSystemDate.get(Calendar.YEAR);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mYear = mEditDate.get(Calendar.YEAR);
		}
		
		String year = String.valueOf(CurrentTime.mYear);
		return year;
	}

	/**
	 * Month 반환 (두글자)
	 * 
	 * @return
	 */
	public static String getCurrentMonth() {
		String month = getStatusbarCurrentMonth();
		if (month.length() == 1)
			month = String.format("%02d", CurrentTime.mMonth);

		return month;
	}

	/**
	 * Month 반환 (한글자)
	 * 
	 * @return
	 */
	public static String getStatusbarCurrentMonth() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mMonth = mSystemDate.get(Calendar.MONTH);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mMonth = mEditDate.get(Calendar.MONTH);
		}
		
		CurrentTime.mMonth++;
		String month = String.valueOf(CurrentTime.mMonth);
		return month;
	}

	/**
	 * 요일 반환
	 * 
	 * @return
	 */
	public static String getCurrentDayOfWeek() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mDayOfWeek = mSystemDate.get(Calendar.DAY_OF_WEEK);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mDayOfWeek = mEditDate.get(Calendar.DAY_OF_WEEK);
		}

		String dayofweek = "";
		switch (CurrentTime.mDayOfWeek) {
		case 1:
			dayofweek = "일요일";
			break;
		case 2:
			dayofweek = "월요일";
			break;
		case 3:
			dayofweek = "화요일";
			break;
		case 4:
			dayofweek = "수요일";
			break;
		case 5:
			dayofweek = "목요일";
			break;
		case 6:
			dayofweek = "금요일";
			break;
		case 7:
			dayofweek = "토요일";
			break;

		default:
			break;
		}
		return dayofweek;
	}

	/**
	 * day 반환 (두글자)
	 * 
	 * @return
	 */
	public static String getCurrentDay() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mDay = mSystemDate.get(Calendar.DAY_OF_MONTH);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mDay = mEditDate.get(Calendar.DAY_OF_MONTH);
		}
		String day = String.valueOf(CurrentTime.mDay);
		if (day.length() == 1)
			day = String.format("%02d", CurrentTime.mDay);

		return day;
	}

	/**
	 * day 반환 (한글자)
	 * 
	 * @return
	 */
	public static String getStatusbarCurrentDay() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mDay = mSystemDate.get(Calendar.DAY_OF_MONTH);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mDay = mEditDate.get(Calendar.DAY_OF_MONTH);
		}
		
		String day = String.valueOf(CurrentTime.mDay);
		return day;
	}

	/**
	 * hour 반환
	 * 
	 * @return
	 */
	public static String getCurrentHour() {
		if (bIsAutoTime) {
			// Calendar.HOUR_OF_DAY : 0~24시간, Calendar.HOUR : 0~11시간
			if (mSetHour == HP_Index.TIME_SET_24_HOUR)
				CurrentTime.mHour = mSystemDate.get(Calendar.HOUR_OF_DAY);
			else {
				CurrentTime.mHour = mSystemDate.get(Calendar.HOUR);
				
				if (CurrentTime.mHour == 0)
					CurrentTime.mHour = 12;
			}
		} else {
			mEditDate = Calendar.getInstance();
			
			if (mSetHour == HP_Index.TIME_SET_24_HOUR)
				CurrentTime.mHour = mEditDate.get(Calendar.HOUR_OF_DAY);
			else {
				CurrentTime.mHour = mEditDate.get(Calendar.HOUR);
				if (CurrentTime.mHour == 0)
					CurrentTime.mHour = 12;
			}
		}

		String hour = String.valueOf(CurrentTime.mHour);
		if (hour.length() == 1)
			hour = String.format("%02d", CurrentTime.mHour);

		return hour;
	}

	/**
	 * minute 반환
	 * 
	 * @return
	 */
	public static String getCurrentMin() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mMin = mSystemDate.get(Calendar.MINUTE);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mMin = mEditDate.get(Calendar.MINUTE);
		}
		String min = String.valueOf(CurrentTime.mMin);
		if (min.length() == 1)
			min = String.format("%02d", CurrentTime.mMin);

		return min;
	}
	
	public static String getCurrentSec() {
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mSec = mSystemDate.get(Calendar.SECOND);
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mSec = mEditDate.get(Calendar.SECOND);
		}
		String sec = String.valueOf(CurrentTime.mSec);
		if (sec.length() == 1)
			sec = String.format("%02d", CurrentTime.mSec);

		return sec;
	}

	/**
	 * minute 반환
	 * 
	 * @return
	 */
	public static String getCurrentAmPm() {
		String ampm = "";
		if (bIsAutoTime) {
			mSystemDate = Calendar.getInstance();
			CurrentTime.mAmPm = mSystemDate.get(Calendar.AM_PM); // 0: 오전, 1: 오후
		} else {
			mEditDate = Calendar.getInstance();
			CurrentTime.mAmPm = mEditDate.get(Calendar.AM_PM);
		}

		if (CurrentTime.mAmPm == HP_Index.TIME_AM)
			ampm = mContext.getResources().getString(R.string.am);
		else
			ampm = mContext.getResources().getString(R.string.pm);

		return ampm;
	}

	public static void setDateAndTime() {
		if (HP_Manager.bIsAutoTime) {
			long when = mSystemDate.getTimeInMillis();
			if (when / 1000 < Integer.MAX_VALUE)
				((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		} else {
			mEditDate = Calendar.getInstance();
			long when = mEditDate.getTimeInMillis();
			Log.i(HP_Manager.TAG_SETTING, CLASS_NAME + "setDateAndTime / " + mEditDate.getTime().toString());
			if (when / 1000 < Integer.MAX_VALUE)
				((AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
		}
	}
}