package com.howietian.chenyan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class Main3Activity extends AppCompatActivity {
private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://mp.weixin.qq.com/s?__biz=MjM5NDgxNTQwNQ==&mid=2650721478&idx=1&sn=52e42e90377057bb4b70132f183b171f&chksm=be88600489ffe9121def4bcc880999473479ce7eacaf21ccfe3868873c06e0837479f7533ac1&mpshare=1&scene=23&srcid=0204naMtmqVWSN5sezs3xwJT#rd");
    }
}
