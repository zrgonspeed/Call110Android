package top.cnzrg.call110.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import top.cnzrg.call110.pojo.EmergencyContact;

/**
 * FileName: EmergencyContactDao
 * Author: ZRG
 * Date: 2019/8/27 15:53
 */
public class EmergencyContactDao {

    // 单例模式
    private static EmergencyContactDao mDao = null;
    private final EmergencyContactOpenHelper openHelper;

    public static EmergencyContactDao getInstance(Context context) {
        if (mDao == null)
            return new EmergencyContactDao(context);

        return mDao;

    }

    public EmergencyContactDao(Context context) {
        this.openHelper = new EmergencyContactOpenHelper(context);
    }

    /**
     * 插入数据
     *
     * @param contact
     */
    public Integer insert(EmergencyContact contact) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", contact.getName());
        values.put("phone", contact.getPhone());

        db.insert("contact", null, values);

        // 获取插入数据后的id
        String sql = "select last_insert_rowid() from contact";
        Cursor cursor = db.rawQuery(sql, null);
        int insertId = -1;
        if(cursor.moveToFirst()){
            insertId = cursor.getInt(0);
        }

        db.close();

        return insertId;
    }

    /**
     * 更新一条数据
     */
    public void update(EmergencyContact ec) {
        // 开启数据库，准备写入操作
        SQLiteDatabase db = openHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", ec.getName());
        values.put("phone", ec.getPhone());

        db.update("contact", values, "_id = ?", new String[]{ec.get_id() + ""});

        db.close();
    }

    /**
     * 查询contact表中所有数据
     *
     * @return
     */
    public List<EmergencyContact> selectAll() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        Cursor cursor = db.query("contact", new String[]{"_id", "name", "phone"}, null, null, null, null, "_id desc");

        ArrayList<EmergencyContact> contacts = new ArrayList<>();
        while (cursor.moveToNext()) {
            EmergencyContact contact = new EmergencyContact();
            contact.set_id(Integer.valueOf(cursor.getString(0)));
            contact.setName(cursor.getString(1));
            contact.setPhone(cursor.getString(2));

            contacts.add(contact);
        }
        cursor.close();
        db.close();

        return contacts;
    }

    /**
     * 删除contact表中所有数据
     *
     * @return
     */
    public void deleteAll() {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.delete("contact", null, null);

        db.close();

    }

    /**
     * 删除单条记录
     *
     * @return
     */
    public void delete(Integer _id) {
        SQLiteDatabase db = openHelper.getWritableDatabase();

        db.delete("contact", "_id = ?", new String[]{_id + ""});

        db.close();

    }
}
