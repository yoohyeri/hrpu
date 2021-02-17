package com.mobilus.hp.launcher;

import android.os.Handler;
import android.os.Message;

public class HP_Timer {
	private String CLASS_NAME = "[HP_Timer ]  ";
	private TimerListener mListner;
	private long mDelayTime;
	private long mPeriod = -1;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(mListner!=null)
				mListner.doAction();
		}
	};

	Runnable mTask = new Runnable() {
		@Override
		public void run() {
			mHandler.sendEmptyMessage(0);
			if (mPeriod != -1 ) {
				mHandler.postDelayed(mTask, mPeriod);
			}
		}
	};

	public HP_Timer(long delaytime, TimerListener listner) {
		mDelayTime = delaytime;
		mListner = listner;
	}

	public HP_Timer(long delaytime, long period, TimerListener listner){
		mDelayTime = delaytime;
		mPeriod = period;
		mListner = listner;
	}
	
	public void setTimerListener(TimerListener listner) {
		mListner = listner;
	}

	public TimerListener getTimerListener() {
		return mListner;
	}

	public void startTimer() {
		removeTimer();
		mHandler.postDelayed(mTask, mDelayTime);
	}

	public void removeTimer() {
		mHandler.removeCallbacksAndMessages(null);
	}

	public void setDelayTime(long _delaytime)
	{
		mDelayTime = _delaytime;
	}

	public interface TimerListener {
		public void doAction();
	}
}
