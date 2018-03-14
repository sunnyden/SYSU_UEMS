/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sunny on 18-3-2.
 */

public class Course {
    private CourseDataBase courseDataBase;
    private Context context;

    public Course(Context context) {
        this.context = context;
    }

    public String getCourseTypeById(int id) {
        courseDataBase = new CourseDataBase(context);
        SQLiteDatabase database = courseDataBase.getReadableDatabase();
        String sql = "select * from coursetype where id=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getString(cursor.getColumnIndex(CourseDataBase.colCategoryName));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
        return null;
    }
}
