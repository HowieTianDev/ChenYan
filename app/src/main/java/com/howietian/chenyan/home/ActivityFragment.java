package com.howietian.chenyan.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.ActivityAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entrance.LoginActivity;
import com.melnykov.fab.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityFragment extends BaseFragment {
    @Bind(R.id.rv_activity)
    RecyclerView recyclerView;
    @Bind(R.id.fab)
    FloatingActionButton fab;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    private List<MActivity> mActivities = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private LinearLayoutManager manager;
    private int lastVisibleItem;
    private int limit = Constant.LIMIT;
    public static final String FROME_ACTIVITY = Constant.FROM_ACTIVIRY;

    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;

    private String lastTime;

    public ActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity, container, false);

    }


    @Override
    public void init() {
        super.init();
        swipeRefreshLayout.setRefreshing(true);
        manager = new LinearLayoutManager(getContext());
        /**
         * 初始化加载数据
         */

        getData(PULL_REFRESH);

        activityAdapter = new ActivityAdapter(getContext(), mActivities);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(activityAdapter);

        // 第三方的fab 和recyclerview 关联起来
        fab.attachToRecyclerView(recyclerView);
        setItemClick();

        refresh();
        loadMore();
    }


    //   从详情页返回时，自动刷新
    @Override
    public void onResume() {
        super.onResume();
//        返回是自动刷新存在问题;若用户看到后面的信息，自动刷新会只请求到最新的信息
//        getData(PULL_REFRESH);
        Log.e("TAG", "onReume");
    }

    private void setItemClick() {
        activityAdapter.setOnItemClickListener(new ActivityAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {

                final Intent intent = new Intent(getContext(), ActivityDetailActivity.class);
                /**
                 * 把Activity对象通过Gson转化为String传递到详情页
                 */

                MActivity mActivity = mActivities.get(position);
                Gson gson = new Gson();
                String msg = gson.toJson(mActivity, MActivity.class);
                intent.putExtra(FROME_ACTIVITY, msg);
                jumpTo(intent, false);
            }
        });


    }

    /**
     * 刷新操作
     */
    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(PULL_REFRESH);
            }
        });
    }

    /**
     * 上拉加载操作
     */

    private void loadMore() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
//               表示滑动最底部 SCROLL——STATE——IDLE 滑动静止
                if (newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItem + 1 == activityAdapter.getItemCount()) {
                    getData(LOAD_MORE);

                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = manager.findLastVisibleItemPosition();
            }
        });
    }


    @OnClick(R.id.fab)
    public void publishActivity() {
        if (MyApp.isLogin()) {
            jumpTo(PublishAActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }

    /**
     * 分页获取数据，分别为下拉刷新，上拉加载
     */

    private void getData(final int type) {

        BmobQuery<MActivity> query = new BmobQuery<>();
        query.include("currentUser");
        query.order("-createdAt");
//        设置每页查询的item数目
        query.setLimit(limit);

//        加载更多
        if (type == LOAD_MORE) {
//         处理时间查询
            Date date = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = dateFormat.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//         只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
        }

        query.findObjects(new FindListener<MActivity>() {
            @Override
            public void done(List<MActivity> list, BmobException e) {
                if (e == null) {
//                    查出有数据
                    if (list.size() > 0) {
                        if (type == PULL_REFRESH) {
                            mActivities.clear();
                        }
                        mActivities.addAll(list);
                        activityAdapter.notifyDataSetChanged();
                        lastTime = mActivities.get(mActivities.size() - 1).getCreatedAt();
                        Log.e("HHH", lastTime);

//                        查询到无数据
                    } else {
                        if (type == LOAD_MORE) {
                            showToast("没有更多数据了");
                        } else if (type == PULL_REFRESH) {
                            showToast("服务器没有数据");
                        }

                    }
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showToast("请求服务器异常" + e.getMessage() + "错误代码" + e.getErrorCode());
                    Log.e("HHH", e.getMessage() + "错误代码" + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });
    }
}
