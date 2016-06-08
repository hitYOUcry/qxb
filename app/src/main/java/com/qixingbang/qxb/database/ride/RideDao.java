package com.qixingbang.qxb.database.ride;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.qixingbang.qxb.beans.mine.map.RideInfo;
import com.qixingbang.qxb.database.QSQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by zqj on 2016/6/7 15:34.
 */
public class RideDao {
    private QSQLiteOpenHelper mOpenHelper;
    private SQLiteDatabase mDatabase;

    public RideDao(Context context) {
        mOpenHelper = new QSQLiteOpenHelper(context);
        mDatabase = mOpenHelper.getWritableDatabase();
    }

    public void add(RideInfo rideInfo) {
        mDatabase.insert(RideTable.TABLE_NAME, null, getRideContentValues(rideInfo));
    }

    public List<RideInfo> getAll() {
        Cursor cursor = mDatabase.query(RideTable.TABLE_NAME, null, null, null, null, null, null);
        List<RideInfo> rideInfos = new ArrayList<>();
        if (null != cursor) {
            try {
                while (cursor.moveToNext()) {
                    RideInfo rideInfo = getRideInfoFromCursor(cursor);
                    rideInfos.add(rideInfo);
                }
            } catch (Exception e) {
            } finally {
                cursor.close();
            }
        }
        Collections.sort(rideInfos);
        return rideInfos;
    }

    private RideInfo getRideInfoFromCursor(Cursor cursor) {
        String rideInfo = cursor.getString(cursor.getColumnIndex(RideTable.COLUMN_RIDE_INFO));
        return RideInfo.fromString(rideInfo);
    }

    private ContentValues getRideContentValues(RideInfo rideInfo) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(RideTable.COLUMN_START_TIME, rideInfo.getStartTime());
        contentValues.put(RideTable.COLUMN_RIDE_INFO, rideInfo.toJson().toString());
        return contentValues;
    }

    public void deleteAll() {
        mDatabase.delete(RideTable.TABLE_NAME, null, null);
    }

    public void close() {
        mDatabase.close();
    }
}
