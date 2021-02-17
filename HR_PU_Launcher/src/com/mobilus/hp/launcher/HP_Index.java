package com.mobilus.hp.launcher;

public class HP_Index {

	public final static int MODEL_HYUNDAI = 0;
	public final static int MODEL_KIA = 1;

	public final static int TIME_1_SECOND = 1024;
	public final static int TIME_1DAY_HOUR = 24;
	public final static int MONTH_MAX = 12;

	public final static int DEFUALT_VOL_POPUP_W = 540;
	public final static int DEFUALT_VOL_POPUP_H = 143;

	public final static int DEFUALT_POPUP_W = 600;
	public final static int DEFUALT_POPUP_H = 320;

	public final static int UPDATE_POPUP_W = 760;
	public final static int UPDATE_POPUP_H = 440;

	public final static int SHOW_LIST_VOL_POPUP_W = 500;
	public final static int SHOW_LIST_VOL_POPUP_H = 143;

	public final static int VOL_POPUP_DEFUALT = 0;
	public final static int VOL_POPUP_SMALL = 1;

	public final static int FUEL_DIESEL = 0;
	public final static int FUEL_ELECTRIC = 1;
	public final static int FUEL_LPG = 2;

	/* View */
	public final static int CURRENT_VIEW_HOME = 0;
	public final static int CURRENT_VIEW_DMB = 1;
	public final static int CURRENT_VIEW_MAP = 2;
	public final static int CURRENT_VIEW_SETTING = 3;
	public final static int CURRENT_VIEW_SCREEN_SAVER = 4;

	public final static int LCD_OFF = 0;
	public final static int LCD_ON = 1;

	// Screen size
	public static final int SCREENMODE_LETTERBOX = 0;
	public static final int SCREENMODE_PANSCAN = 1;
	public static final int SCREENMODE_FULL = 2;

	/* Fragment */
	public final static int FRAGMENT_SET_SCREEN = 0;
	public final static int FRAGMENT_SET_SOUND = 1;
	public final static int FRAGMENT_SET_SYSTEM = 2;
	public final static int FRAGMENT_SETTING_MAIN = 3;
	public final static int FRAGMENT_LAUNCHER_MAIN = 4;
	public final static int FRAGMENT_DMB_MAIN = 5;
	public final static int FRAGMENT_SET_SYSTEM_SCREEN_SAVER = 6;
	public final static int FRAGMENT_HIDDEN_MENU = 7;
	
	public final static int CLOCK_WIDGET_MODE = 0;
	public final static int CLOCK_FULL_MODE = 1;
	public final static int MAP_WIDGET_MODE = 2;

	public final static int BACK_HOME = 0;
	public final static int BACK_DMB = 1;
	public final static int BACK_MAP = 2;
	public final static int BACK_SETTING_MAIN = 3;

	/* DMB Volume POPUP */
	public final static int TYPE_POPUP_VISIBLE = 0;

	public final static int VALUE_POPUP_VISIBLE = 0;
	public final static int VALUE_POPUP_INVISIBLE = 1;

	public final static int SET_ALPHA_DMB = 200;
	public final static int SET_ALPHA_LAUNCHER = 255;

	/* DMB Default POPUP */
	public final static int DEFAULT_POPUP_ONE_BUTTON = 0;
	public final static int DEFAULT_POPUP_TWO_BUTTON = 1;
	public final static int DEFAULT_POPUP_LARGE = 2;
	public final static int DEFAULT_POPUP_GONE_BUTTON = 3;
	public final static int DEFAULT_POPUP_SCAN = 4;
	public final static int DEFAULT_POPUP_NOTICE = 5;
	
	/* mode에 따라 출력되는 dialog가 다름 */
	public final static int DIALOG_SHOW_MODE_LAUNCHER = 0;
	public final static int DIALOG_SHOW_MODE_DMB_NORMAL = 1;
	public final static int DIALOG_SHOW_MODE_DMB_CHANNEL_LIST = 2;

	/* Mute state */
	public final static int DMB_SOUND_UNMUTE = 0;
	public final static int DMB_SOUND_MUTE = 1;

	public final static int SYSTEM_SOUND_UNMUTE = 0;
	public final static int SYSTEM_SOUND_MUTE = 1;

	/* Progress Bar Thumb */
	public final static float SCROLL_W = 64;
	public final static float SCROLL_H = 56;

	/* Navi Sound */
	public final static int NAVI_SOUND_UNMUTE = 0;
	public final static int NAVI_SOUND_MUTE = 1;

	/**
	 * DMB
	 */
	/*******************************************************************************
	 * Option Value
	 *******************************************************************************/
	public final static int DEFAULT_VALUE_PRESET = -1;
	public final static int DEFAULT_VALUE_AREA_CODE = -1;
	public final static int DEFAULT_VALUE_PARENTAL_RATING = 5; // default
	public final static String DEFAULT_VALUE_STORAGE = "/mnt/sdcard"; // default
	public final static int DEFAULT_VALUE_AUDIO_ONOFF = 1;
	public final static int DEFAULT_VALUE_VIDEO_ONOFF = 1;
	public final static int DEFAULT_VALUE_SYNC_MODE = 1;
	/*******************************************************************************/

	/* DMB Screen Size */
	public final static int WIDTH_SCREEN = 800;
	public final static int HEIGHT_SCREEN = 480;
	public final static int WIDGET_DMB_WIDTH = 390;
	public final static int WIDGET_DMB_HEIGHT = 263;

	public final static int WIDGET_CLOCK_WIDTH = 393;
	public final static int WIDGET_CLOCK_HEIGHT = 395;
	public final static int WIDGET_CLOCK_MARGIN_LEFT = 5;
	public final static int WIDGET_CLOCK_MARGIN_TOP = 15;

	public final static int DMB_SIGNAL_0 = 0;
	public final static int DMB_SIGNAL_1 = 1;
	public final static int DMB_SIGNAL_2 = 2;
	public final static int DMB_SIGNAL_3 = 3;
	public final static int DMB_SIGNAL_4 = 4;

	/* DMB Type */
	public final static int TYPE_DAB = 1;
	public final static int TYPE_TDMB = 2;

	public final static int COORDINATES_X = 0x00;
	public final static int COORDINATES_Y = 0x01;

	public final static int MPROGRESS_READY = 0;
	public final static int MPROGRESS_SHOW = 1;
	public final static int MPROGRESS_UNSHOW = 2;
	public final static int MNO_SIGNAL_SHOW = 5;

	public final static int REVERSE_OFF = 0;
	public final static int REVERSE_ON = 1;

	/* channel change */
	public final static int CHANNEL_PREV = 0;
	public final static int CHANNEL_NEXT = 1;

	/* Scan Dialog */
	public final static int DIALOG_SCAN = 0;
	public final static int DIALOG_SCAN_START = 1;

	/* DMB On/Off */
	public final static int DMB_VIDEO_ON = 0;
	public final static int DMB_VIDEO_OFF = 1;

	/* Setting Root Menu */
	public final static int ROOT_MENU_SCREEN = 0;
	public final static int ROOT_MENU_SOUND = 1;
	public final static int ROOT_MENU_SYSTEM = 2;
	public final static int ROOT_MENU_SETTING = 3; // Setting main

	/* Setting Sub Menu List */
	public final static int SUB_MENU_LIST_0 = 0;
	public final static int SUB_MENU_LIST_1 = 1;
	public final static int SUB_MENU_LIST_2 = 2;
	public final static int SUB_MENU_LIST_3 = 3;
	public final static int SUB_MENU_LIST_4 = 4;

	public final static int SCREEN_MENU_ADJUST = 0;
	public final static int SCREEN_MENU_LCD_SET = 1;
	public final static int SCREEN_MENU_REAR_CAM = 2;
	public final static int SCREEN_MENU_RATIO = 3;
	public final static int SCREEN_MENU_INIT = 4;

	public final static int SOUND_MENU_VOL_SET = 0;
	public final static int SOUND_MENU_BEEP_SET = 1;
	public final static int SOUND_MENU_L_R_BALANCE = 2;
	public final static int SOUND_MENU_TONE_SET = 3;
	public final static int SOUND_MENU_INIT = 4;

	public final static int SYSTEM_MENU_SCREEN_SAVER = 0;
	public final static int SYSTEM_MENU_TIME_SET = 1;
	// public final static int SYSTEM_MENU_POWER_SET = 2;
	public final static int SYSTEM_MENU_UPDATE = 2;
	public final static int SYSTEM_MENU_SYSTEM_INFO = 3;
	public final static int SYSTEM_MENU_SYSTEM_FACTORY = 4;

	public final static int PROGRESS_MAX = 25;

	public final static int PROGRESS_UP = 0;
	public final static int PROGRESS_DOWN = 1;
	public final static int PROGRESS_SET = 2;
	public final static int PROGRESS_MOVE = 3;

	public final static boolean BRIGHTNESS_DAY_MODE = false; // 주간
	public final static boolean BRIGHTNESS_NIGHT_MODE = true; // 야간

	public final static int DISPLAY_RATIO_4_3 = 0;
	public final static int DISPLAY_RATIO_16_9 = 1;

	/* Power Setting */
	public final static int POWER_SET_DMB_MUTE = 0;
	public final static int POWER_SET_NAVI_DMB_MUTE = 1;

	/* Screen Saver */
	public final static int SCREEN_SAVER_DIGITAL = 0;
	public final static int SCREEN_SAVER_ANALOG = 1;
	public final static int SCREEN_SAVER_NONE = 2;

	/* Time Setting */
	public final static int TIME_SET_24_HOUR = 0;
	public final static int TIME_SET_12_HOUR = 1;

	public final static int TIME_SET_BTN_UP = 0;
	public final static int TIME_SET_BTN_DOWN = 1;

	public final static int TIME_AM = 0;
	public final static int TIME_PM = 1;

	/* Default Brightness */
	public final static int DEFAULT_BRIGHTNESS_STEP = 20;
	
	public final static int GUIDLINE_ENABLE 						= 0;
	public final static int GUIDLINE_DISABLE 				   		= 1;
	
	public final static int GUIDLINE_ENABLE_VEHICLE_TYPE_1T_2W 		= 0;
	public final static int GUIDLINE_ENABLE_VEHICLE_TYPE_1T_4W 		= 1;
	public final static int GUIDLINE_ENABLE_VEHICLE_TYPE_1_2T_2W 	= 2;
	
	// 20191219 yhr
	public final static int REAR_CAM_REFRESH_OFF					= 0;
	public final static int REAR_CAM_REFRESH_ON				   		= 1;
	
	// 20200203 yhr
	public final static int LAST_MODE_NAVI_FULL						= 0;
	public final static int LAST_MODE_DMB_FULL				   		= 1;
	public final static int LAST_MODE_HOME				   			= 2;

}
