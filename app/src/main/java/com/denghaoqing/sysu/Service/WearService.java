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

package com.denghaoqing.sysu.Service;


import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.denghaoqing.sysu.Schedule.Course;
import com.denghaoqing.sysu.Schedule.Schedule;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;


public class WearService extends WearableListenerService {
    private static final String PATH_TODAY_SCHEDULE = "/today-schedule";
    private GoogleApiClient mGoogleApiClient;

    public WearService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        mGoogleApiClient.connect();
        Log.e("WearService", "engaging");
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

    public void sendMessage(final PutDataMapRequest putDataMapRequest) {

        connectWearApi(new Runnable() {
            @Override
            public void run() {
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest());
            }
        });
    }

    public void deleteItems(final String dataPath) {

        connectWearApi(new Runnable() {
            @Override
            public void run() {
                Uri uri = new Uri.Builder()
                        .scheme(PutDataRequest.WEAR_URI_SCHEME)
                        .path(dataPath)
                        .build();

                Wearable.DataApi.deleteDataItems(mGoogleApiClient, uri, DataApi.FILTER_LITERAL);
            }
        });
    }

    public void deleteAllItems(final String dataPath) {

        connectWearApi(new Runnable() {
            @Override
            public void run() {
                Uri uri = new Uri.Builder()
                        .scheme(PutDataRequest.WEAR_URI_SCHEME)
                        .path(dataPath)
                        .build();

                Wearable.DataApi.deleteDataItems(mGoogleApiClient, uri, DataApi.FILTER_PREFIX);
                Wearable.DataApi.deleteDataItems(mGoogleApiClient, uri);
            }
        });
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("WearService", messageEvent.getPath());
        String path = messageEvent.getPath();
        String data = new String(messageEvent.getData());
        if (path.equalsIgnoreCase(PATH_TODAY_SCHEDULE)) {
            retrieveTodaySchedule();
        }
    }

    private void retrieveTodaySchedule() {
        final PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(PATH_TODAY_SCHEDULE);
        ArrayList<DataMap> coursesDataMap = new ArrayList<>();
        Schedule schedule = new Schedule(this);
        Calendar calendar = Calendar.getInstance();
        Log.e("WearService", calendar.toString());
        List<Course> courses = schedule.getCoursesByDate(calendar);
        Course formerCourse = null;
        Course currentCourse = null;
        DataMap dataMap = null;
        Iterator<Course> iterator = courses.iterator();
        while (iterator.hasNext()) {
            if (formerCourse == null) {
                currentCourse = iterator.next();
                formerCourse = currentCourse;
                Calendar startTime = (Calendar) calendar.clone();
                Calendar endTime = (Calendar) calendar.clone();
                //Log.e(LOG_TAG, startTime.toString());
                //Log.e(LOG_TAG, endTime.toString());
                startTime.set(Calendar.HOUR_OF_DAY, currentCourse.StartHour);
                startTime.set(Calendar.MINUTE, currentCourse.StartMinute);
                startTime.set(Calendar.SECOND, 0);
                endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                endTime.set(Calendar.SECOND, 0);
                dataMap = new DataMap();
                dataMap.putString("course", currentCourse.courseName);
                dataMap.putString("teacher", currentCourse.teacher);
                dataMap.putString("classroom", currentCourse.classroom);
                dataMap.putLong("start", startTime.getTimeInMillis());
                dataMap.putLong("end", endTime.getTimeInMillis());
                continue;
            }
            formerCourse = currentCourse;
            currentCourse = iterator.next();
            if (Course.isContinuum(currentCourse, formerCourse)) {
                Calendar endTime = Calendar.getInstance();
                endTime.setTimeInMillis(dataMap.getLong("end"));
                endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                dataMap.putLong("end", endTime.getTimeInMillis());
            } else {
                coursesDataMap.add(dataMap);
                Calendar startTime = (Calendar) calendar.clone();
                Calendar endTime = (Calendar) calendar.clone();
                //Log.e(LOG_TAG, startTime.toString());
                //Log.e(LOG_TAG, endTime.toString());
                startTime.set(Calendar.HOUR_OF_DAY, currentCourse.StartHour);
                startTime.set(Calendar.MINUTE, currentCourse.StartMinute);
                startTime.set(Calendar.SECOND, 0);
                endTime.set(Calendar.HOUR_OF_DAY, currentCourse.EndHour);
                endTime.set(Calendar.MINUTE, currentCourse.EndMinute);
                endTime.set(Calendar.SECOND, 0);
                dataMap = new DataMap();
                dataMap.putString("course", currentCourse.courseName);
                dataMap.putString("teacher", currentCourse.teacher);
                dataMap.putString("classroom", currentCourse.classroom);
                dataMap.putLong("start", startTime.getTimeInMillis());
                dataMap.putLong("end", endTime.getTimeInMillis());
            }

        }
        if (dataMap != null) {
            coursesDataMap.add(dataMap);
        }
        putDataMapRequest.getDataMap().putDataMapArrayList("/schedule", coursesDataMap);
        sendMessage(putDataMapRequest);
    }
}
