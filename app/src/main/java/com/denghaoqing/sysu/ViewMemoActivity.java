/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
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
