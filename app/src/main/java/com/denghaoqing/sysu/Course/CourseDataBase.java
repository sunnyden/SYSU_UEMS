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
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-3-2.
 */

public class CourseDataBase extends SQLiteOpenHelper {
    public static final String colCategoryId = "id";
    public static final String colCategoryName = "name";
    public static final String dbCourseType = "coursetype";
    public static final String colCourseCnName = "cn_name";
    public static final String colCourseEnName = "en_name";
    public static final String colCourseNumber = "course_number";
    public static final String colCourseType = "course_type";
    public static final String colSemester = "semester";
    public static final String colPlanPeriod = "period";
    public static final String colPlanCredit = "credit";
    public static final String courseDb = "course";
    private static final String COURSE_DB_FILENAME = "course.db";

    public CourseDataBase(Context context) {
        super(context, COURSE_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists coursetype(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "name VARCHAR(10))");
        sqLiteDatabase.execSQL("create table if not exists course(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "cn_name VARCHAR(128)," +
                "en_name VARCHAR(128)," +
                "course_number VARCHAR(32) UNIQUE," +
                "credit FLOAT," +
                "period INTEGER," +
                "course_type INTEGER," +
                "semester VARCHAR(10))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
