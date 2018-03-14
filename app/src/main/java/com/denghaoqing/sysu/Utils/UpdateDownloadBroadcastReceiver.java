/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Utils;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UpdateDownloadBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        long localId = context.getSharedPreferences("updater", Context.MODE_PRIVATE).getLong("downloadId", -1);
        if (localId != -1 && localId == downId) {
            String serviceString = Context.DOWNLOAD_SERVICE;
            DownloadManager dm = (DownloadManager) context
                    .getSystemService(serviceString);
            Intent install = new Intent(Intent.ACTION_VIEW);
            if (dm != null) {
                Uri downloadFileUri = dm
                        .getUriForDownloadedFile(localId);
                install.setDataAndType(downloadFileUri,
                        "application/vnd.android.package-archive");
                install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(install);
            }

        }
    }
}
