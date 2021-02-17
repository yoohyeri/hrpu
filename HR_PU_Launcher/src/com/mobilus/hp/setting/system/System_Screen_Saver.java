package com.mobilus.hp.setting.system;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class System_Screen_Saver extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[HP_System_Screen_Saver ]  ";

	private TextView mSelectBtnDigital, mSelectBtnAnalog, mSelectBtnNone;
	private TextView mRadioDigital, mRadioAnalog, mRadioNone;

	public System_Screen_Saver() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_system_screen_saver, container, false);

		return inflater.inflate(R.layout.fragment_system_screen_saver, container, false);
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
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SYSTEM)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SYSTEM_MENU_SCREEN_SAVER)
			return;

		setLoadView();
		setRadioButton();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setLoadView() {
		mSelectBtnDigital = (TextView) HP_Manager.mContext.findViewById(R.id.btnSaverDigital);
		mSelectBtnAnalog = (TextView) HP_Manager.mContext.findViewById(R.id.btnSaverAnalog);
		mSelectBtnNone = (TextView) HP_Manager.mContext.findViewById(R.id.btnSaverNone);

		mRadioDigital = (TextView) HP_Manager.mContext.findViewById(R.id.radioDigital);
		mRadioAnalog = (TextView) HP_Manager.mContext.findViewById(R.id.radioAnalog);
		mRadioNone = (TextView) HP_Manager.mContext.findViewById(R.id.radioNone);

		mSelectBtnDigital.setOnClickListener(this);
		mSelectBtnAnalog.setOnClickListener(this);
		mSelectBtnNone.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btnSaverDigital:
			HP_Manager.mScreenSaver = HP_Index.SCREEN_SAVER_DIGITAL;
			setRadioButton();
			break;
		case R.id.btnSaverAnalog:
			HP_Manager.mScreenSaver = HP_Index.SCREEN_SAVER_ANALOG;
			setRadioButton();
			break;
		case R.id.btnSaverNone:
			HP_Manager.mScreenSaver = HP_Index.SCREEN_SAVER_NONE;
			setRadioButton();
			break;
		default:
			break;
		}
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_SYSTEM_SCREEN);
	}

	private void setRadioButton() {
		if (mRadioDigital == null)
			return;

		if (HP_Manager.mScreenSaver == HP_Index.SCREEN_SAVER_DIGITAL) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));
			else
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));

		} else if (HP_Manager.mScreenSaver == HP_Index.SCREEN_SAVER_ANALOG) {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));
			else
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));
		} else {
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioDigital.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));
			else
				mRadioAnalog.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));
			else
				mRadioNone.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));
		}
	}
}