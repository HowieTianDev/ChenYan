package com.howietian.chenyan.personpage;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.PageAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
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
    @Bind(R.id.tv_focus)
    TextView tvFocus;
    @Bind(R.id.tv_join)
    TextView tvJoin;

    private User dynamicAuthor;
    private User user = BmobUser.getCurrentUser(User.class);
    private PageAdapter adapter;
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> titles = new ArrayList<>();
    private ArrayList<String> focusId = new ArrayList<>();
    //    判断关注的标志
    private boolean isFocus = false;
    //    判断加入的标志
    private boolean isJoin = false;


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
        initDatas();
        initViews();
        initListeners();
    }

    private void initDatas() {

        if (getIntent() != null) {
            String userMsg = getIntent().getStringExtra(Constant.TO_PERSON_PAGE);
            dynamicAuthor = new Gson().fromJson(userMsg, User.class);
        }

        PersonActivityFragment activityFragment = PersonActivityFragment.newInstance("person_activity_fragment");
        PersonDynamicFragment dynamicFragment = PersonDynamicFragment.newInstance("person_dynamic_fragment");
        activityFragment.setCurrentUser(dynamicAuthor);
        dynamicFragment.setCurrentUser(dynamicAuthor);
        fragments.add(activityFragment);
        fragments.add(dynamicFragment);
        titles.add("活动");
        titles.add("动态");
        adapter = new PageAdapter(getSupportFragmentManager(), fragments, titles);
        viewpager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewpager);
    }

    private void initViews() {
        setSupportActionBar(toolbar);
        if (dynamicAuthor.getAvatar() != null) {
            ImageLoader.with(this, dynamicAuthor.getAvatar().getUrl(), avatar);
        }
        tvNickName.setText(dynamicAuthor.getNickName());
        tvFan.setText(dynamicAuthor.getFanNum().toString());
        if (dynamicAuthor.getFocusIds() != null) {
            tvFollow.setText(dynamicAuthor.getFocusIds().size() + "");
        } else {
            tvFollow.setText(0 + "");
        }
        tvPublish.setText(dynamicAuthor.getDynamicNum().toString());

        showFocus();
        queryFanNum();

    }

    private void initListeners() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //    判断当前用户是否显示关注以及显示关注还是已关注
    private void showFocus() {
//        自己查看自己的主页，不显示关注
        if (dynamicAuthor.getObjectId().equals(user.getObjectId())) {
            tvFocus.setVisibility(View.GONE);
            tvJoin.setVisibility(View.GONE);
        } else {
            tvFocus.setVisibility(View.VISIBLE);
            if(dynamicAuthor.getClub()!=null){
                if (dynamicAuthor.getClub()) {
                    tvJoin.setVisibility(View.VISIBLE);
                } else {
                    tvJoin.setVisibility(View.GONE);
                }
            }



            if (user.getFocusIds() != null) {
                for (String focusId : user.getFocusIds()) {
                    if (focusId.equals(dynamicAuthor.getObjectId())) {
                        tvFocus.setText("已关注");
                        isFocus = true;
                        break;
                    }
                }
            } else {
                tvFocus.setText("关注");
                isFocus = false;
            }

            if (dynamicAuthor.getMemberIds() != null) {
                if (dynamicAuthor.getMemberIds().contains(user.getObjectId())) {
                    isJoin = true;
                    tvJoin.setText("已加入");
                }
            } else {
                tvJoin.setText("加入");
                isJoin = false;
            }

        }
    }

    /**
     * 查询粉丝数目
     */

    private void queryFanNum() {
        BmobQuery<User> query = new BmobQuery<>();
        query.include("focus");
        BmobQuery<BmobUser> innerQuery = new BmobQuery<>();
        innerQuery.addWhereEqualTo("objectId", dynamicAuthor.getObjectId());
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


    @OnClick(R.id.tv_focus)
    public void focus() {
        if (!isFocus) {
            toFocus();
        } else {
            cancelFocus();
        }
    }

    // 加入社团
    @OnClick(R.id.tv_join)
    public void join() {
        if (!isJoin) {
            showToast("请等待审核通过！");
            BmobPushManager bmobPushManager = new BmobPushManager();

            BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
            String minstallationId = dynamicAuthor.getInstallationId();
            query.addWhereEqualTo("installationId", minstallationId);
            bmobPushManager.setQuery(query);
            Log.e("bmobInstalltionID", minstallationId);
            Log.d("bmob发送", new Gson().toJson(user, User.class));
            bmobPushManager.pushMessage(new Gson().toJson(user, User.class), new PushListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        showToast("发送成功！");
                    } else {
                        showToast("发送失败！");
                    }
                }
            });
        }

    }

    //设置关注的方法
    private void toFocus() {

//        将动态作者的ID添加
        if (user.getFocusIds() != null) {
            focusId = user.getFocusIds();
        }
        BmobRelation relation = new BmobRelation();
        relation.add(dynamicAuthor);
        user.setFocus(relation);
        focusId.add(dynamicAuthor.getObjectId());
        user.setFocusIds(focusId);
        tvFocus.setText("已关注");
        isFocus = true;
        String nums = tvFan.getText().toString();
        Integer num = Integer.valueOf(nums);
        Integer numshow = num + 1;
        tvFan.setText(numshow.toString());
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e("个人主页", "关注成功！");
                } else {
                    showToast("关注失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }

    //    取消关注的方法
    private void cancelFocus() {
        BmobRelation relation = new BmobRelation();
        relation.remove(dynamicAuthor);
        user.setFocus(relation);
        //      移除当前用户的ID
        if (user.getFocusIds() != null) {
            focusId = user.getFocusIds();
        }
        focusId.remove(dynamicAuthor.getObjectId());
        user.setFocusIds(focusId);
        tvFocus.setText("关注");
        String nums = tvFan.getText().toString();
        Integer num = Integer.valueOf(nums);
        Integer numshow = num - 1;
        tvFan.setText(numshow.toString());
        isFocus = false;
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    Log.e("关注", "取消关注成功！");
                } else {
                    showToast("取消关注失败！" + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }
}
