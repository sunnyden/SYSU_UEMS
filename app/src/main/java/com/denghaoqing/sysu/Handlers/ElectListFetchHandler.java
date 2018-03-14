/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
