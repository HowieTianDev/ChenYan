package com.howietian.chenyan.entities;

/**
 * Created by 83624 on 2018/3/19 0019.
 */

public class Message {
    private Integer id;
    private String message;


    public Message() {
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", message='" + message + '\'' +
                '}';
    }
}
