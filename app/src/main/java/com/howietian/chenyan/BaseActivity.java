package com.howietian.chenyan;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.net.URL;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLayout();
    }

    /**
     * 抽象方法，每个布局设置布局文件，需要子类去具体实现，下面加上ButterKnife的初始化
     */
    public abstract void setMyContentView();

    private void initLayout() {
        setMyContentView();
        ButterKnife.bind(this);
        init();
    }
//    一个初始化方法
    public void init(){}

    /**
     * 常用的几个方法
     */
    public void showToast(String s) {
        if (!TextUtils.isEmpty(s)) {
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        Intent intent = new Intent(this, clazz);
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

    public void jumpTo(Intent intent,boolean isFinish){
        startActivity(intent);
        if(isFinish){
            finish();
        }
    }

    /**
     * 检查EditText是否为空
     */
    public boolean isEmpty(EditText... ets) {
        for (EditText et : ets) {
            String s = et.getText().toString();
            if (TextUtils.isEmpty(s)) {
                return true;
            }
        }
        return false;
    }
    /**
     * 封装Glide
     */
    public void loadImage(Uri uri, ImageView imageView){
        Glide.with(this).load(uri).into(imageView);
    }
    public void loadImage(URL url,ImageView imageView){
        Glide.with(this).load(url).into(imageView);
    }
    public void loadImage(String s,ImageView imageView){
        Glide.with(this).load(s).into(imageView);
    }


}
