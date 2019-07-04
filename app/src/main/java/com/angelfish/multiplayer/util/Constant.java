package com.angelfish.multiplayer.util;

public class Constant {
    public static final String DATABASE_NAME = "play_records.db";  // 数据库名称
    public static final int DATABASE_VERSION = 1;          //数据库版本
    public static final String TABLE_NAME = "PLAY_DATA";     //数据库表名
    /**
     *ID、TEMP、HUMIDITY、CO2、LAST_TIME、LAST_TIME 一下是数据库表中的字段
     */
    public static  final String ID = "id";                //id主键
    public static  final String NAME = "name";
    public static final String REMOTE_URL = "url";
    public static final String  FILE_NAME = "filename";
    public static final String REMOTE_ID = "remote_id";
    public static final String PLAY_TIMES = "play_times";
    public static final String EXPIRE_TIME = "expire_time"; // 失效时间
    public static final String TYPE = "type";
    public static final String SORT = "sort";
    public static final String LENGTH = "length";
}

