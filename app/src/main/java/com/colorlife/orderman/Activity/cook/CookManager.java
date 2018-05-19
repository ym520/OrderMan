package com.colorlife.orderman.Activity.cook;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.desk.DeskManager;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.setting.SettingIndex;
import com.colorlife.orderman.Adapter.CookManagerListAdapter;
import com.colorlife.orderman.Adapter.CookTypeAdapter;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.domain.CookTypeList;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIItemListener;
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

/**
 * Created by ym on 2018/5/3 0003.
 */
@ContentView(R.layout.cook_manager)
public class CookManager extends AppCompatActivity {
    private String TAG=this.toString();

    @ViewInject(R.id.cookManager_cookType_recyclerView)
    private RecyclerView  cookTypeRecyclerView;
    @ViewInject(R.id.cookManager_cook_listView)
    private ListView cookListView;
    @ViewInject(R.id.cookManager_PullToRefreshLayout_refresh)
    private PullToRefreshLayout refresh;

    private CookTypeAdapter cookTypeAdapter;
    private CookManagerListAdapter cookManagerListAdapter;
    private List<CookTypeList> cookTypeList=new ArrayList<>();
    private List<CookRequest> cookList=new ArrayList<>();

    private Integer pn=1;
    private String keyWord="";
    private Integer cookTypeId=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
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
                Log.d(TAG, "onItemClick: view:"+view.getId()+" position:"+position+"  cookTypeId:"+ StatusUtil.cookTypeId);
                pn=1;
                CookTypeList list=cookTypeList.get(position);
                cookTypeId=list.getId();
                initCookData();
            }
        });

        cookManagerListAdapter=new CookManagerListAdapter(this,R.layout.cook_manager_list,cookList);
        cookListView.setAdapter(cookManagerListAdapter);
        cookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CookRequest request=cookList.get(i);
                Intent  intent=new Intent(CookManager.this,UpdateCook.class);
                intent.putExtra("cook",request);
                startActivity(intent);
            }
        });

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

        //初始化菜品类型数据
        initCookTypeData();
        //初始化菜品数据
        initCookData();
    }
    //新增按钮
    @Event(R.id.cookManager_imageButton_add)
    private void intoAddCook(View view){
        List<String> stringList=new ArrayList<>();
        stringList.add("新增菜单类型");
        stringList.add("新增菜品");
        DialogUIUtils.showBottomSheetAndCancel(this, stringList, "取消", new DialogUIItemListener() {
            @Override
            public void onItemClick(CharSequence text, int position) {
                if (position==0){
                    Log.d(TAG, "onItemClick: 0");
                    DialogUIUtils.showAlertInput(CookManager.this, "新增菜品类型", null, "菜品类型名称", "取消", "确定", new DialogUIListener() {
                        @Override
                        public void onPositive() {

                        }

                        @Override
                        public void onNegative() {

                        }

                        @Override
                        public void onGetInput(CharSequence input1, CharSequence input2) {
                            super.onGetInput(input1, input2);

                        }
                    });
                }else if (position==1){
                    Log.d(TAG, "onItemClick: 1");
                }
            }
        });
        Intent intent=new Intent(CookManager.this, AddCook.class);
        startActivity(intent);
    }
    //返回
    @Event(R.id.cookManager_imageButton_back)
    private void doBack(View view){
        Intent intent=new Intent(CookManager.this, SettingIndex.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
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
                        ViewUtil.showToast(CookManager.this,"查询不到数据！");
                    }else {
                        cookTypeAdapter.updateData(cookTypeList);
                        Log.d(TAG, "onSuccess: 数据总个数："+cookTypeList.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(CookManager.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(CookManager.this,LoginActivity.class);
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

    //初始化菜品数据
    public void initCookData() {
        RequestParams params=new RequestParams(HttpUrl.findAllCookByTypeIdUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        params.addParameter("pn",pn);
        params.addParameter("pageSize",30);
        params.addParameter("typeId",cookTypeId);
        params.addParameter("keyWord",keyWord);
        Log.d(TAG, "initCookData: params"+params.toString());
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    List<CookRequest> list=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),CookRequest.class);
                    cookList=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),CookRequest.class);
                    /*for (CookRequest r:list){
                        r.setOrderCount(0);
                        cookList.add(r);
                    }*/
                    if (cookList.size()==0){
                        if (pn==1){
                            cookManagerListAdapter.clear();
                        }
                        cookManagerListAdapter.addAll(cookList);
                        cookManagerListAdapter.notifyDataSetChanged();
                        ViewUtil.showToast(CookManager.this,"查询不到数据！");
                    }else {
                        if (pn==1){
                            cookManagerListAdapter.clear();
                        }
                        cookManagerListAdapter.addMore(cookList);
                        cookManagerListAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onSuccess: 数据总个数："+cookList.size());
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(CookManager.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                        Intent intent=new Intent(CookManager.this, LoginActivity.class);
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
                Log.d(TAG, "onError: "+ex.getMessage());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(CookManager.this,"网络连接有问题，请您切换到流畅网络。");
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

    //新增菜品操作
    public void doAddCookType(String name){
        
    }
}
