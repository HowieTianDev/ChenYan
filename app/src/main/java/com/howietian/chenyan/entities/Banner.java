package com.howietian.chenyan.entities;

import java.util.ArrayList;

import cn.bmob.v3.BmobObject;

/**
 * Created by 83624 on 2018/3/31 0031.
 */

public class Banner extends BmobObject {
    public static final int HOME = 0;
    public static final int Circle = 1;

    private ArrayList<String> urls;
    private ArrayList<String> articleIds;
    private Integer type;

    public Banner() {
    }

    public ArrayList<String> getUrls() {
        return urls;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public ArrayList<String> getArticleIds() {
        return articleIds;
    }

    public void setArticleIds(ArrayList<String> articleIds) {
        this.articleIds = articleIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Banner{" +
                "urls=" + urls +
                ", articleIds=" + articleIds +
                ", type=" + type +
                '}';
    }
}
