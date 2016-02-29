package com.qixingbang.qxb.common.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.qixingbang.qxb.R;

/**
 * Created by Z.H.Jeongho on 2015/12/1.
 */
public class SlideSwitch extends View{

    private static final int SWITCH_ON = 0;
    private static final int SWITCH_OFF = 1;

    private String textOn;
    private String textOff;
    private Bitmap backgroundOn;
    private Bitmap backgroundOff;
    private Bitmap switchButton;
    private int switchHeight;
    private int switchWidth;
    private int mCurrentStatus = SWITCH_OFF;
    private boolean mHasScrolled = false;

    public SlideSwitch(Context context) {
        this(context, null);
    }

    public SlideSwitch(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SlideSwitch);

        try {
            textOn = typedArray.getString(R.styleable.SlideSwitch_text_on);
            textOff = typedArray.getString(R.styleable.SlideSwitch_text_off);
            backgroundOn = ((BitmapDrawable)typedArray.getDrawable(R.styleable.SlideSwitch_background_on)).getBitmap();
            backgroundOff = ((BitmapDrawable)typedArray.getDrawable(R.styleable.SlideSwitch_background_off)).getBitmap();
            switchButton = ((BitmapDrawable)typedArray.getDrawable(R.styleable.SlideSwitch_switch_button)).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            typedArray.recycle();
        }

        int backgroundOnWidth = backgroundOn.getWidth();
        int backgroundOnHeight = backgroundOn.getHeight();
        int backgroundOffWidth = backgroundOff.getWidth();
        int backgroundOffHeight = backgroundOff.getHeight();
        Log.d("SlideSwitch", backgroundOnWidth + " " +
                 backgroundOnHeight + " " + backgroundOffWidth + " " + backgroundOffHeight);

        switchHeight = backgroundOnHeight;
        switchWidth = backgroundOnWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mCurrentStatus == SWITCH_ON){
            canvas.drawBitmap(backgroundOn, 0, 0, new Paint());
            canvas.drawBitmap(switchButton, 3, 3, new Paint());
            Paint textPaint = new Paint();
            textPaint.setTextSize(30);
            textPaint.setColor(Color.parseColor("#ffffff"));
            Rect textBound = new Rect();
            textPaint.getTextBounds(textOn, 0, textOn.length(), textBound);

            float x = backgroundOn.getWidth() - 21 - textBound.width();
            float y = -textBound.top + backgroundOn.getHeight() / 2 - textBound.height() / 2;
            canvas.drawText(textOn, x, y, textPaint);
        }else if (mCurrentStatus == SWITCH_OFF){
            canvas.drawBitmap(backgroundOff, 0, 0, new Paint());

            canvas.drawBitmap(switchButton,backgroundOff.getWidth() - 3 - switchButton.getWidth(), 3, new Paint());
            Paint textPaint = new Paint();
            textPaint.setTextSize(30);
            textPaint.setColor(Color.parseColor("#ffffff"));
            Rect textBound = new Rect();
            textPaint.getTextBounds(textOff, 0, textOff.length(), textBound);

            float x = 21;
            float y = -textBound.top + backgroundOff.getHeight() / 2 - textBound.height() / 2;
            canvas.drawText(textOff, x, y, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                if (mCurrentStatus == SWITCH_OFF){
                    mCurrentStatus = SWITCH_ON;
                    invalidate();
                }else {
                    mCurrentStatus = SWITCH_OFF;
                    invalidate();
                }
                break;
        }
        return true;
    }

    public void setStatusOn(boolean on){
        mCurrentStatus = ( on ? SWITCH_ON : SWITCH_OFF);
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params.width = backgroundOn.getWidth();
        params.height = backgroundOn.getHeight();
        super.setLayoutParams(params);
    }

    public int getSex(){
        return mCurrentStatus;
    }
}
