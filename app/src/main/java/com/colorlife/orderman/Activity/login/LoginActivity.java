package com.colorlife.orderman.Activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.main.IndexActivity;
import com.colorlife.orderman.R;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.http.cookie.DbCookieStore;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.net.HttpCookie;
import java.util.List;


/**
 * Created by ym on 2018/4/18.
 */
@ContentView(R.layout.login)
public class LoginActivity extends BaseActivity {
    private String TAG=this.toString();


    @ViewInject(R.id.edit_UserName)
    private EditText edit_UserName;
    @ViewInject(R.id.edit_Password)
    private EditText edit_Password;
    @ViewInject(R.id.button_login)
    private Button button_login;
    @ViewInject(R.id.button_register)
    private Button button_register;
    @ViewInject(R.id.button_forgetPassword)
    private Button button_forgetPassword;

    //登录
    @Event(R.id.button_login)
    private void doLogin(View view){
        if(!HttpUrl.isNet(LoginActivity.this)){
            ViewUtil.showToast(this,"网路异常，请检查你的网路！");
        }

        final String password=edit_Password.getText().toString();
        final String username=edit_UserName.getText().toString();

        if ("".equals(password)||"".equals(username)){
            ViewUtil.showToast(LoginActivity.this,"你没有填写完信息，请输入完整信息！");
        }else {
            RequestParams params=new RequestParams(HttpUrl.loginUrl);
            params.addParameter("loginName",username);
            params.addParameter("password",password);
            Log.d("login","进入发送post请求");
            //发送登录请求
            DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    Log.d(TAG, "onSuccess: "+result);
                    String code= JSON.parseObject(result).getString("code");
                    String msg= JSON.parseObject(result).getString("msg");
                    if (code.equals("10000")){
                        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("loginName",username);
                        editor.putString("password",password);
                        editor.commit();

                        DbCookieStore instance = DbCookieStore.INSTANCE;
                        List<HttpCookie> cookies = instance.getCookies();
                        for (HttpCookie cookie : cookies) {
                            String name = cookie.getName();
                            String value = cookie.getValue();
                            if ("JSESSIONID".equals(name)) {
                                SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
                                SharedPreferences.Editor editor2 = sp2.edit();
                                editor2.putString(name,value);
                                editor2.commit();
                                break;
                            }
                        }
                        StatusUtil.isLogin=true;
                        Intent intent=new Intent(LoginActivity.this, IndexActivity.class);
                        startActivity(intent);
                        finish();
                    }else {
                        //提示登录信息
                        ViewUtil.showToast(LoginActivity.this,msg);
                    }
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d(TAG, "onError: "+ex.getMessage().toString());
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
                @Override
                public void onFinished() {
                    DialogUIUtils.dismiss();
                    Log.d(TAG, "onFinished: 请求完成！");
                }
            });
        }
    }

    //跳转到登录页
    @Event(R.id.button_register)
    private void intoRegister(View view){
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
    }

    //跳转到忘记密码页面
    @Event(R.id.button_forgetPassword)
    private void intoForgetPassword(View view){
        Intent intent=new Intent(LoginActivity.this,ForgetPassword.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
