package com.sleep.shortsleepalarm.listeners;

import com.sleep.shortsleepalarm.model.AlarmModel;

/**
 * Created by Aravindraj on 12/6/2016.
 */
public interface EditAlarmListener {
    public void onLongClick(int flag,AlarmModel am);
    public void onSettingsClick(int flag,AlarmModel am);

    void onCheckedChange(boolean isChecked,AlarmModel am);
}
