package top.cnzrg.call110.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * FileName: TestUploadURL
 * Author: ZRG
 * Date: 2019/9/2 9:50
 */
@RunWith(AndroidJUnit4.class)
public class TestUploadURL {
    private static final String TAG = "TestUploadURL";

    @Test
    public void getUploadURLFromProperties() {
        LogUtils.i("单元测试: " + TAG);

        Context context = InstrumentationRegistry.getTargetContext();
        String uploadURL = InfoUtils.getUploadURL(context);

        LogUtils.i("uploadURL: " + uploadURL);

    }
}
