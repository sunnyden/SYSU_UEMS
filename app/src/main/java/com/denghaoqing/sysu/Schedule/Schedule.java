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

package com.denghaoqing.sysu.Schedule;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-2-27.
 */

public class Schedule {
    private final static String LOG_TAG = "Schedule";
    private Context context;
    private CookieHelper cookieHelper;
    private ScheduleDataBase scheduleDataBase;
    private boolean haveSemasterResult = false;

    public Schedule(Context context) {
        this.context = context;
        this.cookieHelper = new CookieHelper(context);
        this.scheduleDataBase = new ScheduleDataBase(context);
    }

    public void pullScheduleFromServer(final String semester, final int week) {
        //TODO 1.Verify whether the data exists in the local storage
        //TODO 2.Pull non-existing data from server
        SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
        String querySQL = "select * from schoolcalender where ac_year=? and week=?";
        Cursor cursor = db.rawQuery(querySQL, new String[]{semester, String.valueOf(week)});
        cursor.moveToLast();
        try {
            if (cursor.getCount() != 0 && cursor.getInt(cursor.getColumnIndex("pulled")) == 0) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                final AsyncHttpClient client = new AsyncHttpClient();
                client.setCookieStore(cookieHelper);
                client.get(String.format(UEMS.UEMS_SCHEDULE_URL, semester, String.valueOf(week)), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.e(LOG_TAG, new String(responseBody));
                        try {
                            JSONObject response = new JSONObject(new String(responseBody));
                            if (response.getInt("code") == 200) {

                                JSONArray schedule = response.getJSONArray("data");
                                for (int i = 0; i < schedule.length(); i++) {
                                    JSONObject item = schedule.getJSONObject(i);
                                    Iterator<String> keys = item.keys();
                                    int section = item.getInt("section");
                                    while (keys.hasNext()) {
                                        String key = keys.next();
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put("ac_year", semester);
                                        contentValues.put("week", week);
                                        contentValues.put("section", section);
                                        int time = -1;
                                        switch (key) {
                                            case "sunday":
                                                time = 0;
                                                break;
                                            case "monday":
                                                time = 1;
                                                break;
                                            case "tuesday":
                                                time = 2;
                                                break;
                                            case "wednesday":
                                                time = 3;
                                                break;
                                            case "thursday":
                                                time = 4;
                                                break;
                                            case "friday":
                                                time = 5;
                                                break;
                                            case "saturday":
                                                time = 6;
                                                break;
                                        }
                                        if (time != -1) {
                                            contentValues.put("time", time);
                                            String rawCourseInfo = item.getString(key);
                                            String[] courseInfo = rawCourseInfo.split(",");
                                            Log.e(LOG_TAG, rawCourseInfo);
                                            contentValues.put("course", courseInfo[0]);
                                            contentValues.put("teacher", courseInfo[1]);
                                            if (courseInfo.length == 3) {
                                                contentValues.put("classroom", courseInfo[2]);
                                            } else {
                                                contentValues.put("classroom", "");
                                            }

                                            SQLiteDatabase database = scheduleDataBase.getWritableDatabase();
                                            database.insert("schedule", null, contentValues);
                                            ContentValues flagValue = new ContentValues();
                                            flagValue.put("pulled", 1);
                                            database.update("schoolcalender", flagValue, "ac_year=? and week=?", new String[]{semester, String.valueOf(week)});
                                            database.close();
                                            Log.d(LOG_TAG, contentValues.toString());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            //CAS.LOGIN=false;
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }


    }

    public void manualSetCalender(final String semester, Calendar calendar) {
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        Log.e(LOG_TAG, calendar.toString());
        while (calendar.get(Calendar.DAY_OF_WEEK) != 1) {
            calendar.add(Calendar.DATE, -1);
        }
        int week = 1;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        do {
            ContentValues contentValues = new ContentValues();
            contentValues.put("bgn_time", sdf.format(calendar.getTime()));
            calendar.add(Calendar.DATE, 6);
            contentValues.put("end_time", sdf.format(calendar.getTime()));
            contentValues.put("ac_year", semester);
            contentValues.put("week", week);
            SQLiteDatabase db = scheduleDataBase.getWritableDatabase();
            db.insert("schoolcalender", null, contentValues);
            db.close();
            calendar.add(Calendar.DATE, 1);
            pullScheduleFromServer(semester, week);
            week++;
        } while (week < 25);

        Log.e(LOG_TAG, calendar.toString());
    }

    public void pullCalenderScheduleFromServer(final String semester, final int week) {
        SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
        String querySQL = "select * from schoolcalender where ac_year=? and week=?";
        Cursor cursor = db.rawQuery(querySQL, new String[]{semester, String.valueOf(week)});
        try {
            cursor.moveToLast();
            if (cursor.getCount() == 0) {
                if (!cursor.isClosed()) {
                    cursor.close();
                }
                AsyncHttpClient client = new AsyncHttpClient();
                client.setCookieStore(cookieHelper);
                client.get(String.format(UEMS.UEMS_SCHOOL_CALENDER, semester, String.valueOf(week)), new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        //Log.e(LOG_TAG,new String(responseBody));
                        try {
                            JSONObject response = new JSONObject(new String(responseBody));
                            if (response.getInt("code") == 200) {
                                JSONObject calender = response.getJSONObject("data");
                                String bgnDate = calender.getString("startTime");
                                String endDate = calender.getString("endTime");
                                ContentValues contentValues = new ContentValues();
                                contentValues.put("bgn_time", bgnDate);
                                contentValues.put("end_time", endDate);
                                contentValues.put("ac_year", semester);
                                contentValues.put("week", week);
                                SQLiteDatabase db = scheduleDataBase.getWritableDatabase();
                                db.insert("schoolcalender", null, contentValues);
                                db.close();
                                pullScheduleFromServer(semester, week);
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (!cursor.isClosed()) {
                cursor.close();
            }
        }
    }

    public void addCalendarManually() {

    }

    public void fetchSemasterSchedule(String semester) {
        int week = 1;
        for (week = 1; week < 24; week++) {
            //Log.e(LOG_TAG,String.valueOf(week));
            pullCalenderScheduleFromServer(semester, week);

        }
    }

    public boolean haveSemaster(Semaster semaster) {
        haveSemasterResult = false;
        SyncHttpClient client = new SyncHttpClient();
        client.setCookieStore(cookieHelper);
        client.get(String.format(UEMS.UEMS_SCHOOL_CALENDER, semaster.toString(), "1"), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("code") == 200 && response.has("data")) {
                        haveSemasterResult = true;
                    }
                } catch (Exception e) {
                    //
                }
            }
        });
        return haveSemasterResult;
    }

    public int getWeekByDate(Calendar calendar) {
        String sqlWeek = "select * from schoolcalender where bgn_time <= ? and end_time >=?;";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String datestr = sdf.format(calendar.getTime());
        //Log.e(LOG_TAG,datestr);
        SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
        Cursor cursor = db.rawQuery(sqlWeek, new String[]{datestr, datestr});
        cursor.moveToFirst();
        int week = cursor.getInt(cursor.getColumnIndex("week"));
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return week;
    }

    public ArrayList<Course> getCoursesByDate(Calendar date) {
        try {
            ArrayList<Course> courses = new ArrayList<>();
            String sqlWeek = "select * from schoolcalender where bgn_time <= ? and end_time >=?;";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //be aware that ths Calendar object counts months from zero!!
            String datestr = sdf.format(date.getTime());
            //Log.e(LOG_TAG,datestr);
            SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
            Cursor cursor = db.rawQuery(sqlWeek, new String[]{datestr, datestr});
            cursor.moveToFirst();
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            String semaster = cursor.getString(cursor.getColumnIndex("ac_year"));
            Calendar calendarOfWeek = Calendar.getInstance();
            calendarOfWeek.setTime(sdf.parse(cursor.getString(cursor.getColumnIndex("bgn_time"))));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            date.setFirstDayOfWeek(Calendar.SUNDAY);
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) - 1;
            String sqlCourse = "select * from schedule where ac_year=? and week=? and time=? order by `section`";
            cursor = db.rawQuery(sqlCourse, new String[]{semaster, String.valueOf(week), String.valueOf(dayOfWeek)});
            //Log.e(LOG_TAG,String.valueOf(week));
            //Log.e(LOG_TAG,String.valueOf(dayOfWeek));
            while (cursor.moveToNext()) {
                String courseName = cursor.getString(cursor.getColumnIndex("course"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                int section = cursor.getInt(cursor.getColumnIndex("section"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                //Log.e(LOG_TAG,"here"+courseName);
                //int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
                Course course = new Course(courseName, teacher, classroom, section, dayOfWeek, week);
                course.setId(id);
                courses.add(course);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (db.isOpen()) {
                db.close();
            }
            return courses;
        } catch (Exception e) {
            //e.printStackTrace();
            return new ArrayList<Course>();
        }

    }


    public ArrayList<Course> getWeekRemainingCourses(Calendar date, int weekOffset) {
        try {
            ArrayList<Course> courses = new ArrayList<>();
            String sqlWeek = "select * from schoolcalender where bgn_time <= ? and end_time >=?;";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //be aware that ths Calendar object counts months from zero!!
            String datestr = sdf.format(date.getTime());
            //Log.e(LOG_TAG,datestr);
            SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
            Cursor cursor = db.rawQuery(sqlWeek, new String[]{datestr, datestr});
            cursor.moveToFirst();
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            String semaster = cursor.getString(cursor.getColumnIndex("ac_year"));
            Calendar calendarOfWeek = Calendar.getInstance();
            calendarOfWeek.setTime(sdf.parse(cursor.getString(cursor.getColumnIndex("bgn_time"))));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            date.setFirstDayOfWeek(Calendar.SUNDAY);
            int dayOfWeek = date.get(Calendar.DAY_OF_WEEK) - 1;
            if (weekOffset == 0) {
                String sqlCourse = "select * from schedule where ac_year=? and week=? and time>=? order by `time`,`section`";
                cursor = db.rawQuery(sqlCourse, new String[]{semaster, String.valueOf(week), String.valueOf(dayOfWeek)});
            } else {
                String sqlCourse = "select * from schedule where ac_year=? and week=? order by `time`,`section`";
                cursor = db.rawQuery(sqlCourse, new String[]{semaster, String.valueOf(week + weekOffset)});
            }

            //Log.e(LOG_TAG,String.valueOf(week));
            //Log.e(LOG_TAG,String.valueOf(dayOfWeek));
            while (cursor.moveToNext()) {
                String courseName = cursor.getString(cursor.getColumnIndex("course"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                int section = cursor.getInt(cursor.getColumnIndex("section"));
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                //Log.e(LOG_TAG,"here"+courseName);
                //int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
                Course course = new Course(courseName, teacher, classroom, section, dayOfWeek, week);
                course.setId(id);
                courses.add(course);
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (db.isOpen()) {
                db.close();
            }
            return courses;
        } catch (Exception e) {
            //e.printStackTrace();
            return new ArrayList<Course>();
        }

    }

    public ArrayList<Course> getCoursesByCourseName(String courseName, int laterThanWeek) {
        ArrayList<Course> courses = new ArrayList<>();
        SQLiteDatabase database = scheduleDataBase.getReadableDatabase();
        String sql = "select * from schedule where course = ? and week >= ? order by week";
        Cursor cursor = database.rawQuery(sql, new String[]{courseName, String.valueOf(laterThanWeek)});
        while (cursor.moveToNext()) {
            String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
            int section = cursor.getInt(cursor.getColumnIndex("section"));
            int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
            //Log.e(LOG_TAG,"here"+courseName);
            //int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
            Course course = new Course(courseName, teacher, classroom, section, dayOfWeek, week);
            course.setId(id);
            courses.add(course);
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        if (database.isOpen()) {
            database.close();
        }
        return courses;
    }

    public Course getOngoingCourse() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int section = GeneralSchedule.getOngoingSection(calendar);
        if (section == 0) {
            return null;
        }
        try {
            ArrayList<Course> courses = new ArrayList<>();
            String sqlWeek = "select * from schoolcalender where bgn_time <= ? and end_time >=?;";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //be aware that ths Calendar object counts months from zero!!
            String datestr = sdf.format(calendar.getTime());
            //Log.e(LOG_TAG,datestr);
            SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
            Cursor cursor = db.rawQuery(sqlWeek, new String[]{datestr, datestr});
            cursor.moveToFirst();
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            String semaster = cursor.getString(cursor.getColumnIndex("ac_year"));
            Calendar calendarOfWeek = Calendar.getInstance();
            calendarOfWeek.setTime(sdf.parse(cursor.getString(cursor.getColumnIndex("bgn_time"))));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            String sqlCourse = "select * from schedule where ac_year=? and week=? and time=? order by `section`";
            cursor = db.rawQuery(sqlCourse, new String[]{semaster, String.valueOf(week), String.valueOf(dayOfWeek)});
            //Log.e(LOG_TAG,String.valueOf(week));
            //Log.e(LOG_TAG,String.valueOf(dayOfWeek));
            while (cursor.moveToNext()) {
                String courseName = cursor.getString(cursor.getColumnIndex("course"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                int curSection = cursor.getInt(cursor.getColumnIndex("section"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                //Log.e(LOG_TAG,"here"+courseName);
                //int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
                if (section == curSection) {
                    return new Course(courseName, teacher, classroom, curSection, dayOfWeek, week);
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();

        }

        return null;
    }

    public Course getUpComingCourse() {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);
        int section = GeneralSchedule.getUpcomingSection(calendar);
        try {
            ArrayList<Course> courses = new ArrayList<>();
            String sqlWeek = "select * from schoolcalender where bgn_time <= ? and end_time >=?;";
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            //be aware that ths Calendar object counts months from zero!!
            String datestr = sdf.format(calendar.getTime());
            //Log.e(LOG_TAG,datestr);
            SQLiteDatabase db = scheduleDataBase.getReadableDatabase();
            Cursor cursor = db.rawQuery(sqlWeek, new String[]{datestr, datestr});
            cursor.moveToFirst();
            int week = cursor.getInt(cursor.getColumnIndex("week"));
            String semaster = cursor.getString(cursor.getColumnIndex("ac_year"));
            Calendar calendarOfWeek = Calendar.getInstance();
            calendarOfWeek.setTime(sdf.parse(cursor.getString(cursor.getColumnIndex("bgn_time"))));
            if (!cursor.isClosed()) {
                cursor.close();
            }
            calendar.setFirstDayOfWeek(Calendar.SUNDAY);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            String sqlCourse = "select * from schedule where ac_year=? and week=? and time=? order by `section`";
            cursor = db.rawQuery(sqlCourse, new String[]{semaster, String.valueOf(week), String.valueOf(dayOfWeek)});
            //Log.e(LOG_TAG,String.valueOf(week));
            //Log.e(LOG_TAG,String.valueOf(dayOfWeek));
            while (cursor.moveToNext()) {
                String courseName = cursor.getString(cursor.getColumnIndex("course"));
                String teacher = cursor.getString(cursor.getColumnIndex("teacher"));
                int curSection = cursor.getInt(cursor.getColumnIndex("section"));
                String classroom = cursor.getString(cursor.getColumnIndex("classroom"));
                //Log.e(LOG_TAG,"here"+courseName);
                //int dayOfWeek = cursor.getInt(cursor.getColumnIndex("time"));
                if (section == curSection) {
                    return new Course(courseName, teacher, classroom, curSection, dayOfWeek, week);
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            if (db.isOpen()) {
                db.close();
            }
        } catch (Exception e) {
            //e.printStackTrace();

        }

        return null;
    }

}
