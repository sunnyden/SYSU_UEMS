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

package com.denghaoqing.sysu;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.denghaoqing.sysu.Memo.Memo;
import com.squareup.picasso.Picasso;

import java.io.File;

import ru.noties.markwon.Markwon;

public class ViewMemoActivity extends AppCompatActivity {
    private TextView textContent;
    private ImageView bgImg;
    private Memo memo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_view_memo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(ViewMemoActivity.this, AddMemoActivity.class);
                mIntent.putExtra("memoId", memo.getId());
                startActivity(mIntent);
            }
        });
        textContent = findViewById(R.id.memo_detail_content);
        bgImg = findViewById(R.id.bg_img);
        try {
            memo = Memo.getMemo(this, (int) getIntent().getLongExtra("memoId", -1));
            Markwon.setMarkdown(textContent, memo.getContent());
            if (memo.hasImage) {
                Picasso.with(this).load(FileProvider.getUriForFile(this,
                        "com.denghaoqing.sysu.fileprovider", new File(memo.getImagePath())))
                        .into(bgImg);
            }
            this.setTitle(memo.getTitle());
        } catch (Exception e) {
            e.printStackTrace();
            this.finish();
        }

    }
}
