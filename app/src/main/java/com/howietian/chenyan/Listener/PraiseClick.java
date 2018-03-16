package com.howietian.chenyan.Listener;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.UIHelper;

/**
 * Created by 83624 on 2017/cup_7/19.
 * 这里可能会有点问题，selector可能没设置好
 */

public class PraiseClick extends ClickableSpanEx {
    private static final int DEFAULT_COLOR = 0xff517fae;
    private int color;
    private Context mContext;
    private int textSize;
    private User user;

    private PraiseClick() {}

    private PraiseClick(Builder builder) {
        super(builder.color,builder.clickBgColor);
        this.mContext = builder.mContext;
        this.user = builder.user;
        this.textSize = builder.textSize;
    }

    @Override
    public void onClick(View widget) {
        if (user !=null)
            UIHelper.ToastMessage("当前用户名是： " + user.getNickName() + "   它的ID是： " + user.getObjectId());
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
        private int textSize=16;
        private User user;
        private int clickBgColor;

        public Builder(Context context, @NonNull User info) {
            mContext = context;
            user=info;
        }

        public Builder setTextSize(int textSize) {
            this.textSize = UIHelper.sp2px(textSize);
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }

        public Builder setClickEventColor(int color){
            this.clickBgColor=color;
            return this;
        }

        public PraiseClick build() {
            return new PraiseClick(this);
        }
    }
}
