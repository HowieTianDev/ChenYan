package com.howietian.chenyan.me;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.NotifyAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.personpage.PersonPageActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class MyMsgActivity extends BaseActivity {
    private NotifyAdapter adapter;
    private RecyclerView.LayoutManager manager;
    private ArrayList<String> memberIds = new ArrayList<>();

    @Bind(R.id.rv_my_msg)
    RecyclerView rvMyMsg;
    @Bind(R.id.tb_my_msg)
    Toolbar tbMyMsg;

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_msg);
        Log.d("bmobMsg", MyApp.userList.toString());
    }

    @Override
    public void init() {
        super.init();
        setSupportActionBar(tbMyMsg);
        tbMyMsg.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter = new NotifyAdapter(MyApp.userList, this);
        manager = new LinearLayoutManager(this);
        rvMyMsg.setLayoutManager(manager);
        rvMyMsg.setAdapter(adapter);
        rvMyMsg.setHasFixedSize(true);

        initListener();

    }

    private void initListener() {
        adapter.setOnInfoClickListener(new NotifyAdapter.onInfoClickListener() {
            @Override
            public void onClick(int position) {
                User user = MyApp.userList.get(position);
                String userMsg = new Gson().toJson(user, User.class);
                Intent intent = new Intent(MyMsgActivity.this, PersonPageActivity.class);
                intent.putExtra(Constant.TO_PERSON_PAGE, userMsg);
                jumpTo(intent, false);
            }
        });


        adapter.setOnDisagreeClickListener(new NotifyAdapter.onDisagreeClickListener() {
            @Override
            public void onClick(int position) {
                MyApp.userList.remove(position);
                adapter.notifyDataSetChanged();
                showToast("拒绝");
            }
        });

        adapter.setOnAgreeClickListener(new NotifyAdapter.onAgreeClickListener() {
            @Override
            public void onClick(int position) {

                User applyUser = MyApp.userList.get(position);
                MyApp.userList.remove(position);
                adapter.notifyDataSetChanged();
                User user = BmobUser.getCurrentUser(User.class);
                if (user.getMemberIds() != null) {
                    memberIds = user.getMemberIds();
                }
                memberIds.add(applyUser.getObjectId());
                BmobRelation relation = new BmobRelation();
                relation.add(applyUser);

                user.setMembers(relation);
                user.setMemberIds(memberIds);

                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            Log.e("消息", "添加成功");
                        } else {
                            showToast("添加失败"+e.getErrorCode()+e.getMessage());
                        }
                    }
                });

            }
        });
    }
}
