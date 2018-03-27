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

package com.denghaoqing.sysu;

import android.support.wear.widget.WearableRecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.Schedule.Course;

/**
 * Created by sunny on 18-3-27.
 */

public class CourseViewHolder extends WearableRecyclerView.ViewHolder {
    private TextView mClassRoom, mCourseName, mTeacher, mTime;
    private RelativeLayout courseCard;
    private Course course;

    public CourseViewHolder(View view) {
        super(view);
        mClassRoom = view.findViewById(R.id.classroom);
        mCourseName = view.findViewById(R.id.course_name);
        mTeacher = view.findViewById(R.id.teacher);
        mTime = view.findViewById(R.id.time);
        courseCard = view.findViewById(R.id.course_card);
    }

    public void setCourse(Course course) {
        this.course = course;
        mClassRoom.setText(course.classroom);
        mCourseName.setText(course.courseName);
        mTeacher.setText(course.teacher);
        mTime.setText(mTime.getContext().getString(R.string.section, course.sections[0], course.sections[1]));
        courseCard.setBackgroundColor(courseCard.getContext().getColor(getColorByString(course.courseName)));
    }

    public void setText(String cName, String teacher, String time, String classroom) {
        mClassRoom.setText(classroom);
        mTeacher.setText(teacher);
        mTime.setText(time);
        mCourseName.setText(cName);
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

}
