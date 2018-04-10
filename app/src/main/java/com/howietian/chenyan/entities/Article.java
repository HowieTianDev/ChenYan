package com.howietian.chenyan.entities;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 83624 on 2017/cup_6/29.
 */

public class Article extends BmobObject{
    private String title;
    private String content;
    private BmobFile photo;

    private Integer commentNum;

    private String url;

    private BmobRelation like;
    private BmobRelation collect;

//    为了更快地初始化详情页，显示是否点赞和收藏
    private ArrayList<String> likeIdList;
    private ArrayList<String> collectIdList;

    private String type;

    public Article() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BmobFile getPhoto() {
        return photo;
    }

    public void setPhoto(BmobFile photo) {
        this.photo = photo;
    }


    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public BmobRelation getLike() {
        return like;
    }

    public void setLike(BmobRelation like) {
        this.like = like;
    }

    public BmobRelation getCollect() {
        return collect;
    }

    public void setCollect(BmobRelation collect) {
        this.collect = collect;
    }

    public ArrayList<String> getLikeIdList() {
        return likeIdList;
    }

    public void setLikeIdList(ArrayList<String> likeIdList) {
        this.likeIdList = likeIdList;
    }

    public ArrayList<String> getCollectIdList() {
        return collectIdList;
    }

    public void setCollectIdList(ArrayList<String> collectIdList) {
        this.collectIdList = collectIdList;
    }

    @Override
    public String toString() {
        return "Article{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", photo=" + photo +
                ", url='" + url + '\'' +
                ", like=" + like +
                ", collect=" + collect +
                ", likeIdList=" + likeIdList +
                ", collectIdList=" + collectIdList +
                '}';
    }
}
