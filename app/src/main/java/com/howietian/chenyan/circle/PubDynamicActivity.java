package com.howietian.chenyan.circle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.app.MyApp;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.entrance.LoginActivity;
import com.melnykov.fab.FloatingActionButton;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class PubDynamicActivity extends BaseActivity {
    @Bind(R.id.tb_pub_circle)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.ll_choose_type)
    LinearLayout llChooseType;
    @Bind(R.id.tv_type)
    TextView tvType;

    private static final int REQUEST_CODE = 1;
    String content;
    String type;
    User user = BmobUser.getCurrentUser(User.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_pub_dynamic);
    }

    @Override
    public void init() {
        super.init();
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_submit:
                submit();
                break;
        }
        return true;
    }

    private void submit() {

        content = etContent.getText().toString();
        type = tvType.getText().toString();
        if (TextUtils.isEmpty(content)) {
            showToast("动态不能为空哦");
            return;
        }
        if(TextUtils.isEmpty(type)){
            showToast("类型不能为空哦");
            return;
        }

        Dynamic dynamic = new Dynamic();
        dynamic.setContent(content);
        dynamic.setUser(user);
        dynamic.setType(type);
        dynamic.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    showToast("发布成功");
                } else {
                    showToast("发布失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });

    }

    @OnClick(R.id.ll_choose_type)
    public void chooseType() {
        Intent intent = new Intent(this, TypeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            switch (requestCode){
                case REQUEST_CODE:
                    String type = data.getStringExtra(TypeActivity.TYPE);
                    tvType.setText(type);
                    break;
            }
        }
    }


}
