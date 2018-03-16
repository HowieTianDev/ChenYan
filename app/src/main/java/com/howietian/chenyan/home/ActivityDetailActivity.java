package com.howietian.chenyan.home;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
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
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Comment;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ActivityDetailActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.tb_activity)
    Toolbar tbActivity;
    @Bind(R.id.rv_activity_comment)
    RecyclerView commentList;
    @Bind(R.id.sv_activity)
    NestedScrollView scrollView;
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.tv_up_time)
    TextView tvUpTime;
    @Bind(R.id.tv_up_name)
    TextView tvUpName;
    @Bind(R.id.tv_flag)
    TextView tvFlag;
    @Bind(R.id.iv_photo)
    ImageView ivPhoto;
    @Bind(R.id.tv_content)
    TextView tvContent;
    @Bind(R.id.btn_join)
    Button btnJoin;
    @Bind(R.id.tv_comment_num)
    TextView tvCommentNum;
    @Bind(R.id.tv_like_num)
    TextView tvLikeNum;
    @Bind(R.id.tv_deadline)
    TextView tvDeadline;
    @Bind(R.id.tv_comment)
    TextView tvComment;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.iv_collect)
    ImageView ivCollect;
    @Bind(R.id.iv_like)
    ImageView ivLike;


    private static final String TAG = "ActivityDetailActivity";

    //    用来存储喜欢和收藏该帖子的用户的ID，以便更快的初始化
    private ArrayList<String> likeIdList = new ArrayList<>();
    private ArrayList<String> collectIdList = new ArrayList<>();
    private ArrayList<String> joinIdList = new ArrayList<>();

    private CommentAdapter adapter;
    private List<Comment> comments = new ArrayList<>();
    private RecyclerView.LayoutManager manager;
    private MActivity mactivity;
    private Button btnSend;
    private EditText etComment;
    private PopupWindow popupWindow;


    //    是否收藏和点赞的标志
    boolean isCollect = false;
    boolean isLike = false;
    String commentNum = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_detail);
    }

    @Override
    public void init() {
        super.init();
        back();
//        解决scrollview嵌套recyclerview时，界面会默认滚动到recyclerview的第一个item的问题
        /*scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.scrollTo(0, 0);
            }
        });*/
//        nestedScrollview 不需要解决默认滚到recyclerview 第一个item的问题
//  解决nestedscrollview 嵌套 recyclerview 粘滞的问题
        commentList.setNestedScrollingEnabled(false);

        initViews();
        queryCommentList();
        likeIdList = mactivity.getLikeIdList();
        if (likeIdList == null) {
            likeIdList = new ArrayList<>();
        }

        collectIdList = mactivity.getCollectIdList();
        if (collectIdList == null) {
            collectIdList = new ArrayList<>();
        }

        joinIdList = mactivity.getJoinIdList();
        if (joinIdList == null) {
            joinIdList = new ArrayList<>();
        }
    }

    private void back() {
        setSupportActionBar(tbActivity);
        tbActivity.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                commentNum = tvCommentNum.getText().toString();
                intent.putExtra("commentNum", commentNum);
                intent.putStringArrayListExtra("likeIdList", likeIdList);
                intent.putStringArrayListExtra("collectIdList", collectIdList);
                intent.putStringArrayListExtra("joinIdList", joinIdList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    //重写返回事件，将数据返回上一级页面,去掉superonbackpressed(),才能传Intent。。。。
    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        commentNum = tvCommentNum.getText().toString();
        intent.putExtra("commentNum", commentNum);
        intent.putStringArrayListExtra("likeIdList", likeIdList);
        intent.putStringArrayListExtra("collectIdList", collectIdList);
        intent.putStringArrayListExtra("joinIdList", collectIdList);
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initViews() {

//        通过Gson将传递过来的String重新转化为MActivity 对象
        Intent intent = getIntent();
        String msg = intent.getStringExtra(ActivityFragment.FROME_ACTIVITY);
        Gson gson = new Gson();
        mactivity = gson.fromJson(msg, MActivity.class);


        tvUpName.setText(mactivity.getCurrentUser().getNickName());
        tvTitle.setText(mactivity.getTitle());
        tvUpTime.setText(mactivity.getUpTime());

        loadImage(mactivity.getPhoto().getUrl(), ivPhoto);
        tvContent.setText(mactivity.getContent());
        tvCommentNum.setText(mactivity.getCommentNum() + "");
        if (mactivity.getLikeIdList() != null) {
            tvLikeNum.setText(mactivity.getLikeIdList().size() + "");
        }
        tvDeadline.setText(mactivity.getDeadline());

        /**
         * 判断用户是否点赞和收藏，并初始化界面的点赞和收藏的图标
         *
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


        /**
         * 判断是否已报名的标志
         */
        if (MyApp.isLogin()) {
            if (isPoster()) {
                isDeadLine();
                btnJoin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                btnJoin.setText("查看报名情况");
                btnJoin.setClickable(true);
            } else if (mactivity.getJoinIdList() != null) {
                for (String id : mactivity.getJoinIdList()) {
                    if (id.equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                        btnJoin.setText(R.string.joined);
                        btnJoin.setBackground(getResources().getDrawable(R.color.unused));
                        btnJoin.setClickable(false);
                        tvFlag.setText(R.string.joined);
                        break;
                    }
                }
            } else {
                isDeadLine();
            }
        } else {
            isDeadLine();
        }


        manager = new LinearLayoutManager(this);
        adapter = new CommentAdapter(this, comments);
        commentList.setLayoutManager(manager);
        commentList.setHasFixedSize(true);
        commentList.setAdapter(adapter);


    }

    /**
     * 判断是否截止，然后UI的各种操作
     */
    private void isDeadLine() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//            把当前时间转化为字符串
            String dates = dateFormat.format(new Date());
            Date deadline = dateFormat.parse(mactivity.getDeadline());
            Date currentdate = new Date();
            if (currentdate.before(deadline) || dates.equals(mactivity.getDeadline())) {
                tvFlag.setText(R.string.on_join);
            } else {
                tvFlag.setText(R.string.end_join);
                btnJoin.setText(R.string.end_join);
                btnJoin.setBackground(getResources().getDrawable(R.color.unused));
                btnJoin.setClickable(false);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断当前用户是否为发帖人
     *
     * @return
     */
    private boolean isPoster() {
        if (MyApp.isLogin()) {
            if (mactivity.getCurrentUser().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                return true;
            }
        }

        return false;

    }

    //    判断当前用户是否点赞
    private boolean isShowLike() {

        if (mactivity.getLikeIdList() != null && MyApp.isLogin()) {
            if (mactivity.getLikeIdList().contains(BmobUser.getCurrentUser(User.class).getObjectId())) {
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
        if (mactivity.getCollectIdList() != null && MyApp.isLogin()) {
            if (mactivity.getCollectIdList().contains(BmobUser.getCurrentUser(User.class).getObjectId())) {
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
//        查询该帖子的评论列表
        query.addWhereEqualTo("mActivity", mactivity);
        query.order("createdAt");
//        希望同时查询到发消息的人的信息
        query.include("user");
        query.findObjects(new FindListener<Comment>() {
            @Override
            public void done(final List<Comment> list, BmobException e) {
                if (e == null) {
                    comments.clear();
                    comments.addAll(list);
                    adapter.notifyDataSetChanged();
                    tvCommentNum.setText(list.size() + "");
                    mactivity.setCommentNum(new Integer(list.size()));
                    mactivity.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                Log.e(TAG, "评论数目更新成功");
                            } else {
                                Log.e(TAG, "评论数目更新失败" + e.getErrorCode() + e.getMessage());
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
     * 点击报名按钮的事件
     */

    @OnClick(R.id.btn_join)
    public void tojoin() {
        if (MyApp.isLogin()) {
            User user = BmobUser.getCurrentUser(User.class);
            String school = user.getSchool();
            String phone = user.getMobilePhoneNumber();
            String realName = user.getRealName();
            if (isPoster()) {
//           去报名详情界面
                toJoinedDetail();
            } else {
                if (!TextUtils.isEmpty(school) && !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(realName)) {
                    showJoinDialog();
                } else {
                    showToast("请先完善个人信息");
                }
            }
        } else {
            jumpTo(LoginActivity.class, true);
        }

    }

    private void toJoinedDetail() {
        Intent intent = new Intent(this, JoinedUserActivity.class);
        String msg = new Gson().toJson(mactivity, MActivity.class);
        intent.putExtra(Constant.FROM_ACTIVIRY, msg);
        jumpTo(intent, false);
    }

    private void showJoinDialog() {
        User user = BmobUser.getCurrentUser(User.class);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String msg = "姓名：" + user.getRealName() + "\n学校：" + user.getSchool() + "\n联系方式：" + user.getMobilePhoneNumber();
        builder.setTitle("请确认报名信息准确无误");

        builder.setMessage(msg);
        builder.setPositiveButton("是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                join();
            }
        });
        builder.setNegativeButton("否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                builder.create().dismiss();
            }
        });

        builder.create().show();
    }

    /**
     * 具体的报名逻辑
     */
    private void join() {
        BmobRelation relation = new BmobRelation();
        relation.add(BmobUser.getCurrentUser(User.class));
        mactivity.setJoin(relation);

//        添加当前用户的ID
        if (mactivity.getJoinIdList() != null) {
            joinIdList = mactivity.getJoinIdList();
        }
        joinIdList.add(BmobUser.getCurrentUser(User.class).getObjectId());
        mactivity.setJoinIdList(joinIdList);

        mactivity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("报名成功，请及时参加");
                    btnJoin.setText(R.string.joined);
                    btnJoin.setBackground(getResources().getDrawable(R.color.unused));
                    btnJoin.setClickable(false);
                    tvFlag.setText(R.string.joined);
                } else {
                    showToast("报名失败，请稍后重试" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 底部三个按钮的点击事件
     */
    @OnClick(R.id.tv_comment)
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
        mactivity.setCollect(relation);

        if (mactivity.getCollectIdList() != null) {
            collectIdList = mactivity.getCollectIdList();
        }
        collectIdList.add(BmobUser.getCurrentUser(User.class).getObjectId());
        mactivity.setCollectIdList(collectIdList);
        ivCollect.setImageResource(R.drawable.ic_favorite_orange_500_24dp);
        isCollect = true;

        mactivity.update(new UpdateListener() {
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
        mactivity.setCollect(relation);

//                    移除当前用户的收藏ID
        if (mactivity.getCollectIdList() != null) {
            collectIdList = mactivity.getCollectIdList();
        }
        collectIdList.remove(BmobUser.getCurrentUser(User.class).getObjectId());
        mactivity.setCollectIdList(collectIdList);
        ivCollect.setImageResource(R.drawable.ic_favorite_border_grey_500_24dp);
        isCollect = false;

        mactivity.update(new UpdateListener() {
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


    //    设置点赞的方法
    private void toLike() {
        BmobRelation relation = new BmobRelation();
//      将当前用户添加到MActivity表中的like字段值中，表名当前用户喜欢这个帖子
        relation.add(BmobUser.getCurrentUser(User.class));
        mactivity.setLike(relation);

        //添加当前用户的ID
        if (mactivity.getLikeIdList() != null) {
            likeIdList = mactivity.getLikeIdList();
        }

        likeIdList.add(BmobUser.getCurrentUser(User.class).getObjectId());
        mactivity.setLikeIdList(likeIdList);
        tvLikeNum.setText(likeIdList.size() + "");
        ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
        isLike = true;

        mactivity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e(TAG, "文章点赞成功！");
                } else {
                    Log.e(TAG, "文章点赞失败！");
                    showToast("设置点赞失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    //    取消点赞的方法
    private void cancelLike() {
        BmobRelation relation = new BmobRelation();
//      将当前用户移出这个关系，表明当前用户不喜欢这个帖子
        relation.remove(BmobUser.getCurrentUser(User.class));
        mactivity.setLike(relation);

        //      移除当前用户的ID
        if (mactivity.getLikeIdList() != null) {
            likeIdList = mactivity.getLikeIdList();
        }
        likeIdList.remove(BmobUser.getCurrentUser(User.class).getObjectId());
        mactivity.setLikeIdList(likeIdList);
        tvLikeNum.setText(likeIdList.size() + "");
        ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
        isLike = false;

        mactivity.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e(TAG, "取消文章点赞成功！");
                } else {
                    Log.e(TAG, "取消文章点赞失败！");
                    showToast("取消点赞失败！" + e.getMessage() + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 如果查询点赞人员的具体情况，及必须用这个，如果只是查询点赞的个数，小题大做了
     */
//    //    查询点赞的情况
//    private void queryLikes() {
//        BmobQuery<User> query = new BmobQuery<>();
////      根据当前的帖子查询点赞的所有用户
//        query.addWhereRelatedTo("like", new BmobPointer(mactivity));
//        query.findObjects(new FindListener<User>() {
//            @Override
//            public void done(final List<User> list, BmobException e) {
//                if (e == null) {
//
//                    mactivity.setLikeNum(new Integer(list.size()));
//                    mactivity.update(new UpdateListener() {
//                        @Override
//                        public void done(BmobException e) {
//                            if (e == null) {
////                  首先查到当前的点赞个数，同时实时地将点赞数目更新到Comment上面，在所有的准备都完成后，更新UI
//                                tvLikeNum.setText("赞（" + list.size() + "）");
//                                Log.e(TAG, "点赞数目更新成功");
//                            } else {
//                                Log.e(TAG, "点赞数目更新失败" + e.getMessage() + e.getErrorCode());
//                            }
//                        }
//                    });
//
//                } else {
//                    showToast("查询失败" + e.getMessage() + e.getErrorCode());
//                }
//            }
//        });
//    }
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
                hidePopUpWindow();

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

            }
        }, 0);
    }

    //  设置软键盘隐藏
    private void hidePopUpWindow() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    /**
     * 实现具体的点击事件
     *
     * @param view
     */
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
        comment.setmActivity(mactivity);
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
