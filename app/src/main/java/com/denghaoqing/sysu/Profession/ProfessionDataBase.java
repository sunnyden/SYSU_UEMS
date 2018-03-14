/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Profession;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-3-2.
 */

public class ProfessionDataBase extends SQLiteOpenHelper {
    private static final String PROFESSION_DB_FILENAME = "profession.db";

    public ProfessionDataBase(Context context) {
        super(context, PROFESSION_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists profession(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "code VARCHAR(32) UNIQUE," +
                "professionId VARCHAR(128) UNIQUE," +
                "cn_name VARCHAR(128)," +
                "en_name VARCHAR(128)," +
                "edusys INTEGER," +
                "max_year INTEGER," +
                "degree_name VARCHAR(255))");
        /*
        sqLiteDatabase.execSQL("create table if not exists schoolcalender(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "ac_year VARCHAR(10)," +
                "week INTEGER," +
                "bgn_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "end_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL," +
                "pulled TINYINT(1) DEFAULT 0)");*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
