package com.mobilus.hp.setting.system.pkt;

import android.content.Context;
import android.os.IMTX;
import android.os.ServiceManager;
import android.util.*;

/**
 * @Project : P1Radio
 * @Package : com.hms.p1.radio.pkt
 * @Name : MPacket.java
 * @Author : hlson@hitecms.co.kr
 * @Date : 2016. 3. 31.
 * @Description : P1 - Radio Packet
 */
public class MPacket {
	/**
	 * SOP - Start Position
	 */
	public byte mSOP;

	/**
	 * Revision
	 */
	public byte mTYPE;

	/**
	 * Command
	 */
	public byte mCmd;

	/**
	 * Length
	 */
	public int mLen1;

	/**
	 * Length
	 */
	public int mLen2;

	/**
	 * Header + Data
	 */
	public int mTotalLen;

	/**
	 * Data Buffer
	 */
	public byte[] mDataBuf;

	/**
	 * Checksum
	 */
	public byte mChecksum;

	/**
	 * EOP - End Position
	 */
	public byte mEOP;

	/**
	 * MPacket
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 31.
	 * @Description : Constructor
	 *
	 */

	public static final int INIT_ = 0;
	public static final int ERROR_ = -1;

	public MPacket() {
		mInit();
	}

	/**
	 * mInit
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 8.
	 * @Description : Initializer
	 *
	 */
	public void mInit() {
		mSOP = FD_PKT.DATA_SOP;

		mTYPE = FD_PKT.DATA_TYPE;

		mLen1 = INIT_;

		mLen2 = INIT_;

		mCmd = ERROR_;

		mDataBuf = null;

		mChecksum = INIT_;

		mEOP = FD_PKT.DATA_EOP;
	}

	/**
	 * mSetBuf
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 7.
	 * @Description : Buffer Set
	 *
	 * @param _buf
	 */
	public void mSetBuf(byte[] _buf) {
		if (_buf == null || _buf.length <= FD_PKT.SIZE_PACKET_HEADER)
			return;
		// SOP
		mSOP = _buf[FD_PKT.IDX_SOP];

		mTYPE = _buf[FD_PKT.IDX_TYPE];

		mLen1 = (int) _buf[FD_PKT.IDX_LEN1];

		mLen2 = (int) _buf[FD_PKT.IDX_LEN2];

		mCmd = _buf[FD_PKT.IDX_CMD];

		// Length = IDX_LEN ~ Data
		mDataBuf = new byte[mGetLen(mLen1, mLen2) - FD_PKT.SIZE_PACKET_LEN - FD_PKT.SIZE_PACKET_CMD];

		// DEBUG_.debug("++++++ mLen : " + mGetLen(mLen1, mLen2) + ",
		// mDataBuf.length : " + mDataBuf.length);

		// Checksum
		mChecksum = _buf[FD_PKT.IDX_DATA + mDataBuf.length];

		System.arraycopy(_buf, FD_PKT.IDX_DATA, mDataBuf, 0, mDataBuf.length);

		// EOP
		mEOP = _buf[FD_PKT.IDX_LEN1 + mDataBuf.length + FD_PKT.SIZE_PACKET_CS];
	}

	/**
	 * mSetBuf
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 31.
	 * @Description :
	 *
	 * @param _cmd1
	 * @param _ar_data
	 * @return true : Set Success, false : Set Fail
	 */
	public boolean mSetBuf(byte _cmd, byte[] _ar_data) {
		if (_ar_data == null || _ar_data.length == 0)
			return false;

		mSOP = FD_PKT.DATA_SOP;

		mTYPE = FD_PKT.DATA_TYPE;

		mCmd = _cmd;

		int _Len = _ar_data.length + FD_PKT.SIZE_PACKET_LEN + FD_PKT.SIZE_PACKET_CMD;

		mLen1 = mGetLenH(_Len);

		mLen2 = mGetLenL(_Len);

		mDataBuf = _ar_data;

		byte[] _checkBuf = new byte[_Len];// FD_PKT.SIZE_CHECK_CRC_HEADER +
											// _len];
		_checkBuf[FD_PKT.IDX_CS_LEN1] = (byte) mLen1;
		_checkBuf[FD_PKT.IDX_CS_LEN2] = (byte) mLen2;
		_checkBuf[FD_PKT.IDX_CS_CMD] = _cmd;
		System.arraycopy(_ar_data, 0, _checkBuf, (FD_PKT.SIZE_PACKET_LEN + FD_PKT.SIZE_PACKET_CMD), _ar_data.length);

		mChecksum = (byte) ~(mCheckSum(_checkBuf));

		mEOP = FD_PKT.DATA_EOP;

		mTotalLen = _ar_data.length + FD_PKT.SIZE_PACKET_HEADER;
		return true;
	}

	/**
	 * mSetBuf
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 31.
	 * @Description :
	 *
	 * @param _cmd1
	 * @param _data
	 * 
	 * @return true : Set Success, false : Set Fail
	 */
	public boolean mSetBuf(byte _cmd, byte _data) {
		return mSetBuf(_cmd, new byte[] { _data });
	}

	/**
	 * mGetAllBuf
	 *
	 * @Author : hlson@hitecms.co.kr
	 * @Date : 2016. 3. 31.
	 * @Description : Buffer
	 *
	 * @return not null : Set Success, is null : Set Fail
	 */
	public byte[] mGetAllBuf() {

		if (mDataBuf == null || mDataBuf.length == 0)
			return null;

		mTotalLen = mDataBuf.length + FD_PKT.SIZE_PACKET_HEADER;
		byte[] _buf = new byte[mTotalLen];

		_buf[FD_PKT.IDX_SOP] = mSOP;

		_buf[FD_PKT.IDX_TYPE] = mTYPE;

		_buf[FD_PKT.IDX_LEN1] = (byte) mLen1;

		_buf[FD_PKT.IDX_LEN2] = (byte) mLen2;

		_buf[FD_PKT.IDX_CMD] = mCmd;

		System.arraycopy(mDataBuf, 0, _buf, FD_PKT.IDX_DATA, mDataBuf.length);

		int _len = mGetLen((int) mLen1, (int) mLen2);
		byte[] _checkBuf = new byte[_len];
		_checkBuf[FD_PKT.IDX_CS_LEN1] = (byte) mLen1;
		_checkBuf[FD_PKT.IDX_CS_LEN2] = (byte) mLen2;
		_checkBuf[FD_PKT.IDX_CS_CMD] = mCmd;
		System.arraycopy(mDataBuf, 0, _checkBuf, (FD_PKT.SIZE_PACKET_LEN + FD_PKT.SIZE_PACKET_CMD), mDataBuf.length);

		_buf[FD_PKT.IDX_LEN1 + _checkBuf.length] = (byte) ~(mCheckSum(_checkBuf));

		_buf[FD_PKT.IDX_LEN1 + _checkBuf.length + FD_PKT.SIZE_PACKET_CS] = mEOP;

		return _buf;

	}

	public int mCheckSum(byte[] bytes) {
		byte sum = 0;
		for (byte b : bytes) {
			sum += (b & 0xff);
		}
		return sum;
	}

	public int mGetLen(int _len1, int _len2) {
		return _len1 * 256 + _len2;
	}

	public int mGetLenH(int _len) {
		return _len / 256;
	}

	public int mGetLenL(int _len) {
		return _len % 256;
	}
}
