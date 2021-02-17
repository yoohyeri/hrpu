package com.mobilus.hp.setting.system.pkt;

/**
 * @Project : R1PLauncher
 * @Package : com.hms.r1p.launcher
 * @Name : MCU_CMD.java
 * @Author : hlson@hitecms.co.kr
 * @Date : 2016. 9. 27.
 * @Description :
 */
public class MCU_ {
	// File Path
	/**
	 * File Path
	 */
	public static final String FILE_PATH = "/storage/usb0/";

	/**
	 * File Name
	 */
	public static final String FILE_NAME = "HR_PU_FW.bin";

	public static final String FILE_FULL_PATH = FILE_PATH + FILE_NAME;

	/**
	 * MCU Bin File Version - Index 3
	 */
	public static final int IDX_MCU_VERSION = 3;

	// Main Command
	/**
	 * Main - CAN ID Number
	 */
	public static final byte M_CMD_CAN = 0x00;
	// Main Command

	// Start Command
	/**
	 * Sub - CAN ID Number
	 */
	public static final byte S_CMD_CAN_ID = 0x0A;

	// Data
	/**
	 * Data - CAN ID Number
	 */
	public static final byte DATA_CAN_ID = 0x00;

	/**
	 * 1 : ?�뜝�룞�삕?�뜝�룞�삕 ?�뜝�룞�삕�뜝占�?
	 */
	public static final byte DATA_CAN_ID_REQ_INS = 1;

	/**
	 * 2 : ?�뜝�룞�삕�뜝占�? ?�뜝�룞�삕?�뜝�룞�삕
	 */
	public static final byte DATA_CAN_ID_REQ_OK = 2;

	/**
	 * 3 : ?�뜝�룞�삕�뜝占�? 椰꾧낀�삕?
	 */
	public static final byte DATA_CAN_ID_REQ_FAIL = 3;

	/**
	 * 4 : CAN ID ?�뜝�룞�삕?�뜝�룞�삕
	 */
	public static final byte DATA_CAN_ID_SET = 4;

	public static final int IDX_SEAT = 0;
	public static final int IDX_DATA_CMD = 1;

	public static final int SIZE_MAX_CAN_ID_EA = 21;

	public static final int CHEAT_KEY = 7777;
}
