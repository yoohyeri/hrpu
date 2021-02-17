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

public class DMB_Manager
{
	/*******************************************************************************
	 * Option Value
	 *******************************************************************************/
		public static int		DEFAULT_VALUE_PRESET			= -1;
		public static int		DEFAULT_VALUE_AREA_CODE			= -1;
		public static int		DEFAULT_VALUE_PARENTAL_RATING	= 5;		// default
		public static String	DEFAULT_VALUE_STORAGE			= "/mnt/sdcard";	// default
		public static int		DEFAULT_VALUE_AUDIO_ONOFF		= 1;
		public static int		DEFAULT_VALUE_VIDEO_ONOFF		= 1;
		public static int		DEFAULT_VALUE_SYNC_MODE			= 1;
	/*******************************************************************************/

		public static int		DMB_AUDIO_ON			= 1;
		public static int		DMB_AUDIO_OFF			= 0;
	
	enum PLAYER
	{
		ATSC,
		DVBT,
		ISDBT,
		TDMB,
		NULL
	}
	static PLAYER	ePLAYER;
	
	/**
	 * TDMB Player Activity
	 */
	static LauncherMainActivity mContext;
	
	/*
	 * Componet
	 */
	static Component g_Component = new Component();
	
	/*
	 * Information
	 */
	static Information	g_Information = new Information();
	
	/**
	 * Initialize
	 * @param context
	 */
	public static void init(LauncherMainActivity context)
	{
		/*	Set Player	*/
		ePLAYER	= PLAYER.TDMB;
		mContext = context;
		
		/*	Set ListView	*/
		if(ePLAYER == PLAYER.ISDBT)
		{
			g_Information.cLIST.iCount_Tab	= 1;
		}
		
		/*	set Option	*/
		Information.OPTION	mOption	= g_Information.cOption;
	}	
}