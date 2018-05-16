package com.colorlife.orderman.util.staticContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ym on 2018/4/25.
 */

public class StatusUtil {
    public static boolean isLogin=true;
    public static Integer cookTypeId=0;

    //支付
    public static List<String> getPayWayList(){
        String way[]={"现金","商户微信","银联支付","免单","会员卡","钱包","刷卡","签单","支付宝","百度钱包"};
        List<String> payWay=new ArrayList<>();
        for (String i:way){
            payWay.add(i);
        }
        return payWay;
    }
}
