package com.qixingbang.qxb.activity.mine.clipHeadPortrait;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

/**
 * 类名待定
 * 蒙版下面的imageView
 * Created by Z.H. on 2015/9/9 10:39.
 */
public class ClipBaseView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener, ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private static final String TAG = "ClipBaseView";
    private boolean mOnce = true;
    /**
     * 初始化缩放比例
     */
    private float mInitScale;
    /**
     * 双击的缩放比例
     */
    private float mMidScale;
    /**
     * 最大的缩放比例
     */
    private float mMaxScale;
    /**
     * 多点触控，手势
     */
    private ScaleGestureDetector mScaleGestureDetector;

    private GestureDetector mGestureDetector;
    private Matrix mScaleMatrix  = new Matrix();
    /**
     * 上一次触摸点个数
     */
    private int lastPointerCount;
    private float lastX;
    private float lastY;
    private boolean isCanMove;
    private int mTouchSlop;
    private RectF mMatrixRectF;

    /**
     * 所有的初始化代码都可以写在3个参数中，前两个会最终调用第三个构造方法
     */
    public ClipBaseView(Context context) {
        this(context, null);
    }

    public ClipBaseView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClipBaseView(final Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //初始化
        setScaleType(ScaleType.MATRIX);

        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                Toast.makeText(context, "Double Tap" , Toast.LENGTH_SHORT).show();

                //TODO 第二次双击返回原来大小
                float multiple;
                if (getScale() < mMidScale){
                    Log.d("getScale", getScale() + "");
                    multiple = mMidScale / getScale();
                    mScaleMatrix.postScale(multiple, multiple,
                            e.getX(), e.getY());
                }else if (mMidScale <= getScale() && getScale() < mMaxScale){
                    multiple = mMaxScale / getScale();
                    mScaleMatrix.postScale(multiple, multiple,
                            e.getX(), e.getY());
                }else{
                    mScaleMatrix.postScale(0.5F, 0.5F,
                            e.getX(), e.getY());
                }

                checkBorder();
                setImageMatrix(mScaleMatrix);
                return true;
            }
        });
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }


    /**
     * 全局的布局完成后调用
     */
    @Override
    public void onGlobalLayout() {
        if (mOnce){
            //获取imageview大小  比较 缩放
            //获取控件宽高
            int parentWidth = getWidth();
            int width = 600;
            int parentHeight = getHeight();
            int height = 600;
            //获取图片宽高
            Drawable d = getDrawable();
            //图片不存在返回
            if (d == null){
                return;
            }
            //获取图片高度
            int dWidth = d.getIntrinsicWidth();
            int dHeight = d.getIntrinsicHeight();
            Log.d("ClipBaseView", "width = " + width + ",  height = " + height + ", dWidth = " + dWidth + ",  dHeight = " + dHeight);
            //图片放缩比例
            float scale = 1.0f;
            if (dWidth == -1 || dHeight == -1){
                Log.d("图片为:", "solid color");
            }

            if (dWidth > width && dHeight <= height)
            {
                scale = height * 1.0f / dHeight;
            }
            if (dHeight > height && dWidth <= width)
            {
                scale = width * 1.0f / dWidth;
            }
            // 如果宽和高都大于屏幕，则让其按按比例适应屏幕大小
            if (dWidth > width && dHeight > height)
            {
                scale = Math.max(width * 1.0f / dWidth, height * 1.0f / dHeight);
            }
            if (width > dWidth && height > dHeight){
                //控件宽高均大于图片宽高  图片放大 取Max
                scale = Math.max(width * 1.0f / dWidth, height * 1.0f / dHeight);
            }
            mInitScale = scale;

            mMidScale = mInitScale * 2;
            mMaxScale = mInitScale * 4;

            Log.e("Clip", "initScale = " + mInitScale);
            Log.e("Clip", "mMidScale = " + mMidScale);
            Log.e("Clip", "mMaxScale = " + mMaxScale);

            mScaleMatrix.postTranslate((parentWidth - dWidth) / 2, (parentHeight - dHeight) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);
            // 图片移动至屏幕中心
            setImageMatrix(mScaleMatrix);
            mOnce = false;
        }

    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {

        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;

        /**
         * 缩放的范围控制
         */
        if ((scale < mMaxScale && scaleFactor > 1.0f)
                || (scale > mInitScale && scaleFactor < 1.0f))
        {
            /**
             * 最大值最小值判断
             */
            if (scaleFactor * scale < mInitScale)
            {
                scaleFactor = mInitScale / scale;
            }
            if (scaleFactor * scale > mMaxScale)
            {
                scaleFactor = mMaxScale / scale;
            }
            /**
             * 设置缩放比例
             * 不能放到if外面  否则  判断无任何用处
             */
            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            setImageMatrix(mScaleMatrix);
        }

        return true;
    }

    /**
     * 边界检测
     */
    private void checkBorder() {

        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        int mHorizontalPadding = (width - 600) / 2;
        int mVerticalPadding = (height - 600) / 2;

        if (rectF.width() + 0.01 >= 600){
            if (rectF.left > mHorizontalPadding){
                deltaX = -rectF.left + mHorizontalPadding;
            }
            if (rectF.right < width - mHorizontalPadding){
                deltaX = width - mHorizontalPadding - rectF.right;
            }
        }
        if (rectF.height() + 0.01 >= 600){
            if (rectF.top > mVerticalPadding)
            {
                deltaY = -rectF.top + mVerticalPadding;
            }
            if (rectF.bottom < height - mVerticalPadding)
            {
                deltaY = height - mVerticalPadding - rectF.bottom;
            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        //得到触摸点个数
        int touchPointerCount = event.getPointerCount();
        //求出中心点坐标
        float x = 0;
        float y = 0;
        for (int i = 0; i < touchPointerCount; i++){
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / touchPointerCount;
        y = y / touchPointerCount;

        //触摸点个数 发生变化的时候  设置不可以移动 重置中心点坐标
        if (touchPointerCount != lastPointerCount){
            isCanMove = false;
            lastX = x;
            lastY = y;
        }

        lastPointerCount = touchPointerCount;

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:

                float dx = x - lastX;
                float dy = y - lastY;
                Log.e(TAG, "dx =  " + String.valueOf(dx) + "dy =  " + String.valueOf(dy));

                //判断是否达到移动的标准
                if (!isCanMove){
                    isCanMove = isCanMove(dx, dy);
                }

                if (isCanMove){
                    if (getDrawable() != null){

                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();
                        setImageMatrix(mScaleMatrix);
                    }
                }

                lastX = x;
                lastY = y;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.e(TAG, "ACTION_UP");
                lastPointerCount = 0;
                break;
        }
        return true;
    }

    private boolean isCanMove(float dx, float dy) {
        double moveLength = Math.sqrt((dx * dx) + (dy * dy));
        return true;
    }

    private float getScale() {
        float[] values = new float[9];
        mScaleMatrix.getValues(values);
        return values[Matrix.MSCALE_X];
    }

    public RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    public Bitmap onClip() {
        Paint paint = new Paint();
        setDrawingCacheEnabled(true);
        Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(),
                false);
        setDrawingCacheEnabled(false);
        Bitmap newBitmap = Bitmap.createBitmap(600, 600,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        RectF dst = new RectF(-bitmap.getWidth() / 2 + 300, -getHeight()
                / 2 + 300, bitmap.getWidth() - bitmap.getWidth() / 2
                + 300, getHeight() - getHeight() / 2 + 300);
        canvas.drawBitmap(bitmap, null, dst, paint);
        return newBitmap;
    }
}
