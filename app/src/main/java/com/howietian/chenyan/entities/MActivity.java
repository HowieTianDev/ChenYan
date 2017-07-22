package com.howietian.chenyan.entities;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by 83624 on 2017/6/29.
 */

public class MActivity extends BmobObject {
    private User currentUser;
    private String title;
    private String content;
    private BmobFile photo;
    private Integer likeNum;
    private Integer commentNum;
    private String deadline;
    private String upTime;
    private BmobRelation like;
    private BmobRelation collect;
    private BmobRelation join;

//    为了更快地初始化详情页，显示是否点赞和收藏和是否报名

    private ArrayList<String> likeIdList;
    private ArrayList<String> collectIdList;
    private ArrayList<String> joinIdList;

    public MActivity() {
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
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

    public Integer getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(Integer likeNum) {
        this.likeNum = likeNum;
    }

    public Integer getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(Integer commentNum) {
        this.commentNum = commentNum;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getUpTime() {
        return upTime;
    }

    public void setUpTime(String upTime) {
        this.upTime = upTime;
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

    public BmobRelation getJoin() {
        return join;
    }

    public void setJoin(BmobRelation join) {
        this.join = join;
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

    public ArrayList<String> getJoinIdList() {
        return joinIdList;
    }

    public void setJoinIdList(ArrayList<String> joinIdList) {
        this.joinIdList = joinIdList;
    }


    @Override
    public String toString() {
        return "MActivity{" +
                "currentUser=" + currentUser +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", photo=" + photo +
                ", likeNum=" + likeNum +
                ", commentNum=" + commentNum +
                ", deadline='" + deadline + '\'' +
                ", upTime='" + upTime + '\'' +
                ", like=" + like +
                ", collect=" + collect +
                ", join=" + join +
                ", likeIdList=" + likeIdList +
                ", collectIdList=" + collectIdList +
                ", joinIdList=" + joinIdList +
                '}';
    }
}
