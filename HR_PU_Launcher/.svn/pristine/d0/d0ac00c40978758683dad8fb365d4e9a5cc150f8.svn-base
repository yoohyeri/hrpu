package com.mobilus.hp.setting;

import java.util.ArrayList;
import java.util.List;

import com.mobilus.hp.launcher.HP_Index;
import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 리스트가 포함된 화면
 * 
 * @author yhr
 *
 */
public class Setting_Main_List extends Fragment {

	private static final String CLASS_NAME = "[Setting_Main_List ]  ";

	/**
	 * ListView
	 */
	private ListView lvMenuList;

	/**
	 * Screen Menu List Title
	 */
	private String[] strListTitle;

	/**
	 * Screen List
	 */
	public ArrayList<Setting_Menu> mMenuTitleList = new ArrayList<Setting_Menu>();

	/**
	 * Menu List Adapter
	 */
	private ScreenMenuListAdapter mMenuListAdapter;

	/**
	 * Menu Type --> Type에 따라 메뉴 리스트가 바뀜
	 */
	private static int mMenuType;

	public Setting_Main_List() {
	}

	/**
	 * Menu type에 따라 리스트의 메뉴들이 바뀐다.
	 * 
	 * @param menu_type
	 */
	public Setting_Main_List(int menu_type, int submenu) {
		mMenuType = menu_type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			return inflater.inflate(R.layout._kia_fragment_setting_list, container, false);

		return inflater.inflate(R.layout.fragment_setting_list, container, false);
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

		if (HP_Manager.mCurrentView != HP_Index.CURRENT_VIEW_SETTING)
			return;
		setLoadView();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	/**
	 * GUI초기화
	 */
	private void setLoadView() {
		lvMenuList = (ListView) HP_Manager.mContext.findViewById(R.id.menu_list);
		
		/* 메뉴 타입에 따라 리스트 변경 */
		if (mMenuType == HP_Index.ROOT_MENU_SCREEN)
			strListTitle = HP_Manager.mContext.getResources().getStringArray(R.array.screen_menu_list);
		else if (mMenuType == HP_Index.ROOT_MENU_SOUND)
			strListTitle = HP_Manager.mContext.getResources().getStringArray(R.array.sound_menu_list);
		else
			strListTitle = HP_Manager.mContext.getResources().getStringArray(R.array.system_menu_list);

		if (HP_Manager.Vendor.equals(HP_Manager.SYSTEM_VENDOR_KIA))
			mMenuListAdapter = new ScreenMenuListAdapter(HP_Manager.mContext, R.layout._kia_layout_setting_list_item, mMenuTitleList);
		else
			mMenuListAdapter = new ScreenMenuListAdapter(HP_Manager.mContext, R.layout.layout_setting_list_item, mMenuTitleList);
		lvMenuList.setAdapter(mMenuListAdapter);

		mMenuTitleList.clear();
		for (int i = 0; i < strListTitle.length; i++) {
			Setting_Menu strTitle = new Setting_Menu();
			strTitle.idx = i;
			strTitle.title = strListTitle[i];
			mMenuTitleList.add(strTitle);
		}
		mMenuListAdapter.mSelectedID = HP_Manager.mSubMenu;
		mMenuListAdapter.notifyDataSetChanged();
		HP_Manager.callListItemFragment(mMenuType, HP_Manager.mSubMenu);
	}

	/**
	 * Ext Menu Click Event
	 * 
	 * @author yhr
	 */
	private class ScreenMenuListAdapter extends Setting_MenuListAdapter {

		public ScreenMenuListAdapter(Context context, int resource, List<Setting_Menu> objects) {
			super(context, resource, objects);
		}

		@Override
		public void mClickEvent(int _id) {
			HP_Manager.callListItemFragment(mMenuType, _id);
		}
	}
}