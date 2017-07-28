package com.howietian.chenyan.circle;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

import butterknife.Bind;

public class ChooseCircleActivity extends BaseActivity {

    @Bind(R.id.tb_circle)
    Toolbar tbCircle;
    @Bind(R.id.recyclerView_circle)
    RecyclerView recyclerViewCircle;
    @Bind(R.id.swipeLayout_circle)
    SmartRefreshLayout swipeLayoutCircle;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_choose_circle);
    }

    @Override
    public void init() {
        super.init();
        initDatas();
        initViews();
        initListener();
    }

    private void initDatas() {

        if(getIntent()!=null){
            type = getIntent().getStringExtra(CircleFragment.TYPE);
            showToast(type);
        }
    }
    private void initViews(){
        setSupportActionBar(tbCircle);
        tbCircle.setTitle(type);
    }

    private void initListener() {

    }
}
