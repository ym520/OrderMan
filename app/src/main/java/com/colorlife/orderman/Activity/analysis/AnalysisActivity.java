package com.colorlife.orderman.Activity.analysis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.main.IndexActivity;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.ReportVo;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;

import static android.content.ContentValues.TAG;

/**
 * Created by ym on 2018/4/21.
 */
@ContentView(R.layout.analysis)
public class AnalysisActivity extends AppCompatActivity {
    private DecimalFormat df = new DecimalFormat("#.00");
    String TAG=this.toString();
    @ViewInject(R.id.analysis_textView_clients)
    private TextView clients;
    @ViewInject(R.id.analysis_textView_orders)
    private TextView orders;
    @ViewInject(R.id.analysis_textView_prices)
    private TextView prices;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        initData();
    }

    private void initData() {
        RequestParams requestParams=new RequestParams(HttpUrl.anaDataUrl);
        //获取cookie
        SharedPreferences sp2 =getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        requestParams.addHeader("Cookie","JSESSIONID="+cookie);
        DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.d(TAG, "onSuccess: "+result);
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    ReportVo report=JSON.parseObject(JSON.parseObject(result).getString("data"),ReportVo.class);
                    if (report!=null){
                        clients.setText(report.getClients().toString());
                        prices.setText(df.format(report.getTotalSales()));
                        orders.setText(report.getOrderTotal().toString());
                    }

                }else {
                    ViewUtil.showToast(AnalysisActivity.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(AnalysisActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onFinished() {
                DialogUIUtils.dismiss();
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: "+ex.getMessage().toString());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(AnalysisActivity.this,"网络连接有问题，请您切换到流畅网络。");
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

    @Event(value = {R.id.analysis_imageButton_back,R.id.analysis_textView_back})
    private void doBack(View view){
        Intent intent=new Intent(AnalysisActivity.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


}
