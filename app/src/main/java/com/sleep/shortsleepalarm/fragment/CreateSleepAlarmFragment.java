package com.sleep.shortsleepalarm.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.common.EverydayApplication;
import com.sleep.shortsleepalarm.database.DatabaseHandler;
import com.sleep.shortsleepalarm.model.AlarmModel;

/**
 * Created by Aravindraj on 12/8/2016.
 */
public class CreateSleepAlarmFragment extends Fragment implements View.OnClickListener {


    TextView mFooterSave, mTxtTime, mChooseSong;
    EditText mEdtTxtName;
    DatabaseHandler db;
    AlarmModel amod;
    int flag = 0;
    ImageView trash;
    Button fivem, tenm, thirtym, sevenh, fiveh;
    int total;
    Uri uri;

    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Create Sleep Alarm");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

checkPermission();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_sleep_alarm, container, false);
        init(v);
        setListeners();

        Bundle bundle = getArguments();
        if (bundle != null) {
            amod = bundle.getParcelable("edit");
            flag = bundle.getInt("editint");
            Log.e("CreateAlarmFragment","bundle=="+amod.getId());

        } else {
            amod = new AlarmModel();

        }


        init(v);
        setListeners();
        if (flag == 1) {
            loadValues();
        }


        return v;
    }

    private void loadValues() {

        getHandM(amod.getHour() * 60 + amod.getMinute());
        total = amod.getHour() * 60 + amod.getMinute();
        mEdtTxtName.setText(amod.getName());
        mChooseSong.setText(amod.getTone());
    }

    private void setListeners() {
        mFooterSave.setOnClickListener(this);
        mTxtTime.setOnClickListener(this);
        mChooseSong.setOnClickListener(this);
        fivem.setOnClickListener(this);
        fiveh.setOnClickListener(this);
        thirtym.setOnClickListener(this);
        tenm.setOnClickListener(this);
        sevenh.setOnClickListener(this);
        trash.setOnClickListener(this);

    }

    private void init(View v) {
        mFooterSave = (TextView) v.findViewById(R.id.footer_save);
        mTxtTime = (TextView) v.findViewById(R.id.txt_time);
        mEdtTxtName = (EditText) v.findViewById(R.id.edt_txt_alarm_name);
        mChooseSong = (TextView) v.findViewById(R.id.choosesong);
        trash = (ImageView) v.findViewById(R.id.trash);
        fivem = (Button) v.findViewById(R.id.five_min);
        fiveh = (Button) v.findViewById(R.id.five_hr);
        tenm = (Button) v.findViewById(R.id.ten_min);
        thirtym = (Button) v.findViewById(R.id.thirty_min);
        sevenh = (Button) v.findViewById(R.id.seven_hr);


        db = new DatabaseHandler(getContext());


        total = 0;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.trash:
                cleardata();
                break;
            case R.id.footer_save:
                if(total>0) {
                    amod.setName(mEdtTxtName.getText().toString());
                    if (flag == 1)
                        EverydayApplication.getInstance().cancelAlarm(getContext(), amod, amod.getId());
                    int hour = total / 60;
                    int minute = total % 60;
                    amod.setHour(hour);
                    amod.setMinute(minute);

                    if (flag == 1) {
                        db.updateAlarmModel(amod);
                        amod.setIsEnabled(1);
                        EverydayApplication.getInstance().setSleepAlarm(1,null,getContext(), amod, amod.getId(), 0);

                    } else {
                        amod.setIsEnabled(1);
                        amod.setType("sa");
                        db.addAlarmModel(amod);
                        amod.setId(db.getId());
                        Log.e("pointing", "dbcreate=" + db.getId());
                        EverydayApplication.getInstance().setSleepAlarm(1,null,getContext(), amod, amod.getId(), 0);
                    }
                    getActivity().getSupportFragmentManager().popBackStack();

                }
                else
                    Toast.makeText(getContext(),"Please set a valid sleep duration",Toast.LENGTH_SHORT).show();
                    break;

            case R.id.choosesong:
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
                this.startActivityForResult(intent, 5);
                break;


            case R.id.five_min:
                total = total + 5;
                getHandM(total);
                break;


            case R.id.ten_min:
                total = total + 10;
                getHandM(total);
                break;

            case R.id.thirty_min:
                total = total + 30;
                getHandM(total);
                break;

            case R.id.five_hr:
                total = total + 300;
                getHandM(total);
                break;
            case R.id.seven_hr:
                total = total + 420;
                getHandM(total);
                break;
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

    private void cleardata() {
        total = 0;
        getHandM(total);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 003: {
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
    private String getHandM(int t) {
        int hours = t / 60; //since both are ints, you get an int
        int minutes = t % 60;


        String s = String.format("%02d hr %02d min", hours, minutes);
        mTxtTime.setText(s);
        return s;
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
            } else {
//                this.chosenRingtone = null;
                amod.setTone("Default Alarm Tone");
                amod.setToneurl("");
                mChooseSong.setText(amod.getTone());
            }
        }
    }
}
