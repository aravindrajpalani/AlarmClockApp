package com.sleep.shortsleepalarm.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.sleep.shortsleepalarm.R;
import com.sleep.shortsleepalarm.fragment.AboutFragment;
import com.sleep.shortsleepalarm.fragment.HomeFragment;
import com.sleep.shortsleepalarm.fragment.SettingsFragment;
import com.sleep.shortsleepalarm.fragment.UsageHistoryFragment;
import com.sleep.shortsleepalarm.listeners.FromHomeListener;

public class MainActivity extends AppCompatActivity implements FromHomeListener, FragmentManager.OnBackStackChangedListener {
    private final int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 127;
    private final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 007;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    Toolbar mToolbar;
    SharedPreferences.Editor editor;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        init();
        setListeners();
        initNavigationDrawer();

    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                // You don't have permission
                checkPermission();

            } else {
                //do as per your logic
            }

        }


    }

    private void init() {
        //public static final String MY_PREFS_NAME = "MyPrefsFile";

        prefs = getSharedPreferences("counterprefs", MODE_PRIVATE);

        int count = prefs.getInt("counter", 0);
    count=count+1;
        editor = getSharedPreferences("counterprefs", MODE_PRIVATE).edit();

        editor.putInt("counter", count);
        editor.commit();
        mNavigationView = (NavigationView)

                findViewById(R.id.navigation_view);

        mDrawerLayout = (DrawerLayout)

                findViewById(R.id.drawer);

        mToolbar = (Toolbar)

                findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);

        HomeFragment firstFragment = new HomeFragment();
        firstFragment.setListener(this);
        // Add the fragment to the 'fragment_container' FrameLayout
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");

        fragmentTransaction.commit();

    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void setListeners() {
    }

    private void initNavigationDrawer() {


        //Setting <a href="#">Navigation View</a> Item Selected Listener to handle the item click of the navigation menu
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {


                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) menuItem.setChecked(false);
                else menuItem.setChecked(true);

                //Closing drawer on item click
                mDrawerLayout.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {


                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.menu_infographics:
                        openFrag(3);
                        return true;

                    // For rest of the options we just show a toast on click

                    case R.id.menu_settings:
                        openFrag(2);
                        return true;

                    case R.id.menu_about:
                        openFrag(1);
                        return true;

                    default:
                        return true;

                }
            }
        });
        // Initializing Drawer Layout and ActionBarToggle

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
                for (int i = 0; i < mNavigationView.getMenu().size(); i++) {
                    mNavigationView.getMenu().getItem(i).setChecked(false);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank

                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        mActionBarDrawerToggle.syncState();
        getSupportFragmentManager().addOnBackStackChangedListener(this);
        mActionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("backst", "kstack");
                getSupportFragmentManager().popBackStack();
            }
        });

    }


    @Override
    public void onBackPressed() {


        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() > 1) {
            Log.i("MainActivity", "popping backstack");
            fm.popBackStack();

        } else {
            Log.i("MainActivity", "nothing on backstack, calling super");
            this.finish();
        }

        fm.addOnBackStackChangedListener(this);

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);
        if (f != null)
            Log.e("tagggg", "" + f.getTag());


    }


    private void openFrag(int i) {
        Fragment firstFragment = null;
        switch (i) {
            case 1:
                firstFragment = new AboutFragment();
                break;
            case 2:
                firstFragment = new SettingsFragment();
                break;
            case 3:
                firstFragment = new UsageHistoryFragment();
                break;
            // Add the fragment to the 'fragment_container' FrameLayout

        }
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

        fragmentTransaction.replace(R.id.frame, firstFragment, "h");
        fragmentTransaction.addToBackStack("h");

        fragmentTransaction.commit();
    }

    @Override
    public void onFromHomeListener() {
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    @Override
    public void onBackStackChanged() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            Log.e("homescreen", "===");

            mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
    }
}

