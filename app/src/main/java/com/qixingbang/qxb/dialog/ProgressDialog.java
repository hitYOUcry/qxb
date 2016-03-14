package com.qixingbang.qxb.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.common.utils.FileUtil;

/**
 * Created by zqj on 2016/3/14 20:27.
 */
public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);
    }

    public ProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    private SeekBar mSeekBar;
    private TextView mTitleTextView;
    private TextView mHintTextView;
    private int mMax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                validateProgressHint(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        mTitleTextView = (TextView) findViewById(R.id.textView_title);
        mHintTextView = (TextView) findViewById(R.id.textView_progressHint);
    }

    public void setTitle(int titleRes) {
        if (null != mTitleTextView) {
            mTitleTextView.setText(titleRes);
        }
    }

    public void setMax(int max) {
        if (null != mSeekBar) {
            mSeekBar.setMax(max);
        }
        mMax = max;
    }

    public void setProgress(int progress) {
        if (null != mSeekBar) {
            mSeekBar.setProgress(progress);
        }
    }

    private void validateProgressHint(int progress) {
        String progressHint = FileUtil.getDirSize(progress) + "/" + FileUtil.getDirSize(mMax);
        mHintTextView.setText(progressHint);

    }
}
