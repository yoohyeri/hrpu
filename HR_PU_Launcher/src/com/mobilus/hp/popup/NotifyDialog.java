package com.mobilus.hp.popup;

import java.io.File;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.Launcher_Main;
import com.mobilus.hp.launcher.MAP_INFO;
import com.mobilus.hp.mapupdate.GINIAPPInfo;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.mobilus.hp.setting.system.System_fw_Update;
import com.telechips.android.tdmb.GiniBroadcastReceiver;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;
import com.telechips.android.tdmb.SDCardMonitorReceiver;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NotifyDialog extends Service implements OnClickListener {

	final public String CLASS_NAME = "[NotifyDialog ]  ";

	public final static int TRAFFIC_RULE_POPUP = 0;
	public final static int SDCARD_LOAD_POPUP = 1;
	public final static int SDCARD_UNMOUNT_POPUP = 2;
	public final static int LOG_SAVE_POPUP = 3;
//	public final static int FW_UPDATE_LOADING_POPUP = 4;
//	public final static int FACTORY_RESET_POPUP = 5;
	public final static int INITIALIZE = 6;
//	public final static int SDCARD_UNMOUNT = 7;
	public final static int REBOOTING_POPUP = 8;
	public final static int NAVI_START_POPUP = 9;
	public final static int GPS_UPDATE_POPUP = 10;
	public final static int FW_UPDATE_LOADING_POPUP = 11;
	public final static int UPDATE_REBOOTING_NOTI_POPUP = 12;
	public final static int UPDATE_COMPLETE = 13;
	public final static int SD_UNMOUNT_FW_UPDATE_FAIL_POPUP = 14;
	public final static int FW_UPDATE_TIMEOUT = 15;
	public final static int UPDATE_FILE_COPY = 16;
	public final static int NAVI_KILL_POPUP = 17;

	private static View mView;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mParams;

	private TextView btnAgree;
	public static int mCurrentPopup;
	
	public static NotifyDialog mNotifyDialog = null;
	private BroadcastReceiver mReceiver;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "onCreate - OS : " + LauncherMainActivity.getInstance().mOSUpgradeMode + ", FW : "
				+ LauncherMainActivity.getInstance().mFWUpgradeMode
				+ ", mCurrentPopup : " + mCurrentPopup
				+ ", mShowUpdatePopup : " + LauncherMainActivity.mShowUpdatePopup);
		mNotifyDialog = this;
		registerReceiver();

		switch (mCurrentPopup) {
		case TRAFFIC_RULE_POPUP:
			onSetTrafficRulesPopup();
			break;
		case LOG_SAVE_POPUP:
		case SDCARD_LOAD_POPUP:
		case NAVI_KILL_POPUP:
		case NAVI_START_POPUP:
		case INITIALIZE:
			onSetLodingImgPopup(550,300);
			break;
		case UPDATE_FILE_COPY:
			onSetProgressPopup(550,300);
			break;
		case SDCARD_UNMOUNT_POPUP:
		case REBOOTING_POPUP:
		case UPDATE_REBOOTING_NOTI_POPUP:
		case UPDATE_COMPLETE:
		case FW_UPDATE_TIMEOUT:
			onSetNotifyPopup(600,300);
			break;
		case SD_UNMOUNT_FW_UPDATE_FAIL_POPUP:
			try {
				mWindowManager.removeView(mView);
			} catch (NullPointerException e) {
			}
			onSetNotifyPopup(600,300);
			break;
		case FW_UPDATE_LOADING_POPUP:
			
			try {
				mWindowManager.removeView(mView);
			} catch (NullPointerException e) {
			}
			
			onSetLodingImgPopup(550,300);  // 업데이트 준비중
			break;

		default:
			break;
		}
	}

	private void registerReceiver() {		
		final IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction("android.intent.action.MEDIA_MOUNTED");
		intentfilter.addAction("android.intent.action.MEDIA_UNMOUNTED");
		
		this.mReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (HP_Manager.mCallback != null) 
					Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "onReceive -  " + intent.getAction() + ", onChangeSDCardState 1");
					HP_Manager.mCallback.onChangeSDCardState(intent);
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
	
	@Override
	public void onDestroy() {
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "onDestroy - OS : " + LauncherMainActivity.getInstance().mOSUpgradeMode + ", FW : "
				+ LauncherMainActivity.getInstance().mFWUpgradeMode
				+ ", mCurrentPopup : " + mCurrentPopup
				+ ", mShowUpdatePopup : " + LauncherMainActivity.mShowUpdatePopup);

		mHandler.removeMessages(MSG_REBOOTING_SYSTEM);
		mHandler.removeMessages(MSG_BTN_ENABLE);
		mHandler.removeMessages(MSG_DISMISS_POPUP);
		mHandler.removeMessages(MSG_LOADING_POPUP_TIMEOUT);

		unregisterReceiver();
			
		if (LauncherMainActivity.getInstance().mOSUpgradeMode) {
			if(LauncherMainActivity.mShowUpdatePopup && LauncherMainActivity.getInstance().mDefaultDialog != null)
			{
				if(GiniBroadcastReceiver.mMapUpdateMode == false)
				{
					mHandler.postDelayed(new Runnable() {
						
						@Override
						public void run() {
							Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "================================================== mOSUpgradeMode");
							LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE;
							LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.os_upgrade_msg));
						}
					}, 500);
				}
			}
			
			try {
				mWindowManager.removeView(mView);
				mWindowManager = null;
			} catch (NullPointerException e) {
			}
			super.onDestroy();
			return;
		}
		else
		{
			if (LauncherMainActivity.getInstance().mFWUpgradeMode)
			{
				if(LauncherMainActivity.mShowUpdatePopup && LauncherMainActivity.getInstance().mDefaultDialog != null)
				{
					if(GiniBroadcastReceiver.mMapUpdateMode == false)
					{
						mHandler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "================================================== mFWUpgradeMode");
								LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE;
								LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.fw_upgrade_msg));
							}
						}, 500);
					}
					
				}
				
				try {
					mWindowManager.removeView(mView);
					mWindowManager = null;
				} catch (NullPointerException e) {
				}
				super.onDestroy();
			}
		}
	
		try {
			mWindowManager.removeView(mView);
			mWindowManager = null;
		} catch (NullPointerException e) {
			// TODO: handle exception
		}

		super.onDestroy();
	}

	private void onSetLodingImgPopup(int w, int h)
	{
		if (mWindowManager == null)
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams(w, h, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
				PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mParams.dimAmount = 1; // 팝업 뒷 배경 검은색으로
		mSetLoadingImgView();
		mWindowManager.addView(mView, mParams);
	}
	
	private void onSetProgressPopup(int w, int h)
	{
		if (mWindowManager == null)
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams(w, h, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
				PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mParams.dimAmount = 1; // 팝업 뒷 배경 검은색으로
		mSetProgressView();
		mWindowManager.addView(mView, mParams);
	}
	
	private void onSetNotifyPopup(int w, int h)
	{
		if (mWindowManager == null)
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams(w, h, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
				PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mParams.dimAmount = 1; // 팝업 뒷 배경 검은색으로
		mSetNotifyView();
		try {
			mWindowManager.addView(mView, mParams);
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
		}
	}
	
	@SuppressWarnings("deprecation")
	private void onSetTrafficRulesPopup() {
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
				PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mSetLoadViewTrafficRulesPopup();
		mWindowManager.addView(mView, mParams);
	}
	
	public static ProgressBar mProgressBar;
	private void mSetProgressView()
	{
		String msg = "";
		if(mCurrentPopup == UPDATE_FILE_COPY)
			msg = HP_Manager.mContext.getResources().getString(R.string.update_file_copy_msg);
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_update_ready),null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_update_ready), null);

		((RelativeLayout) mView.findViewById(R.id.rl_default_bg)).setBackgroundColor(Color.BLACK);
		((TextView) mView.findViewById(R.id.default_popup_text)).setText(msg);  
		
		mProgressBar = (ProgressBar) mView.findViewById(R.id.copy_progress);
	}
	
	private void mSetLoadingImgView()
	{
		String msg = "";
		if(mCurrentPopup == LOG_SAVE_POPUP)
			msg = HP_Manager.mContext.getResources().getString(R.string.info_q_wait);
		else if(mCurrentPopup == SDCARD_LOAD_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.sdcard_insert_msg);
			mHandler.sendEmptyMessageDelayed(MSG_LOADING_POPUP_TIMEOUT, 20 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == NAVI_START_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.navi_start_msg);
			mHandler.sendEmptyMessageDelayed(MSG_LOADING_POPUP_TIMEOUT, 15 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == INITIALIZE)
			msg = HP_Manager.mContext.getResources().getString(R.string.initialize_ing);
		else if(mCurrentPopup == GPS_UPDATE_POPUP)
			msg = HP_Manager.mContext.getResources().getString(R.string.gps_update_msg);
		else if(mCurrentPopup == NAVI_KILL_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.navi_kill_msg);
			
			//200214 yhr
			LauncherMainActivity.mRunningMAP = false;
			
			if(HP_Manager.mProductionProcess == false)
				mHandler.sendEmptyMessageDelayed(MSG_LOADING_POPUP_TIMEOUT, 20 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == FW_UPDATE_LOADING_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.fw_update_msg);
			
			//190731 yhr
			new Handler().postDelayed(new Runnable() {
				
				@Override
				public void run() {
					System_fw_Update.startFWUpdate();
				}
			}, 3000);
			mHandler.sendEmptyMessageDelayed(MSG_LOADING_POPUP_TIMEOUT, 20 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == UPDATE_FILE_COPY)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.update_file_copy_msg);
		}
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_default_popup),null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_default_popup), null);

		((RelativeLayout) mView.findViewById(R.id.rl_default_bg)).setBackgroundColor(Color.BLACK);
		((TextView) mView.findViewById(R.id.default_popup_text)).setText(msg); 
		((ProgressBar) mView.findViewById(R.id.loadingIcon)).setVisibility(View.VISIBLE);
		((TextView) mView.findViewById(R.id.noticeIcon)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.btnConfirm)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.btnCancle)).setVisibility(View.GONE);

		btnAgree = (TextView) mView.findViewById(R.id.btnAgree);
		btnAgree.setVisibility(View.GONE);
		btnAgree.setOnClickListener(this);
	}
	
	private void mSetNotifyView() {
		try {
			if(LauncherMainActivity.M_MTX.getScreenOnOff() == false)
				LauncherMainActivity.M_MTX.setScreenOnOff(true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		String msg = "";
		if(mCurrentPopup == SDCARD_UNMOUNT_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.sdcard_remove_msg);
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 5 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == REBOOTING_POPUP)
			msg = HP_Manager.mContext.getResources().getString(R.string.sdcard_rebooting_msg);
		else if(mCurrentPopup == UPDATE_REBOOTING_NOTI_POPUP)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.update_rebooting_msg);
			mHandler.sendEmptyMessageDelayed(MSG_LOADING_POPUP_TIMEOUT, 30 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == UPDATE_COMPLETE)
		{
			msg = HP_Manager.mContext.getResources().getString(R.string.update_complete_msg);
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 6 * HP_Index.TIME_1_SECOND);
		}
		else if(mCurrentPopup == SD_UNMOUNT_FW_UPDATE_FAIL_POPUP)
		{
			mHandler.sendEmptyMessage(MSG_LOADING_POPUP_TIMEOUT);
			return;
		}
		else if(mCurrentPopup == FW_UPDATE_TIMEOUT)
		{
			mHandler.sendEmptyMessage(MSG_LOADING_POPUP_TIMEOUT);
			return;
		}
		
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_default_popup), null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_default_popup), null);

		((TextView) mView.findViewById(R.id.default_popup_text)).setText(msg); // txtMsg
		((ProgressBar) mView.findViewById(R.id.loadingIcon)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.noticeIcon)).setVisibility(View.VISIBLE);
		((TextView) mView.findViewById(R.id.btnConfirm)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.btnCancle)).setVisibility(View.GONE);
	}
	
	private void mSetLoadViewTrafficRulesPopup() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_default_popup), null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_default_popup), null);

		((RelativeLayout) mView.findViewById(R.id.rl_default_bg)).setBackgroundColor(Color.BLACK);
		((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.traffic_rules_msg)); // txtMsg
		((ProgressBar) mView.findViewById(R.id.loadingIcon)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.noticeIcon)).setVisibility(View.VISIBLE);

		btnAgree = (TextView) mView.findViewById(R.id.btnAgree);
		btnAgree.setVisibility(View.VISIBLE);
		btnAgree.setEnabled(false);
		btnAgree.setAlpha((float) 0.5);
		btnAgree.setText(HP_Manager.mContext.getResources().getString(R.string.noti_confirm));
		btnAgree.setOnClickListener(this);

		mHandler.sendEmptyMessageDelayed(MSG_BTN_ENABLE, 10 * HP_Index.TIME_1_SECOND);

		((TextView) mView.findViewById(R.id.btnConfirm)).setVisibility(View.GONE);
		((TextView) mView.findViewById(R.id.btnCancle)).setVisibility(View.GONE);
	}
	
	public void exitNotifyPopup() {
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "exitNotifyPopup  // mCurrentPopup : " + mCurrentPopup + ", mUpdateFileFW : " + HP_Manager.mUpdateFileFW);

		if(mCurrentPopup == FW_UPDATE_LOADING_POPUP && HP_Manager.mUpdateFileFW == null)
		{
			Handler _handler = new Handler();
			_handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intent);
					
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
			}, 500);
			return;
		}
		
		if(mCurrentPopup == SD_UNMOUNT_FW_UPDATE_FAIL_POPUP )
		{
			Handler _handler = new Handler();
			_handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intent);
					
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
			}, 500);
			return;
		}
		else if(mCurrentPopup == FW_UPDATE_TIMEOUT)
		{
			Handler _handler = new Handler();
			_handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intent);
					
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
			}, 500);
			return;
		}
		else if(mCurrentPopup == UPDATE_REBOOTING_NOTI_POPUP)
		{
			Handler _handler = new Handler();
			_handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					stopService(intent);
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
			}, 500);
			return;
		}
			
		if (mCurrentPopup == TRAFFIC_RULE_POPUP)
		{
			boolean reverse = false;
			try {
				reverse = LauncherMainActivity.M_MTX.getStateReverse();

			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if(reverse == false) {
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						Screen_LCD _ScreenLCD = new Screen_LCD();
						_ScreenLCD.mBootComplete();						
					}
				}, 1);
			}

			try {
				if(LauncherMainActivity.M_MTX.loadUpdateMode() == LauncherMainActivity.UPDATE_MODE_NONE)
					mCurrentPopup = SDCARD_LOAD_POPUP;

			} catch (RemoteException e) {
				e.printStackTrace();
			}
			HP_Manager.mIsRebooting = false;
		}
		mHandler.removeMessages(MSG_LOADING_POPUP_TIMEOUT);

		Intent intent = new Intent(getApplicationContext(), NotifyDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "exitNotifyPopup // stopService");
		stopService(intent);
		
		if(GiniBroadcastReceiver.mMapUpdateMode){
			LauncherMainActivity.getInstance().mShowNotiDialog(NotifyDialog.NAVI_START_POPUP);
			return;
		}
		
		//200204 yhr
		try {
			Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"exitNotifyPopup // SD Card State : " + LauncherMainActivity.M_MTX.readGPIO((byte) 170) + ", last : " + HP_Manager.mLastMode
					+ ", map widget : " + HP_Manager.mWidgetMap + ", mCurrentView : " + HP_Manager.mCurrentView
					+ ", mRunningMAP : " + LauncherMainActivity.mRunningMAP
					+ ", mNaviBind : " + Launcher_Main.mNaviBind
					+", mCurrentPopup : " + mCurrentPopup);
			
			// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
			if (LauncherMainActivity.M_MTX.readGPIO((byte) 170) == 1)
			{
				if( HP_Manager.mLastMode == HP_Index.LAST_MODE_NAVI_FULL )
				{
					HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				}
			}
			else
			{
				if(HP_Manager.mLastMode == HP_Index.LAST_MODE_NAVI_FULL)
				{
					// 200212 yhr
					if(LauncherMainActivity.mRunningMAP)
						HP_Manager.mWidgetMap = true;
					/////
					
					if(HP_Manager.mWidgetMap)
					{
						LauncherMainActivity.mForgroundNavi = true;
						HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_MAP;
						
						//200214 yhr
						if(Launcher_Main.mNaviBind == false)
						{
							if(mCurrentPopup >= 0)
							{
								// 로딩팝업
								LauncherMainActivity.getInstance().mShowNotiDialog(SDCARD_LOAD_POPUP);
								Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"exitNotifyPopup // startService 1 >>  mCurrentPopup : " + mCurrentPopup );
							}
						}
						return;
					}
					else
					{
						LauncherMainActivity.mForgroundNavi = false;
						HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
						HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
						HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
					}
				}
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "exitNotifyPopup // checkTopNavi : " +checkTopNavi() + ", mForgroundNavi : " + LauncherMainActivity.mForgroundNavi
				+ ", GiniBroadcastReceiver.mMapLifeCycle " + GiniBroadcastReceiver.mMapLifeCycle
				+ ", mCurrentPopup : " + mCurrentPopup);
		
		
		if(HP_Manager.mLastMode != HP_Index.LAST_MODE_NAVI_FULL)
		{
			if (checkTopNavi() || LauncherMainActivity.mForgroundNavi) {
				mHandler.removeMessages(MSG_DISMISS_POPUP);
				if(GiniBroadcastReceiver.mMapLifeCycle == MAP_INFO.BM_NOTIFY_ACTIVATE_ONDESTROY)
					return;
				
				//200214 yhr
				if(mCurrentPopup >= 0)
				{
					Intent _intent = new Intent(getApplicationContext(), NotifyDialog.class);
					_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startService(_intent);
					Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"exitNotifyPopup // startService 2 >>  mCurrentPopup : " + mCurrentPopup );
					return;
				}
			}
		}
			
		// 190507
		if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP){
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
		}
		
		// SD Eject && Home Screen
		if (mCurrentPopup == NotifyDialog.SDCARD_UNMOUNT_POPUP && HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
			Launcher_Main.reflashWidget1();
	}

	/**
	 * 팝업을 제거하기 전에 Navi화면이 화면에 보여지는중인지 체크
	 */
	public boolean checkTopNavi() {
		if (LauncherMainActivity.getTopActivity().equals(GINIAPPInfo.PACKAGE_NAME))
			return true;
		else
			return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnAgree:
			exitNotifyPopup();
			break;
		default:
			break;
		}
	}

	public void setBtnEnable() {
		if (btnAgree == null)
			return;

		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME+"setBtnEnable");
		
		if (LauncherMainActivity.getInstance().mBootCompleted == false)
			HP_Manager.mCallback.oncheckSDCardState();
	
		btnAgree.setEnabled(true);
		btnAgree.setAlpha(1);
		mHandler.removeMessages(MSG_DISMISS_POPUP);
		mHandler.removeMessages(MSG_BTN_ENABLE);
		
		try {
			if(LauncherMainActivity.M_MTX.getStateReverse() && LauncherMainActivity.getInstance().mDismissPopup == false)
				return;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 4 * HP_Index.TIME_1_SECOND);
	}
	
	public void setDismissPopop()
	{
		if(btnAgree == null)
			return;
		
		if(btnAgree.isEnabled())
		{
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 10 * HP_Index.TIME_1_SECOND);
		}
		else
		{
			mHandler.sendEmptyMessageDelayed(MSG_BTN_ENABLE, 10 * HP_Index.TIME_1_SECOND);
		}
	}

	public void changeLoadingPopup() {
		try {
			mWindowManager.removeView(mView);
		} catch (NullPointerException e) {
		}
		
		try {
			if(LauncherMainActivity.M_MTX.loadUpdateMode() == LauncherMainActivity.UPDATE_MODE_NONE)
			{
				mCurrentPopup = SDCARD_LOAD_POPUP;
				onSetLodingImgPopup(550, 300);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void changeGPSUpdatePopup() {
		mHandler.removeMessages(MSG_BTN_ENABLE);
		mHandler.removeMessages(MSG_DISMISS_POPUP);
		mHandler.removeMessages(MSG_LOADING_POPUP_TIMEOUT);
		mCurrentPopup = NotifyDialog.GPS_UPDATE_POPUP;
		
		try {
			mWindowManager.removeView(mView);
		} catch (NullPointerException e) {
		}
		onSetLodingImgPopup(550,  300);
	}

	public void removeMsgBtnEnable() {
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"removeMsgBtnEnable");
		if(mHandler != null)
			mHandler.removeMessages(MSG_BTN_ENABLE);
	}

	public void onExitMapLoadingPopup() {
		mHandler.removeMessages(MSG_LOADING_POPUP_TIMEOUT);
		mHandler.sendEmptyMessage(MSG_LOADING_POPUP_TIMEOUT);
	}
	
	public static final int MSG_BTN_ENABLE = 0;
	public final static int MSG_DISMISS_POPUP = 1;
	public final static int MSG_LOADING_POPUP_TIMEOUT = 2;
	private final int MSG_REBOOTING_SYSTEM = 3;
	@SuppressLint("HandlerLeak")
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == MSG_BTN_ENABLE) {
				setBtnEnable();
			} else if (msg.what == MSG_DISMISS_POPUP) {
				removeMessages(MSG_DISMISS_POPUP);
				Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"MSG_DISMISS_POPUP // mCurrentPopup : " + mCurrentPopup);
				if(mCurrentPopup == UPDATE_COMPLETE)
				{
					try {
						LauncherMainActivity.M_MTX.saveUpdateMode(-1);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
										
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
				else if(mCurrentPopup == SD_UNMOUNT_FW_UPDATE_FAIL_POPUP)
				{
					PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
					pm.reboot(null);
				}
				else
				{
					exitNotifyPopup();
				}
			} else if (msg.what == MSG_REBOOTING_SYSTEM) {
				mHandler.removeMessages(MSG_REBOOTING_SYSTEM);
				exitNotifyPopup();
			} else if (msg.what == MSG_LOADING_POPUP_TIMEOUT) {
				removeMessages(MSG_LOADING_POPUP_TIMEOUT);

				if(mCurrentPopup == NAVI_START_POPUP)
					return;

				btnAgree = (TextView) mView.findViewById(R.id.btnAgree);
				
				if(mCurrentPopup == FW_UPDATE_LOADING_POPUP)
				{
					// 에러 팝업 후 exitNotifyPopup()호출되면 재부팅 > 재부팅을 하기 위해 popup index 변경
					mCurrentPopup = SD_UNMOUNT_FW_UPDATE_FAIL_POPUP;
					
					// SD Card가 제거되어 파일을 찾을 수 없음
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_2));
				}
				else if(mCurrentPopup == SD_UNMOUNT_FW_UPDATE_FAIL_POPUP)
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_1));
				else if(mCurrentPopup == UPDATE_REBOOTING_NOTI_POPUP)
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_1));
				else if(mCurrentPopup == FW_UPDATE_TIMEOUT)
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_1));
				else if(mCurrentPopup == NAVI_KILL_POPUP)
				{
					//200214 yhr
					LauncherMainActivity.mRunningMAP = false;
					
					if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
						HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
					
					Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"memory_card_check 3");
					
					GiniBroadcastReceiver.mMapLifeCycle = MAP_INFO.BM_NOTIFY_ACTIVATE_ONDESTROY;
					
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.memory_card_check));
				}
				else
				{
					//200214 yhr
					LauncherMainActivity.mRunningMAP = false;
					
					Log.d(HP_Manager.TAG_POPUP, CLASS_NAME+"memory_card_check 4");
					
					GiniBroadcastReceiver.mMapLifeCycle = MAP_INFO.BM_NOTIFY_ACTIVATE_ONDESTROY;
					
					((TextView) mView.findViewById(R.id.default_popup_text)).setText(HP_Manager.mContext.getResources().getString(R.string.memory_card_check)); // txtMsg
				}
				
				((ProgressBar) mView.findViewById(R.id.loadingIcon)).setVisibility(View.GONE);
				((TextView) mView.findViewById(R.id.noticeIcon)).setVisibility(View.VISIBLE);
				((TextView) mView.findViewById(R.id.btnConfirm)).setVisibility(View.GONE);
				((TextView) mView.findViewById(R.id.btnCancle)).setVisibility(View.GONE);
				btnAgree.setVisibility(View.VISIBLE);
				btnAgree.setText(HP_Manager.mContext.getResources().getString(R.string.popup_btn_ok));
				btnAgree.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						exitNotifyPopup();
					}
				});
				
				HP_Manager.mWidgetMap = false;
				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME) {
					Launcher_Main.reflashWidget1();
				}
			}
			super.handleMessage(msg);
		}
	};
}