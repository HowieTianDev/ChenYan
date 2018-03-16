package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 83624 on 2017/cup_7/12.
 */

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private List<User> users = new ArrayList<>();
    private Context context;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener){
        this.onItemClickListener = listener;
    }
    public UserAdapter(Context context,List<User> users){
        this.context = context;
        this.users = users;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        User user = users.get(position);
        if(user.getAvatar()!=null){
            Glide.with(context).load(user.getAvatar().getUrl()).into(holder.avatar);
        }
        holder.realName.setText(user.getRealName());
        holder.school.setText(user.getSchool());
        holder.phone.setText(user.getMobilePhoneNumber());

        if(onItemClickListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_avatar)
        CircleImageView avatar;
        @Bind(R.id.tv_realName)
        TextView realName;
        @Bind(R.id.tv_school)
        TextView school;
        @Bind(R.id.tv_phone)
        TextView phone;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
