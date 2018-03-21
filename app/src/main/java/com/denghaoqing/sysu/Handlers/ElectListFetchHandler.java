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

package com.denghaoqing.sysu.Handlers;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.denghaoqing.sysu.Fragments.ElectListFragment;
import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.UEMS.Elect;
import com.denghaoqing.sysu.UEMS.ElectType;

/**
 * Created by sunny on 18-3-5.
 */

public class ElectListFetchHandler extends Handler {
    private Elect elect;
    private Activity activity;
    private ElectType type;

    public ElectListFetchHandler(Elect elect, Activity activity) {
        this.activity = activity;
        this.elect = elect;
    }

    public ElectType getType() {
        return type;
    }

    public void setType(ElectType type) {
        this.type = type;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == 0) {
            ElectListFragment fragment = new ElectListFragment();
            fragment.setElect(elect);
            fragment.setType(type);
            activity.getFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).addToBackStack("BS").commit();
        }

    }
}
