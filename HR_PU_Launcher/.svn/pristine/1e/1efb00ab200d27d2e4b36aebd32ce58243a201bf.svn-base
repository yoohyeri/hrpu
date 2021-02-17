package com.mobilus.hp.popup;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;
import com.mobilus.hp.mapupdate.GINIAPPInfo;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.GiniBroadcastReceiver;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

public class NaviMenuDialog extends Service implements OnClickListener{

	final public String CLASS_NAME = "[NaviMenuDialog ]  ";

    private View mView;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams  mParams;
    
	private TextView btnHome, btnDMB;
	
	public static int mChangeView = -1;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		onSetWindowManager();
		mHandlerDeviceMenu.postDelayed(mRunnableDeviceMenu, 200);
	}

	@Override
	public void onDestroy() {
		mHandlerDeviceMenu.removeCallbacks(mRunnableDeviceMenu);
		HP_Manager.mShowNaviMenuPopup = false;
		GiniBroadcastReceiver.mCntMenuPressed = 0;
		
		try {
			mWindowManager.removeView(mView);
	        mWindowManager = null;
		} catch (NullPointerException e) {
		}		
		super.onDestroy();
	}
	
	@SuppressWarnings("deprecation")
	private void onSetWindowManager()
	{
		HP_Manager.mShowNaviMenuPopup = true;
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		mParams = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.FILL_PARENT,
		        WindowManager.LayoutParams.FILL_PARENT,
		        WindowManager.LayoutParams.TYPE_PHONE, // 항상 최상위
		        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
		        PixelFormat.TRANSLUCENT);

		mParams.x = 0;
		mParams.y = 0;
		mSetLoadView();
		mWindowManager.addView(mView, mParams);	
	}
	
	private void mSetLoadView()
    {	
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_navi_menu), null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_navi_menu), null);
		
		btnHome = (TextView) mView.findViewById(R.id.btnHome);
		btnDMB = (TextView) mView.findViewById(R.id.btnDMB);
		
		btnHome.setOnClickListener(this);
		btnDMB.setOnClickListener(this);
		mView.setOnTouchListener(mViewTouchListener);
    }
	
	private void exitMenuPopup()
    {
		GiniBroadcastReceiver.handler.removeMessages(GiniBroadcastReceiver.MSG_SHOW_NAVI_MENU);
		HP_Manager.mWidgetMap = true;
    	Intent intent = new Intent(getApplicationContext(), NaviMenuDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(intent);
    }

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnHome:
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_HIDE, 0, 0);
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_HOME;
			mChangeView = HP_Index.CURRENT_VIEW_HOME;
			break;
		case R.id.btnDMB:
			if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
			{
				HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
				
				if(DxbView_Normal.mfromUserMute == false)
					DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
			}
			
			HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_DMB;
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_HIDE, 0, 0);
			mChangeView = HP_Index.CURRENT_VIEW_DMB;
			break;
		default:
			break;
		}
		
		mHandler.removeMessages(MSG_DISMISS_DIALOG);
		mHandler.sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIMEOUT_DISMISS_DIALOG);
		exitMenuPopup();
	}
	
	//190520 yhr
	private final int MSG_DISMISS_DIALOG = 0;
	private final int TIMEOUT_DISMISS_DIALOG = 200;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_DISMISS_DIALOG:
				removeMessages(MSG_DISMISS_DIALOG);
				if(LauncherMainActivity.getTopActivity().equals(GINIAPPInfo.PACKAGE_NAME))
				{
					sendEmptyMessageDelayed(MSG_DISMISS_DIALOG, TIMEOUT_DISMISS_DIALOG);
					HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SET_HIDE, 0, 0);
				}
				break;
			}
		}
	};
	
	private Handler mHandlerDeviceMenu = new Handler();
	private Runnable mRunnableDeviceMenu = new Runnable() {
		
		@Override
		public void run() {
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_SHOW_DEVICE_MENU, 0, 0);
		}
	};

	
	private OnTouchListener mViewTouchListener = new OnTouchListener() 
    {
        @SuppressLint("ClickableViewAccessibility")
		@Override
        public boolean onTouch(View v, MotionEvent event) 
        {
            switch (event.getAction()) 
            {
            case MotionEvent.ACTION_UP:
            	if(HP_Manager.mShowNaviMenuPopup)
            	{
            		exitMenuPopup();
            		return false;
            	}
            	else
            		GiniBroadcastReceiver.handler.sendEmptyMessageDelayed(GiniBroadcastReceiver.MSG_SHOW_NAVI_MENU, 200);
            	break;
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_DOWN:
            	GiniBroadcastReceiver.handler.removeMessages(GiniBroadcastReceiver.MSG_SHOW_NAVI_MENU);
            	break;
            }
            return true;
        }
    };
}