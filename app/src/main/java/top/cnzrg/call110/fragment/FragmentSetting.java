package top.cnzrg.call110.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import top.cnzrg.call110.R;
import top.cnzrg.call110.util.LogUtils;

/**
 * FileName: FragmentSetting
 * Author: ZRG
 * Date: 2019/8/26 15:00
 */
public class FragmentSetting {
    // 定位当前选中的Fragment
    public static Fragment currentFragment;

    // 紧急联系人的Fragment
    public static EmergencyContactFragment emergencyContactFragment;

    /**
     * hide和show切换Fragment
     *
     * @param fragment
     */
    public static void switchFragment(Fragment fragment, FragmentActivity mContext) {
        //1.得到FragmentManger
        FragmentManager manager = mContext.getSupportFragmentManager();

        //2.开启事务
        FragmentTransaction ft = manager.beginTransaction();

        if (fragment != FragmentSetting.currentFragment) {
            if (!fragment.isAdded()) {
                if (FragmentSetting.currentFragment == null) {
                    // 创建新的Fragment
                    LogUtils.w("ft.add----------------------");

                    ft.add(R.id.fl_main_content, fragment).addToBackStack(null);
                } else {
                    LogUtils.w("ft.hide currentFragment, add ----------------------");

                    ft.hide(FragmentSetting.currentFragment).add(R.id.fl_main_content, fragment).addToBackStack(null);
                }

            } else {
                LogUtils.w("ft.hide currentFragment, show ----------------------");

                ft.hide(FragmentSetting.currentFragment).show(fragment).addToBackStack(null);
            }
            FragmentSetting.currentFragment = fragment;
        }

        //4.提交事务
        ft.commit();

    }

    /**
     * // 返回上一个fragment
     *
     * @param mContext
     */
    public static void backPreFragment(FragmentActivity mContext) {

        mContext.getSupportFragmentManager().popBackStack();
        FragmentSetting.currentFragment = null;
    }
}
