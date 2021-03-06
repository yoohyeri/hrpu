package com.telechips.android.tdmb;

import java.io.File;
import java.util.List;

import com.mdstec.android.tpeg.TpegService;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.Launcher_Main;
import com.mobilus.hp.launcher.MAP_INFO;
import com.mobilus.hp.mapupdate.ApplicationManager;
import com.mobilus.hp.mapupdate.GINIAPPInfo;
import com.mobilus.hp.mapupdate.OnInstalledPackaged;
import com.mobilus.hp.popup.NaviMenuDialog;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.setting.Setting_TitleBar;
import com.mobilus.hp.setting.screen.Screen_LCD;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.BatteryStats.HistoryPrinter;
import android.util.Log;
import android.view.View;

public class GiniBroadcastReceiver extends BroadcastReceiver {
	private static final String CLASS_NAME = "[GiniBroadcastReceiver ]  ";

	/**
	 * Check Action
	 */
	public static final String ACTION_MOUNTED = "android.intent.action.MEDIA_MOUNTED";

	/**
	 * APK Replaced
	 */
	public static final String ACTION_APK_REPLACED = "android.intent.action.PACKAGE_REPLACED";
	
	private Handler mHandler = new Handler();
	private static Context mContext;
	private static String pathStorage = "";

	public static int mCntMenuPressed;
	public static boolean mMapUpdateMode;
	public static int mMapLifeCycle = -1;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		mContext = context;

		if (action.equals(ACTION_MOUNTED)) {
			String _uri = intent.getDataString();
			if ((_uri == null) || (_uri.length() == 0))
				return;
			
			final String header = "file://";
			String pathStorage = _uri.replace(header, "");
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "path : " + pathStorage);

			String pathGini = pathStorage + GINIAPPInfo.FOLDER_NAME_GINI;
			
			mSetMapPath(pathGini);
		}else if(action.equals(intent.ACTION_BOOT_COMPLETED)){
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_BOOT_COMPLETED");
		}else if(action.equals(intent.ACTION_POST_QUICKBOOT)){
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_POST_QUICKBOOT");
		}else if(action.equals(intent.ACTION_QUICK_CLOCK)){
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_QUICK_CLOCK");
		}else if (action.equals(ACTION_APK_REPLACED)) {
			String packageName = intent.getData().getSchemeSpecificPart();

			if (packageName.equals(GINIAPPInfo.PACKAGE_NAME)) {
				String _appPath = mGetMapPath() + GINIAPPInfo.FILE_NAME;

				Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_APK_REPLACED / path:" + _appPath);

				File _mFile = new File(_appPath);
				if (_mFile.isFile()) {
					String _appRenamePath = mGetMapPath() + GINIAPPInfo.C_FILE_NAME;
					File _mRenameFile = new File(_appRenamePath);
					_mFile.renameTo(_mRenameFile);
				} else {
					Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_APK_REPLACED / none file....!!!");
				}
			}
		} else if (action.equals(MAP_INFO.MAP_TO_DEV)) {

			if (intent != null) {
				int wParam = intent.getIntExtra(MAP_INFO.CMD_MAIN, 0);
				int lParam = intent.getIntExtra(MAP_INFO.CMD_SUB, 0);
				String text = intent.getStringExtra(MAP_INFO.CMD_TEXT);

				switch (wParam) {
				case MAP_INFO.WM_USER_SET_WIDGET_ONOFF:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / WM_USER_SET_WIDGET_ONOFF >> lParam : " + lParam + ", text : " + text);
					
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 3");
					handler.removeMessages(MSG_CHECK_NAVI_START);
					
					if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
					{
						if(NotifyDialog.mNotifyDialog != null)
							NotifyDialog.mNotifyDialog.setBtnEnable();
					}
					break;
				case MAP_INFO.BM_NOTIFY_HW_MENU:
					if (!LauncherMainActivity.isServiceRunningCheck()) {
						Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_HW_MENU ");
						
						
						//////// 191126 NTC Test
//						int evtCode = 0;
//						String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/TPEG/TPEGDATA/";
//						
//						evtCode = TpegService.IM_NTC;
//						file_path = file_path + TpegService.NTC_NAME;
//						
//						Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[EVT_NTC] // " + file_path);
//						
//						Intent _intent = new Intent();
//						_intent.setAction(MAP_INFO.DEV_TO_MAP);
//						_intent.putExtra(MAP_INFO.CMD_MAIN_1, evtCode);
//						_intent.putExtra(MAP_INFO.CMD_DATA_1, file_path);
//						HP_Manager.mContext.sendBroadcastAsUser(_intent, new UserHandle(UserHandle.USER_CURRENT));
						//////////////////////////////////////////////////////////////////////////////////////////////////
						
						
						Intent popup = null;
						popup = new Intent(mContext, NaviMenuDialog.class);
						mContext.startService(popup);
					}
					break;
				case MAP_INFO.BM_NOTIFY_DESTROY:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_DESTROY");
					if (!LauncherMainActivity.isServiceRunningCheck()) {
						handler.removeMessages(MSG_EXIT_NAVI_MENU);
						handler.sendEmptyMessage(MSG_EXIT_NAVI_MENU);
					}
					break;
				case MAP_INFO.BM_NOTIFY_MENU_MAIN:
				case MAP_INFO.BM_NOTIFY_MAP_TO_MENU:
				case MAP_INFO.BM_NOTIFY_MENU_TO_MAP:
					if (!LauncherMainActivity.isServiceRunningCheck()) {
						handler.removeMessages(MSG_EXIT_NAVI_MENU);
						handler.sendEmptyMessage(MSG_EXIT_NAVI_MENU);
					}
					break;
				case MAP_INFO.BM_START_VOICE_GUIDE:
					HP_Manager.mNaviSoundStart = true;
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "NAVI SOUND START / mNaviSoundStart : " + HP_Manager.mNaviSoundStart);
					if (HP_Manager.mCallback != null)
						HP_Manager.mCallback.onNaviGuidance(HP_Index.NAVI_SOUND_UNMUTE);
					break;
				case MAP_INFO.BM_END_OVERSPEED_SOUND:
				case MAP_INFO.BM_END_VOICE_GUIDE:
					HP_Manager.mNaviSoundStart = false;
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "NAVI SOUND END / mNaviSoundStart : " + HP_Manager.mNaviSoundStart);
					if (HP_Manager.mCallback != null)
						HP_Manager.mCallback.onNaviGuidance(HP_Index.NAVI_SOUND_MUTE);
					break;
				case MAP_INFO.BM_STATUS_VOLUME_MUTE:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME +  "Navi --> HW / BM_STATUS_VOLUME_MUTE : " + lParam );
//					break;
//				case MAP_INFO.BM_SET_GUIDE_MUTE:
//					if (NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
//						break;

					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_SET_GUIDE_MUTE : " + lParam + ", mNAVIMuteStatus : " + HP_Manager.mNAVIMuteStatus);
					
					HP_Manager.mNAVIMuteStatus = lParam;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_NAVI_MUTE_STATE);
					
					if (lParam == HP_Index.NAVI_SOUND_UNMUTE) {
						HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
						if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE) {
							if (LauncherMainActivity.iconMute.getVisibility() == View.VISIBLE)
								LauncherMainActivity.iconMute.setVisibility(View.INVISIBLE);
						} else {
							LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
//							Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "icon_navi_mute / VISIBLE 100 /" + Setting_TitleBar.mStartBlackbox );
							if (LauncherMainActivity.iconMute.getVisibility() == View.INVISIBLE )
							{
								Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "icon_navi_mute / VISIBLE 100 " );
								LauncherMainActivity.iconMute.setVisibility(View.VISIBLE);
							}
						}
					} else {
						if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE) {
							LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
							if (LauncherMainActivity.iconMute.getVisibility() == View.INVISIBLE)
							{
								LauncherMainActivity.iconMute.setVisibility(View.VISIBLE);
							}
						} else {
							HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_MUTE;
							LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
							if (LauncherMainActivity.iconMute.getVisibility() == View.INVISIBLE)
								LauncherMainActivity.iconMute.setVisibility(View.VISIBLE);
						}
					}
					break;
				case MAP_INFO.BM_NOTIFY_INIT_START:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_INIT_START >> lParam : " + lParam);
					mMapUpdateMode = false;
					
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "sendEmptyMessageDelayed MSG_CHECK_NAVI_START 1");
					handler.sendEmptyMessageDelayed(MSG_CHECK_NAVI_START, 10 * HP_Index.TIME_1_SECOND);
					break;
				case MAP_INFO.BM_NOTIFY_ACTIVATE:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_ACTIVATE >> mMapLifeCycle : " + mMapLifeCycle);
					mMapLifeCycle = lParam;
//					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 1");
//					handler.removeMessages(MSG_CHECK_NAVI_START);
					
					if (lParam == MAP_INFO.BM_NOTIFY_ACTIVATE_ONCREATE) {
						Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "BM_NOTIFY_ACTIVATE_ONCREATE / mIsRebooting : " + HP_Manager.mIsRebooting );
						LauncherMainActivity.onChangeMuteIcon();
						HP_Manager.mWidgetMap = true;
						
						if (NotifyDialog.mNotifyDialog != null) {
							NotifyDialog.mNotifyDialog.removeMsgBtnEnable();
						}
					}
					else if(lParam == MAP_INFO.BM_NOTIFY_ACTIVATE_ONPAUSE)
					{
						LauncherMainActivity.mForgroundNavi = false;
						if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_HOME && NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_KILL_POPUP) {
							NotifyDialog.mNotifyDialog.mHandler.removeMessages(NotifyDialog.MSG_LOADING_POPUP_TIMEOUT);
						
							if(LauncherMainActivity.isServiceRunningCheck())
							{
								Intent popup = null;
								popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
								popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								HP_Manager.mContext.stopService(popup);
							}
						}
					}
					break;
				case MAP_INFO.BM_NOTIFY_INIT_END:
//					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_INIT_END >> mNotifyDialog : " + NotifyDialog.mNotifyDialog);
					
					if (NotifyDialog.mNotifyDialog != null)
						NotifyDialog.mNotifyDialog.removeMsgBtnEnable();
					
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 2");
					handler.removeMessages(MSG_CHECK_NAVI_START);
					break;
				case MAP_INFO.BM_NOTIFY_NAVI_UPDATE: ////////// 맵업데이트
					mMapUpdateMode = true;
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_NAVI_UPDATE >> mCurrentPopup : " + NotifyDialog.mCurrentPopup);

					if(LauncherMainActivity.isServiceRunningCheck() && NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP )
					{
						
					}
					else
					{
						Intent _intent = new Intent(mContext, NotifyDialog.class);
						_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.stopService(_intent);
						
						LauncherMainActivity.getInstance().mShowNotiDialog(NotifyDialog.NAVI_START_POPUP);
					}
					
					break;
				case MAP_INFO.BM_NOTIFY_DESTROY_END:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_DESTROY_END >> mMapUpdateMode : "+ mMapUpdateMode);
					if(mMapUpdateMode)
					{	
						File _folder = new File(mGetMapPath());
						if (_folder.isDirectory()) { // MAP 폴더 있음
							String appPath = mGetMapPath() + GINIAPPInfo.FILE_NAME;
							File _fileAtlan = new File(appPath);
							mAppUpdate(context, _fileAtlan);
						} else {
							if(LauncherMainActivity.isServiceRunningCheck() && NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_START_POPUP )
								LauncherMainActivity.getInstance().mHideNotiDialog();
							mMapUpdateMode = false;
						}
					}
					else
					{
						Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "sendEmptyMessageDelayed MSG_CHECK_NAVI_START 2");
						handler.sendEmptyMessageDelayed(MSG_CHECK_NAVI_START, 5 * HP_Index.TIME_1_SECOND);
					}
					break;
				case MAP_INFO.BM_NOTIFY_WARNING_START: // 경고창이 디스플레이 된 후 동의함버튼 선택이 가능한 시점에 전송
					HP_Manager.mWidgetMap = true;
					Launcher_Main.reflashWidget1();
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_WARNING_START");
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_WARNING_CLOSED, 0, 0);
					break;
				case MAP_INFO.BM_NOTIFY_WARNINGAGREE_END:
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_WARNINGAGREE_END >> mFuel : " + HP_Manager.mFuel);
					try {
						if(Screen_LCD.isAutoIllum)
						{
							if(LauncherMainActivity.M_MTX.getStateIllumination())
								HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
							else
								HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					
					Launcher_Main.startServiceBind();
					
					if(HP_Manager.mLastMode == HP_Index.LAST_MODE_NAVI_FULL)
						mShowNaviFull();
					else
					{
//						Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "isInstallBlackbox : "+LauncherMainActivity.getInstance().isInstallBlackbox);
						if(LauncherMainActivity.getInstance()!= null)
							HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_HIDE, 0, 0);
					
						//200204 yhr
						Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "sendEmptyMessageDelayed MSG_CHECK_NAVI_START 3");
						handler.sendEmptyMessageDelayed(MSG_CHECK_NAVI_START, 5 * HP_Index.TIME_1_SECOND);
					}
					
					// 190828 userdata 삭제 시 유종 유지 안되는 현상으로인해 추가
					if(HP_Manager.mFuel == HP_Index.FUEL_DIESEL)
					{
						SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "0");
						HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_DIESEL, 0);
					}
					else if(HP_Manager.mFuel == HP_Index.FUEL_ELECTRIC)
					{
						SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "1");
						HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_ELECTRIC, 0);
					}
					else if(HP_Manager.mFuel == HP_Index.FUEL_LPG)
					{
						SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "2");
						HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_LPG, 0);
					}
					break;
				case MAP_INFO.BM_NOTIFY_HIDE:
					mMapUpdateMode = false;
					Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_HIDE >> mNAVIMuteStatus : " + HP_Manager.mNAVIMuteStatus
							+ ", Last : " + HP_Manager.mLastMode);
					
					if (NotifyDialog.mNotifyDialog == null)
					{
						Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + "NotifyDialog is null...");
						break;
					}
					
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 1");
					handler.removeMessages(MSG_CHECK_NAVI_START);
					
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							if (NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP) {
								
								//200214
								if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL || HP_Manager.mLastMode == HP_Index.LAST_MODE_HOME)
								{
//									Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 1");
//									handler.removeMessages(MSG_CHECK_NAVI_START);
								
									if(NotifyDialog.mNotifyDialog != null)
										NotifyDialog.mNotifyDialog.setBtnEnable();
								}
								
							} else if (NotifyDialog.mCurrentPopup == NotifyDialog.SDCARD_LOAD_POPUP ||
									NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_START_POPUP) {
								
								if(LauncherMainActivity.isNotifyDialogcheck())
								{
									Intent _intent = new Intent(mContext, NotifyDialog.class);
									_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
									mContext.stopService(_intent);
									
									//200214 yhr
//									if(LauncherMainActivity.getInstance().mExistBlackBoxFile && LauncherMainActivity.getInstance().isInstallBlackbox == false)
//									{
//										LauncherMainActivity.getInstance().mShowNotiDialog(NotifyDialog.BLACKBOX_INSTALL_FAIL);
//										LauncherMainActivity.getInstance().mExistBlackBoxFile = false;
//										Log.d(HP_Manager.TAG_LAUNCHER, "mExistBlackBoxFile 3");
//									}
								}
							}
						}
					}, 1000);
					
					break;
				case MAP_INFO.BM_CHANGE_DAY_TO_NIGHT: // 주간 --> 야간
					break;
				case MAP_INFO.BM_CHANGE_NIGHT_TO_DAY: // 야간 --> 주간
					break;
				default:
					break;
				}
			}
		} else {
			int wParam = intent.getIntExtra(MAP_INFO.CMD_MAIN_1, 0);
			String strwParam = String.valueOf(wParam);
			if (strwParam.length() == 4) {
				int lParam = intent.getIntExtra(MAP_INFO.CMD_DATA_1, 0);
				int text = intent.getIntExtra(MAP_INFO.CMD_DATA_2, 0);
				Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + "HW --> Navi / wParam : " + wParam + ", lParam : " + lParam 
													  + ", text : " + text + " curretn popup : " + NotifyDialog.mCurrentPopup);
				// 200204 yhr
				if(wParam == MAP_INFO.WM_USER_SET_WIDGET_ONOFF)
				{
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 3");
					handler.removeMessages(MSG_CHECK_NAVI_START);
					
					if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
					{
						if(NotifyDialog.mNotifyDialog != null)
							NotifyDialog.mNotifyDialog.setBtnEnable();
					}
				}
				
				// 200204 yhr
//				if(wParam == MAP_INFO.WM_USER_SET_HIDE)
//				{
////					if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL || HP_Manager.mLastMode == HP_Index.LAST_MODE_HOME)
//					if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL)
//					{
//						Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "removeMessages MSG_CHECK_NAVI_START 1");
//						handler.removeMessages(MSG_CHECK_NAVI_START);
//					
//						if(NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP)
//						{
//							if(NotifyDialog.mNotifyDialog != null)
//								NotifyDialog.mNotifyDialog.setBtnEnable();
//						}
//					}
//				}
				
				if(wParam == MAP_INFO.WM_USER_SET_WIDGET_ONOFF && NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_KILL_POPUP) {
					NotifyDialog.mNotifyDialog.mHandler.removeMessages(NotifyDialog.MSG_LOADING_POPUP_TIMEOUT);
					
					if(LauncherMainActivity.isServiceRunningCheck())
					{
						Intent popup = null;
						popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
						popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						HP_Manager.mContext.stopService(popup);
					}
				}
				
				//200203 yhr
				if(wParam == MAP_INFO.WM_USER_SET_WIDGET_ONOFF && HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL)
				{
					try {
						HP_Manager.mCallback.onChangeDMBView();
					} catch (IllegalStateException e) {
						// TODO: handle exception
					}
				}
			}
		}
	}

	private void mShowNaviFull()
	{
		mMapUpdateMode = false;
		
		//200204 yhr
		handler.removeMessages(MSG_CHECK_NAVI_START);
		
		if (NotifyDialog.mNotifyDialog == null)
			return; 

		Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + "mShowNaviFull() // BM_NOTIFY_WARNINGAGREE_END 2 // Last : " + HP_Manager.mLastMode
				+ ", NotifyDialog.mCurrentPopup : " + NotifyDialog.mCurrentPopup);
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP) {
					
				} else if (NotifyDialog.mCurrentPopup == NotifyDialog.SDCARD_LOAD_POPUP ||
						NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_START_POPUP) {

					if(LauncherMainActivity.isNotifyDialogcheck())
					{
						Intent _intent = new Intent(mContext, NotifyDialog.class);
						_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						mContext.stopService(_intent);
						Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + "=========stopService");
						
						//200214 yhr
//						if(LauncherMainActivity.getInstance().mExistBlackBoxFile && LauncherMainActivity.getInstance().isInstallBlackbox == false)
//						{
//							LauncherMainActivity.getInstance().mShowNotiDialog(NotifyDialog.BLACKBOX_INSTALL_FAIL);
//							LauncherMainActivity.getInstance().mExistBlackBoxFile = false;
//							Log.d(HP_Manager.TAG_LAUNCHER, "mExistBlackBoxFile 2");
//						}
					}
				}
			}
		}, 1000);
		
		if (NotifyDialog.mCurrentPopup == NotifyDialog.TRAFFIC_RULE_POPUP) {
			if(NotifyDialog.mNotifyDialog != null)
				NotifyDialog.mNotifyDialog.setBtnEnable();
		}
		
		if(NotifyDialog.mCurrentPopup == NotifyDialog.NAVI_KILL_POPUP) {
			NotifyDialog.mNotifyDialog.mHandler.removeMessages(NotifyDialog.MSG_LOADING_POPUP_TIMEOUT);
			
			if(LauncherMainActivity.isServiceRunningCheck())
			{
				Intent popup = null;
				popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
				popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				HP_Manager.mContext.stopService(popup);
			}
		}
	}

	/**
	 * mIsAppRunning
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2015. 6. 22.
	 * @Description :
	 *
	 * @param _context
	 * @return
	 */
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

	public boolean mIsAppInstalled(Context _context) {
		PackageManager installedPages = _context.getPackageManager();
		List<PackageInfo> packageList = installedPages.getInstalledPackages(PackageManager.GET_PERMISSIONS);

		for (PackageInfo pi : packageList) {
			if (pi.applicationInfo.packageName.equals(GINIAPPInfo.PACKAGE_NAME)) {
				return true;
			}
		}
		return false;
	}

	public String mGetMapPath() {
		return pathStorage;
	}
	
	public void mSetMapPath(String path) {
		pathStorage = path;
	}
	

	/**
	 * mAppUpdate
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2015. 6. 22.
	 * @Description :
	 *
	 * @param _context
	 * @param _file_name
	 */

	private Context _mContext;
	private File mFile;
	public void mAppUpdate(Context _context, File _file) {
		_mContext = _context;
		mFile = _file;

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				appUpdate(_mContext, mFile);
			}
		}, 3000);
	}

	private void appUpdate(Context _context, File _file) {
		Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "##### appUpdate #####");
		try {
			final ApplicationManager am = new ApplicationManager(_context);
			am.setOnInstalledPackaged(new OnInstalledPackaged() {
				public void packageInstalled(String packageName, int returnCode) {
					if (returnCode == ApplicationManager.INSTALL_SUCCEEDED) {
						File _mFile = new File(mGetMapPath() + GINIAPPInfo.FILE_NAME);

						Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME +  "##### _mFile : " + mGetMapPath() + GINIAPPInfo.FILE_NAME);
						if (_mFile.isFile()) {
							File _mRenameFile = new File(mGetMapPath() + GINIAPPInfo.FILE_NAME);
							_mFile.renameTo(_mRenameFile);
						} else {
							Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME +  "###### appUpdate : none File");
						}

						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								// Toast.makeText(mContext,
								// R.string.popup_msg_install_success,
								// Toast.LENGTH_LONG).show();
							}
						}, 1);

					} else {
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// Toast.makeText(mContext,
								// R.string.popup_msg_install_fail,
								// Toast.LENGTH_LONG).show();
							}
						}, 1);
					}
					
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  mStartNavi 5");
							LauncherMainActivity.getInstance().mStartNavi(true);
						}
					}, 1);
				}
			});
			am.installPackage(_file);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public final static int MSG_MUTE_OFF = 0;
	public final static int MSG_SHOW_NAVI_MENU = 1;
	public final static int MSG_EXIT_NAVI_MENU = 2;
	public final static int MSG_CHECK_NAVI_START = 3;
	public static Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Intent popup = null;
			popup = new Intent(mContext, NaviMenuDialog.class);

			if (msg.what == MSG_MUTE_OFF) {
				removeMessages(MSG_MUTE_OFF);

				if (HP_Manager.mCallback != null)
					HP_Manager.mCallback.onNaviGuidance(HP_Index.NAVI_SOUND_MUTE);
			} else if (msg.what == MSG_SHOW_NAVI_MENU) {
				removeMessages(MSG_SHOW_NAVI_MENU);

				if (!LauncherMainActivity.isServiceRunningCheck()) {
					Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "Navi --> HW / BM_NOTIFY_HW_MENU ");
					mContext.startService(popup);
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SHOW_DEVICE_MENU, 0, 0);
				}
			} else if (msg.what == MSG_EXIT_NAVI_MENU) {
				Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME +  "Navi --> HW / MSG_EXIT_NAVI_MENU ");
				removeMessages(MSG_EXIT_NAVI_MENU);
				if (HP_Manager.mShowNaviMenuPopup)
					mContext.stopService(popup);
			} else if (msg.what == MSG_CHECK_NAVI_START) {
				Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME +  "Navi --> HW / MSG_CHECK_NAVI_START ");
				removeMessages(MSG_CHECK_NAVI_START);
				
				if (NotifyDialog.mNotifyDialog != null)
					NotifyDialog.mNotifyDialog.setBtnEnable();
				
				HP_Manager.mWidgetMap = false;
				
				/////////////////////////////////////////////
				// MAP 구동 실패 팝업 추가 예정
				if(LauncherMainActivity.getInstance().mDefaultDialog != null)
				{
					Log.d(HP_Manager.TAG_LAUNCHER, "memory_card_check 5");
					LauncherMainActivity.mForgroundNavi = false;
					GiniBroadcastReceiver.mMapLifeCycle = MAP_INFO.BM_NOTIFY_ACTIVATE_ONDESTROY;
					LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,
							HP_Manager.mContext.getResources().getString(R.string.memory_card_check));
					
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CLOSE, 0, 0);
					Launcher_Main.reflashWidget1();
				}
			}
			super.handleMessage(msg);
		}
	};
}