package com.howietian.chenyan.entities;

import java.util.Date;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by 83624 on 2017/6/30.
 */

public class User extends BmobUser {
    private String nickName;
    private String realName;
    private BmobFile avatar;
    private Boolean gender;
    private String intro;
    private String school;
    private String position;
    private String like;
    private String birthday;

    private Integer dynamicNum;
    private Integer followNum;
    private Integer fanNum;


    public User() {
    }

    public User(String nickName, String realName, BmobFile avatar, Boolean gender, String intro, String school, String position, String like, String birthday) {
        this.nickName = nickName;
        this.realName = realName;
        this.avatar = avatar;
        this.gender = gender;
        this.intro = intro;
        this.school = school;
        this.position = position;
        this.like = like;
        this.birthday = birthday;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Integer getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(Integer dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public Integer getFollowNum() {
        return followNum;
    }

    public void setFollowNum(Integer followNum) {
        this.followNum = followNum;
    }

    public Integer getFanNum() {
        return fanNum;
    }

    public void setFanNum(Integer fanNum) {
        this.fanNum = fanNum;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickName='" + nickName + '\'' +
                ", realName='" + realName + '\'' +
                ", avatar=" + avatar +
                ", gender=" + gender +
                ", intro='" + intro + '\'' +
                ", school='" + school + '\'' +
                ", position='" + position + '\'' +
                ", like='" + like + '\'' +
                ", birthday='" + birthday + '\'' +
                ", dynamicNum=" + dynamicNum +
                ", followNum=" + followNum +
                ", fanNum=" + fanNum +
                '}';
    }
}
