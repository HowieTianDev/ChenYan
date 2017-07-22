package com.howietian.chenyan.entrance;

import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.MainActivity;
import com.howietian.chenyan.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.iv_bg)
    ImageView iv_bg;
    private Handler handler = new Handler();


    @Override
    public void setMyContentView() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
    }


    @Override
    public void init() {
        super.init();
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
        alphaAnimation.setDuration(2000);
        iv_bg.setAnimation(alphaAnimation);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                jumpTo(MainActivity.class, true);
            }
        }, 2000);
    }
}
