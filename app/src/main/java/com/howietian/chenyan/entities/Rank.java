package com.howietian.chenyan.entities;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

/**
 * Created by HowieTian on 2017/cup_8/2 0002.
 */

public class Rank extends BmobObject {
    private BmobFile image;
    private String title;
    private User one;
    private User two;
    private User three;
    private User four;
    private User five;
    private User six;
    private User seven;
    private User eight;
    private User nine;
    private User ten;
    private Integer order;

    public Rank() {
    }

    public Rank(BmobFile image, String title, User one, User two, User three, User four, User five, User six, User seven, User eight, User nine, User ten, Integer order) {
        this.image = image;
        this.title = title;
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
        this.five = five;
        this.six = six;
        this.seven = seven;
        this.eight = eight;
        this.nine = nine;
        this.ten = ten;
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

    public User getOne() {
        return one;
    }

    public void setOne(User one) {
        this.one = one;
    }

    public User getTwo() {
        return two;
    }

    public void setTwo(User two) {
        this.two = two;
    }

    public User getThree() {
        return three;
    }

    public void setThree(User three) {
        this.three = three;
    }

    public User getFour() {
        return four;
    }

    public void setFour(User four) {
        this.four = four;
    }

    public User getFive() {
        return five;
    }

    public void setFive(User five) {
        this.five = five;
    }

    public User getSix() {
        return six;
    }

    public void setSix(User six) {
        this.six = six;
    }

    public User getSeven() {
        return seven;
    }

    public void setSeven(User seven) {
        this.seven = seven;
    }

    public User getEight() {
        return eight;
    }

    public void setEight(User eight) {
        this.eight = eight;
    }

    public User getNine() {
        return nine;
    }

    public void setNine(User nine) {
        this.nine = nine;
    }

    public User getTen() {
        return ten;
    }

    public void setTen(User ten) {
        this.ten = ten;
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
                ", one=" + one +
                ", two=" + two +
                ", three=" + three +
                ", four=" + four +
                ", five=" + five +
                ", six=" + six +
                ", seven=" + seven +
                ", eight=" + eight +
                ", nine=" + nine +
                ", ten=" + ten +
                ", order=" + order +
                '}';
    }
}
