package com.sleep.shortsleepalarm.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.sleep.shortsleepalarm.R;

/**
 * Created by Aravindraj on 1/13/2017.
 */
public class SettingsFragment extends Fragment implements CompoundButton.OnCheckedChangeListener {
    SeekBar s;
    private RadioGroup radioGroup;
    private RadioButton radioButton1, radioButton2, radioButton3;
    SharedPreferences.Editor editor ;
    private AudioManager audioManager = null;
    SharedPreferences prefs ;
    TextView percent;


    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Settings");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        init(v);
        loadValues();

        setListeners();
        return v;
    }

    private void loadValues() {
        int snooze=prefs.getInt("snooze", 5);
        int volume=prefs.getInt("volume",5);
        switch (snooze)
        {
            case 5:
                radioButton1.setChecked(true);
                break;
            case 15:
                radioButton3.setChecked(true);
                break;
            case 10:
                radioButton2.setChecked(true);
                break;

        }
        s.setProgress(volume);
        percent.setText("15/" + volume);
    }

    private void setListeners() {
        radioButton1.setOnCheckedChangeListener(this);

        radioButton2.setOnCheckedChangeListener(this);

        radioButton3.setOnCheckedChangeListener(this);
    }

    private void init(View v) {
        editor = getContext().getSharedPreferences("MYPREFNAME", Context.MODE_PRIVATE).edit();
        prefs = getContext().getSharedPreferences("MYPREFNAME", Context.MODE_PRIVATE);
        s = (SeekBar) v.findViewById(R.id.seekbar);
        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup);
        radioButton1 = (RadioButton) v.findViewById(R.id.radioButton1);
        radioButton2 = (RadioButton) v.findViewById(R.id.radioButton2);
        radioButton3 = (RadioButton) v.findViewById(R.id.radioButton3);
        percent = (TextView) v.findViewById(R.id.percent);

        try
        {
            s = (SeekBar)v.findViewById(R.id.seekbar);
            audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            s.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            s.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            s.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                       }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {

                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    editor.putInt("volume", progress);
                    editor.commit();
                    percent.setText("15/" + progress);

                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
            switch (buttonView.getId()) {
                case R.id.radioButton1:
                    editor.putInt("snooze", 5);
                    editor.commit();
                    break;
                case R.id.radioButton2:
                    editor.putInt("snooze", 10);
                    editor.commit();
                    break;
                case R.id.radioButton3:
                    editor.putInt("snooze", 15);
                    editor.commit();
                    break;
            }
        }
    }
}
