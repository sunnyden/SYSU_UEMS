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

package com.denghaoqing.sysu.Achievement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.denghaoqing.sysu.Course.CourseDataBase;

/**
 * Created by sunny on 18-3-2.
 */

public class AchievementDatabase extends CourseDataBase {

    public static final String colCourseID = "courseId";
    public static final String colCourseName = "courseName";
    public static final String colTeacher = "teacher";
    public static final String colScore = "score";
    public static final String colPoint = "point";
    public static final String colCateID = "cate_id";
    public static final String colCredit = "credit";
    public static final String colRank = "rank";
    public static final String colTotalPeople = "total_people";
    public static final String colSemester = "semester";
    public static final String colSchoolYear = "school_year";
    public static final String dbScore = "score";

    public AchievementDatabase(Context context) {
        super(context);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        super.onCreate(sqLiteDatabase);
        sqLiteDatabase.execSQL("create table if not exists score(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "courseId VARCHAR(32) UNIQUE," +   //I think it should be unique, for indexing
                "courseName VARCHAR(128)," +
                "teacher VARCHAR(32)," +
                "score FLOAT," +
                "point FLOAT," +
                "cate_id INTEGER," +
                "credit FLOAT," +
                "rank INTEGER," +
                "total_people INTEGER," +
                "school_year VARCHAR(16)," +
                "semester INTEGER)");
    }

}
