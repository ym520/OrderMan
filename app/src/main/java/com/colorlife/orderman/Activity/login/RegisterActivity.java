package com.colorlife.orderman.Activity.login;

import android.app.Dialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.R;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.dou361.dialogui.DialogUIUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by ym on 2018/4/19.
 */

@ContentView(R.layout.register)
public class RegisterActivity extends BaseActivity {
    String TAG=this.toString();
    //顶部界面
    @ViewInject(R.id.imageButton_back)
    private ImageButton imageButton_back;
    @ViewInject(R.id.textView_back)
    private TextView textView_back;
    @ViewInject(R.id.textView_register)
    private TextView textView_register;

    //注册输入框
    @ViewInject(R.id.editText_register_userName)
    private EditText userName;
    @ViewInject(R.id.editText_register_password)
    private EditText password;
    @ViewInject(R.id.editText_register_confirmPassword)
    private EditText confirmPassword;
    @ViewInject(R.id.editText_register_phone)
    private EditText phone;
    @ViewInject(R.id.editText_register_shopName)
    private EditText shopName;

    //执行注册
    @Event(R.id.textView_register)
    private void doRegister(View view) {
        if ("".equals(userName.getText().toString()) ||
                "".equals(password.getText().toString()) ||
                "".equals(confirmPassword.getText().toString()) ||
                "".equals(phone.getText().toString()) ||
                "".equals(shopName.getText().toString())
                ) {
            ViewUtil.showToast(RegisterActivity.this, "您没有填写完数据，请填写完整数据！");
        } else {
            if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
                ViewUtil.showToast(RegisterActivity.this,"两次输入的密码不一致！请重新输入！");
            }else {
                RequestParams params=new RequestParams(HttpUrl.registerUrl);
                params.addParameter("id",0);
                params.addParameter("name",userName.getText().toString());
                params.addParameter("password",password.getText().toString());
                params.addParameter("phone",phone.getText().toString());
                params.addParameter("storeName",shopName.getText().toString());
                params.addParameter("province",null);
                params.addParameter("city",null);
                params.addParameter("status",1);
                params.addParameter("createTime","");
                params.addParameter("modifyTime","");

                final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
                x.http().post(params, new Callback.CommonCallback<String>() {
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
                    public void onSuccess(String result) {
                        Integer code= JSON.parseObject(result).getInteger("code");
                        String msg=JSON.parseObject(result).getString("msg");
                        if (code==20000){
                            ViewUtil.showToast(RegisterActivity.this,"注册成功！");
                            Intent  intent=new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            ViewUtil.showToast(RegisterActivity.this,msg);
                        }
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d(TAG, "onCancelled: "+cex.getMessage().toString());
                    }
                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                        Log.d(TAG, "onFinished: 请求完成！");
                    }
                });
            }
        }
    }

    //返回登录界面
    @Event(value = {R.id.textView_back, R.id.imageButton_back})
    private void intoLogin(View view) {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
