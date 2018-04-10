package com.howietian.chenyan.publish;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.PageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PublishActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewPager)
    ViewPager viewPager;

    private PageAdapter adapter;
    private List<String> titles = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private PubActivityFragment pubActivityFragment;
    private PubDynamicFragment pubDynamicFragment;

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_publish);
    }

    @Override
    public void init() {
        super.init();

        initData();
        initView();
        initListener();
    }

    private void initData() {
        titles.add("动态");
        titles.add("活动");
        pubActivityFragment = new PubActivityFragment();
        pubDynamicFragment = new PubDynamicFragment();
        fragments.add(pubDynamicFragment);
        fragments.add(pubActivityFragment);


    }

    private void initView() {
        setSupportActionBar(toolbar);
        adapter = new PageAdapter(getSupportFragmentManager(), fragments, titles);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }


    private void initListener() {
        back();
    }

    private void back() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.activity_close);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_submit) {

            if (viewPager.getCurrentItem() == 0) {
                pubDynamicFragment.submit();
            } else {
                pubActivityFragment.submit();
            }
        }
        return true;
    }
}
