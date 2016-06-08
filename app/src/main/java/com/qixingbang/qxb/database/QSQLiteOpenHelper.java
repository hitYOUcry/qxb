package com.qixingbang.qxb.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qixingbang.qxb.database.ride.RideTable;

/**
 * Created by zqj on 2016/6/7 15:24.
 */
public class QSQLiteOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "qxb.db";
    public static final int DATABASE_VERSION = 1;

    public QSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RideTable.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
