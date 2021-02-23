package top.cnzrg.call110.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * FileName: InfoUtils
 * Author: ZRG
 * Date: 2019/7/30 17:29
 */
public class InfoUtils {
    private static final String TAG = "InfoUtils";

    /**
     * 获取当前本地apk的版本
     *
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        LogUtils.i("版本号: " + versionCode);

        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.i("版本号名称: " + verName);
        return verName;
    }

    /**
     * 获取电话号码
     *
     * @return
     */
    public static String getPhoneNumber(TelephonyManager telephonyManager) {
        // 获取手机号码
        // String deviceid = tm.getDeviceId();// 获取智能设备唯一编号

        @SuppressLint("MissingPermission") String localPhoneNumer = telephonyManager.getLine1Number();// 获取本机号码
        @SuppressLint("MissingPermission") String simSN = telephonyManager.getSimSerialNumber();// 获得SIM卡的序号

        // String imsi = tm.getSubscriberId();// 得到用户Id

        LogUtils.i("本机号码: " + localPhoneNumer);
        LogUtils.i("本机SIM卡序号: " + simSN);

        if (localPhoneNumer == null || localPhoneNumer.trim().isEmpty()) {
            return simSN;
        }
        return localPhoneNumer;
    }

    /**
     * 获取上传报警信息的url
     *
     * @param context
     * @return
     */
    public static String getUploadURL(Context context) {
        Properties properties = new Properties();
        String UPLOAD_URI = "";
        String UPLOAD_ACTION = "";

        try {
            properties.load(context.getAssets().open("upload.properties"));
            UPLOAD_URI = properties.getProperty("UPLOAD_URI");
            UPLOAD_ACTION = properties.getProperty("UPLOAD_ACTION");

//            LogUtils.e("UPLOAD_URI:" + UPLOAD_URI);
//            LogUtils.e("UPLOAD_ACTION:" + UPLOAD_ACTION);
//            LogUtils.e("UPLOAD_URL:" + UPLOAD_URI + UPLOAD_ACTION);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return UPLOAD_URI + UPLOAD_ACTION;
    }
}
