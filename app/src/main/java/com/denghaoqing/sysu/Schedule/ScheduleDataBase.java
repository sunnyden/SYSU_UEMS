/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Schedule;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-2-27.
 */

public class ScheduleDataBase extends SQLiteOpenHelper {
    private static final String SCHEDULE_DB_FILENAME = "Schedule.db";

    public ScheduleDataBase(Context context) {
        super(context, SCHEDULE_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists schedule(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "ac_year VARCHAR(10)," +
                "week INTEGER," +
                "time INTEGER," +   //time is a symbol of weekday,0 means sunday and so forth
                "course VARCHAR(255)," +
                "teacher VARCHAR(255)," +
                "section INTEGER," +
                "classroom VARCHAR(255))");
        sqLiteDatabase.execSQL("create table if not exists schoolcalender(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "ac_year VARCHAR(10)," +
                "week INTEGER," +
                "bgn_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "end_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "pulled TINYINT(1) DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
