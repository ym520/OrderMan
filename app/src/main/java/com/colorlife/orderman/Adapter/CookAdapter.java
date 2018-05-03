package com.colorlife.orderman.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;

import java.util.List;

/**
 * Created by ym on 2018/4/27.
 */

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.ViewHolder> {
    List<CookRequest> cookRequestList;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cookName;
        public TextView cookPrice;
        public TextView cookSelectCount;
        public ImageView CookImg;
        public ViewHolder(View view){
            super(view);
            cookName= (TextView) view.findViewById(R.id.cook_textView_name);
            cookPrice= (TextView) view.findViewById(R.id.cook_textView_price);
            cookSelectCount= (TextView) view.findViewById(R.id.cook_textView_select_count);
            CookImg= (ImageView) view.findViewById(R.id.cook_list_img);
        }
    }
    public CookAdapter(List<CookRequest> requests){
        this.cookRequestList=requests;
    }

    //更新数据
    public void update(List<CookRequest> cookRequests){
        this.cookRequestList=cookRequests;
        notifyDataSetChanged();
    }

    //新增数据
    public void addMore(List<CookRequest> cookRequests){
        if (cookRequests.size()>0){
            for (CookRequest request:cookRequests){
                this.cookRequestList.add(request);
            }
            notifyDataSetChanged();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cook_list,parent,false);
        CookAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CookRequest cookRequest=cookRequestList.get(position);
        holder.cookName.setText(cookRequest.getName());
        holder.cookPrice.setText(cookRequest.getPrice()+"/每份");
    }

    @Override
    public int getItemCount() {
        return this.cookRequestList.size();
    }
}
