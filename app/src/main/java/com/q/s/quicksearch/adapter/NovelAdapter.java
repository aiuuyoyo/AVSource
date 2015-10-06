package com.q.s.quicksearch.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.q.s.quicksearch.R;
import com.q.s.quicksearch.models.Novel;

import java.util.List;

/**
 * Created by Administrator on 2015/7/17.
 */
public class NovelAdapter extends BaseAdapter {
    private List<Novel> data;
    private Context context;
    private LayoutInflater inflater;

    public NovelAdapter(List<Novel> data, Context context) {
        this.data = data;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
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
        TextView novelText;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.layout_item_3, parent, false);
            novelText = (TextView) convertView.findViewById(R.id.id_novel_text);
            convertView.setTag(novelText);
        } else {
            novelText = (TextView) convertView.getTag();
        }
        Novel novel = data.get(position);
        novelText.setText(novel.getTitle() + "-" + novel.getId());
        return convertView;
    }
}
