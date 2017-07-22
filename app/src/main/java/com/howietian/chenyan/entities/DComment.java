package com.howietian.chenyan.entities;

import cn.bmob.v3.BmobObject;

/**
 * Created by 83624 on 2017/7/21.
 */

public class DComment extends BmobObject {
    private String content;
    private User author;
    private User reply;
    private Dynamic dynamic;

    public DComment() {
    }

    public DComment(String content, User author, User reply, Dynamic dynamic) {
        this.content = content;
        this.author = author;
        this.reply = reply;
        this.dynamic = dynamic;
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


    @Override
    public String toString() {
        return "DComment{" +
                "content='" + content + '\'' +
                ", author=" + author +
                ", reply=" + reply +
                ", dynamic=" + dynamic +
                '}';
    }
}
