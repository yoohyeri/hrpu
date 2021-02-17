package com.mobilus.hp.setting;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 설정 메인 화면
 * 
 * @author yhr
 *
 */
public class Setting_Main extends Fragment implements View.OnClickListener, View.OnTouchListener {

	private static final String CLASS_NAME = "[Setting_Main ]  ";

	private final int HIDDEN_STEP_1 = 0;
	private final int HIDDEN_STEP_2 = 1;
	private final int HIDDEN_STEP_3 = 2;
	private final int HIDDEN_STEP_4 = 3;
	private final int HIDDEN_STEP_5 = 4;

	// Setting Menu
	private TextView btnScreen, btnSound, btnSystem;
	private TextView mSelectDiesel, mSelectElectric, mSelectLPG;
	private TextView mCheckBoxDiesel, mCheckBoxElectric, mCheckBoxLPG;
	private TextView mHiddenTab1, mHiddenTab2, mHiddenTab3, mHiddenTab4;
	private TextView mSelect1T2W, mSelect1T4W, mSelect1_2T2W;
	private static TextView mCheckBox1T2W;
	private static TextView mCheckBox1T4W;
	private static TextView mCheckBox1_2T2W;
	private RelativeLayout m1_2T2WLayout;

	private int mHiddenStep = -1;
	
	public static boolean mHiddenMode = false;;

	public Setting_Main() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_setting_main, container, false);

		return inflater.inflate(R.layout.fragment_setting_main, container, false);
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
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		setLoadView();
	}

	@Override
	public void onStart() {
		HP_Manager.mRootMenu = HP_Index.ROOT_MENU_SETTING;
		HP_Manager.mSubMenu = -1;
		super.onStart();
	}

	public void mBootComplete() {
//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mBootComplete / mFuel : " + HP_Manager.mFuel );
		if (HP_Manager.mFuel == HP_Index.FUEL_DIESEL)
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "0");
		else if(HP_Manager.mFuel == HP_Index.FUEL_ELECTRIC)
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "1");
		else if(HP_Manager.mFuel == HP_Index.FUEL_LPG)
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "2");
	}

	/**
	 * GUI 초기화
	 */
	private void setLoadView() {
		m1_2T2WLayout = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.rl1_2T2W);
			
		btnScreen = (TextView) HP_Manager.mContext.findViewById(R.id.screen_btn);
		btnSound = (TextView) HP_Manager.mContext.findViewById(R.id.sound_btn);
		btnSystem = (TextView) HP_Manager.mContext.findViewById(R.id.system_btn);

		mSelectDiesel = (TextView) HP_Manager.mContext.findViewById(R.id.SelectDiesel);
		mSelectElectric = (TextView) HP_Manager.mContext.findViewById(R.id.SelectElectric);
		mSelectLPG = (TextView) HP_Manager.mContext.findViewById(R.id.SelectLPG);

		mCheckBoxDiesel = (TextView) HP_Manager.mContext.findViewById(R.id.fuel_diesel_checkbox);
		mCheckBoxElectric = (TextView) HP_Manager.mContext.findViewById(R.id.fuel_electric_checkbox);
		mCheckBoxLPG = (TextView) HP_Manager.mContext.findViewById(R.id.fuel_lpg_checkbox);
		
		mSelect1T2W = (TextView) HP_Manager.mContext.findViewById(R.id.Select1T2W);
		mSelect1T4W = (TextView) HP_Manager.mContext.findViewById(R.id.Select1T4W);
		mSelect1_2T2W = (TextView) HP_Manager.mContext.findViewById(R.id.Select1_2T2W);

		mCheckBox1T2W = (TextView) HP_Manager.mContext.findViewById(R.id._1T2W_checkbox);
		mCheckBox1T4W = (TextView) HP_Manager.mContext.findViewById(R.id._1T4W_checkbox);
		mCheckBox1_2T2W = (TextView) HP_Manager.mContext.findViewById(R.id._1_2T2W_checkbox);

		mHiddenTab1 = (TextView) HP_Manager.mContext.findViewById(R.id.hiddenTab1);
		mHiddenTab2 = (TextView) HP_Manager.mContext.findViewById(R.id.hiddenTab2);
		mHiddenTab3 = (TextView) HP_Manager.mContext.findViewById(R.id.hiddenTab3);
		mHiddenTab4 = (TextView) HP_Manager.mContext.findViewById(R.id.hiddenTab4);

		
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
		{
			if(m1_2T2WLayout != null)
				m1_2T2WLayout.setVisibility(View.INVISIBLE);
			else
				return;
		}
		else
		{
			if(m1_2T2WLayout != null)
			{	
				m1_2T2WLayout.setVisibility(View.VISIBLE);
				mSelect1_2T2W.setOnClickListener(this);
			}
			else
				return;
		}
		
		mHiddenTab1.setOnTouchListener(this);
		mHiddenTab2.setOnTouchListener(this);
		mHiddenTab3.setOnTouchListener(this);
		mHiddenTab4.setOnTouchListener(this);

		btnScreen.setOnClickListener(this);
		btnSound.setOnClickListener(this);
		btnSystem.setOnClickListener(this);

		mSelectDiesel.setOnClickListener(this);
		mSelectElectric.setOnClickListener(this);
		mSelectLPG.setOnClickListener(this);
		
		mSelect1T2W.setOnClickListener(this);
		mSelect1T4W.setOnClickListener(this);
		
		setVehicleCheckBox(HP_Manager.mVehicleType);
		setFuelCheckBox(HP_Manager.mFuel);
	}

	public static void setVehicleCheckBox(int vehicle) {
		if (vehicle == HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mCheckBox1_2T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));

		} else if (vehicle == HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_4W) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mCheckBox1_2T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			
		} else if (vehicle == HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1_2T_2W) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox1T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBox1T4W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_off));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mCheckBox1_2T2W.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_check_on));
		}
	}
	
	private void setFuelCheckBox(int fuel) {
		if (fuel == HP_Index.FUEL_DIESEL) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.kia_check_on));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.kia_check_off));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.kia_check_off));

		} else if (fuel == HP_Index.FUEL_ELECTRIC) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.kia_check_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.kia_check_on));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.kia_check_off));
		} else if (fuel == HP_Index.FUEL_LPG) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxDiesel.setBackground(getResources().getDrawable(R.drawable.kia_check_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.check_off));
			else
				mCheckBoxElectric.setBackground(getResources().getDrawable(R.drawable.kia_check_off));
			
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.check_on));
			else
				mCheckBoxLPG.setBackground(getResources().getDrawable(R.drawable.kia_check_on));
		}
		HP_Manager.mFuel = fuel; 
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_FUEL_PREF);
	}

	/**
	 * 화면/사운드/시스템 설정 화면으로 이동 버튼 클릭으로 리스트가 보이는 메뉴화면으로 이동 시, 리스트의 0번 항목의 SubMenu
	 * 화면이 나와야 함
	 */
	@Override
	public void onClick(View v) {

		int id = v.getId();
		int submenu = HP_Index.SUB_MENU_LIST_0;
		switch (id) {
		case R.id.screen_btn:
			HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
			HP_Manager.callSettingMenuFragment(HP_Index.FRAGMENT_SET_SCREEN, submenu);
			break;
		case R.id.sound_btn:
			HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
			HP_Manager.callSettingMenuFragment(HP_Index.FRAGMENT_SET_SOUND, submenu);
			break;
		case R.id.system_btn:
			HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
			HP_Manager.callSettingMenuFragment(HP_Index.FRAGMENT_SET_SYSTEM, submenu);
			break;
		case R.id.SelectDiesel:
			HP_Manager.mFuel = HP_Index.FUEL_DIESEL;
			setFuelCheckBox(HP_Manager.mFuel);
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "0");

			// 19.03.21 yhr - 연료메세지 전송
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_DIESEL, 0);
			break;
		case R.id.SelectElectric:
			HP_Manager.mFuel = HP_Index.FUEL_ELECTRIC;
			setFuelCheckBox(HP_Manager.mFuel);
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "1");

			// 19.03.21 yhr - 연료메세지 전송
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_ELECTRIC, 0);
			break;
		case R.id.SelectLPG:
			HP_Manager.mFuel = HP_Index.FUEL_LPG;
			setFuelCheckBox(HP_Manager.mFuel);	
			SystemProperties.set(LauncherMainActivity.PROPERTIES_NAVI_EVMENU, "2");

			// 19.03.21 yhr - 연료메세지 전송
			HP_Manager.sendAppToNavi(MAP_INFO.WM_USER_CHANGE_FUEL, MAP_INFO.FUEL_LPG, 0);
			break;
		case R.id.Select1T2W:			
			HP_Manager.mVehicleType = HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W;
			if(samePreVehicleType())
				break;
			
			setVehicleCheckBox(HP_Manager.mVehicleType);
			LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_CHANGE_VEHICLE;
			LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.vehicle_change_message));
			break;
		case R.id.Select1T4W:
			HP_Manager.mVehicleType = HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1T_4W;
			if(samePreVehicleType())
				break;
			
			setVehicleCheckBox(HP_Manager.mVehicleType);
			LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_CHANGE_VEHICLE;
			LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.vehicle_change_message));
			break;
		case R.id.Select1_2T2W:
			HP_Manager.mVehicleType = HP_Index.GUIDLINE_ENABLE_VEHICLE_TYPE_1_2T_2W;
			if(samePreVehicleType())
				break;
			
			setVehicleCheckBox(HP_Manager.mVehicleType);
			LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_CHANGE_VEHICLE;
			LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.vehicle_change_message));
			break;
		}
	}
	
	private boolean samePreVehicleType()
	{
		boolean result = false;
		try {
			if(HP_Manager.mVehicleType == LauncherMainActivity.M_MTX.loadCarType())
				result = true;
			else
				result = false;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public boolean onTouch(View v, MotionEvent me) {
		int id = v.getId();
		
		if(me.getAction() == MotionEvent.ACTION_DOWN)
		{
			switch (id) {
			case R.id.hiddenTab1:
				if (mHiddenStep < 0)
					mHiddenStep = HIDDEN_STEP_1;
				break;
			case R.id.hiddenTab2:
				if (mHiddenStep == HIDDEN_STEP_2)
					mHiddenStep = HIDDEN_STEP_3;
				else
					mHiddenStep = -1;
				break;
			case R.id.hiddenTab3:
				if (mHiddenStep == HIDDEN_STEP_3)
					mHiddenStep = HIDDEN_STEP_4;
				else if (mHiddenStep == HIDDEN_STEP_4) {
					mHiddenStep = -1;
					HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
					HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
	
					mHiddenMode = true;
					
					// Hidden Manu List 화면으로 전환
					FragmentTransaction transaction = HP_Manager.mContext.getFragmentManager().beginTransaction();
					Setting_Hidden_List fragment1 = new Setting_Hidden_List();
					transaction.replace(R.id.setting_fragment_container, fragment1);
					transaction.commit();
				} else
					mHiddenStep = -1;
	
				break;
			case R.id.hiddenTab4:
				if (mHiddenStep == HIDDEN_STEP_1)
					mHiddenStep = HIDDEN_STEP_2;
				else
					mHiddenStep = -1;
				break;
			}
		}
		return false;
	}
}
