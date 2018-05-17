package com.colorlife.orderman.Activity.orderManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Adapter.PayWayAdapter;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderRequest;
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

/**
 * Created by ym on 2018/5/13.
 */
@ContentView(R.layout.check_out_order)
public class CheckOutOrder extends AppCompatActivity {
    String TAG=this.toString();
    private Integer id;
    private OrderRequest orderRequest=new OrderRequest();
    private PayWayAdapter adapter;

    @ViewInject(R.id.CheckOutOrder_textView_deskName)
    private TextView deskName;
    @ViewInject(R.id.CheckOutOrder_textView_orderPrice)
    private TextView orderPrice;
    @ViewInject(R.id.CheckOutOrder_textView_priceTotal)
    private TextView priceTotal;
    @ViewInject(R.id.CheckOutOrder_editText_discounts)
    private EditText discount;
    @ViewInject(R.id.CheckOutOrder_editText_remark)
    private EditText remark;
    @ViewInject(R.id.CheckOutOrder_recyclerView_payWay)
    private RecyclerView payWay_recyclerView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        id=getIntent().getIntExtra("orderId",0);
        initData(id);
        adapter=new PayWayAdapter(StatusUtil.getPayWayList());
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        payWay_recyclerView.setLayoutManager(layoutManager);
        payWay_recyclerView.setAdapter(adapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    @Event(value={R.id.CheckOutOrder_imageButton_back,R.id.CheckOutOrder_textView_deskName},type = View.OnClickListener.class)
    private void doBack(View view){
        Intent intent=new Intent(CheckOutOrder.this, OrderDetail.class);
        intent.putExtra("orderId",id);
        startActivity(intent);
        finish();
    }

    @Event(R.id.CheckOutOrder_button_trueOrder)
    private void doCheckOut(View view){
        orderRequest.setPaywayName(adapter.getPayWay());
        orderRequest.setPaywayValue(adapter.getPayWay());
        orderRequest.setStatus(2);
        doCheckOutOrder(orderRequest);
    }

    //初始化订单数据
    private void initData(Integer id) {
        RequestParams params=new RequestParams(HttpUrl.findOrderDetailUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.addParameter("id",id);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    orderRequest=JSON.parseObject(JSON.parseObject(result).getString("data"),new TypeReference<OrderRequest>(){});
                    //桌子名称
                    deskName.setText(orderRequest.getDeskName());
                    //总价
                    DecimalFormat df = new DecimalFormat("#.00");
                    orderPrice.setText(df.format(orderRequest.getSaleTotal()));
                    double price=orderRequest.getSaleTotal()-Double.valueOf(discount.getText().toString());
                    priceTotal.setText(df.format(price));
                }else {
                    ViewUtil.showToast(CheckOutOrder.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(CheckOutOrder.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }
            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: "+ex.getMessage().toString());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(CheckOutOrder.this,"网络连接有问题，请您切换到流畅网络。");
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
    public void doCheckOutOrder(final OrderRequest orderRequest){
        RequestParams params=new RequestParams(HttpUrl.orderCheckOutUrl);
        params.setAsJsonContent(true);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setBodyContent(JSON.toJSONString(orderRequest));
        DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    ViewUtil.showToast(CheckOutOrder.this,msg);
                    //跳转
                    Intent intent=new Intent(CheckOutOrder.this, OrderDetail.class);
                    //绑定数据
                    intent.putExtra("orderId",id);
                    startActivity(intent);
                    finish();
                }else {
                    ViewUtil.showToast(CheckOutOrder.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(CheckOutOrder.this,LoginActivity.class);
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
