/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Profession;

import android.content.Context;
import android.util.Log;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.ByteArrayEntity;

/**
 * Created by sunny on 18-3-2.
 */

public class ProfessionListHelper {
    private final static String HTTP_CONTENT_TYPE_JSON = "application/json";
    private final static String LOG_TAG = "ProfessionListHelper";
    private Context context;

    public ProfessionListHelper(Context context) {
        this.context = context;
    }

    public void fetchList() {
        AsyncHttpClient client = new AsyncHttpClient();
        CookieHelper cookieHelper = new CookieHelper(context);
        client.setCookieStore(cookieHelper);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("pageNo", 1);
            jsonObject.put("pageSize", 5000);
            jsonObject.put("total", true);
            JSONObject subObj = new JSONObject();
            subObj.put("majorProfessionDircetion", 0); //note that this spelling mistake is following the API :)
            jsonObject.put("param", subObj);
            ByteArrayEntity byteArrayEntity = new ByteArrayEntity(jsonObject.toString().getBytes("UTF-8"));
            byteArrayEntity.setContentType(HTTP_CONTENT_TYPE_JSON);
            client.post(context, UEMS.UEMS_SCHOOL_PROFESSION_LIST, byteArrayEntity, HTTP_CONTENT_TYPE_JSON, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        if (response.getInt("code") == 200) {
                            JSONObject data = response.getJSONObject("data");
                            JSONArray rows = data.getJSONArray("rows");
                            int i = 0;
                            for (i = 0; i < rows.length(); i++) {
                                Log.d(LOG_TAG, String.valueOf(i));
                                JSONObject profession = rows.getJSONObject(i);
                                Profession professionObj = new Profession(context);
                                try {
                                    long res = professionObj.addProfession(profession.getString("code"),
                                            profession.getString("id"), profession.getString("name"),
                                            profession.getString("enName"), Integer.parseInt(profession.getString("educationalSystem")),
                                            profession.getInt("maxStudyYear"), profession.has("degreeGrantName") ? profession.getString("degreeGrantName") : "");
                                    Log.i(LOG_TAG, String.valueOf(res));
                                } catch (Exception e) {
                                    Log.e(LOG_TAG, profession.toString());
                                    Log.e(LOG_TAG, e.toString());
                                }

                            }
                        }
                        Log.e(LOG_TAG, String.valueOf(response.getInt("code")));
                        Log.e(LOG_TAG, String.valueOf(response.getJSONObject("data").getInt("total")));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        } catch (Exception e) {

        }

    }
}
