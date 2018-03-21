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
