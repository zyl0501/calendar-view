# 日历控件

<img src="https://github.com/zyl0501/calendar-view/blob/master/preview/preview.gif"> 

Step 1. Add it in your root build.gradle at the end of repositories:
``` gradle
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```

Step 2. Add the dependency
``` gradle
dependencies {
	 compile 'com.github.zyl0501:calendar-view:1.0.9'
}
```

1.CalendarView
```xml
    <com.ray.widget.calendar.CalendarView
        android:id="@+id/calendar_layout"
        android:layout_width="match_parent"
        android:layout_height="300dp"
        android:visibility="visible"
        app:cv_title_layout="@layout/widget_calendar_title_layout2"/>
```
cv_titie_layout 表示向前，向后，显示当前月份的 layout

	对应的 id 分别是 cv_cal_left_img，cv_cal_right_img，cv_cal_year_month_tv

CalendarView 由 MonthLabelView 和 NoLimitViewPager+MonthLayout 组合而成

<img src="https://github.com/zyl0501/calendar-view/blob/master/preview/1.png"> 

CalendarView.CallBack 方法解释

Method | Summary
--- | ---
`boolean onDatePick(int year, int month, int day)` | 点击具体某一天的日期，返回 true 表示消耗掉本次点击，false 表示不消耗
`boolean onYearMonthClick(View view)` | 点击 cv_titie_layout 该布局中 cv_cal_year_month_tv ，返回 true 表示消耗掉本次点击，false 表示不消耗
`void onChangeMonth(int newYear, int newMonth)` | 月份变化
`CharSequence getShowText(int year, int month)` | 用于自定义 cv_titie_layout 中 cv_cal_year_month_tv 的显示格式
`int[] getDots(int year, int month, int day)` | 返回具体日期下的标记点颜色


2.MonthLayout

Styleable | Summary
--- | ---
`ml_primaryColor` | 控件的主色
`ml_selectTextColor` | 选中的日期的文本颜色
`ml_weekLabelColor` | 星期的颜色
`ml_textSize` | 普通情况文本的颜色
`ml_weekLabelSize` | 星期的文本大小
`ml_cellSize` | 具体日期占用的大小，默认按控件大小平均分配
`ml_dotSize` | 日期下面标记点的大小
`ml_dotMargin` | 标记点之间的距离
`ml_dotMarginText` | 标记点和文本之间的距离
`ml_weeksArray` | 星期的文本显示，如【日，一，二...六】，配置的时候注意是星期日开始
`ml_weekLabelHeight` | 星期栏的高度，默认会根据 cellSize 计算
`ml_showWeekLabel` | 是否显示星期栏
`ml_showRelativeMonth` | 第一行和最后一行是否显示临近月份的日期
`ml_firstWeek` | 最左列从星期几开始
