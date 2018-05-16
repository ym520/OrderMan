package com.colorlife.orderman.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.colorlife.orderman.R;
import com.colorlife.orderman.domain.DeskList;
import com.colorlife.orderman.domain.OrderDetailRequest;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ym on 2018/5/15.
 */

public class DeskManagerAdapter extends ArrayAdapter<DeskList> {

    private int resourceID;
    private List<DeskList> lists=new ArrayList<>();

    public DeskManagerAdapter(Context context, int resource, List<DeskList> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
        lists=objects;
    }

    public void addMore(List<DeskList> object){
        if (object.size()>0){
            for (DeskList  list: object){
                lists.add(list);
            }
        }
    }

    public void update(List<DeskList> object){
        this.lists=object;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DeskList deskList=getItem(position);
        DeskManagerAdapter.ViewHolder viewHolder;
        if (convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
            viewHolder=new DeskManagerAdapter.ViewHolder();
            viewHolder.deskName= (TextView) convertView.findViewById(R.id.deskManager_list_textView_deskName);
            viewHolder.deskPerson= (TextView) convertView.findViewById(R.id.deskManager_list_textView_deskPerson);
            viewHolder.status= (Switch) convertView.findViewById(R.id.deskManager_list_Switch_status);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (DeskManagerAdapter.ViewHolder) convertView.getTag();
        }
        viewHolder.deskName.setText(deskList.getCode());
        if (deskList.getPersons()!=null){
            viewHolder.deskPerson.setText(deskList.getPersons().toString());
        }
        if (deskList.getStatus()==1){
            viewHolder.status.setChecked(true);
        }else if (deskList.getStatus()==4){
            viewHolder.status.setChecked(false);
        }
        return convertView;
    };

    class ViewHolder{
        TextView deskName;
        TextView deskPerson;
        Switch status;
    };
}
