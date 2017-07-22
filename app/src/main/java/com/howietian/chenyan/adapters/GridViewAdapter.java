package com.howietian.chenyan.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.howietian.chenyan.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 83624 on 2017/7/18.
 */

public class GridViewAdapter extends BaseAdapter {

    private List<Integer> imageid = new ArrayList<>();
    private List<String> text = new ArrayList<>();
    private Context context;


    public GridViewAdapter(List<Integer> imageId,List<String> text,Context context){
        this.context = context;
        this.imageid = imageId;
        this.text = text;
    }


    @Override
    public int getCount() {
        return text.size();
    }

    @Override
    public Object getItem(int i) {
        return text.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup viewGroup) {
        ViewHolder vh;
        if(convertview == null){
            convertview = LayoutInflater.from(context).inflate(R.layout.item_grid,viewGroup,false);
            vh = new ViewHolder();
            vh.image = (ImageView) convertview.findViewById(R.id.iv_image);
            vh.text = (TextView) convertview.findViewById(R.id.iv_text);
            convertview.setTag(vh);

        }else{
            vh = (ViewHolder) convertview.getTag();
        }
        vh.image.setImageResource(imageid.get(i));
        vh.text.setText(text.get(i));

        return convertview;
    }


    static class ViewHolder{
        TextView text;
        ImageView image;
    }
}
