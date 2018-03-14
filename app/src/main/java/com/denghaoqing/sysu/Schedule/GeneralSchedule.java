/*
 *  Copyrights (C) Haoqing Deng<admin@denghaoqing.com> All rights reserved
 *  This file is part of the project SYSU_UEMS
 */

package com.denghaoqing.sysu.Schedule;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by sunny on 18-2-27.
 */

public class GeneralSchedule {
    public int StartHour = 0, StartMinute = 0, EndHour = 0, EndMinute = 0;

    public static int[] getSectionByTime(Calendar beginTime, Calendar endTime) {
        int section[] = new int[2];
        if (beginTime.get(Calendar.HOUR_OF_DAY) == 8 && beginTime.get(Calendar.MINUTE) == 0) {
            section[0] = 1;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 8 && beginTime.get(Calendar.MINUTE) == 55) {
            section[0] = 2;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 10 && beginTime.get(Calendar.MINUTE) == 0) {
            section[0] = 3;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 10 && beginTime.get(Calendar.MINUTE) == 55) {
            section[0] = 4;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 14 && beginTime.get(Calendar.MINUTE) == 20) {
            section[0] = 5;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 15 && beginTime.get(Calendar.MINUTE) == 15) {
            section[0] = 6;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 16 && beginTime.get(Calendar.MINUTE) == 20) {
            section[0] = 7;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 17 && beginTime.get(Calendar.MINUTE) == 15) {
            section[0] = 8;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 19 && beginTime.get(Calendar.MINUTE) == 0) {
            section[0] = 9;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 19 && beginTime.get(Calendar.MINUTE) == 55) {
            section[0] = 10;
        } else if (beginTime.get(Calendar.HOUR_OF_DAY) == 20 && beginTime.get(Calendar.MINUTE) == 50) {
            section[0] = 11;
        }
        if (endTime.get(Calendar.HOUR_OF_DAY) == 8 && endTime.get(Calendar.MINUTE) == 45) {
            section[1] = 1;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 9 && endTime.get(Calendar.MINUTE) == 40) {
            section[1] = 2;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 10 && endTime.get(Calendar.MINUTE) == 45) {
            section[1] = 3;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 11 && endTime.get(Calendar.MINUTE) == 40) {
            section[1] = 4;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 15 && endTime.get(Calendar.MINUTE) == 5) {
            section[1] = 5;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 16 && endTime.get(Calendar.MINUTE) == 0) {
            section[1] = 6;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 17 && endTime.get(Calendar.MINUTE) == 5) {
            section[1] = 7;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 18 && endTime.get(Calendar.MINUTE) == 0) {
            section[1] = 8;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 19 && endTime.get(Calendar.MINUTE) == 45) {
            section[1] = 9;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 20 && endTime.get(Calendar.MINUTE) == 40) {
            section[1] = 10;
        } else if (endTime.get(Calendar.HOUR_OF_DAY) == 21 && endTime.get(Calendar.MINUTE) == 35) {
            section[1] = 11;
        }

        return section;
    }

    public static int getOngoingSection(Calendar calendar) {
        int i;
        Date dateNow = calendar.getTime();
        for (i = 1; i < 12; i++) {
            GeneralSchedule schedule = new GeneralSchedule();
            schedule.getSectionDuration(i);
            Date bgnDate = calendar.getTime();
            Date endDate = calendar.getTime();
            bgnDate.setHours(schedule.StartHour);
            bgnDate.setMinutes(schedule.StartMinute);
            bgnDate.setSeconds(0);
            endDate.setHours(schedule.EndHour);
            endDate.setMinutes(schedule.EndMinute);
            endDate.setSeconds(0);
            if (bgnDate.getTime() - dateNow.getTime() < 0 && endDate.getTime() - dateNow.getTime() > 0) {
                return i;
            }
        }
        return 0;
    }

    public static int getUpcomingSection(Calendar calendar) {
        int i;
        Date dateNow = calendar.getTime();
        for (i = 1; i < 12; i++) {
            GeneralSchedule schedule = new GeneralSchedule();
            schedule.getSectionDuration(i);
            Date bgnDate = calendar.getTime();
            bgnDate.setHours(schedule.StartHour);
            bgnDate.setMinutes(schedule.StartMinute);
            bgnDate.setSeconds(0);
            long diffMills = bgnDate.getTime() - dateNow.getTime();
            if (diffMills / 1000 <= 3600 && diffMills >= 0) {
                return i;
            }
        }
        return 0;
    }

    public static long getTimeInterval(Calendar calendar, int section) {
        Date dateNow = calendar.getTime();
        GeneralSchedule schedule = new GeneralSchedule();
        schedule.getSectionDuration(section);
        Date bgnDate = calendar.getTime();
        bgnDate.setHours(schedule.StartHour);
        bgnDate.setMinutes(schedule.StartMinute);
        bgnDate.setSeconds(0);
        return bgnDate.getTime() - dateNow.getTime();
    }

    public void getSectionDuration(int section) {
        switch (section) {
            case 1:
                StartHour = 8;
                StartMinute = 0;
                EndHour = 8;
                EndMinute = 45; //8:00~8:45
                break;
            case 2:
                StartHour = 8;
                StartMinute = 55;
                EndHour = 9;
                EndMinute = 40; //8:55~9:40
                break;
            case 3:
                StartHour = 10;
                StartMinute = 0;
                EndHour = 10;
                EndMinute = 45; //10:00~10:45
                break;
            case 4:
                StartHour = 10;
                StartMinute = 55;
                EndHour = 11;
                EndMinute = 40; //10:55~11:40
                break;
            case 5:
                StartHour = 14;
                StartMinute = 20;
                EndHour = 15;
                EndMinute = 5; //14:20~15:05
                break;
            case 6:
                StartHour = 15;
                StartMinute = 15;
                EndHour = 16;
                EndMinute = 0; //15:15~16:00
                break;
            case 7:
                StartHour = 16;
                StartMinute = 20;
                EndHour = 17;
                EndMinute = 5; //16:20~17:05
                break;
            case 8:
                StartHour = 17;
                StartMinute = 15;
                EndHour = 18;
                EndMinute = 0; //17:15~18:00
                break;
            case 9:
                StartHour = 19;
                StartMinute = 0;
                EndHour = 19;
                EndMinute = 45;
                break;
            case 10:
                StartHour = 19;
                StartMinute = 55;
                EndHour = 20;
                EndMinute = 40;
                break;
            case 11:
                StartHour = 20;
                StartMinute = 50;
                EndHour = 21;
                EndMinute = 35;
                break;
        }
    }
}
