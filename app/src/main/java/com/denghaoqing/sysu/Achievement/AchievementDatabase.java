/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
