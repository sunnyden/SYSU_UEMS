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
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.denghaoqing.sysu.Handlers.ElectListFetchHandler;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.UEMS.Elect;
import com.denghaoqing.sysu.UEMS.ElectType;

import java.util.ArrayList;

public class ElectMainFragment extends Fragment {

    private ArrayList<ElectType> electTypes;
    private Elect elect;
    private ElectListFetchHandler electListFetchHandler;

    public void setElect(Elect elect) {
        this.elect = elect;
    }

    public void setElectTypes(ArrayList<ElectType> electTypes) {
        this.electTypes = electTypes;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_elect_main, container, false);
        electListFetchHandler = new ElectListFetchHandler(elect, getActivity());
        ListView listView = view.findViewById(R.id.listElectable);
        listView.setAdapter(new ElectTypeListAdapter(getContext(), electTypes));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ElectType type = electTypes.get((int) l);
                electListFetchHandler.setType(type);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        elect.fetchSelectedCourseList(type, null);
                        elect.fetchAvailableCourseList(type, null);
                        electListFetchHandler.sendEmptyMessage(0);

                    }
                }).start();

            }
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    public class ElectTypeListAdapter extends BaseAdapter {
        private ArrayList<ElectType> electTypes;
        private Context context;

        public ElectTypeListAdapter(Context context, ArrayList<ElectType> electTypeArrayList) {
            electTypes = electTypeArrayList;
            this.context = context;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = null;
            itemView = View.inflate(context, R.layout.item_elect_type, null);
            TextView electType = itemView.findViewById(R.id.item_elect_type_name);
            electType.setText(electTypes.get(i).getTypeName());
            return itemView;
        }

        @Override
        public int getCount() {
            return electTypes.size();
        }

        @Nullable
        @Override
        public ElectType getItem(int position) {
            return electTypes.get(position);
        }
    }
}
