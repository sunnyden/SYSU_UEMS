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

package com.denghaoqing.sysu.CAS;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.BinaryHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;
import java.util.Iterator;

import cz.msebera.android.httpclient.Header;


/**
 * Created by sunny on 18-2-26.
 */

public class CAS {
    public static final String USER_AGENT = "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0";
    public static final String PREF_KEY_STORE = "login";
    private static final String SERVICE_AUTH_KEY = "service";
    private static final String SERVICE_AUTH_CALLBACK = "http://uems.sysu.edu.cn/jwxt/api/sso/cas/login?pattern=student-login";
    private static final String CAPTCHA_URL = "https://cas.sysu.edu.cn/cas/captcha.jsp";
    private static final String AUTH_POST_ACTION = "https://cas.sysu.edu.cn/cas/login?service=http://uems.sysu.edu.cn/jwxt/api/sso/cas/login?pattern=student-login";
    public static boolean LOGIN = false;
    private final String LOG_TAG = "CAS";
    private final String CAS_HOST = "https://cas.sysu.edu.cn";
    private final String CAS_URL = "https://cas.sysu.edu.cn/cas/login";
    public ImageView CaptchaView = null;
    private RequestParams LoginRequest = new RequestParams();
    private Bitmap Captcha;
    private Context context;
    private String storedNetId, storedPasswd;
    private Boolean requestFinish = false;
    private Boolean authSuccess = false;
    private Boolean captchaCorrect = false;

    public CAS(final Context context, final ImageView view) {
        try {
            this.context = context;
            storedNetId = context.getSharedPreferences(PREF_KEY_STORE, Context.MODE_PRIVATE).getString("netid", null);
            storedPasswd = context.getSharedPreferences(PREF_KEY_STORE, Context.MODE_PRIVATE).getString("password", null);
            LOGIN = (storedNetId != null && storedPasswd != null);
            URL authUrl = new URL(CAS_URL);
            final AsyncHttpClient client = new AsyncHttpClient();
            CookieHelper cookieHelper = new CookieHelper(context);
            client.setCookieStore(cookieHelper);
            client.addHeader("User-Agent", USER_AGENT);
            RequestParams requestParams = new RequestParams();
            requestParams.add(SERVICE_AUTH_KEY, SERVICE_AUTH_CALLBACK);
            client.get(CAS_URL, requestParams, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            Document doc = Jsoup.parse(new String(responseBody));
                            //Document doc = Jsoup.connect("https://cas.sysu.edu.cn/cas/login").get();
                            Element element = doc.getElementById("fm1");
                            if (element == null) {
                                try {
                                    if (context instanceof Activity) {
                                        ((Activity) context).finish();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return;
                            }
                            Log.e(LOG_TAG, doc.getElementById("fm1").attr("action"));
                        /*
                        Iterator<Element> elementIterator = doc.getElementById("fm1").children().iterator();
                        while (elementIterator.hasNext()){
                            Iterator<Element> subElementIterator = elementIterator.next().children().iterator();
                            while (element.)
                            Log.e(LOG_TAG,elementIterator.next().html());

                        }*/
                            Iterator<Element> elementIterator = element.children().select(".row.btn-row")
                                    .get(0).select("input").iterator();
                            while (elementIterator.hasNext()) {
                                Element hiddenElement = elementIterator.next();
                                LoginRequest.put(hiddenElement.attr("name"), hiddenElement.attr("value"));
                                Log.e(LOG_TAG, hiddenElement.attr("name"));
                                Log.e(LOG_TAG, hiddenElement.attr("value"));
                            }
                            //elementIterator = element.select(".row.btn-row").get(0).children().iterator();
                            client.get(CAPTCHA_URL, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    Captcha = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                                    view.setImageBitmap(Captcha);
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "Already Logged in.");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
            //String cookies=httpsURLConnection.getHeaderField("Set-Cookie");
            //Log.e(LOG_TAG,cookies);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CAS(Context context) {
        try {
            this.context = context;
            URL authUrl = new URL(CAS_URL);
            final AsyncHttpClient client = new AsyncHttpClient();
            CookieHelper cookieHelper = new CookieHelper(context);
            client.setCookieStore(cookieHelper);
            client.addHeader("User-Agent", USER_AGENT);
            RequestParams requestParams = new RequestParams();
            requestParams.add(SERVICE_AUTH_KEY, SERVICE_AUTH_CALLBACK);
            client.get(CAS_URL, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (statusCode == 200) {
                        try {
                            Document doc = Jsoup.parse(new String(responseBody));
                            //Document doc = Jsoup.connect("https://cas.sysu.edu.cn/cas/login").get();
                            Element element = doc.getElementById("fm1");
                            Log.e(LOG_TAG, doc.getElementById("fm1").attr("action"));
                        /*
                        Iterator<Element> elementIterator = doc.getElementById("fm1").children().iterator();
                        while (elementIterator.hasNext()){
                            Iterator<Element> subElementIterator = elementIterator.next().children().iterator();
                            while (element.)
                            Log.e(LOG_TAG,elementIterator.next().html());

                        }*/
                            Iterator<Element> elementIterator = element.children().select(".row.btn-row")
                                    .get(0).select("input").iterator();
                            while (elementIterator.hasNext()) {
                                Element hiddenElement = elementIterator.next();
                                LoginRequest.put(hiddenElement.attr("name"), hiddenElement.attr("value"));
                                Log.e(LOG_TAG, hiddenElement.attr("name"));
                                Log.e(LOG_TAG, hiddenElement.attr("value"));
                            }
                            //elementIterator = element.select(".row.btn-row").get(0).children().iterator();
                            client.get(CAPTCHA_URL, new BinaryHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] binaryData) {
                                    if (statusCode == 200) {
                                        Captcha = BitmapFactory.decodeByteArray(binaryData, 0, binaryData.length);
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] binaryData, Throwable error) {

                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d(LOG_TAG, "Already Logged in.");
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                }
            });
            //String cookies=httpsURLConnection.getHeaderField("Set-Cookie");
            //Log.e(LOG_TAG,cookies);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLoginState(Context context) {
        String netid, passwd;
        netid = context.getSharedPreferences(PREF_KEY_STORE, Context.MODE_PRIVATE).getString("netid", null);
        passwd = context.getSharedPreferences(PREF_KEY_STORE, Context.MODE_PRIVATE).getString("password", null);
        LOGIN = (netid != null && passwd != null);
    }

    public Bitmap getCaptcha() {
        return Captcha;
    }

    public Boolean getAuthSuccess() {
        return authSuccess;
    }

    public Boolean getRequestFinish() {
        return requestFinish;
    }

    public Boolean authWithKeyStoreSync(String captcha) {
        String netid = storedNetId;
        String password = storedPasswd;
        try {
            requestFinish = false;
            captchaCorrect = false;
            authSuccess = false;
            SyncHttpClient authClient = new SyncHttpClient();
            authClient.addHeader("Referer", "https://cas.sysu.edu.cn/cas/login");
            authClient.addHeader("User-Agent", USER_AGENT);
            final CookieHelper cookieHelper = new CookieHelper(context);
            authClient.setCookieStore(cookieHelper);
            LoginRequest.put("username", netid);
            LoginRequest.put("password", password);
            LoginRequest.put("captcha", captcha);
            //LoginRequest.remove("execution");
            //LoginRequest.put("execution","dfjdalkfjkljdfklajfdklajkldfalf");
            authClient.setEnableRedirects(true);
            authClient.setLoggingLevel(Log.INFO);
            Log.e(LOG_TAG, LoginRequest.toString());
            //HttpsURLConnection connection = (HttpsURLConnection) new URL(CAS_URL).openConnection();

            authClient.post(CAS_URL, LoginRequest, new JsonHttpResponseHandler() {


                @Override
                public void onSuccess(int statusCode, Header[] headers, String responseString) {
                    Log.e(LOG_TAG, String.valueOf(statusCode));
                    Log.e(LOG_TAG, responseString);
                    authSuccess = true;
                    Log.e("sync", "sync");
                    if (!responseString.contains("Captcha is wrong.")) {
                        captchaCorrect = true;
                    } else {
                        authSuccess = false;
                    }
                    requestFinish = true;
                    if (authSuccess) {
                        UEMS uems = new UEMS(context);
                        uems.getBasicStudentInfo();

                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    Log.e(LOG_TAG, String.valueOf(statusCode));
                    Log.e(LOG_TAG, responseString);
                    authSuccess = true;
                    Log.e("sync", "sync");
                    if (!responseString.contains("Captcha is wrong.")) {
                        captchaCorrect = true;
                    } else {
                        authSuccess = false;
                    }
                    requestFinish = true;
                    if (authSuccess) {
                        UEMS uems = new UEMS(context);
                        uems.getBasicStudentInfo();

                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        Log.e("sync", "execlater");
        return authSuccess;
    }

    public Boolean Auth(final String netid, final String password, String captcha) {
        try {
            requestFinish = false;
            captchaCorrect = false;
            authSuccess = false;
            AsyncHttpClient authClient = new AsyncHttpClient();
            authClient.addHeader("Referer", "https://cas.sysu.edu.cn/cas/login");
            authClient.addHeader("User-Agent", USER_AGENT);
            final CookieHelper cookieHelper = new CookieHelper(context);
            authClient.setCookieStore(cookieHelper);
            LoginRequest.put("username", netid);
            LoginRequest.put("password", password);
            LoginRequest.put("captcha", captcha);
            //LoginRequest.remove("execution");
            //LoginRequest.put("execution","dfjdalkfjkljdfklajfdklajkldfalf");
            authClient.setEnableRedirects(true);
            authClient.setLoggingLevel(Log.INFO);
            Log.e(LOG_TAG, LoginRequest.toString());
            //HttpsURLConnection connection = (HttpsURLConnection) new URL(CAS_URL).openConnection();


            authClient.post(CAS_URL, LoginRequest, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    Log.e(LOG_TAG, String.valueOf(statusCode));
                    Log.e(LOG_TAG, new String(responseBody));
                    authSuccess = true;

                    if (!new String(responseBody).contains("Captcha is wrong.")) {
                        captchaCorrect = true;
                    } else {
                        authSuccess = false;
                    }
                    requestFinish = true;
                    if (authSuccess) {
                        UEMS uems = new UEMS(context);
                        uems.getBasicStudentInfo();
                        SharedPreferences sp = context.getSharedPreferences(PREF_KEY_STORE, Context.MODE_PRIVATE);
                        sp.edit().putString("netid", netid).putString("password", password).apply();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    //Log.e(LOG_TAG,String.valueOf(statusCode));
                    //Log.e(LOG_TAG,new String(responseBody));
                    for (Header header : headers) {
                        Log.e(LOG_TAG, header.toString());
                    }
                    captchaCorrect = true;
                    //error.printStackTrace();
                    requestFinish = true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public void updateCaptcha(final ImageView view) {
        AsyncHttpClient client = new AsyncHttpClient();
        CookieHelper cookieHelper = new CookieHelper(context);
        client.setCookieStore(cookieHelper);
        client.get(CAPTCHA_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Captcha = BitmapFactory.decodeByteArray(responseBody, 0, responseBody.length);
                view.setImageBitmap(Captcha);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //Log.e(LOG_TAG,"Captcha Failure:"+String.valueOf(statusCode));
            }
        });
    }

    public Boolean getCaptchaCorrect() {
        return captchaCorrect;
    }
}
