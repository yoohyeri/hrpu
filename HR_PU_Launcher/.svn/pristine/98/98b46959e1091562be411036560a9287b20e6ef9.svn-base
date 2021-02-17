package com.mobilus.hp.popup;

import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class NaviUpdateDialog extends Service {

	final public String CLASS_NAME = "[NaviUpdateDialog ]  ";

	private View mView;
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams mParams;

	public static int mChangeView = -1;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		onSetWindowManager();
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

	@SuppressWarnings("deprecation")
	private void onSetWindowManager() {
		mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

		mParams = new WindowManager.LayoutParams(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
				PixelFormat.TRANSLUCENT);
		mParams.x = 0;
		mParams.y = 0;
		mSetLoadView();
		mWindowManager.addView(mView, mParams);
	}

	private void mSetLoadView() {
		LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout.dialog_navi_update), null);
		else
			mView = mInflater.inflate(getApplicationContext().getResources().getLayout(R.layout._kia_dialog_navi_update), null);
	}

	private void exitMenuPopup() {
		Intent intent = new Intent(getApplicationContext(), NaviUpdateDialog.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		stopService(intent);
	}
}