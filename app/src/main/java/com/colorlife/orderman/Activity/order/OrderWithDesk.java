package com.colorlife.orderman.Activity.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.main.IndexActivity;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.Adapter.DeskListAdapter;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;
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

import static android.content.ContentValues.TAG;

/**
 * Created by ym on 2018/4/21.
 */

@ContentView(R.layout.order)
public class OrderWithDesk extends AppCompatActivity {
    String TAG=this.toString();

    //全部
    @ViewInject(R.id.order_textView_all)
    private TextView all;
    //使用中
    @ViewInject(R.id.order_textView_user)
    private TextView inUser;
    //预订
    @ViewInject(R.id.order_textView_book)
    private TextView book;
    //空闲
    @ViewInject(R.id.order_textView_free)
    private TextView free;
    @ViewInject(R.id.order_recyclerView_desk)
    private RecyclerView deskListView;
    //刷新
    @ViewInject(R.id.order_ImageView_refresh)
    private ImageView ImageRefresh;
    //刷新控件
    @ViewInject(R.id.order_PullToRefreshLayout_refresh)
    private PullToRefreshLayout refresh;
    @ViewInject(R.id.order_imageButton_back)
    private ImageButton back;
    @ViewInject(R.id.order_textView_back)
    private TextView textViewBack;


    private DeskListAdapter adapter;
    private OrderRequest order;
    private DeskList deskList;
    private Intent intent;
    //当前页
    private Integer pn=1;
    //状态
    private Integer status=1;

    List<DeskList> deskLists=new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        DialogUIUtils.init(OrderWithDesk.this);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(6,StaggeredGridLayoutManager.VERTICAL);
        deskListView.setLayoutManager(layoutManager);
        adapter=new DeskListAdapter(deskLists,OrderWithDesk.this);
        deskListView.setAdapter(adapter);
        initDesk(pn,status);
        refresh.setRefreshListener(new BaseRefreshListener() {
            @Override
            public void refresh() {
                pn=1;
                initDesk(pn,status);
                refresh.finishRefresh();
            }

            @Override
            public void loadMore() {
                pn++;
                initDesk(pn,status);
                refresh.finishLoadMore();
            }
        });
    }

    public void initDesk(final Integer pn, final Integer status){
        RequestParams params=new RequestParams(HttpUrl.deskAllUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.addParameter("pn",pn);
        params.addParameter("pageSize",30);
        params.addParameter("status",status);
        DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    deskLists=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),DeskList.class);
                    if (deskLists.size()==0){
                        if (pn==1){
                            adapter.clearAll();
                            adapter.updateData(deskLists);
                        }
                        ViewUtil.showToast(OrderWithDesk.this,"查询不到数据！");
                    }else {
                        if (pn==1){
                            adapter.clearAll();
                        }
                        adapter.addMore(deskLists);
                        Log.d(TAG, "onSuccess: 数据总个数："+deskLists.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(OrderWithDesk.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        if (!StatusUtil.isLogin){
                            intoLogin();
                        }
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
                    if (ex.getMessage().contains("failed to connect")/*||ex.getMessage().contains("isConnected failed: EHOSTUNREACH")*/){
                        ViewUtil.showToast(OrderWithDesk.this,"网络连接有问题，请您切换到流畅网络。");
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

    //所有的状态
    @Event(R.id.order_textView_all)
    private void getAllDesk(View view){
        //设置背景
        inUser.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        all.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        book.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        free.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);

        status=0;
        pn=1;
        initDesk(pn,status);
        adapter.notifyDataSetChanged();
    }
    //在用状态
    @Event(R.id.order_textView_user)
    private void getInUserDesk(View view){
        //设置背景
        inUser.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        all.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        book.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        free.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);

        pn=1;
        status=2;
        initDesk(pn,status);
        adapter.notifyDataSetChanged();
    }
    //空闲状态
    @Event(R.id.order_textView_free)
    private void getFreeDesk(View view){
        //设置背景
        inUser.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        all.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        book.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        free.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);

        pn=1;
        status=1;
        initDesk(pn,status);
        adapter.notifyDataSetChanged();
    }
    //预订状态
    @Event(R.id.order_textView_book)
    private void getBookDesk(View view){
        //设置背景
        inUser.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        all.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        book.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        free.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);

        pn=1;
        status=3;
        initDesk(pn,status);
        adapter.notifyDataSetChanged();
    }

    //刷新
    @Event(R.id.order_ImageView_refresh)
    private void getRefresh(View view){
        inUser.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        all.setBackgroundResource(R.drawable.bg_ordemanage_detail_bank);
        book.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);
        free.setBackgroundResource(R.drawable.bg_ordemanage_detail_white);

        pn=1;
        status=0;
        initDesk(pn,status);
    }

    @Event({R.id.order_textView_back,R.id.order_imageButton_back})
    private void doBack(View view){
        Intent intent=new Intent(OrderWithDesk.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    private void intoLogin(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
