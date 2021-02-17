package com.mobilus.hp.popup;

import com.telechips.android.tdmb.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class UpgradeDialog extends Service {

	final public String CLASS_NAME = "[UpgradeDialog ]  ";

	public final static int OS_UPGRADE_POPUP = 0;
	public final static int FW_UPGRADE_POPUP = 1;
	
	public final static int DIALOG_LARGE_W = 720;
	public final static int DIALOG_LARGE_H = 410;

	private View mView;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mParams;

	public static int mCurrentPopup;
	public static UpgradeDialog mUpgradeDialog = null;
	
	public static int mProgressMax; 
	public ProgressBar mProgressbar;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mUpgradeDialog = this;

		if (mCurrentPopup == OS_UPGRADE_POPUP)
			onSetOSUpgradePopup();
		else if (mCurrentPopup == FW_UPGRADE_POPUP)
			onSetFWUpgradePopup();
	}
	
	@Override
	public void onDestroy() {
		try {
			mWindowManager.removeView(mView);
			mWindowManager = null;
		} catch (NullPointerException e) {
		}
		super.onDestroy();
	}

	/*
	 * 시스템 업데이트 팝업 
	 */
	private void onSetOSUpgradePopup() {
		if (mWindowManager == null)
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, 
												 WindowManager.LayoutParams.FILL_PARENT, 
												 WindowManager.LayoutParams.TYPE_PHONE,
												 WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
												 PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mParams.dimAmount = 1; // 팝업 뒷 배경 검은색으로
		mSetUpdateLoadingPopup();
		mWindowManager.addView(mView, mParams);
	}
	
	/*
	 * 펌웨어 업데이트 팝업
	 */
	private void onSetFWUpgradePopup() {
		if (mWindowManager == null)
			mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		
		mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT, 
												 WindowManager.LayoutParams.FILL_PARENT, 
												 WindowManager.LayoutParams.TYPE_PHONE,
												 WindowManager.LayoutParams.FLAG_DIM_BEHIND, // 팝업 뒷 배경 검은색으로
												 PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mParams.dimAmount = 1; // 팝업 뒷 배경 검은색으로
		mSetUpdateLoadingPopup();
		mWindowManager.addView(mView, mParams);
	}

	/*
	 * 펌웨어 업데이트 로딩 팝업
	 */
	private void mSetUpdateLoadingPopup() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_update_popup),null);
		mProgressbar = (ProgressBar) mView.findViewById(R.id.update_progress);
	}
	
	/*
	 * 펌웨어 업데이트 팝업 종료
	 */
	public void exitMCUUpdatePopup() {
		Intent intent = new Intent(getApplicationContext(), UpgradeDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(intent);
	}
}