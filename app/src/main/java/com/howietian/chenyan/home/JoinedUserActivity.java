package com.howietian.chenyan.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.adapters.UserAdapter;
import com.howietian.chenyan.app.Constant;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ExcelUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class JoinedUserActivity extends BaseActivity {

    private static final int REQUEST_STORAGE = 0;
    @Bind(R.id.rv_user)
    RecyclerView recyclerView;
    @Bind(R.id.swipeLayout_user)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.tb_joined_user)
    Toolbar toolbar;
    @Bind(R.id.tv_joinedNum)
    TextView joinedNum;

    private List<User> users = new ArrayList<>();
    private LinearLayoutManager manager;
    private UserAdapter adapter;
    private MActivity mActivity;
    // Excel的标题
    private static String[] title = {"编号", "姓名", "学校", "联系方式", "辰言ID", "注册手机号"};
    private File file;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * TODO:不要在这个方法里做其他操作
     */
    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_joined_user);
    }

    @Override
    public void init() {
        super.init();
        initDatas();
        initViews();
    }

    private void initDatas() {
        if (getIntent() != null) {
            String msg = getIntent().getStringExtra(Constant.FROM_ACTIVIRY);
            mActivity = new Gson().fromJson(msg, MActivity.class);
        }
        queryJoinedUser();

    }

    private void initViews() {

        swipeRefreshLayout.setRefreshing(true);

        setSupportActionBar(toolbar);
        adapter = new UserAdapter(this, users);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        back();

        refresh();
    }

    private void back() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void requestExportExcel() {

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        } else {
            //有权限，直接导出Excel
            exportExcel();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以导出Excel
                exportExcel();
            } else {
                showToast("STORAGE PERMISSION DENIED");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    /**
     * 渲染菜单文件
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_export, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export:
                if (users.size() != 0) {
                    requestExportExcel();
                } else {
                    showToast("当前用户为0，无法导出");
                }
                break;
        }
        return true;
    }

    private void refresh() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                queryJoinedUser();
            }
        });
    }

    private void queryJoinedUser() {
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereRelatedTo("join", new BmobPointer(mActivity));
        query.findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (users != null) {
                            users.clear();
                        }
                        users.addAll(list);
                        adapter.notifyDataSetChanged();
//                        并设置人数
                        joinedNum.setText(list.size() + "");
                    } else {
                        showToast("服务器没有数据");
                    }
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    showToast("访问服务器失败，稍后重试" + e.getMessage() + e.getErrorCode());
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    /**
     * 导出Excel文件
     */
    public void exportExcel() {
        file = new File(getSDPath() + "/" + "辰言");
        makeDir(file);
        ExcelUtils.initExcel(file.toString() + "/" + mActivity.getTitle() + "报名表.xls", title);
        fileName = getSDPath() + "/" + "辰言" + "/" + mActivity.getTitle() + "报名表.xls";
        ExcelUtils.writeObjListToExcel(transferData(), fileName, this);
    }

    /**
     * 将数据集合 转化为ArrayList<String>
     *
     * @return
     */
    private ArrayList<ArrayList<String>> transferData() {
        ArrayList<ArrayList<String>> joinExcel = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            int num = i + 1;
            ArrayList<String> beanList = new ArrayList<>();
            beanList.add(num + "");
            beanList.add(user.getRealName());
            beanList.add(user.getSchool());
            beanList.add(user.getMobilePhoneNumber());
            beanList.add(user.getNickName());
            beanList.add(user.getUsername());
            joinExcel.add(beanList);
        }

        return joinExcel;
    }

    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        String dir = sdDir.toString();
        return dir;
    }

    public void makeDir(File dir) {
        if (!dir.getParentFile().exists()) {
            makeDir(dir.getParentFile());
        }
        dir.mkdir();
    }


}
