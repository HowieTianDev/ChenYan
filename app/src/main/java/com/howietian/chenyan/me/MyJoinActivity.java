package com.howietian.chenyan.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.ActivityAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.home.ActivityDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyJoinActivity extends BaseActivity {

    @Bind(R.id.tb_join_mActivity)
    Toolbar toolbar;
    @Bind(R.id.rv_join_activity)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_join_activity)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<MActivity> activities = new ArrayList<>();
    private ActivityAdapter activityAdapter;
    private LinearLayoutManager manager;

    private User user = BmobUser.getCurrentUser(User.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_join);
    }

    @Override
    public void init() {
        super.init();
        back();
        swipeRefreshLayout.setRefreshing(true);

        queryMyJoinedActivity();
        manager = new LinearLayoutManager(this);
        activityAdapter = new ActivityAdapter(this,activities);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(activityAdapter);
        recyclerView.setHasFixedSize(true);

        refresh();
        setOnItemClickListener();


    }

    private void back(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 查询我参与的活动
     */
    private void queryMyJoinedActivity(){
        BmobQuery<MActivity> query = new BmobQuery<>();
        query.include("currentUser,join");
        query.order("-createdAt");

        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",user.getObjectId());

        query.addWhereMatchesQuery("join","_User",innerQuery);
        query.findObjects(new FindListener<MActivity>() {
            @Override
            public void done(List<MActivity> list, BmobException e) {
                if(e==null){
                    if(list.size()>0){
                        activities.clear();
                        activities.addAll(list);
                        activityAdapter.notifyDataSetChanged();
                    }else{
                        showToast(getString(R.string.no_data));
                    }
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    showToast("请求服务器失败，请稍后重试"+e.getMessage()+e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 刷新
     */

    private void refresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryMyJoinedActivity();
            }
        });

    }

    private void setOnItemClickListener(){
        activityAdapter.setOnItemClickListener(new ActivityAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                MActivity activity = activities.get(position);
                String msg = new Gson().toJson(activity, MActivity.class);
                Intent intent = new Intent(MyJoinActivity.this, ActivityDetailActivity.class);
                intent.putExtra(Constant.FROM_ACTIVIRY, msg);
                jumpTo(intent, false);
            }
        });
    }
}
