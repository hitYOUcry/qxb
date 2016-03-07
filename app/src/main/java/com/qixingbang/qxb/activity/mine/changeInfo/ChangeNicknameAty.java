package com.qixingbang.qxb.activity.mine.changeInfo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.base.activity.BaseActivity;

/**
 * Created by Jeongho on 16/3/7.
 */
public class ChangeNicknameAty extends BaseActivity {

    private TextView mTitleTv;
    private EditText mNameEdt;
    private Button mSaveBtn;
    private static final int NICKNAME_RESULT_CODE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nickname);
        initView();
        initData();
        initListener();
    }

    @Override
    protected void initView() {
        mTitleTv = (TextView) findViewById(R.id.textView_tabTip);
        mNameEdt = (EditText) findViewById(R.id.edt_nickname);
        mSaveBtn = (Button) findViewById(R.id.btn_save_nickname);
    }

    @Override
    protected void initData() {
        mTitleTv.setText("更改昵称");
        mNameEdt.setText(getIntent().getStringExtra("nickname"));
    }

    private void initListener() {
        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("nickname", mNameEdt.getText().toString());
                intent.putExtras(bundle);
                setResult(NICKNAME_RESULT_CODE, intent);
                //TODO：上传服务器
                ChangeNicknameAty.this.finish();
            }
        });
    }
}
