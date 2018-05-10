package com.colorlife.orderman.Application;

import android.app.Application;

import com.dou361.dialogui.DialogUIUtils;

import org.xutils.x;

/**
 * Created by ym on 2018/4/19.
 */

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        x.Ext.setDebug(false); // 是否输出debug日志
    }
}
