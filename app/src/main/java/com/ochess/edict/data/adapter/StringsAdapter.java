package com.ochess.edict.data.adapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ochess.edict.R;
import com.ochess.edict.util.ActivityRun;

public class StringsAdapter extends BaseAdapter {
    private boolean hasIndex = false;
    String[] datas;
    public StringsAdapter(String[] strs){
        datas = strs;
    }
    public StringsAdapter(String[] strs,boolean hasIndex){
            datas = strs;
            this.hasIndex=hasIndex;
    }
    @Override
    public int getCount() {
        return datas.length;
    }

    @Override
    public Object getItem(int i) {
        return datas[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView tv = new TextView(ActivityRun.context);
        tv.setText((i+1)+". "+datas[i]);
        //tv.setGravity(Gravity.CENTER);
        tv.setTextColor(tv.getResources().getColor(R.color.text));
        return tv;
    }
}
