package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 83624 on 2018/2/10 0010.
 */

public class NotifyAdapter extends RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder> {

    private List<User> userList = new ArrayList<>();
    private Context context;
    private onInfoClickListener onInfoClickListener;
    private onAgreeClickListener onAgreeClickListener;
    private onDisagreeClickListener onDisagreeClickListener;

    public NotifyAdapter(List<User> users, Context context) {
        this.userList = users;
        this.context = context;
    }

    public interface onInfoClickListener {
        void onClick(int position);
    }

    public void setOnInfoClickListener(onInfoClickListener listener) {
        this.onInfoClickListener = listener;
    }

    public interface onAgreeClickListener {
        void onClick(int position);
    }

    public void setOnAgreeClickListener(onAgreeClickListener listener) {
        this.onAgreeClickListener = listener;
    }

    public interface onDisagreeClickListener {
        void onClick(int position);
    }

    public void setOnDisagreeClickListener(onDisagreeClickListener listener) {
        this.onDisagreeClickListener = listener;
    }


    @Override
    public NotifyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        NotifyViewHolder viewHolder = null;
        View view = LayoutInflater.from(context).inflate(R.layout.item_notify, parent, false);
        viewHolder = new NotifyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NotifyViewHolder holder, final int position) {
        User user = userList.get(position);
        if (user.getAvatar() != null) {
            ImageLoader.with(context, user.getAvatar().getUrl(), holder.ivAvatar);
        }
        holder.tvNick.setText(user.getNickName());
        holder.tvIntro.setText(user.getIntro());

        if (onInfoClickListener != null) {
            holder.llMyInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onInfoClickListener.onClick(position);
                }
            });
        }
        if (onAgreeClickListener != null) {
            holder.btnAgree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onAgreeClickListener.onClick(position);
                }
            });
        }
        if (onDisagreeClickListener != null) {
            holder.btnDisagree.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onDisagreeClickListener.onClick(position);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class NotifyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        CircleImageView ivAvatar;
        @Bind(R.id.tv_nick)
        TextView tvNick;
        @Bind(R.id.tv_intro)
        TextView tvIntro;
        @Bind(R.id.ll_myInfo)
        LinearLayout llMyInfo;
        @Bind(R.id.btn_agree)
        Button btnAgree;
        @Bind(R.id.btn_disagree)
        Button btnDisagree;


        public NotifyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
