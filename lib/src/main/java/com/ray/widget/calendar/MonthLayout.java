package com.ray.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;

/**
 * @author zyl
 * @date Created on 2017/12/13
 */
public class MonthLayout extends View {
    private static final String TAG = MonthLayout.class.getSimpleName();
    private static final int WEEK_COUNT = 7;

    Calendar calendar;
    int year;
    int month;
    /**
     * 星期左侧第一个
     */
    int firstWeek;

    /**
     * 一级坐标表示行（第几周）
     * 二级长度固定为7（一周），表示对应的日期
     */
    Cell[][] dates;

    Paint weekLabelPaint;
    Paint textPaint;
    Paint.FontMetrics fontMetrics;
    Paint cellBgPaint;
    Paint dotPaint;
    int primaryColor;
    int selectTextColor;
    /**
     * -1 表示自动适配选择最佳size
     */
    int cellSize;
    boolean autoCellSize;
    int dotSize;
    int dotMargin;
    int dotMarginText;
    /**
     * -1 表示和日期行高度一致
     */
    int weekLabelHeight;
    boolean autoWeekLabelHeight;
    boolean showWeekLabel;
    /**
     * 是否显示临近月份的日期
     */
    boolean showRelativeMonth;
    //可以显示多少周
    int weeks;
    CharSequence[] weekTexts;

    Calendar todayCal;
    Calendar selectCal;

    CallBack callBack;
    InitListener initListener;
    ItemClickListener itemClickListener;
    GestureDetector gestureDetector;

    public MonthLayout(Context context) {
        this(context, null);
    }

    public MonthLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public MonthLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, @Nullable AttributeSet attrs) {
        weekLabelPaint = new Paint();
        textPaint = new Paint();
        cellBgPaint = new Paint();
        dotPaint = new Paint();
        int textSize;
        int weekLabelTextSize;
        int weekLabelTextColor;
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MonthLayout);
            primaryColor = a.getColor(R.styleable.MonthLayout_ml_primaryColor, ContextCompat.getColor(context, R.color.ml_primaryColor));
            weekLabelTextColor = a.getColor(R.styleable.MonthLayout_ml_weekLabelColor, ContextCompat.getColor(context, R.color.ml_weekLabelColor));
            selectTextColor = a.getColor(R.styleable.MonthLayout_ml_selectTextColor, ContextCompat.getColor(context, R.color.ml_selectTextColor));
            textSize = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_textSize, Utils.getDimen(context, R.dimen.ml_textSize));
            weekLabelTextSize = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_weekLabelSize, Utils.getDimen(context, R.dimen.ml_weekLabelSize));
            cellSize = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_cellSize, -1);
            dotSize = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_dotSize, Utils.getDimen(context, R.dimen.ml_dotSize));
            dotMargin = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_dotMargin, Utils.getDimen(context, R.dimen.ml_dotMargin));
            dotMarginText = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_dotMarginText, Utils.getDimen(context, R.dimen.ml_dotMarginText));
            firstWeek = a.getInt(R.styleable.MonthLayout_ml_firstWeek, Calendar.SUNDAY);
            showWeekLabel = a.getBoolean(R.styleable.MonthLayout_ml_showWeekLabel, false);
            showRelativeMonth = a.getBoolean(R.styleable.MonthLayout_ml_showRelativeMonth, false);
            weekLabelHeight = a.getDimensionPixelOffset(R.styleable.MonthLayout_ml_weekLabelHeight, -1);
            weekTexts = a.getTextArray(R.styleable.MonthLayout_ml_weeksArray);
            if (weekTexts == null) {
                weekTexts = context.getResources().getTextArray(R.array.ml_weeks);
            }
            a.recycle();
        } else {
            primaryColor = ContextCompat.getColor(context, R.color.ml_primaryColor);
            weekLabelTextColor = ContextCompat.getColor(context, R.color.ml_weekLabelColor);
            selectTextColor = ContextCompat.getColor(context, R.color.ml_selectTextColor);
            textSize = context.getResources().getDimensionPixelOffset(R.dimen.ml_textSize);
            weekLabelTextSize = context.getResources().getDimensionPixelOffset(R.dimen.ml_weekLabelSize);
            cellSize = -1;
            dotSize = Utils.getDimen(context, R.dimen.ml_dotSize);
            dotMargin = Utils.getDimen(context, R.dimen.ml_dotMargin);
            dotMarginText = Utils.getDimen(context, R.dimen.ml_dotMarginText);
            firstWeek = Calendar.SUNDAY;
            showWeekLabel = false;
            showRelativeMonth = false;
            weekLabelHeight = -1;
            weekTexts = context.getResources().getTextArray(R.array.ml_weeks);
        }
        textPaint.setTextSize(textSize);
        textPaint.setColor(primaryColor);
        textPaint.setAntiAlias(true);
        cellBgPaint.setColor(primaryColor);
        cellBgPaint.setAntiAlias(true);
        weekLabelPaint.setTextSize(weekLabelTextSize);
        weekLabelPaint.setColor(weekLabelTextColor);
        weekLabelPaint.setAntiAlias(true);

        autoCellSize = cellSize == -1;
        autoWeekLabelHeight = weekLabelHeight == -1;
        fontMetrics = textPaint.getFontMetrics();
        todayCal = Calendar.getInstance();
        selectCal = Calendar.getInstance();
        gestureDetector = new GestureDetector(context, new GestureListener());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (dates == null || dates.length <= 0) {
            return;
        }
        int left = getPaddingLeft();
        int right = canvas.getWidth() - getPaddingRight();
        int top = getPaddingTop();
        int bottom = canvas.getHeight() - getPaddingBottom();
        int validWidth = right - left;
        int validHeight = bottom - top;
        calculateAutoSize(validWidth, validHeight);
        int cellValidHeight = validHeight - getWeekLabelShowHeight();

        int transStepX = getStepX(validWidth);
        int transStepY = getStepY(cellValidHeight);

        int s1 = canvas.save();
        canvas.translate(left, top);

        if (showWeekLabel) {
            drawWeekText(canvas);
            canvas.translate(0, weekLabelHeight);
        }

        int row = 0;
        int column = 0;
        for (Cell[] weekDays : dates) {
            int saveRow = canvas.save();
            canvas.translate(0, row * transStepY);
            for (Cell day : weekDays) {
                boolean isNotCurrentMonth = day.year != year || day.month != month;
                if (!showRelativeMonth && isNotCurrentMonth) {
                    canvas.translate(transStepX, 0);
                    continue;
                }
                drawCellBox(canvas, day);
                drawDots(canvas, day);
                canvas.translate(transStepX, 0);
            }
            canvas.restoreToCount(saveRow);
            row++;
        }
        canvas.restoreToCount(s1);
    }

    private void calculateAutoSize(int validWidth, int validHeight) {
        // weekLabelHeight 为-1，跟每行高度一致
        if (weekLabelHeight == -1) {
            if (cellSize == -1) {
                int widthExpireSize = validWidth / WEEK_COUNT * 4 / 5;
                int heightExpireSize = validHeight / (weeks + 1) * 4 / 5;
                int tempCellSize = Math.min(widthExpireSize, heightExpireSize);
                weekLabelHeight = dates.length == 1 ? validHeight / 2 : (validHeight - tempCellSize) / dates.length;
            } else {
                weekLabelHeight = getStepY(validHeight);
            }
        }
        //日期部分有效的高度
        int cellValidHeight = validHeight - getWeekLabelShowHeight();
        //cellSize为-1，根据控件大小自动适配
        if (cellSize == -1) {
            int widthExpireSize = validWidth / WEEK_COUNT * 4 / 5;
            int heightExpireSize = cellValidHeight / weeks * 4 / 5;
            cellSize = Math.min(widthExpireSize, heightExpireSize);
        }
    }

    void drawWeekText(Canvas canvas) {
        if (weekTexts == null || weekTexts.length != WEEK_COUNT) {
            Log.w(TAG, "week text array is invalid, must be array of 7 size.\nArray must be first of sunday.");
            return;
        }
        int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int validHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        calculateAutoSize(validWidth, validHeight);

        int centerX = cellSize / 2;
        int centerY = weekLabelHeight / 2;
        int transStepX = getStepX(validWidth);
        int saveWeekLabel = canvas.save();
        for (int i = 0; i < WEEK_COUNT; i++) {
            CharSequence weekStr = weekTexts[(firstWeek - 1 + i) % WEEK_COUNT];
            Utils.drawCenterText(weekStr.toString(), canvas, centerX, centerY, weekLabelPaint);
            canvas.translate(transStepX, 0);
        }
        canvas.restoreToCount(saveWeekLabel);
    }

    /**
     * 绘制圆和日期
     */
    private void drawCellBox(Canvas canvas, Cell day) {
        int halfSize = cellSize / 2;
        if (isToday(day)) {
            //今天
            cellBgPaint.setColor(primaryColor);
            textPaint.setColor(selectTextColor);
            cellBgPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(halfSize, halfSize, halfSize, cellBgPaint);
            Utils.drawCenterText(String.valueOf(day.day), canvas, halfSize, halfSize, textPaint);
        } else if (day.year == selectCal.get(Calendar.YEAR)
                && day.month == selectCal.get(Calendar.MONTH)
                && day.day == selectCal.get(Calendar.DAY_OF_MONTH)) {
            //选中状态
            cellBgPaint.setStyle(Paint.Style.STROKE);
            cellBgPaint.setColor(primaryColor);
            textPaint.setColor(primaryColor);
            canvas.drawCircle(halfSize, halfSize, halfSize, cellBgPaint);
            Utils.drawCenterText(String.valueOf(day.day), canvas, halfSize, halfSize, textPaint);
        } else {
            //普通状态
            textPaint.setColor(primaryColor);
            Utils.drawCenterText(String.valueOf(day.day), canvas, halfSize, halfSize, textPaint);
        }
    }

    private void drawDots(Canvas canvas, Cell day) {
        if (day.dotColors != null && !isToday(day)) {
            float dayTextBottom = Utils.getBaseline(cellSize / 2, textPaint) + fontMetrics.bottom;

            int saveDot = canvas.save();
            int length = day.dotColors.length;
            int dotAllWidth = dotSize * (length) + dotMargin * (length - 1);
            int dotLeft = cellSize / 2 - dotAllWidth / 2;

            canvas.translate(dotLeft, dayTextBottom + dotMarginText);
            for (int color : day.dotColors) {
                int halfSize = dotSize / 2;
                dotPaint.setColor(color);
                canvas.drawCircle(halfSize, halfSize, halfSize, dotPaint);
                canvas.translate(dotSize + dotMargin, 0);
            }
            canvas.restoreToCount(saveDot);
        }
    }

    private int getStepX(int validWidth) {
        return (validWidth - cellSize) / (WEEK_COUNT - 1);
    }

    private int getStepY(int validHeight) {
        return dates.length == 1 ? validHeight : (validHeight - cellSize) / (dates.length - 1);
    }

    /**
     * 返回 week label实际显示高度，如果不显示，则返回0
     */
    private int getWeekLabelShowHeight() {
        if (showWeekLabel) {
            return weekLabelHeight;
        } else {
            return 0;
        }
    }

    int getWeekLabelSuggestHeight(int validWidth, int validHeight) {
        if (weekLabelHeight == -1) {
            if (cellSize == -1) {
                int widthExpireSize = validWidth / WEEK_COUNT * 4 / 5;
                int heightExpireSize = validHeight / (weeks + 1) * 4 / 5;
                int tempCellSize = Math.min(widthExpireSize, heightExpireSize);
                if (dates == null) {
                    weekLabelHeight = tempCellSize;
                } else {
                    weekLabelHeight = dates.length == 1 ? validHeight / 2 : (validHeight - tempCellSize) / dates.length;
                }
            } else {
                weekLabelHeight = getStepY(validHeight);
            }
        }
        return weekLabelHeight;
    }

    private boolean isToday(Cell day) {
        int currentYear = todayCal.get(Calendar.YEAR);
        int currentMonth = todayCal.get(Calendar.MONTH);
        int currentDay = todayCal.get(Calendar.DAY_OF_MONTH);
        return day.year == currentYear && day.month == currentMonth && day.day == currentDay;
    }

    public void setDate(int year, int month) {
        this.year = year;
        this.month = month;
        refresh();
    }

    public void refresh() {
        if (calendar == null) {
            calendar = Calendar.getInstance();
        }
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        //当月最大天数
        int days = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        int week = calendar.get(Calendar.DAY_OF_WEEK);
        int weekOffset = getWeekOffset(week, firstWeek);
        calendar.add(Calendar.DAY_OF_MONTH, -weekOffset);
        weeks = (int) Math.ceil((float) (days + weekOffset) / WEEK_COUNT);
        dates = new Cell[weeks][WEEK_COUNT];
        for (int row = 0; row < weeks; row++) {
            for (int col = 0; col < WEEK_COUNT; col++) {
                Cell cell = new Cell();
                cell.year = calendar.get(Calendar.YEAR);
                cell.month = calendar.get(Calendar.MONTH);
                cell.day = calendar.get(Calendar.DAY_OF_MONTH);
                if (callBack != null) {
                    cell.dotColors = callBack.getDots(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                }
                dates[row][col] = cell;
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
        }
        if (autoCellSize) {
            cellSize = -1;
        }
        if (autoWeekLabelHeight) {
            weekLabelHeight = -1;
        }

        //可以用局部刷新优化
        invalidate();
        logDate();
    }

    /**
     * 设置当前选中的年月日
     */
    public void setSelectDay(int year, int month, int day) {
        selectCal.set(year, month, day);
        invalidate();
    }

    private int getWeekOffset(int week, int firstWeek) {
        int weekOffset;
        if (week == firstWeek) {
            weekOffset = 0;
        } else if (week > firstWeek) {
            weekOffset = week - firstWeek;
        } else {
            //week < firstWeek
            weekOffset = WEEK_COUNT - firstWeek + week;
        }
        return weekOffset;
    }

    private class Cell {
        int year;
        int month;
        int day;
        int[] dotColors;
    }

    private void logDate() {
        if (dates == null) {
            Log.d(TAG, "dates is null.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (Cell[] weekDays : dates) {
                for (Cell day : weekDays) {
                    sb.append(day.day).append(" ");
                }
                sb.append("\n");
            }
            Log.d(TAG, sb.toString());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        Cell dayCell;

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if (dayCell != null) {
                if (itemClickListener != null) {
                    itemClickListener.onItemClick(MonthLayout.this, dayCell.year, dayCell.month, dayCell.day);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onDown(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();
            dayCell = getClickCell(x, y);
            return dayCell != null && (showRelativeMonth || dayCell.year == year && dayCell.month == month);
        }
    }

    /**
     * 获取点击的 cell
     *
     * @param x 触摸的 x
     * @param y 触摸的 y
     * @return 返回点击的 cell
     */
    private Cell getClickCell(float x, float y) {
        if (dates == null) {
            return null;
        }
        float cellY = y - getWeekLabelShowHeight();
        //点击label部分，不需要响应
        if (cellY < 0) {
            return null;
        }
        int validWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int validHeight = getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - getWeekLabelShowHeight();
        int stepX = getStepX(validWidth);
        int stepY = getStepY(validHeight);
        int column = (int) (x / stepX);
        int cOff = (int) (x % stepX);
        int row = (int) (cellY / stepY);
        int rOff = (int) (cellY % stepY);
        if (cOff <= cellSize && rOff <= cellSize) {
            return dates[row][column];
        } else {
            return null;
        }
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
    }

    public interface CallBack {
        /**
         * 获取点的颜色
         *
         * @param year  year
         * @param month month
         * @param day   day
         * @return 返回点的颜色集合，一个表示一个点
         */
        int[] getDots(int year, int month, int day);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        /**
         * item 点击
         *
         * @param monthLayout view
         * @param year        年
         * @param month       月
         * @param day         日
         */
        void onItemClick(MonthLayout monthLayout, int year, int month, int day);
    }

    void setInitListener(InitListener initListener) {
        this.initListener = initListener;
    }

    interface InitListener {
        void onInitFinish();
    }
}
