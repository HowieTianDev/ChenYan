package com.howietian.chenyan.me.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.FUserAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.personpage.PersonPageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ManageActivity extends BaseActivity {


    @Bind(R.id.tb_manage)
    Toolbar tbManage;
    @Bind(R.id.tv_club_name)
    TextView tvClubName;
    @Bind(R.id.tv_club_id)
    TextView tvClubId;
    @Bind(R.id.rv_club_member)
    RecyclerView rvClubMember;

    private List<User> userList = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private FUserAdapter adapter;
    User user = BmobUser.getCurrentUser(User.class);

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_manage);
    }

    @Override
    public void init() {
        super.init();
        initViews();
        initDatas();
        initListener();
    }

    private void initDatas() {


        queryFocusUser();


    }

    private void initViews() {
        setSupportActionBar(tbManage);

        adapter = new FUserAdapter(this, userList);
        layoutManager = new LinearLayoutManager(this);
        rvClubMember.setNestedScrollingEnabled(false);
        rvClubMember.setHasFixedSize(true);
        rvClubMember.setLayoutManager(layoutManager);
        rvClubMember.setAdapter(adapter);

        if (user.getNickName() != null) {
            tvClubName.setText(user.getNickName());
        }
        if (user.getClubId() != null) {
            tvClubId.setText(user.getClubId());
        }

    }

    private void initListener() {
        tbManage.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter.setOnItemClickListener(new FUserAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                User user = userList.get(position);
                String userMsg = new Gson().toJson(user,User.class);
                Intent intent = new Intent(ManageActivity.this,PersonPageActivity.class);
                intent.putExtra(Constant.TO_PERSON_PAGE,userMsg);
                jumpTo(intent,false);
            }
        });
    }

    private void queryFocusUser() {

        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("members", new BmobPointer(user));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (userList != null) {
                            userList.clear();
                        }
                        userList.addAll(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        showToast(getString(R.string.no_data_club_members));
                    }
                } else {
                    showToast("访问服务器失败，稍后重试" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
