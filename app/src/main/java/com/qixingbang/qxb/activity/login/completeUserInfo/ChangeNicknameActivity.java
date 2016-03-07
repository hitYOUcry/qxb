package com.qixingbang.qxb.activity.login.completeUserInfo;

import android.app.Activity;
import android.os.Bundle;

import com.qixingbang.qxb.R;

/**
 * Created by Z.H. on 2015/11/21 15:52.
 */
public class ChangeNicknameActivity extends Activity {
//    @Bind(R.id.edt_nickname)
//    EditText mEdtNickname;
//    @Bind(R.id.btn_save_nickname)
//    Button mBtnSaveNickname;

    private static final int NICKNAME_RESULT_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_change_nickname);
//        ButterKnife.bind(this);
//
//        String nickname = getIntent().getExtras().getString("nickname");
//        mEdtNickname.setText(nickname);
//        mEdtNickname.setSelectAllOnFocus(true);
    }

//    @OnClick(R.id.btn_save_nickname)
//    public void saveNickname(){
//
//        Intent intent = new Intent();
//        Bundle bundle = new Bundle();
//        bundle.putString("nickname", mEdtNickname.getText().toString());
//        intent.putExtras(bundle);
//        setResult(NICKNAME_RESULT_CODE, intent);
//        this.finish();
//    }
}
