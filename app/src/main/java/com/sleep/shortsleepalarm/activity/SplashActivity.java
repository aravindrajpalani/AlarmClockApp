package com.sleep.shortsleepalarm.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

import com.sleep.shortsleepalarm.R;

/**
 * Created by Aravindraj on 12/13/2016.
 */
public class SplashActivity extends ActionBarActivity implements View.OnClickListener {

    TextView eula, accept, footer;
    SharedPreferences mPrefs;
    String welcomeScreenShownPref = "welcomeScreenShown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        eula = (TextView)findViewById(R.id.eula);
        accept = (TextView)findViewById(R.id.accept);
        footer = (TextView)findViewById(R.id.footer);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        eula.setOnClickListener(this);
        footer.setOnClickListener(this);
        accept.setOnClickListener(this);
        Boolean welcomeScreenShown = mPrefs.getBoolean(welcomeScreenShownPref, false);
        if (welcomeScreenShown) {

            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            this.finish();
            startActivity(i);
        }


        accept.setText(Html.fromHtml("By using this application , agree to Everyday Alarm Clock app's <font color='#ff4e4a'>Terms and Privacy Policy</font>"));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.accept:
                accept.setVisibility(View.GONE);
                eula.setVisibility(View.VISIBLE);
                break;

            case R.id.footer:
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putBoolean(welcomeScreenShownPref, true);
                editor.commit();
                this.finish();
                startActivity(i);


                break;
        }

    }
}
