package com.howietian.chenyan;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.URL;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    BaseActivity baseActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        baseActivity = (BaseActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = createMyView(inflater, container, savedInstanceState);
        ButterKnife.bind(this, view);

        init();
        return view;
    }

    public abstract View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    public void init() {
    }

    /**
     * 一些常用的方法
     */

    public void showToast(String s) {
        baseActivity.showToast(s);
    }

    public void jumpTo(Class<?> clazz, boolean isFinish) {
        baseActivity.jumpTo(clazz, isFinish);
    }

    public void jumpTo(Intent intent, boolean isFinish) {
        baseActivity.jumpTo(intent, isFinish);
    }

    /**
     * 封装Glide
     */
    public void loadImage(Uri uri, ImageView imageView){
        Glide.with(this).load(uri).into(imageView);
    }
    public void loadImage(URL url, ImageView imageView){
        Glide.with(this).load(url).into(imageView);
    }
    public void loadImage(String s,ImageView imageView){
        Glide.with(this).load(s).into(imageView);
    }
}
