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

import com.mobilus.hp.launcher.DMB_Widget;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.LauncherMainActivity.DXB_LIFE_CYCLE;
import com.telechips.android.tdmb.player.TDMBPlayer;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class DxbView {
	static String CLASS_NAME = "[DxbView ]  ";

	public static Component gComponent;
	public static Information gInformation;
	private static Information.OPTION gInfo_Option;

	// DxbView State
	public enum STATE {
		WIDGET, NORMAL_VIEW, FULL, NULL
	}

	public static STATE eState = STATE.NULL;

	public static final int DMB_VIEW_NORMAL = 0;
	public static final int DMB_VIEW_SCAN = DMB_VIEW_NORMAL + 1;
	public static final int DMB_VIEW_CHANNEL_CHANING = DMB_VIEW_NORMAL + 2;
	public static final int DMB_VIEW_WEAK = DMB_VIEW_NORMAL + 3;
//	public static final int DMB_VIEW_PARKING = DMB_VIEW_NORMAL + 4;
	public static int mDMBViewState = DMB_VIEW_NORMAL;

	// SurfaceView
	static SurfaceView mSurfaceView;
	static SurfaceHolder mSurfaceHolder;

	/* Display */
	int Surface_width;
	int Surface_height;

	static Handler mHandler_Main = new Handler();

	// Setting DB
	public static DxbDB_Setting mDB;

	// Option
	public static Intent intentSubActivity;

	static int mAudioMaxVolume = 0;
	static int mAudioCurrentVolume = 0;

	private static AnimationDrawable frameAnimation = null;
	public static boolean mIsShowScreen = false;;

	public static final int MPROGRESS_READY = 0;
	public static final int MPROGRESS_SHOW = 1; // 채널 전환중
	public static final int MPROGRESS_UNSHOW = 2;
	public static final int MNO_SIGNAL_SHOW = 5; // 신호약함
	static int mProgressState = MPROGRESS_READY;

	static int HALF_COUNT_LIST = 2;

	static boolean mIsPrepared = false;

	// SCAN 화면
	private static TextView textProgress = null;
	private static TextView textSearchChannel = null;
	private static ProgressBar scanProgressBar = null;
	private static RelativeLayout scanView = null;
	private static TextView btnCancle = null;

	public static FrameLayout widget = null;
	public static RelativeLayout widgetNotiBg = null;
	public static LinearLayout llwidgetNotiBg = null;
	public static LinearLayout llwidgetDMBOff = null;
	public static TextView widgetNotiMsg = null;		// 주행 중 시청금지 안내문구
	public static ImageView widgetNotiImage = null;

	private static ImageView widget_noti_imageView;

	/**
	 * Initialize
	 */
	static void init() {
		gComponent = DMB_Manager.g_Component;
		gInformation = DMB_Manager.g_Information;
		gInfo_Option = DMB_Manager.g_Information.cOption;

		initSystemSet();

		DxbView_Widget.init();
		DxbView_Normal.init();
		DxbView_Full.init();
		
		setDefaultValue();

		scanView = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.scan_view);
		btnCancle = (TextView) HP_Manager.mContext.findViewById(R.id.btnScanStop);
		textProgress = (TextView) HP_Manager.mContext.findViewById(R.id.scan_txt_progress);
		scanProgressBar = (ProgressBar) HP_Manager.mContext.findViewById(R.id.scan_progress);
		textSearchChannel = (TextView) HP_Manager.mContext.findViewById(R.id.scan_txt_channel);

		widgetNotiBg = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_bg);
		
		llwidgetNotiBg = (LinearLayout) HP_Manager.mContext.findViewById(R.id.widget_pre_image);
		widgetNotiImage = (ImageView) HP_Manager.mContext.findViewById(R.id.widget_image);
		widgetNotiMsg = (TextView) HP_Manager.mContext.findViewById(R.id.widget_text);
		
		// dmb off
		llwidgetNotiBg = (LinearLayout) HP_Manager.mContext.findViewById(R.id.widget_dmb_off);
		
		btnCancle.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DxbScan.cancel();
			}
		});
		HP_Manager.mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		DxbView_Normal.updateChannelList(0);
	}

	/**
	 * Initialize System
	 */
	private static void initSystemSet() {
		Display display = ((WindowManager) HP_Manager.mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		gInformation.cCOMM.iDisplayWidth = display.getWidth();
		gInformation.cCOMM.iDisplayHeight = display.getHeight();

		DisplayMetrics displayMetrics = new DisplayMetrics();
		HP_Manager.mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
	}

	/**
	 * Initialize Surface
	 */
	static void initDMBSurfaceView() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "initDMBSurfaceView");
		mSurfaceView = (SurfaceView) HP_Manager.mContext.findViewById(R.id.surface_view);
		if (mSurfaceView == null) {
			Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "SurfaceView is Null");
			return;
		}

		SurfaceHolder holder = mSurfaceView.getHolder();
		if (holder == null) {
			Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "SurfaceHolder is Null");
			return;
		}
		holder.addCallback(mSHCallback);
	}

	/**
	 * Surface Callback
	 */
	static SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			if (mSurfaceHolder == null)
				return;
			DxbPlayer.setLCDUpdate();
			DxbPlayer.useSurface(0);
			initSystemSet();

			if (eState == STATE.FULL)
				FullView_SetLayout();
			else if (eState == STATE.NORMAL_VIEW)
				FullView_SetLayout();
			else if (eState == STATE.WIDGET)
				WidgetView_SetLayout();

			updateScreen();
		}

		public void surfaceCreated(SurfaceHolder holder) {
			mSurfaceHolder = holder;
			mSurfaceHolder.setFixedSize(HP_Index.WIDTH_SCREEN, HP_Index.HEIGHT_SCREEN);

			if (DxbPlayer.mPlayer != null) {
				DxbPlayer.mPlayer.setDisplay(mSurfaceHolder);
				DxbPlayer.setSurface();
			} else {
				DxbPlayer.setListener_Player();
				DxbPlayer.setSurface();
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			mSurfaceHolder = null;
			DxbPlayer.releaseSurface();
		}
	};

	/**
	 * Set Play Mode Default : Normal Mode
	 */
	private static void setDefaultValue() {
		if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
			eState = STATE.WIDGET;
		else if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
			eState = STATE.NORMAL_VIEW;

		DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
	}

	/**
	 * 화면 전환을 위한 State 설정
	 * 
	 * @param isRefresh
	 * @param eChange_state
	 */
	public static void setState(boolean isRefresh, STATE eChange_state) {
		
		//191017
		if(LauncherMainActivity.getInstance().mScanDialog.isShowing() && eChange_state == STATE.FULL)
			return;
		
		if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_SCREEN_SAVER)
			return;
		
		if ((!isRefresh) && (eState == eChange_state)) {
			Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "Fail : Dxb_setViewState() - state error");
			return;
		}

		if (HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME){
			eChange_state = STATE.WIDGET;
		}
		else if (HP_Manager.mCurrentView == HP_Index.FRAGMENT_HIDDEN_MENU)
			eChange_state = STATE.NORMAL_VIEW;

		if (LauncherMainActivity.cas_state)
			eChange_state = STATE.NORMAL_VIEW;

		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "Dxb_setViewState(eChange_state=" + eChange_state + ")" + "/ currentView : "
						+ HP_Manager.mCurrentView + ", cas_state : " + LauncherMainActivity.cas_state
						+ ", mDMBVideoOnOff : " + HP_Manager.mDMBVideoOnOff );

		updateBackgroundLayout(eChange_state);

		if (eChange_state == STATE.FULL) {
			LauncherMainActivity.engineer_cas = false;
			if (isRefresh && (DxbPlayer.eState == DxbPlayer.STATE.OPTION_MANUAL_SCAN)) {
				DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
				DxbScan.startManual(gInfo_Option.scan_manual);
			}
		}

		if (eChange_state == STATE.NORMAL_VIEW) {
			//200303 yhr
			if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
				HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
			
			FullView_SetLayout();
		} else if (eChange_state == STATE.FULL) {
			DxbView_Normal.ClearDisplayFull();
			FullView_SetLayout();
		} else if (eChange_state == STATE.WIDGET) {
			DxbView_Normal.ClearDisplayFull();
			WidgetView_SetLayout();
		}
		updateScreen();
	}

	/**
	 * Set DMB Widget Mode
	 * 
	 * @return
	 */
	static Rect WidgetView_SetLayout() {
		Rect crop_rect = new Rect();

		int cropX = 407;
		int cropY = 69;
		int cropWidth = HP_Index.WIDGET_DMB_WIDTH;
		int cropHeight = HP_Index.WIDGET_DMB_HEIGHT;

		crop_rect.set(cropX, cropY, cropWidth + cropX, cropHeight + cropY);
		View child = (View) HP_Manager.mContext.findViewById(R.id.surface_view);
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(cropWidth, cropHeight, cropX, cropY);
		child.setLayoutParams(lp);

		return crop_rect;
	}

	static Rect FullView_SetLayout() {
		Rect crop_rect = new Rect();

		int layoutWidth, layoutHeight;
		int startX, startY;

		// screen mode setting value on fullview
		layoutWidth = getToPosition(HP_Index.COORDINATES_X, HP_Index.WIDTH_SCREEN);
		layoutHeight = getToPosition(HP_Index.COORDINATES_Y, HP_Index.HEIGHT_SCREEN);

		startX = getToPosition(HP_Index.COORDINATES_X, 0);
		startY = getToPosition(HP_Index.COORDINATES_Y, 0);

		float disp_ratio = (layoutWidth / (float) layoutHeight);
		float video_ratio = (float) (4 / 3.0);

		int width, height;
		View child = (View) HP_Manager.mContext.findViewById(R.id.surface_view);
		switch (DxbView_Full.eState_ScreenSize) {
		case HP_Index.SCREENMODE_LETTERBOX: // 4:3
		{
			if (disp_ratio > video_ratio) {
				width = (int) (layoutHeight * 4 / 3.0);
				startX = (layoutWidth - width) / 2;
				layoutWidth = width;
			} else if (disp_ratio < video_ratio) {
				height = (int) (layoutWidth * 3 / 4.0);
				startY = (layoutHeight - height) / 2;
				layoutHeight = height;
			}
		}

		crop_rect.set(startX, startY, layoutWidth + startX, layoutHeight + startY);
		AbsoluteLayout.LayoutParams lp = new AbsoluteLayout.LayoutParams(layoutWidth, layoutHeight, startX, startY);
		child.setLayoutParams(lp);
		break;

		case HP_Index.SCREENMODE_PANSCAN: {
			int cropX = 0;
			int cropY = 0;
			int cropWidth = 320;
			int cropHeight = 240;
			int r;

			if (disp_ratio > video_ratio) {
				cropHeight = (int) (240 * video_ratio / disp_ratio);
				cropY = (240 - cropHeight) / 2;
				r = cropY % 4;
				cropY -= r;
				cropHeight += r;
				cropHeight = (cropHeight >> 2) << 2;
			} else if (disp_ratio < video_ratio) {
				cropWidth = (int) (320 * disp_ratio / video_ratio);
				cropWidth = ((cropWidth + 15) >> 4) << 4;
				cropX = (320 - cropWidth) / 2;
			}
			crop_rect.set(cropX, cropY, cropWidth + cropX, cropHeight + cropY);
		}
		AbsoluteLayout.LayoutParams _lp = new AbsoluteLayout.LayoutParams(layoutWidth, layoutHeight, startX, startY);
		child.setLayoutParams(_lp);
		break;

		case HP_Index.SCREENMODE_FULL:

			int cropX = 0;
			int cropY = 0;
			int cropWidth = HP_Index.WIDTH_SCREEN;
			int cropHeight = HP_Index.HEIGHT_SCREEN;

			crop_rect.set(cropX, cropY, cropWidth + cropX, cropHeight + cropY);
			AbsoluteLayout.LayoutParams lp_ = new AbsoluteLayout.LayoutParams(cropWidth, cropHeight, cropX, cropY);
			child.setLayoutParams(lp_);

			break;
		}
		return crop_rect;
	}

	private static int getToPosition(int co, int position) {
		int return_position = 0;

		if (co == HP_Index.COORDINATES_X) {
			return_position = position * gInformation.cCOMM.iDisplayWidth / HP_Index.WIDTH_SCREEN;
		} else if (co == HP_Index.COORDINATES_Y) {
			return_position = position * gInformation.cCOMM.iDisplayHeight / HP_Index.HEIGHT_SCREEN;
		}
		return return_position;
	}

	/**
	 * Update Background Layout - 화면모드에 따라 화면갱신
	 * 
	 * @param eChange_state
	 */
	private static void updateBackgroundLayout(STATE eChange_state) {
		if (eChange_state != STATE.NORMAL_VIEW)
			HP_Manager.mContext.findViewById(R.id.layout_normal).setVisibility(View.GONE);

		if (eChange_state != STATE.FULL)
			HP_Manager.mContext.findViewById(R.id.layout_full).setVisibility(View.GONE);

		if (eChange_state == STATE.NORMAL_VIEW) {
			HP_Manager.mContext.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
					| WindowManager.LayoutParams.FLAG_FULLSCREEN);
			HP_Manager.mContext.findViewById(R.id.id_channel_bg).setBackgroundColor(HP_Manager.mContext.getResources().getColor(R.color.color_2));
			HP_Manager.mContext.findViewById(R.id.layout_normal).setVisibility(View.VISIBLE);
			if (!HP_Manager.mProductionProcess) {
				HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
				HP_Manager.mContext.findViewById(R.id.title_bar).setVisibility(View.VISIBLE);

			} else {
				HP_Manager.mContext.findViewById(R.id.title_bar).setVisibility(View.GONE);
				HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.GONE);
			}

		} else if (eChange_state == STATE.FULL) {
			HP_Manager.mContext.getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_FULLSCREEN
							| 0x80000000, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_FULLSCREEN | 0x80000000);
			HP_Manager.mContext.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			HP_Manager.mContext.findViewById(R.id.id_channel_bg).setBackgroundColor(HP_Manager.mContext.getResources().getColor(R.color.color_2));
			HP_Manager.mContext.findViewById(R.id.layout_full).setVisibility(View.VISIBLE);
			HP_Manager.mContext.findViewById(R.id.layout_normal).setVisibility(View.GONE);
			
			if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
				HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.GONE);
			
			if (HP_Manager.mContext.findViewById(R.id.layout_ext_menu_list).getVisibility() == View.VISIBLE)
				HP_Manager.mContext.findViewById(R.id.layout_ext_menu_list).setVisibility(View.GONE);
		} else if (eChange_state == STATE.WIDGET) {
			if (HP_Manager.mContext.findViewById(R.id.StatusBar).getVisibility() == View.GONE)
				HP_Manager.mContext.findViewById(R.id.StatusBar).setVisibility(View.VISIBLE);
		}
		eState = eChange_state;
	}

	private static String strMsg = "";
	/**
	 * 모드에 따라 화면 업데이트
	 */
	@SuppressWarnings("static-access")
	public static void updateScreen() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "updatescreen // mProgressState : " + mProgressState + ", mParkingStatus : " + LauncherMainActivity.mParkingStatus 
				+ ", HP_Manager.mRootMenu : " + HP_Manager.mRootMenu + " , sub : " + HP_Manager.mSubMenu);

		if (HP_Manager.mCurrentView == HP_Index.FRAGMENT_HIDDEN_MENU)
			return;
		
		View View = null;
		RelativeLayout notiBg = null;
		ImageView imageView = null;
		TextView textView_info = null;
		TextView scan_textView = null;
		TextView scan_stop = null;

		RelativeLayout notiView = null;

		int image = 0, text = 0, list_text = 0, info_text = 0;

		notiBg = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.noti_bg);
		notiBg.setVisibility(View.GONE);
		notiView = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.noti_view);
		notiView.setVisibility(View.GONE);
		
		scan_textView = (TextView) HP_Manager.mContext.findViewById(R.id.scan_text);
		scan_stop = (TextView) HP_Manager.mContext.findViewById(R.id.btnScanStop);
		
		// 200221 yhr
		if(HP_Manager.mProductionProcess && HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
			HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
		
		if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
			mDMBViewState = DMB_VIEW_SCAN;
			
			if(LauncherMainActivity.tvStrength.getVisibility() == View.VISIBLE)
				LauncherMainActivity.tvStrength.setVisibility(View.GONE);

			if (gComponent.normalview.llTitleBar.getVisibility() == View.VISIBLE)
				HP_Manager.mContext.findViewById(R.id.title_bar).setVisibility(View.INVISIBLE);

			if (gComponent.normalview.channelListView.getVisibility() == View.VISIBLE)
				gComponent.normalview.channelListView.setVisibility(View.GONE);
			
			// 191022
			if (gComponent.normalview.llControlPanel.getVisibility() == View.VISIBLE)
				gComponent.normalview.llControlPanel.setVisibility(View.GONE);

			if (HP_Manager.mProductionProcess) {
				RelativeLayout.LayoutParams scanlayout = (RelativeLayout.LayoutParams) scanView.getLayoutParams();
				scanlayout.topMargin = 110;
				scanView.setLayoutParams(scanlayout);
			} else {
				RelativeLayout.LayoutParams scanlayout = (RelativeLayout.LayoutParams) scanView.getLayoutParams();
				scanlayout.topMargin = 90;
				scanView.setLayoutParams(scanlayout);
			}
			scanView.setVisibility(View.VISIBLE);
			notiView.setVisibility(View.GONE);
			notiBg.setVisibility(View.GONE);
			textProgress.setText("0%");
		} else {
			if (HP_Manager.mContext.findViewById(R.id.title_bar).getVisibility() == View.INVISIBLE)
				HP_Manager.mContext.findViewById(R.id.title_bar).setVisibility(View.VISIBLE);
			
			scanView.setVisibility(View.GONE);
			mDMBViewState = DMB_VIEW_NORMAL;
			
			notiView.setVisibility(View.VISIBLE);
			notiBg.setVisibility(View.VISIBLE);
			
			DxbView_Normal.setMoveView();
		}

		// Select - state
		if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
			gInformation.cCOMM.isEnable_Video = false;
			image = R.drawable.notice_scan;
			if (HP_Manager.mProductionProcess)
				strMsg = "Channel scanning..";
			else {
				info_text = R.string.msg_scanning;
				list_text = R.string.msg_scanning;
			}
			
		} else {
			if(HP_Manager.mProductionProcess)
			{
				strMsg = "";
				// 채널이 없을 경우
				if(HP_Manager.mCntSearchChannel == 0)
				{
					if(LauncherMainActivity.tvStrength.getVisibility() == View.VISIBLE)
						LauncherMainActivity.tvStrength.setVisibility(View.GONE);
					
					gInformation.cCOMM.isEnable_Video = false;
					image = R.drawable.notice_parking;
					if (DxbPlayer.eState == DxbPlayer.STATE.SCAN_FAIL) {
						strMsg = "Failed to the channel surfing.\nPlease try again in a few minutes.";

						// 19.1.18 yhr ==> Scan 중에 이전채널/채널검색/다음채널 버튼 활성화
						DxbView_Normal.setBtnEnable(true);
					} else {
						if (HP_Manager.mProductionProcess)
							strMsg = "There is no channel.";
						else
							text = R.string.no_channel;
					}
					DxbView_Normal.gComponent.btnProdeuctionSCAN.setEnabled(true);
					DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
					list_text = text;
					info_text = text;

					// if(DxbPlayer.getServiceName() != null)
					{
						if (eState == STATE.NORMAL_VIEW)
							gComponent.normalview.strChannelName.setText(HP_Manager.mContext.getResources().getString(R.string.dmb_title));
						else if (eState == STATE.WIDGET) {
							if (DMB_Widget.mChannelName != null) {
								DMB_Widget.mChannelName.setText(HP_Manager.mContext.getResources().getString(R.string.dmb_title));
//								Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "updateScreen - No Channel ");
							}
						}
					}
				}
				else
				{
					if(LauncherMainActivity.tvStrength.getVisibility() != View.VISIBLE)
						LauncherMainActivity.tvStrength.setVisibility(View.VISIBLE);
					
					DxbView_Normal.setBtnEnable(true);
					DxbPlayer.eState = DxbPlayer.STATE.GENERAL;

					if (mProgressState == MNO_SIGNAL_SHOW) {
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "1 updateScreen - Channel Change - " + DxbPlayer.getServiceName());
					} else if (mProgressState <= MPROGRESS_SHOW) {
						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "2 updateScreen - Channel Change - " + DxbPlayer.getServiceName());
						gInformation.cCOMM.isEnable_Video = false;
						image = R.drawable.ch_chainge_01;
						strMsg = "Changing channel";
						mDMBViewState = DMB_VIEW_CHANNEL_CHANING;		
					}
				}
			}
			else
			{
				DxbView_Normal.setBtnEnable(true);
				
				// 191022
				DxbView_Normal.gComponent.llControlPanel.setVisibility(View.VISIBLE);
				
				if (gInformation.cCOMM.iCount_Current <= 0) {
					gInformation.cCOMM.isEnable_Video = false;
					image = R.drawable.notice_parking;
					if (DxbPlayer.eState == DxbPlayer.STATE.SCAN_FAIL) {
						text = R.string.scan_fail;

						// 19.1.18 yhr ==> Scan 중에 이전채널/채널검색/다음채널 버튼 활성화
						DxbView_Normal.setBtnEnable(true);

						DxbPlayer.mTDMBChannelList.clear();
						if (DxbPlayer.mBackupChannelList.size() > 0) {
							DxbPlayer.mTDMBChannelList.addAll(DxbPlayer.mBackupChannelList);
						}
						DxbPlayer.mBackupChannelList.clear();
					} else {
						text = R.string.no_channel;
					}

					DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
					list_text = text;
					info_text = text;

					// if(DxbPlayer.getServiceName() != null)
					{
						if (eState == STATE.NORMAL_VIEW)
							gComponent.normalview.strChannelName
							.setText(HP_Manager.mContext.getResources().getString(R.string.dmb_title));
						else if (eState == STATE.WIDGET) {
							if (DMB_Widget.mChannelName != null) {
								DMB_Widget.mChannelName.setText(HP_Manager.mContext.getResources().getString(R.string.dmb_title));
							}
						}
					}
				} else {
					if (mProgressState == MNO_SIGNAL_SHOW) {
//						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "updateScreen - Signal Strength Weak");
						mDMBViewState = DMB_VIEW_WEAK;
						gInformation.cCOMM.isEnable_Video = false;
						image = R.drawable.notice_weaksignal;

						if (HP_Manager.mProductionProcess)
							strMsg = "Receiving signal strength is weak.\nPlease wait a minute.";
						else
							info_text = R.string.receiving_signal_strength_is_weak; // 수신감도가 약할때

					} else if (mProgressState <= MPROGRESS_SHOW) {
						
//						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "updateScreen - Channel Change - " + DxbPlayer.getServiceName());
						gInformation.cCOMM.isEnable_Video = false;
						image = R.drawable.ch_chainge_01;

						if (HP_Manager.mProductionProcess)
							strMsg = "Changing channel";
						else
							info_text = R.string.msg_channel_change; // 화면 전환할때

						mDMBViewState = DMB_VIEW_CHANNEL_CHANING;

						// 위젯에 채널 제목 출력
						if (DxbPlayer.getServiceName() != null) {
							gComponent.normalview.strChannelName.setText(DxbPlayer.getServiceName());
							if (eState == STATE.WIDGET && DMB_Widget.mChannelName != null) {
								DMB_Widget.mChannelName.setText(DxbPlayer.getServiceName());
							}
						}
					}
				}
			}
		}

		///////////////////////////////////////////////////////////////////////////
		// Select - Display area --> Parking Gear
		if (eState == STATE.NORMAL_VIEW) {
			imageView = (ImageView) HP_Manager.mContext.findViewById(R.id.noti_image);
			View = gComponent.fullview.vVideo;
			textView_info = (TextView) HP_Manager.mContext.findViewById(R.id.noti_text);
			
			if (image == 0) {
				if (LauncherMainActivity.mParkingStatus == false) {
					image = R.drawable.notice_parking;

					if (HP_Manager.mProductionProcess)
						strMsg = "While driving, the screen is not displayed.\nIf you would like to operate, park your vehicle";
					else
						info_text = R.string.msg_parking;
					
					gInformation.cCOMM.isEnable_Video = false;
				} else {
					gInformation.cCOMM.isEnable_Video = true;
				}
			}
			
			if (!HP_Manager.mProductionProcess)
			{
//				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "llControlPanel.setVisibility(View.VISIBLE) 1");
//				DxbView_Normal.gComponent.llControlPanel.setVisibility(View.VISIBLE);
			}
			else
			{
				DxbView_Normal.gComponent.llControlPanel.setVisibility(View.GONE);
			}

		} else if (eState == STATE.FULL) {
			imageView = (ImageView) HP_Manager.mContext.findViewById(R.id.full_image);
			View = gComponent.fullview.vVideo;
			textView_info = (TextView) HP_Manager.mContext.findViewById(R.id.full_text);
			
			if (image == 0) {
				if (LauncherMainActivity.mParkingStatus == false) {
					image = R.drawable.notice_parking;

					if (HP_Manager.mProductionProcess)
						strMsg = "While driving, the screen is not displayed.\nIf you would like to operate, park your vehicle";
					else
						info_text = R.string.msg_parking;

					gInformation.cCOMM.isEnable_Video = false;
				} else {
					gInformation.cCOMM.isEnable_Video = true;
				}
			}
		} else if (eState == STATE.WIDGET) {
			if (DMB_Widget.rlWidgetBg != null) {
				DMB_Widget.rlWidgetBg.setVisibility(View.VISIBLE);
			}

			imageView = (ImageView) HP_Manager.mContext.findViewById(R.id.widget_image);
			View = gComponent.widgetview.vVideo;
			textView_info = (TextView) HP_Manager.mContext.findViewById(R.id.widget_text);
//			textView_info.setText(HP_Manager.mContext.getResources().getString(info_text));  // 190611 yhr
			
			if (imageView == null)
				imageView = widget_noti_imageView;
						
			
			if (image == 0) {
				//200220 yhr
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
				{	
					if (LauncherMainActivity.mParkingStatus == false) {
						image = R.drawable.notice_parking;
	
						if (HP_Manager.mProductionProcess)
							strMsg = "While driving, the screen is not displayed.\nIf you would like to operate, park your vehicle";
						else
							info_text = R.string.msg_parking;
	
						showDMBOnOffView(HP_Manager.mDMBVideoOnOff);
					} else {
						mDMBViewState = DMB_VIEW_NORMAL;
						showDMBOnOffView(HP_Manager.mDMBVideoOnOff);
					}
				}
				else
				{
					showDMBOnOffView(HP_Manager.mDMBVideoOnOff);
				}
			}
			else
			{
				showDMBOnOffView(HP_Manager.mDMBVideoOnOff);
			}
		}
		
		/////////////////////////////////////////////////////////////////////////////////////////
		// (Video area) - display image --> 채널 전환중
		if (imageView != null) {
			// 0-play video, !0-display image
			if (image == 0) {
				
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
				{
					if (frameAnimation != null) {
						frameAnimation.stop();
						frameAnimation = null;
					}
	
					notiBg.setVisibility(View.GONE); // 19.01.18 yhr
					imageView.setVisibility(android.view.View.GONE);
	
					if (eState != STATE.WIDGET)
						View.setBackgroundColor(0x00000000);
					else {
						if (DMB_Widget.rlWidgetBg != null) {
							DMB_Widget.rlWidgetBg.setVisibility(View.GONE);
						}
					}
	
					if(LauncherMainActivity.getInstance().mScanDialog.isShowing() == false)
					{
						if(eState == STATE.NORMAL_VIEW)
							DxbView_Normal.ResetDisplayFull();
					}
				}
				else
				{
					if (DMB_Widget.rlWidgetBg != null) {
						DMB_Widget.rlWidgetBg.setVisibility(View.VISIBLE);
					}
				}
			} else {
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
				{
					if (image == R.drawable.ch_chainge_01) {
						imageView.setImageResource(R.drawable.animation_change_channel);
						
						frameAnimation = (AnimationDrawable) imageView.getDrawable();
						frameAnimation.start();
					} else {
						if (frameAnimation != null) {
							frameAnimation.stop();
							frameAnimation = null;
						}
						imageView.setImageResource(image);
					}
					
					imageView.setVisibility(android.view.View.VISIBLE);
					notiBg.setVisibility(View.VISIBLE); // 19.01.18 yhr
					if (eState != STATE.WIDGET)
						View.setBackgroundColor(0xFF000000);
					else {
						if (DMB_Widget.rlWidgetBg != null) {
							DMB_Widget.rlWidgetBg.setVisibility(View.VISIBLE);
						}
					}
	
					if (mSurfaceHolder == null)
						return;
	
					// SurfaceView Clear
					Canvas canvas = mSurfaceView.getHolder().lockCanvas();
					if (canvas != null) {
						canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
						mSurfaceView.getHolder().unlockCanvasAndPost(canvas);
					}
				}
			}
		}
		
//		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "updateScreen // isEnable_Video : " + gInformation.cCOMM.isEnable_Video);
		if (HP_Manager.mContext.findViewById(R.id.dmb_widget_title_bar) != null) {
			if (gInformation.cCOMM.isEnable_Video)
				HP_Manager.mContext.findViewById(R.id.dmb_widget_title_bar).setVisibility(View.GONE);
			else
				HP_Manager.mContext.findViewById(R.id.dmb_widget_title_bar).setVisibility(View.VISIBLE);
		}
		
		////////////////////////////////////////////////////////////////////////////
		// 0-hide, !0-display text ==> 채널 리스트
		if (DxbPlayer.eState != DxbPlayer.STATE.SCAN) {
			if (list_text == 0)
				gComponent.normalview.channelListView.setVisibility(android.view.View.VISIBLE);
			else
				gComponent.normalview.channelListView.setVisibility(android.view.View.GONE);
		} else if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
			if (list_text != 0)
				gComponent.normalview.channelListView.setVisibility(android.view.View.GONE);
		}
		
		if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
		{
			if (textView_info != null) {
				if (info_text == 0) {
					textView_info.setVisibility(android.view.View.GONE);
					if (HP_Manager.mProductionProcess) {
						if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
							scanProgressBar.setProgress(DxbScan.mScanProgress);
							textProgress.setText(DxbScan.msgScanning);
							scan_stop.setText("Cancel");
							scan_textView.setText(strMsg);
							textSearchChannel.setText(String.valueOf(HP_Manager.mCntSearchChannel) + " Channel");
	
							if (DxbScan.mScanProgress == 100) {
								Handler _handler = new Handler();
								
								_handler.postDelayed(new Runnable() {
	
									@Override
									public void run() {
										DxbScan.mScanProgress = 0;
										DxbScan.msgScanning = DxbScan.mScanProgress + "%";
	
										scanProgressBar.setProgress(DxbScan.mScanProgress);
										textProgress.setText(DxbScan.msgScanning);
									}
								}, 500);
							}
						}
					}
				} else {
					if (DxbPlayer.eState == DxbPlayer.STATE.SCAN) {
						scanProgressBar.setProgress(DxbScan.mScanProgress);
						textProgress.setText(DxbScan.msgScanning);
						scan_textView.setText(HP_Manager.mContext.getResources().getString(info_text));
						scan_stop.setText("취소");
						textSearchChannel.setText(String.valueOf(HP_Manager.mCntSearchChannel) + " 채널");
	
						if (DxbScan.mScanProgress == 100) {
							Handler _handler = new Handler();
							_handler.postDelayed(new Runnable() {
	
								@Override
								public void run() {
									DxbScan.mScanProgress = 0;
									DxbScan.msgScanning = DxbScan.mScanProgress + "%";
	
									scanProgressBar.setProgress(DxbScan.mScanProgress);
									textProgress.setText(DxbScan.msgScanning);
	
								}
							}, 500);
						}
					}
					
					textView_info.setText(HP_Manager.mContext.getResources().getString(info_text));
					textView_info.setVisibility(android.view.View.VISIBLE);
				}
			}
		}

		if (HP_Manager.mProductionProcess) {
			if(textView_info == null)
				return;
			textView_info.setText(strMsg);
			textView_info.setVisibility(android.view.View.VISIBLE);
			DxbView_Normal.gComponent.rlProductionProcess.setVisibility(View.VISIBLE);
			DxbView_Normal.gComponent.llTitleBar.setVisibility(View.GONE);
			DxbView_Normal.gComponent.llControlPanel.setVisibility(View.GONE);
			if(DxbPlayer.eState != DxbPlayer.STATE.SCAN && HP_Manager.mCntSearchChannel > 0)
			{
				LauncherMainActivity.tvStrength.setVisibility(View.VISIBLE);
			}
		} else {
			DxbView_Normal.gComponent.rlProductionProcess.setVisibility(View.GONE);
			if (DxbPlayer.eState != DxbPlayer.STATE.SCAN)
				DxbView_Normal.gComponent.llTitleBar.setVisibility(View.VISIBLE);
		}
	}
	
	private static void showDMBOnOffView(int onoff)
	{
		if (eState != STATE.WIDGET)
			return;
		
		if(onoff == HP_Index.DMB_VIDEO_OFF)
		{
			gInformation.cCOMM.isEnable_Video = false;
			if (DMB_Widget.rlWidgetBg != null) 
				DMB_Widget.rlWidgetBg.setVisibility(View.VISIBLE);
			
			if (DMB_Widget.llWidgetBg != null)
				DMB_Widget.llWidgetBg.setVisibility(View.GONE);
			
			if (DMB_Widget.llWidgetDMBOff != null)
				DMB_Widget.llWidgetDMBOff.setVisibility(View.VISIBLE);
		}
		else
		{
			if (LauncherMainActivity.mParkingStatus == false)
			{
				gInformation.cCOMM.isEnable_Video = false;
				if (DMB_Widget.rlWidgetBg != null) 
					DMB_Widget.rlWidgetBg.setVisibility(View.VISIBLE);
			}
			else
			{
				if(mDMBViewState == DMB_VIEW_CHANNEL_CHANING)
					gInformation.cCOMM.isEnable_Video = false;
				else
					gInformation.cCOMM.isEnable_Video = true;
				
				if (DMB_Widget.rlWidgetBg != null) 
					DMB_Widget.rlWidgetBg.setVisibility(View.GONE);
			}
			
			if (DMB_Widget.llWidgetBg != null)
				DMB_Widget.llWidgetBg.setVisibility(View.VISIBLE);
			
			if (DMB_Widget.llWidgetDMBOff != null)
				DMB_Widget.llWidgetDMBOff.setVisibility(View.GONE);
		}
	}

	/**
	 * 
	 */
	static TDMBPlayer.OnPreparedListener ListenerOnPrepared = new TDMBPlayer.OnPreparedListener() {
		public void onPrepared(TDMBPlayer player, int idx, int ret) {
			// 210113 
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================ onPrepared ================ // ret :  " + ret);
			
			
			if (idx != player.mModuleIndex)
				return;
			
			
			if (ret != 0) {
				DxbPlayer.mPlayer = null;
				DxbPlayer.setListener_Player();
				DxbPlayer.setSurface();
				return;
			}
			if (mIsPrepared == true)
				return;

			mIsPrepared = true;
			
			if(gInformation.cCOMM.iCount_Current <= 0)
			{
				if(HP_Manager.mProductionProcess)
				{
					DxbPlayer.mProductionProcessChannelList.clear();
//					DxbPlayer.mChannelManager.deleteAllProductionProcessChannel();
					DxbScan.startProductionProcessManual();
					return;
				}
			}
		
			if ((gInformation.cCOMM.iCurrent_TV >= gInformation.cCOMM.iCount_TV)
					|| (gInformation.cCOMM.iCurrent_TV < 0))
				gInformation.cCOMM.iCurrent_TV = 0;

			if (gInformation.cCOMM.iCurrent_TV >= 0) {
				if (gInformation.cCOMM.curChannels != null) {
					gInformation.cCOMM.curChannels.moveToPosition(gInformation.cCOMM.iCurrent_TV);
				}

				setChannel();
			} else if (gInformation.cLIST.iCount_Tab > 1) {
				DxbView_Normal.updateChannelList(1);

				if ((gInformation.cCOMM.iCurrent_Radio >= gInformation.cCOMM.iCount_Radio)
						|| (gInformation.cCOMM.iCurrent_Radio < 0))
					gInformation.cCOMM.iCurrent_Radio = 0;

				if (gInformation.cCOMM.iCurrent_Radio >= 0) {
					if (gInformation.cCOMM.curChannels != null) {
						Cursor c = gInformation.cCOMM.curChannels;
						c.moveToPosition(gInformation.cCOMM.iCurrent_Radio);
					}
					setChannel();
				}
			}
		}
	};

	static TDMBPlayer.OnVideoOutputListener ListenerOnVideoOutput = new TDMBPlayer.OnVideoOutputListener() {
		public void onVideoOutputUpdate(TDMBPlayer player) {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================ onVideoOutputUpdate ================ // mAudioOnOff : " + DxbPlayer.mAudioOnOff
					+ ", HP_Manager.mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus);

			DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_VIDEO_OUT_TIMEOUT);
			mVideoOutUpdate();
		}
	};

	public static void mVideoOutUpdate()
	{
		mHandler_Main.postDelayed(mViewRunnable, 500);
		mDMBViewState = DMB_VIEW_NORMAL;
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "mVideoOutUpdate  // mfromUserMute : " + DxbView_Normal.mfromUserMute
				  + ", HP_Manager.mDMBVideoOnOff : " + HP_Manager.mDMBVideoOnOff);
		
		// 200227
		if(HP_Manager.mProductionProcess)
		{
			DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
		}
		else
		{
			//200302
			if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
			{
				if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
				{
					HP_Manager.mDMBSoundStart = false;
					startAudioOut();
				}
			}
			else
				DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
		}
	}
	
	static TDMBPlayer.OnAudioOutputListener ListenerOnAudioOutput = new TDMBPlayer.OnAudioOutputListener() {
		public void onAudioOutputUpdate(TDMBPlayer player) {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================ onAudioOutputUpdate ================  mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus);
		
			if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
				startAudioOut();
			else
				closeAudioOut();
		}
		
	};

	/**
	 * Change Channel
	 * 
	 * @param state
	 */
	static void changeChannel(int state) {
		switch (state) {
		case HP_Index.CHANNEL_PREV:
			if (gInformation.cCOMM.iCount_Current > 0) {
				if (gInformation.cCOMM.iCurrent_TV == 0) {
					gInformation.cCOMM.iCurrent_TV = gInformation.cCOMM.iCount_Current;
					gInformation.cCOMM.iCurrent_TV--;
					DxbView_Normal.mPrePosition--;
				} 
				else if (gInformation.cCOMM.iCurrent_TV < 0)
				{
					gInformation.cCOMM.iCurrent_TV = 0;
					DxbView_Normal.mPrePosition = 0;
				}
				else
				{
					gInformation.cCOMM.iCurrent_TV--;
					DxbView_Normal.mPrePosition--;
				}
				
				if (gInformation.cCOMM.curChannels != null) {
					gInformation.cCOMM.curChannels.moveToPosition(gInformation.cCOMM.iCurrent_TV);
				}
			}
			break;
		case HP_Index.CHANNEL_NEXT:
			if (gInformation.cCOMM.iCount_Current > 0) {
				if (gInformation.cCOMM.iCurrent_TV == gInformation.cCOMM.iCount_TV - 1)
				{
					gInformation.cCOMM.iCurrent_TV = 0;
					DxbView_Normal.mPrePosition = 0;
				}
				else if (gInformation.cCOMM.iCurrent_TV >= gInformation.cCOMM.iCount_TV)
				{
					gInformation.cCOMM.iCurrent_TV = gInformation.cCOMM.iCount_TV - 1;
					DxbView_Normal.mPrePosition = DxbView_Normal.mPrePosition -1;
				}
				else
				{
					gInformation.cCOMM.iCurrent_TV++;
					DxbView_Normal.mPrePosition++;
				}

				if (gInformation.cCOMM.curChannels != null) {
					gInformation.cCOMM.curChannels.moveToPosition(gInformation.cCOMM.iCurrent_TV);
				}
			}
			break;
		default:
			return;
		}
		setChannel();
	}

	/**
	 * Set Channel
	 */
	static void setChannel() {
		DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_VIDEO_OUT_TIMEOUT);
		DxbView_Normal.ClearDisplayFull();

		if ((DxbPlayer.eState == DxbPlayer.STATE.SCAN) || (DxbPlayer.eState == DxbPlayer.STATE.SCAN_STOP)
				|| (LauncherMainActivity.eCycle_Life == DXB_LIFE_CYCLE.ON_DESTROY)) {
			return;
		}
		
		// 190415 yhr
		if (DxbView.eState == DxbView.STATE.NORMAL_VIEW) {
			
			if (DxbView_Normal.gComponent.mWeakSignalPopup.getVisibility() == View.VISIBLE)
				DxbView_Normal.gComponent.mWeakSignalPopup.setVisibility(View.GONE);
		} else if (DxbView.eState == DxbView.STATE.FULL) {
			if (DxbView_Full.gComponent.mFullWeakSignalPopup.getVisibility() == View.VISIBLE)
				DxbView_Full.gComponent.mFullWeakSignalPopup.setVisibility(View.GONE);
		} else {
			if (DMB_Widget.mWeakSignalPopup != null) {
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
				{
					if (DMB_Widget.mWeakSignalPopup.getVisibility() == View.VISIBLE)
						DMB_Widget.mWeakSignalPopup.setVisibility(View.GONE);
				}
			}
		}

		if ((gInformation.cCOMM.iCount_Current > 0) && (gInformation.cCOMM.curChannels != null)) {
			gInformation.cCOMM.iCurrent_TV = gInformation.cCOMM.curChannels.getPosition();
			DxbView_Normal.mPrePosition = gInformation.cCOMM.iCurrent_TV;
			mDB.putPosition(0, gInformation.cCOMM.iCurrent_TV);

			DxbPlayer.setChannel(gInformation.cCOMM.curChannels);
			gComponent.normalview.channelListView.post(new Runnable() {

				@Override
				public void run() {
					ensureVisible(gComponent.normalview.channelListView, gInformation.cCOMM.iCurrent_TV);
				}
			});

			updateScreen();
			gComponent.normalview.channelListView.invalidateViews();
		} else {
			DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
		}
	}

	/**
	 * Channel List Update
	 */
	public static void updateList() {
		int _pos = gInformation.cCOMM.iCurrent_TV - HALF_COUNT_LIST;
		gComponent.normalview.channelListView.setSelection(_pos);
	}

	/**
	 * Close Video
	 */
	public static void closeVideoOut() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + " closeVideoOut ");
		mHandler_Main.removeCallbacks(mViewRunnable);
	}

	public static Runnable mViewRunnable = new Runnable() {

		@Override
		public void run() {
			DxbPlayer.mVideoOutput = false;
			mProgressState = MPROGRESS_UNSHOW;

			mIsShowScreen = true;
			gInformation.cCOMM.isEnable_Video = true;
			DxbView.updateScreen();
		}
	};

	/**
	 * List 스크롤 이동
	 * 
	 * @param listView
	 * @param pos
	 */
	private static void ensureVisible(ListView listView, int pos) {
		if (listView == null)
			return;

		if (pos < 0 || pos >= listView.getCount())
			return;

		int first = listView.getFirstVisiblePosition();
		int last = listView.getLastVisiblePosition();

		if (pos <= first || pos > last) {
			int _pos = pos - HALF_COUNT_LIST;
			if (_pos < 0)
				_pos = 0;

			listView.setSelection(_pos);
		}
	}

	public static void startAudioOut() {
		if (DxbPlayer.mPlayer != null) {
			if (HP_Manager.mIsNaviGuidance) {
				if (HP_Manager.mNaviSoundStart)
					return;
			}
			
			if(HP_Manager.mCurrentDMBVol == 0)
				return;
			
			if(HP_Manager.mDMBSoundStart)
				return;

			if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
			{
				if(HP_Manager.mLastMode != HP_Index.LAST_MODE_DMB_FULL)
					DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
				return;
			}
			
			Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "yhr // Start Audio (Mute OFF!!!) mNaviSoundStart : " + HP_Manager.mNaviSoundStart
											+ ", mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus
											+ ", vol : " + LauncherMainActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
											+ ", HP_Manager.mDMBSoundStar : " + HP_Manager.mDMBSoundStart);

			HP_Manager.mDMBMuteStatus = HP_Index.DMB_SOUND_UNMUTE;
			
			HP_Manager.mDMBSoundStart = true;
			DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
		}
		
		// 190510
//		if(HP_Manager.mProductionProcess && LauncherMainActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 22)
//		{
//			LauncherMainActivity.mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 22, 0);
//			Log.e(HP_Manager.TAG_POPUP, CLASS_NAME + "yhr // Start Audio (Mute OFF!!!) setStreamVolume 3 : " + LauncherMainActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)); 
//		}
	}

	public static void closeAudioOut() {
//		if(HP_Manager.mProductionProcess)
//			return;
		
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + " @@ Close Audio (Mute ON!!!) // " + DxbPlayer.mAudioOnOff + ", HP_Manager.mDMBMuteStatus: " +HP_Manager.mDMBMuteStatus);
		HP_Manager.mDMBSoundStart = false;
		DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
	}
}
