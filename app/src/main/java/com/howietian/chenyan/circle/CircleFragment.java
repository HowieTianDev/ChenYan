package com.howietian.chenyan.circle;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.DynamicAdapter;
import com.howietian.chenyan.adapters.GridViewAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.views.CommentWidget;
import com.howietian.chenyan.views.PraiseWidget;
import com.melnykov.fab.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CircleFragment extends BaseFragment {


    @Bind(R.id.bgaBanner)
    BGABanner bgaBanner;
    @Bind(R.id.tb_circle)
    Toolbar toolbar;
    @Bind(R.id.recyclerView_circle)
    RecyclerView recyclerView;
    @Bind(R.id.fab_pub)
    FloatingActionButton fab;
    @Bind(R.id.nestedScrollView_circle)
    NestedScrollView scrollView;
    @Bind(R.id.swipeLayout_circle)
    SwipeRefreshLayout swipeRefreshLayout;



    private static final String CIRCLE_FRAGMENT = "circle_fragment";

    private List<Dynamic> dynamicList = new ArrayList<>();
    private DynamicAdapter dynamicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int lastVisibleItem;
    private int limit = Constant.LIMIT;
    private String lastTime;
    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;



    public static CircleFragment newInstance(String args) {
        CircleFragment fragment = new CircleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CIRCLE_FRAGMENT, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    public CircleFragment() {
        // Required empty public constructor
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frament_circle, container, false);
    }

    @Override
    public void init() {
        super.init();


        initDatas();
        initListener();

    }


    private void initDatas() {
        swipeRefreshLayout.setRefreshing(true);
        getData(PULL_REFRESH);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        bgaBanner.setData(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4);

        Dynamic dynamic = new Dynamic();
        dynamic.setContent("恭喜辰言2017年跳水活动举办成功！\nfjlskdfj\nfsdfsdf\nfsdf\n1\n2\n3\n4\n5\n6\n7\n8");
        dynamic.setUser(BmobUser.getCurrentUser(User.class));
        for (int i = 0; i < 9; i++) {
            dynamicList.add(dynamic);
        }
        dynamicAdapter = new DynamicAdapter(getContext(), dynamicList);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dynamicAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.setNestedScrollingEnabled(false);



    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initListener() {

//         scrollView.post(new Runnable() {
//            @Override
//            public void run() {
//                scrollView.scrollTo(0, 0);
//            }
//        });

        bgaBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                showToast(position + "");
            }
        });

        loadMore();
        refresh();



    }

    private void refresh(){
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData(PULL_REFRESH);
            }
        });
    }


    private void loadMore(){
        //        nestedScrollView 嵌套 recyclerView 导致recyclerview 监听不到，通过对recyclerview 的监听来实现fab的显示与隐藏

        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > oldScrollY) {
                    // 向下滑动
                    fab.hide();
                }

                if (scrollY < oldScrollY) {
                    // 向上滑动
                    fab.show();
                }

                if (scrollY == 0) {
                    // 顶部
                }

                if (scrollY == (v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight())) {
                    // 底部
                    getData(LOAD_MORE);
                }
            }
        });
    }

    @OnClick(R.id.fab_pub)
    public void pubDynamic() {
        if (MyApp.isLogin()) {
            jumpTo(PubDynamicActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }
    }



    /**
     * 分页获取数据，分别为下拉刷新，上拉加载
     */

    private void getData(final int type) {

        final BmobQuery<Dynamic> query = new BmobQuery<>();
        query.include("user");
        query.order("-createdAt");
//        设置每页查询的item数目
        query.setLimit(limit);

//        加载更多
        if (type == LOAD_MORE) {
//         处理时间查询
            Date date = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = dateFormat.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
//         只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
        }

        query.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> list, BmobException e) {
                if (e == null) {
//                    查出有数据
                    if (list.size() > 0) {
                        if (type == PULL_REFRESH) {
                            dynamicList.clear();
                        }
                        dynamicList.addAll(list);
                        dynamicAdapter.notifyDataSetChanged();
                        lastTime = dynamicList.get(dynamicList.size() - 1).getCreatedAt();
                        Log.e("HHH", lastTime);
                        

//                        查询到无数据
                    } else {
                        if (type == LOAD_MORE) {
                            showToast("没有更多数据了");
                        } else if (type == PULL_REFRESH) {
                            showToast("服务器没有数据");
                        }

                    }
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showToast("请求服务器异常" + e.getMessage() + "错误代码" + e.getErrorCode());
                    Log.e("HHH", e.getMessage() + "错误代码" + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }


            }
        });
    }





}
