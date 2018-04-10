package com.howietian.chenyan.me.collect;


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
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.home.ActivityDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectActivityFragment extends BaseFragment {


    @Bind(R.id.rv_c_activity)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_c_activity)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<MActivity> mActivities = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private LinearLayoutManager manager;
    private User user = BmobUser.getCurrentUser(User.class);

    public static final String FROME_ACTIVITY = Constant.FROM_ACTIVIRY;
    public static final String COLLECT_ACTIVITY = "collect_activity";

    public static CollectActivityFragment newInstance(String args){
        CollectActivityFragment fragment = new CollectActivityFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COLLECT_ACTIVITY,args);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CollectActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect_activity, container, false);
    }

    @Override
    public void init() {
        super.init();
        swipeRefreshLayout.setRefreshing(true);
        manager = new LinearLayoutManager(getContext());


    }

    //   从详情页返回时，自动刷新
    @Override
    public void onResume() {
        super.onResume();
        getData();
        activityAdapter = new ActivityAdapter(getContext(), mActivities);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(activityAdapter);

        setItemClick();
        refresh();
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
                getData();
            }
        });
    }


    /**
     * 只执行刷新操作,查询当前用户喜欢的活动
     *  采用内部查询的方法，来实现反查询
     */
    private void getData() {

        BmobQuery<MActivity> query = new BmobQuery<>();
        query.include("currentUser,collect");
        query.order("-createdAt");

        BmobQuery<BmobUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",user.getObjectId());

        query.addWhereMatchesQuery("collect","_User",innerQuery);

        query.findObjects(new FindListener<MActivity>() {
            @Override
            public void done(List<MActivity> list, BmobException e) {
                if (e == null) {
//                    查出有数据
                    if (list.size() > 0) {
                        mActivities.clear();
                        mActivities.addAll(list);
                        activityAdapter.notifyDataSetChanged();

//                        查询到无数据
                    } else {
                        showToast(getString(R.string.no_data));
                    }
                } else {
                    showToast("请求服务器异常" + e.getMessage() + "错误代码" + e.getErrorCode());
                    Log.e("HHH", e.getMessage() + "错误代码" + e.getErrorCode());
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
