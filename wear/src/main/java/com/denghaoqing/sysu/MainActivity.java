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

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.wear.widget.WearableLinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.widget.TextView;

import com.denghaoqing.sysu.Schedule.Course;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends WearableActivity implements GoogleApiClient.ConnectionCallbacks, DataApi.DataListener {
    private GoogleApiClient googleApiClient;
    private TextView mTextView;
    private CourseViewListAdapter mAdapter;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            getContents();
        }
    };
    private Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WearableRecyclerView recyclerView = findViewById(R.id.recyc_view);
        recyclerView.setEdgeItemsCenteringEnabled(true);
        CustomScrollingLayoutCallback customScrollingLayoutCallback =
                new CustomScrollingLayoutCallback();
        recyclerView.setLayoutManager(
                new WearableLinearLayoutManager(this, customScrollingLayoutCallback));
        mAdapter = new CourseViewListAdapter();
        recyclerView.setAdapter(mAdapter);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .build();
        googleApiClient.connect();

        // Enables Always-on
        //setAmbientEnabled();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("connected", "connected");
        getContents();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                mHandler.obtainMessage(1).sendToTarget();
            }
        }, 0, 60000);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if ((googleApiClient != null) && (googleApiClient.isConnected())) {
            Wearable.DataApi.removeListener(googleApiClient, this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    private void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // broadcast the message to all connected devices
                final NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(googleApiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(googleApiClient, node.getId(), path, message.getBytes()).await();

                }
            }
        }).start();
    }

    // Get Conferences from the data items repository (cache).
    // If not available, we refresh the data from the Mobile device.
    //
    private void getContents() {
        final String pathToContent = "/today-schedule";
        Uri uri = new Uri.Builder()
                .scheme(PutDataRequest.WEAR_URI_SCHEME)
                .path(pathToContent)
                .build();
        Wearable.DataApi.getDataItems(googleApiClient, uri)
                .setResultCallback(
                        new ResultCallback<DataItemBuffer>() {
                            @Override
                            public void onResult(DataItemBuffer dataItems) {
                                if (dataItems.getCount() == 0) {
                                    // refresh the list of conferences from Mobile
                                    sendMessage(pathToContent, "get list of conferences");
                                    dataItems.release();
                                    return;
                                }

                                DataMap dataMap = DataMap.fromByteArray(dataItems.get(0).getData());
                                if (dataMap == null) {
                                    // refresh the list of conferences from Mobile
                                    sendMessage(pathToContent, "get list of conferences");
                                    dataItems.release();
                                    return;
                                }

                                // retrieve and display the conferences from the cache
                                //ConferencesListWrapper conferencesListWrapper = new ConferencesListWrapper();

                                //final List<Conference> conferencesList = conferencesListWrapper.getConferencesList(dataMap);
                                ArrayList<Course> courseArrayList = new ArrayList<>();
                                ArrayList<DataMap> dataMaps = dataMap.getDataMapArrayList("/schedule");
                                if (dataMaps == null) {
                                    return;
                                }
                                for (DataMap map : dataMaps) {
                                    Course course = new Course(map.getString("course"),
                                            map.getString("teacher"), map.getString("classroom"),
                                            map.getLong("start"), map.getLong("end"));
                                    courseArrayList.add(course);
                                }
                                mAdapter.setCourseArrayList(courseArrayList);
                                dataItems.release();

                            }
                        }
                );
    }

    @Override
    protected void onDestroy() {
        googleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        googleApiClient.disconnect();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("suspended", String.valueOf(i));
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        Log.e("hi", "change");
    }

}
