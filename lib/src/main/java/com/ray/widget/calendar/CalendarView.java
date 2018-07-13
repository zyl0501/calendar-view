package com.ray.widget.calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Pair;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.ray.widget.viewpager.NoLimitViewPager;

import java.util.Calendar;
import java.util.Locale;

/**
 * @author zyl
 * @date Created on 2017/12/14
 */
public class CalendarView extends LinearLayout {
    private static final int MAX_POSITION = Integer.MAX_VALUE - 3;
    private static final int BEGIN_POSITION = MAX_POSITION / 2;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    ImageView leftImg;
    ImageView rightImg;
    TextView yearMonthTv;
    LayoutInflater inflater;
    MonthLabelView monthLabelView;
    int titleLayoutRes;

    Calendar selectCal;
    Calendar tempCal;
    SparseArray<MonthLayout> cacheMonthLayouts;
    CallBack callBack;

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
        tempCal = Calendar.getInstance();
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
                goPreMonth();
            }
        });
        rightImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                goNextMonth();
            }
        });
        yearMonthTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (callBack == null || !callBack.onYearMonthClick(v)) {
                    Pair<Integer, Integer> yearMonth = getPositionYearMonth(viewPager.getCurrentItem());
                    showDefaultMonthPickDialog(yearMonth.first, yearMonth.second);
                }
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
        viewPager = new NoLimitViewPager(getContext());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                notifyTitleChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pagerAdapter = new PagerAdapter() {
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
                Pair<Integer, Integer> yearMonth = getPositionYearMonth(position);
                int year = yearMonth.first;
                int month = yearMonth.second;

                MonthLayout monthLayout = generateMonthLayout(year, month);
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
        };
        viewPager.setAdapter(pagerAdapter);
        viewPager.setCurrentItem(BEGIN_POSITION, false);

        LayoutParams lp = generateDefaultLayoutParams();
        lp.height = LayoutParams.MATCH_PARENT;
        addView(viewPager, lp);
    }

    private int getRealPosition(int position) {
        return position - BEGIN_POSITION;
    }

    /**
     * 根据年月生成要显示{@link MonthLayout}
     *
     * @param year  年
     * @param month 月
     * @return {@link MonthLayout}
     */
    private MonthLayout generateMonthLayout(int year, int month) {
        final MonthLayout monthLayout = (MonthLayout) inflater.inflate(R.layout.widget_calendar_month_layout, viewPager, false);
        monthLayout.setItemClickListener(new MonthLayout.ItemClickListener() {
            @Override
            public void onItemClick(MonthLayout monthLayout, int year, int month, int day) {
                if (callBack == null || !callBack.onDatePick(year, month, day)) {
                    setSelectDate(year, month, day);
                }
            }
        });
        monthLayout.setCallBack(new MonthLayout.CallBack() {
            @Override
            public int[] getDots(int year, int month, int day) {
                if (callBack != null) {
                    return callBack.getDots(year, month, day);
                } else {
                    return null;
                }
            }
        });
        monthLayout.setDate(year, month);
        if (selectCal != null) {
            monthLayout.setSelectDay(selectCal.get(Calendar.YEAR), selectCal.get(Calendar.MONTH), selectCal.get(Calendar.DAY_OF_MONTH));
        }
        return monthLayout;
    }

    private Pair<Integer, Integer> getPositionYearMonth(int position) {
        tempCal.setTimeInMillis(System.currentTimeMillis());
        tempCal.add(Calendar.MONTH, getRealPosition(position));
        return new Pair<>(tempCal.get(Calendar.YEAR), tempCal.get(Calendar.MONTH));
    }

    private void showDefaultMonthPickDialog(int initYear, int initMonth) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        if (inflater == null) {
            inflater = LayoutInflater.from(getContext());
        }

        Calendar cal = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.widget_calendar_default_year_month_pick, null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(initMonth + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(year - 50);
        yearPicker.setMaxValue(year + 50);
        yearPicker.setValue(initYear);

        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        setMonth(yearPicker.getValue(), monthPicker.getValue() - 1);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    /**
     * 设置选中的日期
     *
     * @param year  年
     * @param month 月
     * @param day   日
     */
    public void setSelectDate(int year, int month, int day) {
        if (selectCal == null) {
            selectCal = Calendar.getInstance();
        }
        selectCal.set(year, month, day);
        if (cacheMonthLayouts != null) {
            for (int i = 0, size = cacheMonthLayouts.size(); i < size; i++) {
                cacheMonthLayouts.valueAt(i).setSelectDay(year, month, day);
            }
        }
    }

    /**
     * 设置显示月份
     *
     * @param year  年
     * @param month 月
     */
    public void setMonth(int year, int month) {
        //当前系统的时间
        tempCal.setTimeInMillis(System.currentTimeMillis());
        int curYear = tempCal.get(Calendar.YEAR);
        int curMonth = tempCal.get(Calendar.MONTH);
        int monthDiff = getMonthDiff(year, month, curYear, curMonth);
        viewPager.setCurrentItem(BEGIN_POSITION + monthDiff, true);
    }

    public void notifyChanged() {
        if (cacheMonthLayouts != null) {
            for (int i = 0, size = cacheMonthLayouts.size(); i < size; i++) {
                cacheMonthLayouts.valueAt(i).refresh();
            }
        }
        notifyTitleChanged(viewPager.getCurrentItem());
    }

    public void notifyTitleChanged(int position){
        Pair<Integer, Integer> yearMonth = getPositionYearMonth(position);
        int year = yearMonth.first;
        int month = yearMonth.second;
        if (callBack != null) {
            callBack.onChangeMonth(year, month);
            yearMonthTv.setText(callBack.getShowText(year, month));
        } else {
            yearMonthTv.setText(String.format(Locale.getDefault(), "%02d-%02d", year, month + 1));
        }
    }

    /**
     * 获取两个日期相差的月数
     *
     * @return 如果 date1 > date2，返回正数，反则相等，返回0，否则返回负数
     */
    static int getMonthDiff(int year1, int month1, int year2, int month2) {
        int diffYear = year1 - year2;
        return diffYear * 12 + month1 - month2;
    }

    public Pair<Integer, Integer> getShowYearMonth() {
        return getPositionYearMonth(viewPager.getCurrentItem());
    }

    public Calendar getSelectDate() {
        return selectCal == null ? null : (Calendar) selectCal.clone();
    }

    public void goPreMonth() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() - 1, true);
    }

    public void goNextMonth() {
        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
    }

    public void setCallBack(CallBack callBack) {
        this.callBack = callBack;
        notifyChanged();
    }

    public interface CallBack extends MonthLayout.CallBack {
        /**
         * 日期选择的时候
         *
         * @param year  年
         * @param month 月
         * @param day   日
         * @return 是否消耗掉本次点击，false 表示不消耗
         */
        boolean onDatePick(int year, int month, int day);

        /**
         * 年月点击事件
         *
         * @param view view
         * @return 是否消耗掉本次点击，false 表示不消耗，会显示默认的
         */
        boolean onYearMonthClick(View view);

        /**
         * 显示内容变化的时候
         *
         * @param newYear  new year
         * @param newMonth new  month
         */
        void onChangeMonth(int newYear, int newMonth);

        /**
         * 显示年月
         *
         * @param year  年
         * @param month 月
         * @return 根据年月显示内容
         */
        CharSequence getShowText(int year, int month);
    }
}
