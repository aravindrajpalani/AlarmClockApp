package com.sleep.shortsleepalarm.fragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.adapter.AlarmAdapter;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.listeners.CreateAlarmOpened;
import com.sleep.shortsleepalarm.listeners.EditAlarmListener;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aravindraj on 9/6/2016.
 */
public class AlarmFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, EditAlarmListener {
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 005;
    private List<AlarmModel> alarmList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AlarmAdapter mAdapter;
    private FloatingActionButton mFab;
    private DatabaseHandler db;
    private CreateAlarmOpened lis;
    private CoordinatorLayout mCor;
    AlarmModel ammod = null;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);
        init(v);
        setListeners();
        return v;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getGroupId()) {
            case 1:

                break;
            case 2:
                // do your stuff
                break;
        }
        return super.onContextItemSelected(item);
    }


    private void setListeners() {
        mFab.setOnClickListener(this);
    }


    private void init(View v) {

        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mCor = (CoordinatorLayout) v.findViewById(R.id.cor);
        mFab = (FloatingActionButton) v.findViewById(R.id.fab);
        db = new DatabaseHandler(getContext());
        alarmList = db.getAllAlarmModels();
        mAdapter = new AlarmAdapter(getContext(), alarmList, this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        SharedPreferences prefs = getContext().getSharedPreferences("counterprefs", Context.MODE_PRIVATE);

        int count = prefs.getInt("counter", 0);
        if (EverydayApplication.getInstance().isOnline(getContext())) {
            MobileAds.initialize(getContext(), getResources().getString(R.string.banner_ad_unit_id));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        } else
            mAdView.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab:
                Log.e("fab", "selected");
                openCreateAlarm(null, null);
                break;

        }
    }

    void setListener(CreateAlarmOpened listener) {
        this.lis = listener;
    }

    private void openCreateAlarm(String flag, AlarmModel ams) {
        CreateAlarmFragment firstFragment = new CreateAlarmFragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        if (flag != null && ams != null) {
            if (flag.equals("edit")) {
                Bundle ar = new Bundle();
                ar.putParcelable("edit", ams);
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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.e("hrhrhr", "=" + hourOfDay);
    }

    @Override
    public void onLongClick(int flag, AlarmModel am) {
        ammod = null;
        ammod = am;
        switch (flag) {
            case 1:

                openCreateAlarm("edit", ammod);
                break;
            case 2:
                db.deleteAlarmModel(ammod);
                EverydayApplication.getInstance().doCancelAlarm(getContext(), ammod);


                alarmList.remove(ammod);
                mAdapter.notifyDataSetChanged();
                mFab.show();
                break;
        }
    }

    @Override
    public void onSettingsClick(int flag, AlarmModel am) {
        ammod = null;
        ammod = am;
        switch (flag) {
            case 1:

                openCreateAlarm("edit", ammod);
                break;
            case 2:
                db.deleteAlarmModel(ammod);
                EverydayApplication.getInstance().doCancelAlarm(getContext(), ammod);


                alarmList.remove(ammod);

                mAdapter.notifyDataSetChanged();
                mFab.show();
                recyclerView.invalidate();
                break;
        }
    }



    @Override
    public void onCheckedChange(boolean isChecked, AlarmModel am) {
        if (isChecked) {
            am.setIsEnabled(1);
            db.updateAlarmModel(am);
            EverydayApplication.getInstance().doCreateAlarm(1,mCor ,getContext(), am);

        } else {
            am.setIsEnabled(0);
            db.updateAlarmModel(am);
            EverydayApplication.getInstance().doCancelAlarm(getContext(), am);
        }
    }


    private void showSettingsDialog(AlarmModel am) {
        final Dialog dialog = new Dialog(getContext());
        ammod = null;
        ammod = am;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.settings_dialog);
        TextView edit = (TextView) dialog.findViewById(R.id.edit);
        TextView delete = (TextView) dialog.findViewById(R.id.delete);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                openCreateAlarm("edit", ammod);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteAlarmModel(ammod);
                EverydayApplication.getInstance().doCancelAlarm(getContext(), ammod);


                alarmList.remove(ammod);
                mAdapter.notifyDataSetChanged();
                mFab.show();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("red", "=" + db.getId());
    }
}
