package com.colorlife.orderman.Activity.base;

import android.Manifest;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import org.xutils.x;
import rx.Observer;


/**
 * Created by ym on 2018/4/18.
 */

public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        x.view().inject(this);
        Log.d("BaseActivity",getClass().getSimpleName());
        requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    //请求权限
    public void requestPermissions() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onNext(Boolean value) {
                        if (value) {
                            Log.d("BaseActivity", "已经获取网络、存储权限");
                        } else {
                            Toast.makeText(BaseActivity.this, "请授予相应权限", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });

    }


    //获取cookie
    public String getCookies(){
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        return cookie;
    }

}
