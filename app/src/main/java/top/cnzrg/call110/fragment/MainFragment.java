package top.cnzrg.call110.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Date;

import top.cnzrg.call110.R;
import top.cnzrg.call110.constant.AppConstants;
import top.cnzrg.call110.constant.ConfigConstants;
import top.cnzrg.call110.pojo.CallInfo;
import top.cnzrg.call110.service.UploadService;
import top.cnzrg.call110.util.CallUtils;
import top.cnzrg.call110.util.InfoUtils;
import top.cnzrg.call110.util.LogUtils;

public class MainFragment extends Fragment {
    private final static String mName = "Main---------";
    /**
     * 上下文
     */
    private FragmentActivity mContext;

    // Fragment的布局View
    private View mView;

    // 版本信息
    private TextView tv_version;

    //--------------------------------------------------------------------------------------------------------------
    public MainFragment() {
    }

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    private void initUI() {
        tv_version = mView.findViewById(R.id.tv_version);

        // 地图定位 的图片
        ImageView iv_map = mView.findViewById(R.id.iv_map);
        iv_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到地图定位的Fragment
                FragmentSetting.switchFragment(MapFragment.newInstance(), mContext);
            }
        });

        // 语音报警 的图片
        ImageView iv_audio_call = mView.findViewById(R.id.iv_audio_call);
        iv_audio_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 语音通话
                CallUtils.callPhone(AppConstants.CAll_POLICE_PHONE_NUMBER, mContext);

                // 上传报警信息
                startUploadService();

            }
        });

        // 紧急联系人 的图片
        ImageView iv_contact = mView.findViewById(R.id.iv_contact);
        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 跳转到 紧急联系人的Fragment
                FragmentSetting.switchFragment(EmergencyContactFragment.newInstance(), mContext);
                FragmentSetting.emergencyContactFragment = (EmergencyContactFragment) FragmentSetting.currentFragment;

            }
        });

        // 说明 的图片
        ImageView iv_desc = mView.findViewById(R.id.iv_desc);
        iv_desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("点击了说明按钮");

                // 跳转到 说明的Fragment
                FragmentSetting.switchFragment(HelpFragment.newInstance(), mContext);
            }
        });
    }

    /**
     * 开启上传报警信息的线程
     */
    private void startUploadService() {
        // 设置上传信息
        CallInfo callInfo = new CallInfo(
                InfoUtils.getPhoneNumber((TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE)),   // 手机号
                null,   // 经度
                null,   // 纬度
                DateFormat.format("yyyy/MM/dd HH:mm:ss", new Date()).toString()
        );

        // 向servie传CallInfo对象
        Intent intent = new Intent(mContext.getApplicationContext(), UploadService.class);
        intent.putExtra(ConfigConstants.Key_CALL_INFO, callInfo);

        //-------------------------------------------
        mContext.startService(intent);
    }

    public void initData() {
        LogUtils.e("initData()");

        // 版本信息显示
        tv_version.setText("v" + InfoUtils.getVerName(mContext));

    }

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
        this.mView = inflater.inflate(R.layout.fragment_main, container, false);

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

    }

    // -------------------------------------

    /**
     * 此回调与Activity的OnPause()相绑定，与Activity的OnPause()意义一样。
     */
    @Override
    public void onPause() {
        super.onPause();
        LogUtils.i("onPause " + mName);

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
