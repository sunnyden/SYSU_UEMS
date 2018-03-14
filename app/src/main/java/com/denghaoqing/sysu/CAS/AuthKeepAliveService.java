/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.CAS;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.denghaoqing.sysu.Cookie.CookieHelper;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class AuthKeepAliveService extends Service {
    public static boolean stillAlive = false;
    public static boolean networkErr = false;
    private Runnable heartBeat = new Runnable() {
        @Override
        public void run() {
            CookieHelper cookieHelper = new CookieHelper(getApplicationContext());
            while (true) {
                try {
                    SyncHttpClient client = new SyncHttpClient();
                    client.setCookieStore(cookieHelper);

                    client.get(UEMS.HEART_BEAT, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            stillAlive = false;
                            networkErr = false;
                            try {
                                JSONObject resp = new JSONObject(new String(responseBody));
                                if (resp.getInt("code") == 200) {
                                    if (resp.getInt("data") == 1) {
                                        stillAlive = true;
                                    }
                                } else {
                                    networkErr = true;
                                }

                            } catch (Exception e) {
                                networkErr = true;
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            stillAlive = false;
                            networkErr = true;
                        }
                    });
                    Thread.sleep(60000);
                } catch (Exception e) {
                    //
                }
            }
        }
    };

    public AuthKeepAliveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return new Binder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread mWorker = new Thread(heartBeat);
        mWorker.start();
        return super.onStartCommand(intent, flags, startId);
    }
}
