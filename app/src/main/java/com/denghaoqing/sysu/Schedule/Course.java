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

package com.denghaoqing.sysu.Schedule;

/**
 * Created by sunny on 18-2-28.
 */

public class Course {
    public int id = -1;
    public int section;
    public int dayOfWeek;
    public String courseName;
    public String teacher;
    public int timeSegment;// 1--morning 2--afternoon 3--night
    public String classroom;
    public int StartHour;
    public int StartMinute;
    public int EndHour;
    public int EndMinute;
    public int week;
    private long remainingTime;
    private String readableString = "";
    private GeneralSchedule generalSchedule;

    public Course(String cName, String teacher, String classroom, int section, int dayOfWeek, int week) {
        this.courseName = cName;
        this.teacher = teacher;
        this.classroom = classroom;
        this.section = section;
        this.dayOfWeek = dayOfWeek;
        this.week = week;
        if (section <= 4) {
            timeSegment = 1;
        } else if (section > 4 && section <= 8) {
            timeSegment = 2;
        } else {
            timeSegment = 3;
        }
        generalSchedule = new GeneralSchedule();
        generalSchedule.getSectionDuration(this.section);
        this.StartHour = generalSchedule.StartHour;
        this.StartMinute = generalSchedule.StartMinute;
        this.EndHour = generalSchedule.EndHour;
        this.EndMinute = generalSchedule.EndMinute;
    }

    public static boolean isContinuum(Course course1, Course course2) {
        if (course1.dayOfWeek != course2.dayOfWeek) {
            return false;
        }
        if (course1.timeSegment != course2.timeSegment) {
            return false;
        }
        if (Math.abs(course1.section - course2.section) != 1) {
            return false;
        }
        if (!course1.teacher.equals(course2.teacher)) {
            return false;
        }
        if (!course1.classroom.equals(course2.classroom)) {
            return false;
        }
        return course1.courseName.equals(course2.courseName);
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long mills) {
        this.remainingTime = mills;
    }

    public void setString(String string) {
        readableString = string;
    }

    @Override
    public String toString() {
        return readableString;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
