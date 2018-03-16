package com.howietian.chenyan.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.CommentAdapter;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.entities.Comment;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ArticleDetailActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tb_article)
    Toolbar tbArticle;
    @Bind(R.id.rv_article_comment)
    RecyclerView commentList;
    @Bind(R.id.scrollView)
    NestedScrollView scrollView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_upTime)
    TextView tvUpTime;
    @Bind(R.id.iv_image)
    ImageView ivImage;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.tv_comment_num)
    TextView tvCommentNum;
    @Bind(R.id.tv_like_num)
    TextView tvLikeNum;
    @Bind(R.id.tv_send_comment)
    TextView tvSendComment;
    @Bind(R.id.iv_collect)
    ImageView ivCollect;
    @Bind(R.id.iv_like)
    ImageView ivLike;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.webView_article)
    WebView wbArticle;


    private static final String TAG = "Article";
    private ArrayList<String> likeIdList = new ArrayList<>();
    private ArrayList<String> collectIdList = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();
    private CommentAdapter adapter;
    private RecyclerView.LayoutManager manager;
    //    当前操作的文章对象
    private Article article;
    private Button btnSend;
    private EditText etComment;
    private PopupWindow popupWindow;

    //    全局的用户对象
    final User user = BmobUser.getCurrentUser(User.class);

    //    是否点赞和收藏的标志
    boolean isCollect = false;
    boolean isLike = false;
    //    评论的数量
    String  commentNum = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {

        setContentView(R.layout.activity_article_detail);
    }

    @Override
    public void init() {
        super.init();
        back();

        commentList.setNestedScrollingEnabled(false);
        initDatas();

        queryCommentList();
        likeIdList = article.getLikeIdList();
        if(likeIdList == null){
            likeIdList = new ArrayList<>();
        }

        collectIdList = article.getCollectIdList();
        if(collectIdList == null){
            collectIdList = new ArrayList<>();
        }

    }

    //返回按钮,将是否点赞、收藏、评论数量返回上一级页面
    private void back() {
        setSupportActionBar(tbArticle);
        tbArticle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                commentNum = tvCommentNum.getText().toString();
                intent.putExtra("commentNum",commentNum);
                intent.putStringArrayListExtra("likeIdList",likeIdList);
                intent.putStringArrayListExtra("collectIdList",collectIdList);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

    }
//重写返回事件，将数据返回上一级页面,去掉superonbackpressed(),才能传Intent。。。。
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        commentNum = tvCommentNum.getText().toString();
        intent.putExtra("commentNum",commentNum);
        intent.putStringArrayListExtra("likeIdList",likeIdList);
        intent.putStringArrayListExtra("collectIdList",collectIdList);
        setResult(RESULT_OK,intent);
        finish();
    }

    private void initDatas() {
        Intent intent = getIntent();
        String msg = intent.getStringExtra(ArticleFragment.FROM_ARTICLE);
        article = new Gson().fromJson(msg, Article.class);

        tvTitle.setText(article.getTitle());
        tvUpTime.setText(article.getUpTime());
        loadImage(article.getPhoto().getUrl(), ivImage);
        wbArticle.loadUrl(article.getUrl());
        // 设置webview只能用内置浏览器打开
        WebSettings wSet = wbArticle.getSettings();
        wSet.setJavaScriptEnabled(true);
        wbArticle.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

        tvContent.setText(article.getContent());
        if (article.getCommentNum() != null) {
            tvCommentNum.setText(article.getCommentNum()+"");
        }
        if (article.getLikeIdList() != null) {
            tvLikeNum.setText(article.getLikeIdList().size()+"");
        }


        /**
         * 判断用户是否点赞，并初始化界面的点赞和收藏的图标
         */
        if (isShowLike()) {
            ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
            isLike = true;
        } else {
            ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
            isLike = false;
        }

        if (isShowCollect()) {
            ivCollect.setImageResource(R.drawable.ic_favorite_orange_500_24dp);
            isCollect = true;
        } else {
            ivCollect.setImageResource(R.drawable.ic_favorite_border_grey_500_24dp);
            isCollect = false;
        }
        manager = new LinearLayoutManager(this);
        adapter = new CommentAdapter(this, comments);
        commentList.setLayoutManager(manager);
        commentList.setHasFixedSize(true);
        commentList.setAdapter(adapter);
    }

    //    判断当前用户是否点赞
    private boolean isShowLike() {

        if (article.getLikeIdList() != null && MyApp.isLogin()) {
            if (article.getLikeIdList().contains(BmobUser.getCurrentUser(User.class).getObjectId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    //    判断当前用户是否收藏
    private boolean isShowCollect() {

        if (article.getCollectIdList() != null && MyApp.isLogin()) {
            if (article.getCollectIdList().contains(BmobUser.getCurrentUser(User.class).getObjectId())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    private void queryCommentList() {
        BmobQuery<Comment> query = new BmobQuery<>();
//        查询推文的评论列表
        query.addWhereEqualTo("article", article);
        query.order("createdAt");
        query.include("user");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(final List<Comment> list, BmobException e) {
                if (e == null) {
                    comments.clear();
                    comments.addAll(list);
                    adapter.notifyDataSetChanged();

//                   更新该推文的评论数量
                    article.setCommentNum(list.size());
                    tvCommentNum.setText( list.size() +"");
                    article.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e(TAG, "评论数目更新成功！");
//                              在查询评论数量成功后且更新该推文的评论数量成功后，在更新UI
                            } else {
                                Log.e(TAG, "评论数目更新失败！" + e.getMessage() + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    showToast("评论列表获取失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 底部三个按钮的点击事件
     */
    /**
     * 点击弹出输入框，同时弹出软键盘
     */
    @OnClick(R.id.tv_send_comment)
    public void inputComment() {
        if (MyApp.isLogin()) {
            showPopup();
            popupInputMethodWindow();
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }

    @OnClick(R.id.iv_collect)
    public void setCollect() {
        if (MyApp.isLogin()) {
            if (!isCollect) {
                toCollect();
            } else {
                cancelCollect();
            }
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }

    @OnClick(R.id.iv_like)
    public void setLike() {
        if (MyApp.isLogin()) {
            if (!isLike) {
                toLike();
            } else {
                cancelLike();
            }
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }


    //    设置收藏的方法
    private void toCollect() {
        BmobRelation relation = new BmobRelation();
//        将当前用户添加到MActivity表中的collect字段中，表名当前用户收藏了该活动内容
        relation.add(BmobUser.getCurrentUser(User.class));
        article.setCollect(relation);

        if (article.getCollectIdList() != null) {
            collectIdList = article.getCollectIdList();
        }
        collectIdList.add(BmobUser.getCurrentUser().getObjectId());
        article.setCollectIdList(collectIdList);
        ivCollect.setImageResource(R.drawable.ic_favorite_orange_500_24dp);
        isCollect = true;

        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e(TAG, "收藏成功");
                } else {
                    Log.e(TAG, "收藏失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }

    //    取消收藏的方法
    private void cancelCollect() {
        BmobRelation relation = new BmobRelation();
        relation.remove(BmobUser.getCurrentUser(User.class));
        article.setCollect(relation);
        //                    移除当前用户的收藏ID
        if (article.getCollectIdList() != null) {
            collectIdList = article.getCollectIdList();
        }
        collectIdList.remove(user.getObjectId());
        article.setCollectIdList(collectIdList);
        ivCollect.setImageResource(R.drawable.ic_favorite_border_grey_500_24dp);
        isCollect = false;
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e(TAG, "取消收藏成功");
                } else {
                    Log.e(TAG, "取消收藏失败" + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    //设置点赞的方法
    private void toLike() {
        BmobRelation relation = new BmobRelation();
//      将当前用户添加到MActivity表中的like字段值中，表名当前用户喜欢这个帖子
        relation.add(BmobUser.getCurrentUser(User.class));
        article.setLike(relation);

        //                    添加当前用户的ID
        if (article.getLikeIdList() != null) {
            likeIdList = article.getLikeIdList();
        }
        likeIdList.add(user.getObjectId());
        article.setLikeIdList(likeIdList);
        tvLikeNum.setText(likeIdList.size()+"");
        ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
        isLike = true;
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    //queryLikes();
                   Log.e(TAG,"文章点赞成功！");

                } else {
                   Log.e(TAG,"文章点赞失败！"+e.getErrorCode()+e.getMessage());
                }
            }
        });
    }

    //    取消点赞的方法
    private void cancelLike() {
        BmobRelation relation = new BmobRelation();
//      将当前用户移出这个关系，表明当前用户不喜欢这个帖子
        relation.remove(user);
        article.setLike(relation);

        //      移除当前用户的ID
        if (article.getLikeIdList() != null) {
            likeIdList = article.getLikeIdList();
        }
        likeIdList.remove(BmobUser.getCurrentUser(User.class).getObjectId());
        article.setLikeIdList(likeIdList);
        tvLikeNum.setText( likeIdList.size() + "");
        ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
        isLike = false;
        article.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e(TAG,"文章取消点赞成功！");
                } else {
                    Log.e(TAG,"文章取消点赞失败！");
                }
            }
        });
    }


    //    弹出底部的popupwindow
    private void showPopup() {
        llBottom.setVisibility(View.GONE);
        View parent = LayoutInflater.from(this).inflate(R.layout.activity_detail, null);
        View view = LayoutInflater.from(this).inflate(R.layout.comment_view, null);

        etComment = (EditText) view.findViewById(R.id.et_comment_text);
        btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
//      弹出动画
        //   popupWindow.setAnimationStyle();
//      使其聚焦，要想监听菜单控件的时间就必须要调用此方法
        popupWindow.setFocusable(true);
//      设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
//      设置背景，这个是为了点击返回 back 也能使其消失，并且不会影响你的背景
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
//       软键盘不会挡着popupwindow
        popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        设置菜单显示的位置
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
//        监听菜单的关闭事件
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                llBottom.setVisibility(View.VISIBLE);
//                在popupwindow结束后，重新查询评论列表
                queryCommentList();
                closeSoftKeybord(etComment, ArticleDetailActivity.this);
            }
        });
//        监听触屏事件
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });


    }

    //    弹出软键盘的方法
    private void popupInputMethodWindow() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                //哇！别人的代码！这个东西让我找了好久。。。
             /*   scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 0);
                    }
                });*/
            }
        }, 0);
    }

    // 隐藏软键盘的方法

    private void closeSoftKeybord(EditText mEditText, Context mContext) {
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mEditText.getWindowToken(), 0);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                sendComment();
                break;
        }
    }

    private void sendComment() {
        String content = etComment.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToast("评论内容不能为空");
            return;
        }
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setUser(BmobUser.getCurrentUser(User.class));
        comment.setArticle(article);
        comment.setLikeNum(new Integer(0));
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
//                    发送成功后，对话框直接消失
                    popupWindow.dismiss();
                } else {
                    showToast("评论上传失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }
}
