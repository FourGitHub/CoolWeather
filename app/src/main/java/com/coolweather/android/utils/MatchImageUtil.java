package com.coolweather.android.utils;

import com.coolweather.android.R;

/**
 * Created by Four on 2018/3/9 0009.
 */

public class MatchImageUtil {
    public static int matchImage(String type) {
        switch (type) {
            case "comf":
                return R.drawable.image_comfort;
            case "drsg":
                return R.drawable.image_clothing;
            case "flu":
                return R.drawable.image_drag;
            case "sport":
                return R.drawable.image_sport;
            case "trav":
                return R.drawable.image_traval;
            case "uv":
                return R.drawable.image_uv;
            case "cw":
                return R.drawable.image_car;
            case "air":
                return R.drawable.image_air;
            default:
                return -1;

        }
    }

    /**
     * @param weatherIocnCode 服务器返回JSON数据的天气状况图标代号
     * @return 匹配该代号对应的天气状况图标
     */
    public static int matchWeatherIocnCode(String weatherIocnCode) {
        switch (weatherIocnCode) {
            case "100":
                return R.drawable.icon_100;
            case "100n":
                return R.drawable.icon_100n;
            case "101":
                return R.drawable.icon_101;
            case "102":
                return R.drawable.icon_102;
            case "103":
                return R.drawable.icon_103;
            case "103n":
                return R.drawable.icon_103n;
            case "104":
                return R.drawable.icon_104;
            case "104n":
                return R.drawable.icon_104n;
            case "200":
                return R.drawable.icon_200;
            case "201":
                return R.drawable.icon_201;
            case "202":
                return R.drawable.icon_202;
            case "203":
                return R.drawable.icon_203;
            case "204":
                return R.drawable.icon_204;
            case "205":
                return R.drawable.icon_205;
            case "206":
                return R.drawable.icon_206;
            case "207":
                return R.drawable.icon_207;
            case "208":
                return R.drawable.icon_208;
            case "209":
                return R.drawable.icon_209;
            case "210":
                return R.drawable.icon_210;
            case "211":
                return R.drawable.icon_211;
            case "212":
                return R.drawable.icon_212;
            case "213":
                return R.drawable.icon_213;
            case "300":
                return R.drawable.icon_300;
            case "300n":
                return R.drawable.icon_300n;
            case "301":
                return R.drawable.icon_301;
            case "301n":
                return R.drawable.icon_301n;
            case "302":
                return R.drawable.icon_302;
            case "303":
                return R.drawable.icon_303;
            case "304":
                return R.drawable.icon_304;
            case "305":
                return R.drawable.icon_305;
            case "306":
                return R.drawable.icon_306;
            case "307":
                return R.drawable.icon_307;
            case "309":
                return R.drawable.icon_309;
            case "310":
                return R.drawable.icon_310;
            case "311":
                return R.drawable.icon_311;
            case "312":
                return R.drawable.icon_312;
            case "313":
                return R.drawable.icon_313;
            case "400":
                return R.drawable.icon_400;
            case "401":
                return R.drawable.icon_401;
            case "402":
                return  R.drawable.icon_402;
            case "403":
                return  R.drawable.icon_403;
            case "404":
                return  R.drawable.icon_404;
            case "405":
                return  R.drawable.icon_405;
            case "406":
                return  R.drawable.icon_406;
            case "406n":
                return  R.drawable.icon_406n;
            case "407":
                return  R.drawable.icon_407;
            case "407n":
                return  R.drawable.icon_407n;
            case "500":
                return  R.drawable.icon_500;
            case "501":
                return  R.drawable.icon_501;
            case "502":
                return  R.drawable.icon_502;
            case "503":
                return  R.drawable.icon_503;
            case "504":
                return  R.drawable.icon_504;
            case "507":
                return  R.drawable.icon_507;
            case "508":
                return  R.drawable.icon_508;
            case "900":
                return  R.drawable.icon_900;
            case "901":
                return  R.drawable.icon_901;
            default:
                return R.drawable.icon_999;
        }
    }
}
