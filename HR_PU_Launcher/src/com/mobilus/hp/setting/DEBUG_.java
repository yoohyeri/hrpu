package com.mobilus.hp.setting;

import java.io.*;
import java.text.*;
import java.util.*;

import com.mobilus.hp.launcher.HP_Manager;

import java.lang.Process;

import android.os.*;
import android.util.Log;

/**
 * @project : usb
 * @package : co.kr.hitecms.usb.debug
 * @name : DEBUG__.java
 * @author : ThinkCodeHelp
 * @date : 2015. 3. 31.
 * @description :
 */
public class DEBUG_ {
	/**
	 * @author : ThinkCodeHelp
	 * @date : 2015. 3. 31.
	 * @description :
	 **/
	public DEBUG_() {
	}

	/** The Constant ERROR_. */
	public static final int ERROR_ = -1;

	/**
	 * Yes
	 */
	public static final String STR_YES = "1";

	/**
	 * No
	 */
	public static final String STR_NO = "0";

	public static final int FW_MODE_YES = 50;
	public static final int FW_MODE_NO = 51;

	/**
	 * Initializer
	 */
	public static final int INIT_ = 0;

	/**
	 * Debug Tag
	 */
	public static final String _DEBUG = "debug";

	/**
	 * Debug Mode
	 */
	public final static boolean isDebugMode =
			// true;
			false;

	/**
	 * Decoration String
	 */
	private static final String _DECO_STR = "=====> ";

	/**
	 * @name : debug
	 * @author : ThinkCode
	 * @description : Debug
	 * @param _msg
	 */
	public static void debug(String _msg) {
		if (isDebugMode)
			Log.d(_DEBUG, _DECO_STR + _msg);
	}

	public static void adebug(String _msg) {
		Log.d(_DEBUG, _DECO_STR + _msg);
	}

	/**
	 * @name : error
	 * @author : ThinkCode
	 * @description : Error
	 * @param _msg
	 */
	public static void error(String _msg) {
		if (isDebugMode)
			Log.e(_DEBUG, _DECO_STR + _msg);
	}

	// ------------------File Log
	// ------------Log Save
	/**
	 * 외장 메모리에 Log file 생성
	 *
	 * MTP로 PC에 접속 시 Android USB에 버그 있음.. - 폰을 재부팅 하기 전에 PC 에서 외장 메모리에 생성한 디렉토리나
	 * 파일이 보이지 않음 - 외장 메모리에 로그가 제대로 씌여졌는지 확인하려면.. 1. 폰을 재부팅 하거나 2. adb shell 을
	 * 이용해서 확인하거나
	 * 
	 * http://stackoverflow.com/questions/13737261/nexus-4-not-showing-files-via
	 * -mtp/14074407#14074407
	 *
	 */

	public static String mNandDir = Environment.getExternalStorageDirectory() + File.separator + "Log";
	private static String mUSBDir = "/storage/sdcard1/Log";

	public static boolean createLogFile() {
		boolean _ret = false;
		int BUFFER_SIZE = 256;
//		SimpleDateFormat sdfilename = new SimpleDateFormat("yyyyMMddHHmmss"); // 포맷변경
//																				// (
//																				// 년월일
//																				// 시분초)

//		String today = null;
//		Calendar cal = Calendar.getInstance();

//		SimpleDateFormat sdformat = new SimpleDateFormat("MM-dd HH:mm:ss"); // 포맷변경
//																			// (
//																			// 년월일
//																			// 시분초)
//		today = sdformat.format(cal.getTime());

		File mLogFile = null;
		FileOutputStream fos;
		File folder = new File(mUSBDir);
		if (folder.mkdir() || folder.isDirectory()) {
			File dir = makeDirectory(mUSBDir);
//			String today = HP_Manager.getCurrentYear() + "_"   // 년
//							+ HP_Manager.getCurrentMonth()+HP_Manager.getCurrentDay()+"_"  // 월일
//							+ HP_Manager.getCurrentHour()+":"+HP_Manager.getCurrentMin();  // 시분
			String today = HP_Manager.getCurrentYear()
					+ HP_Manager.getCurrentMonth()+HP_Manager.getCurrentDay()
					+ HP_Manager.getCurrentHour()+HP_Manager.getCurrentMin();
			mLogFile = makeFile(dir, mUSBDir + "/" + "log_" + today + ".txt");
//			mLogFile = makeFile(dir, mUSBDir + "/" + "log_" + sdfilename.format(cal.getTime()) + ".txt");

			Process logcatProc = null;
			try {
				logcatProc = Runtime.getRuntime().exec("logcat -d -v time");
			} catch (IOException e) {
				e.printStackTrace();
				return _ret;
			}

			BufferedReader reader = null;
			String lineSeparatoer = System.getProperty("line.separator");
			StringBuilder strOutput = new StringBuilder();
			try {
				reader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()), 2048);
				String line;
				String _data;

				fos = new FileOutputStream(mLogFile);

				while ((line = reader.readLine()) != null) {
					_ret = true;

					// _data = line.substring(0, 14);
					// if(today.compareTo(_data) == 0 || today.compareTo(_data)
					// < 0) break;;

					fos.write(line.getBytes());

					fos.write(lineSeparatoer.getBytes());
				}
				fos.flush();
				fos.getFD().sync();
				fos.close();

				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _ret;
	}

	private static final String TAG = "TestFileActivity";

	/**
	 * 디렉토리 생성
	 * 
	 * @return dir
	 */
	private static File makeDirectory(String dir_path) {
		File dir = new File(dir_path);
		if (!dir.exists()) {
			dir.mkdirs();
			Log.i(TAG, "!dir.exists");
		} else {
			Log.i(TAG, "dir.exists");
		}

		return dir;
	}

	/**
	 * 파일 생성
	 * 
	 * @param dir
	 * @return file
	 */
	private static File makeFile(File dir, String file_path) {
		File file = null;
		boolean isSuccess = false;
		if (dir.isDirectory()) {
			file = new File(file_path);
			if (file != null && !file.exists()) {
				Log.i(TAG, "!file.exists");
				try {
					isSuccess = file.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					Log.i(TAG, "file exists = " + isSuccess);
				}
			} else {
				Log.i(TAG, "file.exists");
			}
		}
		return file;
	}

	public static boolean removeFile(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) { // 파일이 디렉토리인지 확인
				File[] files = file.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (files[i].delete()) {
						System.out.println(files[i].getName() + " 삭제성공");
					} else {
						System.out.println(files[i].getName() + " 삭제실패");
					}
				}
			}
			if (file.delete())
				return true;
			else
				return false;
		} else {
			return false;
		}
	}

	/**
	 * 디렉토리 여부 체크 하기
	 * 
	 * @param dir
	 * @return
	 */
	private static boolean isDirectory(File dir) {
		boolean result;
		if (dir != null && dir.isDirectory()) {
			result = true;
		} else {
			result = false;
		}
		return result;
	}

	public static boolean isFile(String _path) {
		File files = new File(_path);
		if (files.exists() == true)
			return true;

		return false;
	}

	
	public static void toHexString(String _tag, byte buf[]) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			sb.append(Integer.toHexString(0x0100 + (buf[i] & 0x00FF)).substring(1));
			sb.append(" ");
		}
		
		Log.d("debug", "=======================!!!!!!!!!!!!!!!!!!!!!!!!!!==============");
		Log.d("debug", "==========> " + _tag + " : " + sb.toString());
	}
}
