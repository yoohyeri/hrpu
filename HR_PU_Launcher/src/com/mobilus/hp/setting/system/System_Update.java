package com.mobilus.hp.setting.system;

import java.io.File;
import java.io.IOException;

import com.mobilus.hp.launcher.FileCopy;
import com.mobilus.hp.launcher.FileCopy.onCopyCompletedListener;
import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.popup.NotifyDialog;
import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 화면 조정에 대한 Fragment
 * 
 * @author yhr
 *
 */
public class System_Update extends Fragment implements View.OnClickListener {

	private static final String CLASS_NAME = "[System_Update ]  ";

	public static final int MSG_START_SYSTEM_UPDATE = 0;
	private static final int MSG_START_MUC_UPDATE = 1;

	private LinearLayout llNewVersion, llNewFullVersion, llCurrentVersion, llCurrentFullVersion;
	private LinearLayout llFullUpdateBtnLayout, llUpdateBtnLayout;
	private LinearLayout llFullUpdateVersion, llUpdateVersion;
	private TextView txtCurrentVersion, txtFullCurrentVersion;

	private TextView txtNewVersion, txtFullNewVersion;
	private TextView msgUpdate, btnUpdate, btnFullUpdate;

	public System_Update() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_system_update, container, false);
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
		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;

		if (HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SYSTEM)
			return;

		if (HP_Manager.mSubMenu != HP_Index.SYSTEM_MENU_UPDATE)
			return;

		setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * GUI 초기화
	 */
	private void setLoadView() {
		llFullUpdateVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.systemfullversionlayout);
		llUpdateVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.systemversionlayout);
		
		llNewVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llNewVer);
		llNewFullVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llfullNewVer);
		
		llCurrentVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llCurrentVer);
		llCurrentFullVersion = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llfullCurrentVer);
		
		txtCurrentVersion = (TextView) HP_Manager.mContext.findViewById(R.id.currentVersion);
		txtFullCurrentVersion = (TextView) HP_Manager.mContext.findViewById(R.id.currentfullVersion);
		
		txtNewVersion = (TextView) HP_Manager.mContext.findViewById(R.id.newVersion);
		txtFullNewVersion = (TextView) HP_Manager.mContext.findViewById(R.id.newfullVersion);

		msgUpdate = (TextView) HP_Manager.mContext.findViewById(R.id.UpdateRequestMsg);

		llFullUpdateBtnLayout = (LinearLayout) HP_Manager.mContext.findViewById(R.id.fullupdateBtnLayout);
		llUpdateBtnLayout = (LinearLayout) HP_Manager.mContext.findViewById(R.id.updateBtnLayout);
		
		btnUpdate = (TextView) HP_Manager.mContext.findViewById(R.id.btnUpdate);
		btnFullUpdate = (TextView) HP_Manager.mContext.findViewById(R.id.fullbtnUpdate);
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
		{
			btnUpdate.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_2));
			btnFullUpdate.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable.btn_2));
		}
		else
		{
			btnUpdate.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_2));
			btnFullUpdate.setBackground(HP_Manager.mContext.getResources().getDrawable(R.drawable._kia_btn_2));
		}
		btnUpdate.setOnClickListener(this);
		btnFullUpdate.setOnClickListener(this);
		changeUpdateMode();
	}

	/**
	 * 최신버전일 경우 문구 변경
	 */
	public void changeUpdateMode() {
		if (LauncherMainActivity.getInstance() == null)
			return;

		if (llCurrentVersion == null)
			return;

		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "1 changeUpdateMode // mOSUpgradeMode : " + LauncherMainActivity.getInstance().mOSUpgradeMode 
											+ ", mFWUpgradeMode : "+ LauncherMainActivity.getInstance().mFWUpgradeMode 
											+ ", mExistFWUpgradeFile : " + LauncherMainActivity.getInstance().mExistFWUpgradeFile 
											+ ", mExistOSUpgradeFile : " + LauncherMainActivity.getInstance().mExistOSUpgradeFile);
		
		if(LauncherMainActivity.getInstance().mOSUpgradeMode && LauncherMainActivity.getInstance().mFWUpgradeMode)
		{
			String strCurrentFullVersion = " (시스템) " + SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, "없음") 
											+ "\n (펌웨어) " + SystemProperties.get(HP_Manager.PROPERTIES_VER_MCU, "없음");
			llFullUpdateVersion.setVisibility(View.VISIBLE);
			llUpdateVersion.setVisibility(View.GONE);
			llFullUpdateBtnLayout.setVisibility(View.VISIBLE);
			llUpdateBtnLayout.setVisibility(View.GONE);
			
			txtFullCurrentVersion.setText(strCurrentFullVersion);
			txtFullNewVersion.setText(" (시스템) " + HP_Manager.strNewOSVersion + "\n (펌웨어) " + HP_Manager.strNewFWVersion);
			llCurrentFullVersion.setVisibility(View.VISIBLE);
			llNewFullVersion.setVisibility(View.VISIBLE);
			msgUpdate.setText(HP_Manager.mContext.getString(R.string.update_msg));
			btnUpdate.setAlpha((float) 1);
			btnUpdate.setEnabled(true);
		}
		else
		{
			llFullUpdateVersion.setVisibility(View.GONE);
			llUpdateVersion.setVisibility(View.VISIBLE);
			llFullUpdateBtnLayout.setVisibility(View.GONE);
			llUpdateBtnLayout.setVisibility(View.VISIBLE);
			
			if (LauncherMainActivity.getInstance().mOSUpgradeMode) {
				txtCurrentVersion.setText(" " + SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, "없음"));
				txtNewVersion.setText(" " + HP_Manager.strNewOSVersion);
				llCurrentVersion.setVisibility(View.VISIBLE);
				llNewVersion.setVisibility(View.VISIBLE);
				msgUpdate.setText(HP_Manager.mContext.getString(R.string.update_msg));
				btnUpdate.setAlpha((float) 1);
				btnUpdate.setEnabled(true);
			} else if (LauncherMainActivity.getInstance().mFWUpgradeMode) {
				txtCurrentVersion.setText(" " + SystemProperties.get(HP_Manager.PROPERTIES_VER_MCU, "없음"));
				txtNewVersion.setText(" " + HP_Manager.strNewFWVersion);
				llCurrentVersion.setVisibility(View.VISIBLE);
				llNewVersion.setVisibility(View.VISIBLE);
				msgUpdate.setText(HP_Manager.mContext.getString(R.string.update_msg));
				btnUpdate.setAlpha((float) 1);
				btnUpdate.setEnabled(true);
			} else {
				
				llCurrentVersion.setVisibility(View.GONE);
				llNewVersion.setVisibility(View.GONE);
				msgUpdate.setText(HP_Manager.mContext.getString(R.string.no_update_msg));
				btnUpdate.setAlpha((float) 0.5);
				btnUpdate.setEnabled(false);
			}
		}
	}

	@Override
	public void onClick(View view) {
//		int value = 0;
		int id = view.getId();

		switch (id) {
		case R.id.fullbtnUpdate:
		case R.id.btnUpdate:
			if (HP_Manager.mContext == null)
				break;

			LauncherMainActivity.getInstance().mHandlerSDCheck.removeCallbacks(LauncherMainActivity.getInstance().mRunnableUnmountCheck);
			
			Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "mUpdateFile : " + HP_Manager.mUpdateFile 
												+ ", mOSUpgradeMode : " + LauncherMainActivity.getInstance().mOSUpgradeMode
												+ ", mUpdateFileFW : " + HP_Manager.mUpdateFileFW 
												+ ", mFWUpgradeMode : " + LauncherMainActivity.getInstance().mFWUpgradeMode);

			if(LauncherMainActivity.getInstance().mFWUpgradeMode && LauncherMainActivity.getInstance().mOSUpgradeMode)
			{
				DxbPlayer.mPlayer.stop();
				mHandler.sendEmptyMessageDelayed(MSG_START_SYSTEM_UPDATE, 500);
			}
			else
			{
				if (LauncherMainActivity.getInstance().mOSUpgradeMode && HP_Manager.mUpdateFile != null) {
					DxbPlayer.mPlayer.stop();
					mHandler.sendEmptyMessageDelayed(MSG_START_SYSTEM_UPDATE, 500);
				} else if (LauncherMainActivity.getInstance().mFWUpgradeMode && HP_Manager.mUpdateFileFW != null) {
					if(LauncherMainActivity.getInstance().mOSUpgradeMode == false)
					{
						DxbPlayer.mPlayer.stop();
						mHandler.sendEmptyMessageDelayed(MSG_START_MUC_UPDATE, 500);
					}
				}
			}
			break;
		default:
			break;
		}
	}
	
	private File mSystemUpdateFile,mFWUpdateFile;
	private File mSrcFile;
	
	public static String SYSTEM_UPDATE_COPY_PATH = "/storage/sdcard0/hr_pu_ota_package.zip";
	public static String SYSTEM_UPDATE_SDCARD_PATH = "/storage/sdcard1/hr_pu_ota_package.zip";
	
	private static String model = "hr_pu";
    private final static String file = "_ota_package.zip"; 
	private final static String file_release = "_ota_package.zip"; 
    private final static String file_once = "_ota_package_1.zip"; 
    private final static String file_clear = "_recovery_package.zip";  
	public Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_START_SYSTEM_UPDATE:
				removeMessages(MSG_START_SYSTEM_UPDATE);
				
				File mFileDst = new File(SYSTEM_UPDATE_COPY_PATH);
				mSystemUpdateFile = mFileDst;
				mSrcFile = HP_Manager.mUpdateFile;
				
				FileCopy copy = new FileCopy(HP_Manager.mContext, mSrcFile, mFileDst, HP_Manager.mContext.getString(R.string.update_file_copy_msg));
				FileCopy.onCopyCompletedListener listener = new onCopyCompletedListener() {
					
					@Override
					public void onCopySuccess() {
//						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "System Upgrade - onCopySuccess // mSrcFile.getName : " + mSrcFile.getName() 
//															+ ", getNameFile_Once : " +getNameFile_Once());
						if(mSrcFile.getName().equals(getNameFile_Once())) {
							mSrcFile.delete();
						}
						
						// 190926
						try {
							Log.d(HP_Manager.TAG_SETTING,  CLASS_NAME + "HP_Manager.strNewOSVersion : " + HP_Manager.strNewOSVersion
												+ "loadGPSReset : " + LauncherMainActivity.M_MTX.loadGPSReset());
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (NoSuchMethodError e) {
							// TODO: handle exception
						}
						
						if(HP_Manager.strNewOSVersion.contains("1.0.0"))
						{
							try {
								Log.d(HP_Manager.TAG_SETTING,  CLASS_NAME + "loadGPSReset : " + LauncherMainActivity.M_MTX.loadGPSReset());
								if(LauncherMainActivity.M_MTX.loadGPSReset() == 1)
								{
									LauncherMainActivity.M_MTX.saveGPSReset(0);
									Log.d(HP_Manager.TAG_LAUNCHER, CLASS_NAME + "2 ================>> saveGPSReset(0)");
								}
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (NoSuchMethodError e) {
								// TODO: handle exception
							}
						}
						
						
						try {
							// OS/FW 모두 업데이트 해야하는 경우
							if(LauncherMainActivity.getInstance().mFWUpgradeMode && LauncherMainActivity.getInstance().mOSUpgradeMode)
							{
								File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
								mFWUpdateFile = mFW_FileDst;
								mSrcFile = HP_Manager.mUpdateFileFW;
//								Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "upgrade - onCopySuccess // mSrcFile.getName() : " + mSrcFile.getName() 
//																	+ ", mUpdateFile.toString() : " +mFWUpdateFile.toString() 
//																	+ ", mSrcFile.toString() : " + mSrcFile.toString());
								
								// OS 복사 완료 후 FW파일도 복사
								FileCopy mFW_copy = new FileCopy(HP_Manager.mContext, mSrcFile, mFW_FileDst, HP_Manager.mContext.getString(R.string.update_file_copy_msg));
								FileCopy.onCopyCompletedListener fw_listener = new onCopyCompletedListener() {

									@Override
									public void onCopySuccess() {
										// FW 파일 복사완료 되면 OS업데이트 시작
										try {
											LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_FULL_MODE);
										} catch (RemoteException e) {
											e.printStackTrace();
										}
										
										mHandler.postDelayed(new Runnable() {

											@Override
											public void run() {
												
												try {
//													Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "installPackage 1 /  mSystemUpdateFile.toString() : " + mSystemUpdateFile.toString());
													RecoverySystem.installPackage(HP_Manager.mContext, mSystemUpdateFile);
												} catch (IOException e) {
													e.printStackTrace();
												}	 
											}
										}, 100);
									}

									@Override
									public void onCopyFail() {
										
									}
								};
								mFW_copy.setOnCopyCompletedListener(fw_listener);
								mFW_copy.mFWThreadRun();
								
								return;
							}
							else
							{
								if (LauncherMainActivity.getInstance().mOSUpgradeMode && HP_Manager.mUpdateFile != null) {
									if(LauncherMainActivity.getInstance().mFWUpgradeMode == false)
									{
										Log.d(HP_Manager.TAG_LAUNCHER, "saveUpdateMode(UPDATE_MODE_OS) 1 ");
										LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_OS);
										Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== OS UPDATE ============ loadUpdateMode : " + LauncherMainActivity.M_MTX.loadUpdateMode());
									}
								}
							}
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								try {
//									Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "installPackage 2 - mSystemUpdateFile.toString() : " + mSystemUpdateFile.toString());
									RecoverySystem.installPackage(HP_Manager.mContext, mSystemUpdateFile);	  
								} catch (IOException e) {
									e.printStackTrace();
								}	    		
							}
						}, 100);
					}
					
					@Override
					public void onCopyFail() {
						Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "onCopyFail // file copy error!!");
						
						try {
							Log.d(HP_Manager.TAG_POPUP, "saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE) 4");
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						
						if(LauncherMainActivity.isServiceRunningCheck())
							NotifyDialog.mNotifyDialog.exitNotifyPopup();
						
						LauncherMainActivity.getInstance().deleteCacheFile();
						LauncherMainActivity.getInstance().mDefaultDialog.mDialogMode = LauncherMainActivity.getInstance().mDefaultDialog.DIALOG_MODE_UPGRADE_COPY_ERROR;
						LauncherMainActivity.getInstance().mDefaultDialog.showDefaultDialog(HP_Index.DEFAULT_POPUP_ONE_BUTTON,	HP_Manager.mContext.getResources().getString(R.string.update_fail_msg_error_code_3));
					}
				};
				
				copy.setOnCopyCompletedListener(listener);
		        copy.mThreadRun();

				break;
			case MSG_START_MUC_UPDATE:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "=============>>> MSG_START_MUC_UPDATE ");
				removeMessages(MSG_START_MUC_UPDATE);
				
				File mFW_FileDst = new File(System_fw_Update.FW_UPDATE_FILE_COPY_PATH);
				mFWUpdateFile = mFW_FileDst;
				mSrcFile = HP_Manager.mUpdateFileFW;
				
				FileCopy mFW_copy = new FileCopy(HP_Manager.mContext, mSrcFile, mFW_FileDst, HP_Manager.mContext.getString(R.string.update_file_copy_msg));
				FileCopy.onCopyCompletedListener FW_listener = new onCopyCompletedListener() {
					
					@Override
					public void onCopySuccess() {
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "upgrade - onCopySuccess // mSrcFile.getName() : " + mSrcFile.getName() 
															+ ", getNameFile_Once : " +getNameFile_Once());
						if(mSrcFile.getName().equals(getNameFile_Once())) {
							mSrcFile.delete();
						}
						
						try {
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_FW);
						} catch (RemoteException e) {
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
								
//								Log.d(HP_Manager.TAG_SETTING, CLASS_NAME +  "System_fw_Update 3 // " + mFWUpdateFile.toString());
								System_fw_Update _fw = new System_fw_Update(mFWUpdateFile);
								_fw.mStartUpdate();
								
							}
						}, 3000);
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "================================== FW UPDATE ======================================== // " + HP_Manager.mUpdateFileFW);
					}
				
					@Override
					public void onCopyFail() {
						Log.e(HP_Manager.TAG_SETTING, CLASS_NAME + "onCopyFail // file copy error!!");
						
						try {
							Log.d(HP_Manager.TAG_POPUP, "saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE) 5");
							LauncherMainActivity.M_MTX.saveUpdateMode(LauncherMainActivity.UPDATE_MODE_NONE);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
						
						if(LauncherMainActivity.isServiceRunningCheck())
							NotifyDialog.mNotifyDialog.exitNotifyPopup();
						
						LauncherMainActivity.getInstance().deleteCacheFile();
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
}