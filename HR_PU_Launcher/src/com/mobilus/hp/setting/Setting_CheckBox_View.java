package com.mobilus.hp.setting;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;
import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * CheckBox만 있는 View 1. Screen - 후방카메라 2. Sound - 동시 음량 설정 3. Sound - 버튼효과음
 * 
 * @author yhr
 *
 */
public class Setting_CheckBox_View extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[Setting_CheckBox_View ]  ";

	private static TextView mCheckBox, txtMenuTitle, txtMessage;
	private static TextView mSelectBtn;
	private int mCurrentMenu;

	public static boolean isBeepSound = true;

	public Setting_CheckBox_View(int menu) {
		mCurrentMenu = menu;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_setting_checkbox_view, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		if(mCurrentMenu == HP_Index.SCREEN_MENU_REAR_CAM)
		{
			try {
				if(HP_Manager.mParkingGuideLine)
					LauncherMainActivity.M_MTX.saveParkingLine(0);
				else 
					LauncherMainActivity.M_MTX.saveParkingLine(1);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		else
			HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SOUND_CHECK_BOX);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM)
			return;

		if (HP_Manager.mSubMenu == HP_Index.SCREEN_MENU_REAR_CAM) {
			setLoadView();
			return;
		}

		if (HP_Manager.mSubMenu == HP_Index.SOUND_MENU_BEEP_SET) {
			setLoadView();
			return;
		}

		if (HP_Manager.mSubMenu == HP_Index.SOUND_MENU_VOL_SET) {
			setLoadView();
			return;
		}
	}

	@Override
	public void onStart() {
		mClickEvnetCnt = -1;
		super.onStart();
	}

	private void setLoadView() {
		mCheckBox = (TextView) HP_Manager.mContext.findViewById(R.id.checkBox);
		mSelectBtn = (TextView) HP_Manager.mContext.findViewById(R.id.checkedLayout);
		txtMenuTitle = (TextView) HP_Manager.mContext.findViewById(R.id.txtTitle);
		txtMessage = (TextView) HP_Manager.mContext.findViewById(R.id.txtMessage);

		if (mCurrentMenu == HP_Index.SCREEN_MENU_REAR_CAM) {
			if(txtMenuTitle == null || txtMessage == null)
				return;
			
			txtMenuTitle.setText(HP_Manager.mContext.getResources().getString(R.string.activation));
			txtMessage.setText(HP_Manager.mContext.getResources().getString(R.string.msg_activation));
			try {
				if(LauncherMainActivity.M_MTX.loadParkingLine() == HP_Index.GUIDLINE_ENABLE)
					HP_Manager.mParkingGuideLine = true;
				else
					HP_Manager.mParkingGuideLine = false;
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			setRearCamGuideLine();
		} else if (mCurrentMenu == HP_Index.SOUND_MENU_VOL_SET) {
			if(txtMenuTitle == null || txtMessage == null)
				return;
			
			txtMenuTitle.setText(HP_Manager.mContext.getResources().getString(R.string.set_volume));
			txtMessage.setText(HP_Manager.mContext.getResources().getString(R.string.msg_volume));

			setNaviGuidance();
		} else if (mCurrentMenu == HP_Index.SOUND_MENU_BEEP_SET) {
			if(txtMenuTitle == null || txtMessage == null)
				return;
			
			txtMenuTitle.setText(HP_Manager.mContext.getResources().getString(R.string.set_beep));
			txtMessage.setText(HP_Manager.mContext.getResources().getString(R.string.msg_beep));
			setBeepSound();
		}
		mSelectBtn.setOnClickListener(this);
		mCheckBox.setOnClickListener(this);
		txtMenuTitle.setOnClickListener(this);
	}

	private void setRearCamGuideLine() {
		if (HP_Manager.mParkingGuideLine) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		} else {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
		}
	}

	private void setNaviGuidance() {
		if(mCheckBox == null)
			return;
		
		if (HP_Manager.mIsNaviGuidance) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		} else {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			
			HP_Manager.mNaviSoundStart = false;
			if(HP_Manager.mSystemMuteStatus == HP_Index.SYSTEM_SOUND_UNMUTE)
			{
				if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
					DxbView.startAudioOut();
			}
		}
	}

	private void setBeepSound() {
		if (isBeepSound) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));

			Settings.System.putInt(HP_Manager.mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 1);
		} else {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));

			Settings.System.putInt(HP_Manager.mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0);
		}
	}

	private static int mClickEvnetCnt;
	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {

		case R.id.checkedLayout:
		case R.id.checkBox:
		case R.id.txtTitle:
			if (mCurrentMenu == HP_Index.SOUND_MENU_BEEP_SET) {
				isBeepSound = !isBeepSound;
				setBeepSound();
				if (isBeepSound)
					getView().playSoundEffect(SoundEffectConstants.CLICK);
				
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SOUND_CHECK_BOX);
			} else if (mCurrentMenu == HP_Index.SCREEN_MENU_REAR_CAM) {
				try {
					Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "  Before - loadParkingLine :  " + LauncherMainActivity.M_MTX.loadParkingLine());
					if(LauncherMainActivity.M_MTX.loadParkingLine() == HP_Index.GUIDLINE_ENABLE)
					{
						HP_Manager.mParkingGuideLine = false;
						LauncherMainActivity.M_MTX.setParkingLine(HP_Index.GUIDLINE_DISABLE);
						LauncherMainActivity.M_MTX.saveParkingLine(HP_Index.GUIDLINE_DISABLE);
					}
					else
					{
						HP_Manager.mParkingGuideLine = true;
						LauncherMainActivity.M_MTX.setParkingLine(HP_Index.GUIDLINE_ENABLE);
						LauncherMainActivity.M_MTX.saveParkingLine(HP_Index.GUIDLINE_ENABLE);
					}
					Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + " After - loadParkingLine :  " + LauncherMainActivity.M_MTX.loadParkingLine());
					setRearCamGuideLine();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			else if (mCurrentMenu == HP_Index.SOUND_MENU_VOL_SET)
			{
				HP_Manager.mIsNaviGuidance = !HP_Manager.mIsNaviGuidance;
				setNaviGuidance();
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SOUND_CHECK_BOX);
			}
			break;

		default:
			break;
		}
	}
	
	private static final int MSG_SET_REARCAM_GUIDELINE = 0;
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SET_REARCAM_GUIDELINE:
				removeMessages(MSG_SET_REARCAM_GUIDELINE);
				
				if(mClickEvnetCnt > 0)
					mClickEvnetCnt = -1;
				
				try {
					if(HP_Manager.mParkingGuideLine)
					{
						LauncherMainActivity.M_MTX.setParkingLine(0);
						LauncherMainActivity.M_MTX.saveParkingLine(0);
					}
					else
					{
						LauncherMainActivity.M_MTX.setParkingLine(1);
						LauncherMainActivity.M_MTX.saveParkingLine(1);
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}