package com.ray.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Calendar;

/**
 * @author zyl
 * @date Created on 2017/12/14
 */
public class MonthLabelView extends View {
    private static final String TAG = MonthLabelView.class.getSimpleName();
    private static final int WEEK_COUNT = 7;

    Paint weekLabelPaint;
    CharSequence[] weekTexts;
    /**
     * 星期左侧第一个
     */
    int firstWeek;

    MonthLayout monthLayout;

    public MonthLabelView(Context context) {
        this(context, null);
    }

    public MonthLabelView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MonthLabelView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        weekLabelPaint = new Paint();

        int weekLabelTextSize;
        int weekLabelTextColor;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MonthLabelView);
            weekLabelTextSize = a.getDimensionPixelOffset(R.styleable.MonthLabelView_lv_weekLabelSize, Utils.getDimen(context, R.dimen.ml_weekLabelSize));
            weekLabelTextColor = a.getColor(R.styleable.MonthLabelView_lv_weekLabelColor, ContextCompat.getColor(context, R.color.ml_weekLabelColor));
            firstWeek = a.getInt(R.styleable.MonthLabelView_lv_firstWeek, Calendar.SUNDAY);
            weekTexts = a.getTextArray(R.styleable.MonthLabelView_lv_weeksArray);
            if (weekTexts == null) {
                weekTexts = context.getResources().getTextArray(R.array.ml_weeks);
            }
            a.recycle();
        } else {
            weekLabelTextColor = ContextCompat.getColor(context, R.color.ml_weekLabelColor);
            weekLabelTextSize = context.getResources().getDimensionPixelOffset(R.dimen.ml_weekLabelSize);
            firstWeek = Calendar.SUNDAY;
            weekTexts = context.getResources().getTextArray(R.array.ml_weeks);
        }
        weekLabelPaint.setTextSize(weekLabelTextSize);
        weekLabelPaint.setColor(weekLabelTextColor);
        weekLabelPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (monthLayout == null) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        } else {
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height = MeasureSpec.getSize(heightMeasureSpec);
            height = monthLayout.getWeekLabelSuggestHeight(width, height);
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (monthLayout != null) {
            monthLayout.drawWeekText(canvas);
        } else {
            drawWeekText(canvas);
        }
    }

    public void setupWith(MonthLayout monthLayout) {
        this.monthLayout = monthLayout;
        invalidate();
    }

    public MonthLayout getMonthLayout() {
        return monthLayout;
    }

    private void drawWeekText(Canvas canvas) {
        if (weekTexts == null || weekTexts.length != WEEK_COUNT) {
            Log.w(TAG, "week text array is invalid, must be array of 7 size.\nArray must be first of sunday.");
            return;
        }
        int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int validHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();

        int cellSize = validWidth / WEEK_COUNT * 4 / 5;
        int centerX = cellSize / 2;
        int centerY = validHeight / 2;
        int transStepX = getStepX(validWidth, cellSize);
        int saveWeekLabel = canvas.save();
        for (int i = 0; i < WEEK_COUNT; i++) {
            CharSequence weekStr = weekTexts[(firstWeek - 1 + i) % WEEK_COUNT];
            Utils.drawCenterText(weekStr.toString(), canvas, centerX, centerY, weekLabelPaint);
            canvas.translate(transStepX, 0);
        }
        canvas.restoreToCount(saveWeekLabel);
    }

    private int getStepX(int validWidth, int cellSize) {
        return (validWidth - cellSize) / (WEEK_COUNT - 1);
    }
}
