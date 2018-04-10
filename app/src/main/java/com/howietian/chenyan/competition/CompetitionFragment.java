package com.howietian.chenyan.competition;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.CompetitionAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.Competition;
import com.howietian.chenyan.home.MRankDetailActivity;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class CompetitionFragment extends BaseFragment {


    public CompetitionFragment() {
        // Required empty public constructor
    }

    @Bind(R.id.recycleView)
    RecyclerView recycleView;
    @Bind(R.id.swipeLayout_article)
    SmartRefreshLayout swipeLayoutRank;

    private static final String COMPETITION_FRAGMENT = "competition_fragment";
    private List<Competition> ranks = new ArrayList<>();
    private CompetitionAdapter competitionAdapter;
    private LinearLayoutManager manager;
    private int lastVisibleItem;
    private int limit = Constant.LIMIT;
    public static final String FROM_Competition = "from_competition";
    private static final int ARTICLE_REQUEST_CODE = 0;

    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;

    private String lastTime;
    int currentPosition;


    public static CompetitionFragment newInstance(String args) {
        CompetitionFragment fragment = new CompetitionFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COMPETITION_FRAGMENT, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_competion, container, false);

    }


    @Override
    public void init() {
        super.init();


        swipeLayoutRank.autoRefresh();


        manager = new LinearLayoutManager(getContext());
        /**
         * 初始化加载数据
         */
        getData(PULL_REFRESH);

        competitionAdapter = new CompetitionAdapter(getContext(), ranks);
//        设置布局方向
        recycleView.setLayoutManager(manager);
//        提高性能
        recycleView.setHasFixedSize(true);
//        设置Adapter
        recycleView.setAdapter(competitionAdapter);

        setItemClick();

        refresh();
        loadMore();
    }

    /**
     * RecycleView Item 的点击事件
     */
    private void setItemClick() {
        competitionAdapter.setOnItemClickListener(new CompetitionAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), CompetitionDetailActivity.class);
                /**
                 * 把Article对象转化为通过Gson转化为字符串，传递到详情页
                 */
                currentPosition = position;
                Competition rank = ranks.get(position);
                String msg = new Gson().toJson(rank, Competition.class);
                intent.putExtra(FROM_Competition, msg);
                startActivityForResult(intent, ARTICLE_REQUEST_CODE);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ARTICLE_REQUEST_CODE && resultCode == RESULT_OK) {
            String commentNum = data.getStringExtra("commentNum");
            ArrayList<String> likeIdList = data.getStringArrayListExtra("likeIdList");
            Competition rank = ranks.get(currentPosition);
            Log.e("测试", commentNum);
            rank.setCommentNum(Integer.valueOf(commentNum));
            rank.setLikeIdList(likeIdList);
            ranks.set(currentPosition, rank);
        }
    }


    /**
     * 刷新操作
     */
    private void refresh() {
        swipeLayoutRank.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(PULL_REFRESH);
            }
        });
    }

    private void loadMore() {
//        最后一个可以看到的item

        swipeLayoutRank.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(LOAD_MORE);
            }
        });
    }


    /**
     * 分页获取数据，分别为下拉刷新，上拉加载
     */

    private void getData(final int type) {
        BmobQuery<Competition> query = new BmobQuery<>();
        query.order("-createdAt");
//        设置每页查询的item数目
        query.setLimit(limit);

//       加载更多
        if (type == LOAD_MORE) {
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

        query.findObjects(new FindListener<Competition>() {
            @Override
            public void done(List<Competition> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (type == PULL_REFRESH) {
                            ranks.clear();
                        }

                        ranks.addAll(list);
                        competitionAdapter.notifyDataSetChanged();
//                        获得最后一个item的创建时间
                        lastTime = ranks.get(ranks.size() - 1).getCreatedAt();
                        if (type == LOAD_MORE) {
                            swipeLayoutRank.finishLoadmore();
                        } else {
                            swipeLayoutRank.finishRefresh();
                        }
                    } else {
                        if (type == LOAD_MORE) {
                            showToast(getString(R.string.no_data));
                            swipeLayoutRank.finishLoadmore();
                        } else {
                            showToast(getString(R.string.no_data_article));
                            swipeLayoutRank.finishRefresh();
                        }
                    }

                } else {
                    showToast("请求服务器异常" + e.getMessage() + e.getErrorCode());
                    Log.e("测试", e.getMessage() + "==>" + e.getErrorCode());
                    if (type == PULL_REFRESH) {
                        swipeLayoutRank.finishRefresh();
                    } else {
                        swipeLayoutRank.finishLoadmore();
                    }
                }
            }
        });


    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
