package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.OrderDetailRequest;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ym on 2018/4/30.
 */

public class OrderDetailAdapter extends ArrayAdapter<OrderDetailRequest> {
    private int resourceID;
    private DecimalFormat df = new DecimalFormat("#.00");
    public OrderDetailAdapter(Context context, int resource, List<OrderDetailRequest> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailRequest detailRequest=getItem(position);
        ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder=new ViewHolder();
            viewHolder.CookName= (TextView) convertView.findViewById(R.id.orderDetail_list_textView_cookName);
            viewHolder.MenuPriceAndCount= (TextView) convertView.findViewById(R.id.orderDetail_list_textView_MenuPriceAndCount);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.CookName.setText(detailRequest.getCookName());
        viewHolder.MenuPriceAndCount.setText(df.format(detailRequest.getPrice())+" x "+detailRequest.getCount());
        return convertView;
    };

    class ViewHolder{
        TextView CookName;
        TextView MenuPriceAndCount;
    };
}
