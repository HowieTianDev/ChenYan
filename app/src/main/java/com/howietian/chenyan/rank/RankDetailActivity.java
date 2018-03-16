package com.howietian.chenyan.rank;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.RankUserAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Rank;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.personpage.PersonPageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class RankDetailActivity extends BaseActivity {

    @Bind(R.id.tb_rank)
    Toolbar toolbar;
    @Bind(R.id.rv_rank)
    RecyclerView recyclerView;

    private List<User> userList = new ArrayList<>();
    private RankUserAdapter adapter;
    private RecyclerView.LayoutManager manager;


    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_rank_detail);
    }

    @Override
    public void init() {
        super.init();
        initDatas();
        initViews();
        initListener();
    }
    private void initDatas(){
        if(getIntent()!=null){
            Intent intent = getIntent();
            String rankMsg = intent.getStringExtra(RankFragment.FROM_RANK_FRAGMENT);

            Rank rank = new Gson().fromJson(rankMsg,Rank.class);
            userList.add(rank.getOne());
            userList.add(rank.getTwo());
            userList.add(rank.getThree());
            userList.add(rank.getFour());
            userList.add(rank.getFive());
            userList.add(rank.getSix());
            userList.add(rank.getSeven());
            userList.add(rank.getEight());
            userList.add(rank.getNine());
            userList.add(rank.getTen());
            setSupportActionBar(toolbar);
            toolbar.setTitle(rank.getTitle());
        }
    }

    private void initViews(){


        adapter = new RankUserAdapter(this,userList);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initListener(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        adapter.setOnItemClickListener(new RankUserAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                if(MyApp.isLogin()){
                    User user = userList.get(position);
                    String userMsg = new Gson().toJson(user,User.class);
                    Intent intent = new Intent(RankDetailActivity.this, PersonPageActivity.class);
                    intent.putExtra(Constant.TO_PERSON_PAGE,userMsg);
                    jumpTo(intent,false);
                }else {
                    jumpTo(LoginActivity.class,false);
                }

            }
        });
    }
}
