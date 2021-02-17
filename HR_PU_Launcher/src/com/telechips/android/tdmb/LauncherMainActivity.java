/*
 * Copyright (C) 2013 Telechips, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telechips.android.tdmb;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.mdstec.android.tpeg.TpegService;
import com.mobilus.hp.launcher.Clock_Widget;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.HP_Manager.CurrentTime;
import com.mobilus.hp.launcher.HP_Manager.onEventListener;
import com.mobilus.hp.mapupdate.GINIAPPInfo;
import com.mobilus.hp.launcher.Launcher_Main;
import com.mobilus.hp.launcher.MAP_INFO;
import com.mobilus.hp.popup.DefaultDialog;
import com.mobilus.hp.popup.ScanDialog;
import com.mobilus.hp.popup.ScanDialog.ICustomSCANDialogEventListener;
import com.mobilus.hp.popup.DefaultDialog.ICustomDefaultDialogEventListener;
import com.mobilus.hp.popup.NaviMenuDialog;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.screensaver.ScreenSaverActivity;
import com.mobilus.hp.setting.DEBUG_;
import com.mobilus.hp.setting.Setting_Hidden_List;
import com.mobilus.hp.setting.Setting_Initialize_View;
import com.mobilus.hp.setting.Setting_Main;
import com.mobilus.hp.setting.screen.Screen_Adjust;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.mobilus.hp.setting.screen.Screen_Ratio;
import com.mobilus.hp.setting.sound.Sound_LR_Balance;
import com.mobilus.hp.setting.system.System_Time;
import com.mobilus.hp.setting.system.System_Update;
import com.mobilus.hp.setting.system.System_fw_Update;
import com.mobilus.hp.setting.system.pkt.MCU_;
import com.telechips.android.tdmb.player.Channel;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IMTX;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.media.AudioManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;

public class LauncherMainActivity extends Activity {
	private static final String CLASS_NAME = "[LauncherMainActivity ]  ";
	
	
	//////////////////////////////////////////////////////////////////////////////////////////////// 191122
	/**
	 * Port Tag
	 */
	public static final String UART_SERIAL_NAME			= "HMS_UART_SN_";
	
	/**
	 * Port Path
	 */
	public static final String MICOM_PORT_PATH			= "/dev/ttyS5";
	
	/**
	 * Serial Baud Rate
	 */
	public static final int UART_SERIAL_BAUD_RATE				= 115200;

	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	private final String PROPERTIES_TDMB = "tcc.dxb.tdmb";
	private final String SDCARD_PATH = "file:///storage/sdcard1";

	// navigation bar
	private final String PROPERTIES_NAVI = "tcc.wifi.display.sink.connect";

	// map
	private final String PROPERTIES_NAVI_GPS = "sys.gini.drgps.use";
	
	private final String PROPERTIES_NAVI_CODE = "sys.gini.notice.code";
	private final String PROPERTIES_NAVI_PLATFORM = "sys.gini.notice.platform";
	private final String PROPERTIES_NAVI_GENERATION = "sys.gini.notice.generation";
	private final String PROPERTIES_NAVI_ENVIRONMENT = "sys.gini.notice.environment";
	private final String PROPERTIES_NAVI_NOTICE_USE= "sys.gini.notice.use";
	
	private final String PROPERTIES_NAVI_AUDIO = "sys.gini.audio.streamtype";
	private final String PROPERTIES_NAVI_AUTOINSTALL = "sys.gini.autoinstaller.use";
	private final String PROPERTIES_NAVI_POWER_UI = "sys.gini.powerui.use";
	public final static String PROPERTIES_NAVI_EVMENU = "sys.gini.evmenu.use";
	private final String PROPERTIES_NAVI_TPEG = "sys.gini.tpeg.use";
	
	// 190516
	public final static String PROPERTIES_ILLUMINATION = "mtx.system.illumination";
	public final static String PROPERTIES_CAS = "mtx.dmbcas.enable";
	
	// 191104 yhr
	public final static String PROPERTIES_HOUR_OF_DAY = "mtx.time.hourofday";

	/*******************************************************************************
	 * Check State
	 *******************************************************************************/
	// Dxb_Player Life Cycle
	public enum DXB_LIFE_CYCLE {
		ON_CREATE, ON_START, ON_RESUME, ON_PAUSE, ON_STOP, ON_DESTROY, NULL
	}

	static public DXB_LIFE_CYCLE eCycle_Life = DXB_LIFE_CYCLE.ON_DESTROY;
	/*******************************************************************************/

	public TextView txtTitle, txtSpeed;
	private FrameLayout mFragmentArea;
	private TextView btnHome, btnNavi;
	private TextView txtCurrentTime, txtCurrentMonth, txtCurrentDay;
	private TextView txtAmPm;
	public TextView iconSDCard;
	public static TextView iconMute;

	private TimeReceiver timeReceiver;
	public static TextView tvStrength;

	/*
	 * 기본 팝업 - 확인/취소 or 확인
	 */
	public DefaultDialog mDefaultDialog;

	/*
	 * SCAN 팝업
	 */
	public ScanDialog mScanDialog;

	/**
	 * MTX Service
	 */
	public static IMTX M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));

	/**
	 * CAS 관련
	 */
	public static boolean cas_state = false;
	public static boolean engineer_cas = false;

	/**
	 * Audio Manager
	 */
	public static AudioManager mAudioManager;

	/**
	 * Audio Focus State
	 */
	private boolean mIsAudioGain = false;

	/**
	 * Current DMB Volume
	 */
	public static int mCurrentVolume = -1;

	/**
	 * Parking State
	 */
	public static boolean mParkingStatus = true;

	/**
	 * Scan Dialog
	 */
	public Dialog mScanStartDialog;

	private boolean mTopActivity = false;
	private boolean mLossTransient = false;
	private boolean bIsRegisterReceiver = false;

	/* Speed info */
	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	double mySpeed;

	private static LauncherMainActivity mInstance = null;

	public static boolean mRunningMAP;
	public static boolean mForgroundNavi;
	public boolean mBootCompleted = false;
	
	public static final int UPDATE_MODE_NONE = 0;
	public static final int UPDATE_MODE_OS = 1;
	public static final int UPDATE_MODE_FW = 2;
	public static final int UPDATE_FULL_MODE = 3;
	public static final int UPDATE_COMPLETE = 4;
	
	private static int SD_CARD_UNMOUNT = 0;
	private static int SD_CARD_MOUNT = 1;
	private static int SD_CARD_UNMOUNT_REBOOT = 2;
	private static int mSdCardState;
	
	public static boolean mShowUpdatePopup;
	
	private int MSG_CHECK_SDCARD = 0;
	private int DELAY_SD_CHECK = 100;
	public Handler mHandlerSDCheck = new Handler();
	private System_Time mSystemTime = null;
	
	public boolean mDismissPopup = false;
	
	//////////////////////////////////////////////////////////////////
	// UPDATE 관련
	private final String FW_UPDATE_FILE_NAME = "HR_PU_FW";

	public boolean mExistOSUpgradeFile = false;
	public boolean mExistFWUpgradeFile = false;
	
	public boolean mOSUpgradeMode = false;
	public boolean mFWUpgradeMode = false;
	public boolean mFullUpdateMode = false;
	
	public boolean mExistMapDataFile = false;
	
	public boolean mEmptySDCard = false;
	
	public boolean mforceUpdate = false;
	public static boolean mFactoryReset = false;
	////////////////////////////////////////////////////////////////

	public Runnable mRunnableUnmountCheck = new Runnable() {

		@Override
		public void run() {
			if(NotifyDialog.mCurrentPopup == NotifyDialog.REBOOTING_POPUP)
				return;
			
			if (isServiceRunningCheck()) {
				if (NotifyDialog.mNotifyDialog != null && NotifyDialog.mCurrentPopup != NotifyDialog.TRAFFIC_RULE_POPUP)
					NotifyDialog.mNotifyDialog.exitNotifyPopup();
				
				if(getTopActivity().equals(GINIAPPInfo.PACKAGE_NAME))
					HP_Manager.mWidgetMap = true;
			}
			
			if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_FILE_COPY)
			{
				mHandlerSDCheck.removeCallbacks(LauncherMainActivity.getInstance().mRunnableUnmountCheck);
				return;
			}
					
			if(HP_Manager.mWidgetMap)
				HP_Manager.mCallback.onRebooting();
			else
			{
				if(NotifyDialog.mCurrentPopup != NotifyDialog.TRAFFIC_RULE_POPUP)
				{
					// 메모리카드 해제 팝업
					NotifyDialog.mCurrentPopup = NotifyDialog.SDCARD_UNMOUNT_POPUP;
					Intent popup = null;
					popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
					startService(popup);
				}
				
				if(iconSDCard == null)
					return;
				
				iconSDCard.setVisibility(View.VISIBLE);
				
				mSdCardState = SD_CARD_UNMOUNT;
				HP_Manager.mNaviSoundStart = false;
				HP_Manager.mNAVIMuteStatus = HP_Index.NAVI_SOUND_MUTE;

				if (mDefaultDialog.isShowing())
					mDefaultDialog.dismiss();

				// 190419 yhr >> SD카드 제거할 때 Mute Icon 처리
				if (HP_Manager.mSystemMuteStatus == HP_Index.DMB_SOUND_MUTE) {
					if (DxbPlayer.getScanCount() > 0) {
						if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE)
							iconMute.setBackground(	HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
						else {
							HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
							iconMute.setVisibility(View.INVISIBLE);
						}
					} else {
						HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
						iconMute.setVisibility(View.INVISIBLE);
					}
				} else
					iconMute.setVisibility(View.INVISIBLE);

				if (HP_Manager.mIsNaviGuidance && !DxbView_Normal.mfromUserMute) {
					// 200212 yhr
//					if (HP_Manager.mLCDOnOff == HP_Index.LCD_ON ) 
					{
						HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
						HP_Manager.mCallback.onChangeDMBMuteState(HP_Index.DMB_SOUND_UNMUTE, false);
					}
				}
				
				// 19.01.23 yhr
				if (HP_Manager.mRootMenu == HP_Index.FRAGMENT_SET_SYSTEM && HP_Manager.mSubMenu == HP_Index.SUB_MENU_LIST_2) {
					System_Update _update = new System_Update();
					_update.changeUpdateMode();
				}

				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "unmount // root : " + HP_Manager.mRootMenu + ", sub : " + HP_Manager.mSubMenu + ", mCurrentView : " + HP_Manager.mCurrentView);

				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			
				// 19.02.28 yhr
				if (HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM && HP_Manager.mSubMenu == HP_Index.SYSTEM_MENU_UPDATE)
				{
					System_Update _update = new System_Update();
					_update.changeUpdateMode();
				}

				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME) 
					Launcher_Main.reflashWidget1();
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(HP_Manager.TAG_BROADCAST, "============== onCreate 210216 ============");
		
		mInstance = this;
		mRunningMAP = false;
		mShowUpdatePopup = true;
		mSdCardState = -1;
		mForgroundNavi = false;

		eCycle_Life = DXB_LIFE_CYCLE.ON_CREATE;
		HP_Manager.Vendor = SystemProperties.get("mtx.system.vendor", HP_Manager.SYSTEM_VENDOR_HYUNDAI);
		
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		SystemProperties.set(PROPERTIES_NAVI, "1"); // Navigation Bar Gone
		
		boolean bIsProductionProcess = getIntent().getBooleanExtra("ProductionProcess", false);
		HP_Manager.init(this, mEventListener, bIsProductionProcess);
		SystemProperties.set(PROPERTIES_TDMB, "on");
		
		//190906 yhr
		try {
			if(Settings.System.getInt(this.getContentResolver(), Settings.System.SHOW_TOUCHES) == 1)
				Settings.System.putInt(this.getContentResolver(), Settings.System.SHOW_TOUCHES,  0);
			
			if(Settings.System.getInt(this.getContentResolver(), Settings.System.POINTER_LOCATION) == 1)
				Settings.System.putInt(this.getContentResolver(), Settings.System.POINTER_LOCATION, 0);
		} catch (SettingNotFoundException e1) {
			e1.printStackTrace();
		}
		
		// 20210216 yhr
		showNotiPopup();	
		
		try {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME+"onCreate // loadGPSReset : " + M_MTX.loadGPSReset());
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NoSuchMethodError e){
			Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME+"NoSuchMethodError");
		}
		
		if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_COMPLETE)
			setContentView(R.layout.activity_base_black);
		else if(NotifyDialog.mCurrentPopup == NotifyDialog.FW_UPDATE_LOADING_POPUP)
			setContentView(R.layout.activity_base_black);
		else if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP)
			setContentView(R.layout.activity_base_black);
		else
		{
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				setContentView(R.layout.activity_base);
			else
				setContentView(R.layout._kia_activity_base);
			
			/* Status Bar GUI */
			setLoadView();
		}
		
		DMB_Manager.init(this);
		
		if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP )
		{
			mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
			requestAudioFocus();

			// Map System Properties  /////////////////////////////////////////////////
			if (!SystemProperties.get(PROPERTIES_NAVI_AUDIO).equals("1"))
				SystemProperties.set(PROPERTIES_NAVI_AUDIO, "1");

			SystemProperties.set(PROPERTIES_NAVI_AUTOINSTALL, "0");
			SystemProperties.set(PROPERTIES_NAVI_GPS, "1");
			SystemProperties.set(PROPERTIES_NAVI_POWER_UI, "0");
			SystemProperties.set(PROPERTIES_NAVI_TPEG, "1"); // TPEG 사용함

			// 191010
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				SystemProperties.set(PROPERTIES_NAVI_CODE, "PU");
			else if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				SystemProperties.set(PROPERTIES_NAVI_CODE, "HR");
			SystemProperties.set(PROPERTIES_NAVI_PLATFORM, "15");
			SystemProperties.set(PROPERTIES_NAVI_GENERATION, "0");
			SystemProperties.set(PROPERTIES_NAVI_ENVIRONMENT, "1");
			SystemProperties.set(PROPERTIES_NAVI_NOTICE_USE, "1");
			///////////////////////////////////////////////////////////////////////////

			// 초기화
			DxbPlayer.init();
			DxbView.init();

			Criteria criteria = new Criteria();
			criteria.setSpeedRequired(true);

			mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			mLocationListener = new SpeedActionListener();
			
			try {
				mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
			} catch (IllegalArgumentException e) {
				// TODO: handle exception
			}
			
			if (DxbPlayer.mChannelManager == null) {
				DxbPlayer.mChannelManager = new ChannelManager(this);
				DxbPlayer.mChannelManager.open();
			}

			if (DxbView.mDB == null)
				DxbView.mDB = new DxbDB_Setting(this).open();

			HP_Manager.mRootMenu = HP_Index.FRAGMENT_LAUNCHER_MAIN;
			HP_Manager.mSubMenu = 0;
			HP_Manager.callFragment(HP_Manager.mRootMenu, HP_Manager.mSubMenu);

			// system time - intent filter 설정
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);

			// 시간 변경에 대한 time receiver
			timeReceiver = new TimeReceiver();
			bIsRegisterReceiver = true;
			registerReceiver(timeReceiver, filter);

			// Surface 초기화
			DxbView.initDMBSurfaceView();
			if(DxbView.mSurfaceView != null)
				DxbView.mSurfaceView.setVisibility(View.VISIBLE);

			// Dialog 생성
			createDefaultDialog(this);
			createScanDialog(this);

			DxbView_Normal.mBootComplete();

			// 시스템과 통신할 Receiver 등록
			registerReceiver();

			// BootComplete App -> System
			Screen_Adjust _ScreenAdjust = new Screen_Adjust();
			Screen_Ratio _ScreenRatio = new Screen_Ratio();
			mSystemTime = new System_Time();
			Setting_Main _SettingMain = new Setting_Main();

			_ScreenAdjust.mBootComplete();
			_ScreenRatio.mBootComplete();
			mSystemTime.mBootComplete();
			_SettingMain.mBootComplete();
			setNaviMuteOnOff(HP_Manager.mNAVIMuteStatus);
			
			// 200214 yhr // 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
			try {
				if (M_MTX.readGPIO((byte) 170) == 1)
				{
					if(HP_Manager.mLastMode == HP_Index.LAST_MODE_NAVI_FULL)
					{
						HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
						HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
					}
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if (bIsProductionProcess == false) {
			// 안전법규 팝업
			Intent popup = null;
			popup = new Intent(this, NotifyDialog.class);
			startService(popup);
		}
	}

	/////////////////////////////////////////////////////////////////////////////////
	// 210122 yhr
	// 부팅 시 자동으로 로그 저장 ( 남양장착장 이슈 확인 용)
//	private class MLogSaveAsyncTask extends AsyncTask<Void, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Void... arg0) {
//			return DEBUG_.createLogFile();
//		}
//
//		@Override
//		public void onPostExecute(Boolean _st) {
//			if (_st) {
//				Toast.makeText( getApplicationContext(), 
//								HP_Manager.mContext.getResources().getString(R.string.info_success_log),
//								Toast.LENGTH_SHORT).show();
//				
//			} else {
//				Toast.makeText( getApplicationContext(), 
//						HP_Manager.mContext.getResources().getString(R.string.info_fail_log),
//						Toast.LENGTH_SHORT).show();
//			}
//		}
//	}
	/////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * 2021.02.16 yhr
	 * 업데이트 모드에 따라 화면에 보여지는 팝업을 설정
	 * 기아에서 현대로 모델이 바뀔경우 업데이트 완료 후 후방카메라 차종 설정
	 */
	private void showNotiPopup() {
		try {

			// 200917 yhr
			if(M_MTX == null)
				M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));

			Log.d(HP_Manager.TAG_LAUNCHER, "onCreate // loadUpdateMode : " + M_MTX.loadUpdateMode());



			if(M_MTX.loadUpdateMode() == UPDATE_FULL_MODE)
			{
				try {
					// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
					if (M_MTX.readGPIO((byte) 170) == 1)
					{
						if(SystemProperties.get("tcc.QB.boot.with").equals("snapshot"))
						{	
							File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
							if(mFW_FileDst.isFile())
							{
								HP_Manager.mUpdateFileFW = mFW_FileDst;
								System_fw_Update _fw = new System_fw_Update(HP_Manager.mUpdateFileFW);
								_fw.mCheckMode();

								NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;
							}
							NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;
						}
						else
							NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;	//업데이트를 위한재부팅 알림 팝업
					}
					else
						NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;	//업데이트를 위한재부팅 알림 팝업
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			else if(M_MTX.loadUpdateMode() == UPDATE_MODE_FW)
			{
				File mFWFile = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);

				if(mFWFile.isFile() && mFWFile.length() == 0)
					mFWFile.delete();

				NotifyDialog.mCurrentPopup = NotifyDialog.FW_UPDATE_LOADING_POPUP;		// FW 업데이트 준비중 팝업
			}
			else if(M_MTX.loadUpdateMode() == UPDATE_MODE_OS)
			{
				try {
					M_MTX.saveUpdateMode(UPDATE_COMPLETE);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;	// 업데이트를 위한 재부팅 알림 팝업 (퀵부트이미지 만들때 출력시킬 팝업)
			}
			else if(M_MTX.loadUpdateMode() == UPDATE_MODE_NONE)
				NotifyDialog.mCurrentPopup = NotifyDialog.TRAFFIC_RULE_POPUP;		// 동의함 팝업
			else if(M_MTX.loadUpdateMode() == UPDATE_COMPLETE)
			{
				try {
					//191122 yhr 후방카메라 차종 설정
					if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					{
						if(M_MTX.loadCarType() == HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1_2T_2W)
						{
							M_MTX.saveCarType(HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W);
							HP_Manager.mVehicleType = M_MTX.loadCarType();
						}
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				deleteCacheFile();
				NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_COMPLETE;
			}
			else
			{
				NotifyDialog.mCurrentPopup = NotifyDialog.TRAFFIC_RULE_POPUP;
				try {
					//191122 yhr
					if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					{
						if(M_MTX.loadCarType() == HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1_2T_2W)
						{
							M_MTX.saveCarType(HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W);
							HP_Manager.mVehicleType = M_MTX.loadCarType();
						}
					}
					M_MTX.saveUpdateMode(UPDATE_MODE_NONE);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}		
	}

	public void deleteCacheFile()
	{
		File mSystemFile = new File(System_Update.SYSTEM_UPDATE_COPY_PATH);
		File mFWFile = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
		if(mSystemFile.isFile())
			mSystemFile.delete();
		
		if(mFWFile.isFile())
			mFWFile.delete();
	}
	
	private BroadcastReceiver mReceiver;

	@Override
	public void onStart() {
		super.onStart();
		Log.i(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onStart()");
		
		eCycle_Life = DXB_LIFE_CYCLE.ON_START;
		if ((DxbPlayer.eState != DxbPlayer.STATE.OPTION_MANUAL_SCAN) && (DxbPlayer.eState != DxbPlayer.STATE.OPTION_MAKE_PRESET)) {
			DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
		}

		if(HP_Manager.mProductionProcess)
		{
			LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 22, 0);
			playDMB();
		}
	}

	private void registerReceiver() {
		final IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction("android.mtxintent.action.RESPONSE");
		intentfilter.addAction("android.intent.action.BOOT_COMPLETED");
		intentfilter.addAction("android.intent.action.MEDIA_MOUNTED");
		intentfilter.addAction("android.intent.action.USER_REAR_CAMERA_STOP");
		intentfilter.addAction("android.intent.action.USER_REAR_CAMERA_START");
		this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (intent.getAction().equals("android.mtxintent.action.RESPONSE")) {
					String result = intent.getStringExtra("RESPONS");
					
					// 190909 yhr
					String init = intent.getStringExtra("INIT");
					if(result != null)
					{
						if (result.equals("OK")) {
							HP_Manager.mCntSearchChannel = DxbPlayer.getScanCount();
							DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
							DxbView_Normal.updateChannelList(0);
							if(DxbPlayer.getScanCount() > 0)
								DxbPlayer.setChannel();
							else
								DxbPlayer.stop();
							
							// 190919
							if(HP_Manager.mWidgetMap == false)
								iconSDCard.setVisibility(View.VISIBLE);
							else
								iconSDCard.setVisibility(View.GONE);
							
							NotifyDialog.mCurrentPopup = -1;
							
							if(Screen_LCD.isAutoIllum)
								SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "auto");
							else
								SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "manual");
							
							HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
//							createVolumeDialog(getInstance());
							HP_Manager.mProductionProcess = false;
							HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
							if(tvStrength.getVisibility() == View.VISIBLE)
								tvStrength.setVisibility(View.GONE);
							
							// 190723 YHR
							Sound_LR_Balance _SoundLR = new Sound_LR_Balance();
							_SoundLR.mBootComplete();
							
							Screen_LCD _screenLCD = new Screen_LCD();
							_screenLCD.mBootComplete();
							
							//200221 yhr
							changeStatusBar(HP_Manager.mCurrentView);
							HP_Manager.mContext.findViewById(R.id.layout_full).setVisibility(View.VISIBLE);
							HP_Manager.mContext.findViewById(R.id.layout_normal).setVisibility(View.VISIBLE);
							HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
							HP_Manager.mContext.findViewById(R.id.surface_view).setVisibility(View.VISIBLE);
							mFragmentArea.setVisibility(View.VISIBLE);
							HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
							HP_Manager.mCallback.onChangeSettingView();
							
							HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
							HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
							HP_Manager.mFragmentSetIndex = HP_Index.FRAGMENT_HIDDEN_MENU;
							
							// Hidden List 화면으로 전환
							FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
							Setting_Hidden_List fragment1 = new Setting_Hidden_List();
							transaction.replace(R.id.setting_fragment_container, fragment1);
							transaction.commitAllowingStateLoss();
							
							HP_Manager.mDMBVideoOnOff = HP_Manager.mPreferences.getDMBVideoOnOff();
							if(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != HP_Manager.mCurrentDMBVol)
								mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HP_Manager.mCurrentDMBVol, 0);
							Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=========================== PDUCTION PROCESS EXIT ============================== // mWidgetMap : " + HP_Manager.mWidgetMap
									+ ", mDMBVideoOnOff  : " + HP_Manager.mDMBVideoOnOff 
									+ ", mfromUserMute : " + DxbView_Normal.mfromUserMute
									+ ", mCurrentDMBVol : " + HP_Manager.mCurrentDMBVol
									+ ", getStreamVolume : " + mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
						}else
							HP_Manager.mProductionProcess = true;
					}
					
					if(init != null)
					{
						if(init.equals("OK"))
						{
							Log.d(HP_Manager.TAG_BROADCAST, "============== SYSTEM INITIALIZE ============");
							if (Setting_Initialize_View.mInitSystem()) 
								HP_Manager.mPreferences.mSystemInitializePreferences();
						}
					}
					
				} else if (intent.getAction().equals("android.intent.action.USER_REAR_CAMERA_STOP")) {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "USER_REAR_CAMERA_STOP : " + isNotifyDialogcheck() + ", currentpopup : " + NotifyDialog.mNotifyDialog.mCurrentPopup);
					
					if(isNotifyDialogcheck() == false)
						return;
					
					if(NotifyDialog.mCurrentPopup == NotifyDialog.REBOOTING_POPUP)
					{
						Handler _handler = new Handler();
						_handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
								pm.reboot(null);
							}
						}, 3000);
					}
					else
					{
						if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
						{
							Screen_LCD _ScreenLCD = new Screen_LCD();
							_ScreenLCD.mBootComplete();
						}
						// R기어 해제 시 10초동안 팝업에 보여주도록 
						if(NotifyDialog.mNotifyDialog != null)
							NotifyDialog.mNotifyDialog.setDismissPopop();
					}
					
				} else if (intent.getAction().equals("android.intent.action.USER_REAR_CAMERA_START")) {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "USER_REAR_CAMERA_START : " + isNotifyDialogcheck());
					
					if(isNotifyDialogcheck() == false)
						return;
					
					mDismissPopup = true;
					
					if(NotifyDialog.mNotifyDialog != null)
						NotifyDialog.mNotifyDialog.setDismissPopop();
				}else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "BOOT_COMPLETED ");
				}
			}
		};
		this.registerReceiver(this.mReceiver, intentfilter);
	}

	private void unregisterReceiver() {
		if (mReceiver != null) {
			this.unregisterReceiver(mReceiver);
			mReceiver = null;
		}
	}

	public boolean mIsAppRunning(Context _context) {
		ActivityManager activityManager = (ActivityManager) _context.getSystemService(Activity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < procInfos.size(); i++) {
			if (procInfos.get(i).processName.equals(GINIAPPInfo.PACKAGE_NAME)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void onResume() {
		
		// 201214 yhr 
		super.onResume();
		
		if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP
			|| NotifyDialog.mCurrentPopup == NotifyDialog.FW_UPDATE_LOADING_POPUP
			|| NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_COMPLETE)
		{
//			super.onResume();   
			return;
		}
		
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume() 1");
		eCycle_Life = DXB_LIFE_CYCLE.ON_RESUME;
		HP_Manager.mShowDMBSignal = false;
		
		if(HP_Manager.mProductionProcess)
			HP_Manager.mCntSearchChannel = 0;

		// Surface설정 및 Player Start
		if (DxbPlayer.mPlayer == null) {
			if (DxbView.mSurfaceHolder != null) {
				DxbPlayer.setListener_Player();
				DxbPlayer.setSurface();
			}
		} else {
			DxbPlayer.mPlayer.setAudioMute(false);
		}

		
		//201214 yhr delay 시간 바뀜 300 --> 500
		Handler mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				playDMB();
			}
		}, 500);
		/////////////////////////////
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume() 2");
		
		DxbView.setState(true, DxbView.eState);

		// DMB On 설정
		DxbPlayer.setDMBOnOff(true);
		DxbView.mHandler_Main.postDelayed(DxbView_Indicator.mRunnable_Signal, HP_Index.TIME_1_SECOND);
		
		// 210114 yhr / 현재 시간 Status Bar에 출력  
		setStatusBarTime();
		
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume() 3");
		
		// 내비 메뉴 팝업에서 설정된 화면으로 전환
		if (NaviMenuDialog.mChangeView == HP_Index.CURRENT_VIEW_DMB) {
			NaviMenuDialog.mChangeView = -1;
			HP_Manager.mCallback.onChangeDMBView();
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume() 4");
			
			//200205 yhr
			HP_Manager.mLastMode = HP_Index.LAST_MODE_DMB_FULL;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
		} else if (NaviMenuDialog.mChangeView == HP_Index.CURRENT_VIEW_HOME) {
			NaviMenuDialog.mChangeView = -1;
			HP_Manager.mCallback.onChangeHome();
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onResume() 5");
			//200205 yhr
			HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
		}
			
		
		if (HP_Manager.mProductionProcess) {
			HP_Manager.mCallback.onChangeDMBView();
			DxbScan.startProductionProcessManual();
			return;
		}
		
		//210122 yhr - 부팅 시 자동으로 로그 저장하도록 설정
//		new MLogSaveAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		/////////////////////////////////////////////////
	}
	
	@Override
	protected void onPause() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onPause()");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onStop()");
		super.onStop();
		if (eCycle_Life == DXB_LIFE_CYCLE.ON_PAUSE)
			eCycle_Life = DXB_LIFE_CYCLE.ON_STOP;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onDestroy()");
		
		eCycle_Life = DXB_LIFE_CYCLE.ON_DESTROY;
		mSdCardState = -1;

		unregisterReceiver();
		unregisterAudioFocus();
		
		destroyDMB();
		System.exit(0);
	}

	// 210114 yhr
	private void setStatusBarTime()
	{
		String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
		String month = HP_Manager.getStatusbarCurrentMonth();
		String day = HP_Manager.getStatusbarCurrentDay();
		txtCurrentTime.setText(time);
		txtCurrentMonth.setText(month);
		txtCurrentDay.setText(day);
		txtCurrentTime.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				int Menuvisible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
				if(Menuvisible == View.VISIBLE)
					DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
				
				// 190701
				if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
					HP_Manager.mChangeHome = true;
				else if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
				{
					HP_Manager.mChangeDMB = true;
					Handler _hadler = new Handler();
					_hadler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							DxbView_Normal.gComponent.llControlPanel.setVisibility(View.GONE);
							if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
								DxbScan.cancel();
							}
						}
					}, 500);
				}
				HP_Manager.mCallback.goSettingMenu(HP_Index.FRAGMENT_SET_SYSTEM, HP_Index.SUB_MENU_LIST_1);
			}
		});
		
		if (HP_Manager.getCurrentAmPm().equals(getString(R.string.am)))
			txtAmPm.setText(getString(R.string.home_time_am));
		else if (HP_Manager.getCurrentAmPm().equals(getString(R.string.pm)))
			txtAmPm.setText(getString(R.string.home_time_pm));

		if (HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			txtAmPm.setVisibility(View.VISIBLE);
		else
			txtAmPm.setVisibility(View.GONE);
	}
	
	public static LauncherMainActivity getInstance() {
		return mInstance;
	}

	public boolean isSdMounted() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	public void playDMB() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB()");
		if (SystemProperties.get(PROPERTIES_TDMB).equals("off"))
			SystemProperties.set(PROPERTIES_TDMB, "on");
		
		if (DxbPlayer.mChannelManager == null) {
			DxbPlayer.mChannelManager = new ChannelManager(this);
			DxbPlayer.mChannelManager.open();
		}

		if (DxbView.mDB == null)
			DxbView.mDB = new DxbDB_Setting(this).open();
		
		// 210114
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 1 ");
		
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		requestAudioFocus();

		eCycle_Life = DXB_LIFE_CYCLE.ON_RESUME;

		// Surface 초기화
		if(mParkingStatus)
			DxbView.initDMBSurfaceView();
		
		// 210114
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 2 ");
				
		if(DxbView.mSurfaceView != null)
			DxbView.mSurfaceView.setVisibility(View.VISIBLE);

		if(mParkingStatus){
			// Surface설정 및 Player Start
			if (DxbPlayer.mPlayer == null) {
				if (DxbView.mSurfaceHolder != null) {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 3");
					DxbPlayer.setListener_Player();
					DxbPlayer.setSurface();
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 4 ");
				}
			} else {
				DxbPlayer.mPlayer.setAudioMute(false);
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 5 ");
			}

			// DMB On 설정
			DxbPlayer.setDMBOnOff(true);
			DxbView.mHandler_Main.postDelayed(DxbView_Indicator.mRunnable_Signal, HP_Index.TIME_1_SECOND);
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 6 ");
		}
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "playDMB() 7 ");
	}

	public void pauseDMB() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "pauseDMB()");
		eCycle_Life = DXB_LIFE_CYCLE.ON_PAUSE;

		engineer_cas = false;

		if (DxbPlayer.eState == DxbPlayer.STATE.SCAN)
			DxbScan.cancel();

		removeDialog(DxbView_Message.DIALOG_SCAN_START);
		
		if(DxbView.mSurfaceView != null)		
			DxbView.mSurfaceView.setVisibility(View.GONE);
		
		DxbPlayer.releaseSurface();
		DxbView_Message.onPause();
		mTopActivity = false;
	}

	/**
	 * DMB 종료
	 */
	private void destroyDMB() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "destroyDMB ");
		eCycle_Life = DXB_LIFE_CYCLE.ON_DESTROY;

		if (mDefaultDialog != null && mDefaultDialog.isShowing()) {
			mDefaultDialog.dismiss();
			mDefaultDialog = null;
		}

		if (mScanDialog != null && mScanDialog.isShowing()) {
			mScanDialog.dismiss();
			mScanDialog = null;
		}

		mLocationManager.removeUpdates(mLocationListener);

		DxbView.mSurfaceView.setVisibility(View.GONE);
		// SystemProperties.set(PROPERTIES_TDMB, "off");

		if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
			DxbPlayer.eState = DxbPlayer.STATE.SCAN_STOP;
			DxbScan.cancel();
			SystemClock.sleep(HP_Index.TIME_1_SECOND / 2);
		}

		DxbPlayer.releaseSurface();
		DMB_Manager.g_Information.cCOMM.isEnable_Video = false;

		mLossTransient = false;
		mTopActivity = false;

		if (DxbPlayer.mPlayer != null) {
			DxbView_Indicator.Signal_invisible();

			if (DxbPlayer.mAudioOnOff == 0) {
				DxbPlayer.setAudioOnOff(1);
			}

			DxbPlayer.mPlayer.stop();
			DxbPlayer.mPlayer.release();
			DxbPlayer.mPlayer = null;
		}

		DxbView_Message.onDestroy();
		DxbView.mProgressState = DxbView.MPROGRESS_READY;

		if (bIsRegisterReceiver) {
			unregisterReceiver(timeReceiver);
			bIsRegisterReceiver = false;
		}
	}

	@Override
	public void onBackPressed() {
		if (cas_state) {
			DxbView_CAS_debug.onBackPressed();
			return;
		}
		super.onBackPressed();
	}

	/**
	 * Status Bar의 GUI
	 */
	@TargetApi(23)
	private void setLoadView() {
		// date
		txtCurrentMonth = (TextView) findViewById(R.id.status_month);
		txtCurrentDay = (TextView) findViewById(R.id.status_day);

		// time
		txtCurrentTime = (TextView) findViewById(R.id.status_time);
		txtAmPm = (TextView) findViewById(R.id.status_ampm);

		if (HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
			txtAmPm.setVisibility(View.VISIBLE);
		else
			txtAmPm.setVisibility(View.GONE);
		
		txtCurrentTime.setOnLongClickListener(mLongClickListener);
		txtCurrentTime.setOnTouchListener(mTouchListener);

		//////////////////////////////////////////////////////////////////////

		// icon
		iconSDCard = (TextView) findViewById(R.id.status_sd_card);
		iconMute = (TextView) findViewById(R.id.status_mute);
		
		///////////////////////////////////////////////
		mFragmentArea = (FrameLayout) findViewById(R.id.frag_container);
		txtTitle = (TextView) findViewById(R.id.txt_home_title);
		btnHome = (TextView) findViewById(R.id.btn_home);
		btnNavi = (TextView) findViewById(R.id.btn_navi);

		btnHome.setOnClickListener(onClickListener);
		btnHome.setOnLongClickListener(mLongClickListener);
		btnHome.setOnTouchListener(mTouchListener);
		
		btnNavi.setOnClickListener(onClickListener);
		btnNavi.setOnLongClickListener(mLongClickListener);
		btnNavi.setOnTouchListener(mTouchListener);
		
		txtSpeed = (TextView) findViewById(R.id.tvSpeed);
		txtSpeed.setVisibility(View.GONE);

		// 채널 정보
		tvStrength = (TextView) findViewById(R.id.tvStrength);
		tvStrength.setVisibility(View.GONE);
	}

	/*
	 * Default Dialog 생성 - 확인/취소 or 확인버튼
	 */
	public void createDefaultDialog(LauncherMainActivity context) {
		if (mDefaultDialog == null) {
			mDefaultDialog = new DefaultDialog(context, iCustomDefaultDialogEventListener);
			mDefaultDialog.setCancelable(false);
			mDefaultDialog.getWindow().getDecorView()
			.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

			mDefaultDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			mDefaultDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		}
	}

	public void createScanDialog(LauncherMainActivity context) {
		if (mScanDialog == null) {
			mScanDialog = new ScanDialog(context, iCustomSCANDialogEventListener);
			
			//190517 yhr (외부터치 시 팝업 종료 안되게)
			mScanDialog.setCancelable(false);
			
			mScanDialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
					| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
					| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

			mScanDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			mScanDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		}
	}

	/**
	 * default 팝업창 이벤트
	 */
	private ICustomDefaultDialogEventListener iCustomDefaultDialogEventListener = new ICustomDefaultDialogEventListener() {

		@SuppressLint("NewApi")
		@Override
		public void customDialogClickEvent(int id) {
			switch (id) {
			case DefaultDialog.BTN_ID_CONFIRM:
				if (mDefaultDialog != null && mDefaultDialog.isShowing())
					mDefaultDialog.dismiss();

				if (mDefaultDialog.mDialogMode == mDefaultDialog.DIALOG_MODE_UPGRADE) {
					if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
						HP_Manager.mBackView = HP_Index.BACK_HOME;
					else if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
						HP_Manager.mBackView = HP_Index.BACK_DMB;
					else if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
						HP_Manager.mBackView = HP_Index.BACK_MAP;
					
					// 'Setting > Upgrade' 메뉴로 이동
					mShowUpdatePopup = false; 
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
					HP_Manager.mCallback.goSettingMenu(HP_Index.FRAGMENT_SET_SYSTEM, HP_Index.SUB_MENU_LIST_2);

				} else if (mDefaultDialog.mDialogMode == mDefaultDialog.DIALOG_MODE_FACTORY_RESET) {
					sendBroadcast(new Intent("android.intent.action.MASTER_CLEAR"));
				}else if(mDefaultDialog.mDialogMode == mDefaultDialog.DIALOG_MODE_INITAILIZE) {
					Handler _handler = new Handler();
					
					if(Setting_Initialize_View.mMenuType == HP_Index.ROOT_MENU_SCREEN)
					{
						NotifyDialog.mCurrentPopup = NotifyDialog.INITIALIZE;
						Intent popup = null;
						popup = new Intent(getInstance(), NotifyDialog.class);
						startService(popup);
						
						_handler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								if (Setting_Initialize_View.screenSettingReset()) 
								{
									HP_Manager.mPreferences.mScreenInitializePreferences();
									Intent popup = null;
									popup = new Intent(getInstance(), NotifyDialog.class);
									stopService(popup);
									
									mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_NOTICE,
											HP_Manager.mContext.getResources().getString(R.string.msg_initialize_sucess));
								}
							}
						}, 3000);
					}
					else if(Setting_Initialize_View.mMenuType == HP_Index.ROOT_MENU_SOUND)
					{
						NotifyDialog.mCurrentPopup = NotifyDialog.INITIALIZE;
						Intent popup = null;
						popup = new Intent(getInstance(), NotifyDialog.class);
						startService(popup);
						
						_handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								if (Setting_Initialize_View.soundSettingReset()) 
								{
									HP_Manager.mPreferences.mSoundInitializePreferences();
									Intent popup = null;
									popup = new Intent(getInstance(), NotifyDialog.class);
									stopService(popup);
									mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_NOTICE,
											HP_Manager.mContext.getResources().getString(R.string.msg_initialize_sucess));
								}
							}
						}, 3000);
					}
					else 
					{
						NotifyDialog.mCurrentPopup = NotifyDialog.INITIALIZE;
						if (Setting_Initialize_View.systemSettingReset()) 
						{
							HP_Manager.mPreferences.mSystemInitializePreferences();
						}
						Intent popup = null;
						popup = new Intent(getInstance(), NotifyDialog.class);
						startService(popup);
						
						_handler.postDelayed(new Runnable() {

							@Override
							public void run() {
								HP_Manager.mPreferences.mInitializePreferences();

								Intent popup = null;
								popup = new Intent(getInstance(), NotifyDialog.class);
								stopService(popup);
								
								mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_NOTICE, HP_Manager.mContext.getResources().getString(R.string.msg_initialize_sucess));
							}
						}, 3000);
					}
				}else if(mDefaultDialog.mDialogMode == mDefaultDialog.DIALOG_MODE_CHANGE_VEHICLE){
					try {
						
						LauncherMainActivity.getInstance().M_MTX.saveCarType(HP_Manager.mVehicleType);
						PowerManager pm = (PowerManager)HP_Manager.mContext.getSystemService(Context.POWER_SERVICE);
						pm.reboot("");
						
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				break;
			case DefaultDialog.BTN_ID_CANCLE:
				if (mDefaultDialog != null && mDefaultDialog.isShowing()) {
					// 19.01.23 yhr --> 업데이트 화면에서 업데이트 팝업창 이벤트가 발생되면 화면 갱신
					if (HP_Manager.mRootMenu == HP_Index.FRAGMENT_SET_SYSTEM && HP_Manager.mSubMenu == HP_Index.SUB_MENU_LIST_2)
					{
						System_Update _update = new System_Update();
						_update.changeUpdateMode();
					}
					
					if(mDefaultDialog.mDialogMode == mDefaultDialog.DIALOG_MODE_CHANGE_VEHICLE)
					{
						try {
							HP_Manager.mVehicleType = LauncherMainActivity.M_MTX.loadCarType();
							Setting_Main.setVehicleCheckBox(HP_Manager.mVehicleType);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					mShowUpdatePopup = false;
					mDefaultDialog.dismiss();
				}
				break;
			case DefaultDialog.BTN_ID_AGREE:
				if (mDefaultDialog != null && mDefaultDialog.isShowing())
					mDefaultDialog.dismiss();
				
				if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_FILE_COPY && HP_Manager.mWidgetMap)
					HP_Manager.mCallback.onRebooting();
				break;
			default:
				break;
			}
		}
	};

	private ICustomSCANDialogEventListener iCustomSCANDialogEventListener = new ICustomSCANDialogEventListener() {

		@Override
		public void customDialogClickEvent(int id) {

			if (mScanDialog.isShowing())
				mScanDialog.dismiss();

			switch (id) {
			case DefaultDialog.BTN_ID_CONFIRM:
				engineer_cas = false;
				if (LauncherMainActivity.tvStrength.getVisibility() == View.VISIBLE)
					LauncherMainActivity.tvStrength.setVisibility(View.GONE);

				if( DMB_Manager.mContext.findViewById(R.id.layout_channel_list).getVisibility() == View.VISIBLE)
					DMB_Manager.mContext.findViewById(R.id.layout_channel_list).setVisibility(View.GONE);
				
				DxbScan.startFull();
				break;
			case DefaultDialog.BTN_ID_CANCLE:
				engineer_cas = true;
				DxbView_Normal.ResetDisplayFull();
				break;
			default:
				break;
			}
		}
	};
	

	/**
	 * 브로트 캐스트 리시버를 상속받은 클래스 1분마다 리시브 됨 ==> 시계 업데이트
	 */
	class TimeReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
				
				HP_Manager.mSystemDate = Calendar.getInstance();
				String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
				String month = HP_Manager.getStatusbarCurrentMonth();
				String day = HP_Manager.getStatusbarCurrentDay();
				txtCurrentTime.setText(time);
				txtCurrentMonth.setText(month);
				txtCurrentDay.setText(day);
				HP_Manager.getCurrentAmPm();
				if (CurrentTime.mAmPm == HP_Index.TIME_AM)
					txtAmPm.setText("AM");
				else
					txtAmPm.setText("PM");

				if (HP_Manager.mViewMode == HP_Index.CLOCK_WIDGET_MODE)
					Clock_Widget.updateTime();
				
				if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SETTING)
				{
					if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM && HP_Manager.mSubMenu == HP_Index.SYSTEM_MENU_TIME_SET)
					{
						//190703 yhr > 시스템 설정의 시간메뉴화면일 경우 시간 메뉴의 시간정보도 함께 업데이트
						if(mSystemTime != null)
							mSystemTime.setChangeView();
						else
						{
							mSystemTime = new System_Time();
							mSystemTime.setChangeView();
						}
					}
				}
			} else if (intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {
				
				String time = HP_Manager.getCurrentHour() + ":" + HP_Manager.getCurrentMin();
				String month = HP_Manager.getStatusbarCurrentMonth();
				String day = HP_Manager.getStatusbarCurrentDay();

				if (CurrentTime.mAmPm == HP_Index.TIME_AM)
					txtAmPm.setText("AM");
				else
					txtAmPm.setText("PM");
				
				txtCurrentTime.setText(time);
				txtCurrentMonth.setText(month);
				txtCurrentDay.setText(day);

				Clock_Widget.updateTime();
			}

			if (HP_Manager.mSetHour == HP_Index.TIME_SET_12_HOUR)
				txtAmPm.setVisibility(View.VISIBLE);
			else
				txtAmPm.setVisibility(View.GONE);
		}
	}

	/**
	 * Satatus Bar의 버튼 클릭 이벤트
	 */
	private View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			
			if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SETTING) // 3
			{
				if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SCREEN) // 0
					HP_Manager.mPreferences.mSavePreferenceScreen();
				else if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SOUND) // 1
					HP_Manager.mPreferences.mSavePreferenceSound();
				else if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM) //2
					HP_Manager.mPreferences.mSavePreferenceSystem();
				
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
			}
			
			int Menuvisible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
			if(Menuvisible == View.VISIBLE)
				DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
			
			switch (id) {
			case R.id.btn_home:
				HP_Manager.mChangeDMB = false;
				if (DxbPlayer.eState == DxbPlayer.STATE.SCAN)
					DxbScan.cancel();

				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
					break;

				HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
				changeStatusBar(HP_Manager.mCurrentView);

				//200204 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				
				HP_Manager.mCallback.onChangeHome();
				break;
			case R.id.btn_navi:
				HP_Manager.mChangeDMB = false;
				if (DxbPlayer.eState == DxbPlayer.STATE.SCAN)
					DxbScan.cancel();

				if (!HP_Manager.mWidgetMap) {
					mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.memory_card_check));
					break;
				}

				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
					break;

				HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_MAP;

				//200204 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_NAVI_FULL;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  mStartNavi 1");
				mStartNavi(false);
				break;
			}
		}
	};

	static OnLongClickListener mLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			int id = v.getId();
			switch(id)
			{
			case R.id.btn_home:
			case R.id.btn_navi:
			case R.id.status_time:
				DxbView_Normal.ClearDisplayFull();
				break;
			}
			return false;
		}
	};
	
	private static OnTouchListener mTouchListener = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent me) {
			if(me.getAction() == MotionEvent.ACTION_UP)
			{
				int id = v.getId();
				switch(id)
				{
				case R.id.btn_home:
				case R.id.btn_navi:
				case R.id.status_time:
					DxbView_Normal.ResetDisplayFull();
					break;
				}
			}
			return false;
		}
	};

	/**
	 * 화면에 따라 Status Bar의 UI가 바뀜
	 * 
	 * @param view
	 */
	public void changeStatusBar(int view) {
		if (view == HP_Index.CURRENT_VIEW_HOME) {
			txtTitle.setVisibility(View.GONE);
			btnHome.setVisibility(View.GONE);
			btnNavi.setVisibility(View.GONE);
		} else {
			if (view == HP_Index.CURRENT_VIEW_MAP)
				return;

			txtTitle.setVisibility(View.GONE);
			btnHome.setVisibility(View.VISIBLE);
			btnNavi.setVisibility(View.VISIBLE);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////// Audio Manager
	/////////////////////////////////////////////////////////////////////////////////////////////////////// //////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////

	private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		@Override
		public void onAudioFocusChange(int audiofocus) {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  onAudioFocusChange(" + audiofocus + ")");
			
			switch (audiofocus) {
			case AudioManager.AUDIOFOCUS_LOSS:
				// unregisterAudioFocus();
				// destroyDMB();
				// finish();
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
				mLossTransient = true;
				if (eCycle_Life == DXB_LIFE_CYCLE.ON_RESUME)
					mTopActivity = true;

				if (DxbPlayer.mAudioOnOff == DxbPlayer._ON_) {
					DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
				}
				break;
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				mLossTransient = true;
				if (eCycle_Life == DXB_LIFE_CYCLE.ON_RESUME)
					mTopActivity = true;

				if (DxbPlayer.mAudioOnOff == DxbPlayer._ON_) {
					DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
				}
				break;
			case AudioManager.AUDIOFOCUS_GAIN:
				if (mLossTransient) {
					if (DxbPlayer.mAudioOnOff == DxbPlayer._OFF_) {
						DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
					}
					if (mTopActivity) {
						mTopActivity = false;
						Intent intent = new Intent(DMB_Manager.mContext, LauncherMainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  startActivity 1");
						startActivity(intent);
					}
				}
				mTopActivity = false;
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
				if (mLossTransient) {
					if (DxbPlayer.mAudioOnOff == DxbPlayer._OFF_) {
						DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
					}
					if (mTopActivity) {
						mTopActivity = false;

						Intent intent = new Intent(DMB_Manager.mContext, LauncherMainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  startActivity 2");
						startActivity(intent);
					}
				}
				mTopActivity = false;
				break;
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:
				if (mLossTransient) {
					if (DxbPlayer.mAudioOnOff == DxbPlayer._OFF_) {
						DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
					}
					if (mTopActivity) {
						mTopActivity = false;

						Intent intent = new Intent(DMB_Manager.mContext, LauncherMainActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
						
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  startActivity 3");
						startActivity(intent);
					}
				}
				mTopActivity = false;
				break;
			}
		}
	};

	/**
	 * Request Audio Focus
	 */
	private void requestAudioFocus() {
		mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
		mIsAudioGain = true;
	}

	/**
	 * Unregister Audio Focus
	 */
	private void unregisterAudioFocus() {
		if (mIsAudioGain) {
			mAudioManager.unregisterAudioFocusListener(mOnAudioFocusChangeListener);
			mIsAudioGain = false;
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////// Dialog
	/////////////////////////////////////////////////////////////////////////////////////////////////////// ///////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	protected Dialog onCreateDialog(int id) {
		DxbView_Normal.ClearDisplayFull();

		// scan 중
		if (id == DxbView_Message.DIALOG_SCAN) {
//			if (mVolumeDialog.isShowing())
//				mVolumeDialog.dismissVolumeDialog();
		}

		// 검색 팝업
		else if (id == DxbView_Message.DIALOG_SCAN_START) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			if (HP_Manager.mProductionProcess) {
				alertDialog.setTitle("Channel scan");
				alertDialog.setMessage("Do you want to search for channels?");
				alertDialog.setIcon(R.drawable.icon_search);
				alertDialog.setCancelable(true);
				alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						engineer_cas = false;
						if(HP_Manager.mProductionProcess == false)
							DxbScan.startFull();
						else
							DxbScan.startProductionProcessManual();
					}
				});
				alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						engineer_cas = true;
						DxbView_Normal.ResetDisplayFull();
						DxbView_Normal.gComponent.btnProdeuctionSCAN.setEnabled(true);
						
					}
				});
			} else {
				alertDialog.setTitle(getResources().getString(R.string.scaning_channel_title));
				alertDialog.setMessage(getResources().getString(R.string.scan_start_message));
				alertDialog.setIcon(R.drawable.icon_search);
				alertDialog.setCancelable(true);
				alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						engineer_cas = false;
						if(HP_Manager.mProductionProcess == false)
							DxbScan.startFull();
						else
							DxbScan.startProductionProcessManual();
					}
				});
				alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						engineer_cas = true;
						DxbView_Normal.ResetDisplayFull();
					}
				});
			}
			mScanStartDialog = alertDialog.create();
			return mScanStartDialog;
		} else if (id == DxbView_Message.DIALOG_SCAN_FAIL) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			alertDialog.setTitle(getResources().getString(R.string.scaning_channel_title));
			alertDialog.setMessage(getResources().getString(R.string.scan_fail_message));

			alertDialog.setIcon(R.drawable.icon_search);
			alertDialog.setCancelable(true);
			alertDialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					engineer_cas = false;
					DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
					if(HP_Manager.mProductionProcess == false)
						DxbScan.startFull();
					else
						DxbScan.startProductionProcessManual();
				}
			});
			alertDialog.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					engineer_cas = true;
					DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
					DxbPlayer.mPlayer.searchCancel();
					DxbView_Normal.ResetDisplayFull();
				}
			});
			mScanStartDialog = alertDialog.create();
			return mScanStartDialog;
		}
		return null;
	}

	public String getFileVersion(File apk) throws ZipException, IOException {
		ZipFile zip = null;
		InputStream in = null;
		String ver = "없음";

		for (int i = 0; i < 2; i++) {
			try {
				if (!ver.equals("없음"))
					break;

				zip = new ZipFile(apk);
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "zip : " + zip.getName());
				ZipEntry entry = zip.getEntry("system/etc/version");
				if (entry != null) {
					in = zip.getInputStream(entry);
					BufferedReader r = new BufferedReader(new InputStreamReader(in));
					ver = r.readLine();
				}
				else
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "entry is null...");
			} catch (Exception e) {
				if (zip != null) {
					zip.close();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}

			finally {
				try {
					if (zip != null) {
						zip.close();
					}
				} catch (IOException e) {
				}
			}
		}
		return ver;
	}

	/**
	 * HP Manager와 통신
	 */
	private onEventListener mEventListener = new onEventListener() {

		/**
		 * DBM 화면으로 전환
		 */
		@Override
		public void onChangeDMBView() {
			HP_Manager.mRootMenu = HP_Index.FRAGMENT_DMB_MAIN;
			HP_Manager.mSubMenu = 0;
			
			Fragment mFragment = HP_Manager.mContext.getFragmentManager().findFragmentById(R.id.frag_container);
			FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
			if (mFragment != null)
				transaction.remove(mFragment).commit();

			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_DMB;
			changeStatusBar(HP_Manager.mCurrentView);
			mFragmentArea.setVisibility(View.GONE);
			DxbView.setState(true, DxbView.STATE.NORMAL_VIEW);
			
			// 190612 yhr -> 셋팅으로 이동 후 해당 버튼위치를 터치하면 Beep음 출력되는 현상을 막기위해 추가
			DxbView_Normal.setVisibleTitleBarBtn(true);
			
			if(SystemProperties.get(LauncherMainActivity.PROPERTIES_CAS).equals("true"))
			{
				LauncherMainActivity.engineer_cas = true;
				DxbView_Normal.mHandler.sendEmptyMessage(DxbView_Normal.MSG_INSERT_CAS_MODE);
			}
			else
			{
				//200203 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_DMB_FULL;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
			}
		}

		/**
		 * Launcher에서 채널변경 버튼을 누를 경우
		 * 
		 * @param ch - Channel Prev or Next
		 */
		@Override
		public void onChangeChannel(int ch) {
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			DxbView.changeChannel(ch);
		}

		/**
		 * Setting Main화면으로 전환
		 */
		@Override
		public void onChangeSettingView() {
			if(mFragmentArea.getVisibility() != View.VISIBLE)
				mFragmentArea.setVisibility(View.VISIBLE);
			
			exitPopup();
			
			//200203 yhr
			HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
			
			HP_Manager.mRootMenu = HP_Index.FRAGMENT_SETTING_MAIN;
			HP_Manager.mSubMenu = 0;
			HP_Manager.callFragment(HP_Manager.mRootMenu, HP_Manager.mSubMenu);
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
			DxbView_Normal.ClearDisplayFull();
		}

		/**
		 * Mute 상태에따라 Status Bar의 Mute Icon을 변경
		 * 
		 * @param state : mute on/off
		 * @param visible : mute icon 표출여부
		 * 
		 */
		@SuppressLint("NewApi")
		@Override
		public void onChangeDMBMuteState(int state, boolean visible) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + 
					"@@ onChangeDMBMuteState - state : " + state + ", visible : " + visible 
					+ ", mNaviSoundStart : " + HP_Manager.mNaviSoundStart 
					+ ", System : " + HP_Manager.mSystemMuteStatus 
					+ ", navi : " + HP_Manager.mNAVIMuteStatus 
					+ ", mCurrentDMBVol : " + HP_Manager.mCurrentDMBVol
					+ ", mCurrentView : " + HP_Manager.mCurrentView);

			HP_Manager.mDMBMuteStatus = state;

			if (state == HP_Index.DMB_SOUND_MUTE) {
				if (visible) {
					if (HP_Manager.mSystemMuteStatus == HP_Index.SYSTEM_SOUND_MUTE) {
						if (HP_Manager.mWidgetMap)
							iconMute.setBackground(	HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
						else {
							if (DxbPlayer.getScanCount() == 0)
								iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
							else
								iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
						}
					} else {
						if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE) {
							HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_MUTE;
							iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
						} else
							iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
					}
					iconMute.setVisibility(View.VISIBLE);
				}
				DxbView_Normal.updateVolume(0);
				DxbView.closeAudioOut();
			} else {
				if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE) {
					if (HP_Manager.mWidgetMap) {
						iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
						iconMute.setVisibility(View.VISIBLE);
					} else
						iconMute.setVisibility(View.INVISIBLE);
				} else
					iconMute.setVisibility(View.INVISIBLE);
				
				int currentVol = HP_Manager.mCurrentDMBVol;
				DxbView_Normal.updateVolume(currentVol);
				if(HP_Manager.mCurrentDMBVol > 0 && HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
					DxbView.startAudioOut();
			}
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
		}

		@Override
		public void goSettingMenu(int rootMenu, int subMenu) {
			exitPopup();
			
			if (rootMenu == HP_Index.FRAGMENT_SET_SCREEN) {
				switch (subMenu) {
				case HP_Index.SUB_MENU_LIST_0:
				case HP_Index.SUB_MENU_LIST_3:
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
					DxbView_Normal.ClearDisplayFull();
					HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
					mFragmentArea.setVisibility(View.VISIBLE);

					break;
				case HP_Index.SUB_MENU_LIST_1:
				case HP_Index.SUB_MENU_LIST_2:
				case HP_Index.SUB_MENU_LIST_4:
					HP_Manager.mBackView = HP_Manager.mCurrentView;
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
					break;

				default:
					break;
				}
			} else if (rootMenu == HP_Index.FRAGMENT_SET_SYSTEM) {
				HP_Manager.mBackView = HP_Manager.mCurrentView;
				HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
				
				// 업데이트 화면으로 전환
				if (subMenu == HP_Index.SUB_MENU_LIST_2 || subMenu == HP_Index.SUB_MENU_LIST_1) {
					DxbView_Normal.ClearDisplayFull();
					HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
					mFragmentArea.setVisibility(View.VISIBLE);
				}
			} else {
				HP_Manager.mBackView = HP_Manager.mCurrentView;
				HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
			}
			
			HP_Manager.callFragment(rootMenu, subMenu);
			
			// 190612 yhr -> 셋팅으로 이동 후 해당 버튼위치를 터치하면 Beep음 출력되는 현상을 막기위해 추가
			DxbView_Normal.setVisibleTitleBarBtn(false);
			
			//200203 yhr
			HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
		}

		@Override
		public void onChangeScreenSaverFullMode() {
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SCREEN_SAVER;
			Intent intent = new Intent(HP_Manager.mContext, ScreenSaverActivity.class);
			startActivity(intent);
			exitPopup();
		}

		@Override
		public void onChangeHome() {
			HP_Manager.mRootMenu = HP_Index.FRAGMENT_LAUNCHER_MAIN;
			HP_Manager.mSubMenu = 0;
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			changeStatusBar(HP_Manager.mCurrentView);

			HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
			HP_Manager.mContext.findViewById(R.id.surface_view).setVisibility(View.VISIBLE);
			mFragmentArea.setVisibility(View.VISIBLE);
			
			if(mParkingStatus)
				playDMB();
			
			exitPopup();
			
			Launcher_Main.reflashWidget1();
			HP_Manager.callFragment(HP_Manager.mRootMenu, HP_Manager.mSubMenu);
			
			//200204 yhr
			HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
		}

		@Override
		public void onChangeSDCardState(Intent intent) {
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "##################### mCurrentPopup : " + NotifyDialog.mCurrentPopup);
			if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_COMPLETE)
			{
				setGPSReset(intent);
				return;
			}
				
			try {
				if(M_MTX.loadUpdateMode() == UPDATE_MODE_FW)
					return;
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			
			try {
				if(M_MTX.loadUpdateMode() == UPDATE_MODE_FW || M_MTX.loadUpdateMode() == UPDATE_FULL_MODE)
				{
					if(intent.getAction().equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED))
					{
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "checkSDCardFile 1");
						checkSDCardFile(intent.getDataString());
						return;
					}
				}

				//0715
				if(M_MTX.loadUpdateMode() == UPDATE_MODE_OS)
					return;
				
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
			String action = intent.getAction();
			String path = intent.getDataString();

			// 마운트 된 상태
			if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED)) {
				if(mDefaultDialog != null && mDefaultDialog.isShowing())
					mDefaultDialog.dismiss();
				
				if(mSdCardState == SD_CARD_UNMOUNT_REBOOT)
					return;
				
				if(DxbPlayer.eState == DxbPlayer.STATE.SCAN)
					DxbScan.cancel();
				
				
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "##################### ACTION_MEDIA_MOUNTED 1");
				mFactoryReset = false;
				mSdCardState = SD_CARD_MOUNT;
				mHandlerSDCheck.removeCallbacks(mRunnableUnmountCheck);
				if(iconSDCard != null)
					iconSDCard.setVisibility(View.GONE);

				if (NotifyDialog.mCurrentPopup != NotifyDialog.TRAFFIC_RULE_POPUP) {
					try {
						if(M_MTX.loadUpdateMode() == LauncherMainActivity.UPDATE_MODE_NONE)
							NotifyDialog.mCurrentPopup = NotifyDialog.SDCARD_LOAD_POPUP;
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					if (isServiceRunningCheck()) {
						if (NotifyDialog.mNotifyDialog != null) {
							NotifyDialog.mNotifyDialog.exitNotifyPopup();
						}
					}
					
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "##################### ACTION_MEDIA_MOUNTED 2");
					Intent popup = null;
					popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
					startService(popup);
				}
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "checkSDCardFile 2");
				checkSDCardFile(path);
			} else if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_UNMOUNTED)) {
				try {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onChangeSDCardState - ACTION_MEDIA_UNMOUNTED 1 // readGPIO : " + M_MTX.readGPIO((byte) 170)
														 + ", mCurrentPopup : " + NotifyDialog.mCurrentPopup
														 + ", getTopActivity : " + getTopActivity());
			
					// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
					if (M_MTX.readGPIO((byte) 170) == 0 && HP_Manager.mIsRebooting == false)
						return;
					
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				try {
					if(M_MTX.loadUpdateMode() != UPDATE_MODE_NONE)
					{
						if(M_MTX.loadUpdateMode() == UPDATE_MODE_FW)
						{
							File mFWFile = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
							Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onChangeSDCardState - ACTION_MEDIA_UNMOUNTED 3 // loadUpdateMode : " + M_MTX.loadUpdateMode() + ", mFWFile.isFile() : " + mFWFile.isFile());
							if(mFWFile.isFile())
								return;
							
							System_fw_Update.mFWUpdateState = System_fw_Update.FW_UPDATE_MODE_STOP;
							
							// 메모리카드 해제 팝업
							NotifyDialog.mCurrentPopup = NotifyDialog.SD_UNMOUNT_FW_UPDATE_FAIL_POPUP;
							Intent popup = null;
							popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
							startService(popup);
						}
						return;
					}
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				
				if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
					HP_Manager.mIsRebooting = false;
				
				try {
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "onChangeSDCardState - ACTION_MEDIA_UNMOUNTED 4 // loadUpdateMode : " + M_MTX.loadUpdateMode());
					if(M_MTX.loadUpdateMode() == UPDATE_FULL_MODE)
					{
						Log.d(HP_Manager.TAG_POPUP, "saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE) 8");
						M_MTX.saveUpdateMode(UPDATE_MODE_NONE);
					}
					
				} catch (RemoteException e1) {
					e1.printStackTrace();
				}
				
				mExistFWUpgradeFile = false;
				mExistMapDataFile = false;
				mExistOSUpgradeFile = false;
				mOSUpgradeMode = false;
				mFWUpgradeMode = false;
				mFactoryReset = true;
				
				if (TpegService.mServicedBind) {
					TpegService.unbindService(getApplicationContext());
					TpegService.stopService(getApplicationContext());
				}
				mHandlerSDCheck.postDelayed(mRunnableUnmountCheck, DELAY_SD_CHECK);
			}
		}

		@Override
		public void onPlayDMB() {
			playDMB();
			DxbView_Normal.ClearDisplayFull();
			HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
		}

		@Override
		public void onChangeMAPView() {
			exitPopup();
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_MAP;
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  mStartNavi 2");
			mStartNavi(false);
		}

		@Override
		public void onNaviGuidance(int state) {
			if (HP_Manager.mIsNaviGuidance == false)	
				return;
			
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE )
				return;

			if (state == HP_Index.NAVI_SOUND_UNMUTE) {
				DxbView.closeAudioOut();
			} else {
				DxbView.startAudioOut();
				if (LauncherMainActivity.mCurrentVolume >= 0) 
				{
					mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume, 0);
					LauncherMainActivity.mCurrentVolume = -1;
				}
			}
		}

		@Override
		public void oncheckSDCardState() {
			if (mSdCardState == SD_CARD_MOUNT)
				return;

			String status = Environment.getExternalStorageState();
			if (status.equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
				mSdCardState = SD_CARD_MOUNT;
				String path = SDCARD_PATH;

				if (path.contains("storage")) {
					path = path.substring(path.indexOf("storage"));
					File[] files = new File(path + File.separator).listFiles();

					if (files == null || files.length == 0)
						return;

					for (File f : files) {
						if (f.getName().contains("GNA")) // MAP Data File
						{
							mExistMapDataFile = true;

							if (HP_Manager.iNaviService == null )
								Launcher_Main.startServiceBind();

							Launcher_Main.reflashWidget1();
						}
					}
				}
			}
		}

		/**
		 * SD Card 제거 시 리부팅
		 */
		@Override
		public void onRebooting() {
			
			if(HP_Manager.mProductionProcess)
				return;
			
			Log.d(HP_Manager.TAG_LAUNCHER, "onRebooting");
//			if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
			{
				Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				stopService(intent);
			}
			
			HP_Manager.mIsRebooting = true;
			mSdCardState = SD_CARD_UNMOUNT_REBOOT;
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_WIDGET_ONOFF, MAP_INFO.NAVI_WIDGET_OFF, 0);
			NotifyDialog.mCurrentPopup = NotifyDialog.REBOOTING_POPUP;
			
			//200204 yhr
			if(HP_Manager.mLastMode == HP_Index.LAST_MODE_NAVI_FULL)
			{
				HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
			}
			
			// 메모리카드 해제 팝업
			Intent popup = null;
			popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
			startService(popup);

			// 200921
			if(iconSDCard != null)
				iconSDCard.setVisibility(View.VISIBLE);
			
			// 200921 yhr
			if(DxbPlayer.mPlayer != null)
				DxbPlayer.mPlayer.stop();
			
			HP_Manager.mWidgetMap = false;
			Launcher_Main.reflashWidget1();
			
			// navi aidl 서비스 해제
			try{
				Launcher_Main.stopServiceBind();
			}catch(IllegalArgumentException e){			

			}
						
			try {
				if(M_MTX != null)
				{
					if(M_MTX.getStateReverse())
						return;
				}
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}
			
			Handler _handler = new Handler();
			_handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
			}, 3000);
		}
	};
	
	private void setGPSReset(Intent intent)
	{
		String action = intent.getAction();
		String path = intent.getDataString();
		if (action.equalsIgnoreCase(Intent.ACTION_MEDIA_MOUNTED)) 
		{
			path = path.substring(path.indexOf("storage"));
			File[] files = new File(path + File.separator).listFiles();
			File[] systemfiles = new File(path + File.separator + "SYSTEM").listFiles();

			if (files == null || files.length == 0) {
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "files is null....");
				return;
			}

			for (File f : files) {
				if(f.getName().equals("SYSTEM"))
				{
					if (systemfiles == null || systemfiles.length == 0) {
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "systemfiles is null....");
						break;
					}

					for (File sf : systemfiles) {
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "1 setGPSReset / getName : " + sf.getName());
						if(sf.getName().contains(".zip"))
						{
							try {
								if(sf.getName().contains("1.0.0"))
								{
									Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "1 setGPSReset / loadGPSReset : " + M_MTX.loadGPSReset() + ", version : " + sf.getName());
//									M_MTX.saveGPSReset(0);
								}
								else
								{
									if(M_MTX.loadGPSReset() == 0)
									{
										M_MTX.saveGPSReset(1);
										Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "1 setGPSReset / saveGPSReset 1 - " + sf.getName());
									}
								}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchMethodError e) {
								// TODO: handle exception
							}
							Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "1 setGPSReset / strNewOSVersion :  " +SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, "없음"));
						}
					}
				}
				else
				{
					Log.w(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 setGPSReset / getName : " + f.getName());
					if(f.getName().contains(".zip")) // os 업데이트 파일 확장자
					{ 
						try {
							if(f.getName().contains("1.0.0"))
							{
								Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 setGPSReset / loadGPSReset : " + M_MTX.loadGPSReset());
//								M_MTX.saveGPSReset(0);
							}
							else
							{
								if(M_MTX.loadGPSReset() == 0)
								{
									M_MTX.saveGPSReset(1);
									Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 setGPSReset / saveGPSReset 1 - " + f.getName());
								}
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodError e) {
							// TODO: handle exception
						}
						
						Log.w(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 setGPSReset / strNewOSVersion :  " +SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, "없음"));
					}
				}
			}
		}
	}

	public static String getTopActivity() {
		if (HP_Manager.mContext == null) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "HP_Manager.mContext is null");
			return "";
		}

		ActivityManager am = (ActivityManager) HP_Manager.mContext.getSystemService("activity");
		List<ActivityManager.RunningTaskInfo> Info = am.getRunningTasks(1);
		ComponentName topActivity = Info.get(0).topActivity;
		String topactivityname = topActivity.getPackageName();
		return topactivityname;
	}

	public static final String ACTION_PRODUCTION_PROGRESS_RESULT = "android.hmsintent.action.PRODUCTION_PROGRESS_RESULT";
	public static final String ACTION_RESULT = "result";
	public void exitProductionProgressDMB(String str_value) {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME+"exitProductionProgressDMB");
		HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
		HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;


		Intent _intent = new Intent(ACTION_PRODUCTION_PROGRESS_RESULT);
		_intent.putExtra(ACTION_RESULT, str_value);
		sendBroadcast(_intent);

		// 생산공정 프로그램 실행
		Intent intent = HP_Manager.mContext.getPackageManager().getLaunchIntentForPackage("com.mobilusauto.app.productionprocess");
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		HP_Manager.mContext.startActivity(intent);
	}

	public void exitPopup() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME+"exitPopup");
		if (mDefaultDialog.isShowing())
		{
			if(HP_Manager.mLastMode != HP_Index.LAST_MODE_NAVI_FULL && !mShowUpdatePopup)
				mDefaultDialog.dismiss();
		}

		if (mScanDialog.isShowing())
			mScanDialog.dismiss();
	}

	/**
	 * SD Card 파일 체크
	 * @param path
	 */
	public static final String UBX_FOLDER_NAME = "UBX";
	private void checkSDCardFile(String path) {
		Log.i(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "checkSDCardFile");
		if(path.contains("storage")) 
		{
			path = path.substring(path.indexOf("storage"));
			File[] files = new File(path + File.separator).listFiles();
			File[] systemfiles = new File(path + File.separator + "SYSTEM").listFiles();
			File[] apkfiles = new File(path + File.separator + "apk").listFiles();
			
			// 20200611 yhr - UBX 폴더 체크
			String _ubx_path = path + File.separator + UBX_FOLDER_NAME;
			File _ubx_dir = new File(_ubx_path);
			
			if(_ubx_dir.isDirectory() == false)
			{
				_ubx_dir.mkdirs();  
			}
			/////////////////////////////////////////////////////
			
			if (files == null || files.length == 0) {
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "files is null....");
				return;
			}
			
			for (File f : files) {
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "############# Upgrade !!! checkSDCardFile / File Name : " + f.getName()); 
				if (f.getName().equals("GNA")) // MAP Data File
					mExistMapDataFile = true;
				else if(f.getName().equals("forceUpdate"))
					mforceUpdate = true;
				else if(f.getName().equals("factoryReset"))
					mFactoryReset = true;
				else if(f.getName().equals("SYSTEM"))
				{
					if (systemfiles == null || systemfiles.length == 0) {
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "systemfiles is null....");
						break;
					}
					
					for (File sf : systemfiles) {
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "############# Upgrade !!! checkSDCardFile // SYSTEM : " + sf.getName());
						if(sf.getName().contains(".zip"))
						{
							mOSUpgradeMode = false;
							checkSystemUpdateFile(sf);
						}
						else if(sf.getName().contains(".bin"))
						{
							mFWUpgradeMode = false;
							checkFWUpdateFile(sf);
						}
					}
				}
				else if(f.getName().equals("capture-screen.txt"))
					HP_Manager.mCaptureScreen = true;
				else
				{
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "############# Upgrade !!! checkSDCardFile // " + f.getName() + ", mforceUpdate : " + mforceUpdate);
					if(f.getName().contains(".zip")) // os 업데이트 파일 확장자
						checkSystemUpdateFile(f);
					else if(f.getName().contains(".bin")) // fw 업데이트 파일 확장자
						checkFWUpdateFile(f);
				}
			}
			
			try {
				if(M_MTX.loadUpdateMode() == UPDATE_FULL_MODE)
				{
					if (mFWUpgradeMode) {
						new Handler().postDelayed(new Runnable() {
							
							@Override
							public void run() {
								File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
								HP_Manager.mUpdateFileFW = mFW_FileDst;
								Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "System_fw_Update 2 // " + HP_Manager.mUpdateFileFW.toString());
								System_fw_Update _fw = new System_fw_Update(HP_Manager.mUpdateFileFW);
								_fw.mCheckMode();
							}
						}, 3000);
					}
					return;
				}
				
				if(M_MTX.loadUpdateMode() != UPDATE_MODE_NONE )
					mExistMapDataFile = false;
		
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(mforceUpdate)
				forceUpdateMode();
			
			if (mFWUpgradeMode || mExistMapDataFile || mOSUpgradeMode)
				mEmptySDCard = false;
			else
				mEmptySDCard = true;
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "1 checkSDCardFile - mFWUpgradeMode : " + mFWUpgradeMode + ", mExistMapDataFile : "
							+ mExistMapDataFile + ", mOSUpgradeMode : " + mOSUpgradeMode + ", mEmptySDCard : " + mEmptySDCard);

			//200212 yhr
			if (mEmptySDCard)
			{
				if(mDefaultDialog != null && NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
				{
					mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,HP_Manager.mContext.getResources().getString(R.string.memory_card_check));
				}
				return;
			}
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 checkSDCardFile - mFWUpgradeMode : " + mFWUpgradeMode + ", mExistMapDataFile : "
					+ mExistMapDataFile + ", mOSUpgradeMode : " + mOSUpgradeMode + ", mEmptySDCard : " + mEmptySDCard);
			
			if (mExistMapDataFile) {
				HP_Manager.mWidgetMap = true;
				if (NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP) {
					if (NotifyDialog.mNotifyDialog != null) {
						// Navi 파일이 SD카드에 있고 동의함팝업이 발생될 경우
						NotifyDialog.mNotifyDialog.mHandler.removeMessages(NotifyDialog.MSG_BTN_ENABLE);
						NotifyDialog.mNotifyDialog.mHandler.sendEmptyMessageDelayed(NotifyDialog.MSG_BTN_ENABLE, 10 * HP_Index.TIME_1_SECOND);
					} else
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "Notify Dialog is null...");

					if (!getTopActivity().equals(GINIAPPInfo.PACKAGE_NAME) && mRunningMAP == false) {
						mStartNavi(true);
					} else
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "getTopActivity() : " + getTopActivity());

					if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME) {
						Launcher_Main.reflashWidget1();
					}
				} else {
					if (!isServiceRunningCheck()) {
						NotifyDialog.mCurrentPopup = NotifyDialog.SDCARD_LOAD_POPUP;
						
						// 로딩 팝업 표출
						Intent popup = null;
						popup = new Intent(this, NotifyDialog.class);
						startService(popup);

						if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER)
							HP_Manager.mCallback.onChangeHome();
					} else {
						if (NotifyDialog.mCurrentPopup != NotifyDialog.TRAFFIC_RULE_POPUP) {
							if(NotifyDialog.mCurrentPopup != NotifyDialog.SDCARD_LOAD_POPUP){
								if (NotifyDialog.mNotifyDialog != null) {
									NotifyDialog.mNotifyDialog.exitNotifyPopup();
								}
								
								NotifyDialog.mCurrentPopup = NotifyDialog.SDCARD_LOAD_POPUP;
							
								// 로딩 팝업 표출
								Intent popup = null;
								popup = new Intent(this, NotifyDialog.class);
								startService(popup);
							}
						}
					}
				}
				
				if(mRunningMAP == false) 
					mStartNavi(true);
				
				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME) {
					Launcher_Main.reflashWidget1();
				}
			} else {
				if (mOSUpgradeMode) {
					if(mDefaultDialog != null)
					{
						mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE;
						mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON,HP_Manager.mContext.getResources().getString(R.string.os_upgrade_msg));
					}
				} else if (mEmptySDCard){
					if(mDefaultDialog != null)
					{
						mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,HP_Manager.mContext.getResources().getString(R.string.memory_card_check));
					}
				}

				if (mFWUpgradeMode) {
					
					if(System_fw_Update.mFWUpdateState == System_fw_Update.FW_UPDATE_MODE_START)
						return;
					
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
							if(mFW_FileDst.isFile())
								HP_Manager.mUpdateFileFW = mFW_FileDst;
						
							Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "System_fw_Update 1 // " + HP_Manager.mUpdateFileFW.toString());
							System_fw_Update _fw = new System_fw_Update(HP_Manager.mUpdateFileFW);
							_fw.mCheckMode();
						}
					}, 3000);
				}
			}
		}
	}
	
		
	/**
	 * 190731 yhr
	 * 강제 업데이트 
	 */
	private void forceUpdateMode()
	{
		if(LauncherMainActivity.getInstance().mExistFWUpgradeFile)
			LauncherMainActivity.getInstance().mFWUpgradeMode = true;
		
		if(LauncherMainActivity.getInstance().mExistOSUpgradeFile)
			LauncherMainActivity.getInstance().mOSUpgradeMode = true;
		
		// 190710 강제 업데이트 시 우선순위 : OS
		if(LauncherMainActivity.getInstance().mOSUpgradeMode && LauncherMainActivity.getInstance().mFWUpgradeMode)
		{
			mShowUpdatePopup = true;
			LauncherMainActivity.getInstance().mFWUpgradeMode = false;
		}
		
		if (LauncherMainActivity.getInstance().mOSUpgradeMode && HP_Manager.mUpdateFile != null) {
			mShowUpdatePopup = true;
		}
		else
		{
			if(mOSUpgradeMode){
				mShowUpdatePopup = true;
			}
			else
			{
				if(mFWUpgradeMode)
					mShowUpdatePopup = true;
				else
				{
					deleteCacheFile();
					
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "UPDATE file is null...  " + HP_Manager.mUpdateFileFW);
					LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE_COPY_ERROR;
					LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_3));
				}
			}
		}
	}
	
	/**
	 * FW 업데이트 버전 비교하여 업데이트 모드 체크
	 * @param f
	 */
	private void checkFWUpdateFile(File f)
	{
		if (f.getName().contains(FW_UPDATE_FILE_NAME)) {
			if(mFWUpgradeMode)
				return;
			
			mExistFWUpgradeFile = true;
			HP_Manager.strNewFWVersion = mGetMCUBinFileVersion(f);
			String strCurrentFWVersion = SystemProperties.get(HP_Manager.PROPERTIES_VER_MCU, "없음");
			HP_Manager.mUpdateFileFW = f;
			
			if (HP_Manager.strNewFWVersion.compareTo(strCurrentFWVersion) > 0) {
				mFWUpgradeMode = true;
				mShowUpdatePopup = true;
			} else	{
				mFWUpgradeMode = false;
				mShowUpdatePopup = false;
			}
			
			if(strCurrentFWVersion.equals("없음"))
				mFWUpgradeMode = true;
			
			if(mOSUpgradeMode)
				mShowUpdatePopup = true;
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "FW Upgrade File >>> strNewFWVersion :  " + HP_Manager.strNewFWVersion + ", strCurrentFWVersion : " + strCurrentFWVersion
					+ ", mFWUpgradeMode : " + mFWUpgradeMode + ", HP_Manager.mUpdateFileFW : " + HP_Manager.mUpdateFileFW 
					+ ", mShowUpdatePopup : " + mShowUpdatePopup + ", mExistFWUpgradeFile : " + mExistFWUpgradeFile);
		}
	}

	private void checkSystemUpdateFile(File f)
	{
		if(f.getName().contains(".zip"))
		{
			if (f.getName().contains("hr") || f.getName().contains("pu")) {
				try {
					if(mOSUpgradeMode)
						return;
					
					mExistOSUpgradeFile = true;
					HP_Manager.strNewOSVersion = getFileVersion(f);
					String strCurrentOSVersion = SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, "없음");
					HP_Manager.mUpdateFile = f;
					Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "OS Upgrade File >>> strNewOSVersion :  " + HP_Manager.strNewOSVersion 
																		+ ", strCurrentOSVersion : " + strCurrentOSVersion
																		+ ", mOSUpgradeMode : " + mOSUpgradeMode
																		+ ", mUpdateFile : " + HP_Manager.mUpdateFile
																		+ ", mCurrentPopup : " + NotifyDialog.mCurrentPopup);
					
					if(HP_Manager.strNewOSVersion.contains("HR") && strCurrentOSVersion.contains("HR"))
					{								
						// 190621 yhr (업그레이드만 가능하게 수정/ 다운그레이드는 히든메뉴로 )
						if (HP_Manager.strNewOSVersion.compareTo(strCurrentOSVersion) > 0) {
							mOSUpgradeMode = true;
							mShowUpdatePopup = true;
						} else {
							mOSUpgradeMode = false;
							mShowUpdatePopup = false;
						}
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "HR OS Upgrade File / mOSUpgradeMode : " + mOSUpgradeMode + ", mShowUpdatePopup : " + mShowUpdatePopup);
						
					}
					else if(HP_Manager.strNewOSVersion.contains("PU") && strCurrentOSVersion.contains("PU"))
					{
						// 190621 yhr (업그레이드만 가능하게 수정/ 다운그레이드는 히든메뉴로 )
						if (HP_Manager.strNewOSVersion.compareTo(strCurrentOSVersion) > 0) {
							mOSUpgradeMode = true;
							mShowUpdatePopup = true;
						} else {
							mOSUpgradeMode = false;
							mShowUpdatePopup = false;
						}
						Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "PU OS Upgrade File / mOSUpgradeMode : " + mOSUpgradeMode + ", mShowUpdatePopup : " + mShowUpdatePopup);
					}
					
					if(mFWUpgradeMode)
						mShowUpdatePopup = true;
				} catch (ZipException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
	}
	
	public void mStartNavi(boolean _firstRun) {
		if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP
				|| NotifyDialog.mCurrentPopup == NotifyDialog.FW_UPDATE_LOADING_POPUP
				|| NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_COMPLETE)
			{
				return;
			}
		
		Intent intent = getPackageManager().getLaunchIntentForPackage(GINIAPPInfo.PACKAGE_NAME);
		if (intent != null) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + " mStartNavi ========================== MAP START 1 ==========================// mRunningMAP : " + mRunningMAP
					+ ", isAutoIllum ; " + Screen_LCD.isAutoIllum
					+ ", _firstRun : " + _firstRun);
			if(_firstRun) {
				mRunningMAP = true;
				Setting_Main _SettingMain = new Setting_Main();
				_SettingMain.mBootComplete();
		
				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			} else {
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			}
			mForgroundNavi = true;
			
			startActivity(intent);
			mSDCardCheckHandler.sendEmptyMessageDelayed(MSG_CHECK_SDCARD, DELAY_SD_CHECK);
		}
	}
	
	public void mShowNotiDialog(int _dlgType) {
		NotifyDialog.mCurrentPopup = _dlgType;
		Intent popup = null;
		popup = new Intent(getInstance(), NotifyDialog.class);
		startService(popup);
	}
	
	public void mHideNotiDialog() {
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "mHideNotiDialog");
		Intent _intent = new Intent(HP_Manager.mContext, NotifyDialog.class);
		_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		HP_Manager.mContext.stopService(_intent);
	}

	public String mGetMCUBinFileVersion(File file) {
		String _version = "";
		if (file != null && file.exists()) {
			try {
				FileInputStream fis = new FileInputStream(file);
				byte[] buffer = new byte[40];
				fis.read(buffer);
				if ((new String(buffer, 0, 3).equals("ver"))) {
					_version = SystemProperties.get("ro.product.fgcode", "") + "_" + buffer[MCU_.IDX_MCU_VERSION] + "."
							+ buffer[MCU_.IDX_MCU_VERSION + 1] + "." + buffer[MCU_.IDX_MCU_VERSION + 2];

					if (buffer[MCU_.IDX_MCU_VERSION + 3] == 1) {
						_version += " beta";
					}
				} else {
					String _ver = new String(buffer, 0, buffer.length);
					String[] words = _ver.split(",");
					_version = words[0];
					// if(mFWUpgradeMode == DEBUG_.ERROR_) { //Board FW
					// _version = SystemProperties.get("ro.product.fgcode", "")
					// + "_" + words[0];
					// }
				}
				fis.close();
			} catch (Exception e) {
				Log.d("debug", "____ file error : " + e.toString());
			}
		}
		return _version;
	}

	public static boolean mUpdateTime = false;
	public static int calSpeed;
	public static int updateTimeCnt = 0;
	private class SpeedActionListener implements LocationListener {
		private int cntCheck = 0;

		@Override
		public void onLocationChanged(Location location) {
			
			long time = location.getTime();
			Date date = new Date(time);
			Calendar calendar = Calendar.getInstance();
			long diff;
			
			// GPS 시간 업데이트
			if(mUpdateTime)
			{
				if(date.getYear() >= 100)
				{
					calendar.setTime(date);
					HP_Manager.mSystemDate.setTime(date);
					SystemClock.setCurrentTimeMillis(calendar.getTimeInMillis());
					mUpdateTime = false;
					
					//190924
					if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SETTING)
					{
						if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM && HP_Manager.mSubMenu == HP_Index.SYSTEM_MENU_TIME_SET)
						{
							if(mSystemTime != null)
								mSystemTime.setChangeView();
							else
							{
								mSystemTime = new System_Time();
								mSystemTime.setChangeView();
							}
						}
					}
				}
			}
			else
			{
				diff = Math.abs((date.getTime() - calendar.getTime().getTime())/1000);
				if (++updateTimeCnt > 50 ){
					if(diff >= 5)
					{
						if(HP_Manager.bIsAutoTime)
							mUpdateTime = true;
					}
					updateTimeCnt=0;
				}
			}

			// 주행 중 시청금지 표시
			calSpeed = (int) (location.getSpeed() * 3.6);
//			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "calSpeed : " + calSpeed);
			if (txtSpeed.getVisibility() == View.VISIBLE)
				txtSpeed.setText(String.valueOf(calSpeed));
			
			if(calSpeed <= 5.0){
				if (mParkingStatus == false) 
				{
					if (++cntCheck > 1) {
						if (HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SCREEN) {
							if (HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_ADJUST )
								Screen_Adjust.mAdjustBG.setVisibility(View.GONE);
							else if(HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_RATIO)
								Screen_Ratio.mRatioBG.setVisibility(View.GONE);
						} else{
							// 영상출력
							mParkingStatus = true;
							DxbView.updateScreen();
							cntCheck = 0;
						}

						// 영상출력
						mParkingStatus = true;
						DxbView.updateScreen();
						cntCheck = 0;
					}
				} else {
					cntCheck = 1;
				}
			} else {
				if (mParkingStatus) 
				{
					if (++cntCheck > 1) {
						mParkingStatus = false;
						DxbView.updateScreen();
						cntCheck = 0;
					}
				} 
				else 
				{
					if (HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SCREEN) {							
						if (HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_ADJUST )
							Screen_Adjust.mAdjustBG.setVisibility(View.VISIBLE);
						else if(HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_RATIO)
							Screen_Ratio.mRatioBG.setVisibility(View.VISIBLE);
					}
					cntCheck = 1;
				}
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
		}

		@Override
		public void onProviderEnabled(String arg0) {
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		}
	}

	public static boolean isServiceRunningCheck() {
		if (HP_Manager.mContext == null)
			return false;

		ActivityManager manager = (ActivityManager) HP_Manager.mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (NotifyDialog.class.getName().equals(service.service.getClassName())) {
				return true;
			}

			if (NaviMenuDialog.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isNotifyDialogcheck() {
		if (HP_Manager.mContext == null)
			return false;

		ActivityManager manager = (ActivityManager) HP_Manager.mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (NotifyDialog.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	static public SharedPreferences _prefs;
	static public SharedPreferences.Editor _prefs_edt;
	public final String TPEG_DATA = "TPEG_DATA";

	public void createSharedPreference() {
		Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "********************* TPEG >> createSharedPreference");
		_prefs = getSharedPreferences(TPEG_DATA, Context.MODE_PRIVATE);
		_prefs_edt = _prefs.edit();
	}

	static public void saveSharedPreference() {

		Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "********************* TPEG >> saveSharedPreference");
		if (_prefs_edt == null)
			return;

		if (DxbPlayer.mTpegChannel != null) {
			_prefs_edt.putString("ensembleName", DxbPlayer.mTpegChannel.ensembleName);
			_prefs_edt.putInt("ensembleID", DxbPlayer.mTpegChannel.ensembleID);
			_prefs_edt.putInt("ensembleFreq", DxbPlayer.mTpegChannel.ensembleFreq);
			_prefs_edt.putString("serviceName", DxbPlayer.mTpegChannel.serviceName);
			_prefs_edt.putInt("serviceID", DxbPlayer.mTpegChannel.serviceID);
			_prefs_edt.putString("channelName", DxbPlayer.mTpegChannel.channelName);
			_prefs_edt.putInt("channelID", DxbPlayer.mTpegChannel.channelID);
			_prefs_edt.putInt("type", DxbPlayer.mTpegChannel.type);
			_prefs_edt.putInt("bitrate", DxbPlayer.mTpegChannel.bitrate);
			_prefs_edt.putInt("chIdx", DxbPlayer.mTpegChannel.chIdx);

			for (int regIndex = 0; regIndex < 7; regIndex++)
				_prefs_edt.putInt(String.valueOf(regIndex), DxbPlayer.mTpegChannel.reg[regIndex]);
		} 

		_prefs_edt.commit();
	}

	public void loadSharedPreference() {

		Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "********************* TPEG >> loadSharedPreference");
		if (_prefs_edt == null)
			return;
		if (_prefs.getString("serviceName", "").length() <= 0)
			return;
		if (DxbPlayer.mTpegChannel == null)
			DxbPlayer.mTpegChannel = new Channel();

		DxbPlayer.mTpegChannel.ensembleName = _prefs.getString("ensembleName", "");
		DxbPlayer.mTpegChannel.ensembleID = _prefs.getInt("ensembleID", 0);
		DxbPlayer.mTpegChannel.ensembleFreq = _prefs.getInt("ensembleFreq", 0);
		DxbPlayer.mTpegChannel.serviceName = _prefs.getString("serviceName", "");
		DxbPlayer.mTpegChannel.serviceID = _prefs.getInt("serviceID", 0);
		DxbPlayer.mTpegChannel.channelName = _prefs.getString("channelName", "");
		DxbPlayer.mTpegChannel.channelID = _prefs.getInt("channelID", 0);
		DxbPlayer.mTpegChannel.type = _prefs.getInt("type", 0);
		DxbPlayer.mTpegChannel.bitrate = _prefs.getInt("bitrate", 0);
		DxbPlayer.mTpegChannel.chIdx = _prefs.getInt("chIdx", 0);

		for (int regIndex = 0; regIndex < 7; regIndex++)
			DxbPlayer.mTpegChannel.reg[regIndex] = _prefs.getInt(String.valueOf(regIndex), 0);
	}

	/**
	 * Power Short Event Radio, DMB, cNavi 모두 Mute
	 * 
	 * @param mutestate
	 *            : mute on/off
	 * @param visible
	 *            : mute icon 표출여부
	 */
	public void setPowerMuteOnOff(int mutestate, boolean visible) {
//		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + " setPowerMuteOnOff // mutestate : " + mutestate + ", visible : " + visible);
		HP_Manager.mDMBMuteStatus = mutestate;
		HP_Manager.mNAVIMuteStatus = mutestate;

		// DMB Mute
		HP_Manager.mCallback.onChangeDMBMuteState(HP_Manager.mDMBMuteStatus, visible);

		// 19.01.18 yhr Navi
		setNaviMuteOnOff(mutestate);
	}

	public void setNaviMuteOnOff(int mutestate) {
		if (HP_Manager.mWidgetMap == false)
		{
			if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE)
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
			else
				iconMute.setVisibility(View.INVISIBLE);
			return;
		}

		HP_Manager.mNAVIMuteStatus = mutestate;
		HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_GUIDE_MUTE, mutestate, 0);
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_NAVI_MUTE_STATE);
//		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "setNaviMuteOnOff // mNAVIMuteStatus : " + HP_Manager.mNAVIMuteStatus 
//											 + ", mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus 
//											 + ", mWidgetMap : " + HP_Manager.mWidgetMap);
		
		// 190417 yhr
		if (mutestate == HP_Index.NAVI_SOUND_UNMUTE) {
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE) {
				if (iconMute.getVisibility() == View.VISIBLE)
					iconMute.setVisibility(View.INVISIBLE);
			}
			else
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
		} else {
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE)
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
			else
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
		
			if (iconMute.getVisibility() == View.INVISIBLE) {
//				if (HP_Manager.mLCDOnOff == HP_Index.LCD_ON)
					iconMute.setVisibility(View.VISIBLE);
			}
		}
	}

	public static void onChangeMuteIcon() {
		if(iconMute == null)
			return;
		
//		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "@@ onChangeMuteIcon // system : " +
//				HP_Manager.mSystemMuteStatus + ", dmb : " + HP_Manager.mDMBMuteStatus
//				+ ", HP_Manager.mWidgetMap : " + HP_Manager.mWidgetMap);
		
		if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE && HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE)
			HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_MUTE;
		else
			HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
	
		if (HP_Manager.mSystemMuteStatus == HP_Index.SYSTEM_SOUND_MUTE)
		{
			if(HP_Manager.mWidgetMap)
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
			else
				iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
		}
		else {
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE) {
				if(HP_Manager.mWidgetMap)
				{
					if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_UNMUTE)
						iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
					else 
						iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
				}
				else
					iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
			} else {
				if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE)
				{
					if (HP_Manager.mWidgetMap == false)
					{
						iconMute.setVisibility(View.INVISIBLE);
						return;
					}
					iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
				}
				else {
					iconMute.setVisibility(View.INVISIBLE);
					return;
				}
			}
		}
		iconMute.setVisibility(View.VISIBLE);
	}
	
	public void mSetSystemMute(int _dmbMuteState, int _naviMuteState) {
//		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "@@@@@@@@@ HP_Manager.mSystemMuteStatus : " + HP_Manager.mSystemMuteStatus
//				+ ", mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus + ", mNAVIMuteStatus : "
//				+ HP_Manager.mNAVIMuteStatus + ", mNaviSoundStart : " + HP_Manager.mNaviSoundStart
//				+ ", mWidgetMap : " + HP_Manager.mWidgetMap);
		
		//navi와 dmb mute를 &&연산하기 때문에 Map이 실행중이지 않으면 navi는 dmb음성과 동일하다고 본다.
		if (HP_Manager.mWidgetMap == false)	
			_naviMuteState = _dmbMuteState;
		
		if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
		{
			if (_dmbMuteState == HP_Index.DMB_SOUND_MUTE && _naviMuteState == HP_Index.NAVI_SOUND_MUTE) {
				HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
				DxbView_Normal.mfromUserMute = false;
				HP_Manager.mCallback.onChangeDMBMuteState(HP_Manager.mCurrentDMBVol == 0 ? HP_Index.DMB_SOUND_MUTE : HP_Index.DMB_SOUND_UNMUTE, true);
				LauncherMainActivity.getInstance().setNaviMuteOnOff(HP_Index.NAVI_SOUND_UNMUTE);
			}
			else {
				HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_MUTE;
				DxbView_Normal.mfromUserMute = true;
				HP_Manager.mCallback.onChangeDMBMuteState(HP_Index.DMB_SOUND_MUTE, true);
				LauncherMainActivity.getInstance().setNaviMuteOnOff(HP_Index.NAVI_SOUND_MUTE);
			}
		}
		else
		{
			if (_naviMuteState == HP_Index.NAVI_SOUND_MUTE)
				LauncherMainActivity.getInstance().setNaviMuteOnOff(HP_Index.NAVI_SOUND_UNMUTE);
			else
				LauncherMainActivity.getInstance().setNaviMuteOnOff(HP_Index.NAVI_SOUND_MUTE);
		}
	}
	
	public void setStatusBarButteonEnable(boolean enable)
	{
		btnHome.setEnabled(enable);
		btnNavi.setEnabled(enable);
		if(enable)
		{
			btnHome.setAlpha(1);
			btnNavi.setAlpha(1);
		}
		else
		{
			btnHome.setAlpha((float) 0.5);
			btnNavi.setAlpha((float) 0.5);
		}
	}
	
	public Handler mSDCardCheckHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MSG_CHECK_SDCARD)
			{
				sendEmptyMessageDelayed(MSG_CHECK_SDCARD, DELAY_SD_CHECK);
				try {
					if (M_MTX.readGPIO((byte) 170) == 1) // SD Card 제거된 상태 
					{
						removeMessages(MSG_CHECK_SDCARD);
						if (TpegService.mServicedBind) {
							TpegService.unbindService(getApplicationContext());
							TpegService.stopService(getApplicationContext());
						}
						mHandlerSDCheck.postDelayed(mRunnableUnmountCheck, DELAY_SD_CHECK);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			super.handleMessage(msg);
		}
	};
	
	public void screenshot()
	{
		String folderName = "/storage/sdcard1/screenshot";
		boolean ret = true;
	    File folder = new File(folderName);
	    if(folder.isDirectory() == false) ret = folder.mkdir();
		    
	    if(ret == false) {    
	    	Toast.makeText(this, "폴더 생성 실패!", Toast.LENGTH_SHORT).show();
	    }
	    else {
			Process rmCache = null;
			Date date = new Date(System.currentTimeMillis());
			    SimpleDateFormat sdfNow = new SimpleDateFormat("MM_dd_HH_mm_ss");
			 
			String filename = "/storage/sdcard1/screenshot/"+ sdfNow.format(date);
			filename += ".png";
			 
			try {
				rmCache = Runtime.getRuntime().exec("screencap "+filename);
				rmCache.getErrorStream().close();
				rmCache.getInputStream().close(); 
				rmCache.getOutputStream().close(); 
		
				ret = true;
			} catch (IOException e) {
				e.printStackTrace();
				ret = false;
			}		
			try {
				rmCache.waitFor();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	    }
	    
	    if(ret) {
	    	new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(LauncherMainActivity.getInstance(), "화면캡쳐가 완료되었습니다.", Toast.LENGTH_SHORT).show();
				}
			}, 1000);
	    } else {
	    	Toast.makeText(LauncherMainActivity.getInstance(), "화면캡쳐가 실패되었습니다.", Toast.LENGTH_SHORT).show();
	    }
	}
}