package com.mobilus.hp.setting.screen;

import com.mobilus.hp.launcher.CustomSeekBar;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.LongClickEvent;
import com.mobilus.hp.launcher.LongClickEvent.LongClickCallBack;
import com.mobilus.hp.setting.PreferenceConst;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.ContentResolver;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;

/**
 * 화면 조정에 대한 Fragment
 * 
 * @author yhr
 *
 */
public class Screen_Adjust extends Fragment implements View.OnClickListener, OnSeekBarChangeListener {

	private static final String CLASS_NAME = "[HP_Screen_Adjust ]  ";

	private static final String VIDEO_COLORCONTROL = "VIDEO_COLORCONTROL";
	private static final int VideoColorSettingDefault = 0x050505;

	private LongClickEvent btnBrightnessDown, btnBrightnessUp;
	private LongClickEvent btnContrastDown, btnContrastUp;
	private LongClickEvent btnHueDown, btnHueUp;
	public static RelativeLayout mAdjustBG;

	private SeekBar mBrightnessSeekbar;
	private SeekBar mContrastSeekbar;
	private SeekBar mSaturationSeekbar;

	private boolean initialized = false;
	private static int mVideoSetting;
	private static TextView tvBrightnessValue, tvContrastValue, tvSaturationValue;

	public static int mBrightnessStep;
	public static int mContrastStep;
	public static int mSaturationStep;
	public static final int PROGRESS_MAX = 10;

	private boolean bIsMaintainView;

	public Screen_Adjust() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_screen_adjust, container, false);

		return inflater.inflate(R.layout.fragment_screen_adjust, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		bIsMaintainView = false;
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_ADJUST);
	}

	@Override
	public void onResume() {
		super.onResume();

//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "onResume / root : " + HP_Manager.mRootMenu + ", sub : " + HP_Manager.mSubMenu + ", bIsMaintainView : " + bIsMaintainView);

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SCREEN)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SCREEN_MENU_ADJUST)
			return;

		if(bIsMaintainView)
			return;
		
		bIsMaintainView = true;
		DxbView.eState = DxbView.STATE.NORMAL_VIEW;

		setLoadView();
		startObserve();
		if(LauncherMainActivity.mParkingStatus)
			HP_Manager.mCallback.onPlayDMB();
	}

	private final static int MSG_SET_BRIGHTNESS	= 0;
	private final static int MSG_SET_CONTRAST	= 1;
	private final static int MSG_SET_HUE		= 2;
	private final static int MSG_DELAY_TIME		= 100;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			int progress = msg.arg1;
			switch (msg.what) {
			case MSG_SET_BRIGHTNESS:
//				Log.d(Dlog.TAG_SETTING, CLASS_NAME + "adjust - MSG_SET_BRIGHTNESS");
				removeMessages(MSG_SET_BRIGHTNESS);
				setBrightness(progress);
				break;
			case MSG_SET_CONTRAST:
//				Log.d(Dlog.TAG_SETTING, CLASS_NAME + "adjust - MSG_SET_CONTRAST");
				removeMessages(MSG_SET_CONTRAST);
				setContrast(progress);
				break;
			case MSG_SET_HUE:
//				Log.d(Dlog.TAG_SETTING, CLASS_NAME + "adjust - MSG_SET_HUE");
				removeMessages(MSG_SET_HUE);
				setSaturation(progress);
				break;
			}
			super.handleMessage(msg);
		}
	};

	public void startObserve() {
//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "startObserve");
		init();
		if (initialized == false) {
			handleVideoColor();
			initialized = true;
		}
	}

	/**
	 * progress 값을 textview의 x좌표에 출력
	 * 
	 * @author yhr
	 * @date 19.01.24
	 * @param textview
	 * @param progress
	 * @param left
	 */
	public static void setText(TextView textview, String progress, float left) {
		if (textview == null)
			return;
		
		textview.setText(progress);
		textview.setX(left);
	}

	private void handleVideoColor() {
		final ContentResolver resolver = HP_Manager.mContext.getContentResolver();
		final int oldVideoColor = mVideoSetting;

		try {
			mVideoSetting = Settings.System.getInt(resolver, VIDEO_COLORCONTROL);
		} catch (SettingNotFoundException e) {
		}

//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "handleVideoColor // oldVideoColor : " + oldVideoColor + ", mVideoSetting : "+ mVideoSetting);
//		if (oldVideoColor != mVideoSetting) {
			updateVideoColor(mVideoSetting);
//		}
	}

	private void updateVideoColor(final int setting) {
		int b, c, s;

		// parse each color setp
		b = (setting >> 16) & 0xFF;
		c = (setting >> 8) & 0xFF;
		s = (setting) & 0xFF;

		// convert to color value
		int brightness = b * 5 - 25;
		int contrast = c * 10 + 50;
		int saturation = s * 15 + 50;

		try {
//			Log.d(Dlog.TAG_SETTING, CLASS_NAME + "updateVideoColor - brightness : " + brightness + ", contrast : " + contrast + ", saturation : " + saturation);
			LauncherMainActivity.M_MTX.setVideoColor(1, brightness, contrast, saturation);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void init() {
		if (HP_Manager.mContext == null) {
//			Log.i(Dlog.TAG_SETTING, CLASS_NAME + "invalid context");
			return;
		}
	}

	public void setVideoColorToDefault() {
//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "setVideoColorToDefault");
//		synchronized (mLock) {
			mVideoSetting = VideoColorSettingDefault;
			updateVideoColor(mVideoSetting);
//		}
	}

	@Override
	public void onStart() {
		DxbView.setState(false, DxbView.STATE.FULL);
		super.onStart();
	}

	/**
	 * GUI 초기화
	 */
	private void setLoadView() {
		mAdjustBG = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.adjustBG);
		
		if(LauncherMainActivity.getInstance().mParkingStatus == false)
			mAdjustBG.setVisibility(View.VISIBLE);
		else
			mAdjustBG.setVisibility(View.GONE);
		
		btnBrightnessDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnBrightnessDown);
		btnBrightnessUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnBrightnessUp);
		btnContrastDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnContrastDown);
		btnContrastUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnContrastUp);
		btnHueDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHueDown);
		btnHueUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHueUp);

		mBrightnessSeekbar = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarBrightness);
		mContrastSeekbar = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarContrast);
		mSaturationSeekbar = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarHue);

		tvBrightnessValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtBrightnessProgress);
		tvContrastValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtContrastProgress);
		tvSaturationValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtHueProgress);

		btnBrightnessDown.setLongClickCallback(mLongClickEvent);
		btnBrightnessUp.setLongClickCallback(mLongClickEvent);
		btnContrastDown.setLongClickCallback(mLongClickEvent);
		btnContrastUp.setLongClickCallback(mLongClickEvent);
		btnHueDown.setLongClickCallback(mLongClickEvent);
		btnHueUp.setLongClickCallback(mLongClickEvent);

		mBrightnessSeekbar.setMax(PROGRESS_MAX);
		mContrastSeekbar.setMax(PROGRESS_MAX);
		mSaturationSeekbar.setMax(PROGRESS_MAX);

		mBrightnessSeekbar.setProgress(PROGRESS_MAX / 2);
		mContrastSeekbar.setProgress(PROGRESS_MAX / 2);
		mSaturationSeekbar.setProgress(PROGRESS_MAX / 2);

		updatePref();

		mBrightnessSeekbar.setOnSeekBarChangeListener(this);
		mContrastSeekbar.setOnSeekBarChangeListener(this);
		mSaturationSeekbar.setOnSeekBarChangeListener(this);
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

	private void mBtnClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btnBrightnessDown:
			setProgressBar(mBrightnessSeekbar, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnBrightnessUp:
			setProgressBar(mBrightnessSeekbar, HP_Index.PROGRESS_UP);
			break;
		case R.id.btnContrastDown:
			setProgressBar(mContrastSeekbar, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnContrastUp:
			setProgressBar(mContrastSeekbar, HP_Index.PROGRESS_UP);
			break;
		case R.id.btnHueDown:
			setProgressBar(mSaturationSeekbar, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnHueUp:
			setProgressBar(mSaturationSeekbar, HP_Index.PROGRESS_UP);
			break;
		default:
			break;
		}
	}

	private void updatePref() {
//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "updatePref / mBrightnessStep : "+ mBrightnessStep + " / mBrightnessSeekbar ; " + (mBrightnessSeekbar.getProgress()-5) + ", tvBrightnessValue : " + tvBrightnessValue.getText().toString()
//				+ ", mContrastStep  : " + mContrastStep  + " / mContrastSeekbar ; " + (mContrastSeekbar.getProgress()-5)  + ", tvContrastValue : " + tvContrastValue.getText().toString()
//				+ ", mSaturationStep : " + mSaturationStep + " / mSaturationSeekbar ; " + (mSaturationSeekbar.getProgress()-5) + ", tvSaturationValue : " + tvSaturationValue.getText().toString());
	
		mBrightnessSeekbar.setProgress(mBrightnessStep);
		mContrastSeekbar.setProgress(mContrastStep);
		mSaturationSeekbar.setProgress(mSaturationStep);

		((CustomSeekBar) mBrightnessSeekbar).setThumbText(tvBrightnessValue, String.valueOf(mBrightnessStep - 5));
		((CustomSeekBar) mContrastSeekbar).setThumbText(tvContrastValue, String.valueOf(mContrastStep - 5));
		((CustomSeekBar) mSaturationSeekbar).setThumbText(tvSaturationValue, String.valueOf(mSaturationStep - 5));
	}

	/**
	 * 프로그레스바 조정
	 * 
	 * @param seekbar
	 * @param state
	 */
	private void setProgressBar(SeekBar seekbar, int state) {
		int value = 0;

		if (state == HP_Index.PROGRESS_DOWN)
			value = seekbar.getProgress() - 1;
		else if (state == HP_Index.PROGRESS_UP)
			value = seekbar.getProgress() + 1;

		if (value < 0 || value > 10)
			return;

		seekbar.setProgress(value);
	}

	private void setColorValue() {
		int value = ((mBrightnessStep & 0xFF) << 16) | ((mContrastStep & 0xFF) << 8) | (mSaturationStep & 0xFF);
		
//		Log.d(Dlog.TAG_SETTING, CLASS_NAME + "==>>> setColorValue // mBrightnessStep : " + mBrightnessStep 
//											+ ", mContrastStep : " + mContrastStep
//											+ ", mSaturationStep : " + mSaturationStep
//											+ ", value : " + value);
									
		Settings.System.putInt(HP_Manager.mContext.getContentResolver(), VIDEO_COLORCONTROL, value);
		handleVideoColor();
	}

	private void setContrast(int value) {
		mContrastStep = value;
		setColorValue();
	}

	private void setBrightness(int value) {
		mBrightnessStep = value;
		setColorValue();
	}

	private void setSaturation(int value) {
		mSaturationStep = value;
		setColorValue();
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

		if (fromUser) {
			seekBar.setProgress(progress);
		}

		mHandler.removeMessages(MSG_SET_BRIGHTNESS);
		mHandler.removeMessages(MSG_SET_CONTRAST);
		mHandler.removeMessages(MSG_SET_HUE);
		
		int perfValue;
		Message msg = new Message();
		switch (seekBar.getId()) {
		case R.id.seekbarBrightness:
			perfValue = seekBar.getProgress() - 5;
			if (seekBar instanceof CustomSeekBar)
				((CustomSeekBar) seekBar).setThumbText(tvBrightnessValue, String.valueOf(perfValue));
			
			if(fromUser == false)
				HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Brightness, progress).commit();
			
			msg.what = MSG_SET_BRIGHTNESS;
			msg.arg1 = progress;
			mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);
			break;
		case R.id.seekbarContrast:
			perfValue = seekBar.getProgress() - 5;
			if (seekBar instanceof CustomSeekBar)
				((CustomSeekBar) seekBar).setThumbText(tvContrastValue, String.valueOf(perfValue));
			
			if(fromUser == false)
				HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Contrast, progress).commit();
			
			msg.what = MSG_SET_CONTRAST;
			msg.arg1 = progress;
			mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);
			break;
		case R.id.seekbarHue:
			perfValue = seekBar.getProgress() - 5;
			if (seekBar instanceof CustomSeekBar)
				((CustomSeekBar) seekBar).setThumbText(tvSaturationValue, String.valueOf(perfValue));
			
			if(fromUser == false)
				HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Saturation, progress).commit();
			
			msg.what = MSG_SET_HUE;
			msg.arg1 = progress;
			mHandler.sendMessageDelayed(msg, MSG_DELAY_TIME);
			break;
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		seekBar.playSoundEffect(SoundEffectConstants.CLICK);
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		int progress = seekBar.getProgress();
		switch (seekBar.getId()) {
		case R.id.seekbarBrightness:
			HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Brightness, progress).commit();
			break;
		case R.id.seekbarContrast:
			HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Contrast, progress).commit();
			break;
		case R.id.seekbarHue:
			HP_Manager.mPreferences.mAdjustPref.edit().putInt(PreferenceConst.ADJUST_Saturation, progress).commit();
			break;
		}
	}

	public void mBootComplete() {
		setColorValue();
		handleVideoColor();
	}
}