package com.howietian.chenyan.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Comment;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by 83624 on 2017/7/3.
 */

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.MyViewHolder> {
    private static final String TAG = "Comment";

    private Context context;
    private List<Comment> comments = new ArrayList<>();
    private ArrayList<String> likeIdList = new ArrayList<>();


    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        //    设置点赞的标志
        final boolean[] isLike = {false};
        final Comment comment = comments.get(position);
//        加载头像
        if (comment.getUser().getAvatar() != null) {
            Glide.with(context).load(comment.getUser().getAvatar().getUrl()).into(holder.ivAvatar);
        }


        holder.tvNickName.setText(comment.getUser().getNickName());
        holder.tvTime.setText(comment.getCreatedAt());
        holder.tvContent.setText(comment.getContent());
        if (comment.getLikeIdList() != null) {
            holder.tvLikeNum.setText(comment.getLikeIdList().size() + "");
        }


        if (MyApp.isLogin()) {
            if (comment.getLikeIdList() != null) {
                for (String id : comment.getLikeIdList()) {
                    if (id.equals(BmobUser.getCurrentUser(User.class).getObjectId())) {
                        holder.ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
                        isLike[0] = true;
                        break;
                    } else {
                        holder.ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
                        isLike[0] = false;
                    }
                }
            } else {
                holder.ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
            }

        } else {
            holder.ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
        }

        likeIdList.clear();
        holder.ivLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                首先判断用户是否登录
                if (MyApp.isLogin()) {
                    if (!isLike[0]) {
                        BmobRelation relation = new BmobRelation();
                        relation.add(BmobUser.getCurrentUser(User.class));
                        comment.setLike(relation);

                        if (comment.getLikeIdList() != null) {
                            likeIdList = comment.getLikeIdList();
                        }
                        likeIdList.add(BmobUser.getCurrentUser(User.class).getObjectId());
                        comment.setLikeIdList(likeIdList);

                        comment.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e(TAG, "点赞更新成功");
//                                查询当前评论的点赞数目，并更新 总数量

                                    holder.tvLikeNum.setText(likeIdList.size() + "");
                                    holder.ivLike.setImageResource(R.drawable.ic_thumb_up_orange_500_24dp);
                                    isLike[0] = true;
                                } else {
                                    Log.e(TAG, "点赞更新失败" + e.getErrorCode() + e.getMessage());
                                }
                            }
                        });

                    } else {

                        BmobRelation relation = new BmobRelation();
                        relation.remove(BmobUser.getCurrentUser(User.class));
                        comment.setLike(relation);

                        if (comment.getLikeIdList() != null) {
                            likeIdList = comment.getLikeIdList();
                        }
                        likeIdList.remove(BmobUser.getCurrentUser(User.class).getObjectId());
                        comment.setLikeIdList(likeIdList);

                        comment.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Log.e(TAG, "取消点赞成功");
//                                查询取消点赞后的点赞总数 并将最新数据更新到 likenum中

//                                将该用户的ID，从赞这个评论的list中移出
                                    holder.tvLikeNum.setText(likeIdList.size() + "");
                                    holder.ivLike.setImageResource(R.drawable.ic_thumb_up_grey_500_24dp);
                                    isLike[0] = false;

                                } else {
                                    Log.e(TAG, "取消点赞更新失败" + e.getErrorCode() + e.getMessage());
                                }
                            }
                        });
//
                    }
                } else {
                    Intent intent = new Intent(context, LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        ImageView ivAvatar;
        @Bind(R.id.tv_nickName)
        TextView tvNickName;
        @Bind(R.id.tv_create_time)
        TextView tvTime;
        @Bind(R.id.tv_content)
        TextView tvContent;
        @Bind(R.id.tv_like_num)
        TextView tvLikeNum;
        @Bind(R.id.iv_like)
        ImageView ivLike;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
