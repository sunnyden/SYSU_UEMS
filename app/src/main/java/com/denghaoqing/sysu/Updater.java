

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
