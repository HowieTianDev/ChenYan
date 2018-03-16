package com.howietian.chenyan.home;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.ArticleAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.entities.User;
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
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArticleFragment extends BaseFragment {
    @Bind(R.id.recycleView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_article)
    SmartRefreshLayout swipeRefreshLayout;


    private List<Article> articles = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private LinearLayoutManager manager;
    private int lastVisibleItem;
    private int limit = Constant.LIMIT;
    public static final String FROM_ARTICLE = Constant.FROM_ARTICLE;
    private static final int ARTICLE_REQUEST_CODE = 0;

    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;

    private String lastTime;
    int currentPosition;

    public ArticleFragment() {
        // Required empty public constructor
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_article, container, false);
    }

    @Override
    public void init() {
        super.init();

        swipeRefreshLayout.autoRefresh();


        manager = new LinearLayoutManager(getContext());
        /**
         * 初始化加载数据
         */
        getData(PULL_REFRESH);

        articleAdapter = new ArticleAdapter(getContext(), articles);
//        设置布局方向
        recyclerView.setLayoutManager(manager);
//        提高性能
        recyclerView.setHasFixedSize(true);
//        设置Adapter
        recyclerView.setAdapter(articleAdapter);

        setItemClick();

        refresh();
        loadMore();


    }

    /**
     * RecycleView Item 的点击事件
     */
    private void setItemClick() {
        articleAdapter.setOnItemClickListener(new ArticleAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                /**
                 * 把Article对象转化为通过Gson转化为字符串，传递到详情页
                 */
                currentPosition = position;
                Article article = articles.get(position);
                String msg = new Gson().toJson(article, Article.class);
                intent.putExtra(FROM_ARTICLE, msg);
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
            ArrayList<String> collectIdList = data.getStringArrayListExtra("collectIdList");
            Article article = articles.get(currentPosition);
            Log.e("测试", commentNum);
            article.setCommentNum(Integer.valueOf(commentNum));
            article.setCollectIdList(collectIdList);
            article.setLikeIdList(likeIdList);
            articles.set(currentPosition, article);
        }
    }

    /**
     * 刷新操作
     */
    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(PULL_REFRESH);
            }
        });
    }

    /**
     * 上拉加载操作,通过监听recyclerview的滚动事件和状态，
     * 当滑动到最底部且手指离开屏幕后，触发此操作
     */
    private void loadMore() {
//        最后一个可以看到的item

        swipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
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
        BmobQuery<Article> query = new BmobQuery<>();
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

        query.findObjects(new FindListener<Article>() {
            @Override
            public void done(List<Article> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (type == PULL_REFRESH) {
                            articles.clear();
                        }

                        articles.addAll(list);
                        articleAdapter.notifyDataSetChanged();
//                        获得最后一个item的创建时间
                        lastTime = articles.get(articles.size() - 1).getCreatedAt();
                        if (type == LOAD_MORE) {
                            swipeRefreshLayout.finishLoadmore();
                        } else {
                            swipeRefreshLayout.finishRefresh();
                        }
                    } else {
                        if (type == LOAD_MORE) {
                            showToast("没有更多数据了");
                            swipeRefreshLayout.finishLoadmore();
                        } else {
                            showToast("服务器没有数据");
                            swipeRefreshLayout.finishRefresh();
                        }
                    }

                } else {
                    showToast("请求服务器异常" + e.getMessage() + e.getErrorCode());
                    Log.e("测试",e.getMessage()+"==>"+e.getErrorCode());
                    if(type==PULL_REFRESH){
                        swipeRefreshLayout.finishRefresh();
                    }else{
                        swipeRefreshLayout.finishLoadmore();
                    }
                }
            }
        });


    }


}
