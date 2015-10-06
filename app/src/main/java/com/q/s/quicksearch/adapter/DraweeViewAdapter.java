package com.q.s.quicksearch.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.facebook.drawee.view.SimpleDraweeView;
import com.q.s.quicksearch.R;

import java.util.List;

/**
 * Created by Administrator on 2015/7/21.
 */
public class DraweeViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    public DraweeViewAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SimpleDraweeView draweeView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_image_item, parent, false);
            draweeView = (SimpleDraweeView) convertView.findViewById(R.id.my_image_view);
            draweeView.setAspectRatio(1.0f);
            convertView.setTag(draweeView);
        } else {
            draweeView = (SimpleDraweeView) convertView.getTag();
        }
        Uri uri = Uri.parse(data.get(position));
        draweeView.setImageURI(uri);
        return convertView;
    }
}
