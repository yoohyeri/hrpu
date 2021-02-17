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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.method.DialerKeyListener;
import android.util.Log;

import com.mdstec.android.tpeg.TpegService;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.DxbAdapter_Service.ViewHolder;
import com.telechips.android.tdmb.player.Channel;
import com.telechips.android.tdmb.player.DABDLSData;
import com.telechips.android.tdmb.player.EWSData;
import com.telechips.android.tdmb.player.TDMBPlayer;

public class DxbPlayer {
	static String CLASS_NAME = "[DxbPlayer ]  ";

	public static Information gInformation;

	// Test
//	public static final int PRODUCTION_PROGRESS_SET_LOW_CHANNEL = 181280; 
//	public static final int PRODUCTION_PROGRESS_SET_MID_CHANNEL = 183008; 
//	public static final int PRODUCTION_PROGRESS_SET_HIG_CHANNEL = 184736; 
//	
	// 공정프로그램 채널
	public static final int PRODUCTION_PROGRESS_SET_LOW_CHANNEL = 177008; // 7B채널
	public static final int PRODUCTION_PROGRESS_SET_MID_CHANNEL = 195008; // 10B채널
	public static final int PRODUCTION_PROGRESS_SET_HIG_CHANNEL = 207008; // 12B채널

	/* check playr OFF state */
	static boolean isOFF = false;

	/* Dxb_Player State */
	public enum STATE {
		GENERAL, OPTION_MANUAL_SCAN, OPTION_MAKE_PRESET, SCAN, SCAN_STOP,
		// SCAN_STOP_DESTROY,
		DMB_OFF, DMB_ON,
		SCAN_FAIL, UI_PAUSE, NULL
	}

	public static STATE eState = STATE.NULL;

	public static DxbDB_EWS mEwsManager = null;
	public static ChannelManager mChannelManager = null;
	public static Channel mChannel;

	public static final int _OFF_ = 0;
	public static final int _ON_ = 1;

	/* Visible Check */
	public static boolean VISIBLE_LIST_preview_half = true;

	/* TDMB Only */
	public static TDMBPlayer mPlayer = null;
	public static TDMBPlayer mDATASVC = null;

	private static final int KOREA_BAND = 0x01;
	private static int[] freqList = { 181280, 183008, 184736, 205280, 207008, 208736, 211280 };
	
	private static int[] production_progress_freqList = { 177008, 195008, 207008};
	public static int iCountryCode = KOREA_BAND; // 886; //Tiwan

	private static final int TYPE_DAB = 1;
	private static final int TYPE_TDMB = 2;

	private static EWSParser mEWSParser;

	/* TDMB Only --> */
	private static final String DBPATH = "/data/data/com.telechips.android.tdmb/databases/TDMB.db";
	private static final int basebandType = 4; // 4:TCC3171 I2C+STS, 5:TCC3171
												// I2C+SPIMS

	// TDMB Channel List
	public static ArrayList<Channel> mTDMBChannelList = new ArrayList<Channel>();
	public static ArrayList<Channel> mNormalChannelList = new ArrayList<Channel>();
	public static ArrayList<Channel> mHDChannelList = new ArrayList<Channel>();
	public static ArrayList<Channel> mProductionProcessChannelList = new ArrayList<Channel>();
	public static ArrayList<Channel> mBackupChannelList = new ArrayList<Channel>();

	// kslee
	// TPEG Channel
	public static Channel mTpegChannel = null;
	public static Channel mTpegChannelBackup = null;
	public static boolean mTpegStart;

	public static boolean mTunninging;
	public static boolean mVideoOutput;
	public static boolean mStart;
	
	// yhr 생산공정
	public static Channel mProductionProcessChannel;
	
	/**
	 * Initialize
	 */
	static void init() {
		if (mTDMBChannelList == null)
			mTDMBChannelList = new ArrayList<Channel>();

		if (mNormalChannelList == null)
			mNormalChannelList = new ArrayList<Channel>();

		if (mHDChannelList == null)
			mHDChannelList = new ArrayList<Channel>();

		if (mProductionProcessChannelList == null)
			mProductionProcessChannelList = new ArrayList<Channel>();

		gInformation = DMB_Manager.g_Information;

		// Channel Manager 생성 및 open --> DB
		if (mChannelManager == null) {
			mChannelManager = new ChannelManager(DMB_Manager.mContext);
			mChannelManager.open();
		}

		if (mEwsManager == null) {
			mEwsManager = new DxbDB_EWS(DMB_Manager.mContext);
			mEwsManager.open();
		}

		if (DxbView.mDB != null) {
			DxbView.mDB.close();
			DxbView.mDB = null;
		}

		DxbView.mDB = new DxbDB_Setting(DMB_Manager.mContext).open();
		if (DxbView.mDB != null) {
			DMB_Manager.g_Information.cCOMM.iCurrent_TV = DxbView.mDB.getPosition(0);
			DxbView_Normal.mPrePosition = DxbView.mDB.getPosition(0);
			DMB_Manager.g_Information.cCOMM.iCurrent_Radio = DxbView.mDB.getPosition(1);
		} else {
			Log.d(HP_Manager.TAG_DMB, "not open DB!!!");
		}
	}

	/**
	 * Manual Search channels
	 * 
	 * @param Hz_frequency
	 */
	public static void manualScan(int Hz_frequency) {
		// 190513
		DxbPlayer.eState = DxbPlayer.STATE.SCAN;
		mPlayer.manual_search(Hz_frequency);
	}

	public static int getScanCount() {
		Cursor cursorChannel;
		int iCountTV;
		
		cursorChannel = DxbPlayer.getChannels(0);
		iCountTV = cursorChannel.getCount();
		cursorChannel.close();

		return iCountTV;
	}

	public static void setScreenMode(int screenMode) {
		Rect crop_rect = DxbView.FullView_SetLayout();
	}

	/**
	 * Search channels
	 */
	public static void search() {
		if (mPlayer != null) {
			
			//190717 yhr
			DxbView.closeAudioOut();
			
			mTunninging = false;
			mVideoOutput = false;

			if (DxbView.mIsPrepared) {
				mPlayer.search(KOREA_BAND, freqList, 0);
			} else {
				DxbView.mHandler_Main.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (DxbView.mIsPrepared) {
							mPlayer.search(KOREA_BAND, freqList, 0);
						} else {
							DxbView.mHandler_Main.postDelayed(this, 100);
						}
					}
				}, 100);
			}
		}
	}
	
	/**
	 * Search channels (production process)
	 */
	public static void mProductionProcessSearch() {
		if (mPlayer != null) {
			mTunninging = false;
			mVideoOutput = false;

			if (DxbView.mIsPrepared) {
				mPlayer.search(KOREA_BAND, production_progress_freqList, 0);
			} else {
				DxbView.mHandler_Main.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (DxbView.mIsPrepared) {
							mPlayer.search(KOREA_BAND, production_progress_freqList, 0);
						} else {
							DxbView.mHandler_Main.postDelayed(this, 100);
						}
					}
				}, 100);
			}
		}
	}

	/**
	 * Player - Set Listener
	 */
	public static void setListener_Player() {
		
		/* TDMB Only --> */
		// if(mPlayer == null)
		{
			mPlayer = new TDMBPlayer();
			DxbView.mIsPrepared = false;
			mTunninging = false;
			mVideoOutput = false;
			mStart = false;

			mPlayer.setOnPreparedListener(DxbView.ListenerOnPrepared);
			mPlayer.setOnAudioOutputListener(DxbView.ListenerOnAudioOutput);
			mPlayer.setOnVideoOutputListener(DxbView.ListenerOnVideoOutput);
			// mPlayer.setOnErrorListener(DxbView.ListenerOnError);

			// Scan
			mPlayer.setOnSearchPercentListener(DxbScan.ListenerOnSearchPercent);
			mPlayer.setOnSearchCompletionListener(DxbScan.ListenerOnSearchCompletion);
			mPlayer.setOnSignalStatusListener(DxbView_Indicator.ListenerOnSignalStatus);
			 mPlayer.setOnCASListener(DxbView_CAS_debug.ListenerOnCAS);

			setListener_TDMB();
			mPlayer.setDisplay(DxbView.mSurfaceHolder);
			setPrepare();
		}

		// kslee
		if (mDATASVC == null) {
			mTpegStart = false;
			mDATASVC = new TDMBPlayer();
			mIsDATASVCPrepared = false;
			setDATAPrepare();
		}
		
		/* TDMB Only --> */
		if (mChannel == null) {
			mChannel = new Channel();
		}
	}

	public static void setPrepare() {
		if (mPlayer != null) {
			int bbtype = basebandType; // 4:TCC3171 I2C+STS, 5:TCC3171 I2C+SPIMS
			int moduleidx = 0;
			mPlayer.mModuleIndex = moduleidx;
			mPlayer.setBBModuleIndex(bbtype, moduleidx);
			mPlayer.prepare(bbtype, DBPATH); // 1,2,3:not support 4:TCC3171
												// I2C+STS 5:TCC3171 I2C+SPIMS
		}
	}

	// kslee
	static boolean mIsDATASVCPrepared = false;

	static void setDATAPrepare() {
		if (mDATASVC != null) {
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					int bbtype = basebandType;
					int moduleidx = 1;
					mDATASVC.mModuleIndex = moduleidx;
					mDATASVC.setOnPreparedListener(ListenerOnPrepared);
					mDATASVC.setBBModuleIndex(bbtype, moduleidx);
					mDATASVC.prepare(bbtype, null); // 1:TCC3150 2:TCC3510
													// CSPI+STS 3:TCC3510 or
													// TCC3161 I2C+STS

					new Handler().postDelayed(new Runnable() {

						@Override
						public void run() {
							DxbScan.mTpegStart();
						}
					}, 5000);
				}
			}, 5000);
		}
	}

	// kslee
	static TDMBPlayer.OnPreparedListener ListenerOnPrepared = new TDMBPlayer.OnPreparedListener() {
		public void onPrepared(TDMBPlayer player, int idx, int ret) {
			if (idx != player.mModuleIndex)
				return;

			// mSetTpegChannel();
		}
	};

	static void mSetProductProcessChannel() {
		DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
		if(mProductionProcessChannel != null && mPlayer != null)
			manual_setChannel(mPlayer, mProductionProcessChannel);
		
		DxbView.updateScreen();
	}
	
	
	static void mSetTpegChannel() {
		if (mIsDATASVCPrepared == true)
			return;

		mIsDATASVCPrepared = true;

		if (mDATASVC != null && mIsDATASVCPrepared == true && mTpegChannel != null) {
			manual_setChannel(mDATASVC, mTpegChannel);
		} else {
			mIsDATASVCPrepared = false;
		}
	}
	
	/**
	 * Video Out - Thread
	 */
	private static Runnable mRunnable_VideoOutput = new Runnable() {
		@Override
		public void run() {
			if (mVideoOutput) {
				_setChannel();
			}
		}
	};

	/************************************************************************************************************************
	 * setListener -----> Start
	 ************************************************************************************************************************/
	private static void setListener_TDMB() {
		// TDMB Only
		if (mEWSParser == null) {
			mEWSParser = new EWSParser();
		}

		mPlayer.setOnChannelUpdateListener(ListenerOnChannelUpdate);
		mPlayer.setOnDABDLSDataUpdateListener(ListenerOnDABDLSDataUpdate);
//		mPlayer.setOnEWSDataUpdateListener(ListenerOnEWSDataUpdate);
		mPlayer.setOnTunningCompletionListener(ListenerTunningCompletion);
	}

	static TDMBPlayer.OnChannelUpdateListener ListenerOnChannelUpdate = new TDMBPlayer.OnChannelUpdateListener() {
		public void onChannelUpdate(TDMBPlayer player, int moduleidx, Channel channel) {
//			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "1 onChannelUpdate - " + channel.type + ", " + channel.serviceName + ", mCntSeatchChanne : "
//							+ HP_Manager.mCntSearchChannel + ", HP_Manager.mProductionProcess : "
//							+ HP_Manager.mProductionProcess + ", ensembleFreq : " + channel.ensembleFreq);
			
			if (channel.type == TYPE_DAB || channel.type == TYPE_TDMB) 
			{
				// 190513
				if(HP_Manager.mProductionProcess)
				{
					if(	channel.ensembleFreq == DxbPlayer.PRODUCTION_PROGRESS_SET_LOW_CHANNEL ||
						channel.ensembleFreq == DxbPlayer.PRODUCTION_PROGRESS_SET_MID_CHANNEL ||
						channel.ensembleFreq == DxbPlayer.PRODUCTION_PROGRESS_SET_HIG_CHANNEL)
					{
//						Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "2 onChannelUpdate - type : " + channel.type 
//														+ ", serviceName : " + channel.serviceName 
//														+ ", mCntSearchChannel : " + HP_Manager.mCntSearchChannel 
//														+ ", ensembleFreq : " + channel.ensembleFreq);
						HP_Manager.mCntSearchChannel++;
						if (mProductionProcessChannel == null)
							mProductionProcessChannel = new Channel();

						mProductionProcessChannel.ensembleName = channel.ensembleName;
						mProductionProcessChannel.ensembleID = channel.ensembleID;
						mProductionProcessChannel.ensembleFreq = channel.ensembleFreq;
						mProductionProcessChannel.serviceName = channel.serviceName;
						mProductionProcessChannel.serviceID = channel.serviceID;
						mProductionProcessChannel.channelName = channel.channelName;
						mProductionProcessChannel.channelID = channel.channelID;
						mProductionProcessChannel.type = channel.type;
						mProductionProcessChannel.bitrate = channel.bitrate;
						mProductionProcessChannel.chIdx = channel.chIdx;

						for (int regIndex = 0; regIndex < 7; regIndex++) {
							mProductionProcessChannel.reg[regIndex] = channel.reg[regIndex];
						}
					}
				}
				else
				{
					HP_Manager.mCntSearchChannel++;
				
					if (channel.serviceName.contains("HD")) {
						mHDChannelList.add(channel);
					} else {
						mNormalChannelList.add(channel);
					}
				}
			}
			
			// kslee
			if (HP_Manager.mProductionProcess == false && channel.serviceName.equals("KBS TPEG")) {

				if (mTpegChannel == null)
					mTpegChannel = new Channel();

				mTpegChannel.ensembleName = channel.ensembleName;
				mTpegChannel.ensembleID = channel.ensembleID;
				mTpegChannel.ensembleFreq = channel.ensembleFreq;
				mTpegChannel.serviceName = channel.serviceName;
				mTpegChannel.serviceID = channel.serviceID;
				mTpegChannel.channelName = channel.channelName;
				mTpegChannel.channelID = channel.channelID;
				mTpegChannel.type = channel.type;
				mTpegChannel.bitrate = channel.bitrate;
				mTpegChannel.chIdx = channel.chIdx;

				for (int regIndex = 0; regIndex < 7; regIndex++) {
					mTpegChannel.reg[regIndex] = channel.reg[regIndex];
				}
			}
		}
	};

	static TDMBPlayer.OnDABDLSDataUpdateListener ListenerOnDABDLSDataUpdate = new TDMBPlayer.OnDABDLSDataUpdateListener() {
		public void onDABDLSDataUpdate(TDMBPlayer player, DABDLSData dabdlsData) {
			if (dabdlsData.dlsDataType != 4) {
				try {
					if (dabdlsData.dlsDataSize > 0) {
						dabdlsData.strDLSData = new String(dabdlsData.dlsData, 0, dabdlsData.dlsDataSize, "KSC5601");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
	};

	static TDMBPlayer.OnTunningCompletionListener ListenerTunningCompletion = new TDMBPlayer.OnTunningCompletionListener() {
		public void onTunningCompletion(TDMBPlayer player, int idx, int bStatus) {
			if (idx != player.mModuleIndex)
				return;

			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================ TunningCompletion  ================ // mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus);

			// 190401 yhr >> 채널전환 완료되면 mRunnable_Signal 동작 시작
			DxbPlayer.start();
			if(HP_Manager.mProductionProcess)
				DxbView_Normal.gComponent.btnProdeuctionSCAN.setEnabled(true);

			mTunninging = false;
			useSurface(0);
			mHandler.sendEmptyMessageDelayed(MSG_VIDEO_OUT_TIMEOUT, 15 * HP_Index.TIME_1_SECOND);
		}
	};
	/************************************************************************************************************************
	 * setListener -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * Channel -----> Start
	 ************************************************************************************************************************/
	public static void setChannel() {
		DxbView.mHandler_Main.removeCallbacks(mRunnable_VideoOutput);
		DxbView.mHandler_Main.removeCallbacks(mRunnable_setChannel);
		
		DxbView.closeVideoOut();

		DxbView.mProgressState = DxbView.MPROGRESS_SHOW;

		if (DxbView.mIsPrepared && mTunninging == false)
			_setChannel();
		else
			DxbView.mHandler_Main.postDelayed(mRunnable_setChannel, HP_Index.TIME_1_SECOND);
	}

	private static void _setChannel() {
		DxbView.mIsShowScreen = false;
		gInformation.cCOMM.isEnable_Video = false;
		manual_setChannel(mPlayer, mChannel);
	}

	private static TDMBPlayer mTDMBPlayer, mTpegPlayer;
	private static int[] mChInfo;
	private static int[] mChInfoTPeg;
	private static boolean mTpegBind = false;
	public static void manual_setChannel(TDMBPlayer player, Channel channel) {
		if ((player != null) && (channel != null)) {
			mTunninging = true;

			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "[Channel Info] Module Index : " + player.mModuleIndex + ", ensembleFreq : " + channel.ensembleFreq
							+ ", ensembleID : " + channel.ensembleID + ", serviceID : " + channel.serviceID
							+ ", channelID : " + channel.channelID + ", type : " + channel.type + ", bitrate :"
							+ channel.bitrate);

			int[] chInfo = { player.mModuleIndex, channel.ensembleFreq, channel.ensembleID, channel.serviceID,
					channel.channelID, channel.type, channel.bitrate, channel.reg[0], channel.reg[1], channel.reg[2],
					channel.reg[3], channel.reg[4], channel.reg[5], channel.reg[6] };

			if (channel.type == 3) { // TPEG
				if(mTpegBind == false) {
					mTpegBind = true;
					TpegService.startService(LauncherMainActivity.getInstance().getApplicationContext());
					TpegService.bindService(LauncherMainActivity.getInstance().getApplicationContext());
				}
				mTpegPlayer = player;
				mChInfoTPeg = chInfo;
				mHandler.removeMessages(MSG_SET_TPEG);
				mHandler.sendEmptyMessageDelayed(MSG_SET_TPEG, 6000);
				
			} else {
				mHandler.removeMessages(MSG_SET_CHANNEL);
				DxbView_Normal.mChoiceChannelName = channel.serviceName;

				// 190401 yhr
				// DxbPlayer.stop();

				// 190325 yhr > 연속으로 채널 변경 이벤트가 발생될 경우에 대한 예외처리 추가
				mTDMBPlayer = player;
				mChInfo = chInfo;
				mHandler.sendEmptyMessageDelayed(MSG_SET_CHANNEL, 2000);
			}
		} else {
			mTunninging = false;
		}
	}

	public final static int MSG_SET_CHANNEL = 0;
	public final static int MSG_SET_TPEG = 1;
	public final static int MSG_VIDEO_OUT_TIMEOUT = 2;
	public static Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SET_CHANNEL:
				removeMessages(MSG_SET_CHANNEL);
				removeMessages(MSG_VIDEO_OUT_TIMEOUT);
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + ">>> MSG_SET_CHANNEL // mChoiceChannelName : " + DxbView_Normal.mChoiceChannelName);
				mTDMBPlayer.manual_setChannel(mChInfo);
				
				break;
			case MSG_SET_TPEG:
				removeMessages(MSG_SET_TPEG);
				
				if(mChInfoTPeg != null && mTpegPlayer != null)
					mTpegPlayer.manual_setChannel(mChInfoTPeg);
				break;
			case MSG_VIDEO_OUT_TIMEOUT:
				removeMessages(MSG_VIDEO_OUT_TIMEOUT);
				Log.d(HP_Manager.TAG_DMB, CLASS_NAME + ">>> MSG_VIDEO_OUT_TIMEOUT");
				DxbView.mVideoOutUpdate();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * Thread
	 */
	private static Runnable mRunnable_setChannel = new Runnable() {
		@Override
		public void run() {
			if (DxbView.mIsPrepared) {
				_setChannel();
			} else if (mTunninging) {
				_setChannel();
			} else {
				if ((mChannel != null) && (mPlayer != null)) {
					DxbView.mHandler_Main.postDelayed(this, HP_Index.TIME_1_SECOND);
				}
			}
		}
	};

	/************************************************************************************************************************
	 * Channel -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * device access -----> Start
	 ************************************************************************************************************************/
	public static void start() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================= start() ================= // mDMBMuteStatus : " + HP_Manager.mDMBMuteStatus
										+ ",mStart : " + mStart 
										+ ",getStreamVolume : " + LauncherMainActivity.mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
		if (mStart == false) {
			if (mPlayer != null) {
				mPlayer.start(iCountryCode);
				mStart = true;
				DxbView_Indicator.Signal_visible();
					
				//200228 yhr
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
				{
					if (HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
						DxbView.startAudioOut();
					else
						DxbView.closeAudioOut();
				}
				else
				{
					DxbPlayer.setAudioOnOff(DxbPlayer._OFF_);
				}
			}
		}

		if (mDATASVC != null && mTpegStart == false) {
			mTpegStart = true;
			mDATASVC.start(iCountryCode);
		}
	}

	// 190401 yhr
	public static void stop() {
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "================= stop() ================= // mStart : " + mStart);
		if (mStart == true) {
			if (mPlayer != null) {
				mPlayer.stop();
				mStart = false;
				DxbView_Indicator.Signal_invisible();
				DxbView.closeAudioOut();
			}
		}
	}

	/**
	 * disableSurface = true -> DMB 영상  출력 X
	 * disableSurface = false -> DMB 영상 출력
	 */
	public static void setSurface() {		
		if (mPlayer != null) {
			try {
				mPlayer.setSurface();
				mPlayer.setDisplayEnable();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	public static void releaseSurface() {
		if (mPlayer != null) {
			
			mPlayer.setDisplayDisable();
			mPlayer.releaseSurface();
		}
	}

	public static void useSurface(int arg) {
		if (mPlayer != null) {
			mPlayer.useSurface(arg);
		}
	}

	public static void setLCDUpdate() {
		if (mPlayer != null) {
			mPlayer.setLCDUpdate();
		}
	}

	public static void setRecord(String name) {
		if (mPlayer != null)
			mPlayer.setRecord(name);
	}

	public static void setRecStop() {
		if (mPlayer != null)
			mPlayer.setRecStop();
	}

	public static void setDMBServiceMode(int mode) {
		if (mPlayer != null)
			mPlayer.setDMBServiceMode(mode);
	}

	public static void cmdCAS(int index) {
		if (mPlayer != null)
			mPlayer.cmdCAS(index);
	}

	public static void cmdCASRestoration() {
		if (mPlayer != null)
			mPlayer.cmdCASRestoration(iCountryCode, mChannel.chIdx, mChannel.type);
	}

	public static void setChannel(Cursor mChannelsCursor) {
		DxbView.mHandler_Main.removeCallbacks(mRunnable_setChannel);
		mHandler.removeMessages(MSG_SET_CHANNEL);

		// min 20190409
		DxbPlayer.stop();

		if ((mChannelsCursor != null) && (mChannel != null) && (mPlayer != null)) {

			if (!HP_Manager.mProductionProcess) {
				try {
					DxbView.closeAudioOut();
					DxbView.closeVideoOut();
					DxbView.mProgressState = DxbView.MPROGRESS_SHOW;

					Cursor c = mChannelsCursor;
					mChannel.ensembleName = c.getString(1);
					mChannel.ensembleID = c.getInt(2);
					mChannel.ensembleFreq = c.getInt(3);
					mChannel.serviceName = c.getString(4);
					mChannel.serviceID = c.getInt(5);
					mChannel.channelName = c.getString(6);
					mChannel.channelID = c.getInt(7);
					mChannel.type = c.getInt(8);
					mChannel.bitrate = c.getInt(9);
					mChannel.chIdx = c.getInt(0);

					for (int regIndex = 0; regIndex < 7; regIndex++) {
						mChannel.reg[regIndex] = c.getInt(regIndex + 10);
					}

					if (DxbView.mIsPrepared && mTunninging == false) {
						_setChannel();
					} else {
						DxbView.mHandler_Main.postDelayed(mRunnable_setChannel, 1000);
					}
				} catch (Exception e) {
				}
			} 
		}
	}

	/************************************************************************************************************************
	 * device access -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * Signal -----> Start
	 ************************************************************************************************************************/
	public static int getSignalStrength() {		
		
		// test 0106
		mPlayer = null;
		int[] dSignal = mPlayer.getSignalStrength();
		
		if (dSignal[0] > 4)
			dSignal[0] = 4;

		return dSignal[0];
		
		
		// 1211 yhr 
//		if(mPlayer == null)
//		{
//			Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "mPlayer is null...!");
//			return 0;
//		}
//		else
//		{
//			int[] dSignal = mPlayer.getSignalStrength();
//	
//			if (dSignal[0] > 4)
//				dSignal[0] = 4;
//	
//			return dSignal[0];
//		}
	}

	public static int[] getSignalStrengthAll() {
		if (mPlayer == null)
			return null;
		
		return mPlayer.getSignalStrength();
	}

	/************************************************************************************************************************
	 * Signal -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * Service -----> Start
	 ************************************************************************************************************************/
	private static int iIndex_serviceType, iIndex_serviceName, iIndex_remoconID, iIndex_pmtPID;

	public static void setService_ColumnIndex(Cursor cursor) {
		if (cursor != null) {
			iIndex_serviceName = cursor.getColumnIndexOrThrow(ChannelManager.KEY_SERVICE_NAME);
		}
	}

	public static void bindView(Cursor cursor, ViewHolder vh) {
		int ensembleID = cursor.getInt(2);
		int serviceID = cursor.getInt(5);
		String serviceName = cursor.getString(iIndex_serviceName);

		int position = cursor.getPosition();
		String index = String.valueOf((position + 1));

		if ((gInformation.cCOMM.iCurrent_TV != position))
		{
			vh.name.setTextColor(Color.rgb(255, 255, 255)); // white
			vh.index.setTextColor(Color.rgb(255, 255, 255));
			vh.rlListItem.setHovered(false);
			vh.index.setText(String.valueOf(index));
		} else {
			DxbView_Normal.mChoiceEnsembleID = ensembleID;
			DxbView_Normal.mChoiceChannelID = serviceID;
			DxbView_Normal.mChoiceChannelName = serviceName;

			vh.rlListItem.setHovered(true);
			vh.name.setTextColor(Color.BLACK);
			vh.index.setTextColor(Color.BLACK);
			vh.index.setText(String.valueOf(index));
		}

		vh.name.setText(serviceName);
	}
	/************************************************************************************************************************
	 * Service -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * DB control -----> Start
	 ************************************************************************************************************************/
	/* Service */
	public static String getServiceName() {
		if (mChannel != null && mChannel.serviceName != null)
			return mChannel.serviceName;
		
		return null;
	}

	public static Cursor getChannels(int iTab) {
		return mChannelManager.getAllChannels2();
	}
	
	public static String getCurrent_ServiceName() {
		return ChannelManager.KEY_SERVICE_NAME;
	}

	public static String getCurrent_ServiceType() {
		return ChannelManager.KEY_TYPE;
	}
	/************************************************************************************************************************
	 * DB control -----> End
	 ************************************************************************************************************************/

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/************************************************************************************************************************
	 * (TDMB) Setting Audio,Video On/Off. Set SyncMode
	 ************************************************************************************************************************/

	public static int mAudioOnOff = DMB_Manager.DEFAULT_VALUE_AUDIO_ONOFF;
	public static void setAudioOnOff(int OnOff) {
		if (mPlayer != null) {
			mAudioOnOff = OnOff;
			mPlayer.setAudioOnOff(OnOff);
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "yhr  setAudioOnOff(" + OnOff + ")");
		}
	}

	public static void setVideoOnOff(int OnOff) {
		if (mPlayer != null) {
			Log.d(HP_Manager.TAG_DMB, CLASS_NAME +  "setVideoOnOff(" + OnOff + ")");
			mPlayer.setVideoOnOff(OnOff);
		}
	}

	public static void setAudioVideoSyncMode(int Mode) {
		if (mPlayer != null) {
			mPlayer.setAudioVideoSyncMode(Mode);
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	static boolean isVisiableSignal = false;
	static int siganlLevel = 0;
	public static void setDMBOnOff(boolean onoff) {
		if (isVisiableSignal != onoff) {
			isVisiableSignal = onoff;
			siganlLevel = 0;

			Intent intent = new Intent("com.telechips.android.tdmb.DMB_STATE_CHANGED");
			intent.putExtra("dmb_onoff", onoff);
			intent.putExtra("dmb_level", siganlLevel);
			DMB_Manager.mContext.sendBroadcastAsUser(intent, new UserHandle(UserHandle.USER_CURRENT));
		}
	}

	public static void monitoringSignal(int iLevel) {
		if (siganlLevel != iLevel) {
			siganlLevel = iLevel;
			Intent intent = new Intent("com.telechips.android.tdmb.DMB_STATE_CHANGED");
			intent.putExtra("dmb_onoff", isVisiableSignal);
			intent.putExtra("dmb_level", iLevel);
			DMB_Manager.mContext.sendBroadcastAsUser(intent, new UserHandle(UserHandle.USER_CURRENT));
//			HP_Manager.mCallback.onChangeDMBSignal(iLevel);
		}
	}
	
	public static void mTPEGFactoryReset()
	{
		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + ">> mTPEGFactoryReset");
		mTpegStart = false;
		mIsDATASVCPrepared = false;
		
		if(mTpegBind == true) {
			mTpegBind = false;
			TpegService.stopService(LauncherMainActivity.getInstance().getApplicationContext());
			TpegService.unbindService(LauncherMainActivity.getInstance().getApplicationContext());
		}
		
		if(mTpegChannel != null) {
			mTpegChannel.serviceName = "";
			LauncherMainActivity.saveSharedPreference();
			mTpegChannel = null;
			mTpegChannelBackup = null;
		}
	}
}