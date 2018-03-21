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
