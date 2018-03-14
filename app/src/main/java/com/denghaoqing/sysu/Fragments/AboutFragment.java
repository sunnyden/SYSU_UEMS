/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.denghaoqing.sysu.R;

/**
 * Created by sunny on 18-3-13.
 */

public class AboutFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        TextView textView = view.findViewById(R.id.about_version_code);
        try {
            textView.setText(getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (Exception e) {
            textView.setText("N/A");
        }

        return view;
    }
}
