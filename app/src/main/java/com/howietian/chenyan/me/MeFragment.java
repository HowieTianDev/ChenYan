package com.howietian.chenyan.me;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.howietian.chenyan.me.collect.MyCollectActivity;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends BaseFragment {
    @Bind(R.id.avatar)
    CircleImageView avatar;
    @Bind(R.id.tv_nickName)
    TextView nickName;
    @Bind(R.id.tv_logout)
    TextView tvLogout;
    @Bind(R.id.tv_fan)
    TextView tvFan;
    @Bind(R.id.tv_follow)
    TextView tvFollow;
    @Bind(R.id.tv_publish)
    TextView tvPublish;
    @Bind(R.id.tv_pub_activity)
    TextView tvPubActivity;
    @Bind(R.id.ll_fan)
    LinearLayout llFan;
    @Bind(R.id.ll_follow)
    LinearLayout llFollow;
    @Bind(R.id.ll_publish)
    LinearLayout llPublish;

    private static final String ME_FRAGMENT = "me_fragment";


    public static MeFragment newInstance(String args) {
        MeFragment fragment = new MeFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ME_FRAGMENT, args);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MeFragment() {
        // Required empty public constructor
    }


    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_me, container, false);
    }


    @Override
    public void init() {
        super.init();

    }

    //  初始化数据
    private void initDatas() {
        User user = BmobUser.getCurrentUser(User.class);
        if (MyApp.isLogin()) {
            if (user.getAvatar() != null) {
                loadImage(user.getAvatar().getUrl(), avatar);
            }
            if (!TextUtils.isEmpty(user.getNickName())) {
                nickName.setText(user.getNickName());
            }

            tvPublish.setText(user.getDynamicNum() + "");
            if (user.getFocusIds() != null) {
                tvFollow.setText(user.getFocusIds().size() + "");
            } else {
                tvFollow.setText(0 + "");
            }
            queryFanNum(user);
        } else {

            avatar.setImageResource(R.drawable.ic_account_circle_blue_grey_100_36dp);
            nickName.setText("请登录");
            tvFan.setText("0");
            tvPublish.setText("0");
            tvFollow.setText("0");
        }
    }

    /**
     * 查询粉丝数目
     */

    private void queryFanNum(User user) {
        BmobQuery<User> query = new BmobQuery<>();
        query.include("focus");
        BmobQuery<BmobUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", user.getObjectId());
        query.addWhereMatchesQuery("focus", "_User", innerQuery);
        query.count(User.class, new CountListener() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    tvFan.setText(integer.toString());
                } else {
                    showToast("查询粉丝数目失败" + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("HHH", "onResume()");
        initDatas();
    }

    /**
     * 通过点击头像实现跳转到我的信息界面
     */
    @OnClick(R.id.avatar)
    public void setAvatar() {
        if (MyApp.isLogin()) {
            jumpTo(MyInfoActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }

    /**
     * 通过点击我的资料进入个人信息
     */
    @OnClick(R.id.tv_myInfo)
    public void toMyInfo() {
        if (MyApp.isLogin()) {
            jumpTo(MyInfoActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }

    }

    /**
     * 通过点击我的收藏进入
     */
    @OnClick(R.id.tv_myCollect)
    public void toMyCollect() {
        if (MyApp.isLogin()) {
            jumpTo(MyCollectActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }
    }

    /**
     * 通过点击我的发布进入
     */
    @OnClick(R.id.tv_pub_activity)
    public void toMyPubActivity() {
        if (MyApp.isLogin()) {
            jumpTo(MyPubActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }
    }


    /**
     * 通过点击我的发布进入
     */
    @OnClick(R.id.tv_myJoin)
    public void toMyJoin() {
        if (MyApp.isLogin()) {
            jumpTo(MyJoinActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, false);
        }
    }


    /**
     * 退出登录操作
     */
    @OnClick(R.id.tv_logout)
    public void logout() {
        if (MyApp.isLogin()) {
            BmobUser.logOut();
            initDatas();
            showToast("已成功退出");
        } else {
            return;
        }
    }

    @OnClick(R.id.ll_fan)
    public void toFan() {
        if (MyApp.isLogin()) {
            jumpTo(MyFanActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, true);
        }

    }

    @OnClick(R.id.ll_publish)
    public void toPublish() {
        if (MyApp.isLogin()) {
            jumpTo(MyDynnamicActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, true);
        }

    }

    @OnClick(R.id.ll_follow)
    public void toFollow() {
        if (MyApp.isLogin()) {
            jumpTo(MyFocusActivity.class, false);
        } else {
            jumpTo(LoginActivity.class, true);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


}
