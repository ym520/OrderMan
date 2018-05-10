package com.colorlife.orderman.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colorlife.orderman.Listener.OnItemClickListener;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.domain.OrderDetailRequest;
import com.colorlife.orderman.util.staticContent.HttpUrl;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by ym on 2018/4/27.
 */

public class CookAdapter extends RecyclerView.Adapter<CookAdapter.ViewHolder> {
    private OnItemClickListener onItemClickListener;
    private TextView orderCount;
    private TextView priceCount;
    List<CookRequest> cookRequestList;
    //点菜的列表单子
    private List<OrderDetailRequest> OrderCookList;
    ImageOptions imageOptions;

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cookName;
        public TextView cookPrice;
        public TextView cookSelectCount;
        public ImageView CookImg;
        public TextView orderCount;
        public ViewHolder(View view,TextView textView){
            super(view);
            cookName= (TextView) view.findViewById(R.id.cook_textView_name);
            cookPrice= (TextView) view.findViewById(R.id.cook_textView_price);
            cookSelectCount= (TextView) view.findViewById(R.id.cook_textView_select_count);
            CookImg= (ImageView) view.findViewById(R.id.cook_list_img);
            orderCount=textView;
        }
    }
    public CookAdapter(List<CookRequest> requests,TextView textView,List<OrderDetailRequest> list,TextView priceCount){
        this.cookRequestList=requests;
        this.orderCount=textView;
        this.OrderCookList=list;
        this.priceCount=priceCount;
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

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cook_list,parent,false);
        CookAdapter.ViewHolder viewHolder=new ViewHolder(view,this.orderCount);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CookRequest cookRequest=cookRequestList.get(position);
        //Log.d(this.toString(), "onBindViewHolder:  Name:"+cookRequest.getName()+" ImageUrl:"+cookRequest.getImageUrl());
        holder.cookName.setText(cookRequest.getName());
        DecimalFormat df = new DecimalFormat("#.00");
        holder.cookPrice.setText(df.format(cookRequest.getPrice())+"/每份");
        if (cookRequest.getImageUrl()!=null && !"".equals(cookRequest.getImageUrl())){
            if (cookRequest.getImageUrl().contains("http")){
                x.image().bind(holder.CookImg, cookRequest.getImageUrl(),getImageOptions());
            }else {
                //Log.d(this.toString(), "onBindViewHolder: "+HttpUrl.downloadImage+cookRequest.getImageUrl());
                x.image().bind(holder.CookImg, HttpUrl.downloadImage+cookRequest.getImageUrl().replaceAll("\\\\","%5C"),getImageOptions());
            }
        }
        holder.cookSelectCount.setText(0+"");
        holder.cookSelectCount.setVisibility(View.INVISIBLE);
        holder.orderCount.setText(0+"");
        holder.orderCount.setVisibility(View.INVISIBLE);
        holder.CookImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Log.d(this.toString(), "onClick: 图片点击事件");
                int count=Integer.valueOf(holder.cookSelectCount.getText().toString());
                int orderCount=Integer.valueOf(holder.orderCount.getText().toString());
                count++;
                orderCount++;
                holder.cookSelectCount.setText(count+"");
                holder.cookSelectCount.setVisibility(View.VISIBLE);
                holder.orderCount.setText(orderCount+"");
                holder.orderCount.setVisibility(View.VISIBLE);
                //获取当前的菜品信息，添加到想吃菜单购物车中
                OrderDetailRequest request=new OrderDetailRequest();
                request.setCookId(cookRequest.getId());
                request.setCookName(cookRequest.getName());
                request.setCount(count);
                request.setPrice(cookRequest.getPrice());
                request.setStatus(1);
                request.setId(0);
                request.setTasteId(0);
                //去掉重复的菜品，把数量更新
                if (OrderCookList.contains(request)){
                    OrderCookList.remove(request);
                    OrderCookList.add(request);
                }else {
                    OrderCookList.add(request);
                }

                if (OrderCookList.size()>0){
                    Double saleCount=0.00;
                    for (OrderDetailRequest d:OrderCookList){
                        saleCount=saleCount+d.getCount()*d.getPrice();
                    }
                    DecimalFormat df = new DecimalFormat("#.00");
                    priceCount.setText(df.format(saleCount));
                }


            }
        });

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
