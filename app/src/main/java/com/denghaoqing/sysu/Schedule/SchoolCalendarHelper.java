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
