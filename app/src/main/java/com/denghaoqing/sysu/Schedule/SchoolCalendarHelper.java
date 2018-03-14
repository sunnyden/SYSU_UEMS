/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Schedule;

import android.content.Context;

import com.denghaoqing.sysu.R;

import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by sunny on 18-3-1.
 */

public class SchoolCalendarHelper {
    private ArrayList<String> arrayStringList = new ArrayList<>();
    private ArrayList<Semaster> semasters = new ArrayList<>();
    private Semaster curSemaster;
    private Context context;

    public SchoolCalendarHelper(int semasterCountFromNow, Context context) {
        this.context = context;
        int i = 0;
        Calendar calendar = Calendar.getInstance();
        int term;
        if (calendar.get(Calendar.MONTH) <= 6) {
            term = 2;
        } else {
            term = 1;
        }
        curSemaster = new Semaster(calendar.get(Calendar.YEAR) - 1, term);
        arrayStringList.add(String.format(context.getString(R.string.item_semaster),
                String.valueOf(curSemaster.getYear()), curSemaster.getTerm() == 1 ? context.getString(R.string.fall_semaster) : context.getString(R.string.spring_semaster)));
        semasters.add(curSemaster);
        for (i = 0; i < semasterCountFromNow - 1; i++) {
            curSemaster = curSemaster.former();
            arrayStringList.add(String.format(context.getString(R.string.item_semaster),
                    String.valueOf(curSemaster.getYear()), curSemaster.getTerm() == 1 ? context.getString(R.string.fall_semaster) : context.getString(R.string.spring_semaster)));
            semasters.add(curSemaster);
        }
    }

    public ArrayList<Semaster> getSemasters() {
        return semasters;
    }

    public ArrayList<String> getArrayStringList() {
        return arrayStringList;
    }
}
