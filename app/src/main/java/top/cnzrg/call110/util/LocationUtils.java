package top.cnzrg.call110.util;

import com.baidu.location.BDLocation;

/**
 * FileName: LocationUtils
 * Author: ZRG
 * Date: 2019/8/10 10:08
 */
public class LocationUtils {

    /**
     * 获取运营商名称,不好用，有时会返回未知运营商
     *
     * @param operators
     * @return
     */
    public static String getOperatorsName(int operators) {
        switch (operators) {
            case BDLocation.OPERATORS_TYPE_MOBILE:
                return "中国移动";

            case BDLocation.OPERATORS_TYPE_TELECOMU:
                return "中国电信";

            case BDLocation.OPERATORS_TYPE_UNICOM:
                return "中国联通";

            default:
                LogUtils.e("operators: " + operators);
                return "未知运营商";
        }
    }

    /**
     * 获取网络定位下的定位方式
     *
     * @param networkLocationType
     * @return
     */
    public static String getNetworkLocationType(String networkLocationType) {

        if ("wf".equals(networkLocationType)) {
            return "wifi定位";
        }
        if ("cl".equals(networkLocationType)) {
            return "基站定位";
        }
        if ("ll".equals(networkLocationType)) {
            return "GPS定位";
        }

        return "没有获取到定位结果";
    }

    /**
     * 获取定位类型
     *
     * @param locType
     * @return GPS定位  网络定位  离线定位  缓存定位 无法定位 网络连接失败..
     */
    public static String getLocationType(int locType) {
        switch (locType) {
            case BDLocation.TypeGpsLocation:
                return "GPS定位";

            case BDLocation.TypeNetWorkLocation:
                return "网络定位";

            case BDLocation.TypeOffLineLocation:
                return "离线定位";

            case BDLocation.TypeCacheLocation:
                return "缓存定位";

            case BDLocation.TypeCriteriaException:
                return "无法定位";

            case BDLocation.TypeNetWorkException:
                return "网络连接失败";

            case BDLocation.TypeServerError:
                return "server定位失败";

            default:
                return locType + "";
        }

    }

    /**
     * 返回室内外状态
     *
     * @param state
     * @return
     */
    public static String getUserIndoorState(int state) {
        switch (state) {
            case BDLocation.USER_INDDOR_TRUE:
                return "室内";

            case BDLocation.USER_INDOOR_FALSE:
                return "室外";

            default:
                return "未知";
        }
    }
}
