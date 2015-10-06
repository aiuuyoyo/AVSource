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
import com.q.s.quicksearch.MainActivity;
import com.q.s.quicksearch.R;
import com.q.s.quicksearch.VideoDetailsActivity;
import com.q.s.quicksearch.adapter.ThunderAVSourceAdapter;
import com.q.s.quicksearch.models.ThunderAVSource;
import com.q.s.quicksearch.models.ThunderAVSourceDao;
import com.q.s.quicksearch.utils.HandlerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.query.Query;
import de.greenrobot.dao.query.QueryBuilder;

public class ThunderAVSFragment extends Fragment implements MainActivity.SearchLitener{
    private final List<ThunderAVSource> data = new ArrayList<>();
    private ListView dataView;
    private ThunderAVSourceAdapter adapter;
    ThunderAVSourceDao thunderAVSourceDao;

    public static ThunderAVSFragment newInstance() {
        return new ThunderAVSFragment();
    }

    public ThunderAVSFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVApplication app = (AVApplication) getActivity().getApplication();
        this.thunderAVSourceDao = app.getThunderAVSourceDao();
    }

    private int offset = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        long totalCount = thunderAVSourceDao.count();
        if (totalCount != 0 && totalCount > 100) {
            offset = new Random().nextInt((int) (totalCount - 100));
        }

        View rootView = inflater.inflate(R.layout.fragment_thunder_avs, container, false);
        dataView = (ListView) rootView.findViewById(R.id.listView);
        adapter = new ThunderAVSourceAdapter(data, getActivity());
        dataView.setAdapter(adapter);
        dataView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private boolean isLoadFinish = false;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && dataView.getLastVisiblePosition() == data.size() - 1) {
                    if (isSearching) {
                        if (!isLoadFinish) {
                            isLoadFinish = true;
                            Toast.makeText(getActivity(), "数据加载完了！", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    List<ThunderAVSource> src = queryByPage(data.size() + offset, 20);
                    if (src == null || src.isEmpty()) {
                        if (!isLoadFinish) {
                            isLoadFinish = true;
                            Toast.makeText(getActivity(), "数据加载完了！", Toast.LENGTH_SHORT).show();
                        }
                        return;
                    }
                    data.addAll(src);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            }
        });
        dataView.setOnItemClickListener(new AbsListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent showDetailsIntent = new Intent(getActivity(), VideoDetailsActivity.class);
                showDetailsIntent.putExtra("source", data.get(position));
                startActivity(showDetailsIntent);
            }
        });
        data.addAll(queryByPage(data.size() + offset, 20));
        adapter.notifyDataSetChanged();
        return rootView;
    }

    private List<ThunderAVSource> queryByPage(int offset, int limit) {
        QueryBuilder builder = thunderAVSourceDao.queryBuilder();
        builder.offset(offset);
        builder.limit(limit);
        Query query = builder.build();
        query.forCurrentThread();
        return query.list();
    }

    private boolean isSearching = false;

    @Override
    public void onSearch(final String query) {
        isSearching = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ThunderAVSource> sourceList = thunderAVSourceDao.loadAll();
                final List<ThunderAVSource> list = new ArrayList<ThunderAVSource>(sourceList.size() / 3);
                String querys[] = query.trim().split(" ");
                for (ThunderAVSource source : sourceList) {
                    int count = 0;
                    for (String s : querys) {
                        if (source.getContent().contains(s)) {
                            count++;
                        }
                    }
                    if (count == querys.length) {
                        list.add(source);
                    }
                }
                HandlerUtils.HANDLER.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!list.isEmpty()) {
                            data.clear();
                            data.addAll(list);
                            adapter.notifyDataSetChanged();
                        } else {
                            isSearching = false;
                            Toast.makeText(getActivity(), "没有搜索到任何内容", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).start();
    }
}
