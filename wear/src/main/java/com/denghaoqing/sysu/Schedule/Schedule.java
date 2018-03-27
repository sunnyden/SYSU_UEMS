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

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by sunny on 18-2-27.
 */

public class Schedule {
    private final static String LOG_TAG = "Schedule";
    private Context context;
    private ScheduleDataBase scheduleDataBase;
    private boolean haveSemasterResult = false;

    public Schedule(Context context) {
        this.context = context;
        this.scheduleDataBase = new ScheduleDataBase(context);
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
