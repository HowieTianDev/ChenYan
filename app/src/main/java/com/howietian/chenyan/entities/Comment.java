package com.howietian.chenyan.entities;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 83624 on 2017/cup_7/3.
 */

public class Comment extends BmobObject {
    private User user;
//    这个可能是文章ID或是活动ID
    private MActivity mActivity;
    private Article article;
    private String content;
    private Integer likeNum;
    private BmobRelation like;

// 喜欢该评论的用户ID集合，用来判断当前用户是否点赞
    private ArrayList<String> likeIdList;

    public Comment() {
    }

    public Comment(User user, MActivity mActivity, Article article, String content, Integer likeNum, BmobRelation like, ArrayList<String> likeIdList) {
        this.user = user;
        this.mActivity = mActivity;
        this.article = article;
        this.content = content;
        this.likeNum = likeNum;
        this.like = like;
        this.likeIdList = likeIdList;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MActivity getmActivity() {
        return mActivity;
    }

    public void setmActivity(MActivity mActivity) {
        this.mActivity = mActivity;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public BmobRelation getLike() {
        return like;
    }

    public void setLike(BmobRelation like) {
        this.like = like;
    }

    public ArrayList<String> getLikeIdList() {
        return likeIdList;
    }

    public void setLikeIdList(ArrayList<String> likeIdList) {
        this.likeIdList = likeIdList;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "user=" + user +
                ", mActivity=" + mActivity +
                ", article=" + article +
                ", content='" + content + '\'' +
                ", likeNum=" + likeNum +
                ", like=" + like +
                ", likeIdList=" + likeIdList +
                '}';
    }
}
