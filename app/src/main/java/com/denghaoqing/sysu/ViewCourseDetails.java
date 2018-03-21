/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.denghaoqing.sysu.Dashboard.MemoViewHolder;
import com.denghaoqing.sysu.Memo.Memo;
import com.denghaoqing.sysu.Memo.MemoDefaultAdapter;
import com.denghaoqing.sysu.Schedule.GeneralSchedule;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class ViewCourseDetails extends AppCompatActivity {
    private NestedScrollView baseLayout;
    private Intent courseIntent;
    private int previousFingerPosition = 0;
    private int baseLayoutPosition = 0;
    private int defaultViewHeight;
    private boolean isClosing = false;
    private boolean isScrollingUp = false;
    private boolean isScrollingDown = false;
    private RelativeLayout courseTimeLayout, teacherLayout, classroomLayout, memoAddLayout;
    private RecyclerView memoCardList;
    private String courseName;
    private String teacher;
    private String classroom = null;
    private Calendar beginTime, endTime;
    private String rawEvent;
    private TextView mCourseTime, mTeacher, mLocation;
    private float baseLayoutDefaultY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_course_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //baseLayout = (NestedScrollView)findViewById(R.id.course_detail_view);
        //baseLayout.setOnTouchListener(this);
        //baseLayoutDefaultY = baseLayout.getY();
        courseIntent = getIntent();
        beginTime = (Calendar) courseIntent.getSerializableExtra("startTime");
        endTime = (Calendar) courseIntent.getSerializableExtra("endTime");
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final long eventId = addCalendarAccount(ViewCourseDetails.this);
                Snackbar.make(view, R.string.event_added, Snackbar.LENGTH_LONG)
                        .setAction(R.string.undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ContentResolver cr = getContentResolver();
                                ContentValues values = new ContentValues();
                                Uri deleteUri = null;
                                deleteUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
                                int rows = getContentResolver().delete(deleteUri, null, null);

                            }
                        }).show();
            }
        });
        rawEvent = courseIntent.getStringExtra("courseInfo");
        String[] parasedEvent = rawEvent.split("\n");
        courseName = parasedEvent[0];
        this.setTitle(courseName);
        teacherLayout = findViewById(R.id.teacherLayout);
        courseTimeLayout = findViewById(R.id.courseTimeLayout);
        classroomLayout = findViewById(R.id.classroomLayout);
        memoAddLayout = findViewById(R.id.add_memo_layout);
        memoAddLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(ViewCourseDetails.this, AddMemoActivity.class);
                mIntent.putExtra("courseName", courseName);
                startActivity(mIntent);
            }
        });

        mCourseTime = findViewById(R.id.courseTime);
        mTeacher = findViewById(R.id.teacherName);
        mLocation = findViewById(R.id.classroom);
        teacher = parasedEvent[1];
        mTeacher.setText(teacher);
        int[] sect = GeneralSchedule.getSectionByTime(beginTime, endTime);
        beginTime.setFirstDayOfWeek(Calendar.SUNDAY);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        mCourseTime.setText(String.format(getResources().getString(R.string.section_brief),
                parseWeekIntToString(beginTime.get(Calendar.DAY_OF_WEEK) - 1),
                String.valueOf(sect[0]), String.valueOf(sect[1]), sdf.format(beginTime.getTime()),
                sdf.format(endTime.getTime())));
        if (parasedEvent.length == 3) {
            classroom = parasedEvent[2];
            mLocation.setText(classroom);
        } else {
            classroomLayout.setVisibility(View.GONE);
        }
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.setBackgroundColor(courseIntent.getIntExtra("color", getResources().getColor(R.color.colorPrimary)));
        toolbar.setBackgroundColor(courseIntent.getIntExtra("color", getResources().getColor(R.color.colorPrimary)));
        getWindow().setStatusBarColor(courseIntent.getIntExtra("color", getResources().getColor(R.color.colorPrimary)));
        CollapsingToolbarLayout colToolbarLayout = findViewById(R.id.toolbar_layout);
        colToolbarLayout.setContentScrimColor(courseIntent.getIntExtra("color", getResources().getColor(R.color.colorPrimary)));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        //Animation animation = AnimationUtils.loadAnimation(this, R.anim.);

        memoCardList = findViewById(R.id.memo_card_list_view_recyc);
        final MemoDefaultAdapter adapter = new MemoDefaultAdapter(Memo.getMemo(this, courseName), this);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        memoCardList.setLayoutManager(manager);
        memoCardList.setAdapter(adapter);
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int swipeDir) {
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                adapter.removeItem(position);
                adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                final Memo memo = ((MemoViewHolder) viewHolder).getMemo();
                final Thread deleteThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(10000);
                            memo.delete();
                        } catch (Exception e) {

                        }
                    }
                });
                deleteThread.start();
                Snackbar snackbar = Snackbar.make(viewHolder.itemView, getString(R.string.memo_deleted)
                        , Snackbar.LENGTH_LONG).setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteThread.interrupt();
                        adapter.restoreItem(position, memo);
                    }
                });
                snackbar.show();


            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return true;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(memoCardList);
    }


    private String parseWeekIntToString(int id) {
        switch (id) {
            case 0:
                return getResources().getString(R.string.sunday);
            case 1:
                return getResources().getString(R.string.monday);
            case 2:
                return getResources().getString(R.string.tuesday);
            case 3:
                return getResources().getString(R.string.wednesday);
            case 4:
                return getResources().getString(R.string.thursday);
            case 5:
                return getResources().getString(R.string.friday);
            case 6:
                return getResources().getString(R.string.saturday);
            default:
                return null;
        }
    }

    private long addCalendarAccount(Context context) {
        long calID = getCalendarId(context);
        long startMillis = 0;
        long endMillis = 0;
        startMillis = beginTime.getTimeInMillis();
        endMillis = endTime.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, courseName);
        values.put(CalendarContract.Events.DESCRIPTION, teacher);
        values.put(CalendarContract.Events.EVENT_LOCATION, classroom);
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        try {
            Uri uri = cr.insert(CalendarContract.Events.CONTENT_URI, values);
            long eventID = Long.parseLong(uri.getLastPathSegment());

            ContentValues remindValues = new ContentValues();
            remindValues.put(CalendarContract.Reminders.MINUTES, 30);
            remindValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
            remindValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            cr.insert(CalendarContract.Reminders.CONTENT_URI, remindValues);

            return eventID;
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return -1;
    }

    private int getCalendarId(Context context) {
        try {
            Cursor cursor = null;
            ContentResolver contentResolver = context.getContentResolver();
            Uri calendars = CalendarContract.Calendars.CONTENT_URI;

            String[] EVENT_PROJECTION = new String[]{
                    CalendarContract.Calendars._ID,                           // 0
                    CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                    CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
                    CalendarContract.Calendars.OWNER_ACCOUNT,                 // 3
                    CalendarContract.Calendars.IS_PRIMARY                     // 4
            };

            int PROJECTION_ID_INDEX = 0;
            int PROJECTION_ACCOUNT_NAME_INDEX = 1;
            int PROJECTION_DISPLAY_NAME_INDEX = 2;
            int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
            int PROJECTION_VISIBLE = 4;

            cursor = contentResolver.query(calendars, EVENT_PROJECTION, null, null, null);

            if (cursor.moveToFirst()) {
                String calName;
                long calId = 0;
                String visible;

                do {
                    calName = cursor.getString(PROJECTION_DISPLAY_NAME_INDEX);
                    calId = cursor.getLong(PROJECTION_ID_INDEX);
                    visible = cursor.getString(PROJECTION_VISIBLE);
                    if (visible.equals("1")) {
                        return (int) calId;
                    }
                } while (cursor.moveToNext());

                return (int) calId;
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
        } catch (SecurityException e) {

        }

        return 1;
    }
}
