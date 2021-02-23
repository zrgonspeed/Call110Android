package top.cnzrg.call110.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.text.DecimalFormat;

import top.cnzrg.call110.constant.ConfigConstants;
import top.cnzrg.call110.util.LocationUtils;
import top.cnzrg.call110.util.LogUtils;

/**
 * 位置定位服务类 使用百度定位
 * FileName: UnlessLocationService
 * Author: ZRG
 * Date: 2019/8/3 16:46
 */
public class UnlessLocationService extends Service {
    private static final String TAG = "UnlessLocationService";

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    /**
     * 更新UI的次数
     */
    private int mCount;

    /**
     * 初始化定位参数配置
     */

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
        locationOption.setOpenAutoNotifyMode(3000, 1, LocationClientOption.LOC_SENSITIVITY_HIGHT);

        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);

        //开始定位
        mLocationClient.start();
    }

    class MyLocationListener extends BDAbstractLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {

            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();

            //获取经度信息
            double longitude = location.getLongitude();

            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();

            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int locCode = location.getLocType();

            // ------------------------------------------------------------
            // 更新Activity的UI
            final Intent intent = new Intent();
            intent.setAction(ConfigConstants.ACTION_UPDATE_UI);

            // 格式化保留6位小数
            DecimalFormat formatDouble = new DecimalFormat("#.######");

            intent.putExtra("count", ++mCount);
            intent.putExtra("longitude", formatDouble.format(longitude));
            intent.putExtra("latitude", formatDouble.format(latitude));

            // 桂林市 七星区 七里店
            intent.putExtra("location_1", location.getCity() + location.getDistrict() + location.getStreet());

            // 在XXXXX附件
            intent.putExtra("location_2", location.getLocationDescribe());

            // 定位方式
            intent.putExtra("position", LocationUtils.getLocationType(locCode));

            /*LogUtils.e("百度定位: latitude: " + latitude + "   longitude: " + longitude);
            LogUtils.e("坐标类型: " + coorType);

            // 中国广西壮族自治区桂林市七星区七里店路
            LogUtils.e("getAddrStr: " + location.getAddrStr());

            // 在俊丰汽车修理厂附近
            LogUtils.e("getLocationDescribe: " + location.getLocationDescribe());

            // 桂林市
            LogUtils.e("getCity: " + location.getCity());

            // 七星区
            LogUtils.e("getDistrict: " + location.getDistrict());

            // 七里店路
            LogUtils.e("getDistrict: " + location.getStreet());

            LogUtils.e("定位结果描述 " + location.getLocTypeDescription());

            // 在网络定位的情况下
            // "wf"： wifi定位结果   "cl": cell定位结果  "ll"：GPS定位结果    null 没有获取到定位结果采用的类型
            LogUtils.e("网络定位的情况下的定位方式: " + LocationUtils.getNetworkLocationType(location.getNetworkLocationType()));

            LogUtils.e("百度API 运营商: " + LocationUtils.getOperatorsName(location.getOperators()));

            LogUtils.e("室内外状态: " + LocationUtils.getUserIndoorState(location.getUserIndoorState()));*/

            // 通过广播发送消息更新UI
            sendBroadcast(intent);

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: 定位服务创建");

        // 初始化百度定位
        initLocationOption();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocationClient.stop();

        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        mMyLocationListener = null;
        mLocationClient = null;
        Log.i(TAG, "onDestroy: 定位服务销毁");

    }

    /**
     * // 保留小数后6位
     *             DecimalFormat formatDouble = new DecimalFormat("#.######");
     */

}
