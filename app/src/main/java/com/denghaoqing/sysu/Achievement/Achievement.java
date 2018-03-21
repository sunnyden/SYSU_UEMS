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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.Course.Course;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-3-2.
 */

public class Achievement {
    private static final String LOG_TAG = "Achievement";
    public boolean expanded;
    public String strCourseName, strScore, strPoint, strTeacher, strCourseType, strYear, strCredit;
    public int rank, total, category, semester;
    public float point, score, credit;
    private Context context;
    private AchievementDatabase achievementDatabase;


    public Achievement(String strCourseName, String strScore, String strPoint, String strTeacher, String strCourseType, String strYear,
                       float score, int rank, float point, int total, int category, int semester, float credit, String strCredit) {
        this.strCourseName = strCourseName;
        this.strScore = strScore;
        this.strPoint = strPoint;
        this.strTeacher = strTeacher;
        this.strCourseType = strCourseType;
        this.strYear = strYear;
        this.score = score;
        this.rank = rank;
        this.point = point;
        this.total = total;
        this.category = category;
        this.semester = semester;
        this.credit = credit;
        this.strCredit = strCredit;
    }


    public Achievement(Context context) {
        this.context = context;
        achievementDatabase = new AchievementDatabase(context);
    }

    public static ArrayList<Achievement> getAchievementListWithCondition(Context context, String query, String[] params) {
        ArrayList<Achievement> achievements = new ArrayList<>();
        AchievementDatabase achievementDatabase = new AchievementDatabase(context);
        SQLiteDatabase database = achievementDatabase.getReadableDatabase();
        Cursor cursor = database.rawQuery(query, params);
        while (cursor.moveToNext()) {
            String strCourseName, strScore, strPoint, strTeacher, strCourseType, strYear, strCredit;
            int rank, total, category, semester;
            float score, point, credit;
            strCourseName = cursor.getString(cursor.getColumnIndex(AchievementDatabase.colCourseName));
            strTeacher = cursor.getString(cursor.getColumnIndex(AchievementDatabase.colTeacher));
            score = cursor.getFloat(cursor.getColumnIndex(AchievementDatabase.colScore));
            point = cursor.getFloat(cursor.getColumnIndex(AchievementDatabase.colPoint));
            total = cursor.getInt(cursor.getColumnIndex(AchievementDatabase.colTotalPeople));
            rank = cursor.getInt(cursor.getColumnIndex(AchievementDatabase.colRank));
            category = cursor.getInt(cursor.getColumnIndex(AchievementDatabase.colCateID));
            semester = cursor.getInt(cursor.getColumnIndex(AchievementDatabase.colSemester));
            credit = cursor.getFloat(cursor.getColumnIndex(AchievementDatabase.colCredit));
            strScore = String.format("%.1f", score);
            strPoint = String.format("%.1f", point);
            strCredit = String.format("%.1f", credit);

            Course course = new Course(context);
            strCourseType = course.getCourseTypeById(category);
            strYear = cursor.getString(cursor.getColumnIndex(AchievementDatabase.colSchoolYear));
            achievements.add(new Achievement(strCourseName, strScore, strPoint, strTeacher, strCourseType, strYear, score, rank, point, total, category, semester, credit, strCredit));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
        return achievements;
    }

    public void pullScoreFromServer() {
        CookieHelper cookieHelper = new CookieHelper(context);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(cookieHelper);
        client.get(UEMS.UEMS_PERSONAL_SCORE, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    Log.e(LOG_TAG, "OnSuccess");
                    JSONObject response = new JSONObject(new String(responseBody));
                    if (response.getInt("code") == 200) {
                        JSONArray jsonArray = response.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject score = jsonArray.getJSONObject(i);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(AchievementDatabase.colRank, score.getString("rank").split("/")[0]);
                                contentValues.put(AchievementDatabase.colCateID, score.getString("scoCourseCategory"));
                                contentValues.put(AchievementDatabase.colCourseName, score.getString("scoCourseName"));
                                contentValues.put(AchievementDatabase.colCourseID, score.getString("scoCourseNumber"));
                                contentValues.put(AchievementDatabase.colCredit, score.getString("scoCredit"));
                                contentValues.put(AchievementDatabase.colScore, score.getString("scoFinalScore"));
                                contentValues.put(AchievementDatabase.colPoint, score.getString("scoPoint"));
                                contentValues.put(AchievementDatabase.colSchoolYear, score.getString("scoSchoolYear"));
                                contentValues.put(AchievementDatabase.colSemester, score.getString("scoSemester"));
                                contentValues.put(AchievementDatabase.colTeacher, score.getString("scoTeacherName"));
                                contentValues.put(AchievementDatabase.colTotalPeople, score.getString("total"));
                                ContentValues categoryCV = new ContentValues();
                                categoryCV.put(AchievementDatabase.colCategoryId, score.getString("scoCourseCategory"));
                                categoryCV.put(AchievementDatabase.colCategoryName, score.getString("scoCourseCategoryName"));
                                Log.e(LOG_TAG, contentValues.toString());
                                SQLiteDatabase database = achievementDatabase.getWritableDatabase();
                                try {
                                    database.insertOrThrow(AchievementDatabase.dbScore, null, contentValues);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                try {
                                    database.insertOrThrow(AchievementDatabase.dbCourseType, null, categoryCV);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            } catch (Exception e) {
                                Log.e(LOG_TAG, String.valueOf(i));
                                Log.d(LOG_TAG, e.toString());
                            }


                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }
}
