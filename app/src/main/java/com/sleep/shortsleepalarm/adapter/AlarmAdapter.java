package com.sleep.shortsleepalarm.adapter;

/**
 * Created by Aravindraj on 9/6/2016.
 */

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.listeners.EditAlarmListener;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.MyViewHolder> {

    private List<AlarmModel> alarmList;
    private EditAlarmListener editListener;
    public Context context;
    private List<MyViewHolder> lstHolders = null;
    AlarmModel list;
    private Handler mHandler = new Handler();
    private Runnable updateRemainingTimeRunnable = new Runnable() {
        @Override
        public void run() {
            synchronized (lstHolders) {
                long currentTime = System.currentTimeMillis();
                for (MyViewHolder holder : lstHolders) {
                    holder.updateTimeRemaining(currentTime);
                }
            }
        }
    };

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

    String calculateTime(AlarmModel amod) {
        long milliii = 0;
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
                milliii = millis;
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
                milliii = millis;

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


        return time + " Remaining";
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView time, name, repeat, remaining;
        public CardView card;
        public ImageView settingsImg, sleepBody, sleepBed, screenshot;
        public SwitchCompat switchcom;
        AlarmModel mModel;

        public void setData(AlarmModel item) {
            mModel = item;

            updateTimeRemaining(System.currentTimeMillis());
        }

        public void updateTimeRemaining(long currentTime) {


            remaining.setText(calculateTime(mModel));

        }

        public MyViewHolder(View view) {
            super(view);

            time = (TextView) view.findViewById(R.id.time);
            name = (TextView) view.findViewById(R.id.name);
            remaining = (TextView) view.findViewById(R.id.remaining);
            repeat = (TextView) view.findViewById(R.id.repeat);
            card = (CardView) view.findViewById(R.id.card_view);
            settingsImg = (ImageView) view.findViewById(R.id.settings);
            sleepBody = (ImageView) view.findViewById(R.id.sleepbody);
            sleepBed = (ImageView) view.findViewById(R.id.sleepbed);
            screenshot = (ImageView) view.findViewById(R.id.screenshot);
            switchcom = (SwitchCompat) view.findViewById(R.id.switch_compat);

        }


    }


    public AlarmAdapter(Context con, List<AlarmModel> list, EditAlarmListener elis) {
        this.alarmList = list;
        this.editListener = elis;
        this.context = con;
        lstHolders = new ArrayList<>();
        startUpdateTimer();
    }

    private void startUpdateTimer() {
        Timer tmr = new Timer();
        tmr.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.post(updateRemainingTimeRunnable);
            }
        }, 1, 1000);

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_alarm_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setData(alarmList.get(position));
        synchronized (lstHolders) {
            lstHolders.add(holder);
        }
        holder.updateTimeRemaining(System.currentTimeMillis());
        String _24HourTime = "" + alarmList.get(position).getHour() + ":" + alarmList.get(position).getMinute();
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");
        Log.e("ALARMIDDB", "=" + alarmList.get(position).getId());
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (alarmList.get(position).isEnabled() == 1) {
            holder.remaining.setVisibility(View.VISIBLE);

        } else {
            holder.remaining.setVisibility(View.GONE);
        }
        holder.time.setText("" + _12HourSDF.format(_24HourDt));
        holder.name.setText(alarmList.get(position).getName());
        holder.repeat.setText(alarmList.get(position).getRepeat());
        if (alarmList.get(position).isEnabled() == 1) {
            holder.switchcom.setChecked(true);
            holder.sleepBody.setVisibility(View.VISIBLE);
            holder.sleepBed.setVisibility(View.VISIBLE);
        } else {
            holder.switchcom.setChecked(false);
            holder.sleepBody.setVisibility(View.GONE);
            holder.sleepBed.setVisibility(View.GONE);
        }

        holder.card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.settingsImg);
                //inflating menu from xml resource
                popup.inflate(R.menu.context_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                //handle menu1 click
                                editListener.onLongClick(1, alarmList.get(position));
                                break;
                            case R.id.menu2:

                                holder.screenshot.setImageBitmap(EverydayApplication.getInstance().loadBitmapFromView(holder.card, holder.card.getWidth(), holder.card.getHeight()));
                                holder.screenshot.setVisibility(View.VISIBLE);
                                Animation anim = AnimationUtils.loadAnimation(context, R.anim.carddelanim);
                                holder.screenshot.startAnimation(anim);
                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                        holder.card.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        holder.card.setVisibility(View.VISIBLE);
                                        holder.screenshot.setVisibility(View.GONE);
                                        editListener.onLongClick(2, alarmList.get(position));

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });


                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

                return false;
            }
        });
        holder.settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context, holder.settingsImg);
                //inflating menu from xml resource
                popup.inflate(R.menu.context_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                //handle menu1 click
                                editListener.onSettingsClick(1, alarmList.get(position));
                                break;
                            case R.id.menu2:
                                //handle menu2 click

                                holder.screenshot.setImageBitmap(EverydayApplication.getInstance().loadBitmapFromView(holder.card, holder.card.getWidth(), holder.card.getHeight()));
                                holder.screenshot.setVisibility(View.VISIBLE);
                                Animation anim = AnimationUtils.loadAnimation(context, R.anim.carddelanim);
                                holder.screenshot.startAnimation(anim);
                                anim.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                        holder.card.setVisibility(View.GONE);

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        holder.card.setVisibility(View.VISIBLE);
                                        holder.screenshot.setVisibility(View.GONE);
                                        editListener.onSettingsClick(2, alarmList.get(position));


                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });

                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
        String a = null;
        if (alarmList.get(position).getRepeat() != null) {
            if (alarmList.get(position).getRepeat().length() == 1) {
                if (alarmList.get(position).getRepeat().equals("o"))
                    holder.repeat.setText("Once");
                holder.repeat.setTextColor(context.getResources().getColor(R.color.grey2));
            } else {
                for (int i = 0; i < alarmList.get(position).getRepeat().length(); i++) {
                    String h = alarmList.get(position).getRepeat();

                    switch (i) {
                        case 0:
                            if (h.charAt(i) == 't')
                                a = "<font color='#ff4e4a'>MO </font>";
                            else
                                a = "MO ";
                            break;
                        case 1:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>TU </font>";
                            else
                                a = a + "TU ";
                            break;
                        case 2:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>WE </font>";
                            else
                                a = a + "WE ";
                            break;
                        case 3:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>TH </font>";
                            else
                                a = a + "TH ";
                            break;
                        case 4:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>FR </font>";
                            else
                                a = a + "FR ";
                            break;
                        case 5:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>SA </font>";
                            else
                                a = a + "SA ";
                            break;
                        case 6:
                            if (h.charAt(i) == 't')
                                a = a + "<font color='#ff4e4a'>SU </font>";
                            else
                                a = a + "SU ";
                            break;


                    }
                }
                holder.repeat.setText(Html.fromHtml(a));
            }

        }


        holder.switchcom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editListener.onCheckedChange(isChecked, alarmList.get(position));
                if (isChecked) {
                    holder.sleepBed.setVisibility(View.VISIBLE);
                    Animation anim = AnimationUtils.loadAnimation(context, R.anim.sleepbedanim);
                    holder.sleepBed.startAnimation(anim);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            holder.sleepBody.setVisibility(View.VISIBLE);
                            Animation anim2 = AnimationUtils.loadAnimation(context, R.anim.sleepbodyanim);
                            holder.sleepBody.startAnimation(anim2);


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                    holder.remaining.setVisibility(View.VISIBLE);

                } else {
                    holder.sleepBed.setVisibility(View.GONE);
                    holder.sleepBody.setVisibility(View.GONE);
                    holder.remaining.setVisibility(View.GONE);

                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}