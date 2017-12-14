package com.ray.widget.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

/**
 * @author zyl
 * @date Created on 2017/12/14
 */
public class CalendarView extends LinearLayout {
    private static final int MAX_POSITION = Integer.MAX_VALUE;
    private static final int BEGIN_POSITION = MAX_POSITION / 2;

    ViewPager viewPager;
    ImageView leftImg;
    ImageView rightImg;
    TextView yearMonthTv;
    LayoutInflater inflater;
    MonthLabelView monthLabelView;
    int titleLayoutRes;

    Calendar selectCal;
    SparseArray<MonthLayout> cacheMonthLayouts;
    OnDatePickListener onDatePickListener;

    public CalendarView(Context context) {
        this(context, null);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarView);
            titleLayoutRes = a.getResourceId(R.styleable.CalendarView_cv_title_layout, R.layout.widget_calendar_title_layout);
            a.recycle();
        }
        init();
    }

    private void init() {
        selectCal = Calendar.getInstance();
        cacheMonthLayouts = new SparseArray<>();
        setOrientation(VERTICAL);
        addTitleLayout();
        addMonthLabel();
        addViewPager();
    }

    private void addTitleLayout() {
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }
        inflater.inflate(titleLayoutRes, this, true);
        leftImg = findViewById(R.id.cv_cal_left_img);
        yearMonthTv = findViewById(R.id.cv_cal_year_month_tv);
        rightImg = findViewById(R.id.cv_cal_right_img);

        leftImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
            }
        });
        rightImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            }
        });
        yearMonthTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    private void addMonthLabel() {
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }
        monthLabelView = (MonthLabelView) inflater.inflate(R.layout.widget_calendar_month_label, this, false);
        addView(monthLabelView);
    }

    private void addViewPager() {
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }
        viewPager = new ViewPager(getContext());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                int realPos = getRealPosition(position);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, realPos);
                yearMonthTv.setText(String.valueOf(calendar.get(Calendar.YEAR)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return MAX_POSITION;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                int realPos = getRealPosition(position);
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, realPos);

                final MonthLayout monthLayout = (MonthLayout) inflater.inflate(R.layout.widget_calendar_month_layout, viewPager, false);
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                monthLayout.setSelectDay(selectCal.get(Calendar.YEAR), selectCal.get(Calendar.MONTH), selectCal.get(Calendar.DAY_OF_MONTH));
                monthLayout.setDate(year, month);
                monthLayout.setItemClickListener(new MonthLayout.ItemClickListener() {
                    @Override
                    public void onItemClick(MonthLayout monthLayout, int year, int month, int day) {
                        select(year, month, day);
                        if (onDatePickListener != null) {
                            onDatePickListener.onDatePick(year, month, day);
                        }
                    }
                });
                container.addView(monthLayout);
                if (monthLabelView != null && monthLabelView.getMonthLayout() == null) {
                    monthLabelView.setupWith(monthLayout);
                }
                cacheMonthLayouts.put(position, monthLayout);
                return monthLayout;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                cacheMonthLayouts.remove(position);
                container.removeView((View) object);
            }
        });
        viewPager.setCurrentItem(BEGIN_POSITION, false);

        LayoutParams lp = generateDefaultLayoutParams();
        lp.height = LayoutParams.MATCH_PARENT;
        addView(viewPager, lp);
    }

    private int getRealPosition(int position) {
        return position - BEGIN_POSITION;
    }

    public void select(int year, int month, int day) {
        selectCal.set(year, month, day);
        if (cacheMonthLayouts != null) {
            for (int i = 0, size = cacheMonthLayouts.size(); i < size; i++) {
                cacheMonthLayouts.valueAt(i).setSelectDay(year, month, day);
            }
        }
    }

    public void setOnDatePickListener(OnDatePickListener onDatePickListener) {
        this.onDatePickListener = onDatePickListener;
    }

    public interface OnDatePickListener {
        void onDatePick(int year, int month, int day);
    }
}
