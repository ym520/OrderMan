package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderListVo;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ym on 2018/4/22.
 */

public class OrderManagerListAdapter extends ArrayAdapter<OrderListVo> {
    private DecimalFormat df = new DecimalFormat("#.00");
    private int resourceId;
    public OrderManagerListAdapter(Context context, int resource, List<OrderListVo> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderListVo orderListVo=getItem(position);
        ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.remark= (TextView) convertView.findViewById(R.id.orderManager_list_remark);
            viewHolder.saleTotal=(TextView) convertView.findViewById(R.id.orderManager_list_saleTotal);
            viewHolder.status=(TextView) convertView.findViewById(R.id.orderManager_list_status);
            viewHolder.createTime=(TextView) convertView.findViewById(R.id.orderManager_list_createTime);
            viewHolder.deskName=(TextView) convertView.findViewById(R.id.orderManager_list_deskName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.remark.setText(orderListVo.getRemark());
        viewHolder.saleTotal.setText(df.format(orderListVo.getSaleTotal()));
        if (orderListVo.getStatus()==1){
            viewHolder.status.setText("已下单，未结账");
        }else if (orderListVo.getStatus()==2){
            viewHolder.status.setText("已付款");
        }

        viewHolder.createTime.setText(orderListVo.getCreateTime());
        viewHolder.deskName.setText(orderListVo.getDeskName());
        return convertView;
    }

    class ViewHolder{
        TextView remark;
        TextView saleTotal;
        TextView status;
        TextView createTime;
        TextView deskName;

    }
}
