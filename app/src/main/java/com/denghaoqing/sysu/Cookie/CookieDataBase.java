/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Cookie;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-2-26.
 */

public class CookieDataBase extends SQLiteOpenHelper {
    private static final String COOKIE_DB_FILENAME = "Cookie.db";

    public CookieDataBase(Context context) {
        super(context, COOKIE_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists cookie(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "domain VARCHAR(255)," +
                "path VARCHAR(255)," +
                "name VARCHAR(255)," +
                "value VARCHAR(4096))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
