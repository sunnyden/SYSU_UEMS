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

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.denghaoqing.sysu.Memo.Memo;
import com.denghaoqing.sysu.Schedule.Course;
import com.denghaoqing.sysu.Schedule.Schedule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class AddMemoActivity extends AppCompatActivity {
    private final int PICK_IMAGE = 1;
    private final int CAMERA_REQUEST = 2;
    String mCurrentPhotoPath;
    private RelativeLayout menuTrigger, actionTakePhoto, actionGallery, actionLinkCourse, actionCancelSelection, layoutGroup;
    private Schedule schedule;
    private int curWeek = 0;
    private String courseName;
    private ArrayList<Course> courseArrayList, courseMerged = new ArrayList<>();
    private String[] courseString;
    private String imgPath = "";
    private String camPath = "";
    private boolean imgSelFlag = false;
    private EditText title, content;
    private Course courseSelected = null;
    private Memo editMemo = null;
    private long memoId = -1;
    private int refCourseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_memo);
        menuTrigger = findViewById(R.id.action_toolbar_expand);
        layoutGroup = findViewById(R.id.layout_list_viewgroup);
        //layoutGroup.setVisibility(View.GONE);
        actionTakePhoto = findViewById(R.id.action_view_take_photo);
        actionGallery = findViewById(R.id.action_view_choose_image);
        actionLinkCourse = findViewById(R.id.action_view_link_course);
        actionCancelSelection = findViewById(R.id.action_view_cancel_selection);
        menuTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchVisibility();
            }
        });
        schedule = new Schedule(this);
        curWeek = schedule.getWeekByDate(Calendar.getInstance());
        courseName = getIntent().getStringExtra("courseName");
        memoId = getIntent().getLongExtra("memoId", -1);
        if (memoId != -1) {
            editMemo = Memo.getMemo(this, (int) memoId);
            courseName = editMemo.getCourseName();
            refCourseId = editMemo.getRefCourse();
        }
        courseArrayList = schedule.getCoursesByCourseName(courseName, curWeek);
        title = findViewById(R.id.titleEt);
        content = findViewById(R.id.contentEt);
        if (memoId != -1) {
            title.setText(editMemo.getTitle());
            content.setText(editMemo.getContent());
            imgPath = editMemo.getImagePath();
            if (!imgPath.equals("")) {
                setImgFlag(true);
            }

        }
        //courseString = new String[courseArrayList.size()];

        int courseSectStart = 0, courseSectEnd = 0;
        Course formerCourse = null;
        for (Course course : courseArrayList) {
            if (formerCourse == null) {
                formerCourse = course;
                formerCourse.setString(String.format(getString(R.string.week_dayofweek),
                        formerCourse.week, parseWeekIntToString(formerCourse.dayOfWeek)));
                if (formerCourse.getId() == refCourseId) {
                    courseSelected = formerCourse;
                }
                courseMerged.add(formerCourse);
                continue;
            }
            if (formerCourse.week != course.week || formerCourse.dayOfWeek != course.dayOfWeek) {
                formerCourse = course;
                formerCourse.setString(String.format(getString(R.string.week_dayofweek),
                        formerCourse.week, parseWeekIntToString(formerCourse.dayOfWeek)));
                if (formerCourse.getId() == refCourseId) {
                    courseSelected = formerCourse;
                }
                courseMerged.add(formerCourse);
            }
        }


        courseString = new String[courseMerged.size()];
        int i = 0;
        for (Course course : courseMerged) {
            courseString[i] = course.toString();
            i++;
        }

        if (courseSelected != null) {
            ((TextView) actionLinkCourse.findViewById(R.id.txt_link_course)).setText(courseSelected.toString());
        }

        actionLinkCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(AddMemoActivity.this);
                listDialog.setTitle(getString(R.string.link_course));
                listDialog.setItems(courseString, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        courseSelected = courseMerged.get(which);
                        ((TextView) actionLinkCourse.findViewById(R.id.txt_link_course)).setText(courseSelected.toString());
                        Toast.makeText(AddMemoActivity.this, courseSelected.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                listDialog.show();
            }
        });
        actionGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        actionTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File picFile = createImageFile();
                    if (picFile != null) {
                        camPath = picFile.getAbsolutePath();
                        Log.e("path", camPath);
                        Uri photoURI = FileProvider.getUriForFile(AddMemoActivity.this,
                                "com.denghaoqing.sysu.fileprovider",
                                picFile);
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(AddMemoActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }

            }
        });
        actionCancelSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    new File(imgPath).delete();
                    setImgFlag(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                imgPath = "";
            }
        });
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "MEMO_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void switchVisibility() {
        if (actionLinkCourse.getVisibility() == View.GONE) {
            if (!imgSelFlag) {
                actionTakePhoto.setVisibility(View.VISIBLE);
                actionGallery.setVisibility(View.VISIBLE);
                actionCancelSelection.setVisibility(View.GONE);
            } else {
                actionCancelSelection.setVisibility(View.VISIBLE);
                actionTakePhoto.setVisibility(View.GONE);
                actionGallery.setVisibility(View.GONE);
            }
            actionLinkCourse.setVisibility(View.VISIBLE);
        } else {
            actionTakePhoto.setVisibility(View.GONE);
            actionGallery.setVisibility(View.GONE);
            actionCancelSelection.setVisibility(View.GONE);
            actionLinkCourse.setVisibility(View.GONE);
        }
    }

    private void updateVisibility() {
        if (actionLinkCourse.getVisibility() != View.GONE) {
            if (!imgSelFlag) {
                actionTakePhoto.setVisibility(View.VISIBLE);
                actionGallery.setVisibility(View.VISIBLE);
                actionCancelSelection.setVisibility(View.GONE);
            } else {
                actionCancelSelection.setVisibility(View.VISIBLE);
                actionTakePhoto.setVisibility(View.GONE);
                actionGallery.setVisibility(View.GONE);
            }
            actionLinkCourse.setVisibility(View.VISIBLE);
        } else {
            actionTakePhoto.setVisibility(View.GONE);
            actionGallery.setVisibility(View.GONE);
            actionCancelSelection.setVisibility(View.GONE);
            actionLinkCourse.setVisibility(View.GONE);
        }
    }

    private void setImgFlag(boolean flag) {
        imgSelFlag = flag;
        updateVisibility();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                File store = createImageFile();
                FileOutputStream outputStream = new FileOutputStream(store);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                outputStream.close();
                imgPath = store.getAbsolutePath();
            } catch (Exception e) {
                e.printStackTrace();
            }
            setImgFlag(true);
        } else if (requestCode == CAMERA_REQUEST) {
            try {
                if (data != null) {
                    imgPath = camPath;
                    setImgFlag(true);
                } else {
                    imgPath = "";
                    setImgFlag(false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setImgFlag(false);
                imgPath = "";
            }

        }
        updateVisibility();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.memo_action, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_memo) {
            if (!title.getText().toString().equals("") && !content.getText().toString().equals("")) {
                if (memoId == -1) {
                    Memo memo = null;
                    if (courseSelected != null) {
                        memo = Memo.writeMemo(this, courseName, title.getText().toString(), content.getText().toString(), imgPath, courseSelected.getId());
                    } else {
                        memo = Memo.writeMemo(this, courseName, title.getText().toString(), content.getText().toString(), imgPath, -1);
                    }
                    if (memo != null) {
                        Toast.makeText(this, R.string.memo_stored, Toast.LENGTH_SHORT).show();
                        this.finish();
                    }
                } else {
                    editMemo.setTitle(title.getText().toString());
                    editMemo.setContent(content.getText().toString());
                    if (courseSelected != null) {
                        editMemo.setRefCourse(courseSelected.getId());
                    } else {
                        editMemo.setRefCourse(-1);
                    }
                    editMemo.setImagePath(imgPath);
                    editMemo.save();
                    Toast.makeText(this, R.string.memo_stored, Toast.LENGTH_SHORT).show();
                    this.finish();
                }


            } else {
                Toast.makeText(this, R.string.memo_title_content_empty, Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
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

}
