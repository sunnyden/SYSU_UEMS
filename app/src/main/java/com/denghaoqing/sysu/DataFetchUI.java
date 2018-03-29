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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.denghaoqing.sysu.Achievement.Achievement;
import com.denghaoqing.sysu.CAS.CAS;
import com.denghaoqing.sysu.Profession.ProfessionListHelper;
import com.denghaoqing.sysu.Schedule.Schedule;
import com.denghaoqing.sysu.Schedule.SchoolCalendarHelper;
import com.denghaoqing.sysu.Schedule.Semaster;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.denghaoqing.sysu.Utils.TaskScheduler;

import java.util.Calendar;

public class DataFetchUI extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner semesterChooser;
    private SchoolCalendarHelper calendarHelper;
    private RelativeLayout laySubmit;
    private TextView welcomeText;
    private ImageView resultImageView;
    private TextView errMsgText;
    private DatePicker datePicker;
    private ProgressBar progressBar;
    private Semaster selSemaster;
    private boolean haveCalendar = false;

    private ProgressBar captAuthProg = null;
    private ProgressBar fetchProgressBar = null;
    private ImageView captchaView;
    private EditText captchaInput;
    private CAS mCAS;

    private AlertDialog dialogCaptcha = null;
    //private final WeakReference<DataFetchUI> loginActivityWeakRef;
    private Handler loginHandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setContentView(R.layout.activity_data_fetch_ui);
        String studName = this.getSharedPreferences(UEMS.SHARE_PREF_NAME, Context.MODE_PRIVATE).getString("studentName", null);
        if (studName == null) {
            finish();
        }
        semesterChooser = findViewById(R.id.semesterSelector);
        calendarHelper = new SchoolCalendarHelper(5, this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, calendarHelper.getArrayStringList());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterChooser.setOnItemSelectedListener(this);
        semesterChooser.setAdapter(adapter);
        welcomeText = findViewById(R.id.welcome_text);
        welcomeText.setText(String.format(getString(R.string.msg_welcome), studName));
        errMsgText = findViewById(R.id.err_msg_textview);
        resultImageView = findViewById(R.id.chk_result);
        datePicker = findViewById(R.id.termStartPicker);
        laySubmit = findViewById(R.id.rlay_touch_sync);
        laySubmit.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.chk_progress);
        fetchProgressBar = findViewById(R.id.dataFetchProgress);

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(DataFetchUI.this);
                    String netid = getApplicationContext().getSharedPreferences(CAS.PREF_KEY_STORE, Context.MODE_PRIVATE).getString("netid", null);
                    if (netid != null) {
                        builder.setTitle(String.format(getString(R.string.continue_with_netid), netid));
                        LayoutInflater inflater = getLayoutInflater();
                        final View dialogView = inflater.inflate(R.layout.dialog_captcha, null);
                        builder.setView(dialogView);
                        captchaView = dialogView.findViewById(R.id.diag_captcha_img);
                        captAuthProg = dialogView.findViewById(R.id.captchaLoginProgress);
                        captchaInput = dialogView.findViewById(R.id.diag_captcha_input);
                        mCAS = new CAS(getApplicationContext(), captchaView);
                        final Handler loginHandler = new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                if (msg.what == 1) {
                                    if (dialogCaptcha != null) {
                                        dialogCaptcha.dismiss();
                                    }

                                } else {
                                    captchaInput.setVisibility(View.VISIBLE);
                                    captchaInput.setVisibility(View.VISIBLE);
                                    captchaInput.requestFocus();
                                    captchaInput.setError(getString(R.string.captcha_incorrect));
                                    captAuthProg.setVisibility(View.GONE);
                                    mCAS.updateCaptcha(captchaView);
                                }
                            }
                        };
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Runnable auth = new Runnable() {
                                    @Override
                                    public void run() {
                                        if (mCAS.authWithKeyStoreSync(captchaInput.getText().toString())) {
                                            loginHandler.sendEmptyMessage(1);
                                        } else {
                                            loginHandler.sendEmptyMessage(2);
                                        }
                                        captAuthProg.setVisibility(View.VISIBLE);

                                        //loginHandler.sendEmptyMessage(1);
                                    }
                                };
                                new Thread(auth).start();

                            }
                        });

                        dialogCaptcha = builder.create();
                        dialogCaptcha.show();
                    }
                } else {
                    finish();
                }
            }
        };
        laySubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Schedule schedule = new Schedule(getApplicationContext());
                fetchProgressBar.setVisibility(View.VISIBLE);
                if (haveCalendar) {
                    schedule.fetchSemasterSchedule(selSemaster.toString());
                } else {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                    schedule.manualSetCalender(selSemaster.toString(), calendar);
                }
                ProfessionListHelper professionListHelper = new ProfessionListHelper(DataFetchUI.this);
                professionListHelper.fetchList();


                UEMS uems = new UEMS(DataFetchUI.this);
                uems.getBasicStudentInfo();

                Achievement achievement = new Achievement(DataFetchUI.this);
                achievement.pullScoreFromServer();
                //finish();
                laySubmit.setEnabled(false);
                laySubmit.setVisibility(View.GONE);
                laySubmit.setOnClickListener(null);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (TaskScheduler.runningThreads > 0) ;
                        handler.sendEmptyMessage(2);
                    }
                }).start();

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                UEMS uems = new UEMS(getApplicationContext());
                if (!uems.getLoginState()) {
                    handler.sendEmptyMessage(1);
                }
            }
        }).start();


    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {

        super.onPostCreate(savedInstanceState, persistentState);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        final Semaster semaster = calendarHelper.getSemasters().get((int) l);
        progressBar.setVisibility(View.VISIBLE);
        resultImageView.setVisibility(View.INVISIBLE);
        datePicker.setVisibility(View.GONE);
        errMsgText.setVisibility(View.GONE);
        haveCalendar = false;
        CheckCalendar mCheckCalendar = new CheckCalendar(semaster);
        mCheckCalendar.execute((Void) null);

        selSemaster = semaster;
        //Toast.makeText(this,calendarHelper.getSemasters().get((int)l).toString(),Toast.LENGTH_LONG).show();


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public class CheckCalendar extends AsyncTask<Void, Void, Boolean> {
        Semaster semaster;

        CheckCalendar(Semaster semaster) {
            this.semaster = semaster;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Schedule schedule = new Schedule(getApplicationContext());
            return schedule.haveSemaster(semaster);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            progressBar.setVisibility(View.INVISIBLE);
            if (result) {
                resultImageView.setImageResource(R.drawable.ic_done_green_24dp);
                haveCalendar = true;
            } else {
                resultImageView.setImageResource(R.drawable.ic_warning_24dp);
                datePicker.setVisibility(View.VISIBLE);
                errMsgText.setVisibility(View.VISIBLE);
            }
            resultImageView.setVisibility(View.VISIBLE);
            laySubmit.setVisibility(View.VISIBLE);
        }
    }


}
