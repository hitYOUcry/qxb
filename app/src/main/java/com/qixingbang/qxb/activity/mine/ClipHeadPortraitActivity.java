package com.qixingbang.qxb.activity.mine;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.common.utils.L;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.ClipBaseView;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.common.utils.BitmapUtil;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.server.UrlUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Z.H. on 2015/9/8 13:55.
 */
public class ClipHeadPortraitActivity extends BaseActivity {
    private TextView tvTitle;
    private ClipBaseView cbv;
    private Bitmap bitmap;
    private Button btnClipBitmap;
    private String token;
    private Bitmap mBitmap;
    private ImageView backImageView;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                //                Intent intent = new Intent(ClipHeadPortraitActivity.this, SettingActivity.class);
                //                intent.setAction("Head Portrait");

                SettingActivity.mSelectedBitmap = BitmapUtil.resizeBitmap(mBitmap, mBitmap.getWidth() / 2);
                setResult(RESULT_OK);
                ClipHeadPortraitActivity.this.finish();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clip_head_portrait);
        initView();
        initData();

        token = QAccount.getToken();

        String path = getIntent().getStringExtra("imagePath");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 2;
        bitmap = BitmapFactory.decodeFile(path, options);

        int degree = 0;
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null) {
            //读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        }

        //进行图片旋转
        if (degree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), m, true);
        }

        cbv.setImageBitmap(bitmap);
    }

    @Override
    public void initView() {
        tvTitle = (TextView) findViewById(R.id.textView_tabTip);
        cbv = (ClipBaseView) findViewById(R.id.cbv);
        btnClipBitmap = (Button) findViewById(R.id.btn_clip);
        btnClipBitmap.setOnClickListener(this);
        backImageView = (ImageView) findViewById(R.id.imageView_back);
        backImageView.setOnClickListener(this);
    }

    @Override
    public void initData() {
        tvTitle.setText("移动和缩放");
        //        cbv.setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_clip:
                mBitmap = cbv.onClip();
                if (bitmap == null) {
                    L.d("null");
                } else {
                    L.d("have bitmap");
                }

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pushHeadPortraitToServer(mBitmap);
                        mHandler.sendEmptyMessage(0);
                    }
                }).start();
                break;
            case R.id.imageView_back:
                setResult(RESULT_CANCELED);
                break;
            default:
                break;
        }
    }

    private void pushHeadPortraitToServer(Bitmap headBitmap) {

        File dirFile = new File(FileUtil.PATH_QXB);
        if (!dirFile.exists()) {
            dirFile.mkdir();
        }
        String bitmapFilePath = FileUtil.PATH_QXB + "head";
        File bitmapFile = new File(bitmapFilePath);
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(bitmapFile));
            headBitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String path = bitmapFile.getPath();
        if (bitmapFile == null) {
            L.d("bitmap null");
        } else {
            L.d(path);
        }

        String url = UrlUtil.getMyHeadPortrait();

        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        params.addBodyParameter("icon", bitmapFile);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, url, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                L.d(responseInfo.result);
            }

            @Override
            public void onFailure(HttpException e, String s) {
                e.printStackTrace();
                L.d(s);
            }
        });
    }

    public static void start(Activity activity, String imagePath, int requestCode) {
        Intent intent = new Intent(activity, ClipHeadPortraitActivity.class);
        intent.putExtra("imagePath", imagePath);
        activity.startActivityForResult(intent, requestCode);
    }
}
