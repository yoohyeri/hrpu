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
import com.telechips.android.tdmb.player.TDMBPlayer;

import android.util.Log;
import android.view.View;

public class DxbView_Indicator {
	private static final String CLASS_NAME = "[DxbView_Indicator ]  ";

	public final static int DEFAULT_PCBER = 10000;
	public final static int DEFAULT_VBER = 500;

	static TDMBPlayer.OnSignalStatusListener ListenerOnSignalStatus = new TDMBPlayer.OnSignalStatusListener() {
		public void onSignalStatusUpdate(TDMBPlayer player, int idx, int nStatus) {
			// nStatus - [0 : bad] [1 : good]
			Log.i(HP_Manager.TAG_DMB, CLASS_NAME + "ListenerOnSignalStatus --> onSignalStatusUpdate::IDX[" + idx + "] (" + nStatus + ")");
		}
	};

	private static int _signals[];
	private static int _freq = 0;
	private static int _PCBer, _ViterBer;
	private static int mWeakCnt = 0;
	private static String _channelname = "";
	static Runnable mRunnable_Signal = new Runnable() {
		public void run() {

			_signals = DxbPlayer.getSignalStrengthAll();
			if (_signals == null)
				return;
			
			if(HP_Manager.mProductionProcess)
			{
				if(DxbPlayer.mProductionProcessChannel != null)
				{
					_freq = DxbPlayer.mProductionProcessChannel.ensembleFreq;
					_channelname = DxbPlayer.mProductionProcessChannel.serviceName;
				}
			}
			else
			{
				_freq = DxbPlayer.mChannel.ensembleFreq;
				_channelname = DxbPlayer.mChannel.serviceName;
			}
			
			_PCBer = _signals[2];
			_ViterBer = _signals[4];

			if (LauncherMainActivity.tvStrength != null && LauncherMainActivity.tvStrength.getVisibility() == View.VISIBLE) {
				String strength = "Channel Name : " + _channelname + "\nSignal Strength : "
						+ _signals[0] + "\nSNR : " + _signals[1] + "\nPCBer : " + _signals[2] + "\nRSSI : "
						+ _signals[3] + "\nViterBer : " + _signals[4] + "\nFICBer : " + _signals[5] + "\nMSCBer : "
						+ _signals[6] + "\nensenbleFreq :" + _freq + ", service ID : " + DxbPlayer.mChannel.serviceID;
				LauncherMainActivity.tvStrength.setText(strength);
			}

			if (DxbView_Normal.gComponent.mWeakSignalPopup == null) {
				Log.e(HP_Manager.TAG_DMB, CLASS_NAME + "WeakSignalPopup(Normal) is null...");
				DxbView.mHandler_Main.postDelayed(mRunnable_Signal, 1000);
				return;
			}

			if (DxbPlayer.mPlayer != null) {
				if ((DMB_Manager.g_Information.cCOMM.iCount_TV > 0)
						|| (DMB_Manager.g_Information.cCOMM.iCount_Radio > 0)) {
					if (_PCBer >= DEFAULT_PCBER || _ViterBer >= DEFAULT_VBER) {
						mWeakCnt++;
						if (mWeakCnt > 2) {
//							 Log.d(HP_Manager.TAG_CAS, CLASS_NAME + "Weak Signal // DxbView.eState : "+ DxbView.eState +", PCBer : " + _PCBer + ", _ViterBer : " + _ViterBer);
							 
							if (DxbView.eState == DxbView.STATE.NORMAL_VIEW) {
								if (DxbView_Normal.gComponent.mWeakSignalPopup.getVisibility() == View.GONE) {
									if (HP_Manager.mProductionProcess)
										DxbView_Normal.gComponent.mWeakSignalPopup.setVisibility(View.GONE);
									else
									{
										DxbView_Normal.gComponent.mWeakSignalPopup.setVisibility(View.VISIBLE);
									}

									DxbView_Normal.gComponent.mWeakSignalPopup.setAlpha((float) 0.8);
									if(DxbView_Normal.mTouchLayout == false)
									{
										DxbView_Normal.mIsFullView = true;
										DxbView_Normal.ResetDisplayFull();
									}
								}
							} else if (DxbView.eState == DxbView.STATE.WIDGET) {
								if (DMB_Widget.mWeakSignalPopup != null) {
									
									if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
									{
										if (DMB_Widget.llTitleBar.getVisibility() == View.VISIBLE) {
											DMB_Widget.mWeakSignalFullModePopup.setVisibility(View.VISIBLE);
											DMB_Widget.mWeakSignalPopup.setVisibility(View.GONE);
											DMB_Widget.mWeakSignalFullModePopup.setAlpha((float) 0.8);
										} else {
											DMB_Widget.mWeakSignalFullModePopup.setVisibility(View.GONE);
											DMB_Widget.mWeakSignalPopup.setVisibility(View.VISIBLE);
											DMB_Widget.mWeakSignalPopup.setAlpha((float) 0.8);
										}
									}
								}
							} else {
								if (DxbView_Full.gComponent.mFullWeakSignalPopup.getVisibility() == View.GONE) {
									if (HP_Manager.mProductionProcess)
										DxbView_Full.gComponent.mFullWeakSignalPopup.setVisibility(View.GONE);
									else
									{
										if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
											DxbView_Full.gComponent.mFullWeakSignalPopup.setVisibility(View.VISIBLE);
									}

									DxbView_Full.gComponent.mFullWeakSignalPopup.setAlpha((float) 0.8);
								}
							}
						}
					} else {
						mWeakCnt = 0;
						if (DxbView.eState == DxbView.STATE.NORMAL_VIEW) {
							if (DxbView_Normal.gComponent.mWeakSignalPopup.getVisibility() == View.VISIBLE) {
								DxbView_Normal.gComponent.mWeakSignalPopup.setVisibility(View.GONE);
								if(DxbView_Normal.mTouchLayout == false)
								{
									DxbView_Normal.mIsFullView = true;
									DxbView_Normal.ResetDisplayFull();
								}

							}
						} else if (DxbView.eState == DxbView.STATE.WIDGET) {
							if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_ON)
							{
								if (DMB_Widget.mWeakSignalPopup != null) {
									if (DMB_Widget.mWeakSignalPopup.getVisibility() == View.VISIBLE)
										DMB_Widget.mWeakSignalPopup.setVisibility(View.GONE);
	
									if(DMB_Widget.mWeakSignalFullModePopup.getVisibility() == View.VISIBLE)
										DMB_Widget.mWeakSignalFullModePopup.setVisibility(View.GONE);
								}
							}
						} else {
							if (DxbView_Full.gComponent.mFullWeakSignalPopup.getVisibility() == View.VISIBLE)
								DxbView_Full.gComponent.mFullWeakSignalPopup.setVisibility(View.GONE);
						}
					}
				}

				if (DxbPlayer.getScanCount() > 0)
				{
					DxbView.mHandler_Main.postDelayed(mRunnable_Signal, 1000);
				}
				else
				{
					if(HP_Manager.mProductionProcess == false)
					{
						DxbView.mHandler_Main.removeCallbacks(mRunnable_Signal);
					}
				}
				
			}
		};
	};

	public static void Signal_visible() {
		DxbView.mHandler_Main.removeCallbacks(mRunnable_Signal);
		DxbView.mHandler_Main.postDelayed(mRunnable_Signal, 1000);
	}

	public static void Signal_invisible() {
		DxbView.mHandler_Main.removeCallbacks(mRunnable_Signal);
	}

	public static void Signal_changeLevel(int iLevel) {
		DxbPlayer.monitoringSignal(iLevel);
	}
}