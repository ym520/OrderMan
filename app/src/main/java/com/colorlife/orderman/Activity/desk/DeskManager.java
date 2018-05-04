package com.colorlife.orderman.Activity.desk;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

/**
 * Created by ym on 2018/5/3 0003.
 */
@ContentView(R.layout.desk_manager)
public class DeskManager extends AppCompatActivity {
    private String TAG=this.toString();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        x.view().inject(this);
        ActivityCollector.addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
