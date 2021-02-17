package com.mobilus.hp.popup;

import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ScanDialog extends Dialog implements View.OnClickListener {
	public final static String CLASS_NAME = "[ScanDialog ]  ";
	
	public final static int	BTN_ID_CONFIRM			= 0;
	public final static int BTN_ID_CANCLE			= 1;	
	
	public final static int DIALOG_DEFAULT_W		= 350;
	public final static int DIALOG_DEFAULT_H		= 200;
	
	public ICustomSCANDialogEventListener onCustomScanEventListener;
	private TextView btnConfirm, btnCancle;
	
	public ScanDialog(Activity context, ICustomSCANDialogEventListener iCustomDialogEventListener) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			setContentView(R.layout.dialog_scan);
		else
			setContentView(R.layout._kia_dialog_scan);
		
		this.onCustomScanEventListener = iCustomDialogEventListener;
		
		btnConfirm = (TextView) findViewById(R.id.btnConfirm);
		btnCancle = (TextView) findViewById(R.id.btnCancle);
		
		btnConfirm.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void setDialogSize()
	{
		WindowManager wm = (WindowManager) HP_Manager.mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		WindowManager.LayoutParams params = getWindow().getAttributes();

		params.width = DIALOG_DEFAULT_W;
		params.height = DIALOG_DEFAULT_H;
		params.x = 0;
		params.y = 0;
		getWindow().setAttributes(params);
	}

	public void showScanDialog()
	{
		DxbView_Normal.ClearDisplayFull();
		setDialogSize();
		show();
	}
	
	@Override
	public void dismiss() {
		DxbView_Normal.ClearDisplayFull();
		DxbView_Normal.ResetDisplayFull();
		super.dismiss();
	}

	/**
	 * Acitivivy와 통신을 하기 위한 interface
	 * @author yhr
	 *
	 */
	public interface ICustomSCANDialogEventListener {
		public void customDialogClickEvent(int id);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnConfirm)
			onCustomScanEventListener.customDialogClickEvent(BTN_ID_CONFIRM);
		else if(v.getId() == R.id.btnCancle)
			onCustomScanEventListener.customDialogClickEvent(BTN_ID_CANCLE);
	}
}
