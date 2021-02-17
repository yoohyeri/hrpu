package com.mobilus.hp.popup;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DefaultDialog extends Dialog implements View.OnClickListener {

	private final String CLASS_NAME = "[DefaultDialog ]  ";
	
	public final static int	BTN_ID_CONFIRM			= 0;
	public final static int BTN_ID_CANCLE			= 1;	
	public final static int BTN_ID_AGREE			= 2;
	
	public final static int DIALOG_LARGE_W			= 720;	
	public final static int DIALOG_LARGE_H			= 410;
	
	public final static int DIALOG_DEFAULT_W		= 600;
	public final static int DIALOG_DEFAULT_H		= 320;
	
	private final int MSG_BTN_ENABLE				= 0;
	private final int MSG_DISMISS_POPUP				= 1;
	
	public final int DIALOG_MODE_UPGRADE			= 0;
	public final int DIALOG_MODE_FACTORY_RESET		= 1;
	public final int DIALOG_MODE_INITAILIZE			= 2;
	public final int DIALOG_MODE_CHANGE_VEHICLE		= 3;
	public final int DIALOG_MODE_UPGRADE_COPY_ERROR = 4;
	
	public ICustomDefaultDialogEventListener onCustomDialogEventListener;
	
	private TextView noticeIcon, btnConfirm, btnCancle, btnAgree, txtMsg;
	private ProgressBar loadingIcon;
	private Context mContext;
	
	public int mDialogMode = DIALOG_MODE_UPGRADE;
	
	public DefaultDialog(Activity context, ICustomDefaultDialogEventListener iCustomDialogEventListener) {
		super(context);
		
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			setContentView(R.layout.dialog_default_popup);
		else
			setContentView(R.layout._kia_dialog_default_popup);
		
		this.onCustomDialogEventListener = iCustomDialogEventListener;
		
		loadingIcon = (ProgressBar) findViewById(R.id.loadingIcon);
		noticeIcon = (TextView) findViewById(R.id.noticeIcon);
		btnConfirm = (TextView) findViewById(R.id.btnConfirm);
		btnCancle = (TextView) findViewById(R.id.btnCancle);
		btnAgree = (TextView) findViewById(R.id.btnAgree);
		txtMsg = (TextView) findViewById(R.id.default_popup_text);
		
		btnConfirm.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
		btnAgree.setOnClickListener(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void setDialogSize(int mode)
	{
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		
		WindowManager.LayoutParams params = getWindow().getAttributes();
		  
		if(mode == HP_Index.DEFAULT_POPUP_LARGE)
		{
			params.width = DIALOG_LARGE_W;
			params.height = DIALOG_LARGE_H;
			params.x =0;
			params.y =25;
		}
		else
		{
			params.width = DIALOG_DEFAULT_W;
			params.height = DIALOG_DEFAULT_H;
			params.x = 0;
			params.y = 0;			
		}
		getWindow().setAttributes(params);
		show();
	}

	/**
	 * 모드에 따라 표출되는 Dialog가 바뀜
	 * @param mode
	 */
	public void showDefaultDialog(int mode, String msg)
	{	
		if(mode == HP_Index.DEFAULT_POPUP_ONE_BUTTON)
		{
			loadingIcon.setVisibility(View.GONE);
			noticeIcon.setVisibility(View.VISIBLE);
			
			btnAgree.setVisibility(View.VISIBLE);
			btnConfirm.setVisibility(View.GONE);
			btnCancle.setVisibility(View.GONE);
			txtMsg.setText(msg);
		}
		else if(mode == HP_Index.DEFAULT_POPUP_TWO_BUTTON)
		{
			loadingIcon.setVisibility(View.GONE);
			noticeIcon.setVisibility(View.VISIBLE);
			
			btnAgree.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.VISIBLE);
			btnCancle.setVisibility(View.VISIBLE);
			txtMsg.setText(msg);
		}
		else if(mode == HP_Index.DEFAULT_POPUP_SCAN)
		{
			loadingIcon.setVisibility(View.GONE);
			noticeIcon.setVisibility(View.GONE);
			
			btnAgree.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.VISIBLE);
			btnCancle.setVisibility(View.VISIBLE);
			txtMsg.setText(msg);
		}
		else if(mode == HP_Index.DEFAULT_POPUP_LARGE)	// 법규안내 팝업
		{
			txtMsg.setText(msg);
			loadingIcon.setVisibility(View.GONE);
			noticeIcon.setVisibility(View.VISIBLE);
			
			btnAgree.setVisibility(View.VISIBLE);
			btnAgree.setEnabled(false);
			btnAgree.setAlpha((float) 0.5);
			
			mHandler.sendEmptyMessageDelayed(MSG_BTN_ENABLE, 3*HP_Index.TIME_1_SECOND);
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 10*HP_Index.TIME_1_SECOND);
			
			btnConfirm.setVisibility(View.GONE);
			btnCancle.setVisibility(View.GONE);
			
		}
		else if(mode == HP_Index.DEFAULT_POPUP_GONE_BUTTON)
		{
			txtMsg.setText(msg);
			loadingIcon.setVisibility(View.VISIBLE);
			noticeIcon.setVisibility(View.GONE);
			
			btnAgree.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.GONE);
			btnCancle.setVisibility(View.GONE);
		}
		else if(mode == HP_Index.DEFAULT_POPUP_NOTICE)
		{
			txtMsg.setText(msg);
			loadingIcon.setVisibility(View.GONE);
			noticeIcon.setVisibility(View.VISIBLE);
			
			btnAgree.setVisibility(View.GONE);
			btnConfirm.setVisibility(View.GONE);
			btnCancle.setVisibility(View.GONE);
			mHandler.sendEmptyMessageDelayed(MSG_DISMISS_POPUP, 3*HP_Index.TIME_1_SECOND);
		}
		setDialogSize(mode);
	}
	
	/**
	 * activity와 통신을 하기 위한 interface
	 * @author yhr
	 *
	 */
	public interface ICustomDefaultDialogEventListener {
		public void customDialogClickEvent(int id);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	@Override
	protected void onStop() {
		mHandler.removeMessages(MSG_BTN_ENABLE);
		mHandler.removeMessages(MSG_DISMISS_POPUP);
		super.onStop();
	}
	
	@Override
	public void dismiss() {
		Log.d(HP_Manager.TAG_POPUP, CLASS_NAME + "dismiss");
		super.dismiss();
	}
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btnConfirm)
			onCustomDialogEventListener.customDialogClickEvent(BTN_ID_CONFIRM);
		else if(v.getId() == R.id.btnCancle)
			onCustomDialogEventListener.customDialogClickEvent(BTN_ID_CANCLE);
		else if(v.getId() == R.id.btnAgree)	// 버튼 1개만 있는 팝업에서 '확인/동의함' 버튼을 누를 경우.
			onCustomDialogEventListener.customDialogClickEvent(BTN_ID_AGREE);
	}
	
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == MSG_BTN_ENABLE)
			{
				removeMessages(MSG_BTN_ENABLE);
				
				btnAgree.setEnabled(true);
				btnAgree.setAlpha(1);
			}
			else if(msg.what == MSG_DISMISS_POPUP)
			{
				removeMessages(MSG_DISMISS_POPUP);
				if(isShowing())
					onCustomDialogEventListener.customDialogClickEvent(BTN_ID_AGREE);
			}
			super.handleMessage(msg);
		}
	};
}