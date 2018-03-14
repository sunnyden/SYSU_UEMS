/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
