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

import com.mobilus.hp.launcher.DynamicTextSeekbar;
import com.mobilus.hp.launcher.LongClickEvent;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class Component
{
	cIndicator	indicator					 = new cIndicator();
	cWidgetView widgetview 				     = new cWidgetView();
	cNormalView	normalview					 = new cNormalView();
	cFullView	fullview					 = new cFullView();
	cScreenSaverFullView screensaverfullView = new cScreenSaverFullView();
	
	/*
	 * Indicator
	 */
	class cIndicator
	{
		ImageView	ivSection;
		ImageView	ivSignal;
		TextView	timeIndicator;
	}
	
	/*
	 * DMB Widget View
	 */
	class cWidgetView
	{
		// Video
		View vVideo;
		
		LinearLayout llWidgetTitleBar;
		TextView channelIcon;
		TextView channelName;
		
		RelativeLayout rlWidgetBg;
		LinearLayout llWidgetBg;
		ImageView imgWidgetNoti;
		TextView txtWidgetNoti;
		
		LinearLayout llWidgetDMBOff;
	}
	
	/*
	 * Normal View
	 */
	public class cNormalView
	{
		// 생산공정프로그램
		RelativeLayout rlProductionProcess;
		Button btnOK;
		Button btnNG;
		Button btnProdeuctionSCAN;
				
		LinearLayout llStausBar;
		
		// Title Bar
		LinearLayout llTitleBar;
		TextView strChannelName;
		TextView btnScan;
		TextView btnMenu;
		TextView btnBack;
		
		// Bottom Bar
		LinearLayout llControlPanel;

		TextView mMuteOnOff;
		LongClickEvent mVolumeDown, mVolumeUp;
		public DynamicTextSeekbar mSeekBar;
		
		
		// Ext Menu List
		RelativeLayout rlExtMenuList;
		ListView extMenuListView;
		String[] strExtListTitle;
		
		//	Channel List
		ListView channelListView;

		// Video
		View vVideo;
		int	ID_row;
		
		// 수신감도 미약
		RelativeLayout mWeakSignalPopup;
	}

	/*
	 * DMB Full View
	 */
	class cFullView
	{
		// Video
		View vVideo;
		
		// 수신감도 미약
		RelativeLayout mFullWeakSignalPopup;
	}
	
	/*
	 * Screen Saver Full View
	 */
	class cScreenSaverFullView
	{
		// View
		View vScreenSaverFull;
	}
}