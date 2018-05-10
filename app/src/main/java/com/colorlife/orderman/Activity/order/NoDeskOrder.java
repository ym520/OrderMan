package com.colorlife.orderman.Activity.order;

import android.content.Intent;
import android.content.SharedPreferences;
import android.gesture.GestureUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Adapter.CookAdapter;
import com.colorlife.orderman.Adapter.CookTypeAdapter;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.domain.CookTypeList;
import com.colorlife.orderman.domain.OrderDetailRequest;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
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
@ContentView(R.layout.no_desk_order)
public class NoDeskOrder extends AppCompatActivity {
    private String TAG=this.toString();
    //菜品列表
    @ViewInject(R.id.noDeskOrder_cook_recyclerView)
    private RecyclerView cookRecyclerView;
    //菜品类型列表
    @ViewInject(R.id.noDeskOrder_cookType_recyclerView)
    private RecyclerView cookTypeRecyclerView;
    //关键词输入框
    @ViewInject(R.id.noDeskOrder_edit_cookName)
    private EditText cookName;
    //返回按钮
    @ViewInject(R.id.noDeskOrder_imageButton_back)
    private ImageButton back;
    //输入框清除按钮
    @ViewInject(R.id.noDeskOrder_ImageView_delete)
    private ImageView delete;
    //上拉加载更多下拉刷新
    @ViewInject(R.id.noDeskOrder_PullToRefreshLayout_refresh)
    private PullToRefreshLayout refresh;
    //下单按钮
    @ViewInject(R.id.noDeskOrder_textView_order)
    private TextView order;
    //下单的菜总量
    @ViewInject(R.id.noDeskOrder_textView_cookCountInOrder)
    private TextView OrderCount;
    //下单总价钱
    @ViewInject(R.id.noDeskOrder_textView_sellCount)
    private TextView priceCount;


    private Integer pn=1;
    private String keyWord="";
    private Integer cookTypeId=0;
    private List<CookTypeList> cookTypeList=new ArrayList<>();
    private List<CookRequest> cookList=new ArrayList<>();

    private List<OrderDetailRequest> wantOrderCookList=new ArrayList<>();

    private CookTypeAdapter cookTypeAdapter;
    private CookAdapter cookAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(15,StaggeredGridLayoutManager.VERTICAL);
        cookTypeRecyclerView.setLayoutManager(layoutManager);
        cookTypeAdapter=new CookTypeAdapter(cookTypeList);
        cookTypeRecyclerView.setAdapter(cookTypeAdapter);
        cookTypeAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick: view:"+view.getId()+" position:"+position+"  cookTypeId:"+StatusUtil.cookTypeId);
                pn=1;
                initCookData();
            }
        });

        StaggeredGridLayoutManager layoutManager2=new StaggeredGridLayoutManager(8,StaggeredGridLayoutManager.VERTICAL);
        cookRecyclerView.setLayoutManager(layoutManager2);
        cookAdapter=new CookAdapter(cookList,OrderCount,wantOrderCookList);
        cookRecyclerView.setAdapter(cookAdapter);

        initCookTypeData();
        initCookData();

        //刷新相关
        refresh.setRefreshListener(new BaseRefreshListener() {
            //下拉刷新
            @Override
            public void refresh() {
                pn=1;
                StatusUtil.cookTypeId=0;
                initCookData();
                refresh.finishRefresh();
            }

            //上拉加载更多
            @Override
            public void loadMore() {
                pn++;
                initCookData();
                refresh.finishLoadMore();
            }
        });

    }

    //搜索关键词
    @Event(value = R.id.orderManager_EditText_keyword,type = TextWatcher.class)
    private void findCookByKeyWord(View view){
        keyWord=cookName.getText().toString();
        pn=1;
        initCookData();
    }
    //清除输入框数据
    @Event(R.id.noDeskOrder_ImageView_delete)
    private void deleteEditData(View  view){
        cookName.setText("");
    }
    //下单
    @Event(R.id.noDeskOrder_textView_order)
    private void doOrder(View view){
        //获取数据
        OrderRequest request=new OrderRequest();
        request.setStatus(1);
        request.setDeskName("无桌位");
        request.setDeskId(0);
        request.setNumber(System.currentTimeMillis()+"");
        request.setId(0);
        request.setUserId(0);
        request.setType(1);
        request.setSaleTotal(Double.valueOf(priceCount.getText().toString()));
        request.setOrderDetailRequests(wantOrderCookList);
        doCreateOrder(request);;
    }

    //初始化菜单数据
    public void initCookData() {
        RequestParams params=new RequestParams(HttpUrl.findAllCookByTypeIdUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.addParameter("pn",pn);
        params.addParameter("pageSize",30);
        params.addParameter("typeId",StatusUtil.cookTypeId);
        params.addParameter("keyWord",keyWord);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    cookList=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),CookRequest.class);
                    if (cookList.size()==0){
                        if (pn==1){
                            cookAdapter.update(cookList);
                        }
                        ViewUtil.showToast(NoDeskOrder.this,"查询不到数据！");
                    }else {
                        if (pn==1){
                            cookAdapter.update(cookList);
                        }
                        cookAdapter.addMore(cookList);
                        Log.d(TAG, "onSuccess: 数据总个数："+cookList.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(NoDeskOrder.this,msg);
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
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")/*||ex.getMessage().contains("isConnected failed: EHOSTUNREACH")*/){
                        ViewUtil.showToast(NoDeskOrder.this,"网络连接有问题，请您切换到流畅网络。");
                        refresh.finishLoadMore();
                        refresh.finishRefresh();
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

    //初始化菜品类型数据
    private void initCookTypeData() {
        //新增默认数据
        CookTypeList cookType=new CookTypeList();
        cookType.setId(0);
        cookType.setTypeName("全部");
        cookTypeList.add(cookType);

        RequestParams params=new RequestParams(HttpUrl.findAllTypeCookUrl);
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
                    cookTypeList=JSON.parseArray(JSON.parseObject(result).getString("data"),CookTypeList.class);
                    if (cookTypeList.size()==0){
                        ViewUtil.showToast(NoDeskOrder.this,"查询不到数据！");
                    }else {
                        cookTypeAdapter.updateData(cookTypeList);
                        Log.d(TAG, "onSuccess: 数据总个数："+cookTypeList.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(NoDeskOrder.this,msg);
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

    //跳转到登录
    private void intoLogin(){
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public int doCreateOrder(final OrderRequest orderRequest){
        RequestParams params=new RequestParams(HttpUrl.addOrderUrl);
        params.setAsJsonContent(true);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setBodyContent(JSON.toJSONString(orderRequest));
        final int[] orderId = new int[1];

        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String orderID=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    ViewUtil.showToast(NoDeskOrder.this,msg);
                    if (orderID!=null && !"".equals(orderID)){
                        orderId[0] =Integer.valueOf(orderID);
                        //跳转
                        Intent intent=new Intent(NoDeskOrder.this, OrderDetail.class);
                        //绑定数据
                        intent.putExtra("orderId",orderId[0]);
                        startActivity(intent);
                    }
                }else {
                    ViewUtil.showToast(NoDeskOrder.this,msg);
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

        return orderId[0];
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
