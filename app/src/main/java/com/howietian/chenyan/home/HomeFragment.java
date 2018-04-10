package com.howietian.chenyan.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.PageAdapter;
import com.howietian.chenyan.entities.Banner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bingoogolapple.bgabanner.BGABanner.Adapter;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

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

    private static String ARTICLE = "精选";
    private static String ACTIVITY = "活动";
    private static String RANK = "排行";


    private ActivityFragment activityFragment;
    private ArticleFragment articleFragment;
    private RankFragment rankFragment;
    private PageAdapter pageAdapter;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    ArrayList<String> articleIds = new ArrayList<>();

    public static final String FROM_BANNER = "from_banner";


    public HomeFragment() {
        // Required empty public constructor
    }

    private static final String HOME_FRAGMENT = "home_fragment";


    public static HomeFragment newInstance(String args) {
        HomeFragment fragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(HOME_FRAGMENT, args);
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
        rankFragment = new RankFragment();

        fragments.add(articleFragment);
        fragments.add(activityFragment);
        fragments.add(rankFragment);

        titles.add(ARTICLE);
        titles.add(ACTIVITY);
        titles.add(RANK);

        pageAdapter = new PageAdapter(getChildFragmentManager(), fragments, titles);

        viewPager.setAdapter(pageAdapter);
        //一次加载所有的页面,默认是一次加载两个页面，所以在切换的时候会有一点问题
        viewPager.setOffscreenPageLimit(titles.size());
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);



        bgaBanner.setAdapter(new Adapter<ImageView, String>() {
            @Override
            public void fillBannerItem(BGABanner banner, ImageView itemView, String model, int position) {
                Glide.with(HomeFragment.this)
                        .load(model)
                        .placeholder(R.drawable.banner1)
                        .error(R.drawable.banner2)
                        .dontAnimate()
                        .into(itemView);
            }
        });


        queryBanner(Banner.HOME);

        bgaBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                if(!articleIds.isEmpty()){
                    Intent intent = new Intent(getContext(),ArticleDetailActivity.class);
                    intent.putExtra(FROM_BANNER,articleIds.get(position));

                    jumpTo(intent,false);
                }

            }
        });
    }

    @OnClick(R.id.tv_search)
    public void toSearch() {
        jumpTo(SearchActivity.class, false);
    }

    private void queryBanner(int type) {
        BmobQuery<Banner> query = new BmobQuery<>();
        query.addWhereEqualTo("type", type);
        query.findObjects(new FindListener<Banner>() {
            @Override
            public void done(List<Banner> list, BmobException e) {
                if (e == null) {
                    ArrayList<String> urls = new ArrayList<>();
                    urls.addAll(list.get(0).getUrls());
                    Log.e("首页", list.toString());
                    if(articleIds!=null){
                        articleIds.clear();
                    }
                    articleIds.addAll(list.get(0).getArticleIds());

                    bgaBanner.setData(urls, null);
                } else {
                    showToast(e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
