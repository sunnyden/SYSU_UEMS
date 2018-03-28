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

import android.app.NotificationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.Timer;
import java.util.TimerTask;

public class DataSyncService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String PATH_TODAY_SCHEDULE = "/today-schedule";
    private static final String PATH_UPCOMING_COURSE = "/upcoming-course";
    private GoogleApiClient mGoogleApiClient;
    private Timer timer;

    public DataSyncService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
        Log.e("WearService", "engaging");
        timer = new Timer();
        Log.e("startservice", "servicestart");
    }

    private void disconnect() {
        if ((mGoogleApiClient != null) && (mGoogleApiClient.isConnected())) {
            mGoogleApiClient.disconnect();
        }
    }

    private void connectWearApi(final Runnable onConnectedAction) {

        if (mGoogleApiClient.isConnected()) {
            onConnectedAction.run();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                        @Override
                        public void onConnected(Bundle bundle) {
                            onConnectedAction.run();
                        }

                        @Override
                        public void onConnectionSuspended(int cause) {

                        }
                    }).build();
            mGoogleApiClient.connect();
        }
    }

    private void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // broadcast the message to all connected devices
                final NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), path, message.getBytes()).await();

                }
            }
        }).start();
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("WearService", messageEvent.getPath());
        String path = messageEvent.getPath();
        String data = new String(messageEvent.getData());
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        super.onDataChanged(dataEventBuffer);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("failed", connectionResult.getErrorMessage());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("conn", "success");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Uri uri = new Uri.Builder()
                        .scheme(PutDataRequest.WEAR_URI_SCHEME)
                        .path(PATH_UPCOMING_COURSE)
                        .build();
                Wearable.DataApi.getDataItems(mGoogleApiClient, uri)
                        .setResultCallback(
                                new ResultCallback<DataItemBuffer>() {
                                    @Override
                                    public void onResult(DataItemBuffer dataItems) {
                                        sendMessage(PATH_UPCOMING_COURSE, "get upcoming course");
                                        if (dataItems.getCount() == 0) {
                                            // refresh the list of conferences from Mobile
                                            sendMessage(PATH_UPCOMING_COURSE, "get upcoming course");
                                            dataItems.release();
                                            return;
                                        }

                                        DataMap dataMap = DataMap.fromByteArray(dataItems.get(0).getData());
                                        if (dataMap == null) {
                                            // refresh the list of conferences from Mobile
                                            sendMessage(PATH_UPCOMING_COURSE, "get upcoming course");
                                            dataItems.release();
                                            return;
                                        }

                                        DataMap courseDM = dataMap.getDataMap("upcoming");
                                        if (courseDM == null) {
                                            dataItems.release();
                                            return;
                                        }
                                        if (courseDM.getBoolean("hasCourse")) {
                                            NotificationCompat.Builder notificationBuilder = null;
                                            long timeInterval = courseDM.getLong("interval");
                                            int minutes = (int) (timeInterval / 60000);
                                            String subText = "";
                                            if (minutes < 1) {
                                                subText = getString(R.string.seconds_later);
                                            } else {
                                                subText = getString(R.string.minutes_later, minutes);
                                            }

                                            notificationBuilder = new NotificationCompat.Builder(DataSyncService.this)
                                                    .setContentTitle(courseDM.getString("course"))
                                                    .setSmallIcon(R.drawable.ic_notification)
                                                    .setOngoing(true)
                                                    .setOnlyAlertOnce(true);

                                            notificationBuilder.setContentText(courseDM.getString("classroom") + "\n" + subText);
                                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.notify(5, notificationBuilder.build());

                                        } else {
                                            try {
                                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                                notificationManager.cancel(5);
                                            } catch (Exception e) {
                                            }
                                        }
                                        dataItems.release();

                                    }
                                }
                        );
                Log.e("timetick", "tick");
            }
        }, 0, 60000);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

}
