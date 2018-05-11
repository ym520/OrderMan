package com.colorlife.orderman.Activity.order;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Adapter.CookAdapter;
import com.colorlife.orderman.Adapter.CookCardListAdapter;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static com.colorlife.orderman.R.id.pop_cookCard_LinearLayout_clearAll;

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
    //菜品购物车组件
    @ViewInject(R.id.noDeskOrder_FrameLayout_cookCard)
    private FrameLayout cookCard;

    private int mWidth;
    private int mHeight;

    private Integer pn=1;
    private String keyWord="";
    private Integer cookTypeId=0;
    private List<CookTypeList> cookTypeList=new ArrayList<>();
    private List<CookRequest> cookList=new ArrayList<>();

    private List<OrderDetailRequest> wantOrderCookList=new ArrayList<>();
    private OrderRequest request;
    private CookTypeAdapter cookTypeAdapter;
    private CookAdapter cookAdapter;
    private CookCardListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        //设置菜品类型列表
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

        //设置菜品列表
        StaggeredGridLayoutManager layoutManager2=new StaggeredGridLayoutManager(8,StaggeredGridLayoutManager.VERTICAL);
        cookRecyclerView.setLayoutManager(layoutManager2);
        cookAdapter=new CookAdapter(cookList,OrderCount,wantOrderCookList,priceCount);
        cookRecyclerView.setAdapter(cookAdapter);

        initCookTypeData();
        initCookData();
        request= (OrderRequest) getIntent().getSerializableExtra("order");
        if (request==null){
            request=new OrderRequest();
        }
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

    //打开购物车
    @Event(R.id.noDeskOrder_FrameLayout_cookCard)
    private void openCookCard(View view){
        Log.d(TAG, "openCookCard: ");
        View contentView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.cook_card,null);
        contentView.setBackgroundColor(Color.WHITE);
        //设置弹窗
        calWidthAndHeight(getApplicationContext());
        final PopupWindow popupWindow=new PopupWindow(findViewById(R.id.no_desk_order));
        popupWindow.setContentView(contentView);
        popupWindow.setHeight(mHeight);
        popupWindow.setWidth(mWidth);
        //设置背景
        setBackgroundAlpha(0.5f);
        //设置控件
        ListView listView= (ListView) contentView.findViewById(R.id.pop_cookCard_listView_cookList);
        adapter=new CookCardListAdapter(this,R.layout.pop_cook_list,wantOrderCookList);
        listView.setAdapter(adapter);

        //清除购物车的按钮
        LinearLayout clearAll= (LinearLayout) contentView.findViewById(pop_cookCard_LinearLayout_clearAll);
        clearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //清空购物车的数据
                wantOrderCookList.clear();
                adapter.notifyDataSetChanged();
                List<CookRequest> list=new ArrayList<CookRequest>();
                list.addAll(cookList);
                //清空原来的菜品列表数据，设置每一个点菜量为0，重新赋值
                cookList.clear();
                for (CookRequest request:list){
                    request.setOrderCount(0);
                    cookList.add(request);
                }
                //更新菜品购物车数据
                OrderCount.setText("0");
                //更新菜品列表
                cookAdapter.update(cookList);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                Log.d(TAG, "onItemClick: "+view.getId());
                final TextView add= (TextView) view.findViewById(R.id.pop_cookCard_textView_add);
                TextView sub= (TextView) view.findViewById(R.id.pop_cookCard_textView_subtraction);
                final TextView order_count= (TextView) view.findViewById(R.id.pop_cookCard_textView_cookCount);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int count=Integer.valueOf(wantOrderCookList.get(i).getCount());
                        count++;
                        //Log.d(TAG, "addonClick: "+count);
                        order_count.setText(String.valueOf(count));
                        OrderDetailRequest request=wantOrderCookList.get(i);
                        request.setCount(count);
                        wantOrderCookList.set(i,request);

                        for (int i=0;i<cookList.size();i++){
                            CookRequest c=cookList.get(i);
                            if (c.getId()==request.getCookId()){
                                c.setOrderCount(count);
                                cookList.set(i,c);
                                break;
                            }
                        }
                        //更新菜品的角标数据
                        cookAdapter.update(cookList,wantOrderCookList);
                    }
                });
                sub.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int count=Integer.valueOf(wantOrderCookList.get(i).getCount());
                        count--;
                        //Log.d(TAG, "addonClick: "+count);

                        if (count>0){
                            order_count.setText(String.valueOf(count));
                            OrderDetailRequest request=wantOrderCookList.get(i);
                            request.setCount(count);
                            wantOrderCookList.set(i,request);

                            for (int i=0;i<cookList.size();i++){
                                CookRequest c=cookList.get(i);
                                if (c.getId()==request.getCookId()){
                                    c.setOrderCount(count);
                                    cookList.set(i,c);
                                    break;
                                }
                            }
                            //更新菜品的角标数据
                            cookAdapter.update(cookList,wantOrderCookList);
                        }else if (count==0){
                            order_count.setText(String.valueOf(count));
                            OrderDetailRequest request=wantOrderCookList.get(i);
                            request.setCount(count);
                            wantOrderCookList.remove(i);
                            adapter.notifyDataSetChanged();

                            for (int i=0;i<cookList.size();i++){
                                CookRequest c=cookList.get(i);
                                if (c.getId()==request.getCookId()){
                                    c.setOrderCount(count);
                                    cookList.set(i,c);
                                    break;
                                }
                            }
                            //更新菜品的角标数据
                            cookAdapter.update(cookList,wantOrderCookList);
                        }

                    }
                });
            }
        });


        //显示
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(cookCard);
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,0,0);
        //点击屏幕外面PopupWindow消失
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    popupWindow.dismiss();
                }
                return false;
            }
        });
        //关闭时调整背景透明度
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                // popupWindow隐藏时恢复屏幕正常透明度
                setBackgroundAlpha(1.0f);
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
                    List<CookRequest> list=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),CookRequest.class);
                    for (CookRequest r:list){
                        r.setOrderCount(0);
                        cookList.add(r);
                    }
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
                    List<CookTypeList> List=JSON.parseArray(JSON.parseObject(result).getString("data"),CookTypeList.class);
                    if (List.size()>0){
                        for (CookTypeList i:List){
                            cookTypeList.add(i);
                        }
                    }
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

    /**
     * 设置PopupWindow的大小
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth=metrics.widthPixels;
        //设置高度为全屏高度的70%
        mHeight= (int) (metrics.heightPixels*0.5);
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity)NoDeskOrder.this).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) NoDeskOrder.this).getWindow().setAttributes(lp);
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
