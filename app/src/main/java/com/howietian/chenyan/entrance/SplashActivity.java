package com.howietian.chenyan.entrance;

import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ListView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.MainActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.Splash;
import com.howietian.chenyan.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SplashActivity extends BaseActivity {
    @Bind(R.id.iv_bg)
    ImageView iv_bg;
    private Handler handler = new Handler();

    private String imageUrl = "";


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
        queryImage();
    }

    private void queryImage() {
        BmobQuery<Splash> query = new BmobQuery<>();
        query.findObjects(new FindListener<Splash>() {
            @Override
            public void done(List<Splash> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        imageUrl = list.get(0).getImage().getUrl();
                        ImageLoader.with(SplashActivity.this, imageUrl, iv_bg);
//                        AlphaAnimation alphaAnimation = new AlphaAnimation(0.5f, 1.0f);
//                        alphaAnimation.setDuration(2000);
//                        iv_bg.setAnimation(alphaAnimation);
                    }

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            jumpTo(MainActivity.class, true);
                        }
                    }, 2000);
                } else {
                    jumpTo(MainActivity.class, true);
                }
            }
        });
    }
}
