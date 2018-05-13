package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.login.LoginActivity;
import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Activity.order.OrderWithDesk;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Activity.orderManager.OrderManager;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;
import com.colorlife.orderman.domain.OrderListVo;
import com.colorlife.orderman.domain.OrderRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;

import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by ym on 2018/4/23.
 */

public class DeskListAdapter extends RecyclerView.Adapter<DeskListAdapter.ViewHolder> {
    private Context context;
    private List<DeskList> deskLists;
    private OrderRequest order;
    private OnItemClickListener onItemClickListener;
    private Intent intent;
    private View view2;
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView deskName;
        ImageView deskImage;

        public ViewHolder(View itemView) {
            super(itemView);
            deskName= (TextView) itemView.findViewById(R.id.desk_textView_deskName);
            deskImage= (ImageView) itemView.findViewById(R.id.desk_textView_image);
        }
    }
    public DeskListAdapter(List<DeskList> deskLists, Context context){
        this.deskLists=deskLists;
        this.context=context;
    }

    public void updateData(List<DeskList> deskLists){
        this.deskLists = deskLists;
        notifyDataSetChanged();
    }

    public void addMore(List<DeskList> deskLists){
        if (deskLists.size()>0){
            for (DeskList deskList:deskLists){
                this.deskLists.add(deskList);
            }
            notifyDataSetChanged();
        }
    }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public DeskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.desk_list,parent,false);
        view2=view;
        DeskListAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeskListAdapter.ViewHolder holder, int position) {
        final DeskList deskList=deskLists.get(position);
        holder.deskName.setText(deskList.getCode()+"");
        if (deskList.getStatus()==2){
            holder.deskImage.setImageResource(R.mipmap.desk_using);
        }

        holder.deskImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (deskList.getStatus()==1){
                    order = new OrderRequest();
                    DialogUIUtils.showAlertInput(context, "开台  " + deskList.getCode(), null, "人数", "取消", "确定", true, false, new DialogUIListener() {
                        @Override
                        public void onPositive() {
                            //取消

                        }
                        @Override
                        public void onNegative() {
                            //确定
                            order.setDeskId(deskList.getId());
                            order.setDeskName(deskList.getCode());
                            intent = new Intent(context, NoDeskOrder.class);
                            intent.putExtra("order", order);
                            context.startActivity(intent);
                        }
                        @Override
                        public void onGetInput(CharSequence input1, CharSequence input2) {
                            if (input2.toString()!=null && !"".equals(input2.toString())){
                                order.setPersonCount(Integer.valueOf(input2.toString()));
                            }
                        }
                    }).show();
                }else if (deskList.getStatus()==2){
                    RequestParams requestParams=new RequestParams(HttpUrl.findOrderByDeskIdUrl);
                    requestParams.addParameter("id",deskList.getId());
                    x.http().get(requestParams, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            //Log.d(TAG, "onSuccess: "+result);
                            String code= JSON.parseObject(result).getString("code");
                            String msg=JSON.parseObject(result).getString("msg");
                            if (code.equals("10000")){
                                Integer orderId=Integer.valueOf(JSON.parseObject(result).getString("data"));
                               if (orderId!=null){
                                   Intent intent=new Intent(context, OrderDetail.class);
                                   //查询当前的桌位被占用的订单
                                   intent.putExtra("orderId",orderId);
                                   context.startActivity(intent);
                               }
                            }else {
                                ViewUtil.showToast(view2.getContext(),msg);
                                if (msg.equals("你当前没有登录！没有该权限")){
                                    StatusUtil.isLogin=false;
                                    Intent intent=new Intent(context, LoginActivity.class);
                                    context.startActivity(intent);
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
                                    ViewUtil.showToast(view2.getContext(),"网络连接有问题，请您切换到流畅网络。");
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
        });
    }

    @Override
    public int getItemCount() {
        return deskLists.size();
    }

    public void clearAll(){
        this.deskLists.clear();
    }
}
