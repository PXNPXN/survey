package com.example.survey;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class MyAdapter extends BaseAdapter {

    List<Map<String, Object>> list;
    LayoutInflater inflater;

    public MyAdapter(Context context){
        this.inflater = LayoutInflater.from(context);
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public  void removeData(int position){
        list.remove(position);
    }
    // 处理掉item点击高亮  同时也导致item的click和longclick无法响应
    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        View view=null;
        if(convertView == null) {
            view = inflater.inflate(R.layout.item, null);
            holder = new ViewHolder();
            holder.question = view.findViewById(R.id.tv_item);
            view.setTag(holder);
        } else {
            view=convertView;
            holder = (ViewHolder) convertView.getTag();
        }

        Map map = list.get(position);
        holder.question.setText((String)map.get("question"));
        return view;
    }

    public static class ViewHolder {
        TextView question;

    }
}
