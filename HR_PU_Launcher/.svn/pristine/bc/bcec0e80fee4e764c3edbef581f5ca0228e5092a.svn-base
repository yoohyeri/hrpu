package com.mobilus.hp.setting.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.popup.UpgradeDialog;
import com.mobilus.hp.setting.DEBUG_;
import com.mobilus.hp.setting.system.pkt.FD_PKT;
import com.mobilus.hp.setting.system.pkt.FD_RQST;
import com.mobilus.hp.setting.system.pkt.FD_RSPS;
import com.mobilus.hp.setting.system.pkt.MPacket;
import com.mobilus.hp.setting.system.pkt.MTXUartProtocol;
import com.mobilus.hp.setting.system.pkt.PKTUtils;

import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IMTX;
import android.os.IMTXMCUCallback;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;

/**
 * 화면 조정에 대한 Fragment
 * 
 * @author yhr
 *
 */
public class System_fw_Update {

	private static final String CLASS_NAME = "[System_fw_Update ]  ";

	String PROPERTIES_USB_TYPE_KEY = "mtx.sdfs";
	String PROPERTIES_USB_TYPE_DEFAULT = "NTFS";
	
	public static String FW_UPDATE_FILE_COPY_PATH 	= "/storage/sdcard0/HR_PU_FW_upgrade.bin";
	public static String FW_UPDATE_FILE_SDCARD_PATH = "/storage/sdcard1/HR_PU_FW_upgrade.bin";
	public static final int FW_UPDATE_MODE_START 	= 1;
	public static final int FW_UPDATE_MODE_STOP 	= 2;

	IntentFilter receiverFilter = new IntentFilter();
	static private IMTX mIMTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));

	File mFile;

	static boolean mCheckFWUpdate = false;
	public static int mFWUpdateState;

	public System_fw_Update() {
	}

	public System_fw_Update(File _file) { 
		mFWUpdateState = -1;
		mFile = _file;
	}

	public void mRebootingCmd()
	{
		new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT))
		.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}
	
	public void mStartUpdate() {
		if (isServiceRunningCheck()) {
			Intent intent = new Intent(HP_Manager.mContext, NotifyDialog.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			HP_Manager.mContext.stopService(intent);
		}
		
		mFWUpdateState = FW_UPDATE_MODE_START;
		UpgradeDialog.mCurrentPopup = UpgradeDialog.FW_UPGRADE_POPUP;
		Intent popup = null;
		popup = new Intent(HP_Manager.mContext, UpgradeDialog.class);
		HP_Manager.mContext.startService(popup);
		
		try {
			mIMTX.setMCUListener(cServiceCallback);
			new MUpgradeLoadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	public static final int UPDATE_TIMEOUT = 0;
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(msg.what == UPDATE_TIMEOUT)
			{
				removeMessages(UPDATE_TIMEOUT);
				Intent updatepopup = null;
				updatepopup = new Intent(LauncherMainActivity.getInstance(), UpgradeDialog.class);
				LauncherMainActivity.getInstance().stopService(updatepopup);
				
				mFWUpdateState = FW_UPDATE_MODE_STOP;
				NotifyDialog.mCurrentPopup = NotifyDialog.FW_UPDATE_TIMEOUT;
				Intent popup = null;
				popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
				LauncherMainActivity.getInstance().startService(popup);
			}
		}
	};
					
	public void mCheckMode() {
		if(mFWUpdateState == FW_UPDATE_MODE_START)
			return;
		
		mCheckFWUpdate = true;
		
		try {
			mIMTX.setMCUListener(cServiceCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		new MUpgradeLoadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	public boolean isServiceRunningCheck() {
		ActivityManager manager = (ActivityManager) LauncherMainActivity.getInstance().getSystemService(Activity.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (NotifyDialog.class.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public byte[] mSetBuf(byte _cmd, byte _data) {
		return mSetBuf(_cmd, new byte[] { _data });
	}

	private int mcnt = 0;
	public byte[] mSetBuf(byte _cmd, byte[] _ar_data) {
		int mTotalLen = _ar_data.length + FD_PKT.SIZE_PACKET_HEADER;
		int _Len = _ar_data.length + FD_PKT.SIZE_PACKET_LEN + FD_PKT.SIZE_PACKET_CMD;

		byte[] _buf = new byte[mTotalLen];

		_buf[FD_PKT.IDX_SOP] = FD_PKT.DATA_SOP;
		_buf[FD_PKT.IDX_TYPE] = FD_PKT.DATA_TYPE;
		_buf[FD_PKT.IDX_LEN1] = (byte) PKTUtils.mGetLenH(_Len);
		_buf[FD_PKT.IDX_LEN2] = (byte) PKTUtils.mGetLenL(_Len);
		_buf[FD_PKT.IDX_CMD] = _cmd;

		System.arraycopy(_ar_data, 0, _buf, FD_PKT.IDX_DATA, _ar_data.length);

		byte[] _checkBuf = new byte[_Len];
		_checkBuf[FD_PKT.IDX_CS_LEN1] = (byte) _buf[FD_PKT.IDX_LEN1];
		_checkBuf[FD_PKT.IDX_CS_LEN2] = (byte) _buf[FD_PKT.IDX_LEN2];
		_checkBuf[FD_PKT.IDX_CS_CMD] = _buf[FD_PKT.IDX_CMD];

		System.arraycopy(_ar_data, 0, _checkBuf, (FD_PKT.SIZE_PACKET_LEN + FD_PKT.SIZE_PACKET_CMD), _ar_data.length);

		_buf[FD_PKT.IDX_LEN1 + _checkBuf.length] = (byte) ~(PKTUtils.mCheckSum(_checkBuf));
		_buf[FD_PKT.IDX_LEN1 + _checkBuf.length + FD_PKT.SIZE_PACKET_CS] = FD_PKT.DATA_EOP;

		if(mFWUpdateState == FW_UPDATE_MODE_START)
		{
			if(UpgradeDialog.mUpgradeDialog != null && UpgradeDialog.mUpgradeDialog.mProgressbar != null)
			{
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "===>>> FW Progress : " + (mcnt++) + ", max : " + UpgradeDialog.mUpgradeDialog.mProgressMax);
				UpgradeDialog.mUpgradeDialog.mProgressbar.setProgress(mcnt);
				mHandler.removeMessages(UPDATE_TIMEOUT);
				mHandler.sendEmptyMessageDelayed(UPDATE_TIMEOUT, 20*HP_Index.TIME_1_SECOND);
				
				if(mcnt == g_count)
				{
					try {
						LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_COMPLETE);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else if(mFWUpdateState == FW_UPDATE_MODE_STOP)
		{
			try {
				LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_FW);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		return _buf;
	}

	private class MUpgradeLoadAsyncTask extends AsyncTask<Void, Void, Boolean> {
		public MUpgradeLoadAsyncTask() {
		}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			return true;
		}

		@Override
		public void onPostExecute(Boolean _st) {
			if (_st) {
				new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_GET_STATE, FD_RQST.DATA_GET_STATE)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				new MUpgradeLoadAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			}
		}
	}

	static public class MUpgradeWriteAsyncTask extends AsyncTask<Void, Void, Void> {
		final byte[] mBuf;

		public MUpgradeWriteAsyncTask(byte[] _buf) {
			mBuf = _buf;
		}

		@Override
		protected Void doInBackground(Void... _void) {
			try {
				DEBUG_.toHexString("[send]", mBuf);
				mIMTX.sendData(mBuf);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public void onPostExecute(Void _void) {
			// 업데이트 진행중 팝업
			// if(mCmdState != -1) {
			// mNewPopupMsg.setText(R.string.info_q_file_copy);
			// mPopupDialogMSGMode();
			// mNewMessageDialog.show();
			// }
		}
	}

	private class MPopupUpTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... _void) {
			return null;
		}

		@Override
		public void onPostExecute(Void _void) {
			try {
				if(LauncherMainActivity.M_MTX.loadUpdateMode() == LauncherMainActivity.UPDATE_FULL_MODE)
				{
					try {
						LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_FW);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					startFWUpdate();
				}
				else
				{
					if(LauncherMainActivity.isServiceRunningCheck())
					{
						if(NotifyDialog.mCurrentPopup == NotifyDialog.FW_UPDATE_LOADING_POPUP)
						{
							startFWUpdate();
							return;
						}
					}
					LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE;
					LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.fw_upgrade_msg));
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}	
		}
	}

	/**
	 * 190731 yhr
	 * 복사된 파일 존재 여부 판단 후 업데이트 시작
	 * 복사된 파일이 없을 경우 SD Card의 파일로 업데이트 진행
	 */
	public static void startFWUpdate()
	{
		File mFW_FileDst = new File(FW_UPDATE_FILE_COPY_PATH);
		if(mFW_FileDst.isFile())
		{
			if(mFW_FileDst.length() > 0)
				HP_Manager.mUpdateFileFW = mFW_FileDst;
			else
			{
				try {
					// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
					if (LauncherMainActivity.M_MTX.readGPIO((byte) 170) == 0)
					{
						File mFW_FileSDCardDst = new File(FW_UPDATE_FILE_SDCARD_PATH);
						if(mFW_FileSDCardDst.isFile())
							HP_Manager.mUpdateFileFW = mFW_FileSDCardDst;
					}
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}	
		}
		else 
		{
			try {
				// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
				if (LauncherMainActivity.M_MTX.readGPIO((byte) 170) == 0)
				{
					File mFW_FileSDCardDst = new File(FW_UPDATE_FILE_SDCARD_PATH);
					if(mFW_FileSDCardDst.isFile())
						HP_Manager.mUpdateFileFW = mFW_FileSDCardDst;
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		System_fw_Update _fw = new System_fw_Update(HP_Manager.mUpdateFileFW);
		_fw.mStartUpdate();
	}
	
	private IMTXMCUCallback cServiceCallback = new IMTXMCUCallback.Stub() {
		boolean bSOP = false;
		boolean bLength = false;
		boolean bLength2 = false;
		int nLength = 0;
		int nType = 0;
		ArrayList<Byte> mPacket = new ArrayList<Byte>();

		@Override
		public void onReceive(byte[] arg0) throws RemoteException {
			byte[] recvData = arg0;
			DEBUG_.toHexString("[recv]", recvData);
			for (int i = 0; i < recvData.length; i++) {
				byte recv = recvData[i];
				if (!bSOP) {
					mPacket.clear();
					if (recv == (byte) MTXUartProtocol.SOP) {
						bSOP = true;
						bLength = false;
						bLength2 = false;
						nLength = nType = 0;
						mPacket.add(recv);
					}
				} else if (nType == 0) {
					switch (recv) {
					case MTXUartProtocol.TYPE_FW_UPGRADE:
						nType = recv;
						mPacket.add(recv);
						break;
					default:
						nType = nLength = 0;
						bLength = bLength2 = bSOP = false;
						break;
					}
				} else if (!bLength) {
					bLength = true;
					nLength = recv;
					mPacket.add(recv);
				} else if (!bLength2) {
					bLength2 = true;
					nLength = nLength * 256 + recv;
					mPacket.add(recv);
				} else {
					mPacket.add(recv);
					if (mPacket.size() - 4 >= nLength) {
						bSOP = false;
						if (mPacket.get(mPacket.size() - 1) == MTXUartProtocol.EOP) {

							if (mPacket.get(nLength + 2) == (byte) MTXUartProtocol.MakeCRC(mPacket, nLength)) {
								MPacket _m = new MPacket();

								Byte[] arr = mPacket.toArray(new Byte[0]);
								byte[] b2 = new byte[arr.length];
								for (int j = 0; j < arr.length; j++) {
									b2[j] = arr[j];
								}

								_m.mSetBuf(b2);
								Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mPKTCompleteMCU");
								mPKTCompleteMCU(_m);
							} else {
								Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "Packet Checksum Error");
							}
						} else {
							Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "Packet EOP Error");
						}
					}
				}
			}
		}
	};

	public static final int CMD_DEFAULT = 0, CMD_NONE = 1, CMD_READ = 2, ACK_COUNT = 3;
	public int _ack_count = ACK_COUNT;
	public byte g_index = 1;
	public int g_count = 0;
	public void mPKTCompleteMCU(MPacket _pkt) {
		switch (_pkt.mCmd) {
		case FD_RQST.CMD_GET_STATE: // DOWN_READ
			if (_pkt.mDataBuf[0] == FD_RSPS.MODE_DOWNLOAD) {
				_ack_count = ACK_COUNT;
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME +  "mStartUpdate 2 // mCheckFWUpdate : " + mCheckFWUpdate + ", mFWUpdateState : " + mFWUpdateState);
				if(mFWUpdateState == FW_UPDATE_MODE_START)
					mCheckFWUpdate = false;
				
				if (mCheckFWUpdate) {
					try {
						mIMTX.setMCUListener(null);
					} catch (RemoteException e) {
						e.printStackTrace();
					}

					mCheckFWUpdate = false;
					this.mStartUpdate();
					return;
				}

				new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_READY_SUCCESS, FD_RQST.DATA_READY_SUCCESS))
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else if (_pkt.mDataBuf[0] == FD_RSPS.MODE_NORMAL) {
				_ack_count = ACK_COUNT;
				if (mCheckFWUpdate) {
					try {
						mIMTX.setMCUListener(null);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					mCheckFWUpdate = false;
					
					if (LauncherMainActivity.getInstance().mOSUpgradeMode == false) {
						if(LauncherMainActivity.getInstance().mFWUpgradeMode)
							new MPopupUpTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
				}

				new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_READY, FD_RQST.DATA_READY))
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				if (_ack_count > 0) {
					_ack_count--;

					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_GET_STATE, FD_RQST.DATA_GET_STATE))
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT))
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;
		case FD_RQST.CMD_READY: // DOWN_READ
			if (_pkt.mDataBuf[0] == FD_RSPS.NON_ERROR) {
				LauncherMainActivity.getInstance().sendBroadcast(new Intent(Intent.ACTION_SYNC));
				_ack_count = ACK_COUNT;
				new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else if (_pkt.mDataBuf[0] == FD_RSPS.MODE_ERROR) {
				_ack_count = ACK_COUNT;
				new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_READY_SUCCESS, FD_RQST.DATA_READY_SUCCESS)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else {
				if (_ack_count > 0) {
					_ack_count--;
					
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_READY, FD_RQST.DATA_READY)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;

		case FD_RQST.CMD_READY_SUCCESS:
			if (_pkt.mDataBuf[0] == FD_RSPS.NON_ERROR) {
				_ack_count = ACK_COUNT;
				SendFileData(FD_RQST.CMD_INFO, true);
			} else {
				if (_ack_count > 0) {
					_ack_count--;

					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_READY_SUCCESS, FD_RQST.DATA_READY_SUCCESS)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;

		case FD_RQST.CMD_INFO:
			if (_pkt.mDataBuf[0] == FD_RSPS.NON_ERROR) {
				_ack_count = ACK_COUNT;

				SendFileData(FD_RQST.CMD_DATA, true);
			} else {
				if (_ack_count > 0) {
					_ack_count--;

					SendFileData(FD_RQST.CMD_INFO, false);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;

		case FD_RQST.CMD_DATA:
			if (_pkt.mDataBuf[0] == FD_RSPS.NON_ERROR) {
			} else {
				if (_ack_count > 0) {
					_ack_count--;

					SendFileData(FD_RQST.CMD_DATA, false);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT))
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;

		case FD_RQST.CMD_NEXT_DATA:
			if (_pkt.mDataBuf[0] == FD_RSPS.REQUEST_PKG) {
				_ack_count = ACK_COUNT;

				SendFileData(FD_RQST.CMD_DATA, true);
			} else {
				if (_ack_count > 0) {
					_ack_count--;

					SendFileData(FD_RQST.CMD_DATA, false);
				} else {
					new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT))
							.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				}
			}
			break;

		case FD_RQST.CMD_SUCCESS:
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "==========> CMD_SUCCESS ");
			LauncherMainActivity.getInstance().sendBroadcast(new Intent(Intent.ACTION_SYNC));

			try {
				file_input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			_ack_count = ACK_COUNT;
			UpgradeDialog.mUpgradeDialog.exitMCUUpdatePopup();
			new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_REBOOT, FD_RQST.DATA_REBOOT)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	public final int FILE_HEADER = 128 * 2;
	public FileInputStream file_input = null;
	private File _file = null;
	private File copy_file = null;
	private File sdcard_file = null;
	private byte[] _checkBuf = null;
	public final int PARSER_TIME = 100;
	public final int TIME_OUT = 1000;
	public final int READ_SIZE = 128;// 1024;
	public final int PKG_SIZE = 128;// 1024;

	private void SendFileData(int type, boolean _bRead) {
		if (type == FD_RQST.CMD_INFO) {

			String state = Environment.getExternalStorageState();
			if (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
				try {
					if (_file == null) {
						_checkBuf = new byte[READ_SIZE + 1];// FD_PKT.SIZE_CHECK_CRC_HEADER
															// + _len];
						
						copy_file = new File(FW_UPDATE_FILE_COPY_PATH);
						
						// 190731 yhr
						if(copy_file.isFile())
							_file = copy_file;
						else
						{
							try {
								// 0 : SD카드 삽입된 상태, 1 : SD카드 탈거된 상태
								if (LauncherMainActivity.M_MTX.readGPIO((byte) 170) == 0)
								{
									sdcard_file = new File(FW_UPDATE_FILE_SDCARD_PATH);
									if(sdcard_file.isFile())
										_file = sdcard_file;
								}
							} catch (RemoteException e) {
								e.printStackTrace();
							}
						}
						
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "====== FW UPDATE FILE PATH : " + _file.toString());
						
//						_file = new File(FILE_FULL_PATH);
						file_input = new FileInputStream(_file);
					}

					if (true) {
						if (_bRead) {
							file_input.read(_checkBuf, 0, READ_SIZE);
							file_input.skip(FILE_HEADER - READ_SIZE);
						}

						byte[] _size = PKTUtils.mIntTo4Byte((int) _file.length());
						byte[] _pkg = PKTUtils.mIntTo4Byte(PKG_SIZE);

						g_count = ((int) _file.length() - FILE_HEADER) / PKG_SIZE;

						if (((int) _file.length() - FILE_HEADER) % PKG_SIZE != 0)
							g_count++;
						byte[] _cnt = PKTUtils.mIntTo4Byte(g_count);

						byte[] _timeout = PKTUtils.mIntTo4Byte(TIME_OUT);

						new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_INFO,
								new byte[] { _size[0], _size[1], _size[2], _size[3], _pkg[0], _pkg[1], _pkg[2], _pkg[3],
										_cnt[0], _cnt[1], _cnt[2], _cnt[3], _timeout[0], _timeout[1], _timeout[2],
										_timeout[3], _checkBuf[3], _checkBuf[4], _checkBuf[5], _checkBuf[6] }))
												.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						
						if(UpgradeDialog.mUpgradeDialog != null)
						{
							UpgradeDialog.mUpgradeDialog.mProgressMax = g_count;
							Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "==========> mProgressMax : " + UpgradeDialog.mUpgradeDialog.mProgressMax);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "==========> CMD_DATA NON_ERROR ");
			try {
				if (true) {
					if (_bRead) {
						Arrays.fill(_checkBuf, FD_RQST.DATA_DEFAULT);
						file_input.read(_checkBuf, 1, READ_SIZE);
						_checkBuf[0] = g_index++;
					}

					if (true) {
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "==========> CMD_DATA step2 ");
						new MUpgradeWriteAsyncTask(mSetBuf(FD_RQST.CMD_DATA, _checkBuf)).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}