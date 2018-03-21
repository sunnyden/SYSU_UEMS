/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.denghaoqing.sysu.R;


public class CourseCardViewHolder extends RecyclerView.ViewHolder {
    private TextView cnCourseName, enCourseName, semasterName, descInfo, mCourseId, mPassed;
    private Context context;

    public CourseCardViewHolder(View view) {
        super(view);
        cnCourseName = view.findViewById(R.id.course_name_chinese);
        enCourseName = view.findViewById(R.id.course_name_english);
        semasterName = view.findViewById(R.id.course_semaster);
        descInfo = view.findViewById(R.id.plan_desc);
        mCourseId = view.findViewById(R.id.course_id);
        mPassed = view.findViewById(R.id.passed);
        context = view.getContext();
    }

    public void setCourseView(String cnName, String enName, String semaster, float credit, int schoolTime, String courseId) {
        cnCourseName.setText(cnName);
        enCourseName.setText(enName);
        semasterName.setText(semaster);
        descInfo.setText(String.format(context.getString(R.string.plan_desc), credit, schoolTime));
        mCourseId.setText(courseId);
        SQLiteDatabase database = new CourseDataBase(context).getReadableDatabase();
        String sql = "select * from score where courseId=?";
        Cursor cursor = database.rawQuery(sql, new String[]{courseId});
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            mPassed.setVisibility(View.VISIBLE);
        } else {
            mPassed.setVisibility(View.INVISIBLE);
        }
        if (!cursor.isClosed()) {
            cursor.close();
            database.close();
        }
    }

    public void setCourseView(CourseItem item) {
        String semasterYear = item.getSemaster().substring(0, 4);
        int semaster = Integer.parseInt(String.valueOf(item.getSemaster().charAt(4)));
        String readable;
        switch (semaster) {
            case 1:
                readable = context.getString(R.string.time_semester, semasterYear, context.getString(R.string.first_semester));
                break;
            case 2:
                readable = context.getString(R.string.time_semester, semasterYear, context.getString(R.string.second_semester));
                break;
            case 3:
                readable = context.getString(R.string.time_semester, semasterYear, context.getString(R.string.third_semester));
                break;
            default:
                readable = "";
                break;
        }

        cnCourseName.setText(item.getCnName());
        enCourseName.setText(item.getEnName());
        semasterName.setText(readable);
        descInfo.setText(String.format(context.getString(R.string.plan_desc), item.getCredit(), item.getSchoolTime()));
        mCourseId.setText(item.getCourseId());
        SQLiteDatabase database = new CourseDataBase(context).getReadableDatabase();
        String sql = "select * from score where courseId=?";
        Cursor cursor = database.rawQuery(sql, new String[]{item.getCourseId()});
        cursor.moveToFirst();
        if (cursor.getCount() != 0) {
            mPassed.setVisibility(View.VISIBLE);
        } else {
            mPassed.setVisibility(View.INVISIBLE);
        }
        if (!cursor.isClosed()) {
            cursor.close();
            database.close();
        }
    }
}
