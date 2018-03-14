/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Dashboard;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denghaoqing.sysu.Memo.Memo;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.Schedule.Course;
import com.denghaoqing.sysu.Schedule.Schedule;
import com.denghaoqing.sysu.UEMS.UEMS;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by sunny on 18-3-12.
 */

public class DashboardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int TYPE_ABNORMAL = 1;
    private final int TYPE_UPCOMING_COURSE = 2;
    private final int TYPE_ONGOING_COURSE = 3;
    private final int TYPE_UPGOING_MEMO = 4;
    private Context context;
    private int errCode;
    private String message;

    private Course upcomingCourse, ongoingCourse;

    private ArrayList<Integer> eventList;
    private ArrayList<Memo> memoArrayList;

    public DashboardAdapter(Context context) {
        this.context = context;
        eventList = new ArrayList<>();
        memoArrayList = new ArrayList<>();
        int code;
        String message;
        code = context.getSharedPreferences(UEMS.SHARE_PREF_NAME, Context.MODE_PRIVATE).getInt("stdstatcode", 200);
        message = context.getSharedPreferences(UEMS.SHARE_PREF_NAME, Context.MODE_PRIVATE).getString("statmsg", "Nothing");
        if (code != 200) {
            errCode = code;
            this.message = message;
            eventList.add(TYPE_ABNORMAL);
        }

        Schedule schedule = new Schedule(context);
        Course ongoingCourse = schedule.getOngoingCourse();
        if (ongoingCourse != null) {
            this.ongoingCourse = ongoingCourse;
            eventList.add(TYPE_ONGOING_COURSE);
        }


        Course course = schedule.getUpComingCourse();
        if (course != null) {
            upcomingCourse = course;
            eventList.add(TYPE_UPCOMING_COURSE);
        }

        /*
        *
        * TODO implement ongoing course later on at EXACTLY this place(for the sequence of feeds)
        *
        * */


        ArrayList<Course> coursesThisWeek = schedule.getWeekRemainingCourses(Calendar.getInstance(), 0); //Week's course
        ArrayList<Course> coursesNextWeek = schedule.getWeekRemainingCourses(Calendar.getInstance(), 1);
        for (Course weekCourse : coursesThisWeek) {
            ArrayList<Memo> memos = Memo.getMemosByRefs(context, weekCourse.getId());
            memoArrayList.addAll(memos);
        }
        for (Course weekCourse : coursesNextWeek) {
            ArrayList<Memo> memos = Memo.getMemosByRefs(context, weekCourse.getId());
            memoArrayList.addAll(memos);
        }
        if (memoArrayList.size() > 0) {
            eventList.add(TYPE_UPGOING_MEMO);
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_ABNORMAL:
                ((AbnormalViewHolder) holder).setErrMsg(errCode, message);
                break;
            case TYPE_UPCOMING_COURSE:
                ((UpcomingCourseViewHolder) holder).setUpcomingCourse(upcomingCourse);
                break;
            case TYPE_ONGOING_COURSE:
                ((OngoingCourseViewHolder) holder).setOngoingCourse(ongoingCourse);
                break;
            case TYPE_UPGOING_MEMO:
                ((MemoViewHolder) holder).setMemo(memoArrayList.get(position - eventList.size() + 1));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size() + memoArrayList.size() - 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < eventList.size()) {
            return eventList.get(position);
        } else {
            return TYPE_UPGOING_MEMO;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ABNORMAL:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_abnormal, parent, false);
                return new AbnormalViewHolder(view);
            case TYPE_UPCOMING_COURSE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_upcoming_course, parent, false);
                return new UpcomingCourseViewHolder(view, parent.getContext());
            case TYPE_ONGOING_COURSE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ongoing_course, parent, false);
                return new OngoingCourseViewHolder(view, parent.getContext());
            case TYPE_UPGOING_MEMO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_memo_card, parent, false);
                return new MemoViewHolder(view, context);
        }

        return null;
    }
}
