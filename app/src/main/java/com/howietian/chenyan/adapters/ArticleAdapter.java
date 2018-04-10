package com.howietian.chenyan.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.Article;
import com.howietian.chenyan.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 83624 on 2017/cup_6/29.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    /**
     * 回调接口
     */
    public interface onItemClickListener {
        void onClick(int position);
    }

    private List<Article> articles = new ArrayList<>();
    private Context context;
    private onItemClickListener onItemClickListener;

    /**
     * 设置回调接口
     *
     * @param listener
     */
    public void setOnItemClickListener(onItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public ArticleAdapter(Context context, List<Article> articles) {
        this.context = context;
        this.articles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
        NormalViewHolder articleViewHolder = new NormalViewHolder(view);
        return articleViewHolder;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof NormalViewHolder) {
            final Article article = articles.get(position);
            //封面图片

            Glide.with(context).load(article.getPhoto().getUrl()).into(((NormalViewHolder) holder).image);

            ((NormalViewHolder) holder).title.setText(article.getTitle());
            ((NormalViewHolder) holder).comment.setText(article.getCommentNum().toString());
            Date date = TimeUtil.getSimpleDateFormat(article.getCreatedAt());
            String time = TimeUtil.getTimeFormatText(date);
            ((NormalViewHolder) holder).time.setText(time + "");


            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
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
        return articles.size();
    }

    static class NormalViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.tv_time)
        TextView time;
        @Bind(R.id.comment)
        TextView comment;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
