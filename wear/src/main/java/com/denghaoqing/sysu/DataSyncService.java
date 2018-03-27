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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;

public class DataSyncService extends WearableListenerService
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static boolean requestFlag = false;
    private final String PATH_DATA = "courseList";
    private GoogleApiClient mGoogleApiClient;
    private boolean nodeConnected = false;
    private Thread syncThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                SharedPreferences sharedPreferences = getSharedPreferences("course", Context.MODE_PRIVATE);
                long lastSyncTimeMills = sharedPreferences.getLong("lastSyncTime", -1);
                if (!DateUtils.isToday(lastSyncTimeMills) && !requestFlag) {
                    if (mGoogleApiClient.isConnected()) {
                        requestFlag = true;
                        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_DATA);
                        putDataMapRequest.getDataMap().putString("data", "request");
                        PutDataRequest request = putDataMapRequest.asPutDataRequest();
                        PendingResult<DataApi.DataItemResult> pendingResult =
                                Wearable.DataApi.putDataItem(mGoogleApiClient, request);

                        pendingResult.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                            @Override
                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                Log.e("WEAR APP", "APPLICATION Result has come");
                            }
                        });
                    } else {
                        mGoogleApiClient.connect();
                    }
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    //
                }
            }
        }
    });

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
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        nodeConnected = false;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        nodeConnected = true;
    }

    @Override
    public void onConnectionSuspended(int i) {
        nodeConnected = false;
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        final ArrayList<DataEvent> events = FreezableUtils.freezeIterable(dataEventBuffer);
        for (DataEvent event : events) {
            PutDataMapRequest putDataMapRequest =
                    PutDataMapRequest.createFromDataMapItem(DataMapItem.fromDataItem(event.getDataItem()));

            String path = event.getDataItem().getUri().getPath();
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                if (PATH_DATA.equals(path)) {
                    DataMap data = putDataMapRequest.getDataMap();
                    String info = data.getString("data");
                } else if (event.getType() == DataEvent.TYPE_DELETED) {

                }
            }
        }
        requestFlag = false;
    }
}
