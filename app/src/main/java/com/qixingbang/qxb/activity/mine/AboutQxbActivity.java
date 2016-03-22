package com.qixingbang.qxb.activity.mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.common.utils.L;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;
import com.qixingbang.qxb.server.RequestUtil;
import com.qixingbang.qxb.server.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;

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
        mTvCurrentVersion.setText("当前版本 " + getCurrentVersionInfo());
    }

    private String getCurrentVersionInfo() {
        //获取当前APP的版本信息
        PackageInfo packInfo;
        try {
            packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName= packInfo.versionName;
            int versionCode = packInfo.versionCode;
            L.d(versionName);
            L.d(versionCode + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    @Override
    protected void initData() {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                UrlUtil.getVersionInfo(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                L.d(response.toString());
                try {
                    newestVersion = response.getJSONObject("update").getString("version");
                    mTvNewestVersion.setText(newestVersion);
                    //downloadUrl = response.getJSONObject("update").getString("url");
                    downloadUrl = "https://www.baidu.com/link?url=HYLdsiU4EljKoOEYYwpIQhlR6yOOdzcrlaXhJhHSryt3VPh5VtCFAOyMTRdQ-8mmSoQUMHDG0pjd4Hcua02BFpOxKDC-1EdLCGjB7L-poQa&wd=&eqid=d345cfd30001c2660000000256d99197";
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        RequestUtil.getInstance().addToRequestQueue(request, "AboutQxbActivity");
    }

    @OnClick(R.id.rl_version_update)
    public void updateVersion(){
        if (versionName.equals(newestVersion)){
            Toast.makeText(this, "已经升级至最新版本", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "马上下载昂，别急", Toast.LENGTH_SHORT).show();
            Toast.makeText(this, "更TMD，更新，体验最新版本！", Toast.LENGTH_SHORT).show();
            //检查url不为空
            //检查是否挂载SD卡
            //url不为空， SD卡可用  执行下面的下载
            if(downloadUrl != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                //文件目标路径（根目录）  apk名字
                String target = Environment.getExternalStorageDirectory() + "/qxb" + "_" + newestVersion + ".apk";
                //联网更新
                RequestParams params = new RequestParams(downloadUrl);
                x.http().get(params, new Callback.CommonCallback<File>() {
                    @Override
                    public void onSuccess(File result) {
                        //安装apk
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_DEFAULT);
                        intent.setDataAndType(Uri.fromFile(result),
                                "application/vnd.android.package-archive");
                        //可能由于签名不同，导致无法覆盖安装
                        //确保正式签名一样
                        startActivity(intent);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Log.d("error", ex.toString());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
            }else if (downloadUrl == null){
                Log.d(TAG, "错误的下载地址");
            }else {
                Log.d(TAG, "SD不可用");
            }
        }
    }

    @OnClick(R.id.rl_service_terms)
    public void serviceTerms(){
        Intent intent = new Intent(this, ServiceTermsActivity.class);
        startActivity(intent);
    }

    //点击返回
    @OnClick(R.id.imageView_back)
    public void back() {
        finish();
    }
}
