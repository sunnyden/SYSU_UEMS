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

package com.denghaoqing.sysu.Profession;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by sunny on 18-3-2.
 */

public class Profession {
    private Context context;
    private ProfessionDataBase professionDataBase = null;
    private String code;
    private String professionId;
    private String cnName;
    private String enName;
    private int eduSys;
    private int maxYear;
    private String degreeName;

    public Profession(Context context) {
        professionDataBase = new ProfessionDataBase(context);
        this.context = context;
    }

    public Profession(Context context, String code) {
        this.context = context;
        professionDataBase = new ProfessionDataBase(context);
        this.code = code;
        SQLiteDatabase database = professionDataBase.getReadableDatabase();
        String sql = "select * from profession where code=?";
        Cursor cursor = database.rawQuery(sql, new String[]{code});
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            this.professionId = cursor.getString(cursor.getColumnIndex("professionId"));
            this.cnName = cursor.getString(cursor.getColumnIndex("cn_name"));
            this.enName = cursor.getString(cursor.getColumnIndex("en_name"));
            this.eduSys = cursor.getInt(cursor.getColumnIndex("edusys"));
            this.maxYear = cursor.getInt(cursor.getColumnIndex("max_year"));
            this.degreeName = cursor.getString(cursor.getColumnIndex("degree_name"));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
    }

    public Profession(Context context, int id) {
        this.context = context;
        professionDataBase = new ProfessionDataBase(context);
        SQLiteDatabase database = professionDataBase.getReadableDatabase();
        String sql = "select * from profession where code=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            this.code = cursor.getString(cursor.getColumnIndex("code"));
            this.professionId = cursor.getString(cursor.getColumnIndex("professionId"));
            this.cnName = cursor.getString(cursor.getColumnIndex("cn_name"));
            this.enName = cursor.getString(cursor.getColumnIndex("en_name"));
            this.eduSys = cursor.getInt(cursor.getColumnIndex("edusys"));
            this.maxYear = cursor.getInt(cursor.getColumnIndex("max_year"));
            this.degreeName = cursor.getString(cursor.getColumnIndex("degree_name"));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
    }

    public long addProfession(String code, String professionId, String cnName, String enName,
                              int eduSys, int maxYear, String degreeName) {
        SQLiteDatabase database = professionDataBase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("code", code);
        contentValues.put("professionId", professionId);
        contentValues.put("cn_name", cnName);
        contentValues.put("en_name", enName);
        contentValues.put("edusys", eduSys);
        contentValues.put("max_year", maxYear);
        contentValues.put("degree_name", degreeName);
        long rows;
        try {
            rows = database.insertOrThrow("profession", null, contentValues);
        } catch (Exception e) {
            rows = 0;
        }

        database.close();
        return rows;
    }

}
