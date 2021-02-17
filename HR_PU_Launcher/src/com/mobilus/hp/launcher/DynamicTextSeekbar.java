package com.mobilus.hp.launcher;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class DynamicTextSeekbar extends SeekBar{

    private String CLASS_NAME = "[DynamicTextSeekbar ]  ";
	private Paint mTextPaint;
	private static String mThumbText;
	
	public DynamicTextSeekbar(Context context) {
		super(context);
		init(context);
	}

	public DynamicTextSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(context);
	}
	
	public DynamicTextSeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init(context);
	}
	
	private void init(Context context){
		mContext = context;
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(26);
		
//		if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_DMB)
//			mTextPaint.setTextSize(16);
//		else
//			mTextPaint.setTextSize(26);
	}
	
	public void setThumbText(String thumbText){
		mThumbText = thumbText;
	}
	
	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if(LauncherMainActivity.getInstance().mVolumeDialog.isShowing())
		{
			if(HP_Manager.mDMBMuteStatus == HP_Index.DMB_SOUND_UNMUTE)
				mThumbText = String.valueOf(HP_Manager.mCurrentDMBVol);
			else
				mThumbText = "0";
		}
		
		RectF thumbRect = new RectF(getThumb().getBounds());
		thumbRect.right = mTextPaint.measureText(mThumbText);
		thumbRect.bottom = mTextPaint.descent() - mTextPaint.ascent();
		thumbRect.left += (getThumb().getBounds().width() - thumbRect.right)/2.0f;
		thumbRect.top += (getThumb().getBounds().height() - thumbRect.bottom)/2.0f;
		canvas.drawText(mThumbText, thumbRect.left+12, thumbRect.top - mTextPaint.ascent(), mTextPaint);
	}
}
