package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.MRank;
import com.howietian.chenyan.utils.ImageLoader;
import com.howietian.chenyan.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 83624 on 2018/3/17 0017.
 */

public class MRankAdapter extends RecyclerView.Adapter<MRankAdapter.RankViewHolder> {
    private List<MRank> ranks = new ArrayList<>();
    private Context context;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public MRankAdapter(Context context, List<MRank> mRanks) {
        this.context = context;
        this.ranks = mRanks;
    }


    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_m_rank, parent, false);
        RankViewHolder holder = new RankViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(RankViewHolder holder, final int position) {
        final MRank mRank = ranks.get(position);
        if (mRank.getImage() != null) {
            ImageLoader.with(context, mRank.getImage().getUrl(), holder.image);
        }
        holder.title.setText(mRank.getTitle());
        holder.comment.setText(mRank.getCommentNum().toString());
        Date date = TimeUtil.getSimpleDateFormat(mRank.getCreatedAt());
        String time = TimeUtil.getTimeFormatText(date);
        holder.tvTime.setText(time + "");

        if (onItemClickListener != null) {
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
        return ranks.size();
    }

    static class RankViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.tv_time)
        TextView tvTime;

        public RankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
