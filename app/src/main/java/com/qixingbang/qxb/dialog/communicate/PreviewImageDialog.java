package com.qixingbang.qxb.dialog.communicate;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qixingbang.qxb.R;
import com.qixingbang.qxb.adapter.communicate.ImageViewPagerAdapter;

import java.util.List;


/**
 * Created by zqj on 2015/12/4 14:43.
 */
public class PreviewImageDialog extends Dialog {

    private RelativeLayout rootView;
    private ViewPager mViewPager;
    private TextView backTextView;

    private ImageViewPagerAdapter mAdapter;
    private List<String> mPicUrls;
    private Context mContext;
    private int mCurrentIndex;

    private boolean mCancelable = true;

    public PreviewImageDialog(Context context, List<String> list) {
        super(context, android.R.style.Theme_Translucent);
        mPicUrls = list;
        mContext = context;
    }

    public PreviewImageDialog(Context context, List<String> list, int index) {
        super(context, android.R.style.Theme_Translucent);
        mPicUrls = list;
        mContext = context;
        mCurrentIndex = index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_showimage);

        rootView = (RelativeLayout) findViewById(R.id.root_view);

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mAdapter = new ImageViewPagerAdapter(mContext, mPicUrls);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentIndex);

        backTextView = (TextView) findViewById(R.id.textView_back);
        backTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        rootView.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mCancelable) {
                    if (event.getX() < mViewPager.getLeft()
                            || event.getX() > mViewPager.getRight()
                            || event.getY() > mViewPager.getBottom()
                            || event.getY() < mViewPager.getTop()) {
                        dismiss();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void dismiss() {
        mAdapter.clean();
        super.dismiss();
    }
}
