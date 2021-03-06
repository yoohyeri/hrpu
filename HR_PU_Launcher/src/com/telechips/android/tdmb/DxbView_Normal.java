/*
 * Copyright (C) 2013 Telechips, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telechips.android.tdmb;

import java.util.ArrayList;
import java.util.List;

import com.mobilus.hp.launcher.DynamicTextSeekbar;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.LongClickEvent;
import com.mobilus.hp.launcher.LongClickEvent.LongClickCallBack;
import com.mobilus.hp.setting.PreferenceConst;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint("ClickableViewAccessibility")
public class DxbView_Normal
{
	private static final String CLASS_NAME = "[DxbView Normal ]  ";
	
	/**
	 * GUI Component
	 */
	public static Component.cNormalView gComponent	= null;
	
	/**
	 * Full Mode 여부
	 */
	public static boolean mIsFullView = true;
	
	/**
	 * Choice Channel 정보
	 */
	public static int mChoiceEnsembleID = -1;
	public static int mChoiceChannelID = -1;
	public static String mChoiceChannelName;
	
	/**
     * Ext Menu List
     */
    public final static int MENU_LIST_DISPLAY_RATIO		= 0;	// 화면비율설정
    public final static int MENU_LIST_ADJUST_VIDEO		= 1;	// 영상 설정
    private static DMBExtMenuListAdapter mExtMenuListAdapter;
    public static ArrayList<DMB_ExtMenu> mExtMenuTitleList = new ArrayList<DMB_ExtMenu>();

    public static boolean mTouchLayout;
	static DxbAdapter_Service adapter;
	public static RelativeLayout notiView;

	public final static int VOL_MAX = 25;
	private static int mCurrentVol = 0;
	public static boolean mfromUserMute;
	
	/**
	 * Initialize
	 */
	static public void init()
	{
		mIsFullView = true;
		setComponent(); 	// GUI초기화
		DxbView_CAS_debug.init(DMB_Manager.mContext);
	}
	
	/**
	 * GUI Initialize
	 */
	private static void setComponent()
	{
		mBackClickEvnetCnt = -1;
		
		gComponent = DMB_Manager.g_Component.normalview;
		notiView = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.noti_view);
		
		// 생산공정
		gComponent.rlProductionProcess = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.ProductionProcessLayout);
		
		// Video
		gComponent.vVideo = DMB_Manager.mContext.findViewById(R.id.dmb_preview);  // 화면터치 시 Full Mode <-> Normal Mode로 바뀜
		gComponent.vVideo.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				return false;
			}
		});
		gComponent.vVideo.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View view, MotionEvent arg1) {
				if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_DMB)
					return false;
		
				if(DxbPlayer.eState == DxbPlayer.STATE.SCAN_STOP || DxbPlayer.eState == DxbPlayer.STATE.SCAN)
					return false;
				
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
					{
						mIsFullView = true;
						ResetDisplayFull();
					}
				}				
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mHandler.removeMessages(MSG_INSERT_CAS_MODE);
					
					if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
					{
						//191218
						if(mTouchLayout == false)
							view.playSoundEffect(SoundEffectConstants.CLICK);
						else
						{
						
							ResetDisplayFull();
							mTouchLayout = false;
							return false;
						}
					}
					
					if(mIsFullView)
						ClearDisplayFull();

					if(mIsFullView)
					{
						DxbView.setState(false, DxbView.STATE.FULL);	// (full view) TV only.
					}
					else
					{
						ResetDisplayFull();
						mIsFullView = true;
					}
				}
				return false;
			}
		});
		
		////////////////////////////////////////////////////////////////////////////
		// Status bar
		/////////////////////////////////////////////////////////////////////////////																	
		gComponent.llStausBar = (LinearLayout) DMB_Manager.mContext.findViewById(R.id.StatusBar);
		gComponent.llStausBar.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mTouchLayout = true;
					mIsFullView = false;
					ClearDisplayFull();
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mTouchLayout = false;
					ResetDisplayFull();
				}
				return false;
			}
		});
		
		////////////////////////////////////////////////////////////////////////////
		// title bar
		/////////////////////////////////////////////////////////////////////////////
		gComponent.llTitleBar = (LinearLayout) DMB_Manager.mContext.findViewById(R.id.title_bar);
		gComponent.llTitleBar.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mTouchLayout = true;
					mIsFullView = false;
					ClearDisplayFull();
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mTouchLayout = false;
					ResetDisplayFull();
				}
				return false;
			}
		});
		
		gComponent.strChannelName = (TextView) DMB_Manager.mContext.findViewById(R.id.titlebar_title);
		
		gComponent.btnMenu = (TextView) DMB_Manager.mContext.findViewById(R.id.btnMenu);
		gComponent.btnMenu.setOnClickListener(mClickListener);
		gComponent.btnMenu.setOnLongClickListener(mLongClickListener);
		gComponent.btnMenu.setOnTouchListener(mTouchListener);
		
		gComponent.btnBack = (TextView) DMB_Manager.mContext.findViewById(R.id.btnBack);
		gComponent.btnBack.setOnClickListener(mClickListener);
		gComponent.btnBack.setOnLongClickListener(mLongClickListener);
		gComponent.btnBack.setOnTouchListener(mTouchListener);
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Menu List 
		////////////////////////////////////////////////////////////////////////////////////////
		
		gComponent.rlExtMenuList = (RelativeLayout) DMB_Manager.mContext.findViewById(R.id.extMenu);
		gComponent.extMenuListView = (ListView) DMB_Manager.mContext.findViewById(R.id.extMenuList);
		
		gComponent.strExtListTitle = DMB_Manager.mContext.getResources().getStringArray(R.array.DMB_ExtMenuList);
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			mExtMenuListAdapter = new DMBExtMenuListAdapter(DMB_Manager.mContext, R.layout.dxb_ext_menu_list_item, mExtMenuTitleList);
		else
			mExtMenuListAdapter = new DMBExtMenuListAdapter(DMB_Manager.mContext, R.layout._kia_dxb_ext_menu_list_item, mExtMenuTitleList);
		
		gComponent.extMenuListView.setAdapter(mExtMenuListAdapter);
		gComponent.extMenuListView.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mTouchLayout = true;
					ClearDisplayFull();
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mTouchLayout = false;
					mNormalView = false;
					ResetDisplayFull();
				}
				return false;
			}
		});
		
		// Ext Menu add
		mExtMenuTitleList.clear();
		for(int i = 0; i < gComponent.strExtListTitle.length; i++)
		{
			DMB_ExtMenu strTitle = new DMB_ExtMenu();
			strTitle.idx = i;
			strTitle.title = gComponent.strExtListTitle[i];
			mExtMenuTitleList.add(strTitle);
		}
		mExtMenuListAdapter.notifyDataSetChanged();
		
		////////////////////////////////////////////////////////////////////////////////////////
		// Channel List 
		////////////////////////////////////////////////////////////////////////////////////////
		gComponent.channelListView = (ListView) DMB_Manager.mContext.findViewById(R.id.channelList);
		gComponent.channelListView.setOnTouchListener(new View.OnTouchListener(){

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mTouchLayout = true;
					ClearDisplayFull();
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mTouchLayout = false;
					ResetDisplayFull();
				}
				return false;
			}
		});
		gComponent.channelListView.setOnItemClickListener(mOnItemClick);
		gComponent.channelListView.setOnScrollListener(mOnScrollListener);

		////////////////////////////////////////////////////////////////////////////
		// Bottom bar
		/////////////////////////////////////////////////////////////////////////////
		gComponent.llControlPanel = (LinearLayout) DMB_Manager.mContext.findViewById(R.id.bottom_bar);	
		gComponent.llControlPanel.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mTouchLayout = true;
					mIsFullView = false;
					ClearDisplayFull();
				}
				else if(arg1.getAction() == MotionEvent.ACTION_UP)
				{
					mTouchLayout = false;
					mIsFullView = true;
					ResetDisplayFull();
				}
				return false;
			}
		});
				
		gComponent.btnScan = (TextView)DMB_Manager.mContext.findViewById(R.id.btnScan);
		gComponent.btnScan.setOnClickListener(mClickListener);
		gComponent.btnScan.setOnLongClickListener(mLongClickListener);
		gComponent.btnScan.setOnTouchListener(mTouchListener);
		
		gComponent.mMuteOnOff = (TextView) DMB_Manager.mContext.findViewById(R.id.btnMuteOnOff);
		gComponent.mMuteOnOff.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View arg0) {
				ClearDisplayFull();
				return false;
			}
		});
		gComponent.mVolumeDown = (LongClickEvent) DMB_Manager.mContext.findViewById(R.id.btnVolDown);
		gComponent.mVolumeUp = (LongClickEvent) DMB_Manager.mContext.findViewById(R.id.btnVolUp);

		gComponent.mSeekBar = (DynamicTextSeekbar) DMB_Manager.mContext.findViewById(R.id.skbPostion);
		gComponent.mSeekBar.setMax(LauncherMainActivity.mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
		mHandler.sendEmptyMessageDelayed(GET_STREAM_VOLUME, 500);
			
		gComponent.mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				LauncherMainActivity.mCurrentVolume = progress;
				DxbView_Normal.ResetDisplayFull();
				
				// mute on/off버튼에 의해 mute off상태가 된 상태
				if (!mfromUserMute) HP_Manager.mCurrentDMBVol = progress;
				
				if (progress == 0) {
					HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_MUTE;
					HP_Manager.mDMBSoundStart = false;
					DxbView.closeAudioOut();
					
					if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
						gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_mute_on));
					else
						gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_mute_on));
				}
				else if (progress > 0) {
					mfromUserMute = false;
					HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
					HP_Manager.mCurrentDMBVol = progress;
					if (HP_Manager.mDMBSoundStart == false) {
						DxbView.startAudioOut();

						if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
							gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_mute_off));
						else
							gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_mute_off));
					}
					
					if(HP_Manager.mProductionProcess == false)
						LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
				}

				LauncherMainActivity.onChangeMuteIcon();

				if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE) {
					int perfValue = seekBar.getProgress();
					if (seekBar instanceof DynamicTextSeekbar) {
						((DynamicTextSeekbar) seekBar).setThumbText(String.valueOf(perfValue));
					}
					return;
				}

				int perfValue = seekBar.getProgress();
				if (seekBar instanceof DynamicTextSeekbar) {
					((DynamicTextSeekbar) seekBar).setThumbText(String.valueOf(perfValue));
					mfromUserMute = false;
					HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
				}
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekbar) {
				seekbar.playSoundEffect(SoundEffectConstants.CLICK);
				DxbView_Normal.ClearDisplayFull();
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				DxbView_Normal.ResetDisplayFull();
				int perfValue = seekBar.getProgress();
				HP_Manager.mPreferences.mDMBSoundPref.edit().putInt(PreferenceConst.CURRENT_VOL, perfValue).commit();
			}
		});

		gComponent.mMuteOnOff.setOnClickListener(onClickListener);
		gComponent.mVolumeDown.setLongClickCallback(mLongClickEvent);
		gComponent.mVolumeUp.setLongClickCallback(mLongClickEvent);
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		gComponent.mWeakSignalPopup = (RelativeLayout)HP_Manager.mContext.findViewById(R.id.toast_popup_layout);
        gComponent.mWeakSignalPopup.setVisibility(View.GONE);
        
		//////////////////////////////////////////////////////////////////////////////
		if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
			gComponent.ID_row = R.layout.dxb_list_row_40px;
		else
			gComponent.ID_row = R.layout._kia_dxb_list_row_40px;
		
		// 생산공정프로그램
		gComponent.btnOK = (Button) HP_Manager.mContext.findViewById(R.id.bt_result_ok);
		gComponent.btnNG = (Button) HP_Manager.mContext.findViewById(R.id.bt_result_ng);
		gComponent.btnProdeuctionSCAN = (Button) HP_Manager.mContext.findViewById(R.id.bt_scan);

		gComponent.btnOK.setEnabled(false);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				gComponent.btnOK.setEnabled(true);
			}
		}, 1000);	
		
		gComponent.btnOK.setOnClickListener(mClickListener);
		gComponent.btnNG.setOnClickListener(mClickListener);
		gComponent.btnProdeuctionSCAN.setOnClickListener(mClickListener);
		gComponent.btnProdeuctionSCAN.setEnabled(true);
		
		if(HP_Manager.mProductionProcess)
		{
			gComponent.rlProductionProcess.setVisibility(View.VISIBLE);
			gComponent.llTitleBar.setVisibility(View.GONE);
			gComponent.llControlPanel.setVisibility(View.GONE);
			LauncherMainActivity.tvStrength.setVisibility(View.VISIBLE);
		}
		else
		{
			gComponent.rlProductionProcess.setVisibility(View.GONE);
			gComponent.llTitleBar.setVisibility(View.VISIBLE);
			gComponent.llControlPanel.setVisibility(View.VISIBLE);
			LauncherMainActivity.tvStrength.setVisibility(View.GONE);
			setVisibleTitleBarBtn(true);
		}
		setMoveView();
	}

	private static void mBtnClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.btnMuteOnOff:
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE) {
				mfromUserMute = true;
				HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_MUTE;
				
				if(HP_Manager.mWidgetMap)
				{
					if (HP_Manager.mNAVIMuteStatus == HP_Index.NAVI_SOUND_MUTE)
						LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_mute));
					else
						LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
				}
				else
					LauncherMainActivity.iconMute.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.icon_dmb_mute));
				
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);

			} else {
				//dmb Mute에서 볼륨 Mute누를때 볼륨바 Mute아이콘 풀리는 이슈 수
				if(HP_Manager.mCurrentDMBVol == 0) return;

				mfromUserMute = false;
				HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_VOLUME);
				DxbView.startAudioOut();
			}

			changeMuteState(HP_Manager.mDMBMuteStatus);
			break;
		case R.id.btnVolDown:		
			int _value = HP_Manager.mCurrentDMBVol - 1;
			if (_value < 0)
				break;

			if (_value == 0) 
			{
				mfromUserMute = true;
				gComponent.mSeekBar.setProgress(_value);
				HP_Manager.mCurrentDMBVol = _value;
				break;
			} 
			else {
				//System Mute일때 dmb Mute가 풀리면 System mute도 풀림
				if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE) {
					HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
					changeMuteState(HP_Manager.mDMBMuteStatus);
				}
				mfromUserMute = false;
			}

			gComponent.mSeekBar.setProgress(_value);
			HP_Manager.mCurrentDMBVol = _value;
			break;
		case R.id.btnVolUp:
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE) {
				HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
				changeMuteState(HP_Manager.mDMBMuteStatus);
			}
			
			int value_ = gComponent.mSeekBar.getProgress() + 1;
			if (value_ > gComponent.mSeekBar.getMax())
				break;

			gComponent.mSeekBar.setProgress(value_);
			HP_Manager.mCurrentDMBVol = value_;
		}
	}

	private static LongClickEvent.LongClickCallBack mLongClickEvent = new LongClickCallBack() {

		@Override
		public void onLongPress(View view) {
			mBtnClick(view);
		}
	};

	private static View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			mBtnClick(v);
		}
	};

	private static void changeMuteState(int state) {
		if (state == HP_Index.DMB_SOUND_MUTE) {
			HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.VISIBLE);
			gComponent.mSeekBar.setProgress(0);
		} else {
			HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.VISIBLE);
			gComponent.mSeekBar.setProgress(getVolume());
		}
	}
	
	public static int getVolume() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"getVolume  1 / HP_Manager.mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus
				+ ", HP_Manager.mIsRebooting : " + HP_Manager.mIsRebooting
				+ ", LauncherMainActivity.mCurrentVolume : " + LauncherMainActivity.mCurrentVolume);
		
		if (HP_Manager.mIsRebooting) {
			//190514
			if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
				mCurrentVol = HP_Manager.mCurrentDMBVol;
			else
				mCurrentVol = 0;

			if (mCurrentVol > VOL_MAX)
				mCurrentVol = VOL_MAX;
			
			LauncherMainActivity.mCurrentVolume = mCurrentVol;
		} else {
			mCurrentVol = HP_Manager.mCurrentDMBVol;
		}

		Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"getVolume  2 / HP_Manager.mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus
				+ ", LauncherMainActivity.mCurrentVolume : " + LauncherMainActivity.mCurrentVolume);
		if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE)
		{
			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_mute_on));
			else
				gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_mute_on));
			return mCurrentVol;
		}
			
		if (HP_Manager.mCurrentDMBVol == 0) {
			//190521 yhr
			HP_Manager.mCallback.onChangeDMBMuteState(HP_Manager.mDMBMuteStatus , true);
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_MUTE) {
				if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_mute_on));
				else
					gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_mute_on));
			}
		} else {
			if (!HP_Manager.mIsNaviGuidance) {
				HP_Manager.mCallback.onChangeDMBMuteState(HP_Manager.mDMBMuteStatus, true);
			} else {
				if (HP_Manager.mNaviSoundStart == false) {
					HP_Manager.mCallback.onChangeDMBMuteState(HP_Manager.mDMBMuteStatus, true);
				} else {
					LauncherMainActivity.mCurrentVolume = mCurrentVol;
					if (HP_Manager.mCurrentDMBVol == 0)
						HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.VISIBLE);
					else
						HP_Manager.mContext.findViewById(R.id.status_mute).setVisibility(View.INVISIBLE);
				}
			}

			if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_mute_off));
			else
				gComponent.mMuteOnOff.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_mute_off));
		}
		
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME+"getVolume  3 / HP_Manager.mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus
				+ ", mCurrentVol : " + mCurrentVol + ",LauncherMainActivity.mCurrentVolume : " +LauncherMainActivity.mCurrentVolume);
		return mCurrentVol;
	}

	public static void mBootComplete()
	{
		if(HP_Manager.mProductionProcess)
			return;
		
		if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
			DxbView.startAudioOut();
		else
		{
			if(!HP_Manager.mWidgetMap)
				HP_Manager.mSystemMuteStatus = HP_Index.SYSTEM_SOUND_UNMUTE;
		
			mfromUserMute = true;
		}
		changeMuteState(HP_Manager.mDMBMuteStatus);
	}

	public static void updateVolume(int progress) {
		gComponent.mSeekBar.setProgress(progress);
	}
	
	public static void setBtnEnable(boolean enable)
	{
		if(enable)
		{
			if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SETTING)
				enable = false;
		}
		
		gComponent.btnScan.setEnabled(enable);
		if(enable)
			gComponent.btnScan.setAlpha(1);
		else
			gComponent.btnScan.setAlpha((float)0.5);
		
		if(DxbPlayer.getScanCount() > 0)
		{
		}
		else
		{
			// 190620 yhr
			int ChannelListVisible = DMB_Manager.mContext.findViewById(R.id.layout_channel_list).getVisibility();
			if(ChannelListVisible == View.VISIBLE)
				DMB_Manager.mContext.findViewById(R.id.layout_channel_list).setVisibility(View.GONE);
			
			DMB_Manager.mContext.findViewById(R.id.layout_bottom).setVisibility(View.GONE);
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) notiView.getLayoutParams();
			lp.leftMargin = 0;
			notiView.setLayoutParams(lp);
		}
	}
	
	/*
	 * 채널리스트 표츌 유무에 따라 화면 문구의 위치가 변경
	 */
	static void setMoveView()
	{
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) notiView.getLayoutParams();
		if(!HP_Manager.mProductionProcess)
		{
			int ChannelListVisible = DMB_Manager.mContext.findViewById(R.id.layout_channel_list).getVisibility();
			if(ChannelListVisible != View.VISIBLE)
				DMB_Manager.mContext.findViewById(R.id.layout_channel_list).setVisibility(View.VISIBLE);

			lp.leftMargin = 290;
		}
		else
			lp.leftMargin = 0;

		DMB_Manager.mContext.findViewById(R.id.layout_bottom).setVisibility(View.VISIBLE);
		notiView.setLayoutParams(lp);
	}
	
	private static int mBackClickEvnetCnt;
	static OnLongClickListener mLongClickListener = new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			int id = v.getId();
			switch(id)
			{
			case R.id.btnMenu:
			case R.id.btnBack:	
			case R.id.btnScan:
				mNormalView = true;
				ClearDisplayFull();
				break;
			}
			return false;
		}
	};
	
	public static boolean mNormalView = false;
	private static OnTouchListener mTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent me) {
			if(me.getAction() == MotionEvent.ACTION_UP)
			{
				int id = v.getId();
				switch(id)
				{
				case R.id.btnMenu:
				case R.id.btnBack:
				case R.id.btnScan:
					mNormalView = false;
					ResetDisplayFull();
					break;
				}
			}
			return false;
		}
	};
	
	/**
	 * Button Click Event
	 */
	static OnClickListener mClickListener = new OnClickListener()
	{
		public void onClick(View v)
		{
			int id = v.getId();
			if(mTouchLayout)
			{
				ResetDisplayFull();
				mTouchLayout = false;
				return;
			}

			if(DxbPlayer.eState == DxbPlayer.STATE.SCAN_STOP)
				return;
			else if(DxbPlayer.eState == DxbPlayer.STATE.SCAN)
			{
				if(v.getId() == R.id.btnScan) {
					DxbPlayer.eState = DxbPlayer.STATE.SCAN_STOP;
					DxbScan.cancel();
				}
				return;
			}
			
			switch(id)
			{
			case R.id.btnMenu:
				if(DxbPlayer.getScanCount() == 0)
				{
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							int menulistvisible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
							if(menulistvisible == View.VISIBLE)
								DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
						}
					}, 10000);
				}
				
				int menulistvisible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
				if(menulistvisible != View.VISIBLE)
					DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.VISIBLE);
				else
					DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
				break;
			case R.id.btnBack:
				mBackClickEvnetCnt++;
				
				//200203 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				
				if(mBackClickEvnetCnt == 0)
					mHandler.sendEmptyMessage(MSG_CHECK_BACK_CLICK_EVENT);
				else
				{
					mHandler.removeMessages(MSG_CHECK_BACK_CLICK_EVENT);
					mHandler.sendEmptyMessageDelayed(MSG_CHECK_BACK_CLICK_EVENT, 200);
				}
				break;
			case R.id.btnScan:
				if(DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility() == View.VISIBLE)
					DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
				if(DxbPlayer.eState == DxbPlayer.STATE.GENERAL)
				{
					// custom dialog
					if(LauncherMainActivity.getInstance() != null)
						LauncherMainActivity.getInstance().mScanDialog.showScanDialog();
				}
				break;
			case R.id.bt_scan:	// 생산공정프로그램에 있는 Scan
				if(DxbPlayer.eState == DxbPlayer.STATE.GENERAL)
				{
					ClearDisplayFull();
					DxbView_Normal.gComponent.btnProdeuctionSCAN.setEnabled(false);
					HP_Manager.mContext.showDialog(DxbView_Message.DIALOG_SCAN_START);
				}
				break;
			case R.id.bt_result_ok:
				if(DxbPlayer.eState != DxbPlayer.STATE.SCAN)
				{
					DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_SET_CHANNEL);
					DxbPlayer.mStart = true;
					DxbPlayer.stop();
					String result = "OK";
					HP_Manager.mContext.exitProductionProgressDMB(result);
					DxbView.closeVideoOut();
					
				}
				HP_Manager.mCurrentView = -1;
				HP_Manager.mProductionProcess = true;

				break;
			case R.id.bt_result_ng:
				if(DxbPlayer.eState != DxbPlayer.STATE.SCAN)
				{
					DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_SET_CHANNEL);
					DxbPlayer.mStart = true;
					DxbPlayer.stop();
					String result_ = "NG";
					HP_Manager.mContext.exitProductionProgressDMB(result_);
					DxbView.closeVideoOut();
				}
				HP_Manager.mCurrentView = -1;
				HP_Manager.mProductionProcess = true;
				break;
			}
		}
	};
	
	static OnScrollListener mOnScrollListener = new OnScrollListener()
	{
		@Override
		public void onScroll(AbsListView arg0, int arg1, int arg2, int arg3) {
			
		}

		@Override
		public void onScrollStateChanged(AbsListView arg0, int scrollState) {
			if(scrollState == OnScrollListener.SCROLL_STATE_IDLE)
				DxbView_Normal.ResetDisplayFull();
			else
				DxbView_Normal.ClearDisplayFull();
		}
	};
	
	/**
	 * List Item Click Event
	 */
	public static int mPrePosition;
	static OnItemClickListener mOnItemClick = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> parent, View v, int position, long id)
		{
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + " ListenerOnItemClick --> onItemClick( position = "+ position+")" + ", mPrePosition : " + mPrePosition);
			
			if(DxbPlayer.eState == DxbPlayer.STATE.SCAN_STOP)
			{
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "ListenerOnItemClick >> please_wait_cancel_scanning");
				return;
			}
			
			if(DxbPlayer.eState == DxbPlayer.STATE.SCAN)
			{
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "ListenerOnItemClick >> please_wait_scanning");
				return;
			}

			if(mPrePosition == position)
			{
				Log.e(HP_Manager.TAG_DMB, CLASS_NAME + " 4 ListenerOnItemClick --> onItemClick( position = "+ position+")" + ", mPrePosition : " + mPrePosition);
				return;
			}
			
			if((DMB_Manager.g_Information.cCOMM.iCurrent_TV != position)
				|| (DMB_Manager.g_Information.cCOMM.iCurrent_Radio != position))
			{
				if(DMB_Manager.g_Information.cCOMM.curChannels != null)
				{
					DMB_Manager.g_Information.cCOMM.curChannels.moveToPosition(position);
					gComponent.channelListView.setSelection(position);
					mPrePosition = position;
					mChoiceChannelName = DMB_Manager.g_Information.cCOMM.curChannels.getString(4);
					gComponent.strChannelName.setText(mChoiceChannelName);
				}
				DxbView.setChannel();
			}		
		}
	};
	
	/**
	 * Full Mode일 경우 Normal Mode로 전환하기위한 removeCallbacks
	 */
	public static void ClearDisplayFull(){
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "mRunnableDisplayFull 0");
		DxbView.mHandler_Main.removeCallbacks(mRunnableDisplayFull);
	}
	
	/**
	 * 5초 뒤에 Full Mode로 전환
	 */
	public static void ResetDisplayFull(){
		
		if(DxbView_Normal.mNormalView)
			return;
		
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "ResetDisplayFull");
		if(HP_Manager.mProductionProcess == false)
		{
			DxbView.mHandler_Main.removeCallbacks(mRunnableDisplayFull);	
			
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "mRunnableDisplayFull 1");
			DxbView.mHandler_Main.postDelayed(mRunnableDisplayFull, 5*HP_Index.TIME_1_SECOND);
		}
		else
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "ResetDisplayFull  // mNormalView : " + DxbView_Normal.mNormalView);
	}
	
	/**
	 * Full Mode <-> Normal Mode로 전환하는 Thread
	 */
	private static Runnable mRunnableDisplayFull = new Runnable() {
		@Override
		public void run() {
			if(DxbView.eState == DxbView.STATE.NORMAL_VIEW) {

				if(DMB_Manager.g_Information.cCOMM.isEnable_Video ) 
				{
					if( (DxbPlayer.eState != DxbPlayer.STATE.SCAN) && (DxbPlayer.eState != DxbPlayer.STATE.SCAN_STOP))
					{
						if(LauncherMainActivity.getInstance().mScanDialog != null && LauncherMainActivity.getInstance().mScanDialog.isShowing())
							mIsFullView = false;
						
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "ResetDisplayFull  // mIsFullView : " + mIsFullView + ", " + SystemProperties.getInt("mtx.systemui.volume", 0));
						
						if(SystemProperties.getInt("mtx.systemui.volume", 0) == 0) {
							if(mIsFullView)
								DxbView.setState(false, DxbView.STATE.FULL);
							return;
						}						
					}
					DxbView.mHandler_Main.postDelayed(this, HP_Index.TIME_1_SECOND);
				}		
				else 
				{
					
					//201211
					Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "############################ 201211 POPUP ERROR ===============");
					
					//201211 "DMB 중지"팝업 원인 (DxbPlayer.getSignalStrength() --> NullPointerException)
//					Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "mRunnableDisplayFull / isEnable_Video : " + DMB_Manager.g_Information.cCOMM.isEnable_Video
//					 + ", getSignalStrength : " + DxbPlayer.getSignalStrength());
				}
			}
		}
	};
	
	/**
	 * Channel List Update
	 * @param iTabNumber
	 */
	static void updateChannelList(int iTabNumber)
	{
		int	cursorPosition;
		if(DMB_Manager.g_Information.cCOMM.curChannels != null)
			DMB_Manager.mContext.stopManagingCursor(DMB_Manager.g_Information.cCOMM.curChannels);
		
		DMB_Manager.g_Information.cCOMM.curChannels = DxbPlayer.getChannels(iTabNumber);
		
		if(DMB_Manager.g_Information.cCOMM.curChannels != null)
		{
			if(iTabNumber == 0)
			{
				DMB_Manager.g_Information.cCOMM.iCount_TV = DMB_Manager.g_Information.cCOMM.curChannels.getCount();
				cursorPosition	= DMB_Manager.g_Information.cCOMM.iCurrent_TV;
			}
			else
			{
				DMB_Manager.g_Information.cCOMM.iCount_Radio	= DMB_Manager.g_Information.cCOMM.curChannels.getCount();
				cursorPosition	= DMB_Manager.g_Information.cCOMM.iCurrent_Radio;
			}
			DMB_Manager.g_Information.cCOMM.curChannels.moveToPosition(cursorPosition);
			DMB_Manager.g_Information.cCOMM.iCount_Current	=  DMB_Manager.g_Information.cCOMM.curChannels.getCount();
			DMB_Manager.mContext.startManagingCursor(DMB_Manager.g_Information.cCOMM.curChannels);
			
			if(adapter == null)
				adapter = new DxbAdapter_Service(DMB_Manager.mContext, gComponent.ID_row, DMB_Manager.g_Information.cCOMM.curChannels, new String[] {}, new int[] {});
			else
				adapter.changeCursor(DMB_Manager.g_Information.cCOMM.curChannels);
			
			gComponent.channelListView.setAdapter(adapter);
			gComponent.channelListView.setSelector(android.R.color.transparent);
			updateChannelCount(iTabNumber, DMB_Manager.g_Information.cCOMM.iCount_Current);
		}
		else
		{
			DMB_Manager.g_Information.cCOMM.iCurrent_TV	= 0;
			DMB_Manager.g_Information.cCOMM.iCurrent_Radio	= 0;
			DMB_Manager.g_Information.cCOMM.iCount_Current	= 0;

			if(iTabNumber == 0)
			{
				DMB_Manager.g_Information.cCOMM.iCount_TV	= 0;
				cursorPosition			= 0;
			}
			else
			{
				DMB_Manager.g_Information.cCOMM.iCount_Radio	= 0;
				cursorPosition			= 0;
			}
			updateChannelCount(iTabNumber, 0);
		}		
	}
	
	/**
	 * Channel Count Update
	 * @param iTab
	 * @param iCount
	 */
	private static void updateChannelCount(int iTab, int iCount)
	{
		DxbView.updateList();
		DxbView.updateScreen();
	}
	
	///////////////////////////////////////////////////////////////////////////////////////
	// Menu 
	
	/**
	 * Ext Manu List Adapter
	 * @author yhr
	 *
	 */
	private static class DMBExtMenuListAdapter extends ArrayAdapter<DMB_ExtMenu> implements View.OnClickListener
	, View.OnLongClickListener, View.OnTouchListener {

        /**
         * Holder
         */
        private ViewHolder mHolder;
        private LayoutInflater mInflater;
        private int mResID;

        /**
         * List<Keyword>
         */
        private List<DMB_ExtMenu> mStrList;

        class ViewHolder {
            TextView btnSelect;
            TextView tvMenu;
        }

        public DMBExtMenuListAdapter(Context context, int resource, List<DMB_ExtMenu> objects) {
            super(context, resource, objects);
            mResID = resource;
            mInflater = ((Activity) context).getLayoutInflater();
            mStrList = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;

            if (row == null) {
                row = mInflater.inflate(mResID, parent, false);

                mHolder = new ViewHolder();
                mHolder.btnSelect = (TextView) row.findViewById(R.id.btnSelectButton);
                mHolder.btnSelect.setOnClickListener(this);
                mHolder.btnSelect.setOnLongClickListener(this);
                mHolder.btnSelect.setOnTouchListener(this);
                mHolder.tvMenu = (TextView) row.findViewById(R.id.tvlistTitle);
                row.setTag(mHolder);
            }
            else
            {
                mHolder = (ViewHolder) convertView.getTag();
            }

            mHolder.tvMenu.setText(mStrList.get(position).title);
            mHolder.btnSelect.setId(position);

            return row;
        }
        
        @Override
        public void onClick(View v) {
            int _id = v.getId();
            
            HP_Manager.mCurrentView = HP_Index.CURRENT_VIEW_SETTING;
			HP_Manager.mBackView = HP_Index.BACK_DMB;	
			ClearDisplayFull();
			
			HP_Manager.mChangeDMB = true;
			if(_id == MENU_LIST_DISPLAY_RATIO) {
				gComponent.llControlPanel.setVisibility(View.GONE);
				setBtnEnable(false);
				HP_Manager.mCallback.goSettingMenu(HP_Index.FRAGMENT_SET_SCREEN, HP_Index.SUB_MENU_LIST_0);
			}
			else if(_id == MENU_LIST_ADJUST_VIDEO) {
				gComponent.llControlPanel.setVisibility(View.GONE);
				HP_Manager.mCallback.goSettingMenu(HP_Index.FRAGMENT_SET_SCREEN, HP_Index.SUB_MENU_LIST_3);
			}
            
            int extMenuList_visible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
            if(extMenuList_visible == View.VISIBLE)
    			DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
        }
    
		@Override
		public boolean onLongClick(View arg0) {
			mNormalView = true;
			ClearDisplayFull();
			return false;
		}

		@Override
		public boolean onTouch(View arg0, MotionEvent arg1) {
			if(arg1.getAction() == MotionEvent.ACTION_UP)
			{
				mNormalView = false;
				ResetDisplayFull();
			}
			return false;
		}
    }
	
	public final static int MSG_INSERT_CAS_MODE	= 0;
	private final static int MSG_CHECK_BACK_CLICK_EVENT	= 1;
	private final static int GET_STREAM_VOLUME = 2;
	
	/**
	 * yhr
	 */
	public static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_STREAM_VOLUME:
				removeMessages(GET_STREAM_VOLUME);
				mCurrentVol = getVolume();
				LauncherMainActivity.mCurrentVolume = mCurrentVol;
				if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
				{
					HP_Manager.mCurrentDMBVol = mCurrentVol;
					gComponent.mSeekBar.setProgress(mCurrentVol);
					if(HP_Manager.mCurrentDMBVol > 0)
					{
						if(HP_Manager.mProductionProcess == false)
							LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, HP_Manager.mCurrentDMBVol, 0);
					}
					else
						DxbView.closeAudioOut();
				}

				((DynamicTextSeekbar) gComponent.mSeekBar).setThumbText(String.valueOf(HP_Manager.mCurrentDMBVol).toString());
				break;
			case MSG_INSERT_CAS_MODE:
				removeMessages(MSG_INSERT_CAS_MODE);
				
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "MSG_INSERT_CAS_MODE // " + LauncherMainActivity.engineer_cas );
				if(LauncherMainActivity.engineer_cas) {
					LauncherMainActivity.engineer_cas = false;
					LauncherMainActivity.cas_state = true;
					ClearDisplayFull();
					DxbView_CAS_debug.setVisible(true);
					HP_Manager.mContext.findViewById(R.id.layout_cas_debug).setVisibility(View.VISIBLE);
					LauncherMainActivity.getInstance().setStatusBarButteonEnable(false);
				}
				
				break;
			case MSG_CHECK_BACK_CLICK_EVENT:
				removeMessages(MSG_CHECK_BACK_CLICK_EVENT);
				
				if(mBackClickEvnetCnt > 0)
					mBackClickEvnetCnt = -1;
				
				int Menuvisible = DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility();
				if(Menuvisible == View.VISIBLE)
					DMB_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);

				//200203 yhr
				HP_Manager.mLastMode = HP_Index.LAST_MODE_HOME;
				HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_LAST_VIEW);
				HP_Manager.mCallback.onChangeHome();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
	
	public static void setVisibleTitleBarBtn(boolean visible)
	{
		if(!visible)
		{
			gComponent.btnScan.setVisibility(View.GONE);
			gComponent.btnMenu.setVisibility(View.GONE);
			gComponent.btnBack.setVisibility(View.GONE);
		}
		else
		{
			gComponent.btnScan.setVisibility(View.VISIBLE);
			gComponent.btnMenu.setVisibility(View.VISIBLE);
			gComponent.btnBack.setVisibility(View.VISIBLE);
		}
	}
}