package com.howietian.chenyan;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.howietian.chenyan.circle.CircleFragment;
import com.howietian.chenyan.competition.CompetitionFragment;
import com.howietian.chenyan.home.HomeFragment;
import com.howietian.chenyan.me.MeFragment;
import com.howietian.chenyan.publish.PublishActivity;
import com.howietian.chenyan.utils.UIHelper;

import butterknife.Bind;

public class MainActivity extends BaseActivity {
    @Bind(R.id.bnvBar)
    BottomNavigationBar bnvBar;

    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 3;
    private CircleFragment circleFragment;

    private MeFragment meFragment;

    private HomeFragment homeFragment;
    private CompetitionFragment competitionFragment;


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

        requestCamera();
        initBnvBar();
//        初始化为第一个fragment显示
        chooseFragments(0);
    }

    private void initBnvBar() {
        bnvBar.setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_DEFAULT);
        bnvBar.setMode(BottomNavigationBar.MODE_FIXED);
        bnvBar.addItem(new BottomNavigationItem(R.drawable.ic_home_black_24dp, R.string.square))
                .addItem(new BottomNavigationItem(R.drawable.circle, R.string.circle))
                .addItem(new BottomNavigationItem(R.drawable.ic_add_box_black_24dp, R.string.publish).setInActiveColorResource(R.color.colorPrimaryDark))
                .addItem(new BottomNavigationItem(R.drawable.rank, R.string.competition))
                .addItem(new BottomNavigationItem(R.drawable.me, R.string.me))
                .setActiveColor(R.color.colorPrimary)
                .initialise();
// 底部导航的点击跳转
        final int[] pre_position = {0};
        bnvBar.setTabSelectedListener(new BottomNavigationBar.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {


                if(position!=2){
                    chooseFragments(position);
                    pre_position[0] = position;
                }else {
                    bnvBar.selectTab(pre_position[0]);
                    jumpTo(PublishActivity.class,false);
                     //切换动画
                    overridePendingTransition(R.anim.activity_open,R.anim.activity_close);
                }

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

        if (competitionFragment != null) {
            ft.hide(competitionFragment);
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
            case 3:
                if (competitionFragment == null) {
                    competitionFragment = CompetitionFragment.newInstance("competition_fragment");
                    ft.add(R.id.frameLayout, competitionFragment,competitionFragment.getClass().getName());
                }
                ft.show(competitionFragment);
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


    private long lastClickBackTime;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastClickBackTime > 2000) { // 后退阻断
            UIHelper.ToastMessage("再点一次退出");
            lastClickBackTime = System.currentTimeMillis();
        } else { // 关掉app
            super.onBackPressed();
        }
    }

    /**
     * 请求权限
     */
    private void requestCamera() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_TAKE_PHOTO_PERMISSION);
        } else {
            //有权限，直接拍照

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
            } else {
                showToast("CAMERA PERMISSION DENIED");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}
