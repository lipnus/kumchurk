package com.lipnus.kumchurk.submenu;

import com.lipnus.kumchurk.firebaseModel.Alarm_fb;

/**
 * Created by Sunpil on 2016-07-13.
 *
 * 리스트뷰의 아이템데이터
 *
 */

public class AlarmListViewItem {

    public Alarm_fb alarm_fb;

    public AlarmListViewItem(Alarm_fb alarm_fb) {
        this.alarm_fb = alarm_fb;
    }
}