package com.howietian.chenyan.me.club;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ImageLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class ApplyClubActivity extends BaseActivity {


    @Bind(R.id.tb_apply_club)
    Toolbar tbApplyClub;
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.et_nick)
    EditText etNick;
    @Bind(R.id.et_realName)
    EditText etRealName;
    @Bind(R.id.et_school)
    EditText etSchool;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.et_intro)
    EditText etIntro;

    private User currentUser = BmobUser.getCurrentUser(User.class);
    public static final String FROM_APPLY_CLUB = "from_apply_club";

    @Override
    public void init() {
        super.init();
        initViews();
        initListeners();
    }

    private void initViews() {
        setSupportActionBar(tbApplyClub);
        //阻止软键盘自动弹出
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (currentUser.getAvatar() != null) {
            ImageLoader.with(this, currentUser.getAvatar().getUrl(), ivAvatar);
        }

        if (currentUser.getRealName() != null) {
            etRealName.setText(currentUser.getRealName());
        }

        if (currentUser.getMobilePhoneNumber() != null) {
            etPhone.setText(currentUser.getMobilePhoneNumber());
        }

        if (currentUser.getSchool() != null) {
            etSchool.setText(currentUser.getSchool());
        }
    }


    private void initListeners() {
        back();
    }

    private void back() {
        tbApplyClub.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_apply_club);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apply, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_apply:
                apply();
                break;
        }
        return true;
    }

    private void apply() {
        String clubName = etNick.getText().toString();
        String realName = etRealName.getText().toString();
        String schoolName = etSchool.getText().toString();
        String phoneNum = etPhone.getText().toString();
        String clubIntro = etIntro.getText().toString();
        if (TextUtils.isEmpty(clubName)) {
            etNick.setError("社团名称不能为空");
            return;
        }
        if (TextUtils.isEmpty(realName)) {
            etRealName.setError("真实姓名不能为空");
            return;
        }
        if (TextUtils.isEmpty(schoolName)) {
            etSchool.setError("学校不能为空");
            return;
        }
        if (TextUtils.isEmpty(phoneNum)) {
            etPhone.setError("联系方式不能为空");
            return;
        }

        if (!isPhoneNum(phoneNum)) {
            etPhone.setError("手机号码不合法");
            return;
        }
        if (TextUtils.isEmpty(clubIntro)) {
            etIntro.setError("社团简介不能为空");
            return;
        }


        final String clubId = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        currentUser.setClub(true);
        currentUser.setClubId(clubId);
        currentUser.setNickName(clubName);
        currentUser.setRealName(realName);
        currentUser.setSchool(schoolName);
        currentUser.setMobilePhoneNumber(phoneNum);
        currentUser.setClubProfile(clubIntro);

        currentUser.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("申请成功！");
                    Intent intent = new Intent(ApplyClubActivity.this, ApplySuccessActivity.class);
                    intent.putExtra(FROM_APPLY_CLUB, clubId);
                    jumpTo(intent, true);
                } else {
                    showToast("请求服务器异常" + e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    //利用正则表达式判断手机号码的合法性,已经改好
    private boolean isPhoneNum(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,1,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.find();
    }
}
