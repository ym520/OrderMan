package com.colorlife.orderman.Adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.util.staticContent.HttpUrl;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.util.List;

/**
 * Created by ym on 2018/4/27.
 */

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.ViewHolder> {
    List<CookRequest> cookRequestList;
    ImageOptions imageOptions;

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
        Log.d(this.toString(), "onBindViewHolder: "+cookRequest.getName());
        holder.cookName.setText(cookRequest.getName());
        holder.cookPrice.setText(cookRequest.getPrice()+"/每份");
        if (cookRequest.getImageUrl()!=null && !"".equals(cookRequest.getImageUrl())){
            if (cookRequest.getImageUrl().contains("http")){
                x.image().bind(holder.CookImg, cookRequest.getImageUrl(),getImageOptions());
            }else {
                x.image().bind(holder.CookImg, HttpUrl.downloadImage+cookRequest.getImageUrl(),getImageOptions());
            }
        }

    }

    @Override
    public int getItemCount() {
        return this.cookRequestList.size();
    }

    public ImageOptions getImageOptions(){
        if (imageOptions!=null){
            return imageOptions;
        }else {
            imageOptions = new ImageOptions.Builder()
                    .setSize(DensityUtil.dip2px(110), DensityUtil.dip2px(110))
                    .setRadius(DensityUtil.dip2px(5))
                    // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                    .setCrop(true)
                    // 加载中或错误图片的ScaleType
                    //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                    .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                    //设置加载过程中的图片
                    .setLoadingDrawableId(R.mipmap.load)
                    //设置加载失败后的图片
                    .setFailureDrawableId(R.mipmap.load_failure)
                    //设置使用缓存
                    .setUseMemCache(true)
                    //设置支持gif
                    .setIgnoreGif(false)
                    //设置显示圆形图片
                    .setCircular(false)
                    .setSquare(true)
                    .build();
            return imageOptions;
        }


    }
}
