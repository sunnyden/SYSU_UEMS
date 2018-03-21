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
