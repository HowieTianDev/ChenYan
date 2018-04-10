package com.howietian.chenyan.home;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.SearchAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.entities.MActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SearchActivity extends BaseActivity {
    @Bind(R.id.tb_search)
    Toolbar toolbar;
    @Bind(R.id.recyclerview)
    RecyclerView recyclerView;

    public static final String MACTIVITY = "mactivity";
    public static final String ARTICLE = "article";
    private static final int FROM_ACTIVITY = 0;
    private static final int FROM_ARTICLE = 1;

    private SearchView mSearchView;
    private SearchAdapter searchAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private SearchView.SearchAutoComplete mSearchAutoComplete;
    private List<MActivity> mActivities = new ArrayList<>();
    private List<Article> articles = new ArrayList<>();
    private List<ArrayList<String>> searchList = new ArrayList<>();
    private List<String> titles = new ArrayList<>();

    boolean isActivity = false;
    boolean isArticle = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FROM_ACTIVITY:
                    mActivities.clear();
                    mActivities.addAll((List<MActivity>) msg.obj);
                    Log.e("search", "查询活动成功！" + mActivities.toString());
                    isActivity = true;

                    break;
                case FROM_ARTICLE:
                    articles.clear();
                    articles.addAll((List<Article>) msg.obj);
                    Log.e("search", "查询推文成功！" + articles.toString());
                    isArticle = true;
                    break;
            }

            if (isActivity && isArticle) {
                changeDatas();
                isActivity = false;
                isArticle = false;

            }


        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_search);
    }

    @Override
    public void init() {
        super.init();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        back();
//        刚进入这个页面的时候查询对应的数据


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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(item);
        mSearchAutoComplete = (SearchView.SearchAutoComplete) mSearchView.findViewById(R.id.search_src_text);

////        三种外观模式
//        //设置搜索框直接展开显示。左侧有放大镜(在搜索框中) 右侧有叉叉 可以关闭搜索框
//        mSearchView.setIconified(true);
//        //设置搜索框直接展开显示。左侧有放大镜(在搜索框外) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
//        mSearchView.setIconifiedByDefault(false);
        //设置搜索框直接展开显示。左侧有无放大镜(在搜索框中) 右侧无叉叉 有输入内容后有叉叉 不能关闭搜索框
        mSearchView.onActionViewExpanded();
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != "") {
                    search(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (searchAdapter != null) {
                    titles.clear();
                    searchAdapter.notifyDataSetChanged();
                }
                return false;
            }
        });


//        Resources resources = getResources();
//        final String[] testStrings = resources.getStringArray(R.array.city);


//        mSearchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView parent, View view, int position, long id) {
//                mSearchView.setQuery(titles.get(position), true);
////                showToast(searchList.get(position).get(0));
//
//
//
//                if (searchList.get(position).get(2).equals(MACTIVITY)) {
//                    Intent intent = new Intent(SearchActivity.this, ActivityDetailActivity.class);
//
//                    intent.putExtra(Constant.FROM_ACTIVIRY, searchList.get(position).get(0));
//                    jumpTo(intent, false);
//                } else {
//                    Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
//                    intent.putExtra(Constant.FROM_ARTICLE, searchList.get(position).get(0));
//                    jumpTo(intent, false);
//                }
//            }
//        });
////        设置触发自动补全所需要的最少字数
//        mSearchAutoComplete.setThreshold(0);

        return true;
    }

    /**
     * 查询所有的活动和推文
     */
    private void search(String title) {

        BmobQuery<MActivity> mActivityBmobQuery = new BmobQuery<>();
        BmobQuery<Article> articleBmobQuery = new BmobQuery<>();
        mActivityBmobQuery.addWhereMatches("title", title);
        articleBmobQuery.addWhereMatches("title", title);
        mActivityBmobQuery.findObjects(new FindListener<MActivity>() {
            @Override
            public void done(List<MActivity> list, BmobException e) {
                if (e == null) {

                    Message message = handler.obtainMessage();
                    message.what = FROM_ACTIVITY;
                    message.obj = list;
                    handler.sendMessage(message);
                } else {
                    showToast("查询活动失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });

        articleBmobQuery.findObjects(new FindListener<Article>() {
            @Override
            public void done(List<Article> list, BmobException e) {
                if (e == null) {
                    Message message = handler.obtainMessage();
                    message.what = FROM_ARTICLE;
                    message.obj = list;
                    handler.sendMessage(message);

                } else {
                    showToast("查询推文失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
// 将查询到的是数据转化为固定格式的数据，便于下一步处理

    private void changeDatas() {
        titles.clear();
        searchList.clear();
        Gson gson = new Gson();

        if (mActivities != null) {
            for (int i = 0; i < mActivities.size(); i++) {
                ArrayList<String> bean = new ArrayList<>();
                bean.add(gson.toJson(mActivities.get(i), MActivity.class));
                bean.add(mActivities.get(i).getTitle());
                bean.add(MACTIVITY);
                searchList.add(bean);
            }
        }
        if (articles != null) {
            for (int i = 0; i < articles.size(); i++) {
                ArrayList<String> bean = new ArrayList<>();
                bean.add(gson.toJson(articles.get(i), Article.class));
                bean.add(articles.get(i).getTitle());
                bean.add(ARTICLE);
                searchList.add(bean);
            }
        }

        for (int i = 0; i < searchList.size(); i++) {
            titles.add(searchList.get(i).get(1));
        }

        if(titles.isEmpty()){
            showToast("查询不到哦~");
        }


        /**
         * 在数据加载完毕后，设置Adapter
         */

        layoutManager = new LinearLayoutManager(this);
        searchAdapter = new SearchAdapter(this, titles);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setHasFixedSize(true);


        searchAdapter.setOnClickListener(new SearchAdapter.onItemClickListener() {
            @Override
            public void onClick(int position) {
                if (searchList.get(position).get(2).equals(MACTIVITY)) {
                    Intent intent = new Intent(SearchActivity.this, ActivityDetailActivity.class);

                    intent.putExtra(Constant.FROM_ACTIVIRY, searchList.get(position).get(0));
                    jumpTo(intent, false);
                } else {
                    Intent intent = new Intent(SearchActivity.this, ArticleDetailActivity.class);
                    intent.putExtra(Constant.FROM_ARTICLE, searchList.get(position).get(0));
                    jumpTo(intent, false);
                }
            }
        });
        //mSearchAutoComplete.setAdapter(new ArrayAdapter<>(this, R.layout.item_list, R.id.tv_item, titles));

    }


}
