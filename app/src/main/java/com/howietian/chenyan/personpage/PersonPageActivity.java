package com.howietian.chenyan.personpage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ImageLoader;

import butterknife.Bind;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonPageActivity extends BaseActivity {

    @Bind(R.id.viewpager)
    ViewPager viewpager;
    @Bind(R.id.avatar)
    CircleImageView avatar;
    @Bind(R.id.tv_nickName)
    TextView tvNickName;
    @Bind(R.id.tv_publish)
    TextView tvPublish;
    @Bind(R.id.tv_follow)
    TextView tvFollow;
    @Bind(R.id.tv_fan)
    TextView tvFan;
    @Bind(R.id.tabLayout)
    TabLayout tabLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    private User dynamicAuthor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_home_page);
    }

    @Override
    public void init() {
        super.init();
        initViews();
        initListeners();
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (getIntent() != null) {
            String userMsg = getIntent().getStringExtra(Constant.TO_PERSON_PAGE);
            dynamicAuthor = new Gson().fromJson(userMsg, User.class);
            if (dynamicAuthor.getAvatar() != null) {
                ImageLoader.with(this, dynamicAuthor.getAvatar().getUrl(), avatar);
            }
            tvNickName.setText(dynamicAuthor.getNickName());
            tvFan.setText(0 + "");
            tvFollow.setText(0 + "");
            tvPublish.setText(0 + "");

        }
    }

    private void initListeners(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
