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
