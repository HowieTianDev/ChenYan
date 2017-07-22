package com.howietian.chenyan.adapters;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.howietian.chenyan.R;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.Comment;
import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.views.ClickShowMoreLayout;
import com.howietian.chenyan.views.CommentWidget;
import com.howietian.chenyan.views.PraiseWidget;
import com.like.LikeButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 83624 on 2017/7/20.
 */

public class DynamicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Dynamic> dynamicList = new ArrayList<>();
    private List<DComment> dComments = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private ArrayList<String> likeId = new ArrayList<>();

    private static final int NORMAL_TYPE = 0;
    private static final int FOOTER_TYPE = 1;

    private static final int PAGE_ITEM_COUNT = Constant.LIMIT;

    public DynamicAdapter(Context context, List<Dynamic> list) {
        this.context = context;
        this.dynamicList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == NORMAL_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.dynamic_empty_content, parent, false);
            DMyViewHolder holder = new DMyViewHolder(view);
            return holder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            FootViewHolder footer = new FootViewHolder(view);
            return footer;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {


//        dynamic.setCommentNum(new Integer(1));
//        dynamic.setLikeId(likeId);



        if (holder instanceof DMyViewHolder) {
            final Dynamic dynamic = dynamicList.get(position);
            setCommentPraiseLayoutVisibility(dynamic, ((DMyViewHolder) holder));
            addCommentWidget(dComments, ((DMyViewHolder) holder));
            ((DMyViewHolder) holder).nick.setText(dynamic.getUser().getNickName());
            ((DMyViewHolder) holder).avatar.setImageResource(R.drawable.banner1);
            ((DMyViewHolder) holder).content.setText(dynamic.getContent());
            ((DMyViewHolder) holder).time.setText("HHHHH");
            ((DMyViewHolder) holder).praiseWidget.setDatas(users);
            ((DMyViewHolder) holder).comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopup(dynamic);
                    popupInputMethodWindow();
                }
            });
        } else {

            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setPadding(0, 0, 0, 0);
            if (dynamicList.size() < PAGE_ITEM_COUNT) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setPadding(0, -holder.itemView.getHeight(), 0, 0);
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setPadding(0, -holder.itemView.getHeight(), 0, 0);

                    }
                }, 1000);
            }


        }


    }

    @Override
    public int getItemCount() {
        return dynamicList.size() == 0 ? 0 : dynamicList.size() + 1;


    }

    //    根据位置的不同，返回不同的View类型
    @Override
    public int getItemViewType(int position) {

        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return NORMAL_TYPE;
        }

    }

    static class DMyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.avatar)
        CircleImageView avatar;
        @Bind(R.id.nick)
        TextView nick;
        @Bind(R.id.tv_create_time)
        TextView time;
        @Bind(R.id.item_text_field)
        ClickShowMoreLayout content;

        @Bind(R.id.comment_layout)
        LinearLayout commentLayout;
        @Bind(R.id.comment_praise_layout)
        LinearLayout commentAndPraiseLayout;
        @Bind(R.id.divider)
        View divider;
        @Bind(R.id.praise)
        PraiseWidget praiseWidget;

        @Bind(R.id.like)
        LikeButton like;
        @Bind(R.id.comment)
        LikeButton comment;

        public DMyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    //    底部footerView的 viewholder
    class FootViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.footer_bar)
        ProgressBar footerBar;
        @Bind(R.id.footer_text)
        TextView footerText;

        public FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void addCommentWidget(List<DComment> commentList, DMyViewHolder holder) {
        if (commentList == null || commentList.size() == 0) {
            return;
        }
        /**
         * 优化方案
         * 滑动的时候必须要removeView或者addView
         * 但为了性能提高，不可以直接removeAllViews
         * 于是采取以下方案
         *      根据现有的view进行remove/add差额
         *      然后统一设置
         */
        final int childCount = holder.commentLayout.getChildCount();
        if (childCount < commentList.size()) {
//            若当前的view少于list的长度，则add相差的view
            int subCount = commentList.size() - childCount;
            for (int i = 0; i < subCount; i++) {
                CommentWidget commentWidget = new CommentWidget(context);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.topMargin = 1;
                params.bottomMargin = 1;
                commentWidget.setLayoutParams(params);
                commentWidget.setLineSpacing(4, 1);
//                实现点击事件
//                commentWidget.setOnClickListener(this);
//                commentWidget.setOnLongClickListener(this);
                holder.commentLayout.addView(commentWidget);
            }
//            若当前的View 多于 list的长度，则remove相差的view
        } else if (childCount > commentList.size()) {
            holder.commentLayout.removeViews(commentList.size(), childCount - commentList.size());
        }
//        最终，不用重新removeall或者addAll 所有的CommentWidget,只要修修补补就可以了
//        然后把数据绑定好，即可
        for (int n = 0; n < commentList.size(); n++) {
            CommentWidget commentWidget = (CommentWidget) holder.commentLayout.getChildAt(n);
            if (commentWidget != null) {
                commentWidget.setCommentText(commentList.get(n));
            }

        }
    }

    /**
     * 控件的可视性判定
     */
    private void setCommentPraiseLayoutVisibility(Dynamic data, DMyViewHolder holder) {
        if ((data.getLikeId() == null || data.getLikeId().size() == 0) && data.getCommentNum() == null) {
//            全空，都不显示
            holder.commentAndPraiseLayout.setVisibility(View.GONE);
        } else {
//            某项不空，则展示layout
            holder.commentAndPraiseLayout.setVisibility(View.VISIBLE);
            if (data.getCommentNum() == null || data.getLikeId() == null || data.getLikeId().size() == 0) {
                holder.divider.setVisibility(View.GONE);
            } else {
                holder.divider.setVisibility(View.VISIBLE);
            }

//           点赞为空，取消点赞控件的可见性
            if (data.getLikeId() == null || data.getLikeId().size() == 0) {
                holder.praiseWidget.setVisibility(View.GONE);
            } else {
                holder.praiseWidget.setVisibility(View.VISIBLE);
            }
//           评论为空，取消评论控件的可见性
            if (data.getCommentNum() == null) {
                holder.commentAndPraiseLayout.setVisibility(View.GONE);
            } else {
                holder.commentAndPraiseLayout.setVisibility(View.VISIBLE);
            }
        }
    }


    private void showPopup(final Dynamic dynamic) {

        View parent = LayoutInflater.from(context).inflate(R.layout.activity_detail, null);
        View view = LayoutInflater.from(context).inflate(R.layout.comment_view, null);

        final PopupWindow popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
        final EditText etComment = (EditText) view.findViewById(R.id.et_comment_text);
        Button btnSend = (Button) view.findViewById(R.id.btn_send);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = etComment.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(context, "评论内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                DComment dcomment = new DComment();
                dcomment.setContent(content);
                dcomment.setAuthor(BmobUser.getCurrentUser(User.class));
                dcomment.setDynamic(dynamic);
                dcomment.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if (e == null) {
//                    发送成功后，对话框直接消失
                            popupWindow.dismiss();
                        } else {
                            Toast.makeText(context, "评论上传失败" + e.getMessage() + e.getErrorCode(), Toast.LENGTH_SHORT).show();
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
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
//                scrollView.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        scrollView.scrollTo(0, 0);
//                    }
//                });
            }
        }, 0);
    }


}
