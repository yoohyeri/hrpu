package com.mobilus.hp.setting.system.pkt;

/**
 * @Project	: P1_DVB-T2
 * @Package : com.hms.p1.dvb_t2.pkt
 * @Name	: FD_PKT.java
 * @Author	: hlson@hitecms.co.kr
 * @Date	: 2016. 3. 31.
 * @Description	: 
 */
public class FD_PKT {
	/**
	 * Index - SOP
	 */
	public static final int IDX_SOP				= 0;
	
	/**
	 * Index - Revision
	 */
	public static final int IDX_TYPE			= 1;
	
	/**
	 * Index - Length1
	 */
	public static final int IDX_LEN1			= 2;
	
	/**
	 * Index - Length2
	 */
	public static final int IDX_LEN2			= 3;
	
	/**
	 * Index - Command
	 */
	public static final int IDX_CMD				= 4;
	
	/**
	 * Index - Data
	 */
	public static final int IDX_DATA			= 5;

	/**
	 * Size Len (Len1, Len2)
	 */
	public static final int SIZE_PACKET_LEN		= 2;
	
	/**
	 * Size CMD (cmd)
	 */
	public static final int SIZE_PACKET_CMD		= 1;
	
	/**
	 * Size CS (cs)
	 */
	public static final int SIZE_PACKET_CS		= 1;
	
	/**
	 * Header Size 5
	 */
	public static final int SIZE_PACKET_HEADER	= 7;
	
	/**
	 * Size = No Checksum Length
	 */
	public static final int SIZE_NO_CHECKSUM	= 2;
	
	
	/**
	 * Index - Checksum Length
	 */
	public static final int IDX_CS_LEN1			= 0;
	
	/**
	 * Index - Checksum Length
	 */
	public static final int IDX_CS_LEN2			= 1;
	
	/**
	 * Index - Checksum CMD1
	 */
	public static final int IDX_CS_CMD			= 2;
	
	
	/**
	 * Revision
	 */
	public static final byte TYPE_				= 0x05;
	
	/**
	 * State Start
	 */
	public static final int STATE_SOP			= 0;
	
	/**
	 * State Revision
	 */
	public static final int STATE_REV			= 1;
	
	/**
	 * State Length 1
	 */
	public static final int STATE_LEN1			= 2;
	
	/**
	 * State Length 1
	 */
	public static final int STATE_LEN2			= 3;
	
	/**
	 * State CMD 1
	 */
	public static final int STATE_CMD			= 4;
	
	/**
	 * State Data
	 */
	public static final int STATE_DATA			= 5;
	
	/**
	 * State Checksum 6
	 */
	public static final int STATE_CS			= 6;
	//----------
	
	/**
	 * State EOP
	 */
	public static final int STATE_EOP			= 7;
	
	/**
	 * Command Data Size 1byte
	 */
	public static final int SIZE_CMD			= 2;
	
	/**
	 * Length Data Size 1byte
	 */
	public static final int SIZE_LEN			= 1;
	

	

	
	
	
	/**
	 * Data Max Size 64byte
	 */
	public static final int MAX_SIZE			= 255;
	
	/**
	 * Data Max Size 59byte
	 */
	public static final int MAX_SIZE_DATA		= MAX_SIZE - SIZE_PACKET_HEADER;
	

	
	/**
	 * Index - Command2
	 */
	public static final int IDX_CMD2			= 4;
	

	
	/**
	 * Index - Checksum Start (IDX_DATA + Length)
	 */
	public static final int IDX_CS				= 1;
	
	/**
	 * Index - EOP Start (IDX_CS + 1)
	 */
	public static final int IDX_EOP				= (IDX_CS + 1);
	
	
	
	/**
	 * Size - Checksum Header
	 */
	public static final int SIZE_CS_HEADER		= 2;
	
	/**
	 * Size - Header - Checksum Data
	 */
	public static final int SIZE_CS_OTHER_HEADER		= 4;
	
	/**
	 * Data - SOP(Start)
	 */
	public static final byte DATA_SOP			= (byte) 0xaa;
	
	/**
	 * Revision
	 */
	public static final int DATA_TYPE			= 0x05;
	
	/**
	 * Data - EOP(Start)
	 */
	public static final byte DATA_EOP			= (byte) 0x55;
	
	/**
	 * Checksum Error
	 */
	public static final int ERROR_CS			= -1;
	
	/**
	 * Packet Error
	 */
	public static final int ERROR_PKT			= -2;
	
	/**
	 * No Error
	 */
	public static final int SUCCESS_			= 0;
	
	
	//--
}
