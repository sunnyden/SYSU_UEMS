/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.UEMS;

import android.content.Context;
import android.util.Log;

import com.denghaoqing.sysu.CAS.CAS;
import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import cz.msebera.android.httpclient.Header;

/**
 * Created by sunny on 18-3-5.
 */

public class ElectCourse {
    private String courseId;
    private String courseCredit;
    private String courseTeacher;
    private String courseName;
    private String courseSpaceTime;
    private String courseType;
    private boolean isConflict;
    private boolean selectable;
    private boolean selected;
    private int capacity;
    private int remainingSeat;
    private int filterCount;
    private boolean cancelable = true;

    public ElectCourse(String courseId, String courseType, String courseName, String courseTeacher,
                       String courseSpaceTime, String courseCredit, boolean isConflict, boolean selectable, boolean selected, int capacity, int remainSeat, int filterCount) {
        this.courseId = courseId;
        this.courseType = courseType;
        this.courseCredit = courseCredit;
        this.courseTeacher = courseTeacher;
        this.courseName = courseName;
        this.courseSpaceTime = courseSpaceTime;
        this.isConflict = isConflict;
        this.selectable = selectable;
        this.remainingSeat = remainSeat;
        this.filterCount = filterCount;
        this.capacity = capacity;
        this.selected = selected;
    }

    public void setUncancellable() {
        cancelable = false;
    }

    public boolean select(Context context, final String sessionId) {
        if (!selected && selectable) {
            CookieHelper cookieHelper = new CookieHelper(context);
            SyncHttpClient client = new SyncHttpClient();
            client.setCookieStore(cookieHelper);
            client.addHeader("User-Agent", CAS.USER_AGENT);
            RequestParams requestParams = new RequestParams();
            requestParams.put("jxbh", courseId);
            requestParams.put("xkjdszid", courseType);
            requestParams.put("sid", sessionId);

            client.post(UEMS.STUDENT_COURSE_ELECT, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    if (statusCode == 200 && (responseString == null || responseString.equals(""))) {
                        selected = true;
                        selectable = false;
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (statusCode == 200 && (responseString == null || responseString.equals(""))) {
                        selected = true;
                        selectable = false;
                    }
                }
            });
        }
        return selected;
    }

    public boolean unselect(Context context, final String sessionId) {
        if (selected && cancelable) {
            CookieHelper cookieHelper = new CookieHelper(context);
            SyncHttpClient client = new SyncHttpClient();
            client.setCookieStore(cookieHelper);
            client.addHeader("User-Agent", CAS.USER_AGENT);
            RequestParams requestParams = new RequestParams();
            requestParams.put("jxbh", courseId);
            requestParams.put("xkjdszid", courseType);
            requestParams.put("sid", sessionId);

            client.post(UEMS.STUDENT_COURSE_UNELECT, requestParams, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.e("err", responseString);
                    if (statusCode == 200 && (responseString == null || responseString.equals(""))) {
                        Log.e("sccedd", "succedd");
                        selected = false;
                        selectable = true;
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (statusCode == 200 && (responseString == null || responseString.equals(""))) {
                        Log.e("sccedd", "succedd");
                        selected = false;
                        selectable = true;
                    }
                }
            });
        }
        return !selected;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getCourseCredit() {
        return courseCredit;
    }

    public void setCourseCredit(String courseCredit) {
        this.courseCredit = courseCredit;
    }

    public String getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(String courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseSpaceTime() {
        return courseSpaceTime;
    }

    public void setCourseSpaceTime(String courseSpaceTime) {
        this.courseSpaceTime = courseSpaceTime;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

    public boolean isConflict() {
        return isConflict;
    }

    public void setConflict(boolean conflict) {
        isConflict = conflict;
    }

    public boolean isSelectable() {
        return selectable;
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRemainingSeat() {
        return remainingSeat;
    }

    public void setRemainingSeat(int remainingSeat) {
        this.remainingSeat = remainingSeat;
    }

    public int getFilterCount() {
        return filterCount;
    }

    public void setFilterCount(int filterCount) {
        this.filterCount = filterCount;
    }

    public boolean isCancelable() {
        return cancelable;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
    }
}
