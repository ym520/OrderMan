package com.colorlife.orderman.Activity.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.colorlife.orderman.Activity.main.IndexActivity;
import com.colorlife.orderman.R;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by ym on 2018/4/18.
 */

@ContentView(R.layout.launch)
public class Launch extends AppCompatActivity{
    String TAG=this.toString();
    private String user=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        Handler handler = new Handler();
        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
        user=sp.getString("loginName",null);
        Log.d(TAG, "onCreate: user:"+user);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user==null){
                    Intent intent= new Intent(Launch.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(Launch.this, IndexActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 2000);

    }
}
