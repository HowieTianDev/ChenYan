package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.Competition;
import com.howietian.chenyan.utils.ImageLoader;
import com.howietian.chenyan.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 83624 on 2018/3/18 0018.
 */

public class CompetitionAdapter extends RecyclerView.Adapter<CompetitionAdapter.CompetitionViewHolder> {
    private List<Competition> competitions = new ArrayList<>();
    private Context context;
    private onItemClickListener onItemClickListener;

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public CompetitionAdapter(Context context, List<Competition> competition) {
        this.context = context;
        this.competitions = competition;
    }


    @Override
    public CompetitionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_m_rank, parent, false);
        CompetitionViewHolder holder = new CompetitionViewHolder(view);
        return holder;

    }

    @Override
    public void onBindViewHolder(CompetitionViewHolder holder, final int position) {
        final Competition competition = competitions.get(position);
        if (competition.getImage() != null) {
            ImageLoader.with(context, competition.getImage().getUrl(), holder.image);
        }
        holder.title.setText(competition.getTitle());
        holder.comment.setText(competition.getCommentNum().toString());
        Date date = TimeUtil.getSimpleDateFormat(competition.getCreatedAt());
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
        return competitions.size();
    }


    static class CompetitionViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.comment)
        TextView comment;
        @Bind(R.id.tv_time)
        TextView tvTime;

        public CompetitionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
