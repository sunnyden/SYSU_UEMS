/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.Schedule.Course;
import com.denghaoqing.sysu.Schedule.GeneralSchedule;

import java.util.Calendar;

/**
 * Created by sunny on 18-3-12.
 */

public class UpcomingCourseViewHolder extends RecyclerView.ViewHolder {
    private TextView upcomingCourse, courseTeacher, courseTime, courseClassroom;
    private Context context;

    public UpcomingCourseViewHolder(View view, Context context) {
        super(view);
        upcomingCourse = view.findViewById(R.id.courseName);
        courseTeacher = view.findViewById(R.id.teacher_name);
        courseClassroom = view.findViewById(R.id.classroom_place);
        courseTime = view.findViewById(R.id.time_text);
        this.context = context;
    }

    public void setUpcomingCourse(Course course) {
        upcomingCourse.setText(course.courseName);
        GeneralSchedule generalSchedule = new GeneralSchedule();
        generalSchedule.getSectionDuration(course.section);
        long timeInterval = GeneralSchedule.getTimeInterval(Calendar.getInstance(), course.section);
        int minutes = (int) (timeInterval / 60000);
        if (minutes < 1) {
            courseTime.setText(R.string.seconds_later);
        } else {
            courseTime.setText(String.format(context.getString(R.string.minutes_later), minutes));
        }
        courseClassroom.setText(course.classroom);
        courseTeacher.setText(course.teacher);
    }
}
