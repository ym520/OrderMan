package com.colorlife.orderman.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.order.NoDeskOrder;
import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookTypeList;
import com.colorlife.orderman.util.staticContent.StatusUtil;

import java.util.List;

/**
 * Created by ym on 2018/4/27.
 */

public class CookTypeAdapter extends RecyclerView.Adapter<CookTypeAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    String TAG=this.toString();
    private List<CookTypeList> cookTypeLists;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView cookTypeName;

        public ViewHolder(View itemView) {
            super(itemView);
            cookTypeName= (TextView) itemView.findViewById(R.id.cookType_textView_name);
        }
    }
    public CookTypeAdapter(List<CookTypeList> lists){
        cookTypeLists=lists;
    }
    //更新数据
    public void updateData(List<CookTypeList> lists){
        this.cookTypeLists=lists;
        notifyDataSetChanged();
    }
    //新增数据
    public void addData(List<CookTypeList> lists){
        if (lists.size()>0){
            for (CookTypeList list:lists){
                this.cookTypeLists.add(list);
            }
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View  view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cooktype_list,parent,false);
        CookTypeAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CookTypeList cookType=cookTypeLists.get(position);
        holder.cookTypeName.setText(cookType.getTypeName());
        final View itemView=holder.itemView;
        if (onItemClickListener!=null){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "onClick: 菜单类型点击事件");
                    StatusUtil.cookTypeId=cookType.getId();
                    int position = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(itemView,position);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return this.cookTypeLists.size();
    }


}
