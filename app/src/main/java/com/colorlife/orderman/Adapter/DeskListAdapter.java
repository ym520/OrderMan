package com.colorlife.orderman.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;

import java.util.List;

/**
 * Created by ym on 2018/4/23.
 */

public class DeskListAdapter extends RecyclerView.Adapter<DeskListAdapter.ViewHolder> {

    private List<DeskList> deskLists;
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView deskName;
        ImageView deskImage;

        public ViewHolder(View itemView) {
            super(itemView);
            deskName= (TextView) itemView.findViewById(R.id.desk_textView_deskName);
            deskImage= (ImageView) itemView.findViewById(R.id.desk_textView_image);
        }
    }
    public DeskListAdapter(List<DeskList> deskLists){
        this.deskLists=deskLists;
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

    @Override
    public DeskListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.desk_list,parent,false);
        DeskListAdapter.ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DeskListAdapter.ViewHolder holder, int position) {
        DeskList deskList=deskLists.get(position);
        holder.deskName.setText(deskList.getCode()+"");
        if (deskList.getStatus()==2){
            holder.deskImage.setImageResource(R.mipmap.desk_using);
        }
    }

    @Override
    public int getItemCount() {
        return deskLists.size();
    }

    public void clearAll(){
        this.deskLists.clear();
    }
}
