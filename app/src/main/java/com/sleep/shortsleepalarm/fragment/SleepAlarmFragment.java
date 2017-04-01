package com.sleep.shortsleepalarm.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.adapter.SleepAlarmAdapter;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.listeners.CreateAlarmOpened;
import com.sleep.shortsleepalarm.listeners.EditAlarmListener;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aravindraj on 12/8/2016.
 */
public class SleepAlarmFragment extends Fragment implements View.OnClickListener, EditAlarmListener {

    private List<AlarmModel> alarmListe = new ArrayList<>();
    private RecyclerView recyclerView;
    private SleepAlarmAdapter mAdapter;
    private FloatingActionButton mFab;
    private DatabaseHandler db;
    private CreateAlarmOpened lis;
    private CoordinatorLayout coord;

    void setListener(CreateAlarmOpened listener) {
        this.lis = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment__sleep_alarm, container, false);
        init(v);
        setListeners();
        return v;
    }

    private void setListeners() {
        mFab.setOnClickListener(this);
    }


    private void init(View v) {
        coord = (CoordinatorLayout) v.findViewById(R.id.coor);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        db = new DatabaseHandler(getContext());
        alarmListe = db.getAllSleepAlarmModels();
        mAdapter = new SleepAlarmAdapter(getContext(), alarmListe, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();


        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        if (EverydayApplication.getInstance().isOnline(getContext())) {
            MobileAds.initialize(getContext(), getResources().getString(R.string.banner_ad_unit_id2));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else
            mAdView.setVisibility(View.GONE);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:

                openCreateAlarm(null, null);
                break;

        }
    }

    private void openCreateAlarm(String flag, AlarmModel am) {
        CreateSleepAlarmFragment firstFragment = new CreateSleepAlarmFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (flag != null && am != null) {
            if (flag.equals("edit")) {
                Bundle ar = new Bundle();
                ar.putParcelable("edit", am);


                ar.putInt("editint", 1);
                firstFragment.setArguments(ar);
            }
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");
        fragmentTransaction.commit();
        lis.onCreateAlarmOpenedListener();
    }

    @Override
    public void onLongClick(int flag, AlarmModel am) {
        switch (flag) {
            case 1:
                openCreateAlarm("edit", am);
                break;
            case 2:
                db.deleteAlarmModel(am);
                EverydayApplication.getInstance().cancelAlarm(getContext(), am, am.getId());
                alarmListe.remove(am);
                mFab.show();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onSettingsClick(int flag, AlarmModel am) {
        switch (flag) {
            case 1:
                openCreateAlarm("edit", am);
                break;
            case 2:
                db.deleteAlarmModel(am);
                EverydayApplication.getInstance().cancelAlarm(getContext(), am, am.getId());
                alarmListe.remove(am);
                mFab.show();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onCheckedChange(boolean isChecked, AlarmModel ama) {
        if (isChecked) {
            ama.setIsEnabled(1);
            db.updateAlarmModel(ama);
            EverydayApplication.getInstance().setSleepAlarm(1,coord,getContext(), ama, ama.getId(), 0);

        } else {
            ama.setIsEnabled(0);
            db.updateAlarmModel(ama);
            EverydayApplication.getInstance().cancelAlarm(getContext(), ama, ama.getId());
        }
    }


    private void showSettingsDialog(final AlarmModel am) {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.settings_dialog);
        TextView edit = (TextView) dialog.findViewById(R.id.edit);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCreateAlarm("edit", am);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.deleteAlarmModel(am);
                EverydayApplication.getInstance().cancelAlarm(getContext(), am, am.getId());
                alarmListe.remove(am);
                mFab.show();
                mAdapter.notifyDataSetChanged();
                dialog.dismiss();

            }
        });

        dialog.show();
    }
}
