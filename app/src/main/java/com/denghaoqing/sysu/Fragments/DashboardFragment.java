/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.denghaoqing.sysu.Dashboard.DashboardAdapter;
import com.denghaoqing.sysu.R;

import java.util.Timer;
import java.util.TimerTask;


public class DashboardFragment extends Fragment {
    public static DashboardFragment currentInstance;
    private TextView studentStatusCode, studentStatusMsg;
    private TextView upcomingCourse, courseTeacher, courseTime, courseClassroom;
    private CardView msgCard, courseCard;
    private RecyclerView feeds;
    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            updateFeeds();
        }
    };
    private Timer timer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        feeds = view.findViewById(R.id.feeds_view);

        updateFeeds();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 10000);


        return view;
    }

    @Override
    public void onDestroyView() {
        timer.cancel();
        super.onDestroyView();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DashboardFragment.currentInstance = this;
    }

    private void updateFeeds() {
        DashboardAdapter adapter = new DashboardAdapter(getContext());
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getContext());
        feeds.setLayoutManager(manager);
        feeds.setAdapter(adapter);
    }
}
