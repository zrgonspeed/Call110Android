package top.cnzrg.call110;

import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * FileName: TestCurrentTime
 * Author: ZRG
 * Date: 2019/8/3 11:10
 */
@RunWith(AndroidJUnit4.class)
public class TestCurrentTime {
    private static final String TAG = "TestCurrentTime";

    /**
     * 在这个测试里循环只执行一次，在app里就正常1秒1次
     */
    @Test
    public void showTime() {
        Log.i(TAG, "showTime: 方法运行");
        
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String str = sdf.format(new Date());
                        Log.i(TAG, "run: 当前时间: " + str);
                        // 有问题，循环只执行了一次
                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
