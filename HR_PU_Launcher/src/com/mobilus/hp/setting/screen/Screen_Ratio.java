package com.mobilus.hp.setting.screen;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView_Full;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Screen_Ratio extends Fragment implements View.OnClickListener {

	private static final String TAG = "HP_Setting_Ratio";

	private TextView mRadioBtnFullMode, mRadioBtnWideMode;
	private TextView mSelectFullMode, mSelectWideMode;
	private TextView mTxtFullMode, mTxtWideMode;
	public static RelativeLayout mRatioBG;
	public static int mRatioMode;

	public Screen_Ratio() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_screen_ratio, container, false);

		return inflater.inflate(R.layout.fragment_screen_ratio, container, false);
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
		super.onPause();
		
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_RATIO);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SCREEN)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SCREEN_MENU_RATIO)
			return;

		setLoadView();
		if(LauncherMainActivity.mParkingStatus)
			HP_Manager.mCallback.onPlayDMB();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * 부팅 후 마지막으로 설정된 화면 비율 적용
	 */
	public void mBootComplete() {
		if (mRatioMode == HP_Index.DISPLAY_RATIO_16_9) // full
			DxbView_Full.eState_ScreenSize = HP_Index.SCREENMODE_FULL;
		else
			DxbView_Full.eState_ScreenSize = HP_Index.SCREENMODE_LETTERBOX;
		DxbPlayer.setScreenMode(DxbView_Full.eState_ScreenSize);
	}

	private void setLoadView() {
		mSelectFullMode = (TextView) HP_Manager.mContext.findViewById(R.id.selectFull);
		mSelectWideMode = (TextView) HP_Manager.mContext.findViewById(R.id.selectWide);

		mRadioBtnFullMode = (TextView) HP_Manager.mContext.findViewById(R.id.radioBtnFull);
		mRadioBtnWideMode = (TextView) HP_Manager.mContext.findViewById(R.id.radioBtnWide);

		mTxtFullMode = (TextView) HP_Manager.mContext.findViewById(R.id.txtFullMode);
		mTxtWideMode = (TextView) HP_Manager.mContext.findViewById(R.id.txtWideMode);

		mRatioBG = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.ratioBG);
		
		// 주행 규제 상태 체크
		if(LauncherMainActivity.getInstance().mParkingStatus == false)
			mRatioBG.setVisibility(View.VISIBLE);
		else
			mRatioBG.setVisibility(View.GONE);
		
		mSelectFullMode.setOnClickListener(this);
		mSelectWideMode.setOnClickListener(this);

		setSelectRatio(mRatioMode);
	}

	private void setSelectRatio(int mode) {
		if (mRatioMode == HP_Index.DISPLAY_RATIO_16_9) // full
		{
			mSelectFullMode.setHovered(true);
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mRadioBtnFullMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));
			else
				mRadioBtnFullMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));

			mSelectWideMode.setHovered(false);
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mRadioBtnWideMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));
			else
				mRadioBtnWideMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));

			/* 글씨색 변경 */
			mTxtFullMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
			mTxtWideMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
			DxbView_Full.eState_ScreenSize = HP_Index.SCREENMODE_FULL;
		} else if (mRatioMode == HP_Index.DISPLAY_RATIO_4_3) {
			mSelectFullMode.setHovered(false);
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mRadioBtnFullMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_off));
			else
				mRadioBtnFullMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_off));

			mSelectWideMode.setHovered(true);
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				mRadioBtnWideMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.radio_on));
			else
				mRadioBtnWideMode.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.kia_radio_on));

			/* 글씨색 변경 */
			mTxtFullMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
			mTxtWideMode.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
			DxbView_Full.eState_ScreenSize = HP_Index.SCREENMODE_LETTERBOX;
		}

		DxbPlayer.setScreenMode(DxbView_Full.eState_ScreenSize);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.selectFull:
			mRatioMode = HP_Index.DISPLAY_RATIO_16_9;
			setSelectRatio(mRatioMode);
			break;
		case R.id.selectWide:
			mRatioMode = HP_Index.DISPLAY_RATIO_4_3;
			setSelectRatio(mRatioMode);
			break;
		default:
			break;
		}
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_RATIO);
	}
}