package com.howietian.chenyan.personpage;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.ActivityAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.home.ActivityDetailActivity;
import com.howietian.chenyan.me.MyJoinActivity;

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
public class PersonActivityFragment extends BaseFragment {
    public static final String TAG = "person_activity_fragment";
    @Bind(R.id.rv_activity)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    //    访问的用户
    private User currentUser;
    private List<MActivity> activities = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private LinearLayoutManager manager;

    public PersonActivityFragment() {
        // Required empty public constructor
    }

    public static PersonActivityFragment newInstance(String args) {
        PersonActivityFragment fragment = new PersonActivityFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_activity, container, false);
    }

    @Override
    public void init() {
        super.init();
        swipeRefreshLayout.setRefreshing(true);

        queryPersonActivity();
        manager = new LinearLayoutManager(getContext());
        activityAdapter = new ActivityAdapter(getContext(), activities);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setHasFixedSize(true);

        refresh();
        setOnItemClickListener();

    }

    /**
     * 查询我参与的活动
     */
    private void queryPersonActivity() {
        BmobQuery<MActivity> query = new BmobQuery<>();
        query.addWhereEqualTo("currentUser", currentUser);
        query.include("currentUser");
        query.order("-createdAt");

        query.findObjects(new FindListener<MActivity>() {
            @Override
            public void done(List<MActivity> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        activities.clear();
                        activities.addAll(list);
                        activityAdapter.notifyDataSetChanged();
                    } else {
                        showToast(getString(R.string.no_data_activity));
                    }
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showToast("请求服务器失败，请稍后重试" + e.getMessage() + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 刷新
     */
    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPersonActivity();
            }
        });

    }

    private void setOnItemClickListener() {
        activityAdapter.setOnItemClickListener(new ActivityAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                MActivity activity = activities.get(position);
                String msg = new Gson().toJson(activity, MActivity.class);
                Intent intent = new Intent(getContext(), ActivityDetailActivity.class);
                intent.putExtra(Constant.FROM_ACTIVIRY, msg);
                jumpTo(intent, false);
            }
        });
    }

}
