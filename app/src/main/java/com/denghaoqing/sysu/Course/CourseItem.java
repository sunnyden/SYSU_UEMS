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

package com.denghaoqing.sysu.Course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sunny on 18-3-21.
 */

public class CourseItem {
    private String cnName, enName, semaster, courseId;
    private float credit;
    private int schoolTime;

    CourseItem(String cnName, String enName, String semaster, float credit, int schoolTime, String courseId) {
        this.cnName = cnName;
        this.enName = enName;
        this.semaster = semaster;
        this.credit = credit;
        this.schoolTime = schoolTime;
        this.courseId = courseId;
    }

    public static CourseItem getCourseById(int id, Context context) {
        SQLiteDatabase database = new CourseDataBase(context).getReadableDatabase();
        String sql = "select * from course where id = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();

        String cnName, enName, semaster, courseId;
        float credit;
        int schoolTime;
        cnName = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseCnName));
        enName = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseEnName));
        semaster = cursor.getString(cursor.getColumnIndex(CourseDataBase.colSemester));
        courseId = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseNumber));
        credit = cursor.getFloat(cursor.getColumnIndex(CourseDataBase.colPlanCredit));
        schoolTime = cursor.getInt(cursor.getColumnIndex(CourseDataBase.colPlanPeriod));
        if (!cursor.isClosed()) {
            cursor.close();
            database.close();
        }
        return new CourseItem(cnName, enName, semaster, credit, schoolTime, courseId);
    }

    public String getCnName() {
        return cnName;
    }

    public String getEnName() {
        return enName;
    }

    public String getSemaster() {
        return semaster;
    }

    public String getCourseId() {
        return courseId;
    }

    public float getCredit() {
        return credit;
    }

    public int getSchoolTime() {
        return schoolTime;
    }

}
