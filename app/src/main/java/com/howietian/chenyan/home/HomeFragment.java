package com.howietian.chenyan.home;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.PageAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment {
    @Bind(R.id.bgaBanner)
    BGABanner bgaBanner;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.viewpager)
    ViewPager viewPager;
    @Bind(R.id.tv_search)
    TextView tvSearch;

    private static String ARTICLE = "精品推文";
    private static String ACTIVITY = "活动";

    private ActivityFragment activityFragment;
    private ArticleFragment articleFragment;
    private PageAdapter pageAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();


    public HomeFragment() {
        // Required empty public constructor
    }

    private static final String HOME_FRAGMENT = "home_fragment";



    public static HomeFragment newInstance(String args){
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(HOME_FRAGMENT,args);
        fragment.setArguments(bundle);
        return fragment;
    }



    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void init() {
        super.init();
        activityFragment = new ActivityFragment();
        articleFragment = new ArticleFragment();

        fragments.add(articleFragment);
        fragments.add(activityFragment);
        titles.add(ARTICLE);
        titles.add(ACTIVITY);

        pageAdapter = new PageAdapter(getChildFragmentManager(), fragments, titles);

        viewPager.setAdapter(pageAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        bgaBanner.setData(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4);
        bgaBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                showToast(position + "");
            }
        });
    }

    @OnClick(R.id.tv_search)
    public void toSearch(){
        jumpTo(SearchActivity.class,false);
    }


}
