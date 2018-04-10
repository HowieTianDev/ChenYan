package com.howietian.chenyan.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobUser;

/**
 * Created by 83624 on 2017/cup_7/3.
 */

public class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<MActivity> activityList = new ArrayList<>();
    private onItemClickListener onItemClickListener;

    /**
     * 实现点击回调的接口
     */
    public interface onItemClickListener {
        void onClick(int position);
    }

    public ActivityAdapter(Context context, List<MActivity> list) {
        this.context = context;
        this.activityList = list;
    }

    /**
     * 让其他Activity实现这个接口的方法，给这个接口变量赋值
     *
     * @param listener
     */
    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    //这里根据viewType的不同，返回两种布局
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.item_activity, parent, false);
        NormalViewHolder holder = new NormalViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof NormalViewHolder) {

            final MActivity activity = activityList.get(position);
            if (activity.getPhoto() != null) {
                Glide.with(context).load(activity.getPhoto().getUrl()).into(((NormalViewHolder) holder).image);
            }
            ((NormalViewHolder) holder).title.setText(activity.getTitle());
            Date date = TimeUtil.getSimpleDateFormat(activity.getCreatedAt());
            String time = TimeUtil.getTimeFormatText(date);
            ((NormalViewHolder) holder).time.setText(time + "");
            if (activity.getCommentNum() != null) {
                ((NormalViewHolder) holder).tvComment.setText(activity.getCommentNum().toString());
            }


/**
 * 实现三种报名标志的选择
 */
            // 登陆才考虑已报名
            if (MyApp.isLogin()) {
                if (activity.getJoinIdList() != null) { //有人报名，考虑是否已报名
                    if (activity.getJoinIdList().contains(BmobUser.getCurrentUser().getObjectId())) {
                        ((NormalViewHolder) holder).tvFlag.setText(R.string.joined);
                    }
                }
            }

            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String datestring = dateFormat.format(new Date());
                Date deadline = dateFormat.parse(activity.getDeadline());
                Date currentdate = new Date();

                if (currentdate.before(deadline) || activity.getDeadline().equals(datestring)) {
                    ((NormalViewHolder) holder).tvFlag.setText(R.string.on_join);
                } else {
                    ((NormalViewHolder) holder).tvFlag.setText(R.string.end_join);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }





//        具体实现点击事件
            if (onItemClickListener != null) {
                ((NormalViewHolder) holder).itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onClick(position);
                    }
                });
            }
        }

    }


    @Override
    public int getItemCount() {
        return activityList.size();
    }


    //    normal item 的viewholder
    static class NormalViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.tv_sign)
        TextView tvFlag;
        @Bind(R.id.tv_time)
        TextView time;
        @Bind(R.id.activity_comment)
        TextView tvComment;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
