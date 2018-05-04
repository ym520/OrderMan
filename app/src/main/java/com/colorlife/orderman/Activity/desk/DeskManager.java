package com.colorlife.orderman.Activity.desk;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.cook.AddCook;
import com.colorlife.orderman.Activity.setting.SettingIndex;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
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
    @Event(R.id.deskManager_imageButton_add)
    private void intoAddDesk(View view){
        Intent intent=new Intent(DeskManager.this,AddDesk.class);
        startActivity(intent);
    }
    @Event(R.id.deskManager_imageButton_back)
    private void doBack(View view){
        Intent intent=new Intent(DeskManager.this, SettingIndex.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
