package com.mobilus.hp.launcher;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.accessibility.AccessibilityEvent;

import com.mobilus.hp.launcher.HP_Timer;
import com.mobilus.hp.launcher.HP_Timer.TimerListener;;

public class LongClickEvent extends View implements OnLongClickListener{

	private String CLASS_NAME = "[LongClickEvent ]  ";
	
	
	private static final long LONG_EVENT_DELAY_TIME = 200;
    private static final long START_COUNT = (500/LONG_EVENT_DELAY_TIME);
    private LongClickCallBack mLongClickEventCallback = null;
    private View view = this;
	private long mRepeatCount = 0;
	
	private HP_Timer mTimer = new HP_Timer(0, LONG_EVENT_DELAY_TIME, new TimerListener() {
		
		@Override
		public void doAction() {
			mRepeatCount++;
			if (mRepeatCount > START_COUNT) {
				if(view.isEnabled()) {
					mLongClickEventCallback.onLongPress(view);
				}
			}
		}
	});
	
    public LongClickEvent(Context context) {
		super(context);
		init();
	}

	public LongClickEvent(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public LongClickEvent(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	private void init(){
		view = this;
		setOnLongClickListener(this);
	}
	
	public void setLongClickCallback(LongClickCallBack callback){
		mLongClickEventCallback = callback;
	}
	
	public LongClickCallBack getLongTouchCallback(){
		return mLongClickEventCallback;
	}
	
	public interface LongClickCallBack{
		public void onLongPress(View view);
	}
	
	@Override
	public boolean onLongClick(View view) {
		return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
        float y = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mRepeatCount = 0;
			if (isLongClickable())
				mTimer.startTimer();
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			mTimer.removeTimer();

			if (hasOnClickListeners()) {
				; // performClick() called at View class.
			} else {
				if(view.isEnabled()) {
					sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
					playSoundEffect(SoundEffectConstants.CLICK);
				}
			}

			if (mRepeatCount > START_COUNT) {
				; // Callback is called more than once.
			} else {
				if(view.isEnabled()) {
					mLongClickEventCallback.onLongPress(view);
				}
			}
			break;
		default:
			mTimer.removeTimer();
		}
		return super.onTouchEvent(event);
	}
}