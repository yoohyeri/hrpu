package com.mobilus.hp.launcher;


import com.telechips.android.tdmb.DxbPlayer;
import com.telechips.android.tdmb.DxbView;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DMB_Widget extends Fragment{

	private static final String CLASS_NAME = "[DMB_Widget ]  ";
	public static TextView mIconChannel, mChannelName;
	public static LinearLayout llTitleBar;
	public static RelativeLayout mWeakSignalPopup, mWeakSignalFullModePopup;
	
	public static RelativeLayout rlWidgetBg;
	public static LinearLayout llWidgetBg, llWidgetDMBOff;
	public static ImageView imgWidgetNoti;
	public static TextView txtWidgetNoti;
	
	public DMB_Widget()
	{
		
	}
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	if(HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
    		return inflater.inflate(R.layout._kia_fragment_dmb, container, false);
    	
        return inflater.inflate(R.layout.dxb_widget, container, false);
    }
    
    @Override
   	public void onCreate(Bundle savedInstanceState) {
   		super.onCreate(savedInstanceState);
   	}

   	@Override
   	public void onDestroy() {
   		super.onDestroy();
   	}

   	@Override
   	public void onPause() {
   		super.onPause();
   	}

   	@Override
   	public void onResume() {
   		super.onResume();
   	}
  	   	
	@Override
   	public void onStart() {
		if(HP_Manager.mCurrentView == HP_Index.CURRENT_VIEW_HOME)
			setLoadView();
   		super.onStart();
   	}
   	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	private void setLoadView()
	{
		mIconChannel = (TextView) HP_Manager.mContext.findViewById(R.id.dmb_widget_icon);
		mChannelName = (TextView) HP_Manager.mContext.findViewById(R.id.dmb_widget_channel_name);
		llTitleBar = (LinearLayout) HP_Manager.mContext.findViewById(R.id.dmb_widget_title_bar);
		mWeakSignalPopup = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_toast_popup_layout);
		mWeakSignalFullModePopup = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_toast_full_popup_layout);
		
		rlWidgetBg = (RelativeLayout) HP_Manager.mContext.findViewById(R.id.widget_bg);
		
		llWidgetBg = (LinearLayout) HP_Manager.mContext.findViewById(R.id.widget_pre_image);
		imgWidgetNoti = (ImageView) HP_Manager.mContext.findViewById(R.id.widget_image);
		txtWidgetNoti = (TextView) HP_Manager.mContext.findViewById(R.id.widget_text);
		
		llWidgetDMBOff = (LinearLayout) HP_Manager.mContext.findViewById(R.id.widget_dmb_off);
		
		if( DxbView.gInformation.cCOMM.isEnable_Video && llTitleBar != null)
			llTitleBar.setVisibility(View.GONE);
		
		if(mChannelName != null)
		{
			if(DxbPlayer.getServiceName() == null)
				mChannelName.setText(HP_Manager.mContext.getResources().getString(R.string.dmb_title));
			else
				mChannelName.setText(DxbPlayer.getServiceName());
		}
		DxbView.setState(true, DxbView.eState);
	}
}
