package com.qixingbang.qxb.activity.mine;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

import java.io.File;

/**
 * Created by Z.H. on 2015/8/19 15:55.
 */
public class VersionUpdateActivity extends BaseActivity{
    //TODO:等待项目经理更新页面的逻辑
    //TODO:等待UI设计APP原型图
    //TODO:下载部分的错误信息提示标准

    private static final String TAG = "VersionUpdateActivity";
    private static final String SERVER_URL = "";
    private static final String VERSION_URL = SERVER_URL + "";
    //下载新版本apk的Url  通过Gson解析得到
    private String downloadUrl = null;
    private String newestVersion;

    private Button btnUpdateVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version_update);
        //先联网后台检查更新  赋值给tv   使用httpConnection
        //安装版本与最新版本 不相同  点击更新
        //安装版本与最新版本 相同  点击提示“已经是最新版本”
        initView();
        initData();
    }

    @Override
    public void initView() {
        btnUpdateVersion = (Button) findViewById(R.id.btn_update_version);
        btnUpdateVersion.setOnClickListener(this);
    }

    @Override
    public void initData() {
        //获取当前APP的版本信息
        PackageInfo packInfo;
        String versionName;
        try {
            packInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName= packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_update_version:
                Toast.makeText(this, "更TMD，更新，体验最新版本！", Toast.LENGTH_SHORT).show();
                //检查url不为空
                //检查是否挂载SD卡
                //url不为空， SD卡可用  执行下面的下载
                if(downloadUrl != null && Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                    //文件目标路径（根目录）  apk名字
                    String target = Environment.getExternalStorageDirectory() + "/qxb" + "_" + newestVersion + ".apk";
                    //联网更新
                    HttpUtils httpUtils = new HttpUtils();
                    httpUtils.download(downloadUrl, target, new RequestCallBack<File>() {

                        /**
                         * 下载中。。
                         * 一般用于进度条显示下载进度
                         * @param total  总下载字节数
                         * @param current   当前已下载量
                         * @param isUploading   是否上传
                         */
                        @Override
                        public void onLoading(long total, long current, boolean isUploading) {
                            super.onLoading(total, current, isUploading);
                            System.out.print("正在下载：" + current * 100 / total + "%");
                        }


                        /**
                         * 下载成功
                         * @param responseInfo
                         */
                        @Override
                        public void onSuccess(ResponseInfo<File> responseInfo) {
                            //安装apk
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addCategory(Intent.CATEGORY_DEFAULT);
                            intent.setDataAndType(Uri.fromFile(responseInfo.result),
                                    "application/vnd.android.package-archive");
                            //可能由于签名不同，导致无法覆盖安装
                            //确保正式签名一样
                            startActivity(intent);
                        }


                        /**
                         * 下载失败
                         * @param e
                         * @param s
                         */
                        @Override
                        public void onFailure(HttpException e, String s) {
                            //错误信息提示
                        }
                    });
                }else if (downloadUrl == null){
                    Log.d(TAG, "错误的下载地址");
                }else {
                    Log.d(TAG, "SD不可用");
                }
                break;
        }
    }
}
