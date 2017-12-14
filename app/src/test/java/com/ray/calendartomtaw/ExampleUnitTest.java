package com.ray.calendartomtaw;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, 2017);
        c.set(Calendar.MONTH, 10);
        System.out.println("------------" + c.get(Calendar.YEAR) + "年" + (c.get(Calendar.MONTH) + 1) + "月的天数和周数-------------");
        System.out.println("天数：" + c.getActualMaximum(Calendar.DAY_OF_MONTH));
        System.out.println("周数：" + c.getActualMaximum(Calendar.WEEK_OF_MONTH));
    }

    @Test
    public void testFor(){
        A[][] dates = new A[10][10];
        for(A[] weekDays : dates){
            for (A day : weekDays) {
                day = new A();
            }
        }

        for(A[] weekDays : dates){
            for (A day : weekDays) {
                System.out.print(day);
            }
            System.out.println();
        }
    }

    @Test
    public void testCeil(){
        System.out.println(Math.ceil(1.0));
        System.out.println(Math.ceil(1.1));
    }

    private class A{}
}