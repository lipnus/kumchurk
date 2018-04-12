package com.lipnus.kumchurk.data;

import java.util.List;

/**
 * Created by Sunpil on 2017-02-26.
 * 좋아요 페이지에 나올 정보
 */

public class Alarm_JSON {

    public List<Alarm> alarmData;

    public Alarm_JSON(List<Alarm> alarmData) {
        this.alarmData = alarmData;
    }
}
