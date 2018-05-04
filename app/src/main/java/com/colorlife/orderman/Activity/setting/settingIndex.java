package com.colorlife.orderman.Activity.setting;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.cook.CookManager;
import com.colorlife.orderman.Activity.desk.DeskManager;
import com.colorlife.orderman.Activity.main.IndexActivity;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by ym on 2018/5/3 0003.
 */

@ContentView(R.layout.setting_index)
public class settingIndex extends BaseActivity {

    @ViewInject(R.id.setting_LinearLayout_cashSetting)
    private LinearLayout cash;
    @ViewInject(R.id.setting_LinearLayout_cookManager)
    private LinearLayout cookManager;
    @ViewInject(R.id.setting_LinearLayout_deskManager)
    private LinearLayout deskManager;
    @ViewInject(R.id.setting_LinearLayout_printSetting)
    private LinearLayout printSetting;
    @ViewInject(R.id.setting_LinearLayout_userSetting)
    private LinearLayout userSetting;
    @ViewInject(R.id.setting_LinearLayout_version)
    private LinearLayout version;

    @Event(R.id.setting_LinearLayout_deskManager)
    private void intoDeskManager(View view){
        Intent intent=new Intent(settingIndex.this,DeskManager.class);
        startActivity(intent);
    }

    @Event(R.id.setting_LinearLayout_cookManager)
    private void intoCookManager(View view){
        Intent intent=new Intent(settingIndex.this, CookManager.class);
        startActivity(intent);
    }

    @Event(R.id.setting_LinearLayout_userSetting)
    private void intoUserSetting(View view){
        Intent intent=new Intent(settingIndex.this,UserSetting.class);
        startActivity(intent);
    }

    @Event(value ={R.id.setting_imageButton_back,R.id.setting_textView_back})
    private void doBack(View view){
        Intent intent=new Intent(settingIndex.this, IndexActivity.class);
        startActivity(intent);
        finish();
    }

}
