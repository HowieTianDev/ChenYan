package com.howietian.chenyan.app;

import android.app.Application;

import com.howietian.chenyan.entities.User;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;

/**
 * Created by 83624 on 2017/6/30.
 */

public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this,Constant.BMOB_KEY);
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
