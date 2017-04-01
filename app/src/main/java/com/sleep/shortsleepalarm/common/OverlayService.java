package com.sleep.shortsleepalarm.common;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.activity.MainActivity;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aravindraj on 1/1/2017.
 */

public class OverlayService extends Service {

    AlarmManager am;
    DatabaseHandler db;
    WindowManager mWindowManager;
    WindowManager.LayoutParams mLayoutParams;
    View mView;
    AlarmModel alarmModel;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        am = (AlarmManager) getSystemService(ALARM_SERVICE);
        db = new DatabaseHandler(this);
        String message = intent.getStringExtra("ALARM_ID");
        String reqcode = intent.getStringExtra("REQUEST_CODE");
        Log.e("CreateAlarmFragment","receiverid="+message);
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int dayofweek = c.get(Calendar.DAY_OF_WEEK);
        if (message != null && reqcode != null) {
            alarmModel = db.getAlarmModel(Integer.parseInt(message));
            alarmModel.setIsCompleted(1);
            alarmModel.setDay(day);
            alarmModel.setMonth(month);
            alarmModel.setYear(year);
            alarmModel.setDayofweek(dayofweek);
            if (((EverydayApplication.getInstance().mp != null && EverydayApplication.getInstance().mp.isPlaying())||((EverydayApplication.getInstance().player != null && EverydayApplication.getInstance().player.isPlaying())))) {
                if (alarmModel.getType().equals("a")) {
                    if (alarmModel.getRepeat().equals("o")) {
                        alarmModel.setIsEnabled(0);
                        db.updateAlarmModel(alarmModel);
                        db.addCompletedAlarmModel(alarmModel);

                    }
                } else {
                    alarmModel.setIsEnabled(0);
                    db.updateAlarmModel(alarmModel);
                    db.addCompletedAlarmModel(alarmModel);

                }
            } else {
                EverydayApplication.getInstance().play(getApplicationContext(), alarmModel);
                showDialog(alarmModel);
            }
        }
        return START_NOT_STICKY;
    }

    private void showDialog(final AlarmModel amod) {
        mWindowManager = null;
        mView = null;
        mView = View.inflate(getApplicationContext(), R.layout.dialog_shout, null);
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        TextView alarmtime = (TextView) mView.findViewById(R.id.alarmtime);
        TextView alarmname = (TextView) mView.findViewById(R.id.alarmname);
        String _24HourTime = "" + amod.getHour() + ":" + amod.getMinute();
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (alarmModel.getType().equals("sa")) {

            int hours = alarmModel.getHour(); //since both are ints, you get an int
            int minutes = alarmModel.getMinute();


            String s = String.format("%02d hr %02d min", hours, minutes);

            alarmtime.setText(s);
        } else
            alarmtime.setText("" + _12HourSDF.format(_24HourDt));
        alarmname.setText(amod.getName());
        Button snooze = (Button) mView.findViewById(R.id.snooze);
        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EverydayApplication.getInstance().setSleepAlarm(0,null,getApplicationContext(), amod, Integer.parseInt(amod.getId() + "88"), 1);
                onDestroy();
                mView.setVisibility(View.GONE);
                EverydayApplication.getInstance().stopMusic();
            }
        });
        Button dismiss = (Button) mView.findViewById(R.id.dismiss);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent iu = new Intent(getBaseContext(), MainActivity.class);
                iu.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(iu);
                onDestroy();
                mView.setVisibility(View.GONE);
                if (amod.getType().equals("a")) {
                    if (amod.getRepeat().equals("o")) {
                        amod.setIsEnabled(0);
                        db.updateAlarmModel(amod);
                        db.addCompletedAlarmModel(alarmModel);

                    }
                } else {
                    amod.setIsEnabled(0);
                    db.updateAlarmModel(amod);
                    db.addCompletedAlarmModel(alarmModel);
                }
                EverydayApplication.getInstance().stopMusic();


            }
        });
        mLayoutParams = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG,
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                , PixelFormat.RGBA_8888);
        mLayoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        mLayoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mLayoutParams.x = 100;
        mLayoutParams.y = 100;
        mView.setLayoutParams(mLayoutParams);

        mView.setVisibility(View.VISIBLE);
        mWindowManager.addView(mView, mLayoutParams);
    }


}