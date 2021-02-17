///*
// * Copyright (C) 2013 Telechips, Inc.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
package com.telechips.android.tdmb;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.mdstec.android.tpeg.TpegService;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;
import com.telechips.android.tdmb.LauncherMainActivity.DXB_LIFE_CYCLE;
import com.telechips.android.tdmb.player.*;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class DxbScan
{
	private final static String CLASS_NAME = "[DxbScan ]  ";
	public static String msgScanning;
	public static int mScanProgress;
	
	static TDMBPlayer.OnSearchPercentListener ListenerOnSearchPercent = new TDMBPlayer.OnSearchPercentListener()
	{
		public void onSearchPercentUpdate(TDMBPlayer player, int idx, int nPercent)
		{	
			if(DxbPlayer.eState != DxbPlayer.STATE.SCAN)
				return;
			
			mHandler.removeMessages(MSG_UPDATE_CHANNEL);
			mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CHANNEL, 10*HP_Index.TIME_1_SECOND);
//			Log.i(HP_Manager.TAG_DMB, CLASS_NAME + "[SCAN ]  ListenerOnSearchPercent --> onSearchPercentUpdate::IDX[" + idx + "] (" + nPercent + "), DxbPlayer.eState : " + DxbPlayer.eState);
		
			if(idx == 0)
			{
				// 진행률 표출
				msgScanning = nPercent + "%";
				mScanProgress = nPercent;
				DxbView.updateScreen();
			}
		}
	};
	
	private static Comparator<Channel> cmpAsc = new Comparator<Channel>() {
		private Collator collator = Collator.getInstance();
		
		@Override
		public int compare(Channel lhs, Channel rhs) {
			return collator.compare(lhs.serviceName, rhs.serviceName);
		}
	};
	
	static TDMBPlayer.OnSearchCompletionListener ListenerOnSearchCompletion = new TDMBPlayer.OnSearchCompletionListener()
	{
		public void onSearchCompletion(TDMBPlayer player, int idx, int manual)
		{
			mHandler.removeMessages(MSG_UPDATE_CHANNEL);
			if(DxbPlayer.eState != DxbPlayer.STATE.SCAN)
				return;
	
//			Log.i(HP_Manager.TAG_DMB, CLASS_NAME + "2 onSearchCompletion() [idx:" + idx + "][manual:" + manual + "]" + "[eState:" + DxbPlayer.eState + "]");
			if(idx == player.mModuleIndex)
			{			
				if(HP_Manager.mProductionProcess)
				{
					DMB_Manager.mContext.findViewById(R.id.layout_channel_list).setVisibility(View.GONE);
					if(LauncherMainActivity.eCycle_Life != DXB_LIFE_CYCLE.ON_DESTROY)
						DxbPlayer.mSetProductProcessChannel();
				}
				else
				{
					DxbView_Normal.gComponent.btnScan.setEnabled(true);
					DxbView_Normal.gComponent.btnScan.setAlpha(1);
					
					if(LauncherMainActivity.eCycle_Life != DXB_LIFE_CYCLE.ON_DESTROY)
						mUpdateChannelList();
				}
			}
				
//			//////// 191126 NTC Test
			int evtCode = 0;
			String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/TPEG/TPEGDATA/";
			
			evtCode = TpegService.IM_NTC;
			file_path = file_path + TpegService.NTC_NAME;
			
			Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[EVT_NTC] // " + file_path);
			
			Intent _intent = new Intent();
			_intent.setAction(MAP_INFO.DEV_TO_MAP);
			_intent.putExtra(MAP_INFO.CMD_MAIN_1, evtCode);
			_intent.putExtra(MAP_INFO.CMD_DATA_1, file_path);
			HP_Manager.mContext.sendBroadcastAsUser(_intent, new UserHandle(UserHandle.USER_CURRENT));
//			//////////////////////////////////////////////////////////////////////////////////////////////////
			
		}
	};
	
	
	public static void mDMBFactoryReset()
	{
		LauncherMainActivity.getInstance().pauseDMB();
		DxbPlayer.mPlayer.stop();
		
		DxbPlayer.mHDChannelList.clear();
		DxbPlayer.mTDMBChannelList.clear();
		DxbPlayer.mBackupChannelList.clear();	
		DxbPlayer.mNormalChannelList.clear();
		DxbPlayer.mProductionProcessChannelList.clear();

		// start - hlson DB
		int _db_size = DxbPlayer.mTDMBChannelList.size();
		ChannelManager _mchannelMGR = new ChannelManager(DMB_Manager.mContext).open();
		_mchannelMGR.deleteAllChannel2();

		if(_db_size > 0) {
			for(int i = 0; i < _db_size; i++) {
				Channel _channel =  DxbPlayer.mTDMBChannelList.get(i);
				_mchannelMGR.insertChannel2(_channel.ensembleName, _channel.ensembleID, _channel.ensembleFreq,
											_channel.serviceName, _channel.serviceID, _channel.channelName,
											_channel.channelID, _channel.type, _channel.bitrate,
											_channel.reg[0], _channel.reg[1], _channel.reg[2],
											_channel.reg[3], _channel.reg[4], _channel.reg[5], _channel.reg[6]);
			}
			DxbPlayer.mNormalChannelList.clear();
			DxbPlayer.mHDChannelList.clear();
			DxbPlayer.mTDMBChannelList.clear();
			DxbPlayer.mProductionProcessChannelList.clear();
		}
		_mchannelMGR.close();

		DxbView_Message.removeDialog();
		DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
		DxbView_Normal.updateChannelList(0);
		HP_Manager.mCntSearchChannel = 0;
		DxbView_Normal.mPrePosition = 0;
	}
	
	public static void mUpdateChannelList()
	{
		// 19.1.18 yhr ==> Scan 중에 이전채널/채널검색/다음채널 버튼 활성화
		DxbView_Normal.setBtnEnable(true);
		
		// 19.01.17 yhr --> HD DMB를 위로 수정
		Collections.sort(DxbPlayer.mHDChannelList, cmpAsc);
		Collections.sort(DxbPlayer.mNormalChannelList, cmpAsc);
		DxbPlayer.mHDChannelList.addAll(DxbPlayer.mNormalChannelList);
		DxbPlayer.mTDMBChannelList.addAll(DxbPlayer.mHDChannelList);
		
		if(DxbPlayer.eState == DxbPlayer.STATE.SCAN_STOP) {
			DxbPlayer.mTDMBChannelList.clear();
			if(DxbPlayer.mBackupChannelList.size() > 0) {
				DxbPlayer.mTDMBChannelList.addAll(DxbPlayer.mBackupChannelList);
			}
			DxbPlayer.mBackupChannelList.clear();											
		}
//		Log.d(HP_Manager.TAG_DMB, CLASS_NAME + "mUpdateChannelList 2 // mBackupChannelList size : " + DxbPlayer.mBackupChannelList.size() 
//																+ ", mTDMBChannelList size : " + DxbPlayer.mTDMBChannelList.size()
//																+ ", eState : " + DxbPlayer.eState);
		
		LauncherMainActivity.saveSharedPreference();
		mTpegStart();
		
		// start - hlson DB
		int _db_size = DxbPlayer.mTDMBChannelList.size();
		ChannelManager _mchannelMGR = new ChannelManager(DMB_Manager.mContext).open();
		_mchannelMGR.deleteAllChannel2();
		
		if(_db_size > 0) {
			for(int i = 0; i < _db_size; i++) {
				Channel _channel =  DxbPlayer.mTDMBChannelList.get(i);
				_mchannelMGR.insertChannel2(_channel.ensembleName, _channel.ensembleID, _channel.ensembleFreq,
											_channel.serviceName, _channel.serviceID, _channel.channelName,
											_channel.channelID, _channel.type, _channel.bitrate,
											_channel.reg[0], _channel.reg[1], _channel.reg[2],
											_channel.reg[3], _channel.reg[4], _channel.reg[5], _channel.reg[6]);
			}
			DxbPlayer.mNormalChannelList.clear();
			DxbPlayer.mHDChannelList.clear();
		}
		_mchannelMGR.close();
		
		// end - DB
		DxbView_Message.removeDialog();
		DxbPlayer.eState = DxbPlayer.STATE.GENERAL;
		DxbView_Normal.updateChannelList(0);
		
		Information.COMM cInfo	= DMB_Manager.g_Information.cCOMM;
		if(cInfo.curChannels != null) {
			ArrayList<Integer> _lIndex = new ArrayList<Integer>();
			
			if(DxbView_Normal.mChoiceChannelName == null)
				DxbView_Normal.mChoiceChannelName="";
			
			if(DxbView_Normal.mChoiceChannelName.length() == 0) {
				cInfo.curChannels.moveToFirst();
			} else {							
				String[] _ar = getIndexForServiceName(cInfo.curChannels);
				for(int i = 0; i< _ar.length; i++) {
					if(_ar[i].equalsIgnoreCase(DxbView_Normal.mChoiceChannelName)) {
						_lIndex.add(i);
						break;
					}
				}
											
				int cntChannls = _lIndex.size();
				if(cntChannls == 0) {
					cInfo.curChannels.moveToFirst();
				}
				else if(cntChannls == 1) {
					cInfo.curChannels.moveToPosition(_lIndex.get(0));
				}
				else {
					int _index = _lIndex.get(0);
					for(int i = 0; i < cntChannls; ++i) {
						cInfo.curChannels.moveToPosition(_lIndex.get(i));
						
						if(HP_Manager.mProductionProcess == false)
						{
							if( (cInfo.curChannels.getInt(2) == DxbView_Normal.mChoiceEnsembleID) &&
								(cInfo.curChannels.getInt(5) == DxbView_Normal.mChoiceChannelID) ) {
								_index = _lIndex.get(i);
								break;
							}
						}
						else
						{
							if(cInfo.curChannels.getInt(3) == DxbPlayer.PRODUCTION_PROGRESS_SET_LOW_CHANNEL
									|| cInfo.curChannels.getInt(3) == DxbPlayer.PRODUCTION_PROGRESS_SET_MID_CHANNEL
									||cInfo.curChannels.getInt(3) == DxbPlayer.PRODUCTION_PROGRESS_SET_HIG_CHANNEL)
								_index = _lIndex.get(i);
						}
					}
					cInfo.curChannels.moveToPosition(_index);
				}							
			}
			
			cInfo.iCount_TV 		= cInfo.curChannels.getCount();
			cInfo.iCount_Current	= cInfo.curChannels.getCount();
			if(SystemProperties.get(HP_Manager.PROPERTIES_CAS_USER, "").equals("")) {
				DxbPlayer.cmdCAS(0);
			}
			DxbView.setChannel();
		} else {
			cInfo.iCount_TV 		= 0;
			cInfo.iCount_Current	= 0;
			
			DxbView.updateScreen();
		}
	}

	public static String[] getIndexForServiceName(Cursor _cursor) throws SQLException {
		String[] _array = null;
		
		if(_cursor != null) {
			int _count = _cursor.getCount();
			_array = new String[_count];
			_cursor.moveToFirst();
			
			for(int i = 0; i < _count; i++) {
				_array[i] = _cursor.getString(4);
				_cursor.moveToNext();
			}
		}
		return _array;
	}
	
	private static Channel getChannelFromCursor(Cursor c) {
		Channel ch = new Channel();
		ch.ensembleName		= c.getString(1);
		ch.ensembleID		= c.getInt(2);
		ch.ensembleFreq		= c.getInt(3);
		ch.serviceName		= c.getString(4);
		ch.serviceID		= c.getInt(5);
		ch.channelName		= c.getString(6);
		ch.channelID		= c.getInt(7);
		ch.type				= c.getInt(8);
		ch.bitrate			= c.getInt(9);		
		ch.chIdx			= c.getInt(0);
		
		for(int regIndex=0; regIndex<7; regIndex++)
			ch.reg[regIndex] = c.getInt(regIndex+10);
		
		return ch;
	}
	
	/**
	 * Scan Start
	 */
	static void startFull()
	{		
		DxbView_Normal.setBtnEnable(false);
		
		// 190401 yhr 채널 검색 시작하면 mRunnable_Signal 정지
		DxbPlayer.stop();
				
		if(DxbView_Normal.gComponent.mWeakSignalPopup.getVisibility() == View.VISIBLE)
			DxbView_Normal.gComponent.mWeakSignalPopup.setVisibility(View.GONE);
		
		HP_Manager.mCntSearchChannel = 0;
		DxbView_Normal.ClearDisplayFull();
		
		DxbPlayer.mNormalChannelList.clear();
		DxbPlayer.mHDChannelList.clear();
		DxbPlayer.mTDMBChannelList.clear();
		DxbPlayer.mProductionProcessChannelList.clear();
		Information.COMM cInfo = DMB_Manager.g_Information.cCOMM;		
		DxbPlayer.mBackupChannelList.clear();
		int len = cInfo.curChannels.getCount();		
		
		if(HP_Manager.mProductionProcess == false)
		{
			if(len > 0) {
				cInfo.curChannels.moveToFirst();
				for(int i = 0; i < len; ++i) {
					DxbPlayer.mBackupChannelList.add(getChannelFromCursor(cInfo.curChannels));
					cInfo.curChannels.moveToNext();
				}
			}
		}
		
		DxbPlayer.eState = DxbPlayer.STATE.SCAN;
		DxbView.updateScreen();
		
		mHandler.removeMessages(MSG_UPDATE_CHANNEL);
		mHandler.sendEmptyMessageDelayed(MSG_UPDATE_CHANNEL, 10*HP_Index.TIME_1_SECOND);
		
		Information.COMM info = DMB_Manager.g_Information.cCOMM;
		info.iCount_TV		= 0;
		info.iCount_Radio	= 0;
		info.iCount_Current	= 0;

		DxbPlayer.search();
		DMB_Manager.mContext.showDialog(DxbView_Message.DIALOG_SCAN);

		//kslee
		mTpegStop();
	}
	
	static void startManual(int Hz_frequency)
	{
		DxbView_Normal.ClearDisplayFull();
		if(DxbPlayer.eState != DxbPlayer.STATE.GENERAL)
		{
			Log.i(HP_Manager.TAG_DMB, CLASS_NAME + "please_wait_cancel_scanning");
			return;
		}

		DxbPlayer.eState = DxbPlayer.STATE.SCAN;	
		DxbView.updateScreen();
		
		Information.COMM	info	= DMB_Manager.g_Information.cCOMM;
		info.iCount_TV		= 0;
		info.iCount_Radio	= 0;
		info.iCount_Current	= 0;

		DxbPlayer.manualScan(Hz_frequency);
		DMB_Manager.mContext.showDialog(DxbView_Message.DIALOG_SCAN);
	}
	
	static void startProductionProcessManual()
	{
		if(DxbView_Normal.notiView != null)
		{
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) DxbView_Normal.notiView.getLayoutParams();
			DMB_Manager.mContext.findViewById(R.id.layout_channel_list).setVisibility(View.GONE);
			lp.leftMargin = 0;
			DxbView_Normal.notiView.setLayoutParams(lp);
		}
		
		HP_Manager.mCntSearchChannel = 0; 
		DxbView_Normal.ClearDisplayFull();
		if(DxbPlayer.eState != DxbPlayer.STATE.GENERAL)
		{
			Log.i(HP_Manager.TAG_DMB, CLASS_NAME + "please_wait_cancel_scanning");
			return;
		}
	
		DxbPlayer.eState = DxbPlayer.STATE.SCAN;	
		DxbPlayer.mStart = false;
		DxbView.updateScreen();
		
		Information.COMM	info	= DMB_Manager.g_Information.cCOMM;
		info.iCount_TV		= 0;
		info.iCount_Radio	= 0;
		info.iCount_Current	= 0;

		DxbPlayer.mProductionProcessSearch();
		DMB_Manager.mContext.showDialog(DxbView_Message.DIALOG_SCAN);
	}
	
	public static void cancel()
	{
		mScanProgress = 0;
		msgScanning = mScanProgress + "%";
		
		mHandler.removeMessages(MSG_UPDATE_CHANNEL);
		mHandler.removeMessages(ACK_UPDATE_CHANNEL);
		
		if(HP_Manager.mProductionProcess == false)
		{
			if(DxbPlayer.mPlayer != null)
			{
				DxbPlayer.mPlayer.searchCancel();
				DxbPlayer.eState = DxbPlayer.STATE.SCAN_STOP;
				mUpdateChannelList();
			}
		}
		else
		{
			if(DxbPlayer.mPlayer != null)
			{
				DxbPlayer.mPlayer.searchCancel();
				DxbPlayer.eState = DxbPlayer.STATE.SCAN_STOP;
//				updateProductionProcessChannelList();
//				DxbPlayer.mPlayer.searchCancel();
			}
		}
			
		DxbView_Normal.setBtnEnable(true);
		DxbView.updateScreen();
		
		//kslee
		mTpegBackUpToChannel();
		mTpegStart();
	}	
	
	private final static int MSG_UPDATE_CHANNEL	= 0;
	private final static int ACK_UPDATE_CHANNEL	= 1;
	private static int mTimeOutCnt = 0;
	
	/**
	 * yhr
	 */
	public static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_UPDATE_CHANNEL:
				removeMessages(MSG_UPDATE_CHANNEL);
				sendEmptyMessage(ACK_UPDATE_CHANNEL);
				break;
			case ACK_UPDATE_CHANNEL:
				removeMessages(ACK_UPDATE_CHANNEL);
				mTimeOutCnt++;
				if(mTimeOutCnt == 5)
				{
					mTimeOutCnt = 0;
					if(DxbPlayer.mPlayer != null)
					{
						DxbPlayer.eState = DxbPlayer.STATE.SCAN_FAIL;
						DxbPlayer.mPlayer.searchCancel();
					}
					DxbView.updateScreen();
				}
				else
					sendEmptyMessageDelayed(MSG_UPDATE_CHANNEL, 10*HP_Index.TIME_1_SECOND);
				break;

			default:
				break;
			}
			
			super.handleMessage(msg);
		}
		
	};
	
	//kslee
	public static void mTpegStart() {
//		Log.i(HP_Manager.TAG_DMB, CLASS_NAME + ">>> mTpegStart");
		DxbPlayer.mSetTpegChannel();
	}
	public static void mTpegStop() {
//		Log.i(HP_Manager.TAG_DMB, CLASS_NAME + ">>> mTpegStop");
		if(DxbPlayer.mDATASVC != null && DxbPlayer.mIsDATASVCPrepared) {
			DxbPlayer.mIsDATASVCPrepared = false;
			if(DxbPlayer.mTpegChannelBackup == null)	DxbPlayer.mTpegChannelBackup = new Channel();
			DxbPlayer.mTpegChannelBackup = DxbPlayer.mTpegChannel;
			DxbPlayer.mTpegChannel = null;
		}
	}
	public static void mTpegChannelToBackUp() {
		if(DxbPlayer.mTpegChannel != null) {
			DxbPlayer.mTpegChannelBackup = new Channel();
			DxbPlayer.mTpegChannelBackup = DxbPlayer.mTpegChannel;
			DxbPlayer.mTpegChannel = null;
		}
	}
	public static void mTpegBackUpToChannel() {
		if(DxbPlayer.mTpegChannelBackup != null) {
			DxbPlayer.mTpegChannel = new Channel();
			DxbPlayer.mTpegChannel = DxbPlayer.mTpegChannelBackup;
			DxbPlayer.mTpegChannelBackup = null;
		}
	}
}