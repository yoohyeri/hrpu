package com.mobilus.hp.setting.system.pkt;

import android.util.Log;

/**
 * @Project : P1_DVB-T2
 * @Package : com.hms.p1.dvb_t2.pkt
 * @Name : PKTUtils.java
 * @Author : hlson@hitecms.co.kr
 * @Date : 2016. 3. 4.
 * @Description :
 */
public class PKTUtils {
	/**
	 * Integer to 2 byte type size
	 */
	public static final int INT_TO_2BYTE_SIZE = 2;
	public static final int INT_TO_4BYTE_SIZE = 4;

	/**
	 * m2ByteToInt
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 8.
	 * @Description : 2 Byte To INT
	 *
	 * @param _2byte_int
	 * @return
	 */
	public static int m2ByteToInt(byte[] _2byte_int) {
		return m2ByteToInt(_2byte_int[1], _2byte_int[0]);
	}

	/**
	 * m2ByteToInt
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 8.
	 * @Description : 2 Byte To INT
	 *
	 * @param _f_byte
	 * @param _s_byte
	 * @return
	 */
	public static int m2ByteToInt(byte _f_byte, byte _s_byte) {
		return ((_f_byte & 0xff) << 8) | (_s_byte & 0xff);
	}

	/**
	 * mCheckSum
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 2. 3.
	 * @Description :
	 *
	 * @param bytes
	 * @return
	 */
	public static int mCheckSum(byte[] bytes) {
		byte sum = 0;
		// int index = 0;
		for (byte b : bytes) {

			sum += (b & 0xff);
			// String str = String.format("index : %d value : %02X sum : %02X",
			// index, b, sum);
			// Log.d("debug", str);
			// index ++;

		}
		return sum;
	}

	/**
	 * mIntTo2Byte
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 4. 6.
	 * @Description :
	 *
	 * @param _int
	 * @return
	 */
	public static byte[] mIntTo2Byte(int _int) {
		byte[] _it2Byte = new byte[INT_TO_2BYTE_SIZE];
		_it2Byte[0] = (byte) (_int & 0xFF);
		_it2Byte[1] = (byte) ((_int >> 8) & 0xFF);
		return _it2Byte;
	}

	public static byte[] mIntTo4Byte(int _int) {
		byte[] _it4Byte = new byte[INT_TO_4BYTE_SIZE];
		_it4Byte[0] = (byte) (_int & 0xFF);
		_it4Byte[1] = (byte) ((_int >> 8) & 0xFF);
		_it4Byte[2] = (byte) ((_int >> 16) & 0xFF);
		_it4Byte[3] = (byte) ((_int >> 24) & 0xFF);
		return _it4Byte;
	}

	public static int mGetLen(int _len1, int _len2) {
		return _len1 * 256 + _len2;
	}

	public static int mGetLenH(int _len) {
		return _len / 256;
	}

	public static int mGetLenL(int _len) {
		return _len % 256;
	}
}
