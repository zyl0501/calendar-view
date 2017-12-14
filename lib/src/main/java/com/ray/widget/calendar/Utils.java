package com.ray.widget.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * @author zyl
 * @date Created on 2017/12/13
 */
class Utils {
    static void drawCenterText(String text, Canvas canvas, int centerX, int centerY, Paint paint) {
        int yPos = (int) getBaseline(centerY, paint);
        //((textPaint.descent() + textPaint.ascent()) / 2) is the distance from the baseline to the center.

        Paint.Align align = paint.getTextAlign();
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(text, centerX, yPos, paint);
        paint.setTextAlign(align);
    }

    static float getBaseline(int textCenter, Paint paint) {
        return textCenter - ((paint.descent() + paint.ascent()) / 2);
    }

    /**
     * 从 dp 的单位 转成为 px(像素)
     */
    static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    static int getDimen(Context context, int id){
        return context.getResources().getDimensionPixelOffset(id);
    }
}
