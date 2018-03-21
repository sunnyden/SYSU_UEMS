/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.UEMS;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.Course.Course;
import com.denghaoqing.sysu.MainActivity;
import com.denghaoqing.sysu.Utils.OnTaskFinishHandler;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import java.io.FileOutputStream;

import cz.msebera.android.httpclient.Header;


public class UEMS {
    public static final String UEMS_STUDENT_PHOTO_URL = "https://uems.sysu.edu.cn/jwxt/student-status/student-info/photo";  //GET
    public static final String UEMS_STUDENT_CREDIT_STATUS = "https://uems.sysu.edu.cn/jwxt/student-status/student-info/credit-situation"; //GET
    public static final String UEMS_STUDENT_INFO_URL = "https://uems.sysu.edu.cn/jwxt/student-status/student-info/detail";
    public static final String UEMS_PERSONAL_SCORE = "https://uems.sysu.edu.cn/jwxt/achievement-manage/score-check/list?addScoreFlag=false";//GET JSON
    public static final String UEMS_SCHOOL_PROFESSION_LIST = "https://uems.sysu.edu.cn/jwxt/base-info/profession-direction/list"; //POST,JSON
    public static final String UEMS_STUDENT_STATUS = "https://uems.sysu.edu.cn/jwxt/achievement-manage/score-check/checkStuStatus";
    public static final String UEMS_STUDENT_PLAN_BASIC = "https://uems.sysu.edu.cn/jwxt/training-programe/training-programe/undergradute/baseinfo";//grade=2016&professionCode=n08070400
    public static final String UEMS_STUDENT_PLAN_COURSE_TYPE = "https://uems.sysu.edu.cn/jwxt/training-programe/training-programe/undergradute/baseinfo/left";//&grade=2016&professionCode=n05010102&programType=all
    public static final String UEMS_STUDENT_PLAN_COURSE_LIST = "https://uems.sysu.edu.cn/jwxt/training-programe/training-programe/undergradute/baseinfo/course";//?&courseTypeCode=10&grade=2016&professionCode=n08070400

    public static final String HEART_BEAT = "https://uems.sysu.edu.cn/jwxt/api/login/status";
    public static final String UEMS_SCHEDULE_URL = "https://uems.sysu.edu.cn/jwxt/student-status/student-info/student-no-schedule?academicYear=%s&weekly=%s";
    public static final String UEMS_SCHOOL_CALENDER = "https://uems.sysu.edu.cn/jwxt/base-info/school-calender?academicYear=%s&weekly=%s";
    public static final String SHARE_PREF_NAME = "StudentInfo";
    public static final String STUDENT_PHOTO_FILE = "Student.jpg";
    public static final String ELECT_AUTH = "http://uems.sysu.edu.cn/elect/casLogin";// Auth and fetch course type list. should allow 302 redirect.
    //For the return, if the operation succeeded, there will be an empty response returned, otherwize, it will return an json with error messages.
    public static final String STUDENT_COURSE_ELECT = "https://uems.sysu.edu.cn/elect/s/elect";//POST,jxbh as course id,xkjdszid for courseType, sid for session id
    public static final String STUDENT_COURSE_UNELECT = "https://uems.sysu.edu.cn/elect/s/unelect";//POST,params is exactly the same as elect


    /*
    * Fetch list by using GET method.
    * xqm for Campus ID, 1 is South camp, 2 is
    *
    * */
    public static final String STUDENT_COURSE_ELECT_LIST = "https://uems.sysu.edu.cn/elect/s/courses?xkjdszid=%s&fromSearch=false&sid=%s"; //GET
    public static final String XQM_SOUTH = "1";
    public static final String XQM_NORTH = "2";
    public static final String XQM_ZHUHAI = "3";
    public static final String XQM_EAST = "4";
    //TODO implement browsing different campus on next term.
    private final String LOG_TAG = "UEMS";
    public boolean initSync = false;
    public JSONObject StudentInfo;
    private Context context;
    private boolean loginState = false;

    public UEMS(Context context) {
        this.context = context;
    }

    public void getBasicStudentInfo() {
        getStudentStatus();
        final CookieHelper cookieHelper = new CookieHelper(context);
        AsyncHttpClient studentInfoClient = new AsyncHttpClient();
        studentInfoClient.setCookieStore(cookieHelper);
        studentInfoClient.get(UEMS_STUDENT_PHOTO_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    Log.e(LOG_TAG, "Onsuccess");
                    FileOutputStream outputStream;
                    try {
                        outputStream = context.openFileOutput(STUDENT_PHOTO_FILE, Context.MODE_PRIVATE);
                        outputStream.write(responseBody);
                        outputStream.close();
                        MainActivity.currentActivity.updateInfo();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(LOG_TAG, String.valueOf(statusCode));
            }
        });
        studentInfoClient.get(UEMS_STUDENT_INFO_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject studentInfo = new JSONObject(new String(responseBody));
                    if (studentInfo.getInt("code") == 200) {
                        StudentInfo = studentInfo.getJSONObject("data");
                        SharedPreferences sharedPref = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("studentNo", StudentInfo.getString("studentNo"));
                        editor.putString("studentName", StudentInfo.getString("studentName"));
                        editor.putString("sex", StudentInfo.getString("sexCode").equals("1") ? "M" : "F");
                        editor.putString("schoolNo", StudentInfo.getString("schoolNo"));
                        editor.putString("SchoolName", StudentInfo.getString("schoolName"));
                        editor.putString("collegeNo", StudentInfo.getString("collegeNo"));
                        editor.putString("collegeName", StudentInfo.getString("collegeName"));
                        editor.putString("majorName", StudentInfo.getString("majorName"));
                        editor.putString("gradeMajorNo", StudentInfo.getString("gradeMajorNo"));
                        editor.putString("gradeMajorName", StudentInfo.getString("gradeMajorName"));
                        editor.putString("grade", StudentInfo.getString("grade"));
                        editor.putString("classNo", StudentInfo.getString("classNo"));
                        editor.putString("studentCategory", StudentInfo.getString("studentCategory"));
                        editor.putString("entranceMode", StudentInfo.getString("entranceMode"));
                        editor.putString("rollStatus", StudentInfo.getString("rollStatus"));
                        editor.putString("atSchoolStatus", StudentInfo.getString("atSchoolStatus"));
                        editor.putString("lengthOfSchooling", StudentInfo.getString("lengthOfSchooling"));
                        editor.apply();
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("grade", StudentInfo.getString("grade"));
                        requestParams.put("professionCode", "n" + StudentInfo.getString("gradeMajorNo").split("n")[1]);
                        AsyncHttpClient teachPlanClient = new AsyncHttpClient();
                        teachPlanClient.setCookieStore(cookieHelper);
                        teachPlanClient.get(UEMS_STUDENT_PLAN_BASIC, requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    JSONObject resp = new JSONObject(new String(responseBody));
                                    if (resp.getInt("code") == 200) {
                                        SharedPreferences sp = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
                                        SharedPreferences.Editor editorPlan = sp.edit();
                                        JSONObject data = resp.getJSONObject("data");
                                        editorPlan.putString("trainingPrograme", data.getString("trainingPrograme"));
                                        editorPlan.putString("ruleRequire", data.getString("ruleRequire"));
                                        editorPlan.putString("majorCharacte", data.getString("majorCharacte"));
                                        editorPlan.putString("majorCore", data.getString("majorCore"));
                                        editorPlan.putString("courseInfo", data.getJSONArray("courseInfo").toString());
                                        editorPlan.apply();
                                        Course course = new Course(context);
                                        course.syncTeachPlanCourse(Integer.parseInt(sp.getString("grade", "2016")),
                                                "n" + StudentInfo.getString("gradeMajorNo").split("n")[1],
                                                new OnTaskFinishHandler() {
                                                    @Override
                                                    public void onTaskSucceed() {

                                                    }

                                                    @Override
                                                    public void onTaskFailure() {

                                                    }
                                                });
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                            }
                        });

                    } else {
                        Log.e(LOG_TAG, "Student Request Error");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });

    }

    public void getStudentStatus() {
        /*
        * {"code":51011403,"message":"你在2017-2018-2学期未进行学籍注册，不能查询成绩！"}
        * */
        CookieHelper cookieHelper = new CookieHelper(context);
        AsyncHttpClient client = new AsyncHttpClient();
        client.setCookieStore(cookieHelper);
        client.get(UEMS_STUDENT_STATUS, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("stdstatcode", response.getInt("code")).apply();
                } catch (Exception e) {

                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    SharedPreferences sharedPreferences = context.getSharedPreferences(SHARE_PREF_NAME, Context.MODE_PRIVATE);
                    sharedPreferences.edit().putInt("stdstatcode", response.getInt("code")).putString("statmsg", response.getString("message")).apply();
                } catch (Exception e) {

                }
            }
        });
    }

    public boolean getLoginState() {
        loginState = false;
        SyncHttpClient client = new SyncHttpClient();
        CookieHelper cookieHelper = new CookieHelper(context);
        client.setCookieStore(cookieHelper);
        client.get(HEART_BEAT, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (response.getInt("code") == 200) {
                        loginState = response.getInt("data") == 1;
                    } else {
                        loginState = false;
                    }
                } catch (Exception e) {
                    loginState = false;
                }

            }
        });
        return loginState;
    }
}
