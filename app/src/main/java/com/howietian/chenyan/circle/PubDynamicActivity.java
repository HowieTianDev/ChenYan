package com.howietian.chenyan.circle;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.Dynamic;
import com.howietian.chenyan.entities.User;
import com.lzy.ninegrid.ImageInfo;
import com.lzy.ninegrid.NineGridView;
import com.lzy.ninegrid.preview.NineGridViewClickAdapter;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class PubDynamicActivity extends BaseActivity {
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 0;
    private static final int REQUEST_CODE_CHOOSE = 2;
    @Bind(R.id.tb_pub_circle)
    Toolbar toolbar;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.ll_choose_type)
    LinearLayout llChooseType;
    @Bind(R.id.tv_type)
    TextView tvType;
    @Bind(R.id.iv_photo)
    ImageView iv_photo;
    @Bind(R.id.nineGridView)
    NineGridView nineGridView;

    private static final int REQUEST_CODE = 1;

    List<Uri> mSelected;
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
        if (TextUtils.isEmpty(type)) {
            showToast("类型不能为空哦");
            return;
        }


        final Dynamic dynamic = new Dynamic();
        dynamic.setContent(content);
        dynamic.setUser(user);
        dynamic.setType(type);

        if (mSelected != null) {
            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("正在上传...");
            progress.setCanceledOnTouchOutside(false);
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.show();
            List<String> pathList = new ArrayList<String>();
            ArrayList<String> imageUrls = new ArrayList<>();
            for (int i = 0; i < mSelected.size(); i++) {
//                将URi转化为绝对路径
                pathList.add(getFilePathFromContentUri(mSelected.get(i)));
                Log.e("PHOTO", getFilePathFromContentUri(mSelected.get(i)));

            }
//            list 2 String
            final String[] paths = pathList.toArray(new String[pathList.size()]);

//            批量上传图片

            BmobFile.uploadBatch(paths, new UploadBatchListener() {
                @Override
                public void onSuccess(List<BmobFile> list, List<String> list1) {
                    if (list1.size() == paths.length) {
                        dynamic.setImageUrls(list1);
                        dynamic.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                if (e == null) {
                                    showToast("上传成功！");
                                    updateUserDynamicNum();
                                    progress.dismiss();
                                } else {
                                    showToast("上传失败！" + e.getMessage() + e.getErrorCode());
                                    progress.dismiss();
                                }
                            }
                        });
                    }
                }

                @Override
                public void onProgress(int curindex, int curpercent, int total, int totalpercent) {

                    progress.setProgress(totalpercent);
                }

                @Override
                public void onError(int i, String s) {
                    showToast("上传失败" + i + s);
                    progress.dismiss();
                }
            });
        } else {
            dynamic.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        showToast("发布成功");
                        updateUserDynamicNum();
                    } else {
                        showToast("发布失败" + e.getMessage() + e.getErrorCode());
                    }
                }
            });
        }


    }

    // 更新用户动态数目，并直接关掉发布页面
    private void updateUserDynamicNum() {
        int num = user.getDynamicNum();
        num++;
        user.setDynamicNum(num);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    finish();
                } else {
                    showToast("动态数目更新失败");
                }
            }
        });
    }

    public String getFilePathFromContentUri(Uri selectedVideoUri) {
        String filePath;
        String[] filePathColumn = {MediaStore.MediaColumns.DATA};
        Cursor cursor = this.getContentResolver().query(selectedVideoUri, filePathColumn, null, null, null);
//      也可用下面的方法拿到cursor
//      Cursor cursor = this.context.managedQuery(selectedVideoUri, filePathColumn, null, null, null);

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        filePath = cursor.getString(columnIndex);
        cursor.close();
        return filePath;
    }


    @OnClick(R.id.ll_choose_type)
    public void chooseType() {
        Intent intent = new Intent(this, TypeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @OnClick(R.id.iv_photo)
    public void choosePhoto() {
        requestGallery();
    }

    /**
     * 跳转相机
     */
    private void requestGallery() {

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_TAKE_PHOTO_PERMISSION);
        } else {
            //有权限，直接拍照
            toGallery();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                toGallery();
            } else {
                showToast("CAMERA PERMISSION DENIED");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void toGallery() {
        Matisse.from(PubDynamicActivity.this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(9)
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE:
                    String type = data.getStringExtra(TypeActivity.TYPE);
                    tvType.setText(type);
                    break;
                case REQUEST_CODE_CHOOSE:
                    mSelected = Matisse.obtainResult(data);
                    List<ImageInfo> imageInfoList = new ArrayList<>();
                    Log.e("PHOTO", mSelected.toString());
                    for (int i = 0; i < mSelected.size(); i++) {
                        ImageInfo info = new ImageInfo();
                        info.setThumbnailUrl(mSelected.get(i).toString());
                        info.setBigImageUrl(mSelected.get(i).toString());
                        imageInfoList.add(info);
                    }
                    nineGridView.setAdapter(new NineGridViewClickAdapter(this, imageInfoList));
                    break;
            }
        }
    }


}
