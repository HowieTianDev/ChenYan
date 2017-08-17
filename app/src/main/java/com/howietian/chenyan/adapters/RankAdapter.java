package com.howietian.chenyan.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.Rank;
import com.howietian.chenyan.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by HowieTian on 2017/8/2 0002.
 */

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {
    private Context context;
    private List<Rank> rankList = new ArrayList<>();
    private int[] images = new int[]{R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5, R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5, R.drawable.banner1, R.drawable.banner2, R.drawable.banner3, R.drawable.banner4, R.drawable.banner5};
    private onItemClickListener mOnItemClickListener;

    public interface onItemClickListener {
        void onClick(int position);
    }

    public void setOnItemClickListener(onItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public RankAdapter(Context context, List<Rank> rankList) {
        this.context = context;
        this.rankList = rankList;
    }

    @Override
    public RankViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RankViewHolder holder;
        View view = LayoutInflater.from(context).inflate(R.layout.item_rank, parent, false);
        holder = new RankViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(RankViewHolder holder, final int position) {
        final Rank rank = rankList.get(position);

       if(rank.getImage()!=null){
           ImageLoader.with(context,rank.getImage().getUrl(),holder.imageView);
       }

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
        return rankList.size();
    }

    static class RankViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_rank)
        ImageView imageView;

        public RankViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
