package com.sleep.shortsleepalarm.database;

/**
 * Created by Aravindraj on 12/6/2016.
 */


import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sleep.shortsleepalarm.model.AlarmModel;

public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "alarmDB";
    private static final String TABLE_ALARMS = "alarmtable";
    private static final String TABLE_COMPLETED_ALARMS = "completedalarmtable";
    private static final String ALARM_ID = "id";
    private static final String OLD_ALARM_ID = "oldid";
    private static final String ALARM_NAME = "name";
    private static final String ALARM_HOUR = "hour";
    private static final String ALARM_MIN = "minute";
    private static final String ALARM_ENABLED = "enabled";
    private static final String ALARM_COMPLETED = "alarmcompleted";
    private static final String ALARM_DAY = "day";
    private static final String ALARM_MONTH = "month";
    private static final String ALARM_YEAR = "year";
    private static final String ALARM_DAY_OF_WEEK = "dayofweek";
    private static final String ALARM_REPEAT = "repeat";
    private static final String ALARM_TYPE = "type";
    private static final String ALARM_TONE = "tone";
    private static final String ALARM_TONE_URL = "toneurl";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ALARMS_TABLE = "CREATE TABLE " + TABLE_ALARMS + "("
                + ALARM_ID + " INTEGER PRIMARY KEY ," + ALARM_NAME + " TEXT,"
                + ALARM_HOUR + " INTEGER," + ALARM_MIN + " INTEGER," + ALARM_ENABLED + " INTEGER," + ALARM_REPEAT + " TEXT," + ALARM_TYPE + " TEXT," + ALARM_TONE + " TEXT," + ALARM_TONE_URL + " TEXT," + ALARM_COMPLETED + " INTEGER," + ALARM_DAY + " INTEGER," + ALARM_MONTH + " INTEGER," + ALARM_YEAR + " INTEGER," + ALARM_DAY_OF_WEEK + " INTEGER" + ")";
        String CREATE_COMPLETED_ALARMS_TABLE = "CREATE TABLE " + TABLE_COMPLETED_ALARMS + "("
                + ALARM_ID + " INTEGER ," + ALARM_NAME + " TEXT,"
                + ALARM_HOUR + " INTEGER," + ALARM_MIN + " INTEGER," + ALARM_ENABLED + " INTEGER," + ALARM_REPEAT + " TEXT," + ALARM_TYPE + " TEXT," + ALARM_TONE + " TEXT," + ALARM_TONE_URL + " TEXT," + ALARM_COMPLETED + " INTEGER," + ALARM_DAY + " INTEGER," + ALARM_MONTH + " INTEGER," + ALARM_YEAR + " INTEGER," + ALARM_DAY_OF_WEEK + " INTEGER" + ")";

        db.execSQL(CREATE_ALARMS_TABLE);
        db.execSQL(CREATE_COMPLETED_ALARMS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ALARMS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new alarmobj
    public void addAlarmModel(AlarmModel alarmobj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmobj.getName()); // AlarmModel Name
        values.put(ALARM_HOUR, alarmobj.getHour()); // AlarmModel Phone
        values.put(ALARM_MIN, alarmobj.getMinute());
        values.put(ALARM_ENABLED, alarmobj.isEnabled());
        values.put(ALARM_REPEAT, alarmobj.getRepeat());
        values.put(ALARM_TYPE, alarmobj.getType());
        values.put(ALARM_TONE, alarmobj.getTone());
        values.put(ALARM_TONE_URL, alarmobj.getToneurl());
        values.put(ALARM_COMPLETED, alarmobj.getIsCompleted());
        values.put(ALARM_DAY, alarmobj.getDay());
        values.put(ALARM_MONTH, alarmobj.getMonth());
        values.put(ALARM_YEAR, alarmobj.getYear());
        values.put(ALARM_DAY_OF_WEEK, alarmobj.getDayofweek());
        // Inserting Row
        db.insert(TABLE_ALARMS, null, values);
        db.close(); // Closing database connection
    }

    public void addCompletedAlarmModel(AlarmModel alarmobj) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ALARM_ID, alarmobj.getId());
        values.put(ALARM_NAME, alarmobj.getName()); // AlarmModel Name
        values.put(ALARM_HOUR, alarmobj.getHour()); // AlarmModel Phone
        values.put(ALARM_MIN, alarmobj.getMinute());
        values.put(ALARM_ENABLED, alarmobj.isEnabled());
        values.put(ALARM_REPEAT, alarmobj.getRepeat());
        values.put(ALARM_TYPE, alarmobj.getType());
        values.put(ALARM_TONE, alarmobj.getTone());
        values.put(ALARM_TONE_URL, alarmobj.getToneurl());
        values.put(ALARM_COMPLETED, alarmobj.getIsCompleted());
        values.put(ALARM_DAY, alarmobj.getDay());
        values.put(ALARM_MONTH, alarmobj.getMonth());
        values.put(ALARM_YEAR, alarmobj.getYear());
        values.put(ALARM_DAY_OF_WEEK, alarmobj.getDayofweek());
        // Inserting Row
        db.insert(TABLE_COMPLETED_ALARMS, null, values);
        db.close(); // Closing database connection
    }


    // Getting single alarmobj
    public AlarmModel getAlarmModel(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ALARMS, new String[]{ALARM_ID,
                ALARM_NAME, ALARM_HOUR, ALARM_MIN, ALARM_ENABLED, ALARM_REPEAT, ALARM_TYPE, ALARM_TONE, ALARM_TONE_URL}, ALARM_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        AlarmModel alarmobj = new AlarmModel(cursor.getInt(0), cursor.getString(1), cursor.getInt(2), cursor.getInt(3), cursor.getInt(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));
        return alarmobj;
    }

    public ArrayList<AlarmModel> getAllAlarmModels() {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE " + ALARM_TYPE + " = 'a'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));
                Log.e("id=", "=" + cursor.getInt(0));
                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }


    public ArrayList<AlarmModel> getAllEnabledAlarmModels() {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE " + ALARM_TYPE + " = 'a' AND " + ALARM_ENABLED + " = 1 ";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));
                Log.e("id=", "=" + cursor.getInt(0));
                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }


    public ArrayList<AlarmModel> getCompletedMonthModels(int month, int year) {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        String selectQuery = "SELECT  * FROM " + TABLE_COMPLETED_ALARMS + " WHERE " + ALARM_COMPLETED + " = 1 AND " + ALARM_MONTH + " = " + month + " AND " + ALARM_YEAR + " = " + year;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));
                alarmobj.setIsCompleted(cursor.getInt(9));
                alarmobj.setDay(cursor.getInt(10));
                alarmobj.setMonth(cursor.getInt(11));
                alarmobj.setYear(cursor.getInt(12));
                alarmobj.setDayofweek(cursor.getInt(13));
                Log.e("id=", "=" + cursor.getInt(0));
                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }


    public ArrayList<AlarmModel> getAllSleepAlarmModels() {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE " + ALARM_TYPE + " = 'sa'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));

                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }

    public ArrayList<AlarmModel> getAllEnabledSleepAlarmModels() {
        ArrayList<AlarmModel> alarmobjList = new ArrayList<AlarmModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS + " WHERE " + ALARM_TYPE + " = 'sa' AND " + ALARM_ENABLED + " = 1 ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                AlarmModel alarmobj = new AlarmModel();
                alarmobj.setId(cursor.getInt(0));
                alarmobj.setName(cursor.getString(1));
                alarmobj.setHour(cursor.getInt(2));
                alarmobj.setMinute(cursor.getInt(3));
                alarmobj.setIsEnabled(cursor.getInt(4));
                alarmobj.setRepeat(cursor.getString(5));
                alarmobj.setType(cursor.getString(6));
                alarmobj.setTone(cursor.getString(7));
                alarmobj.setToneurl(cursor.getString(8));

                // Adding alarmobj to list
                alarmobjList.add(alarmobj);
            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return alarmobjList;
    }

    public int getId() {
        String selectQuery = "SELECT  * FROM " + TABLE_ALARMS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        int id = 1;
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                id = cursor.getInt(0);

            } while (cursor.moveToNext());
        }

        // return alarmobj list
        return id;
    }

    // Updating single alarmobj
    public int updateAlarmModel(AlarmModel alarmobj) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ALARM_NAME, alarmobj.getName());
        values.put(ALARM_HOUR, alarmobj.getHour());
        values.put(ALARM_MIN, alarmobj.getMinute());
        values.put(ALARM_ENABLED, alarmobj.isEnabled());
        values.put(ALARM_REPEAT, alarmobj.getRepeat());
        values.put(ALARM_TYPE, alarmobj.getType());
        values.put(ALARM_TONE, alarmobj.getTone());
        values.put(ALARM_TONE_URL, alarmobj.getToneurl());
        values.put(ALARM_COMPLETED, alarmobj.getIsCompleted());
        values.put(ALARM_DAY, alarmobj.getDay());
        values.put(ALARM_MONTH, alarmobj.getMonth());
        values.put(ALARM_YEAR, alarmobj.getYear());
        values.put(ALARM_DAY_OF_WEEK, alarmobj.getDayofweek());
        // updating row
        return db.update(TABLE_ALARMS, values, ALARM_ID + " = ?",
                new String[]{String.valueOf(alarmobj.getId())});
    }


    // Deleting single alarmobj
    public void deleteAlarmModel(AlarmModel alarmobj) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ALARMS, ALARM_ID + " = ?",
                new String[]{String.valueOf(alarmobj.getId())});
        db.close();
    }


    // Getting alarmtable Count
    public int getAlarmModelsCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ALARMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        // return count
        return cursor.getCount();
    }

}