package com.colorlife.orderman.util;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by ym on 2018/4/19.
 */

public class ViewUtil {

    private static Toast toast;

    public static void showToast(Context context,String content) {
        if (toast == null) {
            toast = Toast.makeText(context,content,Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
