package com.colorlife.orderman.Activity.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.UserRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by ym on 2018/5/3.
 */

@ContentView(R.layout.user_setting)
public class UserSetting extends AppCompatActivity {
    private String TAG=this.toString();

    @ViewInject(R.id.userSetting_editText_city)
    private EditText city;
    @ViewInject(R.id.userSetting_editText_phone)
    private EditText phone;
    @ViewInject(R.id.userSetting_editText_province)
    private EditText province;
    @ViewInject(R.id.userSetting_editText_storeName)
    private EditText storeName;
    @ViewInject(R.id.userSetting_textView_back)
    private TextView textBack;
    @ViewInject(R.id.userSetting_TextView_name)
    private TextView name;
    @ViewInject(R.id.userSetting_textView_finish)
    private TextView finish;

    private UserRequest userRequest=new UserRequest();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        initDate();
    }

    //初始化数据
    private void initDate() {
        RequestParams params=new RequestParams(HttpUrl.findUserInfoUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    userRequest=JSON.parseObject(JSON.parseObject(result).getString("data"),UserRequest.class);
                    if (userRequest!=null){
                        name.setText(userRequest.getName());
                        phone.setText(userRequest.getPhone());
                        city.setText(userRequest.getCity());
                        storeName.setText(userRequest.getStoreName());
                        province.setText(userRequest.getProvince());
                    }

                }else {
                    ViewUtil.showToast(UserSetting.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                    }
                }
            }
            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: "+ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    Log.d(TAG, "onError: responseCode:"+responseCode);
                    Log.d(TAG, "onError: responseMsg:"+responseMsg);
                    Log.d(TAG, "onError: errorResult:"+errorResult);
                } else { // 其他错误
                    // ...
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled: "+cex.getMessage().toString());
            }
        });

    }

    @Event(value = {R.id.userSetting_textView_back,R.id.userSetting_imageButton_back})
    private void doBack(View view){
        Intent intent=new Intent(UserSetting.this,settingIndex.class);
        startActivity(intent);
        finish();
    }

    @Event(R.id.userSetting_textView_finish)
    private void doUpdate(View view){
        Log.d(TAG, "doUpdate: 提交点击事件");
        userRequest.setCity(city.getText().toString());
        userRequest.setProvince(province.getText().toString());
        userRequest.setStoreName(storeName.getText().toString());
        userRequest.setPhone(phone.getText().toString());

        RequestParams params=new RequestParams(HttpUrl.userUpdateUrl);
        params.setAsJsonContent(true);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setBodyContent(JSON.toJSONString(userRequest));
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    ViewUtil.showToast(UserSetting.this,msg);
                }else {
                    ViewUtil.showToast(UserSetting.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                    }
                }
            }
            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: "+ex.getMessage());
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    Log.d(TAG, "onError: responseCode:"+responseCode);
                    Log.d(TAG, "onError: responseMsg:"+responseMsg);
                    Log.d(TAG, "onError: errorResult:"+errorResult);
                } else { // 其他错误
                    // ...
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled: "+cex.getMessage().toString());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
