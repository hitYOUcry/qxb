package com.qixingbang.qxb.activity.login.completeUserInfo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.qixingbang.qxb.R;
import com.qixingbang.qxb.activity.mine.clipHeadPortrait.RoundImageView;
import com.qixingbang.qxb.beans.QAccount;
import com.qixingbang.qxb.beans.mine.UserInfoBean;
import com.qixingbang.qxb.server.UrlUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Z.H. on 2015/11/21 13:26.
 */
public class CompleteDetailsActivity extends Activity {
    @Bind(R.id.iv_back)
    ImageView mIvBack;
    @Bind(R.id.iv_head_portrait)
    RoundImageView mIvHeadPortrait;
    @Bind(R.id.tv_nickname)
    TextView mTvNickname;
    @Bind(R.id.tv_sex)
    TextView mTvSex;
    @Bind(R.id.tv_age)
    TextView mTvAge;
    @Bind(R.id.tv_birthday)
    TextView mTvBirthday;
    @Bind(R.id.rl_head_portrait)
    RelativeLayout mRlHeadPortrait;
    @Bind(R.id.rl_nickname)
    RelativeLayout mRlNickname;
    @Bind(R.id.rl_sex)
    RelativeLayout mRlSex;
    @Bind(R.id.rl_age)
    RelativeLayout mRlAge;
    @Bind(R.id.rl_birthday)
    RelativeLayout mRlBirthday;

    private String token;
    private UserInfoBean mUserInfoBean;
    private BitmapUtils mBitmapUtils;

    private AlertDialog.Builder builder;

    private String[] sex = new String[]{"男", "女"};

    private static final int NICKNAME_REQUEST_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_details);
        ButterKnife.bind(this);

        token = QAccount.getToken();
        String detailsUrl = UrlUtil.getUserDetails();
        mBitmapUtils = new BitmapUtils(this);
        RequestParams params = new RequestParams();
        params.addHeader("authorization", token);
        HttpUtils httpUtils = new HttpUtils();
        httpUtils.send(HttpRequest.HttpMethod.POST, detailsUrl, params, new RequestCallBack<String>() {
            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                Gson gson = new Gson();
                mUserInfoBean = gson.fromJson(responseInfo.result, UserInfoBean.class);

                if (mUserInfoBean.result == 200) {
                    mBitmapUtils.display(mIvHeadPortrait, mUserInfoBean.user.icon);
                    mTvNickname.setText(mUserInfoBean.user.nickname);
                    if (mUserInfoBean.user.sex == false) {
                        mTvSex.setText("男");
                    } else {
                        mTvSex.setText("女");
                    }
                    mTvAge.setText(mUserInfoBean.user.age + "");
                    mTvBirthday.setText(mUserInfoBean.user.birthday);
                }
            }

            @Override
            public void onFailure(HttpException e, String s) {

            }
        });
    }

    @OnClick(R.id.rl_head_portrait)
    public void changeHeadPortrait(){

    }
    @OnClick(R.id.rl_nickname)
    public void changeNickname(){
        Intent intent = new Intent(this, ChangeNicknameActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("nickname", mTvNickname.getText().toString());
        intent.putExtras(bundle);
        startActivityForResult(intent, NICKNAME_REQUEST_CODE);
    }
    @OnClick(R.id.rl_sex)
    public void changeSex(){
        builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(sex, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(CompleteDetailsActivity.this, sex[i], Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
                mTvSex.setText(sex[i]);
            }
        });
        builder.create().show();
    }
    @OnClick(R.id.rl_age)
    public void changeAge(){

    }
    @OnClick(R.id.rl_birthday)
    public void changeBirthday(){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case NICKNAME_REQUEST_CODE:
                String nickname = data.getExtras().getString("nickname");
                mTvNickname.setText(nickname);
                break;
        }
    }
}
