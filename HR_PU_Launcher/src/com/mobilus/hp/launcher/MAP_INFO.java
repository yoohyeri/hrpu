package com.mobilus.hp.launcher;

public class MAP_INFO {
	public static final String MAP_TO_DEV = "com.gini.intent.action.GNX_POSTMESSAGE";
	public static final String DEV_TO_MAP = "com.gini.intent.action.GNX_HW_POSTMESSAGE";
	
	/**
	 * Intent Data Key - Command (Map --> App)
	 */
	public static final String CMD_MAIN = "wParam";
	
	/**
	 * Intent Data Key - Data (Map --> App)
	 */
	public static final String CMD_SUB = "lParam";
	
	/**
	 * Get Data Key - Type
	 */
	public static final String CMD_TEXT = "text";
	
	/**
	 * Intent Data Key - Command (App --> Map)
	 */
	public static final String CMD_MAIN_1 = "dwData";
	
	/**
	 * Intent Data Key - Data1 (App --> Map)
	 */
	public static final String CMD_DATA_1 = "lpData1";
	
	/**
	 * Intent Data Key - Data2 (App --> Map)
	 */
	public static final String CMD_DATA_2 = "lpData2";
	
	/**
	 * Menu
	 * 19.03.27 yhr
	 */
	public static final int WM_USER_SHOW_DEVICE_MENU    = 1243;		// 메뉴 초기화  
	
	/**
	 * Fuel
	 * 19.03.21 yhr - 연료메세지 전송
	 */
	public static final int WM_USER_CHANGE_FUEL   		= 1231;		// 연료  
	public final static int FUEL_DIESEL					= 2;	
	public final static int FUEL_LPG					= 3;
	public final static int FUEL_ELECTRIC				= 4;
	
	/* Widget */
	public static final int WM_USER_SET_WIDGET_ONOFF    = 1242;		// 위젯제어  
	public static final int NAVI_WIDGET_OFF  			= 0;
	public static final int NAVI_WIDGET_ON  			= 1;
	
	
	public static final int BM_NOTIFY_WARNING_START  	= 1019;		// '동의함' 버튼 활성화 시작
	public static final int BM_NOTIFY_WARNINGAGREE_END  = 1011;		// '동의함' 버튼 클릭 시 
	public static final int BM_NOTIFY_HW_MENU  			= 1014;		// 단말 메뉴로 진입 시
	public static final int BM_NOTIFY_NAVI_UPDATE  		= 1500;		// 내비 App 업데이트 필요 시 
	public static final int BM_NOTIFY_DESTROY		   	= 1015;		// 내비게이션 종료 시
	
	// 화면 진입
	public static final int BM_NOTIFY_MENU_MAIN    		= 1301;		// 지도화면에서 내비 메인 화면 버튼 클릭 시
	public static final int BM_NOTIFY_MAP_TO_MENU   	= 1314;		// 지도화면에서 메뉴화면으로 진입 시
	public static final int BM_NOTIFY_MENU_TO_MAP   	= 1315; 	// 메뉴화면에서 지도화면 진입 시
	
	public static final int BM_NOTIFY_ACTIVATE    		= 1102; 	// gini map 실행
	
	public static final int BM_NOTIFY_INIT_START   		= 1000; 	// 초기화가 시작
	public static final int BM_NOTIFY_INIT_END   		= 1001; 	// 초기화가 끝남
	public static final int BM_NOTIFY_DESTROY_END   	= 1016;
	public static final int BM_NOTIFY_HIDE    			= 1103;		// WM_USER_SET_HIDE 수신하여 Navi가 HIDE 될 경우 전송 
	
	public static final int BM_NOTIFY_ACTIVATE_ONCREATE		= 0;
	public static final int BM_NOTIFY_ACTIVATE_ONRESUME		= 1;
	public static final int BM_NOTIFY_ACTIVATE_ONPAUSE		= 2;
	public static final int BM_NOTIFY_ACTIVATE_ONDESTROY	= 3;
	
	// 음성
	public static final int BM_START_VOICE_GUIDE 		= 1200;		// 각종 음성 안내 시작
	public static final int BM_END_VOICE_GUIDE    		= 1201; 	// 1200 음성 안내 종료 시
	
	public static final int BM_START_BTN_CLICKSOUND    	= 1202;		// 버튼 클릭 음 시작 
	public static final int BM_END_BTN_CLICKSOUND   	= 1203; 	// 버튼 클릭 음 종료
	
	public static final int BM_START_OVERSPEED_SOUND   	= 1215; 	// 과속알림, 사용자 제한 속도, 효과음 재생 시작 시
	public static final int BM_END_OVERSPEED_SOUND    	= 1216; 	// 1215 음성안내 종료 시
	
	public static final int BM_SET_GUIDE_MUTE    		= 1206;
	public static final int BM_STATUS_VOLUME_MUTE    	= 1208;
	
	/* illumination */
	public static final int BM_CHANGE_DAY_TO_NIGHT   	= 1004; 	// 주간 --> 야간모드로 변경 됨
	public static final int BM_CHANGE_NIGHT_TO_DAY    	= 1005; 	// 야간 --> 주간모드로 변경 됨
	
	
	/* App2Map */
	public static final int WM_USER_CLOSE  				= 1224;
	public static final int WM_USER_WARNING_CLOSED   	= 1126;
	public static final int WM_USER_SET_HIDE   			= 1137;
	public static final int WM_USER_SET_GUIDE_MUTE  	= 1146;
	public static final int WM_USER_SET_RESTORE     	= 1138;	
//	public static final int WM_USER_SET_EFFECT_MUTE     = 1151;	 // 버튼음

	
	public static final int WM_USER_SET_DAY_MODE  		= 1225;		
	public static final int DAY_MODE  					= 1;
	public static final int NIGHT_MODE  				= 2;
	
	public static final int NAVI_MUTE_OFF  				= 0;
	public static final int NAVI_MUTE_ON  				= 1;
	
	
}
