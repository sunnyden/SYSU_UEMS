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

package com.denghaoqing.sysu.Cookie;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;

/**
 * Created by sunny on 18-2-26.
 */

public class CookieHelper implements CookieStore {
    private static CookieDataBase cookieDataBase = null;
    private final String HOST_CAS = "";
    private final String HOST_UEMS = "";
    private final String LOG_TAG = "CookieHepler";
    private List<Cookie> Cookies = new ArrayList<Cookie>();
    private Context context;

    public CookieHelper(Context context) {
        //Log.d(LOG_TAG,"Initialize...");
        //CookieManager cookieManager = new CookieManager();
        //cookieManager.
        this.context = context;
        loadCookieFromDatabase();
    }

    private void loadCookieFromDatabase() {
        if (cookieDataBase == null) {
            cookieDataBase = new CookieDataBase(context);
        }

        SQLiteDatabase database = cookieDataBase.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from cookie", null);
        Cookies.clear();
        while (cursor.moveToNext()) {
            Cookies.add(new OptimizedCookie(cursor.getString(cursor.getColumnIndex("domain")),
                    cursor.getString(cursor.getColumnIndex("path")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("value"))));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
    }

    @Override
    public void addCookie(Cookie cookie) {
        Log.d(LOG_TAG, "add()");
        Log.d(LOG_TAG, cookie.toString());
        SQLiteDatabase database = cookieDataBase.getWritableDatabase();
        String sql = "select * from cookie where domain=? and path=? and name=?";
        Cursor cursor = database.rawQuery(sql,
                new String[]{cookie.getDomain(), cookie.getPath(), cookie.getName()});
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            sql = "delete from cookie where id=?";
            database.execSQL(sql,
                    new Object[]{cursor.getInt(cursor.getColumnIndex("id"))});
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("domain", cookie.getDomain());
        contentValues.put("path", cookie.getPath());
        contentValues.put("name", cookie.getName());
        contentValues.put("value", cookie.getValue());
        database.insert("cookie", null, contentValues);
        database.close();
        loadCookieFromDatabase();

    }

    @Override
    public boolean clearExpired(Date date) {
        Log.d(LOG_TAG, "clearExpired()");
        return false;
    }

    @Override
    public void clear() {
        Log.d(LOG_TAG, "clear()");
    }

    @Override
    public List<Cookie> getCookies() {
        Log.d(LOG_TAG, "getCookies()");
        Log.d(LOG_TAG, String.valueOf(Cookies.size()));
        return Cookies;
    }


}
