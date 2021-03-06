package com.telechips.android.tdmb;

import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.setting.GPSSettingsActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

public class SDCardMonitorReceiver extends BroadcastReceiver {
	private static final String CLASS_NAME = "[SDCardMonitorReceiver ]  ";
	
	public static final String MOUNT_EVENT = "mtx.intent.action.mount";
	public static final String UNMOUNT_EVENT = "mtx.intent.action.unmount";
	Intent mIntent;

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent _intent = new Intent();
		
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)) {
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_MEDIA_MOUNTED");
			HP_Manager.bIsSDCardMounted = true;
			
			//20200611 yhr
			if(GPSSettingsActivity.mBtnUbxSaveOnOff != null)
				GPSSettingsActivity.mBtnUbxSaveOnOff.setEnabled(true);
			//
			
			HP_Manager.strSDPath = intent.getDataString();
			
			_intent.setAction(MOUNT_EVENT);
			context.sendBroadcast(_intent);
			
		} else if (intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)) {
			HP_Manager.bIsSDCardMounted = false;
		} else if (intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
			Log.i(HP_Manager.TAG_BROADCAST, CLASS_NAME + "ACTION_MEDIA_UNMOUNTED");
			HP_Manager.bIsSDCardMounted = false;
			
			//20200611 yhr
			if(GPSSettingsActivity.mBtnUbxSaveOnOff != null)
			{
				GPSSettingsActivity.mIsUbxSave = false;
				GPSSettingsActivity.mBtnUbxSaveOnOff.setText(HP_Manager.mContext.getResources().getString(R.string.ubx_save_on));
				GPSSettingsActivity.mBtnUbxSaveOnOff.setEnabled(false);
			}
			//
			
			_intent.setAction(UNMOUNT_EVENT);
			context.sendBroadcast(_intent);
		}

		if (HP_Manager.mProductionProcess)
			return;

		if (HP_Manager.mCallback != null) {
			HP_Manager.mCallback.onChangeSDCardState(intent);
		} else {
			mIntent = intent;
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (HP_Manager.mCallback == null)
						return;

					HP_Manager.mCallback.onChangeSDCardState(mIntent);
				}
			}, 3500);  // 210115 yhr '중지팝업' 개선 500 --> 3500으로 변경
		}
	}
}