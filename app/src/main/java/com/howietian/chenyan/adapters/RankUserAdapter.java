package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HowieTian on 2017/8/2 0002.
 */

public class RankUserAdapter extends RecyclerView.Adapter<RankUserAdapter.RankUserViewHolder>{
    private Context context;
    private List<User> userList = new ArrayList<>();
    private onItemClickListener mOnItemClickListener;
    public interface onItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.mOnItemClickListener = listener;
    }
    public RankUserAdapter(Context context,List<User> users){
        this.context = context;
        this.userList = users;
    }
    @Override
    public RankUserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RankUserViewHolder holder;
        View view = LayoutInflater.from(context).inflate(R.layout.item_rank_user,parent,false);
        holder = new RankUserViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RankUserViewHolder holder, final int position) {
        User user = userList.get(position);
        holder.nickName.setText(user.getNickName());
        switch (position){
            case 0:
                holder.imageView.setImageResource(R.drawable.cup_1);
                break;
            case 1:
                holder.imageView.setImageResource(R.drawable.cup_2);
                break;
            case 2:
                holder.imageView.setImageResource(R.drawable.cup_3);
                break;
        }
        if(mOnItemClickListener!=null){
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

    static class RankUserViewHolder extends RecyclerView.ViewHolder{
        @Bind(R.id.iv_image)
        ImageView imageView;
        @Bind(R.id.tv_nickName)
        TextView nickName;
        public RankUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
