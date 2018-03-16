package com.howietian.chenyan.rank;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.RankAdapter;
import com.howietian.chenyan.entities.Rank;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class RankFragment extends BaseFragment {


    @Bind(R.id.tb_rank)
    Toolbar toolbar;
    @Bind(R.id.rv_rank)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_rank)
    SmartRefreshLayout swipeRefreshLayout;

    private RankAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private List<Rank> rankList = new ArrayList<>();

    public static final String FROM_RANK_FRAGMENT = "from_rank_fragment";

    public RankFragment() {
        // Required empty public constructor
    }

    private static final String RANK_FRAGMENT = "rank_fragment";


    public static RankFragment newInstance(String args) {
        RankFragment fragment = new RankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(RANK_FRAGMENT, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_rank, container, false);

    }

    @Override
    public void init() {
        super.init();
        initDates();
        initViews();
        initListeners();
    }

    private void initViews() {
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        adapter = new RankAdapter(getContext(), rankList);
        manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initDates() {
        swipeRefreshLayout.autoRefresh();
        queryRankList();

    }

    private void initListeners() {

        adapter.setOnItemClickListener(new RankAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                Rank rank = rankList.get(position);
                Gson gson = new Gson();
                String rankMsg = gson.toJson(rank, Rank.class);
                Intent intent = new Intent(getContext(), RankDetailActivity.class);
                intent.putExtra(FROM_RANK_FRAGMENT, rankMsg);
                Log.e("排行", rankMsg);
                jumpTo(intent, false);
            }
        });

        refresh();
    }

    private void queryRankList() {
        BmobQuery<Rank> rankQuery = new BmobQuery<>();
        rankQuery.include("one,two,three,four,five,six,seven,eight,nine,ten");
        rankQuery.order("order");
        rankQuery.findObjects(new FindListener<Rank>() {
            @Override
            public void done(List<Rank> list, BmobException e) {
                if (e == null) {
                    if (rankList != null) {
                        rankList.clear();
                    }
                    rankList.addAll(list);
                    adapter.notifyDataSetChanged();
                    showToast("查询成功！");
                    Log.e("排行", rankList.toString());
                    swipeRefreshLayout.finishRefresh();
                } else {
                    showToast("查询失败！" + e.getErrorCode() + e.getMessage());
                    swipeRefreshLayout.finishRefresh();
                }
            }
        });
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                queryRankList();
            }
        });
    }
}
