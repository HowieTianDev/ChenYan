package com.howietian.chenyan.Listener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.personpage.PersonPageActivity;
import com.howietian.chenyan.utils.UIHelper;

public class CommentClick extends ClickableSpanEx {
    private Context mContext;
    private int textSize;
    private User mUserInfo;

    private CommentClick() {
    }

    private CommentClick(Builder builder) {
        super(builder.color, builder.clickEventColor);
        mContext = builder.mContext;
        mUserInfo = builder.mUserInfo;
        this.textSize = builder.textSize;
    }

    @Override
    public void onClick(View widget) {
        if(MyApp.isLogin()){
            if (mUserInfo != null) {
                Intent intent = new Intent(mContext, PersonPageActivity.class);
                intent.putExtra(Constant.TO_PERSON_PAGE,new Gson().toJson(mUserInfo,User.class));
                mContext.startActivity(intent);
            }
        }else{
            Intent intent = new Intent(mContext, LoginActivity.class);
            mContext.startActivity(intent);
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setTextSize(textSize);
        ds.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public static class Builder {
        private int color;
        private Context mContext;
        private int textSize = 16;
        private User mUserInfo;
        private int clickEventColor;

        public Builder(Context context, @NonNull User info) {
            mContext = context;
            mUserInfo = info;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = UIHelper.sp2px(textSize);
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setClickEventColor(int color) {
            this.clickEventColor = color;
            return this;
        }

        public CommentClick build() {
            return new CommentClick(this);
        }
    }
}