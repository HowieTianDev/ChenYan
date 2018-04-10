package com.howietian.chenyan.home;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.CommentAdapter;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.entities.Comment;
import com.howietian.chenyan.entities.MRank;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class MRankDetailActivity extends BaseActivity implements View.OnClickListener {


    @Bind(R.id.tb_rank)
    Toolbar tbRank;
    @Bind(R.id.webView_article)
    WebView webViewRank;
    @Bind(R.id.tv_comment_num)
    TextView tvCommentNum;
    @Bind(R.id.tv_like_num)
    TextView tvLikeNum;
    @Bind(R.id.rv_article_comment)
    RecyclerView rvRankComment;
    @Bind(R.id.tv_send_comment)
    TextView tvSendComment;
    @Bind(R.id.iv_like)
    ImageView ivLike;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;

    private static final String TAG = "MRank";
    private ArrayList<String> likeIdList = new ArrayList<>();

    private List<Comment> comments = new ArrayList<>();
    private CommentAdapter adapter;
    private RecyclerView.LayoutManager manager;

    private MRank rank;
    private ImageView ivSend;
    private EditText etComment;
    private PopupWindow popupWindow;

    //    全局的用户对象
    final User user = BmobUser.getCurrentUser(User.class);

    boolean isLike = false;
    String commentNum = "";

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_mrank_detail);
    }

    @Override
    public void init() {
        super.init();

        back();

        rvRankComment.setNestedScrollingEnabled(false);
        initDatas();

        queryCommentList();
        likeIdList = rank.getLikeIdList();
        if (likeIdList == null) {
            likeIdList = new ArrayList<>();
        }
    }


    //返回按钮,将是否点赞、收藏、评论数量返回上一级页面
    private void back() {
        setSupportActionBar(tbRank);
        tbRank.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                commentNum = tvCommentNum.getText().toString();
                intent.putExtra("commentNum",commentNum);
                intent.putStringArrayListExtra("likeIdList",likeIdList);
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
        setResult(RESULT_OK,intent);
        finish();
    }


    private void initDatas() {
        Intent intent = getIntent();
        String msg = intent.getStringExtra(RankFragment.FROM_MRANK);
        rank = new Gson().fromJson(msg, MRank.class);

        webViewRank.loadUrl(rank.getUrl());
        // 设置webview只能用内置浏览器打开
        WebSettings wSet = webViewRank.getSettings();
        wSet.setJavaScriptEnabled(true);
        webViewRank.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) { //  重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
                view.loadUrl(url);
                return true;
            }
        });

        if (rank.getCommentNum() != null) {
            tvCommentNum.setText(rank.getCommentNum() + "");
        }
        if (rank.getLikeIdList() != null) {
            tvLikeNum.setText(rank.getLikeIdList().size() + "");
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

        manager = new LinearLayoutManager(this);
        adapter = new CommentAdapter(this, comments);
        rvRankComment.setLayoutManager(manager);
        rvRankComment.setHasFixedSize(true);
        rvRankComment.setAdapter(adapter);
    }


    //    判断当前用户是否点赞
    private boolean isShowLike() {
        if (rank.getLikeIdList() != null && MyApp.isLogin()) {
            if (rank.getLikeIdList().contains(BmobUser.getCurrentUser(User.class).getObjectId())) {
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
        query.addWhereEqualTo("mRank", rank);
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
                    rank.setCommentNum(list.size());
                    tvCommentNum.setText( list.size() +"");
                    rank.update(new UpdateListener() {
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


    //设置点赞的方法
    private void toLike() {
        BmobRelation relation = new BmobRelation();
//      将当前用户添加到MActivity表中的like字段值中，表名当前用户喜欢这个帖子
        relation.add(BmobUser.getCurrentUser(User.class));
        rank.setLike(relation);

        //                    添加当前用户的ID
        if (rank.getLikeIdList() != null) {
            likeIdList = rank.getLikeIdList();
        }
        likeIdList.add(user.getObjectId());
        rank.setLikeIdList(likeIdList);
        tvLikeNum.setText(likeIdList.size()+"");
        ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
        isLike = true;
        rank.update(new UpdateListener() {
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
        rank.setLike(relation);

        //      移除当前用户的ID
        if (rank.getLikeIdList() != null) {
            likeIdList = rank.getLikeIdList();
        }
        likeIdList.remove(BmobUser.getCurrentUser(User.class).getObjectId());
        rank.setLikeIdList(likeIdList);
        tvLikeNum.setText( likeIdList.size() + "");
        ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
        isLike = false;
        rank.update(new UpdateListener() {
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
        ivSend = (ImageView) view.findViewById(R.id.btn_send);
        ivSend.setOnClickListener(this);

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
                closeSoftKeybord(etComment, MRankDetailActivity.this);
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
        comment.setmRank(rank);
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
