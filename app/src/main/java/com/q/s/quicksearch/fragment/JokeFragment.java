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
import com.q.s.quicksearch.JokeShowActivity;
import com.q.s.quicksearch.R;
import com.q.s.quicksearch.adapter.JokeAdapter;
import com.q.s.quicksearch.models.Joke;
import com.q.s.quicksearch.models.JokeDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.greenrobot.dao.query.QueryBuilder;

public class JokeFragment extends Fragment {

    public static JokeFragment newInstance() {
        return new JokeFragment();
    }

    public JokeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AVApplication app = (AVApplication) getActivity().getApplication();
        this.jokeDao = app.getJokeDao();
    }

    private ListView dataView;
    private JokeAdapter adapter;
    private List<Joke> data = new ArrayList<Joke>();
    private int offset = 0;
    private JokeDao jokeDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        long totalCount = jokeDao.count();
        if (totalCount != 0 && totalCount > 100) {
            offset = new Random().nextInt((int) (totalCount - 100));
        }

        View root = inflater.inflate(R.layout.fragment_novel, container, false);
        dataView = (ListView) root.findViewById(R.id.id_novel_list);
        adapter = new JokeAdapter(data, getActivity());
        dataView.setAdapter(adapter);
        dataView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), JokeShowActivity.class);
                intent.putExtra("joke", data.get(position));
                getActivity().startActivity(intent);
            }
        });
        dataView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                        && dataView.getLastVisiblePosition() == data.size() - 1) {
                    List<Joke> jokes = queryByPage(data.size() + offset, 20);
                    if (jokes != null && jokes.isEmpty()) {
                        Toast.makeText(getActivity(), "数据加载完了！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    data.addAll(jokes);
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

    private List<Joke> queryByPage(int offset, int limit) {
        QueryBuilder<Joke> builder = jokeDao.queryBuilder();
        builder.offset(offset);
        builder.limit(limit);
        return builder.list();
    }
}
