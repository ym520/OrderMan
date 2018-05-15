package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.util.staticContent.HttpUrl;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15 0015.
 */

public class CookManagerListAdapter extends ArrayAdapter<CookRequest> {
    private int resourceID;
    private DecimalFormat df = new DecimalFormat("#.00");
    ImageOptions imageOptions;
    private List<CookRequest> list=new ArrayList<>();

    public CookManagerListAdapter(Context context, int resource, List<CookRequest> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
        this.list=objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CookRequest cookRequest=getItem(position);
        CookManagerListAdapter.ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder=new CookManagerListAdapter.ViewHolder();
            viewHolder.CookName= (TextView) convertView.findViewById(R.id.cook_manager_list_TextView_cookName);
            viewHolder.CookPrice= (TextView) convertView.findViewById(R.id.cook_manager_list_TextView_cookPrice);
            viewHolder.CookImage= (ImageView) convertView.findViewById(R.id.cook_manager_list_imageView_cookImage);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (CookManagerListAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.CookName.setText(cookRequest.getName());
        viewHolder.CookPrice.setText(df.format(cookRequest.getPrice()));
        //设置列表图片，网络的和本地上传的
        if (cookRequest.getImageUrl()!=null && !"".equals(cookRequest.getImageUrl())){
            //网络的
            if (cookRequest.getImageUrl().contains("http")){
                x.image().bind(viewHolder.CookImage, cookRequest.getImageUrl(),getImageOptions());
            }else {
                //Log.d(this.toString(), "onBindViewHolder: "+HttpUrl.downloadImage+cookRequest.getImageUrl());
                x.image().bind(viewHolder.CookImage, HttpUrl.downloadImage+cookRequest.getImageUrl().replaceAll("\\\\","%5C"),getImageOptions());
            }
        }
        if (cookRequest.getStatus()==1){
            viewHolder.status.setChecked(true);
        }else if (cookRequest.getStatus()==2){
            viewHolder.status.setChecked(false);
        }
        return convertView;
    };

    public void addMore(List<CookRequest> moreList){
        if (moreList.size()>0){
            for (CookRequest r:moreList){
                this.list.add(r);
            }
        }
    }

    class ViewHolder{
        TextView CookName;
        TextView CookPrice;
        ImageView CookImage;
        Switch status;
    };

    //设置图片显示参数
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
