package com.q.s.quicksearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.q.s.quicksearch.R;
import com.q.s.quicksearch.models.ThunderAVSource;

import java.util.List;

/**
 * Created by Administrator on 2015/7/15.
 */
public class ThunderAVSourceAdapter extends BaseAdapter {
    private List<ThunderAVSource> data;
    private LayoutInflater inflater;
    private Context context;

    public ThunderAVSourceAdapter(List<ThunderAVSource> data, Context context) {
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.layout_item_1, parent, false);
            holder.titleView = (TextView) convertView.findViewById(R.id.id_title);
            holder.extrasView = (TextView) convertView.findViewById(R.id.id_extras);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ThunderAVSource source = data.get(position);
        holder.titleView.setText(source.getTitle() + "-" + source.getId());
        StringBuilder stringBuilder = new StringBuilder("是否有码 ： ");
        if (source.getHasMosaic() == null) {
            stringBuilder.append("未知");
        } else {
            if (source.getHasMosaic()) {
                stringBuilder.append("有");
            } else {
                stringBuilder.append("无");
            }
        }
        holder.extrasView.setText(stringBuilder);
        return convertView;
    }

    class ViewHolder {
        public TextView titleView;
        public TextView extrasView;
    }
}
