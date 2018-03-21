/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Course;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denghaoqing.sysu.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunny on 18-3-21.
 */

public class CourseListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<CourseItem> courseItems;

    public CourseListAdapter(Context context, int typeId) {
        courseItems = new ArrayList<>();
        SQLiteDatabase database = new CourseDataBase(context).getReadableDatabase();
        String sql = "select * from course where course_type=?";
        Cursor cursor = database.rawQuery(sql, new String[]{String.valueOf(typeId)});
        while (cursor.moveToNext()) {
            String cnName, enName, semaster, courseId;
            float credit;
            int schoolTime;
            cnName = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseCnName));
            enName = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseEnName));
            semaster = cursor.getString(cursor.getColumnIndex(CourseDataBase.colSemester));
            courseId = cursor.getString(cursor.getColumnIndex(CourseDataBase.colCourseNumber));
            credit = cursor.getFloat(cursor.getColumnIndex(CourseDataBase.colPlanCredit));
            schoolTime = cursor.getInt(cursor.getColumnIndex(CourseDataBase.colPlanPeriod));
            courseItems.add(new CourseItem(cnName, enName, semaster, credit, schoolTime, courseId));
        }
        if (!cursor.isClosed()) {
            cursor.close();
            database.close();
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((CourseCardViewHolder) holder).setCourseView(courseItems.get(position));
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card_course_plan, parent, false);
        return new CourseCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }
}
