package top.cnzrg.call110.util;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * FileName: TestNetworkUtils
 * Author: ZRG
 * Date: 2019/8/3 20:38
 */
@RunWith(AndroidJUnit4.class)
public class TestNetworkUtils {
    private static final String TAG = "TestNetworkUtils";

    @Test
    public void getMobileDbm() {
        Context appContext = InstrumentationRegistry.getTargetContext();

        String mobileDbm = NetworkUtils.getMobileDbm2(appContext);

        LogUtils.e("信号强度: " + mobileDbm);
    }

    @Test
    public void isNetworkConnected() {
    }

    @Test
    public void isMobileConnected() {
    }

    @Test
    public void getConnectedType() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.i(TAG, "getConnectedType: " + NetworkUtils.getConnectedType(appContext));

    }

    @Test
    public void getOperatorName() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.i(TAG, "getOperatorName: " + NetworkUtils.getOperatorName(appContext));

    }

    @Test
    public void getNetworkType() {
        Log.i(TAG, "getNetworkType: " + NetworkUtils.getNetworkType(InstrumentationRegistry.getTargetContext()));
    }

    @Test
    public void getAPNType() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.i(TAG, "getAPNType: " + NetworkUtils.getAPNType(appContext));
    }

    @Test
    public void isGPSEnabled() {
        Context appContext = InstrumentationRegistry.getTargetContext();
        Log.i(TAG, "isGPSEnabled: " + NetworkUtils.isGPSEnabled(appContext));
    }
}