package top.cnzrg.call110.dao;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import top.cnzrg.call110.pojo.EmergencyContact;
import top.cnzrg.call110.util.LogUtils;

/**
 * FileName: EmergencyContactDaoTest
 * Author: ZRG
 * Date: 2019/8/27 16:23
 */
@RunWith(AndroidJUnit4.class)
public class EmergencyContactDaoTest {

    @Test
    public void update() {
        LogUtils.i("单元测试, 更新紧急联系人数据");
        Context appContext = InstrumentationRegistry.getTargetContext();
        EmergencyContactDao dao = EmergencyContactDao.getInstance(appContext);

        EmergencyContact ec = new EmergencyContact();
        ec.set_id(161);
        ec.setName("雨碎江南gg");
        ec.setPhone("1230622");

        dao.update(ec);
    }

    @Test
    public void insert() {
        LogUtils.i("单元测试, 向紧急联系人数据库插入数据");
        Context appContext = InstrumentationRegistry.getTargetContext();

        EmergencyContactDao dao = EmergencyContactDao.getInstance(appContext);

        for (int i = 0; i < 5; i++) {
            EmergencyContact ec = new EmergencyContact();
            ec.setName("顺大佬" + i);
            ec.setPhone("6399093" + i);
            int lastId  = dao.insert(ec);
            LogUtils.e("lastId: " + lastId);
        }

    }

    @Test
    public void selectAll() {
        LogUtils.i("单元测试, 查询所有紧急联系人");
        Context appContext = InstrumentationRegistry.getTargetContext();

        EmergencyContactDao dao = EmergencyContactDao.getInstance(appContext);

        List<EmergencyContact> contacts = dao.selectAll();

        LogUtils.w("contacts size: " + contacts.size());
        for (EmergencyContact contact : contacts) {
            System.out.println(contact);
        }

    }

    @Test
    public void deleteAll() {
        LogUtils.i("单元测试, 删除所有紧急联系人");
        Context appContext = InstrumentationRegistry.getTargetContext();

        EmergencyContactDao dao = EmergencyContactDao.getInstance(appContext);

        dao.deleteAll();

    }

    @Test
    public void delete() {
        LogUtils.i("单元测试, 删除单个指定的紧急联系人");
        Context appContext = InstrumentationRegistry.getTargetContext();

        EmergencyContactDao dao = EmergencyContactDao.getInstance(appContext);

        dao.delete(184);

    }
}
