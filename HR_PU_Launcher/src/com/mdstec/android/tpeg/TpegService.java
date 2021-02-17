package com.mdstec.android.tpeg;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Log;

import com.mdstec.android.tpeg.ITpegDecService;
import com.mdstec.android.tpeg.TPEG_FileCopy.onCopyCompletedListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.mdstec.android.tpeg.ITpegDecCallback;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.MAP_INFO;

public class TpegService {
	private static final String CLASS_NAME = "[TpegService ]  ";

	static final int EVT_CTT_TRANSFER = 0x400 + 5100;
	static final int EVT_SDI_TRANSFER = 6125;
	static final int EVT_CTTSUM_TRANSFER = 6130;
	static final int EVT_CTTPRD_TRANSFER = 6131;

	static final int EVT_POIOPS2_TRANSFER = 6138;
	static final int EVT_POI_TRANSFER = 6126;
	static final int EVT_NWS_TRANSFER = 6128;
	static final int EVT_RTM_TRANSFER = 6129;

	static final int EVT_WEALIVE_TRANSFER = 6145;
	static final int EVT_WEAVILL_TRANSFER = 6146;
	static final int EVT_WEAWEEK_TRANSFER = 6147;
	static final int EVT_WEASPEC_TRANSFER = 6148;
	static final int EVT_WEARAID_TRANSFER = 6149;
	//2019-06-27
	 static final int EVT_NTC_TRANSFER = 6199;

	// GINI Intent Message Type
	static final int IM_CTT_L3 = 0;
	static final int IM_CTT_SUM = 1;
	static final int IM_CTT_PRD = 2;
	static final int IM_SDI = 11;
	static final int IM_RTM = 15;
	static final int IM_POI = 20;
	static final int IM_OIL2 = 22;
	static final int IM_NWS = 24;
	static final int IM_WEALIVE = 26;
	static final int IM_WEAVILL = 27;
	static final int IM_WEAWEEK = 28;
	static final int IM_WEASPEC = 29;
	static final int IM_WEARAID = 30;
	public static final int IM_NTC = 25;	 

	 //GiNi TPEG File Name
	 static final String CTT_L3_NAME = "TPCTT3.BIN";
	 static final String CTT_SUM_NAME = "TPCTTSUM.BIN";
	 static final String CTT_PRD_NAME = "TPCTTPRD.BIN";
	 static final String SDI_NAME = "TPSDI.BIN";
	 static final String RTM_NAME = "TPRTM.BIN";
	 static final String POI_NAME = "TPPOI.BIN";
	 static final String OIL2_NAME = "TPOIL2.BIN";
	 static final String NWS_NAME = "TPNWS.BIN"; 
	 
	 static final String WEALIVE_NAME = "TPWEALIVE.BIN"; 
	 static final String WEAVILL_NAME = "TPWEAVILL.BIN"; 
	 static final String WEAWEEK_NAME = "TPWEAWEEK.BIN"; 
	 static final String WEASPEC_NAME = "TPWEASPEC.BIN"; 
	 static final String WEARAID_NAME = "TPWEARAID.BIN"; 
	  //2019-06-27
	public static final String NTC_NAME = "TPNTC.BIN"; 

	private static int evtcount;

	public static boolean mServicedBind = false;
	public static ITpegDecService mService;

	static Context mContext;

	public static void startService(Context _Context) {
		Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "startService");

		Intent intent = new Intent();
		intent.setClassName("com.mdstec.android.tpeg", "com.mdstec.android.tpeg.decoder.TpegService");
		_Context.startService(intent);
		mContext = _Context;
	}

	public static void stopService(Context _Context) {
		Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "stopService");

		Intent intent = new Intent();
		intent.setClassName("com.mdstec.android.tpeg", "com.mdstec.android.tpeg.decoder.TpegService");
		_Context.stopService(intent);
	}

	public static void bindService(Context _Context) {
		Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "bindService");

		Intent intent = new Intent();
		intent.setClassName("com.mdstec.android.tpeg", "com.mdstec.android.tpeg.decoder.TpegService");
		mServicedBind = _Context.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
	}

	public static void unbindService(Context _Context) {
		Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "unbindService");

		try {
			_Context.unbindService(mConnection);
			mServicedBind = false;
		} catch (IllegalArgumentException e) {
		}
	}

	public static void fillMSCData(byte[] pData) {
		if (TpegService.mService == null)
			return;

		if (mServicedBind) {
			boolean bRunning = false;

			try {
				bRunning = TpegService.mService.getRunningStatus();
			} catch (RemoteException e1) {
				e1.printStackTrace();
			}

			if (bRunning) {
				try {
					TpegService.mService.fillMSCData(pData);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private static ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "onServiceDisconnected");
			if (TpegService.mService != null) {
				try {
					TpegService.mService.unregisterCallback(mCallback);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "onServiceConnected");
			TpegService.mService = ITpegDecService.Stub.asInterface(service);
			try {
				TpegService.mService.registerCallback(mCallback);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};

	public static String TPEG_NTC_COPY_PATH = "/storage/sdcard1/TPNTC";
	private static int mNtcCnt;
	private static File mSrcFile;
	private static File mFileDst;
	private static ITpegDecCallback mCallback = new ITpegDecCallback.Stub() {

		@Override
		public void eventCallback(byte[] data, int eventCode, int subType) throws RemoteException {
			int scid = subType & 0x00FF;
			@SuppressWarnings("unused")
			int sub_type = subType >> 16;
			@SuppressWarnings("unused")
			int evtCode = 0;

			String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/data/TPEG/TPEGDATA/";
			
			switch (eventCode) 
			{
			case EVT_CTT_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_CTT_L3] : " + data.length);
				evtCode = IM_CTT_L3;
				file_path = file_path + "TPCTTL3.bin";
			}
			break;
			case EVT_CTTSUM_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_CTT_SUM] : " + data.length);
				evtCode = IM_CTT_SUM;
				file_path = file_path + "TPCTTSUM.bin";
			}
			break;
			// case EVT_CTTPRD_TRANSFER:
			// {
			// Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_CTT_PRD] : " + data.length);
			// evtCode = IM_CTT_PRD;
			//// file_path = file_path + "TPSDI.bin";
			// }
			// break;
			case EVT_SDI_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_SDI] : " + data.length);
				evtCode = IM_SDI;
				file_path = file_path + "TPSDI.bin";
			}
			break;
			case EVT_NWS_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_NWS] : " + data.length);
				evtCode = IM_NWS;
				file_path = file_path + "TPNWS.bin";

			}
			break;
			case EVT_RTM_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_RTM] : " + data.length);
				evtCode = IM_RTM;
				file_path = file_path + "TPRTM.bin";
			}
			break;
			case EVT_POI_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_POI] : " + data.length);
				evtCode = IM_POI;
				file_path = file_path + "TPPOI.bin";
			}
			break;
			case EVT_POIOPS2_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_OIL2] : " + data.length);
				evtCode = IM_OIL2;
				file_path = file_path + "TPOIL2.bin";
			}
			break;
			case EVT_WEALIVE_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_WEALIVE] : " + data.length);
				evtCode = IM_WEALIVE;
				file_path = file_path + "TPWEALIVE.bin";
			}
			break;
			case EVT_WEAVILL_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_WEAVILL] : " + data.length);
				evtCode = IM_WEAVILL;
				file_path = file_path + "TPWEAVILL.bin";
			}
			break;
			case EVT_WEAWEEK_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_WEAWEEK] : " + data.length);
				evtCode = IM_WEAWEEK;
				file_path = file_path + "TPWEAWEEK.bin";
			}
			break;
			case EVT_WEASPEC_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_WEASPEC] : " + data.length);
				evtCode = IM_WEASPEC;
				file_path = file_path + "TPWEASPEC.bin";
			}
			break;
			case EVT_WEARAID_TRANSFER: {
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[IM_WEARAID] : " + data.length);
				evtCode = IM_WEARAID;
				file_path = file_path + "TPWEARAID.bin";
			}
			break;
			//2019-06-27
			case EVT_NTC_TRANSFER:
			{			
				// TYPE=IM_NTC, PATH=NTC_NAME
//				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[EVT_NTC] : " + data.length);		
				evtCode = IM_NTC;
				file_path = file_path + NTC_NAME;
				
				//190827  NTC Parser Test
//				mNtcParser(file_path);
				
				// 190826 NTC Test
//				mNtcCnt++;
//				String file_dst_path = TPEG_NTC_COPY_PATH + "_" + mNtcCnt + ".BIN";
//				
//				mSrcFile = new File(file_path);
//				mFileDst = new File(file_dst_path);
//				
//				Log.e("yhr", "eventCode[EVT_NTC] : " + data.length + ", mSrcFile.getPath : " + mSrcFile.getPath()  
//							+ ", mSrcFile : " + mSrcFile.isFile()
//						    + ", mFileDst.getPath : " + mFileDst.getPath());	
//				
//				mHandler.sendEmptyMessageDelayed(MSG_START_FILE_COPY, 500);
				
			}
			break;
			default: {
				Log.i(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode[" + eventCode + "] : " + data.length);
			}
			return;
			}
			
			Intent _intent = new Intent();
			_intent.setAction(MAP_INFO.DEV_TO_MAP);
			_intent.putExtra(MAP_INFO.CMD_MAIN_1, evtCode);
			_intent.putExtra(MAP_INFO.CMD_DATA_1, file_path);
			mContext.sendBroadcastAsUser(_intent, new UserHandle(UserHandle.USER_CURRENT));
			evtcount++;
		}
	};
	
	private static void mNtcParser(String fileName)
	{
		try {
			FileInputStream fis = new FileInputStream(new File(fileName));
			int fileSize = fis.available();
			
			byte[] buffer = new byte[fileSize];
			int readBytes = fis.read(buffer);
			
			if(readBytes != fileSize){
				Log.e(HP_Manager.TAG_TPEG, CLASS_NAME + "readBytes is not equals fileSize!!");
				return;
			}
			
			ByteBuffer bb = ByteBuffer.wrap(buffer);
			
			//int type = bb.get();
			byte type = bb.get();
			Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Header Type=" + type);
			int messageVersion = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
			Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Header messageVersion=" + messageVersion);
			int count = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
			Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Header count=" + count);
			int messageGenerationTime = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
			Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Header messageGenerationTime=" + messageGenerationTime);
			int reserved = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
			
			for(int i = 0; i < count; i++){
				int msgStartTime = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
				int msgStopTime = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
				int msgExpireTime = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
				int severityFactor = bb.get();
				int newsType = bb.get();
				int newsSubType = bb.get();
				int newsStatus = bb.get();
				int newsReportTime = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
				int articleTitleLength = bb.get();
				if(articleTitleLength > 0){
					byte[] titleBytes = new byte[articleTitleLength];
					bb.get(titleBytes);
					String title = new String(titleBytes, "EUC-KR");
					Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Title => " + title);
				}
				int articleLength = bb.order(ByteOrder.LITTLE_ENDIAN).getShort();
				if(articleLength > 0){
					byte[] articleBytes = new byte[articleLength];
					bb.get(articleBytes);
					String article = new String(articleBytes, "EUC-KR");
					Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "Article => " + article);
				}
				int imageType = bb.get();
				//char imageType = (char) bb.get();
				int imageLength = bb.order(ByteOrder.LITTLE_ENDIAN).getInt();
				if(imageLength > 0){
					byte[] image = new byte[imageLength];
					bb.get(image);
				}
				int authorshipType = bb.get();
				Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "authorshipType => " + authorshipType);
				int authorshipLength = bb.get();
				Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "authorshipLen => " + authorshipLength);
				if(authorshipLength > 0){
					byte[] authorshipBytes = new byte[authorshipLength];
					bb.get(authorshipBytes);
					String authorship = new String(authorshipBytes, "EUC-KR");
					Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "authorship => " + authorship);
				}
			}
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final int MSG_START_FILE_COPY = 0;
	public static Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_FILE_COPY:
				mNTCFileCopy(mSrcFile, mFileDst);
				break;
			}
			super.handleMessage(msg);
		}
		
	};
	
	private static void mNTCFileCopy(File mSrcFile, File mFileDst)
	{
		Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "mNTCFileCopy / mSrcFile.getPath : " + mSrcFile.getPath()  
									 	 + ", mSrcFile : " + mSrcFile.isFile()
									     + ", mFileDst.getPath : " + mFileDst.getPath());	
		
		TPEG_FileCopy copy = new TPEG_FileCopy(HP_Manager.mContext, mSrcFile, mFileDst, "파일 복사 중");
		TPEG_FileCopy.onCopyCompletedListener listener = new onCopyCompletedListener() {
			
			@Override
			public void onCopySuccess() {
				Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode // onCopySuccess");
			}
			
			@Override
			public void onCopyFail() {
				Log.d(HP_Manager.TAG_TPEG, CLASS_NAME + "eventCode // onCopyFail");
			}
		};
		copy.setOnCopyCompletedListener(listener);
        copy.mThreadRun();
	}
}
