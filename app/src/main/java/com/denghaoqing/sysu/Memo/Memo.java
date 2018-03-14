/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Memo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by sunny on 18-3-4.
 */

public class Memo {
    public boolean hasImage = false;
    public boolean hasRefs = false;
    private Context context;
    private String courseName, title, content, imagePath, time;
    private int refCourse;
    private long id;

    private Memo(Context context) {
        this.context = context;
    }

    public static Memo writeMemo(Context context, String courseName, String title, String content, String imagePath, int refCourse) {
        MemoDatabase memoDatabase = new MemoDatabase(context);
        SQLiteDatabase database = memoDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("course", courseName);
        contentValues.put("title", title);
        contentValues.put("content", content);
        contentValues.put("photo", imagePath);
        if (refCourse != -1) {
            contentValues.put("notice_id", refCourse);
        }

        long id = database.insert("memo", null, contentValues);
        if (database.isOpen()) {
            database.close();
        }
        Memo memo = new Memo(context);
        memo.setContent(content);
        memo.setTitle(title);
        memo.setCourseName(courseName);
        memo.setImagePath(imagePath);
        memo.setRefCourse(refCourse);
        memo.setId(id);
        return memo;

    }

    public static ArrayList<Memo> getMemosByRefs(Context context, int refId) {
        MemoDatabase memoDatabase = new MemoDatabase(context);
        SQLiteDatabase database = memoDatabase.getReadableDatabase();
        String sql = "select * from memo where notice_id = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(refId)});
        ArrayList<Memo> memoArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Memo memo = new Memo(context);
            memo.setContent(cursor.getString(cursor.getColumnIndex("content")));
            memo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            memo.setCourseName(cursor.getString(cursor.getColumnIndex("course")));
            memo.setImagePath(cursor.getString(cursor.getColumnIndex("photo")));
            memo.setRefCourse(cursor.getInt(cursor.getColumnIndex("notice_id")));
            memo.setTime(cursor.getString(cursor.getColumnIndex("time")));
            memo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            memoArrayList.add(memo);
        }
        if (database.isOpen()) {
            database.close();
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return memoArrayList;
    }

    public static Memo getMemo(Context context, int id) {
        MemoDatabase memoDatabase = new MemoDatabase(context);
        SQLiteDatabase database = memoDatabase.getReadableDatabase();
        String sql = "select * from memo where id = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        Memo memo = new Memo(context);
        memo.setContent(cursor.getString(cursor.getColumnIndex("content")));
        memo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
        memo.setCourseName(cursor.getString(cursor.getColumnIndex("course")));
        memo.setImagePath(cursor.getString(cursor.getColumnIndex("photo")));
        memo.setRefCourse(cursor.getInt(cursor.getColumnIndex("notice_id")));
        memo.setTime(cursor.getString(cursor.getColumnIndex("time")));
        memo.setId(id);

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return memo;
    }

    public static ArrayList<Memo> getMemo(Context context, String courseName) {
        MemoDatabase memoDatabase = new MemoDatabase(context);
        SQLiteDatabase database = memoDatabase.getReadableDatabase();
        String sql = "select * from memo where course = ?";
        Cursor cursor = database.rawQuery(sql, new String[]{courseName});
        ArrayList<Memo> memoArrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            Memo memo = new Memo(context);
            memo.setContent(cursor.getString(cursor.getColumnIndex("content")));
            memo.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            memo.setCourseName(cursor.getString(cursor.getColumnIndex("course")));
            memo.setImagePath(cursor.getString(cursor.getColumnIndex("photo")));
            memo.setRefCourse(cursor.getInt(cursor.getColumnIndex("notice_id")));
            memo.setTime(cursor.getString(cursor.getColumnIndex("time")));
            memo.setId(cursor.getInt(cursor.getColumnIndex("id")));
            memoArrayList.add(memo);
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }
        return memoArrayList;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        hasImage = !imagePath.equals("");
        this.imagePath = imagePath;
    }

    public int getRefCourse() {
        return refCourse;
    }

    public void setRefCourse(int refCourse) {
        hasRefs = refCourse != -1;
        this.refCourse = refCourse;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean delete() {
        MemoDatabase memoDatabase = new MemoDatabase(this.context);
        SQLiteDatabase database = memoDatabase.getWritableDatabase();
        int returnValue = database.delete("memo", "id=?", new String[]{String.valueOf(id)});
        if (database.isOpen()) {
            database.close();
        }
        return returnValue >= 1;
    }

    public int save() {
        MemoDatabase memoDatabase = new MemoDatabase(this.context);
        SQLiteDatabase database = memoDatabase.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("content", content);
        contentValues.put("photo", imagePath);
        contentValues.put("notice_id", refCourse);
        int rows = database.update("memo", contentValues, "id=?", new String[]{String.valueOf(id)});
        if (database.isOpen()) {
            database.close();
        }
        return rows;
    }
}
