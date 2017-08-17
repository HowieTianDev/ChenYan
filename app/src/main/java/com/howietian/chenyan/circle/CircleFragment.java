package com.howietian.chenyan.circle;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.solver.SolverVariable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.DynamicAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.personpage.PersonPageActivity;
import com.melnykov.fab.FloatingActionButton;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnLoadmoreListener;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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
    SmartRefreshLayout swipeRefreshLayout;
    @Bind(R.id.appbar)
    AppBarLayout appbarLayout;


    private static final String CIRCLE_FRAGMENT = "circle_fragment";

    private List<Dynamic> dynamicList = new ArrayList<>();
    //    单个Item点赞ID集合，用于初始化界面布局
    private ArrayList<String> likeIdList = new ArrayList<>();
    //    查询到commentMap
    private Map<String, List<DComment>> commentMap = new HashMap<>();
    private DynamicAdapter dynamicAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private int limit = Constant.LIMIT;
    private String lastTime;

    private static final int PULL_REFRESH = 0;
    private static final int LOAD_MORE = 1;
    public static final String TYPE = "type";


    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == DynamicAdapter.REPLY_COMMENT) {

                DComment comment = (DComment) msg.obj;
                int position = msg.arg1;
                if (comment.getAuthor().getObjectId().equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                    return;
                }
                showReplyPopup(comment.getDynamicId(), position, comment.getAuthor());
                popupInputMethodWindow();

            }
        }
    };


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

        swipeRefreshLayout.autoRefresh();

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        bgaBanner.setData(R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4);

        dynamicAdapter = new DynamicAdapter(getContext(), dynamicList, commentMap, mhandler);
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


        bgaBanner.setDelegate(new BGABanner.Delegate() {
            @Override
            public void onBannerItemClick(BGABanner banner, View itemView, Object model, int position) {
                showToast(position + "");
            }
        });

        loadMore();
        refresh();
        comment();
        praise();
        toPersonPage();
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(PULL_REFRESH);
            }
        });
    }

    private void loadMore() {

        swipeRefreshLayout.setOnLoadmoreListener(new OnLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(LOAD_MORE);
            }
        });

// 通过监听AppbarLayout的位置，来控制fab的显示与隐藏
        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean misAppbarExpand = true;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                int scrollRange = appBarLayout.getTotalScrollRange();
                float fraction = 1f * (scrollRange + verticalOffset) / scrollRange;
                if (fraction < 0.1 && misAppbarExpand) {
                    misAppbarExpand = false;
                    fab.hide();
                }
                if (fraction > 0.8 && !misAppbarExpand) {
                    misAppbarExpand = true;
                    fab.show();
                }
            }
        });
    }

    /**
     * 评论功能
     */

    public void comment() {
        dynamicAdapter.setOnCommentListener(new DynamicAdapter.onCommentClickListener() {
            @Override
            public void onClick(int position) {
                if (MyApp.isLogin()) {
                    showPopup(dynamicList.get(position), position);
                    popupInputMethodWindow();
                } else {
                    jumpTo(LoginActivity.class, false);
                }
            }
        });
    }


    /**
     * 点赞事件
     */

    private void praise() {
        dynamicAdapter.setOnPraiseClickListener(new DynamicAdapter.onPraiseClickListener() {
            @Override
            public void onLikeClick(final int position) {
                if (MyApp.isLogin()) {
                    final User currentUser = BmobUser.getCurrentUser(User.class);
                    final Dynamic dynamic = dynamicList.get(position);
                    if (likeIdList != null) {
                        likeIdList.clear();
                    }
                    if (dynamic.getLikeId() != null) {
                        likeIdList = dynamic.getLikeId();
                    }
                    likeIdList.add(currentUser.getObjectId());
                    dynamic.setLikeId(likeIdList);
                    dynamic.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_PRAISE);
                            } else {
                                showToast("点赞更新失败" + e.getMessage() + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    jumpTo(LoginActivity.class, false);
                }
            }

            @Override
            public void onUnLikeClick(final int position) {
                if (MyApp.isLogin()) {
                    final User currentUser = BmobUser.getCurrentUser(User.class);
                    final Dynamic dynamic = dynamicList.get(position);
                    if (likeIdList != null) {
                        likeIdList.clear();
                    }

                    if (dynamic.getLikeId() != null) {
                        likeIdList = dynamic.getLikeId();
                    }
                    likeIdList.remove(currentUser.getObjectId());

                    dynamic.setLikeId(likeIdList);
                    dynamic.update(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_PRAISE);
                            } else {
                                showToast("取消点赞更新失败" + e.getMessage() + e.getErrorCode());
                            }
                        }
                    });
                } else {
                    jumpTo(LoginActivity.class, false);
                }

            }
        });
    }

    /**
     * 点击用户头像跳转个人主页
     */
    private void toPersonPage() {
        dynamicAdapter.setOnAvatarClickListener(new DynamicAdapter.onAvatarClickListener() {
            @Override
            public void onClick(int position) {
                if(MyApp.isLogin()){
                    Dynamic dynamic = dynamicList.get(position);
                    User dynamicAuthor = dynamic.getUser();
                    String userMsg = new Gson().toJson(dynamicAuthor, User.class);
                    Intent intent = new Intent(getContext(), PersonPageActivity.class);
                    intent.putExtra(Constant.TO_PERSON_PAGE, userMsg);
                    jumpTo(intent,false);
                }else{
                    jumpTo(LoginActivity.class,false);
                }

            }
        });
    }

    /**
     * 发布动态跳转
     */

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
                        List<String> dynamicIds = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            dynamicIds.add(list.get(i).getObjectId());
                        }
//   **************************************************************************************
                        BmobQuery<DComment> commentQuery = new BmobQuery<DComment>();
                        commentQuery.addWhereContainedIn("dynamicId", dynamicIds);
                        commentQuery.include("author,reply");
                        commentQuery.findObjects(new FindListener<DComment>() {
                            @Override
                            public void done(List<DComment> list, BmobException e) {
                                if (e == null) {
                                    if (type == PULL_REFRESH) {
                                        commentMap.clear();
                                    }
                                    //    查询到commentMap
//                                        分组算法
                                    Log.e(CIRCLE_FRAGMENT, "评论" + list.toString());
                                    for (DComment comment : list) {
                                        List<DComment> commentList = commentMap.get(comment.getDynamicId());
                                        if (commentList == null) {
                                            commentList = new ArrayList<DComment>();
                                            commentList.add(comment);
                                            commentMap.put(comment.getDynamicId(), commentList);
                                        } else {
                                            commentList.add(comment);
                                        }
                                    }
                                    dynamicAdapter.notifyDataSetChanged();

                                } else {
                                    showToast("查询评论失败" + e.getMessage() + e.getErrorCode());
                                }
                            }
                        });
//           ****************************************************************************************************
                        dynamicList.addAll(list);
                        dynamicAdapter.notifyDataSetChanged();
                        lastTime = dynamicList.get(dynamicList.size() - 1).getCreatedAt();
                        Log.e("HHH", lastTime);
//                        查询到无数据
                        if (type == PULL_REFRESH) {
                            swipeRefreshLayout.finishRefresh();
                        } else {
                            swipeRefreshLayout.finishLoadmore();
                        }
                    } else {
                        if (type == LOAD_MORE) {
                            showToast("没有更多数据了");
                            swipeRefreshLayout.finishLoadmore();
                        } else if (type == PULL_REFRESH) {
                            showToast("服务器没有数据");
                            swipeRefreshLayout.finishRefresh();
                        }
                    }
                } else {
                    showToast("请求服务器异常" + e.getMessage() + "错误代码" + e.getErrorCode());
                    Log.e("HHH", e.getMessage() + "错误代码" + e.getErrorCode());
                    swipeRefreshLayout.finishRefresh();
                }

            }
        });
    }

    // 原创评论的弹出窗口
    private void showPopup(final Dynamic dynamic, final int position) {

        View parent = LayoutInflater.from(getContext()).inflate(R.layout.frament_circle, null);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_view, null);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        final EditText etComment = (EditText) view.findViewById(R.id.et_comment_text);
        Button btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etComment.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    showToast("评论内容不能为空哦");
                    return;
                }
                final DComment dcomment = new DComment();
                dcomment.setContent(content);
                dcomment.setAuthor(BmobUser.getCurrentUser(User.class));
                dcomment.setDynamic(dynamic);
                dcomment.setDynamicId(dynamic.getObjectId());

                dcomment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
//                    发送成功后，对话框直接消失
                            popupWindow.dismiss();
                            List<DComment> commentList = commentMap.get(dynamic.getObjectId());
                            if (commentList == null) {
                                commentList = new ArrayList<DComment>();
                                commentList.add(dcomment);
                                commentMap.put(dynamic.getObjectId(), commentList);
                            } else {
                                commentList.add(dcomment);
                            }

                            Log.e("HHHH", commentList.toString());
                            dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_COMMENT);


                        } else {
                            showToast("评论上传失败" + e.getMessage() + e.getErrorCode());
                        }
                    }
                });
            }
        });

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

//                在popupwindow结束后，重新查询评论列表
                //queryCommentList();
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

    //    评论回复的弹出窗口
    private void showReplyPopup(final String dynamicId, final int position, final User replyUser) {
//        不能回复自己发的评论
        View parent = LayoutInflater.from(getContext()).inflate(R.layout.frament_circle, null);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.comment_view, null);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        final EditText etComment = (EditText) view.findViewById(R.id.et_comment_text);
        etComment.setHint("回复" + replyUser.getNickName() + ":");
        Button btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etComment.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    showToast("评论内容不能为空哦");
                    return;
                }
                final DComment dcomment = new DComment();
                dcomment.setContent(content);
                dcomment.setAuthor(BmobUser.getCurrentUser(User.class));
                dcomment.setReply(replyUser);
                dcomment.setDynamic(null);
                dcomment.setDynamicId(dynamicId);

                dcomment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
//                    发送成功后，对话框直接消失
                            popupWindow.dismiss();
                            List<DComment> commentList = commentMap.get(dynamicId);
                            if (commentList == null) {
                                commentList = new ArrayList<DComment>();
                                commentList.add(dcomment);
                                commentMap.put(dynamicId, commentList);
                            } else {
                                commentList.add(dcomment);
                            }

                            Log.e("HHHH", commentList.toString());
                            dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_COMMENT);


                        } else {
                            showToast("评论上传失败" + e.getMessage() + e.getErrorCode());
                        }
                    }
                });
            }
        });

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

//                在popupwindow结束后，重新查询评论列表
                //queryCommentList();
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
                InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 0);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.cv_complex, R.id.cv_sport, R.id.cv_feeling, R.id.cv_chinese, R.id.cv_match, R.id.cv_star, R.id.cv_art, R.id.cv_animation, R.id.cv_free})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cv_complex:
                toChooseType("综合");
                break;
            case R.id.cv_sport:
                toChooseType("体育");
                break;
            case R.id.cv_feeling:
                toChooseType("情感");
                break;
            case R.id.cv_chinese:
                toChooseType("国学");
                break;
            case R.id.cv_match:
                toChooseType("竞赛");
                break;
            case R.id.cv_star:
                toChooseType("精华");
                break;
            case R.id.cv_art:
                toChooseType("文艺");
                break;
            case R.id.cv_animation:
                toChooseType("动漫");
                break;
            case R.id.cv_free:
                toChooseType("闲情");
                break;
        }
    }

    private void toChooseType(String type) {
        Intent intent = new Intent(getContext(), ChooseCircleActivity.class);
        intent.putExtra(TYPE, type);
        jumpTo(intent, false);
    }
}
