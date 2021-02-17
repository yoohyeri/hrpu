package com.mobilus.hp.setting.system.pkt;

/**
 * @Project	: R1PSettings
 * @Package : com.hitecms.app.rse.settings.pkt
 * @Name	: FD_RSPS.java
 * @Author	: hlson@hitecms.co.kr
 * @Date	: 2016. 10. 5.
 * @Description	: 
 */
public class FD_RSPS {
	public static final byte NON_ERROR					= 0x00;
	public static final byte CHECKSUM_ERROR				= 0x01;
	public static final byte MODE_ERROR					= 0x02;
	public static final byte TIME_OUT					= 0x02;
	public static final byte INDEX_ERROR				= 0x03;
	public static final byte REQUEST_PKG				= 0x00;
	public static final byte FLASH_WRITE_ERROR			= 0x01;
	public static final byte SUCCESS					= 0x00;
	
	public static final byte MODE_NORMAL				= (byte)0xFF;
	public static final byte MODE_DOWNLOAD				= 0x00;
	
	public static final byte UNKNOWN_EQUIPMENT				= 0x00;
	public static final byte CURRENT_EQUIPMENT				= 0x02;
	public static final byte OTHER_EQUIPMENT				= 0x03;	
}
