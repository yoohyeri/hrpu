package com.mdstec.android.tpeg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.*;
import android.util.Log;

public class TPEG_FileCopy {
	
	public interface onCopyCompletedListener {
		void onCopySuccess();
		void onCopyFail();
	}
		
	private Context mContext;
	private File mFileSrc;
	private File mFileDst;
	private String mStrUpdate;
	private int mCntFiles; 
	private boolean isDirectory;
	
	private static final int RESULT_COPY_FAIL = 0;
    private static final int RESULT_COPY_SUCCESS = 1;
	
	private onCopyCompletedListener CopyCompletedListener;
		
	public void setOnCopyCompletedListener(onCopyCompletedListener listener){
		CopyCompletedListener = listener;
	}
		
	ProgressDialog asyncDialog = null;
	
	public TPEG_FileCopy(Context context, File fSrc, File fDst, String strUpdate) {
		mContext = context;
		mFileSrc = fSrc;
		mFileDst = fDst;
		isDirectory = mFileSrc.isDirectory();

		mStrUpdate = strUpdate;	
		
//		Log.d("yhr", "2 TPEG_FileCopy / mFileSrc :  " + mFileSrc.toString() + ", mFileDst : " + mFileDst.toString());
		
//		if(mStrUpdate.length() != 0) {
//			asyncDialog = new ProgressDialog(mContext);
//		}
//		else {
//			asyncDialog = null;
//		}
	}
	
	public void mThreadRun() {
//		if(asyncDialog != null) {
//		     asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//	         asyncDialog.setMessage(mStrUpdate);
//	          
//	         // show dialog
//	         asyncDialog.show();
//	     }
         
         Thread myThread = new Thread(new Runnable() {
             public void run() {            	 	
                	int count = countFilesInDirectory(mFileSrc);	
                	
                	boolean bResult = true;
     	        	
     	        	try {     	        		
						RemoveFile(mFileDst.getPath());						
						CopyFileDirectory(mFileSrc, mFileDst, count, asyncDialog);		
					} catch (IOException e) {
						// TODO Auto-generated catch block
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
	    			execute();
	    			
		    		if(CopyCompletedListener != null) {
		 	        	CopyCompletedListener.onCopyFail();
		 	        }
	    		break;
	    		case RESULT_COPY_SUCCESS:
//	    			asyncDialog.dismiss();
	    			if(CopyCompletedListener != null) {
	 	        		CopyCompletedListener.onCopySuccess();
	 	        	}
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
						Log.d("min", "1 delete file:"+path);
				    }else{
				    	RemoveFile(listFile[i].getPath());		    
				    }				   
				    listFile[i].delete();		
				}
			   } else {			   
				   File f = new File(path);
				   f.delete();
				   Log.d("min", "2 delete file:"+path);
			   }	   
		 } catch (Exception e) {			
		 }
	}
	
	public int CopyFileDirectory(File srcFile, File dstFile, int contFiles, ProgressDialog pd)
			throws IOException
	{
		if(srcFile.isDirectory()) {
			if(!dstFile.exists()) {
				dstFile.mkdir();
			}
			
			String[] children = srcFile.list();
			for(int i=0; i < srcFile.listFiles().length; ++i) {
				mCntFiles = CopyFileDirectory(new File(srcFile,children[i]),
									new File(dstFile,children[i]), contFiles, pd);
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
		        if(pd != null) {
		        	pd.setProgress((int)((mCntFiles * 100) / contFiles));
		        }
		        
		        
	        }
	        else {
	        	try {
	                byte[] buffer = new byte[4096];
	                int bytesRead;
	                while ((bytesRead = in.read(buffer)) >= 0) {
	                    out.write(buffer, 0, bytesRead);
	                    if(pd != null) {
	                    	pd.setProgress((int)((dstFile.length() * 100) / srcFile.length()));
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
        	if(asyncDialog != null) {
	            asyncDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
	            asyncDialog.setMessage(mStrUpdate);
	             
	            // show dialog
	            asyncDialog.show();
	        }
            super.onPreExecute();
        }
 
        @Override
        protected Void doInBackground(Void... arg0) {
            try {
            	        
	        	int count = countFilesInDirectory(mFileSrc);
	        	
	        	RemoveFile(mFileDst.getPath());
    			CopyFileDirectory(mFileSrc, mFileDst, count, asyncDialog);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result) {
        	if(asyncDialog != null) {
            	asyncDialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }
}
