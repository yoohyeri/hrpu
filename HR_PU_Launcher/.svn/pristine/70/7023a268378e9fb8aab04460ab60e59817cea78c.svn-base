package com.mdstec.android.tpeg;

import com.mdstec.android.tpeg.ITpegDecCallback;

interface ITpegDecService{
	boolean getRunningStatus();
	void fillMSCData(in byte[] pData); 
	void registerCallback(ITpegDecCallback cb);
    void unregisterCallback(ITpegDecCallback cb);
	int sdiReset(in int sequence); 
}