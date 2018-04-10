package com.howietian.chenyan.publish;


import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.howietian.chenyan.BaseFragment;
import com.howietian.chenyan.R;
import com.howietian.chenyan.entities.MActivity;
import com.howietian.chenyan.entities.User;
import com.howietian.chenyan.utils.ImageLoader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class PubActivityFragment extends BaseFragment {


    @Bind(R.id.et_title)
    EditText etTitle;
    @Bind(R.id.et_holder)
    EditText etHolder;
    @Bind(R.id.et_place)
    EditText etPlace;
    @Bind(R.id.et_activity_time)
    EditText etActivityTime;
    @Bind(R.id.et_phone)
    EditText etPhone;
    @Bind(R.id.tv_show_time)
    TextView tvShowTime;
    @Bind(R.id.btn_select_time)
    Button btnSelectTime;
    @Bind(R.id.et_content)
    EditText etContent;
    @Bind(R.id.iv_photo)
    ImageView ivPhoto;


    private AlertDialog photoDialog;
    private static final int CAMERA_REQUEST = 0;
    private static final int GALLERY_REQUEST = 1;
    private static final int CROP_REQUEST = 2;
    private static final int REQUEST_TAKE_PHOTO_PERMISSION = 3;

    private Uri tempFileCameraUri;// 临时拍照保存路径
    private File tempFileCamera; // 临时拍照文件
    private Uri cropFileUri;//临时的裁剪后的照片Uri
    private String deadline;
    final User user = BmobUser.getCurrentUser(User.class);
    BmobFile bf;

    public PubActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public View createMyView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pub_activity, container, false);
    }

    @Override
    public void init() {
        super.init();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }


    /**
     * 选择时间的对话框
     */
    @OnClick(R.id.btn_select_time)
    public void selectTime() {
        showDateDialog(tvShowTime);
    }

    private void showDateDialog(final TextView textView) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dialog =
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

                        Date date = new Date(i - 1900, i1, i2);
                        textView.setText(formatDate(date));
                    }
                }, year, month, day);
        dialog.show();
    }


    @OnClick(R.id.iv_photo)
    public void setPhoto() {
        showDialog();
    }


    /**
     * 选择相机、相册的提示对话框
     */
    private void showDialog() {
        photoDialog = new AlertDialog.Builder(getContext()).create();
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


    public void submit() {
        final String title = etTitle.getText().toString();
        final String content = etContent.getText().toString();
        final String deadline = tvShowTime.getText().toString();
        final String holder = etHolder.getText().toString();
        final String place = etPlace.getText().toString();
        final String activityTime = etActivityTime.getText().toString();
        final String phone = etPhone.getText().toString();

        final MActivity mActivity = new MActivity();

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("标题不能为空");
            return;
        } else if (TextUtils.isEmpty(holder)) {
            showToast("主办方不能为空");
            return;
        } else if (TextUtils.isEmpty(place)) {
            showToast("活动地点不能为空");
            return;
        } else if (TextUtils.isEmpty(activityTime)) {
            showToast("活动时间不能为空");
            return;
        } else if (TextUtils.isEmpty(phone)) {
            showToast("联系方式不能为空");
            return;
        } else if (TextUtils.isEmpty(deadline)) {
            showToast("请选择截至时间");
            return;
        } else if (TextUtils.isEmpty(content)) {
            etContent.setError("活动描述不能为空");
            return;
        } else if (cropFileUri == null) {
            showToast("请选择一张图片");
            return;
        }

        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在上传...");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.show();

        File file = new File(cropFileUri.getPath());
        bf = new BmobFile(file);
        bf.uploadblock(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                   // showToast("图片上传成功！");
                    /**
                     * 用当前的系统时间设置上传时间
                     */

                    Date date = new Date();
                    String upTime = formatDate(date);
                    mActivity.setUpTime(upTime);

                    mActivity.setTitle(title);

                    mActivity.setDeadline(deadline);
                    mActivity.setContent(content);
                    mActivity.setPhoto(bf);
                    mActivity.setCommentNum(0);
                    mActivity.setLikeNum(0);
                    mActivity.setCurrentUser(user);
                    mActivity.setHolder(holder);
                    mActivity.setPlace(place);
                    mActivity.setActivityTime(activityTime);
                    mActivity.setPhone(phone);

                    mActivity.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                showToast("提交成功");
                                dialog.dismiss();
                                getActivity().finish();
                            } else {
                                showToast("提交失败" + e.getMessage() + e.getErrorCode());
                                dialog.dismiss();
                            }
                        }
                    });
                } else {

                    showToast("图片上传失败！" + e.getErrorCode() + e.getMessage());
                    dialog.dismiss();
                }
            }
        });


    }



    /**
     * 跳转相机
     */
    private void requestCamera() {

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //申请权限，REQUEST_TAKE_PHOTO_PERMISSION是自定义的常量
            ActivityCompat.requestPermissions(getActivity(),
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
//            从图库中选取图片，会返回图片的Uri
            case GALLERY_REQUEST:
                if (data != null) {
                    if (data.getData() != null) {
                        Uri uri = data.getData();
                        cropImage(uri);
                    }
                }

                break;
//            使用相机拍照，不会返回图片的Uri
            case CAMERA_REQUEST:
//                打开相机页面后，如果按返回键也会回调，所以需要判断是否拍摄了照片

                if (resultCode == getActivity().RESULT_OK && tempFileCameraUri != null) {
                    File file = new File(tempFileCameraUri.getPath());
                    if (file.exists()) {
//                        裁剪图片
                        cropImage(tempFileCameraUri);
                    }
                }
                break;
            case CROP_REQUEST:

                if (resultCode == RESULT_OK) {

                    ImageLoader.with(getContext(), cropFileUri, ivPhoto);
                    /**
                     * 将图片上传服务器
                     */
                    // upLoadAvatar();
                }
                break;
        }
    }


    private void upLoadAvatar() {


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
        intent.putExtra("aspectX", 4);
        intent.putExtra("aspectY", 3);
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


    public static String formatDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
