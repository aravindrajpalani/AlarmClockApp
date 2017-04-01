package com.sleep.shortsleepalarm.adapter;

/**
 * Created by Aravindraj on 9/6/2016.
 */

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
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

import java.util.List;

public class SleepAlarmAdapter extends RecyclerView.Adapter<SleepAlarmAdapter.MyViewHolders> {

    private List<AlarmModel> alarmLists;
    private EditAlarmListener editListener;
    private  Context context;


    public class MyViewHolders extends RecyclerView.ViewHolder {
        public TextView time, name;
        public CardView card;
        public ImageView settingsImg, sleepBody, sleepBed,screenshot;
        public SwitchCompat switchcom;

        public MyViewHolders(View view) {
            super(view);
            time = (TextView) view.findViewById(R.id.time);
            name = (TextView) view.findViewById(R.id.name);
            screenshot = (ImageView) view.findViewById(R.id.screenshot);
            card = (CardView) view.findViewById(R.id.card_view);
            settingsImg = (ImageView) view.findViewById(R.id.settings);
            switchcom = (SwitchCompat) view.findViewById(R.id.switch_compat);
            sleepBody = (ImageView) view.findViewById(R.id.sleepbody);
            sleepBed = (ImageView) view.findViewById(R.id.sleepbed);

        }
    }


    public SleepAlarmAdapter(Context c,List<AlarmModel> lis, EditAlarmListener elis) {
        this.alarmLists = lis;
this.context=c;
        this.editListener = elis;
    }

    @Override
    public MyViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_sleep_alarm_item, parent, false);

        return new MyViewHolders(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolders holder, final int position) {


        holder.time.setText(alarmLists.get(position).getHour() + " hr " + alarmLists.get(position).getMinute() + " min");
        holder.name.setText(alarmLists.get(position).getName());

        if (alarmLists.get(position).isEnabled() == 1) {
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
                                editListener.onLongClick(1,alarmLists.get(position));
                                break;
                            case R.id.menu2:
                                //handle menu2 click
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
                                        editListener.onLongClick(2, alarmLists.get(position));


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
                                editListener.onSettingsClick(1,alarmLists.get(position));
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
                                        editListener.onSettingsClick(2, alarmLists.get(position));


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

        holder.switchcom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editListener.onCheckedChange(isChecked, alarmLists.get(position));
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

                } else {
                    holder.sleepBed.setVisibility(View.GONE);
                    holder.sleepBody.setVisibility(View.GONE);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return alarmLists.size();
    }
}