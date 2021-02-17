package com.telechips.android.tdmb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ChannelManager {
	public static final String KEY_ENSEMBLE_NAME = "ensembleName";
	public static final String KEY_ENSEMBLE_ID = "ensembleID";
	public static final String KEY_ENSEMBLE_FREQ = "ensembleFreq";
	public static final String KEY_SERVICE_NAME = "serviceName";
	public static final String KEY_SERVICE_ID = "serviceID";
	public static final String KEY_CHANNEL_NAME = "channelName";
	public static final String KEY_CHANNEL_ID = "channelID";
	public static final String KEY_TYPE = "type";
	public static final String KEY_BITRATE = "bitrate";
	public static final String KEY_REG_0 = "reg0";
	public static final String KEY_REG_1 = "reg1";
	public static final String KEY_REG_2 = "reg2";
	public static final String KEY_REG_3 = "reg3";
	public static final String KEY_REG_4 = "reg4";
	public static final String KEY_REG_5 = "reg5";
	public static final String KEY_REG_6 = "reg6";

	public static final String KEY_ROWID = "_id";

	public static final int	SERVICE_TYPE_DTV	= 0x01;
	public static final int	SERVICE_TYPE_MHDTV	= 0x11;
	public static final int	SERVICE_TYPE_ASDTV	= 0x16;
	public static final int	SERVICE_TYPE_AHDTV	= 0x19;
	
	public static final int	SERVICE_TYPE_DRADIO		= 0x02;
	public static final int	SERVICE_TYPE_FMRADIO	= 0x07;
	public static final int	SERVICE_TYPE_ADRADIO	= 0x0A;

	private ChannelDbAdapter mChannelDbAdapter;
	private SQLiteDatabase mSQLiteDatabase;

	private static final int DATABASE_VERSION = 2;
	
	private static final String HMS_DATABASE_NAME = "HMS_TDMB.db";
	private static final String HMS_DATABASE_TABLE = "hms_channels";

	private static final String HMS_DATABASE_CREATE =
			"create table " + HMS_DATABASE_TABLE + " (_id integer primary key, "
			+ "ensembleName text not null, ensembleID integer, ensembleFreq integer, serviceName text not null, serviceID integer, channelName text, channelID integer, type integer, bitrate integer, reg0 integer, reg1 integer, reg2 integer, reg3 integer, reg4 integer, reg5 integer, reg6 integer);";
	
	private final Context mContext;
	private static class ChannelDbAdapter  extends SQLiteOpenHelper {
	
		ChannelDbAdapter (Context context) {
			super(context, HMS_DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase database) {
			database.execSQL(HMS_DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
			database.execSQL("DROP TABLE IF EXISTS channels");
			onCreate(database);
		}
	}
	
	public ChannelManager(Context context) {
		mContext= context;
	}

	public ChannelManager open() throws SQLException {
		mChannelDbAdapter= new ChannelDbAdapter (mContext);
		mSQLiteDatabase= mChannelDbAdapter.getWritableDatabase();
		return this;
	}

	public void close()
	{
		mChannelDbAdapter.close();
	}
	
	public long insertChannel2(String ensembleName, int ensembleID, int ensembleFreq, String serviceName, int serviceID, String channelName, int channelID, int type, int bitrate, int reg0, int reg1, int reg2, int reg3, int reg4, int reg5, int reg6) {
		
		ContentValues values= new ContentValues();
		values.put(KEY_ENSEMBLE_NAME, ensembleName);
		values.put(KEY_ENSEMBLE_ID, ensembleID);
		values.put(KEY_ENSEMBLE_FREQ, ensembleFreq);
		values.put(KEY_SERVICE_NAME, serviceName.toUpperCase());
		values.put(KEY_SERVICE_ID, serviceID);
		values.put(KEY_CHANNEL_NAME, channelName.toUpperCase());
		values.put(KEY_CHANNEL_ID, channelID);
		values.put(KEY_TYPE, type);
		values.put(KEY_BITRATE, bitrate);
		values.put(KEY_REG_0, reg0);
		values.put(KEY_REG_1, reg1);
		values.put(KEY_REG_2, reg2);
		values.put(KEY_REG_3, reg3);
		values.put(KEY_REG_4, reg4);
		values.put(KEY_REG_5, reg5);
		values.put(KEY_REG_6, reg6);
		
		return mSQLiteDatabase.insert(HMS_DATABASE_TABLE, null, values);
	}

	public boolean updateChannel2(long rowId, String ensembleName, int ensembleID, int ensembleFreq, String serviceName, int serviceID, String channelName, int channelID, int type, int bitrate, int reg0, int reg1, int reg2, int reg3, int reg4, int reg5, int reg6) {
		ContentValues values= new ContentValues();
		values.put(KEY_ENSEMBLE_NAME, ensembleName);
		values.put(KEY_ENSEMBLE_ID, ensembleID);
		values.put(KEY_ENSEMBLE_FREQ, ensembleFreq);
		values.put(KEY_SERVICE_NAME, serviceName.toUpperCase());
		values.put(KEY_SERVICE_ID, serviceID);
		values.put(KEY_CHANNEL_NAME, channelName.toUpperCase());
		values.put(KEY_CHANNEL_ID, channelID);
		values.put(KEY_TYPE, type);
		values.put(KEY_BITRATE, bitrate);
		values.put(KEY_REG_0, reg0);
		values.put(KEY_REG_1, reg1);
		values.put(KEY_REG_2, reg2);
		values.put(KEY_REG_3, reg3);
		values.put(KEY_REG_4, reg4);
		values.put(KEY_REG_5, reg5);
		values.put(KEY_REG_6, reg6);

		return mSQLiteDatabase.update(HMS_DATABASE_TABLE, values, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteChannel2(long rowId) {
		return mSQLiteDatabase.delete(HMS_DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public boolean deleteAllChannel2() {
		return mSQLiteDatabase.delete(HMS_DATABASE_TABLE, null, null) > 0;
	}

	public Cursor getAllChannels2() {
		return mSQLiteDatabase.query(
				HMS_DATABASE_TABLE,
			new String[] {
							KEY_ROWID,
							KEY_ENSEMBLE_NAME,
							KEY_ENSEMBLE_ID,
							KEY_ENSEMBLE_FREQ,
							KEY_SERVICE_NAME,
							KEY_SERVICE_ID,
							KEY_CHANNEL_NAME,
							KEY_CHANNEL_ID,
							KEY_TYPE,
							KEY_BITRATE,
							KEY_REG_0,
							KEY_REG_1,
							KEY_REG_2,
							KEY_REG_3,
							KEY_REG_4,
							KEY_REG_5,
							KEY_REG_6
							},
				//null, null, null, null, KEY_SERVICE_NAME+" ASC");
				null, null, null, null,
				null);
	}
	
	public Cursor getTypeChannels2(int nType) throws SQLException {
		return mSQLiteDatabase.query(HMS_DATABASE_TABLE,
			new String[] {
							KEY_ROWID,
							KEY_ENSEMBLE_NAME,
							KEY_ENSEMBLE_ID,
							KEY_ENSEMBLE_FREQ,
							KEY_SERVICE_NAME,
							KEY_SERVICE_ID,
							KEY_CHANNEL_NAME,
							KEY_CHANNEL_ID,
							KEY_TYPE,
							KEY_BITRATE,
							KEY_REG_0,
							KEY_REG_1,
							KEY_REG_2,
							KEY_REG_3,
							KEY_REG_4,
							KEY_REG_5,
							KEY_REG_6
							},
				KEY_TYPE + "=" + nType, null, null, null, KEY_SERVICE_NAME+" ASC");
	}
	
	public Cursor getChannel2(long rowId) throws SQLException {
		Cursor cursor = mSQLiteDatabase.query(true, HMS_DATABASE_TABLE,
			new String[] {
							KEY_ROWID,
							KEY_ENSEMBLE_NAME,
							KEY_ENSEMBLE_ID,
							KEY_ENSEMBLE_FREQ,
							KEY_SERVICE_NAME,
							KEY_SERVICE_ID,
							KEY_CHANNEL_NAME,
							KEY_CHANNEL_ID,
							KEY_TYPE,
							KEY_BITRATE,
							KEY_REG_0,
							KEY_REG_1,
							KEY_REG_2,
							KEY_REG_3,
							KEY_REG_4,
							KEY_REG_5,
							KEY_REG_6
							},
				KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if (cursor != null) {
			cursor.moveToFirst();
		}
		return cursor;
	}  
}