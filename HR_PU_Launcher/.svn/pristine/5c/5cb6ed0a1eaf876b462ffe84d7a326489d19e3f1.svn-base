package com.mobilus.hp.setting.sound;

import com.mobilus.hp.launcher.CustomSeekBar;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.LongClickEvent;
import com.mobilus.hp.launcher.LongClickEvent.LongClickCallBack;
import com.mobilus.hp.setting.PreferenceConst;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * 화면 조정에 대한 Fragment
 * @author yhr
 *
 */
public class Sound_Tone extends Fragment implements View.OnClickListener{
	
	private static final String CLASS_NAME = "[Sound_Tone ]  ";
	
	private LongClickEvent btnHighToneDown, btnHighToneUp;
	private LongClickEvent btnMediumToneDown, btnMediumToneUp;
	private LongClickEvent btnLowToneDown, btnLowToneUp;
	private SeekBar skbHighTone, skbMediumTone, skbLowTone;
	
	public static final int PROGRESS_MAX = 20;
	
	public static int mHightToneStep;
	public static int mMediumToneStep;
	public static int mLowToneStep;
	private static TextView tvHightValue, tvMediumValue, tvLowValue;
	
	private final short TON1_INDEX = 0;
	private final short TON3_INDEX = 2;
	private final short TON5_INDEX = 4;
	Equalizer mEqualizer = new Equalizer(0,0);
	private boolean bIsMaintainView;
	
	public Sound_Tone()
	{
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    	if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
    		return inflater.inflate(R.layout._kia_fragment_sound_tone, container, false);
        return inflater.inflate(R.layout.fragment_sound_tone, container, false);
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
		HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_TONE);
	}

	@Override
	public void onResume() {
		super.onResume();
		if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
   			return;
		
		if(HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SOUND)
			return;
		
		if(HP_Manager.mSubMenu != HP_Index.SOUND_MENU_TONE_SET)
			return;
		
		if(bIsMaintainView)
			return;
		
		bIsMaintainView = true;
		setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	/**
	 * GUI 초기화
	 */
	private void setLoadView()
	{		
		btnHighToneDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHighToneDown);
		btnHighToneUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnHighToneUp);
		btnMediumToneDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMediumToneDown);
		btnMediumToneUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnMediumToneUp);
		btnLowToneDown = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnLowToneDown);
		btnLowToneUp = (LongClickEvent) HP_Manager.mContext.findViewById(R.id.btnLowToneUp);
		
		tvHightValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtHightProgress);
		tvMediumValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtMediumProgress);
		tvLowValue = (TextView) HP_Manager.mContext.findViewById(R.id.txtLowProgress);
		
		btnHighToneDown.setLongClickCallback(mLongClickEvent);
		btnHighToneUp.setLongClickCallback(mLongClickEvent);
		btnMediumToneDown.setLongClickCallback(mLongClickEvent);
		btnMediumToneUp.setLongClickCallback(mLongClickEvent);
		btnLowToneDown.setLongClickCallback(mLongClickEvent);
		btnLowToneUp.setLongClickCallback(mLongClickEvent);
				
		skbHighTone = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarHighTone);
		skbMediumTone = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarMediumTone);
		skbLowTone = (SeekBar) HP_Manager.mContext.findViewById(R.id.seekbarLowTone);
		
		skbHighTone.setMax(PROGRESS_MAX);
		skbMediumTone.setMax(PROGRESS_MAX);
		skbLowTone.setMax(PROGRESS_MAX);
		
		skbHighTone.setProgress(PROGRESS_MAX/2);
		skbMediumTone.setProgress(PROGRESS_MAX/2);
		skbLowTone.setProgress(PROGRESS_MAX/2);
				
		updatePref();
		
		skbHighTone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				seekbar.playSoundEffect(SoundEffectConstants.CLICK );
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					seekBar.setProgress(progress);
				}
				
				int perfValue = seekBar.getProgress()-10;
				if (seekBar instanceof CustomSeekBar) {
					((CustomSeekBar) seekBar).setThumbText(tvHightValue, String.valueOf(perfValue));
				}
 				mHightToneStep = progress;
				HP_Manager.mPreferences.mTonePref.edit().putInt(PreferenceConst.TONE_High, progress).commit();
				mSendTon(TON1_INDEX, perfValue);				
			}
		});
		
		skbMediumTone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				seekbar.playSoundEffect(SoundEffectConstants.CLICK );
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					seekBar.setProgress(progress);
				}
				
				int perfValue = seekBar.getProgress()-10;
				if (seekBar instanceof CustomSeekBar) {
					((CustomSeekBar) seekBar).setThumbText(tvMediumValue, String.valueOf(perfValue));
				}
				mMediumToneStep = progress;
				HP_Manager.mPreferences.mTonePref.edit().putInt(PreferenceConst.TONE_Medium, progress).commit();
				mSendTon(TON3_INDEX, perfValue);
			}
		});

		skbLowTone.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
		
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
		
			}
		
			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				seekbar.playSoundEffect(SoundEffectConstants.CLICK );
			}
		
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if (fromUser) {
					seekBar.setProgress(progress);
				}
		
				int perfValue = seekBar.getProgress()-10;
				if (seekBar instanceof CustomSeekBar) {
					((CustomSeekBar) seekBar).setThumbText(tvLowValue, String.valueOf(perfValue));
				}
				mLowToneStep = progress;
				HP_Manager.mPreferences.mTonePref.edit().putInt(PreferenceConst.TONE_Low, progress).commit();
				mSendTon(TON5_INDEX, perfValue);
			}
		});
	}

	/**
	 * progress 값을 textview의 x좌표에 출력
	 * @author yhr
	 * @date 19.01.24
	 * @param textview
	 * @param progress
	 * @param left
	 */
	public static void setText(TextView textview, String progress, float left)
	{
		if(textview == null)
			return;
		
		textview.setText(progress);
		textview.setX(left);
	}
	
	private void updatePref() {
		
		skbHighTone.setProgress(mHightToneStep);
		skbMediumTone.setProgress(mMediumToneStep);
		skbLowTone.setProgress(mLowToneStep);

		((CustomSeekBar) skbHighTone).setThumbText(tvHightValue, String.valueOf(mHightToneStep-10));
		((CustomSeekBar) skbMediumTone).setThumbText(tvMediumValue, String.valueOf(mMediumToneStep-10));
		((CustomSeekBar) skbLowTone).setThumbText(tvLowValue, String.valueOf(mLowToneStep-10));
	}
	
	private void mBtnClick(View view)
	{
		int id = view.getId();
		
		switch (id) {
		case R.id.btnHighToneDown:
			setProgressBar(skbHighTone, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnHighToneUp:
			setProgressBar(skbHighTone, HP_Index.PROGRESS_UP);
			break;
		case R.id.btnMediumToneDown:
			setProgressBar(skbMediumTone, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnMediumToneUp:
			setProgressBar(skbMediumTone, HP_Index.PROGRESS_UP);
			break;
		case R.id.btnLowToneDown:
			setProgressBar(skbLowTone, HP_Index.PROGRESS_DOWN);
			break;
		case R.id.btnLowToneUp:
			setProgressBar(skbLowTone, HP_Index.PROGRESS_UP);
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
	
	/**
	 * 프로그레스바 조정
	 * @param seekbar
	 * @param state
	 */
	private void setProgressBar(SeekBar seekbar, int state)
	{
		int value = 0;
		
		if(state == HP_Index.PROGRESS_DOWN)
			value = seekbar.getProgress()-1;
		else if(state == HP_Index.PROGRESS_UP)
			value = seekbar.getProgress()+1;
		
		if(value < 0 || value > HP_Index.PROGRESS_MAX)
			return;
		
		seekbar.setProgress(value);
	}
	
	private void mSendTon(short _index, int _value) {
		mEqualizer.setEnabled(true);
		mEqualizer.setBandLevel( _index, (short)(_value * (-100)));
	}
	
	public void mBootComplete() {
		mSendTon(TON1_INDEX, mHightToneStep);
		mSendTon(TON3_INDEX, mMediumToneStep);
		mSendTon(TON5_INDEX, mLowToneStep);
	}
}
