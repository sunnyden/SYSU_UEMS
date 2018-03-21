/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class FeedbackActivity extends AppCompatActivity {
    private EditText fbName, fbContact, fbContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fbName = findViewById(R.id.editFbName);
        fbContact = findViewById(R.id.editFbContact);
        fbContent = findViewById(R.id.editFbContent);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!fbContent.getText().toString().equals("")) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    RequestParams values = new RequestParams();
                    client.post("https://www.denghaoqing.com/sysu/fb.php", values, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(FeedbackActivity.this, R.string.fb_success, Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(FeedbackActivity.this, R.string.fb_err, Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(FeedbackActivity.this, R.string.fb_empt, Toast.LENGTH_LONG).show();
                }

            }
        });
    }

}
