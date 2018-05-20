package com.colorlife.orderman.Activity.orderDetail;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Activity.orderManager.CheckOutOrder;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.Adapter.OrderDetailAdapter;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderDetailRequest;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
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
    @ViewInject(R.id.orderDetail_textView_orderPearsonCount)
    private TextView orderPearsonCount;

    private OrderRequest orderRequest=new OrderRequest();
    List<OrderDetailRequest> list=new ArrayList<>();
    private Integer id=0;
    private OrderDetailAdapter adapter;

    private int mWidth;
    private int mHeight;

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

    @Event(R.id.orderDetail_button_doOrder)
    private void intoOrder(View view){
        Intent intent=new Intent(OrderDetail.this, CheckOutOrder.class);
        intent.putExtra("orderId",id);
        startActivity(intent);
        finish();
    }

    @Event(R.id.orderDetail_ImageView_moreMenu)
    private void openMoreMenu(View view){
        Log.d(TAG, "openMoreMenu: ");
        View contentView= LayoutInflater.from(getApplicationContext()).inflate(R.layout.order_detail_menu,null);
        contentView.setBackgroundColor(Color.WHITE);
        //设置弹窗
        calWidthAndHeight(getApplicationContext());
        final PopupWindow popupWindow=new PopupWindow(findViewById(R.id.order_detail));
        popupWindow.setContentView(contentView);
        popupWindow.setHeight(mHeight);
        popupWindow.setWidth(mWidth);
        //设置背景
        setBackgroundAlpha(0.4f);
        //显示
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.showAsDropDown(moreMenu);
        //位置
        popupWindow.showAtLocation(view, Gravity.NO_GRAVITY,location[0]+view.getWidth(), location[1]);
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
        //设置控件
        LinearLayout addCook= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_add_cook);
        LinearLayout bindDesk= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_bind_desk);
        LinearLayout trueOrder= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_true_order);
        LinearLayout presentCook= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_present_cook);
        LinearLayout changeDesk= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_change_desk);
        LinearLayout mixDesk= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_mix_desk);
        LinearLayout editOrder= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_edit_order);
        LinearLayout undoOrder= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_undo_order);
        LinearLayout printPost= (LinearLayout) contentView.findViewById(R.id.orderDetail_linearLayout_print_pos);

        addCook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: addCook");
                Intent intent=new Intent(OrderDetail.this,NoDeskOrder.class);
                startActivity(intent);
            }
        });

        undoOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                undoOrder();
            }
        });

        bindDesk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUIUtils.showAlertInput(OrderDetail.this, "绑定桌位", "请输入进餐人数", "请输入桌位数字编号", "绑定", "取消", new DialogUIListener() {
                    @Override
                    public void onPositive() {

                    }

                    @Override
                    public void onNegative() {
                        
                    }

                    @Override
                    public void onGetInput(CharSequence input1, CharSequence input2) {
                        Log.d(TAG, "onGetInput: input1:"+input1.toString()+" input2:"+input2.toString());
                        orderRequest.setPersonCount(Integer.valueOf(input1.toString()));
                        orderRequest.setDeskId(Integer.valueOf(input2.toString()));
                        bindOrder();
                    }
                }).show();
            }
        });


        changeDesk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogUIUtils.showAlertInput(OrderDetail.this, "改变桌位 原桌位为:"+orderRequest.getDeskName(), null, "请输入新桌位数字编号", "绑定", "取消", new DialogUIListener() {
                    @Override
                    public void onPositive() {

                    }

                    @Override
                    public void onNegative() {

                    }

                    @Override
                    public void onGetInput(CharSequence input1, CharSequence input2) {
                        Log.d(TAG, "onGetInput: input1:"+input1.toString()+" input2:"+input2.toString());
                        orderRequest.setDeskId(Integer.valueOf(input2.toString()));
                        bindOrder();
                    }
                }).show();
            }
        });



    }

    /**
     * 设置PopupWindow的大小
     * @param context
     */
    private void calWidthAndHeight(Context context) {
        WindowManager wm= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics= new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mWidth= (int) (metrics.widthPixels*0.3);
        //设置高度为全屏高度的70%
        mHeight= (int) (metrics.heightPixels*0.4);
    }
    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha
     *            屏幕透明度0.0-1.0 1表示完全不透明
     */
    public void setBackgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity)OrderDetail.this).getWindow()
                .getAttributes();
        lp.alpha = bgAlpha;
        ((Activity) OrderDetail.this).getWindow().setAttributes(lp);
    }

    //初始化订单数据
    private void initData(Integer id) {
        RequestParams params=new RequestParams(HttpUrl.findOrderDetailUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.addParameter("id",id);
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    orderRequest=JSON.parseObject(JSON.parseObject(result).getString("data"),new TypeReference<OrderRequest>(){});


                    if (orderRequest!=null){
                        //桌子名称
                        deskName.setText(orderRequest.getDeskName());
                        //总价
                        DecimalFormat df = new DecimalFormat("#.00");
                        sellPriceCount.setText(df.format(orderRequest.getSaleTotal()));
                        //订单号
                        orderCode.setText("订单号:"+orderRequest.getNumber());
                        //设置下单时间
                        orderTime.setText(orderRequest.getCreateTime());
                        //设置人数
                        if (orderRequest.getPersonCount()==null || orderRequest.getPersonCount()==0){
                            orderPearsonCount.setText("人数： 无");
                        }else {
                            orderPearsonCount.setText("人数： "+orderRequest.getPersonCount());
                        }
                        if (orderRequest.getStatus()==2){
                            Order.setVisibility(View.INVISIBLE);
                            moreMenu.setVisibility(View.INVISIBLE);
                        }
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
                dialog.dismiss();
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


    //撤销订单
    private void undoOrder(){
        RequestParams params=new RequestParams(HttpUrl.undoOrderUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setAsJsonContent(true);
        params.setBodyContent(JSON.toJSONString(orderRequest));
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"请求操作中。。。",true).show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String data=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    dialog.dismiss();
                    ViewUtil.showToast(OrderDetail.this,data);
                    Intent intent=new Intent(OrderDetail.this, OrderManager.class);
                    startActivity(intent);
                    finish();
                }else {
                    ViewUtil.showToast(OrderDetail.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(OrderDetail.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
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

    //绑定桌位
    private void bindOrder(){
        RequestParams params=new RequestParams(HttpUrl.bindOrderURl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setAsJsonContent(true);
        params.setBodyContent(JSON.toJSONString(orderRequest));
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"请求操作中。。。",true).show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String data=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    dialog.dismiss();
                    ViewUtil.showToast(OrderDetail.this,data);
                    initData(id);
                }else {
                    ViewUtil.showToast(OrderDetail.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(OrderDetail.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
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

    //改变桌位
    private void changeDesk(){
        RequestParams params=new RequestParams(HttpUrl.changeDeskURl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.setAsJsonContent(true);
        params.setBodyContent(JSON.toJSONString(orderRequest));
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"请求操作中。。。",true).show();
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String data=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    dialog.dismiss();
                    ViewUtil.showToast(OrderDetail.this,data);
                    initData(id);
                }else {
                    ViewUtil.showToast(OrderDetail.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(OrderDetail.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
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
