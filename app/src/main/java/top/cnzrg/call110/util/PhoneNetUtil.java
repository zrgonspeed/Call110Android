package top.cnzrg.call110.util;

/**
 * FileName: PhoneNetUtil
 * Author: ZRG
 * Date: 2019/8/13 10:53
 */

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 获取手机数据信号强度值工具类
 * dbm的值为负数
 * 0为最强信号值
 * -85以内为满格信号
 */
public class PhoneNetUtil {
    private static final int DBM_1 = -85;
    private static final int DBM_2 = -95;
    private static final int DBM_3 = -105;
    private static final int DBM_4 = -115;
    private static final int DBM_5 = -140;
    private static final String TAG = "PhoneNetUtil";
    private static PhoneNetUtil phoneNetUtil;
    private Context context;
    private PhoneNetListener phoneNetListener;
    private PhoneNetLevelListener phoneNetLevelListener;

    private PhoneNetUtil(Context context) {
        this.context = context;
        getNetDBM(context);
    }

    public static PhoneNetUtil getInstance(Context context) {
        if (phoneNetUtil == null) {
            phoneNetUtil = new PhoneNetUtil(context);
        }
        return phoneNetUtil;
    }

    public void setPhoneNetListener(PhoneNetListener phoneNetListener) {
        this.phoneNetListener = phoneNetListener;
    }

    public void setPhoneNetLevelListener(PhoneNetLevelListener phoneNetLevelListener) {
        this.phoneNetLevelListener = phoneNetLevelListener;
    }

    /**
     * 得到当前手机4G信号强度值dbm
     */
    public void getNetDBM(Context context) {
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onSignalStrengthsChanged(SignalStrength signalStrength) {
                super.onSignalStrengthsChanged(signalStrength);
                //通过反射获取当前信号值
                try {
                    Method method = signalStrength.getClass().getMethod("getDbm");
                    int dbm = (int) method.invoke(signalStrength);
                    Log.d(TAG, "4G-dbm: " + dbm);
                    if (phoneNetListener != null) {
                        phoneNetListener.onNetDbm(dbm);
                    }
                    setNetLevel(dbm);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onSignalStrengthsChanged: 获取4G信号强度值失败");
                }
            }
        };
        //开始监听
        if (tm != null) {
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        } else {
            Log.d(TAG, "get4GNetDBM: TelephonyManager为空，获取手机状态信息失败，无法开启监听");
        }
    }

    /**
     * 1、当信号大于等于 - 85d Bm时候，信号显示满格
     * 2、当信号大于等于 - 95d Bm时候，而小于 - 85d Bm时，信号显示4格
     * 3、当信号大于等于 - 105d Bm时候，而小于 - 95d Bm时，信号显示3格，不好捕捉到。
     * 4、当信号大于等于 - 115d Bm时候，而小于 - 105d Bm时，信号显示2格，不好捕捉到。
     * 5、当信号大于等于 - 140d Bm时候，而小于 - 115d Bm时，信号显示1格，不好捕捉到。
     *
     * @param dbm
     */
    private void setNetLevel(int dbm) {
        if (phoneNetLevelListener != null) {
            if (dbm > DBM_1) {
                phoneNetLevelListener.onNetLevel(5);
            } else if (DBM_2 < dbm && dbm < DBM_1) {
                phoneNetLevelListener.onNetLevel(4);
            } else if (DBM_3 < dbm && dbm < DBM_2) {
                phoneNetLevelListener.onNetLevel(3);
            } else if (DBM_4 < dbm && dbm < DBM_3) {
                phoneNetLevelListener.onNetLevel(2);
            } else if (DBM_5 < dbm && dbm < DBM_4) {
                phoneNetLevelListener.onNetLevel(1);
            } else {
                phoneNetLevelListener.onNetLevel(0);
            }
        }
    }

    /**
     * 当前的信号强度值 dbm
     */
    interface PhoneNetListener {
        void onNetDbm(int dbm);
    }

    /**
     * 信号强度显示格子数
     * 最强信号 5 --> 0 最弱信号
     */
    interface PhoneNetLevelListener {
        void onNetLevel(int level);
    }
}

