package com.howietian.chenyan.me.collect;


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
import com.howietian.chenyan.adapters.ActivityAdapter;
import com.howietian.chenyan.adapters.ArticleAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.home.ActivityDetailActivity;
import com.howietian.chenyan.home.ArticleDetailActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectArticleFragment extends BaseFragment {


    @Bind(R.id.rv_c_article)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_c_article)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<Article> articles = new ArrayList<>();
    private ArticleAdapter articleAdapter;
    private LinearLayoutManager manager;
    private User user = BmobUser.getCurrentUser(User.class);

    public static final String FROM_ARTICLE = Constant.FROM_ARTICLE;
    public static final String COLLECT_ARTICLE = "collect_article";

    public CollectArticleFragment() {
        // Required empty public constructor
    }

    public static CollectArticleFragment newInstance(String args){
        CollectArticleFragment fragment = new CollectArticleFragment();
        Bundle bundle = new Bundle();
        bundle.putString(COLLECT_ARTICLE,args);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect_article, container, false);
    }

    @Override
    public void init() {
        super.init();
        swipeRefreshLayout.setRefreshing(true);
        manager = new LinearLayoutManager(getContext());


    }

    //   从详情页返回时，自动刷新
    @Override
    public void onResume() {
        super.onResume();
        getData();
        articleAdapter = new ArticleAdapter(getContext(), articles);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(articleAdapter);

        setItemClick();
        refresh();
    }

    private void setItemClick() {
        articleAdapter.setOnItemClickListener(new ArticleAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {

                final Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
                /**
                 * 把Article对象通过Gson转化为String传递到详情页
                 */

                Article article = articles.get(position);
                Gson gson = new Gson();
                String msg = gson.toJson(article, Article.class);
                intent.putExtra(FROM_ARTICLE, msg);
                jumpTo(intent, false);
            }
        });

    }

    /**
     * 刷新操作
     */
    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
    }


    /**
     * 只执行刷新操作,查询当前用户喜欢的推文
     * 采用内部查询的方法，来实现反查询
     */
    private void getData() {

        BmobQuery<Article> query = new BmobQuery<>();
        query.include("collect");
        query.order("-createdAt");

        BmobQuery<User> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId",user.getObjectId());
        query.addWhereMatchesQuery("collect","_User",innerQuery);

        query.findObjects(new FindListener<Article>() {
            @Override
            public void done(List<Article> list, BmobException e) {
                if (e == null) {
//                    查出有数据
                    if (list.size() > 0) {
                        articles.clear();
                        articles.addAll(list);
                        articleAdapter.notifyDataSetChanged();

//                        查询到无数据
                    } else {
                        showToast(getString(R.string.no_data));
                    }
                } else {
                    showToast("请求服务器异常" + e.getMessage() + "错误代码" + e.getErrorCode());
                    Log.e("HHH", e.getMessage() + "错误代码" + e.getErrorCode());
                }

                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}
