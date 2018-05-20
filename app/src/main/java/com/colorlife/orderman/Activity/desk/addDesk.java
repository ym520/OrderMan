package com.colorlife.orderman.Activity.desk;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskRequest;
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
 * Created by ym on 2018/5/4.
 */
@ContentView(R.layout.add_desk)
public class AddDesk extends BaseActivity {
    String TAG=this.toString();
    //可住人数
    @ViewInject(R.id.addDesk_editText_volume)
    private EditText volume;
    @ViewInject(R.id.addDesk_editText_deskName)
    private EditText deskName;
    @ViewInject(R.id.addDesk_editText_roomName)
    private EditText roomName;

    @Event(R.id.addDesk_textView_back)
    private void doBack(View view){
        Intent intent=new Intent(AddDesk.this, DeskManager.class);
        startActivity(intent);
        finish();
    }

    @Event(R.id.addDesk_button_add)
    private void doAddDesk(View view){
        if (deskName.getText().toString()==null || "".equals(deskName.getText().toString())){
            ViewUtil.showToast(AddDesk.this,"请填写内容");
        }else {
            DeskRequest request=new DeskRequest();
            request.setCode(deskName.getText().toString());
            request.setStatus(1);

            RequestParams requestParams=new RequestParams(HttpUrl.addDeskUrl);
            requestParams.setAsJsonContent(true);
            //获取cookie
            SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
            String cookie=sp2.getString("JSESSIONID","");
            requestParams.addHeader("Cookie","JSESSIONID="+cookie);
            requestParams.setBodyContent(JSON.toJSONString(request));
            final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
            x.http().post(requestParams, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //Log.d(TAG, "onSuccess: "+result);
                    String code= JSON.parseObject(result).getString("code");
                    String msg=JSON.parseObject(result).getString("msg");
                    String data=JSON.parseObject(result).getString("data");
                    if (code.equals("10000")){
                        ViewUtil.showToast(AddDesk.this,data);
                        Intent intent=new Intent(AddDesk.this,DeskManager.class);
                        startActivity(intent);
                        finish();
                    }else {
                        ViewUtil.showToast(AddDesk.this,msg);
                    }
                }
                @Override
                public void onFinished() {
                    dialog.dismiss();
                    Log.d(TAG, "onFinished: 请求完成！");
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d(TAG, "onError: "+ex.getMessage().toString());
                    if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                        if (ex.getMessage().contains("failed to connect")){
                            ViewUtil.showToast(AddDesk.this,"网络连接有问题，请您切换到流畅网络。");
                        }
                    }
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

    }
}
