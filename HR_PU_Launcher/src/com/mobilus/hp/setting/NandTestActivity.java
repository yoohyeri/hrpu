package com.mobilus.hp.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.DxbView.STATE;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.*;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NandTestActivity extends Activity {
	Button btn, btnback;
	public String mNandDir =  Environment.getExternalStorageDirectory()+File.separator+"NandTest.test";
	private String mUSBDirOrg = "/storage/sdcard1/SD_TEST/NandTest.test";
	private String mUSBDir = "/storage/sdcard1/NandTest.test";
	static boolean mIsFileCopy = false;
	TextView tvIng, tvResult;
	int mCountWrite, mCountErase;
	
	final int DELAY_PROCESS = 700;
	int mProgressCount = 0;
	String [] _copy = {"Test .", "Test .." , "Test ...", "Test ...."};
	Handler mHander = new Handler();
	Runnable mRunnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			
			mProgressCount ++;
			tvIng.setText(_copy[mProgressCount%4]);
			
			if(mIsFileCopy)	mHander.postDelayed(mRunnable, DELAY_PROCESS);
			else			{
				mProgressCount = 0;
				tvIng.setText("");
			}
		}
	};
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_nand_test);
		
		tvIng = (TextView)findViewById(R.id.tv_ing);
		tvResult = (TextView)findViewById(R.id.tv_result);
		
		btn = (Button)findViewById(R.id.bt_start);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(btn.getText().equals(getResources().getText(R.string.btn_nand_test_start))) {
					tvResult.setText("");
					mIsFileCopy = true;
					mCountWrite = 0;
					mCountErase = 0;
					
					ThreadCopy copy = new ThreadCopy();
					copy.start();
					btn.setText(R.string.btn_nand_test_stop);
					mHander.postDelayed(mRunnable, DELAY_PROCESS);
				} else {
					mIsFileCopy = false;
					btn.setText(R.string.btn_nand_test_start);
				}
			}
		});
		
		btnback = (Button) findViewById(R.id.bt_back);
		btnback.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				HP_Manager.mCurrentView = HP_Index.FRAGMENT_HIDDEN_MENU;
				HP_Manager.mBackView = HP_Index.BACK_SETTING_MAIN;
				DxbView.eState = STATE.NORMAL_VIEW;
				
				
				
				mIsFileCopy = false;
				finish();
			}
		});
		
		intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
		intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
		intentFilter.addDataScheme("file");
	}
	
	IntentFilter intentFilter;
	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
				if(DEBUG_.isFile(mUSBDirOrg))	btn.setEnabled(true);
				else						btn.setEnabled(false);
			} else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
				btn.setText(R.string.btn_nand_test_start);
				btn.setEnabled(false);
			} 
		}
	};
	
	public class ThreadCopy extends Thread
	{
	    public void run()
	    {
	    	copyFile(mNandDir, mUSBDir);
	    }
	}
	
	public void copyFile(String src, String dest){
		long fsize = 0;
		try{
			FileInputStream fin = new FileInputStream(mUSBDirOrg);
			FileOutputStream fout = new FileOutputStream(mNandDir);
			
			FileChannel inc = fin.getChannel();
			FileChannel outc = fout.getChannel();
			//������ file size
			fsize = inc.size();
			
			inc.transferTo(0, fsize, outc);
			
			inc.close();
			outc.close();
			fin.close();
			fout.close();
			
			Message _msg = new Message();
			_msg.what = 0;
			_msg.arg1 = mCountWrite;
			_msg.arg2 = mCountErase;
			handler.sendMessage(_msg);
		}
		catch(Exception e){
			mIsFileCopy = false;
			e.printStackTrace();
			return;
		}
		
		while(mIsFileCopy) {
			try{
				FileInputStream fin = new FileInputStream(src);
				FileOutputStream fout = new FileOutputStream(dest);
				
				FileChannel inc = fin.getChannel();
				FileChannel outc = fout.getChannel();
				//������ file size
				fsize = inc.size();
				
				inc.transferTo(0, fsize, outc);
				
				inc.close();
				outc.close();
				fin.close();
				fout.close();
				
				mCountWrite ++;
				
				if(DEBUG_.removeFile(dest))	mCountErase ++;
				
				Message _msg = new Message();
				_msg.what = 0;
				_msg.arg1 = mCountWrite;
				_msg.arg2 = mCountErase;
				handler.sendMessage(_msg);
			}
			catch(Exception e){
				mIsFileCopy = false;
				e.printStackTrace();
				return;
			}
		}
	}
	
	Handler handler = new Handler() {
	    @Override
	    public void handleMessage(Message msg) {
	    	UpdateUI(msg.arg1, msg.arg2);
	    }
	};
	
	public void UpdateUI(int _countWrite, int _countErase) {
		String _msg = getResources().getString(R.string.txt_nand_result);
		_msg = _msg.replace("#s", String.valueOf(_countWrite));
		_msg = _msg.replace("%s", String.valueOf(_countErase));
		tvResult.setText(_msg);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mCountWrite = 0;
		mCountErase = 0; 
		
		if(DEBUG_.isFile(mUSBDirOrg))	btn.setEnabled(true);
		else							btn.setEnabled(false);
		
		registerReceiver(mReceiver, intentFilter);

		super.onResume();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		
		unregisterReceiver(mReceiver);
		super.onPause();
	}	
}