package com.mobilus.hp.launcher;

import java.util.List;

import com.mnsoft.navi.ISurfaceInterface;
import com.mobilus.hp.popup.NaviMenuDialog;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.setting.screen.Screen_LCD;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class Launcher_Main extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[Launcher_Main ]  ";

	private static final int SEND_TO_SERVICE_CREATE = 0;
	private static final int SEND_TO_SERVICE_CHANGE = 1;
	private static final int SEND_TO_SERVICE_DESTROY = 2;
	private static final int MSG_ON_CLICK_EVENT = 3;
	private static final int MSG_CHANGE_SCREEN_SAVER = 4;
	
	private static final int SEND_TO_SERVICE_TIMEOUT = 500;

	// Butteon
	private static TextView btnPrevCH,btnNextCH;
	private TextView btnScreenOnOff, btnSetting;
	public static TextView btnDMBMuteOnOff, btnDMBOnOff;

	// MAP
	public static SurfaceView mWidgetMap;
	private static SurfaceHolder mWidgetMapHolder;
	public static Surface mSurface = null;

	// Widget
	private SurfaceView mWidgetDMBVideo;
	private static FrameLayout mWidgetClock;

	public Launcher_Main() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_launcher, container, false);

		return inflater.inflate(R.layout.fragment_launcher, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mHandler.removeMessages(SEND_TO_SERVICE_CREATE);
		mHandler.removeMessages(SEND_TO_SERVICE_CHANGE);
		mHandler.removeMessages(SEND_TO_SERVICE_DESTROY);
	}

	@Override
	public void onPause() {
		mHandler.removeMessages(MSG_ON_CLICK_EVENT);
		super.onPause();
	}

	@Override
	public void onResume() {
		if (HP_Manager.mShowNaviMenuPopup) {
			Intent popup = null;
			popup = new Intent(HP_Manager.mContext, NaviMenuDialog.class);
			HP_Manager.mContext.stopService(popup);
		}
		super.onResume();
	}

	public void onStart() {
		mClickEvnetCnt = -1;
		initMAPSurface();
		setLoadView();
		super.onStart();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	public static boolean mNaviBind = false;
	public static boolean mServiceConnected;
	private static ServiceConnection mMnSoftNaviConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mServiceConnected = true;
			if (service != null) {
				HP_Manager.iNaviService = ISurfaceInterface.Stub.asInterface(service);
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mServiceConnected = false;
			try{
				Launcher_Main.stopServiceBind();
			}catch(IllegalArgumentException e){			

			}
			
			if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_MAP)
				HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			
			Intent popup = null;
			popup = new Intent(HP_Manager.mContext, NotifyDialog.class);
			
			// 190801 yhr
			if(LauncherMainActivity.isServiceRunningCheck()&& NotifyDialog.mCurrentPopup != NotifyDialog.REBOOTING_POPUP)
			{
				popup.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				HP_Manager.mContext.stopService(popup);
			}
			
			if(HP_Manager.mProductionProcess == false && NotifyDialog.mCurrentPopup != NotifyDialog.REBOOTING_POPUP) {
				
				//190926
				if(NotifyDialog.mCurrentPopup == NotifyDialog.UPDATE_FILE_COPY)
					return;
				/////
				
				NotifyDialog.mCurrentPopup = NotifyDialog.NAVI_KILL_POPUP;
				HP_Manager.mContext.startService(popup);
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  mStartNavi 6");
				LauncherMainActivity.getInstance().mStartNavi(true);
			}
		}
	};

	public static void startServiceBind() {
		if (HP_Manager.mContext == null)
			return;

		Intent intent = new Intent();
		intent.setClassName("com.mnsoft.navi", "com.mnsoft.navi.SurfaceInterface");
		if (!mNaviBind) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "Start Service");
			mNaviBind = HP_Manager.mContext.bindService(intent, mMnSoftNaviConnection,
					Context.BIND_AUTO_CREATE | Context.BIND_NOT_FOREGROUND);
		}
	}

	public static void stopServiceBind() {
		if (HP_Manager.iNaviService != null) {
			if (mNaviBind && isConnectedService()) {
				Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "stopServiceBind");
				HP_Manager.mContext.unbindService(mMnSoftNaviConnection);
				mNaviBind = false;
				HP_Manager.iNaviService = null;
			}
		}
	}

	public static boolean isConnectedService() {
		boolean isConnected = false;
		ActivityManager activity_manager = (ActivityManager) HP_Manager.mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> service_info = activity_manager.getRunningServices(100);

		for (int i = 0; i < service_info.size(); i++) {
			if (service_info.get(i).service.getPackageName().equals("com.mnsoft.navi"))
				isConnected = true;
		}
		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "isConnectedService : " + isConnected);
		return isConnected;
	}

	private void setLoadView() {
		mWidgetClock = (FrameLayout) HP_Manager.mContext.findViewById(R.id.fragment_clock);
		mWidgetDMBVideo = (SurfaceView) HP_Manager.mContext.findViewById(R.id.dmb_widget_touch_view);
		mWidgetClock.setVisibility(View.GONE);

		btnPrevCH = (TextView) HP_Manager.mContext.findViewById(R.id.btnPrevChannel);
		btnDMBMuteOnOff = (TextView) HP_Manager.mContext.findViewById(R.id.btnDMBMuteOnOff);
		btnNextCH = (TextView) HP_Manager.mContext.findViewById(R.id.btnNextChannel);

		btnScreenOnOff = (TextView) HP_Manager.mContext.findViewById(R.id.btnScreenOnOff);
		btnDMBOnOff = (TextView) HP_Manager.mContext.findViewById(R.id.btnDMBOnOff);
		btnSetting = (TextView) HP_Manager.mContext.findViewById(R.id.btnSetting);
		reflashWidget1();

		// DMB 위젯
		HP_Manager.outputWidget2();

		mWidgetDMBVideo.setOnClickListener(this);
		btnPrevCH.setOnClickListener(this);
		btnNextCH.setOnClickListener(this);
		btnScreenOnOff.setOnClickListener(this);
		btnDMBMuteOnOff.setOnClickListener(this);
		btnDMBOnOff.setOnClickListener(this);
		btnSetting.setOnClickListener(this);

		changeMuteState(HP_Manager.mDMBMuteStatus);
		changeDMBVideoOnOff(HP_Manager.mDMBVideoOnOff);
		setChannelBtnEnable();	
	}

	private static void setChannelBtnEnable(){
		if(DxbPlayer.getScanCount() > 0)
		{
			if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
			{
				btnPrevCH.setEnabled(false);
				btnDMBMuteOnOff.setEnabled(false);
				btnNextCH.setEnabled(false);
				btnPrevCH.setAlpha((float) 0.5);
				btnDMBMuteOnOff.setAlpha((float) 0.5);
				btnNextCH.setAlpha((float) 0.5);
			}
			else
			{
				btnPrevCH.setEnabled(true);
				btnDMBMuteOnOff.setEnabled(true);
				btnNextCH.setEnabled(true);
				btnPrevCH.setAlpha(1);
				btnDMBMuteOnOff.setAlpha(1);
				btnNextCH.setAlpha(1);
			}
		}
		else
		{
			btnPrevCH.setEnabled(false);
			btnDMBMuteOnOff.setEnabled(false);
			btnNextCH.setEnabled(false);
			btnPrevCH.setAlpha((float) 0.5);
			btnDMBMuteOnOff.setAlpha((float) 0.5);
			btnNextCH.setAlpha((float) 0.5);
		}
	}
	
	private void initMAPSurface() {
		mWidgetMap = (SurfaceView) HP_Manager.mContext.findViewById(R.id.surface_map);
		mWidgetMap.setVisibility(View.GONE);
		if (mWidgetMap == null) {
			return;
		}

		mWidgetMap.setOnClickListener(this);
		mWidgetMapHolder = mWidgetMap.getHolder();

		if (mWidgetMapHolder == null) {
			Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SurfaceHolder is Null...");
			return;
		}
		mWidgetMapHolder.addCallback(mSurfaceCallBack);
	}

	/**
	 * Surface Callback (MAP Widget)
	 */
	private SurfaceHolder.Callback mSurfaceCallBack = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceChanged // w : " + w + ", h : " + h + ", mNaviBind : " + mNaviBind);
			try {
				if (HP_Manager.iNaviService != null) {
					if (mSurface != null)
						HP_Manager.iNaviService.surfaceChanged(mSurface.toString(), format, w, h);
					else
						Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceChanged // mSurface is null... ");
				} else {
					Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceChanged // iNaviService is null... ");

					Bundle data = new Bundle();
					data.putInt("format", format);
					data.putInt("w", w);
					data.putInt("h", h);

					Message msg = new Message();
					msg.what = SEND_TO_SERVICE_CHANGE;
					msg.setData(data);

					mHandler.sendMessageDelayed(msg, SEND_TO_SERVICE_TIMEOUT);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurface = mWidgetMap.getHolder().getSurface();
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceCreated // " + mSurface + ", mWidgetMap.getVisibility() : "
					+ mWidgetMap.getVisibility() + ", iNaviService  : " + HP_Manager.iNaviService + ", mNaviBind : " + mNaviBind);

			try {
				if (HP_Manager.iNaviService != null) {
					if (mSurface != null) {
						if (HP_Manager.mWidgetMap) {
							HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_WIDGET_ONOFF, MAP_INFO.NAVI_WIDGET_ON, 0);
							
							if(Screen_LCD.isAutoIllum)
							{
								if(!LauncherMainActivity.M_MTX.getStateIllumination()) {	// 주간
									HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
								} else {	// 야간
									HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
								}	
							}
							else
							{
								if(Screen_LCD.isManualDayLightMode)
									HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.NIGHT_MODE, 0);
								else
									HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_DAY_MODE, MAP_INFO.DAY_MODE, 0);
							}
						}
						HP_Manager.iNaviService.surfaceCreated(mSurface);
					}
				} else {
					mHandler.sendEmptyMessageDelayed(SEND_TO_SERVICE_CREATE, SEND_TO_SERVICE_TIMEOUT);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceDestroyed // mNaviBind : " + mNaviBind);
			try {
				if (HP_Manager.iNaviService != null) {
					if (mSurface != null) {

						// if(HP_Manager.mWidgetMap)
						{
							HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_WIDGET_ONOFF, MAP_INFO.NAVI_WIDGET_OFF, 0);
						}
						HP_Manager.iNaviService.surfaceDestroyed(mSurface.toString());
					} else
						Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME +  "MAP : surfaceDestroyed // mSurface is null... ");
				} else {
					Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "MAP : surfaceDestroyed // iNaviService is null... ");
					mHandler.sendEmptyMessageDelayed(SEND_TO_SERVICE_DESTROY, SEND_TO_SERVICE_TIMEOUT);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mSurface = null;
		}
	};

	public static void reflashWidget1() {
		if (mWidgetMap == null)
			return;
		
		if (mWidgetClock == null)
			return;

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_HOME)
			return;

		Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "reflashWidget1");
		
		// 시계, 맵 위젯
		if (HP_Manager.mWidgetMap) {
			if(mWidgetMap.getVisibility() == View.VISIBLE)
				return;
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "########################### WIDGET - MAP ###########################");
			mWidgetClock.setVisibility(View.GONE);
			mWidgetMap.setVisibility(View.VISIBLE);
		} else {
			if(mWidgetClock.getVisibility() == View.VISIBLE)
				return;
			
			if(LauncherMainActivity.mForgroundNavi)
				LauncherMainActivity.mForgroundNavi = false;
			
			Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "########################### WIDGET - CLOCK ###########################");
			mWidgetMap.setVisibility(View.GONE);
			mWidgetClock.setVisibility(View.VISIBLE);
			HP_Manager.outputWidget1(HP_Index.CLOCK_WIDGET_MODE);
			
			//200304 yhr
			if(HP_Manager.mLastMode == HP_Index.LAST_MODE_DMB_FULL)
			{
				try {
					HP_Manager.mCallback.onChangeDMBView();
				} catch (IllegalStateException e) {
					// TODO: handle exception
				}
			}
		}
	}

	private int id;
	private static int mClickEvnetCnt;
	@Override
	public void onClick(View view) {
		mHandler.removeMessages(MSG_ON_CLICK_EVENT);
		id = view.getId();
		switch (id) {
		case R.id.btnPrevChannel:
			if (DxbPlayer.getScanCount() > 0)
				HP_Manager.mCallback.onChangeChannel(HP_Index.CHANNEL_PREV);
			return;
		case R.id.btnNextChannel:
			if (DxbPlayer.getScanCount() > 0)
				HP_Manager.mCallback.onChangeChannel(HP_Index.CHANNEL_NEXT);
			return;
		default:
			break;
		} 
		
		mClickEvnetCnt++;
		if(mClickEvnetCnt == 0)
			mHandler.sendEmptyMessage(MSG_ON_CLICK_EVENT);
		else
			mHandler.sendEmptyMessageDelayed(MSG_ON_CLICK_EVENT, 300);
	}

	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			Bundle data = msg.getData();

			switch (msg.what) {
			case SEND_TO_SERVICE_CREATE:
				try {
					removeMessages(SEND_TO_SERVICE_CREATE);
					
					if (HP_Manager.iNaviService != null) {
						if (mSurface != null) {
							HP_Manager.iNaviService.surfaceCreated(mSurface);
							if (HP_Manager.mWidgetMap) {
								HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_WIDGET_ONOFF, MAP_INFO.NAVI_WIDGET_ON, 0);
							}
						} 
						else
						{
							HP_Manager.mWidgetMap = false;
							Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SEND_TO_SERVICE_CREATE // mSurface is null... ");
						}
					}
					else
					{
						HP_Manager.mWidgetMap = false;
						Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SEND_TO_SERVICE_CREATE // iNaviService is null... ");
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (ExceptionInInitializerError e) {
					Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SEND_TO_SERVICE_CREATE // ExceptionInInitializerError");
				}
				break;
			case SEND_TO_SERVICE_CHANGE:
				removeMessages(SEND_TO_SERVICE_CHANGE);
				try {
					if (HP_Manager.iNaviService != null) {
						if (mSurface != null)
							HP_Manager.iNaviService.surfaceChanged(mSurface.toString(), data.getInt("format"),
									data.getInt("w"), data.getInt("h"));
						else
							Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SEND_TO_SERVICE_CHANGE // mSurface is null... ");
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			case SEND_TO_SERVICE_DESTROY:
				removeMessages(SEND_TO_SERVICE_DESTROY);
				try {
					if (HP_Manager.iNaviService != null) {
						if (mSurface != null)
							HP_Manager.iNaviService.surfaceDestroyed(mSurface.toString());
						else
							Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "SEND_TO_SERVICE_DESTROY // mSurface is null... ");
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			case MSG_CHANGE_SCREEN_SAVER:
				removeMessages(MSG_CHANGE_SCREEN_SAVER);
				HP_Manager.mCallback.onChangeScreenSaverFullMode();
				break;
			case MSG_ON_CLICK_EVENT:
				removeMessages(MSG_ON_CLICK_EVENT);
				
				if(mClickEvnetCnt > 0)
					mClickEvnetCnt = -1;
				switch (id) {
				case R.id.surface_map:
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_MAP;
					
					//200203 yhr
					HP_Manager.mLastMode = HP_Index.LAST_MODE_NAVI_FULL;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
					
					HP_Manager.mCallback.onChangeMAPView();
					break;
				case R.id.dmb_widget_touch_view:
					removeMessages(MSG_CHANGE_SCREEN_SAVER);
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_DMB;
					
					//200203 yhr
					HP_Manager.mLastMode = HP_Index.LAST_MODE_DMB_FULL;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
					
					// 200220 yhr
					if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
					{
						HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
						HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
						
						if(DxbView_Normal.mfromUserMute == false)
						{
							HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
							DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
							HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
							changeMuteState(HP_Manager.mDMBMuteStatus);
						}
					}
					
					try {
						HP_Manager.mCallback.onChangeDMBView();
					} catch (IllegalStateException e) {
						// TODO: handle exception
					}
					
					break;
				case R.id.btnScreenOnOff:
					sendEmptyMessageDelayed(MSG_CHANGE_SCREEN_SAVER, 100);
					break;
				case R.id.btnDMBOnOff:
					if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
					{
						HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_OFF;
						HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
						
						DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
					}
					else
					{
						HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
						HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
						
						if(DxbView_Normal.mfromUserMute == false)
							DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
					}
					changeDMBVideoOnOff(HP_Manager.mDMBVideoOnOff);
//					changeMuteState(HP_Manager.mDMBMuteStatus);
					DxbView.updateScreen();
					
					break;
				case R.id.btnDMBMuteOnOff:
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
					
					changeMuteState(HP_Manager.mDMBMuteStatus);
					
					break;
				case R.id.btnSetting:	
					if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER)
						return;
					
					HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
					HP_Manager.mCallback.onChangeSettingView();
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public static void changeMuteState(int state) {
		
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"changeMuteState / HP_Manager.mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus);
		
		if (state == HP_Index.DMB_SOUND_MUTE) {
//			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
//				btnDMBMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.home_btn_mute_on));
//			else
//				btnDMBMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_home_btn_mute_on));
		
			if(HP_Manager.mWidgetMap)
			{
				if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE)
					LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
				else
					LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
			}
			else
				LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
			
			HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.VISIBLE);
			DxbView_Normal.gComponent.mSeekBar.setProgress(0);
		} else {
//			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
//				btnDMBMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.home_btn_mute_off));
//			else
//				btnDMBMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_home_btn_mute_off));
		
			if(HP_Manager.mWidgetMap)
			{
				if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE)
				{
					LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_navi_mute));
					HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.VISIBLE);
				}
				else
					HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.INVISIBLE);
			}
			else
				HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.INVISIBLE);
			DxbView_Normal.gComponent.mSeekBar.setProgress(DxbView_Normal.getVolume());
		}
	}
	
	
	// 200220 yhr
	public static void changeDMBVideoOnOff(int state) {
		
		if (state == HP_Index.DMB_VIDEO_OFF) {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"changeDMBVideoOnOff / mDMBVideoOnOff : DMB_VIDEO_OFF" );
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				btnDMBOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_dmb_mute_on));
			else
				btnDMBOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_dmb_mute_on));
			
			if (DMB_Widget.mWeakSignalPopup != null) 
					DMB_Widget.mWeakSignalPopup.setVisibility(View.GONE);
			
			if (DMB_Widget.mWeakSignalFullModePopup != null) {
				DMB_Widget.mWeakSignalFullModePopup.setVisibility(View.GONE);
		}
			
		} else {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"changeDMBVideoOnOff / mDMBVideoOnOff : DMB_VIDEO_ON" );
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				btnDMBOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_dmb_mute_off));
			else
				btnDMBOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_dmb_mute_off));
		}
		setChannelBtnEnable();
	}
}
