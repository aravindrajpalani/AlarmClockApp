package com.sleep.shortsleepalarm.adapter;

/**
 * Created by Aravindraj on 9/6/2016.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class GraphAdapter extends RecyclerView.Adapter<GraphAdapter.GraphViewHolder> {

    private ArrayList<AlarmModel> alarmList;

    public Context context;


    public class GraphViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public TextView time, name;

        public DatabaseHandler db;

        public GraphViewHolder(View view) {
            super(view);
            date = (TextView) view.findViewById(R.id.date);
            time = (TextView) view.findViewById(R.id.time);
            name = (TextView) view.findViewById(R.id.name);

        }
    }


    public GraphAdapter(Context con, ArrayList<AlarmModel> list) {
        this.alarmList = list;
        this.context = con;
    }

    @Override
    public GraphViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.inflate_graph_item, parent, false);

        return new GraphViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final GraphViewHolder holder, final int position) {

        holder.name.setText("" + alarmList.get(position).getName());
        holder.date.setText("" + alarmList.get(position).getDay() + "-" + alarmList.get(position).getMonth() + "-" + alarmList.get(position).getYear());
        if (alarmList.get(position).getType().equals("sa")) {
            holder.time.setText(alarmList.get(position).getHour() + " hr " + alarmList.get(position).getMinute() + " min");
        } else {
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


            holder.time.setText("" + _12HourSDF.format(_24HourDt));


        }
    }

    @Override
    public int getItemCount() {
        return alarmList.size();
    }
}