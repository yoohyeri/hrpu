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

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.widget.Toast;
import android.view.Gravity;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemProperties;
import android.util.Log;
import android.util.TypedValue;
import android.text.method.ScrollingMovementMethod;

import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.player.TDMBPlayer;

public class DxbView_CAS_debug extends DxbView
{
	static String CLASS_NAME = "[DxbView_CAS_debug ]  ";
	
	private static final int SXC_CAS_INFO 			= 0;
	private static final int SXC_CAS_ENTI 			= 1;
	private static final int SXC_CAS_SERVICE 		= 2;
	private static final int SXC_CAS_EMM_LOG 		= 3;
	private static final int SXC_CAS_ECM 			= 4;
	private static final int SXC_CAS_EMM 			= 5;
	
	private static final int SXC_CAS_RESTORATION 	= 10;
	private static final int SXC_CAS_CHANNEL_STATUS = 11;

	private static int casCmd = -1;
	private static String casMsg = null;

	private static boolean isAllMenuShowing = false;
	private static boolean isSubMenuShowing = false;
	private static Activity mContext = null;

	private static LinearLayout mCAS_Layout;
	private static LinearLayout mSXC_Info;
	private static LinearLayout mEntitllement_Info;
	private static LinearLayout mService_Info;
	private static LinearLayout mEMM_Log;
	private static LinearLayout mECM_Monitoring;
	private static LinearLayout mEMM_Monitoring;
	private static LinearLayout mSXC_Reset;

	private static LinearLayout mCAS_desc_layout;
	private static TextView mCAS_desc_text;
	private static Button mBackKey;

	//	Toast
	static Toast mToast = null;

	static TextView mTextViewToast = null;
	
	static Handler mUIHandler;

	static public void init(Activity context)
	{
		mContext = context;
		setComponent();
		setListener();
		
		mUIHandler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				if(msg.what == 0) {
					mCAS_desc_text.setText(desc_text);
				}
			};
		};
	}

	private static void setComponent()
	{
		mCAS_Layout = (LinearLayout)mContext.findViewById(R.id.cas_debug_layout);

		mSXC_Info = (LinearLayout)mContext.findViewById(R.id.sxc_info_layout);
		mEntitllement_Info = (LinearLayout)mContext.findViewById(R.id.entitlement_info_layout);
		mService_Info = (LinearLayout)mContext.findViewById(R.id.service_info_layout);
		mEMM_Log = (LinearLayout)mContext.findViewById(R.id.emm_log_layout);
		mECM_Monitoring = (LinearLayout)mContext.findViewById(R.id.ecm_monitoring_layout);
		mEMM_Monitoring = (LinearLayout)mContext.findViewById(R.id.emm_monitoring_layout);
		mSXC_Reset = (LinearLayout)mContext.findViewById(R.id.sxc_data_reset_layout);
		mBackKey = (Button)mContext.findViewById(R.id.btn_back);

		mCAS_desc_layout = (LinearLayout)mContext.findViewById(R.id.cas_desc_layout);
		mCAS_desc_text = (TextView) mContext.findViewById(R.id.cas_desc_text);
		mCAS_desc_text.setMovementMethod(new ScrollingMovementMethod());
	}

	private static void setListener()
	{
		mSXC_Info.setOnClickListener(ListenerOnClick);
		mEntitllement_Info.setOnClickListener(ListenerOnClick);
		mService_Info.setOnClickListener(ListenerOnClick);
		mEMM_Log.setOnClickListener(ListenerOnClick);
		mECM_Monitoring.setOnClickListener(ListenerOnClick);
		mEMM_Monitoring.setOnClickListener(ListenerOnClick);
		mSXC_Reset.setOnClickListener(ListenerOnClick);
		mBackKey.setOnClickListener(ListenerOnClick);
	}

	static public void resetView()
	{
		mSXC_Info.setVisibility(View.GONE);
		mEntitllement_Info.setVisibility(View.GONE);
		mService_Info.setVisibility(View.GONE);
		mEMM_Log.setVisibility(View.GONE);
		mECM_Monitoring.setVisibility(View.GONE);
		mEMM_Monitoring.setVisibility(View.GONE);
		mSXC_Reset.setVisibility(View.GONE);

		mCAS_desc_text.setText("");
		mCAS_desc_layout.setVisibility(View.VISIBLE);
		mBackKey.setVisibility(View.VISIBLE);
	}

	static View.OnClickListener ListenerOnClick = new View.OnClickListener()
	{
		public void onClick(View v) {
			int index = 0;

			int id = v.getId();
			switch (id) {
				case R.id.sxc_info_layout:
					resetView();
					mSXC_Info.setVisibility(View.VISIBLE);
					index = 0;
					isSubMenuShowing = true;
					break;
				case R.id.entitlement_info_layout:
					resetView();
					mEntitllement_Info.setVisibility(View.VISIBLE);
					index = 1;
					isSubMenuShowing = true;
					break;
				case R.id.service_info_layout:
					resetView();
					mService_Info.setVisibility(View.VISIBLE);
					index = 2;
					isSubMenuShowing = true;
					break;
				case R.id.emm_log_layout:
					resetView();
					mEMM_Log.setVisibility(View.VISIBLE);
					index = 3;
					isSubMenuShowing = true;
					break;
				case R.id.ecm_monitoring_layout:
					resetView();
					mECM_Monitoring.setVisibility(View.VISIBLE);
					index = 4;
					isSubMenuShowing = true;
					break;
				case R.id.emm_monitoring_layout:
					resetView();
					mEMM_Monitoring.setVisibility(View.VISIBLE);
					index = 5;
					isSubMenuShowing = true;
					break;
				case R.id.sxc_data_reset_layout:
					index = 6;
					break;
				case R.id.btn_back:
					index = 7;
					break;
			}

			if(index <= 5) {
				isAllMenuShowing = false;
				DxbPlayer.cmdCAS(index);

			}else if( index == 6){
				DxbPlayer.cmdCAS(index);
			}
			else {
				if(isAllMenuShowing == false )
					setVisible(true);
				else
					setVisible(false);
			}
		}
	};

	public static void setVisible(boolean v)
	{
		if( v == true) {
			isAllMenuShowing = true;

			mSXC_Info.setVisibility(View.VISIBLE);
			mEntitllement_Info.setVisibility(View.VISIBLE);
			mService_Info.setVisibility(View.VISIBLE);
			mEMM_Log.setVisibility(View.VISIBLE);
			mECM_Monitoring.setVisibility(View.VISIBLE);
			mEMM_Monitoring.setVisibility(View.VISIBLE);
			mSXC_Reset.setVisibility(View.VISIBLE);

			mCAS_Layout.setVisibility(View.VISIBLE);
			mCAS_desc_layout.setVisibility(View.GONE);
			
			isSubMenuShowing = false;
		}
		else 
		{
			mContext.findViewById(R.id.layout_cas_debug).setVisibility(View.GONE);
			isAllMenuShowing = false;
			
			// 190905 yhr 
			SystemProperties.set(LauncherMainActivity.PROPERTIES_CAS, "false");
			LauncherMainActivity.engineer_cas = false;
			LauncherMainActivity.cas_state = false;
			LauncherMainActivity.getInstance().setStatusBarButteonEnable(true);
		}
	}
	
	static void onBackPressed() {
		if(isSubMenuShowing) {
			setVisible(true);
		}
		else {
			setVisible(false);
			LauncherMainActivity.cas_state = false;
		}
	}
	
	private static String desc_text;
	static TDMBPlayer.OnCASListener ListenerOnCAS = new TDMBPlayer.OnCASListener()
	{
		public void onCAS(int index, String text)
		{
			Log.d(HP_Manager.TAG_CAS, CLASS_NAME + "onCAS : index = " + index);
			switch (index)
			{
				case SXC_CAS_INFO:
				case SXC_CAS_ENTI:
				case SXC_CAS_SERVICE:
				case SXC_CAS_EMM_LOG:
				case SXC_CAS_ECM:
				case SXC_CAS_EMM:
				{
					//Log.i( "onCAS : text = " + text);
					//mCAS_desc_text.setText(text);
					desc_text = text;
					mUIHandler.sendEmptyMessage(0);
					break;
				}

				case SXC_CAS_RESTORATION:
				case SXC_CAS_CHANNEL_STATUS:
				{
					casCmd = index;
					casMsg = text;
					DxbView.mHandler_Main.removeCallbacks(mRunnable_CAS);
					DxbView.mHandler_Main.postDelayed(mRunnable_CAS, 500);
					break;
				}

				default:
					Log.d(HP_Manager.TAG_CAS, CLASS_NAME +  "onCAS : Unknown CAS Commnad");
					break;
			}
		}
	};

	private static Runnable mRunnable_CAS = new Runnable()
	{
		public void run()
		{
			if (casMsg != null)
			{
				if (mTextViewToast == null)
				{
					mTextViewToast = new TextView(mContext);
					mTextViewToast.setBackgroundColor(Color.GRAY);
					mTextViewToast.setText(casMsg);
					mTextViewToast.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
					mTextViewToast.setTextColor(Color.RED); //Color.MAGENTA
				}
				else
					mTextViewToast.setText(casMsg);
				
				if(mToast == null)
				{					
					mToast = Toast.makeText(mContext, "", Toast.LENGTH_LONG);
					mToast.setView(mTextViewToast);
					mToast.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL, 0, 0);
				}
				else
				{					
					//mToast.show();
					mToast.setView(mTextViewToast);
				}
				mToast.show();
			}
			
			if (casCmd == SXC_CAS_RESTORATION)
				DxbPlayer.cmdCASRestoration();
		}
	};
}