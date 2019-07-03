package com.angelfish.multiplayer.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
/**
 * Created by 11929 on 2018/3/11.
 * 主要对数据库的操作工具类
 */
public class DbManager {

    private static MySqliteHelper helper; //建立一个数据库对象

    /**
     *
     * @param context 本类的上下文对象
     * @return
     */
    public static MySqliteHelper getIntance(Context context){
        if (helper == null){
            helper = new MySqliteHelper(context);
        }
        return helper;
    }
    /**
     * 查找方法
     * 返回的是一个Cursor对象
     * selectionArgs 查询条件占位符
     */
    public static Cursor selectSQL(SQLiteDatabase db, String sql, String[] selectionArgs){
        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(sql,selectionArgs);
        }
        return cursor;
    }
    /**
     * 删改数据库
     * @param db  数据库对象
     * @param sql 删改语句
     */
    public static void execSQL(SQLiteDatabase db, String sql){
        if (db!=null) {
            if (sql != null && !"".equals(sql)) {
                db.execSQL(sql);
            }
        }
    }

    /**
     * 将Curcor对象转化成list集合
     * @param cursor 游标
     * @return 集合对象
     */
    public static ArrayList<Play_Data> cursorToList(Cursor cursor){
        ArrayList<Play_Data> list = new ArrayList<>();
        while (cursor.moveToNext()){   //判断游标是否有下一个字段
            //getColumnIndext作用是返回给定字符串的下标(指的是int类型)
            int columnIndex = cursor.getColumnIndex(Constant.ID);
            //通过下标找到指定value
            int id = cursor.getInt(columnIndex);  // 获取id
            String name = cursor.getString( cursor.getColumnIndex(Constant.NAME));
            String remote_url = cursor.getString( cursor.getColumnIndex(Constant.REMOTE_URL));
            String filename = cursor.getString( cursor.getColumnIndex(Constant.FILE_NAME));
            int remote_id = cursor.getInt( cursor.getColumnIndex(Constant.REMOTE_ID));
            int play_times = cursor.getInt( cursor.getColumnIndex(Constant.PLAY_TIMES));
            long expire_time = (long)cursor.getInt( cursor.getColumnIndex(Constant.EXPIRE_TIME));
            int type = cursor.getInt( cursor.getColumnIndex(Constant.TYPE));
            int sort = cursor.getInt( cursor.getColumnIndex(Constant.SORT));
            Play_Data th_data = new Play_Data(id,name,remote_url,filename, remote_id, play_times, expire_time, type, sort);
            list.add(th_data);
        }
        return list;
    }
}
