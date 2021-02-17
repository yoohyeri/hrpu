package com.mobilus.hp.launcher;

import com.mobilus.hp.setting.screen.Screen_Adjust;
import com.mobilus.hp.setting.sound.Sound_Tone;
import com.telechips.android.tdmb.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Custom Seekbar
 * 
 * 가운데에서 시작/ 좌우로 이동시 이동한값만큼 지정된 색으로 표기
 * Thumb에 Text출력
 * 
 * @author yhr
 *
 */
public class CustomSeekBar extends SeekBar{
	private static final String CLASS_NAME = "[CustomSeekBar ]  ";
	
	private Rect rect;
	private Paint paint ;
	private int seekbar_height; 
	private TextView txtProgress;
	private String mThumbText;
	private Paint mTextPaint;
	
	public CustomSeekBar(Context context)
	{
		super(context);
	}
	
	public CustomSeekBar(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		rect = new Rect();
		paint = new Paint();
		seekbar_height = 8;
		
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setTextAlign(Align.LEFT);
		mTextPaint.setColor(Color.BLACK);
		mTextPaint.setTextSize(26);
	}
	
	public CustomSeekBar(Context context, AttributeSet attrs, int defStyle) {
	    super(context, attrs, defStyle);
	}
	
	public void setThumbText(TextView textview, String thumbText){
		txtProgress = textview;
		mThumbText = thumbText;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(mThumbText == null)
			mThumbText = "";

		//text
		RectF thumbRect = new RectF(getThumb().getBounds());
		thumbRect.right = mTextPaint.measureText(mThumbText);
		thumbRect.bottom = mTextPaint.descent() - mTextPaint.ascent();
		thumbRect.left += (getThumb().getBounds().width() - thumbRect.right)/2.0f;
		thumbRect.top += (getThumb().getBounds().height() - thumbRect.bottom)/2.0f;
		
		//progressbar
		rect.set(0 + getThumbOffset(), 
				(getHeight() / 2) - (seekbar_height/2), 
				getWidth()- getThumbOffset(), 
				(getHeight() / 2) + (seekbar_height/2));
		paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_bg_hyundai));
		canvas.drawRect(rect, paint);
				 
		if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SCREEN)
		{			
			// Thumb에 Text 출력
			Screen_Adjust.setText(txtProgress, mThumbText, thumbRect.left+12);
			
			if (this.getProgress() > 5) {
				 rect.set(getWidth() / 2,
						 (getHeight() / 2) - (seekbar_height/2), 
						 getWidth() / 2 + (getWidth() / 10) * (getProgress() - 5)-2,
						 getHeight() / 2 + (seekbar_height/2));
				 
				 if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_hyundai));
				 else
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_kia));
				 
				 canvas.drawRect(rect, paint);
			 }

			 if (this.getProgress() < 5) {
				 rect.set((getWidth() / 2 - ((getWidth() / 10) * (5 - getProgress()))+2),
						 (getHeight() / 2) - (seekbar_height/2),
						 getWidth() / 2,
						 getHeight() / 2 + (seekbar_height/2));
				 if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_hyundai));
				 else
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_kia));
				 canvas.drawRect(rect, paint);
			 }
		}
		else if(HP_Manager.mRootMenu == HP_Index.ROOT_MENU_SOUND)
		{
			// Thumb에 Text 출력
			Sound_Tone.setText(txtProgress, mThumbText, thumbRect.left+12);
			
			if (this.getProgress() > 10) 
			{
				 rect.set(getWidth() / 2, (getHeight() / 2) - (seekbar_height/2), 
						 getWidth() / 2 + (getWidth() / 20) * (getProgress() - 10),
						 getHeight() / 2 + (seekbar_height/2));
				 if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_hyundai));
				 else
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_kia));
				 canvas.drawRect(rect, paint);
			 }

			 if (this.getProgress() < 10) 
			 {
				 rect.set(getWidth() / 2 - ((getWidth() / 20) * (10 - getProgress())),
						 (getHeight() / 2) - (seekbar_height/2), getWidth() / 2,
						 getHeight() / 2 + (seekbar_height/2));
				 if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_HYUNDAI))
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_hyundai));
				 else
					 paint.setColor(HP_Manager.mContext.getResources().getColor(R.color.hrpu_seekbar_fill_kia));
				 canvas.drawRect(rect, paint);
			 }
		}
		 super.onDraw(canvas);
	}
}