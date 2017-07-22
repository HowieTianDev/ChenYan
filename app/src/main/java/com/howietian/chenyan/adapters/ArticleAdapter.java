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

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by 83624 on 2017/6/29.
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

    private static final int NORMAL_TYPE = 0;
    private static final int FOOTER_TYPE = 1;

    private static final int PAGE_ITEM_COUNT = Constant.LIMIT;

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
        if (viewType == NORMAL_TYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_article, parent, false);
            NormalViewHolder articleViewHolder = new NormalViewHolder(view);
            return articleViewHolder;
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.footer_view, parent, false);
            FootViewHolder footer = new FootViewHolder(view);
            return footer;
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof NormalViewHolder) {
            final Article article = articles.get(position);
            //封面图片

            Glide.with(context).load(article.getPhoto().getUrl()).into(((NormalViewHolder) holder).image);

            ((NormalViewHolder) holder).title.setText(article.getTitle());
            ((NormalViewHolder) holder).intro.setText(article.getContent());
            ((NormalViewHolder) holder).comment.setText(article.getCommentNum().toString());
            if(article.getLikeIdList()!=null){
                ((NormalViewHolder) holder).like.setText(article.getLikeIdList().size()+"");
            }


            if (onItemClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onItemClickListener.onClick(position);
                    }
                });
            }

        } else {
            holder.itemView.setVisibility(View.VISIBLE);
            holder.itemView.setPadding(0, 0, 0, 0);
//            当数据源数目小于一页显示的数目时，不显示footer
            if (articles.size() < PAGE_ITEM_COUNT) {
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setPadding(0, -holder.itemView.getHeight(), 0, 0);
            } else {
//                其他情况，让它一秒后隐藏
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        holder.itemView.setVisibility(View.GONE);
                        holder.itemView.setPadding(0, -holder.itemView.getHeight(), 0, 0);

                    }
                }, 1000);
            }
        }


    }

    @Override
    public int getItemCount() {
        return articles.size() == 0 ? 0 : articles.size() + 1;
    }
    // 根据位置的不同，返回不同的View 类型


    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER_TYPE;
        } else {
            return NORMAL_TYPE;
        }
    }

    public class NormalViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;
        @Bind(R.id.intro)
        TextView intro;
        @Bind(R.id.like)
        TextView like;
        @Bind(R.id.comment)
        TextView comment;

        public NormalViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    //    底部footerView的 viewholder
    class FootViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.footer_bar)
        ProgressBar footerBar;
        @Bind(R.id.footer_text)
        TextView footerText;

        public FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


}
