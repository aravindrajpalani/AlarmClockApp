package com.sleep.shortsleepalarm.common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;
import java.util.ArrayList;

/**
 * Created by Aravindraj on 1/21/2017.
 */

public class BootReceiver extends BroadcastReceiver {
    DatabaseHandler mDb;

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        mDb = new DatabaseHandler(context);
        ArrayList<AlarmModel> alarmarray = mDb.getAllAlarmModels();
        for (int i = 0; i < alarmarray.size(); i++) {
            if(alarmarray.get(i).isEnabled()==1)
                EverydayApplication.getInstance().doCreateAlarm(0,null,context,alarmarray.get(i));
        }

        ArrayList<AlarmModel> sleepalarmarray = mDb.getAllSleepAlarmModels();
        for (int j = 0; j < sleepalarmarray.size(); j++) {
            if(sleepalarmarray.get(j).isEnabled()==1)
                EverydayApplication.getInstance().setSleepAlarm(0,null,context, sleepalarmarray.get(j), sleepalarmarray.get(j).getId(),0);
        }

    }
}