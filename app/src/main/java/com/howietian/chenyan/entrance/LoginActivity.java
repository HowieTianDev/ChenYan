package com.howietian.chenyan.entrance;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.howietian.chenyan.MainActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends BaseActivity {
    @Bind(R.id.tv_register)
    TextView register;
    @Bind(R.id.tv_forget_pwd)
    TextView forgetPwd;
    @Bind(R.id.tb_login)
    Toolbar tbLogin;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.iv_delete)
    ImageView ivDelete;
    @Bind(R.id.et_pwd)
    EditText etPwd;
    @Bind(R.id.iv_pwd)
    ImageView ivPwd;
    @Bind(R.id.btn_login)
    Button btnLogin;

    boolean isShow = true;
    String phone;
    String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_login);
    }

    // 跳转注册
    @OnClick(R.id.tv_register)
    public void register() {
        jumpTo(RegisterActivity.class, false);
    }

    // 跳转忘记密码
    @OnClick(R.id.tv_forget_pwd)
    public void forgetPwd() {
        jumpTo(ForgetPwdActivity.class, false);
    }

    @Override
    public void init() {
        super.init();
        setSupportActionBar(tbLogin);
        initListener();
//      从注册界面跳转过来,自动填写用户名和密码
        if (getIntent() != null) {
            Intent intent = getIntent();
            String usernameF = intent.getStringExtra(ForgetPwdActivity.FROM_FORGET_USERNAME);
            String username = intent.getStringExtra(RegisterActivity.FROM_REGISTER_USERNAME);
            String pwd = intent.getStringExtra(RegisterActivity.FROM_REGISTER_PWD);
            if (!TextUtils.isEmpty(pwd)) {
                etPwd.setText(pwd);
            }
            if (!TextUtils.isEmpty(username)) {
                etPhone.setText(username);
            } else if (!TextUtils.isEmpty(usernameF)) {
                etPhone.setText(usernameF);
            }

        }
    }

    private void initListener() {
        exit();
        inputListener();
    }

    //    设置toolbar上的小叉finish事件
    private void exit() {
        tbLogin.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    @OnClick(R.id.btn_login)
    public void login() {
        if (isCheckData()) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(android.R.style.Theme_Material_Dialog_Alert);
            progressDialog.setMessage("正在登录中...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            final User user = new User();
            user.setPassword(pwd);
            user.setUsername(phone);

            user.login(new SaveListener<User>() {
                @Override
                public void done(User user, BmobException e) {
                    if (e == null) {
                        showToast("登录成功！");
                        jumpTo(MainActivity.class, true);
                        progressDialog.dismiss();
                    } else {
                        showToast("用户名或密码错误！");
                        progressDialog.dismiss();
                    }
                }
            });


        }
    }

    //    检查输入数据的合法性
    public boolean isCheckData() {
        phone = etPhone.getText().toString();
        pwd = etPwd.getText().toString();
        //判断2个输入是否为空
        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("手机号码不能为空");
            return false;
        } else if (TextUtils.isEmpty(pwd)) {
            etPwd.setError("密码不能为空");
            return false;
        } else if (!isPhoneNum(phone)) {
            showToast("手机号码格式错误");
            return false;
        } else if (pwd.length() < 6) {
            showToast("密码长度至少为6位");
            return false;
        }
        return true;
    }

    //利用正则表达式判断手机号码的合法性
    public boolean isPhoneNum(String str) {
        String regExp = "^((13[0-9])|(15[^4])|(18[0,1,2,3,5-9])|(17[0-8])|(147))\\d{8}$";
        Pattern p = Pattern.compile(regExp);
        Matcher m = p.matcher(str);
        return m.matches();
    }

}
