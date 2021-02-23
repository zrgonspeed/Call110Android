package top.cnzrg.call110.service;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import top.cnzrg.call110.constant.ConfigConstants;
import top.cnzrg.call110.pojo.CallInfo;
import top.cnzrg.call110.util.InfoUtils;
import top.cnzrg.call110.util.LogUtils;
import top.cnzrg.call110.util.ToastUtils;
import top.cnzrg.call110.util.UrlUtils;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class UploadService extends Service {
    private static final String TAG = "UploadService";

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;
    private BDLocation mLocation;
    /**
     * 更新UI的次数
     */
    private int mCount;

    private Handler mHandler;

    /**
     * 未上传的CALL集合
     */
    public static final List<CallInfo> CALL_INFOS = new ArrayList<>();

    private void initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        mLocationClient = new LocationClient(getApplicationContext());

        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();

        mMyLocationListener = new MyLocationListener();
        //注册监听函数
        mLocationClient.registerLocationListener(mMyLocationListener);

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");

        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);

        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);

        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);

        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);

        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);

        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);

        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);

        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);

        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();

        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(1000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);

        //开始定位
        mLocationClient.start();
    }

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            mLocation = location;

            LogUtils.e("刷新位置次数: " + mCount++);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtils.i("--------->onCreate: ");
        mHandler = new Handler(Looper.getMainLooper());

        // 初始化位置服务
        initLocationOption();

        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // 上传信息
                uploadInfo();
            }
            //  delay为long,period为long：从现在起过delay毫秒以后，每隔period 毫秒执行一次。
        }, 0, 5_000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("--------->onStartCommand: ");

        if (intent == null) {
            LogUtils.i("onStartCommand: intent为null");
            return START_REDELIVER_INTENT;
        }

        // 获取传过来的CallInfo
        CallInfo callInfo = (CallInfo) intent.getSerializableExtra(ConfigConstants.Key_CALL_INFO);
        CALL_INFOS.add(callInfo);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        LogUtils.i("--------->onDestroy: ");

        mLocationClient.stop();
        mLocationClient.unRegisterLocationListener(mMyLocationListener);

        mMyLocationListener = null;
        mLocationClient = null;
        Log.i(TAG, "onDestroy: 上传服务销毁");

        super.onDestroy();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 上传信息
     */
    private void uploadInfo() {
        if (mLocation == null) {
            LogUtils.i("----------------uploadInfo: mLocation为null, 不上传信息");
            return;
        }

        if (CALL_INFOS.size() == 0) {
            LogUtils.i("----------------uploadInfo: CALL_INFOS.size() 为 0, 不上传信息");
            return;
        }

        LogUtils.i("----------------uploadInfo: 上传信息");
        LogUtils.i("CALL_INFOS.size(): " + CALL_INFOS.size());

        for (int i = 0; i < CALL_INFOS.size(); i++) {
            CallInfo callInfo = CALL_INFOS.get(i);

            if (callInfo.getLongitude() == null) {
                // 此处给callInfo封装经纬度
                double latitude = mLocation.getLatitude();
                double longitude = mLocation.getLongitude();

                // 格式化保留6位小数
                DecimalFormat formatDouble = new DecimalFormat("#.######");
                callInfo.setLatitude(formatDouble.format(latitude));
                callInfo.setLongitude(formatDouble.format(longitude));
            }

            // 拼接参数
            String params = "call_time=" + UrlUtils.encode(callInfo.getTime())
                    + "&longitude=" + UrlUtils.encode(callInfo.getLongitude())
                    + "&latitude=" + UrlUtils.encode(callInfo.getLatitude())
                    + "&call_phone=" + UrlUtils.encode(callInfo.getPhone());

            LogUtils.i("上传的参数 ->  ");
            LogUtils.i("手机号: " + callInfo.getPhone());
            LogUtils.i("经度: " + callInfo.getLongitude());
            LogUtils.i("纬度: " + callInfo.getLatitude());
            LogUtils.i("上传的时间: " + callInfo.getTime());

            // 这里发送get请求的时候等待时间太长
            String response = UrlUtils.sendGet(InfoUtils.getUploadURL(getApplicationContext()), params);
            LogUtils.i("--------->response: " + response);

            if (response != null) {
                // 上传信息成功弹出吐司提示
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.toastLong(getApplicationContext(), "上传信息成功");
                    }
                });
                CALL_INFOS.remove(callInfo);
                i--;
            }

        }

    }


}
