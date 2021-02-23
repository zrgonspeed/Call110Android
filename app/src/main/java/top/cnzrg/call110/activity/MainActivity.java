package top.cnzrg.call110.activity;

import top.cnzrg.call110.R;
import top.cnzrg.call110.fragment.FragmentSetting;
import top.cnzrg.call110.fragment.MainFragment;
import top.cnzrg.call110.fragment.MapFragment;
import top.cnzrg.call110.util.LogUtils;

import android.Manifest;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

public class MainActivity extends BasePermissionActivity {

    // 权限请求码
    private static final int REQUEST_CODE = 1001;

    // 要申请的权限
    private static String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE,

            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.MODIFY_PHONE_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,

            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.e("onCreate-----------------------------------------MainActivity");

        // 6.0以上需要动态申请权限
        if (Build.VERSION.SDK_INT >= 23) {
            initPermission(permissions, REQUEST_CODE);
        } else {
            init();
        }
    }

    /**
     * 实现BasePermissionActivity的抽象方法
     */
    protected void init() {
        initUI();
        initData();
    }

    private void initUI() {
        View view = View.inflate(this, R.layout.activity_main, null);

        setContentView(view);

        // 默认进入首页Fragment
        FragmentSetting.switchFragment(MainFragment.newInstance(), this);
    }

    private void initData() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            LogUtils.e("返回键");

            // 返回上一个fragment
            getSupportFragmentManager().popBackStack();
            FragmentSetting.currentFragment = null;

            if (getSupportFragmentManager().getBackStackEntryCount() <= 1)//这里是取出我们返回栈存在Fragment的个数
                finish();

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.e("onStart");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        LogUtils.e("onSaveInstanceState");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.e("onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.e("onPause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.e("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.e("onRestart");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        LogUtils.e("onRestoreInstanceState");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        LogUtils.e("onDestroy");
    }

}
