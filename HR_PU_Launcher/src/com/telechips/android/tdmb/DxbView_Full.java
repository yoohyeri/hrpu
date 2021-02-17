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

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;

import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

public class DxbView_Full {
	private static final String CLASS_NAME = "[DxbView_Full ]  ";
	public static Component.cFullView gComponent = null;
	public static int eState_ScreenSize = HP_Index.SCREENMODE_FULL;

	/**
	 * Initialize
	 */
	static public void init() {
		gComponent = DMB_Manager.g_Component.fullview;
		setComponent();
	}

	/**
	 * GUI Initialize
	 */
	static private void setComponent() {
		gComponent.vVideo = DMB_Manager.mContext.findViewById(R.id.full_view);
		gComponent.vVideo.setOnClickListener(ListenerOnClick);
		gComponent.vVideo.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				
				if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_DMB)
					return true;
				
				return false;
			}
		});
		
		gComponent.mFullWeakSignalPopup = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.full_toast_popup_layout);
		gComponent.mFullWeakSignalPopup.setVisibility(View.GONE);
	}
	
	static OnClickListener ListenerOnClick = new OnClickListener()
	{
		public void onClick(View v)
		{
			int id = v.getId();
			if(id == R.id.full_view)
			{
				DxbView.setState(false, DxbView.STATE.NORMAL_VIEW);
				return;
			}
		}
	};
	
	public final static int MSG_LONG_CLICK	= 0;
	public static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_LONG_CLICK:
				removeMessages(MSG_LONG_CLICK);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};
}