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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ym on 2018/5/12.
 */

public class CookCardListAdapter extends ArrayAdapter<OrderDetailRequest> {
    private int resourceID;
    private DecimalFormat df = new DecimalFormat("#.00");

    public CookCardListAdapter(Context context, int resource, List<OrderDetailRequest> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        OrderDetailRequest detailRequest=getItem(position);
        CookCardListAdapter.ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder=new CookCardListAdapter.ViewHolder();
            viewHolder.CookName= (TextView) convertView.findViewById(R.id.pop_cookCard_textView_cookName);
            viewHolder.CookPrice= (TextView) convertView.findViewById(R.id.pop_cookCard_textView_cookPrice);
            viewHolder.CookCount= (TextView) convertView.findViewById(R.id.pop_cookCard_textView_cookCount);
            viewHolder.subtraction= (TextView) convertView.findViewById(R.id.pop_cookCard_textView_subtraction);
            viewHolder.add= (TextView) convertView.findViewById(R.id.pop_cookCard_textView_add);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (CookCardListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.CookName.setText(detailRequest.getCookName());
        viewHolder.CookPrice.setText(df.format(detailRequest.getPrice()));
        viewHolder.CookCount.setText(detailRequest.getCount().toString());
        return convertView;
    };

    class ViewHolder{
        TextView CookName;
        TextView CookPrice;
        TextView CookCount;
        TextView subtraction;
        TextView add;
    };
}
