package com.sleep.shortsleepalarm.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Aravindraj on 9/6/2016.
 */
public class AlarmModel implements Parcelable {

    int hour=0;
    int minute=0;
    int isEnabled;
    String type;
    String name;
    String tone="";
    String toneurl="";
    int isCompleted=0;
    int day=0;
    int month=0;
    int year=0;
    int dayofweek=0;
    public long expirationTime;

    public long getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(long expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getToneurl() {
        return toneurl;
    }

    public void setToneurl(String toneurl) {
        this.toneurl = toneurl;
    }

    int id;

    public String getTone() {
        return tone;
    }

    public void setTone(String tone) {
        this.tone = tone;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    String repeat;

    protected AlarmModel(Parcel in) {
        hour = in.readInt();
        minute = in.readInt();
        isEnabled = in.readInt();
        type = in.readString();
        name = in.readString();
        id = in.readInt();
        sleepTime = in.readInt();
        repeat = in.readString();
        tone = in.readString();
        toneurl = in.readString();
    }

    public static final Creator<AlarmModel> CREATOR = new Creator<AlarmModel>() {
        @Override
        public AlarmModel createFromParcel(Parcel in) {
            return new AlarmModel(in);
        }

        @Override
        public AlarmModel[] newArray(int size) {
            return new AlarmModel[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    int sleepTime=0;

    public AlarmModel() {
    }

    public AlarmModel(int id,String name, int hour, int minute,int ischecked,String repeat,String tones,String toneurls) {
        this.id = id;
        this.name=name;
        this.hour = hour;
        this.minute = minute;
        this.isEnabled=ischecked;
        this.repeat=repeat;
        this.tone=tones;
        this.toneurl=toneurls;
    }
    public AlarmModel(int id,String name, int hour, int minute,int ischecked,String repeat,String type,String tones,String toneurls) {
        this.id = id;
        this.name=name;
        this.hour = hour;
        this.minute = minute;
        this.isEnabled=ischecked;
        this.repeat=repeat;
        this.type=type;
        this.tone=tones;
        this.toneurl=toneurls;
    }

    public AlarmModel(int id,String name, int hour, int minute,int ischecked,String repeat,String type,String tones,String toneurls,int day,int month , int year , int dayofweek) {
        this.id = id;
        this.name=name;
        this.hour = hour;
        this.minute = minute;
        this.isEnabled=ischecked;
        this.repeat=repeat;
        this.type=type;
        this.tone=tones;
        this.toneurl=toneurls;
        this.day=day;
        this.month=month;
        this.year=year;
        this.dayofweek=dayofweek;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int isEnabled() {
        return isEnabled;
    }

    public int getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(int isCompleted) {
        this.isCompleted = isCompleted;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getDayofweek() {
        return dayofweek;
    }

    public void setDayofweek(int dayofweek) {
        this.dayofweek = dayofweek;
    }

    public void setIsEnabled(int isEnabled) {
        this.isEnabled = isEnabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minute);
        dest.writeInt(isEnabled);
        dest.writeString(type);
        dest.writeString(name);
        dest.writeInt(id);
        dest.writeInt(sleepTime);
        dest.writeString(repeat);
        dest.writeString(tone);
        dest.writeString(toneurl);
    }
}
