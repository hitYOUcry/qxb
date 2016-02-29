package com.qixingbang.qxb.activity.mine;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.common.utils.L;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

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
    @Bind(R.id.rl_version_update)
    RelativeLayout mRlVersionUpdate;
    @Bind(R.id.rl_service_terms)
    RelativeLayout mRlServiceTerms;

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
        String versionName = null;
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

    }

    @OnClick(R.id.rl_version_update)
    public void updateVersion(){

    }

    @OnClick(R.id.rl_service_terms)
    public void serviceTerms(){

    }

    //点击返回
    @OnClick(R.id.imageView_back)
    public void back() {
        finish();
    }
}
