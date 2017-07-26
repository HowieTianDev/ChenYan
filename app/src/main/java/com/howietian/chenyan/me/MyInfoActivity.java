package com.howietian.chenyan.me;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.howietian.chenyan.BaseActivity;
import com.howietian.chenyan.MainActivity;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.User;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.http.bean.Api;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyInfoActivity extends BaseActivity {
    @Bind(R.id.iv_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tb_myInfo)
    Toolbar tbMyInfo;
    @Bind(R.id.et_nick)
    EditText etNick;
    @Bind(R.id.rb_boy)
    RadioButton rbBoy;
    @Bind(R.id.rb_girl)
    RadioButton rbGirl;
    @Bind(R.id.et_intro)
    EditText etIntro;
    @Bind(R.id.et_school)
    EditText etSchool;
    @Bind(R.id.et_position)
    EditText etPosition;
    @Bind(R.id.et_like)
    EditText etLike;
    @Bind(R.id.btn_save)
    Button btnSave;
    @Bind(R.id.tv_birthday)
    TextView tvBirthday;
    @Bind(R.id.et_realName)
    EditText etRealName;
    @Bind(R.id.et_phone)
    EditText etPhone;


    private AlertDialog photoDialog;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private static final int CROP_REQUEST = 2;

    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 3;


    private Uri tempFileCameraUri;// 临时拍照保存路径
    private File tempFileCamera; // 临时拍照文件
    private Uri cropFileUri;//临时的裁剪后的照片Uri


    User user = BmobUser.getCurrentUser(User.class);

    BmobFile bf;
    String nickName;
    Boolean gender;
    String intro;
    String school;
    String position;
    String likes;
    String birthday;
    String realName;
    String phone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDatas();

    }

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_my_info);
    }

    /**
     * 初始化相关信息
     */
    private void initDatas() {
        if (user.getAvatar() != null) {
            loadImage(user.getAvatar().getUrl(), ivAvatar);
        }
        etNick.setText(user.getNickName());
        if (user.getGender() != null) {
            if (user.getGender()) {
                rbBoy.setChecked(true);
            } else {
                rbGirl.setChecked(true);
            }
        }

        etIntro.setText(user.getIntro());
        etSchool.setText(user.getSchool());
        etPosition.setText(user.getPosition());
        etLike.setText(user.getLike());

        tvBirthday.setText(user.getBirthday());
        etPhone.setText(user.getMobilePhoneNumber());
        etRealName.setText(user.getRealName());

    }


    @Override
    public void init() {
        super.init();

//        阻止软键盘自动弹出

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setSupportActionBar(tbMyInfo);
        back();
    }

    private void back() {
        tbMyInfo.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * 保存信息按钮
     */
    @OnClick(R.id.btn_save)
    public void saveInfo() {

        nickName = etNick.getText().toString();
        if (rbBoy.isChecked()) {
            gender = true;
        } else {
            gender = false;
        }
        intro = etIntro.getText().toString();
        school = etSchool.getText().toString();
        position = etPosition.getText().toString();
        likes = etLike.getText().toString();
        birthday = tvBirthday.getText().toString();
        realName = etRealName.getText().toString();
        phone = etPhone.getText().toString();

        if (TextUtils.isEmpty(nickName)) {
            showToast("昵称不能为空");
            return;
        } else if (!isStringFormatCorrect(nickName)) {
            showToast("昵称只能为数字、字母、下划线、中文");
            return;
        }

        user.setAvatar(bf);

        user.setNickName(nickName);
        user.setGender(gender);
        user.setIntro(intro);
        user.setPosition(position);
        user.setSchool(school);
        user.setLike(likes);
        user.setBirthday(birthday);
        user.setRealName(realName);
        user.setMobilePhoneNumber(phone);

        user.update(BmobUser.getCurrentUser(User.class).getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("保存成功！");
                } else {
                    showToast("保存失败" + e.getMessage() + e.getErrorCode());
                }
            }
        });


    }

    /**
     * 选择生日按钮
     */
    @OnClick(R.id.tv_birthday)
    public void selectBirth() {
        showDateDialog(tvBirthday);
    }

    private void showDateDialog(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog =
                new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        //      birthday = new Date(i,i1,i2);
                        Date date = new Date(i - 1900, i1, i2);

                        textView.setText(formatDate(date));
                    }
                }, year, month, day);
        dialog.show();

    }

    /**
     * 设置头像按钮
     */
    @OnClick(R.id.iv_avatar)
    public void setAvatar() {
        showDialog();
    }

    /**
     * 选择相机、相册的提示对话框
     */
    private void showDialog() {
        photoDialog = new AlertDialog.Builder(this).create();
        photoDialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        photoDialog.show();

        Window window = photoDialog.getWindow();
        window.setContentView(R.layout.dialog_choose_pic);
        window.setGravity(Gravity.BOTTOM);

        WindowManager.LayoutParams lp = photoDialog.getWindow().getAttributes();
        DisplayMetrics dm = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        lp.width = dm.widthPixels;
//        设置宽度
        photoDialog.getWindow().setAttributes(lp);

        Button btnCamera = (Button) photoDialog.findViewById(R.id.btn_camera);
        Button btnGallery = (Button) photoDialog.findViewById(R.id.btn_picture);
        Button btnCancel = (Button) photoDialog.findViewById(R.id.btn_cancel);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toCamera();
                requestCamera();

            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toGallery();

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                photoDialog.dismiss();
            }
        });

    }

    /**
     * 跳转相机
     */
    private void requestCamera() {

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_TAKE_PHOTO_PERMISSION);
        } else {
            //有权限，直接拍照
            toCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_TAKE_PHOTO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //申请成功，可以拍照
                toCamera();
            } else {
                showToast("CAMERA PERMISSION DENIED");
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void toCamera() {
        photoDialog.dismiss();
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFileCamera = new File(getFilePath() + ".jpg");
        tempFileCameraUri = Uri.fromFile(tempFileCamera);
//        把拍好的照片保存到这个路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, tempFileCameraUri);
        startActivityForResult(intent, CAMERA_REQUEST);

    }


    /**
     * 跳转相册
     */
    private void toGallery() {
        photoDialog.dismiss();
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            从图库中选取图片，会返回图片的Uri
            case GALLERY_REQUEST:
                if (data.getData() != null) {
                    Uri uri = data.getData();
                    cropImage(uri);
                }
                break;
//            使用相机拍照，不会返回图片的Uri
            case CAMERA_REQUEST:
//                打开相机页面后，如果按返回键也会回调，所以需要判断是否拍摄了照片

                if (resultCode == RESULT_OK && tempFileCameraUri != null) {
                    File file = new File(tempFileCameraUri.getPath());
                    if (file.exists()) {
//                        裁剪图片
                        cropImage(tempFileCameraUri);
                    }
                }
                break;
            case CROP_REQUEST:

                if (resultCode == RESULT_OK) {


                    /**
                     * 将图片上传服务器
                     */
                    upLoadAvatar();
                }
                break;
        }
    }

    /**
     * 裁剪图片
     *
     * @param uri
     */
    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
//        图片处于可裁剪状态
        intent.putExtra("crop", "true");
//        aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
//        是否缩放
        intent.putExtra("scale", true);
//        设置图片的大小，提高头像的上传速度
        intent.putExtra("outputX", 500);
        intent.putExtra("outputY", 500);
//        以Uri的方式传递照片
        File cropFile = new File(getFilePath() + "crop.jpg");
        cropFileUri = Uri.fromFile(cropFile);
//        把裁剪好的图片保存到这个路径
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropFileUri);

        startActivityForResult(intent, CROP_REQUEST);


    }

    /**
     * 使用系统当前日期加以调整作为照片的名称
     */
    private String getFilePath() {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/" + dateFormat.format(date);
        return path;
    }


    private void upLoadAvatar() {
        File file = new File(cropFileUri.getPath());
        Log.e("PHOTO",file.getPath());
        bf = new BmobFile(file);
        bf.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    showToast("上传成功！");
                    /**
                     * 上传新的头像后，把之前的头像从服务器上删除
                     */
                    if (user.getAvatar() != null) {
                        BmobFile preAvatar = new BmobFile();
                        preAvatar.setUrl(user.getAvatar().getUrl());
                        preAvatar.delete(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    showToast("之前头像删除成功！");
                                } else {
                                    showToast("之前的头像删除失败");
                                }
                            }
                        });
                    }

                    Log.e("HHH", bf.getUrl());
//                   可以从本地加载图片

                    loadImage(cropFileUri, ivAvatar);
                } else {
                    showToast("上传失败！" + e.getErrorCode() + e.getMessage());
                }
            }
        });


    }


    //利用正则表达式判断只为数字、字母、下划线、中文
    public boolean isStringFormatCorrect(String str) {
        String strPattern = "^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$";
        Pattern p = Pattern.compile(strPattern);
        Matcher m = p.matcher(str);
        return m.matches();
    }

    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


}
