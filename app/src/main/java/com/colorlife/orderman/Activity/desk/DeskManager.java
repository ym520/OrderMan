package com.colorlife.orderman.Activity.desk;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.IdentityHashMap;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.Activity.setting.SettingIndex;
import com.colorlife.orderman.Adapter.DeskManagerAdapter;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;
import com.colorlife.orderman.domain.OrderListVo;
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
 * Created by ym on 2018/5/3 0003.
 */
@ContentView(R.layout.desk_manager)
public class DeskManager extends AppCompatActivity {
    private String TAG=this.toString();

    @ViewInject(R.id.deskManager_cookType_listView)
    private ListView deskListView;
    @ViewInject(R.id.deskManager_PullToRefreshLayout_refresh)
    private PullToRefreshLayout refresh;

    private DeskManagerAdapter deskAdapter;
    private List<DeskList> deskLists=new ArrayList<>();
    private Integer status=0;
    private Integer pn=1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        deskAdapter=new DeskManagerAdapter(this,R.layout.desk_manager_list,deskLists);
        deskListView.setAdapter(deskAdapter);
        initData(pn,status);
        refresh.setRefreshListener(new BaseRefreshListener() {
            //刷新
            @Override
            public void refresh() {
                Log.d(TAG, "refresh: 刷新");
                pn=1;
                initData(pn,status);
                refresh.finishRefresh();
            }

            //加载更多
            @Override
            public void loadMore() {
                Log.d(TAG, "loadMore: 加载更多");
                pn++;
                initData(pn,status);
                refresh.finishLoadMore();
            }
        });
        deskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick: ");
                final DeskList list=deskLists.get(i);
                final Switch status= (Switch) view.findViewById(R.id.deskManager_list_Switch_status);
                if (status.isChecked()){
                    closeOrOpenDesk(list.getId(),4,status);
                }else {
                    closeOrOpenDesk(list.getId(),1,status);
                }
            }
        });


    }
    @Event(R.id.deskManager_imageButton_add)
    private void intoAddDesk(View view){
        Intent intent=new Intent(DeskManager.this,AddDesk.class);
        startActivity(intent);
    }
    @Event(R.id.deskManager_imageButton_back)
    private void doBack(View view){
        Intent intent=new Intent(DeskManager.this, SettingIndex.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
        deskLists.clear();
    }

    //初始化数据
    private void initData(final Integer pn, Integer status){
        RequestParams requestParams=new RequestParams(HttpUrl.findAllDeskUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");

        requestParams.addHeader("Cookie","JSESSIONID="+cookie);
        requestParams.addParameter("pn",pn);
        requestParams.addParameter("pageSize",20);
        requestParams.addParameter("status",status);
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.d(TAG, "onSuccess: "+result);
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    deskLists=JSON.parseArray(JSON.parseObject(JSON.parseObject(result).getString("data")).getString("list"),DeskList.class);
                    if (deskLists.size()==0){
                        Log.d(TAG, "onSuccess: 数据总个数："+deskLists.size());
                        if (pn==1){
                            deskAdapter.clear();
                        }
                        deskAdapter.addAll(deskLists);
                        deskAdapter.notifyDataSetChanged();
                        ViewUtil.showToast(DeskManager.this,"查询不到数据！");
                    }else {
                        Log.d(TAG, "onSuccess: 数据总个数："+deskLists.size());
                        if (pn==1){
                            deskAdapter.clear();
                        }
                        deskAdapter.addAll(deskLists);
                        deskAdapter.notifyDataSetChanged();
                        Log.d(TAG, "onSuccess: 数据刷新完成！");
                    }
                }else {
                    ViewUtil.showToast(DeskManager.this,msg);
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
                        ViewUtil.showToast(DeskManager.this,"网络连接有问题，请您切换到流畅网络。");
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

    //关闭,开启桌位
    private void closeOrOpenDesk(Integer id, final Integer opt, final Switch status){
        RequestParams requestParams=new RequestParams(HttpUrl.closeOrOpenDeskUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        requestParams.addHeader("Cookie","JSESSIONID="+cookie);
        requestParams.addParameter("id",id);
        requestParams.addParameter("opt",opt);
        final Dialog dialog = DialogUIUtils.showLoadingHorizontal(this,"数据加载中。。。",true).show();
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.d(TAG, "onSuccess: "+result);
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String data=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    if (opt==1){
                        Log.d(TAG, "onSuccess: 开启"+data);
                        status.setChecked(true);
                    }else {
                        Log.d(TAG, "onSuccess: 关闭"+data);
                        status.setChecked(false);
                    }
                }else {
                    ViewUtil.showToast(DeskManager.this,msg);
                }
            }
            @Override
            public void onFinished() {
                dialog.dismiss();
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                refresh.finishLoadMore();
                refresh.finishRefresh();
                Log.d(TAG, "onError: "+ex.getMessage().toString());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(DeskManager.this,"网络连接有问题，请您切换到流畅网络。");
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
