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
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.AddMemoActivity;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.Schedule.Course;

/**
 * Created by sunny on 18-3-13.
 */


public class OngoingCourseViewHolder extends RecyclerView.ViewHolder {
    private TextView ongoingCourse, courseTeacher;
    private RelativeLayout addMemoLayout;
    private Context context;
    private Course curCourse;

    public OngoingCourseViewHolder(View view, Context context) {
        super(view);
        ongoingCourse = view.findViewById(R.id.ongoingCourseName);
        courseTeacher = view.findViewById(R.id.ongoing_teacher_name);
        addMemoLayout = view.findViewById(R.id.ongoing_memo_action);
        this.context = context;
    }

    public void setOngoingCourse(Course course) {
        this.curCourse = course;
        ongoingCourse.setText(course.courseName);
        courseTeacher.setText(course.teacher);
        addMemoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(context, AddMemoActivity.class);
                mIntent.putExtra("courseName", curCourse.courseName);
                context.startActivity(mIntent);
            }
        });
    }
}
