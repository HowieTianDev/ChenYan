package com.howietian.chenyan;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;


import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.circle.CircleFragment;
import com.howietian.chenyan.focus.FocusFragment;
import com.howietian.chenyan.home.HomeFragment;
import com.howietian.chenyan.me.MeFragment;
import com.howietian.chenyan.rank.RankFragment;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    @Bind(R.id.bnvBar)
    BottomNavigationBar bnvBar;


    private CircleFragment circleFragment;
    private FocusFragment focusFragment;
    private MeFragment meFragment;
    private RankFragment rankFragment;
    private HomeFragment homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/**
 * 这里可控制fragment的重叠问题，未写好，日后再说
 */
//        if(savedInstanceState!=null){
//            homeFragment = (HomeFragment) getSupportFragmentManager().findFragmentByTag(homeFragment.getClass().getName());
//            focusFragment = (FocusFragment) getSupportFragmentManager().findFragmentByTag(focusFragment.getClass().getName());
//            meFragment = (MeFragment) getSupportFragmentManager().findFragmentByTag(meFragment.getClass().getName());
//            rankFragment = (RankFragment) getSupportFragmentManager().findFragmentByTag(rankFragment.getClass().getName());
//            circleFragment = (CircleFragment) getSupportFragmentManager().findFragmentByTag(circleFragment.getClass().getName());
//
//        }

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    public void init() {
        super.init();
        initBnvBar();
//        初始化为第一个fragment显示
        chooseFragments(0);
    }

    private void initBnvBar() {
        bnvBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bnvBar.setMode(BottomNavigationBar.MODE_FIXED);
        bnvBar.addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, R.string.square).setActiveColorResource(R.color.colorPrimary))
                .addItem(new BottomNavigationItem(R.drawable.circle, R.string.circle)).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.drawable.ic_gps_fixed_black_24dp, R.string.focus)).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.drawable.rank, R.string.rank)).setActiveColor(R.color.colorPrimary)
                .addItem(new BottomNavigationItem(R.drawable.me, R.string.me)).setActiveColor(R.color.colorPrimary)
                .initialise();
// 底部导航的点击跳转
        bnvBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                chooseFragments(position);
            }

            @Override
            public void onTabUnselected(int position) {

            }

            @Override
            public void onTabReselected(int position) {

            }
        });
    }

    /**
     * 隐藏fragment
     */

    private void hideFragments(FragmentTransaction ft) {
        if (homeFragment != null) {
            ft.hide(homeFragment);
        }
        if (circleFragment != null) {
            ft.hide(circleFragment);
        }
        if (focusFragment != null) {
            ft.hide(focusFragment);
        }
        if (rankFragment != null) {
            ft.hide(rankFragment);
        }
        if (meFragment != null) {
            ft.hide(meFragment);
        }
    }

    /**
     * 选择fragment
     */
    private void chooseFragments(int position) {
        FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
        hideFragments(ft);
        switch (position) {
            case 0:
                if (homeFragment == null) {
                    homeFragment = HomeFragment.newInstance("home_fragment");
                    ft.add(R.id.frameLayout, homeFragment,homeFragment.getClass().getName());
                }
                ft.show(homeFragment);
                break;
            case 1:
                if (circleFragment == null) {
                    circleFragment = CircleFragment.newInstance("circle_fragment");
                    ft.add(R.id.frameLayout, circleFragment,circleFragment.getClass().getName());
                }
                ft.show(circleFragment);
                break;
            case 2:
                if (focusFragment == null) {
                    focusFragment = FocusFragment.newInstance("focus_fragment");
                    ft.add(R.id.frameLayout, focusFragment,focusFragment.getClass().getName());
                }
                ft.show(focusFragment);
                break;
            case 3:
                if (rankFragment == null) {
                    rankFragment = RankFragment.newInstance("rank_fragment");
                    ft.add(R.id.frameLayout, rankFragment,rankFragment.getClass().getName());
                }
                ft.show(rankFragment);
                break;
            case 4:
                if (meFragment == null) {
                    meFragment = MeFragment.newInstance("me_fragment");
                    ft.add(R.id.frameLayout, meFragment,meFragment.getClass().getName());
                }
                ft.show(meFragment);
                break;
        }
        ft.commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        Log.e("onSaveInstanceState",outState.toString());
    }
}
