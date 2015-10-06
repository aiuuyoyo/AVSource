package com.q.s.quicksearch.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.q.s.quicksearch.AVApplication;
import com.q.s.quicksearch.ImageActivity;
import com.q.s.quicksearch.R;
import com.q.s.quicksearch.adapter.ImageAdapter;
import com.q.s.quicksearch.models.Image;
import com.q.s.quicksearch.models.ImageDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ImageFragment extends Fragment {
    private List<Image> data = new ArrayList<>();
    private ListView dataView;
    private ImageAdapter adapter;
    private ImageDao imageDao;
    private int offset;

    public static ImageFragment newInstance() {
        return new ImageFragment();
    }

    public ImageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVApplication avApplication = (AVApplication) getActivity().getApplication();
        imageDao = avApplication.getImageDao();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long totalCount = imageDao.count();
        if (totalCount != 0 && totalCount > 100) {
            offset = new Random().nextInt((int) (totalCount - 100));
        }

        View root = inflater.inflate(R.layout.fragment_image, container, false);
        dataView = (ListView) root.findViewById(R.id.id_images_list_view);
        adapter = new ImageAdapter(getActivity(), data);
        dataView.setAdapter(adapter);
        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                intent.putExtra("image", data.get(position));
                getActivity().startActivity(intent);
            }
        });
        dataView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && dataView.getLastVisiblePosition() == data.size() - 1) {
                    List<Image> imageList = queryByPage(data.size() + offset, 20);
                    if (imageList != null && imageList.isEmpty()) {
                        Toast.makeText(getActivity(), "数据加载完了！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.addAll(imageList);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        data.addAll(queryByPage(data.size() + offset, 20));
        adapter.notifyDataSetChanged();
        return root;
    }

    private List<Image> queryByPage(int offset, int limit) {
        QueryBuilder<Image> builder = imageDao.queryBuilder();
        builder.offset(offset);
        builder.limit(limit);
        Query<Image> query = builder.build();
        return query.list();
    }

}
