package com.q.s.quicksearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.q.s.quicksearch.R;
import com.q.s.quicksearch.models.Image;

import java.util.List;

/**
 * Created by Administrator on 2015/7/22.
 */
public class ImageAdapter extends BaseAdapter {
    private Context context;
    private List<Image> data;

    public ImageAdapter(Context context, List<Image> data) {
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
        return data.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.layout_item_3, parent, false);
            textView = (TextView) convertView.findViewById(R.id.id_novel_text);
            convertView.setTag(textView);
        } else {
            textView = (TextView) convertView.getTag();
        }
        Image image = data.get(position);
        textView.setText(image.getTitle() + "-" + image.getId());
        return convertView;
    }
}
