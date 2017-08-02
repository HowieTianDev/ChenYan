package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * Created by HowieTian on 2017/7/30 0030.
 */

public class FUserAdapter extends RecyclerView.Adapter<FUserAdapter.FMyViewHolder> {
    private Context context;
    private List<User> userList = new ArrayList<>();
    private onItemClickListener mOnItemClickListener;


    public FUserAdapter(Context context,List<User> users){
        this.context = context;
        this.userList = users;
    }
    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public FMyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_fuser, parent, false);
        FMyViewHolder holder = new FMyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FMyViewHolder holder, final int position) {
        final User user = userList.get(position);
        if (user.getAvatar() != null) {
            ImageLoader.with(context, user.getAvatar().getUrl(), holder.avatar);
        }
        holder.tvNickName.setText(user.getNickName());
        holder.tvIntro.setText(user.getIntro());
        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class FMyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_favatar)
        CircleImageView avatar;
        @Bind(R.id.tv_fnickName)
        TextView tvNickName;
        @Bind(R.id.tv_fintro)
        TextView tvIntro;

         FMyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
