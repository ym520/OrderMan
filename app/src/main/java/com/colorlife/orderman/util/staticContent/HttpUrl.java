package com.colorlife.orderman.util.staticContent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import java.io.IOException;

/**
 * Created by ym on 2017/10/10.
 */

public class HttpUrl {
    //教室192.168.43.170
    //教室2：192.168.1.43
    //教室2：192.168.1.6
    //宿舍：192.168.1.106
    //服务器：59.110.141.1

    public static String realUrl="http://111.230.246.68:8080/orderMan/";
    public static String loginUrl=realUrl+"user/login";
    public static String registerUrl=realUrl+"user/register";
    public static String User_exit_Url=realUrl+"user/logout";
    public static String orderManagetUrl=realUrl+"order/findAll";
    public static String deskAllUrl=realUrl+"desk/findAllDesk";
    public static String findAllCookByTypeIdUrl=realUrl+"cook/findAllCookByType";
    public static String findAllTypeCookUrl=realUrl+"cookType/findAll";
    public static String findOrderDetailUrl=realUrl+"order/findDetail";
    public static String addCookUrl=realUrl+"cook/add";
    public static String uploadImage=realUrl+"file/sigleImgUpload";
    public static String downloadImage=realUrl+"file/imgDownload";

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static boolean isNetworkOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("ping -c 3 www.baidu.com");
            int exitValue = ipProcess.waitFor();
            Log.i("Avalible", "Process:" + exitValue);
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    //是否联网
    public static boolean isNet(Context context){
        if (Build.VERSION.SDK_INT < 21) {
            if (HttpUrl.isNetworkOnline() && HttpUrl.isNetworkConnected(context)) {
                return true;
            } else {
                return false;
            }
        }else if (Build.VERSION.SDK_INT == 23) {
            ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkCapabilities networkCapabilities = connMgr.getNetworkCapabilities(connMgr.getActiveNetwork());
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
        }
        return false;
    }
}

