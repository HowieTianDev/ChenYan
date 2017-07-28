package com.howietian.chenyan.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.views.ClickShowMoreLayout;
import com.howietian.chenyan.views.CommentWidget;
import com.howietian.chenyan.views.PraiseWidget;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 83624 on 2017/7/20.
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.DMyViewHolder> {
    private Context context;

    private List<Dynamic> dynamicList = new ArrayList<>();

    //   评论列表Map
    private Map<String, List<DComment>> commentMap = new HashMap<>();
    private List<User> users = new ArrayList<>();
    private onCommentClickListener mOnCommentListener;
    private onPraiseClickListener mOnPraiseClikcListener;

    public static final int REFRESH_PRAISE = 0;
    public static final int REFRESH_COMMENT = 1;
    public static final int REPLY_COMMENT = 2;

    private Handler handler;


    public DynamicAdapter(Context context, List<Dynamic> list, Map<String, List<DComment>> commentMap, Handler handler) {
        this.context = context;
        this.dynamicList = list;
        this.commentMap = commentMap;
        this.handler = handler;
    }


    //    点击评论的接口
    public interface onCommentClickListener {
        void onClick(int position);
    }

    public void setOnCommentListener(onCommentClickListener listener) {
        this.mOnCommentListener = listener;
    }

    //  点赞的接口
    public interface onPraiseClickListener {
        void onLikeClick(int position);

        void onUnLikeClick(int position);
    }

    public void setOnPraiseClickListener(onPraiseClickListener listener) {
        this.mOnPraiseClikcListener = listener;
    }

    @Override
    public DMyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dynamic_empty_content, parent, false);
        DMyViewHolder holder = new DMyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DMyViewHolder holder, int position) {
//        不用这个方法
    }

    @Override
    public void onBindViewHolder(DMyViewHolder holder, final int position, List<Object> payloads) {
        final Dynamic dynamic = dynamicList.get(position);

        List<DComment> commentList = commentMap.get(dynamic.getObjectId());
        if (commentList == null) {
            holder.commentAndPraiseLayout.setVisibility(View.GONE);
        } else {
            holder.commentAndPraiseLayout.setVisibility(View.VISIBLE);
        }
        if (payloads.isEmpty()) {
            holder.nick.setText(dynamic.getUser().getNickName());
            if (dynamic.getUser().getAvatar() != null) {
                Glide.with(context).load(dynamic.getUser().getAvatar().getUrl()).into(holder.avatar);
            }
            holder.content.setText(dynamic.getContent());
            holder.time.setText(dynamic.getCreatedAt());
            if (dynamic.getLikeId() != null) {
                holder.tvLikeNum.setText(dynamic.getLikeId().size() + "");
            } else {
                holder.tvLikeNum.setText(0 + "");
            }
            if (commentList != null) {
                holder.commentLayout.setVisibility(View.VISIBLE);
                addCommentWidget(commentList, holder, position);
            }
            if (users != null) {
                holder.praiseWidget.setDatas(users);
            }
            ArrayList<ImageInfo> imageInfoList = new ArrayList<>();
            if (dynamic.getImageUrls() != null) {
                for (String url : dynamic.getImageUrls()) {
                    ImageInfo info = new ImageInfo();
                    info.setThumbnailUrl(url);
                    info.setBigImageUrl(url);
                    imageInfoList.add(info);
                }
            }
            holder.nineGridView.setAdapter(new NineGridViewClickAdapter(context, imageInfoList));
            /**
             * 初始化时，判断点赞标志
             */
            if (MyApp.isLogin()) {
                if (dynamic.getLikeId() != null) {
                    for (String id : dynamic.getLikeId()) {
                        if (id.equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                            holder.like.setLiked(true);
                        } else {
                            holder.like.setLiked(false);
                        }
                    }
                }
            }

            if (dynamic.getLikeId() == null) {
                holder.like.setLiked(false);
            }
        } else {
            int type = (int) payloads.get(0);
            switch (type) {
                case REFRESH_PRAISE:
                    holder.tvLikeNum.setText(dynamic.getLikeId().size() + "");
                    break;
                case REFRESH_COMMENT:
                    addCommentWidget(commentList, holder, position);
                    break;
            }
        }

        /**
         * 评论事件
         */
        if (mOnCommentListener != null) {
            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnCommentListener.onClick(position);
                }
            });
        }
        /**
         * 点赞事件
         */
        if (mOnPraiseClikcListener != null) {
            holder.like.setOnLikeListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    mOnPraiseClikcListener.onLikeClick(position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    mOnPraiseClikcListener.onUnLikeClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return dynamicList.size();
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
        @Bind(R.id.tv_like_num)
        TextView tvLikeNum;
        @Bind(R.id.nineGridView)
        NineGridView nineGridView;

        public DMyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private void addCommentWidget(final List<DComment> commentList, final DMyViewHolder holder, final int position) {
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
            final DComment dComment = commentList.get(n);
            if (commentWidget != null) {
                commentWidget.setCommentText(commentList.get(n));
                //                实现点击事件
                commentWidget.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (MyApp.isLogin()) {
                            Message message = handler.obtainMessage();
                            message.what = REPLY_COMMENT;
                            message.obj = dComment;
                            message.arg1 = position;
                            handler.sendMessage(message);
                        } else {
                            Intent intent = new Intent(context, LoginActivity.class);
                            context.startActivity(intent);
                        }

                    }
                });
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
////           点赞为空，取消点赞控件的可见性
//            if (data.getLikeId() == null || data.getLikeId().size() == 0) {
//                holder.praiseWidget.setVisibility(View.GONE);
//            } else {
//                holder.praiseWidget.setVisibility(View.VISIBLE);
//            }
//           评论为空，取消评论控件的可见性
            if (data.getCommentNum() == null) {
                holder.commentLayout.setVisibility(View.GONE);
            } else {
                holder.commentLayout.setVisibility(View.VISIBLE);

            }
        }
    }


}
