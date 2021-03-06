package com.mobilus.hp.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import com.mobilus.hp.launcher.HP_Manager;
import com.mobilus.hp.popup.NotifyDialog;
import com.telechips.android.tdmb.LauncherMainActivity;
import com.telechips.android.tdmb.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;

public class GPSSettingsActivity extends Activity implements OnClickListener, LocationListener, OnTouchListener{
	private Button mResultOK;
	
	//20200602 yhr - Speed, Reverse State
	public TextView txtSpeed, txtReverseState, txtCalibGyroz, txtCalibSingleTick, txtCalibAccelx;
	
	//20200608 yhr - GPS Calibration
	public final String PROPERTIES_GPS_CALIB_GYROZ = "mtx.gps.dr.gyroz";
	public final String PROPERTIES_GPS_CALIB_SINGLETICK = "mtx.gps.dr.singletick";
	public final String PROPERTIES_GPS_CALIB_ACCELX = "mtx.gps.dr.accelx";
	
	
	private TextView[] arNumber = new TextView[20];
	private TextView[] arbar = new TextView[20];
	private TextView[] arValue = new TextView[20];
	TextView mTV_State, mTV_hidden;
	public final String PROPERTIES_GPS_VERSION = "mtx.gps.version";
	private Button mTVExit, mBtnDRInit, mBtnColdStart;
	public static Button mBtnUbxSaveOnOff;
	public static boolean mIsUbxSave;
	
	// 20200611 yhr - ubx path
	public static final String UBX_FOLDER_NAME = "UBX";
	public static final String SDCARD_PATH = "storage/sdcard1";
	public static String UBX_LOG_PATH	= "/data/gps"; 
	public static String UBX_LOG_FILE	= "gps.ubx";  
	
	
	public static GPSSettingsActivity mInstance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gps_settings);
		
        locManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAl_TIME, INTERVAl_DIS, this);                        
        locManager.addGpsStatusListener(gpsStatusListener);
        locManager.addNmeaListener(gpsStatusNmeaListener);
        
        mInstance = this;
        
        //20200603 yhr
        mIsUbxSave = false;
        
        mGetView();
        
        //20200602 yhr - Reverse State
        mCntRearOn = 0;
		mHandlerRear.postDelayed(mRunnableRearCheck, DELAY_REAR_CHECK);
	}
	
	////////////////////////////////////////////////////////////////
	//20200602 yhr - Reverse State
	private static int mCntRearOn;
	public static final int REVERSE_ON = 0;
	public static final int REVERSE_OFF = 1;
	private int DELAY_REAR_CHECK = 100;
	public Handler mHandlerRear = new Handler();
	public Runnable mRunnableRearCheck = new Runnable() {

		@Override
		public void run() {
			try {
				txtCalibGyroz.setText(SystemProperties.get(PROPERTIES_GPS_CALIB_GYROZ, "not_calibrated"));
				txtCalibSingleTick.setText(SystemProperties.get(PROPERTIES_GPS_CALIB_SINGLETICK, "not_calibrated"));
				txtCalibAccelx.setText(SystemProperties.get(PROPERTIES_GPS_CALIB_ACCELX, "not_calibrated"));
				
				//20201015 yhr
				if(LauncherMainActivity.M_MTX == null)
					LauncherMainActivity.M_MTX = IMTX.Stub.asInterface(ServiceManager.getService("motrex"));
				
				int rearState = LauncherMainActivity.M_MTX.readGPIO((byte)0x5b);
				if(rearState == REVERSE_ON)
				{
					mCntRearOn++;
					if(mCntRearOn < 6)
						txtReverseState.setText(String.valueOf(mCntRearOn));
					else if(mCntRearOn == 6)
					{
						Log.e(HP_Manager.TAG_LAUNCHER, CLASS_NAME + " =======================>>>>> mRunnableRearCheck  : REVERSE_ON");
						txtReverseState.setText("ON");
					}
				}
				else
				{
					mCntRearOn = 0;
					txtReverseState.setText(String.valueOf(mCntRearOn));
				}
				mHandlerRear.postDelayed(this, DELAY_REAR_CHECK);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	};
	//////////////////////////////////////////////////
	
	Handler mHandlerOKEnable = new Handler();
	Runnable mRunnableOKEnable = new Runnable() {
		@Override
		public void run() {
			mResultOK.setEnabled(true);
		}
	};
	
	@Override
	protected void onResume() {
		super.onResume();
		
		Bundle extra = new Bundle();
		extra.putBoolean("start_engineer", true);
		locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", extra);
	};
	
	@Override
	protected void onPause() {
		Bundle extra = new Bundle();
		extra.putBoolean("stop_engineer", true);
		locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", extra);
		
		mHandlerOKEnable.removeCallbacks(mRunnableOKEnable);
		locManager.removeGpsStatusListener(gpsStatusListener);
        locManager.removeNmeaListener(gpsStatusNmeaListener);
        locManager.removeUpdates(this);
        
        //20200602 yhr - Reverse State
        mHandlerRear.removeCallbacks(mRunnableRearCheck);
        
		super.onPause();
	}
	
	@Override
	public void onClick(View arg0) {
	}
	
	//------------------GPS
    private final String CLASS_NAME = "[GPSSettingsActivity ]  "; 
    public final static long INTERVAl_TIME = 500;
    public final static float INTERVAl_DIS = 0f;

    private GpsStatus mGpsStatus;
    private LocationManager locManager;
    private Iterable<GpsSatellite>  itGpsStatellites;

	// gps status listener
	private final GpsStatus.Listener gpsStatusListener = new GpsStatus.Listener() {
		public void onGpsStatusChanged(int event) {
			switch (event) {
			case GpsStatus.GPS_EVENT_STARTED:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + String.valueOf(event) + "---GPS_EVENT_STARTED");
				break;
			case GpsStatus.GPS_EVENT_STOPPED:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + String.valueOf(event) + "---GPS_EVENT_STOPPED");
				break;
			case GpsStatus.GPS_EVENT_FIRST_FIX:
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + String.valueOf(event) + "---GPS_EVENT_FIRST_FIX");
				break;
			case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
				mGpsStatus = locManager.getGpsStatus(null);

				itGpsStatellites = mGpsStatus.getSatellites();
				Iterator<GpsSatellite> it = itGpsStatellites.iterator();
				int iPos = 0;
				while (it.hasNext()) {
					GpsSatellite gpsS = (GpsSatellite) it.next();
					// gpsInfo.iID[iPos] = gpsS.getPrn();
					// gpsInfo.iAzimuth[iPos] = (int)gpsS.getAzimuth();
					// gpsInfo.iElevation[iPos] =(int)gpsS.getElevation();
					// gpsInfo.iSN[iPos] = (int)gpsS.getSnr();
					iPos++;
				}
				// gpsInfo.iStateNum = iPos;
				// mGpsHandler.setGpsInfo(gpsInfo);
//				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + String.valueOf(event) + "---GPS_EVENT_SATELLITE_STATUS");
				break;
			default:
				break;
			}
		}
	};
	String arName[] = new String[20];
	String arData[] = new String[20];
	int gps_index = -1;
    private final GpsStatus.NmeaListener gpsStatusNmeaListener = new GpsStatus.NmeaListener(){

        public void onNmeaReceived(long timestamp, String nmea) {
//        	Log.d("NMEA", "nmea : " + nmea);
        	if(nmea.contains("$GPGSV")) {
        		String[] txtArr = nmea.split(",") ;
        		if(txtArr[2].equals(String.valueOf(1))) {
        			gps_index =0;
        			for(int i=0; i<20; i++) {
        				arName[i] = "";
        				arData[i] = "";
        			}
        		}
        		if(gps_index < 0) return;
        		
        		for(int i=4; i<txtArr.length-3; i+=4) {
        			String [] _data = txtArr[i+3].split("\\*");
        			arName[gps_index] = txtArr[i];
        			arData[gps_index] = _data[0];
        			gps_index ++;
        		}
        		
        		if(txtArr[1].equals(txtArr[2]))	{
        			mUpdateView();
        		}
        	}
        }
    };

    //20200602 yhr - speed 
    public static int calSpeed;
    public void onLocationChanged(Location location) {
		calSpeed = (int) (location.getSpeed() * 3.6);
		if(calSpeed > 0)
		{
			String speed = String.valueOf(calSpeed) + " Km/h";
			txtSpeed.setText(speed);
		}
//		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "onLocationChanged : " + calSpeed);
    }

    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }
    
    void mGetView() {
    	
    	mTV_State = (TextView) findViewById(R.id.tv_state);
    	mTV_State.setTextSize(22);
    	mUpdateState(0,0, 0, 0);
    	
    	//20200602 yhr - Reverse State
    	txtReverseState = (TextView) findViewById(R.id.tv_reverse_state);
    	
    	//20200602 yhr - Speed
    	txtSpeed = (TextView) findViewById(R.id.tv_speed_value);
    	
    	//20200608 yhr - GPS Calibration
    	txtCalibGyroz = (TextView) findViewById(R.id.tv_gps_calib_gyroscope);
    	txtCalibSingleTick = (TextView) findViewById(R.id.tv_gps_calib_singletick);
    	txtCalibAccelx = (TextView) findViewById(R.id.tv_gps_calib_accelerometer);
    	
     	arNumber[0] = (TextView) findViewById(R.id.txt01);
    	arNumber[1] = (TextView) findViewById(R.id.txt02);
    	arNumber[2] = (TextView) findViewById(R.id.txt03);
    	arNumber[3] = (TextView) findViewById(R.id.txt04);
    	arNumber[4] = (TextView) findViewById(R.id.txt05);
    	arNumber[5] = (TextView) findViewById(R.id.txt06);
    	arNumber[6] = (TextView) findViewById(R.id.txt07);
    	arNumber[7] = (TextView) findViewById(R.id.txt08);
    	arNumber[8] = (TextView) findViewById(R.id.txt09);
    	arNumber[9] = (TextView) findViewById(R.id.txt10);
    	arNumber[10] = (TextView) findViewById(R.id.txt11);
    	arNumber[11] = (TextView) findViewById(R.id.txt12);
    	arNumber[12] = (TextView) findViewById(R.id.txt13);
    	arNumber[13] = (TextView) findViewById(R.id.txt14);
    	arNumber[14] = (TextView) findViewById(R.id.txt15);
    	arNumber[15] = (TextView) findViewById(R.id.txt16);
    	arNumber[16] = (TextView) findViewById(R.id.txt17);
    	arNumber[17] = (TextView) findViewById(R.id.txt18);
    	arNumber[18] = (TextView) findViewById(R.id.txt19);
    	arNumber[19] = (TextView) findViewById(R.id.txt20);
    	
    	arbar[0] = (TextView) findViewById(R.id.tv_seek01);
    	arbar[1] = (TextView) findViewById(R.id.tv_seek02);
    	arbar[2] = (TextView) findViewById(R.id.tv_seek03);
    	arbar[3] = (TextView) findViewById(R.id.tv_seek04);
    	arbar[4] = (TextView) findViewById(R.id.tv_seek05);
    	arbar[5] = (TextView) findViewById(R.id.tv_seek06);
    	arbar[6] = (TextView) findViewById(R.id.tv_seek07);
    	arbar[7] = (TextView) findViewById(R.id.tv_seek08);
    	arbar[8] = (TextView) findViewById(R.id.tv_seek09);
    	arbar[9] = (TextView) findViewById(R.id.tv_seek10);
    	arbar[10] = (TextView) findViewById(R.id.tv_seek11);
    	arbar[11] = (TextView) findViewById(R.id.tv_seek12);
    	arbar[12] = (TextView) findViewById(R.id.tv_seek13);
    	arbar[13] = (TextView) findViewById(R.id.tv_seek14);
    	arbar[14] = (TextView) findViewById(R.id.tv_seek15);
    	arbar[15] = (TextView) findViewById(R.id.tv_seek16);
    	arbar[16] = (TextView) findViewById(R.id.tv_seek17);
    	arbar[17] = (TextView) findViewById(R.id.tv_seek18);
    	arbar[18] = (TextView) findViewById(R.id.tv_seek19);
    	arbar[19] = (TextView) findViewById(R.id.tv_seek20);
    	
    	arValue[0] = (TextView) findViewById(R.id.tv_value01);
    	arValue[1] = (TextView) findViewById(R.id.tv_value02);
    	arValue[2] = (TextView) findViewById(R.id.tv_value03);
    	arValue[3] = (TextView) findViewById(R.id.tv_value04);
    	arValue[4] = (TextView) findViewById(R.id.tv_value05);
    	arValue[5] = (TextView) findViewById(R.id.tv_value06);
    	arValue[6] = (TextView) findViewById(R.id.tv_value07);
    	arValue[7] = (TextView) findViewById(R.id.tv_value08);
    	arValue[8] = (TextView) findViewById(R.id.tv_value09);
    	arValue[9] = (TextView) findViewById(R.id.tv_value10);
    	arValue[10] = (TextView) findViewById(R.id.tv_value11);
    	arValue[11] = (TextView) findViewById(R.id.tv_value12);
    	arValue[12] = (TextView) findViewById(R.id.tv_value13);
    	arValue[13] = (TextView) findViewById(R.id.tv_value14);
    	arValue[14] = (TextView) findViewById(R.id.tv_value15);
    	arValue[15] = (TextView) findViewById(R.id.tv_value16);
    	arValue[16] = (TextView) findViewById(R.id.tv_value17);
    	arValue[17] = (TextView) findViewById(R.id.tv_value18);
    	arValue[18] = (TextView) findViewById(R.id.tv_value19);
    	arValue[19] = (TextView) findViewById(R.id.tv_value20);
    	
    	mTVExit = (Button) findViewById(R.id.btn_exit);
    	mTVExit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
    	
    	mTV_hidden = (TextView) findViewById(R.id.tv_hidden);
    	mTV_hidden.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				if(arg1.getAction() == MotionEvent.ACTION_DOWN) {
					mHandler.postDelayed(mRunnable, 5000);
				} else if(arg1.getAction() == MotionEvent.ACTION_UP){
					mHandler.removeCallbacks(mRunnable);
				}
				return false;
			}
		});
    	
    	/////////////////////////////////////////////////////////////////////////////////////////
    	//20200603 yhr - DR
    	mBtnDRInit = (Button) findViewById(R.id.btn_dr_init);
    	mBtnDRInit.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View arg0) {
    			AlertDialog.Builder builder = new AlertDialog.Builder(mInstance);  
				builder.setTitle("")
				.setMessage(getString(R.string.str_msg_init))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.str_clear_ok), new DialogInterface.OnClickListener(){       
					public void onClick(DialogInterface dialog, int whichButton){
						
						// DR Initialize
						Bundle extra = new Bundle();
						extra.putBoolean("del_config", true);
		    			locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", extra);
		    			
					}
				})
				.setNegativeButton(getString(R.string.str_clear_no), new DialogInterface.OnClickListener(){      
					public void onClick(DialogInterface dialog, int whichButton){
						dialog.cancel();
						
						//log file delete
						String strlog = UBX_LOG_PATH + File.separator + UBX_LOG_FILE;
						File file_log = new File(strlog);
						file_log.delete();
					}
				});
				AlertDialog dialog = builder.create(); 
				dialog.show();
    		}
    	});
    	
    	mBtnColdStart = (Button) findViewById(R.id.btn_cold_start);
    	mBtnColdStart.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View arg0) {
    			
    			locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
    		}
    	});
    	
    	mBtnUbxSaveOnOff = (Button) findViewById(R.id.btn_ubx_save);
    	
    	// 20200611 yhr
    	if(HP_Manager.bIsSDCardMounted == false)
    		mBtnUbxSaveOnOff.setEnabled(false);
    	
    	
    	mBtnUbxSaveOnOff.setOnClickListener(new OnClickListener() {

    		@Override
    		public void onClick(View arg0) {
    			
    			if(mIsUbxSave)
    			{
    				// 200611 yhr - UBX Log ���옣 以묒� 				
    				Bundle extra = new Bundle();
    				extra.putBoolean("stop_log", true);
    				locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", extra);
    				
    				mIsUbxSave = false;
    				mBtnUbxSaveOnOff.setText(getResources().getString(R.string.ubx_save_on));
    				
    				// 200611 yhr - copy
    				String strSrcPath = UBX_LOG_PATH + File.separator + UBX_LOG_FILE;
					String strDestPath = SDCARD_PATH + File.separator + UBX_FOLDER_NAME + File.separator;
					File system_ubx_dir = new File(strSrcPath);
					File sdcard_ubx_dir = new File(strDestPath);
					
//					Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "bIsSDCardMounted : " + HP_Manager.bIsSDCardMounted 
//    						+ ", strSrcPath : " + strSrcPath
//    						+ ", strDestPath : " + strDestPath 
//    						+ ", system_ubx_dir : " + system_ubx_dir.isDirectory()
//    						+ ", sdcard_ubx_dir : " + sdcard_ubx_dir.isDirectory());
    				
//    				if(system_ubx_dir.isDirectory())
//    				{
//    					if(!sdcard_ubx_dir.isDirectory())
//    						sdcard_ubx_dir.mkdirs(); 
//    					
//    					SystemClock.sleep(100);
//    					
//    					copy(new File(strSrcPath + UBX_LOG_FILE), new File(strDestPath + UBX_LOG_FILE));
//    					
//    					//copyFile(strSrcPath, strDestPath);
//    				}
					
    				//if(system_ubx_dir.isDirectory())
					if(system_ubx_dir.isFile())
    				{
    					if(!sdcard_ubx_dir.isDirectory())
    						sdcard_ubx_dir.mkdirs(); 
    					
    					SystemClock.sleep(100);
    					
    					try {
							copy(system_ubx_dir, new File(strDestPath + UBX_LOG_FILE));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
    				}
    			}
    			else
    			{
    				mIsUbxSave = true;
    				mBtnUbxSaveOnOff.setText(getResources().getString(R.string.ubx_save_off));
    				
    				// UBX Log ���옣 �떆�옉
    				Bundle extra = new Bundle();
    				extra.putBoolean("start_log", true);
    				locManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", extra);
    			}		
    		}
    	});
    	////////////////////////////////////////////////////////////////////////////////////////////////////
    }
    
    private void copy(File src, File dst) throws IOException {
    	FileInputStream in = new FileInputStream(src);
        try {
        	FileOutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }
    
    //200611 yhr
    private void copyFile(String strSrc , String strDest){
		File srcFile = new File(strSrc);
		File[] target_file = srcFile.listFiles();
		Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "copyFile / strSrc : " + strSrc + ", strSrc exists : " + srcFile.exists() 
											+ ", strDest : " + strDest + ", size : " + target_file.length );

		if(srcFile!=null && srcFile.exists())
		{
			for (File file : target_file) {
				File temp = new File(strDest + File.separator + file.getName());
				if(file.isDirectory())
				{
					if(temp.mkdir())
					{
						try {
							FileInputStream fis = null;
							FileOutputStream newfos = null;
							fis = new FileInputStream(file);
							newfos = new FileOutputStream(temp);  
							byte[] buffer = new byte[1024];
							int readcount = 0;
							
							while((readcount = fis.read(buffer))!= -1){ 
								newfos.write(buffer,0,readcount);  
							} 
							newfos.close();
							fis.close(); 
						} catch (Exception e) {
							e.printStackTrace();				
						} 
					}
					Log.d(HP_Manager.TAG_SETTING, CLASS_NAME +"mkdir : " +  temp.isDirectory() + "getParent : " +file.getParent()
					 + ", getPath  : " +  temp.getPath() );
				}
			}
		}    
	}
    
    Handler mHandler = new Handler();
	Runnable mRunnable = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Intent intent = getPackageManager().getLaunchIntentForPackage("com.ublox.ucenter");
	        if(intent != null)
	        {
	        	intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
	        	startActivity(intent);
	        }
		}
	};
    
    void mUpdateView() {
    	int _fix = 0, _all = 0, _cnt = 0, _sum=0;
    	for(int i=0; i<20; i++) {
    		if(arName[i].length() < 0) {
    			arValue[i].setText("");
    			arNumber[i].setText("");
    			LayoutParams params = (LayoutParams) arbar[i].getLayoutParams();
    			params.height = 0;
    			arbar[i].setLayoutParams(params);
    		} else {
	    		arValue[i].setText(arData[i]);
	    		if(arValue[i].length() > 0) {
	    			_fix++;
	    			
	    			LayoutParams params = (LayoutParams) arbar[i].getLayoutParams();
	    			params.height = Integer.parseInt(arData[i]);
	    			arbar[i].setLayoutParams(params);
	    			
	    			if(params.height >= 30 ) {
	    				_cnt++;
	    				_sum = _sum + params.height;
	    			}
	    		} else {
	    			LayoutParams params = (LayoutParams) arbar[i].getLayoutParams();
	    			params.height = 0;
	    			arbar[i].setLayoutParams(params);
	    		}
	    		arNumber[i].setText(arName[i]);
    		}
    	}
//    	Log.d("debug", "==========> " + _sum + "   " + _cnt);
//    	if(_sum == 0) _sum = 1;
    	mUpdateState(_fix, gps_index, _cnt, _cnt == 0 ? 0 : _sum/_cnt);
    }
    void mUpdateState(int _fix, int _all, int _cnt, int _arv) {
    	String _str = getString(R.string.str_gps_state);
    	_str = _str.replace("!a", SystemProperties.get(PROPERTIES_GPS_VERSION, ""));
    	_str = _str.replace("%s", Integer.toString(_fix));
    	_str = _str.replace("#s", Integer.toString(_all));
    	_str = _str.replace("@s", Integer.toString(_cnt));
    	_str = _str.replace("!s", Integer.toString(_arv));
		
    	mTV_State.setText(_str);
    }

	@Override
	public boolean onTouch(View arg0, MotionEvent arg1) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	
}
