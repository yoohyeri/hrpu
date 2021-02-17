/*
 * Copyright (C) 2013 Telechips, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.telechips.android.tdmb;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class DxbAdapter_Service extends SimpleCursorAdapter
{
	private AsyncQueryHandler mQueryHandler;
	private String mConstraint = null;
	private boolean mConstraintIsValid = false;
	private Cursor mCursor;

	class ViewHolder
	{
		TextView rlListItem; 	
		TextView index;
		TextView name;
		TextView indicator;
	}

	class QueryHandler extends AsyncQueryHandler
	{
		QueryHandler(ContentResolver res)
		{
			super(res);
		}

		@Override
		protected void onQueryComplete(int token, Object cookie, Cursor cursor)
		{
			
		}
	}

	public DxbAdapter_Service(Context context, int layout, Cursor c, String[] from, int[] to)
	{
		super(context, layout, c, from, to);
	
		mCursor = c;
		mQueryHandler = new QueryHandler(context.getContentResolver());
		DxbPlayer.setService_ColumnIndex(c);
	}

	public AsyncQueryHandler getQueryHandler()
	{
		return mQueryHandler;
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		View v = super.newView(context, cursor, parent);
		ViewHolder vh = new ViewHolder();
		
		vh.rlListItem = (TextView) v.findViewById(R.id.rl_list_item);
		vh.index = (TextView) v.findViewById(R.id.list_row_index);
		vh.name	= (TextView) v.findViewById(R.id.list_row_name);
		vh.indicator = (TextView) v.findViewById(R.id.list_row_indicator);
		v.setTag(vh);
		
		return v;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.SimpleCursorAdapter#bindView(android.view.View,
	 * android.content.Context, android.database.Cursor)
	 */
	@Override
	public void bindView(View view, Context context, Cursor cursor)
	{
		ViewHolder vh = (ViewHolder) view.getTag();
		DxbPlayer.bindView(cursor, vh);
	}

	@Override
	public void changeCursor(Cursor cursor)
	{
		super.changeCursor(cursor);
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint)
	{
		String s = constraint.toString();
		if(	mConstraintIsValid && ((s == null && mConstraint == null) || (s != null && s.equals(mConstraint))))
			return getCursor();
	
		Cursor c = mCursor;
		mConstraint = s;
		mConstraintIsValid = true;
		return c;
	}
}