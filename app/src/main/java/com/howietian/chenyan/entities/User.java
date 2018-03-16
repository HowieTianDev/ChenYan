package com.howietian.chenyan.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

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
    private String clubId;
    private String installationId;

    private Boolean isClub;
    private String clubProfile;


    private Integer dynamicNum = 0;
    private Integer fanNum = 0;
    private BmobRelation focus;
    private BmobRelation members;

    private ArrayList<String> focusIds;
    private ArrayList<String> memberIds;



    public User() {
    }

    public User(String nickName, String realName, BmobFile avatar, Boolean gender, String intro, String school, String position, String like, String birthday, String clubId, String installationId, Boolean isClub, String clubProfile, Integer dynamicNum, Integer fanNum, BmobRelation focus, BmobRelation members, ArrayList<String> focusIds, ArrayList<String> memberIds) {
        this.nickName = nickName;
        this.realName = realName;
        this.avatar = avatar;
        this.gender = gender;
        this.intro = intro;
        this.school = school;
        this.position = position;
        this.like = like;
        this.birthday = birthday;
        this.clubId = clubId;
        this.installationId = installationId;
        this.isClub = isClub;
        this.clubProfile = clubProfile;
        this.dynamicNum = dynamicNum;
        this.fanNum = fanNum;
        this.focus = focus;
        this.members = members;
        this.focusIds = focusIds;
        this.memberIds = memberIds;
    }

    public String getInstallationId() {
        return installationId;
    }

    public void setInstallationId(String installationId) {
        this.installationId = installationId;
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

    public String getClubId() {
        return clubId;
    }

    public void setClubId(String clubId) {
        this.clubId = clubId;
    }

    public Boolean getClub() {
        return isClub;
    }

    public void setClub(Boolean club) {
        isClub = club;
    }

    public String getClubProfile() {
        return clubProfile;
    }

    public void setClubProfile(String clubProfile) {
        this.clubProfile = clubProfile;
    }

    public Integer getDynamicNum() {
        return dynamicNum;
    }

    public void setDynamicNum(Integer dynamicNum) {
        this.dynamicNum = dynamicNum;
    }

    public Integer getFanNum() {
        return fanNum;
    }

    public void setFanNum(Integer fanNum) {
        this.fanNum = fanNum;
    }

    public BmobRelation getFocus() {
        return focus;
    }

    public void setFocus(BmobRelation focus) {
        this.focus = focus;
    }

    public BmobRelation getMembers() {
        return members;
    }

    public void setMembers(BmobRelation members) {
        this.members = members;
    }

    public ArrayList<String> getFocusIds() {
        return focusIds;
    }

    public void setFocusIds(ArrayList<String> focusIds) {
        this.focusIds = focusIds;
    }

    public ArrayList<String> getMemberIds() {
        return memberIds;
    }

    public void setMemberIds(ArrayList<String> memberIds) {
        this.memberIds = memberIds;
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
                ", clubId='" + clubId + '\'' +
                ", installationId='" + installationId + '\'' +
                ", isClub=" + isClub +
                ", clubProfile='" + clubProfile + '\'' +
                ", dynamicNum=" + dynamicNum +
                ", fanNum=" + fanNum +
                ", focus=" + focus +
                ", members=" + members +
                ", focusIds=" + focusIds +
                ", memberIds=" + memberIds +
                '}';
    }
}
