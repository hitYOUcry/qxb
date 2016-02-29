package com.qixingbang.qxb.activity.communicate;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.common.utils.BitmapUtil;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.common.utils.LogUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.dialog.WaitingDialog;
import com.qixingbang.qxb.server.MultipartRequest;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.ResponseUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.apache.http.Consts;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AskActivity extends BaseActivity {

    private final String TAG = AskActivity.class.getName();

    private EditText contentEditText;
    ImageView picOne;
    ImageView picTwo;
    ImageView picThree;

    private PopupWindow mPopupWindow;
    /**
     * 标题栏
     */
    ImageView backImageView;
    TextView commitTextView;
    TextView tabTipTextView;

    //相机图库
    private final int REQUEST_GALLERY = 1;
    private final int REQUEST_CAMERA = 2;

    private final int BTN_INDEX_ONE = 0;
    private final int BTN_INDEX_TWO = 1;
    private final int BTN_INDEX_THREE = 2;
    private int mClickedBtnFlag;

    private List<String> mBitmapPath;
    private WaitingDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask);
        initView();
        initData();
    }


    @Override
    public void initView() {
        //返回
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);
        commitTextView = (TextView) findViewById(R.id.textView_commit);
        commitTextView.setVisibility(View.VISIBLE);
        commitTextView.setOnClickListener(this);
        //标题
        tabTipTextView = (TextView) findViewById(R.id.textView_tabTip);
        tabTipTextView.setText(R.string.ask);
        //提问区
        contentEditText = (EditText) findViewById(R.id.editText_content);
        picOne = (ImageView) findViewById(R.id.imageView_one);
        picOne.setOnClickListener(this);
        picTwo = (ImageView) findViewById(R.id.imageView_two);
        picTwo.setOnClickListener(this);
        picThree = (ImageView) findViewById(R.id.imageView_three);
        picThree.setOnClickListener(this);

        waitingDialog = new WaitingDialog(this);
    }

    @Override
    public void initData() {
        mBitmapPath = Arrays.asList("", "", "");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageView_one:
                mClickedBtnFlag = BTN_INDEX_ONE;
                choosePictures();
                break;
            case R.id.imageView_two:
                mClickedBtnFlag = BTN_INDEX_TWO;
                choosePictures();
                break;
            case R.id.imageView_three:
                mClickedBtnFlag = BTN_INDEX_THREE;
                choosePictures();
                break;
            case R.id.imageView_back:
                finish();
                break;
            case R.id.textView_commit:
                submitQuestion();
                break;
            case R.id.tv_select_img_from_album:
                chooseFromGallery();
                break;
            case R.id.tv_select_img_from_camera:
                takePhoto();
                break;
            case R.id.tv_cancel:
                dismissPopWindow();
                break;
            default:
                break;
        }
    }

    private void choosePictures() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return;
        }
        View view = View.inflate(this, R.layout.popupwindow_select_img_source, null);
        mPopupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPopupWindow.showAtLocation(view, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        mPopupWindow.setOutsideTouchable(true);

        TextView tvAlbum = (TextView) view.findViewById(R.id.tv_select_img_from_album);
        TextView tvCamera = (TextView) view.findViewById(R.id.tv_select_img_from_camera);
        TextView tvCancel = (TextView) view.findViewById(R.id.tv_cancel);
        tvAlbum.setOnClickListener(this);
        tvCamera.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
    }

    public void chooseFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, REQUEST_GALLERY);
    }

    private void takePhoto() {
        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(FileUtil.getTakePhotoPath());
        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        if (openCamera.resolveActivity(getPackageManager()) == null) {
            ToastUtil.toast(R.string.no_camera_app);
            return;
        }
        startActivityForResult(openCamera, REQUEST_CAMERA);
    }

    private boolean dismissPopWindow() {
        if (null != mPopupWindow && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                showAndSaveSelectedPic(BitmapUtil.resizeBitmap(BitmapFactory.decodeFile(FileUtil.getTakePhotoPath()), 800));// 图像重塑，节省空间，减少OOM。
                new File(FileUtil.getTakePhotoPath()).delete();//临时拍照的图片就删了，相册选的则不删
            }
        } else if (requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                Uri galleyUri = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(galleyUri,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();
                showAndSaveSelectedPic(BitmapUtil.resizeBitmap(BitmapFactory.decodeFile(picturePath), 800));
            }
        }
        dismissPopWindow();
    }


    public void showAndSaveSelectedPic(Bitmap cameraBitmap) {
        switch (mClickedBtnFlag) {
            case BTN_INDEX_ONE:
                picOne.setImageBitmap(cameraBitmap);
                break;
            case BTN_INDEX_TWO:
                picTwo.setImageBitmap(cameraBitmap);
                break;
            case BTN_INDEX_THREE:
                picThree.setImageBitmap(cameraBitmap);
                break;
            default:
                break;
        }
        saveSelectedBitmap(cameraBitmap);
    }

    /**
     * 提交问题
     */
    private void submitQuestion() {
        if (!QAccount.hasAccount()) {
            ToastUtil.toast("Login Please!");
            return;
        }

        String content = contentEditText.getText().toString();
        if (content.isEmpty()) {
            ToastUtil.toast(R.string.must_have_text);
            return;
        }
        contentEditText.clearFocus();
        startWaitingDialog();
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
        //        entityBuilder.addPart("title", new StringBody(title, ContentType.create("text/plain", Consts.UTF_8)));
        entityBuilder.addPart("content", new StringBody(content, ContentType.create("text/plain", Consts.UTF_8)));
        for (String path : mBitmapPath) {
            if (null != path && !path.isEmpty()) {
                FileBody fileBody = new FileBody(new File(path));
                entityBuilder.addPart("quesPic", fileBody);
            }
        }
        MultipartRequest multipartRequest = new MultipartRequest(Request.Method.POST, UrlUtil.getSendQuestionUrl(), entityBuilder.build(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.optInt("result") == 200) {
                            ToastUtil.toast(R.string.ask_success);
                            dismissPopWindow();
                            finish();
                        } else if (300 == response.optInt("result")) {
                            ToastUtil.toast(R.string.comment_send_failed);
                            dismissPopWindow();
                        }
                        dismissWaitingDialog();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        ResponseUtil.toastError(error);
                        dismissWaitingDialog();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", QAccount.getToken());
                return headers;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
        RequestUtil.getInstance().addToRequestQueue(multipartRequest);
    }

    private void saveSelectedBitmap(Bitmap bitmap) {
        String bitmapFilePath = FileUtil.PATH_QXB + "quesPic" + mClickedBtnFlag;
        File bitmapFile = new File(bitmapFilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
            mBitmapPath.set(mClickedBtnFlag, bitmapFilePath);
        } catch (IOException e) {
            LogUtil.e(TAG, "save bitmap failed");
        }
    }

    private void startWaitingDialog() {
        waitingDialog.show();
        waitingDialog.setHintText(R.string.submitting);
    }

    private void dismissWaitingDialog() {
        if (null != waitingDialog && waitingDialog.isShowing()) {
            waitingDialog.dismiss();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
            mPopupWindow = null;
        }
        return super.onTouchEvent(event);
    }
}
