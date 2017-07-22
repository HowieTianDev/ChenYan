package com.howietian.chenyan.entrance;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ForgetPwdActivity extends BaseActivity {


    public static final String FROM_FORGET_USERNAME = "from_forge_username";


    String phone;
    String smsCode;
    String newPwd;
    boolean isShow;

    @Bind(R.id.tb_forget_pwd)
    Toolbar tbForgetPwd;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.et_code)
    EditText etCode;
    @Bind(R.id.tv_requestCode)
    TextView tvRequestCode;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.iv_pwd)
    ImageView ivPwd;
    @Bind(R.id.btn_submit)
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_forget_pwd);
    }

    @Override
    public void init() {
        super.init();
        initListener();
        back();
    }

    private void initListener() {
        setSupportActionBar(tbForgetPwd);
        inputListener();
    }

    // 验证验证码+注册的逻辑
    @OnClick(R.id.btn_submit)
    public void submit() {
        if (checkData()) {
            resetPwd();
        }
    }

    private void resetPwd() {
        BmobUser.resetPasswordBySMSCode(smsCode, newPwd, new UpdateListener() {

            @Override
            public void done(BmobException ex) {
                if (ex == null) {
                    Intent intent = new Intent(ForgetPwdActivity.this,LoginActivity.class);
                    intent.putExtra(FROM_FORGET_USERNAME,phone);
                    jumpTo(intent, true);
                } else {
                    showToast("密码重置失败"+ex.getMessage()+ex.getErrorCode());
                }
            }
        });
    }


    //    点击小叉，删除手机号
    @OnClick(R.id.iv_delete)
    public void delete() {
        etPhone.setText("");
    }


    //    点击小眼睛，密码可见与不可见切换
    @OnClick(R.id.iv_pwd)
    public void showPwd() {
//        明文显示
        if (isShow) {
            etPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            ivPwd.setImageResource(R.drawable.ic_visibility_blue_500_24dp);
        } else {
//         密码显示
            etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
            ivPwd.setImageResource(R.drawable.ic_visibility_off_grey_500_24dp);
        }
        isShow = !isShow;
    }

    //    请求验证码
    @OnClick(R.id.tv_requestCode)
    public void requestSmsCode() {

        requestCode();
    }


    private void back() {
        tbForgetPwd.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    //    请求短信验证码的方法
    private void requestCode() {
        phone = etPhone.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("手机号码不能为空");
            return;
        }
        if (!isPhoneNum(phone)) {
            etPhone.setError("手机号码格式不正确");
            return;
        }
        MyCounter counter = new MyCounter(60000, 1000);
        counter.start();
        BmobSMS.requestSMSCode(phone, "辰言", new QueryListener<Integer>() {
            @Override
            public void done(Integer integer, BmobException e) {
                if (e == null) {
                    showToast("验证码发送成功！");
                } else {
                    if (e.getErrorCode() == 10010) {
                        showToast("您请求验证码过去频繁，请于1小时候重试");
                    } else {
                        showToast("验证码发送失败" + e.getErrorCode() + e.getMessage());
                    }

                }
            }
        });
    }


    //    通过判断输入状态，小眼睛与小叉的动态显示
    private void inputListener() {
        etPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
// 参数分别为：text:输入框中改变后的字符串信息，start：输入框中改变后的字符的起始位置
//            before：输入框中改变前的字符串的位置，默认是0，count：输入框中改变后一共输入的字符数量

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = charSequence.length();
                if (len == 0) {
                    ivPwd.setVisibility(View.INVISIBLE);
                    ivPwd.setImageResource(R.drawable.ic_visibility_off_grey_500_24dp);
                    //                    当密码输入框没有输入时，默认显示不可见的小眼睛以及输入密码不可见
                    etPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                } else {
                    ivPwd.setVisibility(View.VISIBLE);
                }
//                设置光标的位置
                etPwd.setSelection(len);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = charSequence.length();
                if (len == 0) {
                    ivDelete.setVisibility(View.INVISIBLE);
                } else {
                    ivDelete.setVisibility(View.VISIBLE);
                }
                etPhone.setSelection(len);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    //判断输入的合法性
    public boolean checkData() {
        phone = etPhone.getText().toString();
        smsCode = etCode.getText().toString();
        newPwd = etPwd.getText().toString();
        if (TextUtils.isEmpty(phone)) {
            etPwd.setError("手机号码不能为空");
            return false;
        } else if (!isPhoneNum(phone)) {
            etPhone.setError("手机号码格式错误");
            return false;
        } else if (TextUtils.isEmpty(smsCode)) {
            etCode.setError("验证码不能为空");
            return false;
        } else if (TextUtils.isEmpty(newPwd)) {
            etPwd.setError("密码不能为空");
            return false;
        } else if (newPwd.length() < 6) {
            showToast("密码长度至少为6位");
            return false;
        }
        return true;
    }


    //利用正则表达式判断手机号码的合法性,已经改好
    private boolean isPhoneNum(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,1,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.find();
    }




    //    自定义计数器
    class MyCounter extends CountDownTimer {

        public MyCounter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //倒计时过程调用的方法
        @Override
        public void onTick(long l) {
            tvRequestCode.setText("再次发送" + l / 1000 + "s");
            tvRequestCode.setClickable(false);
            tvRequestCode.setBackground(getResources().getDrawable(R.drawable.sms_grey_bg));
        }

        //倒计时结束的方法
        @Override
        public void onFinish() {
            tvRequestCode.setText("重新发送");
            tvRequestCode.setClickable(true);
            tvRequestCode.setBackground(getResources().getDrawable(R.drawable.sms_dark_bg));

        }
    }

}
