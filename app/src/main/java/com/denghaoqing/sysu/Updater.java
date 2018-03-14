

/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu;

import android.app.DownloadManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.denghaoqing.sysu.Utils.Software;
import com.denghaoqing.sysu.Utils.UpdateDownloadBroadcastReceiver;

public class Updater extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updater);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(Software.VERSION_CODE);
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Software.APK_URL));
                request.setDestinationInExternalPublicDir("/download/", "sysu_1.0.apk");
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
                request.setVisibleInDownloadsUi(true);
                DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                if (downloadManager != null) {
                    SharedPreferences sp = getSharedPreferences("updater", Context.MODE_PRIVATE);
                    long downloadId = downloadManager.enqueue(request);
                    sp.edit().putLong("downloadId", downloadId).apply();
                }
                UpdateDownloadBroadcastReceiver receiver = new UpdateDownloadBroadcastReceiver();
                IntentFilter filter = new IntentFilter();
                filter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                registerReceiver(receiver, filter);

                Snackbar.make(view, R.string.download_started, Snackbar.LENGTH_LONG).show();
            }
        });
        TextView releaseNote = findViewById(R.id.release_note);
        releaseNote.setText(Software.RELEASE_NOTE);
    }
}
