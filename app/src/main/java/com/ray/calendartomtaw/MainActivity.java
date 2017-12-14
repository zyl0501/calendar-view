package com.ray.calendartomtaw;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ray.widget.calendar.MonthLayout;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MonthLayout monthLayout = findViewById(R.id.month_layout);
        Calendar cal = Calendar.getInstance();
        monthLayout.setDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 14);
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
        monthLayout.setDate(2017, 11);
        monthLayout.setItemClickListener(new MonthLayout.ItemClickListener() {
            @Override
            public void onItemClick(int year, int month, int day) {
                monthLayout.setDate(year, month, day);
            }
        });
    }
}
