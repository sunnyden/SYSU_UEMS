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
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.denghaoqing.sysu.CoursePlanListActivity;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class TeachPlanFragment extends Fragment implements OnChartValueSelectedListener {

    private PieChart mChart;

    private TextView generalView, requirement, coreCourse, featureCourse;

    public TeachPlanFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teach_plan, container, false);
        generalView = view.findViewById(R.id.curriculum_brief);
        requirement = view.findViewById(R.id.curriculum_require);
        coreCourse = view.findViewById(R.id.core_lesson);
        featureCourse = view.findViewById(R.id.character_lesson);
        mChart = view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setCenterText(getContext().getString(R.string.courseType));
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setOnChartValueSelectedListener(this);
        setData();
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);
        mChart.setEntryLabelColor(Color.BLACK);
        mChart.setEntryLabelTextSize(12f);
        return view;
    }

    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        PieEntry entry = (PieEntry) e;
        Toast.makeText(getContext(), String.format(getString(R.string.toast_credit), entry.getLabel(), entry.getValue()), Toast.LENGTH_LONG).show();
        Intent mIntent = new Intent(getActivity(), CoursePlanListActivity.class);
        mIntent.putExtra("typeName", ((PieEntry) e).getLabel());
        getContext().startActivity(mIntent);
    }

    private void setData() {
        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();
        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        SharedPreferences sp = getContext().getSharedPreferences(UEMS.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        generalView.setText(sp.getString("trainingPrograme", "N/A"));
        requirement.setText(sp.getString("ruleRequire", "N/A"));
        coreCourse.setText(sp.getString("majorCore", "N/A"));
        featureCourse.setText(sp.getString("majorCharacte", "N/A"));
        getActivity().setTitle(sp.getString("gradeMajorName", "SYSU"));
        JSONArray courseTypes;
        try {
            courseTypes = new JSONArray(sp.getString("courseInfo", "[]"));
            for (int i = 0; i < courseTypes.length(); i++) {
                JSONObject jsonObject = courseTypes.getJSONObject(i);
                entries.add(new PieEntry((float) jsonObject.getDouble("courseCredit"), jsonObject.getString("courseTypeName")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        PieDataSet dataSet = new PieDataSet(entries, getContext().getString(R.string.course_label));
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        mChart.setData(data);

        mChart.highlightValues(null);

        mChart.invalidate();
    }

    @Override
    public void onDestroyView() {
        getActivity().setTitle(R.string.app_name);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        getActivity().setTitle(R.string.app_name);
        super.onDestroy();
    }
}
