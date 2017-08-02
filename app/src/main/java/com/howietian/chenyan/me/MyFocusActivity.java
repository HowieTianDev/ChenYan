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
import com.howietian.chenyan.adapters.FUserAdapter;
import com.howietian.chenyan.adapters.UserAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.personpage.PersonPageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyFocusActivity extends BaseActivity {

    @Bind(R.id.tb_my_focus)
    Toolbar toolbar;
    @Bind(R.id.rv_my_focus)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_my_focus)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<User> users = new ArrayList<>();
    private LinearLayoutManager manager;
    private FUserAdapter adapter;
    private User user = BmobUser.getCurrentUser(User.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_focus);
    }

    @Override
    public void init() {
        super.init();

        initViews();
        initListeners();
    }

    private void initViews() {

        swipeRefreshLayout.setRefreshing(true);
        queryFocusUser();
        setSupportActionBar(toolbar);
        adapter = new FUserAdapter(this, users);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

    }

    private void initListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        refresh();
        setOnItemClickListener();
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryFocusUser();
            }
        });
    }

    private void queryFocusUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("focus", new BmobPointer(user));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (users != null) {
                            users.clear();
                        }
                        users.addAll(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast("服务器没有数据");
                    }
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showToast("访问服务器失败，稍后重试" + e.getMessage() + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void setOnItemClickListener() {
        adapter.setOnItemClickListener(new FUserAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                User user = users.get(position);
                String userInfo = new Gson().toJson(user, User.class);
                Intent intent = new Intent(MyFocusActivity.this, PersonPageActivity.class);
                intent.putExtra(Constant.TO_PERSON_PAGE, userInfo);
                jumpTo(intent, false);
            }
        });
    }

}
