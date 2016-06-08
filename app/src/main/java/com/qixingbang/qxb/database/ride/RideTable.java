package com.qixingbang.qxb.database.ride;

/**
 * Created by zqj on 2016/6/7 15:28.
 */
public class RideTable {
    public final static String TABLE_NAME = "ride_table";

    public final static String COLUMN_RIDE_INFO = "ride_info";
    public final static String COLUMN_START_TIME = "start_time";

    public final static String CREATE_TABLE = "create table if not exists " + TABLE_NAME + "("
            + COLUMN_RIDE_INFO + " text," + COLUMN_START_TIME + " long primary key)";
}
