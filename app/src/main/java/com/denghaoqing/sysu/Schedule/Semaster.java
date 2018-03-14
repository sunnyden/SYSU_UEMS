/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Schedule;

import java.util.Locale;

/**
 * Created by sunny on 18-3-1.
 */

public class Semaster {
    private int year, term;

    public Semaster(int year, int term) {
        this.year = year;
        this.term = term;
    }

    public int getTerm() {
        return term;
    }

    public int getYear() {
        return year;
    }


    @Override
    public String toString() {
        return String.format(Locale.US, "%d-%d", year, term);
    }

    public Semaster former() {
        switch (term) {
            case 1:
                return new Semaster(year - 1, 2);
            case 2:
                return new Semaster(year, 1);
            default:
                return null;
        }
    }

    public Semaster next() {
        switch (term) {
            case 1:
                return new Semaster(year, 2);
            case 2:
                return new Semaster(year + 1, 1);
            default:
                return null;
        }
    }
}
