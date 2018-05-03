package com.colorlife.orderman.Activity.cook;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.CookRequest;
import com.colorlife.orderman.domain.CookTypeList;
import com.colorlife.orderman.util.ViewUtil;
import com.colorlife.orderman.util.staticContent.HttpUrl;
import com.colorlife.orderman.util.staticContent.StatusUtil;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TImage;
import com.jph.takephoto.model.TResult;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by ym on 2018/4/21.
 */
@ContentView(R.layout.add_cook)
public class AddCook extends TakePhotoActivity {

    private String TAG=this.toString();

    @ViewInject(R.id.addCook_linearLayout_camera)
    private LinearLayout doCamera;
    @ViewInject(R.id.addCook_ImageView_cookImage)
    private ImageView cookImage;
    @ViewInject(R.id.addCook_textView_imageName)
    private TextView imageName;
    @ViewInject(R.id.addCook_button_sure)
    private Button addCookTrue;
    @ViewInject(R.id.addCook_Spinner_cookType)
    private Spinner cookTypes;

    //菜名
    @ViewInject(R.id.addCook_editText_cookName)
    private EditText cookName;
    //菜价
    @ViewInject(R.id.addCook_editText_cookPrice)
    private EditText cookPrice;
    //菜类别名称
    @ViewInject(R.id.addCook_editText_cookTypeName)
    private EditText cookTypeName;
    //菜单位
    @ViewInject(R.id.addCook_editText_cookUnits)
    private EditText cookUnits;


    private CookRequest cookRequest=new CookRequest();
    private List<CookTypeList> list=new ArrayList<>();
    private List<String> listTypeName=new ArrayList<>();
    private ArrayAdapter<String> arr_adapter;

    private TakePhoto takePhoto;
    private CropOptions cropOptions;  //裁剪参数
    private CompressConfig compressConfig;  //压缩参数
    private Uri imageUri;  //图片保存路径

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
        initTakePhoto();
        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listTypeName);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        cookTypes.setAdapter(arr_adapter);
        initData();

        cookTypes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {//选择item的选择点击监听事件
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                String name=listTypeName.get(arg2);
                cookTypeName.setText(name);
                for (CookTypeList l:list){
                    if (l.getTypeName().equals(name)){
                        cookRequest.setTypeId(l.getId());
                        cookRequest.setTypeName(name);
                        break;
                    }
                }
            }

            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    private void initData() {
        RequestParams params=new RequestParams(HttpUrl.findAllTypeCookUrl);
        //获取cookie
        SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
        String cookie=sp2.getString("JSESSIONID","");
        params.addHeader("Cookie","JSESSIONID="+cookie);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                String code= JSON.parseObject(result).getString("code");
                String msg=JSON.parseObject(result).getString("msg");
                if (code.equals("10000")){
                    listTypeName.clear();
                    arr_adapter.clear();
                    list=JSON.parseArray(JSON.parseObject(result).getString("data"),CookTypeList.class);
                    if (list.size()>0){
                        for (CookTypeList l:list){
                            listTypeName.add(l.getTypeName());
                        }
                    }
                    arr_adapter.addAll(listTypeName);
                    arr_adapter.notifyDataSetChanged();
                }else {
                    ViewUtil.showToast(AddCook.this,msg);
                    if (msg.equals("你当前没有登录！没有该权限")){
                        StatusUtil.isLogin=false;
                    }
                }
            }
            @Override
            public void onFinished() {
                Log.d(TAG, "onFinished: 请求完成！");
            }
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.d(TAG, "onError: "+ex.getMessage());
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

    @Event(R.id.addCook_linearLayout_camera)
    private void doCamera(View view){
        takePhoto.onPickFromGallery();
    }

    //进行新增操作
    @Event(R.id.addCook_button_sure)
    private void doAddCook(final View view){
        if (cookName.getText().toString().equals("") ||
                cookTypeName.getText().toString().equals("") ||
                cookPrice.getText().toString().equals("") ||
                cookUnits.getText().toString().equals("")) {
            ViewUtil.showToast(AddCook.this,"请填写完整数据！");
        }else {
            //cookRequest.setTypeName(cookTypeName.getText().toString());
            cookRequest.setName(cookName.getText().toString());
            cookRequest.setPrice(Double.valueOf(cookPrice.getText().toString()));
            cookRequest.setStatus(1);
            RequestParams params=new RequestParams(HttpUrl.addCookUrl);
            params.setAsJsonContent(true);
            //获取cookie
            SharedPreferences sp2 = getSharedPreferences("cookie", MODE_PRIVATE);
            String cookie=sp2.getString("JSESSIONID","");
            params.addHeader("Cookie","JSESSIONID="+cookie);
            params.setBodyContent(JSON.toJSONString(cookRequest));

            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    String code= JSON.parseObject(result).getString("code");
                    String msg=JSON.parseObject(result).getString("msg");
                    if (code.equals("10000")){
                        ViewUtil.showToast(AddCook.this,msg);
                    }else {
                        ViewUtil.showToast(AddCook.this,msg);
                        if (msg.equals("你当前没有登录！没有该权限")){
                            StatusUtil.isLogin=false;
                        }
                    }
                }
                @Override
                public void onFinished() {
                    Log.d(TAG, "onFinished: 请求完成！");
                }
                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    Log.d(TAG, "onError: "+ex.getMessage());
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

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        super.takeSuccess(result);
        Log.d(TAG, "takeSuccess: "+result.getImages().toString());
        ImageOptions imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(300), DensityUtil.dip2px(150))//图片大小
                .setRadius(DensityUtil.dip2px(5))//ImageView圆角半径
                .setCrop(true)// 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.mipmap.ic_launcher)//加载中默认显示图片
                .setFailureDrawableId(R.mipmap.ic_launcher)//加载失败后默认显示图片
                .build();

        if (result.getImages().size()>0){
            for (TImage u:result.getImages()){
                //cookImage.setImageURI();
                x.image().bind(cookImage, u.getOriginalPath(),imageOptions);
                RequestParams params=new RequestParams(HttpUrl.uploadImage);
                params.setMultipart(true);
                params.addBodyParameter("file", new File(u.getCompressPath()));
                params.addParameter("inputId","file");
                x.http().post(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d(TAG, "onSuccess: "+result);
                        String code=JSON.parseObject(result).getString("code");
                        String msg=JSON.parseObject(result).getString("msg");
                        String url=JSON.parseObject(result).getString("data");
                        if (code.equals("10000")){
                            cookRequest.setImageUrl(url);
                            ViewUtil.showToast(AddCook.this,"图片上传成功！");
                        }else {
                            ViewUtil.showToast(AddCook.this,msg);
                        }
                    }
                    @Override
                    public void onFinished() {
                        Log.d(TAG, "onFinished: 请求完成！");
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d(TAG, "onError: "+ex.getMessage());
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }


    //初始化TakePhoto
    private void initTakePhoto() {
        ////获取TakePhoto实例
        takePhoto = getTakePhoto();
        //设置裁剪参数
        cropOptions = new CropOptions.Builder().setAspectX(1).setAspectY(1).setWithOwnCrop(false).create();
        //设置压缩参数
        compressConfig=new CompressConfig.Builder().setMaxSize(50*1024).setMaxPixel(800).create();
        takePhoto.onEnableCompress(compressConfig,true);  //设置为需要压缩
    }
}
