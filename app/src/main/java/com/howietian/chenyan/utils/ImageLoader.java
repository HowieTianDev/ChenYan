package com.howietian.chenyan.utils;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.howietian.chenyan.R;


/**
 * Created by Howie Tian on 2017/7/27 0027.
 * 简单封装一下Glide
 */

public class ImageLoader {

    public static void with(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).into(imageView);
    }

    public static void with(Context context, Uri uri, ImageView imageView) {
        Glide.with(context).load(uri).into(imageView);
    }

    public static void withHolder(Context context, String url, ImageView imageView) {
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .into(imageView);
    }
}
