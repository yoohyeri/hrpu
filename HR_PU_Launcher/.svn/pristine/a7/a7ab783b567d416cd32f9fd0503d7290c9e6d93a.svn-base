package com.mobilus.hp.setting;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mobilus.hp.launcher.FileCopy;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.launcher.FileCopy.onCopyCompletedListener;
import com.mobilus.hp.popup.NotifyDialog;
import com.mobilus.hp.setting.system.System_Update;
import com.mobilus.hp.setting.system.System_fw_Update;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.DxbView_Normal;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IQBService;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * Hidden List
 * 
 * @author yhr
 *
 */
public class Setting_Hidden_List extends Fragment {

	private static final String CLASS_NAME = "[Setting_Hidden_List ]  ";

	/**
	 * ListView
	 */
	private ListView lvHiddenList;

	/**
	 * Hidden List Title
	 */
	private String[] strHiddenMenuListTitle;

	/**
	 * Screen List
	 */
	public ArrayList<Setting_Menu> mHiddenList = new ArrayList<Setting_Menu>();

	/**
	 * Hidden List Adapter
	 */
	private static HiddenListAdapter mHiddenListAdapter;

	private int MANUFACTURING_PROCESS = 0;
	private int GPS_SETTING = 1;
	private int DMB_SIGNAL = 2;
	private int SYSTEM_UPDATE = 3;
	private int LOG_SAVE = 4;
//	private int REAR_CAM_REFRESH = 5;
	private int CAS_MODE = 5;
	private int EXTRACT_QB_IMG = 6;
	public static int SD_CARD_TEST = 7;
	private int FACTORY_RESET = 8;
	

	public Setting_Hidden_List() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment

		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_hidden_list, container, false);

		HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
		return inflater.inflate(R.layout.fragment_hidden_list, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (HP_Manager.mCurrentView != HP_Index.FRAGMENT_HIDDEN_MENU)
			return;

		mHiddenMenu = -1;
		HP_Manager.mProductionProcess = false;
		setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * GUI초기화
	 */
	private void setLoadView() {
		lvHiddenList = (ListView) HP_Manager.mContext.findViewById(R.id.menu_list);

		// 히든메뉴 리스트 항목
		if(LauncherMainActivity.mFactoryReset == false)
			strHiddenMenuListTitle = HP_Manager.mContext.getResources().getStringArray(R.array.hidden_menu_list);
		else
			strHiddenMenuListTitle = HP_Manager.mContext.getResources().getStringArray(R.array.hidden_menu_list_2);

		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			mHiddenListAdapter = new HiddenListAdapter(HP_Manager.mContext, R.layout._kia_layout_hidden_list_item, mHiddenList);
		else
			mHiddenListAdapter = new HiddenListAdapter(HP_Manager.mContext, R.layout.layout_hidden_list_item, mHiddenList);

		if (mHiddenListAdapter != null) {
			lvHiddenList.setAdapter(mHiddenListAdapter);

			mHiddenList.clear();
			for (int i = 0; i < strHiddenMenuListTitle.length; i++) {
				Setting_Menu strTitle = new Setting_Menu();
				strTitle.idx = i;

				if(i == DMB_SIGNAL)
				{
					if(HP_Manager.mShowDMBSignal)
						strTitle.title = strHiddenMenuListTitle[i] + " ON";
					else
						strTitle.title = strHiddenMenuListTitle[i] + " OFF";
				}
//				else if(i == REAR_CAM_REFRESH)		//20191219 yhr
//				{
//					if(HP_Manager.mRearCamRefresh == HP_Index.REAR_CAM_REFRESH_ON)
//						strTitle.title = strHiddenMenuListTitle[i] + " ON";
//					else
//						strTitle.title = strHiddenMenuListTitle[i] + " OFF";
//				}
				else
					strTitle.title = strHiddenMenuListTitle[i];

				mHiddenList.add(strTitle);
			}
			mHiddenListAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * List Click Event
	 * 
	 * @author yhr
	 */
	public static int mHiddenMenu;
	private class HiddenListAdapter extends Setting_MenuListAdapter {

		public HiddenListAdapter(Context context, int resource, List<Setting_Menu> objects) {
			super(context, resource, objects);
		}

		@Override
		public void mClickEvent(int _id) {
			mHiddenMenu = _id;
			if (_id == LOG_SAVE) {
				NotifyDialog.mCurrentPopup = NotifyDialog.LOG_SAVE_POPUP;

				// 로딩 팝업 표출
				Intent popup = null;
				popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
				LauncherMainActivity.getInstance().startService(popup);

				new MLogSaveAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			} else if (_id == MANUFACTURING_PROCESS) {
				HP_Manager.mProductionProcess = true;
				HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;

				// 생산공정 프로그램 실행
				Intent _intent = HP_Manager.mContext.getPackageManager().getLaunchIntentForPackage("com.mobilusauto.app.productionprocess");
				if (_intent != null) {

					//190909 yhr
					DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_SET_CHANNEL);
					DxbPlayer.mHandler.removeMessages(DxbPlayer.MSG_VIDEO_OUT_TIMEOUT);

					SystemProperties.set(LauncherMainActivity.PROPERTIES_ILLUMINATION, "auto");
					HP_Manager.mContext.startActivity(_intent);
					DxbView.closeVideoOut();
					DxbView.closeAudioOut();
				}
			}else if (_id == SD_CARD_TEST) {
				Intent intent = new Intent(HP_Manager.mContext, NandTestActivity.class);
				startActivity(intent);
			} 
//			else if(_id == REAR_CAM_REFRESH)    //20191219 yhr --> 금산 후방카메라 복구모드 On/Off 설정
//			{
//				try {
//					if(HP_Manager.mRearCamRefresh == HP_Index.REAR_CAM_REFRESH_ON)
//					{
//						HP_Manager.mRearCamRefresh = HP_Index.REAR_CAM_REFRESH_OFF;
//						mHiddenList.get(REAR_CAM_REFRESH).title = strHiddenMenuListTitle[REAR_CAM_REFRESH] + " OFF";
//						LauncherMainActivity.M_MTX.saveRecoverReverseType(HP_Index.REAR_CAM_REFRESH_OFF);
//						
//					}
//					else
//					{
//						HP_Manager.mRearCamRefresh = HP_Index.REAR_CAM_REFRESH_ON;
//						mHiddenList.get(REAR_CAM_REFRESH).title = strHiddenMenuListTitle[REAR_CAM_REFRESH] + " ON";
//						LauncherMainActivity.M_MTX.saveRecoverReverseType(HP_Index.REAR_CAM_REFRESH_ON);
//					}
//					Log.d(HP_Manager.TAG_SETTING, "Hidden >>>> mRearCamRefresh : " + LauncherMainActivity.M_MTX.loadRecoverReverseType());
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
			else if (_id == FACTORY_RESET) {
				try {
					LauncherMainActivity.M_MTX.setParkingLine(0);
					LauncherMainActivity.M_MTX.saveParkingLine(0);
				} catch (RemoteException e) {
					e.printStackTrace();
				}

				LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_FACTORY_RESET;
				LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_TWO_BUTTON, HP_Manager.mContext.getResources().getString(R.string.factory_reset_message));

			} 
			else if(_id == GPS_SETTING) {
				Intent intent = new Intent(HP_Manager.mContext, GPSSettingsActivity.class);
				startActivity(intent);
			}
			else if(_id == EXTRACT_QB_IMG) {
				new Thread() {
					@Override
					public void run() {
						Looper.prepare();

						final IQBService qb = IQBService.Stub.asInterface(ServiceManager.checkService("quickboot"));
						if(qb != null) {
							qbExctractCMD("sdcard");

							PowerManager pm = (PowerManager)HP_Manager.mContext.getSystemService(Context.POWER_SERVICE);
							pm.reboot("recovery");
						}
					}
				}.start();
			}
			else if(_id == DMB_SIGNAL)
			{
				if(HP_Manager.mShowDMBSignal)
				{
					HP_Manager.mShowDMBSignal = false;
					LauncherMainActivity.tvStrength.setVisibility(View.GONE);
				}
				else
				{
					HP_Manager.mShowDMBSignal = true;
					LauncherMainActivity.tvStrength.setVisibility(View.VISIBLE);
				}

				//190903 yhr
				if(HP_Manager.mShowDMBSignal)
					mHiddenList.get(DMB_SIGNAL).title = strHiddenMenuListTitle[DMB_SIGNAL] + " ON";
				else
					mHiddenList.get(DMB_SIGNAL).title = strHiddenMenuListTitle[DMB_SIGNAL] + " OFF";

			}
			else if(_id == SYSTEM_UPDATE)
			{
				LauncherMainActivity.getInstance().mHandlerSDCheck.removeCallbacks(LauncherMainActivity.getInstance().mRunnableUnmountCheck);

				if(LauncherMainActivity.getInstance().mExistFWUpgradeFile)
					LauncherMainActivity.getInstance().mFWUpgradeMode = true;

				if(LauncherMainActivity.getInstance().mExistOSUpgradeFile)
					LauncherMainActivity.getInstance().mOSUpgradeMode = true;

				// 190710 강제 업데이트 시 우선순위 : OS
				if(LauncherMainActivity.getInstance().mOSUpgradeMode && LauncherMainActivity.getInstance().mFWUpgradeMode)
					LauncherMainActivity.getInstance().mFWUpgradeMode = false;

				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mUpdateFile : " + HP_Manager.mUpdateFile + ", mExistOSUpgradeFile : " + LauncherMainActivity.getInstance().mOSUpgradeMode
						+ ", mUpdateFileFW : " + HP_Manager.mUpdateFileFW + ", mFWUpgradeMode : " + LauncherMainActivity.getInstance().mFWUpgradeMode);
				if (LauncherMainActivity.getInstance().mOSUpgradeMode && HP_Manager.mUpdateFile != null) {
					DxbPlayer.mPlayer.stop();
					mHandler.sendEmptyMessageDelayed(MSG_START_UPDATE, 500);

					if(LauncherMainActivity.getInstance().mFWUpgradeMode == false)
					{
						try {
//							Log.d(HP_Manager.TAG_LAUNCHER, "saveUpdateMode(UPDATE_MODE_OS) 2");
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_OS);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}

					try {
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== OS UPDATE ============ loadUpdateMode : " + LauncherMainActivity.M_MTX.loadUpdateMode());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				} else if (LauncherMainActivity.getInstance().mFWUpgradeMode && HP_Manager.mUpdateFileFW != null) {

					DxbPlayer.mPlayer.stop();
					mHandler.sendEmptyMessageDelayed(MSG_START_MUC_UPDATE, 500);

					try {
						LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_FW);
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				else
				{
					Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== FW Update File is null...!!! // " + HP_Manager.mUpdateFileFW);
					LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE_COPY_ERROR;
					LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_3));

				}
			}
			else if(_id == CAS_MODE)
			{
				SystemProperties.set(LauncherMainActivity.PROPERTIES_CAS, "true");
				
				//200226 
				if(HP_Manager.mDMBVideoOnOff == HP_Index.DMB_VIDEO_OFF)
				{
					HP_Manager.mDMBVideoOnOff = HP_Index.DMB_VIDEO_ON;
					HP_Manager.mPreferences.mSavePreferences(HP_Manager.mPreferences.MODE_DMB_ON_OFF);
					
					if(DxbView_Normal.mfromUserMute == false)
						DxbPlayer.setAudioOnOff(DxbPlayer._ON_);
				}
				
				HP_Manager.mCallback.onChangeDMBView();

				Log.d(HP_Manager.TAG_SETTING, "===== CAS MODE === " + SystemProperties.get(LauncherMainActivity.PROPERTIES_CAS));
			}

		}
	}

	private File mUpdateFile;
	private File mSrcFile;

	private static String model = "hr_pu";
	private final static String file = "_ota_package.zip"; 
	private final static String file_release = "_ota_package.zip"; 
	private final static String file_once = "_ota_package_1.zip"; 
	private final static String file_clear = "_recovery_package.zip";  

	private static final int MSG_START_UPDATE = 0;
	private static final int MSG_START_MUC_UPDATE = 1;
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_UPDATE:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "MSG_START_UPDATE");
				removeMessages(MSG_START_UPDATE);

				File mFileDst = new File(System_Update.SYSTEM_UPDATE_COPY_PATH);
				mUpdateFile = mFileDst;
				mSrcFile = HP_Manager.mUpdateFile;
				
				FileCopy copy = new FileCopy(HP_Manager.mContext, mSrcFile, mFileDst, HP_Manager.mContext.getString(R.string.update_file_copy_msg));
				FileCopy.onCopyCompletedListener listener = new onCopyCompletedListener() {

					@Override
					public void onCopySuccess() {
						// TODO Auto-generated method stub
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "upgrade // onCopySuccess // " + mSrcFile.getName() + ", getNameFile_Once : " +getNameFile_Once());
						if(mSrcFile.getName().equals(getNameFile_Once())) {
							mSrcFile.delete();
						}

						// 190926
						Log.d(HP_Manager.TAG_SETTING,  CLASS_NAME + "strNewOSVersion : " + HP_Manager.strNewOSVersion);
						if(HP_Manager.strNewOSVersion.contains("1.0.0"))
						{
							try {
								Log.d(HP_Manager.TAG_SETTING,  CLASS_NAME + "loadGPSReset : " + LauncherMainActivity.M_MTX.loadGPSReset());
								if(LauncherMainActivity.M_MTX.loadGPSReset() == 1)
								{
									Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "3 ================>> saveGPSReset(0)");
									LauncherMainActivity.M_MTX.saveGPSReset(0);
								}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchMethodError e) {
								// TODO: handle exception
							}
						}
						

						try {
							if(LauncherMainActivity.getInstance().mFWUpgradeMode && LauncherMainActivity.getInstance().mOSUpgradeMode)
							{
								LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_FULL_MODE);
							}
							else
							{
								if (LauncherMainActivity.getInstance().mOSUpgradeMode && HP_Manager.mUpdateFile != null) {
									if(LauncherMainActivity.getInstance().mFWUpgradeMode == false)
									{
										Log.d(HP_Manager.TAG_LAUNCHER, "saveUpdateMode(UPDATE_MODE_OS) 3 ");
										LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_OS);
										Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== OS UPDATE ============ loadUpdateMode : " + LauncherMainActivity.M_MTX.loadUpdateMode());
									}
								}
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchMethodError e) {
							// TODO: handle exception
						}

						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								try {
									Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "installPackage 2 : " + mUpdateFile.toString());
									RecoverySystem.installPackage(HP_Manager.mContext, mUpdateFile);
								} catch (IOException e) {
									e.printStackTrace();
								}	    		

							}
						}, 100);
					}

					@Override
					public void onCopyFail() {
						// TODO Auto-generated method stub
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "upgrade // file copy error!!" + LauncherMainActivity.isServiceRunningCheck());
						try {
							Log.d(HP_Manager.TAG_POPUP, "saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE) 2");
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if(LauncherMainActivity.isServiceRunningCheck())
							NotifyDialog.mNotifyDialog.exitNotifyPopup();

						LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE_COPY_ERROR;
						LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_3));
					}
				};

				copy.setOnCopyCompletedListener(listener);
				copy.mThreadRun();

				break;
			case MSG_START_MUC_UPDATE:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "MSG_START_MUC_UPDATE");
				removeMessages(MSG_START_UPDATE);

				File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
				mUpdateFile = mFW_FileDst;
				mSrcFile = HP_Manager.mUpdateFileFW;

				FileCopy mFW_copy = new FileCopy(HP_Manager.mContext, mSrcFile, mFW_FileDst, HP_Manager.mContext.getString(R.string.update_file_copy_msg));
				FileCopy.onCopyCompletedListener FW_listener = new onCopyCompletedListener() {

					@Override
					public void onCopySuccess() {
						// TODO Auto-generated method stub
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "MSG_START_MUC_UPDATE upgrade // onCopySuccess // " + mSrcFile.getName() + ", getNameFile_Once : " +getNameFile_Once());
						if(mSrcFile.getName().equals(getNameFile_Once())) {
							mSrcFile.delete();
						}

						try {
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_FW);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_REBOOTING_NOTI_POPUP;
						Intent popup = null;
						popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
						LauncherMainActivity.getInstance().startService(popup);

						Handler mHandler = new Handler();
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								Log.d(HP_Manager.TAG_LAUNCHER, "startFWUpdate 2");
								System_fw_Update.startFWUpdate();
								//								System_fw_Update _fw = new System_fw_Update(mUpdateFile);
								//								_fw.mStartUpdate();

							}
						}, 3000);
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== FW UPDATE ======================================== // " + HP_Manager.mUpdateFileFW);
					}

					@Override
					public void onCopyFail() {
						// TODO Auto-generated method stub
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "upgrade // file copy error!!");

						try {
							Log.d(HP_Manager.TAG_POPUP, "saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE) 3");
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						if(LauncherMainActivity.isServiceRunningCheck())
							NotifyDialog.mNotifyDialog.exitNotifyPopup();

						LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE_COPY_ERROR;
						LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_3));
					}
				};

				mFW_copy.setOnCopyCompletedListener(FW_listener);
				mFW_copy.mThreadRun();

				break;

			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	public static String getNameFile() {
		return model+file;
	}

	public static String getNameFile_Release() {
		return model+file_release;
	}

	public static String getNameFile_Once() {
		return model+file_once;
	}

	public static String getNameFile_Clear() {
		return model+file_clear;
	}


	private int qbExctractCMD(String target) {
		File cmdDir = new File("/cache/recovery/");
		if (cmdDir == null) {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "Failed to create instance /cache/recovery/ dir file.");
			return -1;
		} else if (!cmdDir.exists()) {
			cmdDir.mkdirs();
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "/cache/recovery/ is created.");
		} else {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "/cache/recovery/ is already exist.");
		}

		File cmdFile = new File("/cache/recovery/command");
		if (cmdFile == null) {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "Failed to create instance /cache/recovery/command file.");
			return -2;
		} else if (!cmdFile.exists()) {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "/cache/recovery/command is not exist.");
		} else {
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "/cache/recovery/command is already exist. remove it.");
			cmdFile.delete();
		}

		int ret = 0;
		String qbExtractCmd = "--extract_qbimg=/" + target + "\n--locale=en_US";
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "QB Extract Command [" + qbExtractCmd + "]");
		FileOutputStream cmdFos;
		try {
			cmdFos = new FileOutputStream(cmdFile);
			try {
				cmdFos.write(qbExtractCmd.getBytes());
				cmdFos.flush();
				cmdFos.close();
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "Complete to write QB Extract Cmd[" + qbExtractCmd + "]");
			} catch (IOException e) {
				ret = -3;
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "Failed to write /cache/recovery/command file.");
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			ret = -4;
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "Failed to find /cache/recovery/command file.");
			e.printStackTrace();
		}

		return ret;
	}

	private class MLogSaveAsyncTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... arg0) {
			return DEBUG_.createLogFile();
		}

		@Override
		public void onPostExecute(Boolean _st) {
			Intent popup = null;
			popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
			LauncherMainActivity.getInstance().stopService(popup);

			if (_st) {
				LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,
						HP_Manager.mContext.getResources().getString(R.string.info_success_log));
			} else {
				LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,
						HP_Manager.mContext.getResources().getString(R.string.info_fail_log));
			}
		}
	}
}