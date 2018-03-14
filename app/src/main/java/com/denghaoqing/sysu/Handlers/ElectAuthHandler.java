/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Handlers;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.denghaoqing.sysu.Fragments.ElectMainFragment;
import com.denghaoqing.sysu.MainActivity;
import com.denghaoqing.sysu.R;


public class ElectAuthHandler extends Handler {
    public static final int MSG_AUTH_SUCCESS = 1;
    private Activity operateActivity;

    public ElectAuthHandler(Activity activity) {
        operateActivity = activity;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == MSG_AUTH_SUCCESS) {
            MainActivity mainActivity = (MainActivity) operateActivity;
            FragmentTransaction fragmentTransaction = operateActivity.getFragmentManager().beginTransaction();
            ElectMainFragment electMainFragment = new ElectMainFragment();
            electMainFragment.setElectTypes(((MainActivity) operateActivity).electTypes);
            electMainFragment.setElect(((MainActivity) operateActivity).elect);
            fragmentTransaction.replace(R.id.fragment_container, electMainFragment).addToBackStack("BS");
            fragmentTransaction.commit();
            mainActivity.swapProgressBarVisibility(View.GONE);

        }
    }
}
