package com.howietian.chenyan.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.text.method.LinkMovementMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.howietian.chenyan.Listener.ClickableSpanEx;
import com.howietian.chenyan.Listener.CommentClick;
import com.howietian.chenyan.circle.CircleFragment;
import com.howietian.chenyan.entities.DComment;

/**
 * Created by 83624 on 2017/7/21.
 */

public class CommentWidget extends TextView {
    //    用户名颜色
    private int textColor = 0xff517fae;
    private static final int textSize = 14;
    private CircleFragment fragment = CircleFragment.newInstance("Circle Fragment");
    SpannableStringBuilderCompat mSpannableStringBuilderCompat;


    public CommentWidget(Context context) {
        this(context, null);
    }

    public CommentWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setMovementMethod(LinkMovementMethod.getInstance());
        setOnTouchListener(new ClickableSpanEx.ClickableSpanSelector());
        this.setHighlightColor(0x00000000);
        setTextSize(textSize);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CommentWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setMovementMethod(LinkMovementMethod.getInstance());
        this.setHighlightColor(0x00000000);
        setTextSize(textSize);
    }

    public void setCommentText(DComment info) {
        if (info == null) return;
        try {
            setTag(info);
            createCommentStringBuilder(info);
        } catch (NullPointerException e) {
            e.printStackTrace();
            Log.e("TAG", "虽然在下觉得不可能会有这个情况，但还是捕捉下吧，万一被打脸呢。。。");
        }
    }

    /**
     * 判断到底是回复，原创评论
     *
     * @param info
     */

    private void createCommentStringBuilder(@NonNull final DComment info) {
//        this.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Toast.makeText(getContext(), info.toString(), Toast.LENGTH_LONG).show();
//               // fragment.comment();
//            }
//        });
        if (mSpannableStringBuilderCompat == null) {
            mSpannableStringBuilderCompat = new SpannableStringBuilderCompat();
        } else {
            mSpannableStringBuilderCompat.clear();
            mSpannableStringBuilderCompat.clearSpans();
        }
        String content = ": " + info.getContent() + "\0";
        boolean isApply = (info.getReply() == null);
        // 用户B为空，证明是一条原创评论
        if (info.getAuthor() != null && isApply) {
            CommentClick userA = new CommentClick.Builder(getContext(), info.getAuthor()).setColor(0xff517fae)
                    .setClickEventColor(0xffc6c6c6)
                    .setTextSize(textSize)
                    .build();
            mSpannableStringBuilderCompat.append(info.getAuthor().getNickName(), userA, 0);
            mSpannableStringBuilderCompat.append(content);
        } else if (info.getAuthor() != null && !isApply) {
            //用户A，B不空，证明是回复评论
            CommentClick userA = new CommentClick.Builder(getContext(), info.getAuthor()).setColor(0xff517fae)
                    .setClickEventColor(0xffc6c6c6)
                    .setTextSize(textSize)
                    .build();
            mSpannableStringBuilderCompat.append(info.getAuthor().getNickName(), userA, 0);
            mSpannableStringBuilderCompat.append(" 回复 ");
            CommentClick userB = new CommentClick.Builder(getContext(), info.getReply()).setColor(0xff517fae)
                    .setClickEventColor(0xffc6c6c6)
                    .setTextSize(textSize)
                    .build();
            mSpannableStringBuilderCompat.append(info.getReply().getNickName(), userB, 0);
            mSpannableStringBuilderCompat.append(content);
        }
        setText(mSpannableStringBuilderCompat);
    }

    public DComment getData() throws ClassCastException {
        return (DComment) getTag();
    }

}
