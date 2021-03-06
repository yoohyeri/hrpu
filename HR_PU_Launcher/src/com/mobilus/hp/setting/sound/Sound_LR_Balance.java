package com.mobilus.hp.setting.sound;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.LongClickEvent;
import com.mobilus.hp.launcher.LongClickEvent.LongClickCallBack;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.os.Bundle;
import android.os.IMTX;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class Sound_LR_Balance extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[Sound_LR_Balance ]  ";

	public static final int PROGRESS_MAX = 20;

	private static final int POSITION_L = 0;
	private static final int POSITION_R = 1;
	private static final int POSITION_CHANGE = 2;

	private LongClickEvent btnRight, btnLeft;
	private SeekBar skbLRBalance;
	public static int mBalanceStep;

	int BALANCE_STEP_LEVEL = 6;
	int BALANCE_MAX_LEVEL = 126;

	public Sound_LR_Balance() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_sound_lr_balance, container, false);

		return inflater.inflate(R.layout.fragment_sound_lr_balance, container, false);
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
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LR_BALANCE);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SOUND)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SOUND_MENU_L_R_BALANCE)
			return;

		setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	private void setLoadView() {
		btnRight = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnRight);
		btnLeft = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnLeft);

		btnRight.setLongClickCallback(mLongClickEvent);
		btnLeft.setLongClickCallback(mLongClickEvent);

		skbLRBalance = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarBalance);
		skbLRBalance.setMax(PROGRESS_MAX);
		skbLRBalance.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				seekbar.playSoundEffect(SoundEffectConstants.CLICK);
			}

			@Override
			public void onProgressChanged(SeekBar seekbar, int progress, boolean arg2) {
				setProgressBar(seekbar, POSITION_CHANGE);
			}
		});
		updatePref();
	}

	private void updatePref() {
		skbLRBalance.setProgress(mBalanceStep);
//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "updatePref / mBalanceStep : " + mBalanceStep);
	}

	private void mBtnClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btnRight:
			setProgressBar(skbLRBalance, POSITION_R);
			break;
		case R.id.btnLeft:
			setProgressBar(skbLRBalance, POSITION_L);
			break;
		default:
			break;
		}
	}

	private LongClickEvent.LongClickCallBack mLongClickEvent = new LongClickCallBack() {

		@Override
		public void onLongPress(View view) {
			mBtnClick(view);
		}
	};

	@Override
	public void onClick(View view) {
		mBtnClick(view);
	}

	private void setProgressBar(SeekBar seekbar, int position) {
		int value = 0;

		if (position == POSITION_L)
			value = seekbar.getProgress() - 1;
		else if (position == POSITION_R)
			value = seekbar.getProgress() + 1;
		else
			value = seekbar.getProgress();

		if (value < 0 || value > PROGRESS_MAX)
			return;

		if (mBalanceStep == value)
			return;
		
		mBalanceStep = value;

//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + " setProgressBar : " + value);
		seekbar.setProgress(value);

		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LR_BALANCE);
		mSendLR(value);
	}

	private void mSendLR(int _value) {
		int _left = BALANCE_MAX_LEVEL, _right = BALANCE_MAX_LEVEL;
		if (_value == 0)
			_right = 0;
		else if (_value == PROGRESS_MAX)
			_left = 0;
		else if (_value > PROGRESS_MAX / 2)
			_left = BALANCE_MAX_LEVEL - (BALANCE_STEP_LEVEL * (_value - (PROGRESS_MAX / 2))); // right
																								// move
		else if (_value < PROGRESS_MAX / 2)
			_right = BALANCE_MAX_LEVEL - (BALANCE_STEP_LEVEL * ((PROGRESS_MAX / 2) - _value)); // left
																								// move
//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mSendLR / L : " + _left + ", R : " + _right);
		try {
			//200918 yhr
			if(LauncherMainActivity.M_MTX == null)
				LauncherMainActivity.M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));
			
			LauncherMainActivity.M_MTX.setAudioBalance(_left, _right);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public void mBootComplete() {
		mSendLR(mBalanceStep);
	}
}