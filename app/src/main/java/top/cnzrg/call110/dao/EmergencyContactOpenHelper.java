package top.cnzrg.call110.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * FileName: EmergencyContactOpenHelper
 * Author: ZRG
 * Date: 2019/8/27 15:58
 */
public class EmergencyContactOpenHelper extends SQLiteOpenHelper {
    public EmergencyContactOpenHelper(Context context) {
        super(context, "emergency.db", null, 1);
    }

    /**
     * 创建数据库中的表
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("    create table contact(\n" +
                "           _id integer primary key autoincrement,\n" +
                "           name varchar(5),\n" +
                "           phone varchar(20)\n" +
                "    );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
