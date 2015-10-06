package com.q.s.quicksearch.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.q.s.quicksearch.R;

import net.youmi.android.offers.OffersManager;

public class BlankFragment extends Fragment {
    public static BlankFragment newInstance() {
        return new BlankFragment();
    }

    public BlankFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_blank, container, false);
        Button btn = (Button) root.findViewById(R.id.id_btn_earn_points);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OffersManager.getInstance(getActivity()).showOffersWall();
            }
        });
        return root;
    }

}
