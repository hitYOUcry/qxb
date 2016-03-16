package com.qixingbang.qxb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.qixingbang.qxb.R;

/**
 * Created by zqj on 2016/2/29 15:18.
 */
public class TextDialog extends Dialog {

    public TextDialog(Context context) {
        super(context);
    }

    TextView titleTextView;
    TextView contentTextView;
    Button confirmButton;
    Button cancelButton;

    private View.OnClickListener mConfirmListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_text);
        titleTextView = (TextView) findViewById(R.id.textView_title);
        contentTextView = (TextView) findViewById(R.id.textView_content);
        cancelButton = (Button) findViewById(R.id.button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        confirmButton = (Button) findViewById(R.id.button_confirm);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mConfirmListener)
                    mConfirmListener.onClick(v);
                dismiss();
            }
        });
    }

    public void setTitle(CharSequence title) {
        titleTextView.setText(title);
    }

    public void setTitle(int resId) {
        titleTextView.setText(resId);
    }

    public void setContent(CharSequence title) {
        contentTextView.setText(title);
    }

    public void setContent(int resId) {
        contentTextView.setText(resId);
    }

    public void setConfirmText(CharSequence title) {
        confirmButton.setText(title);
    }

    public void setConfirmText(int resId) {
        confirmButton.setText(resId);
    }

    public void setConfirmListener(View.OnClickListener listener) {
        mConfirmListener = listener;

    }
}
