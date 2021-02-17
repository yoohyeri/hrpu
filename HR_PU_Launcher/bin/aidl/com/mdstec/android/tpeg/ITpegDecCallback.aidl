package com.mdstec.android.tpeg;

interface ITpegDecCallback{
	void eventCallback(in byte[] data, int eventCode, int subType);
}