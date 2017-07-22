package com.howietian.chenyan;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.views.ClickShowMoreLayout;
import com.howietian.chenyan.views.CommentWidget;
import com.howietian.chenyan.views.PraiseWidget;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;

public class Main2Activity extends BaseActivity {
    @Bind(R.id.clickShowMore)
    ClickShowMoreLayout clickShowMoreLayout;
    @Bind(R.id.btn_test)
    Button btn_test;
    @Bind(R.id.praisewidget)
    PraiseWidget praiseWidget;
    @Bind(R.id.edit_text)
    EditText editText;
    @Bind(R.id.commentWidget)
    CommentWidget commentWidget;

    private List<User> users = new ArrayList<>();

    User user = new User();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_main2);
    }

    @Override
    public void init() {
        super.init();

        user.setNickName("小贱人");
        user.setObjectId("###");
        clickShowMoreLayout.setText("1\n2\n3\n4\n5\n6\n7\n8\n9\n10\n");
        for (int i = 0; i < 6; i++) {
            users.add(user);
        }

        praiseWidget.setDatas(users);

        spanTest();

        commentTest();
    }


    @OnClick(R.id.btn_test)
    public void test(View view) {
//        clickShowMoreLayout.setText("1\n2\n3\n4\n5");
//        for (int i = 0; i < 12; i++) {
//            users.add(user);
//        }
//        praiseWidget.setDatas(users);
        DComment comment1 = new DComment("7777777",BmobUser.getCurrentUser(User.class),BmobUser.getCurrentUser(User.class),null);
        commentWidget.setCommentText(comment1);
    }

    private void spanTest() {
        SpannableString spannableString = new SpannableString("欢迎光临我的博客");
//        字体颜色span
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
//        背景颜色span
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(Color.RED);

//        1,3其实是两个位置，末尾不包括
//        Flag 为 span如果新增了文字 的前面不包括样式，后面包括样式
        spannableString.setSpan(colorSpan, 1, 3, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(backgroundColorSpan,5,7,Spanned.SPAN_INCLUSIVE_EXCLUSIVE);

        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        spannableStringBuilder.append(spannableString);

        editText.setText(spannableString);

    }


    private void commentTest(){
        DComment comment = new DComment("66666666", BmobUser.getCurrentUser(User.class),null,null);
        commentWidget.setCommentText(comment);

    }


}
