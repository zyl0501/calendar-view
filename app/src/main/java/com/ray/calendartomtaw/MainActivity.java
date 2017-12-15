package com.ray.calendartomtaw;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ray.widget.calendar.MonthLabelView;
import com.ray.widget.calendar.MonthLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int MAX_POSITION = 20;
    private static final int BEGIN_POSITION = MAX_POSITION / 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        CalendarView calendarView = findViewById(R.id.calendar_layout);
//        calendarView.setOnDatePickListener(new CalendarView.OnDatePickListener() {
//            @Override
//            public void onDatePick(int year, int month, int day) {
//                Log.d("raytest", year + "-" + month + "-" + day);
//            }
//        });

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
