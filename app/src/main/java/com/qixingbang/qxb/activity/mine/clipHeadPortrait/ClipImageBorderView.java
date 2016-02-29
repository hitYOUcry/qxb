package com.qixingbang.qxb.activity.mine.clipHeadPortrait;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Z.H. on 2015/9/8 14:30.
 */
public class ClipImageBorderView extends View{
    public ClipImageBorderView(Context context) {
        super(context);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /*float width = getWidth();
        float height = getHeight();
        int borderColor = Color.parseColor("#aa000000");
        Paint borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        canvas.drawRect(0, 0, width, height, borderPaint);

        Paint centerPaint = new Paint();
        int centerColor = Color.parseColor("#ffffff");
        centerPaint.setColor(centerColor);
        centerPaint.setAntiAlias(true);
//        float ovalLeft = width / 2 - height / 6;
//        float ovalTop  = height / 3;
//        float ovalRight = width / 2 + height / 6;
//        float ovalBottom = height * 2 / 3;
        canvas.drawCircle(width / 2, height / 2, height / 6, centerPaint);*/

        Rect r = new Rect(0, 0, getWidth(), getHeight());;
        RectF rf = new RectF(r);;
        int sc = canvas.saveLayer(rf, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        Paint paint_rect = new Paint();
        Xfermode cur_xfermode;
        //取下层绘制非交集部分。
        cur_xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
        int color = Color.parseColor("#88000000");
        paint_rect.setColor(color);
        canvas.drawRect(r, paint_rect);
        paint_rect.setXfermode(cur_xfermode);
        paint_rect.setColor(Color.WHITE);
        paint_rect.setAntiAlias(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, 300, paint_rect);
        canvas.restoreToCount(sc);
        paint_rect.setXfermode(null);
    }
}
