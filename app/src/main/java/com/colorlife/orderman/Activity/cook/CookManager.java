package com.colorlife.orderman.Activity.cook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.colorlife.orderman.Activity.base.ActivityCollector;
import com.colorlife.orderman.Activity.setting.SettingIndex;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

/**
 * Created by ym on 2018/5/3 0003.
 */
@ContentView(R.layout.cook_manager)
public class CookManager extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        ActivityCollector.addActivity(this);
    }
    @Event(R.id.cookManager_imageButton_add)
    private void intoAddCook(View view){
        Intent intent=new Intent(CookManager.this, AddCook.class);
        startActivity(intent);
    }
    @Event(R.id.cookManager_imageButton_back)
    private void doBack(View view){
        Intent intent=new Intent(CookManager.this, SettingIndex.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
