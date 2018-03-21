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

package com.denghaoqing.sysu.Dashboard;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.denghaoqing.sysu.R;

/**
 * Created by sunny on 18-3-12.
 */

public class AbnormalViewHolder extends RecyclerView.ViewHolder {
    private TextView studentStatusCode, studentStatusMsg;

    public AbnormalViewHolder(View view) {
        super(view);
        studentStatusCode = view.findViewById(R.id.err_code);
        studentStatusMsg = view.findViewById(R.id.err_msg);
    }

    public void setErrMsg(int code, String message) {
        studentStatusCode.setText(String.valueOf(code));
        studentStatusMsg.setText(message);
    }

}
