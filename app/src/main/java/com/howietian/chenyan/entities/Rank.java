package com.howietian.chenyan.entities;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by HowieTian on 2017/8/2 0002.
 */

public class Rank extends BmobObject {
    private BmobFile image;
    private String title;
    private User no1;
    private User no2;
    private User no3;
    private User no4;
    private User no5;
    private User no6;
    private User no7;
    private User no8;
    private User no9;
    private User no10;
    private Integer order;

    public Rank() {
    }

    public Rank(BmobFile image, String title, User no1, User no2, User no3, User no4, User no5, User no6, User no7, User no8, User no9, User no10, Integer order) {
        this.image = image;
        this.title = title;
        this.no1 = no1;
        this.no2 = no2;
        this.no3 = no3;
        this.no4 = no4;
        this.no5 = no5;
        this.no6 = no6;
        this.no7 = no7;
        this.no8 = no8;
        this.no9 = no9;
        this.no10 = no10;
        this.order = order;
    }

    public BmobFile getImage() {
        return image;
    }

    public void setImage(BmobFile image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public User getNo1() {
        return no1;
    }

    public void setNo1(User no1) {
        this.no1 = no1;
    }

    public User getNo2() {
        return no2;
    }

    public void setNo2(User no2) {
        this.no2 = no2;
    }

    public User getNo3() {
        return no3;
    }

    public void setNo3(User no3) {
        this.no3 = no3;
    }

    public User getNo4() {
        return no4;
    }

    public void setNo4(User no4) {
        this.no4 = no4;
    }

    public User getNo5() {
        return no5;
    }

    public void setNo5(User no5) {
        this.no5 = no5;
    }

    public User getNo6() {
        return no6;
    }

    public void setNo6(User no6) {
        this.no6 = no6;
    }

    public User getNo7() {
        return no7;
    }

    public void setNo7(User no7) {
        this.no7 = no7;
    }

    public User getNo8() {
        return no8;
    }

    public void setNo8(User no8) {
        this.no8 = no8;
    }

    public User getNo9() {
        return no9;
    }

    public void setNo9(User no9) {
        this.no9 = no9;
    }

    public User getNo10() {
        return no10;
    }

    public void setNo10(User no10) {
        this.no10 = no10;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Rank{" +
                "image=" + image +
                ", title='" + title + '\'' +
                ", no1=" + no1 +
                ", no2=" + no2 +
                ", no3=" + no3 +
                ", no4=" + no4 +
                ", no5=" + no5 +
                ", no6=" + no6 +
                ", no7=" + no7 +
                ", no8=" + no8 +
                ", no9=" + no9 +
                ", no10=" + no10 +
                ", order=" + order +
                '}';
    }
}
