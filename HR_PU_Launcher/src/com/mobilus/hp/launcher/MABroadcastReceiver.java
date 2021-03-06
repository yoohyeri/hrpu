package com.mobilus.hp.launcher;

import java.io.IOException;

import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.screensaver.ScreenSaverActivity;
import com.mobilus.hp.setting.Setting_TitleBar;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.LauncherMainActivity;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

public class MABroadcastReceiver extends BroadcastReceiver {
	private static final String CLASS_NAME = "[MABroadcastReceiver ]  ";

	public static final String ACTION_USER_SHORT_POWER_BUTTON = "android.intent.action.USER_SHORT_POWER_BUTTON";
	public static final String ACTION_USER_LONG_POWER_BUTTON = "android.intent.action.USER_LONG_POWER_BUTTON";

	public static final String ACTION_USER_ILL_START = "android.intent.action.USER_ILL_START";
	public static final String ACTION_USER_ILL_STOP = "android.intent.action.USER_ILL_STOP";
	public static final String ACTION_GPS_UPDATE = "android.intent.action.USER_GPS_UPDATE_START";
	
	public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";

	public final String POWER_KEY_SKIP = "tcc.mtx.power.key.skip";
	public final String REAR_SKIP = "tcc.mtx.rear.skip";
	public static int mReverseOnOff = HP_Index.REVERSE_ON;
	
//	public static boolean mInitialPower = true;

	@Override
	public void onReceive(Context context, Intent intent) {

		if (LauncherMainActivity.getInstance() == null)
			return;
		
		Screen_LCD _ScreenLCD = new Screen_LCD();
		if (intent.getAction().equalsIgnoreCase(ACTION_USER_SHORT_POWER_BUTTON)) {
			
			//캡쳐일때
			if(HP_Manager.mCaptureScreen) {
				LauncherMainActivity.getInstance().screenshot();
				return;
			}

			// 생산공정 프로그램모드에서는 ublox 제어
			if (HP_Manager.mProductionProcess == true) {
				if (LauncherMainActivity.getTopActivity().equals("com.ublox.ucenter")) {
					new Thread(new Runnable() {
						public void run() {
							new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
						}
					}).start();
				}

				if (SystemProperties.getBoolean(POWER_KEY_SKIP, true))
					return;

				return;
			}

			if (LauncherMainActivity.getTopActivity().equals("com.ublox.ucenter")) {
				new Thread(new Runnable() {
					public void run() {
						new Instrumentation().sendKeyDownUpSync(KeyEvent.KEYCODE_MENU);
					}
				}).start();
			} 
			else {

				Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_SHORT_POWER_BUTTON 2 / DMB Mute state ; " + HP_Manager.mDMBMuteStatus);
				
				//200615 yhr
//				if(mInitialPower)
//				{	
//					mInitialPower = false;
//					return;
//				}
				
				if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER) {
					
					HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;
					if (ScreenSaverActivity.getInstance() != null)
						((ScreenSaverActivity) ScreenSaverActivity.getInstance()).mExit();
				} else {
					if (HP_Manager.mWidgetMap)	
					{
						Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_SHORT_POWER_BUTTON 3 / DMB Mute state ; " + HP_Manager.mDMBMuteStatus);
						LauncherMainActivity.getInstance().mSetSystemMute(HP_Manager.mDMBMuteStatus, HP_Manager.mNAVIMuteStatus);
					}
					else
					{
						Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_SHORT_POWER_BUTTON 4");
						if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
						{
							
							Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_SHORT_POWER_BUTTON 5 / DMB Mute state ; " + HP_Manager.mDMBMuteStatus);
							if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
							{
								DxbView_Normal.mfromUserMute = true;
								HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_MUTE;
								DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
								HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
							}
							else
							{
								DxbView_Normal.mfromUserMute = false;
								HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
								DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
								HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
							}
							Launcher_Main.changeMuteState(HP_Manager.mDMBMuteStatus);
						}
					}		 
				}
			}
		} else if (intent.getAction().equalsIgnoreCase(ACTION_USER_LONG_POWER_BUTTON)) {
			Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_LONG_POWER_BUTTON");
			if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER) {
				HP_Manager.mViewMode = HP_Index.CLOCK_WIDGET_MODE;
				if (ScreenSaverActivity.getInstance() != null)
					((ScreenSaverActivity) ScreenSaverActivity.getInstance()).mExit();
			}

		} else if(intent.getAction().equalsIgnoreCase(ACTION_USER_ILL_START)){
			//			Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_ILL_START");
			if(NotifyDialog.mNotifyDialog.mCurrentPopup != NotifyDialog.TRAFFIC_RULE_POPUP)
				_ScreenLCD.mSetIllumination();
		} else if(intent.getAction().equalsIgnoreCase(ACTION_USER_ILL_STOP)){
			//			Log.d(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_USER_ILL_STOP");
			_ScreenLCD.mSetIllumination();
		} else if(intent.getAction().equalsIgnoreCase(ACTION_BOOT_COMPLETED)){
//			Log.e(HP_Manager.TAG_BROADCAST, CLASS_NAME + " ==================================>> ACTION_BOOT_COMPLETED !!!!!!!!!!! / mInitialPower : " );
//			mInitialPower = false;
			
		} else if(intent.getAction().equalsIgnoreCase(ACTION_GPS_UPDATE)){
			NotifyDialog.mNotifyDialog.changeGPSUpdatePopup();

			try {
				LauncherMainActivity.M_MTX.enableTouch(false);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			new MAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} 
	}

	private class MAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			Process logcatProc = null;

			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=== GPS update Start ===");
			try {
				logcatProc = Runtime.getRuntime().exec("ubloxfwupdate -p /dev/ttyS3 -b 9600:9600:115200 -F /system/etc/flash.xml -a 1 -v 1 /system/etc/ubloxdrfw_4_11.bin");
				logcatProc.getErrorStream().close(); 
				logcatProc.getInputStream().close(); 
				logcatProc.getOutputStream().close();
				logcatProc.waitFor();
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=== GPS update Success === ");
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=== GPS IOException err : " + e.toString());
			} catch (InterruptedException e) {
				Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=== GPS InterruptedException err : " + e.toString());

			}
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "=== GPS update End ===");

			PowerManager pm = (PowerManager)HP_Manager.mContext.getSystemService(Context.POWER_SERVICE);
			pm.reboot("");

			return true;
		}

		@Override
		public void onPostExecute(Boolean _st) {
		}
	}
}
