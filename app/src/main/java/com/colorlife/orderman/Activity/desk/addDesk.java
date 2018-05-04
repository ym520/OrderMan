package com.colorlife.orderman.Activity.desk;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;

import com.colorlife.orderman.Activity.base.BaseActivity;
import com.colorlife.orderman.Activity.setting.settingIndex;
import com.colorlife.orderman.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by ym on 2018/5/4.
 */
@ContentView(R.layout.add_desk)
public class addDesk extends BaseActivity {
    //可住人数
    @ViewInject(R.id.addDesk_editText_volume)
    private EditText volume;
    @ViewInject(R.id.addDesk_editText_deskName)
    private EditText deskName;
    @ViewInject(R.id.addDesk_editText_roomName)
    private EditText roomName;

    @Event(R.id.addDesk_textView_back)
    private void doBack(View view){
        Intent intent=new Intent(addDesk.this, settingIndex.class);
        startActivity(intent);
        finish();
    }

    @Event(R.id.addDesk_button_add)
    private void doAddDesk(View view){

    }
}
