/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.UEMS;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denghaoqing.sysu.R;

import java.util.ArrayList;

/**
 * Created by sunny on 18-3-5.
 */

public class ElectListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public ArrayList<ElectCourse> electCoursesList;
    private Context context;
    private Handler handler;
    private ElectType type;


    public ElectListAdapter(ArrayList<ElectCourse> arrayList, Context context) {
        Log.e("hi", "creating electlistadapter");
        electCoursesList = arrayList;
        this.context = context;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public ElectType getType() {
        return type;
    }

    public void setType(ElectType type) {
        this.type = type;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ElectCourseViewHolder viewHolder = (ElectCourseViewHolder) holder;
        ((ElectCourseViewHolder) holder).setHandler(handler);
        ElectCourse course = electCoursesList.get(position);
        Log.e("hi", "hi");
        viewHolder.setInfo(course, context);
    }

    @Override
    public int getItemCount() {
        Log.e("size", "hihi");
        return electCoursesList.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_elect_course, parent, false);
        return new ElectCourseViewHolder(view);
    }
}
