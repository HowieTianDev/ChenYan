package com.howietian.chenyan.me.club;

import android.content.Intent;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class MyClubActivity extends BaseActivity {


    @Bind(R.id.tb_my_club)
    Toolbar tbMyClub;
    @Bind(R.id.tv_apply_club)
    TextView tvApplyClub;
    @Bind(R.id.rv_my_club)
    RecyclerView rvMyClub;

    private SearchView mSearchView;
    private List<User> userList = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private FUserAdapter adapter;
    private User currentUser = BmobUser.getCurrentUser(User.class);


    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_club);
    }

    @Override
    public void init() {
        super.init();
        initDatas();
        initViews();
        initListener();

    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = BmobUser.getCurrentUser(User.class);
        if (currentUser.getClub() != null) {
            if (currentUser.getClub()) {
                tvApplyClub.setText("管理社团");
            } else {
                tvApplyClub.setText("申请社团");
            }
        } else {
            tvApplyClub.setText("申请社团");
        }

    }

    private void initDatas() {
        queryFanUser();
    }

    private void initViews() {

        setSupportActionBar(tbMyClub);

        adapter = new FUserAdapter(this, userList);
        layoutManager = new LinearLayoutManager(this);
        rvMyClub.setLayoutManager(layoutManager);
        rvMyClub.setAdapter(adapter);
        rvMyClub.setHasFixedSize(true);
        rvMyClub.setNestedScrollingEnabled(false);
    }

    private void initListener() {
        tbMyClub.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        adapter.setOnItemClickListener(new FUserAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                User user = userList.get(position);
                String userMsg = new Gson().toJson(user, User.class);
                Intent intent = new Intent(MyClubActivity.this, PersonPageActivity.class);
                intent.putExtra(Constant.TO_PERSON_PAGE, userMsg);
                jumpTo(intent, false);
            }
        });
    }


    @OnClick(R.id.tv_apply_club)
    public void toApplyClub() {
        if (currentUser.getClub() != null) {
            if (currentUser.getClub()) {
                jumpTo(ManageActivity.class, false);
            } else {
                jumpTo(ApplyClubActivity.class, false);
            }
        } else {
            jumpTo(ApplyClubActivity.class, false);
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_club, menu);
        MenuItem item = menu.findItem(R.id.menu_search_club);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchView.setQueryHint("请输入社团号");
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryClubInfo(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search_club:
                showToast("搜索");
                break;
        }
        return true;
    }

    private void queryClubInfo(String id) {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("clubId", id);
        query.addWhereEqualTo("isClub", true);
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Intent intent = new Intent(MyClubActivity.this, PersonPageActivity.class);
                        intent.putExtra(Constant.TO_PERSON_PAGE, new Gson().toJson(list.get(0)));
                        jumpTo(intent, false);
                    } else {
                        showToast("没有对应的社团");
                    }

                } else {
                    showToast("请求服务器出错" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }


    private void queryFanUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.include("members");
        BmobQuery<BmobUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", currentUser.getObjectId());
        query.addWhereMatchesQuery("members", "_User", innerQuery);

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
                        showToast(getString(R.string.no_data_club));
                    }
                } else {
                    showToast("访问服务器失败，稍后重试" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
