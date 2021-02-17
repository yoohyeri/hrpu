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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DxbDB_EWS {
	public static final String KEY_TYPE = "ews_type";
	public static final String KEY_PRIORITY = "ews_priority";
	public static final String KEY_TIME = "ews_time";
	public static final String KEY_LOCAL_TYPE= "ews_local_type";
	public static final String KEY_LOCAL_COUNT = "ews_local_count";
	public static final String KEY_LOCAL = "ews_local";
	public static final String KEY_MESSAGE_ID = "ews_message_id";
	public static final String KEY_MESSAGE = "ews_message";

	public static final String KEY_ROWID = "_id";

	private EWSDbAdapter mEWSDbAdapter;
	private SQLiteDatabase mSQLiteDatabase;

	private static final String DATABASE_NAME = "TDMB_EWS.db";
	private static final String DATABASE_TABLE = "ews";
	private static final int DATABASE_VERSION = 2;

	private static final String DATABASE_CREATE =
		"create table ews (_id integer primary key, "
			+ "ews_type text, ews_priority text, ews_time text, ews_local_type integer, ews_local_count integer, ews_local text, ews_message_id integer, ews_message text);";

	private final Context mContext;
	private static class EWSDbAdapter  extends SQLiteOpenHelper {

		EWSDbAdapter (Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL("DROP TABLE IF EXISTS ews");
			onCreate(database);
		}
	}

	public DxbDB_EWS(Context context) {
		mContext= context;
	}

	public DxbDB_EWS open() throws SQLException {
		mEWSDbAdapter= new EWSDbAdapter (mContext);
		mSQLiteDatabase= mEWSDbAdapter.getWritableDatabase();
		return this;
	}

	public void close()
	{
		mEWSDbAdapter.close();
	}

	public long insertEWS(String ews_type, String ews_priority, String ews_time, int ews_local_type, int ews_local_count, String ews_local, int ews_message_id, String ews_message)
	{
		ContentValues values= new ContentValues();
		values.put(KEY_TYPE, ews_type);
		values.put(KEY_PRIORITY, ews_priority);
		values.put(KEY_TIME, ews_time);
		values.put(KEY_LOCAL_TYPE, ews_local_type);
		values.put(KEY_LOCAL_COUNT, ews_local_count);
		values.put(KEY_LOCAL, ews_local);
		values.put(KEY_MESSAGE_ID, ews_message_id);
		values.put(KEY_MESSAGE, ews_message);

		return mSQLiteDatabase.insert(DATABASE_TABLE, null, values);
	}

	public Cursor searchEWSData(String ews_time, int ews_message_id) {
		Cursor cursor = mSQLiteDatabase.query(true, DATABASE_TABLE,
			new String[] {
							KEY_ROWID,
							KEY_TYPE,
							KEY_PRIORITY,
							KEY_TIME,
							KEY_LOCAL_TYPE,
							KEY_LOCAL_COUNT,
							KEY_LOCAL,
							KEY_MESSAGE_ID,
							KEY_MESSAGE,
							KEY_TYPE,
							},
				KEY_TIME + "=" + "'"+ews_time+"'" + " AND " + KEY_MESSAGE_ID + "=" + ews_message_id, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}
}