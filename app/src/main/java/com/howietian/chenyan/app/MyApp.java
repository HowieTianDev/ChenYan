package com.howietian.chenyan.app;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;
import com.lzy.ninegrid.NineGridView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;

/**
 * Created by 83624 on 2017/cup_6/30.
 */

public class MyApp extends Application {

    public static List<User> userList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,Constant.BMOB_KEY);
        // 使用推送服务时的初始化操作
        BmobInstallation.getCurrentInstallation().save();
        // 启动推送服务
        BmobPush.startWork(this);
        NineGridView.setImageLoader(new GlideImageLoader());
    }

    private class GlideImageLoader implements NineGridView.ImageLoader {

        @Override
        public void onDisplayImage(Context context, ImageView imageView, String url) {
            Glide.with(context).load(url)//
                   // .placeholder(R.drawable.ic_default_image)//
                    .error(R.drawable.ic_default_image)//
                    .into(imageView);
        }

        @Override
        public Bitmap getCacheImage(String url) {
            return null;
        }
    }


    public static boolean isLogin(){
        User user = BmobUser.getCurrentUser(User.class);
        if(user == null){
            return false;
        }else{
            return true;
        }
    }


}
