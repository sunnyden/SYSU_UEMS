/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Memo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sunny on 18-3-7.
 */

public class MemoDatabase extends SQLiteOpenHelper {
    private static final String MEMO_DB_FILENAME = "memo.db";

    public MemoDatabase(Context context) {
        super(context, MEMO_DB_FILENAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table if not exists memo(" +
                "id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "title VARCHAR(512)," +
                "content TEXT," +
                "photo TEXT," +
                "course VARCHAR(128)," +
                "notice_id INTEGER DEFAULT -1," +
                "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
