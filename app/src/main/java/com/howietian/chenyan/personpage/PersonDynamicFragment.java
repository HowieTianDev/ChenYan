package com.howietian.chenyan.personpage;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonDynamicFragment extends BaseFragment {
    public static String TAG = "person_dynamic_fragment";
    @Bind(R.id.rv_dynamic)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private User currentUser;
    private List<Dynamic> dynamicList = new ArrayList<>();
    //    单个Item点赞ID集合，用于初始化界面布局
    private ArrayList<String> likeIdList = new ArrayList<>();
    //    查询到commentMap
    private Map<String, List<DComment>> commentMap = new HashMap<>();
    private DynamicAdapter dynamicAdapter;
    private RecyclerView.LayoutManager layoutManager;

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

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public PersonDynamicFragment() {
        // Required empty public constructor
    }

    public static PersonDynamicFragment newInstance(String args) {
        PersonDynamicFragment fragment = new PersonDynamicFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_person_dynamic, container, false);
    }


    @Override
    public void init() {
        super.init();
        initDatas();
        initListeners();
    }

    private void initDatas() {
        swipeRefreshLayout.setRefreshing(true);
        queryPersonDynamic();
        dynamicAdapter = new DynamicAdapter(getContext(), dynamicList, commentMap, mhandler);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(dynamicAdapter);
        recyclerView.setHasFixedSize(true);
    }

    private void initListeners() {
        refresh();
        comment();
        praise();
        toPersonPage();
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryPersonDynamic();
            }
        });
    }

    private void comment() {
        dynamicAdapter.setOnCommentListener(new DynamicAdapter.onCommentClickListener() {
            @Override
            public void onClick(int position) {
                if (MyApp.isLogin()) {
                    showPopup(dynamicList.get(position), position);
                    popupInputMethodWindow();
                } else {
                    jumpTo(LoginActivity.class, true);
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
                    ArrayList<String> mLikeIdList = new ArrayList<>();
                    final User currentUser = BmobUser.getCurrentUser(User.class);
                    final Dynamic dynamic = dynamicList.get(position);

                    if (dynamic.getLikeId() != null) {
                        mLikeIdList.addAll(dynamic.getLikeId());
                    }
                    if (!mLikeIdList.contains(currentUser.getObjectId())) {
                        mLikeIdList.add(currentUser.getObjectId());
                        dynamic.setLikeId(mLikeIdList);
                        dynamicList.set(position, dynamic);
                        dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_PRAISE);
                        dynamic.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e("动态", "点赞成功！");
                                } else {
                                    showToast("点赞更新失败" + e.getMessage() + e.getErrorCode());
                                }
                            }
                        });
                    }

                } else {
                    jumpTo(LoginActivity.class, true);
                }
            }

            @Override
            public void onUnLikeClick(final int position) {
                if (MyApp.isLogin()) {
                    ArrayList<String> mLikeIdList = new ArrayList<>();
                    final User currentUser = BmobUser.getCurrentUser(User.class);
                    final Dynamic dynamic = dynamicList.get(position);


                    if (dynamic.getLikeId() != null) {
                        mLikeIdList.addAll(dynamic.getLikeId());
                    }

                    if (mLikeIdList.contains(currentUser.getObjectId())) {
                        mLikeIdList.remove(currentUser.getObjectId());

                        dynamic.setLikeId(mLikeIdList);
                        dynamicList.set(position,dynamic);
                        dynamicAdapter.notifyItemChanged(position, DynamicAdapter.REFRESH_PRAISE);
                        dynamic.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e("动态", "取消点赞成功！");
                                } else {
                                    showToast("取消点赞更新失败" + e.getMessage() + e.getErrorCode());
                                }
                            }
                        });
                    }

                } else {
                    jumpTo(LoginActivity.class, true);
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
                if (MyApp.isLogin()) {
                    Dynamic dynamic = dynamicList.get(position);
                    User dynamicAuthor = dynamic.getUser();
                    String userMsg = new Gson().toJson(dynamicAuthor, User.class);
                    Intent intent = new Intent(getContext(), PersonPageActivity.class);
                    intent.putExtra(Constant.TO_PERSON_PAGE, userMsg);
                    jumpTo(intent, false);
                } else {
                    jumpTo(LoginActivity.class, true);
                }

            }
        });
    }

    private void queryPersonDynamic() {
        BmobQuery<Dynamic> query = new BmobQuery<>();
        query.include("user");
        query.order("-createdAt");
        query.addWhereEqualTo("user", currentUser);
        query.findObjects(new FindListener<Dynamic>() {
            @Override
            public void done(List<Dynamic> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if(dynamicList!=null){
                            dynamicList.clear();
                        }
                        List<String> dynamicIds = new ArrayList<String>();
                        for (int i = 0; i < list.size(); i++) {
                            dynamicIds.add(list.get(i).getObjectId());
                        }
                        BmobQuery<DComment> commentQuery = new BmobQuery<DComment>();
                        commentQuery.addWhereContainedIn("dynamicId", dynamicIds);
                        commentQuery.include("author,reply");
                        commentQuery.findObjects(new FindListener<DComment>() {
                            @Override
                            public void done(List<DComment> list, BmobException e) {
                                if (e == null) {
                                    commentMap.clear();
                                    //    查询到commentMap
//                                        分组算法
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
                                    swipeRefreshLayout.setRefreshing(false);

                                } else {
                                    showToast("查询评论失败" + e.getMessage() + e.getErrorCode());
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        });
                        dynamicList.addAll(list);
                        dynamicAdapter.notifyDataSetChanged();
                    } else {
                        showToast(getString(R.string.no_data_dynamic));
                        swipeRefreshLayout.setRefreshing(false);
                    }
                } else {
                    showToast("请求服务器失败，请稍后再试" + e.getMessage() + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
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
}
