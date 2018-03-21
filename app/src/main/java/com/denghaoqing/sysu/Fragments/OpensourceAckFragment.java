/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.denghaoqing.sysu.R;

import ru.noties.markwon.Markwon;

/**
 * A simple {@link Fragment} subclass.
 */
public class OpensourceAckFragment extends Fragment {

    private static String opensourceMd = "**Jsoup**  \n" +
            "Copyright 2017 Jonathan Hedley  \n" +
            "License: MIT License  \n" +
            "Website: [github.com/jhy/jsoup/](http://github.com/jhy/jsoup/)\n" +
            "\n" +
            "**Picasso**  \n" +
            "Copyright 2013 Square, Inc.  \n" +
            "License: Apache 2.0  \n" +
            "Website: [github.com/square/picasso](http://github.com/square/picasso/)\n" +
            "\n" +
            "**Android Asynchronous Http Client**  \n" +
            "Copyright 2017 Jame Smith.  \n" +
            "License: Apache 2.0  \n" +
            "Website: [github.com/loopj/android-async-http](https://github.com/loopj/android-async-http)  \n" +
            "\n" +
            "**CircleImageView**  \n" +
            "Copyright 2014 Raquib-ul-Alam  \n" +
            "License: Apache 2.0  \n" +
            "Website: [github.com/alamkanak/Android-Week-View](https://github.com/alamkanak/Android-Week-View)  \n" +
            "\n" +
            "**Markwon**  \n" +
            "Copyright 2017 Dimitry Ivanov  \n" +
            "License: Apache 2.0  \n" +
            "Website: [github.com/noties/Markwon/](https://github.com/noties/Markwon/)  \n" +
            "**MPAndroidChart**  \n" +
            "\n" +
            "Copyright 2018 Philipp Jahoda  \n" +
            "License: Apache 2.0  \n" +
            "Website: [github.com/PhilJay/MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)";

    public OpensourceAckFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_opensource_ack, container, false);
        TextView text = view.findViewById(R.id.opensource);
        Markwon.setMarkdown(text, opensourceMd);
        return view;
    }

}
