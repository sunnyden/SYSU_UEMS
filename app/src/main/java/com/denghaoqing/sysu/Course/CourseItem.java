/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
