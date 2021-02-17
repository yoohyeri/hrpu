package com.mobilus.hp.setting.system.pkt;

import java.util.ArrayList;

import android.util.Log;

public class MTXUartProtocol {

	public static final byte SOP = (byte) 0xAA;
	public static final byte EOP = (byte) 0x55;
	public static final byte TYPE_NORMAL = (byte) 0x01;
	public static final byte TYPE_CAN_STANDARD = (byte) 0x02;
	public static final byte TYPE_CAN_EXTENDED = (byte) 0x03;
	public static final byte TYPE_UART_TOUCH = (byte) 0x04;
	public static final byte TYPE_FW_UPGRADE = (byte) 0x05;

	public static final byte INDEX_STOP = 0;
	public static final byte INDEX_TYPE = 1;
	public static final byte INDEX_LEN = 2;
	public static final byte INDEX_CMD = 3;
	public static final byte INDEX_DATA = 4;

	public static final byte SIZE_LEN = 2;
	public static final byte SIZE_CMD = 1;

	public static final byte INDEX_UPDATE_CMD = 4;
	public static final byte INDEX_UPDATE_DATA = 5;

	public class Command {
		public static final byte SYSTEM = (byte) 0x01;
		public static final byte DISPLAY = (byte) 0x02;
		public static final byte AUDIO = (byte) 0x03;
		public static final byte RADIO = (byte) 0x04;
		public static final byte STEERING_KEY = (byte) 0x05;
		public static final byte DVD = (byte) 0x06;
	}

	public class DVDCommand {
		public static final byte BYPASS = (byte) 0xFF;
	}

	public static int MakeCRC(ArrayList<Byte> buf, int _size) {
		int ret = 0;
		int sum = 0;
		if (buf.size() < 3)
			return -1;

		// for(int i = 0; i < buf.get(_size); i++)
		for (int i = 0; i < _size; i++)
			sum = (sum + (byte) buf.get(i + 2)) & 0xFF;

		ret = ~(sum) & 0xFF;
		return (byte) ret;
	}

	public static byte MakeCRC(ArrayList<Byte> buf) {
		int ret = 0;
		int sum = 0;
		if (buf.size() < 3)
			return -1;

		for (int i = 0; i < buf.get(2); i++)
			sum = (sum + (byte) buf.get(i + 2)) & 0xFF;

		ret = ~(sum) & 0xFF;
		return (byte) ret;
	}
}
