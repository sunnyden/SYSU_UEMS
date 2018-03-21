/*
 *  Copyright (C) 2013 - 2018, Haoqing Deng <dhq.sunny@gmail.com>
 *
 *  This file is part of the SYSU UEMS.
 *
 *  SYSU UEMS is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  SYSU UEMS is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with SYSU UEMS; see the file COPYING. If not, see
 *  <http://www.gnu.org/licenses/>.
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
