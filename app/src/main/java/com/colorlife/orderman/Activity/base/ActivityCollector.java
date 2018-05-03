package com.colorlife.orderman.Activity.base;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ym on 2018/4/18.
 */

public class ActivityCollector {

    public static List<Activity> activities=new ArrayList<>();

    //添加页面
    public static void addActivity(Activity activity){
        activities.add(activity);
    }

    //移除
    public static void removeActivity(Activity activity){
        activities.remove(activity);
    }

    //结束所有活动
    public static void finishAll(){
        for (Activity a:activities){
            if (!a.isFinishing()){
                a.finish();
            }
        }
    }

    public static Activity getActivity(Activity activity){
        if (activities.size()>0) {
            for (Activity a : activities) {
                if (a.toString().equals(activity.toString())) {
                    return a;
                }
            }
        }
        return activity;
    }
}
