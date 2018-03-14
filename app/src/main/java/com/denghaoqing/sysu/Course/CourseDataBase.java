/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Course;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-3-2.
 */

public class CourseDataBase extends SQLiteOpenHelper {
    public static final String colCategoryId = "id";
    public static final String colCategoryName = "name";
    public static final String dbCourseType = "coursetype";
    private static final String COURSE_DB_FILENAME = "course.db";

    public CourseDataBase(Context context) {
        super(context, COURSE_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists coursetype(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
