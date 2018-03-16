package com.howietian.chenyan.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.User;

import org.json.JSONException;
import org.json.JSONObject;

import cn.bmob.push.PushConstants;

/**
 * Created by HowieTian on 2017/cup_8/17 0017.
 */

public class MyPushMessageReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            Log.d("bmob", "客户端收到推送内容：" + intent.getStringExtra("msg"));
            String json = intent.getStringExtra("msg");
            try {
                JSONObject jsonObject = new JSONObject(json);
                String userMsg = jsonObject.getString("alert");
                Log.d("bmob接受", userMsg);
                User user = new Gson().fromJson(userMsg,User.class);
                Log.d("bmobUser",user.getNickName()+user.getInstallationId());
                MyApp.userList.add(user);

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

}
