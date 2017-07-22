package com.howietian.chenyan.rank;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.me.MeFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends BaseFragment {


    public RankFragment() {
        // Required empty public constructor
    }

    private static final String RANK_FRAGMENT = "rank_fragment";



    public static RankFragment newInstance(String args){
        RankFragment fragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RANK_FRAGMENT,args);
        fragment.setArguments(bundle);
        return fragment;
    }




    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank, container, false);

    }

}
