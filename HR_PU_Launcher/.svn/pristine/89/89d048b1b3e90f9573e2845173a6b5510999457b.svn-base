package com.mobilus.hp.setting.system;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 화면 조정에 대한 Fragment
 * @author yhr
 *
 */
public class System_Info extends Fragment {
	
	private static final String CLASS_NAME = "[System_Info ]  ";
	private TextView hiddenVersionMenu, txtOSVersion, txtFWVersion, txtCASID, txtCASBS, txtCASNO, txtDMBVersion;
	private LinearLayout llCasID, llCasBS, llDMB;
	
	private int mCntClick = 0;
	private String strDMBVersion = "";
	
	public System_Info()
	{
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_system_info, container, false);
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
		if(HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
   			return;
		
		if(HP_Manager.mRootMenu != HP_Index.ROOT_MENU_SYSTEM)
			return;
		
		if(HP_Manager.mSubMenu != HP_Index.SYSTEM_MENU_SYSTEM_INFO)
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
	private void setLoadView()
	{
		try {
			PackageInfo dmn_info = HP_Manager.mContext.getPackageManager().getPackageInfo(HP_Manager.mContext.getPackageName(), 0);
			strDMBVersion = " " + dmn_info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		hiddenVersionMenu = (TextView) HP_Manager.mContext.findViewById(R.id.txtOSVersion);
		hiddenVersionMenu.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent motion) {
				if(motion.getAction() == MotionEvent.ACTION_DOWN)
				{
					mCntClick++;
					if(mCntClick == 5)
					{
						llCasID.setVisibility(View.VISIBLE);
						llCasBS.setVisibility(View.VISIBLE);
						llDMB.setVisibility(View.VISIBLE);
						
						txtDMBVersion.setText(strDMBVersion);
					}
				}
				return false;
			}
		});
		
		txtOSVersion = (TextView) HP_Manager.mContext.findViewById(R.id.OSVersion);
		txtFWVersion = (TextView) HP_Manager.mContext.findViewById(R.id.FWVersion);
		txtCASID = (TextView) HP_Manager.mContext.findViewById(R.id.CASID);
		txtCASBS = (TextView) HP_Manager.mContext.findViewById(R.id.CASBS);
		txtCASNO = (TextView) HP_Manager.mContext.findViewById(R.id.CASNO);
		txtDMBVersion = (TextView) HP_Manager.mContext.findViewById(R.id.LauncherVer);
		
		llCasID = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llCasID);
		llCasBS = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llCasBs);
		llDMB = (LinearLayout) HP_Manager.mContext.findViewById(R.id.llDMBLauncher);
		
		llCasID.setVisibility(View.INVISIBLE);
		llCasBS.setVisibility(View.INVISIBLE);
		llDMB.setVisibility(View.INVISIBLE);
		
		txtOSVersion.setText(SystemProperties.get(HP_Manager.PROPERTIES_VER_OS, " 없음"));
		txtFWVersion.setText(SystemProperties.get(HP_Manager.PROPERTIES_VER_MCU, " 없음"));
		
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + " CAS NO : " + SystemProperties.get(HP_Manager.PROPERTIES_CAS_NO, " 없음"));
	
		if(SystemProperties.get(HP_Manager.PROPERTIES_CAS_NO, "없음").equals("ffffffff01"))
			txtCASNO.setText("");
		else
			txtCASNO.setText(SystemProperties.get(HP_Manager.PROPERTIES_CAS_NO, "").toUpperCase());
		
		txtCASID.setText("CAS ID : " + SystemProperties.get(HP_Manager.PROPERTIES_CAS_ID, " 없음"));
		txtCASBS.setText("CAS BS : " + SystemProperties.get(HP_Manager.PROPERTIES_CAS_BS, " 없음"));
		
		txtOSVersion.setSelected(true);
		txtFWVersion.setSelected(true);
		txtCASID.setSelected(true);
		txtCASBS.setSelected(true);
		txtCASNO.setSelected(true);
		llDMB.setSelected(true);
	}
}