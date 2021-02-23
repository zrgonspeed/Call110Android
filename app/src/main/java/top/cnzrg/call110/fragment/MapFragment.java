package top.cnzrg.call110.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.text.DecimalFormat;

import top.cnzrg.call110.R;
import top.cnzrg.call110.constant.ConfigConstants;
import top.cnzrg.call110.util.LocationUtils;
import top.cnzrg.call110.util.LogUtils;

public class MapFragment extends Fragment {
    private final static String mName = "Map---------";

    // Fragment的布局View
    private View mView;

    // 语音报警的点击图片
    private ImageView iv_call;


    // 经度
    private TextView tv_longitude;

    // 纬度
    private TextView tv_latitude;

    // 定位方式
    private TextView tv_position;

    // 网络类型

    /**
     * 显示地理位置的文本
     */
    private TextView tv_location_1;

    /**
     * 显示地理位置描述的文本
     */
    private TextView tv_location_2;

    /**
     * 显示经纬度的刷新次数
     */
    private TextView tv_long_lati_refresh_count;


    /**
     * 上下文
     */
    private FragmentActivity mContext;

    private BaiduMap mBaiduMap;

    //--------------------------------------------------------------------------------------------------------------
    public MapFragment() {
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    /**
     * 更新UI的次数
     */
    private int mCount;
    boolean isFirstLoc = true; // 是否首次定位

    class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {

            //mapView 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(location.getDirection()).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            }

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

            // 格式化保留6位小数
            DecimalFormat formatDouble = new DecimalFormat("#.######");

            LogUtils.i("onReceive: 更新定位信息UI，次数: " + String.valueOf(++mCount));

            tv_latitude.setText(formatDouble.format(latitude));
            tv_longitude.setText(formatDouble.format(longitude));

            // 桂林市 七星区 七里店
            tv_location_1.setText(location.getCity() + location.getDistrict() + location.getStreet());

            // 在XXXXX附近
            tv_location_2.setText(location.getLocationDescribe());

            // 定位方式
            tv_position.setText(LocationUtils.getLocationType(locCode));

        }
    }

    private void initUI() {
        //获取地图控件引用
        mMapView = mView.findViewById(R.id.bmapView);
        mBaiduMap = mMapView.getMap();

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);


        this.tv_longitude = mView.findViewById(R.id.tv_longitude);
        this.tv_latitude = mView.findViewById(R.id.tv_latitude);

        this.tv_location_1 = mView.findViewById(R.id.tv_location_1);
        this.tv_location_2 = mView.findViewById(R.id.tv_location_2);

        this.tv_position = mView.findViewById(R.id.tv_position);

        this.tv_long_lati_refresh_count = mView.findViewById(R.id.tv_long_lati_refresh_count);


        // 返回箭头
        ImageView iv_arror_back = mView.findViewById(R.id.iv_arror_back);
        iv_arror_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回上一个fragment
                FragmentSetting.backPreFragment(mContext);
            }
        });
    }

    private LocationClient mLocationClient;
    private MyLocationListener mMyLocationListener;

    public void initData() {
        LogUtils.e("地图主页initData()");

        // 版本信息显示
//        this.tv_version.setText("v" + InfoUtils.getVerName(mContext));

//        startTimeThread();
//        startShowNetworkTypeThread();

//        startLocatonService();

        // 初始化百度定位
        initLocationOption();

    }

    private void initLocationOption() {
        LogUtils.e("initLocationOption------------");

        //定位初始化
        mLocationClient = new LocationClient(mContext.getApplicationContext());

        //通过LocationClientOption设置LocationClient相关参数
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);

        //可选，设置是否需要地址信息，默认不需要
        option.setIsNeedAddress(true);

        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationDescribe(true);

        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setLocationNotify(true);

        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIsNeedLocationPoiList(true);

        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.setIgnoreKillProcess(true);

        //设置locationClientOption
        mLocationClient.setLocOption(option);

        //注册LocationListener监听器
        this.mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(this.mMyLocationListener);

        //开启地图定位图层
        mLocationClient.start();

        // 设置指南针位置
        mBaiduMap.setCompassPosition(new Point(50, 180));

        // 显示我的位置
        mBaiduMap.setMyLocationEnabled(true);

        mBaiduMap.setOnMapLoadedCallback(new BaiduMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // 设置比例尺控件的位置，在 onMapLoadFinish 后生效
                mMapView.setScaleControlPosition(new Point(25, 980));

                // 设置缩放控件的位置
                mMapView.setZoomControlsPosition(new Point(20, 800));
            }
        });
    }

    /**
     * 更新UI的广播接收者
     */
    private UpdateUIBroadcastReceiver mUpdateUIBroadcastReceiver;

    /**
     * 更新UI的广播接收者
     */
    private class UpdateUIBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("onReceive: 更新定位信息UI，次数: " + String.valueOf(intent.getExtras().getInt("count")));

            tv_latitude.setText(intent.getStringExtra("latitude"));
            tv_longitude.setText(intent.getStringExtra("longitude"));

            tv_location_1.setText(intent.getStringExtra("location_1"));
            tv_location_2.setText(intent.getStringExtra("location_2"));

            tv_position.setText(intent.getStringExtra("position"));
//            tv_long_lati_refresh_count.setText(intent.getExtras().getInt("count") + "");
        }

    }

    /**
     * 开启更新UI的广播
     */
    private void startUpdateUIBroadcastReceiver() {
        IntentFilter filter = new IntentFilter();
        // action过滤
        filter.addAction(ConfigConstants.ACTION_UPDATE_UI);

        mUpdateUIBroadcastReceiver = new UpdateUIBroadcastReceiver();
        // 动态注册广播
        mContext.registerReceiver(mUpdateUIBroadcastReceiver, filter);

        LogUtils.i("注册更新UI的广播----------------------开启广播接收者");
    }

    /**
     * 反注册广播
     */
    public void unRegisterUIBroadcastReceiver() {

        // 注销广播
        if (mUpdateUIBroadcastReceiver != null) {
            mContext.unregisterReceiver(mUpdateUIBroadcastReceiver);

            LogUtils.i("注销更新UI的广播------------------销毁广播接收者");
        }
    }

    /**
     * 开启显示当前时间的线程，格式 HH:mm:ss
     * 1秒刷新一次
     */
/*    private void startTimeThread() {
        // 时间显示线程，1秒更新
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!mStopTimeThread) {
                        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                        String str = sdf.format(new Date());

//                        LogUtils.w("run: 当前线程名称" + Thread.currentThread().getName());

                        Message msg = Message.obtain();
                        msg.obj = str;
                        msg.what = 1;
//                        mHandler.sendMessage(msg);

                        Thread.sleep(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }*/



    private MapView mMapView = null;

    /**
     * onAttach()在fragment与Activity关联之后调调查用。
     * 需要注意的是，初始化fragment参数可以从getArguments()获得，但是，当Fragment附加到Activity之后，就无法再调用setArguments()。
     * 所以除了在最开始时，其它时间都无法向初始化参数添加内容。
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        LogUtils.i("onAttach: " + mName);
    }

    /**
     * 这个只是用来创建Fragment的。此时的Activity还没有创建完成。
     * 因为我们的Fragment也是Activity创建的一部分。所以如果你想在这里使用Activity中的一些资源，将会获取不到
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.i("onCreate " + mName);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LogUtils.i("onCreateView " + mName);
        this.mView = inflater.inflate(R.layout.fragment_map, container, false);

        initUI();
        return this.mView;
    }

    /**
     * 在Activity的OnCreate()结束后，会调用此方法。所以到这里的时候，Activity已经创建完成！
     * 在这个函数中才可以使用Activity的所有资源。
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.i("onActivityCreated " + mName);

        mContext = getActivity();
        initData();
    }

    /**
     * 当到OnStart()时，Fragment对用户就是可见的了。但用户还未开始与Fragment交互。
     * 在生命周期中也可以看到Fragment的OnStart()过程与Activity的OnStart()过程是绑定的。
     * 意义即是一样的。以前你写在Activity的OnStart()中来处理的代码，用Fragment来实现时，依然可以放在OnStart()中来处理。
     */
    @Override
    public void onStart() {
        super.onStart();
        LogUtils.i("onStart " + mName);
    }

    /**
     * 当这个fragment对用户可见并且正在运行时调用。这是Fragment与用户交互之前的最后一个回调。
     * 从生命周期对比中，可以看到，Fragment的OnResume与Activity的OnResume是相互绑定的，意义是一样的。
     * 它依赖于包含它的activity的Activity.onResume。当OnResume()结束后，就可以正式与用户交互了。
     */
    @Override
    public void onResume() {
        super.onResume();
        LogUtils.i("onResume " + mName);

        mMapView.onResume();

        startUpdateUIBroadcastReceiver();
    }

    // -------------------------------------

    /**
     * 此回调与Activity的OnPause()相绑定，与Activity的OnPause()意义一样。
     */
    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i("onPause " + mName);

        mMapView.onPause();
        unRegisterUIBroadcastReceiver();
    }

    /**
     * 这个回调与Activity的OnStop()相绑定，意义一样。
     * 已停止的Fragment可以直接返回到OnStart()回调，然后调用OnResume()。
     */
    @Override
    public void onStop() {
        super.onStop();
        LogUtils.i("onStop " + mName);
    }

    /**
     * 如果Fragment即将被结束或保存，那么撤销方向上的下一个回调将是onDestoryView()。
     * 会将在onCreateView创建的视图与这个fragment分离。下次这个fragment若要显示，那么将会创建新视图。
     * 这会在onStop之后和onDestroy之前调用。这个方法的调用同onCreateView是否返回非null视图无关。
     * 它会潜在的在这个视图状态被保存之后以及它被它的父视图回收之前调用。
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.i("onDestroyView " + mName);
    }

    /**
     * 当这个fragment不再使用时调用。需要注意的是，它即使经过了onDestroy()阶段，但仍然能从Activity中找到，因为它还没有Detach。
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.i("onDestroy " + mName);

//        mContext.stopService(new Intent(mContext, UnlessLocationService.class));

        // 消息机制移除
//        mHandler.removeCallbacksAndMessages(null);

        // 百度地图相关销毁
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;

        mLocationClient.unRegisterLocationListener(mMyLocationListener);
        mMyLocationListener = null;
        mLocationClient = null;
    }

    /**
     * Fragment生命周期中最后一个回调是onDetach()。
     * 调用它以后，Fragment就不再与Activity相绑定，它也不再拥有视图层次结构，它的所有资源都将被释放。
     */
    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.i("onDetach " + mName);
    }
}
