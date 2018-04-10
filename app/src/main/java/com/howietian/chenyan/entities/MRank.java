package com.howietian.chenyan.entities;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 83624 on 2018/3/17 0017.
 */

public class MRank extends BmobObject{
    private String title;
    private String content;
    private BmobFile image;
    private Integer commentNum;
    private String url;

    private BmobRelation like;

    private ArrayList<String> likeIdList;

    public MRank(){}

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

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
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

    public ArrayList<String> getLikeIdList() {
        return likeIdList;
    }

    public void setLikeIdList(ArrayList<String> likeIdList) {
        this.likeIdList = likeIdList;
    }

    @Override
    public String toString() {
        return "MRank{" +
                "title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", image=" + image +
                ", commentNum=" + commentNum +
                ", url='" + url + '\'' +
                ", like=" + like +
                ", likeIdList=" + likeIdList +
                '}';
    }
}
