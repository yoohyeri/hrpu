package com.mobilus.hp.launcher;

import java.util.TimeZone;

import com.telechips.android.tdmb.R;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;

public class AnalogClock extends View {
	public AnalogClock(Context context) {
		this(context, null);
	}

	private Time mCalendar;

	private Drawable mHourHand;
	private Drawable mMinuteHand;
	private Drawable mCenter;
	private Drawable mDial;

	private int mDialWidth;
	private int mDialHeight;

	private boolean mAttached;

	private final Handler mHandler = new Handler();
	private float mMinutes;
	private float mHour;
	private boolean mChanged;

	Context mContext;

	public AnalogClock(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public AnalogClock(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		Resources r = context.getResources();
		mContext = context;
		
		if (this.mDial == null) {
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				this.mDial = r.getDrawable(R.drawable.home_analog_bg_1);
			else
				this.mDial = r.getDrawable(R.drawable.kia_home_analog_bg2);
		}
		
		if (this.mHourHand == null) {
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				this.mHourHand = r.getDrawable(R.drawable.home_analog_hour_1);
			else
				this.mHourHand = r.getDrawable(R.drawable.kia_home_analog_hour_1);
		}
		
		if (this.mMinuteHand == null) {
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				this.mMinuteHand = r.getDrawable(R.drawable.home_analog_min_1);
			else
				this.mMinuteHand = r.getDrawable(R.drawable.kia_home_analog_min_1);
		}
		
		if(this.mCenter == null)
		{
			if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
				this.mCenter = r.getDrawable(R.drawable.home_analog_center_point_1);
			else
				this.mCenter = r.getDrawable(R.drawable.kia_home_analog_center_point_2);
		}
		mCalendar = new Time();
		mDialWidth = mDial.getIntrinsicWidth();
		mDialHeight = mDial.getIntrinsicHeight();
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		if (!mAttached) {
			mAttached = true;
			IntentFilter filter = new IntentFilter();

			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

			getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
		}

		mCalendar = new Time();
		onTimeChanged();
		counter.start();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mAttached) {
			counter.cancel();
			getContext().unregisterReceiver(mIntentReceiver);
			mAttached = false;
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		float hScale = 1.0f;
		float vScale = 1.0f;

		if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
			hScale = (float) widthSize / (float) mDialWidth;
		}

		if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
			vScale = (float) heightSize / (float) mDialHeight;
		}

		float scale = Math.min(hScale, vScale);

		setMeasuredDimension(
				resolveSize((int) (mDialWidth * scale), widthMeasureSpec),
				resolveSize((int) (mDialHeight * scale), heightMeasureSpec));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mChanged = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		boolean changed = mChanged;
		if (changed) {
			mChanged = false;
		}
		boolean seconds = mSeconds;
		if (seconds) {
			mSeconds = false;
		}
		
		int availableWidth = 394;
		int availableHeight = 396;

		int x = availableWidth / 2;
		int y = availableHeight / 2;

		Drawable dial = this.mDial;
		
		int w = dial.getIntrinsicWidth();
		int h = dial.getIntrinsicHeight();

		boolean scaled = false;

		if (availableWidth < w || availableHeight < h) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) w,
					(float) availableHeight / (float) h);
			canvas.save();
			canvas.scale(scale, scale, x, y);
		}

		if (changed) {
			dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
		}
		dial.draw(canvas);

		canvas.save();
		canvas.rotate(mHour / 12.0f * 360.0f, x, y);
		final Drawable hourHand = mHourHand;
		if (changed) {
			w = hourHand.getIntrinsicWidth();
			h = hourHand.getIntrinsicHeight();
			hourHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y	+ (h / 2));
		}
		hourHand.draw(canvas);
		canvas.restore();

		canvas.save();
		canvas.rotate(mMinutes / 60.0f * 360.0f, x, y);
		final Drawable minuteHand = mMinuteHand;
		if (changed) {
			w = minuteHand.getIntrinsicWidth();
			h = minuteHand.getIntrinsicHeight();
			minuteHand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y
					+ (h / 2));
		}
		minuteHand.draw(canvas);
		canvas.restore();
		canvas.save();
		canvas.rotate(mSecond, x, y);
		canvas.restore();
		if (scaled) {
			canvas.restore();
		}
	}

	MyCount counter = new MyCount(10000, 300);
	public class MyCount extends CountDownTimer {
		public MyCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			counter.start();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			mCalendar.setToNow();
			int hour = mCalendar.hour;
			int minute = mCalendar.minute;
			int second = mCalendar.second;

			mSecond = 6.0f * second;
			mSeconds = true;
			AnalogClock.this.invalidate();
		}
	}

	boolean mSeconds = false;
	float mSecond = 0;
	public void onTimeChanged() {
		mCalendar.setToNow();
		
		int hour = mCalendar.hour;
		int minute = mCalendar.minute;
		int second = mCalendar.second;

		mMinutes = minute + second / 60.0f;
		mHour = hour + mMinutes / 60.0f;
		mChanged = true;
	}

	private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				String tz = intent.getStringExtra("time-zone");
				mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
			}
			onTimeChanged();
			invalidate();
		}
	};
}