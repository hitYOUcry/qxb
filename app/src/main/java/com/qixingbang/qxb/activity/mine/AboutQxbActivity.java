package com.qixingbang.qxb.activity.mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.common.utils.L;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.common.utils.FileUtil;
import com.qixingbang.qxb.common.utils.SecurityUtil;
import com.qixingbang.qxb.common.utils.ToastUtil;
import com.qixingbang.qxb.dialog.DialogUtil;
import com.qixingbang.qxb.dialog.ProgressDialog;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Z.H. on 2015/10/21 14:22.
 */
public class AboutQxbActivity extends BaseActivity {
    @Bind(R.id.imageView_back)
    ImageView mImageViewBack;
    @Bind(R.id.imageView_share)
    ImageView mImageViewShare;
    @Bind(R.id.textView_tabTip)
    TextView mTextViewTabTip;
    @Bind(R.id.tv_current_version)
    TextView mTvCurrentVersion;
    @Bind(R.id.tv_newest_version)
    TextView mTvNewestVersion;
    @Bind(R.id.rl_version_update)
    RelativeLayout mRlVersionUpdate;
    @Bind(R.id.rl_service_terms)
    RelativeLayout mRlServiceTerms;

    private String downloadUrl = null;
    private String versionName;
    private String newestVersion;
    private static final String TAG = "AboutQxbActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_qxb);
        ButterKnife.bind(this);
        initView();
        initData();
    }

    @Override
    protected void initView() {
        mTextViewTabTip.setText(R.string.about_qxb);
    }

    @Override
    protected void initData() {
        PackageInfo packInfo;
        try {
            packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packInfo.versionName;
            int versionCode = packInfo.versionCode;
            L.d(versionName);
            L.d(versionCode + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        mTvCurrentVersion.setText("当前版本: " + versionName);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtil.getVersionInfo(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(response.toString());
                JSONObject updateJson = response.optJSONObject("update");
                if (updateJson != null) {
                    newestVersion = updateJson.optString("version");
                    if (!TextUtils.isEmpty(versionName)) {
                        int compare = SecurityUtil.versionNameCompare(versionName, newestVersion);
                        if (compare < 0) {
                            mTvNewestVersion.setText("有新版本(" + newestVersion + ")点击更新");
                        } else if (compare > 0) {
                            mTvNewestVersion.setText("开发者测试版");
                        }
                    }
                    downloadUrl = updateJson.optString("url");
                } else {
                    ToastUtil.toast(R.string.non_version_info);
                    return;
                }

                //                try {
                //                    newestVersion = response.getJSONObject("update").getString("version");
                //                    mTvNewestVersion.setText(newestVersion);
                //                    //downloadUrl = response.getJSONObject("update").getString("url");
                //                    downloadUrl = "https://www.baidu.com/link?url=HYLdsiU4EljKoOEYYwpIQhlR6yOOdzcrlaXhJhHSryt3VPh5VtCFAOyMTRdQ-8mmSoQUMHDG0pjd4Hcua02BFpOxKDC-1EdLCGjB7L-poQa&wd=&eqid=d345cfd30001c2660000000256d99197";
                //                } catch (JSONException e) {
                //                    e.printStackTrace();
                //                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, "AboutQxbActivity");
    }

    @OnClick(R.id.rl_version_update)
    public void updateVersion() {
        if (SecurityUtil.versionNameCompare(versionName, newestVersion) >= 0) {
            ToastUtil.toast(R.string.already_newest_version);
            return;
        } else {

            DialogUtil.showTextDialog(this, R.string.update_version, getString(R.string.newest_version) + newestVersion,
                    R.string.update, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            downloadApk();
                        }
                    });
        }
    }

    @OnClick(R.id.rl_service_terms)
    public void serviceTerms() {
        Intent intent = new Intent(this, ServiceTermsActivity.class);
        startActivity(intent);
    }

    //点击返回
    @OnClick(R.id.imageView_back)
    public void back() {
        finish();
    }

    private static final int DOWNLOAD_ERROR = 0;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWNLOAD_ERROR:
                    ToastUtil.toast(R.string.download_error);
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    private void downloadApk() {
        if (downloadUrl != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            final ProgressDialog pd;    //进度条对话框
            pd = new ProgressDialog(this);
            pd.show();
            pd.setTitle(R.string.downloading);
            new Thread() {
                @Override
                public void run() {
                    try {
                        File file = getFileFromServer(pd);
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(Uri.fromFile(file),
                                "application/vnd.android.package-archive");
                        //可能由于签名不同，导致无法覆盖安装
                        //确保正式签名一样
                        startActivity(intent);
                    } catch (Exception e) {
                        Message msg = Message.obtain(null, DOWNLOAD_ERROR);
                        mHandler.sendMessage(msg);
                        e.printStackTrace();
                    } finally {
                        pd.dismiss(); //结束掉进度条对话框
                    }
                }
            }.start();
        } else if (downloadUrl == null) {
            ToastUtil.toast(R.string.download_url_error);
        } else {
            ToastUtil.toast(R.string.sd_card_error);
        }
    }

    private File getFileFromServer(ProgressDialog pd) throws Exception {
        URL url;
        File file = null;
        url = new URL(downloadUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        //获取到文件的大小
        pd.setMax(conn.getContentLength());
        InputStream is = conn.getInputStream();
        if (FileUtil.getQXBExternalFilesDir().isEmpty()) {
            ToastUtil.toast(R.string.non_sd_card);
            return null;
        }
        file = new File(FileUtil.getQXBExternalFilesDir(), "updata.apk");
        FileOutputStream fos = new FileOutputStream(file);
        BufferedInputStream bis = new BufferedInputStream(is);
        byte[] buffer = new byte[1024];
        int len;
        int total = 0;
        while ((len = bis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            total += len;
            //获取当前下载量
            pd.setProgress(total);
        }
        fos.close();
        bis.close();
        is.close();
        return file;
    }
}
