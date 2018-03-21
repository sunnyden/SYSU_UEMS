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

package com.denghaoqing.sysu.UEMS;

import android.content.Context;
import android.util.Log;

import com.denghaoqing.sysu.CAS.CAS;
import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URLDecoder;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-3-5.
 */

public class Elect {
    private static final String LOG_TAG = "Elect";
    private static String sessionIdString;
    public ArrayList<ElectCourse> courseSelectedArrayList;
    public ArrayList<ElectCourse> courseAvailArrayList;
    private Context context;
    private boolean authSuccess;
    private ArrayList<ElectType> typeArrayList;

    public Elect(Context context) {
        this.context = context;
    }

    public ArrayList<ElectType> getTypeArrayList() {
        return typeArrayList;
    }

    public boolean auth() {
        typeArrayList = new ArrayList<>();
        authSuccess = false;
        SyncHttpClient client = new SyncHttpClient();
        client.setCookieStore(new CookieHelper(context));
        client.setEnableRedirects(true);
        client.addHeader("User-Agent", CAS.USER_AGENT);
        client.get(UEMS.ELECT_AUTH, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                //Log.e(LOG_TAG,responseString);
                //Wont execute here
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e(LOG_TAG, responseString);
                if (responseString.contains("courses")) {
                    authSuccess = true;
                    Document doc = Jsoup.parse(responseString);

                    for (Element element : doc.select("td.c").select("a")) {
                        //eg: <a href="courses?xkjdszid=2017231004001&amp;fromSearch=false&amp;sid=7457d27c-f231-4c47-b58d-93ce1f38d03b"> 专选（改补选） </a>
                        try {
                            String link = element.attr("href");
                            link = URLDecoder.decode(link, "UTF-8");
                            link = link.replace("courses?", "");
                            //Log.e(LOG_TAG,link);
                            String[] params = link.split("&");
                            String courseTypeId = null;
                            for (String param : params) {
                                String keyPairs[] = param.split("=");
                                if (keyPairs[0].equals("xkjdszid")) {
                                    courseTypeId = keyPairs[1];
                                } else if (keyPairs[0].equals("sid")) {
                                    sessionIdString = keyPairs[1];
                                }
                            }
                            if (courseTypeId != null) {
                                typeArrayList.add(new ElectType(element.html(), courseTypeId));
                            }
                        } catch (Exception e) {

                        }

                    }
                }
            }
        });
        return authSuccess;
    }

    public ArrayList<ElectCourse> fetchSelectedCourseList(final ElectType type, String campusId) {
        courseSelectedArrayList = new ArrayList<>();
        final JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                //Log.e(LOG_TAG,response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //Log.e(LOG_TAG,responseString);
                Document document = Jsoup.parse(responseString);
                Element tableElected = document.getElementById("elected");
                for (Element tableRows : tableElected.select("tr")) {
                    Elements htmlElements = tableRows.select("td");
                    if (!htmlElements.hasText()) {
                        Log.e(LOG_TAG, "Avoid table head");
                        continue;
                    }
                    boolean selected = true;
                    boolean selectable = false;
                    String courseId = htmlElements.get(0).child(0).attr("jxbh");
                    String courseName = htmlElements.get(2).child(0).html();
                    String spaceTime = htmlElements.get(3).html();
                    String teacherName = htmlElements.get(4).html();
                    String credits = htmlElements.get(5).html();
                    int capacity = Integer.parseInt(htmlElements.get(6).html());
                    int waitingSelection = Integer.parseInt(htmlElements.get(7).html());
                    int remainingSeat = Integer.parseInt(htmlElements.get(8).html());
                    ElectCourse course = new ElectCourse(courseId, type.getTypeId(), courseName, teacherName, spaceTime, credits, false, selectable, selected, capacity, remainingSeat, waitingSelection);
                    if (courseId == null || courseId.equals("")) {
                        course.setUncancellable();
                    }
                    courseSelectedArrayList.add(course);
                    Log.e(LOG_TAG, courseId);
                    Log.e(LOG_TAG, courseName);

                }

            }
        };
        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.setCookieStore(new CookieHelper(context));
        syncHttpClient.addHeader("User-Agent", CAS.USER_AGENT);
        if (campusId == null) {
            syncHttpClient.get(String.format(UEMS.STUDENT_COURSE_ELECT_LIST, type.getTypeId(), sessionIdString), handler);
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.put("xqm", campusId);
            syncHttpClient.get(String.format(UEMS.STUDENT_COURSE_ELECT_LIST, type.getTypeId(), sessionIdString), requestParams, handler);
        }


        return courseSelectedArrayList;
    }

    public ArrayList<ElectCourse> fetchAvailableCourseList(final ElectType type, String campusId) {
        courseAvailArrayList = new ArrayList<>();
        final JsonHttpResponseHandler handler = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
                //Log.e(LOG_TAG,response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                //Log.e(LOG_TAG,responseString);
                Document document = Jsoup.parse(responseString);
                ElectCourseViewHolder.sessionId = document.getElementById("sid").attr("value");
                Element tableElected = document.getElementById("courses");
                for (Element tableRows : tableElected.select("tr")) {
                    Elements htmlElements = tableRows.select("td");
                    if (!htmlElements.hasText()) {
                        Log.e(LOG_TAG, "Avoid table head");
                        continue;
                    }
                    String courseId;
                    boolean selected = false;
                    boolean selectable = true;
                    try {
                        courseId = htmlElements.get(0).child(0).child(0).attr("jxbh");
                    } catch (Exception e) {
                        courseId = "Unselectable";
                        selectable = false;
                    }
                    String courseName = htmlElements.get(1).child(0).html();

                    String spaceTime = htmlElements.get(3).html();
                    String teacherName = htmlElements.get(4).html();
                    String credits = htmlElements.get(5).html();
                    int capacity = Integer.parseInt(htmlElements.get(6).html());
                    int waitingSelection = Integer.parseInt(htmlElements.get(7).html());
                    int remainingSeat = Integer.parseInt(htmlElements.get(8).html());
                    ElectCourse course = new ElectCourse(courseId, type.getTypeId(), courseName, teacherName, spaceTime, credits, false, selectable, selected, capacity, remainingSeat, waitingSelection);

                    courseAvailArrayList.add(course);


                }

            }
        };
        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.setCookieStore(new CookieHelper(context));
        syncHttpClient.addHeader("User-Agent", CAS.USER_AGENT);
        if (campusId == null) {
            syncHttpClient.get(String.format(UEMS.STUDENT_COURSE_ELECT_LIST, type.getTypeId(), sessionIdString), handler);
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.put("xqm", campusId);
            syncHttpClient.get(String.format(UEMS.STUDENT_COURSE_ELECT_LIST, type.getTypeId(), sessionIdString), requestParams, handler);
        }


        return courseAvailArrayList;
    }
}
