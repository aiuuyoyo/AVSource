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
import com.q.s.quicksearch.NovelShowActivity;
import com.q.s.quicksearch.R;
import com.q.s.quicksearch.adapter.NovelAdapter;
import com.q.s.quicksearch.models.Novel;
import com.q.s.quicksearch.models.NovelDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class NovelFragment extends Fragment {

    public static NovelFragment newInstance() {
        return new NovelFragment();
    }

    public NovelFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVApplication application = (AVApplication) getActivity().getApplication();
        this.novelDao = application.getNovelDao();
    }

    private ListView dataView;
    private NovelAdapter adapter;
    private List<Novel> data = new ArrayList<>();
    private int offset = 0;
    private NovelDao novelDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long totalCount = novelDao.count();
        if (totalCount != 0 && totalCount > 100) {
            offset = new Random().nextInt((int) (totalCount - 100));
        }
        View root = inflater.inflate(R.layout.fragment_novel, container, false);
        dataView = (ListView) root.findViewById(R.id.id_novel_list);
        adapter = new NovelAdapter(data, getActivity());
        dataView.setAdapter(adapter);
        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent novelShowIntent = new Intent(getActivity(), NovelShowActivity.class);
                novelShowIntent.putExtra("novel", data.get(position));
                getActivity().startActivity(novelShowIntent);
            }
        });
        dataView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && dataView.getLastVisiblePosition() == data.size() - 1) {
                    List<Novel> novels = loadByPage(data.size() + offset, 20);
                    if (novels != null && novels.isEmpty()) {
                        Toast.makeText(getActivity(), "数据加载完了！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.addAll(novels);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        data.addAll(loadByPage(data.size() + offset, 20));
        adapter.notifyDataSetChanged();
        return root;
    }

    private List<Novel> loadByPage(int offset, int limit) {
        QueryBuilder<Novel> builder = novelDao.queryBuilder();
        builder.limit(limit);
        builder.offset(offset);
        Query query = builder.build();
        return query.list();
    }

}
