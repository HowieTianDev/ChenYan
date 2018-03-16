package com.howietian.chenyan.entities;

import cn.bmob.v3.BmobObject;

/**
 * Created by 83624 on 2017/cup_7/21.
 */

public class DComment extends BmobObject {
    private String content;
    private User author;
    private User reply;
    private Dynamic dynamic;
    private String dynamicId;

    public DComment() {
    }

    public DComment(String content, User author, User reply, Dynamic dynamic, String dynamicId) {
        this.content = content;
        this.author = author;
        this.reply = reply;
        this.dynamic = dynamic;
        this.dynamicId = dynamicId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public User getReply() {
        return reply;
    }

    public void setReply(User reply) {
        this.reply = reply;
    }

    public Dynamic getDynamic() {
        return dynamic;
    }

    public void setDynamic(Dynamic dynamic) {
        this.dynamic = dynamic;
    }

    public String getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(String dynamicId) {
        this.dynamicId = dynamicId;
    }

    @Override
    public String toString() {
        return "DComment{" +
                "content='" + content + '\'' +
                ", author=" + author +
                ", reply=" + reply +
                ", dynamic=" + dynamic +
                ", dynamicId='" + dynamicId + '\'' +
                '}';
    }
}
