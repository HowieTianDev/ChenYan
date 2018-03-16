package com.howietian.chenyan.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.util.LruCache;
import android.widget.TextView;

import com.howietian.chenyan.Listener.ClickableSpanEx;
import com.howietian.chenyan.Listener.PraiseClick;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 83624 on 2017/cup_7/19.
 */

public class PraiseWidget extends android.support.v7.widget.AppCompatTextView {

    //    点赞名字默认展示的颜色
    private int textColor = 0xff517fae;
    //    点赞列表默认图标
    private int iconRes = R.drawable.ic_thumb_up_orange_500_24dp;
    //    默认字体大小
    private int textSize = 14;
    //    默认点击背景
    private int clickBg = R.color.transparent;

    private List<User> datas ;
    /**
     * 返回用户定义的item的大小，默认返回1代表item的数量，最大size就是最大item值
     */
    private static final LruCache<String, SpannableStringBuilderCompat> praiseCache
            = new LruCache<String, SpannableStringBuilderCompat>(50) {
        @Override
        protected int sizeOf(String key, SpannableStringBuilderCompat value) {
            return 1;
        }
    };

    public PraiseWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//      找到属性数组
        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PraiseWidget);
        textColor = typedArray.getColor(R.styleable.PraiseWidget_font_color, 0xff517fae);
        textSize = typedArray.getDimensionPixelSize(R.styleable.PraiseWidget_font_size, 14);
        clickBg = typedArray.getColor(R.styleable.PraiseWidget_click_bg_color, 0x00000000);
        iconRes = typedArray.getResourceId(R.styleable.PraiseWidget_like_icon, R.drawable.ic_thumb_up_blue_500_18dp);

        typedArray.recycle();
//        如果不设置，clickableSpan 不能相应点击事件
        this.setMovementMethod(LinkMovementMethod.getInstance());
//        设置点击时的颜色
        setOnTouchListener(new ClickableSpanEx.ClickableSpanSelector());
        setTextSize(textSize);
    }

    public void setDatas(List<User> datas) {
        this.datas = datas;
    }

    @Override
    public boolean onPreDraw() {
        if (datas == null || datas.size() == 0) {
            return super.onPreDraw();
        } else {
            createSpanStringBuilder(datas);
            return true;
        }
    }


    private void createSpanStringBuilder(List<User> datas) {
        if (datas == null || datas.size() == 0) {
            return;
        }

        String key = Integer.toString(datas.hashCode() + datas.size());
        SpannableStringBuilderCompat spannableStringBuilder = praiseCache.get(key);
        if (spannableStringBuilder == null) {
            CustomImageSpan icon = new CustomImageSpan(getContext(), iconRes);
//            因为spanstringbuilder 不支持直接append span，所以通过spanstring 转换
            SpannableString iconSpanStr = new SpannableString(" ");
            iconSpanStr.setSpan(icon, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

            spannableStringBuilder = new SpannableStringBuilderCompat(iconSpanStr);
//            点赞图标后给出两个空格
            spannableStringBuilder.append("  ");
            for (int i = 0; i < datas.size(); i++) {
                PraiseClick praiseClick = new PraiseClick.Builder(getContext(), datas.get(i))
                        .setTextSize(textSize).setColor(textColor).setClickEventColor(clickBg)
                        .build();

                try {
                    spannableStringBuilder.append(datas.get(i).getNickName(), praiseClick, 0);

                } catch (NullPointerException e) {
                    e.printStackTrace();
                }

                if (i != datas.size() - 1) {
                    spannableStringBuilder.append(", ");
                } else {
                    spannableStringBuilder.append("\0");
                }

            }

            praiseCache.put(key, spannableStringBuilder);

        }
        setText(spannableStringBuilder);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        praiseCache.evictAll();
        if (praiseCache.size() == 0) {

        }
    }
}
