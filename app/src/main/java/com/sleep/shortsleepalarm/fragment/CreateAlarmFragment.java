package com.sleep.shortsleepalarm.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Aravindraj on 10/26/2016.
 */
public class CreateAlarmFragment extends Fragment implements View.OnClickListener, TimePickerDialog.OnTimeSetListener {

    TextView mFooterSave, mTxtTime, mChooseSong, mRepeatTxt;
    EditText mEdtTxtName;
    DatabaseHandler db;
    AlarmModel amod;
    int flag = 0;
    CheckBox mon, tue, wed, thu, fri, sat, sun, mRepeat;
    HorizontalScrollView mHSV;
    Uri uri;
    private CoordinatorLayout mCor;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermission();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 002: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void checkPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        002);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_alarm, container, false);


        Bundle bundle = getArguments();
        if (bundle != null) {
            if (bundle.getParcelable("edit") != null) {
                amod = bundle.getParcelable("edit");
                flag = bundle.getInt("editint");
                Log.e("CreateAlarmFragment", "bundle" + amod.getId());
            }


        } else {
            amod = new AlarmModel();
            Calendar c = Calendar.getInstance();
            int hrofday = c.get(Calendar.HOUR_OF_DAY);
            int min = c.get(Calendar.MINUTE);
            openTimePicker(hrofday, min);

        }


        init(v);
        setListeners();
        if (flag == 1) {
            loadValues();
        }

        Log.e("ALarmsc", "" + db.getAlarmModelsCount());
        return v;
    }

    private void openTimePicker(int hrofday, int min) {


        TimePickerDialog mTimePickerDialog = new TimePickerDialog(getContext(), this, hrofday, min, false);
        mTimePickerDialog.show();

    }

    private void setListeners() {
        mFooterSave.setOnClickListener(this);
        mTxtTime.setOnClickListener(this);
        mChooseSong.setOnClickListener(this);
        mRepeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mRepeatTxt.setText("Weekly");
                    mHSV.setVisibility(View.VISIBLE);
                } else {
                    mRepeatTxt.setText("Once");
                    mHSV.setVisibility(View.GONE);
                }
            }
        });

    }

    private void init(View v) {
        mFooterSave = (TextView) v.findViewById(R.id.footer_save);
        mTxtTime = (TextView) v.findViewById(R.id.txt_time);
        mEdtTxtName = (EditText) v.findViewById(R.id.edt_txt_alarm_name);
        mChooseSong = (TextView) v.findViewById(R.id.choosesong);
        mRepeatTxt = (TextView) v.findViewById(R.id.rep);
        mCor = (CoordinatorLayout) v.findViewById(R.id.cor);

        AdView mAdView = (AdView) v.findViewById(R.id.adView);
        if (EverydayApplication.getInstance().isOnline(getContext())) {
            MobileAds.initialize(getContext(), getResources().getString(R.string.banner_ad_unit_id3));
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        } else
            mAdView.setVisibility(View.GONE);
        db = new DatabaseHandler(getContext());
        mon = (CheckBox) v.findViewById(R.id.mon);
        tue = (CheckBox) v.findViewById(R.id.tue);
        wed = (CheckBox) v.findViewById(R.id.wed);
        thu = (CheckBox) v.findViewById(R.id.thu);
        fri = (CheckBox) v.findViewById(R.id.fri);
        sat = (CheckBox) v.findViewById(R.id.sat);
        sun = (CheckBox) v.findViewById(R.id.sun);
        mRepeat = (CheckBox) v.findViewById(R.id.repeat_chk_box);
        mHSV = (HorizontalScrollView) v.findViewById(R.id.hsv);


    }

    private void loadValues() {
        String _24HourTime = "" + amod.getHour() + ":" + amod.getMinute();

        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");

        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mChooseSong.setText(amod.getTone());
        mTxtTime.setText("" + _12HourSDF.format(_24HourDt));
        mEdtTxtName.setText(amod.getName());
        if (amod.getRepeat() != null)
            if (amod.getRepeat().equals("o")) {

            } else {
                mRepeat.setChecked(true);
                mRepeatTxt.setText("Weekly");

                if (amod.getRepeat().charAt(0) == 't')
                    mon.setChecked(true);
                if (amod.getRepeat().charAt(1) == 't')
                    tue.setChecked(true);
                if (amod.getRepeat().charAt(2) == 't')
                    wed.setChecked(true);
                if (amod.getRepeat().charAt(3) == 't')
                    thu.setChecked(true);
                if (amod.getRepeat().charAt(4) == 't')
                    fri.setChecked(true);
                if (amod.getRepeat().charAt(5) == 't')
                    sat.setChecked(true);
                if (amod.getRepeat().charAt(6) == 't')
                    sun.setChecked(true);


            }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.footer_save:
                amod.setName(mEdtTxtName.getText().toString());
                if (flag == 1)
                    EverydayApplication.getInstance().doCancelAlarm(getContext(), amod);
                if (mRepeat.isChecked() && (mon.isChecked() || tue.isChecked() || wed.isChecked() || thu.isChecked() || fri.isChecked() || sat.isChecked() || sun.isChecked()))
                    amod.setRepeat(computeCheckbox());
                else
                    amod.setRepeat("o");

                if (flag == 1) {
                    amod.setIsEnabled(1);
                    db.updateAlarmModel(amod);
                    Log.e("CreateAlarmFragment", "modid=" + amod.getId());

                    EverydayApplication.getInstance().doCreateAlarm(1,null,getContext(), amod);

                } else {
                    amod.setIsEnabled(1);
                    amod.setType("a");
                    db.addAlarmModel(amod);
                    amod.setId(db.getId());

                    Log.e("CreateAlarmFragment", "dbid=" + db.getId());
                    EverydayApplication.getInstance().doCreateAlarm(1,null,getContext(), amod);

                }
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.txt_time:
                openTimePicker(amod.getHour(), amod.getMinute());
                break;
            case R.id.choosesong:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                this.startActivityForResult(intent, 5);
                break;
        }
    }


    private String computeCheckbox() {
        String checked = null;
        if (mon.isChecked())
            checked = "t";
        else
            checked = "f";
        if (tue.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";
        if (wed.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";
        if (thu.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";
        if (fri.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";
        if (sat.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";

        if (sun.isChecked())
            checked = checked + "t";
        else
            checked = checked + "f";

        return checked;
    }


    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        if (resultCode == Activity.RESULT_OK && requestCode == 5) {
            Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
            String title = ringtone.getTitle(getContext());
            if (uri != null) {
//                 = uri.toString();
                amod.setTone(title);
                amod.setToneurl(uri.toString());
                mChooseSong.setText(amod.getTone());
                Log.e("CreateAlarmFragment", "=" + amod.getToneurl());
            } else {
//                this.chosenRingtone = null;
                amod.setTone("Default Alarm Tone");
                amod.setToneurl("");
                mChooseSong.setText(amod.getTone());
                Log.e("CreateAlarmFragment", "=" + amod.getToneurl());
            }
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        String _24HourTime = "" + hourOfDay + ":" + minute;
        SimpleDateFormat _24HourSDF = new SimpleDateFormat("HH:mm");
        SimpleDateFormat _12HourSDF = new SimpleDateFormat("hh : mm a");
        amod.setHour(hourOfDay);
        amod.setMinute(minute);
        Date _24HourDt = null;
        try {
            _24HourDt = _24HourSDF.parse(_24HourTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ;
        mTxtTime.setText("" + _12HourSDF.format(_24HourDt));

    }


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Create Alarm");
    }

}
