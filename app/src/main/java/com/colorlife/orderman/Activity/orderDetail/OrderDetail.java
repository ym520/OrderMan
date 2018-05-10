package com.colorlife.orderman.Activity.orderDetail;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Adapter.OrderDetailAdapter;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderDetailRequest;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ym on 2018/4/30.
 */

@ContentView(R.layout.order_detail)
public class OrderDetail extends AppCompatActivity {

    private String TAG=this.toString();

    @ViewInject(R.id.orderDetail_textView_deskName)
    private TextView deskName;
    @ViewInject(R.id.orderDetail_textView_sellCount)
    private TextView sellPriceCount;
    @ViewInject(R.id.orderDetail_button_doOrder)
    private Button Order;
    @ViewInject(R.id.orderDetail_textView_back)
    private TextView back;
    @ViewInject(R.id.orderDetail_ImageView_moreMenu)
    private ImageView moreMenu;
    @ViewInject(R.id.orderDetail_listView_orderCode)
    private TextView orderCode;
    @ViewInject(R.id.orderDetail_textView_orderTime)
    private TextView orderTime;
    @ViewInject(R.id.orderDetail_listView_CookDetail)
    private ListView orderDetail;

    private OrderRequest orderRequest=new OrderRequest();
    List<OrderDetailRequest> list=new ArrayList<>();
    private Integer id=0;
    private OrderDetailAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        Log.d(TAG, "onCreate: "+getClass().getSimpleName());
        adapter=new OrderDetailAdapter(this,R.layout.order_detail_list,list);
        orderDetail.setAdapter(adapter);

        //从前一个页面获取的数据（id），进行数据查询
        id=getIntent().getIntExtra("orderId",0);
       /* if (orderId!=null){
            id=Integer.valueOf(orderId);
        }*/
        initData(id);
    }

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
                    sellPriceCount.setText(df.format(orderRequest.getSaleTotal()));
                    //订单号
                    orderCode.setText("订单号:"+orderRequest.getNumber());
                    //设置下单时间
                    orderTime.setText(orderRequest.getCreateTime());

                    if (orderRequest!=null){
                        list=orderRequest.getOrderDetailRequests();
                        adapter.clear();
                        adapter.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                    if (list.size()==0){
                        ViewUtil.showToast(OrderDetail.this,"查询不到数据！");
                    }else {
                        Log.d(TAG, "onSuccess: 数据总个数："+list.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(OrderDetail.this,msg);
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
                Log.d(TAG, "onError: "+ex.getMessage().toString());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(OrderDetail.this,"网络连接有问题，请您切换到流畅网络。");
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
