/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Course;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.denghaoqing.sysu.Achievement.AchievementDatabase;
import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.denghaoqing.sysu.Utils.OnTaskFinishHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-3-2.
 */

public class Course {
    private CourseDataBase courseDataBase = null;
    private Context context;
    private CookieHelper cookieHelper;
    public Course(Context context) {
        this.context = context;
        cookieHelper = new CookieHelper(context);
        if (courseDataBase == null) {
            courseDataBase = new CourseDataBase(context);
        }
    }

    public String getCourseTypeById(int id) {
        SQLiteDatabase database = courseDataBase.getReadableDatabase();
        String sql = "select * from coursetype where id=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(id)});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            return cursor.getString(cursor.getColumnIndex(CourseDataBase.colCategoryName));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        database.close();
        return null;
    }

    public ArrayList<Integer> getCourseTypeList() {
        ArrayList<Integer> typeIdList = new ArrayList<>();
        SQLiteDatabase database = courseDataBase.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from coursetype", new String[]{});
        while (cursor.moveToNext()) {
            typeIdList.add(cursor.getInt(cursor.getColumnIndex("id")));
        }
        if (!cursor.isClosed()) {
            cursor.close();
            courseDataBase.close();
        }
        return typeIdList;
    }

    public void syncPlanCourseType(int grade, String professionId) {
        final SQLiteDatabase database = courseDataBase.getWritableDatabase();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("grade", grade);
        params.put("professionCode", professionId);
        params.put("programType", "all");
        client.setCookieStore(cookieHelper);
        client.get(UEMS.UEMS_STUDENT_PLAN_COURSE_TYPE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject resp = new JSONObject(new String(responseBody));
                    if (resp.getInt("code") == 200) {
                        JSONArray courseTypeList = resp.getJSONArray("data");
                        for (int i = 0; i < courseTypeList.length(); i++) {
                            ContentValues categoryCV = new ContentValues();
                            JSONObject courseType = courseTypeList.getJSONObject(i);
                            categoryCV.put(AchievementDatabase.colCategoryId, courseType.getString("courseTypeCode"));
                            categoryCV.put(AchievementDatabase.colCategoryName, courseType.getString("courseTypeName"));
                            try {
                                database.insertOrThrow(AchievementDatabase.dbCourseType, null, categoryCV);
                                database.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                    }

                } catch (Exception e) {
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
        //&grade=2016&professionCode=n05010102&programType=all
    }

    public void syncPlanCourseType(int grade, String professionId, final OnTaskFinishHandler handler) {
        final SQLiteDatabase database = courseDataBase.getWritableDatabase();
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("grade", grade);
        params.put("professionCode", professionId);
        params.put("programType", "all");
        client.setCookieStore(cookieHelper);
        client.get(UEMS.UEMS_STUDENT_PLAN_COURSE_TYPE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject resp = new JSONObject(new String(responseBody));
                    if (resp.getInt("code") == 200) {
                        JSONArray courseTypeList = resp.getJSONArray("data");
                        for (int i = 0; i < courseTypeList.length(); i++) {
                            ContentValues categoryCV = new ContentValues();
                            JSONObject courseType = courseTypeList.getJSONObject(i);
                            categoryCV.put(AchievementDatabase.colCategoryId, courseType.getString("courseTypeCode"));
                            categoryCV.put(AchievementDatabase.colCategoryName, courseType.getString("courseTypeName"));
                            try {
                                database.insertOrThrow(AchievementDatabase.dbCourseType, null, categoryCV);
                                database.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        handler.onTaskSucceed();
                    } else {
                        handler.onTaskFailure();
                    }

                } catch (Exception e) {
                    handler.onTaskFailure();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                handler.onTaskFailure();
            }
        });
        //&grade=2016&professionCode=n05010102&programType=all
    }

    public void syncTeachPlanCourse(final int grade, final String professionId, final OnTaskFinishHandler handler) {
        syncPlanCourseType(grade, professionId, new OnTaskFinishHandler() {
            @Override
            public void onTaskSucceed() {
                ArrayList<Integer> typeIds = getCourseTypeList();
                for (final int typeId : typeIds) {
                    //?&courseTypeCode=10&grade=2016&professionCode=n08070400
                    RequestParams params = new RequestParams();
                    params.put("courseTypeCode", typeId);
                    params.put("grade", grade);
                    params.put("professionCode", professionId);
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.setCookieStore(cookieHelper);
                    client.get(UEMS.UEMS_STUDENT_PLAN_COURSE_LIST, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            try {
                                Log.e("test", new String(responseBody));
                                JSONObject resp = new JSONObject(new String(responseBody));
                                if (resp.getInt("code") == 200) {
                                    JSONArray courseList = resp.getJSONObject("data").getJSONArray("courseList");
                                    for (int i = 0; i < courseList.length(); i++) {
                                        JSONObject course = courseList.getJSONObject(i);
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(CourseDataBase.colCourseCnName, course.getString("courseName"));
                                        contentValues.put(CourseDataBase.colCourseEnName, course.getString("courseEnName"));
                                        contentValues.put(CourseDataBase.colCourseNumber, course.getString("courseNumber"));
                                        contentValues.put(CourseDataBase.colCourseType, typeId);
                                        contentValues.put(CourseDataBase.colSemester, course.getString("semester"));
                                        contentValues.put(CourseDataBase.colPlanCredit, course.getString("totalCredit"));
                                        contentValues.put(CourseDataBase.colPlanPeriod, course.getString("totalPeriod"));
                                        try {
                                            SQLiteDatabase database = courseDataBase.getWritableDatabase();
                                            database.insertOrThrow(CourseDataBase.courseDb, null, contentValues);
                                            database.close();
                                            handler.onTaskSucceed();
                                        } catch (Exception e) {

                                        }


                                    }
                                } else {
                                    handler.onTaskFailure();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                handler.onTaskFailure();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e("error", String.valueOf(statusCode));
                            Log.e("rb", new String(responseBody));
                        }
                    });
                }
            }

            @Override
            public void onTaskFailure() {

            }
        });

    }
}
