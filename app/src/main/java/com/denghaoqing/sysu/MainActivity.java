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

import android.Manifest;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.denghaoqing.sysu.Achievement.Achievement;
import com.denghaoqing.sysu.CAS.CAS;
import com.denghaoqing.sysu.CAS.CASAuthActivity;
import com.denghaoqing.sysu.Fragments.AchievementFragment;
import com.denghaoqing.sysu.Fragments.DashboardFragment;
import com.denghaoqing.sysu.Fragments.ScheduleFragment;
import com.denghaoqing.sysu.Fragments.TeachPlanFragment;
import com.denghaoqing.sysu.Handlers.ElectAuthHandler;
import com.denghaoqing.sysu.Service.WearService;
import com.denghaoqing.sysu.UEMS.Elect;
import com.denghaoqing.sysu.UEMS.ElectType;
import com.denghaoqing.sysu.UEMS.UEMS;
import com.denghaoqing.sysu.Utils.Software;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int REQUEST_STORAGE = 0;
    public static MainActivity currentActivity;
    public Elect elect = new Elect(this);
    public ArrayList<ElectType> electTypes;
    private Menu menu;
    private CircleImageView avator;
    private TextView mName;
    private TextView mDepartment;
    private FragmentManager fragmentManager = getFragmentManager();
    private MenuItem uemsItem = null, actionItem = null;
    private ProgressBar fragmentProgressBar;
    private ElectAuthHandler electAuthHandler;
    private ScheduleFragment scheduleFragment = new ScheduleFragment();
    private boolean noticed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try {
            Software.versionCheck(this);
            Log.e("versioncheck", "engage");
        } catch (Exception e) {
            //
        }

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        CAS.updateLoginState(this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                super.onDrawerClosed(drawerView);
            }
        };

        electAuthHandler = new ElectAuthHandler(this);


        drawer.addDrawerListener(toggle);
        currentActivity = this;
        toggle.syncState();

        fragmentProgressBar = findViewById(R.id.fragmentLoadingProgressBar);


        NavigationView navigationView = findViewById(R.id.nav_view);
        avator = navigationView.getHeaderView(0).findViewById(R.id.profile_image);
        mName = navigationView.getHeaderView(0).findViewById(R.id.name);
        mDepartment = navigationView.getHeaderView(0).findViewById(R.id.department);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_dashboard);

        uemsItem = navigationView.getMenu().findItem(R.id.menu_uems_entry);
        actionItem = navigationView.getMenu().findItem(R.id.menu_action_entry);

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        DashboardFragment dashboardFragment = new DashboardFragment();
        fragmentTransaction.replace(R.id.fragment_container, dashboardFragment);
        fragmentTransaction.commit();
        updateInfo();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    REQUEST_STORAGE);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR},
                    REQUEST_STORAGE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        this.menu = menu;
        try {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (getIntent().getAction().equals("com.denghaoqing.sysu.SCHEDULE")) {
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, scheduleFragment).addToBackStack("BS");
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, WearService.class);
        this.startService(serviceIntent);
    }

    public Menu getMenu() {
        return menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_back_today) {
            try {
                scheduleFragment.gotoToday();
            } catch (Exception e) {
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void updateInfo() {
        try {
            Picasso.with(this).load(new File(this.getFilesDir(), UEMS.STUDENT_PHOTO_FILE)).into(avator);
        } catch (Exception e) {
        }

        if (uemsItem != null && actionItem != null) {
            if (CAS.LOGIN) {
                uemsItem.setVisible(true);
                actionItem.setVisible(true);
            } else {
                uemsItem.setVisible(false);
                actionItem.setVisible(false);
            }
        }

        SharedPreferences sharedPreferences = this.getSharedPreferences(UEMS.SHARE_PREF_NAME, Context.MODE_PRIVATE);
        mName.setText(sharedPreferences.getString("studentName", "Not Logged In"));
        mDepartment.setText(sharedPreferences.getString("collegeName", "Not Logged In"));
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_login) {
            Intent mIntent = new Intent(MainActivity.this, CASAuthActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_achievement) {

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            AchievementFragment achievementFragment = new AchievementFragment();
            fragmentTransaction.replace(R.id.fragment_container, achievementFragment).addToBackStack("BS");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_curriculum) {
            TeachPlanFragment teachPlanFragment = new TeachPlanFragment();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, teachPlanFragment).addToBackStack("BS");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_schedule) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, scheduleFragment).addToBackStack("BS");
            fragmentTransaction.commit();
        } else if (id == R.id.nav_library) {
            Achievement achievement = new Achievement(this);
            achievement.pullScoreFromServer();
        } else if (id == R.id.nav_sync) {
            Intent mIntent = new Intent(MainActivity.this, DataFetchUI.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_settings) {
            Intent mIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(mIntent);
        } else if (id == R.id.nav_dashboard) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            DashboardFragment dashboardFragment = new DashboardFragment();
            fragmentTransaction.replace(R.id.fragment_container, dashboardFragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_select) {
            swapProgressBarVisibility(View.VISIBLE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (elect.auth()) {
                        electTypes = elect.getTypeArrayList();
                        electAuthHandler.sendEmptyMessage(ElectAuthHandler.MSG_AUTH_SUCCESS);
                    }
                }
            }).start();
        } else if (id == R.id.nav_feedback) {
            Intent mIntent = new Intent(MainActivity.this, FeedbackActivity.class);
            startActivity(mIntent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void swapProgressBarVisibility(int visibility) {
        fragmentProgressBar.setVisibility(visibility);
    }
}
