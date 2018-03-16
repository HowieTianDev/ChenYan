package com.howietian.chenyan.entities;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 83624 on 2017/cup_7/20.
 */

public class Dynamic extends BmobObject {
    private User user;
    private String content;
    private ArrayList<String> likeId;
    private BmobRelation like;
    private String type;
    //    这个东西用来做判定评论框是否可见
    private Integer commentNum;
    private List<String> imageUrls;

    public Dynamic() {
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<String> getLikeId() {
        return likeId;
    }

    public void setLikeId(ArrayList<String> likeId) {
        this.likeId = likeId;
    }

    public BmobRelation getLike() {
        return like;
    }

    public void setLike(BmobRelation like) {
        this.like = like;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }


    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    @Override
    public String toString() {
        return "Dynamic{" +
                "user=" + user +
                ", content='" + content + '\'' +
                ", likeId=" + likeId +
                ", like=" + like +
                ", type='" + type + '\'' +
                ", commentNum=" + commentNum +
                ", imageUrls=" + imageUrls +
                '}';
    }
}
