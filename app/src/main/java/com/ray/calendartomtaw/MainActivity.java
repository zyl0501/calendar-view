package com.ray.calendartomtaw;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.ray.widget.calendar.CalendarView;
import com.ray.widget.calendar.MonthLabelView;
import com.ray.widget.calendar.MonthLayout;

import java.util.Calendar;
import java.util.Locale;

/**
 * main test
 * @author ray
 */
public class MainActivity extends AppCompatActivity {
    private static final int MAX_POSITION = 20;
    private static final int BEGIN_POSITION = MAX_POSITION / 2;

    int dotNumber = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final CalendarView calendarView = findViewById(R.id.calendar_layout);
        calendarView.setCallBack(new CalendarView.CallBack() {
            @Override
            public boolean onDatePick(int year, int month, int day) {
                return false;
            }

            @Override
            public boolean onYearMonthClick(View view) {
                return false;
            }

            @Override
            public void onChangeMonth(int newYear, int newMonth) {
                Toast.makeText(MainActivity.this, String.format(Locale.getDefault(), "%02d-%02d", newYear, newMonth+1), Toast.LENGTH_SHORT).show();
//                Log.d("raytest", calendarView.getSelectDate().get(Calendar.YEAR) + "-" + calendarView.getSelectDate().get(Calendar.MONTH) + "-" + calendarView.getSelectDate().get(Calendar.DAY_OF_MONTH));
            }

            @Override
            public CharSequence getShowText(int year, int month) {
                return String.format(Locale.getDefault(), "%02d----%02d", year, month+1);
            }

            @Override
            public int[] getDots(int year, int month, int day) {
                if (day == dotNumber) {
                    return new int[]{Color.BLUE};
                } else {
                    return new int[0];
                }
            }
        });
//        calendarView.notifyChanged();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dotNumber = 12;
                calendarView.notifyChanged();
            }
        }, 3000);

        Log.d("raytest", calendarView.getShowYearMonth().first + "-" + calendarView.getShowYearMonth().second);

        final MonthLayout monthLayout = findViewById(R.id.month_layout);
        MonthLabelView labelView = findViewById(R.id.label_layout);
        labelView.setupWith(monthLayout);

        Calendar cal = Calendar.getInstance();
        monthLayout.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
        monthLayout.setCallBack(new MonthLayout.CallBack() {
            @Override
            public int[] getDots(int year, int month, int day) {
                if (day % 7 == 0) {
                    return new int[]{
                            Color.parseColor("#ff6f6f"), Color.parseColor("#1c8be4"),
                    };
                }
                if (day % 7 == 2) {
                    return new int[]{
                            Color.parseColor("#ff6f6f")
                    };
                }
                return null;
            }
        });
        monthLayout.setItemClickListener(new MonthLayout.ItemClickListener() {
            @Override
            public void onItemClick(MonthLayout layout, int year, int month, int day) {
                layout.setSelectDay(year, month, day);
            }
        });
    }

    private int getRealPosition(int position) {
        return position - BEGIN_POSITION;
    }
}
