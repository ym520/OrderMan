package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Activity.order.OrderWithDesk;
import com.colorlife.orderman.Activity.orderDetail.OrderDetail;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;
import com.colorlife.orderman.domain.OrderRequest;
import com.dou361.dialogui.DialogUIUtils;
import com.dou361.dialogui.listener.DialogUIListener;

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
                            order.setPersonCount(Integer.valueOf(input2.toString()));
                        }
                    }).show();
                }else if (deskList.getStatus()==2){
                    Intent intent=new Intent(context, OrderDetail.class);
                    //查询当前的桌位被占用的订单
                    intent.putExtra("orderId",1);
                    context.startActivity(intent);
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
