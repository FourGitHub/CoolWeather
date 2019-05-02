package com.fourweather.learn.entity;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import androidx.annotation.NonNull;

/**
 * Create on 2019/04/28
 *
 * @author Four
 * @description
 */
public class ScheduleEntity extends LitePalSupport {

    @Column(unique = true)
    private int pos;
    private String tripTheme;
    private String tripInfo;
    private String note; // 备注信息

    public ScheduleEntity(int pos, String tripTheme, String tripInfo, String note, boolean willNotify) {
        this.pos = pos;
        this.tripTheme = tripTheme;
        this.tripInfo = tripInfo;
        this.note = note;
        this.willNotify = willNotify;
    }

    private boolean willNotify;

    public ScheduleEntity() {

    }



    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getTripTheme() {
        return tripTheme;
    }

    public void setTripTheme(String tripTheme) {
        this.tripTheme = tripTheme;
    }

    public String getTripInfo() {
        return tripInfo;
    }

    public void setTripInfo(String tripInfo) {
        this.tripInfo = tripInfo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isWillNotify() {
        return willNotify;
    }

    public void setWillNotify(boolean willNotify) {
        this.willNotify = willNotify;
    }

    public static ScheduleEntity getCopyInstance(@NonNull ScheduleEntity scheduleEntity) {
        return new ScheduleEntity(scheduleEntity.pos, scheduleEntity.tripTheme, scheduleEntity.tripInfo, scheduleEntity.note, scheduleEntity.willNotify);
    }

}
