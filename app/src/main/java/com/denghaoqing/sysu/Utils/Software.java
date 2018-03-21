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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.denghaoqing.sysu.R;
import com.denghaoqing.sysu.Updater;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-3-14.
 */

public class Software {
    private static final String VERSION_CHECK_URL = "https://www.denghaoqing.com/sysu/version_check.php";
    public static boolean FLAG_NEW_VERSION = false;
    public static String APK_URL = "";
    public static String RELEASE_NOTE = "";
    public static String VERSION_CODE = "";

    public static void versionCheck(final Context context) throws PackageManager.NameNotFoundException {
        final int versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(VERSION_CHECK_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("version_num") > versionCode) {
                        Log.e("versioncheck", "new version!");
                        FLAG_NEW_VERSION = true;
                        RELEASE_NOTE = response.getString("desc");
                        APK_URL = response.getString("link");
                        VERSION_CODE = response.getString("version_name");
                        NotificationManager mNotifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, Updater.class), 0);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "UPDATE").setWhen(System.currentTimeMillis())
                                .setSmallIcon(R.drawable.ic_system_update_alt_black_24dp)
                                .setContentTitle("Software Update")
                                .setContentText("New version available!")
                                .setStyle(new NotificationCompat.BigTextStyle().bigText(response.getString("desc")))
                                .setPriority(Notification.PRIORITY_MAX)
                                .setContentIntent(pendingIntent);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel channel = new NotificationChannel("UPDATE", "Software update", NotificationManager.IMPORTANCE_DEFAULT);
                            mNotifyManager.createNotificationChannel(channel);
                        }

                        mNotifyManager.notify(0, builder.build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("fail", throwable.toString());
            }
        });
    }
}
