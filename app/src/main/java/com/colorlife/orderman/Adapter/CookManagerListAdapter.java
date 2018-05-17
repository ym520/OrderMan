package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.cook.CookManager;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.dou361.dialogui.DialogUIUtils;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Administrator on 2018/5/15 0015.
 */

public class CookManagerListAdapter extends ArrayAdapter<CookRequest> {
    private int resourceID;
    private DecimalFormat df = new DecimalFormat("#.00");
    ImageOptions imageOptions;
    private Context context;
    String TAG=this.toString();
    private List<CookRequest> list=new ArrayList<>();

    public CookManagerListAdapter(Context context, int resource, List<CookRequest> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
        this.list=objects;
        this.context=context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final CookRequest cookRequest=getItem(position);
        final CookManagerListAdapter.ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder=new CookManagerListAdapter.ViewHolder();
            viewHolder.CookName= (TextView) convertView.findViewById(R.id.cook_manager_list_TextView_cookName);
            viewHolder.CookPrice= (TextView) convertView.findViewById(R.id.cook_manager_list_TextView_cookPrice);
            viewHolder.CookImage= (ImageView) convertView.findViewById(R.id.cook_manager_list_imageView_cookImage);
            viewHolder.status= (Switch) convertView.findViewById(R.id.cook_manager_list_switch_status);
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
        viewHolder.status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.status.isChecked()){
                    closeOrOpenDesk(cookRequest.getId(),1,viewHolder.status);
                }else{
                    closeOrOpenDesk(cookRequest.getId(),2,viewHolder.status);
                }
            }
        });
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

    //关闭,开启菜品
    private void closeOrOpenDesk(Integer id, final Integer opt, final Switch status){
        RequestParams requestParams=new RequestParams(HttpUrl.closeOrOpenCookUrl);
        //获取cookie
        SharedPreferences sp2 = context.getSharedPreferences("cookie", MODE_PRIVATE);
        final String cookie=sp2.getString("JSESSIONID","");
        requestParams.addHeader("Cookie","JSESSIONID="+cookie);
        requestParams.addParameter("id",id);
        requestParams.addParameter("opt",opt);
        DialogUIUtils.showLoadingHorizontal(context,"数据加载中。。。",true).show();
        x.http().get(requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //Log.d(TAG, "onSuccess: "+result);
                DialogUIUtils.dismiss();
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                String data=JSON.parseObject(result).getString("data");
                if (code.equals("10000")){
                    if (opt==1){
                        Log.d(TAG, "onSuccess: 开启"+data);
                        status.setChecked(true);
                        ViewUtil.showToast(context,data);
                    }else {
                        Log.d(TAG, "onSuccess: 关闭"+data);
                        status.setChecked(false);
                        ViewUtil.showToast(context,data);
                    }
                }else {
                    if (opt==1){
                        Log.d(TAG, "onSuccess: 请求开启失败"+data);
                        status.setChecked(false);
                    }else {
                        Log.d(TAG, "onSuccess: 请求关闭失败"+data);
                        status.setChecked(true);
                    }
                    ViewUtil.showToast(context,msg);
                }
            }
            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                DialogUIUtils.dismiss();
                if (opt==1){
                    Log.d(TAG, "onSuccess: 请求开启失败");
                    status.setChecked(false);
                    ViewUtil.showToast(context,"请求开启失败");
                }else {
                    Log.d(TAG, "onSuccess: 请求关闭失败");
                    status.setChecked(true);
                    ViewUtil.showToast(context,"请求关闭失败");
                }
                Log.d(TAG, "onError: "+ex.getMessage().toString());
                if (ex.getMessage()!=null && !"".equals(ex.getMessage())){
                    if (ex.getMessage().contains("failed to connect")){
                        ViewUtil.showToast(context,"网络连接有问题，请您切换到流畅网络。");
                    }
                }
                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
                    String errorResult = httpEx.getResult();
                    Log.d(TAG, "onError: responseCode:"+responseCode);
                    Log.d(TAG, "onError: responseMsg:"+responseMsg);
                    Log.d(TAG, "onError: errorResult:"+errorResult);
                } else { // 其他错误
                    // ...
                }
            }
            @Override
            public void onCancelled(CancelledException cex) {
                Log.d(TAG, "onCancelled: "+cex.getMessage().toString());
            }
        });

    }
}
