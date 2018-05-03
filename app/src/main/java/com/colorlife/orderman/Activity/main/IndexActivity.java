package com.colorlife.orderman.Activity.main;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.analysis.AnalysisActivity;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Activity.order.OrderWithDesk;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by ym on 2018/4/19.
 */

@ContentView(R.layout.index)
public class IndexActivity extends BaseActivity {

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

}
