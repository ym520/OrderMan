package com.colorlife.orderman.Adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colorlife.orderman.R;

import java.util.List;

/**
 * Created by ym on 2018/5/13.
 */

public class PayWayAdapter extends RecyclerView.Adapter<PayWayAdapter.ViewHolder> {

    private List<String> payWayList;
    private String orderPayWay;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView payWayName;

        public ViewHolder(View itemView) {
            super(itemView);
            payWayName= (TextView) itemView.findViewById(R.id.payWay_name);
        }
    }
    public PayWayAdapter(List<String> lists){
        payWayList=lists;
    }
    //更新数据
    public void updateData(List<String> lists){
        this.payWayList=lists;
        notifyDataSetChanged();
    }

    public String getPayWay(){
        return this.orderPayWay;
    }

    @Override
    public PayWayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.payway_list,parent,false);
        PayWayAdapter.ViewHolder viewHolder=new PayWayAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final PayWayAdapter.ViewHolder holder, int position) {
        final String payWay=payWayList.get(position);
        holder.payWayName.setText(payWay);
        holder.payWayName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderPayWay=payWay;
                holder.payWayName.setBackgroundResource(R.drawable.bg_pay_way_textview_blank);
                holder.payWayName.setTextColor(Color.WHITE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.payWayList.size();
    }
}
