/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Achievement;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.R;

import java.util.ArrayList;

/**
 * Created by sunny on 18-3-2.
 */

public class AchievementAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static int count = 0;
    private ArrayList<Achievement> achievements;
    private Context context;


    //private ArrayList<RelativeLayout[]> relativeLayouts=new ArrayList<>();


    public AchievementAdapter(ArrayList<Achievement> achievements, Context context) {
        this.achievements = achievements;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_score, parent, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_score, parent, false);
            return new HeaderViewHolder(view, achievements, context);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position != 0) {
            final int pos = position - 1;
            Log.e("Onbind", "onbind");
            ViewHolder viewHolder = (ViewHolder) holder;
            viewHolder.courseNameDetail.setText(achievements.get(pos).strCourseName);
            viewHolder.courseName.setText(achievements.get(pos).strCourseName);
            viewHolder.courseType.setText(achievements.get(pos).strCourseType);
            viewHolder.courseTeacher.setText(achievements.get(pos).strTeacher);
            viewHolder.courseScore.setText(achievements.get(pos).strScore);
            viewHolder.courseScoreBrief.setText(achievements.get(pos).strScore);
            viewHolder.coursePoint.setText(achievements.get(pos).strPoint);
            viewHolder.courseRank.setText(String.format("%s/%s", String.valueOf(achievements.get(pos).rank), String.valueOf(achievements.get(pos).total)));
            viewHolder.courseTime.setText(String.format(context.getString(R.string.time_semester), achievements.get(pos).strYear,
                    getSemasterString(context, achievements.get(pos).semester)));
            viewHolder.credit.setText(String.format(context.getString(R.string.credits), achievements.get(pos).strCredit));
            RelativeLayout scoreBrief = viewHolder.itemView.findViewById(R.id.score_brief);
            RelativeLayout scoreDetail = viewHolder.itemView.findViewById(R.id.score_detail);
            if (achievements.get(pos).expanded) {
                scoreBrief.setVisibility(View.GONE);
                scoreDetail.setVisibility(View.VISIBLE);
            } else {
                scoreBrief.setVisibility(View.VISIBLE);
                scoreDetail.setVisibility(View.GONE);
            }
            //holder.itemView.setTag(position);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RelativeLayout scoreBrief = view.findViewById(R.id.score_brief);
                    RelativeLayout scoreDetail = view.findViewById(R.id.score_detail);
                    if (scoreBrief.getVisibility() == View.VISIBLE) {
                        achievements.get(pos).expanded = true;
                        scoreBrief.setVisibility(View.GONE);
                        scoreDetail.setVisibility(View.VISIBLE);
                    } else {
                        achievements.get(pos).expanded = false;
                        scoreBrief.setVisibility(View.VISIBLE);
                        scoreDetail.setVisibility(View.GONE);
                    }
                }
            });
            //relativeLayouts.add(position,new RelativeLayout[]{holder.score_brief,holder.score_detail});
        /*holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/
        } else {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.generateOverall();
        }

    }

    public int getContentItemCount() {
        return achievements.size();
    }

    @Override
    public int getItemCount() {
        return achievements.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return 0;
        } else {
            return 1;
        }
    }

    private String getSemasterString(Context context, int i) {
        switch (i) {
            case 1:
                return context.getString(R.string.first_semester);
            case 2:
                return context.getString(R.string.second_semester);
            case 3:
                return context.getString(R.string.third_semester);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView courseName, courseNameDetail, courseType, courseTeacher, courseScore;
        public TextView courseScoreBrief, coursePoint, courseRank, courseTime, credit;
        public CardView cardView;
        public RelativeLayout score_brief, score_detail;

        public ViewHolder(View view) {
            super(view);
            courseName = view.findViewById(R.id.corsename_brief);
            courseNameDetail = view.findViewById(R.id.courseNameBig);
            courseType = view.findViewById(R.id.course_type);
            courseTeacher = view.findViewById(R.id.total_credit);
            courseScore = view.findViewById(R.id.textViewScore);
            courseScoreBrief = view.findViewById(R.id.score);
            coursePoint = view.findViewById(R.id.lesson_passed);
            courseRank = view.findViewById(R.id.rank);
            courseTime = view.findViewById(R.id.test_time);
            cardView = view.findViewById(R.id.cardItem);
            credit = view.findViewById(R.id.credits);
            score_brief = view.findViewById(R.id.score_brief);
            score_detail = view.findViewById(R.id.score_detail);

        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout btnSwitch;
        public ArrayList<Achievement> achievements;
        public Context context;
        private TextView gpaText, totalCredit, coursePassed;
        private boolean fivePointFormat = true;

        public HeaderViewHolder(View view, ArrayList<Achievement> achievements, Context context) {
            super(view);
            gpaText = view.findViewById(R.id.textGPA);
            totalCredit = view.findViewById(R.id.total_credit);
            coursePassed = view.findViewById(R.id.lesson_passed);
            btnSwitch = view.findViewById(R.id.btn_layout_switch);
            this.achievements = achievements;
            this.context = context;
            btnSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switchSystem();
                }
            });
        }

        public void switchSystem() {
            fivePointFormat = !fivePointFormat;
            if (!fivePointFormat) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.warning);
                builder.setMessage(R.string.algrithm_not_accurete);
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
            generateOverall();
        }

        public void generateOverall() {
            if (fivePointFormat) {
                coursePassed.setText(String.valueOf(achievements.size()));
                float credits = 0;
                float pointWeighted = 0;
                for (Achievement achievement : achievements) {
                    credits += achievement.credit;
                    pointWeighted += (achievement.point * achievement.credit);
                }
                float pointOverall = pointWeighted / credits;

                gpaText.setText(String.format("%.1f", pointOverall));
            } else {
                coursePassed.setText(String.valueOf(achievements.size()));
                float credits = 0;
                float pointWeighted = 0;
                for (Achievement achievement : achievements) {
                    credits += achievement.credit;
                    pointWeighted += (4 - 3 * Math.pow(100 - achievement.score, 2) / 1600) * achievement.credit;
                }
                float pointOverall = pointWeighted / credits;
                gpaText.setText(String.format("%.1f", pointOverall));
            }

        }
    }

}
