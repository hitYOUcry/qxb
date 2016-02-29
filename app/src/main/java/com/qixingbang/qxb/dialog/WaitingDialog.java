package com.qixingbang.qxb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.qixingbang.qxb.R;

/**
 * Created by zqj on 2015/12/16 10:10.
 */
public class WaitingDialog extends Dialog {

    TextView hintText;

    public WaitingDialog(Context context) {
        super(context, android.R.style.Theme_Translucent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_waiting);
        hintText = (TextView) findViewById(R.id.textView);
    }

    public void setHintText(CharSequence text) {
        hintText.setText(text);
    }

    public void setHintText(int resId) {
        hintText.setText(resId);
    }


}
