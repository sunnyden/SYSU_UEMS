/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
