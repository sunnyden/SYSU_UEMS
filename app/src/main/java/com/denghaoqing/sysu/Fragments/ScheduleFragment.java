/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Fragments;


import android.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.Schedule.Course;
import com.denghaoqing.sysu.Schedule.Schedule;
import com.denghaoqing.sysu.ViewCourseDetails;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sunny on 18-2-27.
 */

public class ScheduleFragment extends Fragment implements MonthLoader.MonthChangeListener, WeekView.ScrollListener, WeekView.EventClickListener {
    private final static String LOG_TAG = "ScheduleFragment";
    private static int eventID = 0;
    private LinearLayout oneLayout, threeLayout, fiveLayout;
    private FloatingActionButton oneDayFab, threeDayFab, fiveDayFab, fab;
    private WeekView mWeekView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);
        mWeekView = view.findViewById(R.id.weekView);
        mWeekView.setNumberOfVisibleDays(5);
        mWeekView.setShowNowLine(true);
        mWeekView.setMonthChangeListener(this);
        mWeekView.setOnEventClickListener(this);
        Calendar calendar = Calendar.getInstance();
        //mWeekView.goToDate(calendar);
        mWeekView.goToToday();
        //Log.e("hour",String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)));
        mWeekView.goToHour(calendar.get(Calendar.HOUR_OF_DAY));
        //mWeekView.goToToday();


        oneDayFab = view.findViewById(R.id.fabOneDay);
        threeDayFab = view.findViewById(R.id.fabThreeDay);
        fiveDayFab = view.findViewById(R.id.fabFiveDay);
        oneDayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibility();
                mWeekView.setNumberOfVisibleDays(1);
            }
        });
        threeDayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibility();
                mWeekView.setNumberOfVisibleDays(3);
            }
        });
        fiveDayFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibility();
                mWeekView.setNumberOfVisibleDays(5);
            }
        });
        oneLayout = view.findViewById(R.id.layoutFab_one_Day);
        threeLayout = view.findViewById(R.id.layoutFab_three_Day);
        fiveLayout = view.findViewById(R.id.layoutFab_five_Day);
        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibility();
            }
        });

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void switchVisibility() {

        if (oneLayout.getVisibility() == View.GONE) {
            oneLayout.setVisibility(View.VISIBLE);
            threeLayout.setVisibility(View.VISIBLE);
            fiveLayout.setVisibility(View.VISIBLE);
            fab.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.fab_expand)));
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_close_black_24dp));
        } else {
            oneLayout.setVisibility(View.GONE);
            threeLayout.setVisibility(View.GONE);
            fiveLayout.setVisibility(View.GONE);
            fab.setBackgroundTintList(ColorStateList.valueOf(this.getResources().getColor(R.color.fab_collapse)));
            fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_list_white_24dp));
        }
    }

    private boolean eventMatches(WeekViewEvent event, int year, int month) {
        return (event.getStartTime().get(Calendar.YEAR) == year && event.getStartTime().get(Calendar.MONTH) == month - 1) || (event.getEndTime().get(Calendar.YEAR) == year && event.getEndTime().get(Calendar.MONTH) == month - 1);
    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {

        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        try {
            Log.e(LOG_TAG, "OnMonthChange");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.YEAR, newYear);
            calendar.set(Calendar.MONTH, newMonth - 1);
            int day;
            calendar.set(Calendar.DATE, 1);

            int dayMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
            Schedule schedule = new Schedule(getContext());
            for (day = 1; day <= dayMaximum; day++) {
                calendar.set(Calendar.DATE, day);
                List<Course> courses = schedule.getCoursesByDate(calendar);
                Iterator<Course> iterator = courses.iterator();

                Course formerCourse = null;
                Course currentCourse = null;
                WeekViewEvent event = null;
                while (iterator.hasNext()) {
                    if (formerCourse == null) {
                        currentCourse = iterator.next();
                        formerCourse = currentCourse;
                        eventID++;
                        Calendar startTime = (Calendar) calendar.clone();
                        Calendar endTime = (Calendar) calendar.clone();
                        //Log.e(LOG_TAG, startTime.toString());
                        //Log.e(LOG_TAG, endTime.toString());
                        startTime.set(Calendar.HOUR_OF_DAY, currentCourse.StartHour);
                        startTime.set(Calendar.MINUTE, currentCourse.StartMinute);
                        startTime.set(Calendar.SECOND, 0);
                        endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                        endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                        endTime.set(Calendar.SECOND, 0);
                        event = new WeekViewEvent(eventID, currentCourse.courseName + "\n" + currentCourse.teacher + "\n" + currentCourse.classroom, startTime, endTime);
                        continue;
                    }
                    formerCourse = currentCourse;
                    currentCourse = iterator.next();
                    if (Course.isContinuum(currentCourse, formerCourse)) {
                        Calendar endTime = event.getEndTime();
                        endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                        endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                        event.setEndTime(endTime);
                    } else {
                        event.setColor(getResources().getColor(getColorByString(event.getName().split("\n")[0])));
                        events.add(event);
                        eventID++;
                        Calendar startTime = (Calendar) calendar.clone();
                        Calendar endTime = (Calendar) calendar.clone();
                        //Log.e(LOG_TAG, startTime.toString());
                        //Log.e(LOG_TAG, endTime.toString());
                        startTime.set(Calendar.HOUR_OF_DAY, currentCourse.StartHour);
                        startTime.set(Calendar.MINUTE, currentCourse.StartMinute);
                        startTime.set(Calendar.SECOND, 0);
                        endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                        endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                        endTime.set(Calendar.SECOND, 0);
                        event = new WeekViewEvent(eventID, currentCourse.courseName + "\n" + currentCourse.teacher + "\n" + currentCourse.classroom, startTime, endTime);
                    }

                }
                if (event != null) {
                    event.setColor(getResources().getColor(getColorByString(event.getName().split("\n")[0])));
                    events.add(event);
                }

            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return events;
    }

    public int getColorByString(String str) {
        int hash = Math.abs(str.hashCode());
        int colorID = hash % 4;
        //Log.e(LOG_TAG,str);
        //Log.e(LOG_TAG,String.valueOf(hash));
        //Log.e(LOG_TAG,String.valueOf(colorID));
        switch (colorID) {
            case 0:
                return R.color.event_color_01;
            case 1:
                return R.color.event_color_02;
            case 2:
                return R.color.event_color_03;
            case 3:
                return R.color.event_color_04;
        }
        return R.color.event_color_01;
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Intent mIntent = new Intent(getContext(), ViewCourseDetails.class);
        mIntent.putExtra("startTime", event.getStartTime());
        mIntent.putExtra("endTime", event.getEndTime());
        mIntent.putExtra("courseInfo", event.getName());
        mIntent.putExtra("color", event.getColor());
        startActivity(mIntent);
    }

    @Override
    public void onFirstVisibleDayChanged(Calendar newFirstVisibleDay, Calendar oldFirstVisibleDay) {

    }
}
