package com.colorlife.orderman.Activity.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.analysis.AnalysisActivity;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Activity.order.OrderWithDesk;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.Activity.setting.settingIndex;
import com.colorlife.orderman.R;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;

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

@ContentView(R.layout.index)
public class IndexActivity extends BaseActivity {
    private String TAG=this.toString();

    @ViewInject(R.id.index_linearLayout_addMenu)
    private LinearLayout addMenu;
    @ViewInject(R.id.index_linearLayout_analysis)
    private LinearLayout analysis;
    @ViewInject(R.id.index_linearLayout_downloadMenu)
    private LinearLayout download;
    @ViewInject(R.id.index_linearLayout_NoDeskOrder)
    private LinearLayout noDeskOrder;
    @ViewInject(R.id.index_linearLayout_order)
    private LinearLayout order;
    @ViewInject(R.id.index_linearLayout_orderManager)
    private LinearLayout orderManager;
    @ViewInject(R.id.index_imageView_exit)
    private ImageView exit;
    @ViewInject(R.id.index_imageView_setting)
    private ImageView setting;

    //一键下载菜单到本地
    @Event(R.id.index_linearLayout_downloadMenu)
    private void setAddMenu(View view){
    }

    //进入订单管理界面
    @Event(R.id.index_linearLayout_orderManager)
    private void intoOrderManager(View view){
        Intent intent=new Intent(IndexActivity.this, OrderManager.class);
        startActivity(intent);
    }

    //进入点菜界面
    @Event(R.id.index_linearLayout_order)
    private void intoOrder(View view){
        Intent intent=new Intent(IndexActivity.this, OrderWithDesk.class);
        startActivity(intent);
    }

    //进入无餐位点菜界面
    @Event(R.id.index_linearLayout_NoDeskOrder)
    private void intoNoDeskOrder(View view){
        Intent intent=new Intent(IndexActivity.this, NoDeskOrder.class);
        startActivity(intent);
    }

    //进入新增菜单界面
    @Event(R.id.index_linearLayout_addMenu)
    private void intoAddMenu(View view){
        Intent intent=new Intent(IndexActivity.this, AddCook.class);
        startActivity(intent);
    }

    //进入销售统计界面
    @Event(R.id.index_linearLayout_analysis)
    private void intoAnalysis(View view){
        Intent intent=new Intent(IndexActivity.this, AnalysisActivity.class);
        startActivity(intent);
    }

    //进入设置
    @Event(R.id.index_imageView_setting)
    private void intoSetting(View view){
        Intent intent=new Intent(IndexActivity.this,settingIndex.class);
        startActivity(intent);
    }

    //退出
    @Event(R.id.index_imageView_exit)
    private void DoExit(View view){
        RequestParams params=new RequestParams(HttpUrl.User_exit_Url);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
                    SharedPreferences.Editor editor=sp2.edit();
                    editor.clear();
                    editor.commit();

                    SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                    SharedPreferences.Editor editor2 = sp.edit();
                    editor2.clear();
                    editor2.commit();

                    Intent intent=new Intent(IndexActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    ViewUtil.showToast(IndexActivity.this,msg);
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

}
