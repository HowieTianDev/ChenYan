package com.howietian.chenyan;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.howietian.chenyan.entities.DComment;
import com.howietian.chenyan.entities.Rank;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.views.ClickShowMoreLayout;
import com.howietian.chenyan.views.CommentWidget;
import com.howietian.chenyan.views.PraiseWidget;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.filter.Filter;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class Main2Activity extends BaseActivity {
    private static final int REQUEST_CODE_CHOOSE = 0;
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
    @Bind(R.id.nineGridView)
    NineGridView nineGridView;

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
//        DComment comment1 = new DComment("7777777",BmobUser.getCurrentUser(User.class),BmobUser.getCurrentUser(User.class),null,null);
//        commentWidget.setCommentText(comment1);
//       // nineTest();
//        Matisse.from(Main2Activity.this)
//                .choose(MimeType.allOf())
//                .countable(true)
//                .maxSelectable(9)
//                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                .thumbnailScale(0.85f)
//                .imageEngine(new GlideEngine())
//                .forResult(REQUEST_CODE_CHOOSE);
        createRank();
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
        DComment comment = new DComment("66666666", BmobUser.getCurrentUser(User.class),null,null,null);
        commentWidget.setCommentText(comment);

    }
    private void nineTest(){
        List<ImageInfo> imageInfoList = new ArrayList<>();
        for(int i = 0;i<9;i++){
            ImageInfo imageInfo = new ImageInfo();
            imageInfo.setBigImageUrl("http://img1a.xgo-img.com.cn/pics/1538/a1537491.jpg");
            imageInfo.setThumbnailUrl("http://img1a.xgo-img.com.cn/pics/1538/a1537491.jpg");
            imageInfoList.add(imageInfo);
        }

        nineGridView.setAdapter(new NineGridViewClickAdapter(this,imageInfoList));
    }
    static class ImcageInfo{
        private URL thumbnailUrl;
        private URL bigImageUrl;
    }

    List<Uri> mSelected;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.e("Matisse", "mSelected: " + mSelected);
        }
    }

    private void createRank(){
        Rank rank = new Rank();
        User user = BmobUser.getCurrentUser(User.class);
        rank.setTitle("测试");
        Uri uri = Uri.parse("content://media/external/images/media/246751");
//        File file = new File("content://media/external/images/media/246751");
//       // File file1 = new File("")
//        BmobFile bfile = new BmobFile(file);
//        rank.setImage(bfile);
        rank.setNo1(user);
        rank.setNo2(user);
        rank.setNo3(user);
        rank.setNo4(user);
        rank.setNo5(user);
        rank.setNo6(user);
        rank.setNo7(user);
        rank.setNo8(user);
        rank.setNo9(user);
        rank.setNo10(user);

        rank.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null){
                    showToast("保存成功");
                }else{
                    showToast("保存失败"+e.getMessage()+e.getErrorCode());
                }
            }
        });
    }



}
