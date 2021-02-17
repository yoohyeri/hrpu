package com.mobilus.hp.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.mobilus.hp.popup.NotifyDialog;
import com.telechips.android.tdmb.LauncherMainActivity;

import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;

public class FileCopy {
	
	private static String CLASS_NAME = "[FileCopy ]  ";
	
	public interface onCopyCompletedListener {
		void onCopySuccess();
		void onCopyFail();
	}
		
	private File mFileSrc;
	private File mFileDst;
	private int mCntFiles; 
	private boolean isDirectory;
	
	private static final int RESULT_COPY_FAIL = 0;
    private static final int RESULT_COPY_SUCCESS = 1;
	
	private onCopyCompletedListener CopyCompletedListener;
		
	public void setOnCopyCompletedListener(onCopyCompletedListener listener){
		CopyCompletedListener = listener;
	}

	public FileCopy(Context context, File fSrc, File fDst) {
		mFileSrc = fSrc;
		mFileDst = fDst;
		isDirectory = mFileSrc.isDirectory();
	}
	
	public FileCopy(Context context, File fSrc, File fDst, String strUpdate) {
		mFileSrc = fSrc;
		mFileDst = fDst;
		isDirectory = mFileSrc.isDirectory();
	}
	
	public void mThreadRun() {
		NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_FILE_COPY;

		// 로딩 팝업 표출
		Intent popup = null;
		popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
		LauncherMainActivity.getInstance().startService(popup);
         
         Thread myThread = new Thread(new Runnable() {
             public void run() {            	 	
                	int count = countFilesInDirectory(mFileSrc);	
                	boolean bResult = true;
     	        	
     	        	try {     	        		
						RemoveFile(mFileDst.getPath());								
						CopyFileDirectory(mFileSrc, mFileDst, count);
					} catch (IOException e) {
						e.printStackTrace();
						bResult = false;
					}     	        	

     	        	if(LauncherMainActivity.getInstance().mFWUpgradeMode)
     	        		handler.sendEmptyMessageDelayed(bResult?RESULT_COPY_SUCCESS:RESULT_COPY_FAIL, 2000);	  
     	        	else
     	        		handler.sendEmptyMessage(bResult?RESULT_COPY_SUCCESS:RESULT_COPY_FAIL);	     	        
             }
         });
         myThread.start();
	}
	
	public void mTpegNTCThreadRun() {
		NotifyDialog.mCurrentPopup = NotifyDialog.UPDATE_FILE_COPY;

		// 로딩 팝업 표출
		Intent popup = null;
		popup = new Intent(LauncherMainActivity.getInstance(), NotifyDialog.class);
		LauncherMainActivity.getInstance().startService(popup);
		
		 Thread myThread = new Thread(new Runnable() {
             public void run() {            	 	
                	int count = countFilesInDirectory(mFileSrc);	
                	boolean bResult = true;
     	        	
     	        	try {     	        		
//						RemoveFile(mFileDst.getPath());								
						CopyFileDirectory(mFileSrc, mFileDst, count);
					} catch (IOException e) {
						e.printStackTrace();
						bResult = false;
					}     
//     	        	Log.d("yhr", "mTpegNTCThreadRun / mFileSrc : " + mFileSrc.toString() + ", mFileDst : " + mFileDst.toString());
     	        	Handler handler = new Handler(Looper.getMainLooper());
     	        	handler.sendEmptyMessage(bResult?RESULT_COPY_SUCCESS:RESULT_COPY_FAIL);	     	        
             }
         });
         myThread.start();
	}
	
	public void mFWThreadRun() {

		Thread myThread = new Thread(new Runnable() {
			public void run() {            	 	
				int count = countFilesInDirectory(mFileSrc);	
				boolean bResult = true;

				try {     	        		
					RemoveFile(mFileDst.getPath());							
					CopyFileDirectory(mFileSrc, mFileDst, count);
				} catch (IOException e) {
					e.printStackTrace();
					bResult = false;
				}     	        	
				handler.sendEmptyMessage(bResult?RESULT_COPY_SUCCESS:RESULT_COPY_FAIL);	     	        
			}
		});
         myThread.start();
	}
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case RESULT_COPY_FAIL:
				if(CopyCompletedListener != null)
					CopyCompletedListener.onCopyFail();
				break;
			case RESULT_COPY_SUCCESS:
				if(NotifyDialog.mProgressBar != null) 
					NotifyDialog.mProgressBar.setProgress(100);

				if(CopyCompletedListener != null) 
					CopyCompletedListener.onCopySuccess();
				break;
			default:
				break;	    		
			}
	    }
	};
	
	public void execute() {
		FileCopyAsyncTask task = new FileCopyAsyncTask();
		task.execute();
	}
	
	public static int countFilesInDirectory(File dir) {
        int totalFiles = 0;
//        Log.d("yhr", CLASS_NAME + "countFilesInDirectory : "+dir.getPath());
        File[] listFiles = dir.listFiles();
        if (listFiles != null && listFiles.length > 0) {
            for (File file : listFiles) {
                if (file.isFile()) {
                    totalFiles++;
                } else {
                    totalFiles += countFilesInDirectory(file);
                }
            }
        }
        return totalFiles;
    }
	
	public static void RemoveFile(String path)
			throws IOException
	{
		File[] listFile = new File(path).listFiles();
		
		try {
			if (listFile.length > 0) 
			{	   
				for (int i = 0; i < listFile.length; i++) {
					if (listFile[i].isFile()) {
						listFile[i].delete();
						Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "(1) delete file : "+path);
					}else{
						RemoveFile(listFile[i].getPath());		    
					}				   
					listFile[i].delete();		
				}
			} else {			   
				File f = new File(path);
				f.delete();
				Log.d(HP_Manager.TAG_SETTING, CLASS_NAME + "(2) delete file : "+path);
			}	   
		} catch (Exception e) {			
		}
	}

	public int CopyFileDirectory(File srcFile, File dstFile, int contFiles)
			throws IOException
	{
//		Log.d("yhr","CopyFileDirectory / srcFile.isDirectory : " + srcFile.isDirectory());
		
		if(srcFile.isDirectory()) {
			if(!dstFile.exists()) {
				dstFile.mkdir();
			}
			
			String[] children = srcFile.list();
			for(int i=0; i < srcFile.listFiles().length; ++i) {
				mCntFiles = CopyFileDirectory(new File(srcFile,children[i]), new File(dstFile,children[i]), contFiles);
			}
		}
		else {		
			FileInputStream in = new FileInputStream(srcFile);
	        FileOutputStream out = new FileOutputStream(dstFile);
	        
	        if(isDirectory) {
		        try {
	                byte[] buffer = new byte[4096];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) >= 0) {
	                    out.write(buffer, 0, bytesRead);
	                }
	            } finally {
	                out.flush();
	                try {
	                    out.getFD().sync();
	                } catch (IOException e) {
	                }
	                in.close();
	    	        out.close();
	            }
		        mCntFiles += 1;
		        if(NotifyDialog.mProgressBar != null) {
		        	NotifyDialog.mProgressBar.setProgress((int)((mCntFiles * 100) / contFiles));
		        }
		        
	        }
	        else {
	        	try {
	                byte[] buffer = new byte[4096];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) >= 0) {
	                    out.write(buffer, 0, bytesRead);
	                    if(NotifyDialog.mProgressBar != null) {
	                    	NotifyDialog.mProgressBar.setProgress((int)((dstFile.length() * 100) / srcFile.length()));
	                    }
	                }
	            } finally {
	                out.flush();
	                try {
	                    out.getFD().sync();
	                } catch (IOException e) {
	                }
	                in.close();
	                out.close();
	            }
	        	mCntFiles += 1;
	        }        
	    }
		return mCntFiles;
	}
	
	private class FileCopyAsyncTask extends AsyncTask<Void, Void, Void> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
            	        
	        	int count = countFilesInDirectory(mFileSrc);
	        	RemoveFile(mFileDst.getPath());
    			CopyFileDirectory(mFileSrc, mFileDst, count);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}