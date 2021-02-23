package top.cnzrg.call110.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * FileName: CallUtils
 * Author: ZRG
 * Date: 2019/7/30 17:33
 */
public class CallUtils {
    /**
     * 拨打电话（直接拨打电话）
     *
     * @param phoneNum 目标电话号码
     */
    public static void callPhone(String phoneNum, Context context) {
        Intent intent = new Intent(Intent.ACTION_CALL);
        Uri data = Uri.parse("tel:" + phoneNum);
        intent.setData(data);
        context.startActivity(intent);
    }
}
