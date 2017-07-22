package com.howietian.chenyan.me.collect;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.PageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class MyCollectActivity extends BaseActivity {

    @Bind(R.id.tb_collect)
    Toolbar tbCollect;
    @Bind(R.id.tl_collect)
    TabLayout tlCollect;
    @Bind(R.id.vp_collect)
    ViewPager vpCollect;

    private CollectActivityFragment collectActivityFragment;
    private CollectArticleFragment collectArticleFragment;
    private PageAdapter pageAdapter;
    private static final String ARTICLE = "精品推文";
    private static final String ACTIVITY = "活动";
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_collect);
    }

    @Override
    public void init() {
        super.init();
        back();
        initDatas();
        pageAdapter = new PageAdapter(getSupportFragmentManager(),fragments,titles);
        vpCollect.setAdapter(pageAdapter);
        tlCollect.setupWithViewPager(vpCollect);
        tlCollect.setTabMode(TabLayout.MODE_FIXED);

    }

    private void initDatas(){
        collectActivityFragment = CollectActivityFragment.newInstance("collect activity fragment");
        collectArticleFragment = CollectArticleFragment.newInstance("collect article fragment");
        fragments.add(collectArticleFragment);
        fragments.add(collectActivityFragment);
        titles.add(ARTICLE);
        titles.add(ACTIVITY);
    }



    private void back(){
        tbCollect.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
