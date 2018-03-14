/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.UEMS;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.R;

/**
 * Created by sunny on 18-3-5.
 */

public class ElectCourseViewHolder extends RecyclerView.ViewHolder {
    public static String sessionId;
    private TextView courseName, courseTeacher, courseCredit, courseSpaceTime, courseAvailibility;
    private ProgressBar availProgressBar;
    private RelativeLayout select_layout, deselect_layout;
    private ElectCourse electCourse;
    private Handler handler;
    private Elect elect;
    private View linebreak;


    //TODO not a good implementation, change it tomorrow or later.


    public ElectCourseViewHolder(View view) {
        super(view);
        courseName = view.findViewById(R.id.elect_course_name);
        courseTeacher = view.findViewById(R.id.elect_teacher);
        courseAvailibility = view.findViewById(R.id.elect_text_availibility);
        courseSpaceTime = view.findViewById(R.id.elect_space_time);
        courseCredit = view.findViewById(R.id.elect_credits);
        availProgressBar = view.findViewById(R.id.elect_progress);
        select_layout = view.findViewById(R.id.elect_action);
        deselect_layout = view.findViewById(R.id.unelect_action);
        linebreak = view.findViewById(R.id.linebreak);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setInfo(final ElectCourse course, final Context context) {
        Log.e("test", sessionId);
        select_layout.setVisibility(View.GONE);
        deselect_layout.setVisibility(View.GONE);
        linebreak.setVisibility(View.GONE);
        electCourse = course;
        courseName.setText(course.getCourseName());
        courseTeacher.setText(course.getCourseTeacher());
        courseSpaceTime.setText(course.getCourseSpaceTime());
        courseCredit.setText(String.format(context.getString(R.string.credits), course.getCourseCredit()));
        courseAvailibility.setText(String.format(context.getString(R.string.availability), course.getRemainingSeat(), course.getCapacity()));
        availProgressBar.setProgress(course.getRemainingSeat() * 100 / course.getCapacity());
        if (course.isCancelable() && course.isSelected()) {
            linebreak.setVisibility(View.VISIBLE);
            deselect_layout.setVisibility(View.VISIBLE);
            deselect_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.confirmation))
                            .setMessage(context.getString(R.string.delete_hint))
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            course.unselect(context, sessionId);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }).start();
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton(context.getText(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            });
        } else if (course.isSelectable()) {
            linebreak.setVisibility(View.VISIBLE);
            select_layout.setVisibility(View.VISIBLE);

            select_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.confirmation))
                            .setMessage(context.getString(R.string.select_hint))
                            .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            course.select(context, sessionId);
                                            handler.sendEmptyMessage(0);
                                        }
                                    }).start();
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton(context.getText(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).show();
                }
            });
        }
    }
}
