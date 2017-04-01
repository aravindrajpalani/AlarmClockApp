package com.sleep.shortsleepalarm.common;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.activity.MainActivity;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by Aravindraj on 12/13/2016.
 */
public class EverydayApplication extends Application {

    public static EverydayApplication mEverydayapp;
    MediaPlayer mp;
    SharedPreferences prefs;
    MediaPlayer player;


    @Override
    public void onCreate() {
        super.onCreate();
        mEverydayapp = this;
    }

    public static synchronized EverydayApplication getInstance() {
        if (mEverydayapp == null)
            mEverydayapp = new EverydayApplication();
        return mEverydayapp;
    }

    public void stopMusic() {
        if (mp.isPlaying())
            mp.stop();
        if (player.isPlaying())
            player.stop();

    }

    public Bitmap loadBitmapFromView(View view, int width, int height) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);

        Log.e("width", "=" + width);
        Log.e("height", "=" + height);
        return returnedBitmap;
    }

    public void play(Context c, AlarmModel alarmModel) {
        mp = new MediaPlayer();
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        player = MediaPlayer.create(c, notification);
        prefs = c.getSharedPreferences("MYPREFNAME", Context.MODE_PRIVATE);
        final AudioManager am = (AudioManager) c.getSystemService(Context.AUDIO_SERVICE);
        final int originalVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, prefs.getInt("volume", 10), 0);
        int result = am.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED && alarmModel.getToneurl().length() > 2) {
            // Start playback.
            String fileInfo = "";
            mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (alarmModel.getToneurl().startsWith("content://")) {
                try {
                    Uri uri = Uri.parse(alarmModel.getToneurl());
                    fileInfo = getRingtonePathFromContentUri(c, uri);
                    Log.e("onesuccess1", "=");
                } catch (Exception e) {
                    Log.e("ioexceptione", "=" + e.getMessage());
                }
            }
            try {
                Log.e("onesuccess2", "=");
                setMediaPlayerDataSourcePostHoneyComb(c, mp, fileInfo);
            } catch (Exception e) {
                Log.e("ioexception", "=" + e.getMessage());
                try {
                    Log.e("onesuccess21", "=");
                    setMediaPlayerDataSourceUsingFileDescriptor(c, mp,
                            fileInfo);
                } catch (Exception ee) {
                    Log.e("onesuccess22", "=");
                    Log.e("ioexceptionee", "=" + ee.getMessage());
                    String uri = getRingtoneUriFromPath(c, fileInfo);
                    mp.reset();
                    try {
                        Log.e("onesuccess23", "=");
                        mp.setDataSource(uri);
                    } catch (IOException e1) {
                        Log.e("onesuccess24", "=");
                        Log.e("ioexceptione1", "=" + e1.getMessage());
                        e1.printStackTrace();
                    }
                }
            }

            mp.prepareAsync();
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mpe) {
                    mpe.start();
                }
            });

            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                }
            });
        } else if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            player.start();
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    am.setStreamVolume(AudioManager.STREAM_MUSIC, originalVolume, 0);
                }
            });
        }


    }

    public boolean isOnline(Context con) {
        ConnectivityManager cm =
                (ConnectivityManager) con.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setSleepAlarm(int snakFlag, CoordinatorLayout coord, Context context, AlarmModel amod, int requestcode, int isSnooze) {

        int min = 0;
        AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OverlayService.class);
        i.putExtra("ALARM_ID", "" + amod.getId());
        i.putExtra("REQUEST_CODE", "" + requestcode);

        if (isSnooze == 0) {
            setNotifications(context);
            PendingIntent pi = PendingIntent.getService(context, requestcode, i, PendingIntent.FLAG_CANCEL_CURRENT);
            min = (amod.getHour() * 60) + amod.getMinute();
            amgr.set(amgr.RTC, System.currentTimeMillis() + (min * 60 * 1000), pi);
            if (coord != null) {
                Snackbar snackbar = Snackbar.make(coord, "Sleep Alarm set for " + amod.getHour() + " hr " + amod.getMinute() + " min from now", Snackbar.LENGTH_LONG);
                snackbar.show();
            } else if (snakFlag == 1)
                Toast.makeText(context, "Sleep Alarm set for " + amod.getHour() + " hr " + amod.getMinute() + " min from now", Toast.LENGTH_SHORT).show();


        }
        if (isSnooze == 1) {
            PendingIntent pi = PendingIntent.getService(context, Integer.parseInt(requestcode + "88"), i, 0);
            prefs = context.getSharedPreferences("MYPREFNAME", Context.MODE_PRIVATE);
            min = prefs.getInt("snooze", 5);
            amgr.set(amgr.RTC, System.currentTimeMillis() + (min * 60 * 1000), pi);
            Log.e("CreateAlarmFragment", "snooze=" + min);
        }

    }

    public void setNotifications(Context context) {


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent fintent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, 111, fintent, 0);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_alarm_icon)
                .setContentTitle("Everyday Alarm Clock")
                .setOngoing(true);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(111, mBuilder.build());

    }


    public static String getRingtonePathFromContentUri(Context context,
                                                       Uri contentUri) {
        Log.e("onesuccess007", "=");
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor ringtoneCursor = context.getContentResolver().query(contentUri,
                proj, null, null, null);
        ringtoneCursor.moveToFirst();

        String path = ringtoneCursor.getString(ringtoneCursor
                .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));

        ringtoneCursor.close();
        return path;
    }


    private static void setMediaPlayerDataSourcePostHoneyComb(Context context,
                                                              MediaPlayer mp, String fileInfo) {
        mp.reset();
        try {

            mp.setDataSource(context, Uri.parse(fileInfo));
            Log.e("onesuccess3", "=");
        } catch (IOException e) {
            Log.e("onesuccess32", "=");
            Log.e("ioexceptioner", "=" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void setMediaPlayerDataSourceUsingFileDescriptor(
            Context context, MediaPlayer mp, String fileInfo) throws Exception {
        File file = new File(fileInfo);
        Log.e("onesuccess", "=");
        FileInputStream inputStream = new FileInputStream(file);
        mp.reset();
        mp.setDataSource(inputStream.getFD());
        inputStream.close();
    }

    private static String getRingtoneUriFromPath(Context context, String path) {
        Uri ringtonesUri = MediaStore.Audio.Media.getContentUriForPath(path);
        Cursor ringtoneCursor = context.getContentResolver().query(
                ringtonesUri, null,
                MediaStore.Audio.Media.DATA + "='" + path + "'", null, null);
        ringtoneCursor.moveToFirst();
        Log.e("onesuccess", "=");
        long id = ringtoneCursor.getLong(ringtoneCursor
                .getColumnIndex(MediaStore.Audio.Media._ID));
        ringtoneCursor.close();

        if (!ringtonesUri.toString().endsWith(String.valueOf(id))) {
            return ringtonesUri + "/" + id;
        }
        return ringtonesUri.toString();
    }


    public String doCreateAlarm(int snakFlag, CoordinatorLayout mcor, Context con, AlarmModel amod) {
        String time = calculateTime(amod);
        setNotifications(con);
        if (amod.getRepeat() != null) {
            if (amod.getRepeat().equals("o")) {
                StringBuilder sb = new StringBuilder();
                sb.append("" + amod.getId());
                sb.append("00");
                setAlarm(con, amod, Integer.parseInt(sb.toString()), 0, 0);
            } else {
                if (amod.getRepeat().charAt(0) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("01");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 2);

                }
                if (amod.getRepeat().charAt(1) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("02");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 3);
                }
                if (amod.getRepeat().charAt(2) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("03");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 4);
                }
                if (amod.getRepeat().charAt(3) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("04");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 5);

                }
                if (amod.getRepeat().charAt(4) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("05");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 6);
                }
                if (amod.getRepeat().charAt(5) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("06");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 7);
                }
                if (amod.getRepeat().charAt(6) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("07");
                    setAlarm(con, amod, Integer.parseInt(sb.toString()), 1, 1);
                }
            }
        }

        if (mcor != null) {
            Snackbar snackbar = Snackbar.make(mcor, "Alarm set for " + time + " from now", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else if (snakFlag == 1)
            Toast.makeText(con, "Alarm set for " + time + " from now", Toast.LENGTH_SHORT).show();

        return time;
    }

    public String calculateTime(AlarmModel amod) {
        int repeat = 0;
        int week = 0;
        String time = "";
        if (amod.getRepeat() != null) {
            if (amod.getRepeat().equals("o")) {
                repeat = 0;
                week = 0;
                Calendar cal = findCalendarTime(amod, repeat, week);
                Calendar curr = Calendar.getInstance();
                long millis = cal.getTimeInMillis() - curr.getTimeInMillis();
                if (TimeUnit.MILLISECONDS.toHours(millis) != 0)
                    time = String.format("%02d hr %02d min %02d sec", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                else
                    time = String.format("%02d min %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

            } else {
                ArrayList<Calendar> calaarr = new ArrayList<>();

                if (amod.getRepeat().charAt(0) == 't') {
                    repeat = 1;
                    week = 2;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(1) == 't') {
                    repeat = 1;
                    week = 3;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(2) == 't') {
                    repeat = 1;
                    week = 4;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(3) == 't') {
                    repeat = 1;
                    week = 5;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(4) == 't') {
                    repeat = 1;
                    week = 6;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(5) == 't') {
                    repeat = 1;
                    week = 7;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                if (amod.getRepeat().charAt(6) == 't') {
                    repeat = 1;
                    week = 1;
                    calaarr.add(findCalendarTime(amod, repeat, week));
                }
                Calendar smallest = calaarr.get(0);
                for (int r = 0; r < calaarr.size(); r++) {
                    if (calaarr.get(r).compareTo(smallest) < 0)
                        smallest = calaarr.get(r);
                }

                Calendar curr = Calendar.getInstance();
                long millis = smallest.getTimeInMillis() - curr.getTimeInMillis();
                if (TimeUnit.MILLISECONDS.toHours(millis) != 0) {
                    if (TimeUnit.MILLISECONDS.toHours(millis) < 24) {
                        time = String.format("%02d hr %02d min %02d sec", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    } else {
                        long seconds = millis / 1000;
                        long minutes = seconds / 60;
                        long hours = minutes / 60;
                        long days = hours / 24;
                        time = days + " day " + hours % 24 + " hr " + minutes % 60 + " min " + seconds % 60 + " sec";
                    }
                } else
                    time = String.format("%02d min %02d sec",
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
            }
        }


        return time;
    }

    private Calendar findCalendarTime(AlarmModel am, int repeat, int week) {
        Calendar calSet = Calendar.getInstance();
        calSet.setFirstDayOfWeek(Calendar.SUNDAY);
        calSet.setTimeInMillis(System.currentTimeMillis());

        calSet.set(Calendar.HOUR_OF_DAY, am.getHour());
        calSet.set(Calendar.MINUTE, am.getMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        if (repeat == 1) {
            calSet.set(Calendar.DAY_OF_WEEK, week);
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 7);
            }
        } else {
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 1);
            }
        }

        return calSet;
    }


    public void doCancelAlarm(Context con, AlarmModel amod) {

        if (amod.getRepeat() != null) {
            if (amod.getRepeat().equals("o")) {
                StringBuilder sb = new StringBuilder();
                sb.append("" + amod.getId());
                sb.append("00");
                cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
            } else {

                if (amod.getRepeat().charAt(0) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("01");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));

                }
                if (amod.getRepeat().charAt(1) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("02");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
                }
                if (amod.getRepeat().charAt(2) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("03");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
                }
                if (amod.getRepeat().charAt(3) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("04");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));

                }
                if (amod.getRepeat().charAt(4) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("05");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
                }
                if (amod.getRepeat().charAt(5) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("06");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
                }
                if (amod.getRepeat().charAt(6) == 't') {
                    StringBuilder sb = new StringBuilder();
                    sb.append("" + amod.getId());
                    sb.append("07");
                    cancelAlarm(con, amod, Integer.parseInt(sb.toString()));
                }
            }

        }
    }


    public void setAlarm(Context context, AlarmModel am, int requestcode, int repeat, int week) {
        AlarmManager amgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, OverlayService.class);
        Log.e("CreateAlarmFragment", "setAlarm" + am.getId());
        Log.e("CreateAlarmFragment", "setAlarm" + requestcode);
        i.putExtra("ALARM_ID", "" + am.getId());
        i.putExtra("REQUEST_CODE", "" + requestcode);
        Calendar calSet = Calendar.getInstance();
        calSet.setFirstDayOfWeek(Calendar.SUNDAY);
        calSet.setTimeInMillis(System.currentTimeMillis());

        calSet.set(Calendar.HOUR_OF_DAY, am.getHour());
        calSet.set(Calendar.MINUTE, am.getMinute());
        calSet.set(Calendar.SECOND, 0);
        calSet.set(Calendar.MILLISECOND, 0);

        PendingIntent pi = PendingIntent.getService(context, requestcode, i, 0);
        if (repeat == 1) {
            calSet.set(Calendar.DAY_OF_WEEK, week);
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 7);
            }
            amgr.setRepeating(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), 24 * 60 * 60 * 1000, pi);
        } else {
            if (calSet.before(Calendar.getInstance())) {
                calSet.add(Calendar.DATE, 1);
            }
            amgr.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(), pi);
        }

    }

    public void cancelAlarm(Context context, AlarmModel am, int requestcode) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        DatabaseHandler mdb = new DatabaseHandler(context);
        Intent intent = new Intent(context, OverlayService.class);
        PendingIntent sender = PendingIntent.getService(context, requestcode, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
        Log.e("reqcode", "" + requestcode);
        ArrayList<AlarmModel> mar = mdb.getAllEnabledAlarmModels();
        ArrayList<AlarmModel> msar = mdb.getAllEnabledSleepAlarmModels();
        if (mar.size() == 0 && msar.size() == 0)
            mNotificationManager.cancel(111);
    }


}
