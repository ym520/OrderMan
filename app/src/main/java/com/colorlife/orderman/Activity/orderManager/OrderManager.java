package com.colorlife.orderman.Activity.orderManager;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Adapter.OrderManagerListAdapter;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderListVo;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.dou361.dialogui.DialogUIUtils;
import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ym on 2018/4/21.
 */
@ContentView(R.layout.order_manager)
public class OrderManager extends AppCompatActivity {
    String TAG=this.toString();

    @ViewInject(R.id.orderManager_listView_list)
    private ListView list;

    @ViewInject(R.id.orderManager_textView_DealWith)
    private TextView dealWith;

    @ViewInject(R.id.orderManager_textView_DealWith_all)
    private TextView dealWithAll;

    @ViewInject(R.id.orderManager_textView_noDealWith)
    private TextView noDealWith;

    @ViewInject(R.id.orderManager_button_orderWithDesk)
    private Button orderWithDesk;

    @ViewInject(R.id.orderManager_button_orderWithoutDesk)
    private Button orderWithoutDesk;

    @ViewInject(R.id.orderManager_EditText_keyword)
    private EditText OrderKeyword;

    @ViewInject(R.id.orderManager_ImageView_search)
    private ImageView search;
    @ViewInject(R.id.orderManager_PullToRefreshLayout_refresh)
    private PullToRefreshLayout refresh;

    private Integer status=1;
    private Integer pn=1;
    private Integer type=1;
    private String keyword=null;
    private OrderManagerListAdapter adapter;
    private List<OrderListVo> orderListVos=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Log.d("BaseActivity",getClass().getSimpleName());
        ActivityCollector.addActivity(this);
        adapter=new OrderManagerListAdapter(OrderManager.this,R.layout.order_list,orderListVos);
        list.setAdapter(adapter);
        initData(pn,status,type,keyword);

        refresh.setRefreshListener(new BaseRefreshListener() {
            //刷新
            @Override
            public void refresh() {
                Log.d(TAG, "refresh: 刷新");
                pn=1;
                initData(pn,status,type,keyword);
                refresh.finishRefresh();
            }

            //加载更多
            @Override
            public void loadMore() {
                Log.d(TAG, "loadMore: 加载更多");
                pn++;
                initData(pn,status,type,keyword);
                refresh.finishLoadMore();
            }
        });

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //获取列表数据
                OrderListVo listVo=orderListVos.get(i);
                Log.d(TAG, "onItemClick: orderId="+listVo.getId());
                Intent intent=new Intent(OrderManager.this, OrderDetail.class);
                intent.putExtra("orderId",listVo.getId());
                startActivity(intent);
            }
        });
    }

    private void initData(final Integer pn, Integer status, Integer type, String keyword){
        RequestParams requestParams=new RequestParams(HttpUrl.orderManagetUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");

        requestParams.addHeader("Cookie","JSESSIONID="+cookie);
        requestParams.addParameter("pn",pn);
        requestParams.addParameter("pageSize",15);
        requestParams.addParameter("status",status);
        requestParams.addParameter("type",type);
        requestParams.addParameter("keyword",keyword);
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().post(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.d(TAG, "onSuccess: "+result);
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    orderListVos=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),OrderListVo.class);
                    if (orderListVos.size()==0){
                        Log.d(TAG, "onSuccess: 数据总个数："+orderListVos.size());
                        if (pn==1){
                            adapter.clear();
                        }
                        adapter.addAll(orderListVos);
                        adapter.notifyDataSetChanged();
                        ViewUtil.showToast(OrderManager.this,"查询不到数据！");
                    }else {
                        Log.d(TAG, "onSuccess: 数据总个数："+orderListVos.size());
                        if (pn==1){
                            adapter.clear();
                        }
                        adapter.addAll(orderListVos);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(OrderManager.this,msg);
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
                        ViewUtil.showToast(OrderManager.this,"网络连接有问题，请您切换到流畅网络。");
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
    //所有的订单
    @Event(R.id.orderManager_textView_DealWith_all)
    private void getAllDealWithOrder(View view){
        dealWithAll.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        dealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        noDealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        status=0;
        pn=1;
        initData(pn,status,type,keyword);
    }

    //已经处理的订单
    @Event(R.id.orderManager_textView_DealWith)
    private void getDealWithOrder(View view){
        dealWithAll.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        dealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        noDealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        status=2;
        pn=1;
        initData(pn,status,type,keyword);
    }

    //没有处理的订单
    @Event(R.id.orderManager_textView_noDealWith)
    private void getNODealWithOrder(View view){
        dealWithAll.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        dealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        noDealWith.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        status=1;
        pn=1;
        initData(pn,status,type,keyword);
    }

    //查询有桌位的订单
    @Event(R.id.orderManager_button_orderWithDesk)
    private void getOrderWithDesk(View view){
        orderWithoutDesk.setBackgroundColor(Color.parseColor("#ffffff"));
        orderWithDesk.setBackgroundColor(Color.parseColor("#a4a2a2"));
        type=1;
        pn=1;
        initData(pn,status,type,keyword);
    }

    //查询没有桌位的订单
    @Event(R.id.orderManager_button_orderWithoutDesk)
    private void getOrderWithoutDesk(View view){
        orderWithoutDesk.setBackgroundColor(Color.parseColor("#a4a2a2"));
        orderWithDesk.setBackgroundColor(Color.parseColor("#ffffff"));
        type=2;
        pn=1;
        initData(pn,status,type,keyword);
    }

    //查询关键字
    @Event(R.id.orderManager_ImageView_search)
    private void getKeywordOrder(View view){
        keyword=OrderKeyword.getText().toString();
        initData(pn,status,type,keyword);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        orderListVos.clear();
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initData(pn,status,type,keyword);
    }
}
