/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.UEMS.Elect;
import com.denghaoqing.sysu.UEMS.ElectListAdapter;
import com.denghaoqing.sysu.UEMS.ElectType;

/**
 * A simple {@link Fragment} subclass.
 */
public class ElectListFragment extends Fragment {
    private final String FRAGMENT_TAG = this.getClass().getName();
    public Handler actionHandler, updateHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                selAdapter.notifyDataSetChanged();
                availAdapter.notifyDataSetChanged();
            }
        }
    };
    private Elect elect;
    private ElectType type;
    private ElectListAdapter selAdapter, availAdapter;

    public ElectListFragment() {
        // Required empty public constructor
    }

    public void setType(ElectType type) {
        this.type = type;
    }

    public void setElect(Elect elect) {
        this.elect = elect;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_elect_list, container, false);
        RecyclerView selCourseList = view.findViewById(R.id.selCourseList);
        RecyclerView availCourseList = view.findViewById(R.id.availCourseList);

        actionHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            selAdapter.electCoursesList = elect.fetchSelectedCourseList(type, null);
                            availAdapter.electCoursesList = elect.fetchAvailableCourseList(type, null);
                            updateHandler.sendEmptyMessage(0);
                            Log.e("refresh", "handling");
                        }
                    }).start();


                }
            }
        };

        //Log.e("fragment",String.valueOf(elect.courseAvailArrayList.size()));
        selAdapter = new ElectListAdapter(elect.courseSelectedArrayList, getContext());
        availAdapter = new ElectListAdapter(elect.courseAvailArrayList, getContext());
        selAdapter.setHandler(actionHandler);
        availAdapter.setHandler(actionHandler);

        RecyclerView.LayoutManager selManager = new LinearLayoutManager(getContext());
        RecyclerView.LayoutManager availManager = new LinearLayoutManager(getContext());
        selCourseList.setLayoutManager(selManager);
        selCourseList.setAdapter(selAdapter);
        availCourseList.setLayoutManager(availManager);
        availCourseList.setAdapter(availAdapter);

        availCourseList.setNestedScrollingEnabled(false);
        selCourseList.setNestedScrollingEnabled(false);
        return view;
    }

}
