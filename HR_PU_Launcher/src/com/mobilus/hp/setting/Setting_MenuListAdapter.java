package com.mobilus.hp.setting;

import java.util.List;

import com.mobilus.hp.launcher.HP_Manager;
import com.telechips.android.tdmb.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 확장 메뉴 리스트 Adapter
 * 
 * @author yhr
 *
 */
public abstract class Setting_MenuListAdapter extends ArrayAdapter<Setting_Menu> implements OnClickListener {
	public abstract void mClickEvent(int _id);

	private static final String CLASS_NAME = "[Setting_MenuListAdapter ]  ";

	/**
	 * Holder
	 */
	public ViewHolder mHolder;
	public LayoutInflater mInflater;
	public int mResID;
	public int mSelectedID = 0;

	/**
	 * List<Keyword>
	 */
	private final List<Setting_Menu> mListStr;

	static class ViewHolder {
		TextView btnSelect;
		TextView tvMenu;
	}

	public Setting_MenuListAdapter(Context context, int resource, List<Setting_Menu> objects) {
		super(context, resource, objects);
		mResID = resource;
		mInflater = ((Activity) context).getLayoutInflater();
		mListStr = objects;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			row = mInflater.inflate(mResID, parent, false);

			mHolder = new ViewHolder();
			mHolder.btnSelect = (TextView) row.findViewById(R.id.btnSelectButton);
			mHolder.btnSelect.setOnClickListener(this);
			mHolder.tvMenu = (TextView) row.findViewById(R.id.tvlistTitle);

			row.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) convertView.getTag();
		}

		mHolder.tvMenu.setText(mListStr.get(position).title);
		mHolder.btnSelect.setId(position);

		// kslee
		if (mListStr.get(position).title.length() <= 0)
			mHolder.btnSelect.setEnabled(false);

		/* 선택된 메뉴 리스트항목에 Hover 처리 */
		if (mSelectedID == position) {
			mHolder.btnSelect.setHovered(true);
			mHolder.tvMenu.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_black));
		} else {
			mHolder.btnSelect.setHovered(false);
			mHolder.tvMenu.setTextColor(HP_Manager.mContext.getResources().getColor(R.color.color_white));
		}
		return row;
	}

	@Override
	public void onClick(View v) {
		int _id = v.getId();

		if(Setting_Main.mHiddenMode == false)
		{
			if (HP_Manager.mSubMenu == _id)
				return;
		}

		mSelectedID = _id;
		HP_Manager.mSubMenu = _id;

		// 선택된 리스트 항목의 메뉴 화면으로 전환
		mClickEvent(_id);
		notifyDataSetChanged();
	}
}