package com.mobilus.hp.setting;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.Launcher_Main;
import com.mobilus.hp.launcher.MABroadcastReceiver;
import com.mobilus.hp.mapupdate.GINIAPPInfo;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 설정 메인 화면 - Title Bar
 * 
 * @author yhr
 *
 */
public class Setting_TitleBar extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[Setting_TitleBar ]  ";

	// TitleBar
	public static TextView txtIcon, txtTitle, btnBack;

	public Setting_TitleBar() {
	}

	public Setting_TitleBar(int fragIndex, int subMenu) {
		HP_Manager.mFragmentSetIndex = fragIndex;
		HP_Manager.mSubMenu = subMenu;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_setting_titlebar, container, false);
		return inflater.inflate(R.layout.fragment_setting_titlebar, container, false);
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
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	public void onPause() {
		HP_Manager.setDateAndTime();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
		{
			if(HP_Manager.mCurrentView == HP_Index.FRAGMENT_HIDDEN_MENU)
				setLoadView();
			return;
		}

		setLoadView();

		// title bar 밑의 영역에 Setting Main화면 출력
		if (HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SETTING)
			HP_Manager.callSettingMenuFragment(HP_Index.FRAGMENT_SETTING_MAIN, HP_Index.SUB_MENU_LIST_0);
		else
			HP_Manager.callSettingMenuFragment(HP_Manager.mFragmentSetIndex, HP_Manager.mSubMenu);
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * GUI 초기화
	 */
	private void setLoadView() {
		txtIcon = (TextView) HP_Manager.mContext.findViewById(R.id.setting_icon);
		txtTitle = (TextView) HP_Manager.mContext.findViewById(R.id.setting_title);
		btnBack = (TextView) HP_Manager.mContext.findViewById(R.id.setting_btnBack);
		btnBack.setOnClickListener(this);
	}

	/**
	 * 화면/사운드/시스템 설정 화면으로 이동
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.setting_btnBack:
			Setting_Main.mHiddenMode = false;
			if(HP_Manager.mChangeHome)
			{
				HP_Manager.mChangeHome = false;
				HP_Manager.mCallback.onChangeHome();
				
				//200204 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				return;		
			}
			
			if(HP_Manager.mChangeDMB)
			{
				HP_Manager.mChangeDMB = false;
				HP_Manager.mCallback.onChangeDMBView();
				
				//200204 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_DMB_FULL;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				return;		
			}
			
//			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME+"mFragmentSetIndex : " + HP_Manager.mFragmentSetIndex + ", mBackView : " + HP_Manager.mBackView);
			
			if (HP_Manager.mFragmentSetIndex != HP_Index.FRAGMENT_SETTING_MAIN) {
				if (HP_Manager.mBackView == HP_Index.BACK_HOME)
				{
					HP_Manager.mCallback.onChangeHome();
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
				}
				else if (HP_Manager.mBackView == HP_Index.BACK_DMB)
					HP_Manager.mCallback.onChangeDMBView();
				else if (HP_Manager.mBackView == HP_Index.BACK_SETTING_MAIN)
				{	
					if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SCREEN) // 0
						HP_Manager.mPreferences.mSavePreferenceScreen();
					else if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SOUND) // 1
						HP_Manager.mPreferences.mSavePreferenceSound();
					else if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SYSTEM) // 2
						HP_Manager.mPreferences.mSavePreferenceSystem();
				
					HP_Manager.mRootMenu = HP_Index.FRAGMENT_SETTING_MAIN;
					HP_Manager.mSubMenu = 0;
					HP_Manager.callFragment(HP_Manager.mRootMenu, HP_Manager.mSubMenu);
				}
				else {
					HP_Manager.mRootMenu = HP_Index.FRAGMENT_SETTING_MAIN;
					HP_Manager.mSubMenu = 0;
					HP_Manager.callFragment(HP_Manager.mRootMenu, HP_Manager.mSubMenu);
				}

				HP_Manager.mBackView = -1;
				break;
			}
			HP_Manager.mCallback.onChangeHome();
			break;
		}
	}
}